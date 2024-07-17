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
package com.wiseco.var.process.app.server.controller.manifest;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.DesignatedVariableAllListedVersionsQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.ManifestListInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VarModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestConfigInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestCreatInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestDuplicationInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestStateMutationInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableMaximumListedVersionQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestAvailableVersionOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestConfigOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableUseOutputVo;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.enums.QueryConditionMappingEnum;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishVariableVo;
import com.wiseco.var.process.app.server.service.dto.input.VariableManifestDuplicationInputDto;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestBiz;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestSubBiz;
import com.wisecotech.json.JSONArray;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 变量清单 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/25
 */
@RestController
@RequestMapping("/variableManifest")
@Slf4j
@Tag(name = "变量清单")
@Validated
@LoggableClass(param = "variableManifest")
public class VariableManifestController {

    @Autowired
    private VariableManifestBiz variableManifestBiz;

    @Autowired
    private VariableManifestSubBiz variableManifestSubBiz;

    /**
     * 查询参数映射枚举
     *
     * @return com.wiseco.boot.commons.web.APIResult<java.util.List < java.util.Map < java.lang.String, java.lang.Object>>>
     */
    @GetMapping("/queryMapping")
    @Operation(summary = "查询参数映射枚举")
    public APIResult<List<Map<String, Object>>> getQueryMappingList() {
        return APIResult.success(QueryConditionMappingEnum.getMapList());
    }

    /**
     * 查询服务下所有未删除变量清单
     *
     * @param serviceId 实时服务Id
     * @return 服务下所有未删除变量清单
     */
    @PostMapping("/findAvailable/{serviceId}")
    @Operation(summary = "查询服务下所有未删除变量清单")
    public APIResult<List<VariableManifestAvailableVersionOutputDto>> findAvailableManifest(@PathVariable("serviceId") @NotNull(message = "实时服务 ID 不能为空") @Parameter(description = "实时服务 ID") Long serviceId) {
        return APIResult.success(variableManifestBiz.findAvailableManifest(serviceId));
    }

    /**
     * 添加变量清单
     *
     * @param inputDto 输入实体类对象
     * @return 添加变量清单后的Id
     */
    @PostMapping("/create")
    @Operation(summary = "添加变量清单")
    @LoggableMethod(value = "添加变量清单[%s]", params = "name", type = LoggableMethodTypeEnum.CREATE)
    public APIResult<Long> createNewManifest(@RequestBody @Validated VariableManifestCreatInputDto inputDto) {
        return APIResult.success(variableManifestSubBiz.createNewManifest(inputDto));
    }

    /**
     * 一键获取变量清单对应的数据模型
     *
     * @param varModelInputDto 输入实体类对象
     * @return 一变量清单对应的数据模型
     */
    @PostMapping("/getmodelByChosenVar")
    @Operation(summary = "一键获取变量清单对应的数据模型")
    public APIResult<List<VariableManifestDataModelMappingVo>> getModelByVarIdList(@RequestBody @Validated VarModelInputDto varModelInputDto) {
        return APIResult.success(variableManifestBiz.getModelByVarId(varModelInputDto));
    }

    /**
     * 获取启用状态的变量清单使用的数据模型信息-批量回溯
     *
     * @param manifestId 变量清单Id
     * @return 启用状态的变量清单使用的数据模型信息-批量回溯
     */
    @GetMapping("/getModelsOfUpManifest")
    @Operation(summary = "获取启用状态的变量清单使用的数据模型信息-批量回溯")
    public APIResult<List<VariableManifestDataModelMappingVo>> getDataModels(@RequestParam Long manifestId) {
        return APIResult.success(variableManifestBiz.getDataModels(manifestId));
    }

