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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableSimpleTypeEnum;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.JsonSchemaFieldEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.commons.util.ObjectUtils;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.EngineFunctionTemplateInputDto;
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
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.DataValuePrefixEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.enums.template.FunctionCommonProviderEnum;
import com.wiseco.var.process.app.server.enums.template.StaticTemplateEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateDataProviderEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateFunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.properties.TemplateProperties;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.converter.CommonTemplateEnumConverter;
import com.wiseco.var.process.app.server.service.converter.DynamicTreeConverter;
import com.wiseco.var.process.app.server.service.converter.TemplateModelConverter;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import com.wiseco.var.process.app.server.service.dto.input.StaticTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.TreeVarBaseArrayInputDto;
import com.wiseco.var.process.app.server.service.dto.json.ComponentJsonDto;
import com.wiseco.var.process.app.server.service.dto.output.DynamicObjectOutputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestSupportBiz;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

/**
 * @author: zhouxiuxiu
 */
@Slf4j
@Service
@RefreshScope
public class VarProcessTemplateBiz {
    private static final List<String> BASE_DATA_TYPE_LIST = Arrays.asList("int", "string", "double", "boolean", "date", "datetime");
    private static final String THIS = "this_";
    private static final String VOID = "void";
    private static final String DATA_PROVIDER_ARRAY = "data_provider_array_";
    private static final String DATA_PROVIDER_LEFTVALUE_ARRAY = "data_provider_leftValue_array_";
    public static final String RAW_DATA = "rawData";
    public static final String FUNCTION_COMMON_NUMBER_TEMPLATE = "function_common_number_template";
    public static final String FUNCTION_COMMON_DATE_TEMPLATE = "function_common_date_template";
    public static final String FUNCTION_COMMON_STRING_TEMPLATE = "function_common_string_template";
    public static final String FUNCTION_COMMON_BOOL_TEMPLATE = "function_common_bool_template";
    @Autowired
    private TemplateProperties templateProperties;
    @Autowired
    private CommonTemplateBiz commonTemplateBiz;
    @Autowired
    private CommonGlobalDataBiz commonGlobalDataBiz;
    @Autowired
    private CommonLocalDataBiz commonLocalDataBiz;
    @Autowired
    private EngineFunctionBiz engineFunctionBiz;
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessConfigExceptionService varProcessConfigExceptionValueService;
    @Autowired
    private DynamicTreeConverter dynamicTreeConverter;
    @Autowired
    private VarProcessDictService varProcessDictService;
    @Autowired
    private CommonTemplateEnumConverter commonTemplateEnumConverter;
    @Autowired
    private VarProcessDictDetailsService varProcessDictDetailsService;
    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VariableManifestSupportBiz variableManifestSupport;
    @Autowired
    private AuthService authService;

    /**
     * 获取模板
     *
     * @param inputDto 输入实体类对象
     * @return 获取模板
     */
    public JSONObject getVarProcessTemplate(VarProcessTemplateInputDto inputDto) {
        Assert.hasText(templateProperties.getVarTemplateStatic(), "读取静态模板信息出错");
        //1.静态模板数据，处理一些需要动态判断的
        JSONObject templateConfig = initTemplateConfig(inputDto.getType());
        //2.准备表达式使用的所有变量信息
        VarTemplateTypeEnum varTemplateTypeEnum = VarTemplateTypeEnum.getCode(inputDto.getType());
        //3.组装内置函数，公共函数
        engineFunctionBiz.fillFunctionProvider(templateConfig, true);
        fillCommonFunctionProvider(templateConfig, inputDto.getSpaceId(), inputDto.getFunctionId(), varTemplateTypeEnum, null, inputDto.isManifestFlow());
        return templateConfig;
    }

