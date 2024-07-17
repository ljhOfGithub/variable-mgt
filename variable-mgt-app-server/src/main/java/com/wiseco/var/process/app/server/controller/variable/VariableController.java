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
package com.wiseco.var.process.app.server.controller.variable;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.SingleVariableQueryInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableBatchSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableBatchUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCacheContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCompareInfoInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCompareInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariablePropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.Confirm;
import com.wiseco.var.process.app.server.controller.vo.output.ImportVariableOutputDTO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableBatchUpdateOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompareOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableListVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableUseOutputVo;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VariableBatchBiz;
import com.wiseco.var.process.app.server.service.VariableBiz;
import com.wiseco.var.process.app.server.service.VariableContentBiz;
import com.wiseco.var.process.app.server.service.VariableImportExportBiz;
import com.wiseco.var.process.app.server.service.dto.input.SingleVariableQueryInputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wangxianli
 */
@RestController
@RequestMapping("/variable")
@Slf4j
@Tag(name = "变量管理")
@LoggableClass(param = "variable")
public class VariableController {

    @Autowired
    private VariableContentBiz variableContentBiz;

    @Autowired
    private VariableBiz variableBiz;

    @Autowired
    private VariableBatchBiz variableBatchBiz;

    @Autowired
    private VariableImportExportBiz variableImportExportBiz;

    /**
     * 分页查询变量列表
     * @param inputDto 输入实体类对象
     * @return 变量列表
     */
    @PostMapping("/variableList")
    @Operation(summary = "变量列表")
    public APIResult<IPage<VariableListOutputDto>> getVariableList(@RequestBody @Valid VariableQueryInputDto inputDto) {

        return APIResult.success(variableContentBiz.getVariableList(inputDto));
    }

    /**
     * 变量详情
     *
     * @param inputDto 入参
     * @return 变量详情
     */
    @PostMapping("/variableDetail")
    @Operation(summary = "变量详情")
    public APIResult<VariableDetailOutputDto> variableDetail(@RequestBody VariableDetailQueryInputDto inputDto) {

        return APIResult.success(variableContentBiz.variableDetail(inputDto));
    }

    /**
     * 变量使用
     *
     * @param varProcessSpaceId 变量空间Id
     * @param variableId 变量Id
     * @return 变量使用列表
     */
    @GetMapping("/getUseVariableList/{varProcessSpaceId}/{variableId}")
    @Operation(summary = "变量使用")
    public APIResult<List<VariableUseOutputVo>> getUseVariableList(@PathVariable("varProcessSpaceId") Long varProcessSpaceId,
                                                                   @PathVariable("variableId") Long variableId) {
        return APIResult.success(variableContentBiz.getUseManifestList(varProcessSpaceId, variableId));
    }

    /**
     * 变量属性
     *
     * @param inputDto 输入实体类对象
     * @return 变量属性
     */
    @PostMapping("/variableProperties")
    @Operation(summary = "变量属性")
    public APIResult<VariablePropertiesOutputDto> variableProperties(@RequestBody VariablePropertiesInputDto inputDto) {
        if (null == inputDto.getShowType()) {
            inputDto.setShowType(NumberUtils.INTEGER_ONE);
        }
        return APIResult.success(variableContentBiz.variableProperties(inputDto));
    }

    /**
     * 变量逻辑校验
     *
     * @param spaceId spaceId
     * @param variableId variableId
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/processLogicCheck/{spaceId}/{variableId}")
    @Operation(summary = "变量逻辑校验")
    public APIResult processLogicCheck(@PathVariable("spaceId") Long spaceId, @PathVariable("variableId") Long variableId) {
        // 词条的入参核对（变量模版调整后，在变量详情处校验并提示）
        return APIResult.success(Confirm.builder().show(variableBiz.processLogicCheck(variableId)).type("WARNING")
                .msg("变量模版已被修改，系统将加载新模版并清空旧模版，请按照最新模版重新点选.").build());
    }

    /**
     * 导入变量Excel
     *
     * @param file 文件对象
     * @param spaceId 变量空间Id
     * @return 变量Excel导入输出参数
     */
    @PostMapping("/importVariable")
    @Operation(summary = "导入变量Excel")
    public APIResult<ImportVariableOutputDTO> importVariable(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空") @Parameter(description = "变量空间 ID") Long spaceId) {
        return APIResult.success(variableImportExportBiz.importVariable(spaceId, file));
    }

