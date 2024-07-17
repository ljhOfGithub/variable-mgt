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
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.dto.ServiceManifestName;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelRealTimeServiceUseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface VarProcessServiceManifestMapper extends BaseMapper<VarProcessServiceManifest> {

    /**
     * 获取实时服务使用映射
     *
     * @param serviceId  服务 ID
     * @param manifestId 清单 ID
     * @return 变量或公共函数信息 VO
     */
    @Select("SELECT vpsv.id,vprs.service_name as name,vprs.service_code as code,vpc.name as allClass,vpsm.manifest_role as manifestRole,\n"
            + "vpsv.state,vpsm.current_execute_count as currentExecuteCount,vpsm.manifest_id as manifestId from var_process_service_manifest vpsm \n"
            + "INNER JOIN var_process_service_version vpsv on vpsm.service_id=vpsv.id\n"
            + "join var_process_realtime_service vprs on vprs.id = vpsv.service_id \n"
            + "INNER JOIN var_process_category vpc on vpc.id = vprs.category_id \n"
            + "WHERE  vpsv.id = #{serviceId} and vpsm.manifest_id = #{manifestId}  and vpsv.delete_flag=1 and vprs.delete_flag = 1")
    VariableDataModelRealTimeServiceUseVo getRealTimeServiceUseMapping(@Param("serviceId") Long serviceId, @Param("manifestId") Long manifestId);

    /**
     * 获取服务状态
     *
     * @param id 清单 ID
     * @return 变量空间服务信息列表
     */
    @Select("select vpsv.id ,vpsv.state from var_process_service_version vpsv\n"
            + "left join var_process_service_manifest vpsm on vpsm.service_id = vpsv.id \n"
            + "where vpsm.manifest_id = #{id}")
    List<VarProcessServiceVersion> getServiceState(Long id);

    /**
     * 获取实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     * @param variableIds 传入的变量Id的集合(单指标分析时传一个, 指标对比分析时传多个)
     * @return 实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     */
    List<ServiceManifestNameVo> getServiceManifestName(@Param("variableIds") List<Long> variableIds);

    /**
     * 获取单个指标对应的变量清单映射信息
     * @param variableId 变量Id
     * @return 单个指标对应的变量清单映射信息
     */
    List<ServiceManifestNameVo> getVariableAndManifestMapping(@Param("variableId") Long variableId);

    /**
     * 根据服务id查到正在/即将生效的主清单
     * @param serviceId 服务id
     * @return manifestId
     */
    Long findMainManifestByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 查询所有被使用的清单id
     * @param spaceId 空间id
     * @return set
     */
    @Select("select distinct vpsm.manifest_id  from var_process_service_manifest vpsm where var_process_space_id = #{spaceId}")
    Set<Long> findUsedManifests(@Param("spaceId") Long spaceId);

    /**
     * 根据服务版本id list查到使用的清单name list
     * @param serviceVersionIds 服务版本id list
     * @return list
     */
    @Select({"<script>",
            "select vpsv.id as serviceVersionId, vpm.var_manifest_name as manifestName\n"
                    + "from var_process_service_version vpsv \n"
                    + "left join var_process_service_manifest vpsm on vpsm.service_id = vpsv.id\n"
                    + "left join var_process_manifest vpm on vpm.id = vpsm.manifest_id\n"
                    + "where vpsv.delete_flag = 1 and vpsv.id in",
            "<foreach collection='serviceVersionIds' item='versionId' open='(' separator=',' close=')'>", "#{versionId}",
            "</foreach>", "</script>"})
    List<ServiceManifestName> findManifestNames(@Param("serviceVersionIds") List<Long> serviceVersionIds);

    /**
     * 获取清单详细信息
     *
     * @param manifests 清单id集合
     * @return 变量清单的详情 VO
     */
    @Select({
            "<script>",
            "SELECT vpm.id AS manifestId,vpm.var_manifest_name AS manifestName,COUNT(vpmv.manifest_id) AS countVariable,vpm.description AS description\n"
                    + "FROM var_process_manifest_variable vpmv\n" + "RIGHT JOIN var_process_manifest vpm\n" + "ON vpmv.manifest_id = vpm.id\n"
                    + "WHERE vpm.id IN", "<foreach collection='manifests' item='manifest' open='(' separator=',' close=')'>", "#{manifest}",
            "</foreach>", "GROUP BY vpm.id,vpm.var_manifest_name,vpm.description", "</script>"})
    List<ServiceManifestDetailOutputVo> getManifestDetail(@Param("manifests") List<Long> manifests);
}
