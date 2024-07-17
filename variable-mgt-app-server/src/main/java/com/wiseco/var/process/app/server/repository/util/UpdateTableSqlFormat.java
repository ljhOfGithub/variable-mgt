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

import com.wiseco.var.process.app.server.commons.enums.CKColumnTypeEnum;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author xupei
 */
public final class UpdateTableSqlFormat extends TableSqlFormat {

    private static final String COLUMN_AROUND_CHAR = "`";
    private static final String INSERT_TABLE_ALL_SQL = "INSERT INTO `%s` VALUES (%s)";
    private static final String INSERT_ONE_COLUMN_SQL = "ALTER TABLE `%s`.`%s` ADD COLUMN %s %s";
    private static final String INSERT_ONE_COLUMN_SQL_CLUSTER = "ALTER TABLE `%s`.`%s` on cluster %s ADD COLUMN %s %s";
    private static final String DELETE_ONE_COLUMN_SQL = "ALTER TABLE `%s`.`%s` DROP  COLUMN  IF EXISTS `%s`";
    private static final String UPDATE_ONE_COLUMN_SQL = "ALTER TABLE `%s` UPDATE %s WHERE 1";
    private static final String UPDATE_ONE_COLUMN_SQL_JOIN = "%s=joinGet('%s', '%s', increment_id)";
    private static final String EXIST_COLUMN = "SELECT * FROM SYSTEM.COLUMNS WHERE DATABASE='%s' AND TABLE = '%s' AND NAME='%s'";
    private static final String INSERT_TABLE_MORE_SQL = "INSERT INTO `%s`(%s) VALUES (%s)";
    private static final String COPY_SQL_WHERE = "INSERT INTO `%s` SELECT * FROM `%s` WHERE %s";
    private static final String COPY_SQL = "INSERT INTO `%s` SELECT * FROM `%s` WHERE %s";
    private static final String COPY_SOME = "INSERT INTO `%s` SELECT * FROM `%s` WHERE %s and increment_id IN (%s)";
    private static final String INSERT_SELECT = "INSERT INTO `%s` SELECT * FROM `%s`";
    private static final String ALTER_COLUMN_TYPE = "ALTER TABLE `%s` MODIFY COLUMN %s %s";
    private static final String INSERT_ROWS = "INSERT INTO `%s` (%s) VALUES %s";
    private static final String UPDATE = "ALTER TABLE `%s` UPDATE `%s` = %s WHERE `%s` = %s";
    private static final String DELETE_TABLE_BASE_SQL = "DROP TABLE IF EXISTS `%s`";
    private static final String DELETE_TABLE_BASE_SQL_CLUSTER = "DROP TABLE IF EXISTS `%s` on cluster %s sync";
    private static final String DELETE_COLUMN_BASE_SQL = "ALTER TABLE `%s` DROP COLUMN %s";

    private UpdateTableSqlFormat() {
        super();
    }

    /**
     * @param tableName   表名
     * @param columnNames rc
     * @return String
     */
    public static String buildInsertAllColumnsSql(String tableName, List<String> columnNames) {
        final StringBuilder sql = new StringBuilder();
        final StringBuilder sql1 = new StringBuilder();
        IntStream.range(0, columnNames.size()).forEach(i -> {
            sql.append("?").append(",");
            sql1.append(COLUMN_AROUND_CHAR).append(columnNames.get(i)).append(COLUMN_AROUND_CHAR).append(",");
        });
        sql.deleteCharAt(sql.length() - 1);
        sql1.deleteCharAt(sql1.length() - 1);
        return String.format(INSERT_TABLE_MORE_SQL, tableName, sql1, sql);
    }

    /**
     * 构建插入Sql
     * @param databaseName rc
     * @param tableName rc
     * @param columnName rc
     * @param type rc
     * @param cluster rc
     * @param clusterName rc
     * @return sql
     */
    public static String buildInsertOneColumnsSql(String databaseName, String tableName, String columnName, CKColumnTypeEnum type, Boolean cluster,
                                                  String clusterName) {
        if (cluster) {
            return String.format(INSERT_ONE_COLUMN_SQL_CLUSTER, databaseName, tableName, clusterName, columnName, type.getType());
        } else {
            return String.format(INSERT_ONE_COLUMN_SQL, databaseName, tableName, columnName, type.getType());
        }
    }

