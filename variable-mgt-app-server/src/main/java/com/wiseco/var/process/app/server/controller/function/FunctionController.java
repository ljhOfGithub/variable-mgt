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
package com.wiseco.var.process.app.server.controller.function;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.FlowUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCacheContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionPropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSelectInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionSelectOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionUseListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.FunctionBiz;
import com.wiseco.var.process.app.server.service.FunctionContentBiz;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangxianli
 */
@RestController
@RequestMapping("/function")
@Slf4j
@Tag(name = "预处理、变量模板、公共函数共用")
@LoggableClass(param = "function")
public class FunctionController {

    @Resource
    private FunctionContentBiz functionContentBiz;

    @Resource
    private FunctionBiz functionBiz;
    /**
     * 分页列表
     *
     * @param inputVO 输入实体类对象
     * @return 分页查询结果
     */
    @GetMapping("/functionList")
    @Operation(summary = "分页列表")
    public APIResult<IPage<FunctionListOutputVO>> getFunctionList(@Validated FunctionQueryInputVO inputVO) {
        return APIResult.success(functionContentBiz.getFunctionList(inputVO));
    }

    /**
     * 添加保存
     *
     * @param inputDto 输入实体类
     * @return 添加公共函数后的Id
     */
    @PostMapping("/saveFunction")
    @Operation(summary = "添加保存")
    @LoggableDynamicValue(params = {"functionType"})
    @LoggableMethod(value = "添加%s[%s]", params = {"functionType", "name"}, type = LoggableMethodTypeEnum.CREATE)
    public APIResult<Long> saveFunction(@Validated @RequestBody FunctionSaveInputDto inputDto) {
        return APIResult.success(functionBiz.saveFunction(inputDto));
    }

