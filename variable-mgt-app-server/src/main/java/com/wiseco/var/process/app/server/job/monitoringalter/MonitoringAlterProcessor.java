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
package com.wiseco.var.process.app.server.job.monitoringalter;

import com.wiseco.boot.cache.CacheClient;
import com.wiseco.message.sdk.core.MessageClient;
import com.wiseco.message.sdk.core.MessageDTO;
import com.wiseco.message.sdk.core.ReceiverDTO;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationSaveInputVO;
import com.wiseco.var.process.app.server.enums.MonitoringConfComparisonOperatorsEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTimeUnitEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import com.wiseco.var.process.app.server.enums.TriggerRuleEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.job.param.MonitoringAlterTaskParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertConf;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertMessage;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.VarProcessMonitoringAlertConfService;
import com.wiseco.var.process.app.server.service.VarProcessMonitoringAlertMessageService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.statistics.StatisticsCallVolumeService;
import com.wiseco.var.process.app.server.statistics.template.OverallProcessStatistics;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 监控预警定时任务执行
 *
 * @author xupei
 */
@Component
@Slf4j
@RefreshScope
public class MonitoringAlterProcessor implements BasicProcessor {

    public static final String LARK_NAME = "feishu_flag";

    @Value("${monitoring.feishu.hook_url:https://open.feishu.cn/open-apis/bot/v2/hook/a4584635-42af-4b6b-b825-8af0db880984}")
    private  List<String> hookUrlList;

    @Autowired
    private OverallProcessStatistics overallProcessStatistics;

    @Resource
    private VarProcessMonitoringAlertConfService varProcessMonitoringAlertConfService;

    @Autowired
    private VarProcessMonitoringAlertMessageService varProcessMonitoringAlertMessageService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    @Qualifier(value = "remoteCacheClient")
    private CacheClient cacheClient;

    @Autowired
    private MessageClient messageClient;

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsCallVolumeService statisticsCallVolumeService;

