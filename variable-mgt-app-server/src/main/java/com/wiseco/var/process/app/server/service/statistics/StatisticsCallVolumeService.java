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
package com.wiseco.var.process.app.server.service.statistics;

import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.boot.web.util.ExcelExportUtils;
import com.wiseco.decision.common.utils.IdentityGenerator;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.CallVolumeByTimeEnum;
import com.wiseco.var.process.app.server.enums.CallVolumeByWayEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.dto.StatisticsAnalysisDataDTO;
import com.wiseco.var.process.app.server.service.dto.input.CallVolumeDownLoadDto;
import com.wiseco.var.process.app.server.service.dto.input.CallVolumeDto;
import com.wiseco.var.process.app.server.service.dto.output.CallNumberOutputDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author xupei
 */
@Slf4j
@Service
public class StatisticsCallVolumeService {
    @Resource
    private DbOperateService dbOperateService;
    @Resource
    private VarProcessServiceVersionService varProcessServiceVersionService;
    public static final String NUMBER = "number";
    public static final String RATIO = "ratio";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String CHANNEL = "channel";
    public static final String FORMAT = "0.00%";
    private static final String VAR_PROCESS_LOG = "var_process_log";
    public static final String DELETE_SQL = "DELETE FROM var_process_service_call_record_report WHERE call_time >= '%s' and call_time <= '%s'";
    private static final String VAR_PROCESS_SERVICE_CALL_RECORD_REPORT = "var_process_service_call_record_report";

    private static final String SELECT_COUNT = "SELECT count(*) FROM ";

    private static final String ALL_WHERE_CONDITION = " WHERE interface_type = 1 AND request_time >= '%s' AND request_time <= '%s' AND service_id = ";

    private static final String FAILURE_WHERE_CONDITION = " WHERE interface_type = 1 AND request_time >= '%s' AND request_time <= '%s' AND result_status= '失败' AND service_id = ";

    private static final String SELECT_MAX_RESPONSE_TIME = "SELECT max(response_long_time) FROM ";

    private static final String SELECT_AVG_RESPONSE_TIME = "SELECT avg(response_long_time) FROM ";

    private static final String RESPONSE_CODE_WHERE_CONDITION = " WHERE interface_type = 1 AND request_time >= '%s' AND request_time <= '%s' AND response_code = '%s' AND service_id = ";

    /**
     * 统计调用量
     *
     * @param callVolumeDto 参数
     * @return 调用量报表
     */
    public CallNumberOutputDto statisticsCallVolume(CallVolumeDto callVolumeDto) {
        List<StatisticsAnalysisDataDTO> statisticsAnalysisDataDTOList;
        if (!StringUtils.isEmpty(callVolumeDto.getWhichWay())) {
            //统计调用量
            statisticsAnalysisDataDTOList = findDataOfCallNumber(callVolumeDto);
        } else {
            //统计响应时间
            statisticsAnalysisDataDTOList = findDataOfResponseTime(callVolumeDto);
        }

        //缺失值填充
        fillMissingData(statisticsAnalysisDataDTOList, callVolumeDto, getDateFormat(callVolumeDto.getWhichTime()));

        // 适配前端Echarts接收
        return adaptDataToEcharts(statisticsAnalysisDataDTOList, callVolumeDto.getWhichWay());
    }

    /**
     * 获取调用量统计DTO
     *
     * @param callVolumeDto 参数
     * @return 调用量统计DTO
     */
    public List<StatisticsAnalysisDataDTO> findDataOfCallNumber(CallVolumeDto callVolumeDto) {
        //获取时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat(callVolumeDto.getWhichTime()));
        //获取时间区间
        Pair<String, String> sectionTime = getSectionTime(callVolumeDto.getWhichTime());

        //查询数据
        String sql = " SELECT call_time,total_call_number,success_call_number,fail_call_number from var_process_service_call_record_report where service_id = "
                + callVolumeDto.getServiceId() + " and call_time >= '" + sectionTime.getFirst() + "' and call_time < '" + sectionTime.getSecond() + "'"
                + " group by call_time,total_call_number,success_call_number,fail_call_number order by call_time";
        List<StatisticsAnalysisDataDTO> reportList = getStatisticsCallNumberDTOList(sql, dateFormat, callVolumeDto.getWhichWay());

        if (!CollectionUtils.isEmpty(reportList)) {
            //根据时间分组合并调用量
            Map<String, Integer> totalCallSumMap = reportList.stream()
                    .collect(Collectors.groupingBy(StatisticsAnalysisDataDTO::getCallTime, Collectors.summingInt(StatisticsAnalysisDataDTO::getTotalCallNumber)));

            //根据时间分组合并成功量
            Map<String, Integer> successCallSumMap = reportList.stream()
                    .collect(Collectors.groupingBy(StatisticsAnalysisDataDTO::getCallTime, Collectors.summingInt(StatisticsAnalysisDataDTO::getSuccessCallNumber)));

            //根据时间分组合并失败量
            Map<String, Integer> failCallSumMap = reportList.stream()
                    .collect(Collectors.groupingBy(StatisticsAnalysisDataDTO::getCallTime, Collectors.summingInt(StatisticsAnalysisDataDTO::getFailCallNumber)));

            // 将分组后的数据合并成新的对象
            reportList = reportList.stream().collect(Collectors.groupingBy(StatisticsAnalysisDataDTO::getCallTime)).keySet().stream()
                    .map(callTime -> StatisticsAnalysisDataDTO.builder()
                            .callTime(callTime)
                            .totalCallNumber(totalCallSumMap.get(callTime))
                            .successCallNumber(successCallSumMap.get(callTime))
                            .failCallNumber(failCallSumMap.get(callTime)).build())
                    .collect(Collectors.toList());
            //按时间排序
            reportList.sort(Comparator.comparing(StatisticsAnalysisDataDTO::getCallTime));
        }
        return reportList;
    }

