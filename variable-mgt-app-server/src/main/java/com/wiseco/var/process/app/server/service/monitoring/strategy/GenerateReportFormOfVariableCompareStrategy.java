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
import com.wiseco.var.process.app.server.controller.vo.MonitorObjectMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ReportFormVariableMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormDisplayDimensionEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.statistics.template.OverallProcessStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成报表-指标对比分析
 *
 * @author wuweikang
 */
@Component("variableCompare")
@Slf4j
public class GenerateReportFormOfVariableCompareStrategy implements GenerateReportFormStrategy {
    @Autowired
    private OverallProcessStatistics overallProcessStatistics;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Override
    public MonitoringDiagramOutputVo generateReportForm(ReportFormCreateInputDto inputDto) {
        MonitoringDiagramOutputVo monitoringDiagramOutputVo = null;
        ReportFormDisplayDimensionEnum displayDimension = inputDto.getDisplayDimensionVo().getDisplayDimension();
        switch (displayDimension) {
            case TIME_SCOPE:
                monitoringDiagramOutputVo = generateReportOfTimeScope(inputDto);
                break;
            case MONITOR_OBJECT:
                monitoringDiagramOutputVo = generateReportOfMonitorObject(inputDto);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的展示维度");
        }
        return monitoringDiagramOutputVo;
    }


