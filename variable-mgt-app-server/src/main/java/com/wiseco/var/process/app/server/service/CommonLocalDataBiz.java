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
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.toolkit.ObjectTypeEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.RequestTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionCache;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableCache;
import com.wiseco.var.process.app.server.service.dto.input.DataValueAndTypeGetVarTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.DataValueGetVarTypeInputDto;
import com.wiseco.var.process.app.server.service.dto.json.ComponentJsonDto;
import com.wisecotech.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : zhouxiuxiu
 * @since : 2022/6/13 14:10
 */
@RefreshScope
@Service
@Slf4j
public class CommonLocalDataBiz {
    private static final String REGEX = "\\.";
    private static final String QUERY_TYPE_ITSELF_AND_SUBSET = "itselfAndSubset";
    private static final String QUERY_TYPE_DEFAULT = "default";
    @Autowired
    private CommonGlobalDataBiz commonGlobalDataBiz;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessVariableCacheService varProcessVariableCacheService;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessFunctionCacheService varProcessFunctionCacheService;
    @Autowired
    private JavaToolKitBiz javaToolKitBiz;

    /**
     * findParamVarsAndLocalVars
     *
     * @param paramVarsAndLocalVars paramVarsAndLocalVars
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findParamVarsAndLocalVars(ParamVarsAndLocalVars paramVarsAndLocalVars) {
        if (paramVarsAndLocalVars.getDataVarTypeList().contains(DataVariableTypeEnum.OBJECT_TYPE) && paramVarsAndLocalVars.getDataVarTypeList().size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "object类型不能跟其他类型混合查询");
        }
        List<DomainDataModelTreeDto> items = new ArrayList<>();
        if (paramVarsAndLocalVars.getRuleContent() == null) {
            return items;
        }
        ComponentJsonDto componentJsonDto = null;
        if (paramVarsAndLocalVars.getRuleContent() instanceof String) {
            componentJsonDto = JSONObject.parseObject(String.valueOf(paramVarsAndLocalVars.getRuleContent()), ComponentJsonDto.class);
        } else if (paramVarsAndLocalVars.getRuleContent() instanceof ComponentJsonDto) {
            componentJsonDto = (ComponentJsonDto) paramVarsAndLocalVars.getRuleContent();
        } else {
            componentJsonDto = new ComponentJsonDto();
        }

        // 查询参数
        Boolean paramFlag = Boolean.FALSE;
        // 查询本地变量
        Boolean localFlag = Boolean.FALSE;

        String dataValue = null;
        Boolean include = null;
        if (null != paramVarsAndLocalVars.getIncludeDataValue()) {
            dataValue = paramVarsAndLocalVars.getIncludeDataValue().getLeft();
            include = paramVarsAndLocalVars.getIncludeDataValue().getRight();
        }
        boolean flag1 = StringUtils.isBlank(dataValue) || (!dataValue.startsWith(PositionVarEnum.PARAMETERS.getName()) && !dataValue.startsWith(PositionVarEnum.LOCAL_VARS.getName()));
        if (flag1) {
            paramFlag = Boolean.TRUE;
            localFlag = Boolean.TRUE;
        } else if (dataValue.startsWith(PositionVarEnum.PARAMETERS.getName()) && null == include) {
            paramFlag = Boolean.TRUE;
        } else if (dataValue.startsWith(PositionVarEnum.LOCAL_VARS.getName()) && null == include) {
            localFlag = Boolean.TRUE;
        } else if (null != include) {
            paramFlag = Boolean.TRUE;
            localFlag = Boolean.TRUE;
        }

        ComponentJsonDto.BaseData baseData = componentJsonDto.getBaseData();
        List<ComponentJsonDto.Parameter> parameters = null;
        List<ComponentJsonDto.LocalVar> localVars = null;
        if (null != baseData && null != baseData.getDataModel()) {
            ComponentJsonDto.DataModel dataModel = baseData.getDataModel();
            parameters = dataModel.getParameters();
            localVars = dataModel.getLocalVars();
        }

        if (null == paramVarsAndLocalVars.getDataVarTypeList()) {
            paramVarsAndLocalVars.setDataVarTypeList(new ArrayList<>());
        }

        boolean flag = paramFlag && null != parameters && (paramVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_INPUT) || paramVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_OUTPUT));
        if (flag) {
            DomainDataModelTreeDto parametersNode = builderParametersNode(new ParametersNode(paramVarsAndLocalVars.getType(), paramVarsAndLocalVars.getGlobalId(), paramVarsAndLocalVars.getLocationEnumList(), paramVarsAndLocalVars.getDataVarTypeList(), paramVarsAndLocalVars.getIncludeDataValue(), parameters, paramVarsAndLocalVars.getArray(), paramVarsAndLocalVars.getQueryType(), paramVarsAndLocalVars.getRequestType(), paramVarsAndLocalVars.getJsonSchemalList(), localVars));
            if (null != parametersNode) {
                items.add(parametersNode);
            }
        }
        if (localFlag && null != localVars && paramVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.LOCAL_VARS)) {
            DomainDataModelTreeDto localVarsNode = builderLocalVarsNode(new LocalVarsNode(paramVarsAndLocalVars.getType(), paramVarsAndLocalVars.getGlobalId(), paramVarsAndLocalVars.getDataVarTypeList(), paramVarsAndLocalVars.getIncludeDataValue(), localVars, paramVarsAndLocalVars.getArray(), paramVarsAndLocalVars.getQueryType(), paramVarsAndLocalVars.getRequestType(), paramVarsAndLocalVars.getJsonSchemalList()));
            if (null != localVarsNode) {
                items.add(localVarsNode);
            }
        }
        return items;
    }

    /**
     * getProviderType
     *
     * @param type type
     * @param globalId globalId
     * @param localId localId
     * @param dataValue dataValue
     * @param sessionId  sessionId
     * @return java.lang.String
     */
    public String getProviderType(TemplateUnitTypeEnum type, Long globalId, Long localId, String dataValue, String sessionId) {
        return getProviderType(type, globalId, localId, dataValue, Boolean.FALSE, sessionId);
    }

    /**
     * getProviderTypeFullPath
     *
     * @param type type
     * @param globalId globalId
     * @param localId localId
     * @param dataValue dataValue
     * @param sessionId sessionId
     * @return java.lang.String
     */
    public String getProviderTypeFullPath(TemplateUnitTypeEnum type, Long globalId, Long localId, String dataValue, String sessionId) {
        return getProviderType(type, globalId, localId, dataValue, Boolean.TRUE, sessionId);
    }

    /**
     * findParamVarsAndLocalVarsByDataValue
     *
     * @param type type
     * @param globalId  globalId
     * @param localId localId
     * @param locationEnumList locationEnumList
     * @param dataVarTypeList dataVarTypeList
     * @param dataValue dataValue
     * @param content content
     * @return List
     */
    public List<DomainDataModelTreeDto> findParamVarsAndLocalVarsByDataValue(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, String dataValue, String content) {
        Pair<String, Boolean> includeDataValue = null;
        if (StringUtils.isNotBlank(dataValue)) {
            includeDataValue = Pair.of(dataValue, Boolean.TRUE);
        }
        return findParamVarsAndLocalVars(new ParamVarsAndLocalVars(type, globalId, localId, locationEnumList, dataVarTypeList, includeDataValue, null, null, QUERY_TYPE_DEFAULT, RequestTypeEnum.INTERNAL, content, null));
    }

