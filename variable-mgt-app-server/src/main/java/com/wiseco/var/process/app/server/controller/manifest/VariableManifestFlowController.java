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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.ManifestModelMatchTreeInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowDataModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowDetailInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPrepInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowVarInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowPrepOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowVarOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableinternalDataOutputVo;
import com.wiseco.var.process.app.server.service.dto.output.VariableOutsideServiceOutputDto;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestFlowBiz;
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

import java.util.List;

/**
 * 变量清单的流程图 控制器
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@RestController
@RequestMapping("/variableManifestFlow")
@Slf4j
@Tag(name = "变量清单流程图")
public class VariableManifestFlowController {

    @Autowired
    private VariableManifestFlowBiz variableManifestFlowBiz;

    /**
     * 流程信息
     *
     * @param inputDto 输入实体类对象
     * @return 流程信息
     */
    @PostMapping("/flowDetail")
    @Operation(summary = "流程信息")
    public APIResult<VariableManifestFlowDetailOutputDto> flowDetail(@RequestBody VariableManifestFlowDetailInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.flowDetail(inputDto));
    }

    /**
     * 流程属性
     *
     * @param inputDto 输入实体类对象
     * @return 流程属性
     */
    @PostMapping("/flowProperties")
    @Operation(summary = "流程属性")
    public APIResult<VariablePropertiesOutputDto> flowProperties(@RequestBody VariableManifestFlowPropertiesInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.flowProperties(inputDto));
    }

    /**
     * 恢复版本
     *
     * @param inputDto 输入实体类对象
     * @return 恢复版本后的结果
     */
    @PostMapping("/flowRecovery")
    @Operation(summary = "恢复版本")
    public APIResult flowRecovery(@RequestBody VariableManifestFlowDetailInputDto inputDto) {
        variableManifestFlowBiz.flowRecovery(inputDto);
        return APIResult.success();
    }

    /**
     * 保存
     *
     * @param inputDto 输入实体类对象
     * @return 保存流程后的结果
     */
    @PostMapping("/saveFlow")
    @Operation(summary = "保存")
    public APIResult saveFlow(@RequestBody VariableManifestFlowSaveInputDto inputDto) {
        variableManifestFlowBiz.saveFlow(inputDto);
        return APIResult.success();
    }

    /**
     * 验证外数授权码
     * @param authCode 授权码
     * @param outCode 外数code
     * @return true or false
     */
    @GetMapping("/validateOutsideAuthCode")
    @Operation(summary = "验证外数授权码")
    public APIResult<Boolean> validateOutsideAuthCode(@Parameter(description = "授权码") @RequestParam("auth_code") String authCode,
                                             @Parameter(description = "外数code") @RequestParam("outCode") String outCode) {
        return APIResult.success(variableManifestFlowBiz.validateOutsideAuthCode(authCode,outCode));
    }

    /**
     * 验证
     *
     * @param inputDto 输入实体类对象
     * @return 验证变量编译后的结果
     */
    @PostMapping("/checkFlow")
    @Operation(summary = "验证")
    public APIResult<VariableCompileOutputDto> checkFlow(@RequestBody VariableManifestFlowSaveInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.checkFlow(inputDto));
    }

    /**
     * 数据模型列表
     *
     * @param inputDto 输入实体类对象
     * @return 数据模型列表
     */
    @PostMapping("/dataModelObjectList")
    @Operation(summary = "数据模型列表")
    public APIResult<List<String>> dataModelObjectList(@RequestBody VariableManifestFlowDataModelInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.dataModelObjectList(inputDto));
    }

    /**
     * 预处理对象查询
     *
     * @param inputDto 输入实体类对象
     * @return 预处理对象list
     */
    @PostMapping("/prepObjectList")
    @Operation(summary = "预处理对象查询")
    public APIResult<List<VariableManifestFlowPrepOutputDto>> prepObjectList(@RequestBody VariableManifestFlowPrepInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.prepObjectList(inputDto));
    }

    /**
     * 变量列表
     *
     * @param inputDto 输入实体类对象
     * @return 变量列表
     */
    @PostMapping("/variableList")
    @Operation(summary = "变量列表")
    public APIResult<List<VariableManifestFlowVarOutputDto>> variableList(@Validated @RequestBody VariableManifestFlowVarInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.variableList(inputDto));
    }

    /**
     * 内部数据列表
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return 内部数据列表
     */
    @GetMapping("/internalDataList/{spaceId}/{manifestId}")
    @Operation(summary = "内部数据列表")
    public APIResult<List<VariableinternalDataOutputVo>> findInternalDataList(@PathVariable("spaceId") Long spaceId,
                                                                              @PathVariable("manifestId") Long manifestId) {
        return APIResult.success(variableManifestFlowBiz.findInternalDataList(spaceId, manifestId));
    }

    /**
     * 生成变量工作流节点标识
     *
     * @return String
     */
    @GetMapping("/generateNodeIdentifier")
    @Operation(summary = "生成变量工作流节点标识", description =  "测试专用")
    public APIResult<String> generateNodeIdentifier() {
        return APIResult.success(variableManifestFlowBiz.generateNodeIdentifier());
    }

    /**
     * 外部服务列表查询
     *
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return 外部服务列表
     */
    @GetMapping("/findVariableOutsideServiceList/{spaceId}/{manifestId}")
    @Operation(summary = "外部服务列表查询")
    public APIResult<List<VariableOutsideServiceOutputDto>> findVariableOutsideServiceList(@PathVariable("spaceId") Long spaceId,
                                                                                           @PathVariable("manifestId") Long manifestId) {
        return APIResult.success(variableManifestFlowBiz.findVariableOutsideServiceList(spaceId, manifestId));
    }

    /**
     * 变量匹配树接口
     *
     * @param inputDto 输入实体类对象
     * @return 变量匹配树接口
     */
    @PostMapping("/findMatchVarsTree")
    @Operation(summary = "变量匹配树接口")
    public APIResult<List<DomainDataModelTreeDto>> findMatchVarsTree(@RequestBody ManifestModelMatchTreeInputDto inputDto) {
        return APIResult.success(variableManifestFlowBiz.findDataVariable(inputDto));
    }
}
