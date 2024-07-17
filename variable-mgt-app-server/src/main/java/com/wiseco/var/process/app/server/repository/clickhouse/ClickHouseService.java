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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Asker.J
 * @since 2022/7/27
 */
public interface ClickHouseService {

    /**
     * 创建表
     *
     * @param tableName   表名
     * @param columns rc
     * @param columnTypes rc
     * @param o rc
     */
    void createTable(String tableName, List<String> columns, Map<String, CKColumnTypeEnum> columnTypes, String o);

    /**
     * 创建空字符串表
     *
     * @param tableName 表名
     * @param columns rc
     * @param o rc
     * @param idName rc
     */
    void createStringNullableTable(String tableName, List<String> columns, String o, String idName);

    /**
     * 强制删除表，无视是否包含数据
     *
     * @param tableName 要删除的表名
     * @param date      数据
     */
    void forceDeleteTable(String tableName, Date date);

    /**
     * 查找表信息
     *
     * @param ckTableName 表名
     * @return Map
     */
    Map<String, String> describeTable(String ckTableName);

    /**
     * 根据表名查询表字段信息
     *
     * @param ckTableName 表名
     * @return List
     */
    List<String> tableColumns(String ckTableName);

    /**
     * 根据表名和条件查询记录数
     *
     * @param tableName      表名
     * @param whereCondition 条件
     * @return int
     */
    int queryCount(String tableName, @Nullable String whereCondition);

    /**
     * 批量插入
     *
     * @param tableName   表名
     * @param columnNames 字段名
     * @param data        数据
     * @param <T> rc
     */
    <T> void batchInsert(String tableName, List<String> columnNames, List<List<T>> data);

    /**
     * 根据id查询
     *
     * @param tableName   表名
     * @param columnNames 字段名
     * @param headerLine rc
     * @return Map
     */
    Map<String, Object> queryById(String tableName, List<String> columnNames, int headerLine);

    /**
     * 带别名查询列表
     *
     * @param tableName rc
     * @param headers rc
     * @param fields rc
     * @param whereCondition rc
     * @param from rc
     * @param size rc
     * @return List
     */
    List<Map<String, Object>> queryListWithAlias(String tableName, List<String> headers, List<String> fields, String whereCondition, int from,
                                                 int size);

    /**
     * 根据字段名查询列表
     *
     * @param sql rc
     * @param columnNames rc
     * @return List
     */
    List<Map<String, Object>> queryList(String sql, List<String> columnNames);

    /**
     * 分页查询数据
     *
     * @param tableName rc
     * @param columnNames rc
     * @param from rc
     * @param pageSize rc
     * @param orderByDesc rc
     * @param orderByAsc rc
     * @param filter rc
     * @return List
     */
    List<Map<String, Object>> queryList(String tableName, List<String> columnNames, int from, int pageSize, List<String> orderByDesc,
                                        List<String> orderByAsc, Map<String, Object> filter);

    /**
     * 根据表名查询记录总数
     *
     * @param tableName rc
     * @return int
     */
    int queryCount(String tableName);

    /**
     * 查询记录总数
     *
     * @param countSql rc
     * @return long
     */
    long queryCountBySql(String countSql);

    /**
     * queryMin
     *
     * @param tableName rc
     * @param columnNames rc
     * @return double
     */
    double queryMin(String tableName, String... columnNames);

    /**
     * queryMax
     *
     * @param tableName rc
     * @param columnName rc
     * @return double
     */
    double queryMax(String tableName, String columnName);

    /**
     * queryDistinct
     *
     * @param tableName rc
     * @param columnNames rc
     * @param <T> rc
     * @return List
     */
    <T> List<T> queryDistinct(String tableName, String... columnNames);

    /**
     * queryDistinctIn
     *
     * @param tableName rc
     * @param columnName rc
     * @param ins rc
     * @param <T> rc
     * @return T
     */
    <T> List<T> queryDistinctIn(String tableName, String columnName, List ins);

    /**
     * queryList
     *
     * @param sql rc
     * @param columnNames rc
     * @param mapping rc
     * @return List
     */
    List<Map<String, Object>> queryList(String sql, List<String> columnNames, Map<String, String> mapping);

    /**
     * queryOne
     *
     * @param sql rc
     * @param columnNames rc
     * @param mapping rc
     * @return Map
     */
    Map<String, Object> queryOne(String sql, List<String> columnNames, Map<String, String> mapping);

    /**
     * queryTableColumns
     *
     * @param tableName rc
     * @return List
     */
    List<String> queryTableColumns(String tableName);

    /**
     * queryListByIds
     *
     * @param rejectDatasourceTableName rc
     * @param columnNames rc
     * @param copyRowNumList rc
     * @return List
     */
    List<List<Object>> queryListByIds(String rejectDatasourceTableName, List<String> columnNames, List<Integer> copyRowNumList);

    /**
     * createNullableTable
     *
     * @param tableName rc
     * @param columns rc
     * @param columnTypes rc
     * @param o rc
     * @param idName rc
     */
    void createNullableTable(String tableName, List<String> columns, Map<String, CKColumnTypeEnum> columnTypes, String o, String idName);

    /**
     * copyTableToNewTable
     *
     * @param fromTableName rc
     * @param toTableName rc
     */
    void copyTableToNewTable(String fromTableName, String toTableName);

    /**
     * insertByMysql
     *
     * @param ckTableName rc
     * @param ids rc
     * @param url rc
     * @param database rc
     * @param tableName rc
     * @param username rc
     * @param password rc
     */
    void insertByMysql(String ckTableName, String ids, String url, String database, String tableName, String username, String password);

    /**
     * 删除表
     *
     * @param ckTableName rc
     * @param date rc
     */
    void deleteTable(String ckTableName, Date date);

    /**
     * 删除表
     *
     * @param sql rc
     */
    void deleteData(String sql);

    /**
     * 强制删除表，无视是否包含数据
     *
     * @param tableName rc
     */
    void forceDeleteTable(String tableName);

    /**
     * 判断表是否存在
     *
     * @param ckTableName rc
     * @return Boolean
     */
    Boolean isTableExist(String ckTableName);

    /**
     * 创建表
     *
     * @param createTableSql rc
     * @throws SQLException 异常
     */
    void createTable(String createTableSql) throws SQLException;

    /**
     * 插入数据
     *
     * @param insertSql rc
     * @throws SQLException 异常
     */
    void insert(String insertSql) throws SQLException;

    /**
     * 分页查询
     *
     * @param tableName rc
     * @param columnNames rc
     * @param from rc
     * @param pageSize rc
     * @param orderByDesc rc
     * @param orderByAsc rc
     * @param whereContition rc
     * @return List
     */
    List<Map<String, Object>> queryPageList(String tableName, List<String> columnNames, int from, int pageSize, List<String> orderByDesc,
                                                   List<String> orderByAsc, String whereContition);
}
