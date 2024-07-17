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
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsResult;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;


import java.util.List;
import java.util.Map;

/**
 * <p>
 * 批量回溯统计分析结果表 服务类
 * </p>
 *
 * @author wiseco
 */
public interface BacktrackingStatisticsResultService extends IService<VarProcessBatchBacktrackingStatisticsResult> {
    /**
     * getPageList
     *
     * @param page    分页
     * @param queryVO 需要的参数
     * @return 分页结果
     */
    IPage<Map<String, Object>> getPageList(IPage page, BacktrackingStatisticsResultQueryVO queryVO);

    /**
     * 获取批量回溯PSI数据
     * @param configIds 配置id
     * @return 批量回溯PSI数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds);

    /**
     * 获取批量回溯IV数据
     * @param configIds 配置id
     * @return 批量回溯IV数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds);

    /**
     * 获取批量回溯缺失值数据
     * @param configIds 配置id
     * @return 批量回溯缺失值数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds);
}
