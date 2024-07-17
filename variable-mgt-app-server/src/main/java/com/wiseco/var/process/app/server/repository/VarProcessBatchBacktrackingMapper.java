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
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskCodeVO;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.service.dto.BacktrackingDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 批量回溯列表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2023-08-08
 */
@Mapper
public interface VarProcessBatchBacktrackingMapper extends BaseMapper<VarProcessBatchBacktracking> {

    /**
     * 分页查询批量回溯记录
     *
     * @param page     分页设置
     * @param queryDto 查询参数 DTO
     * @return 批量回溯详情 DTO 分页封装
     */
    IPage<BacktrackingDetailDto> findBacktrackingList(IPage page, @Param("queryDto") BacktrackingQueryDto queryDto);

    /**
     * 获取批量回溯详情
     *
     * @param id 批量回溯id
     * @return BacktrackingDetailDto
     */
    @Select("SELECT vpb.id, vpb.name, vpb.manifest_id, vpm.manifest_name FROM var_process_batch_backtracking vpb\n"
            + "        LEFT JOIN (SELECT id,var_manifest_name AS manifest_name FROM var_process_manifest) vpm ON vpb.manifest_id = vpm.id\n"
            + "WHERE vpb.id = #{id}")
    BacktrackingDetailDto findBacktrackingById(Long id);

    /**
     * 获取变量清单个数
     *
     * @param manifestId 变量清单id
     * @return 变量清单个数
     */
    @Select("SELECT COUNT(vpmv.manifest_id) AS countVariable\n" + "FROM var_process_manifest_variable vpmv\n"
            + "RIGHT JOIN var_process_manifest vpm\n" + "ON vpmv.manifest_id = vpm.id\n" + "WHERE vpm.id IN = #{manifestId}")
    Integer getVariableSizeBymanifestId(Long manifestId);

    /**
     * 获取结果表名
     *
     * @param id 批量回溯id
     * @return 结果表名
     */
    @Select("SELECT result_table FROM var_process_batch_backtracking WHERE id = #{id}")
    String findBacktrackingResultTable(Long id);

    /**
     * 获取记录标识
     *
     * @param id 批量回溯id
     * @return BacktrackingTaskCodeVO
     */
    @Select("SELECT task.code, task.start_time AS startTime FROM \n" + "var_process_batch_backtracking batch \n"
            + "inner join var_process_batch_backtracking_task task \n" + "on  batch.id = task.backtracking_id  \n" + "WHERE batch.id = #{id}")
    List<BacktrackingTaskCodeVO> findBacktrackingTaskIds(Long id);

    /**
     * 获取批量回溯任务首次执行的任务批次号
     *
     * @param id 批量回溯id
     * @return BacktrackingTaskCodeVO
     */
    @Select("SELECT task.code FROM \n" + "var_process_batch_backtracking batch \n"
            + "inner join var_process_batch_backtracking_task task \n" + "on  batch.id = task.backtracking_id  \n" + "WHERE batch.id = #{id} ORDER BY task.start_time ASC  \n"
            + "LIMIT 1")
    String findBacktrackingFirstTaskCode(Long id);

    /**
     * 获取任务记录标识
     *
     * @param backtrackingId 批量回溯id
     * @return 记录标识
     */
    @Select("SELECT vpbt.code FROM var_process_batch_backtracking_task vpbt where vpbt.backtracking_id = #{backtrackingId} ORDER BY vpbt.start_time DESC")
    List<String> getBacktrackingTaskCodes(Long backtrackingId);

    /**
     * 获取所有被批量回溯作为流水号使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    @Select("select distinct vpbb.serial_no as varPath from var_process_batch_backtracking vpbb \n"
            + "WHERE delete_flag = 1 and vpbb.serial_no IS NOT NULL;")
    List<VariableUseVarPathDto> getVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 查询所有被使用的清单id
     * @return set
     */
    @Select("select distinct vpbb.manifest_id  from var_process_batch_backtracking vpbb")
    Set<Long> findUsedManifests();
}
