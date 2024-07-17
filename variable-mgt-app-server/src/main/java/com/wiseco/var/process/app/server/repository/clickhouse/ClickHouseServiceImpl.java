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
package com.wiseco.var.process.app.server.repository.clickhouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.enums.CKColumnTypeEnum;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.config.CallLogCondition;
import com.wiseco.var.process.app.server.repository.util.QueryTableSqlFormat;
import com.wiseco.var.process.app.server.repository.util.TableSqlFormat;
import com.wiseco.var.process.app.server.repository.util.UpdateTableSqlFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Asker.J
 * @since 2022/4/27
 */
@Slf4j
@Service
@CallLogCondition.OnClickhouseCallLogEnabled
public class ClickHouseServiceImpl implements ClickHouseService {
    @Resource(name = "clickHouseQueryDaoImpl")
    private ClickHouseQueryDao        clickHouseQueryDao;
    @Resource(name = "clickHouseModifyDaoImpl")
    private ClickHouseModifyDao       clickHouseModifyDao;

    @Value("${spring.clickhouse.cluster}")
    private boolean                   clickHouseCluster;

    private static final String       HAVE_CHINESE = "[\u4e00-\u9fa5]";

    private static final List<String> SPECIAL = new ArrayList<>();

    static {
        SPECIAL.add("\"");
        SPECIAL.add("\'");
        SPECIAL.add("!");
        SPECIAL.add("|");
        SPECIAL.add("--");
        SPECIAL.add("+");
        SPECIAL.add("@");
        SPECIAL.add("#");
        SPECIAL.add("$");
        SPECIAL.add("%");
        SPECIAL.add("^");
        SPECIAL.add("&");
        SPECIAL.add("*");
        SPECIAL.add("{");
        SPECIAL.add("}");
        SPECIAL.add("[");
        SPECIAL.add("]");
        SPECIAL.add("=");
        SPECIAL.add("\\");
        SPECIAL.add("\\\\");
    }

