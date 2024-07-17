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
import org.springframework.util.StringUtils;

/**
 * @author Asker.J
 * @since 2022/12/9
 */

/**
 * 变量类型枚举
 */
public enum VarTypeEnum {
    /**
     * string
     */
    STRING("string"),
    /**
     * int
     */
    INT("int"),
    /**
     * double
     */
    DOUBLE("double"),
    /**
     * date
     */
    DATE("date"),
    /**
     * datetime
     */
    DATETIME("datetime"),
    /**
     * boolean
     */
    BOOL("boolean"),
    /**
     * boolean
     */
    BOOLEAN("boolean");

    @Getter
    private String code;

    VarTypeEnum(String code) {
        this.code = code;
    }

    /**
     * getByEnumName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.VarTypeEnum
     */
    public static VarTypeEnum getByEnumName(String name) {
        if (StringUtils.isEmpty(name)) {
            return STRING;
        }
        for (VarTypeEnum en : VarTypeEnum.values()) {
            if (en.name().equalsIgnoreCase(name)) {
                return en;
            }
        }
        return STRING;
    }

}
