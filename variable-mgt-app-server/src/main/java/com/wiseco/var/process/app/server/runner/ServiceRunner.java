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
package com.wiseco.var.process.app.server.runner;

import com.wiseco.boot.job.config.ConditionalOnWisecoBootJobEnabled;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.job.backtracking.BacktrackingTaskInspectProcessor;
import com.wiseco.var.process.app.server.job.statistics.StatisticsCallOfEveryHourProcess;
import com.wiseco.var.process.app.server.job.statistics.StatisticsCallProcess;
import com.wiseco.var.process.app.server.service.ServiceTaskBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tech.powerjob.common.response.JobInfoDTO;

import javax.annotation.Resource;

/**
 * 在SpringBoot项目启动完毕后，执行这里面的回调函数
 */

@ConditionalOnWisecoBootJobEnabled
@Component
@Slf4j
public class ServiceRunner implements ApplicationRunner {

    @Autowired
    private ServiceTaskBiz serviceTask;

    @Resource
    private WisecoJobOperateUtil jobOperateUtil;

    private static final String BACKTRACKING_TASK_INSPECT = "backtracking_task_inspect";

    private static final String STATISTICS_SERVICE_CALL_EVERY_HOUR = "statistics_service_call_every_hour";

    private static final String STATISTICS_SERVICE_CALL = "statistics_service_call";

    @Value("${statistics.time-interval:10}")
    private int statisticsTimeInterval;

    @Value("${backtracking.task.update-time-interval:5}")
    private int updateTimeInterval;

    @Override
    public void run(ApplicationArguments args) {
        try {
            serviceTask.initTask();
            addBacktrackingTaskInspectJob();
            addStatisticsCallJobOfVeryHourCall();
            saveStatisticsCallJob();
        } catch (Exception e) {
            log.error("初始化任务失败，请检查");
        }
    }

    /**
     * 创建批量回溯task检查任务
     */
    public void addBacktrackingTaskInspectJob() {
        //每10分钟检查一次
        String corn = "*/" + updateTimeInterval + " * * * * ?";
        JobInfoDTO job = jobOperateUtil.findJob(BACKTRACKING_TASK_INSPECT);
        if (job == null) {
            jobOperateUtil.addBasicCronJob(BACKTRACKING_TASK_INSPECT, BacktrackingTaskInspectProcessor.class, corn, "批量回溯task后处理任务", null, null, null);
        } else {
            jobOperateUtil.updateBasicCronJob(BACKTRACKING_TASK_INSPECT, BacktrackingTaskInspectProcessor.class, corn, "批量回溯task后处理任务", null, null, null);
        }
    }

    /**
     * 统计分析——每小时定时统计任务
     */
    public void addStatisticsCallJobOfVeryHourCall() {
        //延迟十秒 防止定时任务提前
        String corn = "10 0 */1 * * ?";
        JobInfoDTO job = jobOperateUtil.findJob(STATISTICS_SERVICE_CALL_EVERY_HOUR);
        if (job == null) {
            jobOperateUtil.addBasicCronJob(STATISTICS_SERVICE_CALL_EVERY_HOUR, StatisticsCallOfEveryHourProcess.class, corn, "统计分析——每小时定时统计任务", null, null, null);
        } else {
            jobOperateUtil.updateBasicCronJob(STATISTICS_SERVICE_CALL_EVERY_HOUR, StatisticsCallOfEveryHourProcess.class, corn, "统计分析——每小时定时统计任务", null, null, null);
        }
    }

    /**
     * 统计分析——每小时定时统计任务
     */
    public void saveStatisticsCallJob() {
        if (statisticsTimeInterval < MagicNumbers.ONE || statisticsTimeInterval > MagicNumbers.INT_59) {
            return;
        }
        //延迟十秒 防止定时任务提前
        String corn = "10 */" + statisticsTimeInterval + " * * * ?";
        JobInfoDTO job = jobOperateUtil.findJob(STATISTICS_SERVICE_CALL);
        if (job == null) {
            jobOperateUtil.addBasicCronJob(STATISTICS_SERVICE_CALL, StatisticsCallProcess.class, corn, "统计分析定时统计任务", null, null, null);
        }
    }
}
