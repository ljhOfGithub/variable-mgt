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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.google.common.collect.Lists;
import com.wiseco.boot.bus.RemoteBusClient;
import com.wiseco.boot.bus.core.Event;
import com.wiseco.boot.bus.core.EventType;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.boot.lock.LockClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.utils.IdentityGenerator;
import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.model.engine.VarDto;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.DbTypeConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingExecuteContext;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingDataTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileSpiltCharEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskResultStatusEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.event.EventPublisherUtil;
import com.wiseco.var.process.app.server.event.obj.BacktrackingTaskFailEvent;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessConfigDefaultService;
import com.wiseco.var.process.app.server.service.backtracking.async.AsyncBacktrackingExecute;
import com.wiseco.var.process.app.server.service.backtracking.param.BacktrackingParamImportService;
import com.wiseco.var.process.app.server.service.backtracking.param.OutsideParamImportServiceFactory;
import com.wiseco.var.process.app.server.service.common.OssFileService;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wiseco.var.process.engine.compiler.ServiceExporter;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.decision.osg.sdk.OsgUtil.getClient;

/**
 * @author xupei
 */
@RefreshScope
@Service
@Slf4j
public class BacktrackingTaskBiz {

    @Resource
    private BacktrackingService backtrackingService;
    @Resource
    private BacktrackingTaskService backtrackingTaskService;
    @Resource
    private AsyncBacktrackingExecute asyncBacktrackingExecute;
    @Resource
    private OutsideParamImportServiceFactory outsideParamImportServiceFactory;
    @Resource
    private SftpClientService sftpClientService;
    @Resource
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Resource
    private DbOperateService dbOperateService;
    @Resource
    private BacktrackingDataModelService backtrackingDataModelService;
    @Resource
    private BacktrackingBiz backtrackingBiz;
    @Resource
    private ServiceExporter serviceExporter;
    @Resource
    private OssFileService ossFileService;

    @Autowired
    private VarProcessConfigDefaultService varProcessConfigDefaultValueService;

    @Autowired
    private RemoteBusClient remoteBusClient;

    @Resource(name = "distributedLockClient")
    private LockClient lockClient;

    @Value("${backtracking.task.read_size:10}")
    private int readSize;

    @Value("${wiseco.boot.oss.endpoint}")
    private String ossServer;

    @Value("${wiseco.boot.oss.bucket}")
    private String ossBucket;

    @Value("${wiseco.boot.oss.secretKey}")
    private String ossSecretKey;

    @Value("${wiseco.boot.oss.appKey}")
    private String ossAccessKey;

    @Value("${spring.kafka.producer.topics.backtrackingTopic:backtracking_task}")
    private String backtrackingTopic;

    @Value("${spring.datasourcetype:mysql}")
    private String dataSourceType;
    @Resource(name = "remoteCacheClient")
    private CacheClient cacheClient;

    private static final String VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT = "var_process_batch_backtracking_task_result";

    public static final String PERCENT_99 = "99.00%";
    public static final String PERCENT_0 = "0.00%";

