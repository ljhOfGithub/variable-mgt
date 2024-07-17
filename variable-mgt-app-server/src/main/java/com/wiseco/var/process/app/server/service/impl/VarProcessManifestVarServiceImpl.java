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
import com.wiseco.var.process.app.server.repository.VarProcessManifestVarMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVar;
import com.wiseco.var.process.app.server.service.dto.ManifestVarForRealTimeServiceVarPathDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量清单-引用数据模型变量关系表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-14
 */
@Service
public class VarProcessManifestVarServiceImpl extends ServiceImpl<VarProcessManifestVarMapper, VarProcessManifestVar> implements
        VarProcessManifestVarService {

    @Autowired
    private VarProcessManifestVarMapper varProcessManifestVarMapper;

    @Override
    public List<VariableUseVarPathDto> getVarUseList(Long spaceId) {
        return varProcessManifestVarMapper.getVarUseList(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getManifestVarUseList(Long spaceId) {
        return varProcessManifestVarMapper.getManifestVarUseList(spaceId);
    }

    @Override
    public List<ManifestVarForRealTimeServiceVarPathDto> getManifestVarForRealTimeService(Long spaceId) {
        return varProcessManifestVarMapper.getManifestVarForRealTimeService(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getSelfVarUseList(Long spaceId) {
        return varProcessManifestVarMapper.getSelfVarUseList(spaceId);
    }

}
