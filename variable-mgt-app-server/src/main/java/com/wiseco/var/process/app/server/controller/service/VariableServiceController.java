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
package com.wiseco.var.process.app.server.controller.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.ServiceManifestMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.AuthorizationConfigInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.OutParamsInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.SerialNoLinkableDataModelInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceAuthorizationInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceAuthorizationSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestAndDataModelInfoVo;
import com.wiseco.var.process.app.server.controller.vo.output.OutSideParamsOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceAuthorizationOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionVo;
import com.wiseco.var.process.app.server.controller.vo.output.VarSimpleServiceOutputDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VariablePmdCheckBiz;
import com.wiseco.var.process.app.server.service.VariableServiceBiz;
import com.wiseco.var.process.app.server.service.dto.TabDto;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 实时服务 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@RestController
@RequestMapping("/variableService")
@Slf4j
@Tag(name = "实时服务")
@Validated
@LoggableClass(param = "variableService")
public class VariableServiceController {

    @Autowired
    private VariableServiceBiz variableServiceBiz;
    @Autowired
    private VariablePmdCheckBiz variablePmdCheckBiz;

    /**
     * 获取已选择的变量清单的详细信息 和 更新数据模型引用信息
     * @param manifests 变量清单Id的list
     * @param dataModels manual为1) 的数据模型id列表
     * @return 已选择的变量清单的详细信息 和 更新数据模型引用信息
     */
    @GetMapping("/getManifestAndDataModelInfo")
    @Operation(summary = "获取已选择的变量清单的详细信息 & 更新数据模型引用信息")
    public APIResult<ManifestAndDataModelInfoVo> getManifestAndDataModelInfo(@Parameter(description = "变量清单id列表") @RequestParam List<Long> manifests,
                                                                             @Parameter(description = "手动添加(manual为1) 的数据模型id列表") @RequestParam List<Long> dataModels) {
        return APIResult.success(variableServiceBiz.getManifestAndDataModelInfo(manifests, dataModels));
    }

    /**
     * 获取可选的外部入参对象列表
     * @param inputVo 输入实体类对象
     * @return 可选的外部入参对象列表
     */
    @GetMapping("/getOutsideParams")
    @Operation(summary = "获取可选的外部入参对象列表")
    public APIResult<IPage<OutSideParamsOutputVo>> getOutParams(OutParamsInputVo inputVo) {
        return APIResult.success(variableServiceBiz.getOutParams(inputVo));
    }

    /**
     * 获取可用于服务调用流水号绑定的数据结构
     * @param inputDto 输入实体类对象
     * @return 可用于服务调用流水号绑定的数据结构
     */
    @PostMapping("/getSerialNoLinkableDataModelTree")
    @Operation(summary = "获取可用于服务调用流水号绑定的数据结构", description =  "仅返回 int, double 和 String 类型的原始数据")
    public APIResult<DomainDataModelTreeDto> getSerialNoLinkableDataModelTree(@RequestBody @Validated SerialNoLinkableDataModelInputVo inputDto) {
        return APIResult.success(variableServiceBiz.getSerialNoLinkableDataModelTree(inputDto));
    }

