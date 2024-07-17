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

import java.util.Arrays;
import java.util.List;

/**
 * 函数类型 枚举类
 *
 * @author wangxianli
 * @since 2022/6/10
 */
@AllArgsConstructor
@Getter
public enum ColumnMappingDataTypeEnum {

    // 操作类型：varchar、char、text->string
    //
    //real、double、float、decimal、numeric->double
    //
    //datetime->datetime
    //
    //date->date

    INT("int", new String[]{"bigint", "int", "tinyint", "smallint", "bit"}), STRING("string", new String[]{"varchar", "char", "enum"}), TEXT(
            "string",
            new String[]{
                    "tinytext",
                    "mediumtext",
                    "longtext",
                    "text",
                    "mediumblob",
                    "longblob",
                    "blob",
                    "json"}), DOUBLE(
            "double",
            new String[]{
                    "real",
                    "double",
                    "float",
                    "decimal",
                    "numeric"}), DATE(
            "date",
            new String[]{"date"}), DATETIME(
            "datetime",
            new String[]{
                    "datetime",
                    "timestamp"}), TIMESTAMP(
            "timestamp",
            new String[]{
                    "datetime",
                    "timestamp"}),

    NUMBER("number", new String[]{"number"}), VARCHAR("string", new String[]{"varchar2", "nvarchar2", "nchar"}), CLOB("string",
            new String[]{"clob",
                    "nclob"}),;

    private String dataType;
    private String[] columnTypeArray;

    /**
     * 根据类型获取枚举
     * @param columnType 字段类型
     * @return ColumnMappingDataTypeEnum
     */
    public static ColumnMappingDataTypeEnum getTypeEnumByColumnType(String columnType) {
        for (ColumnMappingDataTypeEnum typeEnum : ColumnMappingDataTypeEnum.values()) {
            List<String> list = Arrays.asList(typeEnum.getColumnTypeArray());
            if (list.contains(columnType)) {
                return typeEnum;
            }
        }
        return null;
    }

}
