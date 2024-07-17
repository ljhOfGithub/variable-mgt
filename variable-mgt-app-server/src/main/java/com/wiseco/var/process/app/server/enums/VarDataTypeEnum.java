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

@AllArgsConstructor
@Getter
public enum VarDataTypeEnum {
    /**
     * 数据类型
     */
    STRING("string"),
    /**
     * 数据类型
     */
    INTEGER("int"),
    /**
     * 数据类型
     */
    DOUBLE("double"),

    /**
     * 数据类型
     */
    DATE("date"),
    /**
     * 数据类型
     */
    DATETIME("datetime"),

    /**
     * 数据类型
     */
    BOOLEAN("boolean");

    private String desc;

    /**
     * 获取枚举类型
     * @param criterion  desc
     * @return 枚举
     */
    public static VarDataTypeEnum getEnumFromDesc(String criterion) {
        for (VarDataTypeEnum item : VarDataTypeEnum.values()) {
            if (item.getDesc().equals(criterion)) {
                return item;
            }
        }
        return null;
    }
}