    /**
     * 获取响应时间统计DTO
     *
     * @param callVolumeDto 参数
     * @return 调用量统计DTO
     */
    public List<StatisticsAnalysisDataDTO> findDataOfResponseTime(CallVolumeDto callVolumeDto) {
        //获取时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(getDateFormat(callVolumeDto.getWhichTime()));
        //获取时间区间
        Pair<String, String> sectionTime = getSectionTime(callVolumeDto.getWhichTime());
        //查询数据
        String sql = " SELECT call_time,total_call_number,max_response_time,avg_response_time,min_response_time from var_process_service_call_record_report where service_id = "
                + callVolumeDto.getServiceId() + " and call_time >= '" + sectionTime.getFirst() + "' and call_time < '" + sectionTime.getSecond() + "'"
                + " group by call_time,total_call_number,max_response_time,avg_response_time,min_response_time order by call_time";
        List<StatisticsAnalysisDataDTO> reportList = getStatisticsCallNumberDTOList(sql, dateFormat, callVolumeDto.getWhichWay());

        if (!CollectionUtils.isEmpty(reportList)) {
            // 根据时间分组合并数据
            Map<String, StatisticsAnalysisDataDTO> callTimeDtoMap = reportList.stream()
                    .collect(
                            Collectors.groupingBy(StatisticsAnalysisDataDTO::getCallTime,
                                    //Collector.of方法创建一个收集器
                                    Collector.of(
                                            // 初始值 0: sum(平均响应时间*调用量), 1: 最大响应时间, 2: 最小响应时间, 3: sum(调用量)
                                            () -> new int[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0},
                                            (acc, dto) -> {
                                                acc[0] += dto.getAvgResponseTime() * dto.getTotalCallNumber();
                                                acc[1] = Math.max(acc[1], dto.getMaxResponseTime());
                                                acc[MagicNumbers.TWO] = Math.min(acc[MagicNumbers.TWO], dto.getMinResponseTime());
                                                acc[MagicNumbers.THREE] += dto.getTotalCallNumber();
                                            },

                                            (acc1, acc2) -> {
                                                // 合并 avgTime * invocationCall
                                                acc1[0] += acc2[0];
                                                // 合并 maxTime
                                                acc1[1] = Math.max(acc1[1], acc2[1]);
                                                // 合并 minTime
                                                acc1[MagicNumbers.TWO] = Math.min(acc1[MagicNumbers.TWO], acc2[MagicNumbers.TWO]);
                                                // 合并 invocationCall
                                                acc1[MagicNumbers.THREE] += acc2[MagicNumbers.THREE];
                                                return acc1;
                                            },

                                            acc -> StatisticsAnalysisDataDTO.builder()
                                                    .maxResponseTime(acc[1])
                                                    .avgResponseTime(acc[0] / acc[MagicNumbers.THREE])
                                                    .minResponseTime(acc[MagicNumbers.TWO])
                                                    .totalCallNumber(acc[MagicNumbers.THREE]).build())
                            )
                    );

            // 将分组后的数据合并
            reportList = callTimeDtoMap.keySet().stream().map(key -> StatisticsAnalysisDataDTO.builder()
                    .callTime(key)
                    .maxResponseTime(callTimeDtoMap.get(key).getMaxResponseTime())
                    .avgResponseTime(callTimeDtoMap.get(key).getAvgResponseTime())
                    .minResponseTime(callTimeDtoMap.get(key).getMinResponseTime()).build()).collect(Collectors.toList());
            reportList.sort(Comparator.comparing(StatisticsAnalysisDataDTO::getCallTime));
        }
        return reportList;
    }

