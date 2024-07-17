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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceDomain;
import com.wiseco.var.process.app.server.service.dto.VariableServiceAuthorizationInfoDto;

/**
 * <p>
 * 实时服务-授权领域对应关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessServiceDomainService extends IService<VarProcessServiceDomain> {

    /**
     * 查询实时服务授权信息
     *
     * @param pageConfig 分页配置
     * @param serviceId  实时服务 ID
     * @param keywords   决策领域编码/名称关键词
     * @return 实时服务授权信息 DTO 分页
     */
    IPage<VariableServiceAuthorizationInfoDto> findVariableServiceAuthorizationInfoPage(Page<VariableServiceAuthorizationInfoDto> pageConfig,
                                                                                        Long serviceId, String keywords);
}
