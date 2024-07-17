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

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.permission.RPCAccess;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceAddVersionInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceManifestUpdateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceVersionListInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceConfigInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceUpdateInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionInfoOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestDocumentOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableServiceConfigOutputVo;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.ServiceBiz;
import com.wiseco.var.process.app.server.service.ServiceOperateBiz;
import com.wiseco.var.process.app.server.service.VariableConfigDefaultValueBiz;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.input.ServiceManifestUpdateInputDto;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service")
@Slf4j
@Tag(name = "实时服务新版")
@Validated
@LoggableClass(param = "service")
public class ServiceController {

    @Autowired
    private ServiceBiz serviceBiz;

    @Autowired
    private ServiceOperateBiz serviceOperateBiz;

    @Autowired
    private VariableConfigDefaultValueBiz variableConfigDefaultValueBiz;

    /**
     * 查询服务列表
     *
     * @param inputVO 输入实体
     * @return 服务列表
     */
    @GetMapping("/list")
    @Operation(summary = "实时服务列表")
    public ApiResult<Page<RestServiceListOutputVO>> serviceList(ServiceListInputVO inputVO) {
        Page<RestServiceListOutputVO> result = serviceBiz.serviceList(inputVO);
        return ApiResult.success(result);
    }

    /**
     * 服务版本查询
     *
     * @param inputVO 入参
     * @return page
     */
    @GetMapping("/versionList")
    @Operation(summary = "服务版本查询")
    public ApiResult<Page<ServiceVersionInfoOutputVo>> versionList(ServiceVersionListInputVO inputVO) {
        Page<ServiceVersionInfoOutputVo> result = serviceBiz.versionList(inputVO);
        return ApiResult.success(result);
    }

    /**
     * 添加实时服务
     *
     * @param inputVO 入参
     * @return 新实时服务的ID
     */
    @PostMapping("/create")
    @Operation(summary = "添加实时服务")
    @LoggableMethod(value = "添加实时服务[%s]", params = "name", type = LoggableMethodTypeEnum.CREATE)
    public ApiResult<Long> createVariableService(@RequestBody @Validated ServiceSaveInputVO inputVO) {
        Long serviceId = serviceBiz.saveVariableService(inputVO);
        return ApiResult.success(serviceId);
    }

    /**
     * 编辑实时服务
     *
     * @param inputVO 入参
     * @return 新实时服务的ID
     */
    @PostMapping("/update")
    @Operation(summary = "编辑实时服务")
    @LoggableMethod(value = "编辑实时服务[%s]", params = "name", type = LoggableMethodTypeEnum.EDIT)
    public ApiResult<Long> updateVariableService(@RequestBody @Validated ServiceSaveInputVO inputVO) {
        Long serviceId = serviceBiz.saveVariableService(inputVO);
        return ApiResult.success(serviceId);
    }

    /**
     * 删除服务校验
     *
     * @param id 服务id
     * @return message
     */
    @GetMapping("/validateDelete")
    @Operation(summary = "删除服务校验")
    public ApiResult<String> validateDeleteService(@Parameter(description = "实时服务id") @RequestParam("id") @NotNull(message = "实时服务的ID不能为空") Long id) {
        serviceOperateBiz.validateDeleteService(id);
        return ApiResult.success("确认删除？");
    }

