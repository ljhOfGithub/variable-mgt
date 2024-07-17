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

public enum VarProcessManifestStateEnum {

    /**
     * 状态
     * <ul>
     *     <li>0: 编辑中</li>
     *     <li>1: 测试中</li>
     *     <li>2: 待审核</li>
     *     <li>3: 审核拒绝</li>
     *     <li>4: 启用中</li>
     *     <li>5: 启用</li>
     *     <li>6: 停用</li>
     *     <li>7: 启用失败</li>
     *     <li>8: 待上线</li>
     *     <li>9: 待审批</li>
     *     <li>10: 审批拒绝</li>
     * </ul>
     */
    EDIT(0, "编辑中"), TESTING(1, "测试中"),

    UNAPPROVED(2, "待审核"), REFUSE(3, "审核拒绝"),

    ENABLING(4, "启用中"), UP(5, "启用"), DOWN(6, "停用"), FAILED(7, "启用失败"), WAIN_ONLINE(8, "待上线"), WAIN_APPROVAL(9, "待审批"), APPROVAL_REJECTED(10, "审批拒绝"),

    DELETED(11, "已删除");

    private final Integer code;
    private final String desc;

    VarProcessManifestStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举code获取枚举类
     *
     * @param code 枚举code
     * @return VarProcessManifestStateEnum
     */
    public static VarProcessManifestStateEnum getStateEnum(Integer code) {
        for (VarProcessManifestStateEnum stateEnum : VarProcessManifestStateEnum.values()) {
            if (stateEnum.getCode().equals(code)) {
                return stateEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
