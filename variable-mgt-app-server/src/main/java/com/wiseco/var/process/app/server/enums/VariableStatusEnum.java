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
 * 变量状态
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum VariableStatusEnum {
    /**
     * 变量状态类型
     */

    EDIT(1, "编辑中"),

    UP(2, "启用"),

    DOWN(3, "停用"),

    UNAPPROVED(4, "待审核"),

    REFUSE(5, "审核拒绝");

    private Integer code;
    private String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举code
     * @return VariableStatusEnum
     */
    public static VariableStatusEnum getStatus(Integer code) {
        for (VariableStatusEnum statusEnum : VariableStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 根据name获取枚举
     *
     * @param str 枚举name
     * @return ManifestStatusEnum
     */
    public static ManifestStatusEnum getStatustr(String str) {
        for (ManifestStatusEnum statusEnum : ManifestStatusEnum.values()) {
            if (statusEnum.name().equals(str)) {
                return statusEnum;
            }
        }
        return null;
    }

}