    /**
     * 删除实时服务
     *
     * @param id 服务id
     * @return message
     */
    @GetMapping("/delete")
    @Operation(summary = "删除实时服务")
    @LoggableDynamicValue(params = {"var_process_realtime_service", "id"})
    @LoggableMethod(value = "删除实时服务[%s]", params = {"id"}, type = LoggableMethodTypeEnum.DELETE)
    public ApiResult<String> deleteVariableService(@Parameter(description = "实时服务id") @RequestParam("id") @NotNull(message = "实时服务的ID不能为空") Long id) {
        serviceBiz.deleteVariableService(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 获取接口文档
     *
     * @param serviceId 实时服务Id
     * @return 接口文档
     */
    @GetMapping("/getInterfaceDocument")
    @Operation(summary = "获取接口文档")
    public ApiResult<VariableManifestDocumentOutputDto> getInterfaceDocument(@RequestParam("serviceId") @NotNull(message = "实时服务 ID 不能为空。") @Parameter(description = "实时服务 ID") Long serviceId) {
        return ApiResult.success(serviceBiz.getInterfaceDocument(serviceId));
    }

    /**
     * 导出接口文档excel
     *
     * @param serviceId 服务id
     * @param response  响应
     */
    @GetMapping("/exportInterfaceExcel")
    @Operation(summary = "导出接口文档Excel文件")
    public void exportInterfaceExcel(@RequestParam("serviceId") @NotNull(message = "服务 ID 不能为空。") @Parameter(description = "服务 ID") Long serviceId, HttpServletResponse response) {
        serviceBiz.exportInterfaceExcel(serviceId, response);
    }

    /**
     * 添加实时服务的版本(重新创建)
     *
     * @param inputVO 输入VO
     * @return 新实时服务的版本ID
     */
    @PostMapping("/addVersionByCreate")
    @Operation(summary = "添加实时服务的版本(重新创建)")
    @LoggableDynamicValue(params = {"var_process_service_version", "id"})
    @LoggableMethod(value = "实时服务添加版本[%s-%s]", params = {"id"}, type = LoggableMethodTypeEnum.NEW_VERSION)
    public ApiResult<Long> addVersionByCreate(@RequestBody @Validated ServiceAddVersionInputVO inputVO) {
        // 1.调用业务逻辑层的函数
        Long result = serviceBiz.addVersionByCreate(inputVO);
        // 2.返回结果
        return ApiResult.success(result);
    }

    /**
     * 添加实时服务的版本(复制已有版本)
     *
     * @param inputVO 输入VO
     * @return 新实时服务的版本ID
     */
    @PostMapping("/addVersionByCopy")
    @Operation(summary = "添加实时服务的版本(复制已有版本)")
    @LoggableDynamicValue(params = {"var_process_service_version", "copiedServiceId", "id"})
    @LoggableMethod(value = "复制实时服务版本[%s-%s]为[%s-%s]", params = {"copiedServiceId", "id"}, type = LoggableMethodTypeEnum.NEW_VERSION)
    public ApiResult<Long> addVersionByCopy(@RequestBody @Validated ServiceAddVersionInputVO inputVO) {
        // 1.调用业务逻辑层的函数
        Long result = serviceBiz.addVersionByCopy(inputVO);
        // 2.返回结果
        return ApiResult.success(result);
    }

    /**
     * 版本详情
     *
     * @param versionId 版本id
     * @return 基本信息+清单信息+数据模型
     */
    @GetMapping("/versionDetail")
    @Operation(summary = "查看版本详情")
    public ApiResult<VariableServiceConfigOutputVo> versionDetail(@RequestParam("versionId") @NotNull(message = "请传入版本id") Long versionId) {
        return ApiResult.success(serviceBiz.versionDetail(versionId));
    }

    /**
     * 修改服务版本
     *
     * @param inputVO 入参
     * @return 版本id
     */
    @PostMapping("/updateVersion")
    @Operation(summary = "版本修改")
    public ApiResult<Long> updateVersion(@RequestBody @Validated VariableServiceConfigInputVo inputVO) {
        serviceBiz.saveServiceVersionCheck(inputVO);
        Long versionId = serviceBiz.updateVersion(inputVO);
        return ApiResult.success(versionId);
    }

    /**
     * 版本删除
     *
     * @param id 版本id
     * @return 删除成功
     */
    @GetMapping("/deleteVersion")
    @Operation(summary = "删除实时服务版本")
    public ApiResult<String> deleteVersion(@Parameter(description = "版本id") @RequestParam("id") @NotNull(message = "请传入版本id") Long id) {
        serviceBiz.deleteVersion(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 获取服务list及版本号
     *
     * @return 实时服务器列表
     */
    @GetMapping("/listWithVersions")
    @Operation(summary = "获取服务list及版本号")
    public ApiResult<List<ServiceListOutputVo>> findServiceListWithVersions() {
        return ApiResult.success(serviceBiz.findServiceListWithVersions());
    }

    /**
     * 更改实时服务的状态
     *
     * @param inputVO 前端输入的实体对象
     * @return ApiResult实体类对象
     */
    @PostMapping("/updateState/{actionType}")
    @Operation(summary = "更改实时服务的状态，提交、点了审核后通过、点了审核后不通过、退回编辑、启用、停用")
    @LoggableDynamicValue(params = {"var_process_service_version", "actionType", "serviceId", "versionId"})
    @LoggableMethod(value = "%s实时服务[%s-%s]", params = {"actionType", "serviceId", "versionId"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    public ApiResult updateState(@RequestBody @Validated VariableServiceUpdateInputVO inputVO) {
        serviceOperateBiz.updateState(inputVO);
        return ApiResult.success("操作成功!");
    }

    /**
     * 更改实时服务版本状态 校验接口
     *
     * @param inputVO 前端输入的实体对象
     * @return ApiResult实体类对象
     */
    @PostMapping("/validateUpdateState")
    @Operation(summary = "更改实时服务版本状态 校验接口")
    public ApiResult<String> validateUpdateState(@RequestBody @Validated VariableServiceUpdateInputVO inputVO) {
        return ApiResult.success(serviceOperateBiz.validateUpdateState(inputVO));
    }

    /**
     * 根据serviceId(某一个具体版本的实时服务)查看它的生命周期
     *
     * @param serviceId 某一个具体的实时服务ID
     * @return 实时服务的生命周期
     */
    @GetMapping("/getServiceProperties")
    @Operation(summary = "根据serviceId(某一个具体版本的实时服务)查看它的生命周期")
    public ApiResult<List<TabDto>> getServiceProperties(@RequestParam("serviceId") @NotNull(message = "实时服务ID不能为空") @Parameter(description = "某一个具体的实时服务ID") Long serviceId) {
        // 1.调用业务逻辑层的函数
        List<TabDto> result = serviceBiz.getServiceProperties(serviceId);
        // 2.返回结果
        return ApiResult.success(result);
    }

    /**
     * 根据serviceId和manifestId来更新实时服务中变量清单的执行次数
     *
     * @param inputVo serviceId和manifestId的实体类
     * @return 执行是否成功
     */
    @RPCAccess
    @PostMapping("/updateServiceAndManifest")
    @Operation(summary = "给consumer进行远程调用的接口，主要用来更新实时服务中的变量清单的执行次数")
    public ApiResult<Boolean> updateServiceAndManifest(@RequestBody @Validated ServiceManifestUpdateInputVo inputVo) {
        // 1.转换Bean类
        ServiceManifestUpdateInputDto inputDto = new ServiceManifestUpdateInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.调用业务逻辑层的函数
        Boolean result = serviceOperateBiz.updateServiceAndManifest(inputDto);
        return ApiResult.success(result);
    }

    @RPCAccess
    @GetMapping("/getDefaultValueMap")
    public ApiResult<Map<String, String>> getDefaultValueMap() {
        return ApiResult.success(variableConfigDefaultValueBiz.getDefaultValueMap());
    }

    /**
     * 迁移数据
     *
     * @return success
     */
    @GetMapping("/migrateData")
    public ApiResult migrateData() {
        serviceOperateBiz.migrateData();
        return ApiResult.success("success！");
    }

    /**
     * 下载部署文件
     * @param versionId
     * @param resp
     * @return APIResult
     * @throws IOException
     */
    @GetMapping("/download/deploy/file")
    @Operation(summary = "下载部署文件")
    public APIResult downloadDeployFile(@Parameter(description = "实时服务版本ID") @RequestParam("versionId") Long versionId, HttpServletResponse resp) {
        // 1.调用业务逻辑层的逻辑,获取结果
        Map<String, Object> dataMap = serviceBiz.downloadDeployFile(versionId);
        byte[] data = (byte[]) dataMap.get("data");
        String fileName = (String) dataMap.get("fileName");
        // 写入文件字节流
        try (OutputStream outputStream = resp.getOutputStream()) {
            // 设定HTTP响应头部
            resp.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            resp.addHeader(HttpHeaders.PRAGMA, "no-cache");
            resp.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            log.error("实时服务部署文件下载异常！", e);
            throw new RuntimeException("实时服务部署文件下载异常！");
        }
        // 2.返回结果
        return APIResult.success();
    }
}
