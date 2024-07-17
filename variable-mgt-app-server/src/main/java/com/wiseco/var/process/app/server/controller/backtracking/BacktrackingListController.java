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
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingListOutputVO;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingListBiz;
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
import javax.validation.constraints.NotNull;

/**
 * 批量回溯列表、删除、停用和流程处理
 * @author wuweikang
 */
@RestController
@RequestMapping("/backtracking/list")
@Slf4j
@Tag(name = "批量回溯列表、删除、停用和流程处理")
@LoggableClass(param = "backtracking")
public class BacktrackingListController {

    @Resource
    private BacktrackingListBiz backtrackingListBiz;

    /**
     * 列表
     *
     * @param inputDto 入参dto
     * @return 批量回溯列表
     */
    @GetMapping("/list")
    @Operation(summary = "列表")
    public APIResult<IPage<BacktrackingListOutputVO>> list(@Validated BacktrackingQueryInputVO inputDto) {
        return APIResult.success(backtrackingListBiz.list(inputDto));
    }

    /**
     * 删除
     *
     * @param id 批量回溯Id
     * @return 删除的结果
     */
    @GetMapping("/deleteBacktracking")
    @Operation(summary = "删除")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking", "id"})
    @LoggableMethod(value = "删除批量回溯任务[%s]", params = {"id"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteBacktracking(@Parameter(description = "批量回溯id") @RequestParam("id") Long id) {
        backtrackingListBiz.updateStatus(BacktrackingUpdateStatusInputDto.builder().id(id).actionType(FlowActionTypeEnum.DELETE).build());
        return APIResult.success();
    }

    /**
     * 修改状态
     *
     * @param inputDto 输入实体类对象
     * @return 修改状态的结果
     */
    @PostMapping("/updateStatus/{actionType}")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking", "id"})
    @LoggableMethod(value = "%s批量回溯任务[%s]", params = {"id", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "修改状态")
    public APIResult updateStatus(@RequestBody BacktrackingUpdateStatusInputDto inputDto) {
        backtrackingListBiz.updateStatus(inputDto);
        return APIResult.success();
    }

    /**
     * 修改状态校验
     * @param inputDto  修改批量回溯状态 DTO
     * @return 是否可以修改
     */
    @PostMapping("/updateStatusCheck")
    @Operation(summary = "修改状态校验")
    public APIResult<String> updateStatusCheck(@RequestBody BacktrackingUpdateStatusInputDto inputDto) {
        return APIResult.success(backtrackingListBiz.updateStatusCheck(inputDto));
    }

    /**
     * 批量回溯执行操作校验
     * @param id 批量回溯ID
     * @return 是否可以执行
     */
    @GetMapping("/backtrackingExecuteCheck")
    @Operation(summary = "批量回溯执行操作校验")
    public APIResult<String> backtrackingExecuteCheck(@RequestParam("id") @NotNull(message = "批量回溯ID不能为空") @Parameter(description = "批量回溯ID") Long id) {
        return APIResult.success(backtrackingListBiz.backtrackingExecuteCheck(id));
    }
}
