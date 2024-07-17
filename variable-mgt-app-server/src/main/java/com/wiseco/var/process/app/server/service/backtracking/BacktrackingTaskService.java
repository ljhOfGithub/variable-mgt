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
package com.wiseco.var.process.app.server.service.backtracking;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingTaskMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class BacktrackingTaskService extends ServiceImpl<VarProcessBatchBacktrackingTaskMapper, VarProcessBatchBacktrackingTask> {
    @Autowired
    private VarProcessBatchBacktrackingTaskMapper varProcessBatchBacktrackingTaskMapper;

    /**
     * getBacktrackingTaskByBacktrackingId
     *
     * @param backtrackingId 批量回溯Id
     * @return 批量回溯任务的list
     */
    public List<BacktrackingTaskDto> getBacktrackingTaskByBacktrackingId(Long backtrackingId) {
        return varProcessBatchBacktrackingTaskMapper.getBacktrackingTaskByBacktrackingId(backtrackingId);
    }

    /**
     * findBacktrackingTaskList
     *
     * @param page     入参
     * @param queryDto 入参
     * @return Ipage
     */
    public IPage<BacktrackingTaskDetailDto> findBacktrackingTaskList(IPage page, BacktrackingTaskQueryDto queryDto) {
        return varProcessBatchBacktrackingTaskMapper.findBacktrackingTaskList(page, queryDto);
    }

    /**
     * getByCode
     *
     * @param batchNumber 批次号码
     * @return 批量回溯任务的对象
     */
    public VarProcessBatchBacktrackingTask getByCode(String batchNumber) {
        return varProcessBatchBacktrackingTaskMapper.getByCode(batchNumber);
    }


    /**
     * updateStateByIds
     *
     * @param idList                     idList
     * @param backtrackingTaskStatusEnum backtrackingTaskStatusEnum
     */
    public void updateStateByIds(List<Long> idList, BacktrackingTaskStatusEnum backtrackingTaskStatusEnum) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }
        varProcessBatchBacktrackingTaskMapper.updateStateByIds(idList, backtrackingTaskStatusEnum);
    }
}
