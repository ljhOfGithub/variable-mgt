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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingCopyInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingFilePreviewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingPreviewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingViewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MultipartPreviewRespVO;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingBiz;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author wxs
 */
@RestController
@RequestMapping("/backtracking")
@Slf4j
@Tag(name = "批量回溯新增、编辑、查看、复制")
@LoggableClass(param = "backtracking")
public class BacktrackingController {

    @Resource
    private BacktrackingBiz backtrackingBiz;

    /**
     * msgFormatList
     *
     * @return com.wiseco.boot.commons.web.APIResult<java.util.List < java.util.Map < java.lang.String, java.lang.Object>>>
     */
    @GetMapping("/outsideStrategyList")
    @Operation(summary = "外数取值方式列表")
    public APIResult<List<Map<String, Object>>> outsideStrategyList() {
        return APIResult.success(BacktrackingOutsideCallStrategyEnum.getOutsideCallStrategyList());
    }

    /**
     * msgFormatList
     *
     * @return com.wiseco.boot.commons.web.APIResult<java.util.List < java.util.Map < java.lang.String, java.lang.Object>>>
     */
    @GetMapping("/msgFormatList")
    @Operation(summary = "报文格式列表")
    public APIResult<List<Map<String, Object>>> msgFormatList() {
        return APIResult.success(ServiceMsgFormatEnum.getMsgFormatEnumList());
    }

    /**
     * 获取最近执行时间
     *
     * @param cron cronn表达式
     * @return 最近执行时间
     */
    @GetMapping("/getNextExecuteTimeByCron")
    @Operation(summary = "获取最近执行时间")
    public APIResult<List<String>> getNextExecuteTimeByCron(@RequestParam("cron") String cron) {
        return APIResult.success(backtrackingBiz.getNextExecuteTimeByCron(cron));
    }


    /**
     * 保存校验cron表达式是否过期
     *
     * @param taskInfo 任务信息
     * @return 校验结果
     */
    @GetMapping("/checkCronIsExpireByTaskInfo")
    @Operation(summary = "保存校验任务是否过期")
    public APIResult<Boolean> checkCronIsExpire(BacktrackingSaveInputVO.TaskInfo taskInfo) {
        return APIResult.success(backtrackingBiz.checkCronIsExpire(taskInfo));
    }

    /**
     * 添加保存批量回溯
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/save")
    @Operation(summary = "添加保存批量回溯")
    @LoggableMethod(value = "添加批量回溯任务[%s]", params = "name", type = LoggableMethodTypeEnum.NEW_BACKTRACKING_TASK)
    public APIResult<Long> save(@Validated @RequestBody BacktrackingSaveInputVO inputVO) {
        return APIResult.success(backtrackingBiz.save(inputVO));
    }

    /**
     * 编辑保存批量回溯
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/edit")
    @Operation(summary = "编辑保存批量回溯")
    @LoggableMethod(value = "编辑批量回溯任务[%s]", params = "name", type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> edit(@Validated @RequestBody BacktrackingSaveInputVO inputVO) {
        return APIResult.success(backtrackingBiz.save(inputVO));
    }

    /**
     * 复制批量回溯
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/copy")
    @Operation(summary = "复制批量回溯")
    @LoggableDynamicValue(params = {"var_process_batch_backtracking", "copyId"})
    @LoggableMethod(value = "复制批量回溯任务[%s]为[%s]", params = {"copyId", "name"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copy(@RequestBody BacktrackingCopyInputVO inputVO) {
        return APIResult.success(backtrackingBiz.copy(inputVO));
    }

    /**
     * 查看批量回溯
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/view")
    @Operation(summary = "查看批量回溯")
    public APIResult<BacktrackingSaveInputVO> view(@RequestBody BacktrackingViewInputVO inputVO) {
        return APIResult.success(backtrackingBiz.view(inputVO));
    }

    /**
     * 预览数据
     *
     * @param reqVO 请求参数
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @Operation(summary = "预览数据")
    @PostMapping(value = "/preview")
    public ApiResult<MultipartPreviewRespVO> previewData(@RequestBody BacktrackingPreviewInputVO reqVO) {
        MultipartPreviewRespVO respVO = backtrackingBiz.previewData(reqVO);
        return ApiResult.success(respVO);
    }


    /**
     * 文件预览
     *
     * @param backtrackingFilePreviewInputVO 文件预览入参
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @Operation(summary = "文件预览")
    @PostMapping(value = "/filePreview")
    public ApiResult<MultipartPreviewRespVO> filePreview(BacktrackingFilePreviewInputVO backtrackingFilePreviewInputVO) throws IOException {
        return ApiResult.success(backtrackingBiz.filePreview(backtrackingFilePreviewInputVO));
    }


    /**
     * 数据对象映射-外部传入树形结构
     *
     * @param manifestId 变量清单Id
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @Operation(summary = "数据对象映射-外部传入树形结构")
    @GetMapping(value = "/dataModelTree/{manifestId}")
    public ApiResult<List<DomainDataModelTreeDto>> getDataModelTree(@PathVariable("manifestId") Long manifestId) {
        List<DomainDataModelTreeDto> respVO = backtrackingBiz.getDataModelTree(manifestId);
        return ApiResult.success(respVO);
    }

    /**
     * 根据变量清单获取表名称
     *
     * @param manifestId  变量清单Id
     * @param triggerType 触发类型
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @Operation(summary = "根据变量清单获取表名称")
    @GetMapping(value = "/getTableName/{manifestId}/{triggerType}")
    public ApiResult<BacktrackingSaveInputVO.BacktrackingOutputDb> getTableName(@PathVariable("manifestId") Long manifestId,
                                                                                @PathVariable("triggerType") BatchBacktrackingTriggerTypeEnum triggerType) {
        BacktrackingSaveInputVO.BacktrackingOutputDb respVO = backtrackingBiz.getTableName(manifestId, triggerType);
        return ApiResult.success(respVO);
    }

    /**
     * 根据批量回溯的ID来获取这个批量回溯的生命周期
     *
     * @param backtrackingId 批量回溯ID
     * @return 批量回溯的生命周期
     */
    @GetMapping("/getBacktrackingProperties")
    @Operation(summary = "根据批量回溯的ID来获取这个批量回溯的生命周期")
    public APIResult<List<TabDto>> getBacktrackingProperties(@RequestParam("backtrackingId") @NotNull(message = "批量回溯ID不能为空") @Parameter(description = "批量回溯ID") Long backtrackingId) {
        // 1.调用业务逻辑层的函数
        List<TabDto> result = backtrackingBiz.getServiceProperties(backtrackingId);
        // 2.返回结果
        return APIResult.success(result);
    }
}
