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

import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.clickhouse.BalancedClickhouseDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * @author ycc
 * @since 2022/12/30 19:20
 */
@Slf4j
public class MasterClickhouseConnFactory {

    private static BalancedClickhouseDataSource masterDatasource = null;

    private static final Random RANDOM_OBJECT = new Random();

    /**
     * clickhouse 复制模式集群 获取自定义主节点连接（保证在节点不挂的情况下始终获取一个节点上的连接）
     *
     * @param dataSource
     * @return Connection
     */
    private static synchronized Connection getConnection(DataSource dataSource) throws SQLException {
        if (!(dataSource instanceof BalancedClickhouseDataSource)) {
            log.warn("MasterClickhouseConnFactory.getConnection 该数据源类型错误，无法获取连接");
            return null;
        }
        if (masterDatasource == null) {
            masterDatasource = createMasterDatasource(dataSource);
        } else {
            int actualize = masterDatasource.actualize();
            if (actualize == 0) {
                //当前维护的连接不可用，更换其它连接
                masterDatasource = createMasterDatasource(dataSource);
            }
        }
        return masterDatasource.getConnection();
    }

    private static BalancedClickhouseDataSource createMasterDatasource(DataSource dataSource) {
        BalancedClickhouseDataSource balancedClickhouseDataSource = (BalancedClickhouseDataSource) dataSource;
        List<String> enabledClickHouseUrls = balancedClickhouseDataSource.getEnabledClickHouseUrls();
        if (enabledClickHouseUrls == null || enabledClickHouseUrls.size() == 0) {
            //无可用的ck连接
            throw new InternalDataServiceException("无可用的clickhouse节点");
        }
        String url = enabledClickHouseUrls.get(randomIndex(enabledClickHouseUrls.size()));
        return new BalancedClickhouseDataSource(url, balancedClickhouseDataSource.getProperties());
    }

    private static int randomIndex(int size) {
        return RANDOM_OBJECT.nextInt(size);
    }

    /**
     * 获取连接
     * @param dataSource 数据源
     * @param isCluster 是否集群
     * @return Connection
     * @throws SQLException SQL异常
     */
    public static Connection getConnection(DataSource dataSource, Boolean isCluster) throws SQLException {
        if (isCluster) {
            return getConnection(dataSource);
        } else {
            //单机版的直接获取连接返回
            return dataSource.getConnection();
        }
    }
}
