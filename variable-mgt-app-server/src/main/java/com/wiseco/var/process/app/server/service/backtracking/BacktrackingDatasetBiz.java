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
package com.wiseco.var.process.app.server.service.backtracking;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.CaseFormat;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingDatasetQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.FieldConditionVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingMainifestResultVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskCodeVO;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.service.DbOperateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mingao
 * @since 2023/9/9
 */
@RefreshScope
@Service
@Slf4j
public class BacktrackingDatasetBiz {

    @Autowired
    private VarProcessBatchBacktrackingMapper varBatchBacktrackingMapper;

    @Resource
    private DbOperateService dbOperateService;

    @Autowired
    private BacktrackingService backtrackingService;


    private static final ThreadLocal<DateFormat> DATE_TIME_FORMAT = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static final String DESC = "DESC";
    private static final String BACKTRACT_CODE = "batch_no";

    private static final HashMap<String, String> FIXED_COLUM = new HashMap<>();

    private static final String VAR_PROCESS_MANIFEST_HEADER = "var_process_manifest_header";

    static {
        FIXED_COLUM.put("external_serial_no", "string");
        FIXED_COLUM.put("batch_no", "string");
        FIXED_COLUM.put("request_time", "datetime");
    }

    /**
     * 通过行分页，列分页来获取变量数据
     *
     * @param queryVo 变量数据查询信息
     * @return 变量数据
     */
    public BacktrackingMainifestResultVO pageManifestData(BacktrackingDatasetQueryVO queryVo) {
        return newPageManifestData(queryVo);
    }

    /**
     * 通过行分页，列分页来获取变量数据
     *
     * @param queryVo 变量数据查询信息
     * @return 变量数据
     */
    public BacktrackingMainifestResultVO newPageManifestData(BacktrackingDatasetQueryVO queryVo) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getById(queryVo.getBacktrackingId());
        if (!dbOperateService.isTableExist(backtracking.getResultTable())) {
            return null;
        }
        //key：变量 value:索引位置
        Map<String, Integer> variableIndex = getTableHead(queryVo, backtracking);

        //获取数据
        List<Map<String, Object>> dataRecords = getDataRecords(queryVo, backtracking, variableIndex);

