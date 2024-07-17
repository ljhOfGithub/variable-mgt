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
 * CronWeekConfig类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CronWeekConfig extends CronEveryUnitBaseConfig {

    /**
     * executeFrequencyType为LAST时生效
     * 本月最后一个星期X
     */
    private int lastWeekDayNum;

    /**
     * executeFrequencyType为WEEK_TARGET时生效
     * 本月第X个星期X
     */
    private WeekTarget weekTarget;

    /**
     * getDefault
     *
     * @return com.wiseco.var.process.app.server.commons.util.cron.CronWeekConfig
     */
    public CronWeekConfig getDefault() {
        this.setExecuteFrequencyType(ExecuteFrequencyType.NONE);
        return this;
    }
}
