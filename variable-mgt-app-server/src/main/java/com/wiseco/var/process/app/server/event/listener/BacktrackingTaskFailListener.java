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
package com.wiseco.var.process.app.server.event.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.enums.TimeUnit;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.commons.util.cron.ScheduleJobCronUtils;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.event.obj.BacktrackingTaskFailEvent;
import com.wiseco.var.process.app.server.job.BaseJobConfig;
import com.wiseco.var.process.app.server.job.BaseJobInfoFactory;
import com.wiseco.var.process.app.server.job.backtracking.BacktrackingProcessor;
import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskService;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;


/**
 * 批量回溯任务失败监听器
 * @author wuweikang
 */
@Component
@Slf4j
public class BacktrackingTaskFailListener implements ApplicationListener<BacktrackingTaskFailEvent> {
    @Autowired
    private WisecoJobOperateUtil       jobOperateUtil;
    @Resource
    private BacktrackingService backtrackingService;
    @Autowired
    private CacheClient cacheClient;
    @Resource
    private BacktrackingTaskService backtrackingTaskService;

    @Async
    @Override
    public void onApplicationEvent(@NotNull BacktrackingTaskFailEvent event) {
        log.info("监听到批量回溯任务执行失败，event参数:{},", event.getParam());
        BacktrackingTaskParam backtrackingTaskParam = event.getData();
        Long backtrackingId = backtrackingTaskParam.getBacktrackingId();
        try {
            VarProcessBatchBacktracking batchBacktracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                    .select(VarProcessBatchBacktracking::getTaskInfo)
                    .eq(VarProcessBatchBacktracking::getId, backtrackingId));
            BacktrackingSaveInputVO.TaskInfo taskInfoDto = JSON.parseObject(batchBacktracking.getTaskInfo(), BacktrackingSaveInputVO.TaskInfo.class);
            createOrUpdateRetryJob(backtrackingTaskParam, taskInfoDto.getRetryCount(), taskInfoDto.getRetryInterval(), taskInfoDto.getRetryIntervalUnit());
        } catch (Exception e) {
            log.error("event:{},监听到批量回溯任务执行失败，处理失败", JSON.toJSONString(event), e);
        }
    }

    /**
     * 创建或修改重试任务
     *
     * @param backtrackingTaskParam 事件数据
     * @param maxRetryCount 重试次数
     * @param retryInterval 重试间隔
     * @param timeUnit      时间单位
     */
    private void createOrUpdateRetryJob(BacktrackingTaskParam backtrackingTaskParam, int maxRetryCount, int retryInterval, TimeUnit timeUnit) {
        BaseJobConfig jobConfig = BaseJobInfoFactory.getBaseJobConfig(BacktrackingProcessor.class);
        String jobName = MessageFormat.format(jobConfig.getJobName(),
                String.valueOf(backtrackingTaskParam.getBacktrackingId()),String.valueOf(backtrackingTaskParam.getTaskId()));

        //redisKey,用于记录任务重试的次数
        String retryNumCacheKey = CacheKeyPrefixConstant.BACKTRACKING_TASK_RETRY_NUM + backtrackingTaskParam.getTaskId();
        Object retryNumValue = cacheClient.get(retryNumCacheKey);
        if (retryNumValue == null) {
            cacheClient.put(retryNumCacheKey, 1);
        } else {
            int num = Integer.parseInt(retryNumValue.toString());
            cacheClient.put(retryNumCacheKey,++num);
            //重试次数已达到上限,删除重试任务
            if (num > maxRetryCount) {
                log.info("historyId{},批量回溯任务执行重试次数已达到上限，重试结束，删除重试任务", backtrackingTaskParam.getTaskId());
                jobOperateUtil.deleteJob(jobName);
                cacheClient.evict(retryNumCacheKey);
                //更新数据集导入状态为最终失败
                VarProcessBatchBacktrackingTask taskDto = backtrackingTaskService.getById(backtrackingTaskParam.getTaskId());
                taskDto.setStatus(BacktrackingTaskStatusEnum.FAIL);
                backtrackingTaskService.updateById(taskDto);
                return;
            }
        }

        //失败后重试根据任务设置的重试间隔开启一个新的定时任务，由该定时任务执行重试
        //定时任务在重试时有可能还会再次失败，失败事件也将重复被监听，所以这里也需要判断执行重试的定时任务是否已经存在，若已存在就跳过
        String cron = ScheduleJobCronUtils.generateCronBaseNow(retryInterval, timeUnit);
        if (jobOperateUtil.findJob(jobName) != null) {
            //这种重试任务的cron表达式都是执行一次的，即当前获取到的任务状态其实是已经禁用的，直接更新任务cron
            jobOperateUtil.updateBasicCronJob(jobName, cron);
            log.info("historyId:{},更新导数重试定时任务cron:{}", backtrackingTaskParam.getTaskId(), cron);
        } else {
            log.info("historyId:{},创建导数重试定时任务corn表达式为:{}", backtrackingTaskParam.getTaskId(), cron);
            jobOperateUtil.addBasicCronJob(jobName, BacktrackingProcessor.class, cron, "批量回溯任务执行重试", backtrackingTaskParam, null, null);
            log.info("historyId:{},开启重试定时任务，处理结束", backtrackingTaskParam.getTaskId());
        }
    }
}
