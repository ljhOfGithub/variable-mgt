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

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum StreamProcessFilterConditionCmpEnum {
    /**
     * 流式变量过滤条件比较符号
     */
    IS_NULL("为空"),
    IS_NOT_NULL("不为空"),
    EQUAL("等于"),
    NOT_EQUAL("不等于"),
    GREATER_THAN("大于"),
    GREATER_THAN_OR_EQUAL("大于等于"),
    LESS_THAN("小于"),
    LESS_THAN_OR_EQUAL("小于等于"),
    CONTAIN("包含"),
    NOT_CONTAIN("不包含"),
    START_WITH("开始于"),
    END_WITH("结束于");
    /**
     * 描述
     */
    private final String desc;

    public static List<StreamProcessFilterConditionCmpEnum> getComparisonsForString() {
        return Arrays.asList(EQUAL, NOT_EQUAL, CONTAIN, NOT_CONTAIN, START_WITH, END_WITH, IS_NULL, IS_NOT_NULL);
    }

    public static List<StreamProcessFilterConditionCmpEnum> getComparisonsForNumber() {
        return Arrays.asList(EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, IS_NULL, IS_NOT_NULL);
    }

    public static List<StreamProcessFilterConditionCmpEnum> getComparisonsForDate() {
        return Arrays.asList(EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, IS_NULL, IS_NOT_NULL);
    }
}
