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
package com.wiseco.var.process.app.server.service.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.outside.service.rpc.dto.input.OutsideServiceAuthInputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceAuthErrorMsgOutputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.service.VariableDataProviderBiz;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.engine.compiler.EngineCompiler;
import com.wiseco.decision.engine.java.common.enums.CompileEnvEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarFunctionSubTypeEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.IVarCompilerEntry;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleQueryDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableCompileMessageEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessConfigDefaultService;
import com.wiseco.var.process.app.server.service.VarProcessConfigExceptionService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wiseco.var.process.app.server.service.VarProcessTemplateBiz;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.dto.StrComponentVarPathDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.service.VarProcessTemplateBiz.RAW_DATA;

/**
 * @author wangxianli
 */
@Service
@Slf4j
public class VariableCompileBiz {

    public static final String PUBLIC_FUNCTION_ERROR_MESSAGE = "公共函数变量编译缺少变量定义信息";

    @Autowired
    private IVarCompilerEntry                 iVarCompilerEntry;

    @Autowired
    private VarProcessSpaceService            varProcessSpaceService;

    @Autowired
    private VarProcessVariableService         varProcessVariableService;

    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;

    @Autowired
    private VarProcessFunctionService         varProcessFunctionService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VariableDataProviderBiz variableDataProviderBiz;

    @Autowired
    private VarProcessOutsideRefService       varProcessOutsideServiceRefService;

    @Autowired
    private VarProcessConfigDefaultService    varProcessConfigDefaultValueService;

    @Autowired
    private VarProcessConfigExceptionService  varProcessConfigExceptionValueService;

    @Autowired
    private VarProcessInternalDataService     varProcessInternalDataService;

    @Autowired
    private VarProcessTemplateBiz             varProcessTemplateBiz;

    @Autowired
    private EngineCompiler                    engineCompiler;

    @Autowired
    private OutsideService outsideService;

    @Resource
    private CompileDataBuildService compileDataBuildService;

    private static final String               BASE_DATA       = "base_data";
    private static final String               DATA_MODEL      = "data_model";
    private static final String               SPECIFIC_DATA   = "specific_data";
    private static final String               RESULT_BINDINGS = "result_bindings";
    private static final String               PARAMETERS      = "参数";
    private static final String               LOCAL_VARS      = "本地变量";

    /**
     * validate
     * @param type 变量测试的类型
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @param content 内容
     * @return 变量编译验证返回Dto
     */
    public VariableCompileOutputDto validate(TestVariableTypeEnum type, Long spaceId, Long variableId, String content) {
        long starttime = System.currentTimeMillis();
        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        VarCompileData varCompileData = getVarCompileData(type, space, variableId, content);
        Map<String, JSONObject> templateConfig = getTemplateConfig(spaceId, type, variableId);
        //调用编译
        VarCompileResult compileResultVo = iVarCompilerEntry.compile(varCompileData, templateConfig, false, CompileEnvEnum.TEST);

        log.info("变量编译响应 componentId:{},compileResult:{}", variableId, compileResultVo);
        log.info("变量编译调用时长：time={}", (System.currentTimeMillis() - starttime));
        List<String> errorMessageList = new ArrayList<>();
        List<String> warnMessageList = new ArrayList<>();
        VariableCompileOutputDto compileOutputDto = new VariableCompileOutputDto();

        //编译错误
        if (compileResultVo.getErrorException() != null) {
            String msg = compileResultVo.getErrorException().getErrorMessage();
            if (StringUtils.isEmpty(compileResultVo.getErrorException().getErrorMessage())) {
                msg = "未知错误，编译异常";
            }
            if (msg.contains(PUBLIC_FUNCTION_ERROR_MESSAGE)) {
                msg = "缺少变量定义信息";
            }
            errorMessageList.add(msg);
        }
        //编译警告
        if (!CollectionUtils.isEmpty(compileResultVo.getWarnLst())) {
            List<ServiceException> warnLst = compileResultVo.getWarnLst();
            for (ServiceException exception : warnLst) {
                warnMessageList.add(exception.getErrorMessage());
            }
        }

        //编译失败
        if (!compileResultVo.isSuccess()) {
            compileOutputDto.setState(compileResultVo.isSuccess());
            compileOutputDto.setErrorMessageList(errorMessageList);
            compileOutputDto.setWarnMessageList(warnMessageList);
            return compileOutputDto;
        }

        //编译通过后，后端对比校验数据
        VarSyntaxInfo varSyntaxInfo = compileResultVo.getSyntaxInfo();
        //1、数据模型变量对比
        if (varSyntaxInfo.getVarUsedTable() != null && varSyntaxInfo.getVarUsedTable().size() > 0) {
            Map<String, List<String>> validateComVars = validateComVars(space, varSyntaxInfo.getVarUsedTable(), varCompileData.getContent(),
                varCompileData.getType(), varCompileData.getVarFunctionSubType(),variableId);
            errorMessageList.addAll(validateComVars.get("error"));
            warnMessageList.addAll(validateComVars.get("warn"));
        }

        if (type.equals(TestVariableTypeEnum.MANIFEST)) {
            validataManifest(varCompileData, errorMessageList, varSyntaxInfo);
        } else {
            //变量嵌套调用
            if (!CollectionUtils.isEmpty(varSyntaxInfo.getVarIdentifierSet())) {
                List<String> validateComponent = validateReference(varSyntaxInfo.getVarIdentifierSet(), varCompileData.getSpaceId());
                errorMessageList.addAll(validateComponent);
            }
            //引用关系
            if (!CollectionUtils.isEmpty(varSyntaxInfo.getVarFunctionIdentifierSet())) {
                List<String> validateComponent = validateFunction(varSyntaxInfo.getVarFunctionIdentifierSet(), varCompileData.getSpaceId());
                errorMessageList.addAll(validateComponent);
            }
        }

        if (CollectionUtils.isEmpty(errorMessageList)) {
            compileOutputDto.setState(Boolean.TRUE);
        } else {
            compileOutputDto.setState(Boolean.FALSE);
        }
        compileOutputDto.setErrorMessageList(errorMessageList);
        compileOutputDto.setWarnMessageList(warnMessageList);
        compileOutputDto.setCompileResultVo(compileResultVo);
        return compileOutputDto;
    }

