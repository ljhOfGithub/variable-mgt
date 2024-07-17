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
package com.wiseco.var.process.app.server.commons.test;

import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.wiseco.boot.commons.BeanCopyUtils;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormCategoryDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormDictDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormPathOutputDto;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测试在线表单填写结构转换
 *
 * @author wangxianli
 * @since 2022/3/29
 */
public class TestFormUtil {
    public static final String PARENT_ID = "parentId";

    //==========================策略测试==============================//

    /**
     * 将数据模型按对象、数组分割转成为前端表单结构(用于在线表单填写)
     * @param content 入参
     * @param domainDictMap 入参
     * @param strategyDictMap 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferModelTreeToForm(String content, Map<String, List<TestFormDictDto>> domainDictMap,
                                                                      Map<String, List<TestFormDictDto>> strategyDictMap) {

        DomainDataModelTreeDto domainModelTree = DomainModelTreeEntityUtils.getDomainModelTree(content);

        List<TestFormPathOutputDto> resultList = new ArrayList<>();

        recursionModelTreeToForm(domainModelTree, domainDictMap, strategyDictMap, resultList);

        return resultList;
    }

    /**
     * 递归处理数据模型，转换成form
     * @param dto 输入实体类对象
     * @param domainDictMap 字典 出参Dto的list
     * @param strategyDictMap 字典 出参Dto的map
     * @param resultList 结果集
     */
    public static void recursionModelTreeToForm(DomainDataModelTreeDto dto, Map<String, List<TestFormDictDto>> domainDictMap,
                                                Map<String, List<TestFormDictDto>> strategyDictMap, List<TestFormPathOutputDto> resultList) {

        //当前变量是否是数组
        if (DomainModelArrEnum.YES.getCode().equals(dto.getIsArr())) {
            //追加数组
            transferArray(dto, domainDictMap, strategyDictMap, resultList);

        } else if (dto.getValue().equals(CommonConstant.CUSTOM_FUNCTION_RETURN_NAME)
                || dto.getValue().equals(CommonConstant.COMMON_FUNCTION_RETURN_NAME) || dto.getValue().equals(CommonConstant.VARIABLE_RETURN_NAME)) {
            transferFunction(dto, resultList);
        } else {
            transferObject(dto, domainDictMap, strategyDictMap, resultList);

        }

    }

    /**
     * 处理对象
     * @param currentDto
     * @param domainDictMap
     * @param strategyDictMap
     * @param resultList
     */
    private static void transferObject(DomainDataModelTreeDto currentDto, Map<String, List<TestFormDictDto>> domainDictMap,
                                       Map<String, List<TestFormDictDto>> strategyDictMap, List<TestFormPathOutputDto> resultList) {
        List<DomainDataModelTreeDto> children = currentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        //排序
        Collections.sort(children, new Comparator<DomainDataModelTreeDto>() {

            //重写compare方法
            @Override
            public int compare(DomainDataModelTreeDto a, DomainDataModelTreeDto b) {

                String valA = a.getValue();
                String valB = b.getValue();

                return valA.compareTo(valB);

            }
        });

        List<TestFormPathOutputDto> list = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : children) {
            if (!DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(treeDto.getType())) {

                TestFormPathOutputDto testFormDto = new TestFormPathOutputDto();
                testFormDto.setIndex(treeDto.getValue());
                testFormDto.setName(treeDto.getName());
                testFormDto.setLabel(treeDto.getDescribe());
                testFormDto.setType(treeDto.getType());
                testFormDto.setIsArr(treeDto.getIsArr());
                testFormDto.setParameterType(treeDto.getParameterType());
                if (!StringUtils.isEmpty(treeDto.getIsParameterArray())) {
                    testFormDto.setIsParameterArray(Integer.parseInt(treeDto.getIsParameterArray()));
                }
                //数据字典
                if (!StringUtils.isEmpty(treeDto.getEnumName())) {
                    if (treeDto.getValue().startsWith(PositionVarEnum.ENGINE_VARS.getName())) {
                        testFormDto.setDictList(strategyDictMap.getOrDefault(treeDto.getEnumName(), new ArrayList<>()));

                    } else {
                        testFormDto.setDictList(domainDictMap.getOrDefault(treeDto.getEnumName(), new ArrayList<>()));
                    }
                }

                list.add(testFormDto);

            }
        }

