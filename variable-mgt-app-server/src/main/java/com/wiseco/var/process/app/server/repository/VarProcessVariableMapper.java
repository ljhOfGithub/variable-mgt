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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDeployContentOverviewDto;
import com.wiseco.var.process.app.server.service.dto.VariableMaximumListedVersionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableQueryDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableMapper extends BaseMapper<VarProcessVariable> {

    /**
     * 查找变量最大版本列表
     *
     * @param page     分页对象
     * @param queryDto 查询条件
     * @return VarProcessVariable List
     */
    IPage<VariableDetailDto> findVariableMaxVersionList(Page page, @Param("queryDto") VariableQueryDto queryDto);

    /**
     * 获取变量信息列表
     *
     * @param queryDto 查询条件
     * @return VarProcessVariable List
     */
    List<VarProcessVariable> getList(@Param("queryDto") VariableQueryDto queryDto);

    /**
     * 获取最大版本号
     *
     * @param spaceId    空间id
     * @param identifier 编号
     * @return 最大版本号
     */

    @Select("SELECT MAX(version) AS maxNum FROM var_process_variable WHERE var_process_space_id = #{spaceId}  and identifier = #{identifier} and delete_flag=1")
    Integer getMaxVersion(@Param("spaceId") Long spaceId, @Param("identifier") String identifier);

    /**
     * 统计上架变量数 (上架且未删除变量)
     *
     * @param spaceId 变量空间 ID
     * @return 上架变量数
     */
    @Select("SELECT COUNT(DISTINCT identifier)\n" + "FROM var_process_variable\n" + "WHERE status = 2\n" + "  AND delete_flag = 1\n"
            + "  AND var_process_space_id = #{spaceId}")
    Integer countListedVariable(@Param("spaceId") Long spaceId);

    /**
     * 统计空间内变量总数 (未删除变量)
     *
     * @param spaceId 变量空间 ID
     * @return 变量总数
     */
    @Select("SELECT COUNT(DISTINCT identifier)\n" + "FROM var_process_variable\n" + "WHERE delete_flag = 1\n"
            + "  AND var_process_space_id = #{spaceId}")
    Integer countTotalVariable(@Param("spaceId") Long spaceId);

    /**
     * 统计已发布变量数 (所有已发布服务引入的变量)
     *
     * @param spaceId 变量空间 ID
     * @return 已发布变量数
     */
    @Select("SELECT COUNT(DISTINCT vpv.identifier)\n" + "FROM var_process_variable vpv\n"
            + "         INNER JOIN var_process_manifest_variable vpmv ON vpv.id = vpmv.variable_id\n"
            + "         INNER JOIN var_process_manifest vpm ON vpmv.manifest_id = vpm.id\n" + "WHERE vpm.state = 5\n" + "  AND vpv.status = 2\n"
            + "  AND vpv.delete_flag = 1\n" + "  AND vpv.var_process_space_id = #{spaceId}\n")
    Integer countReleasedVariableNumber(@Param("spaceId") Long spaceId);

    /**
     * 查询变量清单输出变量 List
     *
     * @param manifestId 变量清单 ID
     * @return 变量信息 List
     */
    @Select("SELECT vpv.id, vpv.name, vpv.label\n" + "FROM var_process_variable vpv\n"
            + "         INNER JOIN var_process_manifest_variable vpmv on vpv.id = vpmv.variable_id\n" + "WHERE vpmv.manifest_id = #{manifestId}\n")
    List<VarProcessVariable> findManifestOutputVariableList(@Param("manifestId") Long manifestId);

    /**
     * 分页查询变量清单发布数据预览信息
     *
     * @param pageConfig 分页配置
     * @param manifestId 变量清单 ID
     * @return 变量清单发布数据预览 DTO 分页封装
     */
    IPage<VariableManifestDeployContentOverviewDto> getManifestDeployContentPage(Page<VariableManifestDeployContentOverviewDto> pageConfig,
                                                                                 @Param("manifestId") Long manifestId);

    /**
     * 分页查询变量已上架的最大版本
     *
     * @param queryDto 所有变量最大已上架版本记录查询 DTO
     * @return 变量空间表实体类 分页封装
     */
    List<VarProcessVariable> getVariableMaximumListedVersion(@Param("queryDto") VariableMaximumListedVersionQueryDto queryDto);

    /**
     * 根据原变量的ID，获取它下面所有最新版本的发布变量ID
     *
     * @param archetypeManifestId 原变量的ID
     * @return 所有最新版本的发布变量ID
     */
    @Select("SELECT "
            + "var_process_variable.id "
            + "FROM "
            + "var_process_variable "
            + "INNER JOIN ("
            + "SELECT "
            + "identifier, "
            + "MAX(VERSION) AS max_version "
            + "FROM "
            + "var_process_variable "
            + "WHERE identifier IN "
            + "(SELECT identifier FROM var_process_variable WHERE id IN "
            + "(SELECT variable_id FROM var_process_manifest_variable WHERE manifest_id = #{archetypeManifestId})) AND status = 'UP' GROUP BY identifier "
            + ") AS max_version_table " + "ON " + "var_process_variable.identifier = max_version_table.identifier "
            + "AND var_process_variable.version = max_version_table.max_version where var_process_variable.delete_flag = 1;")
    List<Long> getNewVersionOfVariables(@Param("archetypeManifestId") Long archetypeManifestId);

    /**
     * 通过变量Id获取变量
     *
     * @param variableId 变量Id
     * @return 变量列表
     */
    @Select("select vpv.id, vpv.name, vpv.label from var_process_variable_reference vpvr " + "inner join var_process_variable vpv on vpvr.variable_id = vpv.id "
            + "where vpvr.use_by_variable_id = #{variableId}")
    List<VarProcessVariable> getVariablesByVariableId(@Param("variableId") Long variableId);

    /**
     * 条件+分页查询变量
     * @param variableIds 可能的目标变量
     * @param categoryId 分类Id
     * @param dataType 数据类型
     * @param users 可能的创建人
     * @param keyword 关键词(用于变量名称/编码的模糊查询)
     * @param order 排序
     * @return 变量列表
     */
    List<VarProcessVariable> getVariableList(@Param("variableIds") List<Long> variableIds, @Param("categoryId") Long categoryId,
                                             @Param("dataType") String dataType, @Param("users") List<String> users,
                                             @Param("keyword") String keyword, @Param("order") String order);

    /**
     * 获取所有启用的实时服务下, 所关联的启用变量清单, 然后根据这些变量清单，获取所有启用的变量Id
     * @return 变量列表
     */
    List<Long> variableIdsByOther();

    /**
     * 根据清单id拿到变量list
     * @param manifestId 清单id
     * @return list
     */
    @Select("select vpv.id, vpv.name, vpv.label, vpv.data_type \n"
            + "from var_process_variable vpv\n"
            + "left join var_process_manifest_variable vpmv on\n"
            + "\tvpmv.variable_id = vpv.id\n"
            + "where manifest_id = #{manifestId}")
    List<VarProcessVariable> findVariablesByManifest(@Param("manifestId") Long manifestId);
}
