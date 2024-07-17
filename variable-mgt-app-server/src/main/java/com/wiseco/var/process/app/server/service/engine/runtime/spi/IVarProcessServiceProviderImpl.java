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
import com.wiseco.decision.engine.var.runtime.api.IVarProcessServiceProvider;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessService;
import com.wiseco.var.process.app.server.service.VarProcessServiceService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 变量加工-服务实现
 *
 * @author taodizhou
 * @since 2022/6/15 10:46.
 * Copyright 2022 Wiseco Tech, All Rights Reserved.
 */
@Component
public class IVarProcessServiceProviderImpl implements IVarProcessServiceProvider {

    @Autowired
    private VarProcessServiceService varProcessServiceService;
    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Override
    public com.wiseco.decision.model.engine.VarProcessService getService(Long serviceId) {
        VarProcessManifest manifest = varProcessManifestService.getById(serviceId);
        String identifier = manifest.getIdentifier();
        com.wiseco.decision.model.engine.VarProcessService varProcessService = new com.wiseco.decision.model.engine.VarProcessService();
        varProcessService.setCode(identifier);
        return varProcessService;
    }

    @Override
    public com.wiseco.decision.model.engine.VarProcessService getService(Long spaceId, Long serviceCode, Integer... delFlags) {
        return ObjectUtils.clone(varProcessServiceService.getOne((new QueryWrapper<VarProcessService>().lambda()
                .eq(VarProcessService::getVarProcessSpaceId, spaceId)
                .eq(VarProcessService::getCode, serviceCode)
                .in(VarProcessService::getDeleteFlag, delFlags))), com.wiseco.decision.model.engine.VarProcessService.class);
    }


}
