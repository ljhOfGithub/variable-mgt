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
package com.wiseco.var.process.app.server.commons.util.cron;

import lombok.Builder;
import lombok.Data;

/**
 * 定时任务执行计划配置信息
 */
@Data
@Builder
public class ScheduleJobExecuteConfig {

    /**
     * cron表达式的秒配置
     */
    private CronEveryUnitBaseConfig secondConfig;
    /**
     * cron表达式的分配置
     */
    private CronEveryUnitBaseConfig minuteConfig;
    /**
     * cron表达式的时配置
     */
    private CronEveryUnitBaseConfig hourConfig;
    /**
     * cron表达式的日配置
     */
    private CronDayConfig dayConfig;

    /**
     * cron表达式的月配置
     */
    private CronEveryUnitBaseConfig monthConfig;

    /**
     * cron表达式的星期配置
     */
    private CronWeekConfig weekConfig;

    /**
     * cron表达式的年配置，非必须
     */
    private CronEveryUnitBaseConfig yearConfig;

}
