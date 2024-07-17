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

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.nacos.common.utils.Objects;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.google.common.collect.Sets;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.lock.LockClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.input.ManifestListInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VarModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestConfigInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestCreatInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestConfigOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableUseOutputVo;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessManifestActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VariableManifestCreationApproachEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDocument;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestCycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestResults;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessCategoryBiz;
import com.wiseco.var.process.app.server.service.VarProcessCategoryService;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessDocumentService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.VarProcessTestResultsService;
import com.wiseco.var.process.app.server.service.VarProcessTestService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.ManifestListQueryDto;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.ServiceUsingManifestDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VarProcessDataModelDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestOutsideServiceDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestBasicConfigDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishVariableVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishingVariableDTO;
import com.wiseco.var.process.app.server.service.dto.input.VariableManifestDuplicationInputDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.decision.jsonschema.util.enums.JsonSchemaFieldEnum.EXTEND_FIELD;
import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

@Slf4j
@Service
public class VariableManifestSubBiz extends VariableManifestBiz {

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
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VarProcessManifestOutsideService varProcessManifestOutsideServiceService;
    @Autowired
    private VarProcessManifestLifecycleService varProcessManifestLifecycleService;
    @Autowired
    private VarProcessTestService varProcessTestVariableService;
    @Autowired
    private VarProcessTestResultsService varProcessTestVariableResultsService;
    @Autowired
    private VarProcessDocumentService varProcessDocumentService;
    @Autowired
    private BacktrackingService backtrackingService;
    @Autowired
    private VarProcessManifestInternalService varProcessManifestInternalDataService;
    @Resource
    private DeptService deptService;
    @Autowired
    private VarProcessManifestFunctionService varProcessManifestFunctionService;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;
    @Resource(name = "distributedLockClient")
    private LockClient lockClient;

