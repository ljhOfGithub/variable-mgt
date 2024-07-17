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
 * 实时服务的状态枚举
 */

@Getter
@AllArgsConstructor
public enum VarProcessServiceStateEnum {

    /**
     * 状态(Short类型)
     * <ul>
     *     <li>EDITING: 编辑中</li>
     *     <li>PENDING_REVIEW: 待审核</li>
     *     <li>ENABLED: 启用</li>
     *     <li>DISABLED: 停用</li>
     *     <li>REJECTED: 审核拒绝</li>
     * </ul>
     */

    EDITING("编辑中"),

    PENDING_REVIEW("待审核"),

    ENABLED("启用"),

    DISABLED("停用"),

    REJECTED("审核拒绝");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 实时服务的状态枚举
     * @return 实时服务的状态枚举
     */
    public VarProcessServiceStateEnum get(VarProcessServiceStateEnum input) {
        for (VarProcessServiceStateEnum stateEnum : VarProcessServiceStateEnum.values()) {
            if (stateEnum.equals(input)) {
                return stateEnum;
            }
        }
        return null;
    }

}
