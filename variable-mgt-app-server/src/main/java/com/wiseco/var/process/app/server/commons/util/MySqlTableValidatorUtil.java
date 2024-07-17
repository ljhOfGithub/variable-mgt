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
package com.wiseco.var.process.app.server.commons.util;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mingao
 * @since 2023/9/20
 */
@Slf4j
public class MySqlTableValidatorUtil {
    private static final String TABLE_NAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]*$";
    private static final String COLUMN_NAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]*$";

    private static final List<String> WHERECONTITION_PATTERN = Arrays.asList("=", "<>", ">", "<", ">=", "<=", "IN", "NOT IN", "LIKE", "NOT LIKE",
            "IS NULL", "IS NOT NULL", "AND", "OR", "NOT");

    //    private static String INTEGER_PATTERN = "\t-?\\d+";
    //
    //    private static String IFLOAT_PATTERN = "(-?\\d+)(\\.\\d+)?";
    //
    //    private static String VARCHAR_PATTERN = "^.*$";
    //
    //    private static String BOOLEAN_PATTERN = "^(true|false)$";
    //
    //    private static String DATE_PATTERN1 = "(\\d{4}|\\d{2})-((1[0-2])|(0?[1-9]))-(([12][0-9])|(3[01])|(0?[1-9]))";
    //
    //    private static String DATE_PATTERN2 = "((1[0-2])|(0?[1-9]))/(([12][0-9])|(3[01])|(0?[1-9]))/(\\d{4}|\\d{2})";
    //
    //    private static String DATE_TIME = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    private static boolean isValidTableName(String tableName) {
        Pattern pattern = Pattern.compile(TABLE_NAME_PATTERN);
        Matcher matcher = pattern.matcher(tableName);
        return matcher.matches();
    }

    private static boolean isValidColumnName(String columnName) {
        Pattern pattern = Pattern.compile(COLUMN_NAME_PATTERN);
        Matcher matcher = pattern.matcher(columnName);
        return matcher.matches();
    }

    /**
     * 校验表名、表字段是否符合规范
     *
     * @param tableName   表名
     * @param columnNames 字段名
     * @return true:符合 / false:不符合
     */
    public static boolean isValidTableStructure(String tableName, List<String> columnNames) {
        if (!isValidTableName(tableName)) {
            return false;
        }

        for (String columnName : columnNames) {
            if (!isValidColumnName(columnName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验表名长度
     *
     * @param tableName    表名
     * @return true:符合 / false:不符合
     */
    public static boolean isValidTableNameLength(String tableName) {
        return tableName.length() < MagicNumbers.INT_64;
    }

    /**
     * 校验表字段名、字段描述是否重复
     *
     * @param fieldNames    字段名集合
     * @param fieldComments 字段描述集合
     * @return true:符合 / false:不符合
     */
    public static boolean isValidFieldRepeat(List<String> fieldNames, List<String> fieldComments) {
        Set<String> nameSet = new HashSet<>(fieldNames);
        if (nameSet.size() < fieldNames.size()) {
            return false;
        }

        //        Set<String> commentSet = new HashSet<>(fieldComments);
        //        if (commentSet.size() < fieldComments.size()) {
        //            return false;
        //        }

        //        for (int i = 0; i < fieldNames.size(); i++) {
        //            if (fieldComments.get(i).equals(fieldNames.get(i))) {
        //                return false;
        //            }
        //        }
        return true;
    }

    /**
     * 校验where条件, where条件可以为 “”或null
     *
     * @param whereCondition where条件
     * @return true:符合 / false:不符合
     */
    public static boolean validWhereCondition(String whereCondition) {
        if (whereCondition == null || whereCondition.isEmpty()) {
            return true;
        }
        for (String keyword : WHERECONTITION_PATTERN) {
            if (whereCondition.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
