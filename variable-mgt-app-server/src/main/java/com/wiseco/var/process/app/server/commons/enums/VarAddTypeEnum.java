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

import lombok.Getter;

/**
 * @author Asker.J
 * @since 2022/11/4
 */

/**
 * 变量添加类型枚举
 */
public enum VarAddTypeEnum {
    /**
     * ONLINE
     */
    ONLINE("0"),
    /**
     * QUOTE_IN_CHARACTER
     */
    QUOTE_IN_CHARACTER("1");

    @Getter
    private String code;

    VarAddTypeEnum(String code) {
        this.code = code;
    }

    /**
     * getByEnumName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.VarAddTypeEnum
     */
    public static VarAddTypeEnum getByEnumName(String name) {
        for (VarAddTypeEnum dtds : VarAddTypeEnum.values()) {
            if (dtds.name().equals(name)) {
                return dtds;
            }
        }
        return null;
    }

    /**
     * getByCode
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.VarAddTypeEnum
     */
    public static VarAddTypeEnum getByCode(String name) {
        for (VarAddTypeEnum dtds : VarAddTypeEnum.values()) {
            if (dtds.getCode().equals(name)) {
                return dtds;
            }
        }
        return null;
    }
}
