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
package com.wiseco.var.process.app.server.controller.backtracking;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.powerjob.worker.core.processor.ProcessResult;

import javax.annotation.Resource;

/**
 * @author xupei
 */
@RestController
@RequestMapping("/backtracking/task")
@Slf4j
@Tag(name = "批量回溯任务执行")
@LoggableClass(param = "backtracking")
public class BacktrackingTaskController {

    @Resource
    private BacktrackingTaskBiz backtrackingTaskBiz;

    /**
     * 手动执行
     *
     * @param backtrackingId backtrackingId
     * @param taskId         taskId
     * @return tech.powerjob.worker.core.processor.ProcessResult
     */
    @GetMapping("/execute")
    @Operation(summary = "手动执行")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking","backtrackingId"})
    @LoggableMethod(value = "执行批量回溯任务[%s]",params = {"backtrackingId"}, type = LoggableMethodTypeEnum.EXECUTE)
    public APIResult<ProcessResult> execute(@Parameter(description = "批量回溯ID") @RequestParam("backtrackingId") Long backtrackingId,
                                            @Parameter(description = "任务Id") @RequestParam("taskId") Long taskId) {
        return APIResult.success(backtrackingTaskBiz.manualExecute(backtrackingId, taskId,false));
    }

    /**
     * 重新手动执行
     *
     * @param backtrackingId backtrackingId
     * @param taskId         taskId
     * @return tech.powerjob.worker.core.processor.ProcessResult
     */
    @GetMapping("/reExecute")
    @Operation(summary = "重新手动执行")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking","backtrackingId"})
    @LoggableMethod(value = "重新执行批量回溯任务[%s]",params = {"backtrackingId"}, type = LoggableMethodTypeEnum.REXECURE)
    public APIResult<ProcessResult> reExecute(@Parameter(description = "批量回溯ID") @RequestParam("backtrackingId") Long backtrackingId,
                                            @Parameter(description = "任务Id") @RequestParam("taskId") Long taskId) {
        return APIResult.success(backtrackingTaskBiz.manualExecute(backtrackingId, taskId,false));
    }


    /**
     * 手动继续执行
     *
     * @param backtrackingId backtrackingId
     * @param taskId         taskId
     * @return tech.powerjob.worker.core.processor.ProcessResult
     */
    @GetMapping("/continueExecute")
    @Operation(summary = "手动继续执行")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking","backtrackingId"})
    @LoggableMethod(value = "继续执行批量回溯任务[%s]",params = {"backtrackingId"}, type = LoggableMethodTypeEnum.CONTINUE_EXEC)
    public APIResult<ProcessResult> continueExecute(@Parameter(description = "批量回溯ID") @RequestParam("backtrackingId") Long backtrackingId,
                                              @Parameter(description = "任务Id") @RequestParam("taskId") Long taskId) {
        return APIResult.success(backtrackingTaskBiz.manualExecute(backtrackingId, taskId,true));
    }

    /**
     * 暂停执行
     * @param backtrackingId         批量回溯ID
     * @param taskId         任务Id
     * @return tech.powerjob.worker.core.processor.ProcessResult
     */
    @GetMapping("/pauseExecute")
    @Operation(summary = "暂停执行")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking", "backtrackingId"})
    @LoggableMethod(value = "暂停执行批量回溯任务[%s]", params = {"backtrackingId"}, type = LoggableMethodTypeEnum.PAUSE_EXEC)
    public APIResult pauseExecute(@Parameter(description = "批量回溯ID") @RequestParam("backtrackingId") Long backtrackingId,
                                  @Parameter(description = "任务Id") @RequestParam("taskId") Long taskId) {
        backtrackingTaskBiz.pauseExecute(taskId);
        return APIResult.success();
    }
}
