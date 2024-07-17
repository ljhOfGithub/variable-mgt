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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsResult;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 批量回溯统计分析结果表 Mapper 接口
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-11
 */
public interface VarProcessBatchBacktrackingStatisticsResultMapper extends BaseMapper<VarProcessBatchBacktrackingStatisticsResult> {
    /**
     * 批量回溯统计分析结果
     *
     * @param page    分页
     * @param queryVO 查询参数
     * @return 分页结果
     */
    IPage<Map<String, Object>> getPageList(IPage page, @Param("queryVO") BacktrackingStatisticsResultQueryVO queryVO);

    /**
     * 获取批量回溯PSI数据
     * @param configIds 配置id
     * @return 批量回溯PSI数据
     */
    @Select("<script>"
            + "select var_name as name,psi_result as target  from var_process_batch_backtracking_statistics_result as vpbbsr \n"
            + "left join var_process_batch_backtracking_statistics_config vpbbsc on vpbbsr.backtracking_config_id = vpbbsc.id \n"
            + "where vpbbsr.psi_result is not null and vpbbsr.var_name != ''"
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + "AND vpbbsc.id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>")
    List<OverviewTargetStatisticsDto> getOverviewTargetPsi(@Param("configIds") List<Long> configIds);

    /**
     * 获取批量回溯IV数据
     *
     * @param configIds 配置id
     * @return 批量回溯IV数据
     */
    @Select("<script>"
            + "select var_name as name,iv_result as target  from var_process_batch_backtracking_statistics_result  as vpbbsr \n"
            + "left join var_process_batch_backtracking_statistics_config vpbbsc on vpbbsr.backtracking_config_id = vpbbsc.id \n"
            + "left join var_process_batch_backtracking vpbb on vpbbsc.backtracking_id = vpbb.id \n"
            + "where iv_result is not null and var_name != ''"
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + "AND vpbbsc.id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>")
    List<OverviewTargetStatisticsDto> getOverviewTargetIv(@Param("configIds") List<Long> configIds);

    /**
     * 获取批量回溯缺失值数据
     *
     * @param configIds 配置id
     * @return 批量回溯缺失值数据
     */
    @Select("<script>"
            + "select var_name as name,missing_ratio as target  from var_process_batch_backtracking_statistics_result  as vpbbsr \n"
            + "left join var_process_batch_backtracking_statistics_config vpbbsc on vpbbsr.backtracking_config_id = vpbbsc.id \n"
            + "left join var_process_batch_backtracking vpbb on vpbbsc.backtracking_id = vpbb.id \n"
            + "where missing_ratio is not null and var_name != ''"
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + "AND vpbbsc.id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>")
    List<OverviewTargetStatisticsDto> getOverviewTargetMr(@Param("configIds") List<Long> configIds);
}
