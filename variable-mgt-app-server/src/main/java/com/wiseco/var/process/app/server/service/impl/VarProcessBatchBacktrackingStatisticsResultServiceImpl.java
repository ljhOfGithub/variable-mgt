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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingStatisticsResultMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsResult;
import com.wiseco.var.process.app.server.service.BacktrackingStatisticsResultService;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 批量回溯统计分析结果表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessBatchBacktrackingStatisticsResultServiceImpl extends ServiceImpl<VarProcessBatchBacktrackingStatisticsResultMapper, VarProcessBatchBacktrackingStatisticsResult> implements
        BacktrackingStatisticsResultService {
    @Resource
    private VarProcessBatchBacktrackingStatisticsResultMapper resultMapper;

    @Override
    public IPage<Map<String, Object>> getPageList(IPage page, BacktrackingStatisticsResultQueryVO queryVO) {
        return resultMapper.getPageList(page, queryVO);
    }

    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds) {
        return resultMapper.getOverviewTargetPsi(configIds);
    }

    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds) {
        return resultMapper.getOverviewTargetIv(configIds);
    }

    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds) {
        return resultMapper.getOverviewTargetMr(configIds);
    }


}
