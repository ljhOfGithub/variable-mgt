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
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.config.CallLogCondition;
import com.wiseco.var.process.app.server.repository.clickhouse.ClickHouseQueryDao;
import com.wiseco.var.process.app.server.repository.util.QueryTableSqlFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("ckDbOperateServiceImpl")
@CallLogCondition.OnClickhouseCallLogEnabled
public class CkDbOperateServiceImpl implements DbOperateService {

    @Autowired
    @Qualifier("clickHouseJdbcTemplate")
    private JdbcTemplate clickHouseJdbcTemplate;

    @Value("${spring.clickhouse.cluster}")
    private boolean clickHouseCluster;

    @Resource(name = "clickHouseQueryDaoImpl")
    private ClickHouseQueryDao clickHouseQueryDao;

    private static final String COLUMN_DATA_DISTINCT = "SELECT DISTINCT %s FROM %s where %s";

    private static final HashMap<String, String> DATE_TYPE_MAP = new HashMap<>();


    static {
        DATE_TYPE_MAP.put("int", "Int32");
        DATE_TYPE_MAP.put("string", "String");
        DATE_TYPE_MAP.put("double", "Float64");
        DATE_TYPE_MAP.put("date", "Date32");
        DATE_TYPE_MAP.put("datetime", "DateTime");
        DATE_TYPE_MAP.put("boolean", "Int8");
        DATE_TYPE_MAP.put("long", "Int64");
    }

    @Override
    public boolean isTableExist(String ckTableName) {
        try {
            return clickHouseQueryDao.existTable(ckTableName);
        } catch (SQLException e) {
            log.error("查询[" + ckTableName + "]表结构异常：" + e.getMessage());
            throw new InternalDataServiceException("查询数据表失败");
        }
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
    public int queryCount(String tableName, @Nullable String whereCondition) {
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
    public <T> List<T> queryForList(String sql, Class<T> elementType) {
        return clickHouseJdbcTemplate.queryForList(sql, elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) {
        return clickHouseJdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> queryForListOfDynamicTable(String tableName, String sql) {
        return clickHouseJdbcTemplate.queryForList(sql);
    }

    @Override
    public void simpleDeleteRecord(String tableName, String colum, String value) {
        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append("ALTER TABLE ").append(tableName).append(" DELETE WHERE ").append(colum).append(" = '").append(value).append("'");
        String sqlString = deleteSql.toString();
        log.info("删除记录 sql:{}", sqlString);
        clickHouseJdbcTemplate.execute(sqlString);
    }

    @Override
    public void deleteByIn(String tableName, String conditionColumn, List<String> conditionList) {

    }


    @Override
    public void execute(String sql) {
        log.info("尚未实现");
    }

    /**
     * 更新sql
     *
     * @param sql  更新sql
     * @param args 参数
     */
    @Override
    public void update(String sql, Object... args) {
        log.info("尚未实现");
    }

    /**
     * 查询单个
     *
     * @param sql sql语句
     * @return Map
     */
    @Override
    public Map<String, Object> queryForMap(String sql) {
        log.info("尚未实现");
        return null;
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
                insertSql.append(s);
            }
            insertSql.append(")");
        }

        insertSql.append(" VALUES ");
        for (List<String> strings : valueList) {
            insertSql.append("(");
            for (String varData : strings) {
                insertSql.append("'").append(varData).append("',");
            }
            insertSql.deleteCharAt(insertSql.length() - 1).append("),");
        }

        insertSql.deleteCharAt(insertSql.length() - 1);
        String sqlString = insertSql.toString();
        log.info("插入数据 sql:{}", sqlString);
        clickHouseJdbcTemplate.execute(sqlString);
    }

    /**
     * queryPage
     *
     * @param tableName 表名
     * @param page      分页
     * @param condition   sql语句
     * @param  colum     查询列
     * @param args      参数
     * @param clazz     Class
     * @return com.baomidou.mybatisplus.core.metadata.IPage
     */
    @Override
    public <T> IPage<T> queryPage(String tableName, IPage<T> page, List<String> colum, String condition, Object[] args, Class<T> clazz) {
        Long totalCount = clickHouseJdbcTemplate.queryForObject(getCountSql(tableName, condition), args, Long.class);
        if (totalCount != null) {
            page.setTotal(totalCount);
        } else {
            page.setTotal(0L);
        }
        if (page.getTotal() == 0) {
            page.setRecords(Collections.emptyList());
            return page;
        }

        args = ArrayUtils.add(args, page.getSize());
        args = ArrayUtils.add(args, page.getSize() * (page.getCurrent() - 1));
        List<T> resultList = clickHouseJdbcTemplate.query(getPageSql(tableName, condition), args, new BeanPropertyRowMapper<>(clazz));
        page.setRecords(resultList);

        return page;
    }

    @Override
    public long queryForLong(String sql, Object[] args, Class<?> clazz) {
        final Long aLong = clickHouseJdbcTemplate.queryForObject(sql, args, Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public long queryForLong(String sql, Class<?> clazz) {
        final Long aLong = clickHouseJdbcTemplate.queryForObject(sql, Long.class);
        return aLong == null ? 0 : aLong;
    }

    @Override
    public double queryForDouble(String sql) {
        final Double aDouble = clickHouseJdbcTemplate.queryForObject(sql, Double.class);
        return aDouble == null ? 0.00 : aDouble;
    }

    @Override
    public float queryForFloat(String sql, Object[] args) {
        final Float aFloat = clickHouseJdbcTemplate.queryForObject(sql, args, Float.class);
        return aFloat == null ? 0.00f : aFloat;
    }

    @Override
    public List<Object> getColumnDataDistinct(String tableName, String column, String whereCondition) {
        String sql = String.format(COLUMN_DATA_DISTINCT, column, tableName, whereCondition);
        List<Object> resultNames = new ArrayList<>();
        log.info("去重sql：{}", sql);
        try {
            clickHouseQueryDao.execute(sql, r -> {
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
        return null;
    }

    @Override
    public String getDataType(String prototype) {
        String dataType = null;
        for (Map.Entry<String, String> entry : DATE_TYPE_MAP.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(prototype)) {
                dataType = entry.getKey();
                break;
            }
        }
        return dataType;
    }

    @Override
    public List<Object> getColumnData(String tableName, int columnIndex, String whereContition) {
        return null;
    }

    private String getCountSql(String tableName, String sql) {
        sql = "SELECT COUNT(*) from " + tableName + " " + sql;
        log.info("count sql:{}", sql);
        return sql;
    }

    private String getPageSql(String tableName, String sql) {
        sql = "select * from " + tableName + " " + sql + " LIMIT ? OFFSET ?";
        log.info("page sql:{}", sql);
        return sql;
    }
}
