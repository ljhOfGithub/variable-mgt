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

import com.google.common.collect.Lists;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xujiawei
 * @author xupei
 * @since 2021/11/3
 */
@Slf4j
public class TableSqlFormat {
    public static final String INCREMENT_ID = "increment_id";
    public static final String BACKTRACT_ID = "id";
    public static final String ENGINE_NAME = "join";

    public static final String SELECT = "SELECT ";
    public static final String ALL_COLUM = " * ";

    public static final String DISTINCT = " DISTINCT ";

    public static final String FROM = " FROM ";

    public static final String WHERE = " WHERE ";

    public static final String AND = " AND ";

    public static final String OR = " OR ";
    public static final String ORDER_BY = " ORDER BY ";

    public static final String DESC = " DESC ";

    public static final String ASC = " ASC ";
    public static final String LIMIT_D_D = " LIMIT %d,%d";

    public static final String LIMIT = " LIMIT ";

    public static final String ROWS = "ROWS ";
    public static final String OFFSET = "OFFSET ";
    public static final String FETCH_NEXT = "FETCH NEXT ";
    public static final String ONLY = "ONLY ";


    public static final String COMMA = ", ";
    public static final String EQUAL = " = ";
    public static final String GT = " > ";
    public static final String NOT_EQUAL = " != ";
    public static final String QUOTES = "'%s'";
    public static final String BACKTICKS = "`%s`";
    public static final String NULL_STR = "NULL";
    public static final String BRACKETS = "(%s)";
    public static final String IN = " IN ";
    public static final String ZERO = " 0 ";
    public static final String CREATE_VIEW = "CREATE OR REPLACE VIEW model_platform_dev.%s AS SELECT %s from model_platform_dev.%s";
    private static final String CREATE_TABLE_BASE_SQL = "CREATE TABLE `%s`(%s) ENGINE = MergeTree() ORDER BY (increment_id, intHash32(increment_id)) SAMPLE BY intHash32(increment_id)";
    private static final String CREATE_TABLE_BASE_SQL_CLUSTER = "CREATE TABLE `%s` on cluster %s (%s) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/model_platform/%s', '{replica}') ORDER BY (increment_id, intHash32(increment_id)) SAMPLE BY intHash32(increment_id)";
    private static final String CREATE_TABLE_JOIN_GET_SQL = "CREATE TABLE `%s`(%s) ENGINE = Join(ANY, LEFT, increment_id)";
    private static final String CREATE_TABLE_AS = "CREATE TABLE `%s` AS `%s`";
    private static final String MYSQL_INSERT = "CREATE TABLE `%s` ENGINE = MergeTree ORDER BY %s AS SELECT * FROM mysql('%s', '%s', '%s', '%s', '%s')";

    protected TableSqlFormat() {
        throw new InternalDataServiceException("非法创建");
    }

    /**
     * buildCreateTableSql
     * @param tableName 表名
     * @param columnNames 列名(多个)
     * @param cluster 是否集群
     * @return String
     */
    public static String buildCreateTableSql(String tableName, String columnNames, Boolean cluster) {
        if (cluster) {
            return String.format(CREATE_TABLE_BASE_SQL_CLUSTER, tableName, columnNames, tableName);
        } else {
            return String.format(CREATE_TABLE_BASE_SQL, tableName, columnNames);
        }
    }

    /**
     * buildCreateJoinTableSql
     *
     * @param tableName 表名
     * @param columnNames 列名
     * @return String
     */
    public static String buildCreateJoinTableSql(String tableName, String columnNames) {
        return String.format(CREATE_TABLE_JOIN_GET_SQL, tableName, columnNames);
    }

    /**
     * createQuerySql
     * @param columns 多个列名
     * @param tableName 表名
     * @param from 起始地址
     * @param pageSize 页大小
     * @param desc 降序字段
     * @param asc 升序字段
     * @param filter 过滤条件
     * @return String
     */
    public static String createQuerySql(List<String> columns, String tableName, int from, int pageSize, List<String> desc, List<String> asc, Map<String, Object> filter) {
        StringBuilder sb = new StringBuilder().append(SELECT)
                .append(columns.stream().map(s -> String.format(BACKTICKS, s)).collect(Collectors.joining(COMMA)))
                .append(FROM)
                .append(tableName);
        Optional.ofNullable(filter)
                .ifPresent(map -> sb.append(WHERE)
                        .append(map.entrySet().stream()
                                .map(entry -> {
                                    if (entry.getValue() instanceof List) {
                                        List valueList = (List) entry.getValue();
                                        return entry.getKey().concat(IN).concat(String.format(BRACKETS, valueList.stream().map(e -> String.format(QUOTES, e.toString())).collect(Collectors.joining(COMMA))));
                                    } else {
                                        return entry.getKey().concat(EQUAL).concat(entry.getValue() instanceof Number ? entry.getValue().toString() : String.format(QUOTES, entry.getValue().toString()));
                                    }
                                })
                                .collect(Collectors.joining(AND)))
                );
        List<String> order = Optional.ofNullable(desc).map(list -> list.stream().map(s -> s.concat(DESC)).collect(Collectors.toList())).orElse(Lists.newArrayList());
        order.addAll(Optional.ofNullable(asc).map(list -> list.stream().map(s -> s.concat(ASC)).collect(Collectors.toList())).orElse(Lists.newArrayList()));
        if (!CollectionUtils.isEmpty(order)) {
            sb.append(ORDER_BY).append(order.stream().collect(Collectors.joining(COMMA)));
        } else {
            sb.append(ORDER_BY).append(INCREMENT_ID).append(ASC);
        }
        sb.append(String.format(LIMIT_D_D, from, pageSize));
        return sb.toString();
    }

