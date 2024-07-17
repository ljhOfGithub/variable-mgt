/*
 * Licensed to the Wiseco Software Corporation under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wiseco.var.process.app.server.service.engine;

import com.wiseco.decision.engine.var.runtime.api.InternalDataService;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternalDataServiceImpl implements InternalDataService {
    private static final String STRING_1 = "1";

    private static final String DATA_DATE = "data_date";

    private static final Pattern FIND_VALUE_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    @Resource
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;
    @Resource
    private VarProcessInternalDataService varProcessInternalDataService;

    @Override
    public JSONObject getInternalData(Long spaceId, String identifier, Map<String, Object> params) {
        final long startTime = System.currentTimeMillis();
        log.info("Internal data query input argument, spaceId:{},identifier:{},params:{}", spaceId, identifier, params);
        //获取内部数据-数据模型定义
        List<VarProcessInternalData> varProcessInternalDataList = varProcessInternalDataService.findByIdentifier(spaceId, identifier);
        final VarProcessInternalData varProcessInternalData = varProcessInternalDataList.get(0);

        Assert.notNull(varProcessInternalData, "未查询到内部数据配置信息");
        VariableDataModeViewOutputVo.DataModelInsideDataVO dataModelInsideDataVO = JSON.parseObject(varProcessInternalData.getContent(),
                VariableDataModeViewOutputVo.DataModelInsideDataVO.class);
        // 校验入参
        validateParams(dataModelInsideDataVO.getInput(), params);
        // 根据数据模型定义，从数据库获取数据
        JSONObject retData = getDataFromDb(dataModelInsideDataVO, params, varProcessInternalData.getObjectName());
        log.info("Internal data query cost time:{}", System.currentTimeMillis() - startTime);
        log.info("Internal data query result:{}", retData);
        return retData;
    }

    private void validateParams(List<VariableDataModeViewOutputVo.InsideInputVO> input, Map<String, Object> params) {
        log.info("Definition params {}", input);
        log.info("Input params:{}", params);
        // todo 校验参数是否完整，是否有数，后面再实现
    }

    private JSONObject getDataFromDb(VariableDataModeViewOutputVo.DataModelInsideDataVO dataModelInsideDataVO, Map<String, Object> params,
                                     String objectName) {
        log.info("Data model Definition:{}", dataModelInsideDataVO);
        if (dataModelInsideDataVO.getInsideDataType() == VarProcessDataModeInsideDataType.SQL) {
            // sql取数，根节点可以是数组
            return getDataFromDbBySql(dataModelInsideDataVO, params, objectName);
        } else if (dataModelInsideDataVO.getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
            // 表映射取数，根节点不可以是数组
            // 根节点只有一个，所以取第一条
            final VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO = dataModelInsideDataVO.getTableOutput().get(0);
            return getDataFromDbByTable(insideOutputVO, params);
        } else {
            return new JSONObject();
        }
    }

    private JSONObject getDataFromDbBySql(VariableDataModeViewOutputVo.DataModelInsideDataVO dataModelInsideDataVO, Map<String, Object> params,
                                          String objectName) {

        ArrayList<String> useList = new ArrayList<>();
        if (dataModelInsideDataVO.getSqlIsArray()) {
            for (VariableDataModeViewOutputVo.InsideSqlOutputVO sqlOutputDto : dataModelInsideDataVO.getSqlOutput().get(0).getChildren().get(0).getChildren()) {
                if ("0".equals(sqlOutputDto.getIsDelete())) {
                    useList.add(sqlOutputDto.getObjectName());
                }
            }
        } else {
            for (VariableDataModeViewOutputVo.InsideSqlOutputVO sqlOutputDto : dataModelInsideDataVO.getSqlOutput().get(0).getChildren()) {
                if ("0".equals(sqlOutputDto.getIsDelete())) {
                    useList.add(sqlOutputDto.getObjectName());
                }
            }

        }

        // 取数
        final List<String> filters = findValueByRegx(dataModelInsideDataVO.getSqlString());
        final String querySql = getSql(dataModelInsideDataVO.getSqlString(), filters);
        Object[] values = getValues(filters, params);
        List<Map<String, Object>> dataList = internalJdbcTemplate.queryForList(querySql, values);
        // 构建返回数据模型树
        JSONObject retData = new JSONObject();
        if (Boolean.TRUE.equals(dataModelInsideDataVO.getSqlIsArray())) {
            JSONObject twoLevel = new JSONObject();
            JSONArray array = new JSONArray();
            final List<VariableDataModeViewOutputVo.InsideSqlOutputVO> sqlOutput = dataModelInsideDataVO.getSqlOutput().get(0).getChildren().get(0).getChildren();
            if (sqlOutput == null || sqlOutput.isEmpty()) {
                return new JSONObject();
            }
            for (Map<String, Object> dbVars : dataList) {
                JSONObject varData = new JSONObject();
                for (VariableDataModeViewOutputVo.InsideSqlOutputVO insideSqlOutputVO : sqlOutput) {
                    if (useList.contains(insideSqlOutputVO.getObjectName())) {
                        varData.put(insideSqlOutputVO.getObjectName(), dbVars.get(insideSqlOutputVO.getObjectName()));
                    }
                }
                array.add(varData);
            }
            twoLevel.put(dataModelInsideDataVO.getSqlOutput().get(0).getChildren().get(0).getObjectName(),array);
            retData.put(objectName, twoLevel);
        } else {
            final Map<String, Object> dbVars = dataList.get(0);
            JSONObject varData = new JSONObject();
            final List<VariableDataModeViewOutputVo.InsideSqlOutputVO> sqlOutput = dataModelInsideDataVO.getSqlOutput().get(0).getChildren();
            if (sqlOutput == null || sqlOutput.isEmpty()) {
                return new JSONObject();
            }
            for (VariableDataModeViewOutputVo.InsideSqlOutputVO insideSqlOutputVO : sqlOutput) {
                if (useList.contains(insideSqlOutputVO.getObjectName())) {
                    varData.put(insideSqlOutputVO.getObjectName(), dbVars.get(insideSqlOutputVO.getObjectName()));
                }
            }
            retData.put(objectName, varData);
        }

        return retData.getJSONObject(objectName);
    }

    private String getSql(String sqlString, List<String> filters) {
        //将条件中的${str}替换为‘?’
        for (String str : filters) {
            String target = "${" + str + "}";
            sqlString = sqlString.replace(target, "?");
        }
        return sqlString;
    }

    private String getSql(VariableDataModeViewOutputVo.TableConfigsVO tableConfigs, List<String> filters) {
        final String select = tableConfigs.getFieldMapping().stream()
                .map(VariableDataModeViewOutputVo.FieldMappingVO::getName)
                .collect(Collectors.joining(","));
        String conditions = tableConfigs.getConditions();
        //将条件中的${str}替换为‘?’
        for (String str : filters) {
            String target = "${" + str + "}";
            conditions = conditions.replace(target, "?");
        }
        return "select " + select + " from " + tableConfigs.getTableName() + " where " + conditions;
    }

    private Object[] getValues(List<String> filters, Map<String, Object> params) {
        Object[] values = new Object[filters.size()];
        for (int i = 0; i < filters.size(); i++) {
            String filter = filters.get(i);
            values[i] = params.get(filter);
        }
        return values;
    }

    private JSONObject getDataFromDbByTable(VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO, Map<String, Object> params) {
        // 表映射是一个树形结构
        JSONObject retData = new JSONObject();
        // 使用递归方式获取
        getTableData(insideOutputVO, params, retData);
        return retData.getJSONObject(insideOutputVO.getObjectName());
    }

    /**
     * 递归获取数据
     *
     * @param insideOutputVO 数据模型结构定义
     * @param params         sql的参数
     * @param parent         返回的数据
     */
    private void getTableData(VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO, Map<String, Object> params, JSONObject parent) {
        // 是数组的情况
        if (STRING_1.equals(insideOutputVO.getIsArr())) {
            JSONArray nodes = new JSONArray();
            parent.put(insideOutputVO.getObjectName(), nodes);
            // 有映射的情况，添加叶子节点
            if (STRING_1.equals(insideOutputVO.getIsMapping())) {
                final List<Map<String, Object>> dataList = queryData(insideOutputVO, params);
                if (!dataList.isEmpty()) {
                    final List<VariableDataModeViewOutputVo.FieldMappingVO> fieldMapping = insideOutputVO.getTableConfigs().getFieldMapping();
                    for (Map<String, Object> data : dataList) {
                        JSONObject vars = new JSONObject();
                        for (VariableDataModeViewOutputVo.FieldMappingVO fieldMappingVO : fieldMapping) {
                            vars.put(fieldMappingVO.getMappingName(), data.get(fieldMappingVO.getName()));
                        }
                        nodes.add(vars);
                    }
                }
            }
            // 有子节点的情况，递归调用
            // 此处原型设计不明确，如果映射有子节点，则需要设置为子节点个数，否则设置为1(废弃)
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                getChildrenData(insideOutputVO.getChildren(), node, params);
            }
        } else {
            // 有映射的情况，添加叶子节点
            JSONObject node = new JSONObject();
            parent.put(insideOutputVO.getObjectName(), node);
            if (STRING_1.equals(insideOutputVO.getIsMapping())) {
                final List<Map<String, Object>> dataList = queryData(insideOutputVO, params);
                if (!dataList.isEmpty()) {
                    final List<VariableDataModeViewOutputVo.FieldMappingVO> fieldMapping = insideOutputVO.getTableConfigs().getFieldMapping();
                    final Map<String, Object> dbData = dataList.get(0);
                    for (VariableDataModeViewOutputVo.FieldMappingVO fieldMappingVO : fieldMapping) {
                        node.put(fieldMappingVO.getMappingName(), dbData.get(fieldMappingVO.getName()));
                    }
                }
            }
            getChildrenData(insideOutputVO.getChildren(), node, params);
        }
    }

    private void getChildrenData(List<VariableDataModeViewOutputVo.InsideOutputVO> nodesDef, JSONObject parent, Map<String, Object> params) {
        if (nodesDef != null) {
            for (VariableDataModeViewOutputVo.InsideOutputVO nodeDef : nodesDef) {
                getTableData(nodeDef, params, parent);
            }
        }
    }

    private List<Map<String, Object>> queryData(VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO, Map<String, Object> params) {
        final VariableDataModeViewOutputVo.TableConfigsVO tableConfigs = insideOutputVO.getTableConfigs();
        final List<String> filters = findValueByRegx(tableConfigs.getConditions());
        final String querySql = getSql(tableConfigs, filters);
        Object[] values = getValues(filters, params);
        return internalJdbcTemplate.queryForList(querySql, values);
    }

    /**
     * 根据正则表达式获取指定字符之间的值
     *
     * @param str 入参
     * @return List
     */
    private List<String> findValueByRegx(String str) {
        Matcher m = FIND_VALUE_PATTERN.matcher(str);
        List<String> matchStr = new ArrayList<>();
        while (m.find()) {
            matchStr.add(m.group(1));
        }
        return matchStr;
    }

}
