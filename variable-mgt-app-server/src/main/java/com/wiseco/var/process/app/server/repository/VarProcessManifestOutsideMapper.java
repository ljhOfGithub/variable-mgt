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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestOutsideServiceDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量清单-外部服务关系表 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-19
 */
public interface VarProcessManifestOutsideMapper extends BaseMapper<VarProcessManifestOutside> {

    /**
     * 查询变量清单-外部服务关系表
     *
     * @param spaceId    空间id
     * @param manifestId 变量清单id
     * @return 变量清单外数服务列表
     */
    @Select("SELECT ref.outside_service_id as outsideServiceId, os.name as outsideServiceName, os.code as outsideServiceCode,"
            + "ref.name as outsideRefName"
            + " from var_process_manifest_outside vpmo\n"
            + "INNER JOIN var_process_outside_ref ref on vpmo.outside_service_id=ref.outside_service_id and vpmo.var_process_space_id = ref.var_process_space_id "
            + "INNER JOIN outside_service os on ref.outside_service_id = os.id "
            + "WHERE os.delete_flag = 1 and ref.var_process_space_id = #{spaceId} and vpmo.manifest_id = #{manifestId}")
    List<VarProcessManifestOutsideServiceDto> getManifestOutsideService(@Param("spaceId") Long spaceId, @Param("manifestId") Long manifestId);

}