    /**
     * 导出变量Excel
     *
     * @param identifier 唯一标识符
     * @return 继续导入变量Excel的结果
     */
    @GetMapping("/proceedImportVariable")
    @Operation(summary = "继续导入变量Excel")
    public APIResult<List<Long>> proceedImportVariable(@RequestParam("identifier") @Parameter(description = "请求标志 继续导入时传入") String identifier) {
        return APIResult.success(variableImportExportBiz.proceedImportVariable(identifier));
    }

    /**
     * 导出变量Excel
     *
     * @param inputDto inputDto
     * @param response response
     */
    @PostMapping("/exportVariable")
    @Operation(summary = "导出变量Excel")
    public void exportVariable(@RequestBody VariableQueryInputDto inputDto, HttpServletResponse response) {
        variableImportExportBiz.exportVariable(inputDto, response);
    }

    /**
     * 添加保存变量
     *
     * @param inputDto 输入实体类对象
     * @return 添加保存变量的结果
     */
    @PostMapping("/saveVariable")
    @Operation(summary = "添加保存变量")
    @LoggableDynamicValue(params = {"var_process_variable","name"})
    @LoggableMethod(value = "添加变量[%s-%s]",params = {"name"}, type = LoggableMethodTypeEnum.CREATE)
    public APIResult<Long> saveVariable(@RequestBody VariableSaveInputDto inputDto) {

        return APIResult.success(variableBiz.saveVariable(inputDto));
    }

