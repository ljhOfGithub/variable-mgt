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
package com.wiseco.var.process.app.server.commons.enums;

import com.wiseco.decision.common.enums.BaseEnum;
import lombok.Getter;

/**
 * @author mingao
 * @since 2023/08/09
 */

/**
 * 比较操作枚举
 */
public enum ComparisonOperatorEnum implements BaseEnum {
    /**
     * 等于
     */
    EQUALS("=", "等于"),
    /**
     * 不等于
     */
    NOT_EQUALS("<>", "不等于"),
    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUALS(">=", "大于等于"),
    /**
     * 大于
     */
    GREATER_THAN(">", "大于"),
    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUALS("<=", "小于等于"),
    /**
     * 小于
     */
    LESS_THAN("<", "小于"),
    /**
     * 字符串包含
     */
    CONTAINS(" LIKE ", "包含"),
    /**
     * 字符串不包含
     */
    NOT_CONTAINS(" NOT LIKE ", "不包含"),
    /**
     * 字符串以..开始
     */
    START_WITH(" LIKE ", "开始包含"),
    /**
     * 字符串以..结尾
     */
    END_WITH(" LIKE ", "结尾包含");

    @Getter
    String code;
    @Getter
    String desc;

    ComparisonOperatorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
