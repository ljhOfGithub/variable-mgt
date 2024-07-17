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

import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableSimpleTypeEnum;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelTreeFindVarBeanNameEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.converter.DynamicTreeConverter;
import com.wiseco.var.process.app.server.service.converter.ModelTreeFindVarConverter;
import com.wiseco.var.process.app.server.service.dto.input.DataValueAndTypeGetVarTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.DataValueGetVarTypeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.StaticTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.TreeVarBaseArrayInputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author zhouxiuxiu
 * @since  2022/6/13 14:35
 */
@RefreshScope
@Service
@Slf4j
public class CommonGlobalDataBiz {

    @Autowired
    private DynamicTreeConverter dynamicTreeConverter;

    @Autowired
    private ModelTreeFindVarConverter modelTreeFindVarConverter;

    @Autowired
    private VarProcessDataModelService dataModelService;

    /**
     * findStaticTree
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体list
     */
    public List<DomainDataModelTreeDto> findStaticTree(StaticTreeInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());
        dynamicTreeConverter.checkTypeListArray(inputDto.getTypeList());
        dynamicTreeConverter.checkTypeListObject(inputDto.getTypeList());

        Map<String, DomainDataModelTreeDto> contentMap = inputDto.getJsonschemaDtoMap();
        List<DomainDataModelTreeDto> contentList = null;
        if (CollectionUtils.isEmpty(contentMap)) {
            contentList = dynamicTreeConverter.findDsStrategyAllVar(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()), inputDto.getStrategyId(),
                    inputDto.getSpaceId(), inputDto.getPositionList());
        } else {
            contentList = dynamicTreeConverter.filterDsStrategyAllVar(contentMap, inputDto.getPositionList());
        }

        String beanName = dynamicTreeConverter.typeListFindBeanName(inputDto.getTypeList());
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataModelTreeByTypeList(inputDto.getTypeList(), contentList, beanName);
        loopSetTypeRef(list);
        return !CollectionUtils.isEmpty(list) ? list : null;
    }

    /**
     * findDirectStaticTree
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体list
     */
    public List<DomainDataModelTreeDto> findDirectStaticTree(StaticTreeInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());
        dynamicTreeConverter.checkTypeListArray(inputDto.getTypeList());
        dynamicTreeConverter.checkTypeListObject(inputDto.getTypeList());

        Map<String, DomainDataModelTreeDto> contentMap = inputDto.getJsonschemaDtoMap();
        List<DomainDataModelTreeDto> contentList = null;
        if (CollectionUtils.isEmpty(contentMap)) {
            contentList = dynamicTreeConverter.findDsStrategyAllVar(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()), inputDto.getStrategyId(),
                    inputDto.getSpaceId(), inputDto.getPositionList());
        } else {
            contentList = dynamicTreeConverter.filterDsStrategyAllVar(contentMap, inputDto.getPositionList());
        }

        final String[] split = inputDto.getLeftProperty().split("\\.");
        final String content = dataModelService.findByDataModelName(split[0]).getContent();
        List<String> dataTypes = new ArrayList<>();
        String beanName = dynamicTreeConverter.typeDirectListFindBeanName(content, split, dataTypes);
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataModelTreeByTypeList(dataTypes, contentList, beanName);
        loopSetTypeRef(list);
        return !CollectionUtils.isEmpty(list) ? list : null;
    }

    /**
     * findTreeVarBaseArray
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体list
     */
    public List<DomainDataModelTreeDto> findTreeVarBaseArray(TreeVarBaseArrayInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());

        Map<String, DomainDataModelTreeDto> contentMap = inputDto.getJsonschemaDtoMap();
        List<DomainDataModelTreeDto> contentList = null;
        if (CollectionUtils.isEmpty(contentMap)) {
            contentList = dynamicTreeConverter.findDsStrategyAllVar(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()), inputDto.getStrategyId(),
                    inputDto.getSpaceId(), inputDto.getPositionList());
        } else {
            contentList = dynamicTreeConverter.filterDsStrategyAllVar(contentMap, inputDto.getPositionList());
        }

        String beanName = dynamicTreeConverter.typeListFindArrayBeanName(inputDto.getTypeList());
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataModelTreeByTypeList(inputDto.getTypeList(), contentList, beanName);
        loopSetTypeRef(list);
        return list.size() > 0 ? list : null;
    }

    /**
     * findTreeVarsArray
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体list
     */
    public List<DomainDataModelTreeDto> findTreeVarsArray(TreeVarBaseArrayInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());

        Map<String, DomainDataModelTreeDto> contentMap = inputDto.getJsonschemaDtoMap();
        List<DomainDataModelTreeDto> contentList = null;
        if (CollectionUtils.isEmpty(contentMap)) {
            contentList = dynamicTreeConverter.findDsStrategyAllVar(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()), inputDto.getStrategyId(),
                    inputDto.getSpaceId(), inputDto.getPositionList());
        } else {
            contentList = dynamicTreeConverter.filterDsStrategyAllVar(contentMap, inputDto.getPositionList());
        }

        String beanName = dynamicTreeConverter.typeListFindArrayBeanName(inputDto.getTypeList());
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataModelTreeByType(inputDto.getTypeList(), contentList, beanName);
        loopSetTypeRef(list);
        return list.size() > 0 ? list : null;
    }

    /**
     * findByDataValueGetVarTree
     *
     * @param type 模板单元类型
     * @param globalId 全局Id
     * @param dataValue 数据的值
     * @return 决策领域树形结构实体
     */
    public DomainDataModelTreeDto findByDataValueGetVarTree(TemplateUnitTypeEnum type, Long globalId, String dataValue) {
        List<DomainDataModelTreeDto> contentList = dynamicTreeConverter.getContentList(type, globalId);
        for (DomainDataModelTreeDto content : contentList) {
            DomainDataModelTreeDto dataModelTreeDto = dynamicTreeConverter.findTreeByDataValueAndTreeDto(content, dataValue);
            if (null != dataModelTreeDto) {
                return dataModelTreeDto;
            }
        }
        return null;
    }

    /**
     * 通过数据的值获取变量的类型
     *
     * @param inputDto 输入实体类对象
     * @return 变量类型
     */
    public String findDyDataValueGetVarType(DataValueGetVarTypeInputDto inputDto) {
        List<DomainDataModelTreeDto> contentList = dynamicTreeConverter.getContentList(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()),
                inputDto.getGlobalId());
        for (DomainDataModelTreeDto content : contentList) {
            DomainDataModelTreeDto dataModelTreeDto = dynamicTreeConverter.findTreeByDataValueAndTreeDto(content, inputDto.getDataValue());
            if (null != dataModelTreeDto) {
                return dynamicTreeConverter.lastDataValueByType(dataModelTreeDto, inputDto.getDataValue());
            }
        }
        return null;
    }

    /**
     * findDataValueAndTypeGetVarTree
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体
     */
    public DomainDataModelTreeDto findDataValueAndTypeGetVarTree(DataValueAndTypeGetVarTreeInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());
        long count = inputDto.getTypeList().stream()
                .filter(f -> f.equals(DataVariableTypeEnum.ARRAY_TYPE.getMessage()))
                .count();
        long objectCount = inputDto.getTypeList().stream()
                .filter(f -> f.equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage()))
                .count();
        if (objectCount > 0 && inputDto.getTypeList().size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "object类型不能和其它类型进行混合！");
        }

        Map<String, DomainDataModelTreeDto> contentMap = inputDto.getJsonschemaDtoMap();
        List<DomainDataModelTreeDto> contentList = null;
        if (CollectionUtils.isEmpty(contentMap)) {
            contentList = dynamicTreeConverter.findDsStrategyAllVar(TemplateUnitTypeEnum.getTypeEnum(inputDto.getType()), inputDto.getGlobalId(), inputDto.getGlobalId(), TemplateVarLocationEnum.templateVarLocationEnumList());
        } else {
            contentList = dynamicTreeConverter.filterDsStrategyAllVar(contentMap, TemplateVarLocationEnum.templateVarLocationEnumList());
        }

        for (DomainDataModelTreeDto content : contentList) {
            if (objectCount > 0) {
                DomainDataModelTreeDto domainDataModelTreeDto = DomainDataModelTreeDto.builder().build();
                domainDataModelTreeDto = content.deepClone();
                DomainDataModelTreeDto domainDataModelTreeDtoResult = new DomainDataModelTreeDto();
                dynamicTreeConverter.findTreeByDataValueByTypeObject(domainDataModelTreeDto, domainDataModelTreeDtoResult, inputDto.getDataValue(), false);
                if (!StringUtils.isEmpty(domainDataModelTreeDtoResult.getValue())) {
                    setTypeRef(domainDataModelTreeDtoResult);
                    return domainDataModelTreeDtoResult;
                }
                continue;
            }
            //寻找DataValue路劲对象
            DomainDataModelTreeDto domainDataModelTreeDto = dynamicTreeConverter.findTreeByDataValueAndTreeDto(content, inputDto.getDataValue());
            //对象下的数组
            DomainDataModelTreeDto domainDataModelTreeDtoArray = new DomainDataModelTreeDto();
            if (null != domainDataModelTreeDto) {
                //如果有数组
                if (count > 0) {
                    domainDataModelTreeDtoArray = domainDataModelTreeDto;
                    domainDataModelTreeDtoArray = DomainModelTreeEntityUtils.findDataValueByArray(domainDataModelTreeDtoArray, inputDto.getDataValue());
                }
                //在DataValue路劲对象的基础上寻找typeList类型的属性
                domainDataModelTreeDto = DomainModelTreeEntityUtils.findDataValueByTypeList(domainDataModelTreeDto, inputDto.getDataValue(), inputDto.getTypeList());

                //如果数组的寻找的数据树children不为空，且domainDataModelTreeDto对象不为空且Children不为空则需要合并
                if (null != domainDataModelTreeDto
                        && !CollectionUtils.isEmpty(domainDataModelTreeDtoArray.getChildren())
                        && !CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                    domainDataModelTreeDto.getChildren().addAll(domainDataModelTreeDtoArray.getChildren());
                }
            }
            if (null != domainDataModelTreeDto) {
                setTypeRef(domainDataModelTreeDto);
                return domainDataModelTreeDto;
            }
            if (!CollectionUtils.isEmpty(domainDataModelTreeDtoArray.getChildren())) {
                setTypeRef(domainDataModelTreeDtoArray);
                return domainDataModelTreeDtoArray;
            }
        }
        return null;
    }

    /**
     * findDataValueAndTypeGetArrayVarTree
     *
     * @param inputDto 输入实体类对象
     * @return 决策领域树形结构实体
     */
    public DomainDataModelTreeDto findDataValueAndTypeGetArrayVarTree(DataValueAndTypeGetVarTreeInputDto inputDto) {
        dynamicTreeConverter.checkTypeList(inputDto.getTypeList());
        long objectCount = inputDto.getTypeList().stream()
                .filter(f -> f.equals(DataVariableTypeEnum.OBJECT_TYPE.getMessage()))
                .count();
        if (objectCount > 0 && inputDto.getTypeList().size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "object类型不能和其它类型进行混合！");
        }

        for (String positionKey : inputDto.getJsonschemaDtoMap().keySet()) {
            if (StringUtils.isEmpty(inputDto.getJsonschemaDtoMap().get(positionKey))) {
                continue;
            }
            DomainDataModelTreeDto domainDataModelTreeDto = DomainDataModelTreeDto.builder().build();
            domainDataModelTreeDto = inputDto.getJsonschemaDtoMap().get(positionKey).deepClone();
            if (objectCount > 0) {
                DomainDataModelTreeDto domainDataModelTreeDtoResult = new DomainDataModelTreeDto();
                dynamicTreeConverter.findTreeByDataValueByTypeObject(domainDataModelTreeDto, domainDataModelTreeDtoResult, inputDto.getDataValue(), true);
                if (!StringUtils.isEmpty(domainDataModelTreeDtoResult.getValue())) {
                    if (null != domainDataModelTreeDtoResult && !CollectionUtils.isEmpty(domainDataModelTreeDtoResult.getChildren())) {
                        setTypeRef(domainDataModelTreeDtoResult);
                        return domainDataModelTreeDtoResult;
                    }
                    return null;
                }
                continue;
            }

            domainDataModelTreeDto = dynamicTreeConverter.findDataValueTree(domainDataModelTreeDto, inputDto.getDataValue());
            domainDataModelTreeDto = modelTreeFindVarConverter.getDomainDataModelTreeByListType(domainDataModelTreeDto, inputDto.getTypeList(), DomainModelTreeFindVarBeanNameEnum.BASE_ARRAY_TYPE.getMessage());

            if (null != domainDataModelTreeDto) {
                setTypeRef(domainDataModelTreeDto);
                return domainDataModelTreeDto;
            }

        }
        return null;
    }

    /**
     * findDomainDataByDataValueGetObject
     *
     * @param type 模板单元类型枚举
     * @param globalId 全局Id
     * @param dataValue 数据的值
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValueGetObject(TemplateUnitTypeEnum type, Long globalId, String dataValue) {
        Long strategyId = null;
        Long spaceId = null;
        if (type == null || type == TemplateUnitTypeEnum.STRATEGY_COMPONENT) {
            strategyId = globalId;
        } else {
            spaceId = globalId;
        }
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataByDataValue(type, strategyId, spaceId, dataValue,
                DomainModelTreeFindVarBeanNameEnum.OBJECT_DYNAMIC_TYPE.getMessage());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        loopSetTypeRef(list);
        return list;
    }

    /**
     * findDomainDataByDataValueGetArrayObject
     *
     * @param type 模板单元类型枚举
     * @param globalId 全局Id
     * @param dataValue 数据的值
     * @return 决策领域树形结构实体list
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValueGetArrayObject(TemplateUnitTypeEnum type, Long globalId, String dataValue) {
        Long strategyId = null;
        Long spaceId = null;
        if (type == null || type == TemplateUnitTypeEnum.STRATEGY_COMPONENT) {
            strategyId = globalId;
        } else {
            spaceId = globalId;
        }
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataByDataValue(type, strategyId, spaceId, dataValue,
                DomainModelTreeFindVarBeanNameEnum.OBJECT_ARRAY_DYNAMIC_TYPE.getMessage());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        loopSetTypeRef(list);
        return list;
    }

    /**
     * findDomainDataByDataValueGetObject
     *
     * @param type 模板单元类型枚举
     * @param globalId 全局Id
     * @param dataValue 数据的值
     * @param jsonSchemalList json方案的list
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValueGetObject(TemplateUnitTypeEnum type, Long globalId, String dataValue,
                                                                           Map<String, DomainDataModelTreeDto> jsonSchemalList) {
        Long strategyId = null;
        Long spaceId = null;
        if (type == null || type == TemplateUnitTypeEnum.STRATEGY_COMPONENT) {
            strategyId = globalId;
        } else {
            spaceId = globalId;
        }
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataByDataValue(type, strategyId, spaceId, dataValue,
                DomainModelTreeFindVarBeanNameEnum.OBJECT_DYNAMIC_TYPE.getMessage(), jsonSchemalList);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        loopSetTypeRef(list);
        return list;
    }

    /**
     * findDomainDataByDataValueGetArrayObject
     *
     * @param type type
     * @param globalId globalId
     * @param dataValue dataValue
     * @param jsonSchemalList jsonSchemalList
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findDomainDataByDataValueGetArrayObject(TemplateUnitTypeEnum type, Long globalId, String dataValue,
                                                                                Map<String, DomainDataModelTreeDto> jsonSchemalList) {
        Long strategyId = null;
        Long spaceId = null;
        if (type == null || type == TemplateUnitTypeEnum.STRATEGY_COMPONENT) {
            strategyId = globalId;
        } else {
            spaceId = globalId;
        }
        List<DomainDataModelTreeDto> list = dynamicTreeConverter.findDomainDataByDataValue(type, strategyId, spaceId, dataValue,
                DomainModelTreeFindVarBeanNameEnum.OBJECT_ARRAY_DYNAMIC_TYPE.getMessage(), jsonSchemalList);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        loopSetTypeRef(list);
        return list;
    }


    /**
     * findProperty
     *
     * @param type type
     * @param globalId globalId
     * @param dataValue dataValue
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findProperty(TemplateUnitTypeEnum type, Long globalId, String dataValue) {
        List<DomainDataModelTreeDto> contentList = dynamicTreeConverter.getContentList(type, globalId);
        DomainDataModelTreeDto dataModelTreeDto = null;
        boolean shouldContinue = true;
        int index = 0;
        while (shouldContinue && index < contentList.size()) {
            DomainDataModelTreeDto content = contentList.get(index);
            dataModelTreeDto = dynamicTreeConverter.findTreeByDataValueAndTreeDto(content, dataValue);
            if (null != dataModelTreeDto) {
                dataModelTreeDto = dynamicTreeConverter.findDataValueTree(dataModelTreeDto, dataValue);
                if (null != dataModelTreeDto) {
                    if (null == dataModelTreeDto.getChildren()) {
                        setTypeRef(dataModelTreeDto);
                        shouldContinue = false;
                    } else {
                        List<DomainDataModelTreeDto> childrenList = new ArrayList<>();
                        for (DomainDataModelTreeDto children : dataModelTreeDto.getChildren()) {
                            loopSetTypeRef(childrenList);
                            DataVariableTypeEnum dataTypeEnum = DataVariableTypeEnum.getMessageEnum(children.getType());
                            if (null != dataTypeEnum) {
                                childrenList.add(children);
                            }
                        }
                        dataModelTreeDto.setChildren(null);
                        if (!CollectionUtils.isEmpty(childrenList)) {
                            dataModelTreeDto.setChildren(childrenList);
                        }
                        setTypeRef(dataModelTreeDto);
                        shouldContinue = false;
                    }
                }
            }
            index++;
        }
        return dataModelTreeDto;
    }

    /**
     * findPropertyByTreeList
     *
     * @param type type
     * @param globalId globalId
     * @param dataValue dataValue
     * @param contentList contentList
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto findPropertyByTreeList(TemplateUnitTypeEnum type, Long globalId, String dataValue,
                                                         List<DomainDataModelTreeDto> contentList) {
        DomainDataModelTreeDto dataModelTreeDto = null;
        boolean found = false;
        int index = 0;
        while (!found && index < contentList.size()) {
            DomainDataModelTreeDto content = contentList.get(index);
            if (null == content) {
                index++;
                continue;
            }
            dataModelTreeDto = dynamicTreeConverter.findTreeByDataValueAndTreeDto(content, dataValue);
            if (null == dataModelTreeDto) {
                index++;
                continue;
            }
            dataModelTreeDto = dynamicTreeConverter.findDataValueTree(dataModelTreeDto, dataValue);
            if (null == dataModelTreeDto) {
                return null;
            }
            if (null == dataModelTreeDto.getChildren()) {
                setTypeRef(dataModelTreeDto);
                found = true;
            } else {
                List<DomainDataModelTreeDto> childrenList = new ArrayList<>();
                for (DomainDataModelTreeDto children : dataModelTreeDto.getChildren()) {
                    loopSetTypeRef(childrenList);
                    DataVariableTypeEnum dataTypeEnum = DataVariableTypeEnum.getMessageEnum(children.getType());
                    if (null != dataTypeEnum) {
                        childrenList.add(children);
                    }
                }
                dataModelTreeDto.setChildren(null);
                if (!CollectionUtils.isEmpty(childrenList)) {
                    dataModelTreeDto.setChildren(childrenList);
                }
                setTypeRef(dataModelTreeDto);
                found = true;
            }
            index++;
        }
        return dataModelTreeDto;
    }

    /**
     * 循环处理typeRef
     *
     * @param modelVars
     */
    private void loopSetTypeRef(List<DomainDataModelTreeDto> modelVars) {
        if (CollectionUtils.isEmpty(modelVars)) {
            return;
        }
        for (DomainDataModelTreeDto dataModelTreeDto : modelVars) {
            DataVariableSimpleTypeEnum simpleType = DataVariableSimpleTypeEnum.getMessageEnum(dataModelTreeDto.getType());
            if (null != simpleType) {
                dataModelTreeDto.setTypeRef(dataModelTreeDto.getType());
            } else {
                if (!dataModelTreeDto.getValue().startsWith(PositionVarEnum.LOCAL_VARS.getName())
                        && !dataModelTreeDto.getValue().startsWith(PositionVarEnum.PARAMETERS.getName())) {
                    dataModelTreeDto.setTypeRef(dataModelTreeDto.getValue());
                }
            }

            loopSetTypeRef(dataModelTreeDto.getChildren());
        }
    }

    /**
     * 处理typeRef
     *
     * @param node
     */
    private void setTypeRef(DomainDataModelTreeDto node) {
        if (node == null) {
            return;
        }
        DataVariableSimpleTypeEnum simpleType = DataVariableSimpleTypeEnum.getMessageEnum(node.getType());
        if (null != simpleType) {
            node.setTypeRef(node.getType());
        } else {
            node.setTypeRef(node.getValue());
        }
        loopSetTypeRef(node.getChildren());
    }

}