    private static final String VARIABLE_ALERT_MESSAGE = "【%s】实时服务：%s，变量清单：%s，监控对象：%s，监控指标：%s，告警原因：近%s内%s为%s，触发告警，告警时间：%s";
    private static final String VARIABLE_RESTORE_MESSAGE = "【恢复提醒】【%s】 实时服务：%s，变量清单：%s，监控对象：%s，监控指标：%s，恢复原因：最近%s次调用监控指标均未触发告警，恢复时间：%s";

    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        MonitoringAlterTaskParam jobParam = JSON.parseObject(taskContext.getJobParams(), MonitoringAlterTaskParam.class);
        VarProcessMonitoringAlertConf conf = varProcessMonitoringAlertConfService.getById(jobParam.getId());
        if (conf == null) {
            return new ProcessResult(false, "监控预警:定时任务执行失败:监控预警配置不存在");
        }
        VarProcessServiceVersion varProcessService = varProcessServiceVersionService.findServiceByNameAndVersion(conf.getServiceName(),conf.getServiceVersion());
        //服务未删除并且服务未停用才监控
        if (varProcessService != null && varProcessService.getState() != VarProcessServiceStateEnum.DISABLED) {
            //监控开始时间
            LocalDateTime nowDate = LocalDateTime.now();
            log.info("监控预警:执行开始->监控规则名称：{},时间：{}", conf.getConfName(), nowDate);
            try {
                if (conf.getMonitoringType() == MonitoringConfTypeEnum.SERVICE) {
                    //监控服务
                    monitoringService(nowDate, conf, varProcessService.getId());
                } else {
                    //监控指标
                    monitoringVariable(nowDate, conf);
                }
            } catch (Exception e) {
                log.info("监控预警:定时任务执行失败,任务名称：{},异常信息：{}", conf.getConfName(), e.toString());
                return new ProcessResult(false, "监控预警:定时任务执行失败");
            }
        }
        return new ProcessResult(true, "监控预警:定时任务执行成功");
    }


    /**
     * 监控服务
     *
     * @param nowDate   监控时间
     * @param conf      监控配置
     * @param serviceId 服务id
     */
    private void monitoringService(LocalDateTime nowDate, VarProcessMonitoringAlertConf conf, Long serviceId) {
        MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class);
        LocalDateTime fromDate;
        if (paramConfiguration.getTimeUnit() == MonitoringConfTimeUnitEnum.MONTH) {
            fromDate = nowDate.minusMonths(paramConfiguration.getTime());
        } else {
            fromDate = nowDate.minus((long) paramConfiguration.getTime() * paramConfiguration.getTimeUnit().getMinute(), ChronoUnit.MINUTES);
        }

        //计算结果
        List<BigDecimal> dataList = new ArrayList<>();
        if (conf.getMonitoringTarget() == MonitoringConfTargetEnum.RESPONSE_CODE_RATIO) {
            //触发条件信息
            MonitorConfigurationSaveInputVO.TriggerCondition triggerCondition = JSONObject.parseObject(conf.getTriggerCondition(), MonitorConfigurationSaveInputVO.TriggerCondition.class);
            //计算响应码占比
            triggerCondition.getConditionList().forEach(item -> {
                String[] responseCodeArray;
                if (item.getResponseCode().contains(StringPool.COMMA)) {
                    responseCodeArray = item.getResponseCode().split(StringPool.COMMA);
                } else {
                    responseCodeArray = item.getResponseCode().split(StringPool.COMMA_ZH);
                }

                BigDecimal responseCodeRatio = new BigDecimal("0.00");
                for (String responseCode : responseCodeArray) {
                    responseCodeRatio = responseCodeRatio.add(statisticsCallVolumeService.serviceStatics(conf.getMonitoringTarget(), serviceId, fromDate, nowDate, responseCode));
                }
                dataList.add(responseCodeRatio);
            });
        } else {
            dataList.add(statisticsCallVolumeService.serviceStatics(conf.getMonitoringTarget(), serviceId, fromDate, nowDate, ""));
        }

        boolean isTriggerAlert = isTriggerAlert(conf, dataList);
        //发送恢复提醒
        sendRestoreRemindMessage(conf, conf.getServiceName(), nowDate, isTriggerAlert);
        //发送预警
        if (isTriggerAlert) {
            List<String> dataStringList = getDataStringList(conf.getMonitoringTarget(), dataList);
            sendAlterMessage(conf, conf.getServiceName(), nowDate, dataStringList);
        }
    }

    /**
     * 监控指标
     *
     * @param nowDate 监控时间
     * @param conf    监控配置
     */
    private void monitoringVariable(LocalDateTime nowDate, VarProcessMonitoringAlertConf conf) {
        MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class);
        //监控对象
        List<String> variableCodeList = paramConfiguration.getMonitoringObjectList()
                .stream().map(MonitorConfigurationSaveInputVO.MonitoringObject::getVariableCode).collect(Collectors.toList());
        //获取指标计算结果
        List<StatisticsResultVo> resultList = overallProcessStatistics.calculateHandlerOfMonitoringRule(conf, nowDate);
        for (StatisticsResultVo statisticsResultVo : resultList) {
            BigDecimal data;
            switch (conf.getMonitoringTarget()) {
                case MISSING_RATIO:
                    data = statisticsResultVo.getMissingRatio();
                    break;
                case SPECIAL_RATIO:
                    data = statisticsResultVo.getSpecialRatio();
                    break;
                case PSI:
                    data = statisticsResultVo.getPsiResult();
                    break;
                case IV:
                    data = statisticsResultVo.getIvResult();
                    break;
                default:
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知指标");
            }
            log.info("监控预警:variableName：{},监控指标值：{}", statisticsResultVo.getVarName(), data);

            //指标不为监控对象则跳过
            if (!variableCodeList.contains(statisticsResultVo.getVarCode())) {
                continue;
            }

            List<BigDecimal> dataList = Collections.singletonList(data);
            boolean isTriggerAlert = isTriggerAlert(conf, dataList);
            //发送恢复提醒
            sendRestoreRemindMessage(conf, statisticsResultVo.getVarName(), nowDate, isTriggerAlert);
            //判断是否触发告警
            if (isTriggerAlert) {
                List<String> dataStringList = getDataStringList(conf.getMonitoringTarget(), dataList);
                sendAlterMessage(conf, statisticsResultVo.getVarName(), nowDate, dataStringList);
            }
        }

    }

    /**
     * isTriggerAlert
     *
     * @param conf           规则配置
     * @param leftResultList 左值
     * @return boolean
     */
    public boolean isTriggerAlert(VarProcessMonitoringAlertConf conf, List<BigDecimal> leftResultList) {
        //触发条件信息
        MonitorConfigurationSaveInputVO.TriggerCondition triggerCondition =
                JSONObject.parseObject(conf.getTriggerCondition(), MonitorConfigurationSaveInputVO.TriggerCondition.class);

        ArrayList<Boolean> booleans = new ArrayList<>();
        List<MonitorConfigurationSaveInputVO.TriggerConditionDetail> conditionList = triggerCondition.getConditionList();
        //拼接条件
        for (MonitorConfigurationSaveInputVO.TriggerConditionDetail conditionDetail : conditionList) {
            //右值
            BigDecimal rightValue = new BigDecimal(conditionDetail.getRightValue());
            for (BigDecimal leftResult : leftResultList) {
                if (leftResult == null) {
                    if (conditionDetail.getComparisonOperators() == MonitoringConfComparisonOperatorsEnum.NOT_EQUAL) {
                        booleans.add(true);
                    } else {
                        booleans.add(false);
                    }
                    continue;
                }
                //比较左值和右值,-1:左值小 0：相等 1：左值大
                int result = leftResult.compareTo(rightValue);
                switch (conditionDetail.getComparisonOperators()) {
                    case GREATER_THAN:
                        booleans.add(result > 0);
                        break;
                    case GREATER_THAN_OR_EQUAL:
                        booleans.add(result >= 0);
                        break;
                    case LESS_THAN:
                        booleans.add(result < 0);
                        break;
                    case LESS_THAN_OR_EQUAL:
                        booleans.add(result <= 0);
                        break;
                    case EQUAL:
                        booleans.add(result == 0);
                        break;
                    case NOT_EQUAL:
                        booleans.add(result != 0);
                        break;
                    default:
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知比较符");
                }
            }
        }

        //true:触发 false:不触发
        boolean isTriggerAlert;
        if (triggerCondition.getTriggerRule() == TriggerRuleEnum.AND) {
            isTriggerAlert = !booleans.contains(Boolean.FALSE);
        } else {
            isTriggerAlert = booleans.contains(Boolean.TRUE);
        }

        log.info("监控预警:是否触发->{},监控规则名称：{}", isTriggerAlert, conf.getConfName());

        return isTriggerAlert;
    }


    private void sendRestoreRemindMessage(VarProcessMonitoringAlertConf conf, String monitoringTargetName, LocalDateTime nowDate, boolean isTriggerAlert) {
        MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);
        String key = CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + conf.getId() + monitoringTargetName;
        if (alertInfo.getRestoreRemindConf() != null && alertInfo.getRestoreRemindConf().getEnableRestoreRemindConf()) {
            if (isTriggerAlert) {
                //如果触发了告警，正常次数置为0
                cacheClient.put(key, 0);
            } else {
                //如果没有触发告警并且已经产生过告警，则根据正常次数和恢复提醒设置次数来判断是否需要发送恢复提醒
                if (cacheClient.exists(key)) {
                    //累计的正常次数
                    int count = Integer.parseInt(cacheClient.get(key).toString()) + 1;
                    //配置的正常次数
                    Integer normalCount = alertInfo.getRestoreRemindConf().getNormalCount();
                    log.info("监控预警:执行验证恢复提醒->监控规则名称：{},累计正常次数：{},配置正常次数：{}", conf.getConfName(), count, normalCount);

                    if (count >= normalCount) {
                        //清除key
                        cacheClient.evict(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + conf.getId() + monitoringTargetName);
                        //发送恢复提醒
                        sendRestoreRemind(conf, monitoringTargetName, nowDate);
                    } else {
                        cacheClient.put(key, count);
                    }
                }
            }
        }
    }

    private boolean isSendAlertMessage(VarProcessMonitoringAlertConf conf, String monitoringTargetName, LocalDateTime nowDate) {
        MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);
        boolean isSend = true;
        //如果开启沉默设置，判断是否发送告警
        if (alertInfo.getSilentConf() != null && alertInfo.getSilentConf().getEnableSilentConf()) {
            MonitorConfigurationSaveInputVO.SilentConf silentConf = alertInfo.getSilentConf();
            //上一次告警时间
            LocalDateTime lastSendTime = null;
            Object o = cacheClient.get(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + conf.getId() + monitoringTargetName);
            if (o != null) {
                lastSendTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(o.toString())), ZoneOffset.ofHours(MagicNumbers.EIGHT));
            }

            if (lastSendTime != null) {
                //在该时间内不再发送
                LocalDateTime futureDate;
                if (alertInfo.getMonitorFrequencyTimeUnit() == MonitoringConfTimeUnitEnum.MONTH) {
                    futureDate = lastSendTime.plusMonths(silentConf.getSilentTime());
                } else {
                    futureDate = lastSendTime.plus((long) silentConf.getSilentTime() * silentConf.getSilentTimeUnit().getMinute(), ChronoUnit.MINUTES);
                }
                int i = nowDate.compareTo(futureDate);
                isSend = i > 0;
            }
            log.info("监控预警:执行验证沉默时间->监控规则名称：{},上一次执行时间：{}，当前时间：{}，是否发送告警：{}", conf.getConfName(), lastSendTime, nowDate, isSend);
        }
        return isSend;
    }


    private void sendRestoreRemind(VarProcessMonitoringAlertConf conf, String monitoringTargetName, LocalDateTime nowDate) {
        log.info("监控预警:执行发送恢复提醒->监控规则名称：{}", conf.getConfName());
        //发送信息
        sendMessage(conf, monitoringTargetName, null, nowDate, MagicNumbers.TWO);

        //恢复提醒消息
        StringBuilder message = new StringBuilder();
        MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);
        if (conf.getMonitoringType() == MonitoringConfTypeEnum.SERVICE) {
            message.append(alertInfo.getRestoreRemindConf().getRestoreRemindMessage().replace("yyyy-mm-dd hh24:mi:ss", nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        } else {
            message.append(String.format(VARIABLE_RESTORE_MESSAGE, conf.getConfName(), conf.getServiceName() + "(" + conf.getServiceVersion() + ")", conf.getManifestName(), monitoringTargetName,
                    conf.getMonitoringTarget().getDesc(), alertInfo.getRestoreRemindConf().getNormalCount(), nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        VarProcessMonitoringAlertMessage alertMessage = VarProcessMonitoringAlertMessage.builder()
                .monitoringAlertConfId(conf.getId()).messageType(MagicNumbers.TWO).monitoringTargetName(monitoringTargetName).alertMessage(message.toString()).alertDate(nowDate).build();
        BeanUtils.copyProperties(conf, alertMessage);
        alertMessage.setId(null);
        alertMessage.setCreatedTime(new Date());
        alertMessage.setUpdatedTime(new Date());
        varProcessMonitoringAlertMessageService.save(alertMessage);
    }


    private void sendAlterMessage(VarProcessMonitoringAlertConf conf, String monitoringTargetName, LocalDateTime nowDate, List<String> dataList) {
        //是否发送告警消息
        boolean isSendAlertMessage = isSendAlertMessage(conf, monitoringTargetName, nowDate);
        if (isSendAlertMessage) {
            //记录本次发送告警时间戳
            long epochMilli = nowDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            cacheClient.put(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + conf.getId() + monitoringTargetName, epochMilli);
            //发送
            sendMessage(conf, monitoringTargetName, dataList, nowDate, MagicNumbers.ONE);
        }

        //告警消息
        String alertMessage = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class).getAlertMessage();
        if (conf.getMonitoringType() == MonitoringConfTypeEnum.SERVICE) {
            for (String data : dataList) {
                alertMessage = alertMessage.replaceFirst("#", data);
            }
            alertMessage = alertMessage.replace("yyyy-mm-dd hh24:mi:ss", nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            //参数设置
            MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(conf.getParamConfigurationInfo(),
                    MonitorConfigurationSaveInputVO.ParamConfiguration.class);

            alertMessage = String.format(VARIABLE_ALERT_MESSAGE, conf.getConfName(), conf.getServiceName() + "(" + conf.getServiceVersion() + ")", conf.getManifestName(),
                    monitoringTargetName, conf.getMonitoringTarget().getDesc(), paramConfiguration.getTime() + paramConfiguration.getTimeUnit().getDesc(),
                    conf.getMonitoringTarget().getDesc(), dataList.get(0), nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        //保存告警信息
        VarProcessMonitoringAlertMessage varProcessMonitoringAlertMessage = VarProcessMonitoringAlertMessage.builder()
                .monitoringAlertConfId(conf.getId()).messageType(MagicNumbers.ONE).monitoringTargetName(monitoringTargetName).alertMessage(alertMessage).alertDate(nowDate).build();
        BeanUtils.copyProperties(conf, varProcessMonitoringAlertMessage);
        varProcessMonitoringAlertMessage.setId(null);
        varProcessMonitoringAlertMessage.setCreatedTime(new Date());
        varProcessMonitoringAlertMessage.setUpdatedTime(new Date());
        varProcessMonitoringAlertMessageService.save(varProcessMonitoringAlertMessage);
    }


    private void sendMessage(VarProcessMonitoringAlertConf conf, String monitoringTargetName, List<String> dataList, LocalDateTime nowDate, Integer messageType) {
        MessageDTO messageDTO = new MessageDTO();

        MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);

        //获取参数
        List<String> messageParams = getMessageParams(conf, monitoringTargetName, dataList, nowDate, messageType, messageDTO, alertInfo);
        messageDTO.setMessageParams(messageParams);

        //短信接收人
        List<ReceiverDTO> noteReceiver = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alertInfo.getUserListByNote())) {
            List<Integer> userIdList = alertInfo.getUserListByNote().stream().map(MonitorConfigurationSaveInputVO.UserInfo::getUserId).collect(Collectors.toList());
            noteReceiver = userService.findUserSmallByUserIds(userIdList).stream().map(item -> {
                        ReceiverDTO receiverDTO = new ReceiverDTO();
                        receiverDTO.setUserName(item.getUsername());
                        receiverDTO.setMobileReceivers(Collections.singletonList(item.getPhone()));
                        return receiverDTO;
                    }
            ).collect(Collectors.toList());
        }
        //邮件接收人
        List<ReceiverDTO> emailReceivers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alertInfo.getUserListByEmail())) {
            List<Integer> userIdList = alertInfo.getUserListByEmail().stream().map(MonitorConfigurationSaveInputVO.UserInfo::getUserId).collect(Collectors.toList());
            emailReceivers = userService.findUserSmallByUserIds(userIdList).stream().map(item -> {
                        ReceiverDTO receiverDTO = new ReceiverDTO();
                        receiverDTO.setUserName(item.getUsername());
                        receiverDTO.setEmailReceivers(Collections.singletonList(item.getEmail()));
                        return receiverDTO;
                    }
            ).collect(Collectors.toList());
        }

        //飞书
        List<ReceiverDTO> feishuReceivers = new ArrayList<>();
        if (BooleanUtils.isTrue(alertInfo.getFeishuEnabled())) {
            ReceiverDTO receiverDTO = new ReceiverDTO();
            receiverDTO.setUserName(LARK_NAME);
            receiverDTO.setFeiShuBotsHookUrls(hookUrlList);
            feishuReceivers.add(receiverDTO);
        }

        //接收人
        List<ReceiverDTO> receiverDTOList = new ArrayList<>();
        receiverDTOList.addAll(noteReceiver);
        receiverDTOList.addAll(emailReceivers);
        receiverDTOList.addAll(feishuReceivers);

        // 根据用户名进行分组
        Map<String, List<ReceiverDTO>> groupedByUserName = receiverDTOList.stream()
                .collect(Collectors.groupingBy(ReceiverDTO::getUserName));
        // 合并EmailReceivers和MobileReceivers
        List<ReceiverDTO> mergedList = groupedByUserName.values().stream()
                .map(list -> list.stream()
                        .reduce((a, b) -> {
                            a.setEmailReceivers(a.getEmailReceivers() != null ? a.getEmailReceivers() : b.getEmailReceivers());
                            a.setMobileReceivers(a.getMobileReceivers() != null ? a.getMobileReceivers() : b.getMobileReceivers());
                            a.setFeiShuBotsHookUrls(a.getFeiShuBotsHookUrls() != null ? a.getFeiShuBotsHookUrls() : b.getFeiShuBotsHookUrls());
                            return a;
                        }).orElse(null))
                .collect(Collectors.toList());

        messageDTO.setReceivers(mergedList);


        //发送信息
        messageClient.create(messageDTO);
        log.info("监控预警:执行发送告警消息,监控规则名称：{}", conf.getConfName());
    }

    private static List<String> getMessageParams(VarProcessMonitoringAlertConf conf, String monitoringTargetName, List<String> dataList, LocalDateTime nowDate, Integer messageType,
                                                 MessageDTO messageDTO, MonitorConfigurationSaveInputVO.AlertInfo alertInfo) {
        MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class);
        //消息参数
        List<String> messageParams = new ArrayList<>();
        if (messageType == MagicNumbers.ONE) {
            if (conf.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
                //指标的预警信息
                messageDTO.setMessageId("1007");
                messageParams.add("[" + conf.getConfName() + "]");
                messageParams.add(conf.getServiceName() + "(" + conf.getServiceVersion() + ")");
                messageParams.add(conf.getManifestName());
                messageParams.add(monitoringTargetName);
                messageParams.add(conf.getMonitoringTarget().getDesc());
                messageParams.add("近" + paramConfiguration.getTime() + paramConfiguration.getTimeUnit().getDesc() + "内" + conf.getMonitoringTarget().getDesc());
                messageParams.add(dataList.get(0));
                messageParams.add(nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                //服务的预警信息
                messageDTO.setMessageId("1008");
                messageParams.add("[" + conf.getConfName() + "]");
                messageParams.add(conf.getServiceName() + "(" + conf.getServiceVersion() + ")");
                messageParams.add(conf.getMonitoringTarget().getDesc());
                StringBuilder sb = new StringBuilder("近");
                sb.append(paramConfiguration.getTime()).append(paramConfiguration.getTimeUnit().getDesc()).append("内");
                if (conf.getMonitoringTarget() == MonitoringConfTargetEnum.RESPONSE_CODE_RATIO) {
                    //触发条件信息
                    MonitorConfigurationSaveInputVO.TriggerCondition triggerCondition = JSONObject.parseObject(conf.getTriggerCondition(), MonitorConfigurationSaveInputVO.TriggerCondition.class);
                    List<MonitorConfigurationSaveInputVO.TriggerConditionDetail> conditionList = triggerCondition.getConditionList();
                    for (int i = 0; i < conditionList.size(); i++) {
                        sb.append(conditionList.get(i).getResponseCode()).append("的响应码占比为").append(dataList.get(i)).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                } else {
                    sb.append(conf.getMonitoringTarget().getDesc()).append("为").append(dataList.get(0));
                }
                messageParams.add(sb.toString());
                messageParams.add(nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        } else {
            if (conf.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
                //指标的恢复提醒信息
                messageDTO.setMessageId("1009");
                messageParams.add(conf.getConfName());
                messageParams.add(conf.getServiceName() + "(" + conf.getServiceVersion() + ")");
                messageParams.add(conf.getManifestName());
                messageParams.add(monitoringTargetName);
                messageParams.add(conf.getMonitoringTarget().getDesc());
                messageParams.add(alertInfo.getRestoreRemindConf().getNormalCount().toString());
                messageParams.add(nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                //服务的恢复提醒信息
                messageDTO.setMessageId("1010");
                messageParams.add(conf.getConfName());
                messageParams.add(conf.getServiceName() + "(" + conf.getServiceVersion() + ")");
                messageParams.add(conf.getMonitoringTarget().getDesc());
                messageParams.add(alertInfo.getRestoreRemindConf().getNormalCount().toString());
                messageParams.add(nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
        return messageParams;
    }


    private List<String> getDataStringList(MonitoringConfTargetEnum targetEnum, List<BigDecimal> dataList) {
        return dataList.stream().map(item -> {
            return getDataString(targetEnum, item);
        }).collect(Collectors.toList());
    }

    private String getDataString(MonitoringConfTargetEnum targetEnum, BigDecimal data) {
        if (data == null) {
            return "null";
        }
        String dataString;
        DecimalFormat decimalFormat;
        //格式化指标值
        switch (targetEnum) {
            case AVG_RESPONSE_TIME:
                decimalFormat = new DecimalFormat("0.00");
                dataString = decimalFormat.format(data);
                break;
            case FAILURE_RATE:
            case RESPONSE_CODE_RATIO:
            case MISSING_RATIO:
            case SPECIAL_RATIO:
                decimalFormat = new DecimalFormat("0.00%");
                dataString = decimalFormat.format(data);
                break;
            default:
                dataString = String.valueOf(data);
        }
        return dataString;
    }
}