    private void validataManifest(VarCompileData varCompileData, List<String> errorMessageList, VarSyntaxInfo varSyntaxInfo) {
        //外部服务
        if (!CollectionUtils.isEmpty(varSyntaxInfo.getExternalServiceSet())) {
            List<String> validateExternalService = validateExternalService(varSyntaxInfo.getExternalServiceSet(), varCompileData.getSpaceId());
            errorMessageList.addAll(validateExternalService);
        }

        //变量清单
        if (!CollectionUtils.isEmpty(varSyntaxInfo.getVarIdentifierSet())) {
            List<String> validateVariable = validateVariable(varSyntaxInfo.getVarIdentifierSet(), varCompileData.getSpaceId(),
                varCompileData.getVarId());
            errorMessageList.addAll(validateVariable);
        } else {
            errorMessageList.add("流程缺少变量加工节点");
        }

        //预处理逻辑
        if (!CollectionUtils.isEmpty(varSyntaxInfo.getPreProcessSet())) {
            List<String> validatePreProcess = validatePreProcess(varSyntaxInfo.getPreProcessSet(), varCompileData.getSpaceId());
            errorMessageList.addAll(validatePreProcess);
        }

        //内部数据
        if (!CollectionUtils.isEmpty(varSyntaxInfo.getInnerDataSet())) {
            List<String> validateInnerData = validateInnerData(varSyntaxInfo.getInnerDataSet(), varCompileData.getSpaceId());
            errorMessageList.addAll(validateInnerData);
        }
    }

    /**
     * compile
     * @param type 变量测试的类型
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @param content 内容
     * @return 变量编译验证返回Dto
     */
    public VariableCompileOutputDto compile(TestVariableTypeEnum type, Long spaceId, Long variableId, String content) {

        long starttime = System.currentTimeMillis();
        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        VarCompileData varCompileData = getVarCompileData(type, space, variableId, content);
        Map<String, JSONObject> templateConfig = getTemplateConfig(spaceId, type, variableId);
        //调用编译
        VarCompileResult compileResultVo = iVarCompilerEntry.compile(varCompileData, templateConfig, false, CompileEnvEnum.TEST);

        log.info("变量编译响应 componentId:{},compileResult:{}", variableId, compileResultVo);
        log.info("变量编译调用时长：time={}", (System.currentTimeMillis() - starttime));

        VariableCompileOutputDto compileOutputDto = new VariableCompileOutputDto();

        List<String> errorMessageList = new ArrayList<>();

        List<String> warnMessageList = new ArrayList<>();

        //编译错误
        if (compileResultVo.getErrorException() != null) {
            String msg = compileResultVo.getErrorException().getErrorMessage();
            if (StringUtils.isEmpty(compileResultVo.getErrorException().getErrorMessage())) {
                msg = "未知错误，组件编译异常";
            }
            errorMessageList.add(msg);
        }

        //编译警告
        if (!CollectionUtils.isEmpty(compileResultVo.getWarnLst())) {
            List<ServiceException> warnLst = compileResultVo.getWarnLst();
            for (ServiceException exception : warnLst) {
                warnMessageList.add(exception.getErrorMessage());

            }
        }
        //编译失败
        if (!compileResultVo.isSuccess()) {
            compileOutputDto.setState(compileResultVo.isSuccess());
            compileOutputDto.setErrorMessageList(errorMessageList);
            compileOutputDto.setWarnMessageList(warnMessageList);
            return compileOutputDto;
        }
        if (CollectionUtils.isEmpty(errorMessageList)) {
            compileOutputDto.setState(Boolean.TRUE);
        } else {
            compileOutputDto.setState(Boolean.FALSE);
        }
        compileOutputDto.setCompileResultVo(compileResultVo);
        return compileOutputDto;
    }

