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
package com.wiseco.var.process.app.server.service;

import cn.hutool.core.util.ObjectUtil;
import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.commons.util.cron.ScheduleJobCronUtils;
import com.wiseco.var.process.app.server.job.statistics.StatisticsProcessor;
import com.wiseco.var.process.app.server.service.dto.innerdata.TaskInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ServiceTaskBiz extends VariableServiceBiz {

    public static final int HOURS_PER_DAY = 24;
    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;


    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private WisecoJobOperateUtil jobOperateUtil;

    private static final String JOB_NAME_SERVICE_TIMER = "service_timer_job";

    private static final String JOB_NAME_STATISTICS = "statistics_job";

    /**
     * 添加定时任务
     * @param jobName 任务名称
     * @param clazz 执行类
     */
    private void saveTask(String jobName, Class clazz) {
        // 1.创建任务信息
        LocalDateTime now = LocalDateTime.now();
        int hour = DateTimeUtils.getTheHour(now).getHour();
        int minute = DateTimeUtils.getTheMinute(now).getMinute();
        TaskInfoDto taskInfoDto = TaskInfoDto.builder()
                .executionFrequency(JobExecuteFrequency.EVERY_DAY)
                .startTime(hour + ":" + minute)
                .startDate(DateTimeUtils.parse(now))
                .endDate(null)
                .build();
        // 3.生成cron表达式
        String cron = ScheduleJobCronUtils.generateCronByPageConfig(WisecoJobOperateUtil.formCron(taskInfoDto));
        Long startDate = DateTimeUtils.parseMillisecond(DateTimeUtils.parse(taskInfoDto.getStartDate()));
        Long endDate = Optional.ofNullable(taskInfoDto.getEndDate()).map(DateTimeUtils::parse).map(DateTimeUtils::parseMillisecond).orElse(null);
        jobOperateUtil.addBasicCronJob(jobName, clazz, cron, jobName, null, startDate, endDate);
    }

    /**
     * 项目开始后初始化任务
     */
    public void initTask() {

        // 实时服务统计分析
        if (ObjectUtil.isEmpty(jobOperateUtil.findJob(JOB_NAME_STATISTICS))) {
            saveTask(JOB_NAME_STATISTICS, StatisticsProcessor.class);
        }

    }
}
