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

import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.SkipColumnType;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.TextColumnType;
import tech.tablesaw.columns.times.TimeColumnType;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author wiseco
 */
public enum DataType {
    /**
     * 整数
     */
    INT("int", Integer.class, (d1, d2) -> Integer.compare((Integer) d1, (Integer) d2), "Int32", "int", "int", "number"),
    /**
     * 小数
     */
    DOUBLE("double", Double.class, (d1, d2) -> Double.compare((Double) d1, (Double) d2), "Float64", "double", "double", "float"),
    /**
     * 日期（年月日）
     */
    DATE("date", Date.class, (d1, d2) -> ((Date) d1).compareTo((Date) d2), "Date", "Date", "date"),
    /**
     * 日期(年月日 时分秒)
     */
    DATETIME("datetime", Date.class, (d1, d2) -> ((Date) d1).compareTo((Date) d2), "DateTime", "timestamp", "date", "datetime", "timestamp"),
    /**
     * 字符串
     */
    STRING("string", String.class, (d1, d2) -> ((String) d1).compareTo((String) d2), "String", "string", "string", "varchar2"),
    /**
     * 布尔值
     */
    BOOLEAN("boolean", Boolean.class, (d1, d2) -> ((Boolean) d1).compareTo((Boolean) d2), "Bool", "boolean", "boolean");

    private static final String INFINITY = "Infinity";
    private static final String MINUS_INFINITY = "-Infinity";
    private static final Map<String, DataType> PMML_DATA_TYPE_MAP = new HashMap<String, DataType>() {
        {
            put("int", INT);
            put("integer", INT);
            put("double", DOUBLE);
            put("float", DOUBLE);
            put("date", DATE);
            put("datetime", DATETIME);
            put("string", STRING);
            // 如果 PMML 文件未定义 Output 字段, PMML4S 解析后将向 Model 添加的默认 OutputField, real 类型
            // 此时将 real 类型映射为 DOUBLE
            put("real", DOUBLE);
        }
    };
    private final String name;
    private final Class<?> javaClass;
    private final BiFunction<Object, Object, Integer> comparator;
    private final String clickHouseDataType;
    private final String hiveDataType;
    private final Set<String> databaseStringSet;

    DataType(String name, Class<?> javaClass, BiFunction<Object, Object, Integer> comparator, String clickHouseDataType, String hiveDataType,
             String... databaseString) {
        this.name = name;
        this.javaClass = javaClass;
        this.comparator = comparator;
        this.clickHouseDataType = clickHouseDataType;
        this.hiveDataType = hiveDataType;
        this.databaseStringSet = new HashSet<String>(Arrays.asList(databaseString));
    }

    /**
     * isNumber
     * @param type 类型
     * @return boolean
     */
    public static boolean isNumber(DataType type) {
        return type == DataType.INT || type == DataType.DOUBLE;
    }

    /**
     * 获取实例
     * @param name 名称
     * @return 实例
     */
    public static DataType typeOf(String name) {
        for (DataType item : DataType.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * valueOfByDbString
     * @param type 类型
     * @return com.wiseco.var.process.app.server.commons.enums.DataType
     */
    public static DataType typeOf(ColumnType type) {
        DataType value = null;
        if (type instanceof ShortColumnType || type instanceof IntColumnType) {
            value = DataType.INT;
        } else if (type instanceof LongColumnType || type instanceof FloatColumnType || type instanceof DoubleColumnType) {
            value = DataType.DOUBLE;
        } else if (type instanceof BooleanColumnType || type instanceof StringColumnType || type instanceof TextColumnType || type instanceof SkipColumnType) {
            value = DataType.STRING;
        } else if (type instanceof DateColumnType) {
            value = DataType.DATE;
        } else if (type instanceof TimeColumnType || type instanceof DateTimeColumnType) {
            value = DataType.DATETIME;
        }
        return value;
    }

    /**
     * 获取数据库的类型
     *
     * @param dbString 数据库字符串
     * @return 数据库的类型
     */
    public static DataType valueOfByDbString(String dbString) {
        if (dbString == null || dbString.isEmpty()) {
            return null;
        }
        for (DataType value : DataType.values()) {
            if (value.databaseStringSet.contains(dbString.toLowerCase())) {
                return value;
            }
        }
        return null;
    }

    /**
     * valueOfPmmlName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.DataType
     */
    public static DataType valueOfPmmlName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return PMML_DATA_TYPE_MAP.get(name.toLowerCase());
    }

    /**
     * valueOfName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.DataType
     */
    public static DataType valueOfName(String name) {
        for (DataType en : DataType.values()) {
            if (name.equals(en.getName())) {
                return en;
            }
        }
        return null;
    }

    /**
     * valueOfByName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.DataType
     */
    public static DataType valueOfByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (DataType value : DataType.values()) {
            if (value.name().contains(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * judgedTypeBy
     * @param obj 对象
     * @return com.wiseco.var.process.app.server.commons.enums.DataType
     */
    public static DataType judgedTypeBy(Object obj) {

        DataType result;

        if (null == obj) {
            result = null;
        }
        if (org.springframework.util.StringUtils.isEmpty(obj)) {
            result = null;
        }
        if (obj instanceof Number) {
            if (INFINITY.equalsIgnoreCase(String.valueOf(obj))
                    || MINUS_INFINITY.equalsIgnoreCase(String.valueOf(obj))
                    || "nan".equalsIgnoreCase(String.valueOf(obj))) {
                result = null;
            }
            try {
                Integer.parseInt(String.valueOf(obj));
                result = INT;
            } catch (NumberFormatException e) {
                return DOUBLE;
            }
        } else if (obj instanceof String) {
            if (((String) obj).trim().isEmpty()
                    || "Infinity".equalsIgnoreCase(((String) obj).trim())
                    || "-Infinity".equalsIgnoreCase(((String) obj).trim())
                    || "nan".equalsIgnoreCase(((String) obj).trim())) {
                result = null;
            }
            try {
                Integer.parseInt((String) obj);
                result = INT;
            } catch (NumberFormatException e) {
                try {
                    Double.parseDouble((String) obj);
                    result = DOUBLE;
                } catch (NumberFormatException ex) {
                    result = STRING;
                }
            }
        } else {
            result = STRING;
        }

        return result;
    }

    /**
     * compare
     * @param d1 对象1
     * @param d2 对象2
     * @return int
     */
    public int compare(Object d1, Object d2) {
        return comparator.apply(d1, d2);
    }

    public String getName() {
        return name;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public String getClickHouseDataType() {
        return clickHouseDataType;
    }

    public String getHiveDataType() {
        return hiveDataType;
    }
}