    /**
     * 复制变量清单服务
     *
     * @param inputVO 接受前端的输入实体
     * @return 包含了复制后的新变量清单ID
     */
    @PostMapping("/duplicate")
    @Operation(summary = "复制变量清单")
    @LoggableDynamicValue(params = {"var_process_manifest", "archetypeManifestId"})
    @LoggableMethod(value = "复制变量清单[%s]为[%s]", params = {"archetypeManifestId", "manifestNewName"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> duplicateManifestService(@RequestBody @Validated VariableManifestDuplicationInputVO inputVO) {
        // 1.如果字段校验都通过了，就进入业务逻辑层
        VariableManifestDuplicationInputDto inputDto = new VariableManifestDuplicationInputDto();
        BeanUtil.copyProperties(inputVO, inputDto, true);
        Long duplicateManifestId = variableManifestSubBiz.duplicateManifest(inputDto);

        // 2.返回业务执行的结果给前端
        return APIResult.success(duplicateManifestId);
    }

    /**
     * 获取变量清单列表
     *
     * @param inputDto 输入实体类对象
     * @return 变量清单列表
     */
    @PostMapping("/manifestList")
    @Operation(summary = "获取变量清单列表")
    public APIResult<IPage<ManifestListOutputVo>> getManifestList(@RequestBody ManifestListInputVo inputDto) {
        return APIResult.success(variableManifestSubBiz.getManifestList(inputDto));
    }

    /**
     * 获取使用该清单的服务和批量回溯任务信息
     *
     * @param varProcessSpaceId 变量空间Id
     * @param manifestId        变量清单Id
     * @return 使用该清单的服务和批量回溯任务信息
     */
    @GetMapping("/getUsingList/{varProcessSpaceId}/{manifestId}")
    @Operation(summary = "获取使用该清单的服务&批量回溯任务信息")
    public APIResult<List<VariableUseOutputVo>> getUsingList(@PathVariable("varProcessSpaceId") Long varProcessSpaceId,
                                                             @PathVariable("manifestId") Long manifestId) {
        return APIResult.success(variableManifestSubBiz.getUsingList(varProcessSpaceId, manifestId));
    }

    /**
     * 删除变量清单
     *
     * @param manifestId 变量清单Id
     * @return 删除变量清单后的结果
     */
    @GetMapping("/remove")
    @Operation(summary = "删除变量清单")
    @LoggableDynamicValue(params = {"var_process_manifest", "manifestId"})
    @LoggableMethod(value = "删除变量清单[%s]", params = {"manifestId"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult<String> removeManifestVersion(@RequestParam("manifestId") @NotNull(message = "变量清单 ID 不能为空") @Parameter(description = "变量清单 ID") Long manifestId) {
        variableManifestSubBiz.removeManifestVersion(manifestId);
        return APIResult.success("变量清单删除成功");
    }

    /**
     * 获取变量清单配置
     *
     * @param manifestId 变量清单Id
     * @return 变量清单配置
     */
    @GetMapping("/get/{manifestId}")
    @Operation(summary = "获取变量清单配置")
    public APIResult<VariableManifestConfigOutputDto> getManifestConfig(@PathVariable("manifestId") @NotNull(message = "变量清单 ID 不能为空") @Parameter(description = "变量清单 ID") Long manifestId) {
        return APIResult.success(variableManifestSubBiz.getManifestConfig(manifestId));
    }

    /**
     * 保存变量清单配置
     *
     * @param inputDto 输入实体类对象
     * @return 保存变量清单配置后的结果
     */
    @PostMapping("/save")
    @Operation(summary = "保存变量清单配置")
    @LoggableMethod(value = "编辑变量清单[%s]", params = {"name"}, type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> saveManifest(@RequestBody @Validated VariableManifestConfigInputDto inputDto) {
        variableManifestSubBiz.saveManifestCheck(inputDto);
        return APIResult.success(variableManifestSubBiz.saveManifest(inputDto));
    }

    /**
     * 查询所有变量最大已上架版本记录
     *
     * @param inputDto 输入实体类对象
     * @return 所有变量最大已上架版本记录
     */
    @PostMapping("/getVarMaxListedVersion")
    @Operation(summary = "查询所有变量最大已上架版本记录")
    public APIResult<List<VariableManifestPublishVariableVo>> getVariableMaximumListedVersion(@RequestBody @Validated VariableMaximumListedVersionQueryInputDto inputDto) {
        return APIResult.success(variableManifestBiz.getVariableMaximumListedVersion(inputDto));
    }

    /**
     * 查询指定变量所有已上架版本
     *
     * @param inputDto 输入实体类对象
     * @return 指定变量所有已上架版本
     */
    @PostMapping("/getDesignatedVarAllListedVersions")
    @Operation(summary = "查询指定变量所有已上架版本")
    public APIResult<List<VariableManifestPublishVariableVo>> getDesignatedVariableAllListedVersions(@RequestBody @Validated DesignatedVariableAllListedVersionsQueryInputDto inputDto) {
        return APIResult.success(variableManifestBiz.getDesignatedVariableAllListedVersions(inputDto.getSpaceId(), inputDto.getIdentifierList()));
    }

    /**
     * 查询变量空间引入的外部服务接收对象
     *
     * @param spaceId 变量空间Id
     * @return 变量空间引入的外部服务接收对象
     */
    @GetMapping("/getOutsideServiceReceiverObjectTree/{spaceId}")
    @Operation(summary = "查询变量空间引入的外部服务接收对象")
    public APIResult<List<DomainDataModelTreeDto>> getOutsideServiceReceiverObjectTree(@PathVariable("spaceId") @NotNull(message = "变量空间 ID 不能为空") @Parameter(description = "变量空间 ID", required = true) Long spaceId) {
        return APIResult.success(variableManifestBiz.getOutsideServiceReceiverObjectTree(spaceId));
    }

    /**
     * 获取外部服务入参映射对象
     *
     * @param spaceId             变量空间Id
     * @param objectBindingConfig 对象绑定的配置
     * @return 外部服务入参映射对象
     */
    @GetMapping("/getOutsideServiceRequestParam")
    @Operation(summary = "获取外部服务入参映射对象")
    public APIResult<JSONArray> getOutsideServiceRequestParam(@RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空") @Parameter(description = "变量空间 ID", required = true) Long spaceId,
                                                              @RequestParam("objectBindingConfig") @NotBlank(message = "对象绑定配置不能为空") @Parameter(description = "对象绑定配置", required = true) String objectBindingConfig) {
        return APIResult.success(variableManifestBiz.getOutsideServiceRequestParam(spaceId, objectBindingConfig), "操作成功");
    }

    /**
     * 校验清单状态更新
     *
     * @param inputDto 输入实体类对象
     * @return 校验清单状态更新后的结果
     */
    @PostMapping("/validateStatusUpdate")
    @Operation(summary = "校验清单状态更新")
    public APIResult<String> validateStatusUpdate(@RequestBody @Validated VariableManifestStateMutationInputDto inputDto) {
        return APIResult.success(variableManifestBiz.validateStatusUpdate(inputDto));
    }

    /**
     * 更新变量清单状态
     *
     * @param inputDto 输入实体类对象
     * @return 更新变量清单状态后的结果
     * @throws Throwable 异常
     */
    @PostMapping("/updateStatus/{actionType}")
    @LoggableDynamicValue(params = {"var_process_manifest", "manifestId"})
    @LoggableMethod(value = "%s变量清单[%s]", params = {"manifestId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "更新变量清单状态")
    public APIResult<String> updateStatus(@RequestBody @Validated VariableManifestStateMutationInputDto inputDto) throws Throwable {
        variableManifestBiz.updateStatus(inputDto);
        return APIResult.success("操作成功");
    }

    /**
     * 提交测试
     *
     * @param inputDto 输入实体类对象
     * @return 变量编译验证返回DTO
     * @throws Throwable 异常
     */
    @PostMapping("/submitTest")
    @Operation(summary = "提交测试")
    public APIResult<VariableCompileOutputDto> submitTest(@RequestBody @Validated VariableManifestStateMutationInputDto inputDto) throws Throwable {
        return APIResult.success(variableManifestBiz.submitTest(inputDto, null));
    }

    /**
     * 获取变量清单属性信息 (右侧边栏)
     *
     * @param manifestId 变量清单Id
     * @return 变量清单属性信息(右侧边栏)
     */
    @GetMapping("/getManifestProperties")
    @Operation(summary = "获取变量清单属性信息 (右侧边栏)")
    public APIResult<List<TabDto>> getManifestProperties(@RequestParam("manifestId") @NotNull(message = "变量清单 ID 不能为空") @Parameter(description = "变量清单 ID") Long manifestId) {
        return APIResult.success(variableManifestSubBiz.getManifestProperties(manifestId));
    }

    /**
     * 获取实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     *
     * @param variableIds 传入的指标Id的集合(单指标分析时传一个, 指标对比分析时传多个)
     * @return 实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     */
    @GetMapping("/getServiceManifestName")
    @Operation(summary = "获取实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)")
    public APIResult<List<ServiceManifestNameVo>> getServiceManifestName(@Parameter(description = "传入的指标Id的集合(单指标分析时传一个, 指标对比分析时传多个)") @RequestParam("variableIds") List<Long> variableIds) {
        // 1.调用业务逻辑层的逻辑,获取结果
        List<ServiceManifestNameVo> result = variableManifestSubBiz.getServiceManifestName(variableIds);
        // 2.返回结果
        return APIResult.success(result);
    }
}
