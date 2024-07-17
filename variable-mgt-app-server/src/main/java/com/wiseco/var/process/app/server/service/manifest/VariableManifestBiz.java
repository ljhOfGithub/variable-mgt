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
package com.wiseco.var.process.app.server.service.manifest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.lock.LockClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.feign.VarProcessConsumerFeign;
import com.wiseco.var.process.app.server.controller.feign.dto.CreateTabDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestStateMutationInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableMaximumListedVersionQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestAvailableVersionOutputDto;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.JsonSchemaFieldEnum;
import com.wiseco.var.process.app.server.enums.ManifestPublishStateEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicSpaceTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelBoilerplateEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessManifestActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VarProcessParamTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestCycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.SysDynamicServiceBiz;
import com.wiseco.var.process.app.server.service.VarCompileVarRefBiz;
import com.wiseco.var.process.app.server.service.VarProcessCategoryService;
import com.wiseco.var.process.app.server.service.VarProcessConfigTagService;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.VarProcessParamService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.VariableKnowledgeBiz;
import com.wiseco.var.process.app.server.service.VariablePublishBiz;
import com.wiseco.var.process.app.server.service.VariableRefBiz;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.CacheEventSendService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.dto.VarProcessDataModelDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishVariableVo;
import com.wiseco.var.process.app.server.service.dto.VariableMaximumListedVersionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.decision.jsonschema.util.enums.JsonSchemaFieldEnum.EXTEND_FIELD;
import static com.decision.jsonschema.util.enums.JsonSchemaFieldEnum.ITEMS_FIELD;
import static com.decision.jsonschema.util.enums.JsonSchemaFieldEnum.PROPERTIES_FIELD;
import static com.decision.jsonschema.util.enums.JsonSchemaFieldEnum.TYPE_FIELD;
import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;
import static com.wisecotech.json.JSON.parseObject;

/**
 * 变量清单 业务实现
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/26
 */
@Slf4j
@Service
public class VariableManifestBiz {
    public static final String STRING_0 = "0";
    @Autowired
    private VariableManifestSupportBiz variableManifestSupport;
    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;
    @Autowired
    private VarProcessManifestService varProcessManifestService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessCategoryService varProcessCategoryService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VarProcessManifestInternalService varProcessManifestInternalDataService;
    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessOutsideRefService varProcessOutsideServiceRefService;
    @Autowired
    private VarProcessInternalDataService varProcessInternalDataService;
    @Autowired
    private VariableManifestVarBiz variableManifestVarBiz;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VarProcessManifestFunctionService varProcessManifestFunctionService;
    @Autowired
    private VarProcessManifestOutsideService varProcessManifestOutsideServiceService;
    @Autowired
    private VariablePublishBiz variablePublishBiz;
    @Autowired
    private SysDynamicServiceBiz sysDynamicServiceBiz;
    @Autowired
    private VarProcessManifestLifecycleService varProcessManifestLifecycleService;
    @Autowired
    private VariableCompileBiz variableCompileBiz;
    @Autowired
    private VarProcessParamService varProcessParamService;
    @Autowired
    private BacktrackingService backtrackingService;
    @Autowired
    private VariableManifestFlowBiz variableManifestFlowBiz;
    @Resource
    private DeptService deptService;
    @Autowired
    private VariableKnowledgeBiz variableKnowledgeBiz;
    @Autowired
    private VariableRefBiz variableRefBiz;
    @Autowired
    private VarProcessConfigTagService varProcessConfigTagService;
    @Resource
    private OutsideService outsideService;
    @Autowired
    private VarProcessConsumerFeign varProcessConsumerFeign;
    @Autowired
    private CacheEventSendService cacheEventSendService;
    @Autowired
    private AuthService authService;
    @Autowired
    private VarCompileVarRefBiz varCompileVarRefBiz;
    @Autowired
    @Qualifier("distributedLockClient")
    private LockClient distributedLockClient;

    private static final String VARIABLE_MANIFEST = "variable_manifest";

    /**
     * 根据变量ID列表和空间ID查询变量使用到的数据模型
     *
     * @param varModelInputDto varModelInputDto
     * @return 数据模型列表
     */
    public List<VariableManifestDataModelMappingVo> getModelByVarId(VarModelInputDto varModelInputDto) {
        List<VarProcessDataModel> models = varProcessManifestService.getModelsByVariableIds(varModelInputDto);
        return assembleMappingVo(models);
    }

    List<VariableManifestDataModelMappingVo> assembleMappingVo(List<VarProcessDataModel> models) {
        return models.stream().map(model -> {
            VariableManifestDataModelMappingVo dataModelVo = new VariableManifestDataModelMappingVo();
            dataModelVo.setName(model.getObjectName());
            dataModelVo.setDescription(model.getObjectLabel());
            dataModelVo.setVersion(model.getVersion());
            dataModelVo.setSourceType(model.getObjectSourceType());
            dataModelVo.setSource(model.getObjectSourceInfo());
            dataModelVo.setSourceNum(model.getSourcePropertyNum());
            dataModelVo.setExtendNum(model.getExtendPropertyNum());
            dataModelVo.setId(model.getId());
            return dataModelVo;
        }).collect(Collectors.toList());
    }

    List<VariableManifestDataModelMappingVo> assembleMappingVoV2(List<VarProcessDataModelDto> models) {
        return models.stream().map(model -> {
            VariableManifestDataModelMappingVo dataModelVo = new VariableManifestDataModelMappingVo();
            dataModelVo.setName(model.getObjectName());
            dataModelVo.setDescription(model.getObjectLabel());
            dataModelVo.setVersion(model.getVersion());
            dataModelVo.setSourceType(model.getObjectSourceType());
            dataModelVo.setSource(model.getObjectSourceInfo());
            dataModelVo.setSourceNum(model.getSourcePropertyNum());
            dataModelVo.setExtendNum(model.getExtendPropertyNum());
            dataModelVo.setId(model.getId());
            dataModelVo.setQueryConditionList(JSON.parseArray(model.getModelQueryCondition(), VariableManifestDataModelMappingVo.QueryCondition.class));
            return dataModelVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询已经启用的变量清单 引用数据模型信息
     *
     * @param manifestId 清单id
     * @return 已经启用的变量清单 引用数据模型信息
     */
    public List<VariableManifestDataModelMappingVo> getDataModels(Long manifestId) {
        List<VariableManifestDataModelMappingVo> result = new ArrayList<>();
        List<VarProcessManifestDataModel> manifestDataModels = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId));
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(manifestDataModels)) {
            return result;
        }
        //获取数据模型Map<name,label>
        Map<String, String> nameLabelMap = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                        .select(VarProcessDataModel::getObjectName, VarProcessDataModel::getObjectLabel))
                .stream().collect(Collectors.toMap(VarProcessDataModel::getObjectName, VarProcessDataModel::getObjectLabel, (k1, k2) -> k1));

        for (VarProcessManifestDataModel model : manifestDataModels) {
            VariableManifestDataModelMappingVo modelInfo = new VariableManifestDataModelMappingVo();
            modelInfo.setName(model.getObjectName());
            modelInfo.setDescription(nameLabelMap.getOrDefault(model.getObjectName(), ""));
            modelInfo.setSourceType(Objects.requireNonNull(VarProcessDataModelSourceType.getByCode(model.getSourceType())));
            modelInfo.setVersion(model.getObjectVersion());
            modelInfo.setQueryConditionList(JSON.parseArray(model.getModelQueryCondition(), VariableManifestDataModelMappingVo.QueryCondition.class));
            result.add(modelInfo);
        }

