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
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.VarProcessStatisticsResultMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsResult;
import com.wiseco.var.process.app.server.service.VarProcessStatisticsResultService;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计分析结果 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessStatisticsResultServiceImpl extends ServiceImpl<VarProcessStatisticsResultMapper, VarProcessStatisticsResult> implements
        VarProcessStatisticsResultService {

    @Resource
    private VarProcessStatisticsResultMapper statisticsResultMapper;

    @Override
    public IPage<Map<String, Object>> getPageList(IPage page, VarProcessStatisticsResultQueryVO queryVO) {
        return statisticsResultMapper.getPageList(page, queryVO);
    }


    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds) {
        return statisticsResultMapper.getOverviewTargetPsi(configIds);
    }

    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds) {
        return statisticsResultMapper.getOverviewTargetIv(configIds);
    }

    @Override
    public List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds) {
        return statisticsResultMapper.getOverviewTargetMr(configIds);
    }
}
