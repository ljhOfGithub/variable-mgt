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
 * 变量清单操作枚举类
 *
 * @author wangxianli
 * @since 2022/08/17
 */
@Getter
@AllArgsConstructor
public enum VarProcessManifestActionTypeEnum {

    /**
     * 状态
     * <ul>
     *     <li>0: 新建</li>
     *     <li>1: 提交审核</li>
     *     <li>2: 审核通过</li>
     *     <li>3: 审核拒绝</li>
     *     <li>4: 退回编辑</li>
     *     <li>5: 停用</li>
     *     <li>6: 重新启用</li>
     *     <li>7: 删除</li>
     *     <li>8: 导入</li>
     *     <li>9: 申请上线</li>
     *     <li>10: 审批拒绝</li>
     *     <li>11: 审批通过</li>
     * </ul>
     */
    CREATE(0, VarProcessManifestStateEnum.EDIT, "新建"),

    SUBMIT(1, VarProcessManifestStateEnum.UNAPPROVED, "提交"),
    //APPLY_FOR_PUBLISH(2, PENDING_REVIEW, "提交审核"),

    APPROVE(2, VarProcessManifestStateEnum.UP, "审核通过"),

    REJECT(3, VarProcessManifestStateEnum.REFUSE, "审核拒绝"),

    STEP_BACK(4, VarProcessManifestStateEnum.EDIT, "退回编辑"),

    DISABLE(5, VarProcessManifestStateEnum.DOWN, "停用"),

    RE_ENABLE(6, VarProcessManifestStateEnum.UP, "启用"),

    DELETE(7, VarProcessManifestStateEnum.DELETED, "删除"),
    //
    PUBLISH_IMPORT(8, VarProcessManifestStateEnum.WAIN_ONLINE, "导入"),
    //
    APPRO_ONLINE(9, VarProcessManifestStateEnum.WAIN_APPROVAL, "申请上线"),;
    //
    //    APPRO_REJECT(10, APPROVAL_REJECTED, "审批拒绝"),
    //
    //    APPRO_PASS(11, ENABLING, "审批通过"),

    /**
     * 动作类型编码
     */
    private final Integer code;

    /**
     * 动作执行后状态
     */
    private final VarProcessManifestStateEnum changeStatus;

    /**
     * 动作描述
     */
    private final String actionDescription;

    /**
     * 根据枚举code获取枚举
     *
     * @param actionCode 枚举code
     * @return VarProcessManifestActionTypeEnum
     */
    public static VarProcessManifestActionTypeEnum getActionTypeEnum(Integer actionCode) {
        for (VarProcessManifestActionTypeEnum actionTypeEnum : VarProcessManifestActionTypeEnum.values()) {
            if (actionTypeEnum.getCode().equals(actionCode)) {
                return actionTypeEnum;
            }
        }
        return null;
    }
}