    /**
     * 编辑保存
     *
     * @param inputDto 输入实体类
     * @return 编辑公共函数后的Id
     */
    @PostMapping("/editFunction")
    @Operation(summary = "编辑保存")
    @LoggableDynamicValue(params = {"functionType"})
    @LoggableMethod(value = "编辑%s[%s]", params = {"functionType", "name"}, type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> editFunction(@Validated @RequestBody FunctionSaveInputDto inputDto) {
        return APIResult.success(functionBiz.saveFunction(inputDto));
    }

    /**
     * 复制
     *
     * @param inputDto 输入实体类对象
     * @return 复制公共函数后的Id
     */
    @PostMapping("/copyFunction")
    @Operation(summary = "复制")
    @LoggableDynamicValue(params = {"var_process_function", "copyId"})
    @LoggableMethod(value = "复制%s[%s]为[%s]", params = {"copyId", "functionType", "name"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copyFunction(@RequestBody FunctionCopyInputDto inputDto) {
        return APIResult.success(functionBiz.copyFunction(inputDto));
    }

    /**
     * 详情
     *
     * @param inputDto 输入实体类对象
     * @return 公共函数详情
     */
    @PostMapping("/functionDetail")
    @Operation(summary = "详情")
    public APIResult<FunctionDetailOutputDto> functionDetail(@RequestBody FunctionDetailQueryInputDto inputDto) {
        return APIResult.success(functionContentBiz.functionDetail(inputDto));
    }

    /**
     * 启用停用
     *
     * @param inputDto 输入实体类对象
     * @return 启用公共函数后的结果
     */
    @PostMapping("/updateEnableFunction/{actionType}")
    @Operation(summary = "启用停用")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    public APIResult updateUpFunction(@RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.updateStatus(inputDto);
        return APIResult.success();
    }

    /**
     * 修改状态
     *
     * @param inputDto 输入实体类对象
     * @return 修改状态（提交、审核）后的结果
     */
    @PostMapping("/updateStatus/{actionType}")
    @Operation(summary = "修改状态（提交、审核）")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    public APIResult updateStatus(@RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.updateStatus(inputDto);
        return APIResult.success();
    }

    /**
     * 删除
     *
     * @param spaceId    变量空间Id
     * @param functionId 公共函数Id
     * @return 删除公共函数后的结果
     */
    @GetMapping("/deleteFunction")
    @Operation(summary = "删除")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "删除%s[%s]", params = {"functionId"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteFunction(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, @Parameter(description = "公共预处理模板id") @RequestParam("functionId") Long functionId) {
        functionBiz.updateStatus(FlowUpdateStatusInputDto.builder().functionId(functionId).spaceId(spaceId).actionType(FlowActionTypeEnum.DELETE)
                .build());
        return APIResult.success();
    }

    /**
     * 公共函数下拉
     *
     * @param inputDto 输入实体类对象
     * @return 公共函数下拉后的结果
     */
    @PostMapping("/functionSelect")
    @Operation(summary = "公共函数下拉")
    public APIResult<List<FunctionSelectOutputVO>> functionSelect(@RequestBody FunctionSelectInputVO inputDto) {
        return APIResult.success(functionContentBiz.functionSelect(inputDto));
    }

    /**
     * 使用变量列表
     *
     * @param spaceId 变量空间Id
     * @param functionId 公共函数Id
     * @return 变量列表
     */
    @GetMapping("/useVariableList/{spaceId}/{functionId}")
    @Operation(summary = "使用变量列表")
    public APIResult<List<VariableDetailDto>> getUseVariableList(@PathVariable("spaceId") Long spaceId, @PathVariable("functionId") Long functionId) {
        return APIResult.success(functionContentBiz.getUseVariableList(spaceId, functionId));
    }

    /**
     * 公共函数属性
     *
     * @param inputDto 输入实体类对象
     * @return 公共函数属性
     */
    @PostMapping("/functionProperties")
    @Operation(summary = "公共函数属性")
    public APIResult<VariablePropertiesOutputDto> functionProperties(@RequestBody FunctionPropertiesInputDto inputDto) {
        if (null == inputDto.getShowType()) {
            inputDto.setShowType(NumberUtils.INTEGER_ONE);
        }
        return APIResult.success(functionContentBiz.functionProperties(inputDto));
    }

    /**
     * 公共函数使用列表
     *
     * @param inputDto 输入实体类对象
     * @return 公共函数使用列表
     */
    @PostMapping("/useList")
    @Operation(summary = "公共函数使用列表")
    public APIResult<List<FunctionUseListOutputDto>> useList(@Validated @RequestBody FunctionInputDto inputDto) {
        return APIResult.success(functionContentBiz.useList(inputDto));
    }

    /**
     * 验证公共函数
     *
     * @param inputDto 输入实体类对象
     * @return 验证公共函数后的结果
     */
    @PostMapping("/checkFunction")
    @Operation(summary = "验证公共函数")
    public APIResult<VariableCompileOutputDto> checkFunction(@RequestBody FunctionContentInputDto inputDto) {
        return APIResult.success(functionBiz.checkFunction(inputDto));
    }

    /**
     * 预处理删除校验
     *
     * @param inputDto 输入实体类对象
     * @return 预处理删除校验结果
     */
    @PostMapping("/checkPreDelete")
    @Operation(summary = "预处理删除校验")
    public APIResult checkPreDelete(@RequestBody FlowUpdateStatusInputDto inputDto) {
        return APIResult.success(functionBiz.checkPreDelete(inputDto));
    }

    /**
     * 删除变量模板校验
     *
     * @param inputDto 输入实体类对象
     * @return 删除变量模板校验后的结果
     */
    @PostMapping("/checkDeleteVariableTemplate")
    @Operation(summary = "删除变量模板校验")
    public APIResult<String> checkDeleteVariableTemplate(@RequestBody FlowUpdateStatusInputDto inputDto) {
        return APIResult.success(functionBiz.checkDeleteVariableTemplate(inputDto));
    }

    /**
     * 内容比较
     *
     * @param inputDto 输入实体类对象
     * @return 内容比较后的结果
     */
    @PostMapping("/compareContent")
    @Operation(summary = "内容比较")
    public APIResult<Boolean> compareContent(@RequestBody FunctionSaveInputDto inputDto) {
        return APIResult.success(functionBiz.compareContent(inputDto));
    }

    /**
     * 临时缓存内容
     *
     * @param inputDto 输入实体类对象
     * @return 临时缓存内容的结果
     */
    @PostMapping("/cacheContent")
    @Operation(summary = "临时缓存内容")
    public APIResult<String> cacheContent(@RequestBody FunctionCacheContentInputDto inputDto) {
        return APIResult.success(functionBiz.cacheContent(inputDto), "操作成功");
    }

    /**
     * 恢复版本
     *
     * @param inputDto 输入实体类对象
     * @return 恢复版本的结果
     */
    @PostMapping("/restoreVersion")
    @Operation(summary = "恢复版本")
    public APIResult restoreVersion(@RequestBody FunctionDetailQueryInputDto inputDto) {
        functionBiz.restoreVersion(inputDto);
        return APIResult.success();
    }

    /**
     * 公共函数校验
     *
     * @param inputDto 输入实体类对象
     * @return 公共函数校验结果
     */
    @PostMapping("/validatedFunction")
    @Operation(summary = "公共函数校验")
    public APIResult validatedFunction(@Validated @RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.validatedFunction(inputDto);
        return APIResult.success();
    }


    /**
     * 启用停用前校验
     * @param inputDto 修改公共函数状态 DTO
     * @return 校验结果信息
     */
    @PostMapping("/validateStatusUpdate")
    @Operation(summary = "启用停用前校验")
    public APIResult<String> validateStatusUpdate(@RequestBody @Validated FlowUpdateStatusInputDto inputDto) {
        return APIResult.success(functionBiz.validateStatusUpdate(inputDto));
    }

}
