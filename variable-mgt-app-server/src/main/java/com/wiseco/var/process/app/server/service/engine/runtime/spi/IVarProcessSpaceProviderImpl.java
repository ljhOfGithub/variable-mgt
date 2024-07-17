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
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.var.runtime.api.IVarProcessSpaceProvider;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 变量加工-空间数据实现
 *
 * @author taodizhou
 * @since 2022/6/15 10:46.
 * Copyright 2022 Wiseco Tech, All Rights Reserved.
 */
@Component
public class IVarProcessSpaceProviderImpl implements IVarProcessSpaceProvider {

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Override
    public com.wiseco.decision.model.engine.VarProcessSpace getSpace(Long spaceId) {
        return ObjectUtils.clone(varProcessSpaceService.getById(spaceId), com.wiseco.decision.model.engine.VarProcessSpace.class);
    }

    @Override
    public com.wiseco.decision.model.engine.VarProcessSpace getSpace(String spaceCode) {
        return ObjectUtils.clone(varProcessSpaceService.getOne((new QueryWrapper<VarProcessSpace>().lambda()
                .eq(VarProcessSpace::getCode, spaceCode)
                .eq(VarProcessSpace::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))), com.wiseco.decision.model.engine.VarProcessSpace.class);
    }
}
