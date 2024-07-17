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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVar;
import com.wiseco.var.process.app.server.service.dto.ManifestVarForRealTimeServiceVarPathDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量清单-引用数据模型变量关系表 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-14
 */
public interface VarProcessManifestVarMapper extends BaseMapper<VarProcessManifestVar> {

    /**
     * 查询变量-引用数据模型变量关系相关信息
     *
     * @param spaceId 空间id
     * @return VariableUseVarPathDto List
     */
    @Select("select vpm.id, vpmv.var_path, vpmv.parameter_type, vpm.version, vprs.service_name as name, vpm.state as status from var_process_manifest_var vpmv\n"
            + "INNER JOIN var_process_manifest vpm ON vpmv.manifest_id = vpm.id \n"
            + "INNER JOIN var_process_service_version vpsv ON vpsv.id = vpm.service_id\n"
            + "join var_process_realtime_service vprs on vprs.id = vpsv.id \n"
            + "where vpm.delete_flag = 1 and vprs.delete_flag = 1 and vpsv.delete_flag = 1 and vpmv.is_self = 1 and vpm.var_process_space_id = 1 \n"
            + "order by vpsv.id desc,vpm.version desc"
    )
    List<VariableUseVarPathDto> getVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 查询变量-引用数据模型变量关系相关信息
     *
     * @param spaceId 空间id
     * @return VariableUseVarPathDto List
     */
    @Select("select vpm.id, vpm.var_manifest_name as name,vpmv.var_path, vpmv.parameter_type, vpm.version,vpc.name as allClass, vpm.state as status from var_process_manifest_var vpmv\n"
            + " INNER JOIN var_process_manifest vpm ON vpmv.manifest_id = vpm.id "
            + " INNER JOIN var_process_category vpc ON vpc.id = vpm.category_id "
            + " where vpm.delete_flag = 1"
            + " and vpm.var_process_space_id = #{spaceId} order by vpm.version desc")
    List<VariableUseVarPathDto> getManifestVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 查询实时服务引用关系相关信息
     *
     * @param spaceId 空间id
     * @return ManifestVarForRealTimeServiceVarPathDto List
     */
    @Select("select vpm.id as manifestId,vpsm.service_id as serviceId, vpm.var_manifest_name as name,vpmv.var_path, vpmv.parameter_type, vpm.version,vpm.state as status from var_process_manifest_var vpmv\n"
            + " INNER JOIN var_process_manifest vpm ON vpmv.manifest_id = vpm.id "
            + " INNER JOIN var_process_service_manifest vpsm ON vpsm.manifest_id = vpm.id "
            + " where vpm.delete_flag = 1"
            + " and vpm.var_process_space_id = #{spaceId} order by vpm.version desc")
    List<ManifestVarForRealTimeServiceVarPathDto> getManifestVarForRealTimeService(@Param("spaceId") Long spaceId);

    /**
     * 查询所有清单流程中直接使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    @Select("select distinct vpmv.var_path,vpmv.parameter_type,vpm.version from var_process_manifest_var vpmv inner join var_process_manifest vpm on vpmv.manifest_id = vpm.id \n"
            + "where vpm.delete_flag = 1 and vpmv.is_self = 1 and vpm.var_process_space_id = #{spaceId} order by vpm.version desc")
    List<VariableUseVarPathDto> getSelfVarUseList(@Param("spaceId") Long spaceId);
}
