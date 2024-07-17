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
public enum DataTypeEnum {
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
    OBJECT("object"),

    /**
     * 数据类型
     */
    VOID("void"),

    /**
     * 数据类型
     */
    BOOLEAN("boolean");

    private String desc;

    /**
     * 根据desc获取枚举
     *
     * @param desc 枚举描述
     * @return DataTypeEnum
     */
    public static DataTypeEnum getEnum(String desc) {
        for (DataTypeEnum dataTypeEnum : DataTypeEnum.values()) {
            if (dataTypeEnum.getDesc().equalsIgnoreCase(desc)) {
                return dataTypeEnum;
            }
        }
        return null;
    }
}