    /**
     * 新增变量清单
     *
     * @param inputDto inputDto
     * @return java.lang.Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createNewManifest(VariableManifestCreatInputDto inputDto) {
        //验证参数
        checkCreateManifestInputDto(inputDto);
        //保存数据
        VarProcessManifest varManifest = VarProcessManifest
                .builder()
                .varProcessSpaceId(inputDto.getSpaceId())
                //                .serviceId(inputDto.getServiceId())
                .varManifestName(inputDto.getName()).categoryId(inputDto.getCategoryId())
                .description(StringUtils.isEmpty(inputDto.getDescription()) ? "" : inputDto.getDescription())
                .createdUser(SessionContext.getSessionUser().getUsername()).state(VarProcessManifestStateEnum.EDIT).deleteFlag(1).createdTime(new Date())
                .build();
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            varManifest.setDeptCode(department.getCode());
        }
        varManifest.setUpdatedUser(varManifest.getCreatedUser());
        varManifest.setUpdatedTime(varManifest.getCreatedTime());
        varManifest.setIdentifier(GenerateIdUtil.generateId());
        varProcessManifestService.save(varManifest);
        // 记录系统动态
        saveDynamic(SysDynamicOperateTypeEnum.CREATE.getName(), inputDto.getSpaceId(), varManifest.getId(), varManifest.getVersion());
        // 记录生命周期
        recordManifestLifecycle(varManifest.getId(), VarProcessManifestActionTypeEnum.CREATE, null);
        return varManifest.getId();
    }

    /**
     * 验证新增参数
     *
     * @param inputDto 前端发送过来的实体类
     */
    private void checkCreateManifestInputDto(VariableManifestCreatInputDto inputDto) {
        List<VarProcessManifest> selectlist = varProcessManifestService.list(new QueryWrapper<VarProcessManifest>().lambda().
                select(VarProcessManifest::getParentManifestId).
                eq(VarProcessManifest::getVarProcessSpaceId, inputDto.getSpaceId()).
                eq(VarProcessManifest::getVarManifestName, inputDto.getName()).
                ne(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.DELETED.getCode())
        );
        if (!CollectionUtils.isEmpty(selectlist)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_EXISTS, "该变量清单名称已存在！不允许重复");
        }
    }

    /**
     * 保存变量清单时校验
     *
     * @param inputDto 前端发送过来的实体类
     */
    public void saveManifestCheck(VariableManifestConfigInputDto inputDto) {
        StringBuilder errMessage = new StringBuilder();
        //校验清单名称不能重复
        List<VarProcessManifest> list = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getVarManifestName)
                .eq(VarProcessManifest::getVarProcessSpaceId, inputDto.getBasicConfig().getSpaceId())
                .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .ne(VarProcessManifest::getId, inputDto.getBasicConfig().getManifestId()));
        for (VarProcessManifest manifest : list) {
            if (manifest.getVarManifestName().equals(inputDto.getBasicConfig().getName())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_EXISTS, "已经存在名称为[" + manifest.getVarManifestName() + "]的变量清单");
            }
        }
        //校验变量版本是否处于启用状态
        if (!CollectionUtils.isEmpty(inputDto.getVariablePublishList())) {
            List<VariableManifestPublishVariableVo> variablePublishList = inputDto.getVariablePublishList();
            Map<ColRoleEnum, List<VariableManifestPublishVariableVo>> colRoleMap = variablePublishList.stream()
                    .collect(Collectors.groupingBy(variable -> variable.getColRole() != null ? variable.getColRole() : ColRoleEnum.GENERAL));
            Arrays.asList(ColRoleEnum.TARGET,ColRoleEnum.GROUP).forEach(colRoleEnum -> {
                List<VariableManifestPublishVariableVo> indexVariables = colRoleMap.getOrDefault(colRoleEnum, new ArrayList<>());
                if (indexVariables.size() > MagicNumbers.INT_10) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, MessageFormat.format("最多将{0}个变量设置为{1}",MagicNumbers.INT_10,colRoleEnum.getDesc()));
                }
            });
            Map<Long, String> variableNameMap = variablePublishList.stream().collect(Collectors.toMap(variable ->
                    variable.getSelectedVersionInfo().getVariableId(), VariableManifestPublishVariableVo::getName));
            List<Long> varIdList = new ArrayList<>(variableNameMap.keySet());
            if (!CollectionUtils.isEmpty(varIdList)) {
                List<VarProcessVariable> disabledVars = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                        .select(VarProcessVariable::getLabel)
                        .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getBasicConfig().getSpaceId())
                        .in(VarProcessVariable::getId, varIdList)
                        .ne(VarProcessVariable::getStatus, VariableStatusEnum.UP));
                if (!disabledVars.isEmpty()) {
                    disabledVars.forEach(unused -> errMessage.append("[").append(unused.getLabel()).append("],"));
                    errMessage.deleteCharAt(errMessage.length() - 1);
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, "选择的变量" + errMessage + "不属于启用状态");
                }
            }
            if (null == inputDto.getDataModelBindingList()) {
                inputDto.setDataModelBindingList(new ArrayList<>());
            }
            //获取变量使用的数据模型
            List<String> mappingModelNames = new ArrayList<>();
            List<String> pathes = varProcessManifestService.getPathes(varIdList);
            if (!pathes.isEmpty()) {
                mappingModelNames = varProcessManifestService.getNamesByPathes(pathes);
            }
            //获取所有数据模型的名称list
            RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
            roleDataAuthorityDTO.setType(ALL_PERMISSION);
            List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionList(inputDto.getBasicConfig().getSpaceId(), roleDataAuthorityDTO);
            List<String> allModelNames = dataModelList.stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
            //校验数据模型是否在数据库中存在
            for (String modelName : mappingModelNames) {
                if (modelName != null && !allModelNames.contains(modelName)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "数据模型[" + modelName + "]已经被删除，请重新获取数据模型");
                }
            }
            List<VarProcessDataModel> mappingModels = new ArrayList<>();
            if (!CollectionUtils.isEmpty(mappingModelNames)) {
                mappingModels = varProcessManifestService.getModelsByNames(inputDto.getBasicConfig().getSpaceId(), mappingModelNames);
            }
            List<String> inputModelList = inputDto.getDataModelBindingList().stream().map(VariableManifestDataModelMappingVo::getName).collect(Collectors.toList());
            List<String> uncontains = new ArrayList<>();

            checkDataModelsExistence(errMessage, mappingModelNames, mappingModels, inputModelList, uncontains);
            checkDataModelsQueryCondition(inputDto.getDataModelBindingList());
        }
    }

    private void checkDataModelsQueryCondition(List<VariableManifestDataModelMappingVo> dataModelBindingList) {
        Set<String> mappingSet = Sets.newHashSet();
        for (VariableManifestDataModelMappingVo dataModelMappingVo : dataModelBindingList) {
            if (!CollectionUtils.isEmpty(dataModelMappingVo.getQueryConditionList())) {
                for (VariableManifestDataModelMappingVo.QueryCondition queryCondition : dataModelMappingVo.getQueryConditionList()) {
                    if (StringUtils.isEmpty(queryCondition.getMappingCode())) {
                        throw new VariableMgtBusinessServiceException("[" + queryCondition.getFullPathValue() + "]需设置查询条件映射");
                    }
                    if (DataTypeEnum.OBJECT.getDesc().equals(queryCondition.getVarType())) {
                        throw new VariableMgtBusinessServiceException("仅基础属性支持设置为查询条件");
                    }
                    if (!mappingSet.add(queryCondition.getMappingCode())) {
                        throw new VariableMgtBusinessServiceException("查询条件[" + queryCondition.getMappingName() + "]已被映射");
                    }
                    if (mappingSet.size() > MagicNumbers.INT_10) {
                        throw new VariableMgtBusinessServiceException("最多将10个变量设置为查询条件");
                    }
                }
            }
        }
    }

    /**
     * 分页查询变量清单
     *
     * @param inputDto 输入实体
     * @return 分页查询的结果
     */
    public IPage<ManifestListOutputVo> getManifestList(ManifestListInputVo inputDto) {
        //分页设置
        Page<ManifestListOutputVo> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        //* 获取已被使用的变量清单id set
        Set<Long> usedManifests = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                        .select(VarProcessServiceManifest::getManifestId)
                        .eq(VarProcessServiceManifest::getVarProcessSpaceId, inputDto.getSpaceId()))
                .stream()
                .map(VarProcessServiceManifest::getManifestId).collect(Collectors.toSet());
        usedManifests.addAll(backtrackingService.list(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                        .select(VarProcessBatchBacktracking::getManifestId)
                        .eq(VarProcessBatchBacktracking::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                .stream().map(VarProcessBatchBacktracking::getManifestId).collect(Collectors.toSet()));
        //如果查询条件为“已使用”，但已使用的manifest set为空，直接返回
        if (Boolean.TRUE.equals(inputDto.getUsed()) && CollectionUtils.isEmpty(usedManifests)) {
            return page;
        }
        //* 获取分类Map<分类id，分类>
        Map<Long, VarProcessCategory> categoryMap = varProcessCategoryService.list(Wrappers.<VarProcessCategory>lambdaQuery().eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessCategory::getEnabled, 1).eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.MANIFEST))
                .stream().collect(Collectors.toMap(VarProcessCategory::getId, item -> item, (key1, key2) -> key1));
        //* 获取测试list
        List<Long> testedManifests = varProcessTestVariableService.getTestedManifests();

        // 最近一次拒绝审批信息Map集合
        List<VarProcessManifestCycle> lifecycleList = varProcessManifestLifecycleService.list(new QueryWrapper<VarProcessManifestCycle>().lambda()
                .eq(VarProcessManifestCycle::getStatus, VarProcessManifestStateEnum.REFUSE.getCode()));
        Map<Long, VarProcessManifestCycle> latestLifecycleMap = lifecycleList.stream()
                .collect(Collectors.toMap(
                        VarProcessManifestCycle::getManifestId,
                        lifecycle -> lifecycle,
                        (existing, replacement) -> existing.getId() > replacement.getId() ? existing : replacement
                ));
        // 查询manifest表
        ManifestListQueryDto queryDto = new ManifestListQueryDto();
        BeanUtils.copyProperties(inputDto, queryDto);
        if (!StringUtils.isEmpty(inputDto.getDeptId())) {
            queryDto.setDeptCode(inputDto.getDeptId());
        }

        //获取分类下所有分类的ids
        Long categoryId = inputDto.getCategoryId();
        if (categoryId != null) {
            List<Long> categoryIdList = new ArrayList<>();
            categoryIdList.add(categoryId);
            VarProcessCategoryBiz.getCategoriesUndered(categoryMap, categoryIdList, categoryId);
            queryDto.setCategoryIds(categoryIdList);
        }
        //设置排序字段
        setSortedKey(inputDto, queryDto);

        //权限控制
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return page;
        }
        BeanUtils.copyProperties(roleDataAuthority, queryDto);
        IPage<VarProcessManifest> manifestList = varProcessManifestService.getManifestList(page, queryDto);
        Map<Long, Long> variableAmountMap = varProcessManifestVariableService.findVariableAmount(manifestList.getRecords().stream().map(VarProcessManifest::getId).collect(Collectors.toList()));
        List<ManifestListOutputVo> outputVos = asssembleOutputVos(usedManifests, categoryMap, testedManifests, latestLifecycleMap, manifestList, variableAmountMap);
        page.setTotal(manifestList.getTotal()).setPages(manifestList.getPages()).setRecords(outputVos);
        return page;
    }

    private List<ManifestListOutputVo> asssembleOutputVos(Set<Long> usedManifests, Map<Long, VarProcessCategory> categoryMap, List<Long> testedManifests,
                                                          Map<Long, VarProcessManifestCycle> latestLifecycleMap, IPage<VarProcessManifest> manifestList, Map<Long, Long> variableAmountMap) {


        Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(manifestList.getRecords().stream().map(VarProcessManifest::getDeptCode).collect(Collectors.toList()));
        List<ManifestListOutputVo> outputVos = new ArrayList<>();
        List<String> userNames = manifestList.getRecords().stream().flatMap(obj -> Stream.of(obj.getCreatedUser(), obj.getUpdatedUser()))
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userNames);
        for (VarProcessManifest manifest : manifestList.getRecords()) {
            ManifestListOutputVo outputVo = ManifestListOutputVo.builder()
                    .id(manifest.getId())
                    .name(manifest.getVarManifestName())
                    .category(categoryMap.get(manifest.getCategoryId()) == null ? null : categoryMap.get(manifest.getCategoryId()).getName())
                    .status(manifest.getState())
                    .tested(testedManifests.contains(manifest.getId()))
                    .used(usedManifests.contains(manifest.getId()))
                    .createdUser(fullNameMap.getOrDefault(manifest.getCreatedUser(), ""))
                    .updatedUser(fullNameMap.getOrDefault(manifest.getUpdatedUser(), ""))
                    .createdTime(manifest.getCreatedTime())
                    .updatedTime(manifest.getUpdatedTime())
                    .varAmount(variableAmountMap.getOrDefault(manifest.getId(), 0L))
                    .build();
            outputVo.setDeptName(deptMap.get(manifest.getDeptCode()) == null ? "" : deptMap.get(manifest.getDeptCode()));

            // 审核拒绝后的描述
            if (outputVo.getStatus() == VarProcessManifestStateEnum.REFUSE) {

                VarProcessManifestCycle manifestCycle = latestLifecycleMap.get(outputVo.getId());
                if (manifestCycle != null && VarProcessManifestStateEnum.REFUSE.getCode().equals(manifestCycle.getStatus())) {
                    JSONObject desc = new JSONObject();
                    desc.put("审核人", fullNameMap.getOrDefault(manifest.getCreatedUser(), ""));
                    desc.put("审核时间", manifestCycle.getCreatedTime());
                    desc.put("拒绝原因", manifestCycle.getMemo());
                    outputVo.setApproDescription(desc.toJSONString());
                }
            }
            outputVos.add(outputVo);
        }
        return outputVos;
    }

    private static void setSortedKey(ManifestListInputVo inputDto, ManifestListQueryDto queryDto) {
        String sortedKey = inputDto.getOrder();
        if (StringUtils.isEmpty(sortedKey)) {
            queryDto.setSortedKey("updated_time");
            queryDto.setSortmethod("desc");
        } else {
            queryDto.setSortmethod(sortedKey.substring(sortedKey.indexOf("_") + 1));
            String orderKey = sortedKey.substring(0, sortedKey.indexOf("_"));
            switch (orderKey) {
                case "name":
                    queryDto.setSortedKey("var_manifest_name");
                    break;
                case "category":
                    queryDto.setSortedKey("category_id");
                    break;
                case "createdTime":
                    queryDto.setSortedKey("created_time");
                    break;
                default:
                    queryDto.setSortedKey("updated_time");
            }

        }
    }

    /**
     * 复制变量清单的业务逻辑
     *
     * @param inputDto 前端发送过来的实体Dto
     * @return 复制之后的变量清单Id
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long duplicateManifest(VariableManifestDuplicationInputDto inputDto) {
        // 1.获取原变量清单的实体类对象
        VariableManifestDto archetypeManifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getArchetypeManifestId());
        if (archetypeManifestDto == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到被复制的变量清单!");
        }
        // 2.查询副本变量清单的名字是否重复
        LambdaQueryWrapper<VarProcessManifest> manifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        manifestLambdaQueryWrapper.select(VarProcessManifest::getId);
        manifestLambdaQueryWrapper.eq(VarProcessManifest::getVarManifestName, inputDto.getManifestNewName());
        manifestLambdaQueryWrapper.eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        // 3.添加分布式锁
        boolean isLock = false;
        isLock = lockClient.acquire(CacheKeyPrefixConstant.MANIFEST_DUPLICATE_PREFIX + inputDto.getManifestNewName());
        if (!isLock) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "复制变量清单的操作失败!");
        }
        // 4.如果获取分布式锁成功了,就查询是否重复,并释放分布式锁
        try {
            List<VarProcessManifest> manifests = varProcessManifestService.list(manifestLambdaQueryWrapper);
            if (!CollectionUtils.isEmpty(manifests)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_EXISTS, "复制出的变量清单的名称重复了!");
            }
        } finally {
            lockClient.release(CacheKeyPrefixConstant.MANIFEST_DUPLICATE_PREFIX + inputDto.getManifestNewName());
        }
        // 5.开始复制出一个新的副本变量清单
        String deptCode = StringPool.EMPTY;
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            deptCode = department.getCode();
        }
        VarProcessManifest duplicatedManifest = VarProcessManifest.builder().varProcessSpaceId(inputDto.getSpaceId()).identifier(GenerateIdUtil.generateId())
                .serviceId(inputDto.getServiceId()).version(MagicNumbers.ONE).state(VarProcessManifestStateEnum.EDIT)
                .serialNo(archetypeManifestDto.getManifestEntity().getSerialNo()).deleteFlag(archetypeManifestDto.getManifestEntity().getDeleteFlag())
                .description(archetypeManifestDto.getManifestEntity().getDescription()).parentManifestId(inputDto.getArchetypeManifestId())
                .content(archetypeManifestDto.getManifestEntity().getContent())
                .schemaSnapshot(archetypeManifestDto.getManifestEntity().getSchemaSnapshot()).createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername()).varManifestName(inputDto.getManifestNewName())
                .categoryId(archetypeManifestDto.getManifestEntity().getCategoryId()).deptCode(deptCode).build();
        // 6.执行插入操作
        boolean isSave = varProcessManifestService.save(duplicatedManifest);
        if (!isSave) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "复制变量清单失败!");
        }
        // 7.根据用户的选择进行分类处理，如果用户选择了使用原有变量版本
        if (inputDto.getCreateApproach().equals(VariableManifestCreationApproachEnum.ORIGINAL.getCode())) {
            return getArchetypeDuplicationId(inputDto, MagicNumbers.ONE, archetypeManifestDto, duplicatedManifest);
        } else if (inputDto.getCreateApproach().equals(VariableManifestCreationApproachEnum.LATEST.getCode())) {
            // 8.如果用户选择了使用最新变量版本
            return getNewVersionDuplicationId(inputDto, MagicNumbers.ONE, archetypeManifestDto, duplicatedManifest);
        } else {
            // 9.如果创建变量的方式有误
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "不支持的清单版本来源设置");
        }
    }

    /**
     * 按照最新版本的方法复制变量清单
     *
     * @param inputDto             前端发过来的输入实体Dto
     * @param newManifestVersion   新变量清单的版本号
     * @param archetypeManifestDto 原有变量清单的Dto
     * @param duplicatedManifest   复制之后的变量清单Dto
     * @return 复制之后的变量清单的Id
     */
    private Long getNewVersionDuplicationId(VariableManifestDuplicationInputDto inputDto, int newManifestVersion, VariableManifestDto archetypeManifestDto, VarProcessManifest duplicatedManifest) {
        // 1.复制变量
        List<VarProcessManifestVariable> originalVariableLists = archetypeManifestDto.getVariablePublishList();
        // 2.获取版本最新的变量(identifier+id组合)
        List<Long> newVersionOfVariables = varProcessVariableService.getNewVersionOfVariables(archetypeManifestDto.getManifestEntity().getId());
        if (newVersionOfVariables.size() != originalVariableLists.size()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, "该变量清单所关联的某些变量已经下架,使得发布的变量数量前后不一致!");
        }
        if (!CollectionUtils.isEmpty(originalVariableLists)) {
            // 3.给变量更新variable_id和其他属性
            List<VarProcessManifestVariable> duplicatedVariableLists = new ArrayList<>(originalVariableLists.size());
            for (int i = 0; i < originalVariableLists.size(); i++) {
                VarProcessManifestVariable item = new VarProcessManifestVariable();
                item.setId(null);
                item.setVarProcessSpaceId(originalVariableLists.get(i).getVarProcessSpaceId());
                item.setManifestId(duplicatedManifest.getId());
                item.setVariableId(newVersionOfVariables.get(i));
                item.setOutputFlag(originalVariableLists.get(i).getOutputFlag());
                item.setIsIndex(originalVariableLists.get(i).getIsIndex());
                item.setColRole(originalVariableLists.get(i).getColRole());
                item.setCreatedUser(SessionContext.getSessionUser().getUsername());
                item.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                duplicatedVariableLists.add(item);
            }
            varProcessManifestVariableService.saveBatch(duplicatedVariableLists);
        }
        // 4.调用熊哲文的接口，获取新的变量所关联的数据模型列表,然后更新var_process_manifest_data_model表中的内容
        VarModelInputDto inputParam = new VarModelInputDto(newVersionOfVariables, duplicatedManifest.getVarProcessSpaceId());
        List<VarProcessDataModel> modelByVarId = varProcessManifestService.getModelsByVariableIds(inputParam);
        // 5.再次获取手动添加的数据模型
        List<String> objectNames = modelByVarId.stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
        List<VarProcessDataModel> dataModelByHandle = varProcessManifestDataModelService.getDataModelByHandle(objectNames, archetypeManifestDto.getManifestEntity().getId(), 1L);
        modelByVarId.addAll(dataModelByHandle);

        Map<String, String> modelNameQueryConditionMap = archetypeManifestDto.getDataModelMappingList().stream().collect(Collectors.toMap(VarProcessManifestDataModel::getObjectName, model -> Objects.requireNonNullElse(model.getModelQueryCondition(), "")));
        if (!CollectionUtils.isEmpty(modelByVarId)) {
            List<VarProcessManifestDataModel> duplicatedDataModelLists = new ArrayList<>(modelByVarId.size());
            for (VarProcessDataModel varProcessDataModel : modelByVarId) {
                VarProcessManifestDataModel item = new VarProcessManifestDataModel();
                item.setId(null);
                item.setVarProcessSpaceId(duplicatedManifest.getVarProcessSpaceId());
                item.setManifestId(duplicatedManifest.getId());
                item.setObjectName(varProcessDataModel.getObjectName());
                item.setObjectVersion(varProcessDataModel.getVersion());
                item.setModelQueryCondition(modelNameQueryConditionMap.getOrDefault(varProcessDataModel.getObjectName(),""));
                VarProcessDataModelSourceType sourceType = varProcessDataModel.getObjectSourceType();
                item.setSourceType(sourceType.getCode());
                item.setCreatedUser(SessionContext.getSessionUser().getUsername());
                item.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                duplicatedDataModelLists.add(item);
            }
            varProcessManifestDataModelService.saveBatch(duplicatedDataModelLists);
        }
        // 6.记录系统动态和生命周期
        saveDynamic(SysDynamicOperateTypeEnum.COPY.getName(), inputDto.getSpaceId(), duplicatedManifest.getId(), newManifestVersion);
        recordManifestLifecycle(duplicatedManifest.getId(), VarProcessManifestActionTypeEnum.CREATE, null);
        return duplicatedManifest.getId();
    }

    /**
     * 按照原有方法复制变量清单的变量+数据模型
     *
     * @param inputDto             输入实体
     * @param newManifestVersion   新版本的变量清单版本
     * @param archetypeManifestDto 原有的变量清单实体Dto
     * @param duplicatedManifest   复制后的变量清单实体Dto
     * @return 复制之后的变量清单Id
     */
    private Long getArchetypeDuplicationId(VariableManifestDuplicationInputDto inputDto, int newManifestVersion, VariableManifestDto archetypeManifestDto, VarProcessManifest duplicatedManifest) {
        // 1.复制变量
        List<VarProcessManifestVariable> originalVariableLists = archetypeManifestDto.getVariablePublishList();
        if (!CollectionUtils.isEmpty(originalVariableLists)) {
            List<VarProcessManifestVariable> duplicatedVariableLists = new ArrayList<>(originalVariableLists.size());
            for (VarProcessManifestVariable originalVariableList : originalVariableLists) {
                VarProcessManifestVariable item = new VarProcessManifestVariable();
                item.setId(null);
                item.setVarProcessSpaceId(originalVariableList.getVarProcessSpaceId());
                item.setManifestId(duplicatedManifest.getId());
                item.setVariableId(originalVariableList.getVariableId());
                item.setOutputFlag(originalVariableList.getOutputFlag());
                item.setIsIndex(originalVariableList.getIsIndex());
                item.setColRole(originalVariableList.getColRole());
                item.setCreatedUser(SessionContext.getSessionUser().getUsername());
                item.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                duplicatedVariableLists.add(item);
            }
            varProcessManifestVariableService.saveBatch(duplicatedVariableLists);
        }
        // 2.复制数据模型
        List<VarProcessManifestDataModel> originalDataModelLists = archetypeManifestDto.getDataModelMappingList();
        if (!CollectionUtils.isEmpty(originalDataModelLists)) {
            List<VarProcessManifestDataModel> duplicatedDataModelLists = new ArrayList<>(originalDataModelLists.size());
            for (VarProcessManifestDataModel originalDataModelList : originalDataModelLists) {
                VarProcessManifestDataModel item = new VarProcessManifestDataModel();
                item.setId(null);
                item.setVarProcessSpaceId(originalDataModelList.getVarProcessSpaceId());
                item.setManifestId(duplicatedManifest.getId());
                item.setObjectName(originalDataModelList.getObjectName());
                item.setObjectVersion(originalDataModelList.getObjectVersion());
                item.setModelQueryCondition(originalDataModelList.getModelQueryCondition());
                item.setSourceType(originalDataModelList.getSourceType());
                item.setCreatedUser(SessionContext.getSessionUser().getUsername());
                item.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                duplicatedDataModelLists.add(item);
            }
            varProcessManifestDataModelService.saveBatch(duplicatedDataModelLists);
        }
        // 3.记录系统动态和生命周期
        saveDynamic(SysDynamicOperateTypeEnum.COPY.getName(), inputDto.getSpaceId(), duplicatedManifest.getId(), newManifestVersion);
        recordManifestLifecycle(duplicatedManifest.getId(), VarProcessManifestActionTypeEnum.CREATE, null);
        // 4.返回复制后的变量清单ID
        return duplicatedManifest.getId();
    }

    /**
     * 删除变量清单
     *
     * @param manifestId 变量清单的Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeManifestVersion(Long manifestId) {
        // 查询变量清单信息
        VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getId, VarProcessManifest::getVarProcessSpaceId, VarProcessManifest::getVersion,VarProcessManifest::getState)
                .eq(VarProcessManifest::getId, manifestId));
        if (null == manifestEntity) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "待删除的变量清单不存在。");
        }
        deleteCheck(manifestEntity);

        // 逻辑删除变量清单
        varProcessManifestService.update(Wrappers.<VarProcessManifest>lambdaUpdate()
                .eq(VarProcessManifest::getId, manifestId)
                .set(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.DELETED.getCode())
                .set(VarProcessManifest::getUpdatedUser, SessionContext.getSessionUser().getUsername())
                .set(VarProcessManifest::getUpdatedTime, new Date()));
        // 物理删除发布变量和数据模型映射
        varProcessManifestVariableService.remove(Wrappers.<VarProcessManifestVariable>lambdaQuery()
                .eq(VarProcessManifestVariable::getManifestId, manifestId));
        varProcessManifestDataModelService.remove(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId));
        //删除引用关系
        //与外部服务关系
        varProcessManifestOutsideServiceService.remove(
                new QueryWrapper<VarProcessManifestOutside>().lambda()
                        .eq(VarProcessManifestOutside::getManifestId, manifestId)
        );
        //与公共函数
        varProcessManifestFunctionService.remove(
                new QueryWrapper<VarProcessManifestFunction>().lambda()
                        .eq(VarProcessManifestFunction::getManifestId, manifestId)
        );
        //内部数据
        varProcessManifestInternalDataService.remove(
                new QueryWrapper<VarProcessManifestInternal>().lambda()
                        .eq(VarProcessManifestInternal::getManifestId, manifestId)
        );
        // 记录系统动态
        saveDynamic(SysDynamicOperateTypeEnum.DELETE.getName(), manifestEntity.getVarProcessSpaceId(), manifestEntity.getId(), manifestEntity.getVersion());
    }


    /**
     * 获取变量清单的配置信息
     *
     * @param manifestId 变量清单Id
     * @return 变量清单的配置信息
     */
    public VariableManifestConfigOutputDto getManifestConfig(Long manifestId) {
        // 获取变量清单实体类
        VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getId, VarProcessManifest::getVarProcessSpaceId, VarProcessManifest::getVarManifestName, VarProcessManifest::getCategoryId, VarProcessManifest::getVersion, VarProcessManifest::getDescription, VarProcessManifest::getState)
                .eq(VarProcessManifest::getId, manifestId));
        if (null == manifestEntity) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到变量清单。");
        }

        Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(manifestEntity.getVarProcessSpaceId());

        // 0. 查询接口基本信息 & 3. 查询流水号绑定配置
        VariableManifestBasicConfigDto basicConfigDto = VariableManifestBasicConfigDto.builder()
                .spaceId(manifestEntity.getVarProcessSpaceId())