        //表头
        List<String> columnRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(queryVo.getColumns())) {
            //如果用户自选显示列
            columnRecords = queryVo.getColumns();
            queryVo.getColumnPageInfo().setTotalCount(queryVo.getColumns().size());
        } else if (queryVo.getColumnPageInfo().getPageNo() == MagicNumbers.ONE) {
            //如果分页为第一页，则添加固定列
            columnRecords.addAll(FIXED_COLUM.keySet());
            columnRecords.addAll(variableIndex.keySet());
        } else {
            columnRecords.addAll(variableIndex.keySet());
        }

        //组装返回
        BacktrackingMainifestResultVO backtrackingMainifestResultVO = new BacktrackingMainifestResultVO();
        backtrackingMainifestResultVO.setColumnRecords(columnRecords);
        backtrackingMainifestResultVO.setColumnPageInfo(queryVo.getColumnPageInfo());
        backtrackingMainifestResultVO.setRowPageInfo(queryVo.getRowPageInfo());
        backtrackingMainifestResultVO.setDataRecords(dataRecords);
        return backtrackingMainifestResultVO;
    }

    private List<Map<String, Object>> getDataRecords(BacktrackingDatasetQueryVO queryVo, VarProcessBatchBacktracking backtracking, Map<String, Integer> variableIndex) {
        // 生成where条件
        String rowQueryCondition = generateWhereCondition(queryVo.getFieldConditions(), queryVo.getBacktrackingId(), queryVo.getBacktrackingTaskCodes());
        //总行数
        int count = dbOperateService.queryCount(backtracking.getResultTable(), rowQueryCondition);
        queryVo.getRowPageInfo().setTotalCount(count);

        //分页 起始页
        int rowStartNo = (queryVo.getRowPageInfo().getPageNo() - 1) * queryVo.getRowPageInfo().getPageSize();

        //排序条件
        LinkedHashMap<String, String> orderColum = new LinkedHashMap<>();
        String order = queryVo.getOrder();
        if (!StringUtils.isEmpty(order)) {
            String sortType = order.substring(order.lastIndexOf("_") + 1);
            String sortKey = order.substring(0, order.lastIndexOf("_"));
            sortKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortKey);
            orderColum.put(sortKey, sortType);
        }

        //查询行 key:variables value:值
        List<Map<String, Object>> rowMapList = dbOperateService.queryForList(backtracking.getResultTable(), null,
                rowQueryCondition, orderColum, rowStartNo, queryVo.getRowPageInfo().getPageSize());

        //组装数据
        List<Map<String, Object>> dataRecords = new ArrayList<>();

        //循环
        for (Map<String, Object> rowMap : rowMapList) {
            String[] dataArray = String.valueOf(rowMap.get("variables")).split(CommonConstant.SEPARATOR);
            Map<String, Object> dataMap = new LinkedHashMap<>();

            if (!CollectionUtils.isEmpty(queryVo.getColumns())) {
                //用户自己选择了显示,添加显示列中的固定列
                queryVo.getColumns().stream().filter(FIXED_COLUM::containsKey).forEach(item -> {
                    if ("request_time".equals(item)) {
                        Object time = rowMap.get(item);
                        if (time instanceof Date) {
                            dataMap.put(item, DateUtil.format((Date) time, "yyyy-MM-dd HH:mm:ss"));
                        } else {
                            dataMap.put(item, DateUtil.format((LocalDateTime) time, "yyyy-MM-dd HH:mm:ss"));
                        }
                    } else {
                        dataMap.put(item, rowMap.get(item));
                    }
                });
            } else if (queryVo.getColumnPageInfo().getPageNo() == MagicNumbers.ONE) {
                //列分页第一页，添加的固定列
                FIXED_COLUM.keySet().forEach(key -> {
                    if ("request_time".equals(key)) {
                        Object time = rowMap.get(key);
                        if (time instanceof Date) {
                            dataMap.put(key, DateUtil.format((Date) time, "yyyy-MM-dd HH:mm:ss"));
                        } else {
                            dataMap.put(key, DateUtil.format((LocalDateTime) time, "yyyy-MM-dd HH:mm:ss"));
                        }
                    } else {
                        dataMap.put(key, rowMap.get(key));
                    }
                });
            }

            variableIndex.forEach((key, value) -> {
                dataMap.put(key, dataArray[value]);
            });
            dataRecords.add(dataMap);
        }
        return dataRecords;
    }

    private Map<String, Integer> getTableHead(BacktrackingDatasetQueryVO queryVo, VarProcessBatchBacktracking backtracking) {

        //查询列 key:variable_code value:变量代码
        List<Map<String, Object>> columnMapList;

        //列分页条件
        StringBuilder columnQueryCondition = new StringBuilder();
        columnQueryCondition.append(" manifest_id = ").append(backtracking.getManifestId());

        //如果选择了显示列，则不进行列分页
        if (!CollectionUtils.isEmpty(queryVo.getColumns())) {
            //过滤掉固定列
            List<String> collect = queryVo.getColumns().stream().filter(item -> !FIXED_COLUM.containsKey(item)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                columnQueryCondition.append(" and variable_code in (");
                collect.forEach(item -> {
                    columnQueryCondition.append("'").append(item).append("'").append(",");
                });
                columnQueryCondition.deleteCharAt(columnQueryCondition.length() - 1);
                columnQueryCondition.append(")");
            } else {
                return new LinkedHashMap<>();
            }

            columnMapList = dbOperateService.queryForList(VAR_PROCESS_MANIFEST_HEADER, Arrays.asList("variable_code", "order_no"),
                    columnQueryCondition.toString(), null, 0, Integer.MAX_VALUE);

        } else {
            //起始页码
            int columnFrom;
            //分页大小
            int columnSize;
            if (queryVo.getColumnPageInfo().getPageNo() == MagicNumbers.ONE) {
                columnFrom = 0;
                columnSize = queryVo.getColumnPageInfo().getPageSize() - FIXED_COLUM.size();
            } else {
                columnFrom = (queryVo.getRowPageInfo().getPageNo() - 1) * (queryVo.getColumnPageInfo().getPageSize() - FIXED_COLUM.size());
                columnSize = queryVo.getColumnPageInfo().getPageSize();
            }

            columnMapList = dbOperateService.queryForList(VAR_PROCESS_MANIFEST_HEADER, Arrays.asList("variable_code", "order_no"),
                    columnQueryCondition.toString(), null, columnFrom, columnSize);
        }

        //表头
        Map<String, Integer> columnRecords = new LinkedHashMap<>();
        columnMapList.forEach(item -> columnRecords.put(String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_code")), Integer.valueOf(String.valueOf(DmAdapter.mapGetIgnoreCase(item, "order_no")))));

        //总列数
        int count = dbOperateService.queryCount(VAR_PROCESS_MANIFEST_HEADER, columnQueryCondition.toString()) + FIXED_COLUM.size();
        queryVo.getColumnPageInfo().setTotalCount(count);

        return columnRecords;
    }


    /**
     * 生成where条件
     *
     * @param fieldConditions       筛选字段
     * @param backtrackingId        批量回溯任务id
     * @param backtrackingTaskCodes 批量回溯任务下的批次号
     * @return where条件
     */
    private String generateWhereCondition(List<FieldConditionVO> fieldConditions, Integer backtrackingId, List<String> backtrackingTaskCodes) {
        StringBuilder sb = new StringBuilder();

        // 拼查询任务批次号条件
        if (backtrackingTaskCodes != null && backtrackingTaskCodes.size() != 0) {
            backtrackingTaskCodes = backtrackingTaskCodes.stream()
                    .map(code -> code.substring(0, code.indexOf(" ")))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        // 拼装where条件
        sb.append(" backtracking_id = ").append(backtrackingId);
        boolean flag = fieldConditions != null || (backtrackingTaskCodes != null && backtrackingTaskCodes.size() != 0);
        if (flag) {
            if (backtrackingTaskCodes.size() != 0) {
                sb.append(" AND ");
                String result = backtrackingTaskCodes.stream()
                        .map(s -> "'" + s + "'")
                        .collect(Collectors.joining(", "));
                String finalResult = "(" + result + ")";
                sb.append(BACKTRACT_CODE).append(" IN ").append(finalResult);
            }
            if (!CollectionUtils.isEmpty(fieldConditions)) {
                for (FieldConditionVO fieldConditionVo : fieldConditions) {
                    sb.append(" AND (").append(fieldConditionVo.buildSql()).append(") ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * getBacktrackingTaskIds
     *
     * @param id id
     * @return List
     */
    public List<String> getBacktrackingTaskIds(long id) {
        return varBatchBacktrackingMapper.findBacktrackingTaskIds(id).stream().map(BacktrackingTaskCodeVO::generateTaskCode).collect(Collectors.toList());
    }

    /**
     * 显示列设置（列名按照A,B,C,D,E...分类）
     *
     * @param backtrackingId 批量回溯id
     * @return 列map集合
     */
    public List<TableFieldVO> getDatasetVars(Long backtrackingId) {
        String backtrackingResultTable = varBatchBacktrackingMapper.findBacktrackingResultTable(backtrackingId);
        if (!dbOperateService.isTableExist(backtrackingResultTable)) {
            return null;
        }

        List<TableFieldVO> fieldVO = new ArrayList<>();
        Map<String, String> columns = dbOperateService.describeTable(backtrackingResultTable);
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            String type = dbOperateService.getDataType(entry.getValue());
            fieldVO.add(TableFieldVO.builder().fieldName(entry.getKey()).fieldType(type.toLowerCase()).build());
        }
        fieldVO = fieldVO.stream().filter(item -> !("increment_id".equals(item.getFieldName()) || "backtracking_id".equals(item.getFieldName()))).collect(Collectors.toList());
        return fieldVO;
    }


    /**
     * getDatasetVars
     *
     * @param backtrackingId 批量回溯id
     * @param type           类型 1.筛选  2.显示列
     * @return java.util.List<com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO>
     */
    public List<TableFieldVO> getDatasetVars(Long backtrackingId, Integer type) {
        List<TableFieldVO> fieldVO;
        if (type == MagicNumbers.ONE) {
            fieldVO = getFilterVars(backtrackingId);
        } else {
            fieldVO = getDisplayVars(backtrackingId);
        }
        return fieldVO;
    }

    private List<TableFieldVO> getFilterVars(Long backtrackingId) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getResultTable, VarProcessBatchBacktracking::getManifestId)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        if (!dbOperateService.isTableExist(backtracking.getResultTable())) {
            return null;
        }
        String condition = " manifest_id = " + backtracking.getManifestId() + " and  is_index = '1'";
        List<Map<String, Object>> dataMapList = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Arrays.asList("variable_code", "variable_type"), condition, null, 0, Integer.MAX_VALUE);

        List<TableFieldVO> fieldVOList = new ArrayList<>();
        dataMapList.forEach(item -> {
            String variableCode = String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_code"));
            String variableType = String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_type"));
            fieldVOList.add(TableFieldVO.builder().fieldName(variableCode).fieldType(variableType).build());
        });

        FIXED_COLUM.forEach((key, value) -> {
            fieldVOList.add(TableFieldVO.builder().fieldName(key).fieldType(value).build());
        });
        return fieldVOList;
    }


    private List<TableFieldVO> getDisplayVars(Long backtrackingId) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getResultTable, VarProcessBatchBacktracking::getManifestId)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        if (!dbOperateService.isTableExist(backtracking.getResultTable())) {
            return null;
        }
        String condition = " manifest_id = " + backtracking.getManifestId();
        LinkedHashMap<String, String> orderColum = new LinkedHashMap<>();
        orderColum.put("order_no", "ASC");
        List<Map<String, Object>> dataMapList = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Arrays.asList("variable_code", "variable_type"), condition, orderColum, 0, Integer.MAX_VALUE);

        List<TableFieldVO> fieldVOList = new ArrayList<>();
        dataMapList.forEach(item -> {
            String variableCode = String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_code"));
            String variableType = String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_type"));
            fieldVOList.add(TableFieldVO.builder().fieldName(variableCode).fieldType(variableType).build());
        });

        FIXED_COLUM.forEach((key, value) -> {
            fieldVOList.add(TableFieldVO.builder().fieldName(key).fieldType(value).build());
        });
        return fieldVOList;
    }

    /**
     * 导出所有数据
     *
     * @param backtrackingId 批量回溯id
     * @param response       HTTP响应
     */
    public void exportAll(Integer backtrackingId, HttpServletResponse response) {
        newExportAll(backtrackingId, response);
    }


    private void newExportAll(Integer backtrackingId, HttpServletResponse response) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getResultTable, VarProcessBatchBacktracking::getManifestId)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        if (!dbOperateService.isTableExist(backtracking.getResultTable())) {
            return;

        }

        String condition = " manifest_id = " + backtracking.getManifestId();
        LinkedHashMap<String, String> orderColum = new LinkedHashMap<>();
        orderColum.put("order_no", "ASC");
        //查询配置表
        List<Map<String, Object>> columnMapList = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Arrays.asList("variable_code", "variable_type", "order_no"), condition, orderColum, 0, Integer.MAX_VALUE);


        //表字段<字段 : 字段类型>
        Map<String, String> fieldMap = new LinkedHashMap<>(FIXED_COLUM);
        //添加变量：变量类型
        columnMapList.forEach(item -> {
            fieldMap.put(String.valueOf(item.get("variable_code")), String.valueOf(item.get("variable_type")));
        });


        //变量 : 变量索引位置
        Map<String, Integer> variableIndexMap = new LinkedHashMap<>();
        columnMapList.forEach(item -> variableIndexMap.put(String.valueOf(DmAdapter.mapGetIgnoreCase(item, "variable_code")), Integer.valueOf(String.valueOf(DmAdapter.mapGetIgnoreCase(item, "order_no")))));

        //查询行 key:variables value:值
        condition = " backtracking_id = " + backtracking.getId();
        List<Map<String, Object>> rowMapList = dbOperateService.queryForList(backtracking.getResultTable(), null, condition, null, 0, Integer.MAX_VALUE);

        //要导出的数据
        List<Map<String, Object>> records = new ArrayList<>();
        //循环
        for (Map<String, Object> rowMap : rowMapList) {
            String[] dataArray = String.valueOf(rowMap.get("variables")).split(CommonConstant.SEPARATOR);

            Map<String, Object> dataMap = new LinkedHashMap<>();
            //列分页第一页，添加的固定列
            FIXED_COLUM.keySet().forEach(key -> {
                if ("request_time".equals(key)) {
                    dataMap.put(key, DATE_TIME_FORMAT.get().format(rowMap.get(key)));
                } else {
                    dataMap.put(key, rowMap.get(key));
                }
            });

            variableIndexMap.forEach((key, value) -> {
                dataMap.put(key, dataArray[value]);
            });
            records.add(dataMap);
        }

        String csvName = backtracking.getResultTable() + ".csv";

        //导出
        exportToCsv(records, fieldMap, csvName, response);
    }


    /**
     * 导出csv文件
     *
     * @param records  表数据
     * @param fieldMap 表字段<字段 : 字段类型>
     * @param csvName  导出文件名
     * @param response http响应
     */
    private void exportToCsv(List<Map<String, Object>> records, Map<String, String> fieldMap, String csvName, HttpServletResponse response) {

        List<String> lines = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            lines.add(StringUtils.arrayToCommaDelimitedString(fieldMap.keySet().toArray(new String[0])));
        } else {
            //首行
            lines.add(StringUtils.arrayToCommaDelimitedString(records.get(0).keySet().toArray(new String[0])));

            for (Map<String, Object> recordMap : records) {
                String[] convertedValues = new String[recordMap.values().size()];
                int i = 0;
                for (Map.Entry<String, Object> entry : recordMap.entrySet()) {
                    String columnName = entry.getKey();

                    if (recordMap.get(columnName) == null) {
                        convertedValues[i] = null;
                        i++;
                        continue;
                    }
                    if (!StringUtils.isEmpty(recordMap.get(columnName))) {
                        convertedValues[i] = recordMap.get(columnName) + "\t";
                    } else {
                        convertedValues[i] = "";
                    }

                    i++;
                }
                lines.add(StringUtils.arrayToCommaDelimitedString(convertedValues));
            }

        }
        CsvWriter writer;
        try {
            response.setContentType("application/csv;charset=GBK");
            response.setCharacterEncoding("GBK");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(csvName, "UTF-8"));
            writer = CsvUtil.getWriter(response.getWriter());
            writer.write(lines);
            response.flushBuffer();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
