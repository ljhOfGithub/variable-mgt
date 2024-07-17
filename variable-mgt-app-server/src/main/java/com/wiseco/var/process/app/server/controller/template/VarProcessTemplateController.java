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
package com.wiseco.var.process.app.server.controller.template;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.EngineFunctionTemplateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FlowUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.QueryDictInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TemplateStaticGetProviderInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TemplateStaticGetTemplateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarAppendedProviderInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarDataProviderListInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarDataVariableListInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessTemplateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarTemplateComProInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarTemplateDynamicInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailsOutputDto;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.FunctionBiz;
import com.wiseco.var.process.app.server.service.VarProcessTemplateBiz;
import com.wiseco.var.process.app.server.service.VarProcessTemplateCheckBiz;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.output.DynamicObjectOutputDto;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 给前端规则编写提供模板数据
 *
 * @author wiseco
 */
@Slf4j
@RestController
@RequestMapping("/varProcess/template")
@Tag(name = "变量加工模板")
@LoggableClass(param = "template")
public class VarProcessTemplateController {
    @Autowired
    VarProcessTemplateBiz templateBizImpl;

    @Autowired
    VarProcessTemplateCheckBiz  templateCheckBizImpl;

    @Resource
    private FunctionBiz functionBiz;

    /**
     * 标加工模板配置+所有dataProvider数据
     *
     * @param templateInputDto 入参
     * @return 变量加工模板配置+所有dataProvider数据
     */
    @PostMapping("/static")
    @Operation(summary = "变量加工模板配置+所有dataProvider数据")
    public APIResult<JSONObject> getInputTemplateAndStaticData(@RequestBody VarProcessTemplateInputDto templateInputDto) {
        long start = System.currentTimeMillis();
        JSONObject jsonObj = templateBizImpl.getVarProcessTemplate(templateInputDto);
        log.info("/static-cost:{}ms",System.currentTimeMillis() - start);
        return APIResult.success(jsonObj);
    }


    /**
     * 查询静态接口数据provider
     * @param templateStaticInputDto -入参
     * @return 变量模板所有dataProvider数据
     */
    @PostMapping("/getVariablesByProviderNames")
    @Operation(summary = "静态接口 查询dataProvider数据")
    public APIResult<JSONObject> getVariablesByProviderNames(@RequestBody TemplateStaticGetProviderInputDto templateStaticInputDto) {
        long start = System.currentTimeMillis();
        JSONObject jsonObj = templateBizImpl.getVariablesByProviderNames(templateStaticInputDto);
        log.info("/getVariablesByProviderNames-cost:{}ms",System.currentTimeMillis() - start);
        return APIResult.success(jsonObj);
    }

    /**
     * 查询静态接口数据template
     * 查询模板数据
     * @param templateStaticInputDto -入参
     * @return 查询模板具体内容信息
     */
    @PostMapping("/getTemplateByTmpName")
    @Operation(summary = "静态接口 template模板数据")
    public APIResult<JSONObject> getTemplateByTmpName(@RequestBody TemplateStaticGetTemplateInputDto templateStaticInputDto) {
        JSONObject jsonObj = templateBizImpl.getTemplateByTmpName(templateStaticInputDto);
        return APIResult.success(jsonObj);
    }

    /**
     * 根据变量路径查询变量结构定义（属性定义结构)
     *
     * @param inputDto 输入实体类对象
     * @return dataProvider追加数据接口(返回结构同静态接口)
     */
    @PostMapping("/appendedProviderData")
    @Operation(summary = "dataProvider追加数据接口(返回结构同静态接口)")
    public APIResult<List<DomainDataModelTreeDto>> appendedProviderData(@RequestBody VarAppendedProviderInputDto inputDto) {
        List<DomainDataModelTreeDto> configs = templateBizImpl.appendedProviderData(inputDto);
        return APIResult.success(configs);
    }

    /**
     * 根据变量路径查询变量结构定义(只有value下属性)
     *
     * @param inputDto 输入实体类对象
     * @return 变量结构定义(只有value下属性)
     */
    @PostMapping("/appendedThisProviderData")
    @Operation(summary = "只有this_dataProvider追加数据接口")
    public APIResult<List<DomainDataModelTreeDto>> appendedThisProviderData(@RequestBody VarTemplateDynamicInputDto inputDto) {
        List<DomainDataModelTreeDto> configs = templateBizImpl.appendedThisProviderData(inputDto);
        return APIResult.success(configs);
    }

    /**
     * 根据变量路径（对象）获取一级基本属性
     *
     * @param inputDto 输入实体类对象
     * @return 变量路径（对象）的一级基本属性
     */
    @PostMapping("/getVarBasicProperty")
    @Operation(summary = "根据变量路径（对象）获取一级基本属性")
    public APIResult<JSONObject> getVarBasicProperty(@RequestBody VarTemplateDynamicInputDto inputDto) {
        JSONObject jsonObject = templateBizImpl.getVarBasicProperty(inputDto);
        return APIResult.success(jsonObject);
    }

    /**
     * 根据变量路径（对象）动态获取相对象
     *
     * @param inputDto 输入实体类对象
     * @return 变量路径（对象）的相对象
     */
    @PostMapping("/queryObjectDynamic")
    @Operation(summary = "根据变量路径（对象）动态获取相对象")
    public APIResult<DynamicObjectOutputDto> queryObjectDynamic(@RequestBody VarTemplateDynamicInputDto inputDto) {
        return APIResult.success(templateBizImpl.queryObjectDynamic(inputDto));
    }