//                .serviceId(manifestEntity.getServiceId())
                .manifestId(manifestEntity.getId())
                .name(manifestEntity.getVarManifestName())
                .categoryId(manifestEntity.getCategoryId())
                .category(categoryNameMap.get(manifestEntity.getCategoryId()))
                .version(manifestEntity.getVersion())
                .description(manifestEntity.getDescription())
                .state(manifestEntity.getState())
                .build();

        // 1. 查询发布变量清单
        // 查询变量清单版本详情 - 发布变量清单信息
        List<VariableManifestPublishingVariableDTO> publishingVariableDtoList = varProcessManifestVariableService.getPublishingVariableInfo(
                manifestEntity.getVarProcessSpaceId(), manifestEntity.getId());
        List<VariableManifestPublishVariableVo> publishVariableVoList = new ArrayList<>(publishingVariableDtoList.size());
        if (!CollectionUtils.isEmpty(publishingVariableDtoList)) {
            // 收集发布变量标识
            List<String> publishingVariableIdentifierList = publishingVariableDtoList.parallelStream()
                    .map(VariableManifestPublishingVariableDTO::getIdentifier)
                    .collect(Collectors.toList());
            // 查询指定空间, 变量标识的所有上架变量
            List<VarProcessVariable> listedVariableList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(
                            VarProcessVariable::getId,
                            VarProcessVariable::getIdentifier,
                            VarProcessVariable::getVersion
                    )
                    .eq(VarProcessVariable::getVarProcessSpaceId, manifestEntity.getVarProcessSpaceId())
                    .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                    .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .in(VarProcessVariable::getIdentifier, publishingVariableIdentifierList)
                    .orderByDesc(VarProcessVariable::getVersion));
            // 收集发布变量其他上架版本 Map, key: variable identifier, value: VarProcessVariable Entity List
            Map<String, List<VarProcessVariable>> listedVariableMap = listedVariableList.parallelStream()
                    .collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));

            for (VariableManifestPublishingVariableDTO publishingVariableDto : publishingVariableDtoList) {
                //将dto转成Vo
                VariableManifestPublishVariableVo publishVariableVo = transformPublishVariableDtoIntoVo(listedVariableMap, publishingVariableDto);

                publishVariableVoList.add(publishVariableVo);
            }
        }

        // 2. 查询数据模型绑定配置
        List<VariableManifestDataModelMappingVo> mappingVoList = getVariableManifestDataModelMappingVos(manifestId, manifestEntity, publishVariableVoList);

        return VariableManifestConfigOutputDto.builder()
                .basicConfig(basicConfigDto)
                .variablePublishList(publishVariableVoList)
                .dataModelBindingList(mappingVoList)
                .build();
    }

    private static VariableManifestPublishVariableVo transformPublishVariableDtoIntoVo(Map<String, List<VarProcessVariable>> listedVariableMap, VariableManifestPublishingVariableDTO publishingVariableDto) {
        // 创建变量清单发布变量 VO
        VariableManifestPublishVariableVo publishVariableVo = new VariableManifestPublishVariableVo();
        BeanUtils.copyProperties(publishingVariableDto, publishVariableVo);
        // 用户选择的变量版本信息
        VariableManifestPublishVariableVo.VersionInfo selectedVersionInfo = VariableManifestPublishVariableVo.VersionInfo.builder()
                .variableId(publishingVariableDto.getSelectedVersionVariableId())
                .version(publishingVariableDto.getSelectedVersion())
                .build();
        publishVariableVo.setSelectedVersionInfo(selectedVersionInfo);
        // 变量所有上架版本信息
        List<VarProcessVariable> designatedIdentifierListedVariableList = listedVariableMap.get(publishingVariableDto.getIdentifier());
        if (!CollectionUtils.isEmpty(designatedIdentifierListedVariableList)) {
            List<VariableManifestPublishVariableVo.VersionInfo> listedVersionInfoList = designatedIdentifierListedVariableList.stream()
                    .map(entity -> VariableManifestPublishVariableVo.VersionInfo.builder()
                            .variableId(entity.getId())
                            .version(entity.getVersion())
                            .build())
                    .collect(Collectors.toList());
            publishVariableVo.setListedVersionInfoList(listedVersionInfoList);
        } else {
            publishVariableVo.setListedVersionInfoList(new ArrayList<>());
        }
        return publishVariableVo;
    }

    private List<VariableManifestDataModelMappingVo> getVariableManifestDataModelMappingVos(Long manifestId, VarProcessManifest manifestEntity, List<VariableManifestPublishVariableVo> publishVariableVoList) {
        List<VariableManifestDataModelMappingVo> mappingVoList;
        List<VarProcessDataModelDto> dataModelMappings;

        dataModelMappings = varProcessManifestDataModelService.getDataModelsAfterSubmit(manifestId);
        if (VarProcessManifestStateEnum.EDIT.equals(manifestEntity.getState())) {
            //编辑中 -> 将数据模型版本更新为最新
            List<String> modelNames = dataModelMappings.stream().map(VarProcessDataModelDto::getObjectName).collect(Collectors.toList());
            Map<String, Integer> maxVersionMap = varProcessDataModelService.findMaxVersionMap(modelNames);
            dataModelMappings.forEach(item -> item.setVersion(maxVersionMap.get(item.getObjectName())));
        }
        mappingVoList = assembleMappingVoV2(dataModelMappings);
        return mappingVoList;
    }

    /**
     * 保存变量清单的编辑
     *
     * @param inputDto 前端发送过来的输入实体
     * @return 变量清单的Id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveManifest(VariableManifestConfigInputDto inputDto) {
        // 校验清单是否处于 "编辑中" 状态
        VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getId, VarProcessManifest::getDeleteFlag, VarProcessManifest::getState)
                .eq(VarProcessManifest::getId, inputDto.getBasicConfig().getManifestId()));
        if (!verifyManifestStatus(manifestEntity, VarProcessManifestStateEnum.EDIT)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "待编辑变量清单不处于“编辑中”状态，无法被编辑。");
        }
        if (DeleteFlagEnum.DELETED.getCode().equals(manifestEntity.getDeleteFlag())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "待编辑变量清单已被删除，无法被编辑。");
        }

        // 0. 更新变量清单基本信息 & 流水号绑定
        String operatorUsername = SessionContext.getSessionUser().getUsername();
        Long editingManifestId = inputDto.getBasicConfig().getManifestId();

        LambdaUpdateWrapper<VarProcessManifest> updateQuery = Wrappers.<VarProcessManifest>lambdaUpdate()
                .eq(VarProcessManifest::getId, editingManifestId)
                .set(VarProcessManifest::getCategoryId, inputDto.getBasicConfig().getCategoryId())
                .set(VarProcessManifest::getVarManifestName, inputDto.getBasicConfig().getName())
                .set(VarProcessManifest::getUpdatedUser, operatorUsername)
                .set(VarProcessManifest::getUpdatedTime, new Date());


        if (!StringUtils.isEmpty(inputDto.getBasicConfig().getDescription())) {
            updateQuery.set(VarProcessManifest::getDescription, inputDto.getBasicConfig().getDescription());
        }

        if (!ObjectUtil.isEmpty(inputDto.getBasicConfig().getVersion())) {
            updateQuery.set(VarProcessManifest::getVersion, inputDto.getBasicConfig().getVersion());

        }
        if (!ObjectUtil.isEmpty(inputDto.getBasicConfig().getState())) {
            updateQuery.set(VarProcessManifest::getState, inputDto.getBasicConfig().getState());
        }

        varProcessManifestService.update(updateQuery);

        // 1. 更新发布变量清单
        saveManifestVariables(inputDto, operatorUsername, editingManifestId);

        //2.更新清单-数据模型引用关系
        saveManifestDataModels(inputDto, operatorUsername, editingManifestId);

        // 3. 记录系统动态
        saveDynamic(SysDynamicOperateTypeEnum.EDIT.getName(),
                inputDto.getBasicConfig().getSpaceId(),
                inputDto.getBasicConfig().getManifestId(),
                inputDto.getBasicConfig().getVersion());

        return editingManifestId;
    }

    private void saveManifestDataModels(VariableManifestConfigInputDto inputDto, String operatorUsername, Long editingManifestId) {
        //删除旧的数据模型
        varProcessManifestDataModelService.remove(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .eq(VarProcessManifestDataModel::getVarProcessSpaceId, inputDto.getBasicConfig().getSpaceId())
                .eq(VarProcessManifestDataModel::getManifestId, editingManifestId));
        if (!CollectionUtils.isEmpty(inputDto.getDataModelBindingList())) {
            //添加并保存新的数据模型
            List<VarProcessManifestDataModel> newMappingList = new ArrayList<>(inputDto.getDataModelBindingList().size());
            for (VariableManifestDataModelMappingVo dataModelMappingVo : inputDto.getDataModelBindingList()) {

                VarProcessManifestDataModel mapping = VarProcessManifestDataModel.builder()
                        .varProcessSpaceId(inputDto.getBasicConfig().getSpaceId())
                        .manifestId(editingManifestId)
                        .objectName(dataModelMappingVo.getName())
                        .objectVersion(dataModelMappingVo.getVersion())
                        .sourceType(dataModelMappingVo.getSourceType().getCode())
                        .createdUser(operatorUsername)
                        .updatedUser(operatorUsername)
                        .modelQueryCondition(CollectionUtils.isEmpty(dataModelMappingVo.getQueryConditionList()) ? StringPool.EMPTY : JSON.toJSONString(dataModelMappingVo.getQueryConditionList()))
                        .build();

                newMappingList.add(mapping);
            }
            varProcessManifestDataModelService.saveBatch(newMappingList);
        }
    }

    private void saveManifestVariables(VariableManifestConfigInputDto inputDto, String operatorUsername, Long editingManifestId) {
        // 删除旧变量发布设置
        varProcessManifestVariableService.remove(Wrappers.<VarProcessManifestVariable>lambdaQuery()
                .eq(VarProcessManifestVariable::getVarProcessSpaceId, inputDto.getBasicConfig().getSpaceId())
                .eq(VarProcessManifestVariable::getManifestId, editingManifestId)
        );
        if (!CollectionUtils.isEmpty(inputDto.getVariablePublishList())) {
            // 添加并保存新变量发布设置
            List<VarProcessManifestVariable> newPublishVariableInfoList = new ArrayList<>(inputDto.getVariablePublishList().size());
            for (VariableManifestPublishVariableVo variableDto : inputDto.getVariablePublishList()) {
                VarProcessManifestVariable newPublishVariableInfo = VarProcessManifestVariable.builder()
                        .varProcessSpaceId(inputDto.getBasicConfig().getSpaceId())
                        .manifestId(editingManifestId)
                        .variableId(variableDto.getSelectedVersionInfo().getVariableId())
                        .outputFlag(variableDto.getOutputFlag())
                        .isIndex(BooleanUtils.isTrue(variableDto.getIsIndex()) || ColRoleEnum.TARGET == variableDto.getColRole() || ColRoleEnum.GROUP == variableDto.getColRole())
                        .colRole(variableDto.getColRole() == null ? ColRoleEnum.GENERAL : variableDto.getColRole())
                        .createdUser(operatorUsername)
                        .updatedUser(operatorUsername)
                        .build();
                newPublishVariableInfoList.add(newPublishVariableInfo);
            }
            varProcessManifestVariableService.saveBatch(newPublishVariableInfoList);
        }
    }

    /**
     * 查询该变量清单被哪些服务/批量回溯任务使用
     *
     * @param varProcessSpaceId 变量空间的Id
     * @param manifestId        变量清单的ID
     * @return 清单使用信息
     */
    public List<VariableUseOutputVo> getUsingList(Long varProcessSpaceId, Long manifestId) {

        // 1.创建返回体
        List<VariableUseOutputVo> result = new ArrayList<>();

        // 2.查询出调用该清单的实时服务
        List<ServiceUsingManifestDto> usingServices = varProcessManifestService.findUsingService(manifestId, varProcessSpaceId);

        if (!CollectionUtils.isEmpty(usingServices)) {
            VariableUseOutputVo outputVo = getServiceUseOutputVo(varProcessSpaceId, manifestId, usingServices);
            result.add(outputVo);
        }

        //被批量回溯任务使用
        List<VarProcessBatchBacktracking> backTrackingTasks = backtrackingService.list(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getName, VarProcessBatchBacktracking::getStatus)
                .eq(VarProcessBatchBacktracking::getManifestId, manifestId)
                .eq(VarProcessBatchBacktracking::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        if (!CollectionUtils.isEmpty(backTrackingTasks)) {
            VariableUseOutputVo outputVo = getBackTrackingUseOutputVo(backTrackingTasks);
            result.add(outputVo);
        }

        return result;
    }

    private VariableUseOutputVo getServiceUseOutputVo(Long varProcessSpaceId, Long manifestId, List<ServiceUsingManifestDto> usingServices) {
        //查询category表，将<id,类型名称>存在map里
        Map<Long, String> categoryMap = varProcessCategoryService.list(Wrappers.<VarProcessCategory>lambdaQuery()
                        .select(VarProcessCategory::getId, VarProcessCategory::getName)
                        .eq(VarProcessCategory::getVarProcessSpaceId, varProcessSpaceId)
                        .eq(VarProcessCategory::getEnabled, 1)
                        .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                .stream()
                .collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName));

        //创建表头
        List<VariableUseOutputVo.TableHeader> tableHeader = new ArrayList<>();
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("服务名称").prop("name").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("服务编码").prop("code").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("服务分类").prop("category").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("角色").prop("manifestRole").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("状态").prop("state").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("已执行笔数").prop("excutedCount").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("版本号").prop("version").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("操作").prop("operate").build());

        // 表的内容
        List<JSONObject> tableData = new ArrayList<>();
        for (ServiceUsingManifestDto service : usingServices) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", service.getName());
            jsonObject.put("code", service.getCode());
            jsonObject.put("category", categoryMap.getOrDefault(service.getCategoryId(), ""));
            jsonObject.put("manifestRole", service.getManifestRole() == 1 ? "主清单" : "异步加工清单");
            jsonObject.put("state", service.getState().getDesc());

            // 获取该变量清单在实时服务中的调用次数
            String sql = "SELECT count(*) FROM var_process_log WHERE interface_type = 1 and service_id = " + service.getServiceId();
            Long currentExecuteCount = internalJdbcTemplate.queryForObject(sql, Long.class);
            jsonObject.put("excutedCount", currentExecuteCount);
            jsonObject.put("version", service.getVersion());
            jsonObject.put("id", service.getServiceId());
            tableData.add(jsonObject);
        }

        VariableUseOutputVo outputVo = new VariableUseOutputVo();
        outputVo.setTitle("被实时服务使用");
        outputVo.setTableHeader(tableHeader);
        outputVo.setTableData(tableData);
        return outputVo;
    }

    private static VariableUseOutputVo getBackTrackingUseOutputVo(List<VarProcessBatchBacktracking> backTrackingTasks) {
        //创建表头
        List<VariableUseOutputVo.TableHeader> tableHeader = new ArrayList<>();
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("任务名称").prop("name").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("任务状态").prop("state").build());
        tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("操作").prop("operate").build());

        List<JSONObject> tableData = new ArrayList<>();
        for (VarProcessBatchBacktracking task : backTrackingTasks) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", task.getName());
            jsonObject.put("state", task.getStatus().getDesc());
            jsonObject.put("id", task.getId());
            tableData.add(jsonObject);
        }

        VariableUseOutputVo outputVo = new VariableUseOutputVo();
        outputVo.setTitle("被批量回溯任务使用");
        outputVo.setTableHeader(tableHeader);
        outputVo.setTableData(tableData);
        return outputVo;
    }

    /**
     * 获取变量清单的属性集合
     *
     * @param manifestId 变量清单的Id
     * @return 变量请但的属性集合
     */
    public List<TabDto> getManifestProperties(Long manifestId) {
        // 1.获取变量清单对应的Dto
        VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(manifestId);
        List<TabDto> manifestPropertiesTabList = new ArrayList<>(MagicNumbers.TWO);
        // 2.构建属性信息选项卡(目前有测试信息,但是是稍后做) DTO
        TabDto propertyTab = new TabDto();
        propertyTab.setName("属性信息");
        propertyTab.setContent(new LinkedList<>());
        List<PanelDto> propertyTabPanelList = propertyTab.getContent();
        propertyTabPanelList.add(buildManifestTestInfoPanel(manifestDto));
        manifestPropertiesTabList.add(propertyTab);
        // 3.构建生命周期选项卡 DTO
        TabDto lifecycleTab = new TabDto();
        lifecycleTab.setName("生命周期");
        lifecycleTab.setContent(new ArrayList<>(MagicNumbers.ONE));
        lifecycleTab.getContent().add(buildManifestLifecyclePanel(manifestDto));
        manifestPropertiesTabList.add(lifecycleTab);
        return manifestPropertiesTabList;
    }

    /**
     * 构建变量清单测试信息 Panel
     *
     * @param manifestDto 变量清单 DTO
     * @return 测试信息 Panel
     */
    private PanelDto<List<Content>> buildManifestTestInfoPanel(VariableManifestDto manifestDto) {
        // 创建测试信息 Panel
        PanelDto<List<Content>> testInfoPanel = new PanelDto<>();
        testInfoPanel.setTitle("测试信息");
        testInfoPanel.setType(LocalDataTypeEnum.desc.getCode());
        testInfoPanel.setDatas(new LinkedList<>());

        // 查找当前未删除的测试数据集 ID (var_process_test_variable.id)
        // 由于同一变量空间下的的清单共享测试数据集, 故需要使用标识 (变量空间 ID) 作为查询条件
        List<VarProcessTest> availableTestDatasetList = varProcessTestVariableService.list(Wrappers.<VarProcessTest>lambdaQuery()
                .select(VarProcessTest::getId)
                .eq(VarProcessTest::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                .eq(VarProcessTest::getIdentifier, manifestDto.getManifestEntity().getIdentifier())
                .eq(VarProcessTest::getTestType, TestVariableTypeEnum.MANIFEST.getCode())
                .eq(VarProcessTest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        List<Long> availableTestDatasetIdList = availableTestDatasetList.stream()
                .map(VarProcessTest::getId)
                .collect(Collectors.toList());
        // 变量清单最近测试结果
        List<VarProcessTestResults> recentTestResultList = null;
        if (!CollectionUtils.isEmpty(availableTestDatasetIdList)) {
            // 存在未删除的测试数据集: 查找最近测试结果
            recentTestResultList = varProcessTestVariableResultsService.list(Wrappers.<VarProcessTestResults>lambdaQuery()
                    .eq(VarProcessTestResults::getTestType, TestVariableTypeEnum.MANIFEST.getCode())
                    .eq(VarProcessTestResults::getVariableId, manifestDto.getManifestEntity().getId())
                    .in(VarProcessTestResults::getTestId, availableTestDatasetIdList)
                    .orderByDesc(VarProcessTestResults::getTestTime)
            );
        }
        String recentTestPassRate;
        if (CollectionUtils.isEmpty(recentTestResultList)) {
            // 不存在最近测试结果: 显示 "未测试"
            recentTestPassRate = "未测试";
        } else {
            VarProcessTestResults recentTestResult = recentTestResultList.get(0);
            String defaultNa = CommonConstant.DEFAULT_TEST_NA;
            // 存在最后测试结果: 查询测试数据集预期结果表头字段
            VarProcessTest recentTestDataSet = varProcessTestVariableService.getById(recentTestResult.getTestId());
            if (null == recentTestDataSet) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "未查询到组件最后测试结果对应的测试数据集，请检查数据完整性。");
            }
            String recentTestDataSetExpectedTableHeader = recentTestDataSet.getTableHeaderField();
            if (StringUtils.isEmpty(recentTestDataSetExpectedTableHeader) || MagicStrings.CURLY_BRACE.equals(recentTestDataSetExpectedTableHeader)) {
                // 测试数据集不存在预期结果: 显示 "N/A"
                recentTestPassRate = defaultNa;
            } else {
                if (StringUtils.isEmpty(recentTestResult.getSuccessRate()) || defaultNa.equals(recentTestResult.getSuccessRate())) {
                    // 测试失败或成功率为 "N/A": 显示 "N/A"
                    recentTestPassRate = defaultNa;
                } else {
                    // 测试数据集设置预期结果: 显示通过率百分数
                    recentTestPassRate = recentTestResult.getSuccessRate() + "%";
                }
            }
        }
        testInfoPanel.getDatas().add(Content.builder().label("最后测试通过率").value(recentTestPassRate).url(null).build());

        return testInfoPanel;
    }

    /**
     * 构建变量清单外部服务引用 Panel
     *
     * @param manifestDto 变量清单 DTO
     * @return 外部服务引用 Panel
     */
    private PanelDto<List<Content>> buildManifestOutsideServiceReferencePanel(VariableManifestDto manifestDto) {
        // 创建外部服务引用 Panel
        PanelDto<List<Content>> outsideServiceReferencePanel = new PanelDto<>();
        outsideServiceReferencePanel.setTitle("外部服务引用");
        outsideServiceReferencePanel.setType(LocalDataTypeEnum.cell.getCode());
        outsideServiceReferencePanel.setDatas(new LinkedList<>());

        // 查询变量清单引用的外部服务
        List<VarProcessManifestOutsideServiceDto> manifestReferencedOutsideServiceInfoDtoList = varProcessManifestOutsideServiceService
                .getManifestOutsideService(manifestDto.getManifestEntity().getVarProcessSpaceId(), manifestDto.getManifestEntity().getId());
        List<Content> outsideServiceReferencePanelContentList = outsideServiceReferencePanel.getDatas();
        for (VarProcessManifestOutsideServiceDto infoDto : manifestReferencedOutsideServiceInfoDtoList) {
            // 组装外部服务引入信息前端页面导航 URL
            String url = String.format("/process/outServiceImport/detail?outServiceId=%s&spaceId=%s", infoDto.getOutsideServiceId(), manifestDto
                    .getManifestEntity().getVarProcessSpaceId());
            outsideServiceReferencePanelContentList.add(Content.builder().value(infoDto.getOutsideServiceName()).url(url).build());
        }

        return outsideServiceReferencePanel;
    }

    /**
     * 构建变量清单文档信息 Panel
     *
     * @param manifestDto 变量清单 DTO
     * @return 文档信息 Panel
     */
    private PanelDto<List<Content>> buildManifestDocumentInfoPanel(VariableManifestDto manifestDto) {
        // 创建文档信息 Panel

        PanelDto<List<Content>> wordPanel = new PanelDto();
        List<VarProcessDocument> documentList = varProcessDocumentService.list(new QueryWrapper<VarProcessDocument>().lambda()
                .eq(VarProcessDocument::getVarProcessSpaceId, manifestDto.getManifestEntity().getVarProcessSpaceId())
                .eq(VarProcessDocument::getResourceId, manifestDto.getManifestEntity().getId())
                .eq(VarProcessDocument::getFileType, "manifest")
                .eq(VarProcessDocument::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        List<Content> wordList = new ArrayList<>();
        documentList.forEach(document -> wordList.add(Content.builder().value(document.getName()).url(String.valueOf(document.getId())).build()));

        wordPanel.setTitle("文档信息");
        wordPanel.setType(LocalDataTypeEnum.doc.getCode());
        wordPanel.setDatas(wordList);
        return wordPanel;
    }

    /**
     * 构建变量清单生命周期 Panel
     *
     * @param manifestDto 变量清单 DTO
     * @return 生命周期 Panel
     */
    private PanelDto<TableContent> buildManifestLifecyclePanel(VariableManifestDto manifestDto) {
        // 1.创建生命周期 Panel
        PanelDto<TableContent> lifecyclePanel = new PanelDto<>();
        lifecyclePanel.setTitle("生命周期列表");
        lifecyclePanel.setType("lifecycle");
        lifecyclePanel.setDatas(new TableContent());
        // 2.查询变量清单生命周期
        TableContent lifecycleContent = lifecyclePanel.getDatas();
        // 3.生命周期表格头
        List<TableContent.TableHeadInfo> tableHeadInfoList = new ArrayList<>(MagicNumbers.FIVE);
        tableHeadInfoList.add(TableContent.TableHeadInfo.builder().lable("状态").key("status").build());
        tableHeadInfoList.add(TableContent.TableHeadInfo.builder().lable("操作类型").key("operation").build());
        tableHeadInfoList.add(TableContent.TableHeadInfo.builder().lable("操作人").key("operaUserName").build());
        tableHeadInfoList.add(TableContent.TableHeadInfo.builder().lable("操作时间").key("operaTime").build());
        tableHeadInfoList.add(TableContent.TableHeadInfo.builder().lable("备注").key("description").build());
        lifecycleContent.setTableHead(tableHeadInfoList);
        // 4.生命周期表格内容
        List<JSONObject> tableDataList = new LinkedList<>();
        // 5.查询生命周期, 按照时间降序排列
        List<VarProcessManifestCycle> lifecycleList = varProcessManifestLifecycleService.list(Wrappers.<VarProcessManifestCycle>lambdaQuery()
                .eq(VarProcessManifestCycle::getManifestId, manifestDto.getManifestEntity().getId())
                .orderByDesc(VarProcessManifestCycle::getCreatedTime));
        for (VarProcessManifestCycle lifecycle : lifecycleList) {
            JSONObject tableData = new JSONObject();
            String fullName = userService.getFullNameByUserName(lifecycle.getCreatedUser());
            if (!StringUtils.isEmpty(fullName)) {
                tableData.put("operaUserName", fullName);
            } else {
                tableData.put("operaUserName", null);
            }
            tableData.put("description", lifecycle.getMemo());
            tableData.put("operaTime", DateUtil.parseDateToStr(lifecycle.getCreatedTime(), MagicStrings.DATE_TIME_FORMAT));
            VarProcessManifestActionTypeEnum actionTypeEnum = VarProcessManifestActionTypeEnum.getActionTypeEnum(lifecycle.getOperation());
            tableData.put("operation", actionTypeEnum != null ? actionTypeEnum.getActionDescription() : null);
            VarProcessManifestStateEnum stateEnum = VarProcessManifestStateEnum.getStateEnum(lifecycle.getStatus());
            tableData.put("status", stateEnum != null ? stateEnum.getDesc() : null);
            tableDataList.add(tableData);
        }
        lifecycleContent.setTableData(tableDataList);
        return lifecyclePanel;
    }

    /**
     * 移除指定树形结构实体数组类型项目
     *
     * @param domainModelTree 决策领域树形结构实体
     */
    private void removeDomainModelTreeArrayTypedItem(DomainModelTree domainModelTree) {
        if (CollectionUtils.isEmpty(domainModelTree.getChildren())) {
            // 边界条件: 树形结构实体没有子类
            return;
        }
        for (Iterator<DomainModelTree> childrenIterator = domainModelTree.getChildren().listIterator(); childrenIterator.hasNext(); ) {
            DomainModelTree child = childrenIterator.next();
            if ("1".equals(child.getIsArr())) {
                childrenIterator.remove();
            } else if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(child.getType())) {
                // 对象类型: 递归调用
                removeDomainModelTreeArrayTypedItem(child);
            }
        }
    }

    /**
     * 移除指定树形结构实体扩展数据
     *
     * @param domainModelTree 决策领域树形结构实体
     */
    private void removeDomainModelTreeExtendedData(DomainModelTree domainModelTree) {
        if (CollectionUtils.isEmpty(domainModelTree.getChildren())) {
            // 边界条件: 树形结构实体没有子类
            return;
        }
        for (Iterator<DomainModelTree> childrenIterator = domainModelTree.getChildren().listIterator(); childrenIterator.hasNext(); ) {
            DomainModelTree child = childrenIterator.next();
            if ("1".equals(child.getIsExtend())) {
                childrenIterator.remove();
            } else if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(child.getType())) {
                // 对象类型: 递归调用
                removeDomainModelTreeExtendedData(child);
            }
        }
    }

    static void checkDataModelsExistence(StringBuilder errMessage, List<String> mappingModelNames, List<VarProcessDataModel> mappingModels, List<String> inputModelList, List<String> uncontains) {
        //校验变量对应的数据模型是否在数据模型列表中存在
        for (VarProcessDataModel model : mappingModels) {
            if (!inputModelList.contains(model.getObjectName())) {
                uncontains.add(model.getObjectName());
            }
        }
        if (!uncontains.isEmpty()) {
            uncontains.forEach(unContainedModel -> errMessage.append("[").append(unContainedModel).append("],"));
            errMessage.deleteCharAt(errMessage.length() - 1);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "变量依赖的数据模型对象" + errMessage + "在数据模型列表中不存在，请重新获取数据模型");
        }
    }

    /**
     * 移除数据模型 JSON Schema 扩展数据标签字段
     * <p>移除 JSON Object "extend": 0 / 1 字段</p>
     *
     * @param o 数据模型 JSON Schema
     */
    private static void removeJsonSchemaExtendField(JSONObject o) {
        if (null == o) {
            // empty inputted JSON object: return immediately, in case of NPE
            return;
        }
        for (Iterator<Map.Entry<String, Object>> iterator = o.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                // JSON object: recursion
                JSONObject valueJsonObject = (JSONObject) value;
                removeJsonSchemaExtendField(valueJsonObject);
            } else if (value instanceof List) {
                // JSON array
                JSONArray jsonArray = (JSONArray) value;
                if (CollectionUtils.isEmpty(jsonArray)) {
                    // designated JSON array is empty: skip the current iteration
                    continue;
                }
                boolean isArrayObjectTypedFlag = jsonArray.get(0) instanceof Map;
                if (isArrayObjectTypedFlag) {
                    // JSON array with object-typed element: traverse every element then recursively call the same method
                    for (Object element : jsonArray) {
                        JSONObject elementJsonObject = (JSONObject) element;
                        removeJsonSchemaExtendField(elementJsonObject);
                    }
                }
                // it is impossible that the type of "extend" field is "array"
            } else if (EXTEND_FIELD.getMessage().equals(key)) {
                // numeric value with key "extend": remove
                iterator.remove();
            }
        }
    }

    /**
     * 获取实时服务-变量清单的名称集合(给单变量分析和变量对比分析报表调用)
     *
     * @param variableIds 传入的变量Id的集合(单变量分析时传一个, 变量对比分析时传多个)
     * @return 实时服务-变量清单的名称集合(给单变量分析和变量对比分析报表调用)
     */
    public List<ServiceManifestNameVo> getServiceManifestName(List<Long> variableIds) {
        if (CollectionUtils.isEmpty(variableIds)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "必须先选择监控对象, 才能选择变量清单!");
        }
        // 1.调用SQL逻辑，获取结果
        List<ServiceManifestNameVo> result = varProcessServiceManifestService.getServiceManifestName(variableIds);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }
        List<Long> manifestIds = result.stream().map(ServiceManifestNameVo::getManifestId).collect(Collectors.toList());
        List<Long> serviceIds = result.stream().map(ServiceManifestNameVo::getServiceId).collect(Collectors.toList());
        Map<Long, String> manifestIdNameMap = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                        .in(VarProcessManifest::getId, manifestIds)
                        .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                .stream().collect(Collectors.toMap(VarProcessManifest::getId, VarProcessManifest::getVarManifestName));
        Map<Long, ServiceInfoDto> serviceMap = varProcessServiceVersionService.findserviceListByVersionIds(serviceIds)
                .stream().collect(Collectors.toMap(ServiceInfoDto::getId, service -> service));
        // 2.遍历，填充name
        for (ServiceManifestNameVo item : result) {
            ServiceInfoDto service = serviceMap.get(item.getServiceId());
            StringBuilder name = new StringBuilder();
            name.append(service == null ? "" : service.getName());
            name.append(MagicStrings.LEFT_BRACKET);
            name.append(service == null ? "" : service.getVersion());
            name.append(MagicStrings.RIGHT_BRACKET);
            name.append(MagicStrings.HYPHEN);
            name.append(manifestIdNameMap.getOrDefault(item.getManifestId(), ""));
            item.setName(name.toString());
        }
        return result;
    }
}
