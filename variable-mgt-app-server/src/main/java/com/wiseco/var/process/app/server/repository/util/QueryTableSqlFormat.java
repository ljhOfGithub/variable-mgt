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
package com.wiseco.var.process.app.server.repository.util;

import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xujiawei
 * @since 2021/11/3
 */
public final class QueryTableSqlFormat extends TableSqlFormat {
    public static final String EXIST_TABLE_BASE_SQL = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_NAME LIKE '%s'";
    public static final String EXIST_DATA_BASE_SQL = "SELECT * FROM `%s` LIMIT 1";
    public static final String SELECT_COLUMN_TYPE = "SELECT TYPE FROM SYSTEM.COLUMNS WHERE TABLE= '%s' AND NAME='%s'";
    public static final String AS = " as ";
    public static final String AS1 = " AS ";
    public static final String AS2 = "as";
    public static final String AS3 = "AS";
    public static final int COLUMN_LENGTH = 2;
    private static final String COUNT = "SELECT COUNT(*) FROM `%s`";
    private static final String COUNT_WHERE = "SELECT COUNT(*) FROM `%s` WHERE %s";
    private static final String MIN = "SELECT min(`%s`) FROM `%s`";
    private static final String MIN_WHERE = "SELECT min(`%s`) FROM `%s` WHERE %s";
    private static final String MAX = "SELECT max(`%s`) FROM `%s`";
    private static final String MAX_WHERE = "SELECT max(`%s`) FROM `%s` WHERE %s";
    private static final String DESCRIBE_TABLE = " DESCRIBE TABLE `%s`";
    private static final String DISTINCT_QUERY = "SELECT DISTINCT `%s` FROM `%s`";
    private static final String DISTINCT_QUERY_IN = "SELECT DISTINCT `%s` FROM `%s` where `%s` IN (%s)";
    private static final String SELECT_INCREMENT_ID = "SELECT `increment_id` FROM `%s`";
    private static final String SELECT_ROWS_IN = "SELECT * FROM `%s` WHERE increment_id IN (%s)";
    private static final String GET_TABLE_COLUMN_NAMES = "SELECT name FROM system.columns WHERE table = '%s'";
    private static final String MONI_DATA_PREVIEW_LIST_WITH_LIMIT = "SELECT %s FROM `%s` a WHERE a.%s=%d  LIMIT %d,%d";
    private static final String MONI_DATA_PREVIEW_LIST_WITH_LIMIT_IN = "SELECT %s FROM `%s` a WHERE a.%s in (%s)  LIMIT %d,%d";
    private static final String SELECT_LIST = "SELECT %s FROM `%s` ORDER BY increment_id";
    private static final String SELECT_ALIAS_LIST_LIMIT = "SELECT %s FROM `%s` ORDER BY increment_id LIMIT %d,%d";
    private static final String SELECT_ALIAS_LIST_LIMIT_WITH_CONDITION = "SELECT %s FROM `%s` WHERE %s ORDER BY increment_id LIMIT %d,%d";
    private static final String SELECT_BY_ID = "SELECT %s FROM `%s` WHERE increment_id = %d";
    private static final String SELECT_LIST_LIMIT = "SELECT %s FROM `%s` ORDER BY increment_id LIMIT %d";
    private static final String SELECT_LIST_LIMIT_AND_WHERE = "SELECT %s FROM `%s` WHERE %s ORDER BY increment_id LIMIT %d";
    private static final String SYSTEM_PARTS = "select table,sum(rows) as rows,sum(data_uncompressed_bytes)/1048576 as data_uncompressed_bytes,sum(data_compressed_bytes)/1048576 as data_compressed_bytes from system.parts where active and table = '%s' group by table";

    private QueryTableSqlFormat() {
        super();
    }

    /**
     * getExistTableSql
     *
     * @param tableName 入参
     * @return String
     */
    public static String getExistTableSql(String tableName) {
        return String.format(EXIST_TABLE_BASE_SQL, tableName);
    }

    /**
     * getExistDataSql
     *
     * @param tableName 入参
     * @return String
     */
    public static String getExistDataSql(String tableName) {
        return String.format(EXIST_DATA_BASE_SQL, tableName);
    }

    /**
     * getModelAllExistPerformanceTable
     *
     * @param modelId 入参
     * @return String
     */
    public static String getModelAllExistPerformanceTable(long modelId) {
        return String.format(EXIST_TABLE_BASE_SQL, "model" + modelId + "_performance%");
    }

    /**
     * minColumn
     *
     * @param tableName 入参
     * @param column 入参
     * @return String
     */
    public static String minColumn(String tableName, String column) {
        return String.format(MIN, column, tableName);
    }

    /**
     * maxColumn
     *
     * @param tableName 入参
     * @param column 入参
     * @return String
     */
    public static String maxColumn(String tableName, String column) {
        return String.format(MAX, column, tableName);
    }

    /**
     * count
     *
     * @param tableName 入参
     * @return String
     */
    public static String count(String tableName) {
        return String.format(COUNT, tableName);
    }

    /**
     * count
     *
     * @param tableName 入参
     * @param whereCondition 入参
     * @return String
     */
    public static String count(String tableName, String whereCondition) {
        return StringUtils.isEmpty(whereCondition) ? count(tableName) : String.format(COUNT_WHERE, tableName, whereCondition);
    }

