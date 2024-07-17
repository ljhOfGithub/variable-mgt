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
package com.wiseco.var.process.app.server.service;

import com.wiseco.var.process.app.server.commons.constant.DbTypeConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.ExcelExportUtil;
import com.wiseco.var.process.app.server.controller.vo.input.TraceQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.TraceListOutputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.TraceBusinessTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.PagedQueryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wiseco.var.process.app.server.service.VariablePmdCheckBiz.EXCEL_MAX_CHARS;

/**
 * @author wuweikang
 */
@Service
@Slf4j
public class TraceBiz {

    public static final String TRACE_NODES_LIST_SQL = "SELECT * from  var_process_trace_log ";
    public static final String TRACE_VARIABLES_LIST_SQL = "SELECT * from  var_process_trace_log ";
    public static final int TRACE_TYPE_NODE = 1;
    public static final int TRACE_TYPE_VARIABLE = 2;

    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;

    @Value("${spring.datasourcetype:mysql}")
    private String dataSourceType;

    /**
     * 获取trace节点信息列表
     *
     * @param traceQueryInputVO traceQueryInputVO
     * @return trace节点信息列表
     */
    public List<TraceListOutputVO> getTraceNodeList(TraceQueryInputVO traceQueryInputVO) {
        StringBuilder sql = new StringBuilder(TRACE_NODES_LIST_SQL);
        //构造where条件
        String condition = buildCondition(traceQueryInputVO, MagicNumbers.ONE);
        sql.append(condition);
        log.info("查询trace节点信息列表->{}", sql);

        return getData(sql.toString());
    }

    /**
     * 分页查询trace变量信息
     *
     * @param traceQueryInputVO traceQueryInputVO
     * @return trace变量信息
     */
    public PagedQueryResult<TraceListOutputVO> getTraceVariableList(TraceQueryInputVO traceQueryInputVO) {
        StringBuilder sql = new StringBuilder(TRACE_VARIABLES_LIST_SQL);
        int currentNo = traceQueryInputVO.getCurrentNo();
        int size = traceQueryInputVO.getSize();
        //构造where条件
        String condition = buildCondition(traceQueryInputVO, MagicNumbers.TWO);
        if (DbTypeConstant.SQLSERVER.equals(dataSourceType)) {
            sql.append(condition).append("order by id").append(MessageFormat.format(" offset {0} rows fetch next {1} rows only ",Math.max(currentNo - 1, 0) * size,size));
        } else {
            sql.append(condition).append(" limit ").append(Math.max(currentNo - 1, 0) * size).append(",").append(size);
        }
        log.info("查询trace变量信息列表->{}", sql);
        //查询
        ArrayList<TraceListOutputVO> traceListOutputVos = getData(sql.toString());

        //获取总条数
        String getTotalSql = "SELECT count(*) FROM var_process_trace_log " + condition;
        log.info("查询trace变量信息列表总行数->{}", sql);
        Long total = internalJdbcTemplate.queryForObject(getTotalSql, Long.class);

        PagedQueryResult<TraceListOutputVO> pagedQueryResult = new PagedQueryResult<>();
        pagedQueryResult.setPages(((total == null ? 0 : total) + size - 1) / size);
        pagedQueryResult.setTotal(total);
        pagedQueryResult.setRecords(traceListOutputVos);
        pagedQueryResult.setCurrent((long) currentNo);
        pagedQueryResult.setSize((long) size);
        return pagedQueryResult;
    }


    private String buildCondition(TraceQueryInputVO traceQueryInputVO, int traceType) {
        StringBuilder sql = new StringBuilder("where trace_type =" + traceType);

        if (traceQueryInputVO.getEngineSerialNo() != null) {
            sql.append(" and engine_serial_no = '").append(traceQueryInputVO.getEngineSerialNo()).append("'");
        }

        if (!StringUtils.isEmpty(traceQueryInputVO.getTraceNodeType())) {
            sql.append(" and node_type = '").append(traceQueryInputVO.getTraceNodeType()).append("'");
        }

        if (traceQueryInputVO.getNodeState() != null) {
            sql.append(" and node_state = '").append(traceQueryInputVO.getNodeState()).append("'");
        }

        if (traceQueryInputVO.getStartTime() != null) {
            sql.append(" and duration >= '").append(traceQueryInputVO.getStartTime()).append("'");
        }

        if (traceQueryInputVO.getEndTime() != null) {
            sql.append(" and duration <= '").append(traceQueryInputVO.getEndTime()).append("'");
        }

        if (!StringUtils.isEmpty(traceQueryInputVO.getNodeName()) && traceType == MagicNumbers.ONE) {
            sql.append(" and node_name LIKE '%").append(traceQueryInputVO.getNodeName()).append("%'");
        }

        if (!StringUtils.isEmpty(traceQueryInputVO.getNodeName()) && traceType == MagicNumbers.TWO) {
            sql.append(" and ( node_name LIKE '%").append(traceQueryInputVO.getNodeName()).append("%'");
            sql.append(" or variable_name LIKE '%").append(traceQueryInputVO.getNodeName()).append("%')");
        }

        return sql.toString();
    }