    /**
     * 提供给监控报表的公共接口, 用于获取服务名称(版本)的列表
     * @return 服务名称的列表
     */
    @GetMapping("/getServiceNameList")
    @Operation(summary = "提供给监控报表的公共接口, 用于获取服务名称(版本)的列表")
    public APIResult<List<Map<String, Object>>> getServiceNameList() {
        // 1.调用业务逻辑层的函数
        List<Map<String, Object>> result = variablePmdCheckBiz.getServiceNameList();
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 提供给监控规则的公共接口, 用于获取服务名称(版本)的列表
     * @return 服务名称的列表
     */
    @GetMapping("/getServiceNameAndVersionList")
    @Operation(summary = "提供给监控规则的公共接口, 用于获取服务名称(版本)的列表")
    public APIResult<List<ServiceVersionVo>> getServiceNameAndVersionList() {
        // 1.调用业务逻辑层的函数
        List<ServiceVersionVo> result = variablePmdCheckBiz.getServiceNameAndVersionList();
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 服务授权查询
     * @param inputVo 输入实体类对象
     * @return 服务授权查询结果
     */
    @GetMapping("/auth/list")
    @Operation(summary = "授权页list")
    public APIResult<Page<ServiceAuthorizationOutputVo>> listAuthorization(ServiceAuthorizationInputVo inputVo) {
        return APIResult.success(variablePmdCheckBiz.listAuthorization(inputVo));
    }

    /**
     * getAuthCode
     * @param id id
     * @return 授权码
     */
    @GetMapping("/auth/getAuthCode/{id}")
    @Operation(summary = "获取授权码")
    public APIResult<String> getAuthCode(@PathVariable("id") Long id) {
        String authCode = variablePmdCheckBiz.getAuthCode(id);
        return APIResult.success(authCode);
    }

    /**
     * 保存授权
     * @param inputDto 实时服务授权新增信息输入参数
     * @return id
     */
    @PostMapping("/auth/save")
    @Operation(summary = "保存授权")
    @LoggableMethod(value = "添加调用方[%s]",params = "caller",type = LoggableMethodTypeEnum.CREATE)
    public APIResult<Long> saveAuthorization(@RequestBody @Validated VariableServiceAuthorizationSaveInputDto inputDto) {
        return APIResult.success(variablePmdCheckBiz.saveAuthorization(inputDto));
    }

    /**
     * 编辑授权
     * @param inputDto 实时服务授权新增信息输入参数
     * @return id
     */
    @PostMapping("/auth/edit")
    @Operation(summary = "编辑授权")
    @LoggableMethod(value = "编辑调用方[%s]",params = "caller",type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> updateAuthorization(@RequestBody @Validated VariableServiceAuthorizationSaveInputDto inputDto) {
        return APIResult.success(variablePmdCheckBiz.saveAuthorization(inputDto));
    }

    /**
     * 删除授权
     * @param id 授权id
     * @return message
     */
    @GetMapping("/auth/remove")
    @Operation(summary = "删除授权")
    @LoggableDynamicValue(params = {"var_process_authorization","id"})
    @LoggableMethod(value = "删除调用方[%s]",params = "id",type = LoggableMethodTypeEnum.DELETE)
    public APIResult<String> removeAuthorization(@RequestParam("id") @NotNull(message = "请选择待删除的授权记录。") @Parameter(description = "实时服务授权记录 ID") Long id) {
        variablePmdCheckBiz.deleteAuthorization(id);
        return APIResult.success("已取消服务授权。");
    }

    /**
     * 删除授权校验
     * @param id 授权id
     * @return message
     */
    @GetMapping("/auth/removeCheck")
    @Operation(summary = "删除授权校验")
    public APIResult<String> removeAuthorizationCheck(@RequestParam("id") @NotNull(message = "请选择待删除的授权记录。") @Parameter(description = "实时服务授权记录 ID") Long id) {
        variablePmdCheckBiz.removeAuthorizationCheck(id);
        return APIResult.success("确认删除？");
    }

    /**
     * 启用/停用授权
     * @param id 授权id
     * @param actionType 启用：1；停用：0
     * @return 授权id
     */
    @GetMapping("/auth/update")
    @Operation(summary = "启用/停用授权")
    @LoggableDynamicValue(params = {"var_process_authorization","id"})
    @LoggableMethod(value = "%s调用方[%s]",params = "actionType",type = LoggableMethodTypeEnum.UPDATE_STATUS)
    public APIResult<Long> updateAuthorization(@RequestParam("id") @NotNull(message = "请选择授权记录id。") @Parameter(description = "实时服务授权记录 ID") Long id,
                                                 @RequestParam("actionType") @NotNull(message = "请输入操作") @Parameter(description = "启用：1；停用：0") Integer actionType) {
        return APIResult.success(variablePmdCheckBiz.updateAuthorization(id,actionType));
    }

    /**
     * 保存授权配置
     * @param inputVo 入参
     * @return message
     */
    @PostMapping("/auth/saveConfig")
    @Operation(summary = "保存授权配置")
    @LoggableDynamicValue(params = {"var_process_authorization","authorizationId"})
    @LoggableMethod(value = "在调用方[%s]下配置服务",params = "authorizationId",type = LoggableMethodTypeEnum.CONFIG_SERVICE)
    public APIResult<String> saveAuthorizationConfig(@RequestBody AuthorizationConfigInputVo inputVo) {
        variablePmdCheckBiz.saveAuthorizationConfig(inputVo.getAuthorizationId(), inputVo.getServiceCodes());
        return APIResult.success("保存服务授权配置成功");
    }

    /**
     * 获取授权服务配置
     * @param authorizationId 授权id
     * @return list
     */
    @GetMapping("/auth/getConfig")
    @Operation(summary = "获取授权服务配置")
    public APIResult<List<VarSimpleServiceOutputDto>> saveAuthorizationConfig(@RequestParam("authorizationId") @Parameter(description = "授权id") Long authorizationId) {
        return APIResult.success(variablePmdCheckBiz.getAuthorizationConfig(authorizationId));
    }

    /**
     * 获取简单服务列表(启用)
     * @param spaceId 空间id
     * @param excludeCodes 排除的codelist
     * @param keyWord 服务名称\编码搜索
     * @param size 页面大小
     * @param currentNo 当前页码
     * @return list
     */
    @GetMapping("/findSimpleUpServiceList")
    @Operation(summary = "获取简单服务列表(启用状态)")
    public APIResult<Page<VarSimpleServiceOutputDto>> findSimpleUpServiceList(@RequestParam("spaceId") @Parameter(description = "空间id") Long spaceId,
                                                                              @RequestParam("keyWord") @Parameter(description = "搜索关键字") String keyWord,
                                                                              @RequestParam("excludeCodes") @Parameter(description = "排除的服务code") List<String> excludeCodes,
                                                                              @RequestParam("currentNo") @Parameter(description = "当前页码") int currentNo,
                                                                              @RequestParam("size") @Parameter(description = "页面大小") int size) {
        return APIResult.success(variablePmdCheckBiz.findSimpleUpServiceList(spaceId,keyWord,excludeCodes,currentNo,size));
    }

    /**
     * 获取空间下简单格式服务列表
     * @param spaceId 变量空间Id
     * @return 空间下简单格式服务列表
     */
    @GetMapping("/getSpaceSimpleService")
    @Operation(summary = "获取空间下简单格式服务列表")
    public APIResult<List<VarSimpleServiceOutputDto>> getSpaceSimpleService(@RequestParam("spaceId") Long spaceId) {
        return APIResult.success(variablePmdCheckBiz.getSpaceSimpleService(spaceId));
    }

    /**
     * 根据serviceId查看变量清单信息
     * @param serviceId 实时服务Id
     * @return 变量清单信息
     */
    @GetMapping("/getManifestByServiceId")
    @Operation(summary = "根据serviceId查看变量清单信息")
    public APIResult<List<ServiceManifestMappingVo>> getManifestListByServiceId(@RequestParam("serviceId") Long serviceId) {
        return APIResult.success(variableServiceBiz.getManifestByServiceId(serviceId));
    }

    /**
     * 根据serviceId查看它的生命周期
     * @param serviceId 实时服务ID
     * @return 实时服务的生命周期
     */
    @GetMapping("/getServiceProperties")
    @Operation(summary = "根据serviceId查看它的生命周期")
    public APIResult<List<TabDto>> getServiceProperties(@RequestParam("serviceId") @NotNull(message = "实时服务ID不能为空") @Parameter(description = "实时服务ID") Long serviceId) {
        // 1.调用业务逻辑层的函数
        List<TabDto> result = variablePmdCheckBiz.getServiceProperties(serviceId);
        // 2.返回结果
        return APIResult.success(result);
    }
}
