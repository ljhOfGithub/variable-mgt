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

import java.sql.SQLException;
import java.util.List;

/**
 * ClickHouse修改类SQL的DAO
 *
 * @author xujiawei
 * @since 2021/11/2
 */
public interface ClickHouseModifyDao {

    /**
     * 批量执行非查询类的SQL
     *
     * @param modifySql 要执行的SQL
     * @param data      每一行的数据
     * @param size      大小
     * @param <T> 泛型对象
     * @throws SQLException jdbc错误直接上抛
     */
    <T> void batchExecute(String modifySql, int size, List<List<T>> data) throws SQLException;

    /**
     * 更新数据库，执行一条或多条非查询类的SQL语句
     * @param modifySql 要执行的SQL
     * @param data      要格式化的数据
     * @throws SQLException SQL异常
     */
    void execute(String modifySql, Object... data) throws SQLException;

    /**
     * 执行
     * @param modifySql 修改的SQL语句
     * @param sleepMillisecond 休眠时间
     * @param data 数据(可变数组)
     * @throws SQLException SQL异常
     */
    void execute(String modifySql, long sleepMillisecond, Object... data) throws SQLException;

}
