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
package com.wiseco.var.process.app.server.job.backtracking;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskResultStatusEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.event.EventPublisherUtil;
import com.wiseco.var.process.app.server.event.obj.BacktrackingTaskFailEvent;
import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingResultDatabaseService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskService;
import com.wiseco.var.process.app.server.service.backtracking.async.BacktrackingUploadResultFileService;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 批量回溯定时任务执行
 *
 * @author xupei
 */
@Component
@Slf4j
public class BacktrackingTaskInspectProcessor implements BasicProcessor {
    @Resource
    private BacktrackingTaskService backtrackingTaskService;
    @Resource
    private BacktrackingResultDatabaseService backtrackingResultDatabaseService;
    @Resource
    private BacktrackingService backtrackingService;

    @Autowired
    private BacktrackingUploadResultFileService backtrackingUploadResultFileService;

    @Value("${backtracking.task.pause-time-interval:5}")
    private Integer time;
    @Resource(name = "remoteCacheClient")
    private CacheClient cacheClient;

    public static final String END_TIME_START_TIME = "end_time - start_time";

    private static final String VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT = "var_process_batch_backtracking_task_result";

    @Override
    public ProcessResult process(TaskContext taskContext) {
        //1.检查需要更新为暂停状态的任务，并更新
        inspectPaused();

        //2.查询所有执行中且完成率不为空的任务（且完成率不为空说明任务初始化完成）
        List<VarProcessBatchBacktrackingTask> inProgressTaskList = backtrackingTaskService.list(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                .eq(VarProcessBatchBacktrackingTask::getStatus, BacktrackingTaskStatusEnum.IN_PROGRESS)
                .isNotNull(VarProcessBatchBacktrackingTask::getCompletion));
        //3.异步更新线程状态
        for (VarProcessBatchBacktrackingTask inProgressTask : inProgressTaskList) {
            CompletableFuture.runAsync(() -> call(inProgressTask));
        }
        return new ProcessResult(true);
    }