    /**
     * 根据时间范围 获取时间格式
     *
     * @param whichTime 时间范围类型
     * @return 时间格式
     */
    private String getDateFormat(String whichTime) {
        String s = "HH";
        switch (CallVolumeByTimeEnum.getEnumFromName(whichTime)) {
            case TODAY:
            case YESTERDAY:
                s = "HH";
                break;
            case LAST_SEVEN_DAYS:
            case LAST_THIRTY_DAYS:
                s = "yyyy-MM-dd";
                break;
            case LAST_TRIMESTER:
                s = "yyyy-MM";
                break;
            default:
                break;
        }
        return s;
    }

    /**
     * 获取时间区间
     *
     * @param whichTime 时间范围
     * @return 时间区间
     */
    public Pair<String, String> getSectionTime(String whichTime) {
        LocalDateTime now = LocalDateTime.now();
        String startTime = "";
        String endTime = now.format(formatter);
        switch (CallVolumeByTimeEnum.getEnumFromName(whichTime)) {
            case TODAY:
                startTime = now.with(LocalTime.MIN).format(formatter);
                break;
            case YESTERDAY:
                startTime = now.minusDays(MagicNumbers.ONE).with(LocalTime.MIN).format(formatter);
                endTime = now.with(LocalTime.MIN).format(formatter);
                break;
            case LAST_SEVEN_DAYS:
                startTime = now.minusDays(MagicNumbers.SIX).with(LocalTime.MIN).format(formatter);
                break;
            case LAST_THIRTY_DAYS:
                startTime = now.minusDays(MagicNumbers.INT_29).with(LocalTime.MIN).format(formatter);
                break;
            case LAST_TRIMESTER:
                startTime = now.minusMonths(MagicNumbers.TWO).withDayOfMonth(MagicNumbers.ONE).with(LocalTime.MIN).format(formatter);
                break;
            default:
                break;
        }
        return Pair.of(startTime, endTime);
    }


    /**
     * 适配前端Echarts接收
     *
     * @param statisticsAnalysisDataDTOList 调用量统计DTO
     * @param whichWay                      统计方式 number：数量  ratio：比例
     * @return 报表出参
     */
    private CallNumberOutputDto adaptDataToEcharts(List<StatisticsAnalysisDataDTO> statisticsAnalysisDataDTOList, String whichWay) {
        // 表
        CallNumberOutputDto.CallNumberReport table = drawTable(statisticsAnalysisDataDTOList, whichWay);
        // 图
        CallNumberOutputDto.CallNumberDiagram diagram = drawDiagram(statisticsAnalysisDataDTOList, whichWay);
        // 出参
        return new CallNumberOutputDto(table, diagram);
    }

    /**
     * 构建表格
     *
     * @param statisticsAnalysisDataDTOList 调用量统计DTO
     * @param whichWay                      统计方式 number：数量  ratio：比例  空：响应时间
     * @return 表格
     */
    private CallNumberOutputDto.CallNumberReport drawTable(List<StatisticsAnalysisDataDTO> statisticsAnalysisDataDTOList, String whichWay) {
        //表头
        List<CallNumberOutputDto.CallNumberReport.Header> headerList = new ArrayList<>();
        headerList.add(new CallNumberOutputDto.CallNumberReport.Header("统计字段", "channel", "left"));

        //TreeMap对key进行排序，确保数据与日期时间一一对应
        Map<String, String> colum1 = new TreeMap<>();
        Map<String, String> colum2 = new TreeMap<>();
        Map<String, String> colum3 = new TreeMap<>();

        for (StatisticsAnalysisDataDTO dto : statisticsAnalysisDataDTOList) {
            // 构建header
            CallNumberOutputDto.CallNumberReport.Header header = new CallNumberOutputDto.CallNumberReport.Header(dto.getCallTime(), dto.getCallTime(), null);
            headerList.add(header);

            // number:按时间  ratio：按比例 为空：响应时间
            if (NUMBER.equals(whichWay)) {
                colum1.put(dto.getCallTime(), String.valueOf(dto.getTotalCallNumber()));
                colum2.put(dto.getCallTime(), String.valueOf(dto.getSuccessCallNumber()));
                colum3.put(dto.getCallTime(), String.valueOf(dto.getFailCallNumber()));
            } else if (RATIO.equals(whichWay)) {
                colum1.put(dto.getCallTime(), calculateRatio(dto.getSuccessCallNumber(), dto.getTotalCallNumber()));
                colum2.put(dto.getCallTime(), calculateRatio(dto.getFailCallNumber(), dto.getTotalCallNumber()));
            } else {
                colum1.put(dto.getCallTime(), String.valueOf(dto.getMaxResponseTime()));
                colum2.put(dto.getCallTime(), String.valueOf(dto.getAvgResponseTime()));
                colum3.put(dto.getCallTime(), String.valueOf(dto.getMinResponseTime()));
            }
        }

        List<Map<String, String>> dataList = new ArrayList<>();
        if (NUMBER.equals(whichWay)) {
            colum1.put("channel", "调用量");
            colum2.put("channel", "成功量");
            colum3.put("channel", "失败量");
            dataList.add(colum1);
            dataList.add(colum2);
            dataList.add(colum3);
        } else if (RATIO.equals(whichWay)) {
            colum1.put("channel", "成功率");
            colum2.put("channel", "失败率");
            dataList.add(colum1);
            dataList.add(colum2);
        } else {
            colum1.put("channel", "最大用时(ms)");
            colum2.put("channel", "平均用时(ms)");
            colum3.put("channel", "最小用时(ms)");
            dataList.add(colum1);
            dataList.add(colum2);
            dataList.add(colum3);
        }

        CallNumberOutputDto.CallNumberReport table = new CallNumberOutputDto.CallNumberReport();
        table.setHeaders(headerList);
        table.setDatas(dataList);
        return table;
    }

