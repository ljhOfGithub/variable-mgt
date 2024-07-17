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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CronEveryUnitBaseConfig类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CronEveryUnitBaseConfig {

    /**
     * 执行频率类型
     */
    private ExecuteFrequencyType executeFrequencyType = ExecuteFrequencyType.EVERY;

    /**
     * executeFrequencyType为SCOPE时生效
     * 执行范围
     */
    private Scope scope;
    /**
     * executeFrequencyType为FIXED_INTERVAL时生效
     * 固定间隔
     */
    private FixedInterval fixedInterval;

    /**
     * executeFrequencyType为TARGET时生效
     * 指定的执行点
     */
    private int[] target;
}
