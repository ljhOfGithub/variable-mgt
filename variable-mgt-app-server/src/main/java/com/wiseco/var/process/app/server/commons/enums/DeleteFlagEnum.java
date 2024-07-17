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

/**
 * 删除标识枚举类
 */
public enum DeleteFlagEnum {
    /**
     * 可用
     */
    USABLE(1, "可用"),
    /**
     * 已删除
     */
    DELETED(0, "已删除");

    private Integer code;
    private String desc;

    /**
     * DeleteFlagEnum
     *
     * @param code
     * @param desc
     * @return
     */
    DeleteFlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * getTypeEnum
     * @param status 状态
     * @return com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum
     */
    public static DeleteFlagEnum getTypeEnum(Integer status) {
        DeleteFlagEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            DeleteFlagEnum deleteFlagEnum = var1[var3];
            if (deleteFlagEnum.getCode().equals(status)) {
                return deleteFlagEnum;
            }
        }

        return null;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
