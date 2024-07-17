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
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.DataModelMatchTreeInputDto;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.converter.DynamicTreeConverter;
import com.wiseco.var.process.app.server.service.dto.input.DataValueAndTypeGetVarTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.StaticTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.TreeVarBaseArrayInputDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 变量空间通用方法 业务实现
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/18
 */
@Slf4j
@Service
public class VariableCommonBiz {

    @Autowired
    private CommonGlobalDataBiz commonGlobalDataBiz;

    @Autowired
    private VarProcessVariableReferenceService varProcessVariableReferenceService;

    @Autowired
    private AuthService authService;

    @Autowired
    private VarProcessDataModelService varProcessDataModelService;

    @Autowired
    private DynamicTreeConverter dynamicTreeConverter;

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Value("${spring.datasourcetype:mysql}")
    private String dataSourceType;

    /**
     * 获取变量空间中所有变量的名称列表
     *
     * @param spaceId            空间编号
     * @param locationEnumList   变量空间位置列表
     * @param dataVarTypeList    变量数据基础类型枚举
     * @param targetVariablePath 目标变量的路径
     * @param isArray            是否为数组
     * @param sessionId          会话Id
     * @return 决策领域树形结构实体的list
     */
    public List<DomainDataModelTreeDto> findDataVariable(Long spaceId,
                                                         List<TemplateVarLocationEnum> locationEnumList,
                                                         List<DataVariableTypeEnum> dataVarTypeList,
                                                         Pair<String, Boolean> targetVariablePath,
                                                         Boolean isArray,
                                                         String sessionId) {
        // 确定变量树查询范围
        List<DomainModelSheetNameEnum> positionEnumList = new ArrayList<>();
        locationEnumList.forEach(locationEnum -> {
            if (locationEnum == TemplateVarLocationEnum.INPUT) {
                positionEnumList.add(DomainModelSheetNameEnum.INPUT);
            } else if (locationEnum == TemplateVarLocationEnum.EXTERNAL_DATA) {
                positionEnumList.add(DomainModelSheetNameEnum.EXTERNAL_DATA);
            } else if (locationEnum == TemplateVarLocationEnum.INTERNAL_DATA) {
                positionEnumList.add(DomainModelSheetNameEnum.INTERNAL_DATA);
            }
        });

        // 前端返回结果
        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();

        // 获取输入, 外部服务数据, 内部数据
        if (locationEnumList.contains(TemplateVarLocationEnum.INPUT)
                || locationEnumList.contains(TemplateVarLocationEnum.EXTERNAL_DATA)
                || locationEnumList.contains(TemplateVarLocationEnum.INTERNAL_DATA)) {
            String variablePath = null != targetVariablePath ? targetVariablePath.getLeft() : null;
            // 获取输入输出引擎变量
            List<DomainDataModelTreeDto> result = findVarProcessDataModelVariableTree(spaceId, positionEnumList,
                    dataVarTypeList, variablePath, isArray);
            if (null != result) {
                treeDtoList.addAll(result);
            }
        }

        return treeDtoList;
    }

