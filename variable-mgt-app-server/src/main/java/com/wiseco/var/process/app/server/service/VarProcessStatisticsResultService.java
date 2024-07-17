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
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsResult;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计分析结果表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessStatisticsResultService extends IService<VarProcessStatisticsResult> {
    /**
     * getPageList
     *
     * @param page 分页
     * @param queryVO 查询
     * @return IPage
     */
    IPage<Map<String, Object>> getPageList(IPage page, VarProcessStatisticsResultQueryVO queryVO);

    /**
     * 获取实时服务PSI数据
     * @param configIds 配置id
     * @return 实时服务PSI数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds);

    /**
     * 获取实时服务IV数据
     * @param configIds 配置id
     * @return 实时服务IV数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds);

    /**
     * 获取实时服务缺失值数据
     * @param configIds 配置id
     * @return 实时服务缺失值数据
     */
    List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds);
}