        return result;
    }

    /**
     * findAvailableManifest
     *
     * @param serviceId 变量空间Id
     * @return 变量清单可用版本输出参数list
     */
    public List<VariableManifestAvailableVersionOutputDto> findAvailableManifest(Long serviceId) {
        // 查询实时服务下未被删除的接口版本
        List<VarProcessManifest> availableManifestList = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                .eq(VarProcessManifest::getServiceId, serviceId)
                .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .orderByDesc(VarProcessManifest::getVersion)
        );
        // 转换查询结果为出参 DTO
        List<VariableManifestAvailableVersionOutputDto> outputDtoList = new ArrayList<>(availableManifestList.size());
        for (VarProcessManifest availableManifest : availableManifestList) {
            outputDtoList.add(VariableManifestAvailableVersionOutputDto.builder()
                    .manifestId(availableManifest.getId())
                    .version(availableManifest.getVersion())
                    .build());
        }
        return outputDtoList;
    }

    /**
     * 检查变量清单是否处于指定状态
     *
     * @param manifestEntity 变量清单实体类
     * @param stateEnum      指定变量清单状态
     * @return true, 如果变量清单处于指定状态
     */
    public static boolean verifyManifestStatus(VarProcessManifest manifestEntity, VarProcessManifestStateEnum stateEnum) {
        return manifestEntity.getState().equals(stateEnum);
    }

    /**
     * getVariableMaximumListedVersion
     *
     * @param inputDto 输入实体类对象
     * @return 变量清单-变量发布信息list
     */
    public List<VariableManifestPublishVariableVo> getVariableMaximumListedVersion(VariableMaximumListedVersionQueryInputDto inputDto) {
        //  1.设置查询条件, 执行分页查询
        VariableMaximumListedVersionQueryDto queryDto = new VariableMaximumListedVersionQueryDto();
        BeanUtils.copyProperties(inputDto, queryDto);
        List<VariableManifestPublishVariableVo> queryResult = new ArrayList<>();
        if (!StringUtils.isEmpty(inputDto.getDeptId())) {
            queryDto.setDeptCode(inputDto.getDeptId());
        }

        String sortedKey = inputDto.getOrder();
        if (StringUtils.isEmpty(sortedKey)) {
            queryDto.setSortedKey("label");
            queryDto.setSortMethod("asc");
        } else {
            queryDto.setSortMethod(sortedKey.substring(sortedKey.indexOf("_") + 1));
            String orderKey = sortedKey.substring(0, sortedKey.indexOf("_"));
            switch (orderKey) {
                case "name":
                    queryDto.setSortedKey("name");
                    break;
                case "version":
                    queryDto.setSortedKey("version");
                    break;
                default:
                    queryDto.setSortedKey("label");
            }
        }
        VarProcessConfigTag tag = varProcessConfigTagService.getById(inputDto.getTagId());
        if (tag != null && tag.getName() != null) {
            queryDto.setTagNames(Collections.singletonList(tag.getName()));
        }
        if (inputDto.getGroupId() != null) {
            List<String> tagNames = varProcessConfigTagService.list(Wrappers.<VarProcessConfigTag>lambdaQuery()
                            .select(VarProcessConfigTag::getName)
                            .eq(VarProcessConfigTag::getGroupId, inputDto.getGroupId())).stream().map(VarProcessConfigTag::getName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tagNames)) {
                return queryResult;
            }
            queryDto.setTagNames(tagNames);
        }
        if (CollectionUtils.isEmpty(queryDto.getTagNames())) {
            queryDto.setTagNames(null);
        }

        List<VarProcessCategory> categoryList = varProcessCategoryService.getCategoryListByType(CategoryTypeEnum.VARIABLE);
        Map<Long, VarProcessCategory> categoryMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, cat -> cat, (k1, k2) -> k2));
        // 获取变量类别名称　Map, key: categoryId, value: category name
        Map<Long, String> categoryNameMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName, (k1, k2) -> k2));

        queryDto.setCategoryIds(categoryList.stream().filter(cat -> varProcessCategoryService.containsSubCat(inputDto.getCategoryId(), cat, categoryMap)).map(VarProcessCategory::getId).collect(Collectors.toList()));

        //权限控制
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return queryResult;
        }
        BeanUtils.copyProperties(roleDataAuthority, queryDto);
        List<VarProcessVariable> variables = varProcessVariableService.pageQueryVariableMaximumListedVersion(queryDto);
        if (CollectionUtils.isEmpty(variables)) {
            // 查询结果为空: 快速返回
            return queryResult;
        }

        Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(variables.stream().map(VarProcessVariable::getDeptCode).collect(Collectors.toList()));
        for (VarProcessVariable listedVariable : variables) {
            VariableManifestPublishVariableVo outputVariableDto = VariableManifestPublishVariableVo.builder()
                    .identifier(listedVariable.getIdentifier()).name(listedVariable.getName()).label(listedVariable.getLabel())
                    .category(categoryNameMap.getOrDefault(listedVariable.getCategoryId(), StringPool.EMPTY))
                    .dataType(listedVariable.getDataType()).selectedVersionInfo(null)
                    .listedVersionInfoList(null).outputFlag(null).build();
            outputVariableDto.setDept(deptMap.get(listedVariable.getDeptCode()) == null ? "" : deptMap.get(listedVariable.getDeptCode()));
            // 使用 "用户选择的版本" 字段存储已上架变量的最大版本信息
            VariableManifestPublishVariableVo.VersionInfo versionInfo = VariableManifestPublishVariableVo.VersionInfo.builder()
                    .variableId(listedVariable.getId()).version(listedVariable.getVersion()).build();
            outputVariableDto.setSelectedVersionInfo(versionInfo);
            queryResult.add(outputVariableDto);
        }
        return queryResult;
    }


    /**
     * getDesignatedVariableAllListedVersions
     *
     * @param spaceId        变量空间Id
     * @param identifierList 唯一标识符
     * @return 变量清单-变量发布信息list
     */
    public List<VariableManifestPublishVariableVo> getDesignatedVariableAllListedVersions(Long spaceId, List<String> identifierList) {
        // 设置查询条件
        LambdaQueryWrapper<VarProcessVariable> queryWrapper = Wrappers.<VarProcessVariable>lambdaQuery()
                .select(
                        VarProcessVariable::getId,
                        VarProcessVariable::getCategoryId,
                        VarProcessVariable::getIdentifier,
                        VarProcessVariable::getVersion,
                        VarProcessVariable::getName,
                        VarProcessVariable::getLabel,
                        VarProcessVariable::getDataType,
                        VarProcessVariable::getDeptCode
                )
                .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .orderByDesc(VarProcessVariable::getVersion);
        if (!CollectionUtils.isEmpty(identifierList)) {
            // 设置待查询变量的标识符
            queryWrapper.in(VarProcessVariable::getIdentifier, identifierList);
        } else {
            // 未指定待查询变量的标识符: 返回空集合
            return new ArrayList<>();
        }
        // 执行查询
        List<VarProcessVariable> listedVariableList = varProcessVariableService.list(queryWrapper);
        // 获取变量类别名称　Map, key: categoryId, value: category name
        Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(spaceId);
        // 已上架变量列表 Map, key: 变量标识符, value: VariableManifestPublishVariableVo
        Map<String, VariableManifestPublishVariableVo> listedVariableMap = new HashMap<>(MagicNumbers.EIGHT);
        //部门map
        Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(listedVariableList.stream().map(VarProcessVariable::getDeptCode).collect(Collectors.toList()));
        for (VarProcessVariable listedVariable : listedVariableList) {
            String key = listedVariable.getIdentifier();
            listedVariableMap.computeIfAbsent(key, identifier ->
                    // 已上架变量列表 Map 不包含指定变量标识符: 创建并放入 Map
                    VariableManifestPublishVariableVo.builder()
                            .identifier(key)
                            .name(listedVariable.getName())
                            .label(listedVariable.getLabel())
                            .category(categoryNameMap.get(listedVariable.getCategoryId()))
                            .dataType(listedVariable.getDataType())
                            .dept(deptMap.get(listedVariable.getDeptCode()) == null
                                    ? StringPool.EMPTY : deptMap.get(listedVariable.getDeptCode()))
                            .listedVersionInfoList(new ArrayList<>())
                            .outputFlag(null)
                            .build());
            // 向实时服务输出变量版本列表追加新版本信息
            VariableManifestPublishVariableVo.VersionInfo versionInfo = VariableManifestPublishVariableVo.VersionInfo.builder()
                    .variableId(listedVariable.getId())
                    .version(listedVariable.getVersion())
                    .build();
            VariableManifestPublishVariableVo outputVariable = listedVariableMap.get(key);
            outputVariable.getListedVersionInfoList().add(versionInfo);
        }
        return new ArrayList<>(listedVariableMap.values());
    }

    /**
     * getOutsideServiceReceiverObjectTree
     *
     * @param spaceId 变量空间Id
     * @return DomainDataModelTreeDto的list
     */
    public List<DomainDataModelTreeDto> getOutsideServiceReceiverObjectTree(Long spaceId) {
        // 0. 查询变量空间引入的外部服务接收对象信息
        List<VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto> receiverObjectInfoDtoList = varProcessOutsideServiceRefService
                .getVariableSpaceReferencedOutsideServiceReceiverObjectInfo(spaceId);
        // 1. 拼装绑定对象结构
        List<DomainDataModelTreeDto> receiverObjectTree = new LinkedList<>();
        for (VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto infoDto : receiverObjectInfoDtoList) {
            // 第一层: 外部服务名称
            DomainDataModelTreeDto firstLayerTreeDto = DomainDataModelTreeDto.builder().name(infoDto.getOutsideServiceName())
                    .label(infoDto.getOutsideServiceName()).value(null).fullPathValue(null).describe(infoDto.getOutsideServiceName())
                    .type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).typeRef(null).build();
            receiverObjectTree.add(firstLayerTreeDto);
            // 第二层: 变量空间的外部服务接收对象名
            DomainDataModelTreeDto secondLayerTreeDto = DomainDataModelTreeDto.builder().name(infoDto.getReceiverObjectName())
                    .label(infoDto.getReceiverObjectName()).value(infoDto.getReceiverObjectName()).fullPathValue(infoDto.getReceiverObjectName())
                    .describe(infoDto.getReceiverObjectLabel()).type(DataVariableTypeEnum.OBJECT_TYPE.getMessage())
                    .typeRef(infoDto.getReceiverObjectName()).build();
            firstLayerTreeDto.setChildren(Collections.singletonList(secondLayerTreeDto));
            // 第三层: 外部服务响应结构一级对象
            JSONObject outputParameterBindings = parseObject(infoDto.getOutputParameterBindings(), Feature.OrderedField);
            if (null == outputParameterBindings) {
                return receiverObjectTree;
            }
            JSONObject outputParameterBindingsProperties = outputParameterBindings.getJSONObject(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage());
            if (1 == outputParameterBindingsProperties.size()) {
                // 第三层显示条件: 外部服务响应结构有且仅有一个对象
                for (Map.Entry<String, Object> entry : outputParameterBindingsProperties.entrySet()) {
                    JSONObject property = (JSONObject) entry.getValue();
                    if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(property.getString(JsonSchemaFieldEnum.TYPE_FIELD.getMessage()))) {
                        DomainDataModelTreeDto thirdLayerTreeDto = DomainDataModelTreeDto.builder().name(entry.getKey()).label(entry.getKey())
                                .value(infoDto.getReceiverObjectName() + "." + entry.getKey())
                                .fullPathValue(infoDto.getReceiverObjectName() + "." + entry.getKey())
                                .describe(property.getString(JsonSchemaFieldEnum.DESCRIPTION_FIELD.getMessage()))
                                .type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).typeRef(infoDto.getReceiverObjectName() + "." + entry.getKey())
                                .build();
                        secondLayerTreeDto.setChildren(Collections.singletonList(thirdLayerTreeDto));
                    }
                }
            }
        }
        return receiverObjectTree;
    }

    /**
     * 移除数据模型 JSON Schema 扩展数据节点
     * <p>移除带有 "extend": 1 属性的 JSON Object</p>
     *
     * @param jsonSchema 数据模型 JSON Schema
     */
    private static void removeJsonSchemaExtendedData(JSONObject jsonSchema) {
        // 遍历属性
        JSONObject properties = jsonSchema.getJSONObject(PROPERTIES_FIELD.getMessage());
        if (null == properties) {
            // 属性字段不存在: 立刻返回
            return;
        }
        for (Iterator<Map.Entry<String, Object>> entryIterator = properties.entrySet().iterator(); entryIterator.hasNext(); ) {
            JSONObject property = (JSONObject) entryIterator.next().getValue();
            Integer propertyExtend = property.getInteger(EXTEND_FIELD.getMessage());
            if (null != propertyExtend && 1 == propertyExtend) {
                // 扩展字段: 删除
                entryIterator.remove();
            }
            String propertyType = property.getString(TYPE_FIELD.getMessage());
            if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(propertyType)) {
                // 对象类型属性: 递归调用
                removeJsonSchemaExtendedData(property);
            } else if (DataVariableTypeEnum.ARRAY_TYPE.getMessage().equals(propertyType)) {
                // 数组类型属性: 判断数组元素属性
                JSONObject propertyItems = property.getJSONObject(ITEMS_FIELD.getMessage());
                String propertyItemsType = propertyItems.getString(TYPE_FIELD.getMessage());
                if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(propertyItemsType)) {
                    // 数组元素属性类型为对象: 递归调用
                    removeJsonSchemaExtendedData(propertyItems);
                }
            }
        }
    }

    /**
     * getOutsideServiceRequestParam
     *
     * @param spaceId             spaceId
     * @param objectBindingConfig objectBindingConfig
     * @return com.wisecotech.json.JSONArray
     */
    public JSONArray getOutsideServiceRequestParam(Long spaceId, String objectBindingConfig) {
        // 获取外部服务接收对象名称
        String outsideServiceReceiverObjectName = objectBindingConfig.split("\\.")[0];
        // 查询外部服务入参绑定信息
        VarProcessOutsideRef outsideServiceRefRecord = varProcessOutsideServiceRefService.getOne(Wrappers.<VarProcessOutsideRef>lambdaQuery()
                .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getInputParameterBindings)
                .eq(VarProcessOutsideRef::getVarProcessSpaceId, spaceId)
                .eq(VarProcessOutsideRef::getName, outsideServiceReceiverObjectName));
        return JSON.parseArray(outsideServiceRefRecord.getInputParameterBindings());
    }

    /**
     * validateStatusUpdate
     *
     * @param inputDto inputDto
     * @return java.lang.String
     */
    public String validateStatusUpdate(VariableManifestStateMutationInputDto inputDto) {
        VarProcessManifestActionTypeEnum actionTypeEnum = VarProcessManifestActionTypeEnum.getActionTypeEnum(inputDto.getActionType());
        if (actionTypeEnum == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "未找到匹配的操作类型。");
        }
        VarProcessManifest manifestEntity = varProcessManifestService.getById(inputDto.getManifestId());
        if (manifestEntity == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到变量清单相关信息。");
        }
        VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
        String result;
        switch (actionTypeEnum) {
            case SUBMIT:
                // 提交审核
                submitCheck(manifestDto);
                if (!varProcessParamService.getParamStatus(VarProcessParamTypeEnum.VAR_LIST_REVIEW.getCode())) {
                    result = "未开启清单审核，当前清单的状态会直接变成启用，确认提交？";
                } else {
                    result = "提交审核后清单不可再修改，确认提交？";
                }
                break;
            case STEP_BACK:
                // 退回编辑
                result = "确认将该变量清单退回编辑状态？";
                break;
            case APPROVE:
                // 审批通过->启用
                approveCheck(manifestDto);
                result = "审核通过后将自动启用该变量清单，确认审核通过？";
                break;
            case REJECT:
                // 审批拒绝: 无校验操作
                result = "";
                break;
            case DISABLE:
                // 停用
                result = disableCheck(manifestDto);
                break;
            case RE_ENABLE:
                // 重新启用
                enableCheck(inputDto, manifestDto);
                result = "确认重新启用该变量清单？";
                break;
            case DELETE:
                // 校验变量清单状态
                result = deleteCheck(manifestEntity);
                break;
            default:
                // 提交测试使用独立的方法
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "不支持的变量清单操作类型。");
        }
        return result;
    }

    protected String deleteCheck(VarProcessManifest manifestEntity) {
        if (!verifyManifestStatus(manifestEntity, VarProcessManifestStateEnum.EDIT)
                && !verifyManifestStatus(manifestEntity, VarProcessManifestStateEnum.DOWN)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "待删除变量清单不处于“编辑中”或“停用”状态，无法被删除。");
        }
        if (verifyManifestStatus(manifestEntity, VarProcessManifestStateEnum.DOWN)) {
            List<VarProcessServiceManifest> usedManifests = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                    .eq(VarProcessServiceManifest::getManifestId, manifestEntity.getId()));
            if (!CollectionUtils.isEmpty(usedManifests)) {
                return "该变量清单已被使用，确认删除？";
            }
        }
        return "确认删除？";
    }

    /**
     * enableCheck
     *
     * @param inputDto    输入实体
     * @param manifestDto 变量清单实体
     */
    private void enableCheck(VariableManifestStateMutationInputDto inputDto, VariableManifestDto manifestDto) {
        if (!VarProcessManifestStateEnum.DOWN.equals(manifestDto.getManifestEntity().getState())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "清单状态不为停用中，无法重新启用");
        }
        // 初始化提示信息
        StringBuilder promptMessageBuilder = new StringBuilder();
        // 1. 校验待发布变量是否存在已删除 / 未上架的情况
        List<Long> variableIdList = manifestDto.getVariablePublishList().stream()
                .map(VarProcessManifestVariable::getVariableId)
                .collect(Collectors.toList());
        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getId, VarProcessVariable::getCategoryId, VarProcessVariable::getIdentifier,
                                VarProcessVariable::getVersion, VarProcessVariable::getName,
                                VarProcessVariable::getLabel, VarProcessVariable::getDataType,
                                VarProcessVariable::getStatus, VarProcessVariable::getDeleteFlag)
                        .in(VarProcessVariable::getId, variableIdList)
        );
        StringBuilder deletedVariableName = new StringBuilder();
        StringBuilder notListedVariableName = new StringBuilder();
        for (VarProcessVariable variable : variableList) {
            if (DeleteFlagEnum.DELETED.getCode().equals(variable.getDeleteFlag())) {
                // 引用的变量已删除
                deletedVariableName.append(MessageFormat.format("{0}、", variable.getName()));
            } else if (!VariableStatusEnum.UP.equals(variable.getStatus())) {
                // 引用的变量未启用
                notListedVariableName.append(MessageFormat.format("{0}、", variable.getName()));
            }
        }
        if (!StringUtils.isEmpty(deletedVariableName.toString())) {
            promptMessageBuilder.append(MessageFormat.format("引用的变量【{0}】不存在，", deletedVariableName.substring(0, deletedVariableName.length() - 1)));
        }
        if (!StringUtils.isEmpty(notListedVariableName.toString())) {
            promptMessageBuilder.append(MessageFormat.format("引用的变量【{0}】未启用，", notListedVariableName.substring(0, notListedVariableName.length() - 1)));
        }
        if (!StringUtils.isEmpty(promptMessageBuilder.toString())) {
            promptMessageBuilder.append("无法启用。");
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, promptMessageBuilder.toString());
        }
        // 2. 校验变量清单的外部服务 / 内部数据引用
        // 校验变量清单外部服务引用
        String outsideServiceMessage = checkManifestOutside(inputDto.getSpaceId(), inputDto.getManifestId());
        if (!StringUtils.isEmpty(outsideServiceMessage)) {
            promptMessageBuilder.append(outsideServiceMessage);
        }
        // 校验变量清单内部数据引用
        String internalServiceMessage = checkManifestInternal(inputDto.getSpaceId(), inputDto.getManifestId());
        if (!StringUtils.isEmpty(internalServiceMessage)) {
            promptMessageBuilder.append(internalServiceMessage);
        }
        if (!StringUtils.isEmpty(promptMessageBuilder.toString())) {
            promptMessageBuilder.append("不允许启用。");
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, promptMessageBuilder.toString());
        }
        // 3. 校验变量清单公共函数使用情况
        // 查询变量清单使用的公共函数标识
        List<VarProcessManifestFunction> list = varProcessManifestFunctionService.list(Wrappers.<VarProcessManifestFunction>lambdaQuery()
                .eq(VarProcessManifestFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessManifestFunction::getManifestId, inputDto.getManifestId()));
        List<String> utilizedCommonFunctionIdentifierList = new ArrayList<>();
        List<String> prepIdentifierList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            utilizedCommonFunctionIdentifierList = list.stream()
                    .map(VarProcessManifestFunction::getIdentifier)
                    .collect(Collectors.toList());
            List<VarProcessFunction> prepList = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(VarProcessFunction::getIdentifier)
                            .eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.PREP)
                            .in(VarProcessFunction::getIdentifier, utilizedCommonFunctionIdentifierList)
            );
            prepIdentifierList = prepList.stream()
                    .map(VarProcessFunction::getIdentifier)
                    .collect(Collectors.toList());
        }
        validateTestSubmission(manifestDto, utilizedCommonFunctionIdentifierList, prepIdentifierList);
    }

    /**
     * updateStatus
     *
     * @param inputDto inputDto
     * @throws Throwable 异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(VariableManifestStateMutationInputDto inputDto) throws Throwable {
        VarProcessManifestActionTypeEnum actionType = VarProcessManifestActionTypeEnum.getActionTypeEnum(inputDto.getActionType());
        if (actionType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "未找到匹配的操作类型。");
        }
        VarProcessManifest manifestEntity = varProcessManifestService.getById(inputDto.getManifestId());
        if (manifestEntity == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到清单信息。");
        }
        if (VarProcessManifestActionTypeEnum.SUBMIT.equals(actionType)) {
            // 防重复提交
            String lockKey = VARIABLE_MANIFEST + StringPool.COLON + inputDto.getManifestId();
            if (distributedLockClient.acquire(lockKey)) {
                try {
                    VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
                    // 提交审核
                    submitTest(inputDto, manifestDto);
                    updateManifestMappings(manifestDto);
                    //查看审核按钮是否打开
                    if (!varProcessParamService.getParamStatus(VarProcessParamTypeEnum.VAR_LIST_REVIEW.getCode())) {
                        //审核按钮未打开则直接启用
                        actionType = VarProcessManifestActionTypeEnum.APPROVE;
                        // 动态建表
                        createVarTable(inputDto.getSpaceId(), inputDto.getManifestId());
                        cacheEventSendService.manifestChange();
                    }
                    //修改状态
                    mutateStateThroughAction(inputDto.getManifestId(), actionType);
                } finally {
                    distributedLockClient.release(lockKey);
                }
            } else {
                throw new VariableMgtBusinessServiceException("当前清单正在提交，请稍后！");
            }
        } else {
            VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
            //验证
            switch (actionType) {
                case STEP_BACK:
                    // 退回编辑-无校验
                    break;
                case APPROVE:
                    // 审批通过->启用
                    approveCheck(manifestDto);
                    // 动态建表
                    createVarTable(inputDto.getSpaceId(), inputDto.getManifestId());
                    cacheEventSendService.manifestChange();
                    break;
                case REJECT:
                    // 审批拒绝: 无校验操作
                    break;
                case DISABLE:
                    // 停用
                    disableCheck(manifestDto);
                    cacheEventSendService.manifestChange();
                    break;
                case RE_ENABLE:
                    // 重新启用
                    enableCheck(inputDto, manifestDto);
                    cacheEventSendService.manifestChange();
                    break;
                default:
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "不支持的变量清单操作类型。");
            }
        }
        if (!VarProcessManifestActionTypeEnum.SUBMIT.equals(actionType)) {
            //修改状态
            mutateStateThroughAction(inputDto.getManifestId(), actionType);
        }
        // 保存生命周期
        String lifecycleDescription = "";
        if (VarProcessManifestActionTypeEnum.APPROVE.equals(actionType) || VarProcessManifestActionTypeEnum.REJECT.equals(actionType)) {
            // 操作为 "审核通过" 或 "审核拒绝": 记录审批意见
            lifecycleDescription = inputDto.getApproDescription();
        }
        recordManifestLifecycle(inputDto.getManifestId(), actionType, lifecycleDescription);
        // 记录系统动态
        saveDynamic(actionType.getActionDescription(), manifestEntity.getVarProcessSpaceId(), manifestEntity.getId(), manifestEntity.getVersion());
    }


    private void updateManifestMappings(VariableManifestDto manifestDto) {
        //更新数据模型绑定的版本
        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        List<VarProcessDataModel> listByObjectName = varProcessDataModelService.findMaxVersionList(manifestDto.getManifestEntity().getVarProcessSpaceId(), roleDataAuthorityDTO);
        if (CollectionUtils.isEmpty(listByObjectName)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到变量空间下的数据模型");
        }
        Map<String, Integer> dataModelVersionMap = listByObjectName.stream().collect(Collectors.toMap(VarProcessDataModel::getObjectName, VarProcessDataModel::getVersion, (v1, v2) -> v1));
        for (VarProcessManifestDataModel mapping : manifestDto.getDataModelMappingList()) {
            if (!dataModelVersionMap.containsKey(mapping.getObjectName())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "使用的数据模型对象[" + mapping.getObjectName() + "]已删除，请进入编辑页面先保存再提交。");
            }
            mapping.setObjectVersion(dataModelVersionMap.get(mapping.getObjectName()));
        }
        // 删除旧数据模型绑定信息
        varProcessManifestDataModelService.remove(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .eq(VarProcessManifestDataModel::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                .eq(VarProcessManifestDataModel::getManifestId, manifestDto.getManifestEntity().getId())
        );
        if (!CollectionUtils.isEmpty(manifestDto.getDataModelMappingList())) {
            // 添加并保存新数据模型绑定信息
            varProcessManifestDataModelService.saveBatch(manifestDto.getDataModelMappingList());
        }
    }

    private void createVarTable(Long spaceId, Long manifestId) {
        List<CreateTabDto.DbColumn> columns = new ArrayList<>();
        List<VarProcessVariableDto> varDtoList = varProcessManifestVariableService.getVariableInfosByManifestId(spaceId, manifestId);
        for (VarProcessVariableDto var : varDtoList) {
            columns.add(CreateTabDto.DbColumn.builder().columnName(var.getName()).columnDataType(var.getDataType()).isIndex(var.getIsIndex()).colRole(var.getColRole()).build());
        }
        if (columns.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_CREATE_TABLE_FAIL, "变量清单里无任何变量，请添加.");
        }
        String tableName = String.format("var_process_manifest_%s", manifestId);
        CreateTabDto createTabDto = new CreateTabDto();
        createTabDto.setTableName(tableName);
        createTabDto.setColumns(columns);
        APIResult<String> feignResult = varProcessConsumerFeign.createVarTable(createTabDto);
        if (!Objects.equals(feignResult.getCode(), STRING_0)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_CREATE_TABLE_FAIL, feignResult.getMessage());
        }
    }

    /**
     * submitTest
     *
     * @param inputDto 输入实体类对象
     * @param manifestDto
     * @return VariableCompileOutputDto
     * @throws Throwable 异常
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableCompileOutputDto submitTest(VariableManifestStateMutationInputDto inputDto, VariableManifestDto manifestDto) throws Throwable {
        // 0. 准备工作
        VarProcessManifestActionTypeEnum actionTypeEnum = VarProcessManifestActionTypeEnum.getActionTypeEnum(inputDto.getActionType());
        if (actionTypeEnum == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "未找到匹配的操作类型。");
        }
        Long manifestId = inputDto.getManifestId();
        // 查询变量清单 DTO
        if (manifestDto == null) {
            manifestDto = variableManifestSupport.getVariableManifestDto(manifestId);
        }
        Long spaceId = manifestDto.getManifestEntity().getVarProcessSpaceId();
        //校验
        submitCheck(manifestDto);
        // 尝试编译
        VariableCompileOutputDto compileOutputDto = variableCompileBiz.validate(TestVariableTypeEnum.MANIFEST, spaceId, manifestId, manifestDto.getManifestEntity().getContent());
        if (!compileOutputDto.isState()) {
            // 编译失败
            return compileOutputDto;
        }
        // 获取变量清单编译语义信息
        VarSyntaxInfo syntaxInfo = compileOutputDto.getCompileResultVo().getSyntaxInfo();

        //编译通过后，后端对比校验数据
        List<VarProcessCompileVar> varProcessCompileVars = new ArrayList<>();
        Map<String, VarActionHistory> actionHistorys = new HashMap<>(MagicNumbers.INT_64);
        Set<String> allIdentifierList = new HashSet<>();
        if (!CollectionUtils.isEmpty(syntaxInfo.getCallInfo())) {
            varCompileVarRefBiz.analyzeComponentVar(spaceId, manifestId, VarTypeEnum.MAINFLOW, syntaxInfo, varProcessCompileVars, actionHistorys, allIdentifierList);
        }

        // 获取待发布变量标识符
        List<String> varIdentifiers = new ArrayList<>(allIdentifierList);
        List<String> prepIdentifiers = new ArrayList<>(syntaxInfo.getPreProcessSet());
        // 提交测试校验
        validateTestSubmission(manifestDto, varIdentifiers, prepIdentifiers);
        // 导出变量清单 ZIP
        variableKnowledgeBiz.exportAndStoreVariableKnowledge(manifestDto, varIdentifiers);
        //保存数据模型使用关系
        updateManifestMappings(manifestDto);
        // 1. 保存变量清单使用的数据模型 JSON Schema 快照: "外部传入", 无扩展变量
        saveManifestSchemaSnapshot(manifestDto);
        // 2. 保存关联关系
        //使用的组件变量列表（有序）
        variableRefBiz.saveCompileVar(spaceId, manifestId, VarTypeEnum.MAINFLOW, varProcessCompileVars);

        // 变量清单-引用数据模型变量关系
        variableManifestVarBiz.saveVar(spaceId, manifestId, actionHistorys);
        // 与外部服务关系
        variableRefBiz.saveManifestOutsideService(syntaxInfo.getExternalServiceSet(), spaceId, manifestId);
        //
        //        //公共函数：预处理逻辑、变量模板、公共方法
        variableRefBiz.saveManifestFunction(syntaxInfo.getVarFunctionIdentifierSet(), spaceId, manifestId);
        //
        //        //内部数据
        variableRefBiz.saveManifestInternalData(syntaxInfo.getInnerDataSet(), spaceId, manifestId);
        return compileOutputDto;
    }

    /**
     * saveManifestSchemaSnapshot
     *
     * @param manifestDto manifestDto
     */
    public void saveManifestSchemaSnapshot(VariableManifestDto manifestDto) {
        // 过滤外部传入数据, 收集数据模型绑定对象名称和版本 Map, key: 对象名称, value: 对象版本
        Map<String, Integer> objectNameVersionMap = manifestDto.getDataModelMappingList().stream()
                .filter(mapping -> VarProcessDataModelSourceType.OUTSIDE_PARAM.getCode().equals(mapping.getSourceType()))
                .collect(Collectors.toMap(VarProcessManifestDataModel::getObjectName, VarProcessManifestDataModel::getObjectVersion));
        if (!CollectionUtils.isEmpty(objectNameVersionMap)) {
            // 存在需要从外部传入的数据模型
            List<VarProcessDataModel> bindingDataModelList = new LinkedList<>();
            List<VarProcessDataModel> dataModelList = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                    .eq(VarProcessDataModel::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                    .in(VarProcessDataModel::getObjectName, objectNameVersionMap.keySet()));
            for (VarProcessDataModel dataModel : dataModelList) {
                if (dataModel.getVersion().equals(objectNameVersionMap.get(dataModel.getObjectName()))) {
                    // 数据模型对象版本匹配: 添加到结果 List
                    bindingDataModelList.add(dataModel);
                }
            }
            // 根据数据模型组装 JSON Schema 快照
            JSONObject schemaSnapshot = parseObject(assembleRawData(bindingDataModelList), Feature.OrderedField);
            // 移除快照中的扩展数据
            removeJsonSchemaExtendedData(schemaSnapshot);
            // 保存 JSON Schema 快照
            varProcessManifestService.update(Wrappers.<VarProcessManifest>lambdaUpdate()
                    .eq(VarProcessManifest::getId, manifestDto.getManifestEntity().getId())
                    .set(VarProcessManifest::getSchemaSnapshot, schemaSnapshot.toJSONString())
            );
        }
    }

    /**
     * 变量清单启用时，除了判断自身逻辑是否正确外，还需要判断使用的变量版本是否存在，并处于启用状态：
     * 1）如果不存在，则提示“该变量清单引用的变量【XXXXXV1】已经被删除，无法启用”；
     * 2）如果存在，但是处于停用状态，则提示“该变量清单引用的变量【XXXXXV1】已经被停用，无法启用该变量清单”；
     *
     * @param manifestDto 变量清单dto
     */
    private void approveCheck(VariableManifestDto manifestDto) {
        if (!VarProcessManifestStateEnum.UNAPPROVED.equals(manifestDto.getManifestEntity().getState())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "清单状态不为待审核，不允许审核通过");
        }
        StringBuilder promptMessageBuilder = new StringBuilder();
        // 校验待发布变量是否存在已删除 / 未上架的情况
        List<Long> variableIdList = manifestDto.getVariablePublishList().stream()
                .map(VarProcessManifestVariable::getVariableId)
                .collect(Collectors.toList());
        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(
                                VarProcessVariable::getId,
                                VarProcessVariable::getCategoryId,
                                VarProcessVariable::getIdentifier,
                                VarProcessVariable::getVersion,
                                VarProcessVariable::getName,
                                VarProcessVariable::getLabel,
                                VarProcessVariable::getDataType,
                                VarProcessVariable::getStatus,
                                VarProcessVariable::getDeleteFlag
                        )
                        .in(VarProcessVariable::getId, variableIdList)
        );
        StringBuilder deletedVariableName = new StringBuilder();
        StringBuilder notListedVariableName = new StringBuilder();
        for (VarProcessVariable variable : variableList) {
            if (DeleteFlagEnum.DELETED.getCode().equals(variable.getDeleteFlag())) {
                // 待发布变量已删除
                deletedVariableName.append(MessageFormat.format("{0}、", variable.getLabel() + "V" + variable.getVersion()));
            } else if (!VariableStatusEnum.UP.equals(variable.getStatus())) {
                // 待发布变量未上架
                notListedVariableName.append(MessageFormat.format("{0}、", variable.getLabel() + "V" + variable.getVersion()));
            }
        }
        if (!StringUtils.isEmpty(deletedVariableName.toString())) {
            promptMessageBuilder.append(MessageFormat.format("引用的变量【{0}】不存在", deletedVariableName.substring(0, deletedVariableName.length() - 1)));
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, promptMessageBuilder.toString());
        }
        if (!StringUtils.isEmpty(notListedVariableName.toString())) {
            promptMessageBuilder.append(MessageFormat.format("引用的变量【{0}】未启用", notListedVariableName.substring(0, notListedVariableName.length() - 1)));
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, promptMessageBuilder.toString());
        }
    }

    /**
     * 变量清单停用时，需要判断是否被使用：
     * 1）如果只被“编辑中”和“停用”状态的实时服务和批量回溯任务使用，可以停用，停用时提示“该变量清单已经被编辑中/停用状态的实时服务/批量回溯任务使用，确认停用？”
     * 2）如果有被其他状态(审核流程中)的实时服务和批量回溯任务使用，不允许停用，停用时提示“该变量清单已经被非编辑中/停用状态的实时服务/批量回溯任务使用，不允许停用”
     *
     * @param manifestDto 清单dto
     * @return string
     */
    private String disableCheck(VariableManifestDto manifestDto) {
        String msg = "确认停用该变量清单？";
        if (!VarProcessManifestStateEnum.UP.equals(manifestDto.getManifestEntity().getState())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "清单状态不为已启用，不允许停用");
        }
        //校验是否被实时服务引用
        List<VarProcessServiceVersion> serviceState = varProcessServiceManifestService.getServiceState(manifestDto.getManifestEntity().getId());
        if (!CollectionUtils.isEmpty(serviceState)) {
            List<VarProcessServiceVersion> unDisabled = serviceState.stream().filter(service -> service.getState() != VarProcessServiceStateEnum.EDITING && service.getState() != VarProcessServiceStateEnum.DISABLED).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(unDisabled)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "该变量清单已经被非编辑中/停用状态的实时服务任务使用，不允许停用");
            }
            msg = "该变量清单已经被编辑中/停用状态的实时服务任务使用，确认停用？";
        }
        // 校验是否被批量回溯任务使用
        List<VarProcessBatchBacktracking> backTrackingState = backtrackingService.getBackTrackingState(manifestDto.getManifestEntity().getId());
        if (!CollectionUtils.isEmpty(backTrackingState)) {
            List<VarProcessBatchBacktracking> unDisabled = backTrackingState.stream().filter(backTracking -> backTracking.getStatus() != FlowStatusEnum.EDIT && backTracking.getStatus() != FlowStatusEnum.DOWN).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(unDisabled)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "该变量清单已经被非编辑中/停用状态的批量回溯任务使用，不允许停用");
            }
            msg = "该变量清单已经被编辑中/停用状态的批量回溯任务使用，确认停用？";
        }
        return msg;

    }

    /**
     * 提交测试操作校验
     * <p>校验项目:</p>
     * <ol>
     *     <li>变量清单下所有的变量都是已上架状态</li>
     *     <li>变量清单下所有的数据模型绑定都已经配置数据来源</li>
     *     <li>变量清单下所有变量依赖的扩展数据都配置了预处理逻辑</li>
     *     <li>所有依赖的预处理逻辑都已经为 "已上架" 状态</li>
     *     <li>所有依赖的变量模板都已经为 "已上架" 状态</li>
     * </ol>
     *
     * @param manifestDto 清单dto
     * @throws VariableMgtBusinessServiceException 业务异常, 带有校验未通过原因
     */
    private void submitCheck(VariableManifestDto manifestDto) {
        if (!VarProcessManifestStateEnum.EDIT.equals(manifestDto.getManifestEntity().getState())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "清单状态不为编辑中，不允许提交");
        }
        // 收集用户选择的待发布变量 ID
        List<Long> variableIdList = manifestDto.getVariablePublishList().stream()
                .map(VarProcessManifestVariable::getVariableId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(variableIdList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "该变量清单没有引用的变量，不允许提交");
        }
        // 1. 校验待发布变量上架情况
        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(
                                VarProcessVariable::getId,
                                VarProcessVariable::getCategoryId,
                                VarProcessVariable::getIdentifier,
                                VarProcessVariable::getVersion,
                                VarProcessVariable::getName,
                                VarProcessVariable::getLabel,
                                VarProcessVariable::getDataType,
                                VarProcessVariable::getStatus,
                                VarProcessVariable::getDeleteFlag
                        )
                        .in(VarProcessVariable::getId, variableIdList)
        );
        for (VarProcessVariable variable : variableList) {
            if (DeleteFlagEnum.DELETED.getCode().equals(variable.getDeleteFlag())) {
                // 待发布变量已删除
                String exceptionMessage = String.format("该清单引用的变量%s处于已删除状态，不允许提交。", variable.getName());
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, exceptionMessage);
            }
            if (!VariableStatusEnum.UP.equals(variable.getStatus())) {
                // 待发布变量未上架（未启用）
                String exceptionMessage = String.format("该清单引用的变量%s未启用，不允许提交。", variable.getName());
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, exceptionMessage);
            }
        }

        // 2.校验是否有外部传入数据模型
        boolean outParamExist = manifestDto.getDataModelMappingList().stream().anyMatch(item -> Objects.equals(item.getSourceType(), VarProcessDataModelSourceType.OUTSIDE_PARAM.getCode()));
        Assert.isTrue(outParamExist,"变量清单缺少外部传入数据模型对象，请检查");

        // 3.校验加工流程是否可用
        VariableManifestFlowSaveInputDto flowDto = new VariableManifestFlowSaveInputDto(manifestDto.getManifestEntity().getVarProcessSpaceId(),
                manifestDto.getManifestEntity().getId(),
                parseObject(manifestDto.getManifestEntity().getContent()));
        VariableCompileOutputDto variableCompileOutputDto = variableManifestFlowBiz.checkFlow(flowDto);
        if (!CollectionUtils.isEmpty(variableCompileOutputDto.getErrorMessageList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, variableCompileOutputDto.getErrorMessageList().get(0));
        }

    }

    /**
     * 提交测试操作校验
     * <p>校验项目:</p>
     * <ol>
     *     <li>变量清单下所有的变量都是已上架状态</li>
     *     <li>变量清单下所有的数据模型绑定都已经配置数据来源</li>
     *     <li>变量清单下所有变量依赖的扩展数据都配置了预处理逻辑</li>
     *     <li>所有依赖的预处理逻辑都已经为 "已上架" 状态</li>
     *     <li>所有依赖的变量模板都已经为 "已上架" 状态</li>
     * </ol>
     *
     * @param prepIdentifierList           prepIdentifierList
     * @param manifestDto                  变量清单 ID
     * @param commonFunctionIdentifierList 变量清单使用的公共函数标识 List
     * @throws VariableMgtBusinessServiceException 业务异常, 带有校验未通过原因
     */
    private void validateTestSubmission(VariableManifestDto manifestDto, List<String> commonFunctionIdentifierList, List<String> prepIdentifierList) {

        // 3. 校验待发布变量依赖的扩展数据预处理:变量清单下所有变量依赖的扩展数据得预处理逻辑都已经在流程中配置引用；（目前只做了2级对象的验证）
        checkPrep(manifestDto, prepIdentifierList);

        if (!CollectionUtils.isEmpty(commonFunctionIdentifierList)) {
            // 查询变量清单使用的全部公共函数 (预处理逻辑, 变量模板, 公共方法)
            List<VarProcessFunction> utilizedCommonFunctionList = varProcessFunctionService.list(
                    Wrappers.<VarProcessFunction>lambdaQuery()
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
                            .eq(VarProcessFunction::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                            .in(VarProcessFunction::getIdentifier, commonFunctionIdentifierList));
            Map<FunctionTypeEnum, List<VarProcessFunction>> utilizedCommonFunctionMap = utilizedCommonFunctionList.stream()
                    .collect(Collectors.groupingBy(VarProcessFunction::getFunctionType));

            utilizedCommonFunctionMap.forEach((typeEnum, functionList) -> {
                if (!CollectionUtils.isEmpty(functionList)) {
                    functionList.stream().collect(Collectors.groupingBy(VarProcessFunction::getIdentifier)).forEach((identifier, funcList) -> {
                        if (funcList.stream().allMatch(func -> Objects.equals(DeleteFlagEnum.DELETED.getCode(), func.getDeleteFlag()))) {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "引用的" + typeEnum.getDesc() + "【" + funcList.get(0).getName() + "】不存在");
                        } else if (funcList.stream().noneMatch(func -> FlowStatusEnum.UP.equals(func.getStatus()))) {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "引用的" + typeEnum.getDesc() + "【" + funcList.get(0).getName() + "】未启用");
                        }
                    });
                }
            });
        }
    }

    private void checkPrep(VariableManifestDto manifestDto, List<String> prepIdentifierList) {

        //预处理逻辑对象
        Set<String> varPathMap = variableManifestSupport.getExtendedPropertiesUtilizedByPublishingVariable(manifestDto);
        if (CollectionUtils.isEmpty(varPathMap)) {
            return;
        }
        List<VarProcessFunction> prepList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(prepIdentifierList)) {
            prepList = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(VarProcessFunction::getPrepObjectName)
                            .eq(VarProcessFunction::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                            .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.PREP)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                            .in(VarProcessFunction::getIdentifier, prepIdentifierList)
            );
        }
        Set<String> setMap = new HashSet<>();
        if (!CollectionUtils.isEmpty(prepList)) {
            setMap = prepList.stream().map(VarProcessFunction::getPrepObjectName).collect(Collectors.toSet());
        }
        for (String objectName : varPathMap) {
            if (!setMap.contains(objectName)) {
                // 依赖的扩展数据不存在于经过预处理的扩展数据 Set
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "该清单依赖的扩展数据" + objectName + "未定义预处理逻辑，不可提交。");
            }
        }
    }

    /**
     * 校验外部服务引入
     *
     * @param spaceId    变量空间 ID
     * @param manifestId 变量清单 ID
     * @return 提示信息
     */
    private String checkManifestOutside(Long spaceId, Long manifestId) {
        // 查询变量清单执行流程-外部服务的使用关系
        List<VarProcessManifestOutside> flowUtilizationList = varProcessManifestOutsideServiceService.list(
                new QueryWrapper<VarProcessManifestOutside>().lambda()
                        .eq(VarProcessManifestOutside::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessManifestOutside::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(flowUtilizationList)) {
            return null;
        }
        // 查询执行流程使用的外部服务
        List<Long> outsideServiceIds = flowUtilizationList.stream()
                .map(VarProcessManifestOutside::getOutsideServiceId)
                .collect(Collectors.toList());
        // 查询外部服务在变量空间的引入对象
        List<VarProcessOutsideRef> referencedOutsideServiceList = varProcessOutsideServiceRefService.list(
                new QueryWrapper<VarProcessOutsideRef>().lambda()
                        .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getOutsideServiceId)
                        .eq(VarProcessOutsideRef::getVarProcessSpaceId, spaceId)
        );
        List<Long> referencedOutsideServiceIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(referencedOutsideServiceList)) {
            referencedOutsideServiceIdList = referencedOutsideServiceList.stream()
                    .map(VarProcessOutsideRef::getOutsideServiceId)
                    .collect(Collectors.toList());
        }
        for (Long outsideServiceId : outsideServiceIds) {
            OutsideServiceDetailRestOutputDto outsideServiceDetailRestOutputDto = outsideService.getOutsideServiceDetailRestById(outsideServiceId);
            if (outsideServiceDetailRestOutputDto.getName() == null) {
                // 未查询到外部服务信息
                return "未查询到引入的外部服务";
            }
            if (!referencedOutsideServiceIdList.contains(outsideServiceId)) {
                return "引入的外部服务对象" + outsideServiceDetailRestOutputDto.getName() + "已取消引入";
            }
        }
        return null;
    }

    /**
     * 校验内部数据引入
     *
     * @param spaceId    变量空间 ID
     * @param manifestId 变量清单 ID
     * @return 提示信息
     */
    private String checkManifestInternal(Long spaceId, Long manifestId) {
        List<VarProcessManifestInternal> list = varProcessManifestInternalDataService.list(
                new QueryWrapper<VarProcessManifestInternal>().lambda()
                        .eq(VarProcessManifestInternal::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessManifestInternal::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<VarProcessInternalData> refList = varProcessInternalDataService.list(
                new QueryWrapper<VarProcessInternalData>().lambda()
                        .select(VarProcessInternalData::getId, VarProcessInternalData::getDeleteFlag, VarProcessInternalData::getName)
                        .eq(VarProcessInternalData::getVarProcessSpaceId, spaceId)
        );
        Map<String, VarProcessInternalData> identifierMap = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(refList)) {
            identifierMap = refList.stream().collect(Collectors.toMap(VarProcessInternalData::getIdentifier, Function.identity(), (k1, k2) -> k2));
        }
        // 收集删除的内部数据名称
        StringBuilder removedInternalDataNameBuilder = new StringBuilder();
        for (VarProcessManifestInternal internalData : list) {
            VarProcessInternalData data = identifierMap.get(internalData.getIdentifier());
            if (data != null && data.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                removedInternalDataNameBuilder.append(data.getName()).append("、");
            }
        }
        // 组装提示信息
        String removedInternalDataName = removedInternalDataNameBuilder.toString();
        if (StringUtils.isEmpty(removedInternalDataName)) {
            return null;
        } else {
            return "引入的内部数据[" + removedInternalDataName.substring(0, removedInternalDataName.length() - 1) + "]已删除，";
        }
    }

    /**
     * 组装 rawData
     *
     * @param dataModelList 变量空间数据模型 List
     * @return 组装的 rawData JSON Schema String
     */
    private String assembleRawData(List<VarProcessDataModel> dataModelList) {
        JSONObject selectedRawData = parseObject(VarProcessDataModelBoilerplateEnum.RAW_DATA.getContent(), Feature.OrderedField);
        selectedRawData.put("properties", new JSONObject());
        for (VarProcessDataModel dataModel : dataModelList) {
            JSONObject dataModelJsonSchema = parseObject(dataModel.getContent(), Feature.OrderedField);
            // 读取 title 字段
            String key = dataModelJsonSchema.getString("title");
            // 移除 title 字段
            dataModelJsonSchema.remove("title");
            // 添加数据模型 JSONSchema 至 rawData 的 properties 字段下, key 为 title 内容
            selectedRawData.getJSONObject("properties").put(key, dataModelJsonSchema);
        }
        return selectedRawData.toJSONString();
    }

    /**
     * 启用变量清单
     *
     * @param serviceId      某一个具体版本的实时服务ID
     * @param actionTypeEnum 变量清单操作枚举类
     * @param memo           变量清单生命周期备注
     */
    public void enableVariableManifest(Long serviceId, VarProcessManifestActionTypeEnum actionTypeEnum, String memo) {
        // 1.找出所有该实时服务所关联的变量清单（没有发布过）
        List<VarProcessServiceManifest> serviceManifests = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, serviceId)
                .ne(VarProcessServiceManifest::getManifestPublishState, ManifestPublishStateEnum.PUBLISHED));
        Map<Long, String> manifestNameMap = new HashMap<>(MagicNumbers.EIGHT);
        // 2.发布之前未发布过的清单
        serviceManifests.forEach(item -> {
            Long manifestId = item.getManifestId();
            VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(manifestId);
            manifestDto.setServiceManifestId(item.getId());
            manifestNameMap.put(item.getId(), manifestDto.getManifestEntity().getVarManifestName());
            varProcessServiceManifestService.updateById(item.setManifestPublishState(ManifestPublishStateEnum.PUBLISHING));
            // 2.1 发布变量清单
            boolean enableStatus = variablePublishBiz.publishVariable(manifestDto.getServiceManifestId().toString(),
                    MagicNumbers.SHORT_ONE.equals(item.getManifestRole()) ? MagicNumbers.ONE : MagicNumbers.ZERO);
            varProcessServiceManifestService.updateById(item.setManifestPublishState(
                    enableStatus ? ManifestPublishStateEnum.PUBLISHED : ManifestPublishStateEnum.FAILPUBLISH));
        });
        // 3.最后进行验证，看看有没有发布失败的
        serviceManifests = serviceManifests.stream().filter(item -> !ManifestPublishStateEnum.PUBLISHED.equals(item.getManifestPublishState())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(serviceManifests)) {
            StringJoiner joiner = new StringJoiner(",");
            for (VarProcessServiceManifest serviceManifest : serviceManifests) {
                joiner.add(StringPool.LEFT_SQ_BRACKET + manifestNameMap.get(serviceManifest.getId()) + StringPool.RIGHT_SQ_BRACKET);
            }
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_PUBLISH_FAIL, "实时服务启用失败或发布超时，变量清单" + joiner + "未成功发布，请稍后再试");
        }
    }


    /**
     * 通过操作变更变量清单状态
     *
     * @param manifestId     变量接口 ID
     * @param actionTypeEnum 变量清单操作枚举类型
     */
    private void mutateStateThroughAction(Long manifestId, VarProcessManifestActionTypeEnum actionTypeEnum) {
        varProcessManifestService.update(Wrappers.<VarProcessManifest>lambdaUpdate()
                .eq(VarProcessManifest::getId, manifestId)
                .set(VarProcessManifest::getState, actionTypeEnum.getChangeStatus())
                .set(VarProcessManifest::getUpdatedUser, SessionContext.getSessionUser().getUsername())
                .set(VarProcessManifest::getUpdatedTime, new Date()));
    }

    /**
     * 记录接口生命周期
     *
     * @param manifestId     变量清单 ID
     * @param actionTypeEnum 变量清单操作枚举类
     * @param memo           备注
     */
    void recordManifestLifecycle(Long manifestId, VarProcessManifestActionTypeEnum actionTypeEnum, String memo) {
        recordManifestLifecycle(manifestId, actionTypeEnum, actionTypeEnum.getChangeStatus(), memo, SessionContext.getSessionUser().getUsername());
    }

    /**
     * 记录接口生命周期
     *
     * @param manifestId     变量清单 ID
     * @param actionTypeEnum 变量清单操作枚举类
     * @param statusEnum     操作后状态枚举类
     * @param memo           备注
     * @param userName       用户名
     */
    private void recordManifestLifecycle(Long manifestId, VarProcessManifestActionTypeEnum actionTypeEnum, VarProcessManifestStateEnum statusEnum,
                                         String memo, String userName) {
        VarProcessManifestCycle newLifecycleRecord = VarProcessManifestCycle.builder().manifestId(manifestId).operation(actionTypeEnum.getCode())
                .status(statusEnum.getCode()).memo(memo).createdUser(userName).updatedUser(userName).build();
        varProcessManifestLifecycleService.save(newLifecycleRecord);
    }

    /**
     * 记录系统动态
     *
     * @param actionDesc      操作描述
     * @param spaceId         空间 ID
     * @param manifestId      服务 ID
     * @param manifestVersion 清单版本
     */
    void saveDynamic(String actionDesc, Long spaceId, Long manifestId, Integer manifestVersion) {
        // 查询清单名称
        String manifestName = varProcessManifestService.getById(manifestId).getVarManifestName();
        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().spaceType(SysDynamicSpaceTypeEnum.VARIABLE.getCode())
                .varSpaceId(spaceId).operateType(actionDesc).typeEnum(SysDynamicBusinessBucketEnum.VARIABLE_MANIFEST).businessId(spaceId)
                .businessDesc(StringUtils.isEmpty(manifestVersion) ? manifestName : (manifestName + " V" + manifestVersion)).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }
}
