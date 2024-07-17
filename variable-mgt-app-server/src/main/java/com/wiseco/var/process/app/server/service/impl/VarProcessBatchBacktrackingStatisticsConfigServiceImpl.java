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
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingStatisticsConfigMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsConfig;
import com.wiseco.var.process.app.server.service.VarProcessBatchBacktrackingStatisticsConfigService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 批量回溯分析配置表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessBatchBacktrackingStatisticsConfigServiceImpl extends ServiceImpl<VarProcessBatchBacktrackingStatisticsConfigMapper, VarProcessBatchBacktrackingStatisticsConfig> implements
        VarProcessBatchBacktrackingStatisticsConfigService {

    @Autowired
    VarProcessBatchBacktrackingStatisticsConfigMapper batchBacktrackingStatisticsConfigMapper;

    @Autowired
    AuthService authService;
    @Override
    public List<Long> findAccessConfig() {
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        return batchBacktrackingStatisticsConfigMapper.findAccessConfig(roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
    }
}