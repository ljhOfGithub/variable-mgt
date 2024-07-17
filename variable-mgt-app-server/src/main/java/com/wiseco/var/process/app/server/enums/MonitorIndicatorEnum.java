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
package com.wiseco.var.process.app.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 监控指标的枚举
 */

@Getter
@AllArgsConstructor
public enum MonitorIndicatorEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li>CALL_VOLUME: 调用量(服务报表时选择)</li>
     *     <li>FAILURE_RATE: 失败率(服务报表时选择)</li>
     *     <li>MAX_RESPONSE_TIME: 最大响应时间(服务报表时选择)</li>
     *     <li>AVG_RESPONSE_TIME: 平均响应时间(服务报表时选择)</li>
     *     <li>RESPONSE_CODE_RATIO: 响应码占比(服务报表时选择)</li>
     *     <li>MISSING_RATIO: 缺失率(指标报表时选择)</li>
     *     <li>SPECIAL_RATIO: 特殊值占比(指标报表时选择)</li>
     *     <li>PSI: psi(指标报表时选择)</li>
     *     <li>IV: iv(指标报表时选择)</li>
     * </ul>
     */

    CALL_VOLUME("调用量"),

    FAILURE_RATE("失败率"),

    MAX_RESPONSE_TIME("最大响应时间"),

    AVG_RESPONSE_TIME("平均响应时间"),

    RESPONSE_CODE_RATIO("响应码占比"),

    MISSING_RATIO("缺失率"),

    SPECIAL_RATIO("特殊值占比"),

    PSI("psi"),

    IV("iv");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 监控指标的枚举
     * @return 监控指标的枚举
     */
    public MonitorIndicatorEnum get(MonitorIndicatorEnum input) {
        for (MonitorIndicatorEnum indicatorEnum : MonitorIndicatorEnum.values()) {
            if (indicatorEnum.equals(input)) {
                return indicatorEnum;
            }
        }
        return null;
    }
}
