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

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ItemsEnum {
    /**
     * 字段类型枚举
     */

    Int("int"), String("string"), FLOAT("float"), DOUBLE("double"), BOOLEAN("boolean"),DATE("date"),DATETIME("datetime");
    private String fieldType;

    /**
     * 检查类型
     *
     * @param fieldType     字段类型
     * @param conditionType 条件类型
     * @return String
     */
    public static String checkType(String fieldType, String conditionType) {
        ItemsEnum itemsEnum = getItemsEnum(fieldType);
        switch (Objects.requireNonNull(itemsEnum)) {
            case DOUBLE:
            case FLOAT:
            case Int:
                return Objects.requireNonNull(IntType.getIntType(conditionType)).getConditionSql();
            case BOOLEAN:
                return Objects.requireNonNull(BooleanType.getIbooleanType(conditionType)).getConditionSql();
            case DATE:
            case DATETIME:
                return Objects.requireNonNull(DateType.getDateType(conditionType)).getConditionSql();
            default:
                return Objects.requireNonNull(StrType.getStrType(conditionType)).getConditionSql();
        }
    }

    /**
     * 根据字段类型获取枚举
     *
     * @param fieldType 字段类型
     * @return ItemsEnum
     */
    public static ItemsEnum getItemsEnum(String fieldType) {
        for (ItemsEnum item : ItemsEnum.values()) {
            if (item.fieldType.equals(fieldType)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 定义布尔类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum BooleanType {
        /**
         * 布尔操作枚举
         */
        TRUE("EQUALS", " = ?"), FALSE("NOT_EQUALS", " != ?");

        private String conditionType;

        private String conditionSql;

        /**
         * 获取条件类型
         * @param conditionType 条件类型
         * @return BooleanType
         */
        public static BooleanType getIbooleanType(String conditionType) {
            for (BooleanType item : BooleanType.values()) {
                if (item.conditionType.equals(conditionType)) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * 定义整型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum IntType {
        /**
         * 整数类型操作枚举
         */
        EQUALTO("EQUALS", " = ? "), NOTEQUALTO("NOT_EQUALS", " != ? "), GREATERTHANOREQUAL("GREATER_THAN_OR_EQUALS", " >= ? "), GREATERTHAN("GREATER_THAN", " > ? "), LESSTHANOREQUAL("LESS_THAN_OR_EQUALS",
                " <= ? "), LESSTHAN(
                "LESS_THAN",
                " < ? ");

        private String conditionType;

        private String conditionSql;

        /**
         * 获取int条件类型
         * @param conditionType 条件类型
         * @return IntType
         */
        public static IntType getIntType(String conditionType) {
            for (IntType item : IntType.values()) {
                if (item.conditionType.equals(conditionType)) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * 定义字符串枚举
     */
    @Getter
    @AllArgsConstructor
    public enum StrType {
        /**
         * 字符类型操作枚举
         */

        CONTAIN("CONTAINS", " like concat('%%',?,'%%') "), NOTCONTAINS("NOT_CONTAINS", " not like concat('%%',?,'%%') "), STARTSWITH("START_WITH",
                " like concat('',?,'%%') "), ENDSWITH(
                "END_WITH",
                " like concat('%%',?,'') "), EQUALTO(
                "EQUALS",
                " = ? "), NOTEQUALTO(
                "NOT_EQUALS",
                " != ? ");

        private String conditionType;

        private String conditionSql;

        /**
         * 获取String条件类型
         * @param conditionType 条件类型
         * @return StrType
         */
        public static StrType getStrType(String conditionType) {
            for (StrType item : StrType.values()) {
                if (item.conditionType.equals(conditionType)) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * 定义日期类型枚举
     */
    @Getter
    @AllArgsConstructor
    public enum DateType {
        /**
         * 日期类型操作枚举
         */
        EQUALTO("EQUALS", " = ? "), NOTEQUALTO("NOT_EQUALS", " != ? "), GREATERTHANOREQUAL("GREATER_THAN_OR_EQUALS", " >= ? "), GREATERTHAN("GREATER_THAN", " > ? "), LESSTHANOREQUAL("LESS_THAN_OR_EQUALS",
                " <= ? "), LESSTHAN(
                "LESS_THAN",
                " < ? ");

        private String conditionType;

        private String conditionSql;

        /**
         * 获取int条件类型
         * @param conditionType 条件类型
         * @return IntType
         */
        public static DateType getDateType(String conditionType) {
            for (DateType item : DateType.values()) {
                if (item.conditionType.equals(conditionType)) {
                    return item;
                }
            }
            return null;
        }
    }

}
