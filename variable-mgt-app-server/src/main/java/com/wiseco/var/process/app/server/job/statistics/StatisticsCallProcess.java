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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.service.statistics.StatisticsCallVolumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统计分析定时统计任务
 *
 * @author wuweikang
 */
@Component
@Slf4j
public class StatisticsCallProcess implements BasicProcessor {
    @Resource
    private StatisticsCallVolumeService statisticsCallVolumeService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String startTime = now.withMinute(MagicNumbers.ZERO).withSecond(MagicNumbers.ZERO).format(formatter);
        String endTime = now.withMinute(MagicNumbers.INT_59).withSecond(MagicNumbers.INT_59).format(formatter);
        statisticsCallVolumeService.scheduleStatistics(startTime, endTime);
        return new ProcessResult(true);
    }
}