    @Override
    public List<Map<String, Object>> queryList(String tableName, List<String> columnNames, int from, int pageSize, List<String> orderByDesc,
                                               List<String> orderByAsc, Map<String, Object> filter) {
        List<Map<String, Object>> result = Lists.newArrayListWithCapacity(pageSize);
        //ck 查询sql
        String sql = null;
        if (from == 0 && pageSize == 0) {
            sql = TableSqlFormat.createQuerySqlSimple(columnNames, tableName);
        } else {
            sql = TableSqlFormat.createQuerySql(columnNames, tableName, from, pageSize, orderByDesc, orderByAsc, filter);
        }
        log.info("查询sql：{}", sql);
        //ck 查数据
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
                    columnNames.forEach(h -> {
                        try {
                            map.put(h, r.getObject(h));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    result.add(map);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public int queryCount(String tableName) {
        //查询总条数
        final int[] totalCount = new int[1];
        String sql = QueryTableSqlFormat.count(tableName);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                r.next();
                totalCount[0] = r.getInt(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return totalCount[0];
    }

    @Override
    public int queryCount(String tableName, @Nullable String whereCondition) {
        //查询总条数
        final int[] totalCount = new int[1];
        String sql = QueryTableSqlFormat.count(tableName, whereCondition);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                r.next();
                totalCount[0] = r.getInt(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return totalCount[0];
    }

    @Override
    public long queryCountBySql(String countSql) {
        //查询总条数
        final long[] totalCount = new long[1];
        try {
            clickHouseQueryDao.execute(countSql, r -> {
                r.next();
                totalCount[0] = r.getLong(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", countSql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return totalCount[0];
    }

    @Override
    public double queryMin(String tableName, String... columnNames) {
        //查询总条数
        final double[] min = new double[1];
        String sql = QueryTableSqlFormat.minColumn(tableName, columnNames[0]);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                r.next();
                min[0] = r.getDouble(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return min[0];
    }

    @Override
    public double queryMax(String tableName, String columnName) {
        //查询总条数
        final double[] max = new double[1];
        String sql = QueryTableSqlFormat.maxColumn(tableName, columnName);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                r.next();
                max[0] = r.getDouble(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return max[0];
    }

    @Override
    public <T> List<T> queryDistinct(String tableName, String... columnNames) {
        //查询总条数
        final List<T> list = Lists.newArrayList();
        String sql = QueryTableSqlFormat.distinctColumn(tableName, columnNames[0]);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    list.add((T) r.getObject(1));
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public <T> List<T> queryDistinctIn(String tableName, String columnName, List ins) {
        //查询总条数
        final List<T> list = Lists.newArrayList();
        String sql = QueryTableSqlFormat.distinctColumnIn(tableName, columnName, ins);
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    list.add((T) r.getObject(1));
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> queryList(String sql, List<String> columnNames) {
        log.info("查询sql：{}", sql);
        //查询总条数
        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
                    for (String h : columnNames) {
                        map.put(h, r.getObject(h));
                    }
                    result.add(map);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryList(String sql, List<String> columnNames, Map<String, String> mapping) {
        //查询总条数
        List<Map<String, Object>> result = Lists.newArrayList();
        boolean needReplace = !CollectionUtils.isEmpty(mapping);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
                    for (String h : columnNames) {
                        if (needReplace) {
                            map.put(mapping.get(h), r.getObject(h));
                        } else {
                            map.put(h, r.getObject(h));
                        }
                    }
                    result.add(map);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public Map<String, Object> queryOne(String sql, List<String> columnNames, Map<String, String> mapping) {
        //查询总条数
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
        try {
            clickHouseQueryDao.execute(sql, r -> {
                boolean next = r.next();
                if (next) {
                    for (String h : columnNames) {
                        map.put(mapping.get(h), r.getObject(h));
                    }
                }
            });
            return map;
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
    }

    @Override
    public List<String> queryTableColumns(String tableName) {
        String sql = QueryTableSqlFormat.getTableColumnNames(tableName);
        List<String> resultNames = new ArrayList<>();
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    resultNames.add(r.getString("name"));
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return resultNames;
    }

    @Override
    public List<List<Object>> queryListByIds(String rejectDatasourceTableName, List<String> columnNames, List<Integer> copyRowNumList) {
        List<List<Object>> result = new ArrayList<>();
        String query = "select * from %s where %s in (%s)";
        log.info("查询sql：{}", query);
        final List<List<Integer>> lists = splitList(copyRowNumList, 1000);
        for (List<Integer> list : lists) {
            final String sql = String.format(query, rejectDatasourceTableName, TableSqlFormat.INCREMENT_ID, String.join(",", list.stream().map(String::valueOf).collect(Collectors.toSet())));
            try {
                clickHouseQueryDao.execute(sql, r -> {
                    while (r.next()) {
                        List<Object> row = new ArrayList<>();
                        columnNames.forEach(h -> {
                            try {
                                row.add(r.getObject(h));
                            } catch (SQLException e) {
                            }
                        });
                        result.add(row);
                    }
                });
            } catch (SQLException e) {
                log.error("sql: {}, error：{}", sql, e.getMessage());
                throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
            }
        }
        return result;
    }

    @Override
    public void createTable(String tableName, List<String> columns, Map<String, CKColumnTypeEnum> columnTypes, String engineName) {
        try {
            StringBuilder sb = new StringBuilder();
            columns.forEach(key -> {
                sb.append(key);
                sb.append(" ");
                sb.append(columnTypes.get(key).getType());
                sb.append(",");
            });
            sb.deleteCharAt(sb.length() - 1);
            String tableSql;
            if (TableSqlFormat.ENGINE_NAME.equals(engineName)) {
                tableSql = TableSqlFormat.buildCreateJoinTableSql(tableName, sb.toString());
            } else {
                tableSql = TableSqlFormat.buildCreateTableSql(tableName, sb.toString(), clickHouseCluster);
            }
            log.info("建表sql：{}", tableSql);
            clickHouseModifyDao.execute(tableSql);
            log.info("--创建表：{}", tableName);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new InternalDataServiceException("创建数据表失败，请检查建表字段名称，必须是英文开头的英文和数字（包含下划线）组合，且不能重复");
        }
    }

    @Override
    public void createTable(String createTableSql) throws SQLException {
        log.info("创建ck表 SQL:{}", createTableSql);
        clickHouseModifyDao.execute(createTableSql);
    }

    @Override
    public void insert(String insertSql) throws SQLException {
        log.info("插入单条数据sql:\n{}", insertSql);
        clickHouseModifyDao.execute(insertSql);
    }

    /**
     * 创建可以为空的表
     * @param tableName 表名
     * @param columns 列
     * @param columnTypes 列的类型
     * @param engineName 引擎名称
     * @param idName      自建的id字段
     */
    @Override
    public void createNullableTable(String tableName, List<String> columns, Map<String, CKColumnTypeEnum> columnTypes, String engineName, String idName) {
        try {
            StringBuilder sb = new StringBuilder();
            columns.forEach(key -> {
                if (StringUtils.isEmpty(key.trim())) {
                    throw new InternalDataServiceException("表头字段存在空值");
                }
                sb.append(key);
                if (columnTypes.get(key) == CKColumnTypeEnum.STRING) {
                    sb.append(" Nullable(");
                    sb.append(columnTypes.get(key).getType());
                    sb.append("),");
                } else {
                    sb.append(" ");
                    sb.append(columnTypes.get(key).getType());
                    sb.append(" DEFAULT -99000800,");
                }
            });
            if (!columns.contains(idName) && !StringUtils.isEmpty(idName)) {
                sb.append(idName).append(" ").append(CKColumnTypeEnum.INT.getType()).append(",");
            }
            String columnSql = sb.deleteCharAt(sb.length() - 1).toString();
            for (String s : SPECIAL) {
                if (columnSql.contains(s)) {
                    throw new InternalDataServiceException("创建数据表失败，请检查建表字段:" + s + "，必须是英文开头的英文和数字（包含下划线）组合，且不能重复");
                }
            }
            String tableSql;
            if (TableSqlFormat.ENGINE_NAME.equals(engineName)) {
                tableSql = TableSqlFormat.buildCreateJoinTableSql(tableName, sb.toString());
            } else {
                tableSql = TableSqlFormat.buildCreateTableSql(tableName, sb.toString(), clickHouseCluster);
            }
            log.info("建表sql：{}", tableSql);
            clickHouseModifyDao.execute(tableSql);
            log.info("--创建表：{}", tableName);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new InternalDataServiceException("创建数据表失败，请检查建表字段名称，必须是英文开头的英文和数字（包含下划线）组合，且不能重复");
        }
    }

    /**
     * *
     *
     * @param tableName rc
     * @param columns rc
     * @param engineName rc
     * @param idName     自建的id字段
     */
    @Override
    public void createStringNullableTable(String tableName, List<String> columns, String engineName, String idName) {
        //字段检查
        validColumns(columns);
        try {
            StringBuilder sb = new StringBuilder();
            for (String key : columns) {
                if (StringUtils.isEmpty(key.trim())) {
                    throw new InternalDataServiceException("表头字段存在空值");
                }
                //                if (key.contains("#")) {
                //                    throw new BizException(key + " 表头存在特殊字符 # ");
                //                }
                sb.append("`" + key + "`").append(" Nullable(").append(CKColumnTypeEnum.STRING.getType()).append("),");
            }
            if (!columns.contains(idName) && !StringUtils.isEmpty(idName)) {
                sb.append(idName).append(" ").append(CKColumnTypeEnum.INT.getType()).append(",");
            }
            String columnSql = sb.deleteCharAt(sb.length() - 1).toString();
            for (String s : SPECIAL) {
                if (columnSql.contains(s)) {
                    throw new InternalDataServiceException("创建数据表失败，请检查建表字段名称，必须是英文开头的英文和数字（包含下划线）组合，且不能重复");
                }
            }
            String tableSql;
            if (TableSqlFormat.ENGINE_NAME.equals(engineName)) {
                tableSql = TableSqlFormat.buildCreateJoinTableSql(tableName, columnSql);
            } else {
                tableSql = TableSqlFormat.buildCreateTableSql(tableName, columnSql, clickHouseCluster);
            }

            log.info("建表sql：{}", tableSql);
            clickHouseModifyDao.execute(tableSql);
            log.info("--创建表：{}", tableName);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new InternalDataServiceException("创建数据表失败，请检查建表字段名称，必须是英文开头的英文和数字（包含下划线）组合，且不能重复");
        }
    }

    /**
     * 字段校验
     *
     * @param columns
     */
    private void validColumns(List<String> columns) {
        Assert.notEmpty(columns, "字段空！");
        List<String> numberHeaders = Lists.newArrayListWithCapacity(MagicNumbers.TEN);
        List<String> chineseHeaders = Lists.newArrayListWithCapacity(MagicNumbers.TEN);

        for (String col : columns) {
            if (NumberUtils.isNumber(col)) {
                numberHeaders.add(col);
            }
            Pattern p = Pattern.compile(HAVE_CHINESE);
            Matcher m = p.matcher(col);
            if (m.find()) {
                chineseHeaders.add(col);
            }
        }
        StringBuilder err = new StringBuilder();
        if (!CollectionUtils.isEmpty(numberHeaders)) {
            err.append(String.join("、", numberHeaders)).append("为纯数字，不能作为表头;");
        }
        if (!CollectionUtils.isEmpty(chineseHeaders)) {
            err.append(String.join("、", chineseHeaders)).append("包含中文，不能作为表头;");
        }
        Assert.isTrue(StringUtils.isEmpty(err.toString()), err.toString());
    }

    @Override
    public void copyTableToNewTable(String fromTableName, String toTableName) {
        try {
            //先复制原表结构、
            String copyTableSql = TableSqlFormat.createTableAsSql(fromTableName, toTableName);
            clickHouseModifyDao.execute(copyTableSql);
        } catch (SQLException e) {
            log.error("copy table error :" + e.getMessage());
            throw new InternalDataServiceException("创建数据表失败");
        }
    }

    @Override
    public void insertByMysql(String ckTableName, String ids, String url, String database, String tableName, String username, String password) {
        String sql = TableSqlFormat.insertMysql(ckTableName, ids, url, database, tableName, username, password);
        log.info("查询sql：{}", sql);
        try {
            clickHouseModifyDao.execute(sql);
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteTable(String ckTableName, Date date) {
        try {
            if (clickHouseQueryDao.existTable(ckTableName) && clickHouseQueryDao.existData(ckTableName)) {
                throw new InternalDataServiceException("要删除的表中存在数据");
            }
        } catch (SQLException e) {
            log.error("clickhouse error：{}", e.getMessage());
            throw new InternalDataServiceException(String.format("clickhouse error: %s", e.getMessage()));
        }
        forceDeleteTable(ckTableName, date);
    }

    @Override
    public void forceDeleteTable(String tableName, Date date) {
        String sql = UpdateTableSqlFormat.getDeleteTableSql(tableName, clickHouseCluster);
        log.info("查询sql：{}", sql);
        try {
            clickHouseModifyDao.execute(sql);
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }

    }

    @Override
    public void deleteData(String sql) {
        try {
            clickHouseModifyDao.execute(sql);
        } catch (SQLException e) {
            log.error("删除数据异常,sql:{}", sql, e);
            throw new InternalDataServiceException(String.format("删除数据异常: %s", e.getMessage()));
        }
    }

    @Override
    public void forceDeleteTable(String tableName) {
        String sql = UpdateTableSqlFormat.getDeleteTableSql(tableName, clickHouseCluster);
        log.info("查询sql：{}", sql);
        try {
            clickHouseModifyDao.execute(sql);
        } catch (SQLException e) {
            log.error(String.format("sql: {}, error：{}", sql, e.getMessage()), e);
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }

    }

    /**
     * @param tableName 表名
     * @param columnNames 列名
     * @param data 数据
     * @param <T> 泛型
     */
    @Override
    public <T> void batchInsert(String tableName, List<String> columnNames, List<List<T>> data) {
        final String sql = UpdateTableSqlFormat.buildInsertAllColumnsSql(tableName, columnNames);
        log.info("查询sql：{}", sql);
        try {
            clickHouseModifyDao.batchExecute(sql, columnNames.size(), data);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new InternalDataServiceException("请检查数据格式是否正常");
        }
    }

    @Override
    public Map<String, Object> queryById(String tableName, List<String> columnNames, int headerLine) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(columnNames.size());
        try {
            String sql = QueryTableSqlFormat.queryById(tableName, columnNames, headerLine);
            log.info("查询sql：{}", sql);
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    columnNames.forEach(h -> {
                        try {
                            result.put(h, r.getObject(h));
                        } catch (SQLException e) {
                        }
                    });
                }
            });
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new InternalDataServiceException("clickhouse查询数据库失败");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryListWithAlias(String tableName, List<String> columnNames, List<String> fields, String whereCondition, int from, int size) {

        String sql = QueryTableSqlFormat.queryListWithAlias(tableName, fields, whereCondition, from, size);
        log.info("查询sql：{}", sql);
        //查询总条数
        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
                    for (String h : columnNames) {
                        map.put(h, r.getObject(h));
                    }
                    result.add(map);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return result;
    }

    /**
     * 获取多行数据
     * @param list list集合
     * @param size 大小
     * @return 多行数据
     * @param <T> 泛型
     */
    public static <T> List<List<T>> splitList(List<T> list, int size) {
        final List<List<T>> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i = i + size) {
            List subList;
            if (i + size >= list.size()) {
                subList = list.subList(i, list.size());
            } else {
                subList = list.subList(i, i + size);
            }
            ret.add(com.google.common.collect.Lists.newArrayList(subList));
        }
        return ret;
    }

    @Override
    public Map<String, String> describeTable(String ckTableName) {
        try {
            return clickHouseQueryDao.describeTable(ckTableName);
        } catch (SQLException e) {
            log.error("查询[" + ckTableName + "]表结构异常：" + e.getMessage());
            throw new InternalDataServiceException("查询数据表失败");
        }
    }

    @Override
    public List<String> tableColumns(String ckTableName) {
        return Lists.newArrayList(describeTable(ckTableName).keySet());
    }

    @Override
    public Boolean isTableExist(String ckTableName) {
        try {
            return clickHouseQueryDao.existTable(ckTableName);
        } catch (SQLException e) {
            log.error("查询[" + ckTableName + "]表结构异常：" + e.getMessage());
            throw new InternalDataServiceException("查询数据表失败");
        }
    }

    private <T> int getPartitionSize(List<List<T>> data) {
        return (data.size() + 1) / MagicNumbers.TWO;
    }

    /**
     * 行分页，列分页查询
     * @param tableName 表名
     * @param columnNames 列名
     * @param from 指针的开始
     * @param pageSize 页大小
     * @param orderByDesc 降序排序字段
     * @param orderByAsc 升序排序字段
     * @return 多行数据
     */
    @Override
    public List<Map<String, Object>> queryPageList(String tableName, List<String> columnNames, int from, int pageSize, List<String> orderByDesc,
                                                   List<String> orderByAsc, String whereContition) {
        List<Map<String, Object>> result = Lists.newArrayListWithCapacity(pageSize);
        String sql = null;
        if (from == 0 && pageSize == 0) {
            sql = TableSqlFormat.createQuerySqlSimple(columnNames, tableName);
        } else {
            sql = TableSqlFormat.createQueryFilerSql(columnNames, tableName, from, pageSize, orderByDesc, orderByAsc, whereContition);
        }
        log.info("查询sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
                while (r.next()) {
                    Map<String, Object> map = Maps.newHashMapWithExpectedSize(columnNames.size());
                    columnNames.forEach(h -> {
                        try {
                            Object value = r.getObject(h);
                            if (value instanceof Date) {
                                String formattedDate = DateUtil.parseDateToStr((Date) value, MagicStrings.DATE_TIME_FORMAT);
                                map.put(h, formattedDate);
                            } else {
                                map.put(h, String.valueOf(value));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    result.add(map);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return result;
    }
}
