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
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutSideParamsOutputVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.service.dto.OutParamsQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelServiceDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量空间-数据模型 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-08-25
 */
public interface VarProcessDataModelMapper extends BaseMapper<VarProcessDataModel> {

    /**
     * 数据模型表
     *
     * @param page     分页
     * @param queryDto 查询参数
     * @return VarProcessDataModel
     */
    IPage<VarProcessDataModel> findPageList(Page page, @Param("queryDto") VariableDataModelQueryInputVO queryDto);

    /**
     * 获取最大版本号
     * @param spaceId 空间id
     * @param deptCodes 部门code
     * @param userNames 用户名
     * @return 最大版本号
     */
    List<VarProcessDataModel> findMaxVersionList(@Param("spaceId") Long spaceId,@Param("deptCodes") List<String> deptCodes,@Param("userNames") List<String> userNames);

    /**
     * 获取数据模型最大版本号
     *
     * @param sourceType 数据来源
     * @return 数据模型 List
     */
    List<VarProcessDataModel> getDataModelMaxVersionList(@Param("sourceType") String sourceType);

    /**
     * 通过对象名获取数据模型列表
     *
     * @param spaceId        空间id
     * @param objectNameList 对象名称列表
     * @return 数据模型 List
     */
    @Select({
            "<script>",
            "select id, object_name, object_label, source_property_num, extend_property_num," + " version, created_user, updated_user,"
                    + " created_time, updated_time" + " from var_process_data_model " + "WHERE var_process_space_id = #{spaceId} "
                    + " and object_name in ", "<foreach collection='objectNameList' item='objectName' open='(' separator=',' close=')'>",
            "#{objectName}", "</foreach>", " ORDER BY version desc", "</script>"})
    List<VarProcessDataModel> findListByObjectName(@Param("spaceId") Long spaceId, @Param("objectNameList") List<String> objectNameList);

    /**
     * 通过对象名获取相关数据
     *
     * @param spaceId        空间id
     * @param objectNameList 对象名称列表
     * @return VariableDataModelServiceDto
     */
    @Select({"<script>",
            "select vpmdm.object_name,vpmdm.object_version,vprs.service_name as name,vpm.version,vpm.state,vpv.varNums\n"
                    + "from var_process_manifest_data_model vpmdm\n"
                    + "inner join var_process_manifest vpm on vpmdm.manifest_id = vpm.id\n"
                    + "join var_process_service_version vpsv on vpsv.id = vpm.service_id \n"
                    + "inner join var_process_realtime_service vprs on vpsv.service_id = vprs.id\n"
                    + "left join (select manifest_id,count(*) as varNums from var_process_manifest_variable\n"
                    + "where var_process_space_id = #{spaceId} GROUP BY manifest_id) vpv on vpv.manifest_id = vpmdm.manifest_id\n"
                    + "where vpmdm.var_process_space_id = #{spaceId} and vpmdm.object_name in "
                    + "<foreach collection='objectNameList' item='objectName' open='(' separator=',' close=')'>" + "#{objectName}" + "</foreach>"
                    + "</script>"})
    List<VariableDataModelServiceDto> findServiceListByObjectName(@Param("spaceId") Long spaceId, @Param("objectNameList") List<String> objectNameList);

    /**
     * 根据变量清单 ID 查询绑定的数据模型
     *
     * @param manifestId 变量清单 ID
     * @param sourceType 来源
     * @return 变量加工数据模型 List
     */
    List<VarProcessDataModel> listDataModelSpecificVersion(@Param("manifestId") Long manifestId, @Param("sourceType") Integer sourceType);

    /**
     * 通过数据模型名称获取最大版本数据模型信息
     *
     * @param spaceId  空间id
     * @param nameList 名字list
     * @return 数据模型 List
     */
    List<VarProcessDataModel> findMaxVersionListByNames(@Param("spaceId") Long spaceId, @Param("nameList") List<String> nameList);

    /**
     * 获取符合条件的数据模型信息
     *
     * @param objectName    对象名
     * @param objectVersion 对象版本
     * @return 数据模型
     */
    VarProcessDataModel findByDataModelInfo(@Param("objectName") String objectName, @Param("version") Long objectVersion);

    /**
     * 通过数据模型名称获取最大版本信息
     *
     * @param nameList 名称列表
     * @return 数据模型 List
     */
    @Select({"<script>" + "select vpdm.id,\n" + "               vpdm.object_name,\n" + "               vpdm.object_label,\n"
            + "               vpdm.var_process_space_id,\n" + "               vpdm.source_property_num,\n"
            + "               vpdm.extend_property_num,\n" + "               vpdm.version,\n" + "               vpdm.created_user,\n"
            + "               vpdm.updated_user,\n" + "               vpdm.created_time,\n" + "               vpdm.updated_time,\n"
            + "               vpdm.content\n" + "        from var_process_data_model vpdm\n" + "          inner join (\n"
            + "            select object_name, max(version) as version\n" + "            from var_process_data_model\n"
            + "            GROUP BY object_name\n" + "          ) dmx on vpdm.object_name = dmx.object_name and vpdm.version = dmx.version\n"
            + "where vpdm.object_name in " + "<foreach collection='nameList' item='name' open='(' separator=',' close=')'>" + "   #{name}"
            + "</foreach>" + "</script>"})
    List<VarProcessDataModel> findMaxVersionModelsByNames(@Param("nameList") List<String> nameList);


    /**
     * 获取符合条件的数据模型信息
     *
     * @param objectName    对象名
     * @return 数据模型
     */

    VarProcessDataModel findByDataModelName(@Param("objectName") String objectName);

    /**
     * 根据数据模型名称拿到最大版本的content
     * @param modelNames 模型名称list
     * @return 数据模型list
     */
    @Select("<script>" + "select vpdm.version,vpdm.content,vpdm.object_name\n"
            + "FROM var_process_data_model vpdm\n"
            + "JOIN (select object_name,MAX(version) AS max_version\n"
            + "FROM var_process_data_model\n"
            + "WHERE object_name IN "
            + "<foreach collection='modelNames' item='name' open='(' separator=',' close=')'>" + "   #{name}" + "</foreach>"
            + "GROUP by object_name\n"
            + ") max_versions ON vpdm.object_name = max_versions.object_name AND vpdm.version = max_versions.max_version;\n" + "</script>")
    List<VarProcessDataModel> getMaxModelInfoByNames(@Param("modelNames") List<String> modelNames);

    /**
     * 获取外部参数
     *
     * @param pageConfig 入参对象列表分页信息
     * @param queryDto   外部参数查询数据
     * @return 入参对象分页数据
     */
    IPage<OutSideParamsOutputVo> findParams(@Param("pageConfig") Page<OutSideParamsOutputVo> pageConfig,
                                                  @Param("queryDto") OutParamsQueryDto queryDto);

    /**
     * 获取外部参数 count
     *
     * @param pageConfig 入参对象列表分页信息
     * @param queryDto   外部参数查询数据
     * @return long
     */
    Integer findParamsCount(@Param("pageConfig") Page<OutSideParamsOutputVo> pageConfig,
                                            @Param("queryDto") OutParamsQueryDto queryDto);
}
