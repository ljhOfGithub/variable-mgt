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
package com.wiseco.var.process.app.server.service.monitoring.strategy;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.reportform.DateTimeConvertUtils;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.TimeUnitEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuweikang
 */
public interface GenerateReportFormStrategy {
    /**
     * 生成报表
     *
     * @param inputDto 输入实体类对象
     * @return 监控图表输出Vo
     */
    MonitoringDiagramOutputVo generateReportForm(ReportFormCreateInputDto inputDto);


    /**
     * 获取top数据——展示维度未指定清单
     *
     * @param map        map
     * @param displayTop displayTop
     * @return top数据
     */
    default LinkedHashMap<String, BigDecimal> getTop(LinkedHashMap<String, BigDecimal> map, Integer displayTop) {
        // 1.判空
        if (displayTop == null || displayTop == 0) {
            return map;
        }
        // 2.转换为List
        List<Map.Entry<String, BigDecimal>> entryList = new ArrayList<>(map.entrySet());

        // 3.排序
        entryList.sort((o1, o2) -> {
            if (o1.getValue() == null) {
                return 1;
            } else if (o2.getValue() == null) {
                return MagicNumbers.MINUS_INT_1;
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // 4.定义结果
        LinkedHashMap<String, BigDecimal> topMap = new LinkedHashMap<>();
        for (int i = 0; i < entryList.size() && i < displayTop; i++) {
            Map.Entry<String, BigDecimal> entry = entryList.get(i);
            topMap.put(entry.getKey(), entry.getValue());
        }
        return topMap;
    }

    /**
     * 生成图表——展示维度为监控对象
     *
     * @param dataMap  key-value(serviceId, 指标数据)
     * @param inputDto 输入实体类对象
     * @return 监控图表输出Vo
     */
    default MonitoringDiagramOutputVo generateDiagramOfMonitorObject(LinkedHashMap<String, BigDecimal> dataMap, ReportFormCreateInputDto inputDto) {
        MonitoringDiagramOutputVo monitoringDiagramOutputVo = new MonitoringDiagramOutputVo();
        List<String> xAxis = new ArrayList<>();
        List<String> yAxis = new ArrayList<>();
        List<MonitoringDiagramOutputVo.Series> seriesList = new ArrayList<>();
        MonitoringDiagramOutputVo.Series series = new MonitoringDiagramOutputVo.Series();
        List<MonitoringDiagramOutputVo.Value> valueList = new ArrayList<>();
        String displayName = inputDto.getIndicatorMappingVo().getDisplayName();
        switch (inputDto.getType()) {
            // 1.1 折线图、面积图结构一样
            case LINE_CHART:
            case AREA_CHART:
                series.setType("line");
                dataMap.forEach((key, value) -> {
                    xAxis.add(key);
                    valueList.add(new MonitoringDiagramOutputVo.Value(null, value == null ? "0" : value.toString()));
                });
                series.setName(StringUtils.isEmpty(displayName) ? inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum().getDesc() : displayName);
                series.setData(valueList);
                seriesList.add(series);
                break;
            // 1.2 柱状图
            case HISTOGRAM:
                series.setType("bar");
                dataMap.forEach((key, value) -> {
                    xAxis.add(key);
                    valueList.add(new MonitoringDiagramOutputVo.Value(null, value == null ? "0" : value.toString()));
                });
                series.setName(StringUtils.isEmpty(displayName) ? inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum().getDesc() : displayName);
                series.setData(valueList);
                seriesList.add(series);
                break;
            // 1.3 TOP图(展示维度为时间范围就不可选)
            case TOP_CHART:
                series.setType("bar");
                dataMap.forEach((key, value) -> {
                    yAxis.add(key);
                    valueList.add(new MonitoringDiagramOutputVo.Value(key, value == null ? "0" : value.toString()));
                });
                series.setName(StringUtils.isEmpty(displayName) ? inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum().getDesc() : displayName);
                series.setData(valueList);
                seriesList.add(series);
                break;
            // 1.4 环形图(展示维度为时间范围就不可选)
            case RING_CHART:
                series.setType("pie");
                dataMap.forEach((key, value) -> {
                    valueList.add(new MonitoringDiagramOutputVo.Value(key, value == null ? "0" : value.toString()));
                });
                series.setName(StringUtils.isEmpty(displayName) ? inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum().getDesc() : displayName);
                series.setData(valueList);
                seriesList.add(series);
                break;
            // 1.5 表格
            case TABLE:
                List<MonitoringDiagramOutputVo.Header> headers = new ArrayList<>();
                List<Map<String, String>> datas = new ArrayList<>();
                Map<String, String> map = new HashMap<>(MagicNumbers.ONE);
                dataMap.forEach((key, value) -> {
                            headers.add(new MonitoringDiagramOutputVo.Header(key, key));
                            map.put(key, value == null ? "0" : value.toString());
                        }
                );
                datas.add(map);
                monitoringDiagramOutputVo.setMonitoringTable(new MonitoringDiagramOutputVo.MonitoringTable(headers, datas));
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的报表类型");
        }
        monitoringDiagramOutputVo.setTitle(inputDto.getName());
        monitoringDiagramOutputVo.setType(inputDto.getType());
        monitoringDiagramOutputVo.setMonitorIndicator(inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum());
        monitoringDiagramOutputVo.setMonitoringDiagram(new MonitoringDiagramOutputVo.MonitoringDiagram(xAxis, yAxis, seriesList));
        return monitoringDiagramOutputVo;
    }

    /**
     * 生成图表——展示维度为时间范围
     *
     * @param reportFormType       报表类型
     * @param dateList             时间轴数据
     * @param timeUnit             时间刻度
     * @param dataMap              key:服务名称 value:值
     * @param title                图标的title
     * @param monitorIndicatorEnum 监控指标
     * @return 监控图表输出Vo
     */
    default MonitoringDiagramOutputVo generateDiagramOfTimeScope(ReportFormTypeEnum reportFormType, List<LocalDateTime> dateList, TimeUnitEnum timeUnit, LinkedHashMap<String, List<String>> dataMap, String title, MonitorIndicatorEnum monitorIndicatorEnum) {
        // 1.填充表头或者横轴
        List<String> xAxis = new ArrayList<>();
        ArrayList<MonitoringDiagramOutputVo.Header> headers = new ArrayList<>();
        if (reportFormType == ReportFormTypeEnum.TABLE) {
            headers.add(new MonitoringDiagramOutputVo.Header("", "channel"));
            for (LocalDateTime localDateTime : dateList) {
                String format = getFormat(timeUnit, localDateTime);
                headers.add(new MonitoringDiagramOutputVo.Header(format, format));
            }
        } else {
            for (LocalDateTime localDateTime : dateList) {
                String format = getFormat(timeUnit, localDateTime);
                xAxis.add(format);
            }
        }
        // 2.填充表身或者纵轴
        List<MonitoringDiagramOutputVo.Series> dataList = new ArrayList<>();
        List<Map<String, String>> datas = new ArrayList<>();
        dataMap.forEach((key, value) -> {
            MonitoringDiagramOutputVo.Series series;
            switch (reportFormType) {
                // 1.1 折线图、面积图结构一样
                case LINE_CHART:
                case AREA_CHART:
                    series = new MonitoringDiagramOutputVo.Series();
                    series.setName(key);
                    series.setType("line");
                    series.setData(value.stream().map(item -> new MonitoringDiagramOutputVo.Value(null, item)).collect(Collectors.toList()));
                    dataList.add(series);
                    break;
                // 1.2 柱状图
                case HISTOGRAM:
                    series = new MonitoringDiagramOutputVo.Series();
                    series.setName(key);
                    series.setType("bar");
                    series.setData(value.stream().map(item -> new MonitoringDiagramOutputVo.Value(null, item)).collect(Collectors.toList()));
                    dataList.add(series);
                    break;
                // 1.2 表格
                case TABLE:
                    LinkedHashMap<String, String> objectObjectLinkedHashMap = new LinkedHashMap<>();
                    objectObjectLinkedHashMap.put("channel", key);
                    for (int i = 0; i < headers.size(); i++) {
                        MonitoringDiagramOutputVo.Header header = headers.get(i);
                        if (!objectObjectLinkedHashMap.containsKey(header.getValue()) && i > 0) {
                            objectObjectLinkedHashMap.put(header.getValue(), value.get(i - 1));
                        }
                    }
                    datas.add(objectObjectLinkedHashMap);
                    break;
                default:
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的报表类型: " + reportFormType);
            }
        });
        // 3.包装输出类
        MonitoringDiagramOutputVo monitoringDiagramOutputVo = new MonitoringDiagramOutputVo();
        if (reportFormType == ReportFormTypeEnum.TABLE) {
            MonitoringDiagramOutputVo.MonitoringTable monitoringTable = new MonitoringDiagramOutputVo.MonitoringTable();
            monitoringTable.setHeaders(headers);
            monitoringTable.setDatas(datas);
            monitoringDiagramOutputVo.setMonitoringTable(monitoringTable);
        } else {
            MonitoringDiagramOutputVo.MonitoringDiagram monitoringDiagram = new MonitoringDiagramOutputVo.MonitoringDiagram();
            monitoringDiagram.setXAxis(xAxis);
            monitoringDiagram.setDataList(dataList);
            monitoringDiagramOutputVo.setMonitoringDiagram(monitoringDiagram);
        }
        monitoringDiagramOutputVo.setTitle(title);
        monitoringDiagramOutputVo.setType(reportFormType);
        monitoringDiagramOutputVo.setMonitorIndicator(monitorIndicatorEnum);
        return monitoringDiagramOutputVo;
    }

    /**
     * 格式化时间
     *
     * @param timeUnit      时间刻度
     * @param localDateTime 时间
     * @return java.lang.String
     */
    default String getFormat(TimeUnitEnum timeUnit, LocalDateTime localDateTime) {
        String format;
        switch (timeUnit) {
            case HOUR:
                format = DateTimeConvertUtils.getStringByHour(localDateTime);
                break;
            case DAY:
                format = DateTimeConvertUtils.getStringByDay(localDateTime);
                break;
            case WEEK:
                format = DateTimeConvertUtils.getStringByWeek(localDateTime);
                break;
            case MONTH:
                format = DateTimeConvertUtils.getStringByMonth(localDateTime);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的时间刻度");
        }
        return format;
    }

    /**
     * 根据开始时间和结束时间获取一个时间范围
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param timeUnit  时间刻度(小时、天、周和月)
     * @return 时间范围
     */
    default LinkedHashMap<LocalDateTime, LocalDateTime> getTimeZone(LocalDateTime startTime, LocalDateTime endTime, TimeUnitEnum timeUnit) {
        LinkedHashMap<LocalDateTime, LocalDateTime> timeZone = new LinkedHashMap<>();
        switch (timeUnit) {
            case HOUR:
                timeZone = DateTimeConvertUtils.getByHour(startTime, endTime);
                break;
            case DAY:
                timeZone = DateTimeConvertUtils.getByDay(startTime, endTime);
                break;
            case WEEK:
                timeZone = DateTimeConvertUtils.getByWeek(startTime, endTime);
                break;
            case MONTH:
                timeZone = DateTimeConvertUtils.getByMonth(startTime, endTime);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "时间刻度不对!");
        }
        return timeZone;
    }


    /**
     * 获取百分比的形式(给前端展示, 只能用于服务报表的失败率+响应码占比, 还有指标报表的特殊值占比+缺失率)
     *
     * @param number 数字
     * @return 保留四位小数后的结果
     */
    default BigDecimal getPercentage(BigDecimal number) {
        BigDecimal result = null;
        if (number.compareTo(BigDecimal.valueOf(MagicNumbers.ONE)) <= 0) {
            result = number.multiply(BigDecimal.valueOf(MagicNumbers.ONE_HUNDRED));
            result = result.setScale(MagicNumbers.TWO, RoundingMode.HALF_UP);
            return result;
        } else {
            return number;
        }
    }

    /**
     * 获取一个个的响应码
     *
     * @param str 入参
     * @return 响应码的list
     */
    default List<String> getResponseCode(String str) {
        // 1.准备好返回变量
        List<String> result = new ArrayList<>();
        // 2.预处理
        String[] split = str.split(MagicStrings.COMMA_ENGLISH);
        for (String s : split) {
            result.add(s.replace(MagicStrings.SPACE, MagicStrings.EMPTY_STRING));
        }
        return result;
    }
}