    /**
     * findVarProcessDataModelVariableTree
     *
     * @param spaceId                  变量空间Id
     * @param dataModelSectionEnumList 数据变量名称，其中input、output、engineVars三个表示变量导入的时候用于检测sheetName的名称
     * @param dataVarTypeList          变量数据基础类型的list集合
     * @param variablePath             变量路径
     * @param isArray                  是否为数组
     * @return 决策领域树形结构实体的list集合
     */
    public List<DomainDataModelTreeDto> findVarProcessDataModelVariableTree(Long spaceId, List<DomainModelSheetNameEnum> dataModelSectionEnumList,
                                                                            List<DataVariableTypeEnum> dataVarTypeList, String variablePath,
                                                                            Boolean isArray) {
        // 向前端返回的树形结构 DTO 列表
        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();
        // 确定待查询的数据模型部分: input / internalData / externalData
        List<String> dataModelSectionList = new ArrayList<>();
        for (DomainModelSheetNameEnum paramType : dataModelSectionEnumList) {
            dataModelSectionList.add(paramType.getMessage());
        }
        // 确定待查询的数据类型
        List<String> typeList = new ArrayList<>();
        for (DataVariableTypeEnum typeEnum : dataVarTypeList) {
            typeList.add(typeEnum.getMessage());
        }

        if (StringUtils.isBlank(variablePath)) {
            List<DomainDataModelTreeDto> result = null;
            if (null != isArray && isArray) {
                com.wiseco.var.process.app.server.service.dto.input.TreeVarBaseArrayInputDto inputDto = new TreeVarBaseArrayInputDto();
                inputDto.setType(MagicNumbers.TWO);
                inputDto.setSpaceId(spaceId);
                inputDto.setPositionList(dataModelSectionList);
                inputDto.setTypeList(typeList);
                result = commonGlobalDataBiz.findTreeVarBaseArray(inputDto);
            } else {
                StaticTreeInputDto inputDto = new StaticTreeInputDto();
                inputDto.setType(MagicNumbers.TWO);
                inputDto.setSpaceId(spaceId);
                inputDto.setPositionList(dataModelSectionList);
                inputDto.setTypeList(typeList);
                result = commonGlobalDataBiz.findStaticTree(inputDto);
            }
            if (null != result) {
                treeDtoList.addAll(result);
            }
        } else {
            if (null != isArray) {
                if (isArray) {
                    treeDtoList = commonGlobalDataBiz.findDomainDataByDataValueGetArrayObject(TemplateUnitTypeEnum.SPACE_VARIABLE, spaceId,
                            variablePath);
                } else {
                    treeDtoList = commonGlobalDataBiz.findDomainDataByDataValueGetObject(TemplateUnitTypeEnum.SPACE_VARIABLE, spaceId, variablePath);
                }
            } else {
                // 根据dataType过滤
                DataValueAndTypeGetVarTreeInputDto inputDto = new DataValueAndTypeGetVarTreeInputDto();
                inputDto.setType(MagicNumbers.TWO);
                inputDto.setGlobalId(spaceId);
                inputDto.setTypeList(typeList);
                inputDto.setDataValue(variablePath);
                DomainDataModelTreeDto result = commonGlobalDataBiz.findDataValueAndTypeGetVarTree(inputDto);

                if (null != result) {
                    treeDtoList.add(result);
                }
            }
        }

        return treeDtoList;
    }

