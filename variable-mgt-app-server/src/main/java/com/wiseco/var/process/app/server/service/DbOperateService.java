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
import com.wiseco.var.process.app.server.controller.vo.input.DataBaseFieldMapDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wiseco
 */
public interface DbOperateService {

    /**
     * 查询库里是否存在名为tableName的表
     *
     * @param tableName 要查询是否存在的表名
     * @return true/false
     */
    boolean isTableExist(String tableName);

    /**
     * 查询表结构信息
     *
     * @param tableName 表名
     * @return map
     */
    Map<String, String> describeTable(String tableName);

    /**
     * 查询表记录数
     *
     * @param tableName      表名
     * @param whereCondition 条件
     * @return 表记录数
     */
    int queryCount(String tableName, @Nullable String whereCondition);


    /**
     * queryForList
     *
     * @param sql         sql语句
     * @param elementType 元素的类型
     * @param <T>         泛型
     * @return List
     */
    <T> List<T> queryForList(String sql, Class<T> elementType);

    /**
     * 查询表数据
     *
     * @param sql sql语句
     * @return column, data
     */
    List<Map<String, Object>> queryForList(String sql);

    /**
     * 查询表数据——动态表
     *
     * @param tableName 表名
     * @param sql       sql语句
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    List<Map<String, Object>> queryForListOfDynamicTable(String tableName, String sql);

    /**
     * queryForList
     *
     * @param tableName  表名
     * @param colum      字段
     * @param condition  条件
     * @param orderColum 排序字段
     * @param from       分页
     * @param size       分页
     * @return java.util.List
     */
    default List<Map<String, Object>> queryForList(String tableName, List<String> colum, String condition, LinkedHashMap<String, String> orderColum, Integer from, Integer size) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }


    /**
     * queryPage
     *
     * @param tableName 表名
     * @param page      分页对象
     * @param colum     字段
     * @param condition 查询条件sql
     * @param args      参数
     * @param clazz     反射类对象
     * @param <T>       泛型
     * @return com.baomidou.mybatisplus.core.metadata.IPage
     */
    <T> IPage<T> queryPage(String tableName, IPage<T> page, List<String> colum, String condition, Object[] args, Class<T> clazz);

    /**
     * createTable
     *
     * @param tableName  表名
     * @param columMap   列名，列类型
     * @param indexColum 索引列
     * @param primaryKey 主键
     */
    default void createTable(String tableName, LinkedHashMap<String, String> columMap, List<String> indexColum, String primaryKey) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }

    /**
     * 简单删除
     *
     * @param tableName 表名
     * @param colum     条件
     * @param value     条件
     */
    void simpleDeleteRecord(String tableName, String colum, String value);

    /**
     * 删除
     *
     * @param tableName       表名
     * @param conditionColumn 条件列
     * @param conditionList   条件列值
     */
    void deleteByIn(String tableName, String conditionColumn, List<String> conditionList);

    /**
     * 插入记录
     *
     * @param tableName 表名
     * @param columList 列
     * @param valueList 数据
     */
    void batchInsert(String tableName, List<String> columList, List<List<String>> valueList);

    /**
     * 查询数量
     *
     * @param sql   sql语句
     * @param clazz 泛型
     * @return 数量大小
     */
    long queryForLong(String sql, Class<?> clazz);

    /**
     * 查询数量
     *
     * @param sql   sql语句
     * @param args  参数
     * @param clazz 泛型
     * @return 数量大小
     */
    long queryForLong(String sql, Object[] args, Class<?> clazz);

    /**
     * 查询数量
     *
     * @param sql sql语句
     * @return double
     */
    double queryForDouble(String sql);

    /**
     * 查询数量
     *
     * @param sql   sql语句
     * @param args  参数
     * @return 数量大小
     */
    float queryForFloat(String sql, Object[] args);

    /**
     * 查询数据库中某列去重后的数据
     *
     * @param tableName      表名
     * @param column         列名
     * @param whereCondition 条件
     * @return 去重后的数据
     */
    List<Object> getColumnDataDistinct(String tableName, String column, String whereCondition);

    /**
     * 查询数据库中某列去重后的数据
     *
     * @param tableName      表名
     * @param column         列名
     * @param whereCondition 条件
     * @param manifestId     查询字段信息的清单ID
     * @return 去重后的数据
     */
    List<Object> getColumnDataDistinct(String tableName, String column, String whereCondition, Long manifestId);

    /**
     * 获取每个数据源特定的数据类型
     *
     * @param prototype 数据类型
     * @return 对应的数据类型
     */
    String getDataType(String prototype);


    /**
     * 查询数据库中某列的数据
     *
     * @param tableName      表名
     * @param varIndex       下标
     * @param whereCondition 条件
     * @return 数据集合
     */
    List<Object> getColumnData(String tableName, int varIndex, String whereCondition);

    /**
     * 执行sql
     *
     * @param sql sql
     */
    void execute(String sql);

    /**
     * 更新sql
     *
     * @param sql  更新sql
     * @param args 参数
     */
    void update(String sql, Object... args);

    /**
     * 查询单个
     *
     * @param sql sql语句
     * @return Map
     */
    Map<String, Object> queryForMap(String sql);


    /**
     * 关联条件查询——数据条数
     *
     * @param tableJoinList 表关联对象
     * @param whereCondition where条件
     * @return count
     */
    default Integer countOfJoin(List<TableJoinDto> tableJoinList, String whereCondition) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }

    /**
     * 关联条件查询——数据
     *
     * @param tableJoinJson  表连接信息
     * @param whereCondition 查询条件
     * @param offset         偏移量
     * @param size           大小
     * @return resultlist 默认的别名映射关系
     * @throws SQLException 异常
     */
    default List<Map<String, Object>> queryForListOfJoin(List<TableJoinDto> tableJoinJson, String whereCondition, int offset, int size) throws SQLException {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }


    /**
     * 关联查询——查询表的字段信息
     *
     * @param tableJoinList 关联表信息
     * @return 表结构字段集合
     * @throws SQLException 异常
     */
    default List<DataBaseFieldMapDto> tableColumListOfJoin(List<TableJoinDto> tableJoinList) throws SQLException {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }

    /**
     * 获取表的字段名
     *
     * @param tableName 表名
     * @throws SQLException 异常
     * @return 表结构
     */
    default List<String> tableColumList(String tableName) throws SQLException {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "方法暂未实现");
    }
}
