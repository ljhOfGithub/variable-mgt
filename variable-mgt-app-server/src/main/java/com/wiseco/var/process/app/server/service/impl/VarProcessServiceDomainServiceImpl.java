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
import com.wiseco.var.process.app.server.repository.VarProcessServiceDomainMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceDomain;
import com.wiseco.var.process.app.server.service.VarProcessServiceDomainService;
import com.wiseco.var.process.app.server.service.dto.VariableServiceAuthorizationInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 实时服务-授权领域对应关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessServiceDomainServiceImpl extends ServiceImpl<VarProcessServiceDomainMapper, VarProcessServiceDomain> implements
        VarProcessServiceDomainService {

    @Autowired
    private VarProcessServiceDomainMapper varProcessServiceDomainMapper;

    @Override
    public IPage<VariableServiceAuthorizationInfoDto> findVariableServiceAuthorizationInfoPage(Page<VariableServiceAuthorizationInfoDto> pageConfig,
                                                                                               Long serviceId, String keywords) {
        return varProcessServiceDomainMapper.findVariableServiceAuthorizationInfoPage(pageConfig, serviceId, keywords);
    }
}
