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
package com.wiseco.var.process.app.server.job.statistics;

import com.wiseco.var.process.app.server.service.statistics.StatisticsOverallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

/**
 * 统计分析定时任务执行
 */

@Component
@Slf4j
public class StatisticsProcessor implements BasicProcessor {

    @Autowired
    private StatisticsOverallService statisticsOverallService;

    /**
     * 统计分析的回调函数
     * @param taskContext 定时任务的上下文环境
     * @return 定时任务结果对象
     */
    @Override
    public ProcessResult process(TaskContext taskContext) {
        // 1.确定好传入的参数
        log.info("统计分析定时任务开始执行...");
        // 2.在线打印日志，可以直接在控制台查看日志
        OmsLogger omsLogger = taskContext.getOmsLogger();
        omsLogger.info("StatisticsTask start to process, current JobParam is {}.", taskContext.getJobParams());
        try {
            // 3.执行业务逻辑
            statisticsOverallService.runTask();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            omsLogger.error(e.getMessage(), e);
            return new ProcessResult(false, "StatisticsTask executes failed, please check log.");
        }
        omsLogger.info("StatisticsTask completes processing, current JobParam is {}.", taskContext.getJobParams());
        log.info("统计分析定时任务执行结束...");
        return new ProcessResult(true, "Success execute StatisticsTask.");
    }
}
