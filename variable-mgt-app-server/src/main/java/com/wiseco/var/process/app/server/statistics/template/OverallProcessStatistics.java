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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.IndicatorMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ReportFormPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.controller.vo.output.StatisticsConfigDetailOutputVO;
import com.wiseco.var.process.app.server.enums.MonitoringConfTimeUnitEnum;
import com.wiseco.var.process.app.server.enums.ReportFormPsiEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertConf;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsResult;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.VarProcessStatisticsResultService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.statistics.StatisticsOverallService;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import com.wisecotech.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OverallProcessStatistics  extends AbstractOverallStatisticsTemplate {

    private static final String CLICK_HOUSE_TABLE_PRIFIX = "var_process_manifest";


    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    VarProcessStatisticsResultService resultService;

    @Resource
    StatisticsOverallService statisticsOverallService;

    @Resource
    private DbOperateService dbOperateService;

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Value("${spring.datasourcetype:mysql}")
    private String dataSourceType;

    /**
     * 统计分析——计算处理
     *
     * @param varProcessServiceId  实时服务Id
     * @param varProcessManifestId 变量清单Id
     */
    public void calculateHandler(Long varProcessServiceId, Long varProcessManifestId) {
        StatisticsConfigDetailOutputVO detailOutputVO = statisticsOverallService.getConfigDetail(varProcessServiceId, varProcessManifestId);
        //校验
        Assert.notNull(detailOutputVO, "未找到该实施服务对应的配置详情！");
        Assert.notNull(detailOutputVO.getIndexMappingVo(), "计算指标不允许为空！");
        if (!detailOutputVO.getIndexMappingVo().isIv() && !detailOutputVO.getIndexMappingVo().isPsi()
                && !detailOutputVO.getIndexMappingVo().isZeroRatio() && !detailOutputVO.getIndexMappingVo().isUniqueNum()
                && !detailOutputVO.getIndexMappingVo().isPercentage() && !detailOutputVO.getIndexMappingVo().isMissingRatio()
                && !detailOutputVO.getIndexMappingVo().isSpecialRatio()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不存在需要计算的指标！");
        }

        //数据准备
        IndexCalculateContext calculateContext = new IndexCalculateContext();
        calculateContext.setVarProcessServiceId(varProcessServiceId);
        calculateContext.setIndexMappingVo(detailOutputVO.getIndexMappingVo());
        calculateContext.setIvMappingVo(detailOutputVO.getIvMappingVo());
        calculateContext.setPsiMappingVo(detailOutputVO.getPsiMappingVo());
        calculateContext.setSpecialMappingVoList(detailOutputVO.getSpecialMappingVoList());
        calculateContext.setStartDate(detailOutputVO.getStartDate());
        calculateContext.setEndDate(detailOutputVO.getEndDate());
        calculateContext.setTableSourceId(detailOutputVO.getVarProcessManifestId());
        calculateContext.setStatisticsConfigId(detailOutputVO.getStatisticsConfigId());
        calculateContext.setVarNameMap(getIndexNameMap(calculateContext.getTableSourceId()));
        calculateContext.setManifestId(varProcessManifestId);

        executeCalculate(calculateContext);
    }

    /**
     * 监控规则——计算处理
     *
     * @param conf        监控预警规则
     * @param endDateTime 本次监控开始时间
     * @return 计算结果
     */
    public List<StatisticsResultVo> calculateHandlerOfMonitoringRule(VarProcessMonitoringAlertConf conf, LocalDateTime endDateTime) {
        MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class);
        //数据准备
        IndexCalculateContext calculateContext = new IndexCalculateContext();
        Long serviceId = varProcessServiceVersionService.findServiceByNameAndVersion(conf.getServiceName(),conf.getServiceVersion()).getId();

        Long manifestId = varProcessManifestService.getOne(new LambdaQueryWrapper<VarProcessManifest>()
                .eq(VarProcessManifest::getVarManifestName, conf.getManifestName())
                .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).getId();

        calculateContext.setVarNameMap(getIndexNameMap(manifestId));
        calculateContext.setDataSourceInfoList(Collections.singletonList(new IndexCalculateContext.DataSourceInfo(serviceId,manifestId)));

        //时间配置
        LocalDateTime startDatesTime;
        if (paramConfiguration.getTimeUnit() == MonitoringConfTimeUnitEnum.MONTH) {
            startDatesTime = endDateTime.minusMonths(paramConfiguration.getTime());
        } else {
            startDatesTime = endDateTime.minus((long) paramConfiguration.getTime() * paramConfiguration.getTimeUnit().getMinute(), ChronoUnit.MINUTES);
        }
        calculateContext.setStartDate(startDatesTime);
        calculateContext.setEndDate(endDateTime);

        //指标参数
        AnalysisIndexMappingVo analysisIndexMappingVo = new AnalysisIndexMappingVo();
        switch (conf.getMonitoringTarget()) {
            case MISSING_RATIO:
                analysisIndexMappingVo.setMissingRatio(true);
                break;
            case SPECIAL_RATIO:
                analysisIndexMappingVo.setSpecialRatio(true);
                break;
            case PSI:
                MonitorConfigurationSaveInputVO.PsiConfig psiMappingVo = paramConfiguration.getPsiMappingVo();
                ConfigPsiMappingVo configPsiMappingVo = new ConfigPsiMappingVo();
                configPsiMappingVo.setBaseIndexFlag(psiMappingVo.getBaseIndexFlag());
                //选择时间范围作为基准
                if (psiMappingVo.getBaseIndexFlag()) {
                    if (psiMappingVo.getTimeFrame() == MagicNumbers.ONE) {
                        configPsiMappingVo.setStartDateTime(endDateTime.minus(1, ChronoUnit.HOURS));
                        configPsiMappingVo.setEndDateTime(endDateTime);
                    } else if (psiMappingVo.getTimeFrame() == MagicNumbers.TWO) {
                        configPsiMappingVo.setStartDateTime(endDateTime.minus(1, ChronoUnit.DAYS));
                        configPsiMappingVo.setEndDateTime(endDateTime);
                    } else {
                        configPsiMappingVo.setStartDateTime(psiMappingVo.getStartTime());
                        configPsiMappingVo.setEndDateTime(psiMappingVo.getEndTime());
                    }
                } else {
                    //与时间维度一致
                    configPsiMappingVo.setBaseIndex(psiMappingVo.getBaseIndex());
                    configPsiMappingVo.setBaseIndexVal(psiMappingVo.getBaseIndexVal());
                    configPsiMappingVo.setBaseIndexCallDate(psiMappingVo.getBaseIndexCallDate());
                }

                calculateContext.setPsiMappingVo(configPsiMappingVo);
                analysisIndexMappingVo.setPsi(true);
                break;
            case IV:
                calculateContext.setIvMappingVo(paramConfiguration.getIvMappingVo());
                analysisIndexMappingVo.setIv(true);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的指标");
        }
        calculateContext.setSpecialMappingVoList(paramConfiguration.getSpecialMappingVoList());
        calculateContext.setIndexMappingVo(analysisIndexMappingVo);
        //监控对象
        List<String> variableCodeList = paramConfiguration.getMonitoringObjectList()
                .stream().map(MonitorConfigurationSaveInputVO.MonitoringObject::getVariableCode).collect(Collectors.toList());
        calculateContext.setCalculateVariableList(variableCodeList);
        calculateContext.setContextType(MagicNumbers.TWO);
        return executeCalculateOfMonitoringSingleVariable(calculateContext);
    }

    /**
     * 监控报表——计算处理
     *
     * @param inputDto   监控报表入参
     * @param variableCode               变量code
     * @param serviceManifestNameVoList serviceManifestNameVoList
     * @param startTime                 startTime
     * @param endTime                   endTime
     * @return 统计结果 vo的list
     */
    public List<StatisticsResultVo> calculateHandlerOfMonitoringReportFrom(ReportFormCreateInputDto inputDto, String variableCode, List<ServiceManifestNameVo> serviceManifestNameVoList,
                                                                           LocalDateTime startTime, LocalDateTime endTime) {
        //数据准备
        IndexCalculateContext calculateContext = new IndexCalculateContext();
        //指标参数
        AnalysisIndexMappingVo analysisIndexMappingVo = new AnalysisIndexMappingVo();
        IndicatorMappingVo indicatorMappingVo = inputDto.getIndicatorMappingVo();
        switch (indicatorMappingVo.getMonitorIndicatorEnum()) {
            case MISSING_RATIO:
                analysisIndexMappingVo.setMissingRatio(true);
                break;
            case SPECIAL_RATIO:
                analysisIndexMappingVo.setSpecialRatio(true);
                break;
            case PSI:
                analysisIndexMappingVo.setPsi(true);
                ReportFormPsiMappingVo psiMappingVo = indicatorMappingVo.getPsiMappingVo();

                ConfigPsiMappingVo configPsiMappingVo = new ConfigPsiMappingVo();
                configPsiMappingVo.setBaseIndexFlag(psiMappingVo.getBaseIndexFlag() == ReportFormPsiEnum.DATETIME_SCOPE_DATA);

                if (psiMappingVo.getBaseIndexFlag() == ReportFormPsiEnum.DATETIME_SCOPE_DATA) {
                    //选择时间范围作为基准
                    configPsiMappingVo.setStartDateTime(psiMappingVo.getStartDateTime());
                    configPsiMappingVo.setEndDateTime(psiMappingVo.getEndDateTime());
                } else if (psiMappingVo.getBaseIndexFlag() == ReportFormPsiEnum.BASIC_INDICATOR) {
                    //选择选择基准指标
                    configPsiMappingVo.setBaseIndex(psiMappingVo.getBaseIndex());
                    configPsiMappingVo.setBaseIndexVal(psiMappingVo.getBaseIndexVal());
                    configPsiMappingVo.setBaseIndexCallDate(psiMappingVo.getBaseIndexCallDate());
                } else {
                    //选择基准清单
                    ServiceManifestNameVo baseManifest = inputDto.getIndicatorMappingVo().getPsiMappingVo().getServiceManifestNameVo();
                    configPsiMappingVo.setBaseServiceId(baseManifest.getServiceId());
                    configPsiMappingVo.setBaseManifestId(baseManifest.getManifestId());
                }
                calculateContext.setPsiMappingVo(configPsiMappingVo);
                break;
            case IV:
                analysisIndexMappingVo.setIv(true);
                calculateContext.setIvMappingVo(indicatorMappingVo.getIvMappingVo());
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的指标");
        }

        calculateContext.setDataSourceInfoList(serviceManifestNameVoList.stream().map(item -> new IndexCalculateContext.DataSourceInfo(item.getServiceId(), item.getManifestId())).collect(Collectors.toList()));
        calculateContext.setVarNameMap(getIndexNameMap(serviceManifestNameVoList.get(0).getManifestId()));
        calculateContext.setCalculateVariableList(Collections.singletonList(variableCode));
        calculateContext.setStartDate(startTime);
        calculateContext.setEndDate(endTime);
        calculateContext.setSpecialMappingVoList(indicatorMappingVo.getSpecialMappingVoList());
        calculateContext.setIndexMappingVo(analysisIndexMappingVo);
        calculateContext.setContextType(MagicNumbers.TWO);
        return executeCalculateOfMonitoringSingleVariable(calculateContext);
    }

    @Override
    protected String aggregateSql(IndexCalculateContext calculateContext) {
        final ConfigIvMappingVo ivMappingVo = calculateContext.getIvMappingVo();
        //来源于内部数据表
        String sqlTemplate = "select %s as name,%s as target, max(id) from %s where %s group by %s";
        String where = "1=1";
        if (StringUtils.isNotEmpty(ivMappingVo.getGroupedField())) {
            switch (ivMappingVo.getGroupedFieldValueOption()) {
                case APPOINT:
                    where += " and " + ivMappingVo.getGroupedField() + " in ('" + StringUtils.join(ivMappingVo.getGroupedFieldValue(), "','") + "')";
                    break;
                case FIRST:
                    String first = "select distinct %s as fir, min(created_date) from %s";
                    final Map<String, Object> firstValue = dbOperateService.queryForMap(String.format(first, ivMappingVo.getGroupedField(), ivMappingVo.getTableName()));
                    where += " and " + ivMappingVo.getGroupedField() + " = '" + firstValue.get("fir") + "'";
                    break;
                case LAST:
                    String last = "select distinct %s as las, max(created_date) from %s";
                    final Map<String, Object> lastValue = dbOperateService.queryForMap(String.format(last, ivMappingVo.getGroupedField(), ivMappingVo.getTableName()));
                    where += " and " + ivMappingVo.getGroupedField() + " = '" + lastValue.get("las") + "'";
                    break;
                default:
            }
        }
        return String.format(sqlTemplate, ivMappingVo.getRelationField(), ivMappingVo.getTargetField(), ivMappingVo.getTableName(), where, ivMappingVo.getRelationField());
    }

    @Override
    protected String getTableName(Long paramId) {
        return CLICK_HOUSE_TABLE_PRIFIX + "_" + paramId.toString();
    }

    @Override
    protected List<Map<String, Object>> getDataList(String tableName, IndexCalculateContext calculateContext) {
        StringBuilder querySql = new StringBuilder();
        querySql.append("select increment_id ,external_serial_no, variables from ");
        querySql.append(tableName);
        querySql.append(" where ");
        querySql.append(getWhereSql(calculateContext));
        return queryData(calculateContext, tableName, querySql.toString());
    }

    private String getWhereSql(IndexCalculateContext calculateContext) {
        StringBuilder querySql = new StringBuilder();
        if (calculateContext.getManifestId() != null) {
            querySql.append(" manifest_id = ");
            querySql.append(calculateContext.getManifestId());
            querySql.append(" and ");
        }
        querySql.append(" service_id = ");
        querySql.append(calculateContext.getVarProcessServiceId());
        if (calculateContext.getStartDate() != null) {
            querySql.append(" and request_date >= '").append(calculateContext.getStartDate().format(dateTimeFormatter)).append("'");
        }
        if (calculateContext.getEndDate() != null) {
            if (calculateContext.getContextType() == MagicNumbers.ONE) {
                querySql.append(" and request_date <= '").append(calculateContext.getEndDate().withHour(MagicNumbers.INT_23).withMinute(MagicNumbers.INT_59).withSecond(MagicNumbers.INT_59).format(dateTimeFormatter)).append("'");
            } else if (calculateContext.getContextType() == MagicNumbers.TWO) {
                querySql.append(" and request_date <= '").append(calculateContext.getEndDate().format(dateTimeFormatter)).append("'");
            }
        }
        return querySql.toString();
    }

    @Override
    protected Map<String, String> getIndexNameMap(Long paramId) {
        // 获取变量清单发布变量信息
        List<VarProcessVariable> publishedVariableList = varProcessVariableService.findManifestOutputVariableList(paramId);

        return publishedVariableList.stream().collect(Collectors.toMap(VarProcessVariable::getName, VarProcessVariable::getLabel));
    }

    @Override
    protected void saveData(IndexCalculateContext calculateContext, boolean isDelete) {
        if (isDelete) {
            LambdaQueryWrapper<VarProcessStatisticsResult> resultLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resultLambdaQueryWrapper.eq(VarProcessStatisticsResult::getStatisticsConfigId, calculateContext.getStatisticsConfigId());
            resultService.remove(resultLambdaQueryWrapper);
        }

        //保存
        if (CollUtil.isNotEmpty(calculateContext.getResultList())) {

            String username;
            try {
                username = SessionContext.getSessionUser().getUsername();
            } catch (Exception e) {
                username = "task";
            }
            String finalUsername = username;
            List<VarProcessStatisticsResult> list = calculateContext.getResultList().stream().map(e -> {
                VarProcessStatisticsResult result = new VarProcessStatisticsResult();
                BeanUtils.copyProperties(e, result);
                result.setStatisticsConfigId(calculateContext.getStatisticsConfigId());
                result.setCreatedUser(finalUsername);
                result.setUpdatedUser(finalUsername);
                return result;
            }).collect(Collectors.toList());

            resultService.saveBatch(list);
        }
    }

    @Override
    public Map<String, List<Object>> getPsiDataMap(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String varCode = result.getVarCode();
        List<Map<String, Object>> dataList = calculateContext.getDataList();

        Map<String, List<Object>> psiDataMap = new HashMap<>(MagicNumbers.EIGHT);
        List<Object> targetList;
        List<Object> standardList;
        //计算列的位置
        int varIndex = getVarIndex(calculateContext.getAllManifestVarsType(), varCode);

        Map<Long, List<Map<String, Object>>> allManifestVarsTypeListMap = calculateContext.getAllManifestVarsTypeListMap();

        //以变量清单为基准:以清单为基准时，数据集一定有多个，并且数据集中都有相同的varCode
        if (calculateContext.getPsiMappingVo().getBaseServiceId() != null && calculateContext.getPsiMappingVo().getBaseManifestId() != null) {
            if (allManifestVarsTypeListMap.containsKey(calculateContext.getPsiMappingVo().getBaseManifestId())) {
                varIndex = getVarIndex(allManifestVarsTypeListMap.get(calculateContext.getPsiMappingVo().getBaseManifestId()), varCode);
            } else {
                varIndex = getVarIndex(getManifestVarsType(calculateContext.getPsiMappingVo().getBaseManifestId(), null), varCode);
            }
            //目标数列:为全部清单的计算列
            targetList = dataList.stream().map(e -> e.get(varCode)).collect(Collectors.toList());
            //基准数列:为基准清单的计算列
            String whereCondition = "service_id = '" + calculateContext.getPsiMappingVo().getBaseServiceId() + "'";
            standardList = dbOperateService.getColumnData(getTableName(calculateContext.getPsiMappingVo().getBaseManifestId()), varIndex, whereCondition);

        //选择时间范围作为基准
        } else if (Boolean.TRUE.equals(calculateContext.getPsiMappingVo().getBaseIndexFlag())) {
            //目标数列：为全部清单的非空的计算列
            targetList = dataList.stream().filter(Objects::nonNull).map(e -> e.get(varCode)).collect(Collectors.toList());
            if (calculateContext.getContextType() == MagicNumbers.ONE) {
                //整体分析：基准数列为选择的时间范围
                String whereCondition = generateWhereCondition(calculateContext, calculateContext.getVarProcessServiceId(), true);
                String tableName = getTableName(calculateContext.getTableSourceId());
                standardList = dbOperateService.getColumnData(tableName, varIndex, whereCondition);
            } else {
                //监控单指标分析：基准数列选择的时间范围（可能存在多个清单）
                standardList = new ArrayList<>();
                for (IndexCalculateContext.DataSourceInfo dataSourceInfo : calculateContext.getDataSourceInfoList()) {
                    String whereCondition = generateWhereCondition(calculateContext, dataSourceInfo.getServiceId(), true);
                    varIndex = getVarIndex(allManifestVarsTypeListMap.get(dataSourceInfo.getManifestId()), varCode);
                    List<Object> columnData = dbOperateService.getColumnData(getTableName(dataSourceInfo.getManifestId()), varIndex, whereCondition);
                    standardList.addAll(columnData);
                }
            }
        //选择基准指标:选择基准指标时，数据集一定只有一个
        } else {
            String baseIndex = calculateContext.getPsiMappingVo().getBaseIndex();
            String baseIndexVal = calculateContext.getPsiMappingVo().getBaseIndexVal();

            //目标数列为，不与基准值相同的列
            targetList = dataList.stream().filter(e -> e != null && !baseIndexVal.equals(e.get(baseIndex).toString())).map(e -> e.get(varCode)).collect(Collectors.toList());

            //与分析时间设置一致
            if (calculateContext.getPsiMappingVo().getBaseIndexCallDate() == null || calculateContext.getPsiMappingVo().getBaseIndexCallDate()) {
                standardList = dataList.stream().filter(e -> e != null && baseIndexVal.equals(e.get(baseIndex).toString())).map(e -> e.get(varCode)).collect(Collectors.toList());
            } else {
                Long serviceId;
                String tableName;
                if (calculateContext.getContextType() == MagicNumbers.ONE) {
                    serviceId = calculateContext.getVarProcessServiceId();
                    tableName = getTableName(calculateContext.getTableSourceId());
                } else {
                    serviceId = calculateContext.getDataSourceInfoList().get(0).getServiceId();
                    tableName = getTableName(calculateContext.getDataSourceInfoList().get(0).getManifestId());
                }
                String whereCondition = generateWhereCondition(calculateContext, serviceId, false);
                standardList = dbOperateService.getColumnData(tableName, varIndex, whereCondition);
            }
        }

        psiDataMap.put("data1", standardList);
        psiDataMap.put("data2", targetList);
        return psiDataMap;
    }


    /**
     * generateWhereCondition
     *
     * @param calculateContext 统计分析计算信息
     * @param serviceId         服务id
     * @param flag             基准数据 true/基准指标false
     * @return java.lang.String
     */
    private String generateWhereCondition(IndexCalculateContext calculateContext, Long serviceId, boolean flag) {
        String startDate = null;
        String endDate = null;
        ConfigPsiMappingVo psiMappingVo = calculateContext.getPsiMappingVo();
        if (flag) {
            if (calculateContext.getPsiMappingVo().getStartDateTime() != null) {
                startDate = calculateContext.getPsiMappingVo().getStartDateTime().format(dateTimeFormatter);
                endDate = psiMappingVo.getEndDateTime() != null ? psiMappingVo.getEndDateTime().format(dateTimeFormatter) : null;
            } else {
                startDate = psiMappingVo.getStartDate() != null ? psiMappingVo.getStartDate().format(dateFormatter) + " 00:00:00" : null;
                endDate = psiMappingVo.getEndDate() != null ? psiMappingVo.getEndDate().format(dateFormatter) + " 23:59:59" : null;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" service_id = '").append(serviceId).append("'");
        appendCondition(sb, "request_date >= '%s'", startDate);
        appendCondition(sb, "request_date <= '%s'", endDate);
        if (!flag) {
            appendCondition(sb, "%s = '%s'", calculateContext.getPsiMappingVo().getBaseIndex(), calculateContext.getPsiMappingVo().getBaseIndexVal());
        }
        return sb.toString();
    }

    private void appendCondition(StringBuilder sb, String format, String value) {
        if (value != null) {
            sb.append(" and ").append(String.format(format, value));
        }
    }


    private void appendCondition(StringBuilder sb, String format, String value1, String value2) {
        if (value1 != null && value2 != null) {
            sb.append(" and ").append(String.format(format, value1, value2));
        }
    }
}