    /**
     * 编辑保存变量
     *
     * @param inputDto 输入实体类对象
     * @return 编辑保存变量的结果
     */
    @PostMapping("/editVariable")
    @Operation(summary = "编辑保存变量")
    @LoggableDynamicValue(params = {"var_process_variable", "id"})
    @LoggableMethod(value = "编辑变量[%s-%s]", params = {"id"}, type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> editVariable(@RequestBody VariableSaveInputDto inputDto) {
        return APIResult.success(variableBiz.saveVariable(inputDto));
    }

    /**
     * 批量编辑保存变量
     *
     * @param inputDto 输入实体类对象
     * @return 编辑保存变量的结果
     */
    @PostMapping("/batchEditVariable/{updateAction}")
    @Operation(summary = "批量编辑保存变量")
    public APIResult<VariableBatchUpdateOutputDto> batchEditVariable(@RequestBody VariableBatchSaveInputDto inputDto) {
        return APIResult.success(variableBatchBiz.batchSaveVariable(inputDto));
    }

    /**
     * 添加新版本
     *
     * @param spaceId    变量空间Id
     * @param variableId 变量Id
     * @return 复制上架或下架的变量的Id
     */
    @GetMapping("/upgradeVersionVariable")
    @Operation(description = "复制上架或下架的变量")
    @LoggableDynamicValue(params = {"var_process_variable", "variableParentId"})
    @LoggableMethod(value = "变量添加新版本[%s-%s]", params = {"variableId"}, type = LoggableMethodTypeEnum.NEW_VERSION)
    public APIResult<Long> upgradeVersionVariable(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, @Parameter(description = "父变量id") @RequestParam("variableId") Long variableId) {
        return APIResult.success(variableBiz.upgradeVersionVariable(spaceId, variableId));
    }

    /**
     * 复制变量
     *
     * @param inputDto 输入实体类对象
     * @return 复制变量后的Id
     */
    @PostMapping("/copyVariable")
    @Operation(summary = "复制变量")
    @LoggableDynamicValue(params = {"var_process_variable","copyId"})
    @LoggableMethod(value = "复制变量[%s-%s]为[%s-%s]",params = {"label","copyId"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copyVariable(@RequestBody VariableCopyInputDto inputDto) {
        return APIResult.success(variableBiz.copyVariable(inputDto));
    }

    /**
     * 验证变量
     *
     * @param inputDto 输入实体类对象
     * @return 变量编译验证返回DTO
     */
    @PostMapping("/checkVariable")
    @Operation(summary = "验证变量")
    public APIResult<VariableCompileOutputDto> checkVariable(@RequestBody VariableContentInputDto inputDto) {
        return APIResult.success(variableBiz.checkVariable(inputDto));
    }

    /**
     * validateUpdateStatus
     *
     * @param inputDto 输入实体类对象
     * @return 修改状态校验的结果
     */
    @PostMapping("/validateUpdateStatus")
    @Operation(summary = "修改状态校验")
    public APIResult<String> validateUpdateStatus(@RequestBody VariableUpdateStatusInputDto inputDto) {
        return APIResult.success(variableBiz.validateUpdateStatus(inputDto));
    }

    /**
     * 修改状态
     *
     * @param inputDto inputDto
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/updateStatus/{actionType}")
    @LoggableDynamicValue(params = {"var_process_variable", "variableId"})
    @LoggableMethod(value = "%s变量[%s-%s]", params = {"variableId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "修改状态")
    public APIResult updateStatus(@RequestBody VariableUpdateStatusInputDto inputDto) {
        variableBiz.updateStatus(inputDto);
        return APIResult.success();
    }

    /**
     * 批量修改状态
     * @param inputDto 输入实体类对象
     * @return 变量编译验证返回DTO
     */
    @PostMapping("/batchUpdateStatus/{actionType}")
    @LoggableDynamicValue(params = {"var_process_variable", "variableIdList"})
    @LoggableMethod(value = "批量%s变量[%s]", params = {"variableIdList", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "批量修改状态")
    public APIResult<VariableBatchUpdateOutputDto> batchUpdateStatus(@RequestBody VariableBatchUpdateStatusInputDto inputDto) {
        return APIResult.success(variableBatchBiz.batchUpdateStatus(inputDto));
    }

    /**
     * 临时缓存内容
     *
     * @param inputDto 输入实体类对象
     * @return 临时缓存内容的结果
     */
    @PostMapping("/cacheContent")
    @Operation(summary = "临时缓存内容")
    public APIResult<String> cacheContent(@RequestBody VariableCacheContentInputDto inputDto) {
        return APIResult.success(variableBiz.cacheContent(inputDto), "操作成功");
    }

    /**
     * 恢复版本
     *
     * @param inputDto inputDto
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/restoreVersion")
    @Operation(summary = "恢复版本")
    public APIResult restoreVersion(@RequestBody VariableDetailQueryInputDto inputDto) {

        variableBiz.restoreVersion(inputDto);

        return APIResult.success();
    }


    /**
     * 删除变量
     *
     * @param spaceId spaceId
     * @param variableId variableId
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/deleteVariable")
    @Operation(summary = "删除变量")
    @LoggableDynamicValue(params = {"var_process_variable","variableId"})
    @LoggableMethod(value = "删除变量[%s-%s]",params = {"variableId"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteVariable(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, @Parameter(description = "变量id") @RequestParam("variableId") Long variableId) {

        variableBiz.deleteVariable(spaceId, variableId);

        return APIResult.success();
    }

    /**
     * 变量对比
     *
     * @param variableCompareInputDto 输入实体类对象
     * @return 变量对比的结果
     */
    @PostMapping("/compare")
    @Operation(summary = "变量对比")
    public APIResult<VariableCompareOutputDto> variableCompare(@RequestBody VariableCompareInputDto variableCompareInputDto) {
        return APIResult.success(variableContentBiz.variableCompare(variableCompareInputDto));
    }

    /**
     * 变量版本信息列表
     *
     * @param variableCompareInfoInputDto 输入实体类对象
     * @return 变量版本信息列表
     */
    @PostMapping("/versionList")
    @Operation(summary = "变量版本信息列表")
    public APIResult<List<VarProcessVariable>> versionList(@RequestBody VariableCompareInfoInputDto variableCompareInfoInputDto) {

        return APIResult.success(variableContentBiz.versionList(variableCompareInfoInputDto.getVariableId()));
    }

    /**
     * 获取指标(给单指标分析和指标对比分析报表的监控对象选择使用)
     * @param inputVo 输入实体类对象
     * @return 变量清单对应的指标
     */
    @GetMapping("/getVariables")
    @Operation(summary = "获取指标(给单指标分析和指标对比分析报表的监控对象选择使用)")
    public APIResult<List<VariableListVo>> getVariables(SingleVariableQueryInputVo inputVo) {
        // 1.转换实体类
        SingleVariableQueryInputDto inputDto = new SingleVariableQueryInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.返回结果
        List<VariableListVo> result = variableContentBiz.getVariables(inputDto);
        return APIResult.success(result);
    }
}
