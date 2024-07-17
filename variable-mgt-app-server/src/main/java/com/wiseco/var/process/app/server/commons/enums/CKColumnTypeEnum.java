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
 * @since 2022/4/20
 */
public enum CKColumnTypeEnum {
    /**
     * [-128 : 127] 可以用于布尔类型标识0，1
     */
    TINYINT("Int8"),
    /**
     * [-32768 : 32767]
     */
    SMALLINT("Int16"),
    /**
     * [-2147483648 : 2147483647]
     */
    INT("Int32"),
    /**
     * [-9223372036854775808 : 9223372036854775807]
     */
    BIGINT("Int64"), FLOAT("Float32"), DOUBLE("Float64"), STRING("String"),
    /**
     * 2100-01-01
     */
    DATE("Date32"),
    /**
     * 2019-01-01 03:00:00.000
     */
    DATETIME("Datetime64"), UINT("UInt64");

    @Getter
    private final String type;

    CKColumnTypeEnum(String type) {
        this.type = type;
    }

    /**
     * isNumeric
     * @param ckColumnTypeEnum clickhouse的类型枚举
     * @return boolean
     */
    public static boolean isNumeric(CKColumnTypeEnum ckColumnTypeEnum) {
        return ckColumnTypeEnum == TINYINT || ckColumnTypeEnum == SMALLINT || ckColumnTypeEnum == INT || ckColumnTypeEnum == BIGINT
                || ckColumnTypeEnum == FLOAT || ckColumnTypeEnum == DOUBLE;
    }

    /**
     * get
     * @param typeCode 类型码
     * @return com.wiseco.var.process.app.server.commons.enums.CKColumnTypeEnum
     */
    public static CKColumnTypeEnum get(String typeCode) {
        for (CKColumnTypeEnum value : CKColumnTypeEnum.values()) {
            if (typeCode.equals(value.getType())) {
                return value;
            }
        }
        return null;
    }
}
