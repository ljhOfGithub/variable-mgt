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
 * 监控指标枚举
 *
 * @author wuweikang
 */
@AllArgsConstructor
@Getter
public enum MonitoringConfTargetEnum {
    /**
     * 调用量
     */
    CALL_VOLUME("调用量"),
    /**
     * 失败率
     */
    FAILURE_RATE("失败率"),
    /**
     * 最大响应时间
     */
    MAX_RESPONSE_TIME("最大响应时间"),
    /**
     * 平均响应时间
     */
    AVG_RESPONSE_TIME("平均响应时间"),
    /**
     * 响应码占比
     */
    RESPONSE_CODE_RATIO("响应码占比"),
    /**
     * 特殊值占比
     */
    SPECIAL_RATIO("特殊值占比"),
    /**
     * 缺失率
     */
    MISSING_RATIO("缺失率"),
    /**
     * PSI
     */
    PSI("PSI"),
    /**
     * IV
     */
    IV("IV");

    /**
     * 描述
     */
    private final String desc;
}
