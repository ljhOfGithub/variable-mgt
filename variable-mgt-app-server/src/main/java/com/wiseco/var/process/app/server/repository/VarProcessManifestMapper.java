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
import com.wiseco.var.process.app.server.controller.vo.output.ManifestListOutputVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.service.dto.ManifestListQueryDto;
import com.wiseco.var.process.app.server.service.dto.ServiceUsingManifestDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量清单表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessManifestMapper extends BaseMapper<VarProcessManifest> {

    /**
     * 获取变量清单列表
     *
     * @param page     分页信息
     * @param queryDto 查询条件
     * @return 变量清单列表
     */
    IPage<VarProcessManifest> getManifestList(Page<ManifestListOutputVo> page, @Param("queryDto") ManifestListQueryDto queryDto);

    /**
     * 获取使用该清单的服务信息
     *
     * @param manifestId 清单 ID
     * @param spaceId 空间id
     * @return 清单服务信息
     */
    @Select("select vpsm.service_id, vpsm.manifest_role, vpsm.current_execute_count as excuted_count,\n"
            + "vprs.service_name as name, vprs.service_code as code, vpsv.state, vprs.category_id,vpsv.service_version  as version \n"
            + "from var_process_service_manifest vpsm \n"
            + "join var_process_service_version vpsv on vpsv.id = vpsm.service_id \n"
            + "join var_process_realtime_service vprs on vprs.id = vpsv.service_id "
            + "where vprs.space_id = #{spaceId} AND vpsm.manifest_id = #{manifestId}")
    List<ServiceUsingManifestDto> findUsingService(@Param("manifestId") Long manifestId,@Param("spaceId") Long spaceId);

    /**
     * 根据服务ID获取它关联的变量清单名称
     *
     * @param serviceId 服务ID
     * @return 服务ID所关联的变量清单名称
     */
    List<String> getManifestNameByServiceId(@Param("serviceId") Long serviceId);

}
