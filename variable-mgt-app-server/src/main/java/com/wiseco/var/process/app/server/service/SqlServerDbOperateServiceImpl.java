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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.config.CallLogCondition;
import com.wiseco.var.process.app.server.controller.vo.FieldSqlRespVO;
import com.wiseco.var.process.app.server.controller.vo.TableJoinRespVO;
import com.wiseco.var.process.app.server.controller.vo.input.DataBaseFieldMapDto;
import com.wiseco.var.process.app.server.repository.base.ResultSetConsumer;
import com.wiseco.var.process.app.server.repository.util.QueryTableSqlFormat;
import com.wiseco.var.process.app.server.repository.util.SqlHelperUtil;
import com.wiseco.var.process.app.server.repository.util.TableSqlFormat;
import com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("sqlServerDbOperateServiceImpl")
@CallLogCondition.OnSqlServerCallLogEnabled
public class SqlServerDbOperateServiceImpl implements DbOperateService {
    public static final String PRIMARY_KEY_SQL = "CONSTRAINT PK_%s PRIMARY KEY (%s)";
    private static final String COLUMN_DATA_DISTINCT = "SELECT DISTINCT %s FROM %s where %s";

    private static final String QUERY_COUNT = "SELECT COUNT(*) from %s where %s";

    private static final String MYSQL_COUNT_MAIN_SQL = "select count(1) nums from {0} {1} {2}";

    private static final String MYSQL_PAGE_MAIN_SQL = "select {0} from {1} {2} {3} limit {4},{5}";

    /**
     * 动态表前缀
     */
    private static final String DYNAMIC_TABLE_PREFIX = "var_process_manifest_";

    private static final String VAR_PROCESS_MANIFEST_HEADER = "var_process_manifest_header";

    /**
     * mysql关键字转义符
     */
    private static final String TRANSLATEMARK = "`";
    public static final String CREATE_INDEX_SQL = "CREATE INDEX {0}_idxx_{1} ON {0} (\"{1}\" ASC);";

    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;

    /**
     * 配置文件配置动态数据源
     */
    @Autowired
    @Qualifier("internalDataSource")
    private DataSource internalDataSource;

    private static final HashMap<String, String> DATA_TYPE_MAP = new HashMap<>();

    static {
        DATA_TYPE_MAP.put("int", "int");
        DATA_TYPE_MAP.put("string", "nvarchar(255)");
        DATA_TYPE_MAP.put("double", "float");
        DATA_TYPE_MAP.put("date", "date");
        DATA_TYPE_MAP.put("datetime", "datetime");
        DATA_TYPE_MAP.put("boolean", "tinyint");
        DATA_TYPE_MAP.put("long", "bigint");
        DATA_TYPE_MAP.put("text", "nvarchar(max)");
        DATA_TYPE_MAP.put("longtext", "nvarchar(max)");

    }


