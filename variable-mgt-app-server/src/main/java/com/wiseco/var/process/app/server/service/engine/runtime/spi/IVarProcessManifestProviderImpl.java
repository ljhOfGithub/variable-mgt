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
package com.wiseco.var.process.app.server.service.engine.runtime.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.boot.commons.util.ObjectUtils;
import com.wiseco.decision.engine.var.runtime.api.IVarProcessInterfaceProvider;
import com.wiseco.decision.model.engine.VarProcessInterface;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 变量加工-服务接口实现
 *
 * @author taodizhou
 * @since 2022/6/15 10:46.
 * Copyright 2022 Wiseco Tech, All Rights Reserved.
 */
@Component
public class IVarProcessManifestProviderImpl implements IVarProcessInterfaceProvider {

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Override
    public com.wiseco.decision.model.engine.VarProcessInterface getInterface(Long manifestId) {
        VarProcessManifest manifest = varProcessManifestService.getById(manifestId);
        VarProcessInterface processInterface = new VarProcessInterface();
        processInterface.setVarProcessSpaceId(manifest.getVarProcessSpaceId());
        processInterface.setServiceId(manifest.getServiceId());
        processInterface.setVersion(manifest.getVersion());
        processInterface.setState(manifest.getState().getCode());
        processInterface.setSerialNo(manifest.getSerialNo());
        processInterface.setDescription(manifest.getDescription());
        processInterface.setSchemaSnapshot(manifest.getSchemaSnapshot());
        processInterface.setDeleteFlag(manifest.getDeleteFlag());
        processInterface.setParentInterfaceId(manifest.getParentManifestId());
        processInterface.setContent(manifest.getContent());
        processInterface.setCreatedUser(manifest.getCreatedUser());
        processInterface.setUpdatedUser(manifest.getUpdatedUser());
        processInterface.setCreatedTime(manifest.getCreatedTime());
        processInterface.setUpdatedTime(manifest.getUpdatedTime());
        processInterface.setId(manifest.getId());

        return processInterface;
    }

    @Override
    public com.wiseco.decision.model.engine.VarProcessInterface getInterface(Long spaceId, Long manifestId, Integer... states) {
        return ObjectUtils.clone(varProcessManifestService.getOne((new QueryWrapper<VarProcessManifest>().lambda()
                .eq(VarProcessManifest::getVarProcessSpaceId, spaceId)
                .eq(VarProcessManifest::getId, manifestId)
                .in(VarProcessManifest::getState, states))), com.wiseco.decision.model.engine.VarProcessInterface.class);
    }
}