        if (!CollectionUtils.isEmpty(list)) {
            TestFormPathOutputDto testFormOutputDto = new TestFormPathOutputDto();
            testFormOutputDto.setName(currentDto.getValue());
            testFormOutputDto.setLabel(currentDto.getDescribe());
            testFormOutputDto.setIsArr(currentDto.getIsArr());
            testFormOutputDto.setIndex(currentDto.getValue());
            testFormOutputDto.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            testFormOutputDto.setChildren(list);
            resultList.add(testFormOutputDto);
        }
        for (DomainDataModelTreeDto treeDto : children) {
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(treeDto.getType())) {
                if (DomainModelArrEnum.YES.getCode().equals(treeDto.getIsArr())) {
                    //追加数组
                    transferArray(treeDto, domainDictMap, strategyDictMap, resultList);
                } else {
                    //递归处理
                    recursionModelTreeToForm(treeDto, domainDictMap, strategyDictMap, resultList);
                }
            }
        }

    }

    /**
     * 自定义函数
     *
     * @param currentDto 入参
     * @param resultList 入参
     */
    private static void transferFunction(DomainDataModelTreeDto currentDto, List<TestFormPathOutputDto> resultList) {

        TestFormPathOutputDto testFormOutputDto = new TestFormPathOutputDto();
        testFormOutputDto.setName(currentDto.getValue());
        testFormOutputDto.setLabel(currentDto.getDescribe());
        testFormOutputDto.setIsArr(currentDto.getIsArr());
        testFormOutputDto.setIndex(currentDto.getValue());
        testFormOutputDto.setType(currentDto.getType());

        resultList.add(testFormOutputDto);

    }

    /**
     * 处理数组
     * @param currentDto
     * @param domainDictMap
     * @param strategyDictMap
     * @param resultList
     */
    private static void transferArray(DomainDataModelTreeDto currentDto, Map<String, List<TestFormDictDto>> domainDictMap,
                                      Map<String, List<TestFormDictDto>> strategyDictMap, List<TestFormPathOutputDto> resultList) {
        List<DomainDataModelTreeDto> children = currentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        //排序
        Collections.sort(children, new Comparator<DomainDataModelTreeDto>() {

            //重写compare方法
            @Override
            public int compare(DomainDataModelTreeDto a, DomainDataModelTreeDto b) {

                String valA = a.getValue();
                String valB = b.getValue();

                return valA.compareTo(valB);

            }
        });

        List<TestFormPathOutputDto> list = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : children) {
            TestFormPathOutputDto testFormDto = new TestFormPathOutputDto();
            testFormDto.setIndex(treeDto.getValue());
            testFormDto.setName(treeDto.getName());
            testFormDto.setLabel(treeDto.getDescribe());
            testFormDto.setType(treeDto.getType());
            testFormDto.setIsArr(treeDto.getIsArr());
            testFormDto.setParameterType(treeDto.getParameterType());
            if (!StringUtils.isEmpty(treeDto.getIsParameterArray())) {
                testFormDto.setIsParameterArray(Integer.parseInt(treeDto.getIsParameterArray()));
            }
            //数据字典
            if (!StringUtils.isEmpty(treeDto.getEnumName())) {
                if (treeDto.getValue().startsWith(PositionVarEnum.ENGINE_VARS.getName())) {
                    testFormDto.setDictList(strategyDictMap.getOrDefault(treeDto.getEnumName(), new ArrayList<>()));

                } else {
                    testFormDto.setDictList(domainDictMap.getOrDefault(treeDto.getEnumName(), new ArrayList<>()));
                }
            }
            //当前变量是否是数组
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(treeDto.getType())) {
                List<TestFormPathOutputDto> childList = new ArrayList<>();
                recursionModelTreeToForm(treeDto, domainDictMap, strategyDictMap, childList);
                testFormDto.setChildren(childList);
            }

            list.add(testFormDto);
        }

        if (!CollectionUtils.isEmpty(list)) {
            TestFormPathOutputDto testFormOutputDto = new TestFormPathOutputDto();
            testFormOutputDto.setName(currentDto.getValue());
            testFormOutputDto.setLabel(currentDto.getDescribe());
            testFormOutputDto.setIsArr(currentDto.getIsArr());
            testFormOutputDto.setIndex(currentDto.getValue());
            testFormOutputDto.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            testFormOutputDto.setChildren(list);
            resultList.add(testFormOutputDto);
        }
    }

    /**
     * 预期结果转换表单数据结构（用于在线表单填写）
     *
     * @param jsonObject 入参
     * @param output 入参
     * @param engineVars 入参
     * @param domainDictMap 入参
     * @param strategyDictMap 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferExpectToForm(JSONObject jsonObject, String output, String engineVars,
                                                                   Map<String, List<TestFormDictDto>> domainDictMap,
                                                                   Map<String, List<TestFormDictDto>> strategyDictMap) {

        List<TestFormPathOutputDto> resultList = new ArrayList<>();
        //output
        DomainDataModelTreeDto originalOutputModelTree = DomainModelTreeEntityUtils.getDomainModelTree(output);

        //预期结果用到的所有key
        Set<String> expectIncluleKeyList = getExpectIndexKey(jsonObject);

        if (expectIncluleKeyList.contains(originalOutputModelTree.getValue())) {
            DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();

            resetModelTree(originalOutputModelTree, targetModelTree, expectIncluleKeyList);

            recursionModelTreeToForm(targetModelTree, domainDictMap, strategyDictMap, resultList);
        }
        //engineVars
        if (!StringUtils.isEmpty(engineVars)) {
            DomainDataModelTreeDto originalEngineVarsModelTree = DomainModelTreeEntityUtils.getDomainModelTree(engineVars);

            if (expectIncluleKeyList.contains(originalEngineVarsModelTree.getValue())) {
                DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();

                resetModelTree(originalEngineVarsModelTree, targetModelTree, expectIncluleKeyList);

                recursionModelTreeToForm(targetModelTree, domainDictMap, strategyDictMap, resultList);
            }
        }

        return resultList;

    }

    /**
     * 组装数据模型，只要包含的属性
     *
     * @param originalModelTree 原始的模型树
     * @param targetModelTree 目标的模型树
     * @param incluleKeyList 包含的key的列表
     */
    public static void resetModelTree(DomainDataModelTreeDto originalModelTree, DomainDataModelTreeDto targetModelTree, Set<String> incluleKeyList) {

        BeanCopyUtils.copy(originalModelTree, targetModelTree);

        List<DomainDataModelTreeDto> children = originalModelTree.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        List<DomainDataModelTreeDto> targetChildren = new ArrayList<>();
        targetModelTree.setChildren(targetChildren);
        for (DomainDataModelTreeDto treeDto : children) {
            if (!incluleKeyList.contains(treeDto.getValue())) {
                continue;
            }
            DomainDataModelTreeDto targetChildModelTree = new DomainDataModelTreeDto();
            resetModelTree(treeDto, targetChildModelTree, incluleKeyList);
            targetChildren.add(targetChildModelTree);

        }

    }

    /**
     * 预期结果转换表单数据结构（用于在线表单填写）
     *
     * @param jsonObject json对象
     * @param outputContent 入参
     * @param engineVarsContent 入参
     * @return List
     */
    public static List<String> transferStrategyExpectToStringList(JSONObject jsonObject, String outputContent, String engineVarsContent) {

        Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelMap(null, outputContent, engineVarsContent, null,
                null, null, null);
        Set<String> list = new HashSet<>();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                if (subJsonObject.getString("type").equals(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    continue;
                }
                String index = jsonArray.getJSONObject(i).getString("index");
                String name = index.substring(index.indexOf(".") + 1);
                if (!dataModel.containsKey(name)) {
                    continue;
                }
                list.add(name);

            }

        }

        return new ArrayList<>(list);
    }

    /**
     * 预期结果转换表单数据结构（用于在线表单填写）
     *
     * @param formList 测试TestForm对象
     * @return List
     */
    public static List<String> transferFormToStringList(List<TestFormDto> formList) {

        Set<String> list = new HashSet<>();

        if (CollectionUtils.isEmpty(formList)) {
            return new ArrayList<>();
        }

        for (TestFormDto formDto : formList) {
            list.add(formDto.getName());
        }
        return new ArrayList<>(list);
    }

    /**
     * 预期结果转换表单数据结构（用于在线表单填写）
     *
     * @param jsonObject json对象
     * @return List
     */
    public static List<String> transferExpectToStringList(JSONObject jsonObject) {

        Set<String> list = new HashSet<>();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {

            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                if (subJsonObject.getString("type").equals(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    continue;
                }
                String index = jsonArray.getJSONObject(i).getString("index");
                list.add(index.substring(index.indexOf(".") + 1));

            }

        }

        return new ArrayList<>(list);

    }

    /**
     * 获取预期结果所有的key
     *
     * @param jsonObject
     * @return Set
     */
    private static Set<String> getExpectIndexKey(JSONObject jsonObject) {
        Set<String> list = new HashSet<>();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {

            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {

                list.addAll(splitExpectKey(jsonArray.getJSONObject(i)));

            }

        }

        return list;

    }

    /**
     * 拆分预期结果key
     *
     * @param subJsonObject 入参
     * @return List
     */
    private static List<String> splitExpectKey(JSONObject subJsonObject) {
        List<String> list = new ArrayList<>();
        String index = subJsonObject.getString("index");
        index = index.substring(index.indexOf(".") + 1);

        String[] split = index.split("\\.");
        String parentKey = "";
        for (int i = 0; i < split.length; i++) {
            String currentKey = null;
            if (StringUtils.isEmpty(parentKey)) {
                currentKey = split[i];

            } else {
                currentKey = parentKey + "." + split[i];

            }
            parentKey = currentKey;

            list.add(currentKey);
        }

        return list;

    }

    /**
     * 将变量树转换成表单数据结构
     *
     * @param content 入参
     * @param includeVarList 入参
     * @param domainDictMap 入参
     * @param strategyDictMap 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferModelTreeToFormByInclude(String content, List<String> includeVarList,
                                                                               Map<String, List<TestFormDictDto>> domainDictMap,
                                                                               Map<String, List<TestFormDictDto>> strategyDictMap) {
        List<TestFormPathOutputDto> resultList = new ArrayList<>();

        DomainDataModelTreeDto originalOutputModelTree = DomainModelTreeEntityUtils.getDomainModelTree(content);

        Set<String> incluleKeyList = splitVarPathKey(includeVarList);

        if (incluleKeyList.contains(originalOutputModelTree.getValue())) {
            DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();

            resetModelTree(originalOutputModelTree, targetModelTree, incluleKeyList);

            recursionModelTreeToForm(targetModelTree, domainDictMap, strategyDictMap, resultList);
        }

        return resultList;
    }

    /**
     * 拆分变量
     *
     * @param varPathList 入参
     * @return Set
     */
    public static Set<String> splitVarPathKey(List<String> varPathList) {
        Set<String> list = new HashSet<>();

        for (String index : varPathList) {

            String[] split = index.split("\\.");
            String parentKey = "";
            for (int i = 0; i < split.length; i++) {
                String currentKey = null;
                if (StringUtils.isEmpty(parentKey)) {
                    currentKey = split[i];

                } else {
                    currentKey = parentKey + "." + split[i];

                }
                parentKey = currentKey;

                list.add(currentKey);
            }
        }

        return list;

    }

    /**
     * 数据模型转为formData数据结构（用于Excel模板下载）
     *
     * @param modelTreeDtos 模型树 DTO
     * @return List
     */
    public static List<TestFormDto> transferModelTreeToFromData(List<DomainDataModelTreeDto> modelTreeDtos) {

        List<TestFormDto> formData = new ArrayList<>();

        for (DomainDataModelTreeDto dto : modelTreeDtos) {
            recursionModelTreePath(dto, formData);
        }

        return formData;
    }

    /**
     * 递归处理数据模型转formData
     *
     * @param dto
     * @param formData
     */
    private static void recursionModelTreePath(DomainDataModelTreeDto dto, List<TestFormDto> formData) {
        List<DomainDataModelTreeDto> children = dto.getChildren();

        if (CollectionUtils.isEmpty(children)) {
            if (!dto.getType().equals(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {

                TestFormDto testFormDto = new TestFormDto();
                testFormDto.setName(dto.getValue());
                testFormDto.setLabel(dto.getDescribe());

                testFormDto.setType(dto.getType());
                testFormDto.setIsArr(Integer.parseInt(dto.getIsArr()));
                testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                formData.add(testFormDto);
            }

            return;
        }
        for (DomainDataModelTreeDto treeDto : children) {
            recursionModelTreePath(treeDto, formData);
        }

    }

    /**
     * 将数据模型按对象分割转成为对象结构（用于规则配置）
     *
     * @param content 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferModelTreeToRuleConfigObject(String content) {

        DomainDataModelTreeDto domainModelTree = DomainModelTreeEntityUtils.getDomainModelTree(content);

        List<TestFormPathOutputDto> resultList = new ArrayList<>();

        recursionModelTreeToRuleConfigObject(domainModelTree, resultList);

        return resultList;
    }

    /**
     * 递归处理，将数据模型按对象分割转成对象结构
     *
     * @param currentDto 当前的输入实体类对象
     * @param resultList 结果集
     */
    public static void recursionModelTreeToRuleConfigObject(DomainDataModelTreeDto currentDto, List<TestFormPathOutputDto> resultList) {
        List<DomainDataModelTreeDto> children = currentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        //排序
        Collections.sort(children, new Comparator<DomainDataModelTreeDto>() {

            //重写compare方法
            @Override
            public int compare(DomainDataModelTreeDto a, DomainDataModelTreeDto b) {

                String valA = a.getValue();
                String valB = b.getValue();

                return valA.compareTo(valB);

            }
        });

        List<TestFormPathOutputDto> list = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : children) {
            if (!DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(treeDto.getType())) {

                TestFormPathOutputDto testFormDto = new TestFormPathOutputDto();
                testFormDto.setIndex(treeDto.getValue());
                testFormDto.setName(treeDto.getName());
                testFormDto.setLabel(treeDto.getDescribe());
                testFormDto.setType(treeDto.getType());
                testFormDto.setIsArr(treeDto.getIsArr());
                testFormDto.setParameterType(treeDto.getParameterType());
                if (!StringUtils.isEmpty(treeDto.getIsParameterArray())) {
                    testFormDto.setIsParameterArray(Integer.parseInt(treeDto.getIsParameterArray()));
                }
                list.add(testFormDto);

            }
        }

        if (!CollectionUtils.isEmpty(list)) {

            TestFormPathOutputDto testFormOutputDto = new TestFormPathOutputDto();
            testFormOutputDto.setName(currentDto.getValue());
            testFormOutputDto.setLabel(currentDto.getDescribe());
            testFormOutputDto.setIsArr(currentDto.getIsArr());
            testFormOutputDto.setIndex(currentDto.getValue());
            testFormOutputDto.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            testFormOutputDto.setChildren(list);
            resultList.add(testFormOutputDto);
        }

        for (DomainDataModelTreeDto treeDto : children) {
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(treeDto.getType())) {
                recursionModelTreeToRuleConfigObject(treeDto, resultList);

            }
        }

    }

    //==========================组件测试==============================//

    /**
     * 获取组件测试的数据模型变量树
     *
     * @param formDtoList 测试TestForm对象
     * @param modelVarsMap 入参
     * @return List
     */
    public static List<DomainDataModelTreeDto> transferComVarsToModelTreeDto(List<TestFormDto> formDtoList, Map<String, String> modelVarsMap) {
        if (CollectionUtils.isEmpty(formDtoList)) {
            return new ArrayList<>();
        }
        TestFormCategoryDto groupFormDto = getComVarsGroupFormDto(formDtoList);

        Map<String, DomainDataModelTreeDto> modelTreeDtoMap = getModelTreeMap(modelVarsMap);

        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();

        //输入
        getModelTreeList(groupFormDto.getInputList(), modelTreeDtoMap, PositionVarEnum.INPUT.getName(), treeDtoList);

        //原始数据
        getModelTreeList(groupFormDto.getRawDataList(), modelTreeDtoMap, PositionVarEnum.RAW_DATA.getName(), treeDtoList);

        //衍生变量
        getModelTreeList(groupFormDto.getVarsList(), modelTreeDtoMap, PositionVarEnum.VARS.getName(), treeDtoList);

        //输出
        getModelTreeList(groupFormDto.getOutputList(), modelTreeDtoMap, PositionVarEnum.OUTPUT.getName(), treeDtoList);

        //引擎变量
        getModelTreeList(groupFormDto.getEngineVarsList(), modelTreeDtoMap, PositionVarEnum.ENGINE_VARS.getName(), treeDtoList);

        //外部数据
        getModelTreeList(groupFormDto.getExternalDataList(), modelTreeDtoMap, PositionVarEnum.EXTERNAL_DATA.getName(), treeDtoList);

        //公共服务
        getModelTreeList(groupFormDto.getCommonDataList(), modelTreeDtoMap, PositionVarEnum.COMMON_DATA.getName(), treeDtoList);

        // blaze服务
        getModelTreeList(groupFormDto.getBlazeDataList(), modelTreeDtoMap, PositionVarEnum.BLAZE_DATA.getName(), treeDtoList);

        // 实时服务
        getModelTreeList(groupFormDto.getExternalVarsList(), modelTreeDtoMap, PositionVarEnum.EXTERNAL_VARS.getName(), treeDtoList);

        //参数-基础类型
        getModelTreeByBaseList(groupFormDto.getBaseParamList(), PositionVarEnum.PARAMETERS.getName(), "参数", treeDtoList);

        //本地变量-基础类型
        getModelTreeByBaseList(groupFormDto.getBaseLocalList(), PositionVarEnum.LOCAL_VARS.getName(), "本地变量", treeDtoList);

        //参数-引用类型
        getModelTreeByRefList(groupFormDto.getRefParamList(), modelTreeDtoMap, PositionVarEnum.PARAMETERS.getName(), "参数", treeDtoList);
        //本地变量-引用类型
        getModelTreeByRefList(groupFormDto.getRefLocalList(), modelTreeDtoMap, PositionVarEnum.LOCAL_VARS.getName(), "本地变量", treeDtoList);

        //自定义函数
        getModelTreeFunction(groupFormDto.getFunctionReturnList(), treeDtoList);

        return treeDtoList;

    }

    /**
     * 输入、输出、引擎变量、外部数据变量、公共决策变量转变量树
     *
     * @param formDtoList
     * @param modelTreeDtoMap
     * @param type
     * @param treeDtoList
     */
    private static void getModelTreeList(List<TestFormDto> formDtoList, Map<String, DomainDataModelTreeDto> modelTreeDtoMap, String type, List<DomainDataModelTreeDto> treeDtoList) {
        if (CollectionUtils.isEmpty(formDtoList) || !modelTreeDtoMap.containsKey(type)) {
            return;
        }
        DomainDataModelTreeDto originalModelTree = modelTreeDtoMap.get(type);
        List<String> includeVarList = formDtoList.stream().map(TestFormDto::getName).collect(Collectors.toList());
        Set<String> includeSetList = splitVarPathKey(includeVarList);

        DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();
        resetModelTreeByInclude(originalModelTree, targetModelTree, includeSetList);
        treeDtoList.add(targetModelTree);
    }

    private static void resetModelTreeByInclude(DomainDataModelTreeDto originalModelTree, DomainDataModelTreeDto targetModelTree,
                                                Set<String> includeVarList) {

        BeanCopyUtils.copy(originalModelTree, targetModelTree);

        List<DomainDataModelTreeDto> children = originalModelTree.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        List<DomainDataModelTreeDto> targetChildren = new ArrayList<>();
        targetModelTree.setChildren(targetChildren);
        for (DomainDataModelTreeDto treeDto : children) {
            if (!includeVarList.contains(treeDto.getValue())) {
                continue;
            }
            DomainDataModelTreeDto targetChildModelTree = new DomainDataModelTreeDto();
            resetModelTreeByInclude(treeDto, targetChildModelTree, includeVarList);
            targetChildren.add(targetChildModelTree);

        }

    }

    /**
     * 参数或本地变量基本类型转变量树
     *
     * @param formDtoList
     * @param name
     * @param describe
     * @param treeDtoList
     */
    private static void getModelTreeByBaseList(List<TestFormDto> formDtoList, String name, String describe, List<DomainDataModelTreeDto> treeDtoList) {
        if (CollectionUtils.isEmpty(formDtoList)) {
            return;
        }
        DomainDataModelTreeDto originalModelTree = new DomainDataModelTreeDto();
        originalModelTree.setName(name);
        originalModelTree.setDescribe(describe);
        originalModelTree.setIsArr("0");
        originalModelTree.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        originalModelTree.setValue(name);
        originalModelTree.setLabel(name + "-" + describe);

        // PositionVarEnum.PARAMETERS.getName(), "参数"
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (TestFormDto formDto : formDtoList) {
            DomainDataModelTreeDto modelTreeDto = new DomainDataModelTreeDto();
            String substring = formDto.getName().substring(formDto.getName().indexOf(".") + 1);
            modelTreeDto.setName(substring);
            modelTreeDto.setDescribe(formDto.getLabel());
            modelTreeDto.setIsArr(String.valueOf(formDto.getIsArr()));
            modelTreeDto.setType(formDto.getType());
            modelTreeDto.setValue(formDto.getName());
            modelTreeDto.setLabel(substring + "-" + formDto.getLabel());
            modelTreeDto.setParameterLabel(formDto.getParameterLabel());
            modelTreeDto.setParameterType(formDto.getParameterType());
            modelTreeDto.setIsParameterArray(String.valueOf(formDto.getIsParameterArray()));

            children.add(modelTreeDto);
        }
        originalModelTree.setChildren(children);

        treeDtoList.add(originalModelTree);
    }

    /**
     * 参数或本地变量引用类型转变量树
     *
     * @param formDtoList
     * @param modelTreeDtoMap
     * @param name
     * @param describe
     * @param treeDtoList
     */
    private static void getModelTreeByRefList(List<TestFormDto> formDtoList, Map<String, DomainDataModelTreeDto> modelTreeDtoMap, String name, String describe, List<DomainDataModelTreeDto> treeDtoList) {
        if (CollectionUtils.isEmpty(formDtoList)) {
            return;
        }

        Map<String, List<TestFormDto>> dataListMap = formDtoList.stream().collect(Collectors.groupingBy(item -> {
            String[] param = item.getName().split("\\.");
            return MessageFormat.format("{0}.{1}", param[0], param[1]);

        }, HashMap::new, Collectors.toList()));

        Set<Map.Entry<String, List<TestFormDto>>> entries = dataListMap.entrySet();

        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (Map.Entry<String, List<TestFormDto>> entry : entries) {

            List<TestFormDto> valueList = entry.getValue();
            List<String> includeVarList = new ArrayList<>();

            for (TestFormDto testFormDto : valueList) {

                String[] param = testFormDto.getName().split("\\.");
                String paramKey = MessageFormat.format("{0}.{1}", param[0], param[1]);

                includeVarList.add(testFormDto.getName().replaceFirst(paramKey, testFormDto.getParameterType()));
            }
            String parameterLabel = valueList.get(0).getParameterLabel();
            String parameterType = valueList.get(0).getParameterType();
            int isParameterArray = valueList.get(0).getIsParameterArray();
            if (StringUtils.isEmpty(parameterType)) {
                continue;
            }
            DomainDataModelTreeDto treeDto = null;
            if (parameterType.contains(".")) {
                treeDto = modelTreeDtoMap.get(parameterType.substring(0, parameterType.indexOf(".")));
            } else {
                treeDto = modelTreeDtoMap.get(parameterType);
            }

            DomainDataModelTreeDto targetResetModelTree = getTargetResetModelTree(treeDto, entry.getKey(), parameterLabel, parameterType, isParameterArray, includeVarList);

            children.add(targetResetModelTree);
        }

        boolean isExistName = false;
        if (!CollectionUtils.isEmpty(treeDtoList)) {

            for (DomainDataModelTreeDto treeDto : treeDtoList) {
                if (name.equals(treeDto.getName())) {
                    treeDto.getChildren().addAll(children);
                    isExistName = true;
                }
            }
        }

        if (!isExistName) {
            DomainDataModelTreeDto originalModelTree = new DomainDataModelTreeDto();
            originalModelTree.setName(name);
            originalModelTree.setDescribe(describe);
            originalModelTree.setIsArr("0");
            originalModelTree.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            originalModelTree.setValue(name);
            originalModelTree.setLabel(name + "-" + describe);
            originalModelTree.setChildren(children);
            treeDtoList.add(originalModelTree);
        }


    }

    /**
     * 自定义函数
     *
     * @param formDtoList
     * @param treeDtoList
     */
    private static void getModelTreeFunction(List<TestFormDto> formDtoList, List<DomainDataModelTreeDto> treeDtoList) {
        if (CollectionUtils.isEmpty(formDtoList)) {
            return;
        }

        for (TestFormDto formDto : formDtoList) {
            DomainDataModelTreeDto originalModelTree = new DomainDataModelTreeDto();
            originalModelTree.setName(formDto.getName());
            originalModelTree.setDescribe(formDto.getLabel());
            originalModelTree.setIsArr(String.valueOf(formDto.getIsArr()));
            originalModelTree.setType(formDto.getType());
            originalModelTree.setValue(formDto.getName());
            originalModelTree.setLabel(formDto.getName() + "-" + formDto.getLabel());

            treeDtoList.add(originalModelTree);
        }
    }

    /**
     * 将数据模型按对象、数组分割转成为前端表单结构(用于在线表单填写)
     *
     * @param formDtoList 测试TestForm对象
     * @param modelVarsMap 入参
     * @param domainDictMap 入参
     * @param strategyDictMap 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferComVarsToFrom(List<TestFormDto> formDtoList, Map<String, String> modelVarsMap,
                                                                    Map<String, List<TestFormDictDto>> domainDictMap,
                                                                    Map<String, List<TestFormDictDto>> strategyDictMap) {
        if (CollectionUtils.isEmpty(formDtoList)) {
            return new ArrayList<>();
        }
        List<DomainDataModelTreeDto> modelTreeDtoList = transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
        List<TestFormPathOutputDto> resultList = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : modelTreeDtoList) {
            recursionModelTreeToForm(treeDto, domainDictMap, strategyDictMap, resultList);

        }
        return resultList;
    }

    /**
     * 输入、输出、引擎变量、外部数据变量、公共决策变量 数据模型
     *
     * @param modelVarsMap
     * @return java.util.Map<java.lang.String, com.decision.jsonschema.util.dto.DomainDataModelTreeDto>
     */
    private static Map<String, DomainDataModelTreeDto> getModelTreeMap(Map<String, String> modelVarsMap) {

        Map<String, DomainDataModelTreeDto> map = new LinkedHashMap<>();

        Set<Map.Entry<String, String>> modelVarsEntries = modelVarsMap.entrySet();
        for (Map.Entry<String, String> modelVarsEntry : modelVarsEntries) {
            if (StringUtils.isEmpty(modelVarsEntry.getValue())) {
                continue;
            }
            map.put(modelVarsEntry.getKey(), DomainModelTreeEntityUtils.getDomainModelTree(modelVarsEntry.getValue()));

        }

        return map;

    }

    private static DomainDataModelTreeDto getTargetResetModelTree(DomainDataModelTreeDto originalOutputModelTree, String paramKey,
                                                                  String parameterLabel, String parameterType, int isParameterArray,
                                                                  List<String> includeVarList) {
        Set<String> incluleKeyList = splitVarPathKey(includeVarList);

        //找出对应的数据模型
        DomainDataModelTreeDto originalModelTree = new DomainDataModelTreeDto();
        DomainModelTreeEntityUtils.getDomainModelTreeByValue(originalOutputModelTree, parameterType, originalModelTree);

        //替换数据模型前缀为参数前缀
        DomainDataModelTreeDto targetResetModelTree = new DomainDataModelTreeDto();

        resetRefParamModelTree(originalModelTree, targetResetModelTree, incluleKeyList, paramKey, parameterLabel, parameterType, isParameterArray);
        return targetResetModelTree;
    }

    private static void resetRefParamModelTree(DomainDataModelTreeDto originalModelTree, DomainDataModelTreeDto targetModelTree,
                                               Set<String> incluleKeyList, String paramKey, String parameterLabel, String parameterType,
                                               int isParameterArray) {

        if (originalModelTree.getValue().equals(parameterType)) {
            originalModelTree.setIsArr(String.valueOf(isParameterArray));
            String[] param = paramKey.split("\\.");
            originalModelTree.setName(param[1]);

            originalModelTree.setLabel(param[1] + "-" + parameterLabel);

            originalModelTree.setDescribe(parameterLabel);
        }
        originalModelTree.setValue(originalModelTree.getValue().replaceFirst(parameterType, paramKey));
        originalModelTree.setParameterLabel(parameterLabel);
        originalModelTree.setParameterType(parameterType);
        originalModelTree.setIsParameterArray(String.valueOf(isParameterArray));
        BeanCopyUtils.copy(originalModelTree, targetModelTree);

        List<DomainDataModelTreeDto> children = originalModelTree.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        List<DomainDataModelTreeDto> targetChildren = new ArrayList<>();
        targetModelTree.setChildren(targetChildren);
        for (DomainDataModelTreeDto treeDto : children) {
            if (!incluleKeyList.contains(treeDto.getValue())) {
                continue;
            }
            DomainDataModelTreeDto targetChildModelTree = new DomainDataModelTreeDto();
            resetRefParamModelTree(treeDto, targetChildModelTree, incluleKeyList, paramKey, parameterLabel, parameterType, isParameterArray);
            targetChildren.add(targetChildModelTree);

        }

    }

    /**
     * 查找指定数据模型并按对象、数组分割转成为前端表单结构(用于在线表单填写)
     *
     * @param formDtoList
     * @param modelVarsMap
     * @param fullVarPath
     * @return
     */
    /*
    public static List<TestFormPathOutputDto> transferComponentModelTreeToFromByValue(List<TestFormDto> formDtoList, Map<String, String> modelVarsMap, String fullVarPath) {

     List<DomainDataModelTreeDto> modelTreeDtoList = transferComVarsToModelTreeDto(formDtoList, modelVarsMap);

     DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();
     for (DomainDataModelTreeDto originalModelTree : modelTreeDtoList) {

         DomainModelTreeEntityUtils.getDomainModelTreeByValue(originalModelTree, fullVarPath, targetModelTree);
         if (!StringUtils.isEmpty(targetModelTree.getName())) {
             break;
         }
     }
     List<TestFormPathOutputDto> resultList = new ArrayList<>();
     recursionModelTreeToForm(targetModelTree, resultList);
     return resultList;
    }*/

    /**
     * 预期结果转换表单数据结构-组件测试
     *
     * @param outputFormDtoList 测试TestForm对象
     * @param jsonObject json对象
     * @return List
     */
    public static List<TestFormDto> transferComponentExpectToForm(List<TestFormDto> outputFormDtoList, JSONObject jsonObject) {
        List<TestFormDto> formDtoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(outputFormDtoList)) {
            return formDtoList;
        }
        List<String> expectList = transferExpectToStringList(jsonObject);

        for (TestFormDto dto : outputFormDtoList) {
            if (expectList.contains(dto.getName())) {
                formDtoList.add(dto);
            }
        }

        return formDtoList;

    }

    /**
     * 变量树转换成表单字段结构
     *
     * @param formDtoList 测试TestForm对象
     * @param modelVarsMap 入参
     * @param includeVarList 入参
     * @param domainDictMap 入参
     * @param strategyDictMap 入参
     * @return List
     */
    public static List<TestFormPathOutputDto> transferComponentModelTreeToFormByInclude(List<TestFormDto> formDtoList,
                                                                                        Map<String, String> modelVarsMap,
                                                                                        List<String> includeVarList,
                                                                                        Map<String, List<TestFormDictDto>> domainDictMap,
                                                                                        Map<String, List<TestFormDictDto>> strategyDictMap) {
        List<DomainDataModelTreeDto> treeDtoList = transferComVarsToModelTreeDto(formDtoList, modelVarsMap);

        List<TestFormPathOutputDto> resultList = new ArrayList<>();
        for (DomainDataModelTreeDto originalModelTree : treeDtoList) {

            Set<String> incluleKeyList = splitVarPathKey(includeVarList);

            if (!incluleKeyList.contains(originalModelTree.getValue())) {
                continue;
            }
            DomainDataModelTreeDto targetModelTree = new DomainDataModelTreeDto();

            resetModelTree(originalModelTree, targetModelTree, incluleKeyList);

            recursionModelTreeToForm(targetModelTree, domainDictMap, strategyDictMap, resultList);
        }

        return resultList;
    }

    /**
     * 将组件用到的变量分组
     *
     * @param formDtoList
     * @return com.wiseco.var.process.app.server.commons.test.dto.TestFormCategoryDto
     */
    private static TestFormCategoryDto getComVarsGroupFormDto(List<TestFormDto> formDtoList) {
        TestFormCategoryDto dto = new TestFormCategoryDto();
        //用到的组件变量进行分组
        //数据模型：input/output/engineVars/externalData/commonData
        List<TestFormDto> inputDtoList = new ArrayList<>();
        List<TestFormDto> rawDataDtoList = new ArrayList<>();
        List<TestFormDto> outputDtoList = new ArrayList<>();
        List<TestFormDto> engineVarsDtoList = new ArrayList<>();
        List<TestFormDto> externalDataDtoList = new ArrayList<>();
        List<TestFormDto> commonDataDtoList = new ArrayList<>();
        List<TestFormDto> blazeDataDtoList = new ArrayList<>();
        List<TestFormDto> externalVarsDtoList = new ArrayList<>();
        List<TestFormDto> varsDtoList = new ArrayList<>();
        //参数parameters、localVars 基础类型
        List<TestFormDto> baseParamDtoList = new ArrayList<>();
        List<TestFormDto> baseLocalDtoList = new ArrayList<>();

        //参数parameters、localVars 引用类型
        List<TestFormDto> refParamDtoList = new ArrayList<>();
        List<TestFormDto> refLocalDtoList = new ArrayList<>();

        //自定义函数
        List<TestFormDto> functionReturnList = new ArrayList<>();
        for (TestFormDto formDto : formDtoList) {
            if (formDto.getName().startsWith(PositionVarEnum.INPUT.getName())) {
                inputDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.RAW_DATA.getName())) {
                rawDataDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.OUTPUT.getName())) {
                outputDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.ENGINE_VARS.getName())) {
                engineVarsDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.EXTERNAL_DATA.getName())) {
                externalDataDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.COMMON_DATA.getName())) {
                commonDataDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.BLAZE_DATA.getName())) {
                blazeDataDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.EXTERNAL_VARS.getName())) {
                externalVarsDtoList.add(formDto);
            } else if (formDto.getName().startsWith(PositionVarEnum.VARS.getName())) {
                varsDtoList.add(formDto);
            } else if (formDto.getName().equals(CommonConstant.CUSTOM_FUNCTION_RETURN_NAME)
                    || formDto.getName().equals(CommonConstant.COMMON_FUNCTION_RETURN_NAME)
                    || formDto.getName().equals(CommonConstant.VARIABLE_RETURN_NAME)) {
                functionReturnList.add(formDto);
            } else {
                DataVariableBasicTypeEnum nameEnum = DataVariableBasicTypeEnum.getNameEnum(formDto.getParameterType());
                if (nameEnum == null) {
                    if (formDto.getName().startsWith(PositionVarEnum.PARAMETERS.getName())) {
                        refParamDtoList.add(formDto);
                    } else {
                        refLocalDtoList.add(formDto);
                    }
                } else {
                    if (formDto.getName().startsWith(PositionVarEnum.PARAMETERS.getName())) {
                        baseParamDtoList.add(formDto);
                    } else {
                        baseLocalDtoList.add(formDto);
                    }
                }
            }
        }
        dto.setInputList(inputDtoList);
        dto.setRawDataList(rawDataDtoList);
        dto.setOutputList(outputDtoList);
        dto.setEngineVarsList(engineVarsDtoList);
        dto.setExternalDataList(externalDataDtoList);
        dto.setCommonDataList(commonDataDtoList);
        dto.setBlazeDataList(blazeDataDtoList);
        dto.setExternalVarsList(externalVarsDtoList);
        dto.setVarsList(varsDtoList);
        dto.setBaseParamList(baseParamDtoList);
        dto.setBaseLocalList(baseLocalDtoList);
        dto.setRefParamList(refParamDtoList);
        dto.setRefLocalList(refLocalDtoList);
        dto.setFunctionReturnList(functionReturnList);
        return dto;
    }


    /**
     * 转换预期结果表头
     *
     * @param dataModelTreeDtoList 领域数据模型树
     * @param includeVarList 变量列表
     * @return List
     */
    public static List<TestFormDto> transferFormExpectHeader(List<DomainDataModelTreeDto> dataModelTreeDtoList, List<String> includeVarList) {
        if (CollectionUtils.isEmpty(dataModelTreeDtoList)) {
            return new ArrayList<>();
        }
        Set<String> includeList = splitVarPathKey(includeVarList);

        List<DomainDataModelTreeDto> modelTreeList = new ArrayList<>();
        for (DomainDataModelTreeDto treeDto : dataModelTreeDtoList) {
            DomainDataModelTreeDto modelTreeByInclude = DomainModelTreeEntityUtils.getModelTreeByInclude(treeDto, includeList);
            if (modelTreeByInclude != null) {
                modelTreeList.add(modelTreeByInclude);
            }

        }

        List<TestFormDto> list = new ArrayList<>();

        for (DomainDataModelTreeDto domainDataModelTreeDto : modelTreeList) {
            modelToFormData(domainDataModelTreeDto, list);
        }

        return list;
    }

    private static void modelToFormData(DomainDataModelTreeDto domainDataModelTreeDto, List<TestFormDto> list) {
        if (!domainDataModelTreeDto.getType().equals(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
            TestFormDto testFormDto = new TestFormDto();

            testFormDto.setIsArr(Integer.parseInt(domainDataModelTreeDto.getIsArr()));
            testFormDto.setType(domainDataModelTreeDto.getType());
            testFormDto.setLabel(domainDataModelTreeDto.getDescribe());
            testFormDto.setName(domainDataModelTreeDto.getValue());
            testFormDto.setParameterType(domainDataModelTreeDto.getParameterType());
            Integer isParameterArray = StringUtils.isEmpty(domainDataModelTreeDto.getIsParameterArray()) ? 0 : Integer
                    .parseInt(domainDataModelTreeDto.getIsParameterArray());
            testFormDto.setIsParameterArray(isParameterArray);
            testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));
            list.add(testFormDto);
        }
        if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            return;
        }
        List<DomainDataModelTreeDto> children = domainDataModelTreeDto.getChildren();
        for (DomainDataModelTreeDto treeDto : children) {
            modelToFormData(treeDto, list);
        }
    }

    /**
     * 合并输入数据和预期结果数据
     *
     * @param inputJsonData 输入JSON数据
     * @param expectJsonData 期望的JSON数据
     * @param id id
     * @return JSONObject
     */
    public static JSONObject mergeFormData(JSONObject inputJsonData, JSONObject expectJsonData, String id) {

        JSONObject resultJson = new JSONObject();
        if (inputJsonData != null && inputJsonData.size() > 0) {
            inputJsonData = JSON.parseObject(inputJsonData.toJSONString());
            inputJsonData = resetFormData(inputJsonData, id);
            resultJson.put(TestTableEnum.INPUT.getCode(), inputJsonData);
        }

        if (expectJsonData != null && expectJsonData.size() > 0) {
            expectJsonData = JSON.parseObject(expectJsonData.toJSONString());
            expectJsonData = resetFormData(expectJsonData, id);

            resultJson.put(TestTableEnum.EXPECT.getCode(), expectJsonData);

        }

        return resultJson;
    }

    /**
     * 重置测试数据明细ID
     *
     * @param data
     * @param newId
     * @return JSONObject
     */
    private static JSONObject resetFormData(JSONObject data, String newId) {

        //清除空值
        TestExecuteUtil.removeJsonEmptyValue(data);
        //清除多余数据
        JSONObject targetData = removeUselessByFormData(data);
        //重置ID
        resetIdByFormData(targetData, newId);

        return targetData;

    }

    /**
     * 重置测试数据明细ID
     *
     * @param data
     * @param newId
     */
    private static void resetIdByFormData(JSONObject data, String newId) {

        Set<String> keySet = data.keySet();
        for (String key : keySet) {
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                JSONObject jsonObject = data.getJSONObject(key);
                jsonObject.put("id", newId);
            } else {
                JSONArray jsonArray = data.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String tmpSubString = id.substring(id.indexOf("_"));

                    jsonObject.put("id", newId + tmpSubString);

                    String parentId = jsonObject.getString(PARENT_ID);
                    if (parentId.contains("_")) {
                        tmpSubString = parentId.substring(parentId.indexOf("_"));
                        jsonObject.put(PARENT_ID, newId + tmpSubString);
                    } else {
                        jsonObject.put(PARENT_ID, newId);
                    }
                }
            }
        }

    }

    /**
     * 清除多余数据
     *
     * @param data
     * @return JSONObject
     */
    private static JSONObject removeUselessByFormData(JSONObject data) {
        JSONObject targetData = new JSONObject();

        if (!data.containsKey(TestTableEnum.MASTER.getCode())) {
            return targetData;
        }

        //处理master
        JSONObject masterJsonObject = removeUselessSingleJsonObject(data.getJSONObject(TestTableEnum.MASTER.getCode()), data);
        if (masterJsonObject.size() == 0) {
            return targetData;
        }
        targetData.put(TestTableEnum.MASTER.getCode(), masterJsonObject);

        //获取所有的数组key
        Set<String> arrayKeyByJsonObject = getArrayKeyByJsonObject(data);

        Set<String> keySet = data.keySet();
        for (String key : keySet) {
            if (key.equals(TestTableEnum.MASTER.getCode()) || !arrayKeyByJsonObject.contains(key)) {
                continue;
            }
            JSONArray targetJsonArray = new JSONArray();
            JSONArray jsonArray = data.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject targetJsonObject = removeUselessSingleJsonObject(jsonObject, data);
                if (targetJsonObject.size() > 0) {
                    targetJsonArray.add(targetJsonObject);
                }

            }
            if (targetJsonArray.size() > 0) {
                targetData.put(key, targetJsonArray);
            }
        }

        return targetData;
    }

    private static JSONObject removeUselessSingleJsonObject(JSONObject singleJsonObject, JSONObject origanlJsonObject) {
        JSONObject targetJsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : singleJsonObject.entrySet()) {
            if ("id".equals(entry.getKey()) || PARENT_ID.equals(entry.getKey())) {
                continue;
            }
            if ("...".equals(singleJsonObject.getString(entry.getKey())) && !origanlJsonObject.containsKey(entry.getKey())) {
                continue;
            }
            targetJsonObject.put(entry.getKey(), entry.getValue());
        }
        if (targetJsonObject.size() > 0) {
            targetJsonObject.put("id", singleJsonObject.get("id"));
            if (singleJsonObject.containsKey(PARENT_ID)) {
                targetJsonObject.put(PARENT_ID, singleJsonObject.get(PARENT_ID));
            }
        }
        return targetJsonObject;
    }

    private static Set<String> getArrayKeyByJsonObject(JSONObject origanlJsonObject) {
        Set<String> arrKeys = new HashSet<>();
        Set<String> keySet = origanlJsonObject.keySet();
        for (String key : keySet) {
            if (key.equals(TestTableEnum.MASTER.getCode())) {

                JSONObject subJsonObject = origanlJsonObject.getJSONObject(key);
                Set<String> subKeySet = subJsonObject.keySet();
                for (String subKey : subKeySet) {

                    if ("...".equals(subJsonObject.getString(subKey)) && origanlJsonObject.containsKey(subKey)) {
                        arrKeys.add(subKey);
                    }

                }

            } else {
                JSONArray subJsonArray = origanlJsonObject.getJSONArray(key);
                for (int i = 0; i < subJsonArray.size(); i++) {
                    JSONObject subJsonObject = subJsonArray.getJSONObject(i);
                    Set<String> subKeySet = subJsonObject.keySet();
                    for (String subKey : subKeySet) {

                        if ("...".equals(subJsonObject.getString(subKey)) && origanlJsonObject.containsKey(subKey)) {
                            arrKeys.add(subKey);
                        }

                    }

                }
            }
        }

        return arrKeys;
    }

}
