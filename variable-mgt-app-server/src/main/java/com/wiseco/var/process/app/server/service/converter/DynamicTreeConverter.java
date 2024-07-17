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
package com.wiseco.var.process.app.server.service.converter;

import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelTreeFindVarBeanNameEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态树的转换
 */
@Component
public class DynamicTreeConverter {

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private ModelTreeFindVarConverter modelTreeFindVarConverter;

    /**
     * 校验类型入参
     *
     * @param typeList 类型列表
     */
    public void checkTypeList(List<String> typeList) {
        for (String value : typeList) {
            if (null == DataVariableTypeEnum.getMessageEnum(value)) {
                List<String> typeListEnum = Arrays.stream(DataVariableTypeEnum.values()).map(m -> m.getMessage()).collect(Collectors.toList());
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类型入参只能是：" + typeListEnum + "！");
            }
        }
    }

    /**
     * 数组类型校验
     *
     * @param typeList 类型列表
     */
    public void checkTypeListArray(List<String> typeList) {
        if (!CollectionUtils.isEmpty(typeList)) {
            long count = typeList.stream()
                    .filter(f -> f.equals(DataVariableTypeEnum.ARRAY_TYPE.getMessage()))
                    .count();
            if (count > 0 && typeList.size() > 1) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类型入参如果包含array那就不能再包含其它类型！");
            }
        }
    }

    /**
     * 数组类型校验
     *
     * @param typeList 类型列表
     */
    public void checkTypeListObject(List<String> typeList) {
        if (!CollectionUtils.isEmpty(typeList)) {
            long count = typeList.stream()
                    .filter(f -> f.equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage()))
                    .count();
            if (count > 0 && typeList.size() > 1) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "类型入参如果包含object那就不能再包含其它类型！");
            }
        }
    }

    /**
     * 位置校验
     *
     * @param positionList 位置列表
     */
    public void checkPositionList(List<String> positionList) {
        for (String value : positionList) {
            if (null == DomainModelSheetNameEnum.getMessageEnum(value)) {
                List<String> positionListEnum = Arrays.stream(DomainModelSheetNameEnum.values()).map(m -> m.getMessage()).collect(Collectors.toList());
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "位置入参只能是：" + positionListEnum + "！");
            }
        }
    }

    /**
     * lastDataValueByType
     *
     * @param dataModelTreeDto 数据模型树
     * @param dataValue 数据的值
     * @return java.lang.String
     */
    public String lastDataValueByType(DomainDataModelTreeDto dataModelTreeDto, String dataValue) {
        if (dataValue.equals(dataModelTreeDto.getValue())) {
            return dataModelTreeDto.getType();
        }
        if (!CollectionUtils.isEmpty(dataModelTreeDto.getChildren())) {
            for (DomainDataModelTreeDto child : dataModelTreeDto.getChildren()) {
                String value = lastDataValueByType(child, dataValue);
                if (!StringUtils.isEmpty(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * findDsStrategyAllVar
     *
     * @param type 类型
     * @param strategyId 决策Id
     * @param spaceId 变量空间Id
     * @param positionList 位置列表
     * @return DomainDataModelTreeDto的列表(list)
     */
    public List<DomainDataModelTreeDto> findDsStrategyAllVar(TemplateUnitTypeEnum type, Long strategyId, Long spaceId, List<String> positionList) {
        List<DomainDataModelTreeDto> contentList = new ArrayList<>();
        //空间
        VarProcessSpace varProcessSpace = getVarProcessSpace(spaceId);
        boolean isInput = isArrayListByName(positionList, DomainModelSheetNameEnum.RAW_DATA.getMessage());

        if (isInput) {
            contentList.add(varProcessSpace.getInputData() == null ? null : DomainModelTreeEntityUtils.getByContentStaticTemplateTree(varProcessSpace
                    .getInputData()));
        }

        return contentList;
    }

    /**
     * findTreeByDataValue
     *
     * @param content 内容
     * @param dataValue 数据的值
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findTreeByDataValue(String content, String dataValue) {
        DomainDataModelTreeDto domainDataModelTreeDto = DomainModelTreeEntityUtils.getByContentStaticTemplateTree(content);

        //切割要寻找的路径
        String[] split = dataValue.split("\\.");
        //如果只有一个，说明只需要寻找一次
        if (split.length == 1) {
            domainDataModelTreeDto = findDomainModelTreeByValue(domainDataModelTreeDto, split[0], 1);
            return domainDataModelTreeDto;
        }
        //寻找深度大于1，需要寻找多次，按层次循环找，上一层找到的对象是下一层的入参
        for (int i = 0; i < split.length; i++) {
            domainDataModelTreeDto = findDomainModelTreeByValue(domainDataModelTreeDto, split[i], i + 1);
            if (null == domainDataModelTreeDto) {
                return null;
            }
        }
        return domainDataModelTreeDto;
    }

    /**
     * findTreeByDataValueAndTreeDto
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param dataValue 数据的值
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findTreeByDataValueAndTreeDto(DomainDataModelTreeDto domainDataModelTreeDto, String dataValue) {

        //切割要寻找的路径
        String[] split = dataValue.split("\\.");
        //如果只有一个，说明只需要寻找一次
        if (split.length == 1) {
            domainDataModelTreeDto = findDomainModelTreeByValue(domainDataModelTreeDto, split[0], 1);
            return domainDataModelTreeDto;
        }
        //寻找深度大于1，需要寻找多次，按层次循环找，上一层找到的对象是下一层的入参
        for (int i = 0; i < split.length; i++) {
            domainDataModelTreeDto = findDomainModelTreeByValue(domainDataModelTreeDto, split[i], i + 1);
            if (null == domainDataModelTreeDto) {
                return null;
            }
        }
        return domainDataModelTreeDto;
    }

    /**
     * findTreeByDataValueByTypeObject
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param domainDataModelTreeDtoResult domainDataModelTreeDtoResult
     * @param dataValue dataValue
     * @param isArray isArray
     */
    public void findTreeByDataValueByTypeObject(DomainDataModelTreeDto domainDataModelTreeDto, DomainDataModelTreeDto domainDataModelTreeDtoResult,
                                                String dataValue, boolean isArray) {
        //找到了匹配的路径，且类型相等
        if (domainDataModelTreeDto.getValue().equals(dataValue)) {
            List<String> typeList = new ArrayList<>();
            typeList.add(DataVariableTypeEnum.OBJECT_TYPE.getMessage());
            DomainDataModelTreeDto dataModelTreeDto = null;
            if (!DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(domainDataModelTreeDto.getType())) {
                return;
            }
            if (!isArray) {
                dataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListType(domainDataModelTreeDto, typeList,
                        DomainModelTreeFindVarBeanNameEnum.OBJECT_TYPE.getMessage());
            } else {
                dataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListType(domainDataModelTreeDto, typeList,
                        DomainModelTreeFindVarBeanNameEnum.OBJECT_ARRAY_DYNAMIC_TYPE.getMessage());
            }
            if (null != dataModelTreeDto) {
                BeanUtils.copyProperties(dataModelTreeDto, domainDataModelTreeDtoResult);
            } else {
                BeanUtils.copyProperties(domainDataModelTreeDto, domainDataModelTreeDtoResult);
                domainDataModelTreeDtoResult.setChildren(null);
            }
        } else if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            //处理"output.ruleDecision1".contains("output.ruleDecision")的情况，
            //把连个字符都加上.后再进行包含比较output.ruleDecision1.".contains("output.ruleDecision.")这样就满足需求false
            String conditionDataValue = dataValue + ".";
            String treeValue = domainDataModelTreeDto.getValue() + ".";
            if (conditionDataValue.contains(treeValue)) {
                for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                    String treeChildValue = child.getValue() + ".";
                    if (conditionDataValue.contains(treeChildValue)) {
                        findTreeByDataValueByTypeObject(child, domainDataModelTreeDtoResult, dataValue, isArray);
                    }
                }
            }
        }
    }

    /**
     * 返回当前路径的树
     *
     * @param dataModelTreeDto dataModelTreeDto
     * @param dataValue dataValue
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findDataValueTree(DomainDataModelTreeDto dataModelTreeDto, String dataValue) {
        if (dataModelTreeDto.getValue().equals(dataValue)) {
            return dataModelTreeDto;
        } else {
            if (CollectionUtils.isEmpty(dataModelTreeDto.getChildren())) {
                return null;
            }
            for (DomainDataModelTreeDto child : dataModelTreeDto.getChildren()) {
                DomainDataModelTreeDto dataValueTree = findDataValueTree(child, dataValue);
                if (null != dataValueTree) {
                    return dataValueTree;
                }
            }
            return null;
        }
    }

    /**
     * 寻找与value匹配的name
     *
     * @param domainDataModelTreeDto domainDataModelTreeDto
     * @param value value
     * @param index index
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findDomainModelTreeByValue(DomainDataModelTreeDto domainDataModelTreeDto, String value, int index) {
        DomainDataModelTreeDto dynamicTreeOutputDtoCopy = DomainDataModelTreeDto.builder().build();
        dynamicTreeOutputDtoCopy = domainDataModelTreeDto.deepClone();
        String[] valueSplit = domainDataModelTreeDto.getValue().split("\\.");
        //如果是相同等级的，则看看是否匹配，如果匹配则返回该对象，否则返回null
        boolean flag = valueSplit.length == index && !domainDataModelTreeDto.getName().equalsIgnoreCase(value) || (valueSplit.length > index);
        if (valueSplit.length == index && domainDataModelTreeDto.getName().equalsIgnoreCase(value)) {
            return dynamicTreeOutputDtoCopy;
        } else if (flag) {
            //等级相同，但是value和dataValue不相同，说明不存在
            return null;
        } else if (CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            //或者children等于null直接返回null，说明不存在
            return null;
        }
        dynamicTreeOutputDtoCopy.setChildren(null);

        if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            List<DomainDataModelTreeDto> children = new ArrayList<>();
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                DomainDataModelTreeDto childDomainDataModelTreeDto = findDomainModelTreeByValue(child, value, index);
                if (null != childDomainDataModelTreeDto) {
                    children.add(childDomainDataModelTreeDto);
                }
            }
            //如果children等于null则表示在相同等级中没有匹配的数据直接返回null
            if (CollectionUtils.isEmpty(children)) {
                return null;
            }
            dynamicTreeOutputDtoCopy.setChildren(children);
        }
        return dynamicTreeOutputDtoCopy;
    }

    /**
     * getContentList
     *
     * @param type type
     * @param globalId globalId
     * @return DomainDataModelTreeDto的列表(list)
     */
    public List<DomainDataModelTreeDto> getContentList(TemplateUnitTypeEnum type, Long globalId) {
        List<DomainDataModelTreeDto> contentList = new ArrayList<>();

        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(globalId);
        contentList.add(varProcessSpace.getInputData() == null ? null : DomainModelTreeEntityUtils.getByContentStaticTemplateTree(varProcessSpace
                .getInputData()));

        return contentList;
    }

    /**
     * isArrayListByName
     *
     * @param list 列表
     * @param name 名称
     * @return boolean
     */
    public boolean isArrayListByName(List<String> list, String name) {
        long count = list.stream()
                .filter(f -> f.equalsIgnoreCase(name))
                .count();
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * findDomainDataModelByIdOneLevelObject
     *
     * @param content 内容
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findDomainDataModelByIdOneLevelObject(String content) {
        DomainDataModelTreeDto domainDataModelTreeDto = DomainModelTreeEntityUtils.getByContentStaticTemplateTree(content);
        if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            List<DomainDataModelTreeDto> objectList = new ArrayList<>();
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                if (DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(child.getType())) {
                    child.setChildren(null);
                    objectList.add(child);
                }
            }
            if (CollectionUtils.isEmpty(objectList)) {
                domainDataModelTreeDto.setChildren(null);
            } else {
                domainDataModelTreeDto.setChildren(objectList);
            }
        } else {
            domainDataModelTreeDto.setChildren(null);
        }
        return domainDataModelTreeDto;
    }

    /**
     * 根据类型和位置在变量中寻找与之相应的变量树形数据
     *
     * @param typeList 类型列表
     * @param contentList 内容list
     * @param beanName Bean的名称
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataModelTreeByTypeList(List<String> typeList, List<DomainDataModelTreeDto> contentList,
                                                                          String beanName) {
        List<DomainDataModelTreeDto> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(typeList)) {
            return null;
        }
        for (DomainDataModelTreeDto content : contentList) {
            DomainDataModelTreeDto domainDataModelTreeDto = content.deepClone();

            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListType(domainDataModelTreeDto, typeList, beanName);
            if (null != domainDataModelTreeDto && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                list.add(domainDataModelTreeDto);
            }
        }
        return list;
    }

    /**
     * 根据类型和位置在变量中寻找与之相应的变量树形数据
     *
     * @param typeList 类型列表
     * @param contentList 内容list
     * @param beanName Bean的名称
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataModelTreeByType(List<String> typeList, List<DomainDataModelTreeDto> contentList,
                                                                          String beanName) {
        List<DomainDataModelTreeDto> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(typeList)) {
            return null;
        }
        for (DomainDataModelTreeDto content : contentList) {
            DomainDataModelTreeDto domainDataModelTreeDto = content.deepClone();

            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByType(domainDataModelTreeDto, typeList, beanName);
            if (null != domainDataModelTreeDto && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                list.add(domainDataModelTreeDto);
            }
        }
        return list;
    }

    /**
     * 根据类型和位置在变量中寻找与之相应的变量树形数据
     *
     * @param typeList 类型列表
     * @param contentList 内容list
     * @param beanName Bean类名称
     * @param dataValue 数据的值
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataModelTreeByTypeList(List<String> typeList, List<String> contentList, String beanName,
                                                                          String dataValue) {
        List<DomainDataModelTreeDto> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(typeList)) {
            return null;
        }
        for (String content : contentList) {
            DomainDataModelTreeDto domainDataModelTreeDto = DomainModelTreeEntityUtils.getByContentStaticTemplateTree(content);
            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListType(domainDataModelTreeDto, typeList, beanName);
            if (null != domainDataModelTreeDto && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                list.add(domainDataModelTreeDto);
            }
        }
        return list;
    }

    /**
     * 根据类型和位置在变量中寻找与之相应的变量树形数据
     * @param typeList 类型列表
     * @param contentList 内容list
     * @param beanName Bean的名称
     * @param dataValue 数据的值
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataModelTreeByTypeListAndDataValue(List<String> typeList,
                                                                                      List<DomainDataModelTreeDto> contentList, String beanName,
                                                                                      String dataValue) {
        List<DomainDataModelTreeDto> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(typeList)) {
            return null;
        }
        for (DomainDataModelTreeDto content : contentList) {
            DomainDataModelTreeDto domainDataModelTreeDto = content.deepClone();

            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListTypeAndDataValue(domainDataModelTreeDto, typeList,
                    beanName, dataValue);
            if (null != domainDataModelTreeDto && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                list.add(domainDataModelTreeDto);
            }
        }
        return list;
    }

    /**
     * 根据路径查询变量数据，这里根据bean区分逻辑，策略模式
     * @param type 类型
     * @param strategyId 决策Id
     * @param spaceId 变量空间Id
     * @param dataValue 数据的值
     * @param beanName Bean的名称
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValue(TemplateUnitTypeEnum type, Long strategyId, Long spaceId, String dataValue,
                                                                  String beanName) {
        if (StringUtils.isEmpty(dataValue)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据查找节点不能为空！");
        }
        List<String> positionList = new ArrayList<>();
        for (DomainModelSheetNameEnum domainModelSheetNameEnum : DomainModelSheetNameEnum.values()) {
            positionList.add(domainModelSheetNameEnum.getMessage());
        }

        List<DomainDataModelTreeDto> list = new ArrayList<>();
        List<DomainDataModelTreeDto> domainDataModelTreeDtoList = findDsStrategyAllVar(type, strategyId, spaceId, positionList);
        for (DomainDataModelTreeDto domainDataModelTreeDto : domainDataModelTreeDtoList) {
            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByName(domainDataModelTreeDto, dataValue, beanName);
            if (null != domainDataModelTreeDto && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                list.add(domainDataModelTreeDto);
            }
        }
        return list;
    }

    /**
     * 根据路径查询变量数据，这里根据bean区分逻辑，策略模式
     * @param type 类型
     * @param strategyId 决策Id
     * @param spaceId 变量空间Id
     * @param dataValue 数据的值
     * @param beanName Bean的名称
     * @param jsonSchemalList json体
     * @return List
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValue(TemplateUnitTypeEnum type, Long strategyId, Long spaceId, String dataValue,
                                                                  String beanName, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
        if (StringUtils.isEmpty(dataValue)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据查找节点不能为空！");
        }

        List<DomainDataModelTreeDto> list = new ArrayList<>();
        for (Map.Entry<String, DomainDataModelTreeDto> entry : jsonSchemalList.entrySet()) {
            DomainDataModelTreeDto domainDataModelTreeDto = entry.getValue();
            if (domainDataModelTreeDto == null) {
                continue;
            }
            DomainDataModelTreeDto cloneTreeDto = domainDataModelTreeDto.deepClone();
            cloneTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByName(cloneTreeDto, dataValue, beanName);
            if (null != cloneTreeDto && !CollectionUtils.isEmpty(cloneTreeDto.getChildren())) {
                list.add(cloneTreeDto);
            }
        }
        return list;
    }

    /**
     * 把变量字符串转换成变量树DomainDataModelTreeDto
     *
     * @param contentList 内容list
     * @return List
     */
    public List<DomainDataModelTreeDto> convertDomainDataModelTreeDtoList(List<String> contentList) {
        List<DomainDataModelTreeDto> list = new ArrayList<>();
        for (String content : contentList) {
            DomainDataModelTreeDto domainDataModelTreeDto = DomainModelTreeEntityUtils.getByContentStaticTemplateTree(content);
            list.add(domainDataModelTreeDto);
        }
        return list;
    }

    /**
     * 根据typeList寻找到响应的bean名称
     *
     * @param typeList 类型list
     * @return String
     */
    public String typeListFindBeanName(List<String> typeList) {
        String beanName = DomainModelTreeFindVarBeanNameEnum.BASE_TYPE.getMessage();
        //是否为数组
        long arrayCount = typeList.stream()
                .filter(f -> f.equals(DataVariableTypeEnum.ARRAY_TYPE.getMessage()))
                .count();
        if (arrayCount > 0) {
            beanName = DomainModelTreeFindVarBeanNameEnum.ARRAY_TYPE.getMessage();
        }
        long objectCount = typeList.stream()
                .filter(f -> f.equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage()))
                .count();
        if (objectCount > 0) {
            beanName = DomainModelTreeFindVarBeanNameEnum.OBJECT_TYPE.getMessage();
        }
        return beanName;
    }

    /**
     * 根据typeList寻找到响应的bean名称
     *
     * @param content content
     * @param split split+
     * @param dataTypes dataTypes
     * @return String
     */
    public String typeDirectListFindBeanName(String content, String[] split, List<String> dataTypes) {
        JSONObject contentObj = JSON.parseObject(content);
        List<Boolean> isArray = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            final String dataType = contentObj.getString("type");
            if ("object".equals(dataType)) {
                isArray.add(false);
                dataTypes.add(dataType);
                if (i < split.length - 1) {
                    contentObj = contentObj.getJSONObject("properties").getJSONObject(split[i + 1]);
                }
            } else if ("array".equals(dataType)) {
                isArray.add(true);
                final String arrType = contentObj.getJSONObject("items").getString("type");
                dataTypes.add(arrType);
                if (i < split.length - 1) {
                    contentObj = contentObj.getJSONObject("items").getJSONObject("properties").getJSONObject(split[i + 1]);
                }
            } else {
                isArray.add(false);
                dataTypes.add(dataType);
            }
        }
        String beanName;
        boolean isObjectArr = false;
        for (int i = 0; i < dataTypes.size(); i++) {
            if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(dataTypes.get(i))
                    && Boolean.TRUE.equals(isArray.get(i))) {
                isObjectArr = true;
                break;
            }
        }

        if (isObjectArr) {
            beanName = DomainModelTreeFindVarBeanNameEnum.OBJECT_ARRAY_AND_PROPERTY.getMessage();
            List<String> types = new ArrayList<>();
            for (int i = 0; i < isArray.size(); i++) {
                if (Boolean.TRUE.equals(isArray.get(i))) {
                    types.add(dataTypes.get(i) + "-1");
                } else {
                    types.add(dataTypes.get(i) + "-0");
                }
            }
            dataTypes.clear();
            dataTypes.addAll(types);
        } else {
            if (Boolean.TRUE.equals(isArray.get(isArray.size() - 1))) {
                if (dataTypes.get(dataTypes.size() - 1).equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage())) {
                    beanName = DomainModelTreeFindVarBeanNameEnum.BASE_ARRAY_TYPE.getMessage();
                } else {
                    beanName = DomainModelTreeFindVarBeanNameEnum.BASE_ARRAY_TYPE.getMessage();
                }
            } else {
                if (dataTypes.get(dataTypes.size() - 1).equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage())) {
                    beanName = DomainModelTreeFindVarBeanNameEnum.OBJECT_TYPE.getMessage();
                } else {
                    beanName = DomainModelTreeFindVarBeanNameEnum.BASE_TYPE.getMessage();
                }
            }
            final String last = dataTypes.get(dataTypes.size() - 1);
            dataTypes.clear();
            dataTypes.add(last);
        }
        return beanName;
    }

    /**
     * 根据typeList寻找到响应的bean名称
     *
     * @param typeList 类型list
     * @return String
     */
    public String typeListFindArrayBeanName(List<String> typeList) {
        String beanName = DomainModelTreeFindVarBeanNameEnum.BASE_ARRAY_TYPE.getMessage();
        //是否为对象
        long objectCount = typeList.stream()
                .filter(f -> f.equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage()))
                .count();
        if (objectCount > 0) {
            beanName = DomainModelTreeFindVarBeanNameEnum.OBJECT_ARRAY_DYNAMIC_TYPE.getMessage();
        }
        return beanName;
    }

    /**
     * 根据typeList寻找到响应的bean名称
     *
     * @return String
     */
    public String findBaeTypeAndBaseArrayTypeBeanName() {
        return DomainModelTreeFindVarBeanNameEnum.BASE_TYPE_AND_ARRAY_TYPE.getMessage();
    }

    /**
     * 根据位置信息过滤需要的jsonschema
     * @param map
     * @param positionList
     * @return List
     */

    /**
     * 根据位置信息过滤需要的jsonschema
     * @param map map集合
     * @param positionList 位置list
     * @return List
     * @param <T> 泛型类对象
     */
    public <T> List<T> filterDsStrategyAllVar(Map<String, T> map, List<String> positionList) {
        //        <T> T  castType(Object obj,Class<T> targetCls){
        boolean isEngineVars = isArrayListByName(positionList, DomainModelSheetNameEnum.ENGINE_VARS.getMessage());
        boolean isOutput = isArrayListByName(positionList, DomainModelSheetNameEnum.OUTPUT.getMessage());
        boolean isInput = isArrayListByName(positionList, DomainModelSheetNameEnum.INPUT.getMessage());
        boolean isRawData = isArrayListByName(positionList, DomainModelSheetNameEnum.RAW_DATA.getMessage());
        boolean isExternalData = isArrayListByName(positionList, DomainModelSheetNameEnum.EXTERNAL_DATA.getMessage());
        boolean isCommonData = isArrayListByName(positionList, DomainModelSheetNameEnum.COMMON_DATA.getMessage());
        boolean isBlazeData = isArrayListByName(positionList, DomainModelSheetNameEnum.BLAZE_DATA.getMessage());
        boolean isExternalVarData = isArrayListByName(positionList, DomainModelSheetNameEnum.EXTERNAL_VARS.getMessage());
        boolean isParamData = isArrayListByName(positionList, TemplateVarLocationEnum.PARAMETERS.getDisplayName());
        boolean isLocalData = isArrayListByName(positionList, TemplateVarLocationEnum.LOCAL_VARS.getDisplayName());

        List<T> contentList = new ArrayList<>();
        if (isEngineVars && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.ENGINE_VARS.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.ENGINE_VARS.getMessage()));
        }
        if (isOutput && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.OUTPUT.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.OUTPUT.getMessage()));
        }
        if (isInput && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.INPUT.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.INPUT.getMessage()));
        }
        if (isRawData && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.RAW_DATA.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.RAW_DATA.getMessage()));
        }

        if (isExternalData && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.EXTERNAL_DATA.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.EXTERNAL_DATA.getMessage()));
        }
        if (isCommonData && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.COMMON_DATA.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.COMMON_DATA.getMessage()));
        }
        if (isBlazeData && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.BLAZE_DATA.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.BLAZE_DATA.getMessage()));
        }
        if (isExternalVarData && !StringUtils.isEmpty(map.get(DomainModelSheetNameEnum.EXTERNAL_VARS.getMessage()))) {
            contentList.add(map.get(DomainModelSheetNameEnum.EXTERNAL_VARS.getMessage()));
        }
        if (isParamData && !StringUtils.isEmpty(map.get(TemplateVarLocationEnum.PARAMETERS.getDisplayName()))) {
            contentList.add(map.get(TemplateVarLocationEnum.PARAMETERS.getDisplayName()));
        }
        if (isLocalData && !StringUtils.isEmpty(map.get(TemplateVarLocationEnum.LOCAL_VARS.getDisplayName()))) {
            contentList.add(map.get(TemplateVarLocationEnum.LOCAL_VARS.getDisplayName()));
        }
        return contentList;
    }

    /**
     * 查询变量空间信息
     *
     * @param spaceId 变量空间Id
     * @return VarProcessSpace
     */
    public VarProcessSpace getVarProcessSpace(Long spaceId) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND,"变量空间不存在。");
        }
        return varProcessSpace;
    }
}
