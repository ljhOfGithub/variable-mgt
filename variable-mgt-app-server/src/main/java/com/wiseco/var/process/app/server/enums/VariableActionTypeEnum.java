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

import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量操作类型
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum VariableActionTypeEnum {
    /**
     * 变量操作类型
     */

    ADD(1, "新建", VariableStatusEnum.EDIT),

    SUMMIT(2, "提交", VariableStatusEnum.UNAPPROVED),

    DOWN(3, "停用", VariableStatusEnum.DOWN),

    RE_ENABLE(4, "启用", VariableStatusEnum.UP),

    APPROVED(6, "审核通过", VariableStatusEnum.UP),

    REFUSE(7, "审核拒绝", VariableStatusEnum.REFUSE),

    RETURN_EDIT(8, "退回编辑", VariableStatusEnum.EDIT),

    DELETE(9, "删除", null);

    private Integer code;
    private String desc;
    private VariableStatusEnum statusEnum;

    /**
     * 根据code获取对应枚举
     *
     * @param code 枚举code
     * @return VariableActionTypeEnum
     */
    public static VariableActionTypeEnum getStatus(Integer code) {
        for (VariableActionTypeEnum statusEnum : VariableActionTypeEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "非法操作类型");
    }

}
