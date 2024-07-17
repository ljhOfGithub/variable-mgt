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

import cn.hutool.core.lang.Pair;
import com.wiseco.var.process.app.server.commons.enums.DataType;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.util.StringPool.COMMA;
import static com.wiseco.var.process.app.server.commons.util.StringPool.DOT;

/**
 * @author Asker.J
 * @since 2022/9/19
 */
@Slf4j
public final class SqlFormat {

    public static final String TABLE_NAME = "TABLE_NAME";

    public static final String TABLE_COMMENT = "TABLE_COMMENT";
    public static final String TABLE_COLUMNS = "COLUMNS";
    public static final String PREFIX = "\uFEFF";
    /**
     * load data local inpath '文件路径' into table t_user_province_city_county partition(province='zhejiang',city='hangzhou',county='xiaoshan');
     */
    public static final String LOAD_OVERWRITE_DATA = "LOAD DATA INPATH '%s' OVERWRITE INTO TABLE %s ";
    public static final String LOAD_OVERWRITE_PARTITION_DATA = "LOAD DATA INPATH '%s' OVERWRITE INTO TABLE %s PARTITION (%s)";
    public static final String LOAD_PARTITION_DATA = "LOAD DATA INPATH '%s' INTO TABLE %s PARTITION (%s)";
    /**
     * 添加分区
     */
    public static final String ADD_PARTITIONS = "ALTER TABLE %s ADD PARTITION(%s)";
    public static final String UPDATE_PARTITIONS = "ALTER TABLE %s PARTITION (%s) RENAME TO PARTITION (%s)";
    private static final Pattern VALID_HEADER_REGEX = Pattern.compile("(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)");
    /**
     * 删除表格
     */
    private static final String TABLE_DELETE_SQL = "DROP TABLE IF EXISTS %s.%s";
    private static final String ADD_TABLE_COLUMNS_SQL = "ALTER TABLE `%s`.`%s` ADD COLUMNS (%s)";
    private static final String REPLACE_TABLE_COLUMNS_SQL = "ALTER TABLE `%s`.`%s` REPLACE COLUMNS (%s);";
    private static final String CREATE_TABLE_VIEW = "create view if not exists {0} as select {1} from {2}";
    /**
     * 查询语句
     */
    private static final String TABLE_QUERY_SQL = "select %s from %s where %s limit %s,%s";
    //            " TBLPROPERTIES('skip.header.line.count'='1') \n";
    /**
     * 没有where条件的分页查询
     */
    private static final String TABLE_QUERY_SQL_NO_WHERE = "select %s from %s limit %s,%s";
    private static final String LOAD_FILE_SQL = "LOAD DATA LOCAL INPATH '%s' into table %s";
    private static final String DELETE_PARTITION_DATA_SQL = "alter table %s drop partition(%s='%s')";
    /**
     * 创建create + location语句
     */
    private static final String CREAT_LOCATION_SQL = " CREATE TABLE %s (%s)%n"
            +
            //            " ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' \n" +
            //            " ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' \n" +
            //            " WITH SERDEPROPERTIES ('escapeChar'='%s', 'quoteChar'='%s', 'separatorChar'='%s') \n" +
            " ROW FORMAT DELIMITED %n" + "\t FIELDS TERMINATED BY '%s' %n"
            + "\t LINES TERMINATED BY '\\n' %n"
            + " STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' %n"
            + "\t OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'%n"
            + " LOCATION '%s' %n";
    /**
     * 创建create + location语句 + partition
     */
    private static final String CREAT_LOCATION_AND_PARTITION_SQL = " CREATE TABLE %s (%s)%n" + " PARTITIONED BY (%s) %n" + " ROW FORMAT DELIMITED %n"
            + "\t FIELDS TERMINATED BY '%s' %n" + "\t LINES TERMINATED BY '\\n' %n"
            + " STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' %n"
            + "\t OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'%n"
            + " LOCATION '%s' %n";
    /**
     * create table t_user_province_city_county (id int, name string,age int) partitioned by (province string, city string, county string);
     */
    private static final String CREATE_PARTITION_SQL = " CREATE TABLE %s (%s)%n" + " PARTITIONED BY (%s) %n" + " ROW FORMAT DELIMITED %n"
            + " FIELDS TERMINATED BY '%s' %n" + " LINES TERMINATED BY '\\n' %n"
            + " STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' %n"
            + " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'%n";
    /**
     * 建表语句
     * create table t_user_province_city_county (id int, name string,age int)
     */
    private static final String CREATE_SQL = " CREATE TABLE %s (%s)%n" + " ROW FORMAT DELIMITED %n"
            + " FIELDS TERMINATED BY '%s' %n" + " LINES TERMINATED BY '\\n' %n"
            + " STORED AS INPUTFORMAT 'org.apache.hadoop.mapred.TextInputFormat' %n"
            + " OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'%n";
    private static final String COUNT_SQL = "select count(1) from `%s`.`%s` where %s";
    private static final String COUNT_SQL_NO_WHERE = "select count(1) from `%s`.`%s`";
    private static final String INSERT_SELECT_SQL = "FROM %s%n" + "INSERT overwrite TABLE %s partition(%s)%n" + "SELECT %s";

