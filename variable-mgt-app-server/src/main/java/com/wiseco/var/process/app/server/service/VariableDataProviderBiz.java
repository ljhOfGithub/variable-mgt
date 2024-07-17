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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.var.enums.VarFunctionSubTypeEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableClass;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.engine.CompileDataBuildService;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangxianli
 */
@Slf4j
@Service
public class VariableDataProviderBiz {

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Autowired
    private VarProcessFunctionService varProcessFunctionService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessConfigDefaultService varProcessConfigDefaultValueService;

    @Autowired
    private VarProcessConfigExceptionService varProcessConfigExceptionValueService;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VarProcessTemplateBiz varProcessTemplateBiz;

    @Autowired
    private VariableCompileBiz variableCompileBiz;

    @Autowired
    private VariableVarBiz variableVarBiz;

    @Autowired
    private FunctionVarBiz functionVarBiz;

    @Autowired
    private VarProcessVariableClassService varProcessVariableClassService;

    @Autowired
    private VarProcessFunctionClassService varProcessFunctionClassService;

    @Autowired
    private VarProcessFunctionVarService varProcessFunctionVarService;
    @Autowired
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;
    @Resource
    private CompileDataBuildService compileDataBuildService;
    /**
     * varDataProvider
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @param type 变量类型枚举
     * @param identifier 唯一标识符
     * @return VarCompileData对象
     */
    public VarCompileData varDataProvider(Long spaceId, Long manifestId, VarTypeEnum type, String identifier) {
        if (spaceId == null || identifier == null) {
            throw new UnsupportedOperationException("变量空间ID或者identifier为空");
        }
        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        if (space == null) {
            throw new UnsupportedOperationException("未查询到变量空间数据");
        }
        TestVariableTypeEnum testVariableTypeEnum = TestVariableTypeEnum.VAR;
        VarFunctionSubTypeEnum varFunctionSubType = null;
        String name = null; String enName = null;Long variableId = 0L;String content = null;
        String dataType = null;Integer changeNum = 1;String classData = null;
        VarStatusEnum varStatusEnum = VarStatusEnum.CHECK_IN;
        if (type.equals(VarTypeEnum.VAR)) {
            testVariableTypeEnum = TestVariableTypeEnum.VAR;
            VarProcessVariable variable = varProcessManifestVariableService.getVariableByIdentifier(spaceId, manifestId, identifier);
            if (VariableStatusEnum.EDIT.equals(variable.getStatus())) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            name = variable.getLabel();
            enName = variable.getName();
            variableId = variable.getId();
            content = variable.getContent();
            dataType = variable.getDataType();
            changeNum = variable.getVersion();
            List<VarProcessVariableClass> list = varProcessVariableClassService.list(new QueryWrapper<VarProcessVariableClass>().lambda().select(VarProcessVariableClass::getId, VarProcessVariableClass::getClassData).eq(VarProcessVariableClass::getVariableId, variableId));
            if (!CollectionUtils.isEmpty(list)) {
                classData = list.get(0).getClassData();
            }
        } else if (type.equals(VarTypeEnum.FUNCTION)) {
            testVariableTypeEnum = TestVariableTypeEnum.FUNCTION;
            VarProcessFunction variable = varProcessFunctionService.getOne(new QueryWrapper<VarProcessFunction>().lambda().select(VarProcessFunction::getId, VarProcessFunction::getStatus, VarProcessFunction::getName, VarProcessFunction::getContent, VarProcessFunction::getFunctionDataType, VarProcessFunction::getFunctionType).eq(VarProcessFunction::getVarProcessSpaceId, spaceId).eq(VarProcessFunction::getIdentifier, identifier).eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (variable == null) {
                throw new UnsupportedOperationException("未查询到公共函数数据");
            }
            if (FlowStatusEnum.EDIT == variable.getStatus()) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            name = variable.getName();
            variableId = variable.getId();
            content = variable.getContent();
            varFunctionSubType = getFunctionSubType(variable.getFunctionType());
            dataType = variable.getFunctionDataType();
            List<VarProcessFunctionClass> list = varProcessFunctionClassService.list(new QueryWrapper<VarProcessFunctionClass>().lambda().select(VarProcessFunctionClass::getId, VarProcessFunctionClass::getClassData).eq(VarProcessFunctionClass::getFunctionId, variableId));
            if (!CollectionUtils.isEmpty(list)) {
                classData = list.get(0).getClassData();
            }
        }
        //缺失值
        Map<String, String> normalDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigDefault> defaultValueList = varProcessConfigDefaultValueService.list(
                new QueryWrapper<VarProcessConfigDefault>().lambda().eq(VarProcessConfigDefault::getVarProcessSpaceId, spaceId));
        if (!CollectionUtils.isEmpty(defaultValueList)) {
            for (VarProcessConfigDefault varProcessConfigDefault : defaultValueList) {
                if (StringUtils.isEmpty(varProcessConfigDefault.getDefaultValue())) {
                    continue;
                }
                normalDefaultValueLst.put(varProcessConfigDefault.getDataType(), varProcessConfigDefault.getDefaultValue());
            }
        }
        //异常值
        Map<String, String> exceptionDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list();
        if (!CollectionUtils.isEmpty(exceptionValueList)) {
            exceptionDefaultValueLst = exceptionValueList.stream().collect(Collectors.toMap(VarProcessConfigExcept::getExceptionValueCode, VarProcessConfigExcept::getExceptionValue, (v1, v2) -> v2));
        }
        //class
        if (StringUtils.isEmpty(classData)) {
            classData = saveClassData(type, spaceId, variableId, content);
        }
        VarCompileData varCompileData = VarCompileData.builder().varId(variableId).identifier(identifier).changeNum(changeNum)
                .varStatus(varStatusEnum).name(name).enName(enName).type(type).returnType(dataType).spaceId(spaceId)
                .spaceCode(space.getCode()).content(content).varFunctionSubType(varFunctionSubType).normalDefaultValueLst(normalDefaultValueLst)
                .exceptionDefaultValueLst(exceptionDefaultValueLst).javaCls(classData).build();
        varCompileData.setStrategyPropertyEditorMap(compileDataBuildService.getStrategyPropertyEditorMap(spaceId, space.getInputData(), space.getInternalData(), space.getExternalData()));
        compileDataBuildService.assembleCompileData(testVariableTypeEnum, space.getCode(), variableId, spaceId, content, varCompileData);
        log.info("变量编译入参 variableId:{},compileData:{}", variableId, varCompileData);
        return varCompileData;
    }

    /**
     * 保存类数据
     * @param type 变量类型枚举
     * @param spaceId 变量空间Id
     * @param variableId 变量Id
     * @param content 内容
     * @return 保存类数据的结果
     */
    public String saveClassData(VarTypeEnum type, Long spaceId, Long variableId, String content) {

        String classData = null;
        if (type.equals(VarTypeEnum.VAR)) {

            VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.VAR, spaceId, variableId, content);

            if (!compile.isState()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, compile.getErrorMessageList().get(0));
            }
            classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.VAR, spaceId, variableId, content);

