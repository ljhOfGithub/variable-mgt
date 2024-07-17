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

import cn.hutool.core.collection.CollUtil;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableSimpleTypeEnum;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.decision.jsonschema.util.enums.toolkit.AttributeAccessEnum;
import com.decision.jsonschema.util.enums.toolkit.ObjectTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wiseco.decision.common.business.enums.ComponentTypeEnum;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceReqModelTree;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.enums.DataValuePrefixEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.JavaBeanMethodProviderEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateDataProviderEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateFunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitParameter;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.converter.CommonTemplateEnumConverter;
import com.wiseco.var.process.app.server.service.converter.DynamicTreeConverter;
import com.wiseco.var.process.app.server.service.dto.input.DataValueAndTypeGetVarTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.json.ComponentJsonDto;
import com.wiseco.var.process.app.server.service.dto.json.VarBasicPropertyJsonDto;
import com.wiseco.var.process.app.server.service.dto.output.DynamicObjectOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.JavaToolkitClassMethodsInfoDTO;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
import java.util.stream.Collectors;

/**
 * @author: zhouxiuxiu
 * @since  2022/6/13 14:51
 */
@RefreshScope
@Service
@Slf4j
public class CommonTemplateBiz {

    public static final String VOID = "void";
    private static final String DATA_PROVIDER_ARRAR = "data_provider_array_";
    private static final String THIS_REPLACE = "this_";
    private static final String COMMON_FUNCTION = "commonFunction";
    @Autowired
    private DynamicTreeConverter dynamicTreeConverter;
    @Autowired
    private CommonGlobalDataBiz commonGlobalDataBiz;
    @Autowired
    private CommonLocalDataBiz commonLocalDataBiz;
    @Autowired
    private CommonTemplateEnumConverter commonTemplateEnumConverter;
    @Autowired
    private JavaToolKitBiz javaToolKitBiz;
    @Autowired
    private OutsideService outsideService;
    /**
     * originalFullName
     *
     * @param dataValues 入参
     * @return List
     */
    public List<Pair<String, String>> originalFullName(List<String> dataValues) {
        if (CollectionUtils.isEmpty(dataValues)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEMPLATE_ARRAY_LOOP, "数组内追加变量信息失败，请联系技术人员");
        }
        if (Sets.newHashSet(dataValues).size() != dataValues.size()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEMPLATE_ARRAY_LOOP, "需要追加变量的数组元素相同，无法查询，请重新操作");
        }
        //全量路径的value,key：路径的最后，pair：left:引用路径，right:传入路径 修改：兼容多个value最后一层路径相同问题
        Map<String, List<Pair<String, String>>> fullNameMap = new HashMap<>(MagicNumbers.EIGHT);
        //this路径的value,key：路径的第一位，value：全路径
        Map<String, Pair<String, String>> thisNameMap = new HashMap<>(MagicNumbers.EIGHT);
        //key:变量路径全部_链接, pair：left:引用路径，right:传入路径
        Map<String, Pair<String, String>> allUnderLineMap = new HashMap<>(MagicNumbers.EIGHT);
        for (String dataValue : dataValues) {
            if (dataValue.toLowerCase().startsWith("this_")) {
                String replace = dataValue.replace("this_", "");
                String[] split = replace.split("\\.");
                String thisValue = dataValue.substring(0, dataValue.indexOf("."));
                int a = thisValue.length() - thisValue.replace("_", "").length();
                if (a > 1) {
                    String theOrigValue = dataValue;
                    if (allUnderLineMap.containsKey(thisValue.replace("this_", ""))) {
                        Pair<String, String> thisFullPathValue = allUnderLineMap.get(thisValue.replace("this_", ""));
                        theOrigValue = thisFullPathValue.getKey() + dataValue.substring(dataValue.indexOf("."));
                        putFullNameMap(fullNameMap, dataValue.substring(dataValue.lastIndexOf(".") + 1), Pair.of(theOrigValue, dataValue));
                        allUnderLineMap.put(dataValue.replace("this_", "").replace(".", "_"), Pair.of(theOrigValue, dataValue));
                    } else {
                        thisNameMap.put(split[0], Pair.of(replace, dataValue));
                    }
                } else {
                    thisNameMap.put(split[0], Pair.of(replace, dataValue));
                }
                continue;
            }
            for (DataValuePrefixEnum dataValuePrefixEnum : DataValuePrefixEnum.values()) {
                if (dataValue.toLowerCase().startsWith(dataValuePrefixEnum.name().toLowerCase())) {
                    putFullNameMap(fullNameMap, dataValue.substring(dataValue.lastIndexOf(".") + 1), Pair.of(dataValue, dataValue));
                    allUnderLineMap.put(dataValue.replace("this_", "").replace(".", "_"), Pair.of(dataValue, dataValue));
                    break;
                }
            }
        }
        int i = 0;
        while (!CollectionUtils.isEmpty(thisNameMap)) {
            Iterator<Map.Entry<String, Pair<String, String>>> it = thisNameMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Pair<String, String>> entry = it.next();
                String thisFirst = entry.getKey();
                Pair<String, String> originalFull = entry.getValue();
                String thisFull = originalFull.getKey();
                if (allUnderLineMap.containsKey(thisFirst)) {
                    Pair<String, String> thisFullPathValue = allUnderLineMap.get(thisFirst);
                    String dataValue = originalFull.getValue();
                    String refPathValue = thisFullPathValue.getKey() + originalFull.getKey().substring(originalFull.getKey().indexOf("."));
                    putFullNameMap(fullNameMap, dataValue.substring(dataValue.lastIndexOf(".") + 1), Pair.of(refPathValue, dataValue));
                    allUnderLineMap.put(dataValue.replace("this_", "").replace(".", "_"), Pair.of(refPathValue, dataValue));
                    it.remove();
                    continue;
                }
                if (fullNameMap.containsKey(thisFirst)) {
                    String[] thisFullSplit = thisFull.split("\\.");
                    if (fullNameMap.get(thisFullSplit[thisFullSplit.length - 1]) != null && fullNameMap.get(thisFullSplit[thisFullSplit.length - 1]).size() > 1) {
                        throw new VariableMgtBusinessServiceException("需要追加变量的数组元素相同，无法查询，请重新操作");
                    }
                    putFullNameMap(fullNameMap, thisFullSplit[thisFullSplit.length - 1], Pair.of(fullNameMap.get(thisFirst).get(0).getKey() + thisFull.replaceFirst(thisFirst,""), originalFull.getValue()));
                    it.remove();
                }
            }
            i++;
            if (i > MagicNumbers.INT_50) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "数组循环选择数组变量错误，请重新选择");
            }
        }
        List<Pair<String, String>> list = new ArrayList<>();
        for (Map.Entry<String, List<Pair<String, String>>> listEntry : fullNameMap.entrySet()) {
            list.addAll(listEntry.getValue());
        }
        return list;
    }
    private void putFullNameMap(Map<String, List<Pair<String, String>>> fullNameMap, String key, Pair<String, String> valuePair) {
        if (fullNameMap.containsKey(key)) {
            fullNameMap.get(key).add(valuePair);
        } else {
            List<Pair<String, String>> fullNamePairList = new ArrayList<>();
            fullNamePairList.add(valuePair);
            fullNameMap.put(key, fullNamePairList);
        }

    }

    /**
     * appendProviderObjData
     *
     * @param providerObjData 入参
     */
    public void appendProviderObjData(ProviderObjData providerObjData) {
        DomainDataModelTreeDto otherNode = null;
        String dataValue = providerObjData.getDataValuePair().getKey();

        DataValueAndTypeGetVarTreeInputDto inputDto = DataValueAndTypeGetVarTreeInputDto.builder().type(providerObjData.getType().getType())
                .globalId(providerObjData.getGlobalId()).dataValue(dataValue).jsonschemaDtoMap(providerObjData.getJsonSchemalList())
                .typeList(providerObjData.getDataTypeLst() == null ? null : providerObjData.getDataTypeLst().stream().map(dataType -> dataType.getMessage()).collect(Collectors.toList())).build();
        if (!providerObjData.getDataProvider().getProviderName().toLowerCase().startsWith(DATA_PROVIDER_ARRAR)) {
            //组装基本类型 bool,date,string,number,array,left,object,local
            otherNode = commonGlobalDataBiz.findDataValueAndTypeGetVarTree(inputDto);
        } else {
            otherNode = commonGlobalDataBiz.findDataValueAndTypeGetArrayVarTree(inputDto);
        }

        if (otherNode == null) {
            return;
        }

        replaceVarThis(otherNode, providerObjData.getOtherConfig(), providerObjData.getDataProvider(), dataValue);
    }

    /**
     * replaceVarThis
     *
     * @param replaceModelTreeDto 入参
     * @param resultModelTree 入参
     * @param dataProvider 入参
     * @param dataValue 入参
     */
    public void replaceVarThis(DomainDataModelTreeDto replaceModelTreeDto, DomainDataModelTreeDto resultModelTree,
                               TemplateDataProviderEnum dataProvider, String dataValue) {
        //去逗号的最后一级然后拼装this
        String newName = getThisValueName(dataValue);

        String fullPathName = replaceModelTreeDto.getValue();
        String newValue = replaceModelTreeDto.getValue().replace(dataValue, newName);
        String[] parts = dataValue.split("\\.");
        String shortName = "this_" + parts[parts.length - 1];
        replaceModelTreeDto.setName(newValue);
        replaceModelTreeDto.setValue(newValue);
        replaceModelTreeDto.setIsArr("0");
        replaceModelTreeDto.setFullPathValue(fullPathName);
        //子节点只替换value
        replaceOtherNodeChildrenValue(dataValue, newName, replaceModelTreeDto.getChildren(), dataProvider, shortName);
        resultModelTree.getChildren().add(replaceModelTreeDto);
    }

    /**
     * getThisValueName
     *
     * @param dataValue 入参
     * @return String
     */
    private String getThisValueName(String dataValue) {
        String[] parts = dataValue.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append("_").append(part);
        }
        return "this" + sb;
    }

    /**
     * getVarBasicProperty
     *
     * @param type 入参
     * @param globalId 入参
     * @param localId 入参
     * @param dataValue 入参
     * @param fullPathValue 入参
     * @param sessionId 入参
     * @return JSONObject
     */
    public JSONObject getVarBasicProperty(TemplateUnitTypeEnum type, Long globalId, Long localId, String dataValue, String fullPathValue,
                                          String sessionId) {
        //处理value带this情况
        boolean parametersFlag = false;
        String tempDataValue = dataValue;
        fullPathValue = StringUtils.isEmpty(fullPathValue) ? "" : fullPathValue;
        if (dataValue.startsWith(THIS_REPLACE)) {
            tempDataValue = fullPathValue;
        }
        if (StringUtils.isEmpty(tempDataValue)) {
            return new JSONObject();
        }
        dataValue = tempDataValue;

        // 1.查询所有jsonschemal
        String ruleContent = commonLocalDataBiz.getContent(type, globalId, localId, sessionId);
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(ruleContent, ComponentJsonDto.class);
        Map<String, DomainDataModelTreeDto> paramLocalTreeDto = commonLocalDataBiz.fillParamLocalTreeDto(type, globalId, localId, componentJsonDto);

        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemaMap = findAllVarTreeDto(type, globalId, null);
        if (!CollectionUtils.isEmpty(paramLocalTreeDto)) {
            jsonSchemaMap.putAll(paramLocalTreeDto);
        }
        //筛选
        List<DomainDataModelTreeDto> contentList = dynamicTreeConverter.filterDsStrategyAllVar(jsonSchemaMap,
                TemplateVarLocationEnum.templateVarLocationEnumList());

        DomainDataModelTreeDto domainDataModelTreeDto = commonGlobalDataBiz.findPropertyByTreeList(type, globalId, tempDataValue, contentList);
        if (Objects.isNull(domainDataModelTreeDto)) {
            return new JSONObject();
        }
        List<DomainDataModelTreeDto> treeDtoList = domainDataModelTreeDto.getChildren();

        if (CollUtil.isEmpty(treeDtoList)) {
            return new JSONObject();
        }
        return fillVarBasePropertyTemplate(treeDtoList, parametersFlag, fullPathValue, dataValue, tempDataValue);
    }

    /**
     *查询对象动态
     * @param objectDynamic 入参
     * @return DynamicObjectOutputDto
     */
    public DynamicObjectOutputDto queryObjectDynamic(ObjectDynamic objectDynamic) {
        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        if (objectDynamic.getDataValue().startsWith(THIS_REPLACE)) {
            objectDynamic.setDataValue(objectDynamic.getFullPathValue());
        }
        //查询需要的变量数据
        //查询表达式存储内容
        String content = commonLocalDataBiz.getContent(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getLocalId(), objectDynamic.getSessionId());
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(content, ComponentJsonDto.class);
        Map<String, DomainDataModelTreeDto> paramLocalTreeDto = commonLocalDataBiz.fillParamLocalTreeDto(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getLocalId(), componentJsonDto);

        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemalList = findAllVarTreeDto(objectDynamic.getType(), objectDynamic.getGlobalId(), Pair.of(objectDynamic.getFunctionTypeEnum(), objectDynamic.getLocalId()));
        if (!CollectionUtils.isEmpty(paramLocalTreeDto)) {
            jsonSchemalList.putAll(paramLocalTreeDto);
        }

        //全局变量接口查询
        List<DomainDataModelTreeDto> modelVars = commonGlobalDataBiz.findDomainDataByDataValueGetObject(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getDataValue(), jsonSchemalList);
        //本地变量接口查询（local param）
        //        List<DomainDataModelTreeDto> paramVarsAndLocalVars = commonLocalDataBiz.findParamVarsAndLocalVars(type, globalId, localId,
        //                new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.PARAMETER_INPUT, TemplateVarLocationEnum.PARAMETER_OUTPUT, TemplateVarLocationEnum.LOCAL_VARS)),
        //                new ArrayList<>(Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE)), Pair.of(dataValue, false),
        //                sessionId, false, "itselfAndSubset", RequestTypeEnum.INTERNAL, content, jsonSchemalList);
        if (!CollectionUtils.isEmpty(modelVars)) {
            mergeResult.addAll(modelVars);
        }
        log.debug("填充模板动态数据globalId:{},dataValue:{},result:{}", objectDynamic.getGlobalId(), objectDynamic.getDataValue(), mergeResult);
        if (!CollectionUtils.isEmpty(objectDynamic.getLoopDataValues())) {
            buildOtherProviderData(
                    new BuildOtherProvideDataParam(objectDynamic.getType(), mergeResult, objectDynamic.getGlobalId(), objectDynamic.getLocalId(), objectDynamic.getLoopDataValues(), TemplateDataProviderEnum.DATA_PROVIDER_OBJECT, objectDynamic.getSessionId(), jsonSchemalList, componentJsonDto));
        }
        return DynamicObjectOutputDto.builder().items(mergeResult).type("data").objectCouldChoose(1).build();
    }

    /**
     * queryObjectArrayDynamic
     *
     * @param objectDynamic 入参
     * @return DynamicObjectOutputDto
     */
    public DynamicObjectOutputDto queryObjectArrayDynamic(ObjectDynamic objectDynamic) {

        List<DomainDataModelTreeDto> mergeResult = new ArrayList<>();
        if (objectDynamic.getDataValue().startsWith(THIS_REPLACE)) {
            objectDynamic.setDataValue(objectDynamic.getFullPathValue());
        }
        //查询需要的变量数据
        //查询表达式存储内容
        String content = commonLocalDataBiz.getContent(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getLocalId(), objectDynamic.getSessionId());
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(content, ComponentJsonDto.class);
        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemalList = findAllVarTreeDto(objectDynamic.getType(), objectDynamic.getGlobalId(), Pair.of(objectDynamic.getFunctionTypeEnum(), objectDynamic.getLocalId()));
        //全局变量数据查询
        List<DomainDataModelTreeDto> modelVars = commonGlobalDataBiz.findDomainDataByDataValueGetArrayObject(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getDataValue());
        //本地变量（local param） 查询
        List<DomainDataModelTreeDto> paramVarsAndLocalVars = commonLocalDataBiz.findArrayParamVarsAndLocalVars(
                new CommonLocalDataBiz.ArrayParamVarsAndLocalVars(objectDynamic.getType(), objectDynamic.getGlobalId(), objectDynamic.getLocalId(), new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.PARAMETER_INPUT, TemplateVarLocationEnum.PARAMETER_OUTPUT,
                        TemplateVarLocationEnum.LOCAL_VARS)), new ArrayList<>(Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE)), Pair.of(objectDynamic.getDataValue(), false), objectDynamic.getSessionId(), content, jsonSchemalList));
        if (!CollectionUtils.isEmpty(modelVars)) {
            mergeResult.addAll(modelVars);
        }
        if (!CollectionUtils.isEmpty(paramVarsAndLocalVars)) {
            mergeResult.addAll(paramVarsAndLocalVars);
        }
        log.debug("填充模板动态数据globalId:{},dataValue:{},result:{}", objectDynamic.getGlobalId(), objectDynamic.getDataValue(), mergeResult);
        //兼容处理数组循环内，需要追加数组属性信息
        if (!CollectionUtils.isEmpty(objectDynamic.getLoopDataValues())) {
            buildOtherProviderData(
                    new BuildOtherProvideDataParam(objectDynamic.getType(), mergeResult, objectDynamic.getGlobalId(), objectDynamic.getLocalId(), objectDynamic.getLoopDataValues(), TemplateDataProviderEnum.DATA_PROVIDER_ARRAY_OBJECT, objectDynamic.getSessionId(), jsonSchemalList, componentJsonDto));
        }
        return DynamicObjectOutputDto.builder().items(mergeResult).type("data").objectCouldChoose(1).build();
    }

    /**
     * compareObjectProperty
     *
     * @param compareObjectProperty 入参
     */
    public void compareObjectProperty(CompareObjectProperty compareObjectProperty) {
        compareObjectProperty.setDataValueA(compareObjectProperty.getDataValueA().startsWith("this_") ? compareObjectProperty.getFullPathValueA() : compareObjectProperty.getDataValueA());
        compareObjectProperty.setDataValueB(compareObjectProperty.getDataValueB().startsWith("this_") ? compareObjectProperty.getFullPathValueB() : compareObjectProperty.getDataValueB());

        //1.查询所有jsonschemal
        String ruleContent = commonLocalDataBiz.getContent(compareObjectProperty.getType(), compareObjectProperty.getGlobalId(), compareObjectProperty.getLocalId(), compareObjectProperty.getSessionId());
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(ruleContent, ComponentJsonDto.class);
        Map<String, DomainDataModelTreeDto> paramLocalTreeDto = commonLocalDataBiz.fillParamLocalTreeDto(compareObjectProperty.getType(), compareObjectProperty.getGlobalId(), compareObjectProperty.getLocalId(), componentJsonDto);
        //查询各个jsonSchema内容
        Map<String, DomainDataModelTreeDto> jsonSchemaMap = findAllVarTreeDto(compareObjectProperty.getType(), compareObjectProperty.getGlobalId(), null);
        if (!CollectionUtils.isEmpty(paramLocalTreeDto)) {
            jsonSchemaMap.putAll(paramLocalTreeDto);
        }
        //筛选
        List<DomainDataModelTreeDto> contentList = dynamicTreeConverter.filterDsStrategyAllVar(jsonSchemaMap, TemplateVarLocationEnum.templateVarLocationEnumList());

        DomainDataModelTreeDto domainDataModelTreeDtoA = commonGlobalDataBiz.findPropertyByTreeList(compareObjectProperty.getType(), compareObjectProperty.getGlobalId(), compareObjectProperty.getDataValueA(), contentList);
        DomainDataModelTreeDto domainDataModelTreeDtoB = commonGlobalDataBiz.findPropertyByTreeList(compareObjectProperty.getType(), compareObjectProperty.getGlobalId(), compareObjectProperty.getDataValueB(), contentList);

        if (Objects.nonNull(compareObjectProperty.getOutsideServiceId())) {
            // 如果提供外部服务 ID
            // 从外部服务请求参数获取变量结构
            Map<String, DomainDataModelTreeDto> convertedOutsideServiceVariableInputRequestParamMap =
                    findAndConvertOutsideServiceVariableInputRequestParam(compareObjectProperty.getOutsideServiceId());
            // 与前端约定外部服务节点 dataValueA 表示请求参数对象名
            domainDataModelTreeDtoA = convertedOutsideServiceVariableInputRequestParamMap.get(compareObjectProperty.getDataValueA());
        }

        if (domainDataModelTreeDtoA == null) {
            domainDataModelTreeDtoA = findJavaToolkitClassModel(compareObjectProperty.getDataValueA());
        }
        if (domainDataModelTreeDtoB == null) {
            domainDataModelTreeDtoB = findJavaToolkitClassModel(compareObjectProperty.getDataValueB());
        }
        List<DomainDataModelTreeDto> childrenaList = domainDataModelTreeDtoA.getChildren();
        List<DomainDataModelTreeDto> childrenbList = domainDataModelTreeDtoB.getChildren();

        int childAsize = CollectionUtils.isEmpty(childrenaList) ? 0 : childrenaList.size();
        int childBsize = CollectionUtils.isEmpty(childrenbList) ? 0 : childrenbList.size();

        if (childAsize == 0 || childBsize == 0) {
            return;
        }
        Map<String, List<DomainDataModelTreeDto>> collectA = childrenaList.stream().collect(Collectors.groupingBy(DomainDataModelTreeDto::getName));
        Map<String, List<DomainDataModelTreeDto>> collectB = childrenbList.stream().collect(Collectors.groupingBy(DomainDataModelTreeDto::getName));

        StringBuffer difValue = new StringBuffer();
        Boolean matchResult = false;
        for (Map.Entry<String, List<DomainDataModelTreeDto>> entry : collectA.entrySet()) {
            if (!collectB.containsKey(entry.getKey())) {
                continue;
            }
            DomainDataModelTreeDto childA = collectA.get(entry.getKey()).get(0);
            DomainDataModelTreeDto childB = collectB.get(entry.getKey()).get(0);
            if (AttributeAccessEnum.READONLY.getAccess().equals(childB.getAccess())) {
                //只读变量不参与
                continue;
            }
            if (!childA.getType().equals(childB.getType()) || !childA.getIsArr().equals(childB.getIsArr())) {
                difValue.append(MessageFormat.format("{0}、", childA.getValue()));
            }
            if (childA.getType().equals(childB.getType()) && childA.getIsArr().equals(childB.getIsArr())) {
                matchResult = true;
            }
        }
        if (!StringUtils.isEmpty(difValue)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, MessageFormat.format("对象下的数据[{0}}]的数据类型/是否数组属性不一致，不能赋值", difValue.substring(0, difValue.length() - 1)));
        } else if (!matchResult) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "没有可匹配的数据，不能赋值");
        }
    }


    /**
     * 查询并转换外部服务变量输入请求参数
     *
     * @param outsideServiceId 外部服务 ID
     * @return Map, key: 变量路径, value: 数据模型树形结构 DTO
     */
    private Map<String, DomainDataModelTreeDto> findAndConvertOutsideServiceVariableInputRequestParam(Long outsideServiceId) {
        final OutsideServiceDetailRestOutputDto data = outsideService.getOutsideServiceDetailRestById(outsideServiceId);
        if (Objects.isNull(data)) {
            return Collections.emptyMap();
        }
        List<OutsideServiceReqModelTree> outsideServiceReqModelTreeList = JSON.parseArray(data.getReq().getRequestParam(), OutsideServiceReqModelTree.class);
        // 外部服务变量输入请求数据转换结果 Map, key: 变量路径, value: 数据模型树形结构 DTO
        Map<String, DomainDataModelTreeDto> convertedOutsideServiceVariableInputRequestParamMap = new HashMap<>(MagicNumbers.INT_64);
        // 将外部服务请求参数转换为数据模型树形结构
        outsideServiceVariableInputRequestParamConvertHelper(
                convertedOutsideServiceVariableInputRequestParamMap, outsideServiceReqModelTreeList, null, null, false);

        return convertedOutsideServiceVariableInputRequestParamMap;
    }

    private static void outsideServiceVariableInputRequestParamConvertHelper(Map<String, DomainDataModelTreeDto> convertedOutsideServiceVariableInputRequestParamMap,
                                                                             List<OutsideServiceReqModelTree> outsideServiceReqModelTreeList,
                                                                             List<DomainDataModelTreeDto> outsideReqModelTreeList,
                                                                             String parentalVariablePath, boolean isObjectArray) {
        if (CollectionUtils.isEmpty(outsideServiceReqModelTreeList)) {
            // 递归边界条件: 传入请求参数为空
            return;
        }

        for (OutsideServiceReqModelTree outsideServiceReqModelTree : outsideServiceReqModelTreeList) {
            if (!MagicStrings.VAR_INCOM.equals(outsideServiceReqModelTree.getValueType()) && !isObjectArray) {
                // 不分析非变量输入赋值类型的请求参数
                continue;
            }

            String currentVariablePath = org.springframework.util.StringUtils.isEmpty(parentalVariablePath)
                    ? outsideServiceReqModelTree.getParamName() : parentalVariablePath + StringPool.DOT + outsideServiceReqModelTree.getParamName();
            DomainDataModelTreeDto domainDataModelTreeDto = DomainDataModelTreeDto.builder()
                    .name(outsideServiceReqModelTree.getParamName())
                    .label(outsideServiceReqModelTree.getParamName() + StringPool.DASH + outsideServiceReqModelTree.getParamDesc())
                    .value(currentVariablePath)
                    .describe(outsideServiceReqModelTree.getParamDesc())
                    .isArr(outsideServiceReqModelTree.getIsArr())
                    .type(outsideServiceReqModelTree.getParamType())
                    .typeRef(currentVariablePath)
                    .build();
            convertedOutsideServiceVariableInputRequestParamMap.put(currentVariablePath, domainDataModelTreeDto);
            if (outsideReqModelTreeList != null) {
                outsideReqModelTreeList.add(domainDataModelTreeDto);
            }
            if (!CollectionUtils.isEmpty(outsideServiceReqModelTree.getChildren())) {
                List<DomainDataModelTreeDto> childModelTreeDto = new ArrayList<>();
                //判断是否是对象数组
                boolean childIsObjectArray = DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(outsideServiceReqModelTree.getParamType()) && DomainModelArrEnum.YES.getCode().equals(outsideServiceReqModelTree.getIsArr())
                        && MagicStrings.VAR_INCOM.equals(outsideServiceReqModelTree.getValueType());
                outsideServiceVariableInputRequestParamConvertHelper(
                        convertedOutsideServiceVariableInputRequestParamMap, outsideServiceReqModelTree.getChildren(), childModelTreeDto, currentVariablePath, childIsObjectArray);
                domainDataModelTreeDto.setChildren(childModelTreeDto);
            }
        }
    }


    private DomainDataModelTreeDto findJavaToolkitClassModel(String dataValue) {
        String classJsonSchema = javaToolKitBiz.getClassJsonSchemaByExistingJavaType(dataValue);
        if (StringUtils.isEmpty(classJsonSchema)) {
            return new DomainDataModelTreeDto();
        }
        return DomainModelTreeEntityUtils.getDomainModelTree(classJsonSchema);
    }

    /**
     * buildFunctionTemplate
     *
     * @param templateType 入参
     * @param name 入参
     * @param identifier 入参
     * @param content 入参
     * @param functionDataType 入参
     * @return JSONObject
     */
    public JSONObject buildFunctionTemplate(String templateType, String name, String identifier, String content,
                                            com.wiseco.var.process.app.server.enums.DataTypeEnum functionDataType) {
        if (!ComponentTypeEnum.FUNCTION.getCode().equals(templateType) && !VarTemplateTypeEnum.COMMON_FUNCTION.getCode().equals(templateType)) {
            return new JSONObject();
        }
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(content, ComponentJsonDto.class);
        ComponentJsonDto.DataModel dataModel = null;
        if (componentJsonDto == null || componentJsonDto.getBaseData() == null || componentJsonDto.getBaseData().getDataModel() == null) {
            dataModel = new ComponentJsonDto.DataModel();
        } else {
            dataModel = componentJsonDto.getBaseData().getDataModel();
        }
        JSONArray parts = new JSONArray();
        String parseValue = getParseValue(templateType, identifier, functionDataType);

        boolean noParameter = CollectionUtils.isEmpty(dataModel.getParameters()) ? true : false;
        JSONObject partTitle = new JSONObject();
        partTitle.put("label", name);
        partTitle.put("type", "text");
        parts.add(partTitle);
        JSONObject partDesc = new JSONObject();
        partDesc.put("label", "(");
        partDesc.put("type", "text");
        partDesc.put("value", parseValue);
        parts.add(partDesc);
        //有参数
        JSONArray parameterJsonArray = buildParameterJson(noParameter, dataModel);
        if (!CollectionUtils.isEmpty(parameterJsonArray)) {
            parts.addAll(parameterJsonArray);
        }
        JSONObject partBrackets = new JSONObject();
        partBrackets.put("label", ")");
        partBrackets.put("type", "text");
        partBrackets.put("value", ")");
        parts.add(partBrackets);
        //组装单行模板
        JSONObject line = new JSONObject();
        line.put("parts", parts);
        JSONArray lines = new JSONArray();
        lines.add(line);
        JSONObject template = new JSONObject();
        template.put("type", "block");
        template.put("returnType", functionDataType);
        template.put("lines", lines);
        return template;
    }

    /**
     * findAllVarTreeDto
     *
     * @param type 入参
     * @param globalId 入参
     * @param functionPair 入参
     * @return Map
     */
    public Map<String, DomainDataModelTreeDto> findAllVarTreeDto(TemplateUnitTypeEnum type, Long globalId,
                                                                 Pair<TemplateFunctionTypeEnum, Long> functionPair) {
        Map map = new HashMap(MagicNumbers.EIGHT);

        //空间
        VarProcessSpace varProcessSpace = dynamicTreeConverter.getVarProcessSpace(globalId);
        map.put(DomainModelSheetNameEnum.RAW_DATA.getMessage(),
                DomainModelTreeEntityUtils.getByContentStaticTemplateTree(varProcessSpace.getInputData()));
        return map;
    }

    /**
     * buildJavaToolkitMethodTemplate
     *
     * @param javaToolkitMethod 入参
     * @param parameters 入参
     * @return JSONObject
     */
    public JSONObject buildJavaToolkitMethodTemplate(JavaToolkitMethod javaToolkitMethod, List<JavaToolkitParameter> parameters) {
        Collections.sort(parameters, Collections.reverseOrder(Comparator.comparing(JavaToolkitParameter::getIdx)));
        Map<Integer, JavaToolkitParameter> map = new HashMap<>(MagicNumbers.EIGHT);
        for (JavaToolkitParameter parameter : parameters) {
            map.put(parameter.getIdx() + 1, parameter);
        }
        JSONArray parts = new JSONArray();

        //组装方法identifier信息，隐藏
        JSONObject partMethodIdenInfo = new JSONObject();
        partMethodIdenInfo.put("label", javaToolkitMethod.getLabel());
        partMethodIdenInfo.put("value", javaToolkitMethod.getIdentifier());
        partMethodIdenInfo.put("type", "hidden");
        parts.add(partMethodIdenInfo);

        //计算赔付方式B的赔付金额，<$1,一个数值>，<$2,一个字符>
        String template1 = javaToolkitMethod.getTemplate();
        while (template1 != null && !template1.isEmpty()) {
            if (!template1.contains("<")) {
                JSONObject partTitle = new JSONObject();
                partTitle.put("label", template1);
                partTitle.put("type", "text");
                parts.add(partTitle);
                template1 = null;
                continue;
            }
            int leftBracket = template1.indexOf("<");
            String label = template1.substring(0, leftBracket);
            template1 = template1.substring(leftBracket);
            if (!StringUtils.isEmpty(label)) {
                JSONObject partTitle = new JSONObject();
                partTitle.put("label", label);
                partTitle.put("type", "text");
                parts.add(partTitle);
            }

            int rightBracket = template1.indexOf(">");
            String paramLabel = template1.substring(1, rightBracket);
            template1 = template1.substring(rightBracket + 1);
            JSONObject paramJson = new JSONObject();
            int commaIndex = paramLabel.indexOf(",");
            paramJson.put("label", paramLabel.substring(commaIndex + 1));
            paramJson.put("type", "template");
            paramJson.put("paramIndex", paramLabel.substring(1, commaIndex));

            String paramIndex = paramLabel.substring(1, commaIndex);
            JavaToolkitParameter parameter = map.get(Integer.valueOf(paramIndex));
            String paramTemp = commonTemplateEnumConverter.getTemplate(parameter.getWrlType(), parameter.getIsArray() == 1, "in");
            paramJson.put("name", paramTemp);
            paramJson.put("isArray", parameter.getIsArray());
            if (paramTemp.contains("object")) {
                paramJson.put("compareValue", parameter.getJavaType());
            }
            parts.add(paramJson);
        }

        //组装单行模板
        JSONObject line = new JSONObject();
        line.put("parts", parts);
        JSONArray lines = new JSONArray();
        lines.add(line);
        JSONObject template = new JSONObject();
        template.put("type", "block");
        template.put("lines", lines);
        return template;
    }

    /**
     * getJavaTypeVarTree
     *
     * @param jsonschemalMap 入参
     * @param classCanonicalName 入参
     * @return List
     */
    public List<DomainDataModelTreeDto> getJavaTypeVarTree(Map<String, DomainDataModelTreeDto> jsonschemalMap, String classCanonicalName) {
        //查找engineVars变量
        List<DomainDataModelTreeDto> modelVars = new ArrayList<>();
        for (Map.Entry<String, DomainDataModelTreeDto> entry : jsonschemalMap.entrySet()) {
            DomainDataModelTreeDto treeDto = entry.getValue();
            DomainDataModelTreeDto modelTreeDto = treeDto.deepClone();
            findDomainModelTreeByTypeList(modelTreeDto, classCanonicalName);
            if (!CollectionUtils.isEmpty(modelTreeDto.getChildren())) {
                modelVars.add(modelTreeDto);
            }
        }
        return modelVars;
    }

    /**
     * getJavaBeanMethodProvider
     *
     * @param jsonSchemaList 入参
     * @param javaInstanceClazMethodList 入参
     * @param mergeResult 入参
     * @param javaBeanMethodProvider 入参
     */
    public void getJavaBeanMethodProvider(Map<String, DomainDataModelTreeDto> jsonSchemaList,
                                          Map<String, JavaToolkitClassMethodsInfoDTO> javaInstanceClazMethodList,
                                          List<DomainDataModelTreeDto> mergeResult, JavaBeanMethodProviderEnum javaBeanMethodProvider) {
        List<DomainDataModelTreeDto> methodTreeList = new ArrayList<>();
        //1.追加引入javabean的方法
        for (Map.Entry<String, DomainDataModelTreeDto> entry : jsonSchemaList.entrySet()) {
            DomainDataModelTreeDto treeDto = entry.getValue();
            boolean flag = treeDto != null
                    && (PositionVarEnum.ENGINE_VARS.getName().equals(treeDto.getName())
                    || PositionVarEnum.PARAMETERS.getName().equals(treeDto.getName())
                    || PositionVarEnum.LOCAL_VARS.getName().equals(treeDto.getName()));
            if (flag) {
                DomainDataModelTreeDto cloneTree = treeDto.deepClone();
                findJavatypeFillMethods(cloneTree, javaInstanceClazMethodList, javaBeanMethodProvider);
                if (!CollectionUtils.isEmpty(cloneTree.getChildren())) {
                    methodTreeList.add(cloneTree);
                }
            }
        }
        if (!methodTreeList.isEmpty()) {
            mergeResult.addAll(methodTreeList);
        }

    }


    /**
     * getJavaBeanUtilsClazMethodProvider
     *
     * @param javaUtilClazMethodList 入参
     * @param mergeResult 入参
     * @param javaBeanMethodProvider 入参
     */
    public void getJavaBeanUtilsClazMethodProvider(List<JavaToolkitClassMethodsInfoDTO> javaUtilClazMethodList,
                                                   List<DomainDataModelTreeDto> mergeResult, JavaBeanMethodProviderEnum javaBeanMethodProvider) {
        List<DomainDataModelTreeDto> methodTreeList = new ArrayList<>();
        for (JavaToolkitClassMethodsInfoDTO javaToolkitClassMethodsInfoDTO : javaUtilClazMethodList) {
            List<JavaToolkitMethod> javaToolkitMethods = javaToolkitClassMethodsInfoDTO.getJavaToolkitMethods();
            List<DomainDataModelTreeDto> clzMethodTreeList = new ArrayList<>();
            for (JavaToolkitMethod javaToolkitMethod : javaToolkitMethods) {
                boolean flag = (javaBeanMethodProvider.getDataType().contains(javaToolkitMethod.getReturnValueWrlType()) && javaBeanMethodProvider.getIsArray()
                        .equals(javaToolkitMethod.getReturnValueIsArray())) || VOID.equals(javaBeanMethodProvider.getDataType());
                if (flag) {
                    clzMethodTreeList.add(DomainDataModelTreeDto.builder()
                            .instanceValue(javaToolkitClassMethodsInfoDTO.getJavaToolkitClass().getCanonicalName()).name(javaToolkitMethod.getLabel())
                            .value(javaToolkitMethod.getIdentifier()).describe(javaToolkitMethod.getTemplate()).isArr("0").isEmpty("0").type("method")
                            .build());
                }
            }
            if (clzMethodTreeList.isEmpty()) {
                continue;
            }

            methodTreeList.add(DomainDataModelTreeDto.builder().name(javaToolkitClassMethodsInfoDTO.getJavaToolkitClass().getLabel())
                    .value(javaToolkitClassMethodsInfoDTO.getJavaToolkitClass().getCanonicalName())
                    .describe(javaToolkitClassMethodsInfoDTO.getJavaToolkitClass().getLabel()).isArr("0").isEmpty("0")
                    .type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).children(clzMethodTreeList).build());

        }
        if (!methodTreeList.isEmpty()) {
            mergeResult.addAll(methodTreeList);
        }
    }

    /**
     * findDomainModelTreeByTypeList
     *
     * @param domainDataModelTreeDto 入参
     * @param javaType 入参
     * @return boolean
     */
    public boolean findDomainModelTreeByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, String javaType) {
        if (ObjectUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return javaType.equals(domainDataModelTreeDto.getExistingJavaType());
        } else {

            boolean hasJavaType = false;
            // 创建迭代器
            Iterator<DomainDataModelTreeDto> treeDtoIterator = domainDataModelTreeDto.getChildren().iterator();
            while (treeDtoIterator.hasNext()) {
                // 遍历 JSONObject 中的 key, 避免 ConcurrentModificationException
                DomainDataModelTreeDto child = treeDtoIterator.next();
                if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                    treeDtoIterator.remove();
                    continue;
                } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    boolean isJavaType = findDomainModelTreeByTypeList(child, javaType);
                    if (!isJavaType) {
                        // JSONObject 属性均为空时, 移除 JSONObject
                        treeDtoIterator.remove();
                    } else {
                        if (CollectionUtils.isEmpty(child.getChildren())) {
                            child.setChildren(null);
                        }
                        hasJavaType = true;
                    }
                } else {
                    treeDtoIterator.remove();
                }
            }
            return hasJavaType || javaType.equals(domainDataModelTreeDto.getExistingJavaType());
        }

    }

    /**
     * buildParameterJson
     *
     * @param noParameter
     * @param dataModel
     * @return com.wisecotech.json.JSONArray
     */
    private JSONArray buildParameterJson(boolean noParameter, ComponentJsonDto.DataModel dataModel) {
        if (noParameter) {
            return null;
        }
        JSONArray parts = new JSONArray();
        boolean commaFlag = false;
        for (ComponentJsonDto.Parameter parameter : dataModel.getParameters()) {
            JSONObject partComma = new JSONObject();
            if (commaFlag) {
                partComma.put("label", ",");
            } else {
                partComma.put("label", "");
            }
            commaFlag = true;
            partComma.put("type", "text");
            partComma.put("value", ",");
            parts.add(partComma);

            JSONObject part1 = new JSONObject();
            part1.put("label", parameter.getLabel());
            String paramTemp = "";
            if (parameter.getDirection().equals(PositionVarEnum.PARAMETERS_IN.getName())) {
                part1.put("type", "template");
                paramTemp = commonTemplateEnumConverter.getTemplate(parameter.getType(), parameter.getIsArray(), parameter.getDirection());
                part1.put("name", paramTemp);
            } else {
                part1.put("type", "data");
                paramTemp = commonTemplateEnumConverter.getOutDataProvider(parameter.getType(), parameter.getIsArray(), parameter.getDirection());
                part1.put("name", paramTemp);
            }
            part1.put("isArray", parameter.getIsArray());
            if (paramTemp.contains("object")) {
                part1.put("compareValue", parameter.getType());
            }
            parts.add(part1);
        }
        return parts;
    }

    /**
     * fillVarBasePropertyTemplate
     *
     * @param treeDtoList
     * @param parametersFlag
     * @param fullPathValue
     * @param dataValue
     * @param tempDataValue
     * @return com.wisecotech.json.JSONObject
     */
    private JSONObject fillVarBasePropertyTemplate(List<DomainDataModelTreeDto> treeDtoList, boolean parametersFlag, String fullPathValue,
                                                   String dataValue, String tempDataValue) {
        List<VarBasicPropertyJsonDto.Line> lines = Lists.newArrayList();
        boolean finalParametersFlag = parametersFlag;
        for (DomainDataModelTreeDto modelTreeDto : treeDtoList) {
            if (AttributeAccessEnum.READONLY.getAccess().equals(modelTreeDto.getAccess())) {
                //只读变量不可写
                continue;
            }
            List<VarBasicPropertyJsonDto.Part> tempParts = Lists.newArrayList();
            VarBasicPropertyJsonDto.Part part1 = new VarBasicPropertyJsonDto.Part();
            part1.setType("text");
            part1.setLabel(modelTreeDto.getLabel());
            if (StringUtils.isEmpty(fullPathValue)) {
                part1.setValue(modelTreeDto.getValue());
            } else {
                part1.setValue(modelTreeDto.getValue().replace(fullPathValue, dataValue));
            }
            if (finalParametersFlag) {
                int index = modelTreeDto.getValue().lastIndexOf(".");
                part1.setValue(tempDataValue + modelTreeDto.getValue().substring(index));
            }
            part1.setTypeRef(modelTreeDto.getTypeRef());
            part1.setIsArr(modelTreeDto.getIsArr());
            part1.setDataType(modelTreeDto.getType());
            part1.setMarginLeft("20px");
            tempParts.add(part1);
            VarBasicPropertyJsonDto.Part part2 = new VarBasicPropertyJsonDto.Part();
            part2.setType("text");
            part2.setLabel("=");
            part2.setValue(":");
            tempParts.add(part2);
            VarBasicPropertyJsonDto.Part part3 = new VarBasicPropertyJsonDto.Part();
            part3.setType("template");

            Pair<String, String> pair = commonTemplateEnumConverter.getPart3Name(modelTreeDto);
            part3.setName(pair.getValue());
            part3.setLabel(pair.getLeft());
            tempParts.add(part3);

            VarBasicPropertyJsonDto.Line line = new VarBasicPropertyJsonDto.Line();
            line.setParts(tempParts);
            lines.add(line);
        }
        VarBasicPropertyJsonDto jsonDto = new VarBasicPropertyJsonDto();
        jsonDto.setType("lines");
        jsonDto.setLines(lines);
        return JSON.parseObject(JSON.toJSONString(jsonDto));
    }



    /**
     * 自定义函数获取方法名
     * @param templateType 模板类型
     * @param identifier 标识符
     * @param functionDataType 公共函数类型
     * @return String
     */
    private String getParseValue(String templateType, String identifier, com.wiseco.var.process.app.server.enums.DataTypeEnum functionDataType) {
        if (functionDataType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未获取到函数返回值信息");
        }
        if (VOID.equals(functionDataType.getDesc())) {
            //自定义函数void
            return "invokeVar(\"" + identifier + "\", data";
        } else if (templateType.equals(ComponentTypeEnum.FUNCTION.getCode())) {
            DataVariableSimpleTypeEnum type = DataVariableSimpleTypeEnum.getMessageEnum(functionDataType.getDesc());
            return MessageFormat.format("invokeFunctionComponent({1}.class,\"{0}\", data", identifier, type.getJavaType());
        } else if (templateType.equals(COMMON_FUNCTION)) {
            return MessageFormat.format("invokeVar(\"{0}\", data", identifier);
        } else {
            return null;
        }
    }



    /**
     * 数组循环内动态调用非append接口，需要追加this数据
     * @param buildOtherProvideDataParam 请求参数
     * @return DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto buildOtherProviderData(BuildOtherProvideDataParam buildOtherProvideDataParam) {
        //追加信息
        DomainDataModelTreeDto other = DomainDataModelTreeDto.builder().name("other").value("other").describe("其它变量").isArr("0").isEmpty("0")
                .type("object").children(new ArrayList<DomainDataModelTreeDto>()).build();
        //对循环内的this_obj.abc 拼出原始input.obj.abc
        List<Pair<String, String>> loopDataValues = originalFullName(buildOtherProvideDataParam.getDataValues());

        for (Pair<String, String> loopDataValue : loopDataValues) {
            List<DataVariableTypeEnum> dataTypeLst = commonTemplateEnumConverter.getDataTypeByProvider(buildOtherProvideDataParam.getDataProvider());
            List<TemplateVarLocationEnum> parameterAndLocalVarLocationLst = commonTemplateEnumConverter.getParameterAndVarsLocation(buildOtherProvideDataParam.getDataProvider());
            appendProviderObjData(
                    new ProviderObjData(buildOtherProvideDataParam.getType(), buildOtherProvideDataParam.getStrategyId(), buildOtherProvideDataParam.getComponentId(), loopDataValue, other, buildOtherProvideDataParam.getDataProvider(), dataTypeLst, parameterAndLocalVarLocationLst, buildOtherProvideDataParam.getSessionId(), buildOtherProvideDataParam.getJsonSchemalList(), buildOtherProvideDataParam.getContent()));
        }
        //other 为空的话不显示
        if (!other.getChildren().isEmpty()) {
            buildOtherProvideDataParam.getMergeResult().add(other);
        }
        return other;
    }



    /**
     * 追加value属性信息替换this
     * @param target
     * @param replacement
     * @param children
     * @param dataProvider
     * @param shortName
     */
    private void replaceOtherNodeChildrenValue(String target, String replacement, List<DomainDataModelTreeDto> children,
                                               TemplateDataProviderEnum dataProvider, String shortName) {
        if (null == children || children.isEmpty()) {
            return;
        }
        for (DomainDataModelTreeDto dto : children) {
            String fullPathName = dto.getValue();
            if (DataValuePrefixEnum.isLegalVarNew(fullPathName)) {
                dto.setName(fullPathName.replace(target, shortName));
            }
            String newValue = fullPathName.replace(target, replacement);
            dto.setValue(newValue);
            dto.setFullPathValue(fullPathName);
            replaceOtherNodeChildrenValue(target, replacement, dto.getChildren(), dataProvider, shortName);
        }
    }

    /**
     * findJavatypeFillMethods
     *
     * @param domainDataModelTreeDto 入参
     * @param javaInstanceClazMethodList 入参
     * @param javaBeanMethodProvider 入参
     * @return boolean
     */

    public boolean findJavatypeFillMethods(DomainDataModelTreeDto domainDataModelTreeDto,
                                           Map<String, JavaToolkitClassMethodsInfoDTO> javaInstanceClazMethodList,
                                           JavaBeanMethodProviderEnum javaBeanMethodProvider) {
        if (ObjectUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return appendJavaTypeMethods(domainDataModelTreeDto, javaInstanceClazMethodList, javaBeanMethodProvider);

        } else {
            boolean hasJavaType = false;
            // 创建迭代器
            Iterator<DomainDataModelTreeDto> treeDtoIterator = domainDataModelTreeDto.getChildren().iterator();
            while (treeDtoIterator.hasNext()) {
                // 遍历  key, 避免 ConcurrentModificationException
                DomainDataModelTreeDto child = treeDtoIterator.next();
                if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                    treeDtoIterator.remove();
                    continue;
                } else if (child.getType().equalsIgnoreCase(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    boolean isObjectType = findJavatypeFillMethods(child, javaInstanceClazMethodList, javaBeanMethodProvider);
                    if (!isObjectType) {
                        // 本身及child没有java类型, 移除 JSONObject
                        treeDtoIterator.remove();
                    } else {
                        if (CollectionUtils.isEmpty(child.getChildren())) {
                            treeDtoIterator.remove();
                            child.setChildren(null);
                        }
                        hasJavaType = true;
                    }
                } else {
                    treeDtoIterator.remove();
                }
            }
            return hasJavaType || appendJavaTypeMethods(domainDataModelTreeDto, javaInstanceClazMethodList, javaBeanMethodProvider);
        }

    }



    /**
     * 判断当前tree是否是javatype类型，是则追加java扩展方法
     * @param domainDataModelTreeDto
     * @param javaInstanceClazMethodList
     * @param javaBeanMethodProvider
     * @return true or false
     */
    private boolean appendJavaTypeMethods(DomainDataModelTreeDto domainDataModelTreeDto,
                                          Map<String, JavaToolkitClassMethodsInfoDTO> javaInstanceClazMethodList,
                                          JavaBeanMethodProviderEnum javaBeanMethodProvider) {
        if (ObjectTypeEnum.REF.getType().equals(domainDataModelTreeDto.getObjectType())) {
            //引用类型，组装child方法
            JavaToolkitClassMethodsInfoDTO javaToolkitClassMethodsInfoDTO = javaInstanceClazMethodList.get(domainDataModelTreeDto
                    .getExistingJavaType());

            if (javaToolkitClassMethodsInfoDTO == null || CollectionUtils.isEmpty(javaToolkitClassMethodsInfoDTO.getJavaToolkitMethods())) {
                return false;
            }
            List<DomainDataModelTreeDto> chilMethod = new ArrayList<>();
            for (JavaToolkitMethod javaToolkitMethod : javaToolkitClassMethodsInfoDTO.getJavaToolkitMethods()) {
                boolean isVoid = VOID.equals(javaBeanMethodProvider.getDataType());

                DataVariableSimpleTypeEnum simpleTypeEnum = DataVariableSimpleTypeEnum.getMessageEnum(javaToolkitMethod.getReturnValueWrlType());
                //简单类型
                boolean simpleCheck = simpleTypeEnum != null
                        && javaBeanMethodProvider.getDataType().contains(javaToolkitMethod.getReturnValueWrlType())
                        && javaBeanMethodProvider.getIsArray().equals(javaToolkitMethod.getReturnValueIsArray());
                //对象类型
                boolean objectCheck = !(VOID.equals(javaToolkitMethod.getReturnValueWrlType())) && simpleTypeEnum == null
                        && "object".equals(javaBeanMethodProvider.getDataType())
                        && javaBeanMethodProvider.getIsArray().equals(javaToolkitMethod.getReturnValueIsArray());
                if (isVoid || simpleCheck || objectCheck) {
                    chilMethod.add(DomainDataModelTreeDto.builder().name(javaToolkitMethod.getLabel()).value(javaToolkitMethod.getIdentifier())
                            .identifier(javaToolkitMethod.getIdentifier()).describe(javaToolkitMethod.getTemplate())
                            .instanceValue(domainDataModelTreeDto.getValue()).isArr("0").isEmpty("0").type("method").build());
                }

            }
            domainDataModelTreeDto.setChildren(chilMethod);
            return true;
        } else {
            return false;
        }
    }

    static class ProviderObjData {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final Pair<String, String> dataValuePair;
        private final DomainDataModelTreeDto otherConfig;
        private final TemplateDataProviderEnum dataProvider;
        private final List<DataVariableTypeEnum> dataTypeLst;
        private final List<TemplateVarLocationEnum> parameterAndLocalVarLocationLst;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final ComponentJsonDto content;

        ProviderObjData(TemplateUnitTypeEnum type, Long globalId, Long localId, Pair<String, String> dataValuePair, DomainDataModelTreeDto otherConfig, TemplateDataProviderEnum dataProvider, List<DataVariableTypeEnum> dataTypeLst, List<TemplateVarLocationEnum> parameterAndLocalVarLocationLst, String sessionId, Map<String, DomainDataModelTreeDto> jsonSchemalList, ComponentJsonDto content) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.dataValuePair = dataValuePair;
            this.otherConfig = otherConfig;
            this.dataProvider = dataProvider;
            this.dataTypeLst = dataTypeLst;
            this.parameterAndLocalVarLocationLst = parameterAndLocalVarLocationLst;
            this.sessionId = sessionId;
            this.jsonSchemalList = jsonSchemalList;
            this.content = content;
        }

        public TemplateUnitTypeEnum getType() {
            return type;
        }

        public Long getGlobalId() {
            return globalId;
        }

        public Long getLocalId() {
            return localId;
        }

        public Pair<String, String> getDataValuePair() {
            return dataValuePair;
        }

        public DomainDataModelTreeDto getOtherConfig() {
            return otherConfig;
        }

        public TemplateDataProviderEnum getDataProvider() {
            return dataProvider;
        }

        public List<DataVariableTypeEnum> getDataTypeLst() {
            return dataTypeLst;
        }

        public List<TemplateVarLocationEnum> getParameterAndLocalVarLocationLst() {
            return parameterAndLocalVarLocationLst;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Map<String, DomainDataModelTreeDto> getJsonSchemalList() {
            return jsonSchemalList;
        }

        public ComponentJsonDto getContent() {
            return content;
        }
    }

    static class ObjectDynamic {
        private final TemplateUnitTypeEnum type;
        private final TemplateFunctionTypeEnum functionTypeEnum;
        private final Long globalId;
        private final Long localId;
        private String dataValue;
        private final String fullPathValue;
        private final List<String> loopDataValues;
        private final String sessionId;

        ObjectDynamic(TemplateUnitTypeEnum type, TemplateFunctionTypeEnum functionTypeEnum, Long globalId, Long localId, String dataValue, String fullPathValue, List<String> loopDataValues, String sessionId) {
            this.type = type;
            this.functionTypeEnum = functionTypeEnum;
            this.globalId = globalId;
            this.localId = localId;
            this.dataValue = dataValue;
            this.fullPathValue = fullPathValue;
            this.loopDataValues = loopDataValues;
            this.sessionId = sessionId;
        }

        public TemplateUnitTypeEnum getType() {
            return type;
        }

        public TemplateFunctionTypeEnum getFunctionTypeEnum() {
            return functionTypeEnum;
        }

        public Long getGlobalId() {
            return globalId;
        }

        public Long getLocalId() {
            return localId;
        }

        public String getDataValue() {
            return dataValue;
        }

        public String getFullPathValue() {
            return fullPathValue;
        }

        public List<String> getLoopDataValues() {
            return loopDataValues;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setDataValue(String dataValue) {
            this.dataValue = dataValue;
        }
    }

    static class CompareObjectProperty {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private String dataValueA;
        private String dataValueB;
        private final String fullPathValueA;
        private final String fullPathValueB;
        private final String sessionId;
        private final Long outsideServiceId;

        CompareObjectProperty(TemplateUnitTypeEnum type, Long globalId, Long localId, String dataValueA, String dataValueB, String fullPathValueA, String fullPathValueB, String sessionId, Long outsideServiceId) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.dataValueA = dataValueA;
            this.dataValueB = dataValueB;
            this.fullPathValueA = fullPathValueA;
            this.fullPathValueB = fullPathValueB;
            this.sessionId = sessionId;
            this.outsideServiceId = outsideServiceId;
        }

        public TemplateUnitTypeEnum getType() {
            return type;
        }

        public Long getGlobalId() {
            return globalId;
        }

        public Long getLocalId() {
            return localId;
        }

        public String getDataValueA() {
            return dataValueA;
        }

        public String getDataValueB() {
            return dataValueB;
        }

        public String getFullPathValueA() {
            return fullPathValueA;
        }

        public String getFullPathValueB() {
            return fullPathValueB;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Long getOutsideServiceId() {
            return outsideServiceId;
        }

        public void setDataValueA(String dataValueA) {
            this.dataValueA = dataValueA;
        }

        public void setDataValueB(String dataValueB) {
            this.dataValueB = dataValueB;
        }
    }

    @Data
    @AllArgsConstructor
    private static class BuildOtherProvideDataParam {
        private final TemplateUnitTypeEnum type;
        private final List<DomainDataModelTreeDto> mergeResult;
        private final Long strategyId;
        private final Long componentId;
        private final List<String> dataValues;
        private final TemplateDataProviderEnum dataProvider;
        private final String sessionId;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final ComponentJsonDto content;
    }
}