    private SqlFormat() {
        throw new InternalDataServiceException("SqlFormatUtil create error !!");
    }

    /**
     * 组装建表语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @return String
     */
    public static String formatCreateTableSql(String dbName, String tableName, List<Pair<String, DataType>> columns) {
        return formatCreateTableSql(dbName, tableName, columns, COMMA);
    }


    /**
     * 组装建表语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @param partitions 入参
     * @return String
     */
    public static String formatCreatPartitionTableSql(String dbName, String tableName, List<Pair<String, DataType>> columns,
                                                      String separatorChar, List<Pair<String, DataType>> partitions) {
        String cols = columns.stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        String pcols = partitions.stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        String sql = String.format(CREATE_PARTITION_SQL, dbName + DOT + tableName, cols, pcols, separatorChar);
        log.info("hive sql ：{}", sql);
        return sql;
    }

    /**
     * 组装建表语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @return String
     */
    public static String formatCreateTableSql(String dbName, String tableName, List<Pair<String, DataType>> columns, String separatorChar) {
        String cols = columns.stream()
                .filter(pair -> null != pair.getKey() && null != pair.getValue())
                .map(pair -> String.format("`%s` %s", pair.getKey(), pair.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        String sql = String.format(CREATE_SQL, dbName + DOT + tableName, cols, separatorChar);
        log.info("hive sql ：{}", sql);
        return sql;
    }

    /**
     * 组装建表语句
     *
     * @param filePath 入参
     * @param dbName 入参
     * @param tableName 入参
     * @return String
     */
    public static String formatLoadDataSql(String filePath, String dbName, String tableName) {
        String sql = String.format(LOAD_OVERWRITE_DATA, filePath, dbName + DOT + tableName);
        log.info("hive sql ：{}", sql);
        return sql;
    }

    /**
     * 组装建表语句
     *
     * @param filePath 入参
     * @param dbName 入参
     * @param tableName 入参
     * @param partitions 入参
     * @return String
     */
    public static String formatLoadOverWritePartitionDataSql(String filePath, String dbName, String tableName, Map<String, String> partitions) {
        String cols = partitions.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` = '%s'", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(COMMA));
        String sql = String.format(LOAD_OVERWRITE_PARTITION_DATA, filePath, dbName + DOT + tableName, cols);
        log.info("hive LoadPartitionDataSql ：{}", sql);
        return sql;
    }

    /**
     * 组装建表语句
     *
     * @param filePath 入参
     * @param dbName 入参
     * @param tableName 入参
     * @param partitions 入参
     * @return String
     */
    public static String formatLoadPartitionDataSql(String filePath, String dbName, String tableName, List<Pair<String, String>> partitions) {
        String cols = partitions.stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` = '%s'", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(COMMA));
        String sql = String.format(LOAD_PARTITION_DATA, filePath, dbName + DOT + tableName, cols);
        log.info("hive sql ：{}", sql);
        return sql;
    }

    /**
     * 组装查询语句
     *
     * @param headers 入参
     * @param dbName 入参
     * @param tableName 入参
     * @param whereCondition 入参
     * @param start 入参
     * @param size 入参
     * @return String
     */
    public static String formatQuerySql(List<String> headers, String dbName, String tableName, String whereCondition, Integer start, Integer size) {
        String collect = headers.stream().map(header -> String.format("`%s`", header)).collect(Collectors.joining(COMMA));
        if (!StringUtils.isEmpty(whereCondition)) {
            return String.format(TABLE_QUERY_SQL, collect, dbName + DOT + tableName, whereCondition, start, size);
        }
        return String.format(TABLE_QUERY_SQL_NO_WHERE, collect, dbName + DOT + tableName, start, size);
    }

    /**
     * 组装load语句 *
     *
     * @param filePath 入参
     * @param tableName 入参
     * @return String
     */
    public static String formatLoadFileSql(String filePath, String tableName) {
        return String.format(LOAD_FILE_SQL, filePath, tableName);
    }

    /**
     * 创建create + location语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @param filePath 入参
     * @return String
     */
    public static String formatCreateLocationSql(String dbName, String tableName, Map<String, DataType> columns, String separatorChar, String filePath) {
        String cols = columns.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        return String.format(CREAT_LOCATION_SQL, dbName + DOT + tableName, cols, separatorChar, filePath);
    }

    /**
     * 创建create + location语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @param filePath 入参
     * @return String
     */
    public static String formatAddLocationSql(String dbName, String tableName, Map<String, DataType> columns, String separatorChar, String filePath) {
        String cols = columns.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        return String.format(CREAT_LOCATION_SQL, dbName + DOT + tableName, cols, separatorChar, filePath);
    }

    /**
     * 创建create + location语句
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @param filePath 入参
     * @return String
     */
    public static String formatAddLocationAndPartitionSql(String dbName, String tableName, Map<String, DataType> columns, String separatorChar, String filePath) {
        String cols = columns.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(","));
        return String.format(CREAT_LOCATION_SQL, dbName + DOT + tableName, cols, separatorChar, filePath);
    }

    /**
     * 创建create + location语句 + partition
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @param separatorChar 入参
     * @param partition 入参
     * @param filePath 入参
     * @return String
     */
    public static String formatCreateLocationAndPartitionSql(String dbName, String tableName, Map<String, DataType> columns,
                                                             String separatorChar, Map<String, DataType> partition, String filePath) {
        String cols = columns.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(","));
        String partitions = partition.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(","));