            variableVarBiz.saveVarClass(spaceId, variableId, compile.getCompileResultVo(), classData);

        } else {

            VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.FUNCTION, spaceId, variableId, content);

            if (!compile.isState()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, compile.getErrorMessageList().get(0));
            }
            classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.FUNCTION, spaceId, variableId, content);

            functionVarBiz.saveVarClass(spaceId, variableId, compile.getCompileResultVo(), classData);

        }
        return classData;

    }

    /**
     * 按标识符和更改编号提供数据
     * @param spaceId 变量空间Id
     * @param type 变量类型枚举
     * @param identifier 唯一标识符
     * @param changeNum 改变的数量
     * @return 数据
     */
    public VarCompileData varDataProviderByIdentifierAndChangeNum(Long spaceId, VarTypeEnum type, String identifier, Integer changeNum) {
        if (spaceId == null || identifier == null) {
            throw new UnsupportedOperationException("变量空间ID或者identifier为空");
        }
        VarProcessSpace space = varProcessSpaceService.getById(spaceId);
        if (space == null) {
            throw new UnsupportedOperationException("未查询到变量空间数据");
        }
        VarFunctionSubTypeEnum varFunctionSubType = null;
        String name = null;String enName = null;Long variableId = 0L;String content = null;String dataType = null;
        VarStatusEnum varStatusEnum = VarStatusEnum.CHECK_IN;TestVariableTypeEnum testVariableTypeEnum = TestVariableTypeEnum.FUNCTION;
        if (type.equals(VarTypeEnum.VAR)) {
            if (changeNum == null) {
                throw new UnsupportedOperationException("版本为空");
            }
            VarProcessVariable variable = varProcessVariableService.getOne(
                    new QueryWrapper<VarProcessVariable>().lambda().eq(VarProcessVariable::getVarProcessSpaceId, spaceId).eq(VarProcessVariable::getIdentifier, identifier)
                            .eq(VarProcessVariable::getVersion, changeNum).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (variable == null) {
                throw new UnsupportedOperationException("未查询到变量数据");
            }
            if (VariableStatusEnum.EDIT.equals(variable.getStatus())) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            name = variable.getLabel();enName = variable.getName();variableId = variable.getId();content = variable.getContent();dataType = variable.getDataType();
            testVariableTypeEnum = TestVariableTypeEnum.VAR;
        } else if (type.equals(VarTypeEnum.FUNCTION)) {
            VarProcessFunction function = varProcessFunctionService.getOne(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(VarProcessFunction::getId, VarProcessFunction::getName, VarProcessFunction::getStatus, VarProcessFunction::getContent, VarProcessFunction::getFunctionType, VarProcessFunction::getFunctionDataType)
                            .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                            .eq(VarProcessFunction::getIdentifier, identifier).eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (function == null) {
                throw new UnsupportedOperationException("未查询到公共函数数据");
            }
            if (FlowStatusEnum.EDIT == function.getStatus()) {
                varStatusEnum = VarStatusEnum.CHECK_OUT;
            }
            name = function.getName();variableId = function.getId();content = function.getContent();varFunctionSubType = getFunctionSubType(function.getFunctionType());
            dataType = function.getFunctionDataType();testVariableTypeEnum = TestVariableTypeEnum.FUNCTION;
        }
        //缺失值
        Map<String, String> normalDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigDefault> defaultValueList = varProcessConfigDefaultValueService.list(
                new QueryWrapper<VarProcessConfigDefault>().lambda()
                        .eq(VarProcessConfigDefault::getVarProcessSpaceId, spaceId)
        );
        if (!CollectionUtils.isEmpty(defaultValueList)) {
            for (VarProcessConfigDefault varProcessConfigDefault : defaultValueList) {
                if (StringUtils.isEmpty(varProcessConfigDefault.getDefaultValue())) {
                    continue;
                }
                normalDefaultValueLst.put(varProcessConfigDefault.getDataType(), varProcessConfigDefault.getDefaultValue());
            }
        }
        //异常值
        Map<String, String> exceptionDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list();
        if (!CollectionUtils.isEmpty(exceptionValueList)) {
            exceptionDefaultValueLst = exceptionValueList.stream().collect(Collectors.toMap(VarProcessConfigExcept::getExceptionValueCode, VarProcessConfigExcept::getExceptionValue, (v1, v2) -> v2));
        }
        VarCompileData varCompileData = VarCompileData.builder().varId(variableId).identifier(identifier).changeNum(changeNum).varStatus(varStatusEnum).name(name)
                .enName(enName).type(type).spaceId(spaceId).spaceCode(space.getCode()).content(content).varFunctionSubType(varFunctionSubType).returnType(dataType)
                .normalDefaultValueLst(normalDefaultValueLst).exceptionDefaultValueLst(exceptionDefaultValueLst).build();
        varCompileData.setStrategyPropertyEditorMap(compileDataBuildService.getStrategyPropertyEditorMap(spaceId, space.getInputData(), space.getInternalData(), space.getExternalData()));
        compileDataBuildService.assembleCompileData(testVariableTypeEnum, space.getCode(), variableId, spaceId, content, varCompileData);
        log.info("变量编译入参 variableId:{},compileData:{}", variableId, varCompileData);
        return varCompileData;
    }

    private VarFunctionSubTypeEnum getFunctionSubType(FunctionTypeEnum typeEnum) {
        if (typeEnum == null) {
            return null;
        }
        switch (typeEnum) {
            case TEMPLATE:
                return VarFunctionSubTypeEnum.VAR_TEMPLATE;
            case FUNCTION:
                return VarFunctionSubTypeEnum.PUBLIC_METHOD;
            case PREP:
                return VarFunctionSubTypeEnum.PRE_PROCESS;
            default:
                return VarFunctionSubTypeEnum.PUBLIC_METHOD;
        }
    }

    /**
     * 获取清单数据
     * @param manifestId 变量清单Id
     * @return 清单数据
     */
    public VarCompileData getManifestFlowData(Long manifestId) {
        VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getVarProcessSpaceId, VarProcessManifest::getVersion, VarProcessManifest::getContent)
                .eq(VarProcessManifest::getId, manifestId));
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(manifestEntity.getVarProcessSpaceId());
        Set<VarCompileData> varContents = new HashSet<>();
        //变量清单
        List<VarProcessVariableDto> variableList = varProcessManifestVariableService.getVariableListByManifestId(manifestEntity.getVarProcessSpaceId(), manifestId);
        for (VarProcessVariableDto variable : variableList) {
            VarCompileData varCompileData = VarCompileData.builder().varId(variable.getId()).identifier(variable.getIdentifier()).changeNum(variable.getVersion())
                    .varStatus(VarStatusEnum.CHECK_IN).name(variable.getLabel()).enName(variable.getName()).type(VarTypeEnum.VAR).spaceId(varProcessSpace.getId())
                    .spaceCode(varProcessSpace.getCode()).returnType(variable.getDataType()).content(variable.getContent()).build();
            if (StringUtils.isEmpty(variable.getClassData())) {
                String classData = saveClassData(VarTypeEnum.VAR, varProcessSpace.getId(), variable.getId(), variable.getContent());
                varCompileData.setJavaCls(classData);
            } else {
                varCompileData.setJavaCls(variable.getClassData());
            }
            varContents.add(varCompileData);
        }
        //公共函数
        List<VarProcessFunctionDto> functionList = varProcessFunctionService.getFunctionListBySpaceId(manifestEntity.getVarProcessSpaceId());
        if (!CollectionUtils.isEmpty(functionList)) {
            for (VarProcessFunctionDto function : functionList) {
                VarFunctionSubTypeEnum varFunctionSubType = null;
                if (function.getFunctionType() == FunctionTypeEnum.TEMPLATE) {
                    varFunctionSubType = VarFunctionSubTypeEnum.VAR_TEMPLATE;
                } else if (function.getFunctionType() == FunctionTypeEnum.FUNCTION) {
                    varFunctionSubType = VarFunctionSubTypeEnum.PUBLIC_METHOD;
                } else if (function.getFunctionType() == FunctionTypeEnum.PREP) {
                    varFunctionSubType = VarFunctionSubTypeEnum.PRE_PROCESS;
                }
                VarCompileData varCompileData = VarCompileData.builder().varId(function.getId()).identifier(function.getIdentifier()).changeNum(1)
                        .varStatus(VarStatusEnum.CHECK_IN).name(function.getName()).type(VarTypeEnum.FUNCTION).varFunctionSubType(varFunctionSubType)
                        .spaceId(varProcessSpace.getId()).returnType(function.getFunctionDataType()).spaceCode(varProcessSpace.getCode()).content(function.getContent()).build();
                if (StringUtils.isEmpty(function.getClassData())) {
                    String classData = saveClassData(VarTypeEnum.FUNCTION, varProcessSpace.getId(), function.getId(), function.getContent());
                    varCompileData.setJavaCls(classData);
                } else {
                    varCompileData.setJavaCls(function.getClassData());
                }
                varContents.add(varCompileData);
            }
        }
        //缺失值
        Map<String, String> normalDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigDefault> defaultValueList = varProcessConfigDefaultValueService.list(
                new QueryWrapper<VarProcessConfigDefault>().lambda()
                        .eq(VarProcessConfigDefault::getVarProcessSpaceId, varProcessSpace.getId())

        );
        if (!CollectionUtils.isEmpty(defaultValueList)) {
            for (VarProcessConfigDefault varProcessConfigDefault : defaultValueList) {
                if (StringUtils.isEmpty(varProcessConfigDefault.getDefaultValue())) {
                    continue;
                }
                normalDefaultValueLst.put(varProcessConfigDefault.getDataType(), varProcessConfigDefault.getDefaultValue());
            }
        }

        //异常值
        Map<String, String> exceptionDefaultValueLst = new HashMap<>(MagicNumbers.EIGHT);
        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list();
        if (!CollectionUtils.isEmpty(exceptionValueList)) {
            exceptionDefaultValueLst = exceptionValueList.stream().collect(Collectors.toMap(VarProcessConfigExcept::getExceptionValueCode, VarProcessConfigExcept::getExceptionValue, (v1, v2) -> v2));

        }
        VarCompileData varCompileData = VarCompileData.builder().varId(manifestId).identifier(String.valueOf(manifestId)).changeNum(manifestEntity.getVersion()).type(VarTypeEnum.MAINFLOW)
                .spaceId(varProcessSpace.getId()).spaceCode(varProcessSpace.getCode()).content(manifestEntity.getContent()).normalDefaultValueLst(normalDefaultValueLst)
                .exceptionDefaultValueLst(exceptionDefaultValueLst).varContents(varContents).build();
        varCompileData.setStrategyPropertyEditorMap(compileDataBuildService.getStrategyPropertyEditorMap(varCompileData.getSpaceId(), varProcessSpace.getInputData(), varProcessSpace.getInternalData(), varProcessSpace.getExternalData()));
        compileDataBuildService.assembleCompileData(TestVariableTypeEnum.MANIFEST, varProcessSpace.getCode(), manifestId, varProcessSpace.getId(), null, varCompileData);
        return varCompileData;
    }

    /**
     * 获取模板配置
     * @param spaceId 变量空间Id
     * @param type 变量类型枚举
     * @return 模板配置的map集合
     */
    public Map<String, JSONObject> getTemplateConfig(Long spaceId, VarTypeEnum type) {
        VarTemplateTypeEnum varTemplateTypeEnum = null;
        if (VarTypeEnum.VAR == type) {
            varTemplateTypeEnum = VarTemplateTypeEnum.VAR_PROCESS;
        } else if (VarTypeEnum.FUNCTION == type) {
            varTemplateTypeEnum = VarTemplateTypeEnum.COMMON_FUNCTION;
        } else {
            varTemplateTypeEnum = VarTemplateTypeEnum.SERVICE_INTERFACE;
        }

        JSONObject templateConfig = varProcessTemplateBiz.fillStaticTemplateAndFunction(spaceId, varTemplateTypeEnum, null);

        return JSONObject.parseObject(templateConfig.toJSONString(), new TypeReference<Map<String, JSONObject>>() {
        });

    }

    /**
     * 获取有效的变量行为历史数据
     * @param spaceId 变量空间Id
     * @param type 变量类型枚举
     * @param identifier 唯一标识符
     * @return 有效的变量行为历史数据
     */
    public Map<String, VarActionHistory> getValidVarActionHistory(Long spaceId, VarTypeEnum type, String identifier) {

        Map<String, VarActionHistory> result = new HashMap<>(MagicNumbers.EIGHT);

        if (type.equals(VarTypeEnum.FUNCTION)) {
            // 通过identifier查找到对应function
            VarProcessFunction function = varProcessFunctionService.getOne(
                    new QueryWrapper<VarProcessFunction>()
                            .lambda()
                            .select(VarProcessFunction::getId)
                            .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                            .eq(VarProcessFunction::getIdentifier, identifier)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (function == null) {
                throw new UnsupportedOperationException("未查询到公共函数数据");
            }
            // 查询已启用的function其已有的模型对象引用信息
            List<VarProcessFunctionVar> functionVarList = varProcessFunctionVarService.list(
                    new QueryWrapper<VarProcessFunctionVar>().lambda()
                            .eq(VarProcessFunctionVar::getFunctionId, function.getId())
                            .likeRight(VarProcessFunctionVar::getVarPath, "rawData")
            );

            // 构建结果
            for (VarProcessFunctionVar functionVar : functionVarList) {
                VarActionHistory action = VarActionHistory.builder()
                        .actionHistory(functionVar.getActionHistory())
                        .varType(functionVar.getVarType())
                        .label(functionVar.getVarName())
                        .isArr(String.valueOf(functionVar.getIsArray()))
                        .isExtend(functionVar.getIsExtend() == null ? "0" : String.valueOf(functionVar.getIsExtend()))
                        .isDirect(false)
                        .build();
                result.put(functionVar.getVarPath(), action);
            }

        }
        log.info("变量以词条方式解析，获取下游引用对象关系：{}", result);
        return result;
    }

    /**
     * 获取标识符集
     * @param spaceId 变量空间Id
     * @param type 变量类型枚举
     * @param identifier 唯一标识符
     * @return 标识符集
     */
    public Set<String> getIdentifierSet(Long spaceId, VarTypeEnum type, String identifier) {

        Set<String> result = new HashSet<>();

        if (type.equals(VarTypeEnum.FUNCTION)) {
            // 通过identifier查找到对应function
            VarProcessFunction function = varProcessFunctionService.getOne(
                    new QueryWrapper<VarProcessFunction>()
                            .lambda()
                            .select(VarProcessFunction::getId, VarProcessFunction::getIdentifier)
                            .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                            .eq(VarProcessFunction::getIdentifier, identifier)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (function == null) {
                throw new UnsupportedOperationException("未查询到公共函数数据");
            }
            result.add(function.getIdentifier());

            // 查询引用关系
            List<VarProcessFunctionReference> refList = varProcessFunctionReferenceService.list(new QueryWrapper<VarProcessFunctionReference>().lambda()
                    .eq(VarProcessFunctionReference::getUseByFunctionId, function.getId())
                    .eq(VarProcessFunctionReference::getVarProcessSpaceId, spaceId)
            );

            //
            for (VarProcessFunctionReference ref : refList) {
                VarProcessFunction func = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                        .select(VarProcessFunction::getId, VarProcessFunction::getIdentifier)
                        .eq(VarProcessFunction::getId, ref.getFunctionId()));
                if (func != null) {
                    result.add(func.getIdentifier());
                }
            }

        }
        log.info("变量以词条方式解析，获取下游function引用关系：{}", result);
        return result;
    }
}
