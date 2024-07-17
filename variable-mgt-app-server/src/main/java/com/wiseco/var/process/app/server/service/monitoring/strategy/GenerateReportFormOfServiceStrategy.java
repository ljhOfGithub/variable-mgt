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
import com.wiseco.var.process.app.server.controller.vo.MonitorObjectMappingVo;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.ReportFormDisplayDimensionEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.TimeUnitEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.service.statistics.StatisticsCallVolumeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成报表-服务
 *
 * @author wuweikang
 */
@Component("service")
@Slf4j
public class GenerateReportFormOfServiceStrategy implements GenerateReportFormStrategy {

    @Autowired
    private StatisticsCallVolumeService statisticsCallVolumeService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    /**
     * 获取实时服务的监控报表EChart数据
     * @param inputDto 输入实体类对象
     * @return 监控图表输出Vo
     */
    @Override
    public MonitoringDiagramOutputVo generateReportForm(ReportFormCreateInputDto inputDto) {
        MonitoringDiagramOutputVo monitoringDiagramOutputVo = null;
        // 1.监控对象
        MonitorObjectMappingVo monitorObjectMappingVo = inputDto.getMonitorObjectMappingVo();
        List<Long> serviceIds = monitorObjectMappingVo.getServiceIds();
        // 2.监控指标和响应码
        MonitorIndicatorEnum monitoringTarget = inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum();
        String responseCode = inputDto.getIndicatorMappingVo().getResponseCode();
        // 3.展示维度
        ReportFormDisplayDimensionEnum displayDimension = inputDto.getDisplayDimensionVo().getDisplayDimension();
        // 4.展示前n条
        Integer top = inputDto.getDisplayDimensionVo().getDisplayTop();
        // 5.时间刻度
        TimeUnitEnum timeUnit = inputDto.getDisplayDimensionVo().getTimeUnit();
        LocalDateTime startDate = inputDto.getStartTime();
        LocalDateTime endDate = inputDto.getEndTime();
        // 6.根据展示维度, 分别处理
        switch (displayDimension) {
            // 6.1 展示维度为时间范围
            case TIME_SCOPE:
                monitoringDiagramOutputVo = this.getMonitoringDiagramTimeScore(new MonitoringDiagramTimeScopeParams(serviceIds, monitoringTarget, top, timeUnit, startDate, endDate, inputDto.getType(), inputDto.getName(), responseCode));
                break;
            // 6.2 展示维度为监控对象
            case MONITOR_OBJECT:
                monitoringDiagramOutputVo = this.getMonitoringDiagramMonitorObject(inputDto, serviceIds, monitoringTarget, startDate, endDate, responseCode);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的展示维度");
        }

        return monitoringDiagramOutputVo;
    }