    /**
     * findDataVariable
     *
     * @param spaceId         变量空间Id
     * @param dataVarTypeList 变量数据基础类型的list集合
     * @param isArray         是否为数组
     * @return 决策领域树形结构实体的list集合
     */
    public List<DomainDataModelTreeDto> findDataVariable(Long spaceId, List<DataVariableTypeEnum> dataVarTypeList, Boolean isArray) {
        List<DomainDataModelTreeDto> treeDtoList = null;

        List<String> positionList = new ArrayList<>();
        positionList.add(DomainModelSheetNameEnum.RAW_DATA.getMessage());

        List<String> typeList = new ArrayList<>();
        for (DataVariableTypeEnum typeEnum : dataVarTypeList) {
            typeList.add(typeEnum.getMessage());
        }

        if (null != isArray && isArray) {
            treeDtoList = commonGlobalDataBiz.findTreeVarBaseArray(TreeVarBaseArrayInputDto.builder()
                    .type(TemplateUnitTypeEnum.SPACE_VARIABLE.getType()).spaceId(spaceId).positionList(positionList).typeList(typeList).build());
        } else {

            treeDtoList = commonGlobalDataBiz.findStaticTree(StaticTreeInputDto.builder().type(TemplateUnitTypeEnum.SPACE_VARIABLE.getType())
                    .spaceId(spaceId).positionList(positionList).typeList(typeList).build());
        }

        DomainDataModelTreeDto rawData = treeDtoList.get(0);
        List<DomainDataModelTreeDto> children = rawData.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return treeDtoList;
        }
        //筛选出可选(有权限)的数据模型
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        Set<String> objectNameSet = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                        .select(VarProcessDataModel::getObjectName)
                        .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessDataModel::getCreatedDept, roleDataAuthority.getDeptCodes())
                        .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessDataModel::getCreatedUser, roleDataAuthority.getUserNames()))
                .stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toSet());
        List<DomainDataModelTreeDto> newChildren = new ArrayList<>();

        for (DomainDataModelTreeDto treeDto : children) {
            String path = treeDto.getValue();
            // 使用 split 方法将字符串按照 "." 分割成数组
            String[] parts = path.split("\\.");
            if ("rawData".equals(parts[0]) && objectNameSet.contains(parts[1])) {
                newChildren.add(treeDto);
            }
        }
        treeDtoList.get(0).setChildren(newChildren);

        // 补充可选择的变量
        assembleVariables(treeDtoList, typeList);
        return treeDtoList;
    }

    /**
     * 补充可选择的变量
     * @param treeDtoList
     * @param typeList
     */
    private void assembleVariables(List<DomainDataModelTreeDto> treeDtoList, List<String> typeList) {
        List<VarProcessVariable> variableList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                        .select(VarProcessVariable::getName,VarProcessVariable::getLabel,VarProcessVariable::getDataType,VarProcessVariable::getIdentifier)
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .in(!CollectionUtils.isEmpty(typeList),VarProcessVariable::getDataType,typeList)
                        .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                .groupBy(VarProcessVariable::getIdentifier));
        if (!CollectionUtils.isEmpty(variableList)) {
            List<DomainDataModelTreeDto> child = new ArrayList<>();
            variableList.forEach(var -> child.add(DomainDataModelTreeDto.builder()
                    .isUse("0")
                    .isEmpty("0")
                    .name(var.getName())
                    .describe(var.getLabel())
                    .isArr("0")
                    .label(var.getName() + "-" + var.getLabel())
                    .type(var.getDataType())
                    .value(MessageFormat.format("vars.{0}", var.getName()))
                    .identifier(var.getIdentifier())
                    .build()));
            treeDtoList.add(DomainDataModelTreeDto.builder()
                    .name(DomainModelSheetNameEnum.VARS.getMessage())
                    .value(DomainModelSheetNameEnum.VARS.getMessage())
                    .label("vars-变量")
                    .describe("变量")
                    .isArr("0")
                    .isEmpty("0")
                    .type("object")
                    .children(child)
                    .build());
        }
    }

    /**
     * findDirectMatchVarsTree
     *
     * @param inputDto 变量空间Id
     * @return 决策领域树形结构实体的list集合
     */
    public List<DomainDataModelTreeDto> findDirectMatchVarsTree(DataModelMatchTreeInputDto inputDto) {
        List<String> typeList = new ArrayList<>();
        typeList.add(inputDto.getVarType());
        List<String> positionList = new ArrayList<>();
        positionList.add(DomainModelSheetNameEnum.RAW_DATA.getMessage());
        final StaticTreeInputDto treeDto = StaticTreeInputDto.builder()
                .type(TemplateUnitTypeEnum.SPACE_VARIABLE.getType())
                .spaceId(inputDto.getSpaceId())
                .positionList(positionList)
                .typeList(typeList)
                .isArr(inputDto.getIsArrAy())
                .leftProperty(inputDto.getLeftProperty())
                .build();

        return commonGlobalDataBiz.findDirectStaticTree(treeDto);
    }

    /**
     * searchUtilizedVariables
     *
     * @param spaceId            变量空间Id
     * @param employerVariableId 临时变量的Id
     * @return 变量ID集合
     */
    public Set<Long> searchUtilizedVariables(Long spaceId, Long employerVariableId) {
        List<Long> variableIdList = new ArrayList<>();
        variableIdList.add(employerVariableId);
        return this.searchUtilizedVariables(spaceId, variableIdList);
    }

    /**
     * 搜索已经使用过的变量
     *
     * @param spaceId                变量空间Id
     * @param employerVariableIdList 指定变量
     * @return 已经使用过的变量
     */
    public Set<Long> searchUtilizedVariables(Long spaceId, List<Long> employerVariableIdList) {
        // 查询空间内变量的相互关系 List
        List<VarProcessVariableReference> variableInterrelationshipList = varProcessVariableReferenceService.list(new QueryWrapper<VarProcessVariableReference>().lambda()
                .eq(VarProcessVariableReference::getVarProcessSpaceId, spaceId)
        );
        if (CollectionUtils.isEmpty(variableInterrelationshipList)) {
            // 未查询到指定空间变量相互使用关系记录: 返回空 Set
            return Collections.emptySet();
        }

        // 已使用变量 ID 集合
        Set<Long> utilizedVariableIdSet = new HashSet<>();
        // 以 use_by_variable_id 作为标准, 查询指定变量使用的全部变量 ID 集合
        for (Long employerVariableId : employerVariableIdList) {
            searchUtilizedVariablesHelper(variableInterrelationshipList, employerVariableId, utilizedVariableIdSet);
        }

        return utilizedVariableIdSet;
    }

    /**
     * 获取已用变量 ID 集合
     *
     * @param variableInterrelationshipList 变量的相互关系 List
     * @param employerVariableId            使用方变量 ID
     * @param utilizedVariableIdSet         已使用变量 ID 集合
     */
    private void searchUtilizedVariablesHelper(List<VarProcessVariableReference> variableInterrelationshipList, Long employerVariableId,
                                               Set<Long> utilizedVariableIdSet) {
        for (VarProcessVariableReference variableInterrelationship : variableInterrelationshipList) {
            // 遍历相互关系列表
            if (employerVariableId.equals(variableInterrelationship.getUseByVariableId())) {
                // 添加被使用方变量 ID 至 Set
                utilizedVariableIdSet.add(variableInterrelationship.getVariableId());
                // 继续递归调用
                searchUtilizedVariablesHelper(variableInterrelationshipList, employerVariableId, utilizedVariableIdSet);
            }
        }
    }

    /**
     * 获取数据模型下基本数据类型变量树
     *
     * @param dataModelName 数据模型名称
     * @param varTypeList   数据类型 list
     * @return list
     */
    public List<DomainDataModelTreeDto> findDatModelBasicVars(String dataModelName, List<String> varTypeList) {
        dynamicTreeConverter.checkTypeList(varTypeList);
        Map<String, String> modelContentsByNames = varProcessDataModelService.getModelContentsByNames(Collections.singletonList(dataModelName));
        DomainDataModelTreeDto treeDto = DomainModelTreeEntityUtils.transferDataModelTreeDto(modelContentsByNames.get(dataModelName), new HashSet<>());
        treeDto = beanCopyDynamicTreeOutputDtoByTypeList(treeDto, varTypeList);
        return treeDto == null ? new ArrayList<>() : Collections.singletonList(DomainDataModelTreeDto.builder()
                .name(DomainModelSheetNameEnum.RAW_DATA.getMessage())
                .value(DomainModelSheetNameEnum.RAW_DATA.getMessage())
                .label("rawData-原始数据")
                .describe(DomainModelSheetNameEnum.RAW_DATA.getDescribe())
                .isArr(StringPool.ZERO)
                .isEmpty(StringPool.ZERO)
                .type(DataTypeEnum.OBJECT.getDesc())
                .typeRef(DomainModelSheetNameEnum.RAW_DATA.getMessage())
                .children(Collections.singletonList(treeDto))
                .build());
    }

    /**
     * 递归类型转换成DynamicTreeOutputDto且在这个对象中寻找typeList类型的属性
     * 含基础属性的数组，含对象下的基础属性，不含数组对象下的基础属性
     *
     * @param domainDataModelTreeDto dto
     * @param typeList               类型list
     * @return rawData
     */
    public DomainDataModelTreeDto beanCopyDynamicTreeOutputDtoByTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            List<DomainDataModelTreeDto> children = new ArrayList<>();
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                if (child == null) {
                    continue;
                }
                //对象类型数组，该对象下的属性及子对象的属性都不要
                if (child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())
                        && DomainModelTreeUtils.ONE_VALUE.equals(child.getIsArr())) {
                    continue;
                } else if (child.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage())) {
                    //如果是对象，但是不是对象数组，则看对象下是否存在typeList类型的数据，如果存在则继续递归，否则直接返回，不再寻找
                    boolean isTypeList = treeChildrenIsTypeList(child, typeList);
                    //对象中不存在typeList类型的数据，直接返回不再寻找
                    if (!isTypeList) {
                        continue;
                    }
                }
                DomainDataModelTreeDto domainDataModelTreeDtoChild = beanCopyDynamicTreeOutputDtoByTypeList(child, typeList);
                //等于空不放入到children中，也就是删除
                if (null == domainDataModelTreeDtoChild) {
                    continue;
                }
                //类型在typeList存在
                long count = typeList.stream()
                        .filter(f -> f.equalsIgnoreCase(domainDataModelTreeDtoChild.getType()))
                        .count();
                //domainDataModelTreeDtoChild对象中的类型是object
                boolean flag = domainDataModelTreeDtoChild.getType().equalsIgnoreCase(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
                //count>0或者类型是object类型
                if (count > 0 || flag) {
                    children.add(domainDataModelTreeDtoChild);
                }
            }

            //children不为空才放入到domainDataModelTreeDto中，否则返回空，也就是删除
            if (!CollectionUtils.isEmpty(children)) {
                domainDataModelTreeDto.setChildren(children);
            } else {
                return null;
            }
        }
        return domainDataModelTreeDto;
    }

    /**
     * 判断树形结构对象是否还存在typeList类型
     *
     * @param domainDataModelTreeDto 入参
     * @param typeList               list
     * @return true or false
     */
    public boolean treeChildrenIsTypeList(DomainDataModelTreeDto domainDataModelTreeDto, List<String> typeList) {
        if (null != domainDataModelTreeDto.getChildren()) {
            for (DomainDataModelTreeDto child : domainDataModelTreeDto.getChildren()) {
                //类型在typeList存在
                long count = typeList.stream()
                        .filter(f -> f.equalsIgnoreCase(child.getType()))
                        .count();
                if (count > 0) {
                    return true;
                }
                //递归寻找
                if (StringPool.ZERO.equals(domainDataModelTreeDto.getIsArr()) && treeChildrenIsTypeList(child, typeList)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取nacos配置的数据库类型
     * @return mysql || clickhouse || dm || sqlServer
     */
    public String findDataSourceType() {
        return dataSourceType;
    }
}
