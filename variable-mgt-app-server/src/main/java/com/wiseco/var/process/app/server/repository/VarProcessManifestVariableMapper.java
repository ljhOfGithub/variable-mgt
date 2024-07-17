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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.ManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishingVariableDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量清单使用变量表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessManifestVariableMapper extends BaseMapper<VarProcessManifestVariable> {

    /**
     * 查询变量
     *
     * @param spaceId     空间id
     * @param variableIds 变量ids
     * @return VarProcessVariable List
     */
    @Select({
            "<script>",
            "SELECT vpv.id, vpv.name, vpv.label, vpv.data_type from var_process_manifest_variable vpmv\n"
                    + "            INNER JOIN var_process_variable vpv ON vpmv.variable_id = vpv.id\n"
                    + "            WHERE vpmv.var_process_space_id = #{spaceId}\n" + "            and vpv.id in\n"
                    + "<foreach collection='variableIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>", "</script>"})
    List<VarProcessVariable> getVariableList(@Param("spaceId") Long spaceId, @Param("variableIds") List<Long> variableIds);

    /**
     * 查询变量清单使用变量表
     *
     * @param spaceId    空间id
     * @param manifestId 变量清单id
     * @return VarProcessManifestVariableDto List
     */
    @Select("SELECT distinct(variable_id) AS variableId  FROM var_process_manifest_variable")
    List<VarProcessManifestVariableDto> getByManifestId(@Param("spaceId") Long spaceId, @Param("manifestId") Long manifestId);

    /**
     * 查询变量清单使用变量表
     *
     * @param spaceId 空间ID
     * @return VarProcessManifestVariable List
     */
    @Select("SELECT vpmv.variable_id, vpmv.manifest_id from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_manifest vpm ON  vpmv.manifest_id = vpm.id\n"
            + "            WHERE vpmv.var_process_space_id = #{spaceId} and vpm.delete_flag = 1 \n")
    List<VarProcessManifestVariable> getManifestVariableList(@Param("spaceId") Long spaceId);

    /**
     * 查询变量信息
     *
     * @param manifestId 清单id
     * @param identifier identifier
     * @return VarProcessManifestVariable
     */
    @Select("SELECT vpv.* from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_variable vpv ON vpmv.variable_id =vpv.id\n"
            + "            WHERE vpmv.manifest_id = #{manifestId} and vpv.identifier = #{identifier} and vpv.delete_flag = 1\n")
    VarProcessVariable getManifestVariableByIdentifier(@Param("manifestId") long manifestId, @Param("identifier") String identifier);


    /**
     * 查询流程-变量
     *
     * @param variableFlowQueryDto 查询参数
     * @return VarProcessVariable List
     */
    List<VarProcessVariable> getVariableFlow(@Param("variableFlowQueryDto") VariableFlowQueryDto variableFlowQueryDto);

    /**
     * 查询流程-变量
     *
     * @param variableFlowQueryDto 查询参数
     * @return VarProcessVariable List
     */
    List<VarProcessVariable> getVariableListInFlow(@Param("variableFlowQueryDto") VariableFlowQueryDto variableFlowQueryDto);

    /**
     * 根据变量清单ID和identifier 获取变量信息
     *
     * @param spaceId    空间ID
     * @param manifestId 变量清单ID
     * @param identifier 辨别符
     * @return 变量信息
     */
    @Select("SELECT vpv.* from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_variable vpv ON  vpmv.variable_id = vpv.id\n"
            + "            WHERE vpmv.var_process_space_id = #{spaceId}\n"
            + "            and vpmv.manifest_id = #{manifestId} and vpv.identifier = #{identifier}  and vpv.delete_flag = 1"

    )
    VarProcessVariable getVariableByIdentifier(@Param("spaceId") Long spaceId, @Param("manifestId") Long manifestId,
                                               @Param("identifier") String identifier);

    /**
     * 获取变量清单使用变量缓存信息
     *
     * @param date 日期
     * @return VarProcessManifestVariableDto List
     */
    @Select("SELECT vpmv.*, vpv.name as variableName from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_variable vpv ON  vpmv.variable_id = vpv.id\n"
            + "            WHERE vpmv.updated_time > #{date} and vpv.delete_flag = 1 \n")
    List<VarProcessManifestVariableDto> getCacheManifestVariableList(String date);

    /**
     * 通过变量清单id获取变量信息
     *
     * @param spaceId    空间ID
     * @param manifestId 变量清单ID
     * @return VarProcessVariableDto  List
     */
    @Select("SELECT vpv.*, vpvc.class_data,vpmv.is_index from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_variable vpv ON vpmv.variable_id = vpv.id\n"
            + "            LEFT JOIN var_process_variable_class vpvc ON vpvc.variable_id = vpv.id\n"
            + "            WHERE vpmv.var_process_space_id = #{spaceId}\n"
            + "            and vpmv.manifest_id = #{manifestId} and vpv.delete_flag = 1"

    )
    List<VarProcessVariableDto> getVariableListByManifestId(@Param("spaceId") Long spaceId, @Param("manifestId") Long manifestId);

    /**
     * 获取变量清单使用变量信息
     *
     * @param spaceId    空间ID
     * @param variableId 变量ID
     * @return VarProcessManifestVariable  List
     */
    @Select("SELECT vpmv.variable_id, vpmv.manifest_id from var_process_manifest_variable vpmv\n"
            + "            INNER JOIN var_process_manifest vpm ON  vpmv.manifest_id = vpm.id\n"
            + "            WHERE vpmv.var_process_space_id = #{spaceId} and vpmv.variable_id = #{variableId}"
            + "              and vpm.state in('UP') and vpm.delete_flag = 1\n")
    List<VarProcessManifestVariable> getManifestUseVariableList(@Param("spaceId") Long spaceId, @Param("variableId") Long variableId);

    /**
     * 查询变量清单版本详情 - 发布变量清单 信息
     *
     * @param spaceId    变量空间 ID
     * @param manifestId 变量清单 ID
     * @return 变量清单版本详情 - 发布变量清单 DTO List
     */
    List<VariableManifestPublishingVariableDTO> getPublishingVariableInfo(@Param("spaceId") Long spaceId, @Param("manifestId") Long manifestId);

    /**
     * 根据清单id查到变量使用信息
     * @param manifestIds 清单id list
     * @return list
     */
    @Select({
            "<script>",
            "select vpv.name ,vpv.description ,vpv.label ,vpv.data_type ,vpmv.manifest_id \n"
                    + "from var_process_variable vpv\n"
                    + "join var_process_manifest_variable vpmv on vpv.id = vpmv.variable_id\n"
                    + "where manifest_id in "
                    + "<foreach collection='manifestIds' item='manifestId' open='(' separator=',' close=')'>#{manifestId}</foreach>", "</script>"})
    List<ManifestVariableDto> findmanifestVariables(@Param("manifestIds") List<Long> manifestIds);

    /**
     * 查询所有被使用的变量id
     * @return set
     */
    @Select("select distinct vpmv.variable_id  from var_process_manifest_variable vpmv")
    Set<Long> findUsedVariables();

    /**
     * 通过变量清单id获取变量信息
     *
     * @param spaceId    空间ID
     * @param manifestId 变量清单ID
     * @return VarProcessVariableDto  List
     */
    @Select("SELECT vpv.name,vpv.data_type,vpmv.is_index,vpmv.col_role from var_process_manifest_variable vpmv\n"
            + "INNER JOIN var_process_variable vpv ON vpmv.variable_id = vpv.id\n"
            + "WHERE vpmv.var_process_space_id = #{spaceId}\n"
            + "and vpmv.manifest_id = #{manifestId} and vpv.delete_flag = 1"
    )
    List<VarProcessVariableDto> getVariableInfosByManifestId(@Param("spaceId")Long spaceId, @Param("manifestId") Long manifestId);
}
