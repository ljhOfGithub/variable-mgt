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
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VarProcessBatchBacktrackingTaskMapper extends BaseMapper<VarProcessBatchBacktrackingTask> {

    /**
     * 分页查询批量回溯执行记录
     *
     * @param page     分页
     * @param queryDto 查询参数
     * @return BacktrackingTaskDetailDto
     */
    IPage<BacktrackingTaskDetailDto> findBacktrackingTaskList(IPage page, @Param("queryDto") BacktrackingTaskQueryDto queryDto);

    /**
     * 条件查询批量回溯执行记录
     *
     * @param backtrackingId 批量回溯id
     * @return BacktrackingTaskDto
     */
    BacktrackingTaskDto getBacktrackingSingleTask(@Param("backtrackingId") Long backtrackingId);

    /**
     * 条件查询批量回溯执行记录
     *
     * @param backtrackingId 批量回溯id
     * @return BacktrackingTaskDto
     */
    @Select("SELECT vpbt.* from var_process_batch_backtracking_task vpbt\n"
            + "LEFT JOIN var_process_batch_backtracking vpb on vpb.id = vpbt.backtracking_id\n"
            + "where vpb.id = #{backtrackingId} and vpb.delete_flag = 1")
    List<BacktrackingTaskDto> getBacktrackingTaskByBacktrackingId(@Param("backtrackingId") Long backtrackingId);

    /**
     * 通过记录标识查询任务执行记录
     *
     * @param batchNumber 任务记录标识
     * @return VarProcessBatchBacktrackingTask
     */
    VarProcessBatchBacktrackingTask getByCode(String batchNumber);


    /**
     * 获取失败信息
     *
     * @param backtrackingId 批量回溯id
     * @return 错误信息
     */
    String getErrorMessage(@Param("backtrackingId") Long backtrackingId);

    /**
     * updateStateByIds
     *
     * @param idList idList
     * @param backtrackingTaskStatusEnum backtrackingTaskStatusEnum
     */
    void updateStateByIds(@Param("idList") List<Long> idList, @Param("backtrackingTaskStatusEnum") BacktrackingTaskStatusEnum backtrackingTaskStatusEnum);
}
