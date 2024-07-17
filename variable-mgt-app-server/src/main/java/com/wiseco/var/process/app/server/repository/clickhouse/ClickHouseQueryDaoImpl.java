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

import com.google.common.collect.Maps;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.CKColumnTypeEnum;
import com.wiseco.var.process.app.server.commons.exception.BizExceptionMessage;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.config.CallLogCondition;
import com.wiseco.var.process.app.server.repository.base.ResultSetConsumer;
import com.wiseco.var.process.app.server.repository.util.QueryTableSqlFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xujiawei
 * @since 2021/11/2
 */
@Slf4j
@Service
@CallLogCondition.OnClickhouseCallLogEnabled
class ClickHouseQueryDaoImpl extends AbstractClickHouseDao implements ClickHouseQueryDao {


    public static final String SUFFIX = ";";
    @Autowired
    @Qualifier("clickHouseDataSource")
    DataSource dataSource;
    /**
     * @Value("${spring.datasource.clickhouse.cluster}")
     */
    private Boolean isCluster = false;

    @Override
    public void execute(String sql, ResultSetConsumer callbackFunction) throws SQLException {
        log.debug("执行SQL查询：{}", sql);
        Connection connection = MasterClickhouseConnFactory.getConnection(dataSource, isCluster);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {
            callbackFunction.accept(resultSet);
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean existTable(String tableName) throws SQLException {
        MutableBoolean ret = new MutableBoolean(false);
        String sql = QueryTableSqlFormat.getExistTableSql(tableName);
        log.debug("执行SQL查询：{}", sql);
        execute(sql, rs -> ret.setValue(rs.next()));
        return ret.isTrue();
    }

    @Override
    public boolean existData(String tableName) throws SQLException {
        if (!existTable(tableName)) {
            throw new InternalDataServiceException(BizExceptionMessage.TABLE_DONT_EXIST_IN_CK, new Object[]{tableName});
        }
        MutableBoolean ret = new MutableBoolean(false);
        String sql = QueryTableSqlFormat.getExistDataSql(tableName);
        execute(sql, rs -> ret.setValue(rs.next()));
        return ret.isTrue();
    }

    @Override
    public boolean existTableAndData(String tableName) throws SQLException {
        return existTable(tableName) && existData(tableName);
    }

    @Override
    public CKColumnTypeEnum selectColumnType(String tableName, String columnName) throws SQLException {
        final String selectColumnTypeSql = QueryTableSqlFormat.selectColumnTypeSql(tableName, columnName);
        MutableObject<String> ret = new MutableObject();
        execute(selectColumnTypeSql, rs -> {
            rs.next();
            ret.setValue(rs.getString(1));
        });
        return CKColumnTypeEnum.get(ret.toString());
    }

    @Override
    public Map<String, String> describeTable(String tableName) throws SQLException {
        Map<String, String> table = Maps.newLinkedHashMap();
        final String describeTable = QueryTableSqlFormat.describeTable(tableName);
        execute(describeTable, rs -> {
            while (rs.next()) {
                table.put(rs.getString(1), rs.getString(MagicNumbers.TWO));
            }
        });
        return table;
    }

    @Override
    public Map<String, String> selectSystemPart(String tableName) throws SQLException {
        Map<String, String> table = Maps.newLinkedHashMap();
        final String describeTable = QueryTableSqlFormat.getSystemParts(tableName);
        execute(describeTable, rs -> {
            while (rs.next()) {
                table.put("rows", rs.getString(MagicNumbers.TWO));
                table.put("data", rs.getString(MagicNumbers.THREE));
            }
        });
        return table;
    }

    @Override
    public void execute(List<String> sql, ResultSetConsumer callbackFunction) throws SQLException {
        sql = sql.stream().map(String::trim).map(s -> s.endsWith(SUFFIX) ? s.substring(0, s.length() - 1) : s).collect(Collectors.toList());
        try (
                Connection connection = MasterClickhouseConnFactory.getConnection(dataSource, isCluster);
        ) {
            int i = 0;
            for (; i < sql.size() - 1; i++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.get(i))) {
                    preparedStatement.executeUpdate();
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.get(i));
                 ResultSet resultSet = preparedStatement.executeQuery();) {
                callbackFunction.accept(resultSet);
            }
        }
    }

    @Override
    public void executeQuerySql(String sql, ResultSetConsumer callbackFunction) throws SQLException {
        sql = sql.trim();
        if (sql.endsWith(SUFFIX)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        log.debug("执行SQL查询：{}", sql);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            callbackFunction.accept(resultSet);
        }
    }

    @Override
    public void executeQuerySql(List<String> sql, ResultSetConsumer callbackFunction) throws SQLException {
        sql = sql.stream().map(String::trim).map(s -> s.endsWith(SUFFIX) ? s.substring(0, s.length() - 1) : s).collect(Collectors.toList());
        log.debug("执行SQL查询：{}", sql);
        try (
                Connection connection = dataSource.getConnection()
        ) {
            int i = 0;
            for (; i < sql.size() - 1; i++) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.get(i))) {
                    preparedStatement.executeUpdate();
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.get(i));
                 ResultSet resultSet = preparedStatement.executeQuery();) {
                callbackFunction.accept(resultSet);
            }
        }
    }
}
