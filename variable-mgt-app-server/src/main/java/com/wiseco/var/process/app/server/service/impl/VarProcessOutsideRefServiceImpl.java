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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessOutsideRefMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量-外部服务引入对象表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessOutsideRefServiceImpl extends ServiceImpl<VarProcessOutsideRefMapper, VarProcessOutsideRef> implements
        VarProcessOutsideRefService {

    @Autowired
    private VarProcessOutsideRefMapper varProcessOutsideRefMapper;

    @Override
    public Integer countReferencedOutsideServiceNumber(Long spaceId) {
        return varProcessOutsideRefMapper.countReferencedOutsideServiceNumber(spaceId);
    }

    @Override
    public IPage<VariableSpaceReferencedOutsideServiceInfoDto> getVariableSpaceReferencedOutsideServiceInfoPage(Page<VariableSpaceReferencedOutsideServiceInfoDto> pageConfig,
                                                                                                                Long spaceId,
                                                                                                                List<String> outsideServiceState,
                                                                                                                List<String> referenceState,
                                                                                                                String keyword) {
        return varProcessOutsideRefMapper.getVariableSpaceReferencedOutsideServiceInfoPage(pageConfig, spaceId, outsideServiceState, referenceState,
                keyword);
    }

    @Override
    public List<VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto> getVariableSpaceReferencedOutsideServiceReceiverObjectInfo(Long spaceId) {
        return varProcessOutsideRefMapper.getVariableSpaceReferencedOutsideServiceReceiverObjectInfo(spaceId);
    }

    @Override
    public VarProcessOutsideRef findByDataModelId(Long id) {
        return varProcessOutsideRefMapper.findByDataModelId(id);
    }
}
