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
package com.wiseco.var.process.app.server.service.manifest;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestOutsideServiceDto;

import java.util.List;

/**
 * <p>
 * 变量发布接口-外部服务关系表 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-19
 */
public interface VarProcessManifestOutsideService extends IService<VarProcessManifestOutside> {
    /**
     * getManifestOutsideService
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return VarProcessManifestOutsideServiceDto
     */
    List<VarProcessManifestOutsideServiceDto> getManifestOutsideService(Long spaceId, Long manifestId);

}
