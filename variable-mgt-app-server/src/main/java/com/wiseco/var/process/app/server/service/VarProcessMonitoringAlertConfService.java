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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessMonitoringAlertConfMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertConf;
import com.wiseco.var.process.app.server.service.dto.MonitoringConfigurationPageQueryDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wiseco
 * @since 睿信2.3
 */
@Service
public class VarProcessMonitoringAlertConfService extends ServiceImpl<VarProcessMonitoringAlertConfMapper, VarProcessMonitoringAlertConf> {

    @Resource
    private VarProcessMonitoringAlertConfMapper varProcessMonitoringAlertConfMapper;

    /**
     * 分页查询
     *
     * @param page    分页
     * @param queryDto 输入
     * @return 页面
     */
    public IPage<VarProcessMonitoringAlertConf> getPage(Page page, MonitoringConfigurationPageQueryDto queryDto) {
        return varProcessMonitoringAlertConfMapper.getPage(page, queryDto);
    }
}