    /**
     * 服务报表(横轴为时间范围)时获取报表
     * @param monitoringDiagramTimeScopeParams 服务报表(横轴为时间范围)的入参
     * @return 服务报表(横轴为时间范围时)的报表
     */
    private MonitoringDiagramOutputVo getMonitoringDiagramTimeScore(MonitoringDiagramTimeScopeParams monitoringDiagramTimeScopeParams) {
        // 1.根据时间刻度,获取横轴的时间
        LinkedHashMap<LocalDateTime, LocalDateTime> timeZone = this.getTimeZone(monitoringDiagramTimeScopeParams.getStartDate(), monitoringDiagramTimeScopeParams.getEndDate(), monitoringDiagramTimeScopeParams.getTimeUnit());
        // 2.根据监控指标, 分别采取行动
        BigDecimal data = null;
        List<LocalDateTime> dimension = new ArrayList<>();
        for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
            dimension.add(entry.getKey());
        }
        LinkedHashMap<String, List<String>> result = new LinkedHashMap<>(MagicNumbers.EIGHT);
        // 3.遍历每一个实时服务, 求它的监控指标的数值
        for (Long id : monitoringDiagramTimeScopeParams.getServiceIds()) {
            ServiceInfoDto service = varProcessServiceVersionService.findserviceListByVersionIds(Collections.singletonList(id)).get(0);
            List<String> getData = new ArrayList<>();
            switch (monitoringDiagramTimeScopeParams.getMonitoringTarget()) {
                case CALL_VOLUME:
                    for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
                        data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.CALL_VOLUME, id, entry.getKey(), entry.getValue(), null);
                        getData.add(data.toString());
                    }
                    result.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, getData);
                    break;
                case FAILURE_RATE:
                    for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
                        data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.FAILURE_RATE, id, entry.getKey(), entry.getValue(), null);
                        getData.add(this.getPercentage(data).toString());
                    }
                    result.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, getData);
                    break;
                case MAX_RESPONSE_TIME:
                    for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
                        data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.MAX_RESPONSE_TIME, id, entry.getKey(), entry.getValue(), null);
                        getData.add(data.toString());
                    }
                    result.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, getData);
                    break;
                case AVG_RESPONSE_TIME:
                    for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
                        data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.AVG_RESPONSE_TIME, id, entry.getKey(), entry.getValue(), null);
                        getData.add(data.setScale(MagicNumbers.ZERO, RoundingMode.HALF_UP).toString());
                    }
                    result.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, getData);
                    break;
                case RESPONSE_CODE_RATIO:
                    List<String> responseCode = this.getResponseCode(monitoringDiagramTimeScopeParams.getResponseCode());
                    for (Map.Entry<LocalDateTime, LocalDateTime> entry : timeZone.entrySet()) {
                        BigDecimal sum = new BigDecimal(MagicNumbers.ZERO);
                        for (String code : responseCode) {
                            data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.RESPONSE_CODE_RATIO, id, entry.getKey(), entry.getValue(), code);
                            sum = sum.add(data);
                        }
                        getData.add(this.getPercentage(sum).toString());
                    }
                    result.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, getData);
                    break;
                default:
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "监控指标有误!");
            }
        }
        this.getTopResult(result, monitoringDiagramTimeScopeParams.getTop());
        return this.generateDiagramOfTimeScope(monitoringDiagramTimeScopeParams.getTypeEnum(), dimension, monitoringDiagramTimeScopeParams.getTimeUnit(), result, monitoringDiagramTimeScopeParams.getTitle(), monitoringDiagramTimeScopeParams.getMonitoringTarget());
    }

    /**
     * 获取前top的数据
     * @param result 结果集
     * @param top 前n
     */
    private void getTopResult(LinkedHashMap<String, List<String>> result, Integer top) {
        Comparator<Map.Entry<String, List<String>>> comparator = new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                BigDecimal b1 = BigDecimal.valueOf(o1.getValue().stream().mapToDouble(Double::valueOf).sum());
                BigDecimal b2 = BigDecimal.valueOf(o2.getValue().stream().mapToDouble(Double::valueOf).sum());
                return b2.compareTo(b1);
            }
        };
        if (result.size() > top && top != 0) {
            List<Map.Entry<String, List<String>>> entryList = new ArrayList<>(result.entrySet());
            entryList.sort(comparator);
            Map<String, List<String>> collect = entryList.stream().limit(top).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            result.clear();
            result.putAll(collect);
        }
    }

    /**
     * 服务报表(横轴为监控对象)时获取报表
     * @param inputDto 输入实体类对象
     * @param serviceIds 服务Id的list集合
     * @param monitoringTarget 监控指标枚举
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param code 响应码字符串
     * @return 服务报表(横轴为监控对象时)的报表
     */
    private MonitoringDiagramOutputVo getMonitoringDiagramMonitorObject(ReportFormCreateInputDto inputDto, List<Long> serviceIds, MonitorIndicatorEnum monitoringTarget, LocalDateTime startDate, LocalDateTime endDate, String code) {
        // 1.声明要用到的变量
        MonitoringDiagramOutputVo monitoringDiagramOutputVo;
        LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
        List<ServiceInfoDto> serviceList = varProcessServiceVersionService.findserviceListByVersionIds(serviceIds);
        BigDecimal data = null;
        // 2.根据监控指标来分别计算
        switch (monitoringTarget) {
            case CALL_VOLUME:
                for (ServiceInfoDto service : serviceList) {
                    data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.CALL_VOLUME, service.getId(), startDate, endDate, null);
                    map.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, data);
                }
                break;
            case FAILURE_RATE:
                for (ServiceInfoDto service : serviceList) {
                    data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.FAILURE_RATE, service.getId(), startDate, endDate, null);
                    map.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, this.getPercentage(data));
                }
                break;
            case MAX_RESPONSE_TIME:
                for (ServiceInfoDto service : serviceList) {
                    data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.MAX_RESPONSE_TIME, service.getId(), startDate, endDate, null);
                    map.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, data);
                }
                break;
            case AVG_RESPONSE_TIME:
                for (ServiceInfoDto service : serviceList) {
                    data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.AVG_RESPONSE_TIME, service.getId(), startDate, endDate, null);
                    map.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, data.setScale(MagicNumbers.ZERO, RoundingMode.HALF_UP));
                }
                break;
            case RESPONSE_CODE_RATIO:
                List<String> responseCode = this.getResponseCode(code);
                for (ServiceInfoDto service : serviceList) {
                    BigDecimal sum = new BigDecimal(MagicNumbers.ZERO);
                    for (String str : responseCode) {
                        data = statisticsCallVolumeService.serviceStatics(MonitoringConfTargetEnum.RESPONSE_CODE_RATIO, service.getId(), startDate, endDate, str);
                        sum = sum.add(data);
                    }
                    map.put(service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET, this.getPercentage(sum));
                }
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知指标: " + monitoringTarget);
        }
        // 3.获取topN的服务和指标值
        LinkedHashMap<String, BigDecimal> topMap = this.getTop(map, inputDto.getDisplayDimensionVo().getDisplayTop());
        monitoringDiagramOutputVo = this.generateDiagramOfMonitorObject(topMap, inputDto);
        return monitoringDiagramOutputVo;
    }

    @Data
    @AllArgsConstructor
    private static class MonitoringDiagramTimeScopeParams {
        // 服务Id的集合
        private final List<Long> serviceIds;
        // 监控对象枚举
        private final MonitorIndicatorEnum monitoringTarget;
        // 前n
        private final Integer top;
        // 时间刻度
        private final TimeUnitEnum timeUnit;
        // 开始时间
        private final LocalDateTime startDate;
        // 结束时间
        private final LocalDateTime endDate;
        // 报表类型
        private final ReportFormTypeEnum typeEnum;
        // 报表title
        private final String title;
        // 响应码
        private final String responseCode;
    }
}
