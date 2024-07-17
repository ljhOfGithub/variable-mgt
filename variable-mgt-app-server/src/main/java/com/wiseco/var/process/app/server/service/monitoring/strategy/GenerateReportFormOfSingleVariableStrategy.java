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
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormDisplayDimensionEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.statistics.template.OverallProcessStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成报表-单指标分析
 *
 * @author wuweikang
 */
@Component("singleVariable")
@Slf4j
public class GenerateReportFormOfSingleVariableStrategy implements GenerateReportFormStrategy {

    @Autowired
    private OverallProcessStatistics overallProcessStatistics;

    @Override
    public MonitoringDiagramOutputVo generateReportForm(ReportFormCreateInputDto inputDto) {
        MonitoringDiagramOutputVo monitoringDiagramOutputVo = null;
        ReportFormDisplayDimensionEnum displayDimension = inputDto.getDisplayDimensionVo().getDisplayDimension();
        //监控对象code
        String variableCode = inputDto.getMonitorObjectMappingVo().getVariableMappingVo().getVariableCode();
        switch (displayDimension) {
            case TIME_SCOPE:
                monitoringDiagramOutputVo = generateReportOfTimeScope(inputDto);
                break;
            case MANIFEST:
                monitoringDiagramOutputVo = generateReportOfManifest(inputDto, variableCode);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的展示维度");
        }
        return monitoringDiagramOutputVo;
    }

    /**
     * 生成报表——时间维度
     *
     * @param inputDto 输入实体类对象
     * @return 监控图表输出Vo
     */
    public MonitoringDiagramOutputVo generateReportOfTimeScope(ReportFormCreateInputDto inputDto) {
        //监控指标
        MonitorIndicatorEnum monitoringTarget = inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum();
        //监控对象
        String variableCode = inputDto.getMonitorObjectMappingVo().getVariableMappingVo().getVariableCode();
        //变量清单集合
        List<ServiceManifestNameVo> serviceManifestNameVoList = new ArrayList<>();

        //判断是否要排序：排序则先计算总时间范围内的值,然后过滤出topN的变量清单
        Integer displayTop = inputDto.getDisplayDimensionVo().getDisplayTop();
        if (displayTop != null && displayTop != 0 && displayTop > inputDto.getManifests().size()) {
            //全时间段值——key:清单 value:值
            Map<ServiceManifestNameVo, BigDecimal> allVariableAndManifestListMapping = new LinkedHashMap<>();
            inputDto.getManifests().forEach((item) -> {
                List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(inputDto, variableCode, Collections.singletonList(item), inputDto.getStartTime(), inputDto.getEndTime());
                BigDecimal value = getValue(statisticsResultVos, monitoringTarget);
                allVariableAndManifestListMapping.put(item, value);
            });
            //获取排名前n的变量清单
            serviceManifestNameVoList = getTopByMonitoringDiagramOutputVo(allVariableAndManifestListMapping, displayTop);
        }

        //按时间刻度计算——key:清单名称 value:值
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        //按照时间刻度获取时间区间
        LinkedHashMap<LocalDateTime, LocalDateTime> timeZone = getTimeZone(inputDto.getStartTime(), inputDto.getEndTime(), inputDto.getDisplayDimensionVo().getTimeUnit());

        //一层循环清单
        serviceManifestNameVoList.forEach((item) -> {
            ArrayList<String> values = new ArrayList<>();
            //二层循环时间
            timeZone.forEach((key, value) -> {
                List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(
                        inputDto, variableCode, Collections.singletonList(item), key, value);
                BigDecimal bigDecimal = getValue(statisticsResultVos, monitoringTarget);
                values.add(bigDecimal == null ? "0" : bigDecimal.toString());
            });
            map.put(item.getName(), values);
        });

        //计算整体
        ArrayList<String> values = new ArrayList<>();
        timeZone.forEach((stratTime, endTime) -> {
            List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(
                    inputDto, variableCode, inputDto.getManifests(), stratTime, endTime);
            BigDecimal bigDecimal = getValue(statisticsResultVos, monitoringTarget);
            values.add(bigDecimal == null ? "0" : bigDecimal.toString());
        });
        map.put("整体", values);

        ArrayList<LocalDateTime> timeAxle = new ArrayList<>();
        timeZone.forEach((key, value) -> {
            timeAxle.add(key);
        });
        return generateDiagramOfTimeScope(inputDto.getType(), timeAxle, inputDto.getDisplayDimensionVo().getTimeUnit(), map, inputDto.getName(), inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum());
    }


    /**
     * 生成报表——按照变量清单
     *
     * @param inputDto      inputDto
     * @param variableCode variableCode
     * @return 监控图表输出Vo
     */
    private MonitoringDiagramOutputVo generateReportOfManifest(ReportFormCreateInputDto inputDto, String variableCode) {
        MonitorIndicatorEnum monitoringTarget = inputDto.getIndicatorMappingVo().getMonitorIndicatorEnum();
        LinkedHashMap<String, BigDecimal> map = new LinkedHashMap<>();
        for (ServiceManifestNameVo serviceManifestNameVo : inputDto.getManifests()) {
            List<StatisticsResultVo> statisticsResultVos = overallProcessStatistics.calculateHandlerOfMonitoringReportFrom(inputDto, variableCode, Collections.singletonList(serviceManifestNameVo), inputDto.getStartTime(), inputDto.getEndTime());
            //展示维度为监控对象
            BigDecimal value = getValue(statisticsResultVos, monitoringTarget);
            map.put(serviceManifestNameVo.getName(), value);
        }

        //获取topN的服务和指标值
        LinkedHashMap<String, BigDecimal> topMap = getTop(map, inputDto.getDisplayDimensionVo().getDisplayTop());
        return generateDiagramOfMonitorObject(topMap, inputDto);
    }


    private BigDecimal getValue(List<StatisticsResultVo> statisticsResultVos, MonitorIndicatorEnum monitoringTarget) {
        if (CollectionUtils.isEmpty(statisticsResultVos)) {
            return null;
        }

        BigDecimal value;
        switch (monitoringTarget) {
            case MISSING_RATIO:
                value = statisticsResultVos.get(0).getMissingRatio();
                if (value != null) {
                    value = getPercentage(value);
                }
                break;
            case SPECIAL_RATIO:
                value = statisticsResultVos.get(0).getSpecialRatio();
                if (value != null) {
                    value = getPercentage(value);
                }
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
    private List<ServiceManifestNameVo> getTopByMonitoringDiagramOutputVo(Map<ServiceManifestNameVo, BigDecimal> map, Integer displayTop) {
        ArrayList<Map.Entry<ServiceManifestNameVo, BigDecimal>> entryList = new ArrayList<>(map.entrySet());
        // 排序
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
        List<ServiceManifestNameVo> result = new ArrayList<>();
        for (int i = 0; i < entryList.size() && i < displayTop; i++) {
            Map.Entry<ServiceManifestNameVo, BigDecimal> entry = entryList.get(i);
            result.add(entry.getKey());
        }
        return result;
    }
}
