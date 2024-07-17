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
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsResult;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计分析结果表 Mapper 接口
 * </p>
 *
 * @author yaoshun
 * @since 2023-09-11
 */
public interface VarProcessStatisticsResultMapper extends BaseMapper<VarProcessStatisticsResult> {
    /**
     * 统计分析结果数据分页查询
     *
     * @param page    分页信息
     * @param queryVO 变量回溯统计结果查询
     * @return 统计分析结果分页数据
     */
    IPage<Map<String, Object>> getPageList(IPage page, @Param("queryVO") VarProcessStatisticsResultQueryVO queryVO);

    /**
     * 获取实时服务的PSI数据
     * @param configIds 配置id
     * @return 实时服务的PSI数据
     */
    @Select("<script>"
            + "select var_name as name,psi_result as target  from var_process_statistics_result \n"
            + "where psi_result is not null and var_name != '' "
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + " AND statistics_config_id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>")
    List<OverviewTargetStatisticsDto> getOverviewTargetPsi(@Param("configIds") List<Long> configIds);

    /**
     * 获取实时服务的IV数据
     * @param configIds 配置id
     * @return 实时服务的IV数据
     */
    @Select("<script>"
            + "select var_name as name,iv_result as target  from var_process_statistics_result \n"
            + "where iv_result is not null and var_name != '' "
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + " AND statistics_config_id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>"
    )
    List<OverviewTargetStatisticsDto> getOverviewTargetIv(@Param("configIds") List<Long> configIds);

    /**
     * 获取实时服务的缺失值数据
     * @param configIds 配置id
     * @return 实时服务的缺失值数据
     */
    @Select("<script>"
            + "select var_name as name,missing_ratio as target  from var_process_statistics_result \n"
            + "where missing_ratio is not null and var_name != '' "
            + "<if test = 'configIds != null and configIds.size() > 0'>"
            + " AND statistics_config_id IN "
            + "<foreach item='configId' collection='configIds' open='(' separator=',' close=')'>"
            + "#{configId}"
            + "</foreach>"
            + "</if>"
            + "</script>"
    )
    List<OverviewTargetStatisticsDto> getOverviewTargetMr(@Param("configIds") List<Long> configIds);
}