    /**
     * findParamVarsAndLocalVarsByDataValue
     *
     * @param paramVarsAndLocalVarsByDataValue paramVarsAndLocalVarsByDataValue
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findParamVarsAndLocalVarsByDataValue(ParamVarsAndLocalVarsByDataValue paramVarsAndLocalVarsByDataValue) {
        if (CollectionUtils.isEmpty(paramVarsAndLocalVarsByDataValue.getLocationEnumList()) || CollectionUtils.isEmpty(paramVarsAndLocalVarsByDataValue.getDataVarTypeList())) {
            return null;
        }
        Pair<String, Boolean> includeDataValue = null;
        if (StringUtils.isNotBlank(paramVarsAndLocalVarsByDataValue.getDataValue())) {
            includeDataValue = Pair.of(paramVarsAndLocalVarsByDataValue.getDataValue(), Boolean.TRUE);
        }
        return findParamVarsAndLocalVars(new ParamVarsAndLocalVars(paramVarsAndLocalVarsByDataValue.getType(), paramVarsAndLocalVarsByDataValue.getGlobalId(), paramVarsAndLocalVarsByDataValue.getLocalId(), paramVarsAndLocalVarsByDataValue.getLocationEnumList(), paramVarsAndLocalVarsByDataValue.getDataVarTypeList(), includeDataValue, paramVarsAndLocalVarsByDataValue.getSessionId(), null, QUERY_TYPE_DEFAULT, RequestTypeEnum.INTERNAL, paramVarsAndLocalVarsByDataValue.getContent(), paramVarsAndLocalVarsByDataValue.getJsonSchemalList()));
    }

    /**
     * findArrayParamVarsAndLocalVars
     *
     * @param arrayParamVarsAndLocalVars  arrayParamVarsAndLocalVars
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findArrayParamVarsAndLocalVars(ArrayParamVarsAndLocalVars arrayParamVarsAndLocalVars) {
        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();
        if (arrayParamVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_INPUT) || arrayParamVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_OUTPUT) || arrayParamVarsAndLocalVars.getLocationEnumList().contains(TemplateVarLocationEnum.LOCAL_VARS)) {
            // 获取参数、本地变量
            List<DomainDataModelTreeDto> paramVarsAndLocalVars = findParamVarsAndLocalVars(new ParamVarsAndLocalVars(arrayParamVarsAndLocalVars.getType(), arrayParamVarsAndLocalVars.getGlobalId(), arrayParamVarsAndLocalVars.getLocalId(), arrayParamVarsAndLocalVars.getLocationEnumList(), arrayParamVarsAndLocalVars.getDataVarTypeList(), arrayParamVarsAndLocalVars.getIncludeDataValue(), arrayParamVarsAndLocalVars.getSessionId(), Boolean.TRUE, QUERY_TYPE_DEFAULT, RequestTypeEnum.INTERNAL, arrayParamVarsAndLocalVars.getContent(), arrayParamVarsAndLocalVars.getJsonSchemalList()));
            treeDtoList.addAll(paramVarsAndLocalVars);
        }
        return treeDtoList;
    }

    /**
     * findParamVarsAndLocalVarsItselfAndSubset
     *
     * @param paramVarsAndLocalVarsItselfAndSubset paramVarsAndLocalVarsItselfAndSubset
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findParamVarsAndLocalVarsItselfAndSubset(ParamVarsAndLocalVarsItselfAndSubset paramVarsAndLocalVarsItselfAndSubset) {
        return findParamVarsAndLocalVars(new ParamVarsAndLocalVars(paramVarsAndLocalVarsItselfAndSubset.getType(), paramVarsAndLocalVarsItselfAndSubset.getGlobalId(), paramVarsAndLocalVarsItselfAndSubset.getLocalId(), paramVarsAndLocalVarsItselfAndSubset.getLocationEnumList(), paramVarsAndLocalVarsItselfAndSubset.getDataVarTypeList(), paramVarsAndLocalVarsItselfAndSubset.getIncludeDataValue(), paramVarsAndLocalVarsItselfAndSubset.getSessionId(), null, QUERY_TYPE_ITSELF_AND_SUBSET, RequestTypeEnum.INTERNAL, paramVarsAndLocalVarsItselfAndSubset.getContent(), paramVarsAndLocalVarsItselfAndSubset.getJsonSchemalList()));
    }

    /**
     * findArrayParamVarsAndLocalVarsItselfAndSubset
     *
     * @param findArrayParamVarsAndLocalVarsItselfAndSubset findArrayParamVarsAndLocalVarsItselfAndSubset
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> findArrayParamVarsAndLocalVarsItselfAndSubset(FindArrayParamVarsAndLocalVarsItselfAndSubset findArrayParamVarsAndLocalVarsItselfAndSubset) {
        return findParamVarsAndLocalVars(new ParamVarsAndLocalVars(findArrayParamVarsAndLocalVarsItselfAndSubset.getType(), findArrayParamVarsAndLocalVarsItselfAndSubset.getGlobalId(), findArrayParamVarsAndLocalVarsItselfAndSubset.getLocalId(), findArrayParamVarsAndLocalVarsItselfAndSubset.getLocationEnumList(), findArrayParamVarsAndLocalVarsItselfAndSubset.getDataVarTypeList(), findArrayParamVarsAndLocalVarsItselfAndSubset.getIncludeDataValue(), findArrayParamVarsAndLocalVarsItselfAndSubset.getSessionId(), true, QUERY_TYPE_DEFAULT, RequestTypeEnum.INTERNAL, findArrayParamVarsAndLocalVarsItselfAndSubset.getContent(), findArrayParamVarsAndLocalVarsItselfAndSubset.getJsonSchemalList()));
    }

    /**
     * builderParametersNode
     *
     * @param parametersNode1 parametersNode1
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto builderParametersNode(ParametersNode parametersNode1) {
        List<DomainDataModelTreeDto> parametersItems = new ArrayList<>();

        //变量参数节点，根据筛选信息过滤
        parametersNode1.getParameters().stream().forEach(parameter -> {
            String direction = "";
            if (parametersNode1.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_INPUT) && parametersNode1.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_OUTPUT)) {
                log.info("parameter is both PARAMETER_INPUT and PARAMETER_OUTPUT");
            } else if (parametersNode1.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_INPUT)) {
                direction = PositionVarEnum.PARAMETERS_IN.getName();
            } else if (parametersNode1.getLocationEnumList().contains(TemplateVarLocationEnum.PARAMETER_OUTPUT)) {
                direction = PositionVarEnum.PARAMETERS_OUT.getName();
            }
            //组装当前节点信息
            if (StringUtils.isBlank(direction) || direction.equals(parameter.getDirection())) {
                List<DomainDataModelTreeDto> itemList = buildNodeItems(new NodeItems(parametersNode1.getType(), parametersNode1.getGlobalId(), parametersNode1.getIncludeDataValue(), parameter, PositionVarEnum.PARAMETERS, parametersNode1.getDataVarTypeList(), parametersNode1.getArray(), parametersNode1.getQueryType(), parametersNode1.getRequestType(), parametersNode1.getJsonSchemalList()));
                if (null != itemList && itemList.size() > 0) {
                    parametersItems.addAll(itemList);
                }
            }
        });
        //组装参数最外层object信息
        DomainDataModelTreeDto parametersNode = null;
        if (parametersItems.size() > 0) {
            parametersNode = DomainDataModelTreeDto.builder().name(PositionVarEnum.PARAMETERS.getName()).value(PositionVarEnum.PARAMETERS.getName()).describe("参数").isArr("0").isEmpty("0").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).build();
            parametersNode.setChildren(parametersItems);
        }

        return parametersNode;
    }

    /**
     * builderLocalVarsNode
     *
     * @param localVarsNode1 localVarsNode1
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto builderLocalVarsNode(LocalVarsNode localVarsNode1) {
        List<DomainDataModelTreeDto> localVarsItems = new ArrayList<>();
        localVarsNode1.getLocalVars().stream().forEach(localVar -> {
            List<DomainDataModelTreeDto> itemList = buildNodeItems(new NodeItems(localVarsNode1.getType(), localVarsNode1.getGlobalId(), localVarsNode1.getIncludeDataValue(), localVar, PositionVarEnum.LOCAL_VARS, localVarsNode1.getDataVarTypeList(), localVarsNode1.getArray(), localVarsNode1.getQueryType(), localVarsNode1.getRequestType(), localVarsNode1.getJsonSchemalList()));
            if (null != itemList && itemList.size() > 0) {
                localVarsItems.addAll(itemList);
            }
        });
        DomainDataModelTreeDto localVarsNode = null;
        if (localVarsItems.size() > 0) {
            localVarsNode = DomainDataModelTreeDto.builder().name(PositionVarEnum.LOCAL_VARS.getName()).value(PositionVarEnum.LOCAL_VARS.getName()).describe("本地变量").isArr("0").isEmpty("0").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).build();

            localVarsNode.setChildren(localVarsItems);
        }
        return localVarsNode;
    }

    /**
     * buildNodeItems
     *
     * @param nodeItems nodeItems
     * @return java.util.List
     */
    private List<DomainDataModelTreeDto> buildNodeItems(NodeItems nodeItems) {
        List<DomainDataModelTreeDto> itemList = new ArrayList<>();
        Boolean isUse = Boolean.FALSE;
        // 根据dataValue过滤
        if (null != nodeItems.getIncludeDataValue()) {
            String dataValue = nodeItems.getIncludeDataValue().getLeft();
            Boolean include = nodeItems.getIncludeDataValue().getRight();
            String repDataValue = dataValue.replace(nodeItems.getPositionVarEnum().getName() + ".", "");
            if (include && repDataValue.equals(nodeItems.getField().getName())) {
                isUse = Boolean.TRUE;
            } else if (!include && !repDataValue.equals(nodeItems.getField().getName())) {
                isUse = Boolean.TRUE;
            } else if (repDataValue.split(REGEX).length > 1) {
                String refObject = repDataValue.split(REGEX)[0];
                if (refObject.equals(nodeItems.getField().getName())) {
                    repDataValue = repDataValue.replaceFirst(refObject, nodeItems.getField().getType());
                    JSONObject allpathObject = new JSONObject();
                    allpathObject.put("dataValue", repDataValue);
                    allpathObject.put("fieldType", nodeItems.getField().getType());
                    allpathObject.put("fieldName", nodeItems.getField().getName());
                    DomainDataModelTreeDto treeDto = byPath(nodeItems.getType(), nodeItems.getGlobalId(), allpathObject, nodeItems.getPositionVarEnum(), nodeItems.getDataVarTypeList(), nodeItems.getJsonSchemalList());
                    boolean flag = (null != nodeItems.getArray() && nodeItems.getArray()) && (treeDto == null || CollectionUtils.isEmpty(treeDto.getChildren())) && Objects.requireNonNull(treeDto).getValue().equals(dataValue);
                    if (flag) {
                        return null;
                    }
                    if (null != treeDto) {
                        itemList.add(treeDto);
                    }
                    return itemList;
                }
            }
        } else {
            isUse = Boolean.TRUE;
        }
        if (!isUse) {
            return null;
        }
        boolean flag = nodeItems.getDataVarTypeList().isEmpty() || nodeItems.getDataVarTypeList().contains(DataVariableTypeEnum.getMessageEnum(nodeItems.getField().getType())) || (nodeItems.getDataVarTypeList().contains(DataVariableTypeEnum.ARRAY_TYPE) && nodeItems.getField().getIsArray());
        // 如果是对象,引用类型
        if (null == DataVariableTypeEnum.getMessageEnum(nodeItems.getField().getType())) {
            if (QUERY_TYPE_ITSELF_AND_SUBSET.equals(nodeItems.getQueryType())) {
                if (nodeItems.getArray() == null || !nodeItems.getField().getIsArray()) {
                    DomainDataModelTreeDto childrenItem = getSubset(nodeItems.getType(), nodeItems.getGlobalId(), nodeItems.getField(), null, nodeItems.getDataVarTypeList(), nodeItems.getPositionVarEnum(), nodeItems.getJsonSchemalList());
                    if (childrenItem != null) {
                        itemList.add(childrenItem);
                    }
                } else {
                    log.info("isArray != null || field.getIsArray()");
                }
            } else {
                // 如果查对象数组
                if (extractedFindObjectArray(new FindObjectArray(nodeItems.getType(), nodeItems.getGlobalId(), nodeItems.getIncludeDataValue(), nodeItems.getField(), nodeItems.getPositionVarEnum(), nodeItems.getDataVarTypeList(), nodeItems.getArray(), nodeItems.getQueryType(), nodeItems.getRequestType(), nodeItems.getJsonSchemalList(), itemList))) {
                    return null;
                }
            }
        } else if (flag) {
            // 变量类型为空、包含查找变量、查数组、如果是string查全部
            addVariableToListBasedOnConditions(nodeItems.getField(), nodeItems.getPositionVarEnum(), nodeItems.getDataVarTypeList(), nodeItems.getArray(), nodeItems.getQueryType(), itemList);
        }
        return itemList;
    }