        return String.format(CREAT_LOCATION_AND_PARTITION_SQL, dbName + DOT + tableName, cols, partitions, separatorChar, filePath);
    }

    /**
     * 删除 *
     *
     * @param dbName 入参
     * @param tableName 入参
     * @return String
     */
    public static String formatDeleteTableSql(String dbName, String tableName) {
        return String.format(TABLE_DELETE_SQL, dbName, tableName);
    }

    /**
     * 格式化创建视图sql
     *
     * @param dbName       数据库名
     * @param viewName     视图名称
     * @param queryColumns 查询字段 如a1,a2,a3
     * @param tableName    查询表名
     * @return String
     */
    public static String formatCreateViewSql(String dbName, String viewName, String queryColumns, String tableName) {
        viewName = dbName + DOT + viewName;
        tableName = dbName + DOT + tableName;
        return MessageFormat.format(CREATE_TABLE_VIEW, viewName, queryColumns, tableName);
    }

    /**
     * 校验表头是否合法
     *
     * @param headers 入参
     */
    public static void validHeaders(List<String> headers) {
        if (headers == null) {
            return;
        }

        Set<String> set = new HashSet<>();
        for (String header : headers) {
            if (!VALID_HEADER_REGEX.matcher(header).matches()) {
                if (!VALID_HEADER_REGEX.matcher(specialUnicode(header)).matches()) {
                    throw new InternalDataServiceException(String.format("请检查字段名，仅能有英文字母和数字，以及“_”下划线字符；并且首个字符只能为英文字母。错误字段名称为：%s", header));
                }
            }
            set.add(header);
        }
        final List<String> subtract = ListUtils.subtract(headers, set.stream().collect(Collectors.toList()));
        if (!subtract.isEmpty()) {
            throw new InternalDataServiceException(String.format("请检查字段名，不能有重复的字段名。错误字段名称为：%s", JSON.toJSONString(subtract)));
        }
    }

    /**
     * formatAddPartitions
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param partitions 入参
     * @return String
     */
    public static String formatAddPartitions(String dbName, String tableName, Map<String, DataType> partitions) {
        String pcols = partitions.entrySet().stream()
                .filter(entry -> null != entry.getKey() && null != entry.getValue())
                .map(entry -> String.format("`%s` %s", entry.getKey(), entry.getValue().getHiveDataType()))
                .collect(Collectors.joining(","));
        return String.format(ADD_PARTITIONS, dbName + DOT + tableName, pcols);
    }

    /**
     * specialUnicode
     *
     * @param str 入参
     * @return String
     */
    public static String specialUnicode(String str) {
        if (str.startsWith(PREFIX)) {
            str = str.replace(PREFIX, "");
        } else if (str.endsWith(PREFIX)) {
            str = str.replace(PREFIX, "");
        }
        return str;
    }

    /**
     * formatCountSql
     *
     * @param sampleDataName 入参
     * @param tableName 入参
     * @param whereCondition 入参
     * @return String
     */
    public static String formatCountSql(String sampleDataName, String tableName, String whereCondition) {
        if (!StringUtils.isEmpty(whereCondition)) {
            return String.format(COUNT_SQL, sampleDataName, tableName, whereCondition);
        }
        return String.format(COUNT_SQL_NO_WHERE, sampleDataName, tableName);
    }

    /**
     * formatAddTableColumnSql
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @return String
     */
    public static String formatAddTableColumnSql(String dbName, String tableName, List<Pair<String, DataType>> columns) {
        String fields = columns.stream().map(pair -> String.format("`%s` %s", pair.getKey(), pair.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        return String.format(ADD_TABLE_COLUMNS_SQL, dbName, tableName, fields);
    }

    /**
     * formatReplaceTableColumnSql
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param columns 入参
     * @return String
     */
    public static String formatReplaceTableColumnSql(String dbName, String tableName, List<Pair<String, DataType>> columns) {
        String fields = columns.stream().map(pair -> String.format("`%s` %s", pair.getKey(), pair.getValue().getHiveDataType()))
                .collect(Collectors.joining(COMMA));
        return String.format(REPLACE_TABLE_COLUMNS_SQL, dbName, tableName, fields);
    }

    /**
     * formatInsertSelectSql
     *
     * @param dbName 入参
     * @param tableName 入参
     * @param orgTableName 入参
     * @param columns 入参
     * @param partitions 入参
     * @return String
     */
    public static String formatInsertSelectSql(String dbName, String tableName, String orgTableName,
                                               List<Pair<String, DataType>> columns,
                                               List<Pair<String, String>> partitions) {
        String fields = columns.stream().map(pair -> String.format("`%s`", pair.getKey()))
                .collect(Collectors.joining(COMMA));
        String pcols = partitions.stream().map(pair -> String.format("`%s`='%s'", pair.getKey(), pair.getValue()))
                .collect(Collectors.joining(COMMA));
        String tableNameFormat = String.format("`%s`.`%s`", dbName, tableName);
        String orgTableNameFormat = String.format("`%s`.`%s`", dbName, orgTableName);

        return String.format(INSERT_SELECT_SQL, orgTableNameFormat, tableNameFormat, pcols, fields);
    }

    /**
     * formatDeletePartitionDataSql
     *
     * @param sampleDataName 入参
     * @param tableName 入参
     * @param partitionColumn 入参
     * @param partitionValue 入参
     * @return String
     */
    public static String formatDeletePartitionDataSql(String sampleDataName, String tableName, String partitionColumn, String partitionValue) {
        String tableNameFormat = String.format("`%s`.`%s`", sampleDataName, tableName);
        return String.format(DELETE_PARTITION_DATA_SQL, tableNameFormat, partitionColumn, partitionValue);
    }
}
