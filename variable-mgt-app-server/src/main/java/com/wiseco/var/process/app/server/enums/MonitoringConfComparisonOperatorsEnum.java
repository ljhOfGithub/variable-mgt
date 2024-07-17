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
 * 监控规则规则触发条件比较符枚举
 *
 * @author wuweikang
 */
@AllArgsConstructor
@Getter
public enum MonitoringConfComparisonOperatorsEnum {
    /**
     * 大于号
     */
    GREATER_THAN(">"),
    /**
     *  大于等于号
     */
    GREATER_THAN_OR_EQUAL(">="),
    /**
     * 小于号
     */
    LESS_THAN("<"),
    /**
     * 小于等于号
     */
    LESS_THAN_OR_EQUAL("<="),
    /**
     * 等于
     */
    EQUAL("=="),
    /**
     * 不等于
     */
    NOT_EQUAL("!=");
    /**
     * 描述
     */
    private final String desc;
}