    /**
     * 构建图表
     *
     * @param statisticsAnalysisDataDTOList 调用量统计DTO
     * @param whichWay                      统计方式 number：数量  ratio：比例  空：响应时间
     * @return 图表
     */
    private CallNumberOutputDto.CallNumberDiagram drawDiagram(List<StatisticsAnalysisDataDTO> statisticsAnalysisDataDTOList, String whichWay) {
        //值
        List<CallNumberOutputDto.CallNumberDiagram.Value> xaxisValueList = new ArrayList<>();
        List<CallNumberOutputDto.CallNumberDiagram.Value> colum1 = new ArrayList<>();
        List<CallNumberOutputDto.CallNumberDiagram.Value> colum2 = new ArrayList<>();
        List<CallNumberOutputDto.CallNumberDiagram.Value> colum3 = new ArrayList<>();

        for (StatisticsAnalysisDataDTO dto : statisticsAnalysisDataDTOList) {
            xaxisValueList.add(new CallNumberOutputDto.CallNumberDiagram.Value(dto.getCallTime()));
            if (NUMBER.equals(whichWay)) {
                colum1.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getTotalCallNumber())));
                colum2.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getSuccessCallNumber())));
                colum3.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getFailCallNumber())));
            } else if (RATIO.equals(whichWay)) {
                colum1.add(new CallNumberOutputDto.CallNumberDiagram.Value(calculateRatio(dto.getSuccessCallNumber(), dto.getTotalCallNumber())));
                colum2.add(new CallNumberOutputDto.CallNumberDiagram.Value(calculateRatio(dto.getFailCallNumber(), dto.getTotalCallNumber())));
            } else {
                colum1.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getMaxResponseTime())));
                colum2.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getAvgResponseTime())));
                colum3.add(new CallNumberOutputDto.CallNumberDiagram.Value(String.valueOf(dto.getMinResponseTime())));
            }
        }

        //柱状折线图
        List<CallNumberOutputDto.CallNumberDiagram.Series> seriesList = new ArrayList<>();
        if (NUMBER.equals(whichWay)) {
            CallNumberOutputDto.CallNumberDiagram.Series series1 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("调用量").type("bar").build();
            CallNumberOutputDto.CallNumberDiagram.Series series2 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("成功量").type("line").build();
            CallNumberOutputDto.CallNumberDiagram.Series series3 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("失败量").type("line").build();
            series1.setData(colum1);
            series2.setData(colum2);
            series3.setData(colum3);
            seriesList.add(series1);
            seriesList.add(series2);
            seriesList.add(series3);
        } else if (RATIO.equals(whichWay)) {
            CallNumberOutputDto.CallNumberDiagram.Series series1 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("成功率").type("line").build();
            CallNumberOutputDto.CallNumberDiagram.Series series2 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("失败率").type("line").build();
            series1.setData(colum1);
            series2.setData(colum2);
            seriesList.add(series1);
            seriesList.add(series2);
        } else {
            CallNumberOutputDto.CallNumberDiagram.Series series1 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("最大用时(ms)").type("line").build();
            CallNumberOutputDto.CallNumberDiagram.Series series2 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("平均用时(ms)").type("line").build();
            CallNumberOutputDto.CallNumberDiagram.Series series3 = CallNumberOutputDto.CallNumberDiagram.Series.builder().name("最小用时(ms)").type("line").build();
            series1.setData(colum1);
            series2.setData(colum2);
            series3.setData(colum3);
            seriesList.add(series1);
            seriesList.add(series2);
            seriesList.add(series3);
        }


        CallNumberOutputDto.CallNumberDiagram diagram = new CallNumberOutputDto.CallNumberDiagram();
        //x轴
        CallNumberOutputDto.CallNumberDiagram.Xaxis xaxis = new CallNumberOutputDto.CallNumberDiagram.Xaxis();
        xaxis.setType("category");
        xaxis.setData(xaxisValueList);
        diagram.setXAxis(Collections.singletonList(xaxis));

        //y轴
        CallNumberOutputDto.CallNumberDiagram.Yaxis yaxis = new CallNumberOutputDto.CallNumberDiagram.Yaxis();
        yaxis.setType("value");
        diagram.setSeries(seriesList);
        diagram.setYAxis(Collections.singletonList(yaxis));
        return diagram;
    }

    /**
     * 计算比例
     *
     * @param number 数量
     * @param total  总数
     * @return 比例
     */
    private String calculateRatio(int number, int total) {
        if (number == 0) {
            return String.format("%.2f", 0.00);
        }
        double quotient = (double) number / total;
        double percentage = quotient * MagicNumbers.INT_100;
        // 格式化为百分比形式，保留两位小数
        return String.format("%.2f", percentage);
    }


    /**
     * 填充缺失值
     *
     * @param dtoList       数据
     * @param callVolumeDto 入参
     * @param format        时间格式
     */
    private void fillMissingData(List<StatisticsAnalysisDataDTO> dtoList, CallVolumeDto callVolumeDto, String format) {

        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format);
        Pair<LocalDateTime, LocalDateTime> dateTimePair = getStartAndEndLocalDateTime(callVolumeDto.getWhichTime());
        LocalDateTime startDate = dateTimePair.getFirst();
        LocalDateTime endDate = dateTimePair.getSecond();
        // 为空全部填充0
        if (CollectionUtils.isEmpty(dtoList)) {
            while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
                String startDateString = startDate.format(pattern);
                if (!StringUtils.isEmpty(callVolumeDto.getWhichWay())) {
                    //调用量
                    dtoList.add(StatisticsAnalysisDataDTO.builder().callTime(startDateString).totalCallNumber(0).successCallNumber(0).failCallNumber(0).build());
                } else {
                    //响应时间
                    dtoList.add(StatisticsAnalysisDataDTO.builder().callTime(startDateString).maxResponseTime(0).avgResponseTime(0).minResponseTime(0).build());
                }
                startDate = plusLocalDateTime(callVolumeDto.getWhichTime(), startDate);
            }
            return;
        }

        int i = 0;
        Set<String> callTimeSet = dtoList.stream().map(StatisticsAnalysisDataDTO::getCallTime).collect(Collectors.toSet());
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            String startDateString = startDate.format(pattern);
            startDate = plusLocalDateTime(callVolumeDto.getWhichTime(), startDate);
            //如果改时间段有已存在，直接跳过
            if (callTimeSet.contains(startDateString)) {
                i++;
                continue;
            }
            if (!StringUtils.isEmpty(callVolumeDto.getWhichWay())) {
                //调用量
                dtoList.add(i, StatisticsAnalysisDataDTO.builder().callTime(startDateString).totalCallNumber(0).successCallNumber(0).failCallNumber(0).build());
            } else {
                //响应时间
                dtoList.add(i, StatisticsAnalysisDataDTO.builder().callTime(startDateString).maxResponseTime(0).avgResponseTime(0).minResponseTime(0).build());
            }
            i++;
        }
    }

    private static LocalDateTime plusLocalDateTime(String whichTime, LocalDateTime startDate) {
        switch (CallVolumeByTimeEnum.getEnumFromName(whichTime)) {
            case TODAY:
            case YESTERDAY:
                startDate = startDate.plusHours(1);
                break;
            case LAST_SEVEN_DAYS:
            case LAST_THIRTY_DAYS:
                startDate = startDate.plusDays(1);
                break;
            case LAST_TRIMESTER:
                startDate = startDate.plusMonths(1);
                break;
            default:
                break;
        }
        return startDate;
    }


    /**
     * 根据时间范围 获取时间区间
     *
     * @param whichTime 时间范围
     * @return 时间区间
     */
    private static Pair<LocalDateTime, LocalDateTime> getStartAndEndLocalDateTime(String whichTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now;
        LocalDateTime startDate = now;
        switch (CallVolumeByTimeEnum.getEnumFromName(whichTime)) {
            case TODAY:
                startDate = endDate.with(LocalTime.MIN);
                break;
            case YESTERDAY:
                endDate = endDate.minusDays(MagicNumbers.ONE).with(LocalTime.MAX);
                startDate = endDate.with(LocalTime.MIN);
                break;
            case LAST_SEVEN_DAYS:
                startDate = endDate.minusDays(MagicNumbers.SIX);
                break;
            case LAST_THIRTY_DAYS:
                startDate = endDate.minusDays(MagicNumbers.INT_29);
                break;
            case LAST_TRIMESTER:
                startDate = endDate.minusMonths(MagicNumbers.TWO);
                break;
            default:
                break;
        }
        return Pair.of(startDate, endDate);
    }


    private List<StatisticsAnalysisDataDTO> getStatisticsCallNumberDTOList(String sql, SimpleDateFormat dateFormat, String whichWay) {
        // 取得当前service_id下在规定时间内的数据
        List<Map<String, Object>> queryList = dbOperateService.queryForList(sql);

        List<StatisticsAnalysisDataDTO> statisticsAnalysisDataDTOList;
        //whichWay为空统计响应时间，不为空统计调用量
        if (StringUtils.isEmpty(whichWay)) {
            statisticsAnalysisDataDTOList = queryList.stream().map((map) -> StatisticsAnalysisDataDTO.builder()
                    .callTime(dateFormat.format(map.get("call_time")))
                    .totalCallNumber(Integer.valueOf(map.get("total_call_number").toString()))
                    .maxResponseTime(Integer.valueOf(map.get("max_response_time").toString()))
                    .avgResponseTime(Integer.valueOf(map.get("avg_response_time").toString()))
                    .minResponseTime(Integer.valueOf(map.get("min_response_time").toString()))
                    .build()).collect(Collectors.toList());
        } else {
            statisticsAnalysisDataDTOList = queryList.stream().map((map) -> StatisticsAnalysisDataDTO.builder()
                    .callTime(dateFormat.format(map.get("call_time")))
                    .totalCallNumber(Integer.valueOf(map.get("total_call_number").toString()))
                    .successCallNumber(Integer.valueOf(map.get("success_call_number").toString()))
                    .failCallNumber(Integer.valueOf(map.get("fail_call_number").toString()))
                    .build()).collect(Collectors.toList());
        }
        return statisticsAnalysisDataDTOList;
    }

    /**
     * download
     *
     * @param callVolumeDownLoadDto callVolumeDownLoadDto
     * @param response              callVolumeDownLoadDto
     */
    public void download(CallVolumeDownLoadDto callVolumeDownLoadDto, HttpServletResponse response) {
        CallVolumeDto dto = CallVolumeDto.builder()
                .serviceId(callVolumeDownLoadDto.getServiceId()).whichTime(callVolumeDownLoadDto.getWhichTime()).whichWay(callVolumeDownLoadDto.getWhichWay()).build();
        MultipartFile file = callVolumeDownLoadDto.getFile();
        //查询要导出的数据
        CallNumberOutputDto.CallNumberReport callNumberReport = statisticsCallVolume(dto).getCallNumberReport();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet1;
            if (dto.getWhichWay().isEmpty()) {
                sheet1 = wb.createSheet("响应时间报表");
            } else {
                sheet1 = wb.createSheet("调用量报表");
            }

            //创建表头
            XSSFRow headRow = sheet1.createRow(0);
            List<CallNumberOutputDto.CallNumberReport.Header> headers = callNumberReport.getHeaders();
            for (int i = 0; i < headers.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(headers.get(i).getLabel());
            }

            //如果WhichWay为空则是按响应时间的导出，否则是按调用量的导出
            if (dto.getWhichWay().isEmpty()) {
                insertDataByResponseTime(callNumberReport, wb, sheet1);
            } else {
                insertDataByInvoking(dto, callNumberReport, wb, sheet1);
            }

            XSSFSheet sheet2;
            if (dto.getWhichWay().isEmpty()) {
                sheet2 = wb.createSheet("响应时间统计图");
            } else {
                sheet2 = wb.createSheet("调用量图");
            }
            int pictureIdx = wb.addPicture(file.getInputStream(), Workbook.PICTURE_TYPE_JPEG);
            CreationHelper helper = wb.getCreationHelper();
            Drawing<?> drawing = sheet2.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            // 图片所在列的起始位置
            anchor.setCol1(0);
            // 图片所在行的起始位置
            anchor.setRow1(0);
            // 调整图片大小
            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ExcelExportUtils.setResponseHeader(response, sdf.format(new Date()) + ".xlsx");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("导出数据模型Excel文件异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出数据模型Excel文件异常");
        }
    }

    /**
     * 按调用量统计导出
     *
     * @param dto              dto
     * @param callNumberReport callNumberReport
     * @param wb               wb
     * @param sheet            sheet
     */
    private static void insertDataByInvoking(CallVolumeDto dto, CallNumberOutputDto.CallNumberReport callNumberReport, XSSFWorkbook wb, XSSFSheet sheet) {
        CellStyle percentCellStyle = wb.createCellStyle();
        percentCellStyle.setDataFormat(wb.createDataFormat().getFormat(FORMAT));
        List<Map<String, String>> dataList = callNumberReport.getDatas();
        if (!CollectionUtils.isEmpty(dataList)) {
            for (int i = 0; i < dataList.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                Map<String, String> map = dataList.get(i);
                //channel在map的最后一个元素
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(map.get(CHANNEL));
                List<String> values = new ArrayList<>(map.values());
                for (int j = 0; j < values.size() - 1; j++) {
                    cell = row.createCell(j + 1);
                    //如果是按照数量，则数据格式为数字类型，否则是百分比类型
                    if (dto.getWhichWay().equals(CallVolumeByWayEnum.NUMBER.getName())) {
                        cell.setCellValue(parseInt(values.get(j)));
                    } else {
                        cell.setCellStyle(percentCellStyle);
                        cell.setCellValue(parseDouble(values.get(j)) / MagicNumbers.HUNDRED);
                    }
                }
            }
        }
    }

    /**
     * 按响应时间统计导出
     *
     * @param callNumberReport callNumberReport
     * @param wb               wb
     * @param sheet            sheet
     */
    private static void insertDataByResponseTime(CallNumberOutputDto.CallNumberReport callNumberReport, XSSFWorkbook wb, XSSFSheet sheet) {
        List<Map<String, String>> datas = callNumberReport.getDatas();
        if (datas != null) {
            for (int i = 0; i < datas.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                Map<String, String> map = datas.get(i);
                //channel在map的最后一个元素
                XSSFCell cell = row.createCell(0);
                String channel = map.get(CHANNEL);
                cell.setCellValue(channel);
                List<String> values = new ArrayList<>(map.values());
                for (int j = 0; j < values.size() - 1; j++) {
                    cell = row.createCell(j + 1);
                    cell.setCellValue(parseDouble(values.get(j)));
                }
            }
        }
    }

    /**
     * serviceStatics
     *
     * @param target    指标
     * @param serviceId 服务id
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @param code      响应码
     * @return java.math.BigDecimal
     */
    public BigDecimal serviceStatics(MonitoringConfTargetEnum target, Long serviceId, LocalDateTime startDate, LocalDateTime endDate, String code) {
        StringBuilder sb = new StringBuilder();
        BigDecimal bigDecimal;
        switch (target) {
            case CALL_VOLUME:
                sb.append(SELECT_COUNT).append(VAR_PROCESS_LOG).append(ALL_WHERE_CONDITION).append(serviceId);
                long callVolume = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)), Long.class);
                bigDecimal = new BigDecimal(callVolume);
                sb.delete(MagicNumbers.ZERO, sb.length());
                break;
            case FAILURE_RATE:
                sb.append(SELECT_COUNT).append(VAR_PROCESS_LOG).append(ALL_WHERE_CONDITION).append(serviceId);
                long callCount = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)), Long.class);
                if (callCount == 0) {
                    bigDecimal = new BigDecimal(String.valueOf(BigDecimal.ZERO));
                    break;
                }
                sb.delete(MagicNumbers.ZERO, sb.length());
                //查询失败条数
                sb.append(SELECT_COUNT).append(VAR_PROCESS_LOG).append(FAILURE_WHERE_CONDITION).append(serviceId);
                long failureTate = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)), Long.class);
                bigDecimal = new BigDecimal(failureTate / (callCount * 1.0));
                sb.delete(MagicNumbers.ZERO, sb.length());
                break;
            case MAX_RESPONSE_TIME:
                //查询最大响应时间
                sb.append(SELECT_MAX_RESPONSE_TIME).append(VAR_PROCESS_LOG).append(ALL_WHERE_CONDITION).append(serviceId);
                long maxResponseTime = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)), Long.class);
                bigDecimal = new BigDecimal(maxResponseTime);
                sb.delete(MagicNumbers.ZERO, sb.length());
                break;
            case AVG_RESPONSE_TIME:
                //查询平均响应时间
                sb.append(SELECT_AVG_RESPONSE_TIME).append(VAR_PROCESS_LOG).append(ALL_WHERE_CONDITION).append(serviceId);
                double avgResponseTime = dbOperateService.queryForDouble(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)));
                bigDecimal = new BigDecimal(String.valueOf(avgResponseTime));
                sb.delete(MagicNumbers.ZERO, sb.length());
                break;
            case RESPONSE_CODE_RATIO:
                // 查询响应码占比
                sb.append(SELECT_COUNT).append(VAR_PROCESS_LOG).append(ALL_WHERE_CONDITION).append(serviceId);
                long allCount = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate)), Long.class);
                if (allCount == 0) {
                    bigDecimal = new BigDecimal(String.valueOf(BigDecimal.ZERO));
                    break;
                }
                sb.delete(MagicNumbers.ZERO, sb.length());
                sb.append(SELECT_COUNT).append(VAR_PROCESS_LOG).append(RESPONSE_CODE_WHERE_CONDITION).append(serviceId);
                long responseRation = dbOperateService.queryForLong(String.format(sb.toString(), DateTimeUtils.parse(startDate), DateTimeUtils.parse(endDate), code), Long.class);
                bigDecimal = new BigDecimal(responseRation / (allCount * 1.0));
                sb.delete(MagicNumbers.ZERO, sb.length());
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知指标");
        }
        log.info("sql->{}, serviceId->{}, 值->{}", sb, serviceId, bigDecimal);
        return bigDecimal;
    }


    /**
     * 定时统计分析
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void scheduleStatistics(String startTime, String endTime) {
        //删除
        String deleteSql = String.format(DELETE_SQL, startTime, endTime);
        log.info("统计分析定时统计执行SQl:{}", deleteSql);
        dbOperateService.execute(deleteSql);

        //查询启用和停用的服务
        List<Long> serviceIdList = varProcessServiceVersionService.findServiceIdListByState(Arrays.asList(VarProcessServiceStateEnum.ENABLED, VarProcessServiceStateEnum.DISABLED));

        if (CollectionUtils.isEmpty(serviceIdList)) {
            return;
        }

        //统计
        StringBuilder builderQuerySql = new StringBuilder("select service_id,"
                + "count(*) as total_call_number,"
                + "count(CASE WHEN result_status = '成功' THEN 1 ELSE NULL END) AS success_call_number,\n"
                + "count(CASE WHEN result_status = '失败' THEN 1 ELSE NULL END) AS fail_call_number,\n"
                + "max(response_long_time) as max_response_time,\n"
                + "min(response_long_time) as min_response_time,\n"
                + "avg(response_long_time) as avg_response_time\n"
                + "from var_process_log \n"
                + "where  interface_type = 1 and request_time >= '%s' and request_time <= '%s' and service_id in (");
        serviceIdList.forEach(item -> builderQuerySql.append(item).append(","));
        builderQuerySql.deleteCharAt(builderQuerySql.length() - 1).append(")").append(" group by service_id");
        String querySql = String.format(builderQuerySql.toString(), startTime, endTime);
        log.info("统计分析定时统计执行SQl:{}", querySql);
        //统计服务数据
        List<Map<String, Object>> dataMapList = dbOperateService.queryForList(querySql);
        if (!CollectionUtils.isEmpty(dataMapList)) {
            //数据组装
            List<List<String>> values = new ArrayList<>();
            for (Map<String, Object> data : dataMapList) {
                String serviceId = String.valueOf(data.get("service_id"));
                String allCount = String.valueOf(data.get("total_call_number"));
                String successCount = String.valueOf(data.get("success_call_number"));
                String failCount = String.valueOf(data.get("fail_call_number"));
                String maxResponseTime = String.valueOf(data.get("max_response_time"));
                String minResponseTime = String.valueOf(data.get("min_response_time"));
                String avgResponseTime = String.valueOf(data.get("avg_response_time"));
                values.add(Arrays.asList(String.valueOf(IdentityGenerator.nextId()), serviceId, allCount, successCount, failCount, maxResponseTime, minResponseTime, avgResponseTime, startTime));
            }
            //表头
            List<String> header = Arrays.asList("id", "service_id", "total_call_number", "success_call_number", "fail_call_number", "max_response_time", "min_response_time", "avg_response_time", "call_time");
            //插入
            dbOperateService.batchInsert(VAR_PROCESS_SERVICE_CALL_RECORD_REPORT, header, values);
        }
    }
}