    /**
     * extractedFindObjectArray
     *
     * @param findObjectArray findObjectArray
     * @return boolean
     */
    private boolean extractedFindObjectArray(FindObjectArray findObjectArray) {
        if (null != findObjectArray.getArray()) {
            //查找array_object
            if (findObjectArray.getDataVarTypeList().contains(DataVariableTypeEnum.OBJECT_TYPE)) {
                objectType(new ObjectType(findObjectArray.getType(), findObjectArray.getGlobalId(), findObjectArray.getField(), findObjectArray.getDataVarTypeList(), findObjectArray.getPositionVarEnum(), findObjectArray.getArray(), findObjectArray.getQueryType(), findObjectArray.getItemList(), findObjectArray.getJsonSchemalList()));
            } else if (findObjectArray.getArray()) {
                // 如果是数组，就返回
                // 找对象下面的array， 然后平铺 findTreeVarBaseArray
                addDataModelTreeToItemlist(findObjectArray.getType(), findObjectArray.getGlobalId(), findObjectArray.getField(), findObjectArray.getPositionVarEnum(), findObjectArray.getDataVarTypeList(), findObjectArray.getJsonSchemalList(), findObjectArray.getItemList());
            }
        } else if (findObjectArray.getDataVarTypeList().contains(DataVariableTypeEnum.ARRAY_TYPE)) {
            // 如果是数组，就返回
            processFieldArray(new FieldArray(findObjectArray.getType(), findObjectArray.getGlobalId(), findObjectArray.getField(), findObjectArray.getPositionVarEnum(), findObjectArray.getJsonSchemalList(), findObjectArray.getItemList()));
        } else {
            if (findObjectArray.getDataVarTypeList().contains(DataVariableTypeEnum.OBJECT_TYPE)) {
                if (Boolean.FALSE.equals(findObjectArray.getField().getIsArray())) {
                    DomainDataModelTreeDto childrenItem = getSubset(findObjectArray.getType(), findObjectArray.getGlobalId(), findObjectArray.getField(), null, findObjectArray.getDataVarTypeList(), findObjectArray.getPositionVarEnum(), findObjectArray.getJsonSchemalList());
                    if (childrenItem != null) {
                        findObjectArray.getItemList().add(childrenItem);
                    }
                }
            } else {
                if (findAndAddSubsetObjectToList(new AddSubsetObjectToList(findObjectArray.getType(), findObjectArray.getGlobalId(), findObjectArray.getIncludeDataValue(), findObjectArray.getField(), findObjectArray.getPositionVarEnum(), findObjectArray.getDataVarTypeList(), findObjectArray.getRequestType(), findObjectArray.getJsonSchemalList(), findObjectArray.getItemList()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * processFieldArray
     *
     * @param fieldArray fieldArray
     */
    private void processFieldArray(FieldArray fieldArray) {
        if (fieldArray.getField().getIsArray()) {
            /*
             * 1、value转换成对应的前缀 + name
             * 2、type取类型的最后一个
             */
            String value = MessageFormat.format("{0}.{1}", fieldArray.getPositionVarEnum().getName(), fieldArray.getField().getName());
            DomainDataModelTreeDto treeDto = buildDataJson(fieldArray.getField().getName(), value, fieldArray.getField().getType(), fieldArray.getField().getLabel(), DataVariableTypeEnum.OBJECT_TYPE.getMessage(), "1");
            fieldArray.getItemList().add(treeDto);
        } else {
            // 找对象下面的array， 然后平铺
            DomainDataModelTreeDto result = commonGlobalDataBiz.findDataValueAndTypeGetVarTree(DataValueAndTypeGetVarTreeInputDto.builder().type(fieldArray.getType().getType()).globalId(fieldArray.getGlobalId()).dataValue(fieldArray.getField().getType()).typeList(Arrays.asList(DataVariableTypeEnum.ARRAY_TYPE.getMessage())).jsonschemaDtoMap(fieldArray.getJsonSchemalList()).build());
            if (null != result && null != result.getChildren()) {
                for (DomainDataModelTreeDto cTreeDto : result.getChildren()) {
                    DomainDataModelTreeDto childrenItem = pathRefactor(cTreeDto, fieldArray.getField().getType(), MessageFormat.format("{0}.{1}", fieldArray.getPositionVarEnum().getName(), fieldArray.getField().getName()));
                    fieldArray.getItemList().add(childrenItem);
                }
            }
        }
    }

    /**
     * addDataModelTreeToItemlist
     *
     * @param type type
     * @param globalId globalId
     * @param field field
     * @param positionVarEnum positionVarEnum
     * @param dataVarTypeList dataVarTypeList
     * @param jsonSchemalList jsonSchemalList
     * @param itemList itemList
     */
    private void addDataModelTreeToItemlist(TemplateUnitTypeEnum type, Long globalId, ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, Map<String, DomainDataModelTreeDto> jsonSchemalList, List<DomainDataModelTreeDto> itemList) {
        DomainDataModelTreeDto result = commonGlobalDataBiz.findDataValueAndTypeGetArrayVarTree(DataValueAndTypeGetVarTreeInputDto.builder().type(type.getType()).globalId(globalId).dataValue(field.getType()).typeList(dataVarTypeList == null ? null : dataVarTypeList.stream().map(dataType -> dataType.getMessage()).collect(Collectors.toList())).jsonschemaDtoMap(jsonSchemalList).build());
        if (null != result && null != result.getChildren()) {
            if (dataVarTypeList.contains(DataVariableTypeEnum.ARRAY_TYPE)) {
                //只有array需要平铺
                for (DomainDataModelTreeDto cTreeDto : result.getChildren()) {
                    DomainDataModelTreeDto childrenItem = pathRefactor(cTreeDto, field.getType(), MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName()));
                    itemList.add(childrenItem);
                }
            } else {
                itemList.add(result);
            }
        }
    }

    /**
     * addVariableToListBasedOnConditions
     *
     * @param field field
     * @param positionVarEnum positionVarEnum
     * @param dataVarTypeList dataVarTypeList
     * @param isArray isArray
     * @param queryType queryType
     * @param itemList itemList
     */
    private void addVariableToListBasedOnConditions(ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, Boolean isArray, String queryType, List<DomainDataModelTreeDto> itemList) {
        if (null != isArray) {
            if (isArray) {
                if (field.getIsArray() && dataVarTypeList.contains(DataVariableTypeEnum.getMessageEnum(field.getType()))) {
                    String value = MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName());
                    itemList.add(buildDataJson(field.getName(), value, field.getType(), field.getLabel(), field.getType(), field.getIsArray() ? "1" : "0"));
                }
            } else {
                if (!field.getIsArray()) {
                    String value = MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName());
                    itemList.add(buildDataJson(field.getName(), value, field.getType(), field.getLabel(), field.getType(), field.getIsArray() ? "1" : "0"));
                }
            }
        } else if (dataVarTypeList.contains(DataVariableTypeEnum.ARRAY_TYPE) && field.getIsArray()) {
            String value = MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName());
            itemList.add(buildDataJson(field.getName(), value, field.getType(), field.getLabel(), field.getType(), "1"));
        } else {
            if (QUERY_TYPE_ITSELF_AND_SUBSET.equals(queryType) || !field.getIsArray()) {
                String value = MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName());
                itemList.add(buildDataJson(field.getName(), value, field.getType(), field.getLabel(), field.getType(), field.getIsArray() ? "1" : "0"));
            }
        }
    }

    /**
     * findAndAddSubsetObjectToList
     *
     * @param addSubsetObjectToList addSubsetObjectToList
     * @return boolean
     */
    private boolean findAndAddSubsetObjectToList(AddSubsetObjectToList addSubsetObjectToList) {
        // 基础类型
        if (addSubsetObjectToList.getField().getIsArray() != null && addSubsetObjectToList.getField().getIsArray()) {
            // 如果是对象数组
            if (RequestTypeEnum.INTERNAL.equals(addSubsetObjectToList.getRequestType()) && null != addSubsetObjectToList.getIncludeDataValue()) {
                DomainDataModelTreeDto childrenItem = getSubset(addSubsetObjectToList.getType(), addSubsetObjectToList.getGlobalId(), addSubsetObjectToList.getField(), null, addSubsetObjectToList.getDataVarTypeList(), addSubsetObjectToList.getPositionVarEnum(), addSubsetObjectToList.getJsonSchemalList());
                if (childrenItem != null) {
                    addSubsetObjectToList.getItemList().add(childrenItem);
                }
            } else {
                return true;
            }
        } else {
            DomainDataModelTreeDto childrenItem = getSubset(addSubsetObjectToList.getType(), addSubsetObjectToList.getGlobalId(), addSubsetObjectToList.getField(), null, addSubsetObjectToList.getDataVarTypeList(), addSubsetObjectToList.getPositionVarEnum(), addSubsetObjectToList.getJsonSchemalList());
            if (childrenItem != null) {
                addSubsetObjectToList.getItemList().add(childrenItem);
            }
        }
        return false;
    }

    /**
     * byPath
     *
     * @param type type
     * @param globalId globalId
     * @param allpathObject allpathObject
     * @param positionVarEnum positionVarEnum
     * @param dataVarTypeList dataVarTypeList
     * @param jsonSchemalList jsonSchemalList
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto byPath(TemplateUnitTypeEnum type, Long globalId, JSONObject allpathObject, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
        return getSubset(type, globalId, null, allpathObject, dataVarTypeList, positionVarEnum, jsonSchemalList);
    }

    /**
     * objectType
     *
     * @param objectType objectType
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto objectType(ObjectType objectType) {
        DomainDataModelTreeDto treeDto = null;
        if (objectType.getArray() && objectType.getField().getIsArray()) {
            /*
             * 1、value转换成对应的前缀 + name
             * 2、type取类型的最后一个
             */
            String value = MessageFormat.format("{0}.{1}", objectType.getPositionVarEnum().getName(), objectType.getField().getName());
            treeDto = buildDataJson(objectType.getField().getName(), value, objectType.getField().getType(), objectType.getField().getLabel(), DataVariableTypeEnum.OBJECT_TYPE.getMessage(), objectType.getField().getIsArray() ? "1" : "0");
            objectType.getItemList().add(treeDto);
        } else if (objectType.getArray() && !objectType.getField().getIsArray()) {
            // 找对象下面的array， 不再平铺 展示树形结构
            DomainDataModelTreeDto result = commonGlobalDataBiz.findDataValueAndTypeGetArrayVarTree(DataValueAndTypeGetVarTreeInputDto.builder().type(objectType.getType().getType()).globalId(objectType.getGlobalId()).dataValue(objectType.getField().getType()).typeList(Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage())).jsonschemaDtoMap(objectType.getJsonSchemalList()).build());
            if (null != result && null != result.getChildren()) {
                objectType.getItemList().add(result);
            }
        } else if (!objectType.getArray() && !objectType.getField().getIsArray()) {
            treeDto = getSubset(objectType.getType(), objectType.getGlobalId(), objectType.getField(), null, objectType.getDataVarTypeList(), objectType.getPositionVarEnum(), objectType.getJsonSchemalList());
            objectType.getItemList().add(treeDto);
        }
        return treeDto;
    }

    /**
     * getSubset
     *
     * @param type type
     * @param globalId globalId
     * @param field field
     * @param allpathObject allpathObject
     * @param dataVarTypeList dataVarTypeList
     * @param positionVarEnum positionVarEnum
     * @param jsonSchemalList jsonSchemalList
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto getSubset(TemplateUnitTypeEnum type, Long globalId, ComponentJsonDto.Field field, JSONObject allpathObject, List<DataVariableTypeEnum> dataVarTypeList, PositionVarEnum positionVarEnum, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
        List<String> typeList = new ArrayList<>();
        for (DataVariableTypeEnum typeEnum : dataVarTypeList) {
            typeList.add(typeEnum.getMessage());
        }
        // 根据dataType过滤
        DataValueAndTypeGetVarTreeInputDto inputDto = new DataValueAndTypeGetVarTreeInputDto();
        inputDto.setType(type.getType());
        inputDto.setGlobalId(globalId);
        inputDto.setTypeList(typeList);
        inputDto.setJsonschemaDtoMap(jsonSchemalList);
        // 全路径对象
        if (null != allpathObject) {
            inputDto.setDataValue(allpathObject.getString("dataValue"));

            DomainDataModelTreeDto dataModelTreeDto = commonGlobalDataBiz.findDataValueAndTypeGetVarTree(inputDto);
            if (null != dataModelTreeDto) {
                DomainDataModelTreeDto childrenItem = pathRefactor(dataModelTreeDto, allpathObject.getString("fieldType"), MessageFormat.format("{0}.{1}", positionVarEnum.getName(), allpathObject.getString("fieldName")));
                return childrenItem;
            }
        } else {
            inputDto.setDataValue(field.getType());
            DomainDataModelTreeDto dataModelTreeDto = commonGlobalDataBiz.findDataValueAndTypeGetVarTree(inputDto);
            if (null != dataModelTreeDto) {
                dataModelTreeDto.setName(field.getName());
                dataModelTreeDto.setDescribe(field.getLabel());
                dataModelTreeDto.setIsArr(field.getIsArray() ? "1" : "0");
                dataModelTreeDto.setTypeRef(getTypeRef(field.getType(), dataModelTreeDto.getType()));
                // 洗数据
                DomainDataModelTreeDto childrenItem = pathRefactor(dataModelTreeDto, field.getType(), MessageFormat.format("{0}.{1}", positionVarEnum.getName(), field.getName()));
                return childrenItem;
            }
        }
        return null;
    }

    /**
     * getTypeRef
     *
     * @param type type
     * @param typeRef typeRef
     * @return java.lang.String
     */
    private String getTypeRef(String type, String typeRef) {
        List<DataVariableTypeEnum> baseTypeList = new ArrayList<>();
        baseTypeList.add(DataVariableTypeEnum.INT_TYPE);
        baseTypeList.add(DataVariableTypeEnum.DOUBLE_TYPE);
        baseTypeList.add(DataVariableTypeEnum.BOOLEAN_TYPE);
        baseTypeList.add(DataVariableTypeEnum.STRING_TYPE);
        baseTypeList.add(DataVariableTypeEnum.DATE_TYPE);
        baseTypeList.add(DataVariableTypeEnum.DATETIME_TYPE);
        if (DataVariableTypeEnum.getMessageEnum(type) != null && baseTypeList.contains(DataVariableTypeEnum.getMessageEnum(type))) {
            return type;
        } else if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(typeRef)) {
            return type;
        }
        return typeRef;
    }

    /**
     * pathRefactor
     *
     * @param dataModelTreeDto dataModelTreeDto
     * @param targetType targetType
     * @param replacement replacement
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto pathRefactor(DomainDataModelTreeDto dataModelTreeDto, String targetType, String replacement) {
        String newValue = dataModelTreeDto.getValue().replaceFirst(targetType, replacement);
        dataModelTreeDto.setValue(newValue);
        dataModelTreeDto.setTypeRef(getTypeRef(dataModelTreeDto.getType(), dataModelTreeDto.getTypeRef()));
        if (null == DataVariableTypeEnum.getMessageEnum(dataModelTreeDto.getType())) {
            dataModelTreeDto.setType(DataVariableTypeEnum.OBJECT_TYPE.getMessage());
        }
        List<DomainDataModelTreeDto> list = dataModelTreeDto.getChildren();
        if (null != list && list.size() > 0) {
            for (DomainDataModelTreeDto cmt : list) {
                pathRefactor(cmt, targetType, replacement);
            }
        }
        return dataModelTreeDto;
    }

    /**
     * buildDataJson
     *
     * @param name name
     * @param value value
     * @param fieldType fieldType
     * @param describe describe
     * @param type type
     * @param isArr isArr
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto buildDataJson(String name, String value, String fieldType, String describe, String type, String isArr) {
        return DomainDataModelTreeDto.builder().name(name).value(value).type(type).describe(describe).typeRef(getTypeRef(type, fieldType)).isArr(isArr).isEmpty("0").build();
    }

    /**
     * getProviderType
     *
     * @param templateType templateType
     * @param globalId globalId
     * @param localId localId
     * @param dataValue dataValue
     * @param fullPath fullPath
     * @param sessionId sessionId
     * @return java.lang.String
     */
    private String getProviderType(TemplateUnitTypeEnum templateType, Long globalId, Long localId, String dataValue, Boolean fullPath, String sessionId) {
        String providerType = "";
        String ruleContent = getContent(templateType, globalId, localId, sessionId);
        ComponentJsonDto componentJsonDto = JSONObject.parseObject(ruleContent, ComponentJsonDto.class);
        ComponentJsonDto.BaseData baseData = componentJsonDto.getBaseData();
        ComponentJsonDto.DataModel dataModel = baseData.getDataModel();

        String fullDataValue = dataValue;
        if (dataValue.startsWith(PositionVarEnum.PARAMETERS.getName())) {
            dataValue = dataValue.replace(PositionVarEnum.PARAMETERS.getName() + ".", "");
            List<ComponentJsonDto.Parameter> parameters = dataModel.getParameters();
            for (ComponentJsonDto.Parameter parameter : parameters) {
                String type = queryType(parameter.getName(), parameter.getType(), dataValue, fullPath, fullDataValue);
                if (StringUtils.isNotBlank(type)) {
                    providerType = type;
                    break;
                }
            }
        } else if (dataValue.startsWith(PositionVarEnum.LOCAL_VARS.getName())) {
            dataValue = dataValue.replace(PositionVarEnum.LOCAL_VARS.getName() + ".", "");
            List<ComponentJsonDto.LocalVar> localVars = dataModel.getLocalVars();
            for (ComponentJsonDto.LocalVar localVar : localVars) {
                String type = queryType(localVar.getName(), localVar.getType(), dataValue, fullPath, fullDataValue);
                if (StringUtils.isNotBlank(type)) {
                    providerType = type;
                    break;
                }
            }
        } else {
            providerType = commonGlobalDataBiz.findDyDataValueGetVarType(DataValueGetVarTypeInputDto.builder().type(templateType.getType()).globalId(globalId).dataValue(dataValue).build());
        }

        return providerType;
    }

    /**
     * queryType
     *
     * @param name name
     * @param type type
     * @param dataValue dataValue
     * @param fullPath fullPath
     * @param fullDataValue fullDataValue
     * @return java.lang.String
     */
    private String queryType(String name, String type, String dataValue, Boolean fullPath, String fullDataValue) {
        String[] dataValues = dataValue.split(REGEX);
        if (!name.equals(dataValues[0])) {
            return null;
        }
        if (fullPath) {
            return dataValue.replace(dataValues[0], type);
        } else {
            if (null == DataVariableTypeEnum.getMessageEnum(type)) {
                return DataVariableTypeEnum.OBJECT_TYPE.getMessage();
            } else {
                return type;
            }
        }
    }

    /**
     * getContent
     *
     * @param type type
     * @param globalId globalId
     * @param localId localId
     * @param sessionId sessionId
     * @return java.lang.String
     */

    public String getContent(TemplateUnitTypeEnum type, Long globalId, Long localId, String sessionId) {
        if (type == TemplateUnitTypeEnum.SPACE_VARIABLE) {
            //type 2:空间变量
            return getVariableContent(globalId, localId, sessionId);
        } else if (type == TemplateUnitTypeEnum.SPACE_COMMON_FUNCTION) {
            //type 3:空间公共函数
            return getFunctionContent(globalId, localId, sessionId);
        }
        return null;
    }

    /**
     * fillParamLocalTreeDto
     *
     * @param type type
     * @param globalId globalId
     * @param localId localId
     * @param componentJsonDto componentJsonDto
     * @return Map
     */
    public Map<String, DomainDataModelTreeDto> fillParamLocalTreeDto(TemplateUnitTypeEnum type, Long globalId, Long localId, ComponentJsonDto componentJsonDto) {
        if (componentJsonDto == null) {
            return null;
        }
        ComponentJsonDto.BaseData baseData = componentJsonDto.getBaseData();
        List<ComponentJsonDto.Parameter> parameters = null;
        List<ComponentJsonDto.LocalVar> localVars = null;
        if (null != baseData && null != baseData.getDataModel()) {
            ComponentJsonDto.DataModel dataModel = baseData.getDataModel();
            parameters = dataModel.getParameters();
            localVars = dataModel.getLocalVars();
        }
        Map<String, DomainDataModelTreeDto> map = new HashMap<>(MagicNumbers.EIGHT);
        List<DomainDataModelTreeDto> parametersItems = new ArrayList<>();
        //变量参数节点，根据筛选信息过滤
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.stream().forEach(parameter -> {
                parametersItems.add(buildParamLocalTreeDto(new ParamLocalTreeDto(type, globalId, PositionVarEnum.PARAMETERS, parameter.getName(), parameter.getType(), parameter.getLabel(), parameter.getIsArray(), ObjectTypeEnum.fromName(parameter.getObjectType()), parameter.getJavaType())));
            });
            //组装参数最外层object信息
            DomainDataModelTreeDto parametersNode = DomainDataModelTreeDto.builder().name(PositionVarEnum.PARAMETERS.getName()).value(PositionVarEnum.PARAMETERS.getName()).describe("参数").isArr("0").isEmpty("0").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).build();
            parametersNode.setChildren(parametersItems);
            map.put(PositionVarEnum.PARAMETERS.getName(), parametersNode);
        }

        List<DomainDataModelTreeDto> localVarsItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(localVars)) {

            localVars.stream().forEach(localVar -> {
                localVarsItems.add(buildParamLocalTreeDto(new ParamLocalTreeDto(type, globalId, PositionVarEnum.LOCAL_VARS, localVar.getName(), localVar.getType(), localVar.getLabel(), localVar.getIsArray(), ObjectTypeEnum.fromName(localVar.getObjectType()), localVar.getJavaType())));
                DomainDataModelTreeDto localVarsNode = DomainDataModelTreeDto.builder().name(PositionVarEnum.LOCAL_VARS.getName()).value(PositionVarEnum.LOCAL_VARS.getName()).describe("本地变量").isArr("0").isEmpty("0").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).build();

                localVarsNode.setChildren(localVarsItems);
                map.put(PositionVarEnum.LOCAL_VARS.getName(), localVarsNode);
            });
        }
        return map;
    }

    /**
     * fillParamInOrOutTreeDto
     *
     * @param type type
     * @param globalId globalId
     * @param localId localId
     * @param componentJsonDto componentJsonDto
     * @param positionVarEnum positionVarEnum
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto fillParamInOrOutTreeDto(TemplateUnitTypeEnum type, Long globalId, Long localId, ComponentJsonDto componentJsonDto, PositionVarEnum positionVarEnum) {
        if (componentJsonDto == null) {
            return null;
        }
        ComponentJsonDto.BaseData baseData = componentJsonDto.getBaseData();
        List<ComponentJsonDto.Parameter> parameters = null;
        if (null != baseData && null != baseData.getDataModel()) {
            ComponentJsonDto.DataModel dataModel = baseData.getDataModel();
            parameters = dataModel.getParameters();
        }
        List<DomainDataModelTreeDto> parametersItems = new ArrayList<>();
        //变量参数节点，根据筛选信息过滤
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.stream().forEach(parameter -> {
                if (positionVarEnum.getName().equalsIgnoreCase(parameter.getDirection())) {
                    parametersItems.add(buildParamLocalTreeDto(new ParamLocalTreeDto(type, globalId, PositionVarEnum.PARAMETERS, parameter.getName(), parameter.getType(), parameter.getLabel(), parameter.getIsArray(), ObjectTypeEnum.fromName(parameter.getObjectType()), parameter.getJavaType())));

                }
            });
            //组装参数最外层object信息
            DomainDataModelTreeDto parametersNode = DomainDataModelTreeDto.builder().name(PositionVarEnum.PARAMETERS.getName()).value(PositionVarEnum.PARAMETERS.getName()).describe("参数").isArr("0").isEmpty("0").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).build();
            parametersNode.setChildren(parametersItems);
            return parametersNode;
        }

        return null;
    }

    /**
     * buildParamLocalTreeDto
     *
     * @param paramLocalTreeDto paramLocalTreeDto
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    private DomainDataModelTreeDto buildParamLocalTreeDto(ParamLocalTreeDto paramLocalTreeDto) {
        String value = MessageFormat.format("{0}.{1}", paramLocalTreeDto.getPositionVarEnum().getName(), paramLocalTreeDto.getName());
        if (DataVariableTypeEnum.getMessageEnum(paramLocalTreeDto.getFieldType()) != null) {
            return buildDataJson(paramLocalTreeDto.getName(), value, paramLocalTreeDto.getFieldType(), paramLocalTreeDto.getLabel(), paramLocalTreeDto.getFieldType(), paramLocalTreeDto.getArray() ? "1" : "0");
        }
        DomainDataModelTreeDto domainDataModelTreeDto = null;
        if (paramLocalTreeDto.getObjectTypeEnum() == ObjectTypeEnum.REF) {
            //引入Java类型
            String javaObjectJsonschemal = javaToolKitBiz.getClassJsonSchemaByExistingJavaType(paramLocalTreeDto.getJavaType());
            if (StringUtils.isEmpty(javaObjectJsonschemal)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, MessageFormat.format("参数本地变量【{0}】引用类型【{1}】不存在", value, paramLocalTreeDto.getJavaType()));
            }
            domainDataModelTreeDto = DomainModelTreeEntityUtils.getByContentStaticTemplateTree(javaObjectJsonschemal);

        } else {
            domainDataModelTreeDto = commonGlobalDataBiz.findProperty(paramLocalTreeDto.getType(), paramLocalTreeDto.getGlobalId(), paramLocalTreeDto.getFieldType());
        }
        if (Objects.isNull(domainDataModelTreeDto)) {
            return DomainDataModelTreeDto.builder().build();
        }
        pathRefactor(domainDataModelTreeDto, paramLocalTreeDto.getFieldType(), value);
        domainDataModelTreeDto.setName(paramLocalTreeDto.getName());
        domainDataModelTreeDto.setDescribe(paramLocalTreeDto.getLabel());
        domainDataModelTreeDto.setLabel(MessageFormat.format("{0}-{1}", paramLocalTreeDto.getName(), paramLocalTreeDto.getLabel()));
        domainDataModelTreeDto.setIsArr(paramLocalTreeDto.getArray() ? "1" : "0");
        return domainDataModelTreeDto;

    }

    /**
     * getFunctionContent
     *
     * @param globalId globalId
     * @param localId localId
     * @param sessionId sessionId
     * @return java.lang.String
     */
    private String getFunctionContent(Long globalId, Long localId, String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                    .select(VarProcessFunction::getContent)
                    .eq(VarProcessFunction::getId, localId));
            if (varProcessFunction == null || StringUtils.isEmpty(varProcessFunction.getContent())) {
                return null;
            }
            return varProcessFunction.getContent();
        }
        VarProcessFunctionCache varProcessVariableCache = varProcessFunctionCacheService.getOne(new QueryWrapper<VarProcessFunctionCache>().lambda().eq(VarProcessFunctionCache::getFunctionId, localId).eq(VarProcessFunctionCache::getSessionId, sessionId));
        if (varProcessVariableCache == null || StringUtils.isEmpty(varProcessVariableCache.getContent())) {
            return null;
        }
        return varProcessVariableCache.getContent();
    }

    /**
     * getVariableContent
     *
     * @param globalId globalId
     * @param localId localId
     * @param sessionId sessionId
     * @return java.lang.String
     */
    private String getVariableContent(Long globalId, Long localId, String sessionId) {

        if (StringUtils.isBlank(sessionId)) {
            if (localId == null) {
                return null;
            }
            VarProcessVariable varProcessVariable = varProcessVariableService.getById(localId);
            if (varProcessVariable == null || StringUtils.isEmpty(varProcessVariable.getContent())) {
                return null;
            }
            return varProcessVariable.getContent();
        }
        VarProcessVariableCache varProcessVariableCache = varProcessVariableCacheService.getOne(new QueryWrapper<VarProcessVariableCache>().lambda().eq(VarProcessVariableCache::getVariableId, localId).eq(VarProcessVariableCache::getSessionId, sessionId));
        if (varProcessVariableCache == null || StringUtils.isEmpty(varProcessVariableCache.getContent())) {
            return null;
        }
        return varProcessVariableCache.getContent();
    }

    private static final class ParamVarsAndLocalVars {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final String sessionId;
        private final Boolean isArray;
        private final String queryType;
        private final RequestTypeEnum requestType;
        private final Object ruleContent;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        private ParamVarsAndLocalVars(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, String sessionId, Boolean isArray, String queryType, RequestTypeEnum requestType, Object ruleContent, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.sessionId = sessionId;
            this.isArray = isArray;
            this.queryType = queryType;
            this.requestType = requestType;
            this.ruleContent = ruleContent;
            this.jsonSchemalList = jsonSchemalList;
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

        public List<TemplateVarLocationEnum> getLocationEnumList() {
            return locationEnumList;
        }

        public List<DataVariableTypeEnum> getDataVarTypeList() {
            return dataVarTypeList;
        }

        public Pair<String, Boolean> getIncludeDataValue() {
            return includeDataValue;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Boolean getArray() {
            return isArray;
        }

        public String getQueryType() {
            return queryType;
        }

        public RequestTypeEnum getRequestType() {
            return requestType;
        }

        public Object getRuleContent() {
            return ruleContent;
        }

        public Map<String, DomainDataModelTreeDto> getJsonSchemalList() {
            return jsonSchemalList;
        }

        public void setDataVarTypeList(List<DataVariableTypeEnum> dataVarTypeList) {
            this.dataVarTypeList = dataVarTypeList;
        }
    }

    @Data
    static final class ParamVarsAndLocalVarsByDataValue {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final String dataValue;
        private final String sessionId;
        private final Object content;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        ParamVarsAndLocalVarsByDataValue(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, String dataValue, String sessionId, Object content, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.dataValue = dataValue;
            this.sessionId = sessionId;
            this.content = content;
            this.jsonSchemalList = jsonSchemalList;
        }
    }

    @Data
    static final class ArrayParamVarsAndLocalVars {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final String sessionId;
        private final Object content;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        /**
         * @param globalId globalId
         * @param localId localId
         * @param locationEnumList locationEnumList
         * @param dataVarTypeList dataVarTypeList
         * @param includeDataValue 路径，是否包含该路径
         */
        ArrayParamVarsAndLocalVars(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, String sessionId, Object content, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.sessionId = sessionId;
            this.content = content;
            this.jsonSchemalList = jsonSchemalList;
        }
    }

    @Data
    private static final class ParamVarsAndLocalVarsItselfAndSubset {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final String sessionId;
        private final Object content;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        private ParamVarsAndLocalVarsItselfAndSubset(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, String sessionId, Object content, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.sessionId = sessionId;
            this.content = content;
            this.jsonSchemalList = jsonSchemalList;
        }
    }

    @Data
    private static final class FindArrayParamVarsAndLocalVarsItselfAndSubset {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Long localId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final String sessionId;
        private final Object content;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        private FindArrayParamVarsAndLocalVarsItselfAndSubset(TemplateUnitTypeEnum type, Long globalId, Long localId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, String sessionId, Object content, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.localId = localId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.sessionId = sessionId;
            this.content = content;
            this.jsonSchemalList = jsonSchemalList;
        }
    }

    @Data
    private static final class ParametersNode {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final List<TemplateVarLocationEnum> locationEnumList;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final List<ComponentJsonDto.Parameter> parameters;
        private final Boolean isArray;
        private final String queryType;
        private final RequestTypeEnum requestType;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        private final List<ComponentJsonDto.LocalVar> localVars;

        /**
         *
         */
        private ParametersNode(TemplateUnitTypeEnum type, Long globalId, List<TemplateVarLocationEnum> locationEnumList, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, List<ComponentJsonDto.Parameter> parameters, Boolean isArray, String queryType, RequestTypeEnum requestType, Map<String, DomainDataModelTreeDto> jsonSchemalList, List<ComponentJsonDto.LocalVar> localVars) {
            this.type = type;
            this.globalId = globalId;
            this.locationEnumList = locationEnumList;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.parameters = parameters;
            this.isArray = isArray;
            this.queryType = queryType;
            this.requestType = requestType;
            this.jsonSchemalList = jsonSchemalList;
            this.localVars = localVars;
        }

        public Boolean getArray() {
            return isArray;
        }
    }

    @Data
    private static final class LocalVarsNode {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Pair<String, Boolean> includeDataValue;
        private final List<ComponentJsonDto.LocalVar> localVars;
        private final Boolean isArray;
        private final String queryType;
        private final RequestTypeEnum requestType;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        /**
         *
         */
        private LocalVarsNode(TemplateUnitTypeEnum type, Long globalId, List<DataVariableTypeEnum> dataVarTypeList, Pair<String, Boolean> includeDataValue, List<ComponentJsonDto.LocalVar> localVars, Boolean isArray, String queryType, RequestTypeEnum requestType, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.dataVarTypeList = dataVarTypeList;
            this.includeDataValue = includeDataValue;
            this.localVars = localVars;
            this.isArray = isArray;
            this.queryType = queryType;
            this.requestType = requestType;
            this.jsonSchemalList = jsonSchemalList;
        }

        public Boolean getArray() {
            return isArray;
        }
    }

    @Data
    private static final class NodeItems {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Pair<String, Boolean> includeDataValue;
        private final ComponentJsonDto.Field field;
        private final PositionVarEnum positionVarEnum;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Boolean isArray;
        private final String queryType;
        private final RequestTypeEnum requestType;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        /**
         * @param globalId
         * @param includeDataValue 查找的节点
         * @param field            本节点信息
         * @param positionVarEnum  pramaters, locaVars
         * @param dataVarTypeList  节点类型
         * @param isArray          查找是否数组的节点，遇到数组返回
         * @param queryType        查找模式：default默认，itselfAndSubset查自身以及自己（配合dataVarTypeList使用，isArray失效）
         */
        private NodeItems(TemplateUnitTypeEnum type, Long globalId, Pair<String, Boolean> includeDataValue, ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, Boolean isArray, String queryType, RequestTypeEnum requestType, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.includeDataValue = includeDataValue;
            this.field = field;
            this.positionVarEnum = positionVarEnum;
            this.dataVarTypeList = dataVarTypeList;
            this.isArray = isArray;
            this.queryType = queryType;
            this.requestType = requestType;
            this.jsonSchemalList = jsonSchemalList;
        }

        public Boolean getArray() {
            return isArray;
        }
    }

    @Data
    private static final class FindObjectArray {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Pair<String, Boolean> includeDataValue;
        private final ComponentJsonDto.Field field;
        private final PositionVarEnum positionVarEnum;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final Boolean isArray;
        private final String queryType;
        private final RequestTypeEnum requestType;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final List<DomainDataModelTreeDto> itemList;

        private FindObjectArray(TemplateUnitTypeEnum type, Long globalId, Pair<String, Boolean> includeDataValue, ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, Boolean isArray, String queryType, RequestTypeEnum requestType, Map<String, DomainDataModelTreeDto> jsonSchemalList, List<DomainDataModelTreeDto> itemList) {
            this.type = type;
            this.globalId = globalId;
            this.includeDataValue = includeDataValue;
            this.field = field;
            this.positionVarEnum = positionVarEnum;
            this.dataVarTypeList = dataVarTypeList;
            this.isArray = isArray;
            this.queryType = queryType;
            this.requestType = requestType;
            this.jsonSchemalList = jsonSchemalList;
            this.itemList = itemList;
        }

        public Boolean getArray() {
            return isArray;
        }
    }

    @Data
    private static final class FieldArray {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final ComponentJsonDto.Field field;
        private final PositionVarEnum positionVarEnum;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final List<DomainDataModelTreeDto> itemList;

        private FieldArray(TemplateUnitTypeEnum type, Long globalId, ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, Map<String, DomainDataModelTreeDto> jsonSchemalList, List<DomainDataModelTreeDto> itemList) {
            this.type = type;
            this.globalId = globalId;
            this.field = field;
            this.positionVarEnum = positionVarEnum;
            this.jsonSchemalList = jsonSchemalList;
            this.itemList = itemList;
        }
    }

    @Data
    private static final class AddSubsetObjectToList {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final Pair<String, Boolean> includeDataValue;
        private final ComponentJsonDto.Field field;
        private final PositionVarEnum positionVarEnum;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final RequestTypeEnum requestType;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;
        private final List<DomainDataModelTreeDto> itemList;

        private AddSubsetObjectToList(TemplateUnitTypeEnum type, Long globalId, Pair<String, Boolean> includeDataValue, ComponentJsonDto.Field field, PositionVarEnum positionVarEnum, List<DataVariableTypeEnum> dataVarTypeList, RequestTypeEnum requestType, Map<String, DomainDataModelTreeDto> jsonSchemalList, List<DomainDataModelTreeDto> itemList) {
            this.type = type;
            this.globalId = globalId;
            this.includeDataValue = includeDataValue;
            this.field = field;
            this.positionVarEnum = positionVarEnum;
            this.dataVarTypeList = dataVarTypeList;
            this.requestType = requestType;
            this.jsonSchemalList = jsonSchemalList;
            this.itemList = itemList;
        }
    }

    @Data
    private static final class ObjectType {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final ComponentJsonDto.Field field;
        private final List<DataVariableTypeEnum> dataVarTypeList;
        private final PositionVarEnum positionVarEnum;
        private final Boolean isArray;
        private final String queryType;
        private final List<DomainDataModelTreeDto> itemList;
        private final Map<String, DomainDataModelTreeDto> jsonSchemalList;

        private ObjectType(TemplateUnitTypeEnum type, Long globalId, ComponentJsonDto.Field field, List<DataVariableTypeEnum> dataVarTypeList, PositionVarEnum positionVarEnum, Boolean isArray, String queryType, List<DomainDataModelTreeDto> itemList, Map<String, DomainDataModelTreeDto> jsonSchemalList) {
            this.type = type;
            this.globalId = globalId;
            this.field = field;
            this.dataVarTypeList = dataVarTypeList;
            this.positionVarEnum = positionVarEnum;
            this.isArray = isArray;
            this.queryType = queryType;
            this.itemList = itemList;
            this.jsonSchemalList = jsonSchemalList;
        }

        public Boolean getArray() {
            return isArray;
        }
    }

    @Data
    private static final class ParamLocalTreeDto {
        private final TemplateUnitTypeEnum type;
        private final Long globalId;
        private final PositionVarEnum positionVarEnum;
        private final String name;
        private final String fieldType;
        private final String label;
        private final Boolean isArray;
        private final ObjectTypeEnum objectTypeEnum;
        private final String javaType;

        private ParamLocalTreeDto(TemplateUnitTypeEnum type, Long globalId, PositionVarEnum positionVarEnum, String name, String fieldType, String label, Boolean isArray, ObjectTypeEnum objectTypeEnum, String javaType) {
            this.type = type;
            this.globalId = globalId;
            this.positionVarEnum = positionVarEnum;
            this.name = name;
            this.fieldType = fieldType;
            this.label = label;
            this.isArray = isArray;
            this.objectTypeEnum = objectTypeEnum;
            this.javaType = javaType;
        }

        public Boolean getArray() {
            return isArray;
        }
    }
}
