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
package com.wiseco.var.process.app.server.repository;

import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DynamicMapper {

    /**
     * 单纯只是通过id查询名称字段为name的对象
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT name FROM ${tableName} WHERE id = #{id}")
    String selectNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 单纯只是通过id查询实时服务表的service_name字段
     * @param id 实时服务的ID
     * @return service_name字段
     */
    @Select("SELECT service_name FROM var_process_realtime_service WHERE id = #{id} AND delete_flag = 1")
    String selectServiceNameById(@Param("id") Long id);

    /**
     * 通过实时服务(与code相关的)的ID，获取最大的版本号
     * @param id 实时服务(与code相关的)的ID
     * @return 最大的版本号
     */
    @Select("SELECT MAX(service_version) FROM var_process_service_version WHERE service_id = #{id} AND delete_flag = 1")
    Integer getMaxVersionByServiceId(@Param("id") Long id);

    /**
     * 通过实时服务(具体的)的ID，查询其版本号
     * @param id 实时服务(具体的)的ID
     * @return 版本号
     */
    @Select("SELECT service_version FROM var_process_service_version WHERE id = #{id} AND delete_flag = 1")
    Integer getVersionByCopiedServiceId(@Param("id") Long id);

    /**
     * 通过id查询名称字段为label的变量对象名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT label FROM ${tableName} WHERE id = #{id}")
    String selectVariableNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 通过id查询名称字段为label的变量版本
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT version FROM ${tableName} WHERE id = #{id}")
    String selectVariableVersionById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 通过父id查询变量最新版本
     * @param tableName 表名
     * @param parentId parentId
     * @return String
     */
    @Select("SELECT max(version) FROM ${tableName} WHERE parent_id = #{parentId}")
    String selectVariableMaxVersionByParentId(@Param("tableName") String tableName, @Param("parentId") Long parentId);

    /**
     * 通过父id查询变量名称
     * @param tableName 表名
     * @param parentId parentId
     * @return String
     */
    @Select("SELECT distinct(label) FROM ${tableName} WHERE parent_id = #{parentId}")
    String selectVariableNameByParentId(@Param("tableName") String tableName, @Param("parentId") Long parentId);

    /**
     * 数据模型通过对象名查询版本
     * @param tableName 表名
     * @param name name
     * @return String
     */
    @Select("SELECT max(version) FROM ${tableName} WHERE name = #{name}")
    String selectServiceMaxVersionByName(@Param("tableName") String tableName, @Param("name") String name);

    /**
     * 通过id查询名称字段为label的变量清单名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT var_manifest_name FROM ${tableName} WHERE id = #{id}")
    String selectManifestNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 数据模型通过id查询名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT object_name FROM ${tableName} WHERE id = #{id}")
    String selectModelNameById(@Param("tableName") String tableName, @Param("id") Long id);


    /**
     * 标签组通过id查询名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT group_name FROM ${tableName} WHERE id = #{id}")
    String selectTagNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 数据模型通过对象名查询版本
     * @param tableName 表名
     * @param objectName objectName
     * @return String
     */
    @Select("SELECT max(version) FROM ${tableName} WHERE object_name = #{objectName}")
    String selectVersionByObjectName(@Param("tableName") String tableName, @Param("objectName") String objectName);

    /**
     * 查询业务字典类型名称以及字典项名称
     * @param tableName 表名
     * @param innerTableName 连接表
     * @param id id
     * @return String[]
     */
    @Select("SELECT vd.name FROM ${tableName} vd inner join (select name,dict_id from ${innerTableName} where id = #{id}) vdd where id = vdd.dict_id")
    String selectDicAndCategoryById(@Param("tableName") String tableName, @Param("innerTableName") String innerTableName, @Param("id") Long id);

    /**
     * 获取function类型名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT function_type FROM ${tableName} WHERE id = #{id}")
    String getFunctionTypeNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 通过名称获取category类型名称
     * @param tableName 表名
     * @param name 名称
     * @return String
     */
    @Select("SELECT category_type FROM ${tableName} WHERE name = #{name}")
    String getCategoryTypeNameByName(@Param("tableName") String tableName, @Param("name") String name);

    /**
     * 通过id获取category类型名称
     * @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT category_type FROM ${tableName} WHERE id = #{id}")
    String getCategoryTypeNameById(@Param("tableName") String tableName, @Param("id") Long id);

    /**
     * 通过id获取缺失值类型名称
     *  @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT data_type FROM ${tableName} WHERE id = #{id}")
    String getDataTypeNameById(@Param("tableName") String tableName, @Param("id") Long id);


    /**
     * 通过id获取缺失值
     *  @param tableName 表名
     * @param id id
     * @return String
     */
    @Select("SELECT default_value FROM ${tableName} WHERE id = #{id}")
    String getBeforeDefaultById(@Param("tableName") String tableName, @Param("id") Long id);


    /**
     * 获取function类型名称
     * @param functionType 逻辑类型
     * @return String
     */
    default String getFunctionTypeName(FunctionTypeEnum functionType) {
        return functionType.getDesc();
    }

    /**
     * 获取category类型名称
     * @param categoryType 类别名称
     * @return String
     */
    default String getCategoryTypeName(CategoryTypeEnum categoryType) {
        return categoryType.getDesc();
    }

    /**
     * 获取function和批量回溯操作类型名称
     * @param actionType 类别名称
     * @return String
     */
    default String getFunctionAndBacktrackingActionTypeName(FlowActionTypeEnum actionType) {
        return actionType.getDesc();
    }

    /**
     * 根据授权id拿到调用方名称
     * @param tableName 表名
     * @param id 授权id
     * @return caller
     */
    @Select("select caller from ${tableName} where id = ${id}")
    String getAuthNameById(@Param("tableName") String tableName, @Param("id") Long id);
}
