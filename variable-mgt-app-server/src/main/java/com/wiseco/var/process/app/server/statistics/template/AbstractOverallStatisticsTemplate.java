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
package com.wiseco.var.process.app.server.statistics.template;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.wiseco.data.service.commons.util.DateUtils;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.commons.enums.DataType;
import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessConfigDefaultService;
import com.wiseco.var.process.app.server.statistics.IndexCalculateStrategy;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import com.wiseco.var.process.app.server.statistics.factory.IndexCalculateStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractOverallStatisticsTemplate {

    @Resource
    IndexCalculateStrategyFactory calculateStrategyFactory;
    @Resource
    private DbOperateService dbOperateService;
    @Resource
    private VarProcessConfigDefaultService varProcessConfigDefaultValueService;

    private static final int SIZE = 10;
    private static final String SPECIAL_RATIO = "specialRatio";
    static final String SERIAL_NO = "external_serial_no";
    static final String MYSQL_VARIABLES = "variables";


    /**
     * 执行计算
     *
     * @param calculateContext 统计分析计算对象
     */
    public void executeCalculate(IndexCalculateContext calculateContext) {
        //获取数据表名称
        String tableName = getTableName(calculateContext.getTableSourceId());
        //获取数据表列
        Assert.notNull(tableName, "数据表不存在");
        List<Map<String, Object>> manifestVarsType = getManifestVarsType(calculateContext.getManifestId(), null);
        Assert.notEmpty(manifestVarsType, "未找到清单变量类型配置！");
        calculateContext.setAllManifestVarsType(manifestVarsType);

        //要计算的指标
        calculateContext.setCalculateList(getIndexList(calculateContext.getIndexMappingVo()));

        //缺失配置
        handleMissingConfig(calculateContext);

        //分组，每组10列
        List<List<Map<String, Object>>> fieldMapListGroup = Lists.partition(manifestVarsType, SIZE);
        for (int i = 0; i < fieldMapListGroup.size(); i++) {
            // 待计算列名称
            final List<Map<String, Object>> currentVarTypes = fieldMapListGroup.get(i);
            final ArrayList<Map<String, Object>> cloneList = SerializationUtils.clone(new ArrayList<>(currentVarTypes));
            // 添加额外的关联字段
            addRelevanceVars(calculateContext, cloneList);
            calculateContext.setCurrentVarsType(cloneList);
            // 待计算列数据
            List<Map<String, Object>> dataList = getDataList(tableName, calculateContext);
            if (CollectionUtils.isEmpty(dataList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据未生成，无法计算！");
            }
            // 如果勾选IV，还需要聚合标签列
            if (calculateContext.getIndexMappingVo().isIv()) {
                aggregateTarget(dataList, calculateContext);
            }
            calculateContext.setDataList(dataList);
            //计算
            calculateIndex(calculateContext);
            //保存
            saveData(calculateContext, i == 0);
        }
    }

    /**
     * 执行计算——监控单指标计算
     *
     * @param calculateContext 统计分析计算对象
     * @return 结果列表
     */
    public List<StatisticsResultVo> executeCalculateOfMonitoringSingleVariable(IndexCalculateContext calculateContext) {
        //初始化计算指标
        calculateContext.setCalculateList(getIndexList(calculateContext.getIndexMappingVo()));

        //获取缺失值配置
        handleMissingConfig(calculateContext);

        //获取所有清单的数据集类型及指标索引位置
        Map<Long, List<Map<String, Object>>> manifestVarsTypeListMap = new LinkedHashMap<>();
        for (IndexCalculateContext.DataSourceInfo dataSourceInfo : calculateContext.getDataSourceInfoList()) {
            manifestVarsTypeListMap.put(dataSourceInfo.getManifestId(), getManifestVarsType(dataSourceInfo.getManifestId(), null));
        }
        Assert.notEmpty(manifestVarsTypeListMap.values(), "未找到清单变量类型配置！");
        calculateContext.setAllManifestVarsTypeListMap(manifestVarsTypeListMap);

        //计算结果
        List<StatisticsResultVo> resultList = new ArrayList<>();
        //要计算的列和类型
        List<Map<String, Object>> calculateVarsType = getManifestVarsType(calculateContext.getDataSourceInfoList().get(0).getManifestId(), calculateContext.getCalculateVariableList());
        //分列计算
        for (List<Map<String, Object>> currentVarTypes : Lists.partition(calculateVarsType, SIZE)) {
            // 添加额外的关联字段
            final ArrayList<Map<String, Object>> cloneList = SerializationUtils.clone(new ArrayList<>(currentVarTypes));
            addRelevanceVars(calculateContext, cloneList);
            calculateContext.setCurrentVarsType(cloneList);

            //获取数据集
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (IndexCalculateContext.DataSourceInfo dataSourceInfo : calculateContext.getDataSourceInfoList()) {
                calculateContext.setVarProcessServiceId(dataSourceInfo.getServiceId());
                calculateContext.setAllManifestVarsType(manifestVarsTypeListMap.get(dataSourceInfo.getManifestId()));
                dataList.addAll(getDataList(getTableName(dataSourceInfo.getManifestId()), calculateContext));
            }

            if (CollectionUtils.isEmpty(dataList)) {
                resultList.addAll(getEmptyResultList(calculateContext, currentVarTypes));
                continue;
            }
            calculateContext.setDataList(dataList);

            // 如果勾选IV，还需要聚合标签列
            if (calculateContext.getIndexMappingVo().isIv()) {
                aggregateTarget(dataList, calculateContext);
            }

            //计算
            calculateIndex(calculateContext);

            //保存结果
            resultList.addAll(calculateContext.getResultList());
        }
        return resultList;
    }

    private List<StatisticsResultVo> getEmptyResultList(IndexCalculateContext calculateContext, List<Map<String, Object>> currentVarTypes) {
        List<StatisticsResultVo> emptyResultList = new ArrayList<>();
        for (Map<String, Object> fieldMap : currentVarTypes) {
            String varName = fieldMap.get("name").toString();
            StatisticsResultVo statisticsResultVo = new StatisticsResultVo();
            statisticsResultVo.setVarName(calculateContext.getVarNameMap().get(varName));
            statisticsResultVo.setVarCode(varName);
            emptyResultList.add(statisticsResultVo);
        }
        return emptyResultList;
    }


    private void aggregateTarget(List<Map<String, Object>> dataList, IndexCalculateContext calculateContext) {
        final List<String> externalSerialNoList = dataList.stream().map(map -> map.get(SERIAL_NO).toString()).collect(Collectors.toList());
        // 执行关联查询
        String sql = aggregateSql(calculateContext);
        if (sql == null) {
            return;
        }
        final List<Map<String, Object>> targetList = dbOperateService.queryForListOfDynamicTable(calculateContext.getIvMappingVo().getTableName(), sql);
        final Map<String, Object> targetMap = targetList.stream().collect(Collectors.toMap(map -> map.get("name").toString(), map -> map.get("target"), (existingValue, newValue) -> existingValue));

        for (int i = 0; i < dataList.size(); i++) {
            final Map<String, Object> row = dataList.get(i);
            final String serialNo = externalSerialNoList.get(i);
            row.put(CommonConstant.TARGET_NAME, targetMap.get(serialNo));
        }
    }

    /**
     * 生成聚合标签列sql
     *
     * @param calculateContext calculateContext
     * @return String
     */
    protected abstract String aggregateSql(IndexCalculateContext calculateContext);

    /**
     * 获取清单参数类型
     *
     * @param manifestId    manifestId
     * @param variableCodes variableCodes
     * @return java.util.List
     */
    public List<Map<String, Object>> getManifestVarsType(Long manifestId, List<String> variableCodes) {
        StringBuilder sql = new StringBuilder("select variable_code as name, variable_type as type , order_no from var_process_manifest_header where manifest_id=");
        sql.append(manifestId);
        if (!CollectionUtils.isEmpty(variableCodes)) {
            sql.append(" and variable_code in ('");
            sql.append(StringUtils.join(variableCodes, "','"));
            sql.append("')");
        }
        sql.append(" order by order_no");

        final List<Map<String, Object>> maps = dbOperateService.queryForList(sql.toString());
        for (Map<String, Object> map : maps) {
            map.put("type", DataType.typeOf(map.get("type").toString()));
        }
        return maps;
    }

    List<Map<String, Object>> queryData(IndexCalculateContext calculateContext, String tableName, String sqlTemplate) {
        final List<Object> manifestVarNames = calculateContext.getAllManifestVarsType().stream().map(map -> map.get("name")).collect(Collectors.toList());
        final List<String> colNames = calculateContext.getCurrentVarsType().stream().map(map -> map.get("name").toString()).collect(Collectors.toList());
        String targetName = null;
        if (calculateContext.getIvMappingVo() != null && calculateContext.getIvMappingVo().getSourceType() == MagicNumbers.ZERO) {
            targetName = calculateContext.getIvMappingVo().getTargetField();
            if (!colNames.contains(targetName)) {
                colNames.add(targetName);
            }
        }
        final List<DataType> types = calculateContext.getCurrentVarsType().stream().map(map -> (DataType) map.get("type")).collect(Collectors.toList());
        final List<Integer> indexList = colNames.stream().map(manifestVarNames::indexOf).collect(Collectors.toList());
        int offset = MagicNumbers.ZERO;
        int limit = MagicNumbers.INT_10000;
        sqlTemplate = sqlTemplate + " limit %d offset %d";
        List<Map<String, Object>> ret = new ArrayList<>();
        while (true) {
            String sql = String.format(sqlTemplate, limit, offset);
            List<Map<String, Object>> dataList = dbOperateService.queryForListOfDynamicTable(tableName,sql);
            if (CollectionUtils.isEmpty(dataList)) {
                break;
            }
            for (Map<String, Object> valMap : dataList) {
                final String varStr = valMap.get(MYSQL_VARIABLES).toString();
                final List<String> data = Arrays.asList(varStr.split(CommonConstant.SEPARATOR));
                Map<String, Object> row = new HashMap<>(colNames.size());
                for (int j = 0; j < colNames.size(); j++) {
                    final String colName = colNames.get(j);
                    final Integer index = indexList.get(j);
                    final DataType type = types.get(j);
                    row.put(colName, getCellData(data.get(index), type));
                    if (targetName != null && targetName.equals(colName)) {
                        row.put(CommonConstant.TARGET_NAME, getCellData(data.get(index), type));
                    }
                }
                row.put(SERIAL_NO, valMap.get(SERIAL_NO));
                ret.add(row);
            }
            offset += limit;
        }
        return ret;
    }

    int getVarIndex(List<Map<String, Object>> allManifestVarsType, String varName) {
        final List<Object> manifestVarNames = allManifestVarsType.stream().map(map -> map.get("name")).collect(Collectors.toList());
        return manifestVarNames.indexOf(varName);
    }

    Object getCellData(String data, DataType type) {
        Object obj = null;
        try {
            switch (type) {
                case INT:
                    obj = Integer.valueOf(data);
                    break;
                case DOUBLE:
                    obj = Double.valueOf(data);
                    break;
                case STRING:
                    obj = data;
                    break;
                case DATE:
                    obj = DateUtils.parseStrToDate(data, "yyyy-MM-dd");
                    break;
                case DATETIME:
                    obj = DateUtils.parseStrToDate(data, "yyyy-MM-dd HH:mm:ss");
                    break;
                case BOOLEAN:
                    obj = Boolean.valueOf(data);
                    break;
                default:
                    obj = null;
            }
        } catch (Exception e) {
            obj = null;
        }

        return obj;
    }


    private void addRelevanceVars(IndexCalculateContext calculateContext, List<Map<String, Object>> fieldMapList) {
        final List<Object> varNames = fieldMapList.stream().map(map -> map.get("name")).collect(Collectors.toList());
        if (calculateContext.getIndexMappingVo().isIv() && calculateContext.getIvMappingVo() != null && calculateContext.getIvMappingVo().getSourceType() != null && calculateContext.getIvMappingVo().getSourceType() == 0) {
            final String targetField = calculateContext.getIvMappingVo().getTargetField();
            if (!varNames.contains(targetField)) {
                Map<String, Object> y = new HashMap<>(MagicNumbers.EIGHT);
                y.put("name", targetField);
                y.put("type", DataType.STRING);
                y.put("needDelete", true);
                fieldMapList.add(y);
            }
        }
        if (calculateContext.getIndexMappingVo().isPsi() && calculateContext.getPsiMappingVo() != null && StringUtils.isNotEmpty(calculateContext.getPsiMappingVo().getBaseIndex())) {
            final String baseIndex = calculateContext.getPsiMappingVo().getBaseIndex();
            if (!varNames.contains(baseIndex)) {
                Map<String, Object> base = new HashMap<>(MagicNumbers.EIGHT);
                base.put("name", baseIndex);
                base.put("type", DataType.STRING);
                base.put("needDelete", true);
                fieldMapList.add(base);
            }
        }
    }


    /**
     * 获取表名
     *
     * @param paramId 参数Id
     * @return 表名
     */
    protected abstract String getTableName(Long paramId);

    /**
     * 获取数据的列表
     *
     * @param tableName        表名
     * @param calculateContext 计算上下文对象
     * @return 数据的列表
     */
    protected abstract List<Map<String, Object>> getDataList(String tableName, IndexCalculateContext calculateContext);

    /**
     * 根据参数Id获取姓名的map
     *
     * @param paramId 参数Id
     * @return 姓名的map
     */
    protected abstract Map<String, String> getIndexNameMap(Long paramId);

    /**
     * 获取计算psi参数的基准数据、目标数据
     *
     * @param result           统计结果
     * @param calculateContext 统计分析计算
     * @return 基准数列(key : data1)、目标数列(key: data2)
     */
    public abstract Map<String, List<Object>> getPsiDataMap(StatisticsResultVo result, IndexCalculateContext calculateContext);

    protected void calculateIndex(IndexCalculateContext calculateContext) {
        List<StatisticsResultVo> resultVoList = new ArrayList<>();
        List<Map<String, Object>> fieldMapList = calculateContext.getCurrentVarsType();

        //判断是否要计算特殊值
        if (calculateContext.getCalculateList().contains(SPECIAL_RATIO) && calculateContext.getSpecialMappingVoList().size() == MagicNumbers.ONE) {
            if (calculateContext.getSpecialMappingVoList().get(0).getDataType() == null) {
                calculateContext.getCalculateList().remove(SPECIAL_RATIO);
            }
        }

        Map<String, String> varNameMap = calculateContext.getVarNameMap();

        for (Map<String, Object> fieldMap : fieldMapList) {
            String varName = fieldMap.get("name").toString();
            if (varNameMap.get(varName) == null) {
                continue;
            }
            if (fieldMap.get("needDelete") != null && (boolean) fieldMap.get("needDelete")) {
                continue;
            }

            StatisticsResultVo resultVo = new StatisticsResultVo();
            resultVo.setVarCode(varName);
            resultVo.setVarName(varNameMap.get(varName));
            resultVo.setDataType(((DataType) fieldMap.get("type")).getName());
            for (String varCode : calculateContext.getCalculateList()) {
                IndexCalculateStrategy calculateStrategy = calculateStrategyFactory.getCalculateStrategy(varCode);
                calculateStrategy.calculateVar(resultVo, calculateContext);
            }
            resultVoList.add(resultVo);
        }

        calculateContext.setResultList(resultVoList);

    }

    /**
     * 保存数据
     *
     * @param calculateContext 计算上下文对象
     * @param isDelete         是否删除
     */
    protected abstract void saveData(IndexCalculateContext calculateContext, boolean isDelete);

    protected List<String> getIndexList(AnalysisIndexMappingVo indexMappingVo) {
        List<String> indexList = new ArrayList<>();
        indexList.add(AnalysisIndexEnum.COMMON.getCode());

        if (indexMappingVo.isIv()) {
            indexList.add(AnalysisIndexEnum.IV.getCode());
        }
        if (indexMappingVo.isPsi()) {
            indexList.add(AnalysisIndexEnum.PSI.getCode());
        }
        if (indexMappingVo.isZeroRatio()) {
            indexList.add(AnalysisIndexEnum.ZERO_RATIO.getCode());
        }
        if (indexMappingVo.isUniqueNum()) {
            indexList.add(AnalysisIndexEnum.UNIQUE_NUM.getCode());
        }
        if (indexMappingVo.isPercentage()) {
            indexList.add(AnalysisIndexEnum.PERCENTAGE.getCode());
        }
        if (indexMappingVo.isMissingRatio()) {
            indexList.add(AnalysisIndexEnum.MISSING_RATIO.getCode());
        }
        if (indexMappingVo.isSpecialRatio()) {
            indexList.add(AnalysisIndexEnum.SPECIAL_RATIO.getCode());
        }

        return indexList;
    }

    protected void handleMissingConfig(IndexCalculateContext calculateContext) {
        Map<String, VarProcessConfigDefault> dataTypeConfigMap =
                varProcessConfigDefaultValueService.list(new QueryWrapper<VarProcessConfigDefault>().lambda()
                        .eq(VarProcessConfigDefault::getVarProcessSpaceId, CommonConstant.DEFAULT_SPACE_ID)).stream().collect(Collectors.toMap(VarProcessConfigDefault::getDataType, x -> x));
        calculateContext.setDataTypeConfigMap(dataTypeConfigMap);
    }
}