    private ArrayList<TraceListOutputVO> getData(String sql) {
        ArrayList<TraceListOutputVO> traceListOutputVos = new ArrayList<>();
        List<Map<String, Object>> resultMapList = internalJdbcTemplate.queryForList(sql);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> map : resultMapList) {
            TraceListOutputVO traceListOutputVO = new TraceListOutputVO();
            Object nodeName = map.get("node_name");
            if (nodeName != null) {
                traceListOutputVO.setNodeName(nodeName.toString());
            }

            Object variableName = map.get("variable_name");
            if (variableName != null) {
                traceListOutputVO.setVariableName(variableName.toString());
            }

            Object nodeType = map.get("node_type");
            if (nodeType != null) {
                traceListOutputVO.setNodeType(nodeType.toString());
            }

            Object startTime = map.get("start_time");
            if (startTime != null) {
                traceListOutputVO.setStartTime(dateFormat.format(new Date((long) startTime)));
            }

            Object endTime = map.get("end_time");
            if (endTime != null) {
                traceListOutputVO.setEndTime(dateFormat.format(new Date((long) endTime)));
            }


            Object duration = map.get("duration");
            if (duration != null) {
                traceListOutputVO.setDuration(Long.valueOf(duration.toString()));
            }


            Object state = map.get("node_state");
            if (state != null) {
                traceListOutputVO.setNodeState(Integer.valueOf(String.valueOf(state)));
            }

            Object exceptionInfo = map.get("exception_info");
            if (exceptionInfo != null) {
                traceListOutputVO.setExceptionInfo(exceptionInfo.toString());
            }

            StringBuilder remark = new StringBuilder();
            Object businessType = map.get("business_type");
            Object outsideServiceStrategy = map.get("outside_service_strategy");
            //是否是批量回溯的外数节点
            boolean isBacktrackAndOutSide = businessType.equals(TraceBusinessTypeEnum.BACKTRACKING.name()) && outsideServiceStrategy != null;
            if (isBacktrackAndOutSide) {
                BacktrackingOutsideCallStrategyEnum backtrackingOutsideCallStrategyEnum = BacktrackingOutsideCallStrategyEnum.fromName(outsideServiceStrategy.toString());
                if (backtrackingOutsideCallStrategyEnum != null) {
                    remark.append(backtrackingOutsideCallStrategyEnum.getDesc()).append(",");
                }
            }

            if (nodeType != null && "外数调用".equals(nodeType.toString())) {
                Object interfaceQueryState = map.get("interface_query_state");
                Object interfaceQueryResult = map.get("interface_query_result");
                if (interfaceQueryState == null || interfaceQueryResult == null) {
                    remark.append("查询失败,调用异常,");
                } else if ("1".equals(interfaceQueryState.toString()) && "0".equals(interfaceQueryResult.toString())) {
                    remark.append("查询成功,未查得");
                } else if ("1".equals(interfaceQueryState.toString()) && "1".equals(interfaceQueryResult.toString())) {
                    if (isBacktrackAndOutSide) {
                        remark.deleteCharAt(remark.length() - 1);
                    }
                } else {
                    remark.append("查询失败");
                }
            }
            traceListOutputVO.setRemark(remark.toString());
            traceListOutputVos.add(traceListOutputVO);
        }
        return traceListOutputVos;
    }

    /**
     * 导出节点信息trace
     * @param inputDto 入参
     * @param response 响应
     */
    public void exportNodes(TraceQueryInputVO inputDto, HttpServletResponse response) {
        StringBuilder sql = new StringBuilder(TRACE_NODES_LIST_SQL);
        //构造where条件
        String condition = buildCondition(inputDto, TRACE_TYPE_NODE);
        sql.append(condition);
        log.info("export trace sql ->{}", sql);
        ArrayList<TraceListOutputVO> dataList = getData(sql.toString());

        exportToExcel(inputDto.getEngineSerialNo(),dataList,TRACE_TYPE_NODE,response);
    }

    /**
     * 导出变量信息trace
     * @param inputDto 入参
     * @param response 响应
     */
    public void exportVariables(TraceQueryInputVO inputDto,HttpServletResponse response) {
        StringBuilder sql = new StringBuilder(TRACE_VARIABLES_LIST_SQL);
        //构造where条件
        String condition = buildCondition(inputDto, TRACE_TYPE_VARIABLE);
        sql.append(condition);
        log.info("export trace sql ->{}", sql);
        ArrayList<TraceListOutputVO> dataList = getData(sql.toString());

        exportToExcel(inputDto.getEngineSerialNo(),dataList,TRACE_TYPE_VARIABLE,response);
    }

    private void exportToExcel(Long engineSerialNo,ArrayList<TraceListOutputVO> dataList,Integer traceType, HttpServletResponse response) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("trace信息");

            //表头格式
            XSSFCellStyle headStyle = wb.createCellStyle();
            // 设置背景色
            headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont fontWhite = wb.createFont();
            //白色
            fontWhite.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            //粗体显示
            fontWhite.setBold(true);
            headStyle.setFont(fontWhite);

            //创建表头
            XSSFRow headRow = sheet.createRow(0);
            TraceType traceTypeEnum = TraceType.getByCode(traceType);
            List<String> headers = traceTypeEnum.getHeaders();
            for (int i = 0; i < headers.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headStyle);
            }

            //塞数据
            if (!CollectionUtils.isEmpty(dataList)) {

                // 设置左对齐
                XSSFCellStyle styleCenter = wb.createCellStyle();
                styleCenter.setAlignment(HorizontalAlignment.LEFT);

                //塞数据
                for (int i = 0; i < dataList.size(); i++) {
                    XSSFRow row = sheet.createRow(i + 1);

                    TraceListOutputVO traceListOutputVO = dataList.get(i);
                    XSSFCell cell = initialCell(styleCenter, row,0);
                    cell.setCellValue(traceListOutputVO.getNodeName());
                    cell = initialCell(styleCenter, row,1);
                    cell.setCellValue(traceType == TRACE_TYPE_NODE ? traceListOutputVO.getNodeType() : traceListOutputVO.getVariableName());
                    cell = initialCell(styleCenter, row,MagicNumbers.TWO);
                    cell.setCellValue(traceListOutputVO.getStartTime());
                    cell = initialCell(styleCenter, row,MagicNumbers.THREE);
                    cell.setCellValue(traceListOutputVO.getEndTime());
                    cell = initialCell(styleCenter, row,MagicNumbers.FOUR);
                    cell.setCellValue(traceListOutputVO.getDuration());
                    cell = initialCell(styleCenter, row,MagicNumbers.FIVE);
                    cell.setCellValue(traceListOutputVO.getNodeState() == 1 ? "正常" : "异常");
                    cell = initialCell(styleCenter, row,MagicNumbers.SIX);
                    String exceptionInfo = traceListOutputVO.getRemark() + traceListOutputVO.getExceptionInfo();
                    cell.setCellValue(exceptionInfo.length() >= EXCEL_MAX_CHARS ? exceptionInfo.substring(0,EXCEL_MAX_CHARS - 1) : exceptionInfo);
                }
            }

            //组装excel文件名
            String datePattern = "yyyy-MM-dd_HH_mm_ss";
            String formattedDate = new SimpleDateFormat(datePattern).format(new Date());
            String fileName = formattedDate + "_" + traceTypeEnum.getDesc() + ".xlsx";
            ExcelExportUtil.setResponseHeader(response, fileName);
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("导出数据模型Excel文件异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出数据模型Excel文件异常");
        }
    }

    @NotNull
    private static XSSFCell initialCell(XSSFCellStyle cellStyle, XSSFRow row,int columnIndex) {
        XSSFCell cell;
        cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    @Getter
    @AllArgsConstructor
    public enum TraceType {

        /**
         * 节点信息
         */
        NODE(1,"节点信息",Arrays.asList("节点名称","节点类型","执行时间","结束时间","耗时时间(ms)","节点状态","备注")),
        /**
         * 变量信息
         */
        VARIABLE(2,"变量信息",Arrays.asList("节点名称","变量名称","执行时间","结束时间","耗时时间(ms)","节点状态","异常描述"));

        private Integer code;
        private String desc;
        private List<String> headers;

        /**
         * 根据code匹配枚举
         * @param code code
         * @return TraceType
         */
        public static TraceType getByCode(Integer code) {
            if (Objects.equals(code, NODE.getCode())) {
                return NODE;
            } else if (Objects.equals(code, VARIABLE.getCode())) {
                return VARIABLE;
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "未找到匹配的枚举类型");
            }
        }
    }

}