    /**
     * 组装provider数据信息
     *
     * @param inputDto 输入实体类对象
     * @return 获取模板
     */
    public JSONObject getVariablesByProviderNames(TemplateStaticGetProviderInputDto inputDto) {
        //2.准备表达式使用的所有变量信息
        VarTemplateTypeEnum varTemplateTypeEnum = VarTemplateTypeEnum.getCode(inputDto.getType());
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(varTemplateTypeEnum, inputDto.getVariableId(), inputDto.getFunctionId());
        //查询表达式存储内容
        String ruleContent = commonLocalDataBiz.getContent(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), inputDto.getSessionId());
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(ruleContent, ComponentJsonDto.class);
        Map<String, DomainDataModelTreeDto> paramLocalTreeDto = commonLocalDataBiz.fillParamLocalTreeDto(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), componentJsonDto);
        DomainDataModelTreeDto paramOutTreeDto = commonLocalDataBiz.fillParamInOrOutTreeDto(TemplateUnitTypeEnum.STRATEGY_COMPONENT, inputDto.getSpaceId(), pair.getValue(), componentJsonDto, PositionVarEnum.PARAMETERS_OUT);
        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemaList = commonTemplateBiz.findAllVarTreeDto(pair.getKey(), inputDto.getSpaceId(),
                Pair.of(TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()), inputDto.getFunctionId()));
        if (!CollectionUtils.isEmpty(paramLocalTreeDto)) {
            jsonSchemaList.putAll(paramLocalTreeDto);
        }
        VarProcessTemplateInputDto staticInputDto = ObjectUtils.clone(inputDto, VarProcessTemplateInputDto.class);
        //如果为清单流程 可选范围只有基本信息中已经绑定的数据模型和变量
        List<VarProcessVariable> varList = filtMappings(staticInputDto, inputDto.isManifestFlow(), jsonSchemaList);
        //组装return返回信息
        DomainDataModelTreeDto functionRetDto = fillReturnModelTreeDto(staticInputDto);
        JSONObject templateConfig = new JSONObject();
        //2.动态拼装所有provider 信息
        inputDto.getProviderNames().forEach(providerName -> {
            TemplateDataProviderEnum templateDataProviderEnum = TemplateDataProviderEnum.fromName(providerName);
            if (templateDataProviderEnum == null) {
                return;
            }
            fillTemplate(new TemplateParam(staticInputDto, templateConfig, templateDataProviderEnum, jsonSchemaList, paramOutTreeDto, functionRetDto, varList, pair));
        });
        return templateConfig;
    }

    /**
     * 获取模板内容
     *
     * @param inputDto 请求信息
     * @return 模板信息
     */
    public JSONObject getTemplateByTmpName(TemplateStaticGetTemplateInputDto inputDto) {
        VarProcessFunction function = varProcessFunctionService.getOne(
                new QueryWrapper<VarProcessFunction>()
                        .lambda()
                        .select(VarProcessFunction::getFunctionTemplateContent)
                        .eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessFunction::getIdentifier, inputDto.getIdentifier())
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        JSONObject baseObj = new JSONObject();
        baseObj.put(inputDto.getTemplateName(), JSON.parseObject(function.getFunctionTemplateContent()));
        return baseObj;
    }

    private List<VarProcessVariable> filtMappings(VarProcessTemplateInputDto inputDto, Boolean manifestFlow, Map<String, DomainDataModelTreeDto> jsonSchemaList) {
        if (manifestFlow && inputDto.getManifestId() != null) {
            //筛选出可选(指标清单使用)的数据模型&变量
            VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
            List<VarProcessManifestDataModel> dataModelMappingList = manifestDto.getDataModelMappingList();
            Set<String> objectNameSet = dataModelMappingList.stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toSet());
            filterModelObjectJson(jsonSchemaList, objectNameSet);
            List<Long> variableIds = manifestDto.getVariablePublishList().stream().map(VarProcessManifestVariable::getVariableId).collect(Collectors.toList());
            List<VarProcessVariable> varList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(variableIds)) {
                varList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery().in(VarProcessVariable::getId, variableIds));
            }
            return varList;
        } else {
            //获取已上架且有权限的所有数据模型&变量
            RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
            if (!ALL_PERMISSION.equals(roleDataAuthority.getType())) {
                Set<String> objectNameSet = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                                .select(VarProcessDataModel::getObjectName)
                                .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessDataModel::getCreatedDept, roleDataAuthority.getDeptCodes())
                                .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessDataModel::getCreatedUser, roleDataAuthority.getUserNames()))
                        .stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toSet());
                filterModelObjectJson(jsonSchemaList, objectNameSet);
            }
            return fillVariableList(inputDto.getSpaceId(), roleDataAuthority);
        }
    }

    private static void filterModelObjectJson(Map<String, DomainDataModelTreeDto> jsonSchemaList, Set<String> objectNameSet) {
        DomainDataModelTreeDto rawData = jsonSchemaList.get(RAW_DATA);
        List<DomainDataModelTreeDto> children = rawData.getChildren();
        List<DomainDataModelTreeDto> newChildren = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : children) {
            String path = treeDto.getValue();
            // 使用 split 方法将字符串按照 "." 分割成数组
            String[] parts = path.split("\\.");
            if ("rawData".equals(parts[0]) && objectNameSet.contains(parts[1])) {
                newChildren.add(treeDto);
            }
        }
        rawData.setChildren(newChildren);
    }

    /**
     * 扩展数据
     *
     * @param inputDto 输入实体类对象
     * @return 赋值树
     */
    public List<DomainDataModelTreeDto> appendedProviderData(VarAppendedProviderInputDto inputDto) {
        TemplateDataProviderEnum dataProvider = TemplateDataProviderEnum.fromName(inputDto.getProviderName());
        if (dataProvider == null) {
            return null;
        }
        VarProcessTemplateInputDto varProcessTemplateInputDto = new VarProcessTemplateInputDto();
        BeanUtils.copyProperties(inputDto, varProcessTemplateInputDto);
        //1.准备需要使用的数据变量
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        //查询表达式存储内容
        String ruleContent = commonLocalDataBiz.getContent(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), inputDto.getSessionId());
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(ruleContent, ComponentJsonDto.class);
        Map<String, DomainDataModelTreeDto> paramLocalTreeDto = commonLocalDataBiz.fillParamLocalTreeDto(pair.getKey(), inputDto.getSpaceId(),
                pair.getValue(), componentJsonDto);
        DomainDataModelTreeDto paramOutTreeDto = commonLocalDataBiz.fillParamInOrOutTreeDto(TemplateUnitTypeEnum.STRATEGY_COMPONENT, inputDto.getSpaceId(), pair.getValue(), componentJsonDto, PositionVarEnum.PARAMETERS_OUT);
        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemaList = commonTemplateBiz.findAllVarTreeDto(pair.getKey(), inputDto.getSpaceId(),
                Pair.of(TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()), inputDto.getFunctionId()));
        if (!CollectionUtils.isEmpty(paramLocalTreeDto)) {
            jsonSchemaList.putAll(paramLocalTreeDto);
        }
        //组装return返回信息
        DomainDataModelTreeDto functionRetDto = fillReturnModelTreeDto(varProcessTemplateInputDto);
        //获取已上架的变量清单
        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        List<VarProcessVariable> varList = fillVariableList(inputDto.getSpaceId(), roleDataAuthorityDTO);
        //4.组装当前provider的其他变量
        List<DomainDataModelTreeDto> mergeResult = this.mergeData(
                new MergeDataParam(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), dataProvider, inputDto.getSessionId(), jsonSchemaList, paramOutTreeDto, TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()), varList, VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getManifestId()));

        List<DomainDataModelTreeDto> result = new ArrayList<>();
        if (functionRetDto != null && dataProvider.getProviderName().equals(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())) {
            result.add(functionRetDto);
        }
        //添加数据之前先排序
        for (DomainDataModelTreeDto domainDataModelTreeDto : mergeResult) {
            Collections.sort(domainDataModelTreeDto.getChildren(), Comparator.comparing(o -> o.getName().toUpperCase()));
        }
        if (!CollectionUtils.isEmpty(mergeResult)) {
            result.addAll(mergeResult);
        }
        // 左值只有预处理能选到this 右值都能选到
        boolean appendThis = !inputDto.getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())
                || VarTemplateTypeEnum.COMMON_FUNCTION.getCode().equals(inputDto.getType()) && TemplateFunctionTypeEnum.PREP.getCode().equals(inputDto.getFunctionSubType());
        if (appendThis) {
            DomainDataModelTreeDto other = DomainDataModelTreeDto.builder().name("other").value("other").describe("其它变量").isArr("0").isEmpty("0")
                    .type("object").children(new ArrayList<DomainDataModelTreeDto>()).build();
            //2.对循环内的this_obj.abc 拼出原始input.obj.abc
            List<Pair<String, String>> dataValues = commonTemplateBiz.originalFullName(inputDto.getDataValues());
            //3.追加循环value的属性this
            for (Pair<String, String> dataValue : dataValues) {
                List<DataVariableTypeEnum> dataTypeLst = commonTemplateEnumConverter.getDataTypeByProvider(dataProvider);
                List<TemplateVarLocationEnum> parameterAndLocalVarLocationLst = commonTemplateEnumConverter.getParameterAndVarsLocation(dataProvider);
                fillAppendProviderObjData(
                        new AppendProviderObjData(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), dataValue, other, dataProvider, dataTypeLst, parameterAndLocalVarLocationLst, inputDto.getSessionId(), jsonSchemaList, paramOutTreeDto, componentJsonDto, inputDto.getFunctionSubType(), VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getManifestId()));
            }
            //other 为空的话不显示
            if (!other.getChildren().isEmpty()) {
                result.add(other);
            }
        }
        return result;
    }

    /**
     * 预处理模板数组循环追加信息，需要处理扩展数据
     *
     * @param appendProviderObjData
     */
    private void fillAppendProviderObjData(AppendProviderObjData appendProviderObjData) {
        //预处理公共函数raw 只需要可扩展数据
        Map<String, DomainDataModelTreeDto> newJsonSchemalList = handlePreRawData(new HandlePreRawDataParam(appendProviderObjData.getJsonSchemaList(), appendProviderObjData.getParamOutTreeDto(),  appendProviderObjData.getDataProvider(),
                TemplateFunctionTypeEnum.getTypeEnum(appendProviderObjData.getFunctionTypeEnum()), appendProviderObjData.getLocalId(), appendProviderObjData.getDataValuePair().getKey(), appendProviderObjData.getVarTemplateTyp(), appendProviderObjData.getManifestId()));
        commonTemplateBiz.appendProviderObjData(
                new CommonTemplateBiz.ProviderObjData(appendProviderObjData.getTempType(), appendProviderObjData.getSpaceId(), appendProviderObjData.getLocalId(), appendProviderObjData.getDataValuePair(), appendProviderObjData.getOther(), appendProviderObjData.getDataProvider(), appendProviderObjData.getDataTypeLst(), appendProviderObjData.getParameterAndLocalVarLocationLst(), appendProviderObjData.getSessionId(), newJsonSchemalList, appendProviderObjData.getRuleContent()));
    }

    /**
     * 赋值树this对象
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体的list
     */
    public List<DomainDataModelTreeDto> appendedThisProviderData(VarTemplateDynamicInputDto inputDto) {
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        //处理value带this情况
        String tempDataValue = inputDto.getDataValue();
        String fullPathValue = org.apache.commons.lang3.StringUtils.isEmpty(inputDto.getFullPathValue()) ? "" : inputDto.getFullPathValue();
        String dataValue = inputDto.getDataValue();
        if (dataValue.startsWith(THIS)) {
            tempDataValue = fullPathValue;
        }
        if (StringUtils.isEmpty(tempDataValue)) {
            return null;
        }
        dataValue = tempDataValue;
        //本地变量查询
        if (dataValue.toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase())
                || dataValue.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())) {
            tempDataValue = commonLocalDataBiz.getProviderTypeFullPath(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), dataValue,
                    inputDto.getSessionId());
        }
        if (StringUtils.isEmpty(tempDataValue)) {
            return null;
        }
        DomainDataModelTreeDto domainDataModelTreeDto = commonGlobalDataBiz.findProperty(pair.getKey(), inputDto.getSpaceId(), tempDataValue);
        if (Objects.isNull(domainDataModelTreeDto)) {
            return null;
        }
        return removeObjectAndArray(getChildTreeDtos(domainDataModelTreeDto));
    }

    private List<DomainDataModelTreeDto> getChildTreeDtos(DomainDataModelTreeDto parentTreeDto) {
        if (Objects.isNull(parentTreeDto)) {
            return null;
        }
        List<DomainDataModelTreeDto> childrens = parentTreeDto.getChildren();
        if (!CollectionUtils.isEmpty(childrens)) {
            return childrens;
        }
        if (!BASE_DATA_TYPE_LIST.contains(parentTreeDto.getType())) {
            //非基本类型则返回空
            return null;
        }
        //基本类型的开始构造this_返回
        return Collections.singletonList(buildThisModelTreeDto(parentTreeDto));
    }

    private DomainDataModelTreeDto buildThisModelTreeDto(DomainDataModelTreeDto parentTreeDto) {
        DomainDataModelTreeDto modelTreeDto = DomainDataModelTreeDto.builder()
                .isArr("0")
                .describe(parentTreeDto.getDescribe())
                .isEmpty("0")
                .isExtend("0")
                .isRefRootNode("0")
                .label("this")
                .label("this")
                .name("this_" + parentTreeDto.getName())
                .value("this_")
                .build();
        modelTreeDto.setType(parentTreeDto.getType());
        modelTreeDto.setTypeRef(parentTreeDto.getType());
        return modelTreeDto;
    }

    /**
     * 移除数组和对象
     *
     * @param treeDtoList
     * @return DomainDataModelTreeDto List
     */
    private List<DomainDataModelTreeDto> removeObjectAndArray(List<DomainDataModelTreeDto> treeDtoList) {
        if (CollectionUtils.isEmpty(treeDtoList)) {
            return null;
        }
        Iterator it = treeDtoList.iterator();
        while (it.hasNext()) {
            DomainDataModelTreeDto dto = (DomainDataModelTreeDto) it.next();
            if (!"this_".equals(dto.getValue())) {
                dto.setValue(dto.getName());
            }
            if ("1".equals(dto.getIsArr()) || "object".equals(dto.getType())) {
                it.remove(); //移除该对象
            }
        }
        return treeDtoList;
    }

    /**
     * 空间变量，公共函数转模板类型，1组件，2变量，3公共函数
     *
     * @param varTemplateType
     * @param variableId
     * @param functionId
     * @return Pair<TemplateUnitTypeEnum, Long>
     */
    private Pair<TemplateUnitTypeEnum, Long> getTemplateUnitType(VarTemplateTypeEnum varTemplateType, Long variableId, Long functionId) {
        if (varTemplateType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "入参type不合法！");
        }
        TemplateUnitTypeEnum templateUnitTypeEnum = TemplateUnitTypeEnum.SPACE_VARIABLE;
        Long localId = null;
        if (VarTemplateTypeEnum.VAR_PROCESS == varTemplateType) {
            templateUnitTypeEnum = TemplateUnitTypeEnum.SPACE_VARIABLE;
            localId = variableId;
        } else if (VarTemplateTypeEnum.COMMON_FUNCTION == varTemplateType) {
            templateUnitTypeEnum = TemplateUnitTypeEnum.SPACE_COMMON_FUNCTION;
            localId = functionId;
        } else {
            log.info("当前类型不属于变量和公共函数！");
        }
        return Pair.of(templateUnitTypeEnum, localId);
    }

    /**
     * 获取基本属性
     *
     * @param inputDto 输入实体类对象
     * @return JSONObject对象
     */
    public JSONObject getVarBasicProperty(VarTemplateDynamicInputDto inputDto) {
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        return commonTemplateBiz.getVarBasicProperty(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), inputDto.getDataValue(),
                inputDto.getFullPathValue(), inputDto.getSessionId());
    }

    /**
     * 动态查询对象
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体
     */
    public DynamicObjectOutputDto queryObjectDynamic(VarTemplateDynamicInputDto inputDto) {
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        return commonTemplateBiz.queryObjectDynamic(
                new CommonTemplateBiz.ObjectDynamic(pair.getKey(), TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()), inputDto.getSpaceId(), pair.getValue(), inputDto.getDataValue(), inputDto.getFullPathValue(), inputDto.getLoopDataValues(), inputDto.getSessionId()));
    }

    /**
     * 动态查询对象数组
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体
     */
    public DynamicObjectOutputDto queryObjectArrayDynamic(VarTemplateDynamicInputDto inputDto) {
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        return commonTemplateBiz.queryObjectArrayDynamic(
                new CommonTemplateBiz.ObjectDynamic(pair.getKey(), TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()), inputDto.getSpaceId(), pair.getValue(), inputDto.getDataValue(), inputDto.getFullPathValue(), inputDto.getLoopDataValues(), inputDto.getSessionId()));
    }

    /**
     * 比较对象的属性
     *
     * @param inputDto 输入实体类对象
     */
    public void compareObjectProperty(VarTemplateComProInputDto inputDto) {
        Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(),
                inputDto.getFunctionId());
        commonTemplateBiz.compareObjectProperty(
                new CommonTemplateBiz.CompareObjectProperty(pair.getKey(), inputDto.getSpaceId(), pair.getValue(), inputDto.getDataValueA(), inputDto.getDataValueB(), inputDto.getFullPathValueA(), inputDto.getFullPathValueB(), inputDto.getSessionId(), inputDto.getOutsideServiceId()));
    }

    /**
     * 获取数据的变量列表
     *
     * @param inputDto 输入实体类对象
     * @return 变量列表
     */
    public List<Content> getDataVariableList(VarDataVariableListInputDto inputDto) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        //数据模型
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "空间信息不存在不存在！");
        }
        List<String> typeList = new ArrayList();
        DataVariableSimpleTypeEnum[] values = DataVariableSimpleTypeEnum.values();
        for (DataVariableSimpleTypeEnum value : values) {
            typeList.add(value.getMessage());
        }
        if (!StringUtils.isEmpty(varProcessSpace.getInputData())) {
            DomainModelTree domainModelTreeExternalDataVars = getDomainModelTree(varProcessSpace.getInputData());

            //权限筛选
            RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
            Set<String> objectNameSet = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                            .select(VarProcessDataModel::getObjectName)
                            .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessDataModel::getCreatedDept, roleDataAuthority.getDeptCodes())
                            .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessDataModel::getCreatedUser, roleDataAuthority.getUserNames()))
                    .stream()
                    .map(VarProcessDataModel::getObjectName)
                    .collect(Collectors.toSet());

            List<DomainModelTree> newChildren = domainModelTreeExternalDataVars.getChildren().stream()
                    .filter(treeDto -> {
                        String path = treeDto.getValue();
                        // 使用 split 方法将字符串按照 "." 分割成数组
                        String[] parts = path.split("\\.");
                        return "rawData".equals(parts[0]) && objectNameSet.contains(parts[1]);
                    })
                    .collect(Collectors.toList());

            domainModelTreeExternalDataVars.setChildren(newChildren);

            filterRoot(typeList, DomainModelTreeUtils.findDomainModeTreeObjectList(domainModelTreeExternalDataVars),
                    TemplateVarLocationEnum.RAW_DATA.getDisplayName());
        }
        List<Content> contentList = new ArrayList<>();
        for (String value : typeList) {
            Content content = Content.builder().label(value).value(value).build();
            contentList.add(content);
        }
        return contentList;
    }

    /**
     * 组装公共函数
     * @param baseObj 基本对象
     * @param spaceId 变量空间Id
     * @param functionId 公共函数Id
     * @param varTemplateTypeEnum 模板类型枚举
     * @param list 查询的函数集合
     * @param manifestFlow 是否变量清单流程
     */
    public void fillCommonFunctionProvider(JSONObject baseObj, Long spaceId, Long functionId, VarTemplateTypeEnum varTemplateTypeEnum, List<VarProcessFunction> list, boolean manifestFlow) {
        if (list == null || list.isEmpty()) {
            LambdaQueryWrapper<VarProcessFunction> queryWrapper = new QueryWrapper<VarProcessFunction>().lambda()
                    .select(
                            VarProcessFunction::getId,
                            VarProcessFunction::getVarProcessSpaceId,
                            VarProcessFunction::getName,
                            VarProcessFunction::getFunctionType,
                            VarProcessFunction::getIdentifier,
                            VarProcessFunction::getFunctionDataType,
                            VarProcessFunction::getFunctionTemplateContent,
                            VarProcessFunction::getPrepObjectName,
                            VarProcessFunction::getStatus
                    )
                    .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                    .eq(VarProcessFunction::getStatus, FlowStatusEnum.UP)
                    .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
            RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
            LambdaQueryWrapper<VarProcessFunction> templatePrepQueryWrapper = queryWrapper.clone();
            templatePrepQueryWrapper.ne(VarProcessFunction::getFunctionType,FunctionTypeEnum.FUNCTION)
                    .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()),
                            VarProcessFunction::getCreatedDeptCode, roleDataAuthority.getDeptCodes())
                    .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getUserNames()),
                            VarProcessFunction::getCreatedUser, roleDataAuthority.getUserNames());
            list = varProcessFunctionService.list(templatePrepQueryWrapper);
            //公共方法不筛选权限
            LambdaQueryWrapper<VarProcessFunction> functionQueryWrapper = queryWrapper.clone();
            list.addAll(varProcessFunctionService.list(functionQueryWrapper.eq(VarProcessFunction::getFunctionType,FunctionTypeEnum.FUNCTION)));
        }
        if (varTemplateTypeEnum == null || CollectionUtils.isEmpty(list)) {
            return;
        }
        for (FunctionCommonProviderEnum functionCommonProviderEnum : FunctionCommonProviderEnum.values()) {
            getCommonFuncProvicer(baseObj, list, functionId, varTemplateTypeEnum, functionCommonProviderEnum,manifestFlow);
        }
        if (manifestFlow) {
            for (VarProcessFunction varProcessFunction : list) {
                String functionTemplateName = "function_custom_" + varProcessFunction.getIdentifier() + "_template";
                baseObj.put(functionTemplateName, JSON.parseObject(varProcessFunction.getFunctionTemplateContent()));
            }
        }
    }

    /**
     * fillStaticTemplateAndFunction
     *
     * @param spaceId             变量空间Id
     * @param varTemplateTypeEnum 模板枚举
     * @param localId             局部Id
     * @return JSONObject对象
     */
    public JSONObject fillStaticTemplateAndFunction(Long spaceId, VarTemplateTypeEnum varTemplateTypeEnum, Long localId) {
        //1.静态模板数据，处理一些需要动态判断的
        if (varTemplateTypeEnum == null) {
            varTemplateTypeEnum = VarTemplateTypeEnum.SERVICE_INTERFACE;
        }
        JSONObject templateConfig = initTemplateConfig(varTemplateTypeEnum.getCode());
        engineFunctionBiz.fillFunctionProvider(templateConfig);
        List<VarProcessFunction> list = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda()
                .select(
                        VarProcessFunction::getId,
                        VarProcessFunction::getVarProcessSpaceId,
                        VarProcessFunction::getName,
                        VarProcessFunction::getFunctionType,
                        VarProcessFunction::getIdentifier,
                        VarProcessFunction::getFunctionDataType,
                        VarProcessFunction::getFunctionTemplateContent,
                        VarProcessFunction::getPrepObjectName,
                        VarProcessFunction::getStatus
                )
                .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                .eq(VarProcessFunction::getStatus, FlowStatusEnum.UP)
                .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        fillCommonFunctionProvider(templateConfig, spaceId, localId, varTemplateTypeEnum, list,false);
        for (VarProcessFunction varProcessFunction : list) {
            String functionTemplateName = "function_custom_" + varProcessFunction.getIdentifier() + "_template";
            templateConfig.put(functionTemplateName, JSON.parseObject(varProcessFunction.getFunctionTemplateContent()));
        }
        return templateConfig;
    }

    /**
     * 获取数据提供者的列表
     *
     * @param inputDto 输入实体类对象
     * @return 数据提供者的列表
     */
    public List<DomainDataModelTreeDto> getDataProviderList(VarDataProviderListInputDto inputDto) {
        Assert.hasText(templateProperties.getVarTemplateStatic(), "读取静态模板信息出错");
        if (inputDto.getProviderName().startsWith(StaticTemplateEnum.data_provider_exception_value.getCode())) {
            return fillExceptionProvider(inputDto);
        } else if (inputDto.getProviderName().startsWith(StaticTemplateEnum.data_provider_dict_value.getCode())) {
            return fillDictProvider(inputDto);
        }
        return null;
    }

    /**
     * 寻找字典
     *
     * @param inputDto 输入实体类对象
     * @return 字典项出参DTO的list
     */
    public List<DictDetailsOutputDto> findDict(QueryDictInputDto inputDto) {
        List<DictDetailsOutputDto> list = new ArrayList<>();
        VarProcessDict dict = varProcessDictService.getOne(
                new QueryWrapper<VarProcessDict>().lambda()
                        .eq(VarProcessDict::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessDict::getCode, inputDto.getCode())
                        .eq(VarProcessDict::getState, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        if (dict == null) {
            return list;
        }
        List<VarProcessDictDetails> details = varProcessDictDetailsService.list(
                new QueryWrapper<VarProcessDictDetails>().lambda()
                        .eq(VarProcessDictDetails::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessDictDetails::getDictId, dict.getId())
                        .eq(VarProcessDictDetails::getState, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .orderByAsc(VarProcessDictDetails::getCode)
        );
        if (CollectionUtils.isEmpty(details)) {
            return list;
        }
        for (VarProcessDictDetails dictDetails : details) {
            DictDetailsOutputDto outputDto = new DictDetailsOutputDto();
            outputDto.setName(dictDetails.getCode());
            outputDto.setLabel(dictDetails.getName());
            list.add(outputDto);
        }
        return list;
    }

    /**
     * 获取内置函数EngineFunction的模版内容
     *
     * @param inputDto 入参
     * @return 内置函数模版内容
     */
    public JSONObject getEngineFunctionTemplate(EngineFunctionTemplateInputDto inputDto) {
        return engineFunctionBiz.getEngineFunctionTemplate(inputDto);
    }

    /**
     * 组装异常码值provider信息
     *
     * @param inputDto
     * @return DomainDataModelTreeDto List
     */
    private List<DomainDataModelTreeDto> fillExceptionProvider(VarDataProviderListInputDto inputDto) {
        List<String> dataTypeByType = commonTemplateEnumConverter.getDataTypeByType(inputDto.getDataType());
        if (CollectionUtils.isEmpty(dataTypeByType)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "请求入参类型type错误，请重试");
        }
        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list(new QueryWrapper<VarProcessConfigExcept>().lambda()
                .eq(VarProcessConfigExcept::getVarProcessSpaceId, inputDto.getSpaceId())
                .in(VarProcessConfigExcept::getDataType, dataTypeByType)
                .eq(VarProcessConfigExcept::getExceptionType, "2")
                .eq(VarProcessConfigExcept::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (CollectionUtils.isEmpty(exceptionValueList)) {
            return new ArrayList<>();
        }
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        exceptionValueList.stream().forEach(exceptionValue -> {
            mergeResult.add(DomainDataModelTreeDto.builder()
                    .isUse("0")
                    .isEmpty("0")
                    .name("异常值")
                    .describe(exceptionValue.getExceptionExplain())
                    .isArr("0")
                    .label("异常值-" + exceptionValue.getExceptionExplain())
                    .type(exceptionValue.getDataType())
                    .value(exceptionValue.getExceptionValueCode())
                    .build());
        });
        List<DomainDataModelTreeDto> items = new ArrayList<>();
        items.add(DomainDataModelTreeDto.builder()
                .name("exceptionValue")
                .value("exceptionValue")
                .describe("异常值")
                .isArr("0").isEmpty("0").type("object")
                .children(mergeResult)
                .build());
        return items;
    }

    /**
     * 组装字典值provider信息
     *
     * @param inputDto
     * @return DomainDataModelTreeDto List
     */
    private List<DomainDataModelTreeDto> fillDictProvider(VarDataProviderListInputDto inputDto) {
        //处理value带this情况
        String dictValue = inputDto.getDictValue();
        String fullPathValue = StringUtils.isEmpty(inputDto.getDictFullPathValue()) ? "" : inputDto.getDictFullPathValue();
        if (StringUtils.isEmpty(dictValue)) {
            return null;
        }
        dictValue = dictValue.startsWith(THIS) ? fullPathValue : dictValue;
        if (!StringUtils.isEmpty(dictValue)) {
            Pair<TemplateUnitTypeEnum, Long> pair = getTemplateUnitType(VarTemplateTypeEnum.getCode(inputDto.getType()), inputDto.getVariableId(), inputDto.getFunctionId());
            //本地变量查询
            if (dictValue.toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase()) || dictValue.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())) {
                dictValue = commonLocalDataBiz.getProviderTypeFullPath(pair.getLeft(), inputDto.getSpaceId(), pair.getRight(), dictValue, inputDto.getSessionId());
            }
            if (!StringUtils.isEmpty(dictValue)) {
                DomainDataModelTreeDto domainDataModelTreeDto = commonGlobalDataBiz.findProperty(pair.getLeft(), inputDto.getSpaceId(), dictValue);
                if (Objects.isNull(domainDataModelTreeDto)) {
                    return null;
                }
                List<DictDetailsOutputDto> dictList = findDict(QueryDictInputDto.builder().spaceId(inputDto.getSpaceId()).varFullPath(dictValue).code(domainDataModelTreeDto.getEnumName()).build());
                if (CollectionUtils.isEmpty(dictList)) {
                    return new ArrayList<>();
                }
                List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
                dictList.forEach(data -> mergeResult.add(DomainDataModelTreeDto.builder()
                        .isUse("0")
                        .isEmpty("0")
                        .name(data.getName())
                        .describe(data.getLabel())
                        .isArr("0")
                        .label(data.getName() + "-" + data.getLabel())
                        .type(domainDataModelTreeDto.getType())
                        .typeRef(domainDataModelTreeDto.getType())
                        .value(data.getName())
                        .build()));
                return mergeResult;
            }
        }
        return null;
    }

    /**
     * 根据类型组装公共函数模板
     * @param baseObj baseObj
     * @param allCommonFunctionList allCommonFunctionList
     * @param functionId functionId
     * @param varTemplateTypeEnum varTemplateTypeEnum
     * @param providerEnum providerEnum
     * @param manifestFlow manifestFlow
     */
    private void getCommonFuncProvicer(JSONObject baseObj, List<VarProcessFunction> allCommonFunctionList, Long functionId, VarTemplateTypeEnum varTemplateTypeEnum, FunctionCommonProviderEnum providerEnum, boolean manifestFlow) {
        //过滤当前类型的函数数据
        List<String> types = commonTemplateEnumConverter.getDataTypeByType(providerEnum.getDataType());
        List<FunctionTypeEnum> functionTypeList = new ArrayList<>();
        if (varTemplateTypeEnum == VarTemplateTypeEnum.VAR_PROCESS) {
            functionTypeList.add(FunctionTypeEnum.TEMPLATE);
        } else if (varTemplateTypeEnum == VarTemplateTypeEnum.COMMON_FUNCTION) {
            functionTypeList.add(FunctionTypeEnum.FUNCTION);
        } else if (varTemplateTypeEnum == VarTemplateTypeEnum.SERVICE_INTERFACE) {
            functionTypeList.add(FunctionTypeEnum.FUNCTION);
            if (!manifestFlow) {
                functionTypeList.add(FunctionTypeEnum.PREP);
                functionTypeList.add(FunctionTypeEnum.TEMPLATE);
            }
        }
        List<VarProcessFunction> list = allCommonFunctionList.stream()
                .filter(e -> types.contains(e.getFunctionDataType()) && functionTypeList.contains(e.getFunctionType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        JSONArray items = new JSONArray();
        for (VarProcessFunction function : list) {
            if (functionId != null && functionId.equals(function.getId())) {
                continue;
            }
            String functionTemplateName = "function_custom_" + function.getIdentifier() + "_template";
            JSONObject itemJson = new JSONObject();
            itemJson.put("label", function.getName());
            itemJson.put("identifier", function.getIdentifier());
            itemJson.put("type", "template");
            itemJson.put("name", functionTemplateName);
            items.add(itemJson);
        }
        JSONObject functionProvicerJson = new JSONObject();
        functionProvicerJson.put("type", "list");
        functionProvicerJson.put("items", items);
        baseObj.put(providerEnum.getProviderName(), functionProvicerJson);
    }

    /**
     * filterRoot
     *
     * @param typeList  类型列表
     * @param inputType 输入类型
     * @param name      名称
     */
    public void filterRoot(List<String> typeList, List<String> inputType, String name) {
        if (!CollectionUtils.isEmpty(inputType)) {
            inputType = inputType.stream().filter(f -> !f.equals(name)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(inputType)) {
                typeList.addAll(inputType);
            }
        }
    }

    /**
     * 转换成 DomainModelTree
     *
     * @param content 内容
     * @return 决策领域树形结构实体对象
     */
    public DomainModelTree getDomainModelTree(String content) {
        JSONObject jsonObject = JSONObject.parseObject(content, Feature.OrderedField);
        DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);
        return domainModelTree;
    }

    /**
     * 根据provider类型组装变量数据
     *
     * @param templateParam @return JSONObject
     * @return com.wisecotech.json.JSONObject
     */
    private JSONObject fillTemplate(TemplateParam templateParam) {
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        //单独组装自定义函数返回值变量
        if (templateParam.getFunctionRetDto() != null && templateParam.getDataProvider().getProviderName().equals(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())) {
            mergeResult.add(templateParam.getFunctionRetDto());
        }
        //查询组装数据
        List<DomainDataModelTreeDto> mergeData = this.mergeData(
                new MergeDataParam(templateParam.getPair().getKey(), templateParam.getInputDto().getSpaceId(), templateParam.getPair().getValue(), templateParam.getDataProvider(), templateParam.getInputDto().getSessionId(), templateParam.getJsonSchemalList(), templateParam.getParamOutTreeDto(), TemplateFunctionTypeEnum.getTypeEnum(templateParam.getInputDto().getFunctionSubType()), templateParam.getVarList(), VarTemplateTypeEnum.getCode(templateParam.getInputDto().getType()), templateParam.getInputDto().getManifestId()));
        //数据追加前端需要字段

        //添加数据之前排序
        for (DomainDataModelTreeDto domainDataModelTreeDto : mergeData) {
            Collections.sort(domainDataModelTreeDto.getChildren(), Comparator.comparing(o -> o.getName().toUpperCase()));
        }

        if (!CollectionUtils.isEmpty(mergeData)) {
            mergeResult.addAll(mergeData);
        }

        JSONObject providerObj = new JSONObject();
        templateParam.getBaseObj().put(templateParam.getDataProvider().getProviderName(), providerObj);
        providerObj.put("type", "data");
        if (templateParam.getDataProvider().equals(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_OBJECT)
                || templateParam.getDataProvider().equals(TemplateDataProviderEnum.DATA_PROVIDER_OBJECT)
                || templateParam.getDataProvider().equals(TemplateDataProviderEnum.DATA_PROVIDER_ARRAY)
                || templateParam.getDataProvider().equals(TemplateDataProviderEnum.DATA_PROVIDER_ARRAY_OBJECT)
                || templateParam.getDataProvider().equals(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY)) {
            providerObj.put("object_could_choose", 1);
        }
        if (!CollectionUtils.isEmpty(mergeResult)) {
            providerObj.put("items", mergeResult);
        } else {
            providerObj.put("items", new JSONArray());
        }
        templateParam.getBaseObj().put(templateParam.getDataProvider().getProviderName(), providerObj);
        return null;
    }

    /**
     * 扩展数据的value需要添加前缀名
     *
     * @param mergeData
     * @param dataProvider
     * @return DomainDataModelTreeDto List
     */
    private List<DomainDataModelTreeDto> handleRawDataAfterMerge(List<DomainDataModelTreeDto> mergeData, TemplateDataProviderEnum dataProvider) {
        boolean isArrayProvider = dataProvider == TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY;
        //便利删除非扩展字段
        for (DomainDataModelTreeDto modelTreeDto : mergeData) {
            if (PositionVarEnum.fromName(modelTreeDto.getName()) != null) {
                continue;
            }
            List<DomainDataModelTreeDto> children = modelTreeDto.getChildren();
            if (CollectionUtils.isEmpty(children)) {
                continue;
            }
            if (isArrayProvider) {
                List<DomainDataModelTreeDto> newChild = new ArrayList<>();
                children.forEach(child -> {
                    if (!StringUtils.isEmpty(child.getIsExtend()) && "1".equals(child.getIsExtend())) {
                        newChild.add(child);
                    }
                });
                modelTreeDto.setChildren(newChild);
            }
        }
        return mergeData;
    }

    /**
     * 预处理模板，left_provider组装可扩展数据
     * 预处理公共函数raw:预处理函数只需要可扩展数据，服务接口左值只需要逻辑处理对象
     *
     * @param handlePreRawDataParam  参数
     * @return 预处理后的结果Map<String, DomainDataModelTreeDto>
     */
    private Map<String, DomainDataModelTreeDto> handlePreRawData(HandlePreRawDataParam handlePreRawDataParam) {
        Map<String, DomainDataModelTreeDto> jsonSchemalList = handlePreRawDataParam.getJsonSchemalList();
        if (handlePreRawDataParam.getDataProvider().getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())) {
            //如果是左值，优先处理参数，只有出参可以作为左值赋值
            jsonSchemalList = handleLeftValueParamOut(jsonSchemalList, handlePreRawDataParam.getParamOutTreeDto());
        }
        if (handlePreRawDataParam.getVarTemplateTyp() == VarTemplateTypeEnum.SERVICE_INTERFACE
                && handlePreRawDataParam.getDataProvider().getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())) {
            return serviceManifestTemplatePre(jsonSchemalList, handlePreRawDataParam.getDataProvider(), handlePreRawDataParam.getManifestId());
        }
        if (handlePreRawDataParam.getFunctionTypeEnum() != TemplateFunctionTypeEnum.PREP
                || !handlePreRawDataParam.getDataProvider().getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName())) {
            return jsonSchemalList;
        }
        Map<String, DomainDataModelTreeDto> newJsonSchemalList = new HashMap<>(MagicNumbers.EIGHT);
        newJsonSchemalList.putAll(handlePreRawDataParam.getJsonSchemalList());
        VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getId, VarProcessFunction::getPrepObjectName)
                .eq(VarProcessFunction::getId, handlePreRawDataParam.getFunctionId()));
        VarProcessDataModel model = varProcessDataModelService.findByDataModelName(varProcessFunction.getPrepObjectName());
        String extendDataValue = MessageFormat.format("{0}.{1}", TemplateVarLocationEnum.RAW_DATA.getDisplayName(),
                varProcessFunction.getPrepObjectName());
        if (!StringUtils.isEmpty(handlePreRawDataParam.getLoopDataValue()) && !handlePreRawDataParam.getLoopDataValue().startsWith(extendDataValue)) {
            return jsonSchemalList;
        }
        if (!StringUtils.isEmpty(handlePreRawDataParam.getLoopDataValue())) {
            extendDataValue = handlePreRawDataParam.getLoopDataValue();
        }
        //在树形结构中寻找到dataValue条件的对象
        DomainDataModelTreeDto treeByDataValue = dynamicTreeConverter.findTreeByDataValueAndTreeDto(
                newJsonSchemalList.get(TemplateVarLocationEnum.RAW_DATA.getDisplayName()), extendDataValue);
        if (treeByDataValue == null || CollectionUtils.isEmpty(treeByDataValue.getChildren())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据模型未包含预处理对象");
        }
        boolean isInnerLogic = Objects.nonNull(model) && model.getObjectSourceType() == VarProcessDataModelSourceType.INSIDE_LOGIC;
        if (StringUtils.isEmpty(handlePreRawDataParam.getLoopDataValue()) && !isInnerLogic) {
            treeByDataValue = dynamicTreeConverter.findDataValueTree(treeByDataValue, extendDataValue);
        }
        if (handlePreRawDataParam.getDataProvider() == TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_OBJECT) {
            treeByDataValue = TemplateModelConverter.findDomainModelTreeByTypeList(treeByDataValue, handlePreRawDataParam.getLoopDataValue());
        } else if (handlePreRawDataParam.getDataProvider().getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE.getProviderName()) && !handlePreRawDataParam.getDataProvider().getProviderName().startsWith(TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY.getProviderName())) {
            treeByDataValue = TemplateModelConverter.beanCopyDynamicTreeOutputDtoByTypeList(treeByDataValue, handlePreRawDataParam.getLoopDataValue(), handlePreRawDataParam.getDataProvider().getTypeList());
        }
        // 如果对象来源是“内部逻辑计算“，此处所有的数据都是扩展数据，导入时也默认作为扩展数据
        // 左值可以直接选择“内部逻辑计算”的根对象
        // 这里把所有根节点为“扩展数据”都置为可选
        if (treeByDataValue == null) {
            newJsonSchemalList.remove(TemplateVarLocationEnum.RAW_DATA.getDisplayName());
        } else {
            newJsonSchemalList.put(TemplateVarLocationEnum.RAW_DATA.getDisplayName(), treeByDataValue);
        }
        return newJsonSchemalList;
    }

    private Map<String, DomainDataModelTreeDto> handleLeftValueParamOut(Map<String, DomainDataModelTreeDto> jsonSchemalList, DomainDataModelTreeDto paramOutTreeDto) {
        Map<String, DomainDataModelTreeDto> newJsonSchemalList = new HashMap<>(MagicNumbers.EIGHT);
        //处理左值时只能处理入参信息
        for (String key : jsonSchemalList.keySet()) {
            DomainDataModelTreeDto domainDataModelTreeDto = jsonSchemalList.get(key);
            if (domainDataModelTreeDto == null) {
                continue;
            }
            if (!PositionVarEnum.PARAMETERS.getName().equalsIgnoreCase(domainDataModelTreeDto.getName())) {
                newJsonSchemalList.put(key, domainDataModelTreeDto);
                continue;
            } else if (paramOutTreeDto != null && !CollectionUtils.isEmpty(paramOutTreeDto.getChildren())) {
                newJsonSchemalList.put(PositionVarEnum.PARAMETERS.getName(), paramOutTreeDto);
            }
        }
        return newJsonSchemalList;
    }

    /**
     * 服务接口表达式模板预处理
     * rawData只保留逻辑处理对象，并且移除扩展字段
     *
     * @param jsonSchemalList json体
     * @param dataProvider    模板数据生产者的枚举
     * @param manifestId      变量清单Id
     * @return 预处理结果(Map < String, DomainDataModelTreeDto >)
     */
    private Map<String, DomainDataModelTreeDto> serviceManifestTemplatePre(Map<String, DomainDataModelTreeDto> jsonSchemalList, TemplateDataProviderEnum dataProvider, Long manifestId) {
        int varProcessSourceTypeNum = MagicNumbers.FOUR;
        List<VarProcessManifestDataModel> mappingList = varProcessManifestDataModelService.list(new QueryWrapper<VarProcessManifestDataModel>().lambda()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId)
                .eq(VarProcessManifestDataModel::getSourceType, varProcessSourceTypeNum));
        if (CollectionUtils.isEmpty(mappingList)) {
            return null;
        }
        Map<String, DomainDataModelTreeDto> newJsonSchemalList = new HashMap<>(MagicNumbers.EIGHT);
        newJsonSchemalList.putAll(jsonSchemalList);
        DomainDataModelTreeDto rawDataDataModelTreeDto = newJsonSchemalList.get(TemplateVarLocationEnum.RAW_DATA.getDisplayName());
        if (rawDataDataModelTreeDto == null || CollectionUtils.isEmpty(rawDataDataModelTreeDto.getChildren())) {
            return null;
        }
        List<String> objectNameList = mappingList.stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toList());
        List<DomainDataModelTreeDto> newChild = new ArrayList<>();
        rawDataDataModelTreeDto.getChildren().forEach(child -> {
            if (objectNameList.contains(child.getName())) {
                child = removeExtendChild(child);
                if (child != null) {
                    newChild.add(child);
                }
            }
        });
        if (CollectionUtils.isEmpty(newChild)) {
            newJsonSchemalList.remove(TemplateVarLocationEnum.RAW_DATA.getDisplayName());
            return newJsonSchemalList;
        }
        rawDataDataModelTreeDto.setChildren(newChild);
        newJsonSchemalList.put(TemplateVarLocationEnum.RAW_DATA.getDisplayName(), rawDataDataModelTreeDto);
        return newJsonSchemalList;
    }

    /**
     * 移除扩展类型数据
     *
     * @param dataModelTree 数据模型树Dto
     * @return 移除扩展类型数据的结果
     */
    private DomainDataModelTreeDto removeExtendChild(DomainDataModelTreeDto dataModelTree) {
        if (dataModelTree == null || CollectionUtils.isEmpty(dataModelTree.getChildren())) {
            return null;
        }
        Iterator it = dataModelTree.getChildren().iterator();
        while (it.hasNext()) {
            DomainDataModelTreeDto child = (DomainDataModelTreeDto) it.next();
            if (!StringUtils.isEmpty(child.getIsExtend()) && "1".equals(child.getIsExtend())) {
                it.remove(); //移除该对象
            } else {
                removeExtendChild(child);
            }
        }
        return dataModelTree;
    }

    /**
     * 自定义函数返回值
     *
     * @param inputDto 前端发送过来的实体
     * @return 自定义函数返回值的结果
     */
    public DomainDataModelTreeDto fillReturnModelTreeDto(VarProcessTemplateInputDto inputDto) {
        if (inputDto.getType().equals(VarTemplateTypeEnum.VAR_PROCESS.getCode())) {
            VarProcessVariable varProcessVariable = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getDataType)
                    .eq(VarProcessVariable::getId, inputDto.getVariableId()));
            DataTypeEnum type = StringUtils.isEmpty(inputDto.getReturnType()) ? DataTypeEnum.getEnum(varProcessVariable.getDataType().toUpperCase())
                    : inputDto.getReturnType();
            return buildFunctionReturnDto(type.getDesc(), "result", "变量值");
        } else if (inputDto.getType().equals(VarTemplateTypeEnum.COMMON_FUNCTION.getCode())
                && TemplateFunctionTypeEnum.getTypeEnum(inputDto.getFunctionSubType()) != TemplateFunctionTypeEnum.PREP) {
            VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                    .select(VarProcessFunction::getId, VarProcessFunction::getFunctionDataType)
                    .eq(VarProcessFunction::getId, inputDto.getFunctionId()));
            DataTypeEnum type = StringUtils.isEmpty(inputDto.getReturnType()) ? DataTypeEnum.getEnum(varProcessFunction.getFunctionDataType())
                    : inputDto.getReturnType();
            return buildFunctionReturnDto(type.getDesc(), "return", "返回值");
        } else {
            log.info("自定义函数返回值为null");
        }
        return null;
    }

    private DomainDataModelTreeDto buildFunctionReturnDto(String functionDataType, String name, String describe) {
        if (StringUtils.isEmpty(functionDataType) || VOID.equals(functionDataType)) {
            return null;
        }
        return DomainDataModelTreeDto.builder().label("返回值").name(name).value("functionReturn").describe(describe).isArr("0").isEmpty("1")
                .isExtend("0").type(functionDataType).typeRef(functionDataType).build();
    }

    /**
     * 查询已上架的最大版本的变量信息
     *
     * @param spaceId 变量空间Id
     * @param roleDataAuthority 用户的数据权限DTO
     * @return 上架的最大版本的变量信息
     */
    private List<VarProcessVariable> fillVariableList(Long spaceId, RoleDataAuthorityDTO roleDataAuthority) {
        if (roleDataAuthority == null) {
            return null;
        }
        List<VarProcessVariable> varList = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda()
                .select(VarProcessVariable::getId, VarProcessVariable::getVarProcessSpaceId, VarProcessVariable::getCategoryId, VarProcessVariable::getIdentifier, VarProcessVariable::getName,
                        VarProcessVariable::getLabel, VarProcessVariable::getDataType, VarProcessVariable::getVersion
                )
                .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()),
                        VarProcessVariable::getDeptCode, roleDataAuthority.getDeptCodes())
                .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getUserNames()),
                        VarProcessVariable::getCreatedUser, roleDataAuthority.getUserNames()));
        if (CollectionUtils.isEmpty(varList)) {
            return null;
        }
        List<VarProcessVariable> maxVersionVarList = new ArrayList<>();
        Map<String, List<VarProcessVariable>> map = varList.stream().collect(Collectors.groupingBy(b -> b.getIdentifier()));
        for (Map.Entry<String, List<VarProcessVariable>> entry : map.entrySet()) {
            List<VarProcessVariable> varProcessVariables = map.get(entry.getKey());
            maxVersionVarList.add(varProcessVariables.stream().max(Comparator.comparing(VarProcessVariable::getVersion)).get());
        }
        return maxVersionVarList;
    }

    /**
     * 合并数据
     *
     * @param mergeDataParam 输入参数
     * @return 返回合并后的结果(List < DomainDataModelTreeDto >)
     */
    private List<DomainDataModelTreeDto> mergeData(MergeDataParam mergeDataParam) {
        //预处理公共函数raw:预处理函数只需要可扩展数据，服务接口左值只需要逻辑处理对象
        Map<String, DomainDataModelTreeDto> newJsonSchemalList = handlePreRawData(new HandlePreRawDataParam(mergeDataParam.getJsonSchemalList(), mergeDataParam.getParamOutTreeDto(), mergeDataParam.getDataProvider(), mergeDataParam.getFunctionTypeEnum(), mergeDataParam.getLocalId(), null,
                mergeDataParam.getVarTemplateTyp(), mergeDataParam.getManifestId()));
        //1.获取全局变量位置
        List<TemplateVarLocationEnum> modelVarLocationLst = commonTemplateEnumConverter.getModelVarsLocation(mergeDataParam.getDataProvider(), mergeDataParam.getFunctionTypeEnum(), mergeDataParam.getVarTemplateTyp());
        //获取对应的变量类型
        List<DataVariableTypeEnum> dataTypeLst = commonTemplateEnumConverter.getDataTypeByProvider(mergeDataParam.getDataProvider());
        Assert.notEmpty(dataTypeLst, MessageFormat.format("no list<DataVariableTypeEnum> defined by dataProvider[{0}]", mergeDataParam.getDataProvider()));
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        if (mergeDataParam.getDataProvider().getProviderName().startsWith(DATA_PROVIDER_ARRAY) || mergeDataParam.getDataProvider().getProviderName().startsWith(DATA_PROVIDER_LEFTVALUE_ARRAY)) {
            List<DomainDataModelTreeDto> arrayMergeResult = assemBaseArrayType(
                    new BaseArrayParam(mergeDataParam.getTypeEnum(), mergeDataParam.getSpaceId(), mergeDataParam.getLocalId(), mergeDataParam.getDataProvider(), modelVarLocationLst, dataTypeLst, mergeDataParam.getSessionId(), newJsonSchemalList));
            mergeResult.addAll(arrayMergeResult);
        } else {
            //组装基本类型 bool,date,string,number,array,left,object
            List<DomainDataModelTreeDto> baseMergeResult = assemBaseType(
                    new AssemBaseTypeParam(mergeDataParam.getTypeEnum(), mergeDataParam.getSpaceId(), mergeDataParam.getLocalId(), mergeDataParam.getDataProvider(), modelVarLocationLst, dataTypeLst, mergeDataParam.getSessionId(), newJsonSchemalList));
            mergeResult.addAll(baseMergeResult);
        }
        if (!CollectionUtils.isEmpty(mergeResult)) {
            //针对预处理模板的公共函数，left_array需要后置处理一下
            mergeResult = handleRawDataAfterMerge(mergeResult, mergeDataParam.getDataProvider());
        }
        handleVariableList(mergeResult, mergeDataParam.getDataProvider(), mergeDataParam.getVarList(), mergeDataParam.getTypeEnum());
        return mergeResult;
    }

    /**
     * 追加已上架的衍生变量信息
     *
     * @param mergeResult  合并结果
     * @param dataProvider 模板数据生产者的枚举
     * @param varList      变量List集合
     * @param typeEnum     模板单元类型的枚举
     */
    private void handleVariableList(List<DomainDataModelTreeDto> mergeResult, TemplateDataProviderEnum dataProvider, List<VarProcessVariable> varList, TemplateUnitTypeEnum typeEnum) {
        List<String> typeList = getVariableTypeByProvider(dataProvider);
        if (CollectionUtils.isEmpty(typeList) || CollectionUtils.isEmpty(varList) || typeEnum == TemplateUnitTypeEnum.SPACE_COMMON_FUNCTION) {
            return;
        }
        List<VarProcessVariable> typeVarList = varList.stream().filter(b -> typeList.contains(b.getDataType())).collect(Collectors.toList());
        List<DomainDataModelTreeDto> child = new ArrayList<>();
        typeVarList.stream().forEach(var -> {
            child.add(DomainDataModelTreeDto.builder()
                    .isUse("0")
                    .isEmpty("0")
                    .name(var.getName())
                    .describe(var.getLabel())
                    .isArr("0")
                    .label(var.getName() + "-" + var.getLabel())
                    .type(var.getDataType())
                    .typeRef(var.getDataType())
                    .value(MessageFormat.format("vars.{0}", var.getName()))
                    .identifier(var.getIdentifier())
                    .build());
        });
        if (CollectionUtils.isEmpty(child)) {
            log.info("child变量为空");
        } else {
            //衍生变量信息
            mergeResult.add(DomainDataModelTreeDto.builder()
                    .name("vars")
                    .value("vars")
                    .describe("变量")
                    .isArr("0")
                    .isEmpty("0")
                    .type("object")
                    .children(child)
                    .build());
        }
    }

    private List<String> getVariableTypeByProvider(TemplateDataProviderEnum dataProvider) {
        switch (dataProvider) {
            case DATA_PROVIDER_BOOL:
                return Arrays.asList(DataVariableSimpleTypeEnum.BOOLEAN_TYPE.getMessage());
            case DATA_PROVIDER_STRING:
                return Arrays.asList(DataVariableSimpleTypeEnum.STRING_TYPE.getMessage());
            case DATA_PROVIDER_NUMBER:
                return Arrays.asList(DataVariableSimpleTypeEnum.INT_TYPE.getMessage(), DataVariableSimpleTypeEnum.DOUBLE_TYPE.getMessage());
            case DATA_PROVIDER_DATE:
                return Arrays.asList(DataVariableSimpleTypeEnum.DATE_TYPE.getMessage(), DataVariableSimpleTypeEnum.DATETIME_TYPE.getMessage());
            default:
                return null;
        }
    }

    /**
     * 组装基本类型 bool,date,string,number,array,left,objectd的 provider
     *
     * @param assemBaseTypeParam 输入参数
     * @return 组装基本类型的结果(List < DomainDataModelTreeDto >)
     */
    private List<DomainDataModelTreeDto> assemBaseType(AssemBaseTypeParam assemBaseTypeParam) {
        //全局变量数据查询
        List<DomainDataModelTreeDto> modelVars = null;
        if (!CollectionUtils.isEmpty(assemBaseTypeParam.getModelVarLocationLst()) && !CollectionUtils.isEmpty(assemBaseTypeParam.getDataTypeLst()) && !CollectionUtils.isEmpty(assemBaseTypeParam.getJsonSchemalList())) {
            modelVars = commonGlobalDataBiz.findStaticTree(StaticTreeInputDto.builder()
                    .type(assemBaseTypeParam.getTypeEnum().getType())
                    .spaceId(assemBaseTypeParam.getSpaceId())
                    .positionList(assemBaseTypeParam.getModelVarLocationLst().stream().map(location -> location.getDisplayName()).collect(Collectors.toList()))
                    .typeList(assemBaseTypeParam.getDataTypeLst().stream().map(type -> type.getMessage()).collect(Collectors.toList()))
                    .jsonschemaDtoMap(assemBaseTypeParam.getJsonSchemalList())
                    .build());

        }
        //对查询出的数据进行过滤组装
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        if (null != modelVars && modelVars.size() > 0) {
            mergeResult.addAll(modelVars);
        }
        return mergeResult.stream().filter(item -> item != null).collect(Collectors.toList());
    }

    /**
     * 组装基本类型数组 array_bool,array_date,array_string,array_number provider
     * @param baseArrayParam @return DomainDataModelTreeDto
     * @return java.util.List<com.decision.jsonschema.util.dto.DomainDataModelTreeDto>
     */
    private List<DomainDataModelTreeDto> assemBaseArrayType(BaseArrayParam baseArrayParam) {
        List<DomainDataModelTreeDto> modelVars = null;
        if (!baseArrayParam.getModelVarLocationLst().isEmpty() && !CollectionUtils.isEmpty(baseArrayParam.getJsonSchemalList())) {
            //调用谢武service
            modelVars = commonGlobalDataBiz.findTreeVarBaseArray(TreeVarBaseArrayInputDto.builder()
                    .type(baseArrayParam.getTypeEnum().getType())
                    .spaceId(baseArrayParam.getSpaceId())
                    .positionList(baseArrayParam.getModelVarLocationLst().stream().map(location -> location.getDisplayName()).collect(Collectors.toList()))
                    .typeList(baseArrayParam.getDataTypeLst().stream().map(type -> type.getMessage()).collect(Collectors.toList()))
                    .jsonschemaDtoMap(baseArrayParam.getJsonSchemalList())
                    .build());
            log.debug("填充静态模板 provider数据spaceId:{},variableId:{},providerName:{},modelVars:{}", baseArrayParam.getSpaceId(), baseArrayParam.getVariableId(), baseArrayParam.getDataProvider(), modelVars);
        }
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        if (null != modelVars && modelVars.size() > 0) {
            mergeResult.addAll(modelVars);
        }
        return mergeResult.stream().filter(item -> item != null).collect(Collectors.toList());
    }

    /**
     * 预处理模板
     *
     * @param varType 变量类型
     * @return 预处理模板类型
     */
    private JSONObject initTemplateConfig(String varType) {
        VarTemplateTypeEnum varTemplateType = VarTemplateTypeEnum.getCode(varType);
        if (varTemplateType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "入参type不合法！");
        }
        JSONObject templateStatic = JSONObject.parseObject(templateProperties.getVarTemplateStatic());
        if (varTemplateType == VarTemplateTypeEnum.VAR_PROCESS) {
            buildDataTypeProvider(templateStatic, "变量模板");
            buildFunctionCommonTypeTemplate(templateStatic, "变量模板");
        } else if (varTemplateType == VarTemplateTypeEnum.COMMON_FUNCTION) {
            buildDataTypeProvider(templateStatic, "公共方法");
            buildStatementCommon(templateStatic);
            buildFunctionCommonTypeTemplate(templateStatic, "公共方法");
        } else if (varTemplateType == VarTemplateTypeEnum.SERVICE_INTERFACE) {
            buildDataTypeProvider(templateStatic, "公共方法");
            buildStatementCommon(templateStatic);
            buildFunctionCommonTypeTemplate(templateStatic, "公共方法");
        }
        return templateStatic;
    }

    private void buildFunctionCommonTypeTemplate(JSONObject templateStatic, String functionTypeDesc) {
        StaticTemplateEnum.getFunctionCommonTypeTemplate().stream().forEach(functionCommonTypeTemplate -> {
            JSONObject part = new JSONObject();
            part.put("type", "placeholder");
            part.put("label", MessageFormat.format("<{0}{1}>", commonTemplateEnumConverter.getDataTypeMsg(functionCommonTypeTemplate), functionTypeDesc));
            part.put("useParentPlaceholder", true);
            part.put("provider", MessageFormat.format("function_common_provider{0}", functionCommonTypeTemplate.getRemark()));
            JSONArray parts = new JSONArray();
            parts.add(part);
            JSONObject template = new JSONObject();
            template.put("type", "part");
            template.put("parts", parts);
            if (!FUNCTION_COMMON_NUMBER_TEMPLATE.equals(functionCommonTypeTemplate.getCode()) && !FUNCTION_COMMON_DATE_TEMPLATE.equals(functionCommonTypeTemplate.getCode()) && !FUNCTION_COMMON_STRING_TEMPLATE.equals(functionCommonTypeTemplate.getCode()) && !FUNCTION_COMMON_BOOL_TEMPLATE.equals(functionCommonTypeTemplate.getCode())) {
                template.put("statement", true);
            }
            templateStatic.put(functionCommonTypeTemplate.getCode(), template);
        });
    }

    private void buildStatementCommon(JSONObject templateStatic) {
        StaticTemplateEnum.getStatementList().stream().forEach(staticTemplateEnum -> {
            if (!templateStatic.containsKey(staticTemplateEnum.getCode())) {
                return;
            }
            JSONArray itemsJson = templateStatic.getJSONObject(staticTemplateEnum.getCode()).getJSONArray(JsonSchemaFieldEnum.ITEMS_FIELD.getMessage());
            JSONObject json = new JSONObject();
            json.put("type", "template");
            json.put("label", MessageFormat.format("<执行公共方法>", commonTemplateEnumConverter.getDataTypeMsg(staticTemplateEnum)));
            json.put("name", "function_common_template");
            itemsJson.add(MagicNumbers.NINE, json);
        });
    }

    private void buildDataTypeProvider(JSONObject templateStatic, String functionTypeDesc) {
        StaticTemplateEnum.getDataTypeProviderList().stream().forEach(staticTemplateEnum -> {
            JSONArray itemsJson = templateStatic.getJSONObject(staticTemplateEnum.getCode()).getJSONArray(JsonSchemaFieldEnum.ITEMS_FIELD.getMessage());
            JSONObject json = new JSONObject();
            json.put("type", "template");
            json.put("label", MessageFormat.format("<{0}{1}>", commonTemplateEnumConverter.getDataTypeMsg(staticTemplateEnum), functionTypeDesc));
            json.put("name", MessageFormat.format("function_common{0}_template", staticTemplateEnum.getRemark()));
            if (StaticTemplateEnum.logic_provider == staticTemplateEnum) {
                itemsJson.add(itemsJson.size() - MagicNumbers.FOUR, json);
            } else {
                itemsJson.add(itemsJson.size() - MagicNumbers.INT_1, json);
            }
        });
    }

    @Data
    @AllArgsConstructor
    private static class MergeDataParam {
        private final TemplateUnitTypeEnum typeEnum;
        private final Long spaceId;
        private final Long localId;
        private final TemplateDataProviderEnum dataProvider;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final DomainDataModelTreeDto paramOutTreeDto;
        private final TemplateFunctionTypeEnum functionTypeEnum;
        private final List<VarProcessVariable> varList;
        private final VarTemplateTypeEnum varTemplateTyp;
        private final Long manifestId;
    }

    @Data
    @AllArgsConstructor
    private static class AssemBaseTypeParam {
        private final TemplateUnitTypeEnum typeEnum;
        private final Long spaceId;
        private final Long variableId;
        private final TemplateDataProviderEnum dataProvider;
        private final List<TemplateVarLocationEnum> modelVarLocationLst;
        private final List<DataVariableTypeEnum> dataTypeLst;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
    }
    @Getter
    @AllArgsConstructor
    private static class AppendProviderObjData {
        private final TemplateUnitTypeEnum tempType;
        private final Long spaceId;
        private final Long localId;
        private final Pair<String, String> dataValuePair;
        private final DomainDataModelTreeDto other;
        private final TemplateDataProviderEnum dataProvider;
        private final List<DataVariableTypeEnum> dataTypeLst;
        private final List<TemplateVarLocationEnum> parameterAndLocalVarLocationLst;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemaList;
        private final DomainDataModelTreeDto paramOutTreeDto;
        private final ComponentJsonDto ruleContent;
        private final String functionTypeEnum;
        private final VarTemplateTypeEnum varTemplateTyp;
        private final Long manifestId;
    }
    @Getter
    @AllArgsConstructor
    private static class TemplateParam {
        private final VarProcessTemplateInputDto inputDto;
        private final JSONObject baseObj;
        private final TemplateDataProviderEnum dataProvider;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final DomainDataModelTreeDto paramOutTreeDto;
        private final DomainDataModelTreeDto functionRetDto;
        private final List<VarProcessVariable> varList;
        private final Pair<TemplateUnitTypeEnum, Long> pair;

    }
    @Getter
    @AllArgsConstructor
    private static class BaseArrayParam {
        private final TemplateUnitTypeEnum typeEnum;
        private final Long spaceId;
        private final Long variableId;
        private final TemplateDataProviderEnum dataProvider;
        private final List<TemplateVarLocationEnum> modelVarLocationLst;
        private final List<DataVariableTypeEnum> dataTypeLst;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
    }
    @Getter
    @AllArgsConstructor
    private static class HandlePreRawDataParam {
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final DomainDataModelTreeDto paramOutTreeDto;
        private final TemplateDataProviderEnum dataProvider;
        private final TemplateFunctionTypeEnum functionTypeEnum;
        private final Long functionId;
        private final String loopDataValue;
        private final VarTemplateTypeEnum varTemplateTyp;
        private final Long manifestId;
    }
}
