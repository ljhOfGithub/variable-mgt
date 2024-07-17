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

@Getter
@AllArgsConstructor
public enum VarProcessServiceActionEnum {
    /**
     * 变量发布服务操作枚举
     */

    CREATE(0, "新增", VarProcessServiceStateEnum.EDITING, SysDynamicOperateTypeEnum.CREATE),

    SUBMIT_REVIEW(1, "提交", VarProcessServiceStateEnum.PENDING_REVIEW, SysDynamicOperateTypeEnum.VAR_APPLY_UP),

    APPROVE(2, "审核通过", VarProcessServiceStateEnum.ENABLED, SysDynamicOperateTypeEnum.APPROVED),

    REJECT(3, "审核拒绝", VarProcessServiceStateEnum.REJECTED, SysDynamicOperateTypeEnum.REFUSE),

    BACK_EDIT(4, "退回编辑", VarProcessServiceStateEnum.EDITING, SysDynamicOperateTypeEnum.RETURN_EDIT),

    DISABLE(5, "停用", VarProcessServiceStateEnum.DISABLED, SysDynamicOperateTypeEnum.STOP),

    RENABLE(6, "启用", VarProcessServiceStateEnum.ENABLED, SysDynamicOperateTypeEnum.ENABLE),

    DELETE(7, "删除", null, SysDynamicOperateTypeEnum.DELETE),

    EDIT(8, "编辑", VarProcessServiceStateEnum.EDITING, SysDynamicOperateTypeEnum.EDIT);

    private Integer code;
    private String desc;
    /**
     * 操作执行后的状态
     */
    private VarProcessServiceStateEnum susequentState;
    /**
     * 保存系统动态时的操作类型
     */
    private SysDynamicOperateTypeEnum sysoption;

    /**
     * 根据枚举code获取枚举
     *
     * @param code 枚举code
     * @return VarProcessServiceActionEnum
     */
    public static VarProcessServiceActionEnum getAction(Integer code) {
        for (VarProcessServiceActionEnum action : VarProcessServiceActionEnum.values()) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }
        return null;
    }

}