    /**
     * 存在Sql
     * @param database rc
     * @param tableName rc
     * @param columnName rc
     * @return sql
     */
    public static String existColumnSql(String database, String tableName, String columnName) {
        return String.format(EXIST_COLUMN, database, tableName, columnName);
    }

    /**
     * 构建更新列Sql
     * @param tableName rc
     * @param columnNames rc
     * @param tmpTableName rc
     * @return sql
     */
    public static String buildUpdateColumnsSql(String tableName, List<String> columnNames, String tmpTableName) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            sb.append(String.format(UPDATE_ONE_COLUMN_SQL_JOIN, columnName, tmpTableName, columnName)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return String.format(UPDATE_ONE_COLUMN_SQL, tableName, sb);
    }

    /**
     * 构建Insert Sql
     * @param tableName rc
     * @param columns rc
     * @return String
     */
    public static String buildInsertMoreColumnsSql(String tableName, List<String> columns) {
        final StringBuilder sqlNames = new StringBuilder();
        final StringBuilder sqlValues = new StringBuilder();
        IntStream.range(0, columns.size()).forEach(i -> {
            sqlNames.append(columns.get(i)).append(",");
            sqlValues.append("?").append(",");
        });
        sqlNames.append(TableSqlFormat.INCREMENT_ID);
        sqlValues.append("?");
        return String.format(INSERT_TABLE_MORE_SQL, tableName, sqlNames, sqlValues);
    }

    /**
     * 构建从表中插入的Sql语句
     * @param fromTable rc
     * @param toTable rc
     * @param whereSql rc
     * @return sql
     */
    public static String buildInsertSelectedFromTableSql(String fromTable, String toTable, String whereSql) {
        return null == whereSql ? String.format(COPY_SQL, toTable, fromTable) : String.format(COPY_SQL_WHERE, toTable, fromTable, whereSql);
    }

    /**
     * copySomeDataSql
     * @param oldTableName rc
     * @param newTableName rc
     * @param whereSql rc
     * @param rowNums rc
     * @return sql
     */
    public static String copySomeDataSql(String oldTableName, String newTableName, String whereSql, List<Integer> rowNums) {
        return String.format(COPY_SOME, newTableName, oldTableName, whereSql, rowNums.stream().map(Object::toString).collect(Collectors.joining(",")));
    }

    /**
     * 删除Sql
     * @param databaseName rc
     * @param tableName rc
     * @param dropColumnName rc
     * @return sql
     */
    public static String dropColumnSql(String databaseName, String tableName, String dropColumnName) {
        return String.format(DELETE_ONE_COLUMN_SQL, databaseName, tableName, dropColumnName);
    }

    /**
     * 构建修改Sql
     * @param tableName rc
     * @param columnName rc
     * @param type rc
     * @return sql
     */
    public static String buildModifyColumnSql(String tableName, String columnName, String type) {
        return String.format(ALTER_COLUMN_TYPE, tableName, columnName, type);
    }

    /**
     * 更新
     * @param tableName rc
     * @param column rc
     * @param originalValue rc
     * @param targetValue rc
     * @return sql
     */
    public static String update(String tableName, String column, String originalValue, String targetValue) {
        return String.format(UPDATE, tableName, column, targetValue, column, originalValue);
    }

    /**
     * 获取删除Sql
     * @param tableName rc
     * @param cluster rc
     * @return sql
     */
    public static String getDeleteTableSql(String tableName, Boolean cluster) {
        if (cluster) {
            return String.format(DELETE_TABLE_BASE_SQL_CLUSTER, tableName);
        } else {
            return String.format(DELETE_TABLE_BASE_SQL, tableName);
        }
    }

}
