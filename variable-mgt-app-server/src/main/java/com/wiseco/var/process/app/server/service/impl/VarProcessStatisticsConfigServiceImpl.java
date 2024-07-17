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
import com.wiseco.var.process.app.server.repository.VarProcessStatisticsConfigMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsConfig;
import com.wiseco.var.process.app.server.service.VarProcessStatisticsConfigService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 统计分析配置 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessStatisticsConfigServiceImpl extends ServiceImpl<VarProcessStatisticsConfigMapper, VarProcessStatisticsConfig> implements
        VarProcessStatisticsConfigService {

    @Resource
    private VarProcessStatisticsConfigMapper statisticsConfigMapper;

    @Resource
    private AuthService authService;

    /**
     * 寻找当前的统计数据
     * @return 统计数据
     */
    @Override
    public List<VarProcessStatisticsConfig> findCurrentStatistics() {
        final String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return statisticsConfigMapper.findCurrentStatistics(now);
    }

    @Override
    public List<Long> findAccessConfig() {
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        return statisticsConfigMapper.findAccessConfig(roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
    }
}
