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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.config.CallLogCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xujiawei
 * @since 2021/11/2
 */
@Service("clickHouseModifyDaoImpl")
@Slf4j
@Primary
@CallLogCondition.OnClickhouseCallLogEnabled
class ClickHouseModifyDaoImpl extends AbstractClickHouseDao implements ClickHouseModifyDao {

    @Resource(name = "clickHouseDataSource")
    DataSource dataSource;
    @Resource(name = "clickHouseQueryDaoImpl")
    ClickHouseQueryDao clickHouseQueryDao;
    /**
     * @Value("${spring.datasource.clickhouse.cluster}")
     */
    private Boolean isCluster;

    /**
     * 批处理执行
     *
     * @param modifySql 要执行的SQL
     * @param size      大小
     * @param data      每一行的数据
     * @param <T>       泛型
     * @throws SQLException
     */
    @Override
    public <T> void batchExecute(String modifySql, int size, List<List<T>> data) throws SQLException {
        long start = System.currentTimeMillis();
        Connection connection = MasterClickhouseConnFactory.getConnection(dataSource, isCluster);
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(modifySql)
        ) {
            for (List<T> row : data) {
                if (row.size() != size) {
                    log.error("执行sql：{}，待传数据大小：{}，实际数据大小：{}，实际数据：{}", modifySql, size, row.size(),
                            row.stream().map(s -> String.format(" %s ", s)).collect(Collectors.joining(",")));
                    throw new InternalDataServiceException("格式化数据不正确，请检查" + (row.size() > MagicNumbers.TEN ? row.subList(0, MagicNumbers.TEN) : row).stream()
                            .map(s -> String.format(" %s ", s)).collect(Collectors.joining(",")));
                }
                for (int j = 0; j < row.size(); j++) {
                    final Object o = row.get(j);
                    if ("".equals(o)) {
                        preparedStatement.setObject(j + 1, null);
                    } else {
                        preparedStatement.setObject(j + 1, o);
                    }
                }
                preparedStatement.addBatch();
            }
            log.info("batch sql prepare --- 执行耗时{}", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
        } finally {
            connection.close();
        }
        log.info("batch sql execute === 执行耗时{}", System.currentTimeMillis() - start);
    }

    /**
     * 执行
     *
     * @param modifySql 要执行的SQL
     * @param data      要格式化的数据
     * @throws SQLException
     */
    @Override
    public void execute(String modifySql, Object... data) {
        int count = 0;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = MasterClickhouseConnFactory.getConnection(dataSource, isCluster);
            for (String s : modifySql.split(SQL_SPLIT)) {
                if (null == data || data.length == 0) {
                    statement = connection.createStatement();
                    log.debug("执行SQL：{}", s);
                    count += statement.executeUpdate(s);
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(s)) {
                        for (int i = 0; i < data.length; i++) {
                            preparedStatement.setObject(i, data[i]);
                        }
                        count += preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                log.error("statement对象关闭失败!");
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("connection对象关闭失败!");
            }
        }
    }

    /**
     * 执行
     *
     * @param modifySql
     * @param sleepMillisecond
     * @param data
     * @throws SQLException
     */
    @Override
    public void execute(String modifySql, long sleepMillisecond, Object... data) throws SQLException {
        try (Connection connection = MasterClickhouseConnFactory.getConnection(dataSource, isCluster);) {
            for (String s : modifySql.split(SQL_SPLIT)) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(s)) {
                    for (int i = 0; i < data.length; i++) {
                        preparedStatement.setObject(i, data[i]);
                    }

                    preparedStatement.executeUpdate();
                }
                if (sleepMillisecond > 0) {
                    try {
                        Thread.sleep(sleepMillisecond);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        }
    }

}