    /**
     * 获取数据数量
     *
     * @param tableJoinList  表关联对象
     * @param whereCondition where条件
     * @return count
     */
    @Override
    public Integer countOfJoin(List<TableJoinDto> tableJoinList, String whereCondition) {
        TableJoinRespVO tableJoinResult = SqlHelperUtil.tableJoinListFormat(tableJoinList);
        String sql = MessageFormat.format(MYSQL_COUNT_MAIN_SQL, tableJoinResult.getMainTableName(), tableJoinResult.getJoinTableAndOnCondition(), whereCondition);
        return internalJdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public List<Map<String, Object>> queryForListOfJoin(List<TableJoinDto> tableJoinJson, String whereCondition, int offset, int size) throws SQLException {
        //查询全部表的字段
        List<DataBaseFieldMapDto> fieldMapList = tableColumListOfJoin(tableJoinJson);

        //组装查询sql
        TableJoinRespVO tableJoinResult = SqlHelperUtil.tableJoinListFormat(tableJoinJson);
        FieldSqlRespVO fieldSqlResult = SqlHelperUtil.fieldMappingToFieldSql(fieldMapList, null, tableJoinJson.size() == 1, TRANSLATEMARK);
        String querySql = MessageFormat.format(MYSQL_PAGE_MAIN_SQL, fieldSqlResult.getFieldSql(), tableJoinResult.getMainTableName(),
                tableJoinResult.getJoinTableAndOnCondition(), whereCondition, String.valueOf(offset), String.valueOf(size));

        //查询并格式化时间类型的值
        List<Map<String, Object>> dataList = internalJdbcTemplate.queryForList(querySql);
        for (Map<String, Object> data : dataList) {
            data.forEach((key, value) -> {
                if (value instanceof java.sql.Date) {
                    data.put(key, DateUtil.parseDateToStr((java.sql.Date) value, "yyyy-MM-dd"));
                } else if (value instanceof Timestamp) {
                    data.put(key, DateUtil.parseDateToStr((Timestamp) value, "yyyy-MM-dd hh:mm:ss"));
                }
            });
        }
        return dataList;
    }


    @Override
    public List<DataBaseFieldMapDto> tableColumListOfJoin(List<TableJoinDto> tableJoinList) throws SQLException {
        List<DataBaseFieldMapDto> mapParamList = new ArrayList<>();
        //遍历表查询表字段
        for (TableJoinDto tableJoin : tableJoinList) {
            List<String> columList = tableColumList(tableJoin.getTableName());
            for (String colum : columList) {
                DataBaseFieldMapDto baseFieldMapDto = DataBaseFieldMapDto.builder().orgFieldName(colum).orgTableName(tableJoin.getAlias()).build();
                mapParamList.add(baseFieldMapDto);
            }
        }
        return mapParamList;
    }

    @Override
    public List<String> tableColumList(String tableName) throws SQLException {
        ArrayList<String> columNames = new ArrayList<>();
        Connection connection = null;
        try {
            connection = internalDataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            // 获取表的列信息
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                columNames.add(columns.getString("COLUMN_NAME"));
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return columNames;
    }

    /**
     * 执行sql具体实现
     *
     * @param sql              sql语句
     * @param callbackFunction 回调函数
     * @throws SQLException jdbc异常
     */
    private void execute(String sql, ResultSetConsumer callbackFunction) throws SQLException {
        log.debug("执行SQL查询：{}", sql);
        try (Connection connection = internalDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            callbackFunction.accept(resultSet);
        }
    }

    @Override
    public boolean isTableExist(String tableName) {
        MutableBoolean ret = new MutableBoolean(false);
        String sql = QueryTableSqlFormat.getExistTableSql(tableName);
        log.debug("执行SQL查询：{}", sql);
        try {
            execute(sql, rs -> ret.setValue(rs.next()));
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("查询数据表发生异常，sql error: %s", e.getMessage()));
        }
        return ret.isTrue();
    }

    @Override
    public Map<String, String> describeTable(String tableName) {
        Map<String, String> tableColumn = Maps.newLinkedHashMap();
        String sql = QueryTableSqlFormat.describeTable(tableName).replace("TABLE", "");
        try {
            execute(sql, rs -> {
                while (rs.next()) {
                    tableColumn.put(rs.getString(MagicNumbers.ONE), rs.getString(MagicNumbers.TWO));
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("查询数据表结构发生异常，sql error: %s", e.getMessage()));
        }
        return tableColumn;
    }

    @Override
    public int queryCount(String tableName, @Nullable String whereCondition) {
        final int[] count = new int[1];
        String sql = QueryTableSqlFormat.count(tableName, whereCondition);
        sql = sql.replace("`","");
        log.info("查询sql：{}", sql);
        try {
            execute(sql, r -> {
                r.next();
                count[0] = r.getInt(1);
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("查询数据表记录数发生异常，sql error: %s", e.getMessage()));
        }
        return count[0];
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType) {
        return internalJdbcTemplate.queryForList(sql, elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) {
        List<Map<String, Object>> list = internalJdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            if (map.containsKey("Field")) {
                map.put("name", map.get("Field"));
                map.remove("Field");
                map.put("type", map.get("Type"));
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> queryForListOfDynamicTable(String tableName, String sql) {
        //切割原sql语句并重新装配，符合sqlServer规范 select * from table limt 1 offset 2 --> select * from table order by external_serial_no offset 2 rows fetch next 1 rows only
        String newSql = sql.substring(0,sql.lastIndexOf("limit"));
        //分割字符串，获取limit和offset的数据
        String[] sizeAndOffset = sql.substring(sql.lastIndexOf("limit")).split(" ");
        int size = Integer.parseInt(sizeAndOffset[MagicNumbers.ONE]);
        int offset = Integer.parseInt(sizeAndOffset[MagicNumbers.THREE]);
        String appendSql = " order by increment_id desc offset %d rows fetch next %d rows only";
        appendSql = String.format(appendSql, offset, size);
        //拼接sql
        newSql += appendSql;

        List<Map<String, Object>> list = internalJdbcTemplate.queryForList(newSql);
        for (Map<String, Object> map : list) {
            if (map.containsKey("Field")) {
                map.put("name", map.get("Field"));
                map.remove("Field");
                map.put("type", map.get("Type"));
            }
        }
        return list;
    }


    @Override
    public List<Map<String, Object>> queryForList(String tableName, List<String> colum, String condition, LinkedHashMap<String, String> orderColum, Integer from, Integer size) {
        String sql = getQuerySql(tableName, colum, condition, orderColum, from, size);
        log.info("查询sql:{}", sql);

        List<Map<String, Object>> list = internalJdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            if (map.containsKey("Field")) {
                map.put("name", map.get("Field"));
                map.remove("Field");
                map.put("type", map.get("Type"));
            }
        }
        return list;
    }

    @Override
    public <T> IPage<T> queryPage(String tableName, IPage<T> page, List<String> colum, String condition, Object[] args, Class<T> clazz) {
        String countSql = String.format(QUERY_COUNT, tableName, condition);
        log.info("查询sql:{}", countSql);
        Long totalCount = internalJdbcTemplate.queryForObject(countSql, args, Long.class);
        if (totalCount != null) {
            page.setTotal(totalCount);
        } else {
            page.setTotal(0);
            page.setRecords(Collections.emptyList());
            return page;
        }

        long from = page.getSize() * (page.getCurrent() - 1);
        long size = page.getSize();
        String querySql = getQuerySql(tableName, colum, condition, null, (int) from, (int) size);
        log.info("查询sql:{}", querySql);

        List<T> resultList = internalJdbcTemplate.query(querySql, args, new BeanPropertyRowMapper<>(clazz));
        page.setRecords(resultList);
        return page;
    }


    private String getQuerySql(String tableName, List<String> colum, String condition, LinkedHashMap<String, String> orderColum, Integer from, Integer size) {
        StringBuilder sql = new StringBuilder(TableSqlFormat.SELECT);
        if (CollectionUtils.isEmpty(colum)) {
            sql.append(TableSqlFormat.ALL_COLUM);
        } else {
            colum.forEach(item -> sql.append(item).append(","));
            sql.deleteCharAt(sql.length() - 1);
        }

        sql.append(TableSqlFormat.FROM).append(tableName);

        if (!StringUtils.isEmpty(condition)) {
            sql.append(TableSqlFormat.WHERE).append(condition);
        }

        if (CollectionUtils.isEmpty(orderColum)) {
            if (isDynamicTable(tableName)) {
                orderColum = Maps.newLinkedHashMap();
                orderColum.put("increment_id", "DESC");
            } else {
                orderColum = Maps.newLinkedHashMap();
                orderColum.put("id", "DESC");
            }
        }


        sql.append(TableSqlFormat.ORDER_BY);
        orderColum.forEach((key, value) -> {
            sql.append(key).append(" ").append(value).append(",");
        });
        sql.deleteCharAt(sql.length() - 1);


        if (from != null) {
            sql.append(" ").append(TableSqlFormat.OFFSET).append(from).append(" ").append(TableSqlFormat.ROWS);
            if (size != null) {
                sql.append(TableSqlFormat.FETCH_NEXT).append(size).append(" ").append(TableSqlFormat.ROWS)
                        .append(TableSqlFormat.ONLY);
            }
        }
        return sql.toString();
    }

    /**
     * 创建表
     *
     * @param tableName  tableName
     * @param columMap   字段列map
     * @param indexColum 索引列map
     * @param primaryKey 主键
     */
    @Override
    public void createTable(String tableName, LinkedHashMap<String, String> columMap, List<String> indexColum, String primaryKey) {
        if (CollectionUtils.isEmpty(columMap)) {
            throw new InternalDataServiceException("列不能为空");
        }

        // 先删除旧表
        String dropTableSql = MessageFormat.format("drop table if exists {0}",tableName);
        internalJdbcTemplate.execute(dropTableSql);

        StringBuilder tableSql = new StringBuilder();
        tableSql.append("CREATE TABLE ").append(tableName).append("(");
        columMap.forEach((key, value) -> tableSql.append(String.format("[%s]",key)).append(" ").append(DATA_TYPE_MAP.get(value)).append(","));

        if (!StringUtils.isEmpty(primaryKey)) {
            tableSql.append(String.format(PRIMARY_KEY_SQL,tableName,primaryKey));
        }
        tableSql.append(")");
        String sqlString = tableSql.toString();
        log.info("创建表 tableSql:{}", sqlString);
        internalJdbcTemplate.execute(sqlString);

        //创建索引
        indexColum.forEach(col -> {
            internalJdbcTemplate.execute(MessageFormat.format(CREATE_INDEX_SQL,tableName, col));
        });
    }


    @Override
    public void simpleDeleteRecord(String tableName, String colum, String value) {
        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append("delete from ").append(tableName).append(" WHERE ").append(colum).append(" = '").append(value).append("'");
        String sqlString = deleteSql.toString();
        log.info("删除记录 sql:{}", sqlString);
        internalJdbcTemplate.execute(sqlString);
    }

    @Override
    public void deleteByIn(String tableName, String conditionColumn, List<String> conditionList) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE  FROM ").append(tableName).append(" where ").append(conditionColumn).append(" IN (");
        for (String condition : conditionList) {
            sql.append("'").append(condition).append("'").append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        internalJdbcTemplate.execute(sql.toString());
    }

    @Override
    public void batchInsert(String tableName, List<String> columList, List<List<String>> valueList) {
        if (CollectionUtils.isEmpty(valueList)) {
            return;
        }
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO ").append(tableName);

        if (!CollectionUtils.isEmpty(columList)) {
            insertSql.append("(");
            for (String s : columList) {
                insertSql.append(s).append(",");
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.append(")");
        }

        insertSql.append(" VALUES ");

        for (List<String> rowData : valueList) {
            insertSql.append("(");
            for (String value : rowData) {
                if (value == null) {
                    insertSql.append("null");
                } else {
                    String replaceValue = value.replace("'", "''");
                    insertSql.append("'").append(replaceValue).append("'");
                }
                insertSql.append(",");
            }
            insertSql.deleteCharAt(insertSql.length() - 1);
            insertSql.append("), ");
        }

        insertSql.setLength(insertSql.length() - MagicNumbers.TWO);
        String sqlString = insertSql.toString();
        log.info("插入数据 sql:{}", sqlString);
        internalJdbcTemplate.execute(sqlString);
    }

    @Override
    public long queryForLong(String sql, Object[] args, Class<?> clazz) {
        final Long aLong = internalJdbcTemplate.queryForObject(sql, args, Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public long queryForLong(String sql, Class<?> clazz) {
        final Long aLong = internalJdbcTemplate.queryForObject(sql, Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public double queryForDouble(String sql) {
        final Double aDouble = internalJdbcTemplate.queryForObject(sql, Double.class);
        return aDouble == null ? 0.00 : aDouble;
    }

    @Override
    public float queryForFloat(String sql, Object[] args) {
        final Float aFloat = internalJdbcTemplate.queryForObject(sql, args, Float.class);
        return aFloat == null ? 0.00f : aFloat;
    }

    @Override
    public List<Object> getColumnDataDistinct(String tableName, String column, String whereCondition) {
        String sql = String.format(COLUMN_DATA_DISTINCT, column, tableName, whereCondition);
        List<Object> resultNames = new ArrayList<>();
        log.info("去重sql：{}", sql);
        try {
            execute(sql, r -> {
                while (r.next()) {
                    resultNames.add(r.getObject(column));
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return resultNames;
    }

    @Override
    public List<Object> getColumnDataDistinct(String tableName, String column, String whereCondition, Long manifestId) {
        final String varNamesSql = String.format("select variable_code from var_process_manifest_header where manifest_id=%s order by order_no", manifestId);
        final List<String> columns = queryForList(varNamesSql, String.class);
        final int index = columns.indexOf(column);
        final List<Object> columnData = getColumnData(tableName, index, whereCondition);
        final HashSet<Object> set = new HashSet<>(columnData);
        return Arrays.asList(set.toArray());
    }

    @Override
    public String getDataType(String prototype) {
        String dataType = null;
        for (Map.Entry<String, String> entry : DATA_TYPE_MAP.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(prototype)) {
                dataType = entry.getKey();
                break;
            }
        }
        return dataType;
    }

    @Override
    public List<Object> getColumnData(String tableName, int varIndex, String whereCondition) {
        String sql = String.format("SELECT variables FROM %s where %s", tableName, whereCondition);
        List<Object> resultNames = new ArrayList<>();
        log.info("查询sql：{}", sql);
        try {
            execute(sql, r -> {
                while (r.next()) {
                    final String row = r.getString(1);
                    final String[] split = row.split(CommonConstant.SEPARATOR);
                    resultNames.add(split[varIndex]);
                }
            });
        } catch (SQLException e) {
            log.error("sql: {}, error：{}", sql, e.getMessage());
            throw new InternalDataServiceException(String.format("sql error: %s", e.getMessage()));
        }
        return resultNames;
    }


    @Override
    public void execute(String sql) {
        internalJdbcTemplate.execute(sql);
    }

    @Override
    public void update(String sql, Object... args) {
        internalJdbcTemplate.update(sql, args);
    }

    /**
     * 查询单个
     *
     * @param sql sql语句
     * @return Object
     */
    @Override
    public Map<String, Object> queryForMap(String sql) {
        return internalJdbcTemplate.queryForMap(sql);
    }

    /**
     * 判断是否是动态表
     *
     * @param tableName 表名
     * @return java.lang.Object
     */
    private  boolean isDynamicTable(String tableName) {
        return tableName.startsWith(DYNAMIC_TABLE_PREFIX) && !VAR_PROCESS_MANIFEST_HEADER.equals(tableName);
    }

}