    /**
     * 生成报表——时间维度
     *
     * @param inputDto inputDto
     * @return com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo
     */
    public MonitoringDiagramOutputVo generateReportOfTimeScope(ReportFormCreateInputDto inputDto) {
        //监控指标
        MonitorIndicatorEnum monitoringTarget = inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum();
        //监控对象与变量清单映射map
        Map<ReportFormVariableMappingVo, List<ServiceManifestNameVo>> variableAndManifestListMapping = getVariableAndManifestListMapping(inputDto.getMonitorObjectMappingVo(), inputDto.getManifests());

        Integer displayTop = inputDto.getDisplayDimensionVo().getDisplayTop();
        //判断是否要排序：排序则先计算每个监控对象的总时间范围内的值,然后过滤出要进行统计的监控对象
        if (displayTop != null && displayTop != 0 && displayTop > variableAndManifestListMapping.size()) {
            //全时间段计算——key:监控对象 value:值
            Map<ReportFormVariableMappingVo, BigDecimal> allVariableAndManifestListMapping = new LinkedHashMap<>();
            variableAndManifestListMapping.forEach((key, value) -> {
                List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(inputDto, key.getVariableCode(), value, inputDto.getStartTime(), inputDto.getEndTime());
                BigDecimal values = getValue(statisticsResultVos, monitoringTarget);
                allVariableAndManifestListMapping.put(key, values);
            });

            //排名topN的监控对象
            List<ReportFormVariableMappingVo> topByMonitoringDiagramOutputVo = getTopByMonitoringDiagramOutputVo(allVariableAndManifestListMapping, displayTop);

            variableAndManifestListMapping = variableAndManifestListMapping.entrySet().stream()
                    .filter(item -> topByMonitoringDiagramOutputVo.contains(item.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        //按时间刻度计算——key:监控对象名称 value:值
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        //按照时间刻度获取时间区间
        LinkedHashMap<LocalDateTime, LocalDateTime> timeZone = getTimeZone(inputDto.getStartTime(), inputDto.getEndTime(), inputDto.getDisplayDimensionVo().getTimeUnit());

        //一层循环监控对象
        variableAndManifestListMapping.forEach((key, value) -> {
            ArrayList<String> values = new ArrayList<>();
            //二层循环时间
            timeZone.forEach((key1, value1) -> {
                List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(inputDto, key.getVariableCode(), value, key1, value1);
                BigDecimal bigDecimal = getValue(statisticsResultVos, monitoringTarget);
                values.add(bigDecimal == null ? null : bigDecimal.toString());
            });
            map.put(key.getVariableName(), values);
        });

        ArrayList<LocalDateTime> timeAxle = new ArrayList<>();
        timeZone.forEach((key, value) -> {
            timeAxle.add(key);
        });
        return generateDiagramOfTimeScope(inputDto.getType(), timeAxle, inputDto.getDisplayDimensionVo().getTimeUnit(), map, inputDto.getName(), inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum());
    }


    /**
     * 生成报表——按照监控对象
     *
     * @param inputDto inputDto
     * @return 监控图表输出Vo
     */
    public MonitoringDiagramOutputVo generateReportOfMonitorObject(ReportFormCreateInputDto inputDto) {
        MonitorIndicatorEnum monitoringTarget = inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum();
        //key：监控对象code value：值
        LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
        Map<ReportFormVariableMappingVo, List<ServiceManifestNameVo>> variableAndManifestListMapping = getVariableAndManifestListMapping(inputDto.getMonitorObjectMappingVo(), inputDto.getManifests());
        //按指标进行计算
        variableAndManifestListMapping.forEach((key, value) -> {
            List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(inputDto, key.getVariableCode(), value, inputDto.getStartTime(), inputDto.getEndTime());
            map.put(key.getVariableName(), getValue(statisticsResultVos, monitoringTarget));
        });
        //获取topN的服务和指标值
        LinkedHashMap<String, BigDecimal> topMap = getTop(map, inputDto.getDisplayDimensionVo().getDisplayTop());
        return generateDiagramOfMonitorObject(topMap, inputDto);
    }


    /**
     * 获取指标对应的变量清单Vo
     *
     * @param inputVo   监控对象vo
     * @param manifests 输入的指标清单Vo(看其是否重复)
     * @return 指标对应的变量清单Vo
     */
    private Map<ReportFormVariableMappingVo, List<ServiceManifestNameVo>> getVariableAndManifestListMapping(MonitorObjectMappingVo inputVo, List<ServiceManifestNameVo> manifests) {
        // 1.首选获取指标对比分析中的所有指标
        List<ReportFormVariableMappingVo> allVariables = inputVo.getVariableMappingVos();
        // 2.开始填充
        Map<ReportFormVariableMappingVo, List<ServiceManifestNameVo>> result = new HashMap<>(MagicNumbers.EIGHT);
        for (ReportFormVariableMappingVo key : allVariables) {
            // 2.1 先找出这个指标所关联的全部变量清单信息(包括了已经选中的)
            List<ServiceManifestNameVo> allManifests = varProcessServiceManifestService.getVariableAndManifestMapping(key.getVariableId());
            List<ServiceManifestNameVo> value = new ArrayList<>();
            // 2.2 依次遍历这个变量清单中的信息，进行对比
            for (ServiceManifestNameVo item : allManifests) {
                for (ServiceManifestNameVo vo : manifests) {
                    if (vo.getServiceId().equals(item.getServiceId()) && vo.getManifestId().equals(item.getManifestId())) {
                        value.add(vo);
                        break;
                    }
                }
            }
            result.put(key, value);
        }
        return result;
    }

    private BigDecimal getValue(List<StatisticsResultVo> statisticsResultVos, MonitorIndicatorEnum monitoringTarget) {
        BigDecimal value = null;
        if (CollectionUtils.isEmpty(statisticsResultVos)) {
            return value;
        }
        switch (monitoringTarget) {
            case MISSING_RATIO:
                value = statisticsResultVos.get(0).getMissingRatio();
                break;
            case SPECIAL_RATIO:
                value = statisticsResultVos.get(0).getSpecialRatio();
                break;
            case IV:
                value = statisticsResultVos.get(0).getIvResult();
                break;
            case PSI:
                value = statisticsResultVos.get(0).getPsiResult();
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知指标");
        }
        return value;
    }

    /**
     * 获取top数据
     *
     * @param map        map集合
     * @param displayTop 前n
     * @return top数据
     */
    private List<ReportFormVariableMappingVo> getTopByMonitoringDiagramOutputVo(Map<ReportFormVariableMappingVo, BigDecimal> map, Integer displayTop) {

        List<Map.Entry<ReportFormVariableMappingVo, BigDecimal>> entryList = new ArrayList<>(map.entrySet());
        // 排序
        entryList.sort((o1, o2) -> {
            return o2.getValue().compareTo(o1.getValue());
        });

        //定义结果
        List<ReportFormVariableMappingVo> result = new ArrayList<>();
        for (int i = 0; i < entryList.size() && i < displayTop; i++) {
            Map.Entry<ReportFormVariableMappingVo, BigDecimal> entry = entryList.get(i);
            result.add(entry.getKey());
        }
        return result;
    }
}
