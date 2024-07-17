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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingTaskDataQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingTaskOutsideDataQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.TaskInfoQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingOutsideVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskListDetailVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskQueryOutputDataVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutsideTaskInfoOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.TaskInfoOutputVO;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingResultBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author xupei
 */
@RestController
@RequestMapping("/backtracking/result")
@Slf4j
@Tag(name = "批量回溯结果查看-整体情况")
public class BacktrackingResultController {

    @Resource
    private BacktrackingResultBiz backtrackingResultBiz;

    /**
     * 查询结果集文件
     *
     * @param taskId  任务id
     * @return 查询结果集文件
     */
    @GetMapping("/overall/getResultFile")
    @Operation(summary = "查询结果集文件")
    public APIResult<List<String>> getResultFile(@RequestParam("taskId") Long taskId) {
        return APIResult.success(backtrackingResultBiz.getResultFile(taskId));
    }


    /**
     * 导出结果集文件
     *
     * @param taskId   任务id
     * @param fileName   文件名称
     * @param response response
     */
    @GetMapping("/overall/task/exportResultFile")
    @Operation(summary = "导出结果集文件")
    public void exportResultFile(@RequestParam("taskId") Long taskId, @RequestParam("fileName") String fileName, HttpServletResponse response) {
        backtrackingResultBiz.exportResultFile(taskId, fileName, response);
    }

    /**
     * 执行记录列表
     *
     * @param inputDto 入参dto
     * @return 执行记录列表
     */
    @GetMapping("/overall/task/list")
    @Operation(summary = "执行记录列表")
    public APIResult<IPage<TaskInfoOutputVO>> getTaskPage(TaskInfoQueryInputVO inputDto) {
        return APIResult.success(backtrackingResultBiz.getTaskPage(inputDto));
    }

    /**
     * 外数记录
     *
     * @param backtrackingId 批量回溯id
     * @param batchNumber 批量number
     * @return 外数执行记录列表
     */
    @GetMapping("/overall/taskOutside/list")
    @Operation(summary = "外数执行记录列表")
    public APIResult<List<OutsideTaskInfoOutputVO>> getOverallOutsidePage(@RequestParam("backtrackingId") Long backtrackingId,
                                                                          @RequestParam("batchNumber") String batchNumber) {
        return APIResult.success(backtrackingResultBiz.getOverallOutsidePage(backtrackingId, batchNumber));
    }

    /**
     * 选择任务编号下拉列表
     *
     * @param backtrackingId 批量回溯id
     * @return 任务编号下拉列表"
     */
    @GetMapping("/overall/taskOutside/codes/list")
    @Operation(summary = "选择任务编号下拉列表")
    public APIResult<List<String>> getTaskCodes(@RequestParam("backtrackingId") Long backtrackingId) {
        return APIResult.success(backtrackingResultBiz.getBacktrackingTaskCodes(backtrackingId));
    }

    /**
     * 执行数据明细列表
     *
     * @param queryVo 查询Vo
     * @return 执行数据明细列表
     */
    @PostMapping("/overall/task/data/list")
    @Operation(summary = "执行数据明细列表")
    public APIResult<IPage<BacktrackingTaskQueryOutputDataVO>> getTaskDataPage(@RequestBody BacktrackingTaskDataQueryVO queryVo) {
        return APIResult.success(backtrackingResultBiz.getTaskDataPage(queryVo));
    }

    /**
     * 执行记录详细内容查看
     *
     * @param resultCode 批量回溯结果code
     * @return 执行记录详细内容
     */
    @GetMapping("/overall/task/list/detail")
    @Operation(summary = "执行记录详细内容查看")
    public APIResult<BacktrackingTaskListDetailVO> getResultDetail(@RequestParam("resultCode") @NotNull(message = "批量回溯结果code不能为空。") @Parameter(description = "批量回溯结果code") String resultCode) {
        return APIResult.success(backtrackingResultBiz.getResultDetail(resultCode));
    }

    /**
     * 查看调用外数
     *
     * @param inputVO 入参Vo
     * @return com.wiseco.var.process.app.server.controller.vo.output.BacktrackingOutsideVO IPage
     */
    @GetMapping("/overall/outside/list")
    @Operation(summary = "查看调用外数")
    public APIResult<IPage<BacktrackingOutsideVO>> getOutsidePage(@Validated BacktrackingTaskOutsideDataQueryVO inputVO) {
        return APIResult.success(backtrackingResultBiz.getOutsidePage(inputVO));
    }
}