    /**
     * 编译单个变量
     * @param type 变量测试的类型
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @param content 内容
     * @return 编译单个变量后的结果
     */
    public String compileSingleVar(TestVariableTypeEnum type, Long spaceId, Long variableId, String content) {
        long starttime = System.currentTimeMillis();
        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        VarCompileData varCompileData = getVarCompileData(type, space, variableId, content);
        com.wiseco.decision.model.engine.VarProcessSpace varProcessSpace = com.wiseco.decision.model.engine.VarProcessSpace.builder()
            .id(space.getId()).code(space.getCode()).name(space.getName()).inputData(space.getInputData()).build();
        try {
            log.info("变量class编译开始 componentId:{},content:{}", variableId, content);
            String classData = engineCompiler.compileSingleVar(varProcessSpace, varCompileData);
            log.info("变量class编译响应 componentId:{},classData:{}", variableId, classData);
            log.info("变量class编译调用时长：time={}", (System.currentTimeMillis() - starttime));
            return classData;
        } catch (Throwable e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private VarCompileData getVarCompileData(TestVariableTypeEnum type, VarProcessSpace space, Long variableId, String content) {
        VarTypeEnum compileType = null;
        VarFunctionSubTypeEnum functionSubType = null;
        VariableStatusEnum status = null;
        Integer version = null;
        String identifier = null,name = null,enName = null,dataType = null;
        Long spaceId = null;
        VarStatusEnum varStatusEnum = VarStatusEnum.CHECK_IN;
        if (type.equals(TestVariableTypeEnum.VAR)) {
            VarProcessVariable variable = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getStatus, VarProcessVariable::getVersion, VarProcessVariable::getIdentifier, VarProcessVariable::getLabel, VarProcessVariable::getName, VarProcessVariable::getVarProcessSpaceId, VarProcessVariable::getDataType)
                    .eq(VarProcessVariable::getId, variableId));
            status = variable.getStatus();
            if (status != null && status.equals(VariableStatusEnum.EDIT)) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            version = variable.getVersion();
            identifier = variable.getIdentifier();
            name = variable.getLabel();
            enName = variable.getName();
            spaceId = variable.getVarProcessSpaceId();
            compileType = VarTypeEnum.VAR;
            dataType = variable.getDataType();
        } else if (type.equals(TestVariableTypeEnum.FUNCTION)) {
            VarProcessFunction function = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                    .select(VarProcessFunction::getStatus, VarProcessFunction::getIdentifier, VarProcessFunction::getName,
                            VarProcessFunction::getVarProcessSpaceId, VarProcessFunction::getFunctionType, VarProcessFunction::getFunctionDataType)
                    .eq(VarProcessFunction::getId, variableId));
            if (function.getStatus() != null && function.getStatus() == FlowStatusEnum.EDIT) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            version = 1;
            identifier = function.getIdentifier();
            name = function.getName();
            spaceId = function.getVarProcessSpaceId();
            compileType = VarTypeEnum.FUNCTION;
            functionSubType = getVarFunctionSubTypeEnum(functionSubType, function);
            dataType = function.getFunctionDataType();
        } else if (type.equals(TestVariableTypeEnum.MANIFEST)) {
            VarProcessManifest manifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getId, VarProcessManifest::getVersion, VarProcessManifest::getVarProcessSpaceId)
                    .eq(VarProcessManifest::getId, variableId));
            version = manifest.getVersion();
            identifier = String.valueOf(manifest.getId());
            spaceId = manifest.getVarProcessSpaceId();
            compileType = VarTypeEnum.MAINFLOW;
        }
        //异常值
        Map<String, String> exceptionDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list();
        if (!CollectionUtils.isEmpty(exceptionValueList)) {
            exceptionDefaultValueLst = exceptionValueList.stream().collect(Collectors.toMap(VarProcessConfigExcept::getExceptionValueCode, VarProcessConfigExcept::getExceptionValue, (v1, v2) -> v2));
        }
        //缺失值
        Map<String, String> normalDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigDefault> defaultValueList = varProcessConfigDefaultValueService.list(new QueryWrapper<VarProcessConfigDefault>().lambda().eq(VarProcessConfigDefault::getVarProcessSpaceId, spaceId));
        if (!CollectionUtils.isEmpty(defaultValueList)) {
            for (VarProcessConfigDefault varProcessConfigDefault : defaultValueList) {
                if (StringUtils.isEmpty(varProcessConfigDefault.getDefaultValue())) {
                    continue;
                }
                normalDefaultValueLst.put(varProcessConfigDefault.getDataType(), varProcessConfigDefault.getDefaultValue());
            }
        }
        VarCompileData varCompileData = VarCompileData.builder().varId(variableId).identifier(identifier).changeNum(version).varStatus(varStatusEnum).name(name)
                .enName(enName).type(compileType).returnType(dataType).varFunctionSubType(functionSubType).spaceId(spaceId).spaceCode(space.getCode()).content(content)
                .normalDefaultValueLst(normalDefaultValueLst).exceptionDefaultValueLst(exceptionDefaultValueLst).build();
        varCompileData.setStrategyPropertyEditorMap(compileDataBuildService.getStrategyPropertyEditorMap(spaceId, space.getInputData(), space.getInternalData(), space.getExternalData()));

        compileDataBuildService.assembleCompileData(type, space.getCode(), variableId, spaceId, content, varCompileData);
        return varCompileData;
    }



    private static VarFunctionSubTypeEnum getVarFunctionSubTypeEnum(VarFunctionSubTypeEnum functionSubType, VarProcessFunction function) {
        if (function.getFunctionType() == FunctionTypeEnum.TEMPLATE) {
            functionSubType = VarFunctionSubTypeEnum.VAR_TEMPLATE;
        } else if (function.getFunctionType() == FunctionTypeEnum.FUNCTION) {
            functionSubType = VarFunctionSubTypeEnum.PUBLIC_METHOD;
        } else if (function.getFunctionType() == FunctionTypeEnum.PREP) {
            functionSubType = VarFunctionSubTypeEnum.PRE_PROCESS;
        }
        return functionSubType;
    }

    private Map<String, JSONObject> getTemplateConfig(Long spaceId, TestVariableTypeEnum type, Long id) {
        VarTemplateTypeEnum varTemplateTypeEnum = null;
        Long excludeFunctionId = null;
        if (type.equals(TestVariableTypeEnum.VAR)) {
            varTemplateTypeEnum = VarTemplateTypeEnum.VAR_PROCESS;
        } else if (type.equals(TestVariableTypeEnum.FUNCTION)) {
            varTemplateTypeEnum = VarTemplateTypeEnum.COMMON_FUNCTION;
            excludeFunctionId = id;
        } else {
            varTemplateTypeEnum = VarTemplateTypeEnum.SERVICE_INTERFACE;
        }
        JSONObject templateConfig = varProcessTemplateBiz.fillStaticTemplateAndFunction(spaceId, varTemplateTypeEnum, excludeFunctionId);

        return JSONObject.parseObject(templateConfig.toJSONString(), new TypeReference<Map<String, JSONObject>>() {
        });
    }


    /**
     * 变量验证
     * @param space
     * @param varUsedTable
     * @param content
     * @param typeEnum
     * @param subTypeEnum
     * @param variableId
     * @return  Map<String, List<String>>
     */
    private Map<String, List<String>> validateComVars(VarProcessSpace space, Map<String, VarActionHistory> varUsedTable, String content,
                                                      VarTypeEnum typeEnum, VarFunctionSubTypeEnum subTypeEnum,Long variableId) {
        //1.变量是否存在 2.变量的类型是否改变 3.变量是否数组的信息是否改变
        JSONObject dataModelJson = new JSONObject();

        DomainDataModelTreeDto inputDto = DomainModelTreeEntityUtils.getDomainModelTree(space.getInputData());
        transforDataModelToJsonObject(inputDto, dataModelJson);

        List<StrComponentVarPathDto> dataModelVarList = new ArrayList<>();
        List<StrComponentVarPathDto> baseParamVarList = new ArrayList<>();
        List<StrComponentVarPathDto> refParamVarList = new ArrayList<>();
        String returnDataType = null;
        Set<Map.Entry<String, VarActionHistory>> varUsedTableEntries = varUsedTable.entrySet();
        for (Map.Entry<String, VarActionHistory> actionHistory : varUsedTableEntries) {

            String varPath = actionHistory.getKey();
            if (StringUtils.isEmpty(varPath)) {
                continue;
            }
            VarActionHistory varInfo = actionHistory.getValue();
            /*if (varInfo.getIsDirect() == null || !varInfo.getIsDirect()) {
                continue;
            }*/
            if (varPath.startsWith(PositionVarEnum.VARS.getName())) {
                continue;
            }
            //公共函数返回值不进行数据模型验证
            if (CommonConstant.COMMON_FUNCTION_RETURN_NAME.equals(varPath) || CommonConstant.VARIABLE_RETURN_NAME.equals(varPath)) {
                returnDataType = varInfo.getVarType();
                continue;
            }
            if ("void".equals(varInfo.getVarType())) {
                continue;
            }
            String varType = varInfo.getVarType();
            String isArr = varInfo.getIsArr();
            String parameterType = varInfo.getParameterType();
            String isParameterArray = varInfo.getIsParameterArray();
            String actionHistoryString = varInfo.getActionHistory();

            if (varPath.startsWith(PositionVarEnum.PARAMETERS.getName()) || varPath.startsWith(PositionVarEnum.LOCAL_VARS.getName())) {

                StrComponentVarPathDto dto = new StrComponentVarPathDto();
                dto.setVarPath(varPath);
                dto.setVarType(varType);
                dto.setIsArray(Integer.parseInt(isArr));
                dto.setParameterType(parameterType);
                dto.setActionHistory(actionHistoryString);
                if (!StringUtils.isEmpty(isParameterArray)) {
                    dto.setIsParameterArray(Integer.parseInt(isParameterArray));
                }

                if (StringUtils.isEmpty(varInfo.getParameterType()) || DataVariableBasicTypeEnum.getNameEnum(parameterType) != null) {
                    dto.setParameterType(dto.getVarType());
                    dto.setIsParameterArray(dto.getIsArray());
                    //基础类型
                    baseParamVarList.add(dto);
                } else {
                    //引用类型
                    refParamVarList.add(dto);
                }
            } else {
                StrComponentVarPathDto dto = new StrComponentVarPathDto();
                dto.setVarPath(varPath);
                dto.setVarType(varType);
                dto.setIsArray(Integer.parseInt(isArr));
                dataModelVarList.add(dto);
            }

        }

        return getRetMap(new GetRetMapParam(content, typeEnum, subTypeEnum, dataModelJson, dataModelVarList, baseParamVarList, refParamVarList, returnDataType,variableId));
    }

    private Map<String, List<String>> getRetMap(GetRetMapParam getRetMapParam) {
        Map<String, List<String>> retMap = new HashMap<>(MagicNumbers.EIGHT);

        //解析出参数和本地变量
        JSONObject paramLocalVars = getParamLocalVars(getRetMapParam.getContent());

        //校验：组件使用的输入、输出、引擎变量、外部服务变量、公共模块变量与定义的是否一致或属性是否匹配
        //提示：组件使用的输入变量【input.age】已删除 --

        //校验：组件使用的参数、本地变量与定义的是否一致或属性是否匹配
        //提示：组件使用的本地变量【localVars.age】已删除 --

        //校验：组件定义的参数、本地变量选择的引用对象与数据模型、引擎变量定义、外部服务出参、公共模块的不匹配
        //提示：组件定义的参数localVars.age引用的对象【input.age】已删除 --

        List<String> checkDataModelList = checkDataModel(getRetMapParam.getDataModelVarList(), getRetMapParam.getBaseParamVarList(), getRetMapParam.getRefParamVarList(), paramLocalVars, getRetMapParam.getDataModelJson(),getRetMapParam.getTypeEnum(),getRetMapParam.getVariableId());

        //返回值校验
        if (!StringUtils.isEmpty(getRetMapParam.getReturnDataType())) {
            String varDataType = getVarDataType(getRetMapParam.getContent(), getRetMapParam.getTypeEnum(), getRetMapParam.getSubTypeEnum());
            if (varDataType != null && !varDataType.equals(getRetMapParam.getReturnDataType())) {
                String desc = "变量";
                if (getRetMapParam.getTypeEnum() == VarTypeEnum.FUNCTION && getRetMapParam.getSubTypeEnum() == VarFunctionSubTypeEnum.VAR_TEMPLATE) {
                    desc = "变量模板返回值";
                } else if (getRetMapParam.getTypeEnum() == VarTypeEnum.FUNCTION && getRetMapParam.getSubTypeEnum() == VarFunctionSubTypeEnum.PUBLIC_METHOD) {
                    desc = "公共方法返回值";
                }
                checkDataModelList.add(desc + "类型配置不一致。");
            }
        }
        retMap.put("error", checkDataModelList);

        //校验：组件定义的参数、本地变量未使用
        //提示：定义的本地变量【XXX】未被使用 警告
        List<String> notUseParamWarnList = checkParamIsUse(getRetMapParam.getBaseParamVarList(), getRetMapParam.getRefParamVarList(), paramLocalVars);
        retMap.put("warn", notUseParamWarnList);
        return retMap;
    }

    /**
     * 外部服务引入校验
     *
     * @param infoVoSet 外部服务信息 Set
     * @param spaceId   变量空间 ID
     * @return 编译错误信息 List
     */
    private List<String> validateExternalService(Set<VarSyntaxInfo.ExternalServiceInfo> infoVoSet, Long spaceId) {
        // 变量空间-外部服务接收对象名称 Set
        Set<String> responseObjNameSet = infoVoSet.stream()
                .map(VarSyntaxInfo.ExternalServiceInfo::getResponseObjName)
                .collect(Collectors.toSet());
        // 编译错误信息 List
        List<String> compileErrorMessageList = new ArrayList<>();

        // 校验外部服务接收对象是否存在
        List<VarProcessOutsideRef> refObjectList = varProcessOutsideServiceRefService.list(
                new QueryWrapper<VarProcessOutsideRef>().lambda()
                        .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getName)
                        .eq(VarProcessOutsideRef::getVarProcessSpaceId, spaceId)
        );
        if (CollectionUtils.isEmpty(refObjectList)) {
            compileErrorMessageList.add(VariableCompileMessageEnum.OUTSIDE_REF_STRATEGY_DEL.getMessage());
            return compileErrorMessageList;
        }
        // 校验使用的外部服务接收对象是否定义
        Set<String> receivingObjNameSet = refObjectList.stream()
                .map(VarProcessOutsideRef::getName)
                .collect(Collectors.toSet());
        // 从编译结果接收对象 Set 移除当前定义的接收对象 (编译结果接收对象 <= 当前定义的接收对象)
        responseObjNameSet.removeAll(receivingObjNameSet);
        if (!CollectionUtils.isEmpty(responseObjNameSet)) {
            // 编译结果接收对象 Set 出现当前未定义的接收对象
            String undefinedResponseObjNameSeq = org.apache.commons.lang3.StringUtils.join(responseObjNameSet, ", ");
            compileErrorMessageList.add(MessageFormat.format(VariableCompileMessageEnum.OUTSIDE_REF_RECEIVING_OBJECT_MISSING.getMessage(), undefinedResponseObjNameSeq));
            return compileErrorMessageList;
        }
        // 调用外数接口 校验外部服务状态及授权码
        List<OutsideServiceAuthInputDto.AuthCodeMapDto> validateDto = infoVoSet.stream().map(infoVo -> OutsideServiceAuthInputDto.AuthCodeMapDto.builder().serviceCode(infoVo.getServiceCode()).authCode(infoVo.getAuthCode()).build()).collect(Collectors.toList());
        try {
            List<OutsideServiceAuthErrorMsgOutputDto> validateResults = outsideService.validateAuthCodeBatch(OutsideServiceAuthInputDto.builder().dtoList(validateDto).build()).getData();

            if (!CollectionUtils.isEmpty(validateResults)) {
                Map<String, String> externalNodeMap = infoVoSet.stream().collect(Collectors.toMap(o -> (o.getServiceCode() + o.getAuthCode()), o -> o.getLabel()));
                validateResults.forEach(result -> {
                    String authCode = result.getAuthCode();
                    String serviceCode = result.getServiceCode();
                    String nodeName = externalNodeMap.getOrDefault(serviceCode + authCode, "");
                    compileErrorMessageList.add(MessageFormat.format("外数调用节点{0}的{1}",nodeName,result.getMessage()));
                });
            }
        } catch (FeignException e) {
            log.error("fail while remote calling outside service : ", e);
            compileErrorMessageList.add(VariableCompileMessageEnum.OUTSIDE_REF_VALIDATE_FAIL.getMessage());
        }
        return compileErrorMessageList;
    }

    /**
     * 变量清单校验
     *
     * @param identifierLst
     * @param spaceId
     * @param manifestId
     * @return List<String>
     */
    private List<String> validateVariable(Set<String> identifierLst, Long spaceId, Long manifestId) {
        List<String> list = new ArrayList<>();
        List<String> identifiers = new ArrayList<>();
        VariableFlowQueryDto variableFlowQueryDto = VariableFlowQueryDto.builder().spaceId(spaceId).manifestId(manifestId).build();
        List<VarProcessVariable> manifestVariableList = varProcessManifestVariableService.getVariableFlow(variableFlowQueryDto);
        for (VarProcessVariable variable : manifestVariableList) {
            if (!identifierLst.contains(variable.getIdentifier())) {
                list.add(MessageFormat.format(VariableCompileMessageEnum.VARIABLE_REF_CHECK.getMessage(), variable.getName() + "-" + variable.getLabel()));
            } else if (!variable.getStatus().equals(VariableStatusEnum.UP)) {
                list.add(MessageFormat.format(VariableCompileMessageEnum.VARIABLE_REF_OFF.getMessage(), variable.getName() + "-" + variable.getLabel()));
            }
            identifiers.add(variable.getIdentifier());
        }
        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getId, VarProcessVariable::getIdentifier, VarProcessVariable::getName, VarProcessVariable::getLabel)
                        .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                        .in(VarProcessVariable::getIdentifier, new ArrayList<>(identifierLst))
        );
        Map<String, List<VarProcessVariable>> variableMap = variableList.stream().collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));

        for (String str : identifierLst) {
            if (!identifiers.contains(str)) {
                VarProcessVariable variable = variableMap.get(str).get(0);
                list.add(MessageFormat.format(VariableCompileMessageEnum.VARIABLE_REF_List.getMessage(), variable.getName() + "-" + variable.getLabel()));
            }
        }

        return list;
    }

    /**
     * 预处理逻辑
     *
     * @param identifierLst
     * @param spaceId
     * @return  List<String>
     */
    private List<String> validatePreProcess(Set<String> identifierLst, Long spaceId) {

        List<String> list = new ArrayList<>();

        List<String> identifiers = new ArrayList<>(identifierLst);

        List<VarProcessFunction> varProcessFunctionList = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(
                                VarProcessFunction::getId,
                                VarProcessFunction::getVarProcessSpaceId,
                                VarProcessFunction::getIdentifier,
                                VarProcessFunction::getFunctionType,
                                VarProcessFunction::getName,
                                VarProcessFunction::getPrepObjectName,
                                VarProcessFunction::getStatus,
                                VarProcessFunction::getFunctionDataType,
                                VarProcessFunction::getDeleteFlag
                        )
                        .in(VarProcessFunction::getIdentifier, identifiers)
                        .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
        );
        Map<String, List<VarProcessFunction>> idMap = varProcessFunctionList.stream().collect(Collectors.groupingBy(VarProcessFunction::getIdentifier));
        identifierLst.forEach(identifier -> {
            List<VarProcessFunction> functionList = idMap.get(identifier);
            if (CollectionUtils.isEmpty(functionList)) {
                list.add("引用的数据预处理" + "【" + identifier + "】不存在");
            } else if (functionList.stream().allMatch(func -> Objects.equals(func.getDeleteFlag(), DeleteFlagEnum.DELETED.getCode()))) {
                list.add("引用的" + functionList.get(0).getFunctionType().getDesc() + "【" + functionList.get(0).getName() + "】不存在");
            } else if (functionList.stream().noneMatch(func -> FlowStatusEnum.UP.equals(func.getStatus()))) {
                list.add("引用的" + functionList.get(0).getFunctionType().getDesc() + "【" + functionList.get(0).getName() + "】未启用");
            }
        });

        return list;
    }

    /**
     * 内部数据
     * @param identifierLst 唯一标识符的Set集合
     * @param spaceId 变量空间Id
     * @return List<String>
     */
    private List<String> validateInnerData(Set<String> identifierLst, Long spaceId) {

        List<String> list = new ArrayList<>();

        List<String> identifiers = new ArrayList<>(identifierLst);

        List<VarProcessInternalData> varProcessFunctionList = varProcessInternalDataService.list(
                new QueryWrapper<VarProcessInternalData>().lambda()
                        .in(VarProcessInternalData::getIdentifier, identifiers)
                        .eq(VarProcessInternalData::getVarProcessSpaceId, spaceId)
        );
        Map<String, VarProcessInternalData> idMap = varProcessFunctionList.stream().collect(Collectors.toMap(VarProcessInternalData::getIdentifier, value -> value, (key1, key2) -> key1));

        for (String identifier : identifierLst) {
            if (!idMap.containsKey(identifier)) {
                list.add(MessageFormat.format(VariableCompileMessageEnum.INTERNAL_DATA_REF_DEL.getMessage(), identifier));

            } else {
                VarProcessInternalData varProcessFunction = idMap.get(identifier);
                if (varProcessFunction.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                    list.add(MessageFormat.format(VariableCompileMessageEnum.INTERNAL_DATA_REF_DEL.getMessage(), varProcessFunction.getName()));

                }
            }
        }


        return list;
    }

    /**
     * 引用公共函数校验
     *
     * @param identifierLst
     * @param spaceId
     * @return List<String>
     */
    private List<String> validateReference(Set<String> identifierLst, Long spaceId) {


        //提示：引用的变量【XXXX】已删除

        List<String> list = new ArrayList<>();

        List<String> identifiers = new ArrayList<>(identifierLst);

        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(
                                VarProcessVariable::getId,
                                VarProcessVariable::getVarProcessSpaceId,
                                VarProcessVariable::getCategoryId,
                                VarProcessVariable::getIdentifier,
                                VarProcessVariable::getName,
                                VarProcessVariable::getLabel,
                                VarProcessVariable::getDataType,
                                VarProcessVariable::getStatus
                        )
                        .in(VarProcessVariable::getIdentifier, identifiers)
                        .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .orderByDesc(VarProcessVariable::getVersion)
        );
        Map<String, List<VarProcessVariable>> idMap = variableList.stream().collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));

        for (String identifier : identifierLst) {
            if (!idMap.containsKey(identifier)) {
                list.add(MessageFormat.format(VariableCompileMessageEnum.VARIABLE_REF_DEL.getMessage(), identifier));

            } else {
                List<VarProcessVariable> variables = idMap.get(identifier);
                List<VariableStatusEnum> statusList = variables.stream().map(VarProcessVariable::getStatus).collect(Collectors.toList());
                //
                if (!statusList.contains(VariableStatusEnum.UP)) {
                    list.add(MessageFormat.format(VariableCompileMessageEnum.VARIABLE_REF_OFF.getMessage(), variables.get(0).getName()));
                }
            }
        }


        return list;
    }

    /**
     * 引用公共函数校验
     *
     * @param identifierLst
     * @param spaceId
     * @return List<String>
     */
    private List<String> validateFunction(Set<String> identifierLst, Long spaceId) {


        //提示：引用的变量模板【XXXX】已删除

        List<String> list = new ArrayList<>();

        List<String> identifiers = new ArrayList<>(identifierLst);

        List<VarProcessFunction> varProcessFunctionList = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(
                                VarProcessFunction::getId,
                                VarProcessFunction::getVarProcessSpaceId,
                                VarProcessFunction::getIdentifier,
                                VarProcessFunction::getFunctionType,
                                VarProcessFunction::getName,
                                VarProcessFunction::getPrepObjectName,
                                VarProcessFunction::getStatus,
                                VarProcessFunction::getFunctionDataType,
                                VarProcessFunction::getDeleteFlag
                        )
                        .in(VarProcessFunction::getIdentifier, identifiers)
                        .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        Map<String, VarProcessFunction> idMap = varProcessFunctionList.stream().collect(Collectors.toMap(VarProcessFunction::getIdentifier, value -> value, (key1, key2) -> key1));

        for (String identifier : identifierLst) {
            if (!idMap.containsKey(identifier)) {
                list.add(MessageFormat.format(VariableCompileMessageEnum.FUNCTION_REF_NO_FOUND.getMessage(), "公共函数", identifier));

            } else {
                VarProcessFunction varProcessFunction = idMap.get(identifier);
                if (!varProcessFunction.getDeleteFlag().equals(DeleteFlagEnum.USABLE.getCode())) {
                    FunctionTypeEnum typeEnum = varProcessFunction.getFunctionType();
                    list.add(MessageFormat.format(VariableCompileMessageEnum.FUNCTION_REF_DEL.getMessage(), typeEnum.getDesc(), varProcessFunction.getName()));
                } else if (FlowStatusEnum.UP != varProcessFunction.getStatus()) {
                    FunctionTypeEnum typeEnum = varProcessFunction.getFunctionType();
                    list.add(MessageFormat.format(VariableCompileMessageEnum.FUNCTION_REF_NOT_ENABLED.getMessage(), typeEnum.getDesc(), varProcessFunction.getName()));
                }

            }
        }


        return list;
    }

    /**
     * 将数据模型转换为JSONObject
     *
     * @param contentDto
     * @param newNameJson
     */
    private void transforDataModelToJsonObject(DomainDataModelTreeDto contentDto, JSONObject newNameJson) {
        //新JSONObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isArr", contentDto.getIsArr());
        jsonObject.put("type", contentDto.getType());
        newNameJson.put(contentDto.getValue(), jsonObject);

        List<DomainDataModelTreeDto> children = contentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (DomainDataModelTreeDto treeDto : children) {

            transforDataModelToJsonObject(treeDto, newNameJson);
        }
    }


    /**
     * 解析当前变量或者公共函数数据类型
     * @param content 内容
     * @param typeEnum 类型枚举
     * @param subTypeEnum 子类型枚举
     * @return 获取当前变量或者公共函数数据类型
     */
    private String getVarDataType(String content, VarTypeEnum typeEnum, VarFunctionSubTypeEnum subTypeEnum) {

        String result = null;
        if (!StringUtils.isEmpty(content)) {
            JSONObject componentJsonDto = JSONObject.parseObject(content);
            if (typeEnum == VarTypeEnum.VAR) {
                if (!componentJsonDto.containsKey(BASE_DATA)) {
                    return null;
                }
                JSONObject baseData = componentJsonDto.getJSONObject(BASE_DATA);
                result = baseData.getString("dataType");
            } else {
                if (subTypeEnum == VarFunctionSubTypeEnum.PRE_PROCESS) {
                    return null;
                }
                if (!componentJsonDto.containsKey(SPECIFIC_DATA)) {
                    return null;
                }
                JSONObject specificData = componentJsonDto.getJSONObject(SPECIFIC_DATA);
                if (!specificData.containsKey(RESULT_BINDINGS)) {
                    return null;
                }
                result = specificData.getJSONObject(RESULT_BINDINGS).getString("type");
            }
        }
        return result;
    }

    /**
     * 解析参数和本地变量
     *
     * @param content
     * @return JSONObject
     */
    private JSONObject getParamLocalVars(String content) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(content)) {
            return jsonObject;
        }
        JSONObject componentJsonDto = JSONObject.parseObject(content);
        if (!componentJsonDto.containsKey(BASE_DATA)) {
            return jsonObject;
        }
        JSONObject baseData = componentJsonDto.getJSONObject(BASE_DATA);
        if (!baseData.containsKey(DATA_MODEL)) {
            return jsonObject;
        }
        JSONObject dataModel = baseData.getJSONObject(DATA_MODEL);
        Set<String> keySet = dataModel.keySet();
        for (String key : keySet) {
            JSONArray paramJsonArray = dataModel.getJSONArray(key);
            for (int i = 0; i < paramJsonArray.size(); i++) {
                JSONObject paramSubObject = paramJsonArray.getJSONObject(i);
                jsonObject.put(key + "." + paramSubObject.getString("name"), paramSubObject);
            }
        }
        return jsonObject;
    }

    /**
     * 对比数据模型，参数，本地变量
     *
     * @param dataModelVarList
     * @param baseParamVarList
     * @param refParamVarList
     * @param paramLocalVars
     * @param dataModelJson
     * @param varTypeEnum
     * @param variableId 变量id/清单id
     * @return List<String>
     */
    private List<String> checkDataModel(List<StrComponentVarPathDto> dataModelVarList, List<StrComponentVarPathDto> baseParamVarList,
                                        List<StrComponentVarPathDto> refParamVarList, JSONObject paramLocalVars, JSONObject dataModelJson,VarTypeEnum varTypeEnum,Long variableId) {
        List<String> list = new ArrayList<>();

        //数据模型
        if (!CollectionUtils.isEmpty(dataModelVarList)) {
            Set<String> usingMappingSet = new HashSet<>();
            if (VarTypeEnum.MAINFLOW == varTypeEnum) {
                usingMappingSet = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                        .select(VarProcessManifestDataModel::getObjectName)
                        .eq(VarProcessManifestDataModel::getManifestId, variableId)).stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toSet());
            }
            for (StrComponentVarPathDto dto : dataModelVarList) {
                String result = diffDataModel(dto.getVarPath(), dto.getVarType(), dto.getIsArray(), dataModelJson,varTypeEnum,usingMappingSet);
                if (!StringUtils.isEmpty(result)) {
                    list.add(result);
                }
            }
        }

        //基础类型参数或本地变量
        if (!CollectionUtils.isEmpty(baseParamVarList)) {
            for (StrComponentVarPathDto dto : baseParamVarList) {
                String result = diffBaseParamData(dto.getVarPath(), dto.getVarType(), dto.getIsParameterArray(), paramLocalVars);
                if (!StringUtils.isEmpty(result)) {
                    list.add(result);
                }

                //本地变量是否初始化
                if (paramLocalVars.containsKey(dto.getVarPath()) && dto.getVarPath().startsWith(PositionVarEnum.LOCAL_VARS.getName())
                    && dto.getActionHistory().startsWith("r")) {
                    JSONObject jsonObject = paramLocalVars.getJSONObject(dto.getVarPath());
                    boolean flag = (!jsonObject.containsKey("initialValue") || StringUtils.isEmpty(jsonObject.getString("initialValue")))
                            && (jsonObject.containsKey("isArray") && !jsonObject.getBoolean("isArray"));
                    if (flag) {
                        result = MessageFormat.format(VariableCompileMessageEnum.VAR_NOT_INIT.getMessage(), jsonObject.getString("name"));
                        list.add(result);
                    }
                }

            }
        }

        //引用类型
        if (!CollectionUtils.isEmpty(refParamVarList)) {
            for (StrComponentVarPathDto dto : refParamVarList) {

                //参数或本地变量为引用对象
                String[] split = dto.getVarPath().split("\\.");
                String paramKey = split[0] + "." + split[1];
                String targetKey = dto.getVarPath().replace(paramKey, dto.getParameterType());

                String result = diffRefParamData(dto.getVarPath(), dto.getVarType(), dto.getIsArray(), targetKey, dto.getParameterType(),
                    dataModelJson);
                if (!StringUtils.isEmpty(result)) {
                    list.add(result);
                }
            }
        }

        return list;

    }

    /**
     * 对比数据模型
     *
     * @param varPath
     * @param varType
     * @param isArr
     * @param dataModelJson
     * @param varTypeEnum
     * @param usingMappingSet 清单编辑页配置的数据模型set
     * @return String
     */
    private String diffDataModel(String varPath, String varType, Integer isArr, JSONObject dataModelJson,VarTypeEnum varTypeEnum,Set<String> usingMappingSet) {
        String result = null;

        String pos = varPath.substring(0, varPath.indexOf("."));
        DomainModelSheetNameEnum messageEnum = DomainModelSheetNameEnum.getMessageEnum(pos);
        String describe = "";
        if (messageEnum != null) {
            describe = messageEnum.getDescribe();
        }
        if (!dataModelJson.containsKey(varPath)) {

            result = MessageFormat.format(VariableCompileMessageEnum.VAR_DEL.getMessage(), describe, varPath);
        } else {
            JSONObject jsonObject = dataModelJson.getJSONObject(varPath);
            String isArray = String.valueOf(isArr);
            if (!isArray.equals(jsonObject.getString(MagicStrings.ISARR)) || !varType.equals(jsonObject.getString(MagicStrings.TYPE))) {

                result = MessageFormat.format(VariableCompileMessageEnum.VAR_ATTR_DISACCORD.getMessage(), describe, varPath);
            }
        }

        if (VarTypeEnum.MAINFLOW == varTypeEnum) {
            String[] parts = varPath.split("\\.");
            if (RAW_DATA.equals(parts[0]) && !usingMappingSet.contains(parts[1])) {
                result = MessageFormat.format(VariableCompileMessageEnum.VAR_NOT_REF.getMessage(), describe, varPath);
            }
        }

        return result;
    }

    /**
     * 对比参数
     *
     * @param varPath
     * @param varType
     * @param isArr
     * @param dataModelJson
     * @return String
     */
    private String diffBaseParamData(String varPath, String varType, Integer isArr, JSONObject dataModelJson) {
        String result = null;

        String describe = "";
        if (varPath.startsWith(PositionVarEnum.PARAMETERS.getName())) {
            describe = PARAMETERS;
        } else {
            describe = LOCAL_VARS;
        }
        if (!dataModelJson.containsKey(varPath)) {

            result = MessageFormat.format(VariableCompileMessageEnum.VAR_DEL.getMessage(), describe, varPath);
        } else {
            JSONObject jsonObject = dataModelJson.getJSONObject(varPath);

            int isJsonArr = jsonObject.getBoolean(MagicStrings.ISARRAY) ? 1 : 0;
            if (isArr != isJsonArr || !varType.equals(jsonObject.getString(MagicStrings.TYPE))) {

                result = MessageFormat.format(VariableCompileMessageEnum.VAR_ATTR_DISACCORD.getMessage(), describe, varPath);
            }
        }

        return result;
    }


    /**
     * 对比引用参数
     * @param varPath
     * @param varType
     * @param isArr
     * @param targetKey
     * @param parameterType
     * @param dataModelJson
     * @return String
     */
    private String diffRefParamData(String varPath, String varType, Integer isArr, String targetKey, String parameterType, JSONObject dataModelJson) {
        String result = null;

        String describe = "";
        if (varPath.startsWith(PositionVarEnum.PARAMETERS.getName())) {
            describe = PARAMETERS;
        } else {
            describe = LOCAL_VARS;
        }
        if (!dataModelJson.containsKey(targetKey)) {

            result = MessageFormat.format(VariableCompileMessageEnum.VAR_REF_DEL.getMessage(), describe, varPath, targetKey);
        } else {
            JSONObject jsonObject = dataModelJson.getJSONObject(targetKey);
            if (targetKey.equals(parameterType)) {

                if (!varType.equals(jsonObject.getString(MagicStrings.TYPE))) {

                    result = MessageFormat.format(VariableCompileMessageEnum.VAR_REF_ATTR_DISACCORD.getMessage(), describe, varPath, targetKey);
                }
            } else {
                String isArray = String.valueOf(isArr);
                if (!isArray.equals(jsonObject.getString(MagicStrings.ISARR)) || !varType.equals(jsonObject.getString(MagicStrings.TYPE))) {

                    result = MessageFormat.format(VariableCompileMessageEnum.VAR_REF_ATTR_DISACCORD.getMessage(), describe, varPath, targetKey);
                }
            }
        }

        return result;
    }

    /**
     * 组件定义的参数、本地变量未使用
     *
     * @param baseParamVarList
     * @param refParamVarList
     * @param paramLocalVars
     * @return List<String>
     */
    private List<String> checkParamIsUse(List<StrComponentVarPathDto> baseParamVarList, List<StrComponentVarPathDto> refParamVarList,
                                         JSONObject paramLocalVars) {
        List<String> list = new ArrayList<>();

        Set<String> useParamSet = new HashSet<>();
        //基础类型参数或本地变量
        if (!CollectionUtils.isEmpty(baseParamVarList)) {
            for (StrComponentVarPathDto dto : baseParamVarList) {
                useParamSet.add(dto.getVarPath());

            }
        }

        //引用类型
        if (!CollectionUtils.isEmpty(refParamVarList)) {
            for (StrComponentVarPathDto dto : refParamVarList) {

                //参数或本地变量为引用对象
                String[] split = dto.getVarPath().split("\\.");
                String paramKey = split[0] + "." + split[1];
                useParamSet.add(paramKey);
            }
        }

        Set<String> keySet = paramLocalVars.keySet();
        for (String key : keySet) {
            if (!useParamSet.contains(key)) {
                String describe = "";
                if (key.startsWith(PositionVarEnum.PARAMETERS.getName())) {
                    describe = PARAMETERS;
                } else {
                    describe = LOCAL_VARS;
                }

                list.add(MessageFormat.format(VariableCompileMessageEnum.VAR_NOT_USE.getMessage(), describe, key));
            }
        }

        return list;
    }

    /**
     * 获取参数
     * @param inputDto 输入实体类对象
     * @return 参数
     */
    public APIResult getParameters(VariableRuleQueryDto inputDto) {
        VarProcessFunction variable = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getContent)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));
        String content = variable.getContent();
        List list = getParametersVars(content);
        return APIResult.success(list);
    }

    /**
     * getParametersVars
     * @param content 内容
     * @return List
     */
    public List getParametersVars(String content) {
        List list = new ArrayList();
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        JSONObject componentJsonDto = JSONObject.parseObject(content);
        if (!componentJsonDto.containsKey(BASE_DATA)) {
            return null;
        }
        JSONObject baseData = componentJsonDto.getJSONObject(BASE_DATA);
        if (!baseData.containsKey(DATA_MODEL)) {
            return null;
        }
        JSONObject dataModel = baseData.getJSONObject(DATA_MODEL);

        JSONArray paramJsonArray = dataModel.getJSONArray("parameters");
        for (int i = 0; i < paramJsonArray.size(); i++) {
            JSONObject paramSubObject = paramJsonArray.getJSONObject(i);
            list.add(paramSubObject);
        }
        return list;
    }

    @Data
    @AllArgsConstructor
    private static class GetRetMapParam {
        private final String content;
        private final VarTypeEnum typeEnum;
        private final VarFunctionSubTypeEnum subTypeEnum;
        private final JSONObject dataModelJson;
        private final List<StrComponentVarPathDto> dataModelVarList;
        private final List<StrComponentVarPathDto> baseParamVarList;
        private final List<StrComponentVarPathDto> refParamVarList;
        private final String returnDataType;
        private final Long variableId;
    }
}
