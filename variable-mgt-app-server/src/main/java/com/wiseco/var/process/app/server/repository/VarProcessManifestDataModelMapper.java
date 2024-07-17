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
import com.wiseco.var.process.app.server.controller.vo.output.ManifestForRealTimeServiceVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.service.dto.VarProcessDataModelDto;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelManifestUseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 变量清单-数据模型映射表 Mapper 接口
 * </p>
 *
 * @author liaody
 * @since 2022-08-24
 */
public interface VarProcessManifestDataModelMapper extends BaseMapper<VarProcessManifestDataModel> {


    /**
     * 通过数据模型信息获取变量清单
     *
     * @param spaceId       空间id
     * @param objectName    对象名称
     * @param objectVersion 对象版本
     * @return VariableOutsideServiceReferenceListDto
     */
    @Select("SELECT vpm.id,vpc.name as allClass,vpm.version,vpm.state,vpm.var_manifest_name as varManifestName from var_process_manifest_data_model vpmdm\n"
            + "INNER JOIN var_process_manifest vpm on vpmdm.manifest_id=vpm.id\n"
            + "INNER JOIN  var_process_category vpc on vpc.id=vpm.category_id\n"
            + "WHERE  vpm.delete_flag=1 and vpc.delete_flag=1 and\n"
            + "vpmdm.object_name=#{objectName} and vpmdm.object_version=#{objectVersion}")
    List<VariableDataModelManifestUseVo> getManifestUseDataModel(@Param("spaceId") Long spaceId, @Param("objectName") String objectName,
                                                                 @Param("objectVersion") Integer objectVersion);

    /**
     * 获取变量清单信息
     *
     * @param spaceId       空间id
     * @param objectName    对象名称
     * @param objectVersion 对象版本
     * @return ManifestForRealTimeServiceVO
     */
    @Select("SELECT vpsm.service_id as serviceId,vpsm.manifest_id as manifestId \n" + "from var_process_manifest_data_model vpmdm\n"
            + "INNER JOIN var_process_manifest vpm on vpmdm.manifest_id=vpm.id\n"
            + "INNER JOIN var_process_service_manifest vpsm on vpsm.manifest_id=vpm.id\n"
            + "WHERE vpm.delete_flag=1 and vpmdm.object_name=#{objectName} and vpmdm.object_version=#{objectVersion}")
    List<ManifestForRealTimeServiceVO> getManifestForRealTimeService(@Param("spaceId") Long spaceId, @Param("objectName") String objectName,
                                                                     @Param("objectVersion") Integer objectVersion);

    /**
     * 获取变量清单提交后使用的数据模型信息
     *
     * @param manifestId 变量清单id
     * @return 数据模型表
     */
    @Select("select vpmdm.model_query_condition, vpdm.id,vpdm.version ,vpdm.object_label ,vpdm.object_name ,vpdm.object_source_type ,vpdm.object_source_info ,vpdm.source_property_num ,vpdm.extend_property_num \n"
            + "from var_process_data_model vpdm \n"
            + "join var_process_manifest_data_model vpmdm \n"
            + "on vpmdm .object_name =vpdm .object_name \n"
            + "and vpmdm .object_version = vpdm.version \n" + "where vpmdm .manifest_id = #{manifestId}")
    List<VarProcessDataModelDto> getDataModelsAfterSubmit(@Param("manifestId") Long manifestId);

    /**
     * 根据清单id拿到数据模型json content
     * @param manifestIds 清单id list
     * @return 清单id，数据模型 content
     */
    @Select("<script>"
            + "select vpmdm.manifest_id ,vpdm.content from var_process_manifest_data_model vpmdm \n"
            + "join var_process_data_model vpdm \n"
            + "on vpmdm.object_name = vpdm.object_name and vpmdm.object_version = vpdm.version \n"
            + "where vpdm.object_source_type = 'OUTSIDE_PARAM' and  vpmdm.manifest_id in "
            + "<foreach collection='manifestIds' item='manifestId' open='(' separator=',' close=')'>" + "#{manifestId}" + "</foreach>"
            + "group by vpmdm.manifest_id,vpdm.object_name "
            + "</script>")
    @Results({
            @Result(property = "manifestId", column = "manifest_id"),
            @Result(property = "content", column = "content")
    })
    List<Map<String, Object>> findVariableManifestDataModelMappingVos(@Param("manifestIds") List<Long> manifestIds);

    /**
     * 获取数据模型信息
     *
     * @param manifests 清单id列表
     * @return 变量空间数据模型信息列表
     */
    @Select({
            "<script>",
            "select distinct\n" + "\tvpdm.id,\n" + "\tvpmdm.object_name,\n" + "\tvpdm.object_label,\n" + "\t MAX(vpdm.version) as version \n"
                    + "from\n" + "\tvar_process_manifest_data_model vpmdm\n" + "join var_process_data_model vpdm on\n"
                    + "\tvpdm.object_name = vpmdm.object_name\n" + "\tand vpdm.version = vpmdm.object_version", "WHERE vpmdm.manifest_id IN ",
            "<foreach collection='manifests' item='manifest' open='(' separator=',' close=')'>", "#{manifest}", "</foreach>", "group by vpmdm.object_name,vpdm.object_label,vpdm.id",
            "</script>"})
    List<VarProcessDataModel> getDataModelInfos(@Param("manifests") List<Long> manifests);

    /**
     * 获取最大版本的数据模型
     * @param objectName 对象名称(英文名)
     * @return 数据模型的列表
     */
    VarProcessDataModel getMaxVersionDataModel(@Param("objectName") String objectName);

    /**
     * 查询所有被清单使用的数据模型
     * @return set
     */
    @Select("select distinct vpdm.id from var_process_data_model vpdm \n"
            + "join var_process_manifest_data_model vpmdm on vpmdm.object_name = vpdm.object_name and vpmdm.object_version = vpdm.version ")
    Set<Long> findAllUsedModelIds();
}