    /**
     * describeTable
     *
     * @param tableName 入参
     * @return String
     */
    public static String describeTable(String tableName) {
        return String.format(DESCRIBE_TABLE, tableName);
    }

    /**
     * getSystemParts
     *
     * @param tableName 入参
     * @return String
     */
    public static String getSystemParts(String tableName) {
        return String.format(SYSTEM_PARTS, tableName);
    }

    /**
     * distinctColumn
     *
     * @param tableName 入参
     * @param column 入参
     * @return String
     */
    public static String distinctColumn(String tableName, String column) {
        return String.format(DISTINCT_QUERY, column, tableName);
    }

    /**
     * distinctColumnIn
     *
     * @param tableName 入参
     * @param column 入参
     * @param ins 入参
     * @return String
     */
    public static String distinctColumnIn(String tableName, String column, List ins) {
        return String.format(DISTINCT_QUERY_IN, column, tableName, column, ins.stream().collect(Collectors.joining(COMMA)));
    }

    /**
     * selectIncrementId
     *
     * @param tableName 入参
     * @return String
     */
    public static String selectIncrementId(String tableName) {
        return String.format(SELECT_INCREMENT_ID, tableName);
    }

    /**
     * getTableColumnNames
     *
     * @param tableName 入参
     * @return String
     */
    public static String getTableColumnNames(String tableName) {
        return String.format(GET_TABLE_COLUMN_NAMES, tableName);
    }

    /**
     * selectList
     *
     * @param tableName 入参
     * @param columns 入参
     * @return String
     */
    public static String selectList(String tableName, List<String> columns) {
        return String.format(SELECT_LIST, columns.stream().collect(Collectors.joining(",")), tableName);
    }

    /**
     * selectListLimit
     *
     * @param tableName 入参
     * @param columns 入参
     * @param limit 入参
     * @return String
     */
    public static String selectListLimit(String tableName, List<String> columns, int limit) {
        String queryColunms = buildQueryColumnsSql(columns);
        return String.format(SELECT_LIST_LIMIT, queryColunms, tableName, limit);
    }

    /**
     * buildQueryColumnsSql
     *
     * @param columns 入参
     * @return String
     */
    private static String buildQueryColumnsSql(List<String> columns) {
        return columns.stream().map(column -> "`" + column + "`").collect(Collectors.joining(","));
    }

    /**
     * selectColumnTypeSql
     *
     * @param tableName 入参
     * @param columnName 入参
     * @return String
     */
    public static String selectColumnTypeSql(String tableName, String columnName) {
        return String.format(QueryTableSqlFormat.SELECT_COLUMN_TYPE, tableName, columnName);

    }

    /**
     * selectListLimitWithWhereConditionOrNot
     *
     * @param tableName 入参
     * @param columns 入参
     * @param limit 入参
     * @param whereCondition 入参
     * @return String
     */
    public static String selectListLimitWithWhereConditionOrNot(String tableName, List<String> columns, int limit, String whereCondition) {
        return StringUtils.isEmpty(whereCondition) ? selectListLimit(tableName, columns, limit) : String.format(SELECT_LIST_LIMIT_AND_WHERE,
                buildQueryColumnsSql(columns), tableName, whereCondition, limit);
    }

    /**
     * queryById
     *
     * @param tableName 入参
     * @param columnNames 入参
     * @param headerLine 入参
     * @return String
     */
    public static String queryById(String tableName, List<String> columnNames, int headerLine) {
        return String.format(SELECT_BY_ID, buildQueryColumnsSql(columnNames), tableName, headerLine);
    }

    /**
     * ck单表查询(支持别名)
     *
     * @param tableName 入参
     * @param fields 入参
     * @param whereCondition 入参
     * @param from 入参
     * @param size 入参
     * @return String
     */
    public static String queryListWithAlias(String tableName, List<String> fields, String whereCondition, int from, int size) {
        //此处的fields中可能包含查询字段别名 格式如 tableName.columnName as aliasName
        //给tableName、columnName以及aliasName前后都加上`符号
        return String.format(SELECT_ALIAS_LIST_LIMIT_WITH_CONDITION, buildQueryColumnsWithAliasSql(fields, tableName), tableName, whereCondition,
                from, size);
    }

    /**
     * buildQueryColumnsWithAliasSql
     *
     * @param fields
     * @param tableName
     * @return String
     */
    private static String buildQueryColumnsWithAliasSql(List<String> fields, String tableName) {
        StringBuilder queryColumnsBuilder = new StringBuilder();
        fields.forEach(original -> {
            String temp = original.replace(tableName + ".", "");
            if (temp.contains(AS) || temp.contains(AS1)) {
                String splitKey = AS2;
                if (temp.contains(AS3)) {
                    splitKey = AS3;
                }
                String[] columnArr = temp.split(splitKey);
                if (columnArr.length != COLUMN_LENGTH) {
                    throw new InternalDataServiceException("查询ck库SQL语法错误");
                }
                queryColumnsBuilder.append("`").append(columnArr[0].trim()).append("`").append(AS).append("`").append(columnArr[1].trim()).append("`,");

            } else {
                queryColumnsBuilder.append("`").append(temp.trim()).append("`,");
            }
        });
        String queryColumns = queryColumnsBuilder.toString();
        int len = Math.max(queryColumns.length() - 1, 1);
        return queryColumns.substring(0, len);
    }
}
