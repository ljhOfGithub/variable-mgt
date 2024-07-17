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

import com.wiseco.var.process.app.server.commons.enums.CKColumnTypeEnum;
import com.wiseco.var.process.app.server.repository.base.ResultSetConsumer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * ClickHouse查询类SQL的DAO
 *
 * @author xujiawei
 * @since 2021/10/28
 */
public interface ClickHouseQueryDao {
    /**
     * 执行查询SQL，并回调调用方的消费函数
     *
     * @param sql              要执行的查询SQL
     * @param callbackFunction 消费ResultSet的回调函数
     * @throws SQLException jdbc错误直接上抛
     */
    void execute(String sql, ResultSetConsumer callbackFunction) throws SQLException;

    /**
     * 查询库里是否存在名为tableName的表
     *
     * @param tableName 要查询是否存在的表名
     * @return 该表名是否存在
     * @throws SQLException jdbc错误直接上抛
     */
    boolean existTable(String tableName) throws SQLException;

    /**
     * 查询表里是否存在任意一行数据
     *
     * @param tableName 要查询是否存在数据的表名
     * @return 该表是否存在数据
     * @throws SQLException jdbc错误直接上抛
     */
    boolean existData(String tableName) throws SQLException;

    /**
     * 查询表里是否存在表名且该表中存在数据；若不存在表或者不存在任意数据，则返回false
     *
     * @param tableName 要查询的表名
     * @return 是否存在表且包含数据
     * @throws SQLException jdbc错误直接上抛
     */
    boolean existTableAndData(String tableName) throws SQLException;

    /**
     * 查询字段列类型
     *
     * @param tableName  表名
     * @param columnName 字段名
     * @return CKColumnTypeEnum
     * @throws SQLException SQL异常
     */
    CKColumnTypeEnum selectColumnType(String tableName, String columnName) throws SQLException;

    /**
     * 查询表信息
     *
     * @param tableName 表名
     * @return Map
     * @throws SQLException SQL异常
     */
    Map<String, String> describeTable(String tableName) throws SQLException;

    /**
     * 查询系统信息
     *
     * @param tableName 表名
     * @return Map
     * @throws SQLException SQL异常
     */
    Map<String, String> selectSystemPart(String tableName) throws SQLException;

    /**
     * 执行特定sql
     *
     * @param sql sql语句
     * @param callbackFunction 回调函数
     * @throws SQLException SQL异常
     */
    void execute(List<String> sql, ResultSetConsumer callbackFunction) throws SQLException;

    /**
     * 执行查询SQL，并回调调用方的消费函数
     *
     * @param sql              要执行的查询SQL
     * @param callbackFunction 消费ResultSet的回调函数
     * @throws SQLException jdbc错误直接上抛
     */
    void executeQuerySql(String sql, ResultSetConsumer callbackFunction) throws SQLException;

    /**
     * 执行查询一连串SQL，并回调调用方的消费函数
     *
     * @param sql              要执行的一连串SQL，最后一个是查询，返回rs，其他的都只执行更新
     * @param callbackFunction 消费ResultSet的回调函数
     * @throws SQLException jdbc错误直接上抛
     */
    void executeQuerySql(List<String> sql, ResultSetConsumer callbackFunction) throws SQLException;
}
