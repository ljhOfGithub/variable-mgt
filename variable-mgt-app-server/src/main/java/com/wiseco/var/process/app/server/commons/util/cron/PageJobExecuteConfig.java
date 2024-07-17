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

import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 页面任务执行配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageJobExecuteConfig {

    /**
     * 任务执行频率
     */
    private JobExecuteFrequency executeFrequency;

    /**
     * 每月执行日
     */
    private int dayInMonth;
    /**
     * 每周执行日
     */
    private int dayInWeek;
    /**
     * 每季度执行计划
     */
    private List<QuarterExecPlan> quarterExecPlans;
    /**
     * 固定执行月 (X月)  executeFrequency为JobExecuteFrequency.TARGET生效
     */
    private int[] targetMonths;
    /**
     * 固定执行日 (X日) executeFrequency为JobExecuteFrequency.TARGET生效
     */
    private int[] targetDays;

    /**
     * 执行时间：小时
     */
    private int hourNum;

    /**
     * 执行时间：分钟
     */
    private int minuteNum;
}
