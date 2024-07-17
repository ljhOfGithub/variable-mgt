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
 * 监控预警规则 时间维度时间单位枚举
 *
 * @author wuweikang
 */
@AllArgsConstructor
@Getter
public enum MonitoringConfTimeUnitEnum {
    /**
     * 分钟
     */
    MINUTE("分钟", 1),
    /**
     * 小时
     */
    HOUR("小时", 60),
    /**
     * 天
     */
    DAY("天", 60 * 24),
    /**
     * 周
     */
    WEEK("周", 60 * 24 * 7),
    /**
     * 月
     */
    MONTH("月", 60 * 24 * 30);

    /**
     * 详情
     */
    private final String desc;

    /**
     * 分
     */
    private final Integer minute;
}