    /**
     * 根据变量路径（对象）动态获取相对象数组
     *
     * @param inputDto 输入实体类对象
     * @return 变量路径（对象）的相对象数组
     */
    @PostMapping("/queryObjectArrayDynamic")
    @Operation(summary = "根据变量路径（对象）获取相对象数组")
    public APIResult<DynamicObjectOutputDto> queryObjectArrayDynamic(@RequestBody VarTemplateDynamicInputDto inputDto) {
        return APIResult.success(templateBizImpl.queryObjectArrayDynamic(inputDto));
    }

    /**
     * 根据变量路径（对象）一级属性对比
     *
     * @param inputDto inputDto
     * @return APIResult
     */
    @PostMapping("/compareObjectProperty")
    @Operation(summary = "根据变量路径（对象）一级属性对比")
    public APIResult compareObjectProperty(@RequestBody VarTemplateComProInputDto inputDto) {
        templateBizImpl.compareObjectProperty(inputDto);
        return APIResult.success();
    }

    /**
     * 获取数据变量类型集合
     *
     * @param inputDto 输入实体类对象
     * @return 数据变量类型集合
     */
    @PostMapping("/getDataVariableList")
    @Operation(summary = "获取数据变量类型集合")
    public APIResult<List<Content>> getDataVariableList(@RequestBody VarDataVariableListInputDto inputDto) {
        List<Content> list = templateBizImpl.getDataVariableList(inputDto);
        return APIResult.success(list);
    }

    /**
     * 获取数据provider集合
     * @param inputDto 输入实体类对象
     * @return 数据provider集合
     */
    @PostMapping("/getDataProviderList")
    @Operation(summary = "获取数据provider集合")
    public APIResult<List<DomainDataModelTreeDto>> getDataProviderList(@RequestBody VarDataProviderListInputDto inputDto) {
        List<DomainDataModelTreeDto> list = templateBizImpl.getDataProviderList(inputDto);
        return APIResult.success(list);
    }

    /**
     * 获取字典数据provider集合
     * @param inputDto 输入实体类对象
     * @return 字典数据provider集合
     */
    @PostMapping("/getDictProviderList")
    @Operation(summary = "获取字典数据provider集合")
    public APIResult<List<DomainDataModelTreeDto>> getDictProviderList(@RequestBody VarDataProviderListInputDto inputDto) {
        List<DomainDataModelTreeDto> list = templateBizImpl.getDataProviderList(inputDto);
        return APIResult.success(list);
    }

    /**
     * 获取数据字典项
     *
     * @param inputDto 入参
     * @return 数据字典项
     */
    @PostMapping("/findDict")
    @Operation(summary = "获取数据字典项")
    public APIResult<List<DictDetailsOutputDto>> findDict(@RequestBody QueryDictInputDto inputDto) {

        return APIResult.success(templateBizImpl.findDict(inputDto));
    }

    /**
     * 启用停用前校验
     * @param inputDto 修改公共函数状态 DTO
     * @return 校验结果信息
     */
    @PostMapping("/validateStatusUpdate")
    @Operation(summary = "启用停用前校验")
    public APIResult<String> validateStatusUpdate(@RequestBody @Validated FlowUpdateStatusInputDto inputDto) {
        return APIResult.success(templateCheckBizImpl.validateStatusUpdate(inputDto));
    }

    /**
     * 获取内置函数EngineFunction的模版内容
     * @param inputDto 入参
     * @return 内置函数模版内容
     */
    @PostMapping("/getEngineFunctionTemplate")
    @Operation(summary = "获取内置函数EngineFunction的模版内容")
    public APIResult<JSONObject> findDict(@RequestBody EngineFunctionTemplateInputDto inputDto) {
        return APIResult.success(templateBizImpl.getEngineFunctionTemplate(inputDto));
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
    @LoggableMethod(value = "添加%s[%s]",params = {"functionType","name"},type = LoggableMethodTypeEnum.CREATE)
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
    @LoggableMethod(value = "编辑%s[%s]",params = {"functionType","name"}, type = LoggableMethodTypeEnum.EDIT)
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
    @LoggableDynamicValue(params = {"var_process_function","copyId"})
    @LoggableMethod(value = "复制%s[%s]为[%s]",params = {"copyId","functionType","name"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copyFunction(@RequestBody FunctionCopyInputDto inputDto) {
        return APIResult.success(functionBiz.copyFunction(inputDto));
    }

    /**
     * 删除
     *
     * @param spaceId 变量空间Id
     * @param functionId 公共函数Id
     * @return 删除公共函数后的结果
     */
    @GetMapping("/deleteFunction")
    @Operation(summary = "删除")
    @LoggableDynamicValue(params = {"var_process_function","functionId"})
    @LoggableMethod(value = "删除%s[%s]",params = {"functionId"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteFunction(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, @Parameter(description = "公共预处理模板id") @RequestParam("functionId") Long functionId) {
        functionBiz.updateStatus(FlowUpdateStatusInputDto.builder().functionId(functionId).spaceId(spaceId).actionType(FlowActionTypeEnum.DELETE)
                .build());
        return APIResult.success();
    }

    /**
     * 启用停用
     *
     * @param inputDto 输入实体类对象
     * @return 启用公共函数后的结果
     */
    @PostMapping("/updateEnableFunction/{actionType}")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "启用停用")
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
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "修改状态（提交、审核）")
    public APIResult updateStatus(@RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.updateStatus(inputDto);
        return APIResult.success();
    }
}
