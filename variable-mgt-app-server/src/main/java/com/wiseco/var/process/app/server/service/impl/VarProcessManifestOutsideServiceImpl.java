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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessManifestOutsideMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestOutsideServiceDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestOutsideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量发布接口-外部服务关系表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-19
 */
@Service
public class VarProcessManifestOutsideServiceImpl extends ServiceImpl<VarProcessManifestOutsideMapper, VarProcessManifestOutside> implements
        VarProcessManifestOutsideService {

    @Autowired
    private VarProcessManifestOutsideMapper varProcessManifestOutsideMapper;

    @Override
    public List<VarProcessManifestOutsideServiceDto> getManifestOutsideService(Long spaceId, Long manifestId) {
        return varProcessManifestOutsideMapper.getManifestOutsideService(spaceId, manifestId);
    }
}
