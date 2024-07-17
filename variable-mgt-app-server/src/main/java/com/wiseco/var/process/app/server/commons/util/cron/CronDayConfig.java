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

import com.wiseco.var.process.app.server.commons.enums.ExecuteFrequencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * CronDay的配置类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CronDayConfig extends CronEveryUnitBaseConfig {

    /**
     * executeFrequencyType为RECENT_WORK_DAY时生效
     * 最近的工作日参考日
     */
    private int recentWorkDayReferenceDay;

    /**
     * setExecuteDay
     *
     * @param dayInMonth 某个月的具体的一天
     * @return CronDayConfig对象
     */
    public CronDayConfig setExecuteDay(int dayInMonth) {
        this.setExecuteFrequencyType(ExecuteFrequencyType.TARGET);
        this.setTarget(new int[]{dayInMonth});
        return this;
    }

    /**
     * setExecuteDay
     *
     * @param targetDays 目标天
     * @return com.wiseco.var.process.app.server.commons.util.cron.CronDayConfig
     */
    public CronDayConfig setExecuteDay(int[] targetDays) {
        this.setExecuteFrequencyType(ExecuteFrequencyType.TARGET);
        this.setTarget(targetDays);
        return this;
    }
}