    /**
     * 暂停任务
     *
     * @param taskId 任务id
     */
    public void pauseExecute(Long taskId) {
        VarProcessBatchBacktrackingTask task = backtrackingTaskService.getById(taskId);
        Assert.notNull(task, "找不到任务");

        if (task.getStatus() != BacktrackingTaskStatusEnum.IN_PROGRESS) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_PAUSE_EXECUTE_ERROR, "任务状态非执行中，无法暂停");
        }

        if (StringUtils.isEmpty(task.getCompletion())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_PAUSE_EXECUTE_ERROR, "任务数据初始化未完成，无法暂停");
        }

        if (PERCENT_99.equals(task.getCompletion())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_PAUSE_EXECUTE_ERROR, "任务即将执行完成，无法暂停");
        }

        if (cacheClient.exists(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + taskId)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_PAUSE_EXECUTE_ERROR, "任务已全部开始执行，无法暂停");
        }
        task.setStatus(BacktrackingTaskStatusEnum.PAUSED);
        backtrackingTaskService.updateById(task);
    }

    /**
     * 定时任务执行
     *
     * @param taskContext 任务参数
     * @return 任务结果
     */
    public ProcessResult scheduledExecute(TaskContext taskContext) {
        log.info("BacktrackingTask开始执行");
        BacktrackingTaskParam jobParam = JSON.parseObject(taskContext.getJobParams(), BacktrackingTaskParam.class);
        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = taskContext.getOmsLogger();
        omsLogger.info("BacktrackingTask start to process, current JobParams is {}.", taskContext.getJobParams());
        try {
            VarProcessBatchBacktracking backtracking = backtrackingService.getById(jobParam.getBacktrackingId());
            Assert.notNull(backtracking, "批量回溯不存在");
            //获取任务信息
            VarProcessBatchBacktrackingTask taskInfo = getTaskInfo(jobParam, backtracking, true);
            jobParam.setTaskInfo(taskInfo);
            jobParam.setBacktracking(backtracking);
            executeTask(jobParam);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            omsLogger.error(e.getMessage(), e);
            return new ProcessResult(false, "BacktrackingTask execute failed, please check log");
        }
        omsLogger.info("BacktrackingTask complete process, current JobParams is {}.", taskContext.getJobParams());
        log.info("BacktrackingTask执行结束");
        return new ProcessResult(true, "success execute BacktrackingTask");
    }

    /**
     * 手动执行
     *
     * @param backtrackingId    回溯id
     * @param taskId            任务id
     * @param isContinueExecute 是否继续执行
     * @return 任务结果
     */
    public ProcessResult manualExecute(Long backtrackingId, Long taskId, Boolean isContinueExecute) {
        final BacktrackingTaskParam param = BacktrackingTaskParam.builder().backtrackingId(backtrackingId).taskId(taskId).isContinueExecute(isContinueExecute).build();
        VarProcessBatchBacktracking backtracking = backtrackingService.getById(param.getBacktrackingId());
        Assert.notNull(backtracking, "批量回溯不存在");
        //手动执行查询当前批量回溯是否有正在执行的任务
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
            //获取锁: 如果成功获取到锁，判断有没有执行中的任务；没有获取到锁，说明一定有任务正在执行
            boolean isHoldLock = lockClient.acquire(backtrackingId.toString());
            if (isHoldLock) {
                long count = backtrackingTaskService.count(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                        .eq(VarProcessBatchBacktrackingTask::getBacktrackingId, backtrackingId)
                        .eq(VarProcessBatchBacktrackingTask::getStatus, BacktrackingTaskStatusEnum.IN_PROGRESS)
                        .ne(taskId != null, VarProcessBatchBacktrackingTask::getId, taskId));
                if (count > 0) {
                    //释放锁
                    lockClient.release(backtrackingId.toString());
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "已有任务正在执行，请稍后再试");
                }
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "已有任务正在执行，请稍后再试");
            }
        }

        //获取任务信息
        VarProcessBatchBacktrackingTask taskInfo = getTaskInfo(param, backtracking, false);
        param.setTaskInfo(taskInfo);
        param.setBacktracking(backtracking);
        asyncBacktrackingExecute.asyncExecute(param);
        return new ProcessResult(true, "success execute BacktrackingTask");
    }


    /**
     * 获取任务信息，如果任务存在，就更新任务；否则就新建一个任务
     *
     * @param taskParam          任务参数
     * @param backtracking       批量回溯
     * @param isScheduledExecute 是否定时任务
     * @return 批量回溯任务实体
     */
    private VarProcessBatchBacktrackingTask getTaskInfo(BacktrackingTaskParam taskParam, VarProcessBatchBacktracking backtracking, boolean isScheduledExecute) {
        VarProcessBatchBacktrackingTask taskInfo;
        if (taskParam.getTaskId() != null) {
            taskInfo = backtrackingTaskService.getById(taskParam.getTaskId());
            if (taskInfo == null) {
                log.error("批次任务不存在,批次任务Id:" + taskParam.getTaskId());
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_NOT_FOUND, "批次任务不存在");
            }

            if (!taskParam.getIsContinueExecute()) {
                //重新执行校验
                if (taskInfo.getStatus() == BacktrackingTaskStatusEnum.IN_PROGRESS || taskInfo.getStatus() == BacktrackingTaskStatusEnum.SUCCESS || taskInfo.getStatus() == BacktrackingTaskStatusEnum.FILE_GENERATING) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "批次任务执行中或已成功，无法重新执行");
                }
                taskInfo.setStartTime(new Date());
                taskInfo.setCompletion("");
            } else {
                //继续执行校验
                if (BacktrackingTaskStatusEnum.PAUSED != taskInfo.getStatus()) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "任务状态非暂停,无法继续执行");
                }

                if (StringUtils.isEmpty(taskInfo.getCompletion())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "数据初始化未完成,无法继续执行");
                }
            }
            taskInfo.setErrorMessage(null);
            taskInfo.setCreatedUser(isScheduledExecute ? "SSOAdmin" : SessionContext.getSessionUser().getUsername());
            taskInfo.setUpdatedTime(new Date());
            taskInfo.setStatus(BacktrackingTaskStatusEnum.IN_PROGRESS);
            backtrackingTaskService.updateById(taskInfo);
        } else {
            //新建任务
            taskInfo = VarProcessBatchBacktrackingTask.builder().backtrackingId(taskParam.getBacktrackingId()).startTime(new Date())
                    .status(BacktrackingTaskStatusEnum.IN_PROGRESS).createdUser(isScheduledExecute ? "SSOAdmin" : SessionContext.getSessionUser().getUsername())
                    .code(String.valueOf(IdentityGenerator.nextId())).build();
            backtrackingTaskService.save(taskInfo);
        }

        //手动任务需要释放锁，删掉历史执行记录
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {

            lockClient.release(backtracking.getId().toString());

            backtrackingTaskService.remove(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                    .eq(VarProcessBatchBacktrackingTask::getBacktrackingId, backtracking.getId())
                    .ne(VarProcessBatchBacktrackingTask::getId, taskInfo.getId()));
        }
        return taskInfo;
    }

    /**
     * 执行任务
     *
     * @param taskParam 任务参数
     */
    public void executeTask(BacktrackingTaskParam taskParam) {
        VarProcessBatchBacktracking backtracking = taskParam.getBacktracking();
        VarProcessBatchBacktrackingTask taskInfo = taskParam.getTaskInfo();
        try {
            //初始化结果集表
            SavaResultDataParam savaResultDataParam = initResultTable(backtracking, taskParam.getIsContinueExecute());

            //判断是否是重新执行，是则清除历史数据
            removeHistoryData(taskParam, backtracking.getResultTable());

            //获取数据模型使用的外数服务的取值方式映射
            Map<String, String> outsideServiceStrategyMap = getOutsideServiceStrategyMap(backtracking);

            //构建批量任务参数
            final BacktrackingExecuteContext executeContext = BacktrackingExecuteContext.builder().varProcessBatchBacktracking(backtracking).varProcessBatchBacktrackingTask(taskInfo)
                    .enableTrace(backtracking.getEnableTrace()).outsideServiceStrategyMap(outsideServiceStrategyMap).savaResultDataParam(savaResultDataParam).build();

            if (!taskParam.getIsContinueExecute()) {
                //初始化参数
                initParam(backtracking, taskInfo);
                //更新完成率
                taskInfo.setCompletion(PERCENT_0);
                taskInfo.setUpdatedTime(new Date());
                backtrackingTaskService.updateById(taskInfo);
            }

            //开始执行
            executionTask(executeContext);
        } catch (Throwable e) {
            cacheClient.evict(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + taskInfo.getId());
            taskInfo.setErrorMessage(e.getMessage());
            taskInfo.setStatus(BacktrackingTaskStatusEnum.FAIL);
            taskInfo.setEndTime(new Date());
            taskInfo.setUpdatedTime(new Date());
            backtrackingTaskService.updateById(taskInfo);

            //定时任务才可以发送重试事件
            if (taskInfo.getStatus() == BacktrackingTaskStatusEnum.FAIL && backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
                BacktrackingSaveInputVO.TaskInfo taskInfoDto = JSON.parseObject(backtracking.getTaskInfo(), BacktrackingSaveInputVO.TaskInfo.class);
                if (taskInfoDto.getIsRetry() != null && taskInfoDto.getIsRetry().equals(1)) {
                    BacktrackingTaskParam backtrackingTaskParam = BacktrackingTaskParam.builder().backtrackingId(taskInfo.getBacktrackingId()).taskId(taskInfo.getId()).build();
                    log.warn("定时任务失败重试，入参-》{}", backtrackingTaskParam);
                    EventPublisherUtil.publishEvent(new BacktrackingTaskFailEvent(backtrackingTaskParam));
                }
            }
        }
    }

    /**
     * 初始化结果表，获取结果表信息
     *
     * @param backtracking          批量回溯信息
     * @param isContinueExecuteTask 是否重试
     * @return 结果表信息
     */
    private SavaResultDataParam initResultTable(VarProcessBatchBacktracking backtracking, boolean isContinueExecuteTask) {
        //查询清单引用变量（有序）
        LinkedHashMap<String, String> orderColum = new LinkedHashMap<>();
        orderColum.put("order_no", "ASC");
        String condition = " manifest_id = " + backtracking.getManifestId();
        List<Map<String, Object>> variableMapList = dbOperateService.queryForList("var_process_manifest_header", Arrays.asList("variable_code", "variable_type", "is_index"), condition, orderColum, null, null);

        //可作为搜索条件的变量
        LinkedHashMap<String, String> conditionVariableMap = new LinkedHashMap<>();
        variableMapList.stream().filter(
                map -> DmAdapter.mapGetIgnoreCase(map,"is_index") != null && DmAdapter.mapGetIgnoreCase(map,"is_index").toString().equals("1"))
                .forEach(item -> conditionVariableMap.put(String.valueOf(DmAdapter.mapGetIgnoreCase(item,"variable_code")), String.valueOf(DmAdapter.mapGetIgnoreCase(item,"variable_type"))));

        //固定列
        LinkedHashMap<String, String> fixedColumMap = new LinkedHashMap<>();
        fixedColumMap.put("increment_id", "long");
        fixedColumMap.put("manifest_id", "long");
        fixedColumMap.put("backtracking_id", "long");
        fixedColumMap.put("batch_no", "string");
        fixedColumMap.put("result_code", "string");
        fixedColumMap.put("external_serial_no", "string");
        fixedColumMap.put("request_time", "datetime");
        fixedColumMap.put("variables", "longtext");

        //结果表全部列
        LinkedHashMap<String, String> columMap = new LinkedHashMap<>(fixedColumMap);
        columMap.putAll(conditionVariableMap);

        //需要建立索引的列和索引类型
        ArrayList<String> indexColum = new ArrayList<>(conditionVariableMap.keySet());
        indexColum.addAll(Arrays.asList("backtracking_id", "batch_no", "result_code", "external_serial_no", "request_time"));

        //创建表
        dbOperateService.createTable(backtracking.getResultTable(), columMap, indexColum, "increment_id");

        //手动并且非继续执行的任务，删除历史数据
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL && !isContinueExecuteTask) {
            dbOperateService.simpleDeleteRecord(backtracking.getResultTable(), "backtracking_id", backtracking.getId().toString());
        }

        //指标清单的全部指标
        List<String> varNameList = variableMapList.stream().map(item -> String.valueOf(DmAdapter.mapGetIgnoreCase(item,"variable_code"))).collect(Collectors.toList());

        //表头
        List<String> hearList = new ArrayList<>(columMap.keySet());

        //变量类型映射
        Map<String, String> varTypeMap = new HashMap<>(MagicNumbers.EIGHT);
        variableMapList.forEach(item -> varTypeMap.put(String.valueOf(DmAdapter.mapGetIgnoreCase(item,"variable_code")), String.valueOf(DmAdapter.mapGetIgnoreCase(item,"variable_type"))));

        //查询缺失值配置
        Map<String, String> defaultValueMap = varProcessConfigDefaultValueService
                .list(new QueryWrapper<VarProcessConfigDefault>().lambda().select(VarProcessConfigDefault::getDataType, VarProcessConfigDefault::getDefaultValue))
                .stream().collect(Collectors.toMap(VarProcessConfigDefault::getDataType, VarProcessConfigDefault::getDefaultValue, (i1, i2) -> i1));

        return SavaResultDataParam.builder().defaultValueMap(defaultValueMap).fixedColumMap(fixedColumMap).indexColumMap(conditionVariableMap).varNameList(varNameList).hearList(hearList).varTypeMap(varTypeMap).build();
    }

    /**
     * 判断是否是重新执行，是则清除历史数据
     *
     * @param taskParam   任务参数
     * @param resultTable 结果表名称
     */
    private void removeHistoryData(BacktrackingTaskParam taskParam, String resultTable) {
        //重新执行删除全部可能产生的数据
        if (taskParam.getTaskId() != null && !taskParam.getIsContinueExecute()) {
            //查询该批次的请求参数
            List<Map<String, Object>> dataMap = dbOperateService.queryForList(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, Collections.singletonList("code"),
                    " task_id = " + taskParam.getTaskId(), null, null, null);
            List<String> codeList = dataMap.stream().map(item -> String.valueOf(item.get("code"))).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(codeList)) {
                //删除详细记录
                dbOperateService.deleteByIn(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, "code", codeList);
                //删除结果集
                if (dbOperateService.isTableExist(resultTable)) {
                    dbOperateService.deleteByIn(resultTable, "result_code", codeList);
                }
            }
        }
    }

    /**
     * 获取数据模型使用的外数服务的取值方式映射
     *
     * @param backtracking 批量回溯Id
     * @return 数据模型名称 : 取值方式
     */
    private Map<String, String> getOutsideServiceStrategyMap(VarProcessBatchBacktracking backtracking) {
        List<VarProcessBatchBacktrackingDataModel> varProcessBatchBacktrackingDataModelList = backtrackingDataModelService.list(
                new LambdaQueryWrapper<VarProcessBatchBacktrackingDataModel>()
                        .eq(VarProcessBatchBacktrackingDataModel::getBacktrackingId, backtracking.getId())
                        .eq(VarProcessBatchBacktrackingDataModel::getSourceType, VarProcessDataModelSourceType.OUTSIDE_SERVER));

        if (CollectionUtils.isEmpty(varProcessBatchBacktrackingDataModelList)) {
            return null;
        }

        return varProcessBatchBacktrackingDataModelList.stream()
                .filter(item -> !StringUtils.isEmpty(item.getOutsideServiceStrategy()))
                .collect(Collectors.toMap(VarProcessBatchBacktrackingDataModel::getObjectName, item -> item.getOutsideServiceStrategy().getOutsideCallStrategy(), (k1, k2) -> k1));
    }

    /**
     * 初始化请求参数
     *
     * @param backtracking 批量回溯实体
     * @param taskInfo     任务实体
     */
    private void initParam(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask taskInfo) throws URISyntaxException {
        // 获取取值方式
        final BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo = JSON.parseObject(backtracking.getDataGetTypeInfo(), BacktrackingSaveInputVO.DataGetTypeInfo.class);
        //获取取数服务
        BacktrackingParamImportService backtrackingParamImportService = outsideParamImportServiceFactory.getOutsideParamImportService(dataGetTypeInfo);
        switch (backtrackingParamImportService.getType()) {
            case DATABASE:
                getParamOfDb(backtracking, taskInfo, dataGetTypeInfo, backtrackingParamImportService);
                break;
            case FTP_FILE:
                getParamOfFtpFile(backtracking, taskInfo, dataGetTypeInfo, backtrackingParamImportService);
                break;
            case LOCAL_FILE:
                getParamOfLocalFile(backtracking, taskInfo, dataGetTypeInfo, backtrackingParamImportService);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "不支持的取数方式");
        }
    }

    /**
     * 从数据库获取外部传入参数
     *
     * @param backtracking                   批量回溯
     * @param taskInfo                       当前任务信息
     * @param dataGetTypeInfo                外部数据取值信息
     * @param backtrackingParamImportService 外部数据获取服务
     */
    private void getParamOfDb(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask taskInfo,
                              BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, BacktrackingParamImportService backtrackingParamImportService) {
        int from = 0;
        int i = 1;
        try {
            //循环执行
            while (true) {
                //获取外部传入的参数
                List<String> params = backtrackingParamImportService.importDataByDb(dataGetTypeInfo, from, readSize);
                if (CollectionUtils.isEmpty(params)) {
                    break;
                }
                from = from + readSize;
                //保存参数
                saveRequestJson(params, backtracking.getId(), taskInfo.getId(), dataGetTypeInfo.getMsgFormat());
                //防止批量回溯定时检查任务改变任务状态，更新任务的修改时间
                if (i++ % MagicNumbers.TEN == 0) {
                    taskInfo.setUpdatedTime(new Date());
                    backtrackingTaskService.updateById(taskInfo);
                }
            }
        } catch (Exception e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, e.getMessage());
        }
    }


    /**
     * 从FTP文件获取外部传入参数
     *
     * @param backtracking                   批量回溯
     * @param taskInfo                       当前任务信息
     * @param dataGetTypeInfo                外部数据取值信息
     * @param backtrackingParamImportService 外部数据获取服务
     */
    private void getParamOfFtpFile(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask taskInfo, BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, BacktrackingParamImportService backtrackingParamImportService) {
        BatchBacktrackingTriggerTypeEnum triggerType = backtracking.getTriggerType();
        //起始行
        int startLine = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getStartLine() : dataGetTypeInfo.getDataFile().getStartLine();
        //编码
        String charset = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getCharsetType().getDesc() : dataGetTypeInfo.getDataFile().getCharsetType().getDesc();
        //是否包含表头
        boolean includeHeader = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getIncludeHeader() : dataGetTypeInfo.getDataFile().getIncludeHeader();
        //映射方式
        BacktrackingDataTypeEnum inputFileType = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getInputFileType() : dataGetTypeInfo.getDataFile().getInputFileType();
        //分隔符
        String spiltChar = getSpiltChar(backtracking, dataGetTypeInfo);
        long ftpServerId = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getFtpServerId() : dataGetTypeInfo.getDataFile().getFtpServerId();
        SftpClient sftpClient = sftpClientService.login(ftpServerId);
        int i = 1;
        try (InputStream inputStream = backtrackingParamImportService.importDataByFtpFile(dataGetTypeInfo, taskInfo, backtracking.getTriggerType(), sftpClient);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            //表头
            List<String> headers = new ArrayList<>();
            if (includeHeader) {
                headers = backtrackingBiz.getHeaders(bufferedReader, spiltChar);
            }

            while (true) {
                List<Map<String, Object>> dataMapList = backtrackingBiz.getDataMapList(bufferedReader, spiltChar, startLine, readSize, headers);
                if (CollectionUtils.isEmpty(dataMapList)) {
                    break;
                }
                List<String> params;
                if (inputFileType == BacktrackingDataTypeEnum.JSON) {
                    //整体映射选择字段
                    List<String> selectColumns = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getSelectColumns() : dataGetTypeInfo.getDataFile().getSelectColumns();
                    params = backtrackingParamImportService.getJsonListOfWholeMapping(dataMapList, selectColumns);
                } else {
                    //数据模型映射
                    List<DataModelTreeVo> dataModelTreeVoList = triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED ? dataGetTypeInfo.getDataFileScheduled().getDataModelTree() : dataGetTypeInfo.getDataFile().getDataModelTree();
                    params = backtrackingParamImportService.getJsonListOfAttributeMapping(dataMapList, dataModelTreeVoList);
                }

                startLine = 0;

                //保存参数
                saveRequestJson(params, backtracking.getId(), taskInfo.getId(), dataGetTypeInfo.getMsgFormat());

                //防止批量回溯定时检查任务改变任务状态，更新任务的修改时间
                if (i++ % MagicNumbers.TEN == 0) {
                    taskInfo.setUpdatedTime(new Date());
                    backtrackingTaskService.updateById(taskInfo);
                }
            }
        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, e.getMessage());
        } finally {
            sftpClientService.logout(sftpClient);
        }
    }

    /**
     * 从本地文件获取外部传入参数
     *
     * @param backtracking                   批量回溯
     * @param taskInfo                       当前任务信息
     * @param dataGetTypeInfo                外部数据取值信息
     * @param backtrackingParamImportService 外部数据获取服务
     */
    private void getParamOfLocalFile(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask taskInfo,
                                     BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, BacktrackingParamImportService backtrackingParamImportService) throws URISyntaxException {
        File tempFile = null;
        int i = 1;
        BacktrackingSaveInputVO.DataFile dataFile = dataGetTypeInfo.getDataFile();
        //起始行
        int startLine = dataFile.getStartLine();
        //编码
        String charset = dataFile.getCharsetType().getDesc();
        //是否包含表头
        boolean includeHeader = dataFile.getIncludeHeader();
        //数据模型映射
        List<DataModelTreeVo> dataModelTreeVoList = dataFile.getDataModelTree();
        //分隔符
        String spiltChar = getSpiltChar(backtracking, dataGetTypeInfo);
        try {
            tempFile = File.createTempFile(backtracking.getId() + "_" + taskInfo.getId() + "_param", ".txt");
            //下载文件到本地
            downFileToLocal(dataGetTypeInfo, backtrackingParamImportService, tempFile, charset);

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(tempFile.toPath()), charset))) {
                //表头
                List<String> headers = new ArrayList<>();
                if (includeHeader) {
                    headers = backtrackingBiz.getHeaders(bufferedReader, spiltChar);
                }

                while (true) {
                    List<Map<String, Object>> dataMapList = backtrackingBiz.getDataMapList(bufferedReader, spiltChar, startLine, readSize, headers);
                    if (CollectionUtils.isEmpty(dataMapList)) {
                        break;
                    }
                    startLine = 0;
                    List<String> params;
                    if (dataFile.getInputFileType() == BacktrackingDataTypeEnum.JSON) {
                        params = backtrackingParamImportService.getJsonListOfWholeMapping(dataMapList, dataFile.getSelectColumns());
                    } else {
                        params = backtrackingParamImportService.getJsonListOfAttributeMapping(dataMapList, dataModelTreeVoList);
                    }

                    //保存参数
                    saveRequestJson(params, backtracking.getId(), taskInfo.getId(), dataGetTypeInfo.getMsgFormat());

                    //防止批量回溯定时检查任务改变任务状态，更新任务的修改时间
                    if (i++ % MagicNumbers.TEN == 0) {
                        taskInfo.setUpdatedTime(new Date());
                        backtrackingTaskService.updateById(taskInfo);
                    }
                }
            }

        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, e.getMessage());
        } finally {
            if (tempFile != null) {
                boolean flag = tempFile.delete();
                if (!flag) {
                    log.error("文件删除失败,文件-》{}", tempFile);
                }
            }
        }
    }

    /**
     * 下载文件到本地
     *
     * @param dataGetTypeInfo                数据获取参数
     * @param backtrackingParamImportService 批量回溯获取参数
     * @param tempFile                       临时文件
     * @param charset                        编码格式
     */
    private void downFileToLocal(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, BacktrackingParamImportService backtrackingParamImportService, File tempFile, String charset) throws URISyntaxException {
        //oss配置
        ossFileService.setOssConfig(ossServer, ossBucket, ossAccessKey, ossSecretKey);

        try (S3Client s3Client = getClient();
             InputStream inputStream = backtrackingParamImportService.importDataByLocalFile(s3Client, dataGetTypeInfo);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile.toPath()), charset))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件预览分隔符
     *
     * @param backtracking    批量回溯实体
     * @param dataGetTypeInfo 数据获取信息
     * @return 分隔符
     */
    private String getSpiltChar(VarProcessBatchBacktracking backtracking, BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo) {
        String spiltChar;
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
            if (dataGetTypeInfo.getDataFile().getSplit() == BacktrackingFileSpiltCharEnum.OTHER) {
                spiltChar = dataGetTypeInfo.getDataFile().getSplitKey();
            } else {
                spiltChar = dataGetTypeInfo.getDataFile().getSplit().getCode();
            }
        } else {
            if (dataGetTypeInfo.getDataFileScheduled().getSplit() == BacktrackingFileSpiltCharEnum.OTHER) {
                spiltChar = dataGetTypeInfo.getDataFileScheduled().getSplitKey();
            } else {
                spiltChar = dataGetTypeInfo.getDataFileScheduled().getSplit().getCode();
            }
        }
        return spiltChar;
    }

    /**
     * 保存请求json
     *
     * @param params         请求json
     * @param backtrackingId 批量回溯id
     * @param taskId         任务id
     * @param msgFormat      报文数据格式
     */
    private void saveRequestJson(List<String> params, Long backtrackingId, Long taskId, String msgFormat) {
        ArrayList<List<String>> taskJsonList = new ArrayList<>();
        //保存参数
        for (String param : params) {
            // 保存转义后的JSON至数据库，MYSQL会自动去除转义一次
            // 为保证插入到MYSQL的也是转义后的JSON，此处手动转义一次JSON
            boolean isEscape = dataSourceType.equals(DbTypeConstant.MYSQL) && (ServiceMsgFormatEnum.ESCAPED_JSON.getCode().equals(msgFormat) || ServiceMsgFormatEnum.JSON.getCode().equals(msgFormat));
            if (isEscape) {
                param = StringEscape.escapeRawString(param);
            }
            String code = String.valueOf(IdentityGenerator.nextId());
            taskJsonList.add(Arrays.asList(backtrackingId.toString(), taskId.toString(), code, BacktrackingTaskResultStatusEnum.NOT_EXECUTED.name(), param));
        }

        //保存请求json
        dbOperateService.batchInsert(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, Arrays.asList("backtracking_id", "task_id", "code", "status", "request_info"), taskJsonList);
    }


    /**
     * 执行
     *
     * @param executeContext 任务入参
     */
    private void executionTask(BacktrackingExecuteContext executeContext) {
        VarProcessBatchBacktracking backtracking = executeContext.getVarProcessBatchBacktracking();
        VarProcessBatchBacktrackingTask backtrackingTask = executeContext.getVarProcessBatchBacktrackingTask();
        try {
            //获取全部任务信息
            List<Map<String, Object>> dataList = dbOperateService.queryForList(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, Arrays.asList("code", "status"), " task_id = " + backtrackingTask.getId(), null, null, null);

            //未执行任务
            List<String> notExecutedCodeList = dataList.stream()
                    .filter(item -> DmAdapter.mapGetIgnoreCase(item, "status").toString().equals(BacktrackingTaskResultStatusEnum.NOT_EXECUTED.name()))
                    .map(item -> DmAdapter.mapGetIgnoreCase(item, "code").toString()).collect(Collectors.toList());

            //组装kafka消息
            JSONObject kafkaMessage = getKafkaMessage(executeContext, dataList.size(), backtracking, backtrackingTask);

            //分段执行
            for (List<String> codeList : Lists.partition(notExecutedCodeList, readSize)) {
                //校验任务状态
                VarProcessBatchBacktrackingTask currentTask = backtrackingTaskService.getById(backtrackingTask.getId());
                if (currentTask.getStatus() == BacktrackingTaskStatusEnum.PAUSED) {
                    return;
                }
                //发送消息 task服务监听执行
                kafkaMessage.put("codeList", codeList);
                callExecute(kafkaMessage.toJSONString());
            }

            //用于判断是否可暂停
            cacheClient.put(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + backtrackingTask.getId(), "true");
        } catch (Throwable e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, e.getMessage());
        }
    }


    private JSONObject getKafkaMessage(BacktrackingExecuteContext executeContext, int total, VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask backtrackingTask) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("backtrackingId", backtracking.getId());
        jsonObject.put("manifestId", backtracking.getManifestId());
        jsonObject.put("serialNoField", backtracking.getSerialNo());
        jsonObject.put("taskName", backtracking.getName());
        jsonObject.put("batchNo", backtrackingTask.getCode());
        jsonObject.put("resultTable", backtracking.getResultTable());
        jsonObject.put("outsideServiceStrategyMap", executeContext.getOutsideServiceStrategyMap());
        jsonObject.put("enableTrace", executeContext.getEnableTrace());
        jsonObject.put("savaResultDataParam", executeContext.getSavaResultDataParam());
        jsonObject.put("taskId", backtrackingTask.getId());
        jsonObject.put("total", total);
        jsonObject.put("msgFormat", JSON.parseObject(backtracking.getDataGetTypeInfo(), BacktrackingSaveInputVO.DataGetTypeInfo.class).getMsgFormat());
        return jsonObject;
    }

    /**
     * 发送消息给kafka,执行
     *
     * @param message kafka消息队列的接受体
     */
    public void callExecute(String message) {
        try {
            Event<String> event = new Event<>();
            event.setTopicName(backtrackingTopic);
            event.setEventType(EventType.BETWEEN_MODULES);
            event.setMessage(message);
            remoteBusClient.send(event);
        } catch (Throwable e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "send message error");
        }
    }

    /**
     * 获取引擎
     *
     * @param manifestId 清单id
     * @return 引擎
     */
    private Engine getEngine(Long manifestId) {
        try {
            final VariableFlowQueryDto queryDto = VariableFlowQueryDto
                    .builder()
                    .manifestId(manifestId)
                    .build();
            final List<VarProcessVariable> variableFlow = varProcessManifestVariableService.getVariableFlow(queryDto);
            Assert.notNull(variableFlow, "变量流为空");
            final Set<VarDto> varDefSet = variableFlow.stream().map(variable -> {
                VarDto varDto = new VarDto();
                varDto.setName(variable.getName());
                varDto.setType(variable.getDataType());
                varDto.setVersion(variable.getVersion());
                return varDto;
            }).collect(Collectors.toSet());
            return serviceExporter.getServiceInterfaceEngine(queryDto.getManifestId(), varDefSet);
        } catch (Throwable e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXECUTE_ERROR, "引擎获取失败");
        }
    }

    /**
     * 保存结果表
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SavaResultDataParam {
        //固定列
        private LinkedHashMap<String, String> fixedColumMap;
        //索引列
        private LinkedHashMap<String, String> indexColumMap;
        //表头
        private List<String> hearList;
        //清单引用的指标（有序）
        private List<String> varNameList;
        //变量类型
        private Map<String, String> varTypeMap;
        //默认值配置
        private Map<String, String> defaultValueMap;
    }
}