    private void call(VarProcessBatchBacktrackingTask inProgressTask) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getOutputType, VarProcessBatchBacktracking::getTriggerType, VarProcessBatchBacktracking::getTaskInfo, VarProcessBatchBacktracking::getOutputInfo)
                .eq(VarProcessBatchBacktracking::getId, inProgressTask.getBacktrackingId()));

        //更新任务执行信息
        updateTaskExecuteInfo(inProgressTask, "FILE".equals(backtracking.getOutputType()) && !StringUtils.isEmpty(backtracking.getOutputInfo()));

        //上传结果文件
        if (inProgressTask.getStatus() == BacktrackingTaskStatusEnum.FILE_GENERATING) {
            backtrackingUploadResultFileService.uploadResultFile(backtracking, inProgressTask);
        }

        //定时任务任务失败，进行重试
        if (inProgressTask.getStatus() == BacktrackingTaskStatusEnum.FAIL && backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
            cacheClient.evict(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + inProgressTask.getId());
            retry(backtracking, inProgressTask);
            return;
        }

        //任务执行完毕，删除执行过程中产生的一些key
        if (inProgressTask.getStatus() != BacktrackingTaskStatusEnum.IN_PROGRESS) {
            cacheClient.evict(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + inProgressTask.getId());
        }
    }


    private void retry(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask inProgressTask) {
        //定时任务才可以发送重试事件
        BacktrackingSaveInputVO.TaskInfo taskInfoDto = JSON.parseObject(backtracking.getTaskInfo(), BacktrackingSaveInputVO.TaskInfo.class);
        if (taskInfoDto.getIsRetry() != null && taskInfoDto.getIsRetry().equals(1)) {
            BacktrackingTaskParam taskParam = BacktrackingTaskParam.builder().backtrackingId(inProgressTask.getBacktrackingId()).taskId(inProgressTask.getId()).build();
            log.info("定时任务失败重试，入参-》{}", taskParam);
            EventPublisherUtil.publishEvent(new BacktrackingTaskFailEvent(taskParam));
        }
    }

    private void updateTaskExecuteInfo(VarProcessBatchBacktrackingTask inProgressTask, boolean isGeneratingResultFile) {
        HashMap<String, Object> filter = new HashMap<>(MagicNumbers.EIGHT);
        filter.put("task_id", inProgressTask.getId());
        long total = backtrackingResultDatabaseService.count(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, filter);
        //完成条数
        long completeTotal = backtrackingResultDatabaseService.getTaskCompleteTotal(inProgressTask.getId());
        if (total == 0) {
            inProgressTask.setStatus(BacktrackingTaskStatusEnum.FAIL);
            inProgressTask.setErrorMessage("获取外部传入参数为空");
            inProgressTask.setEndTime(new Date());
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("0.00%");
            //任务未执行完只更新完成率
            if (completeTotal < total) {
                String completion = decimalFormat.format((completeTotal * 1.0) / total);
                if (!StringUtils.isEmpty(inProgressTask.getCompletion()) && Objects.equals(completion, inProgressTask.getCompletion())) {
                    return;
                } else {
                    inProgressTask.setCompletion(completion);
                }
            } else {
                filter.put("status", BacktrackingTaskResultStatusEnum.SUCCESS.name());
                long successTotal = backtrackingResultDatabaseService.count(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, filter);
                if (successTotal == 0) {
                    inProgressTask.setCompletion(decimalFormat.format((completeTotal * 1.0) / total));
                    inProgressTask.setErrorMessage("最近执行的任务成功率为0%");
                    inProgressTask.setSuccess("0%");
                    inProgressTask.setStatus(BacktrackingTaskStatusEnum.FAIL);
                    inProgressTask.setEndTime(new Date());
                } else {
                    filter.remove("status");
                    long maxResponseTime = backtrackingResultDatabaseService.max(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, END_TIME_START_TIME, filter);
                    long minResponseTime = backtrackingResultDatabaseService.min(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, END_TIME_START_TIME, filter);
                    float avgResponseTime = backtrackingResultDatabaseService.avg(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, END_TIME_START_TIME, filter);
                    inProgressTask.setMaximumResponseTime((int) maxResponseTime);
                    inProgressTask.setMinimumResponseTime((int) minResponseTime);
                    inProgressTask.setAverageResponseTime(avgResponseTime);
                    inProgressTask.setCompletion(decimalFormat.format((completeTotal * 1.0) / total));
                    inProgressTask.setSuccess(decimalFormat.format((successTotal * 1.0) / total));
                    if (isGeneratingResultFile) {
                        inProgressTask.setStatus(BacktrackingTaskStatusEnum.FILE_GENERATING);
                    } else {
                        inProgressTask.setStatus(BacktrackingTaskStatusEnum.SUCCESS);
                    }
                    inProgressTask.setEndTime(new Date());
                }
            }
            backtrackingTaskService.updateById(inProgressTask);
        }
    }


    private void inspectPaused() {
        //在该时间之前的任务需要更新为失败
        Date afterUpdateTime = Date.from(LocalDateTime.now().minusMinutes(time).atZone(ZoneId.systemDefault()).toInstant());
        //查执行中并且更新时间在afterUpdateTime之前的任务
        List<Long> taskIdList = backtrackingTaskService.list(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                        .eq(VarProcessBatchBacktrackingTask::getStatus, BacktrackingTaskStatusEnum.IN_PROGRESS)
                        .lt(VarProcessBatchBacktrackingTask::getUpdatedTime, afterUpdateTime))
                .stream()
                .map(VarProcessBatchBacktrackingTask::getId)
                //消息未发送完成的才会修改为暂停
                .filter(item -> !cacheClient.exists(CacheKeyPrefixConstant.BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE + item)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(taskIdList)) {
            //状态更新
            log.info("更新批量回溯任务状态，任务id->{}", taskIdList);
            backtrackingTaskService.updateStateByIds(taskIdList, BacktrackingTaskStatusEnum.PAUSED);
        }
    }

}