    /**
     * createQuerySqlSimple
     * @param columns 多个列
     * @param tableName 表名
     * @return String
     */
    public static String createQuerySqlSimple(List<String> columns, String tableName) {
        StringBuilder sb = new StringBuilder().append(SELECT)
                .append(columns.stream().map(s -> String.format(BACKTICKS, s)).collect(Collectors.joining(COMMA)))
                .append(FROM)
                .append(tableName)
                .append(ORDER_BY)
                .append(INCREMENT_ID)
                .append(ASC);
        return sb.toString();
    }

    /**
     * sampleWeightNullByNonNullTarget
     * @param tableName 表名
     * @param targets 目标
     * @param sampleWeights 样本权重
     * @return String
     */
    public static String sampleWeightNullByNonNullTarget(String tableName, List<String> targets, List<String> sampleWeights) {
        return new StringBuilder(SELECT)
                .append(DISTINCT)
                .append(ListUtils.union(targets, sampleWeights).stream().collect(Collectors.joining(COMMA)))
                .append(FROM)
                .append(tableName)
                .append(WHERE)
                .append(String.format(BRACKETS, sampleWeights.stream().map(s -> s.concat(EQUAL).concat(NULL_STR)).collect(Collectors.joining(OR))
                        .concat(OR).concat(sampleWeights.stream().map(s -> s.concat(GT).concat(ZERO)).collect(Collectors.joining(OR)))))
                .append(AND)
                .append(String.format(BRACKETS, targets.stream().map(s -> s.concat(NOT_EQUAL).concat(NULL_STR)).collect(Collectors.joining(OR))))
                .toString();
    }

    /**
     * insertMysql
     * @param ckTableName clickhouse的表名
     * @param ids id字符串
     * @param url 统一资源定位符
     * @param database 数据库名
     * @param tableName 表名
     * @param username 用户名
     * @param password 密码
     * @return String
     */
    public static String insertMysql(String ckTableName, String ids, String url, String database, String tableName, String username, String password) {
        return String.format(MYSQL_INSERT, ckTableName, ids, url, database, tableName, username, password);
    }

    /**
     * createTableAsSql
     *
     * @param oldTableName 旧表名
     * @param newTableName 新表名
     * @return String
     */
    public static String createTableAsSql(String oldTableName, String newTableName) {
        return String.format(CREATE_TABLE_AS, newTableName, oldTableName);
    }

    /**
     * createView
     * @param tableName 旧表名
     * @param viewColumns 预览的列
     * @return String
     */
    public static String createView(String tableName, List<String> viewColumns) {
        return String.format(CREATE_VIEW, tableName + "_view", String.join(",", viewColumns), tableName);
    }

    /**
     * createQueryFilerSql
     * @param columns 多个列
     * @param tableName 表名
     * @param from 起始地址
     * @param pageSize 页大小
     * @param desc 降序字段
     * @param asc 升序字段
     * @param whereContition where后面的条件
     * @return String
     */
    public static String createQueryFilerSql(List<String> columns, String tableName, int from, int pageSize, List<String> desc, List<String> asc, String whereContition) {
        StringBuilder sb = new StringBuilder().append(SELECT)
                .append(columns.stream().map(s -> String.format(BACKTICKS, s)).collect(Collectors.joining(COMMA)))
                .append(FROM)
                .append(tableName);


        // 拼装where条件
        sb.append(" where ").append(whereContition);
        List<String> order = Optional.ofNullable(desc).map(list -> list.stream().map(s -> s.concat(DESC)).collect(Collectors.toList())).orElse(Lists.newArrayList());
        order.addAll(Optional.ofNullable(asc).map(list -> list.stream().map(s -> s.concat(ASC)).collect(Collectors.toList())).orElse(Lists.newArrayList()));
        if (!CollectionUtils.isEmpty(order)) {
            sb.append(ORDER_BY).append(order.stream().collect(Collectors.joining(COMMA)));
        } else {
            sb.append(ORDER_BY).append(BACKTRACT_ID).append(ASC);
        }
        sb.append(String.format(LIMIT_D_D, from, pageSize));
        return sb.toString();
    }
}
