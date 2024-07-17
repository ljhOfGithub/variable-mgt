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
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringResultPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringResultPageOutputVO;
import com.wiseco.var.process.app.server.repository.VarProcessMonitoringAlertResultMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wiseco
 * @since 睿信2.3
 */
@Service
public class VarProcessMonitoringAlertResultService extends ServiceImpl<VarProcessMonitoringAlertResultMapper, MonitoringResultPageOutputVO> {

    @Resource
    private VarProcessMonitoringAlertResultMapper varProcessMonitoringAlertResultMapper;

    /**
     * 分页查询
     *
     * @param page    分页
     * @param inputVO 输入
     * @return 页面
     */
    public IPage<MonitoringResultPageOutputVO> getResultPage(Page page, MonitoringResultPageInputVO inputVO) {
        return varProcessMonitoringAlertResultMapper.getResultPage(page, inputVO);
    }
}
