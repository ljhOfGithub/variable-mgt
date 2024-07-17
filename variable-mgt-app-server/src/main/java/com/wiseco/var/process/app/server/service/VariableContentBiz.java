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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.auth.common.UserSmallDTO;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.controller.vo.SceneListSimpleOutputVO;
import com.wiseco.var.process.app.server.controller.vo.StreamProcessContentInputVO;
import com.wiseco.var.process.app.server.controller.vo.StreamProcessContentOutputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCompareInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariablePropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompareDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompareOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableListVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableUseOutputVo;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessCalFunctionEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessFilterConditionCmpEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessTemplateEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessScene;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSceneEvent;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestResults;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableSaveSub;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableScene;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import com.wiseco.var.process.app.server.service.dto.VariableQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import com.wiseco.var.process.app.server.service.dto.input.SingleVariableQueryInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangxianli
 */
@Slf4j
@Service
public class VariableContentBiz {

    private static final String TABLE_HEADER_FIELD = "{}";
    private static final String NULL = "null";
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessCategoryService varProcessCategoryService;
    @Autowired
    private VarProcessManifestService varProcessManifestService;
    @Autowired
    private VarProcessVariableSaveSubService varProcessVariableSaveSubService;
    @Autowired
    private VarProcessVariableLifecycleService varProcessVariableLifecycleService;
    @Autowired
    private VarProcessTestService varProcessTestVariableService;
    @Autowired
    private VarProcessTestResultsService varProcessTestVariableResultsService;
    @Autowired
    private VarProcessVariableVarService varProcessVariableVarService;
    @Autowired
    private VarProcessVariableFunctionService varProcessVariableFunctionService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VarProcessVariableTagService varProcessVariableTagService;
    @Autowired
    private VarProcessConfigTagService varProcessConfigTagService;
    @Autowired
    private VarProcessVariableReferenceService varProcessVariableReferenceService;
    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private AuthService authService;
    @Autowired
    private VarProcessVariableSceneService varProcessVariableSceneService;
    @Autowired
    private VarProcessSceneService varProcessSceneService;
    @Autowired
    private VarProcessSceneEventService varProcessSceneEventService;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private DmAdapter dmAdapter;

    /**
     * getVariableList
     *
     * @param inputDto inputDto
     * @return VariableListOutputDto
     */
    public IPage<VariableListOutputDto> getVariableList(VariableQueryInputDto inputDto) {
        dmAdapter.modifyGroupOptFlagOfConfigJdbc();
        Page<VariableListOutputDto> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        Set<Long> useVarList = new HashSet<>();
        //查询变量使用
        List<VarProcessManifestVariable> manifestVariableList = varProcessManifestVariableService.getManifestVariableList(inputDto.getSpaceId());
        if (!CollectionUtils.isEmpty(manifestVariableList)) {
            List<Long> collect = manifestVariableList.stream().map(VarProcessManifestVariable::getVariableId).collect(Collectors.toList());
            useVarList.addAll(collect);
        }
        //查询变量之间的引用
        List<VarProcessVariableReference> variableReferenceList = varProcessVariableReferenceService.getVariableReferenceList(inputDto.getSpaceId());
        if (!CollectionUtils.isEmpty(variableReferenceList)) {
            Set<Long> collect = variableReferenceList.stream().map(VarProcessVariableReference::getVariableId).collect(Collectors.toSet());
            useVarList.addAll(collect);
        }

        // 若查询条件为 "已使用", 且被使用的变量 ID Set 为空: 直接返回
        if (Boolean.TRUE.equals(inputDto.getUsed()) && CollectionUtils.isEmpty(useVarList)) {
            return page;
        }
        //获取查询条件
        VariableQueryDto variableQueryDto = getVariableQueryDto(inputDto);
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        variableQueryDto.setDeptCodes(roleDataAuthority.getDeptCodes());
        variableQueryDto.setUserNames(roleDataAuthority.getUserNames());
        IPage<VariableDetailDto> pageList = varProcessVariableService.findVariableMaxVersionList(page, variableQueryDto);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return page;
        }

        //按编号查询当前结果批次下其他版本的变量
        List<VarProcessVariable> varList = getChildVar(variableQueryDto, pageList);
        //key：identifier value：对应的变量
        Map<String, List<VarProcessVariable>> varMap = varList.stream().collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));
        List<Long> subIdList = varList.stream().map(VarProcessVariable::getId).collect(Collectors.toList());
        //查询分类
        Map<Long, String> catMap = getCategoryMap(inputDto);
        //获取标签
        Map<Long, List<VarProcessVariableTag>> tagMap = getTaMap(inputDto, subIdList, pageList);
        //查询已测试变量ID列表
        Set<Long> testedVarIdList = getTestedVarIdList(inputDto);
        // 最近一次拒绝审批对象Map集合
        Map<Long, VarProcessVariableLifecycle> latestLifecycleMap = getLatestLifecycleMap(subIdList, pageList);

        //最终返回的数据
        List<VariableListOutputDto> list = getResultList(useVarList, pageList, varMap, catMap, tagMap, testedVarIdList, latestLifecycleMap);
        page.setTotal(pageList.getTotal());
        page.setPages(pageList.getPages());
        page.setRecords(list);
        return page;
    }

    /**
     * getResultList
     *
     * @param useVarList         useVarList
     * @param pageList           pageList
     * @param varMap             varMap
     * @param catMap             catMap
     * @param tagMap             tagMap
     * @param testedVarIdList    testedVarIdList
     * @param latestLifecycleMap latestLifecycleMap
     * @return VariableListOutputDto List
     */
    private List<VariableListOutputDto> getResultList(Set<Long> useVarList, IPage<VariableDetailDto> pageList, Map<String, List<VarProcessVariable>> varMap, Map<Long, String> catMap,
                                                      Map<Long, List<VarProcessVariableTag>> tagMap, Set<Long> testedVarIdList, Map<Long, VarProcessVariableLifecycle> latestLifecycleMap) {
        List<VariableListOutputDto> list = new ArrayList<>();
        Set<String> deptCodeSet = new HashSet<>();
        Set<String> userNameSet = new HashSet<>();
        getDeptCodeSetAndUserNameList(pageList, varMap, deptCodeSet, userNameSet);
        //部门code:部门名称
        Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(new ArrayList<>(deptCodeSet));
        //用户英文名：用户中文名
        Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(new ArrayList<>(userNameSet));

        for (VariableDetailDto varProcessVariable : pageList.getRecords()) {
            VariableListOutputDto outputDto = new VariableListOutputDto();
            BeanUtils.copyProperties(varProcessVariable, outputDto);
            // 是否测试
            outputDto.setTested(testedVarIdList.contains(varProcessVariable.getId()));
            //是否使用
            outputDto.setUsed(useVarList.contains(varProcessVariable.getId()));
            // 最近一次审批拒绝详情
            outputDto.setLatestLifecycle(latestLifecycleMap.get(varProcessVariable.getId()));
            //类型
            outputDto.setCategoryName(catMap.get(varProcessVariable.getCategoryId()));
            //创建部门
            if (!StringUtils.isEmpty(varProcessVariable.getDeptCode())) {
                outputDto.setCreateDept(deptMap.getOrDefault(varProcessVariable.getDeptCode(), MagicStrings.EMPTY_STRING));
            }
            outputDto.setCreatedUser(userFullNameMap.getOrDefault(outputDto.getCreatedUser(), MagicStrings.EMPTY_STRING));
            outputDto.setUpdatedUser(userFullNameMap.getOrDefault(outputDto.getUpdatedUser(), MagicStrings.EMPTY_STRING));
            //标签
            List<VarProcessVariableTag> variableTags = tagMap.get(varProcessVariable.getId());
            List<VariableTagDto> tags = new ArrayList<>();
            if (!CollectionUtils.isEmpty(variableTags)) {
                for (VarProcessVariableTag tag : variableTags) {
                    tags.add(VariableTagDto.builder().tagName(tag.getTagName()).build());
                }
            }
            outputDto.setTags(tags);
            outputDto.setProcessingMethod(varProcessVariable.getProcessingMethod().getDesc());
            //子项
            if (varMap.containsKey(varProcessVariable.getIdentifier())) {
                List<VarProcessVariable> varProcessVariables = varMap.get(varProcessVariable.getIdentifier());
                List<VariableListOutputDto> subList = new ArrayList<>();
                for (VarProcessVariable sub : varProcessVariables) {
                    VariableListOutputDto subDto = new VariableListOutputDto();
                    BeanUtils.copyProperties(sub, subDto);
                    subDto.setCreatedTime(DateUtil.parseDateToStr(sub.getCreatedTime(), DateUtil.FORMAT_LONG));
                    subDto.setUpdatedTime(DateUtil.parseDateToStr(sub.getUpdatedTime(), DateUtil.FORMAT_LONG));
                    subDto.setTested(testedVarIdList.contains(sub.getId()));
                    subDto.setUsed(useVarList.contains(sub.getId()));
                    subDto.setLatestLifecycle(latestLifecycleMap.get(sub.getId()));
                    subDto.setCategoryName(catMap.get(sub.getCategoryId()));
                    subDto.setProcessingMethod(sub.getProcessingMethod().getDesc());
                    if (!StringUtils.isEmpty(sub.getDeptCode())) {
                        subDto.setCreateDept(deptMap.get(sub.getDeptCode()));
                    }
                    subDto.setCreatedUser(userFullNameMap.getOrDefault(subDto.getCreatedUser(), MagicStrings.EMPTY_STRING));
                    subDto.setUpdatedUser(userFullNameMap.getOrDefault(subDto.getUpdatedUser(), MagicStrings.EMPTY_STRING));
                    List<VarProcessVariableTag> subVariableTags = tagMap.get(sub.getId());
                    List<VariableTagDto> subTags = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(subVariableTags)) {
                        for (VarProcessVariableTag tag : subVariableTags) {
                            subTags.add(VariableTagDto.builder().tagName(tag.getTagName()).build());
                        }
                    }
                    subDto.setTags(subTags);
                    subList.add(subDto);
                }
                outputDto.setChildren(subList);
            }
            list.add(outputDto);
        }
        return list;
    }

    private static void getDeptCodeSetAndUserNameList(IPage<VariableDetailDto> pageList, Map<String, List<VarProcessVariable>> varMap, Set<String> deptIdSet, Set<String> userNameSet) {
        pageList.getRecords().forEach(item -> {
            if (item.getDeptCode() != null) {
                deptIdSet.add(item.getDeptCode());
            }
            userNameSet.add(item.getCreatedUser());
            userNameSet.add(item.getUpdatedUser());

            if (varMap.containsKey(item.getIdentifier())) {
                List<VarProcessVariable> varProcessVariables = varMap.get(item.getIdentifier());
                varProcessVariables.forEach(sub -> {
                    if (sub.getDeptCode() != null) {
                        deptIdSet.add(sub.getDeptCode());
                    }
                    userNameSet.add(sub.getCreatedUser());
                    userNameSet.add(sub.getUpdatedUser());
                });
            }
        });
    }

    /**
     * getChildVar
     *
     * @param variableQueryDto variableQueryDto
     * @param pageList         pageList
     * @return VarProcessVariable List
     */
    private List<VarProcessVariable> getChildVar(VariableQueryDto variableQueryDto, IPage<VariableDetailDto> pageList) {
        List<String> identifierList = pageList.getRecords().stream().map(VariableDetailDto::getIdentifier).collect(Collectors.toList());
        List<Long> idList = pageList.getRecords().stream().map(VariableDetailDto::getId).collect(Collectors.toList());
        variableQueryDto.setIdList(idList);
        variableQueryDto.setIdentifierList(identifierList);
        return varProcessVariableService.getList(variableQueryDto);
    }

    /**
     * getCategoryMap
     *
     * @param inputDto inputDto
     * @return catMap
     */
    private Map<Long, String> getCategoryMap(VariableQueryInputDto inputDto) {
        List<VarProcessCategory> categoryList = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda().eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.VARIABLE));
        Map<Long, String> catMap = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(categoryList)) {
            catMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName, (v1, v2) -> v2));
        }
        return catMap;
    }

    /**
     * getTaMap
     *
     * @param inputDto  inputDto
     * @param subIdList subIdList
     * @param pageList  pageList
     * @return tagMap
     */
    private Map<Long, List<VarProcessVariableTag>> getTaMap(VariableQueryInputDto inputDto, List<Long> subIdList, IPage<VariableDetailDto> pageList) {
        List<Long> variableIdList = pageList.getRecords().stream().map(VariableDetailDto::getId).collect(Collectors.toList());
        variableIdList.addAll(subIdList);
        if (CollectionUtils.isEmpty(variableIdList)) {
            return new HashMap<>(MagicNumbers.ZERO);
        }
        List<VarProcessVariableTag> tagList = varProcessVariableTagService.list(
                new QueryWrapper<VarProcessVariableTag>().lambda()
                        .select(VarProcessVariableTag::getVariableId, VarProcessVariableTag::getTagName)
                        .eq(VarProcessVariableTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .in(VarProcessVariableTag::getVariableId, variableIdList)
        );
        Map<Long, List<VarProcessVariableTag>> tagMap = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(tagList)) {
            tagMap = tagList.stream().collect(Collectors.groupingBy(VarProcessVariableTag::getVariableId));
        }
        return tagMap;
    }

    /**
     * 查询已测试变量ID列表
     *
     * @param inputDto inputDto
     * @return testedVarIdList
     */
    private Set<Long> getTestedVarIdList(VariableQueryInputDto inputDto) {
        List<Long> testDatasetIdListInSpace = varProcessTestVariableService.list(Wrappers.<VarProcessTest>lambdaQuery()
                        .select(VarProcessTest::getId)
                        .eq(VarProcessTest::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessTest::getTestType, TestVariableTypeEnum.VAR.getCode())
                        .eq(VarProcessTest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                .stream()
                .map(VarProcessTest::getId)
                .collect(Collectors.toList());
        Set<Long> testedVarIdList = new HashSet<>();
        if (!CollectionUtils.isEmpty(testDatasetIdListInSpace)) {
            testedVarIdList = varProcessTestVariableResultsService.list(Wrappers.<VarProcessTestResults>lambdaQuery()
                            .eq(VarProcessTestResults::getTestType, TestVariableTypeEnum.VAR.getCode())
                            .in(VarProcessTestResults::getTestId, testDatasetIdListInSpace))
                    .stream()
                    .map(VarProcessTestResults::getVariableId)
                    .collect(Collectors.toSet());
        }
        return testedVarIdList;
    }


    /**
     * 最近一次拒绝审批对象Map集合
     *
     * @param subIdList subIdList
     * @param pageList  pageList
     * @return VarProcessVariableLifecycle  Map
     */
    private Map<Long, VarProcessVariableLifecycle> getLatestLifecycleMap(List<Long> subIdList, IPage<VariableDetailDto> pageList) {
        List<Long> variableIdList = pageList.getRecords().stream().map(VariableDetailDto::getId).collect(Collectors.toList());
        variableIdList.addAll(subIdList);
        if (CollectionUtils.isEmpty(variableIdList)) {
            return new HashMap<>(MagicNumbers.ZERO);
        }
        List<VarProcessVariableLifecycle> lifecycleList = varProcessVariableLifecycleService.list(new QueryWrapper<VarProcessVariableLifecycle>().lambda()
                .eq(VarProcessVariableLifecycle::getStatus, VariableStatusEnum.REFUSE)
                .in(VarProcessVariableLifecycle::getVariableId, variableIdList));

        Set<String> userNameSet = new HashSet<>();
        lifecycleList.forEach(item -> {
            if (!StringUtils.isEmpty(item.getCreatedUser())) {
                userNameSet.add(item.getCreatedUser());
            }
            if (!StringUtils.isEmpty(item.getUpdatedUser())) {
                userNameSet.add(item.getUpdatedUser());
            }
        });
        //用户英文名：用户中文名
        Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(new ArrayList<>(userNameSet));

        return lifecycleList.stream()
                .peek(item -> {
                    item.setCreatedUser(userFullNameMap.getOrDefault(item.getCreatedUser(), MagicStrings.EMPTY_STRING));
                    item.setUpdatedUser(userFullNameMap.getOrDefault(item.getUpdatedUser(), MagicStrings.EMPTY_STRING));
                }).collect(Collectors.toMap(
                        VarProcessVariableLifecycle::getVariableId,
                        lifecycle -> lifecycle,
                        (existing, replacement) -> existing.getId() > replacement.getId() ? existing : replacement
                ));
    }

    /**
     * 构造查询条件
     *
     * @param inputDto inputDto
     * @return VariableQueryDto
     */
    private VariableQueryDto getVariableQueryDto(VariableQueryInputDto inputDto) {
        VariableQueryDto variableQueryDto = new VariableQueryDto();
        BeanUtils.copyProperties(inputDto, variableQueryDto);
        variableQueryDto.setIsUse(inputDto.getUsed());
        String order = inputDto.getOrder();
        if (StringUtils.isEmpty(inputDto.getOrder())) {
            variableQueryDto.setSortKey("updated_time");
            variableQueryDto.setSortType("DESC");
        } else {
            String sortType = order.substring(order.indexOf("_") + 1);
            String sortKey = order.substring(0, order.indexOf("_"));
            sortKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortKey);
            variableQueryDto.setSortKey(sortKey);
            variableQueryDto.setSortType(sortType);
        }

        //查询分类
        List<VarProcessCategory> categoryList = varProcessCategoryService.list(
                new QueryWrapper<VarProcessCategory>().lambda()
                        .eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.VARIABLE)
        );
        Map<Long, List<VarProcessCategory>> categoryMap = categoryList.stream().collect(Collectors.groupingBy(VarProcessCategory::getParentId));
        Set<Long> newCategorySet = new HashSet<>();
        if (!CollectionUtils.isEmpty(inputDto.getCategoryIdList())) {
            Queue<Long> queue = new LinkedList<>(inputDto.getCategoryIdList());
            while (!queue.isEmpty()) {
                Long categoryId = queue.poll();
                newCategorySet.add(categoryId);
                if (categoryMap.containsKey(categoryId)) {
                    for (VarProcessCategory category : categoryMap.get(categoryId)) {
                        queue.add(category.getId());
                    }
                }
            }
            variableQueryDto.setCategoryIdList(new ArrayList<>(newCategorySet));
        }

        //如果不是标签组,根据标签名称查询；如果是标签组，按标签组Id查询
        if (inputDto.getTagId() != null) {
            VarProcessConfigTag tag = varProcessConfigTagService.getById(inputDto.getTagId());
            if (tag != null) {
                variableQueryDto.setTagName(tag.getName());
            }
        } else if (inputDto.getGroupId() != null) {
            variableQueryDto.setTagGroupId(inputDto.getGroupId());
        }

        // 数据类型
        if (!CollectionUtils.isEmpty(inputDto.getDataTypeList())) {
            variableQueryDto.setDataTypeList(inputDto.getDataTypeList());
        }
        //部门id
        if (!StringUtils.isEmpty(inputDto.getDeptId())) {
            variableQueryDto.setDeptCode(inputDto.getDeptId());
        }
        //加工方式
        if (inputDto.getProcessingMethod() != null) {
            variableQueryDto.setProcessingMethod(inputDto.getProcessingMethod());
        }
        return variableQueryDto;
    }

    /**
     * variableDetail
     *
     * @param inputDto inputDto
     * @return VariableDetailOutputDto
     */
    public VariableDetailOutputDto variableDetail(VariableDetailQueryInputDto inputDto) {
        VarProcessVariable variableEntity = varProcessVariableService.getById(inputDto.getVariableId());
        if (variableEntity == null || variableEntity.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, "变量信息不存在或已删除");
        }
        if (inputDto.getSpaceId() == null) {
            inputDto.setSpaceId(variableEntity.getVarProcessSpaceId());
        }

        //查询变量分类详情
        VarProcessCategory varProcessCategory = varProcessCategoryService.getById(variableEntity.getCategoryId());
        VariableDetailOutputDto outputDto = new VariableDetailOutputDto();
        outputDto.setSpaceId(variableEntity.getVarProcessSpaceId());
        outputDto.setId(variableEntity.getId());
        outputDto.setIdentifier(variableEntity.getIdentifier());
        outputDto.setName(variableEntity.getName());
        outputDto.setLabel(variableEntity.getLabel());
        outputDto.setParentId(variableEntity.getParentId());
        outputDto.setCategoryId(variableEntity.getCategoryId());
        outputDto.setCategoryName(ObjectUtils.isEmpty(varProcessCategory) ? "" : varProcessCategory.getName());
        outputDto.setDescription(variableEntity.getDescription());
        outputDto.setCreatedTime(DateUtil.parseDateToStr(variableEntity.getCreatedTime(), DateUtil.FORMAT_LONG));
        //查询用户全名
        Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(Arrays.asList(variableEntity.getCreatedUser(), variableEntity.getUpdatedUser()));
        outputDto.setCreatedUser(userFullNameMap.getOrDefault(variableEntity.getCreatedUser(), MagicStrings.EMPTY_STRING));
        outputDto.setUpdatedUser(userFullNameMap.getOrDefault(variableEntity.getUpdatedUser(), MagicStrings.EMPTY_STRING));
        outputDto.setUpdatedTime(DateUtil.parseDateToStr(variableEntity.getUpdatedTime(), DateUtil.FORMAT_LONG));
        outputDto.setDataType(variableEntity.getDataType());
        outputDto.setStatus(variableEntity.getStatus());
        outputDto.setVersion(variableEntity.getVersion());
        outputDto.setProcessingMethod(variableEntity.getProcessingMethod());
        if (!StringUtils.isEmpty(variableEntity.getContent()) && !NULL.equals(variableEntity.getContent())) {
            outputDto.setContent(JSON.parseObject(variableEntity.getContent()));
        }
        outputDto.setProcessType(variableEntity.getProcessType());
        outputDto.setFunctionId(variableEntity.getFunctionId());
        if (ObjectUtils.isNotEmpty(variableEntity.getFunctionId())) {
            VarProcessFunction function = varProcessFunctionService.getById(variableEntity.getFunctionId());
            outputDto.setFunctionName(function == null ? "" : function.getName());
        }

        if (variableEntity.getStatus().equals(VariableStatusEnum.EDIT) && !StringUtils.isEmpty(inputDto.getContentId())) {
            //查看历史记录
            getVariableDetailBySub(inputDto, outputDto);
        } else {
            List<VarProcessVariableTag> tagList = varProcessVariableTagService.list(
                    new QueryWrapper<VarProcessVariableTag>().lambda()
                            .eq(VarProcessVariableTag::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariableTag::getVariableId, inputDto.getVariableId())
            );
            if (!CollectionUtils.isEmpty(tagList)) {
                List<VariableTagDto> tags = new ArrayList<>();
                for (VarProcessVariableTag tag : tagList) {
                    tags.add(VariableTagDto.builder().groupId(tag.getTagGroupId()).tagName(tag.getTagName()).build());
                }
                outputDto.setTags(tags);
            }
        }

        if (ProcessingMethodEnum.STREAM == variableEntity.getProcessingMethod()) {
            List<VarProcessVariableScene> variableScenes = varProcessVariableSceneService.list(Wrappers.<VarProcessVariableScene>lambdaQuery().eq(VarProcessVariableScene::getVariableId, variableEntity.getId()));

            if (!CollectionUtils.isEmpty(variableScenes)) {
                VarProcessVariableScene variableScene = variableScenes.get(0);
                VarProcessScene scene = varProcessSceneService.getById(variableScene.getSceneId());
                StreamProcessContentOutputVO streamContentOutput = new StreamProcessContentOutputVO();
                BeanUtils.copyProperties(variableScene, streamContentOutput);
                streamContentOutput.setSceneName(scene == null ? "" : scene.getName());
                streamContentOutput.setDataModelName(scene == null ? "" : scene.getDataModelName());
                VarProcessSceneEvent event = varProcessSceneEventService.getById(variableScene.getEventId());
                streamContentOutput.setEventName(event == null ? "" : event.getEventName());
                streamContentOutput.setCalculatePeriod(JSON.parseObject(variableScene.getCalculatePeriod(), StreamProcessContentInputVO.CalculatePeriod.class));
                streamContentOutput.setFilterConditionInfo(JSON.parseObject(variableScene.getFilterConditionInfo(), StreamProcessContentInputVO.FilterCondition.class));
                outputDto.setStreamProcessContent(streamContentOutput);
            }
        }
        return outputDto;
    }

    /**
     * 查看历史记录
     *
     * @param inputDto  inputDto
     * @param outputDto outputDto
     */
    private void getVariableDetailBySub(VariableDetailQueryInputDto inputDto, VariableDetailOutputDto outputDto) {
        VarProcessVariableSaveSub content = varProcessVariableSaveSubService.getById(inputDto.getContentId());
        VariableSaveInputDto variableSaveInputDto = JSON.parseObject(content.getContent(), VariableSaveInputDto.class);
        VarProcessCategory varProcessCategory = varProcessCategoryService.getById(variableSaveInputDto.getCategoryId());

        outputDto.setName(variableSaveInputDto.getName());
        outputDto.setLabel(variableSaveInputDto.getLabel());
        outputDto.setCategoryId(variableSaveInputDto.getCategoryId());
        outputDto.setCategoryName(ObjectUtils.isEmpty(varProcessCategory) ? "" : varProcessCategory.getName());
        outputDto.setContent(variableSaveInputDto.getContent());
        outputDto.setDescription(variableSaveInputDto.getDescription());
        outputDto.setDataType(variableSaveInputDto.getDataType());
        // 处理方式
        outputDto.setProcessType(variableSaveInputDto.getProcessType());
        outputDto.setFunctionId(variableSaveInputDto.getFunctionId());

        if (!CollectionUtils.isEmpty(variableSaveInputDto.getTags())) {
            List<String> tagNames = variableSaveInputDto.getTags().stream().map(VariableTagDto::getTagName).collect(Collectors.toList());
            List<VarProcessConfigTag> tagList = varProcessConfigTagService.list(
                    new QueryWrapper<VarProcessConfigTag>().lambda()
                            .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                            .in(VarProcessConfigTag::getName, tagNames)
            );

            if (!CollectionUtils.isEmpty(tagList)) {
                List<VariableTagDto> tags = new ArrayList<>();
                for (VarProcessConfigTag tag : tagList) {
                    tags.add(VariableTagDto.builder().groupId(tag.getGroupId()).tagName(tag.getName()).build()
                    );
                }
                outputDto.setTags(tags);
            }
        }
    }

    /**
     * 变量使用
     *
     * @param varProcessSpaceId varProcessSpaceId
     * @param variableId        variableId
     * @return VariableUseOutputVo List
     */
    public List<VariableUseOutputVo> getUseManifestList(Long varProcessSpaceId, Long variableId) {
        List<VariableUseOutputVo> result = new ArrayList<>();
        //被其他变量使用
        List<Long> variableIdList = varProcessVariableReferenceService.list(
                        new LambdaQueryWrapper<VarProcessVariableReference>().select(VarProcessVariableReference::getUseByVariableId).eq(VarProcessVariableReference::getVariableId, variableId))
                .stream().map(VarProcessVariableReference::getUseByVariableId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(variableIdList)) {
            List<VarProcessVariable> varProcessVariableList = varProcessVariableService.list(
                    new LambdaQueryWrapper<VarProcessVariable>()
                            .select(VarProcessVariable::getId, VarProcessVariable::getStatus, VarProcessVariable::getVersion, VarProcessVariable::getName, VarProcessVariable::getLabel)
                            .in(VarProcessVariable::getId, variableIdList).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            VariableUseOutputVo variableUseOutputVo = new VariableUseOutputVo();

            //创建表头
            List<VariableUseOutputVo.TableHeader> tableHeader = new ArrayList<>();
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("变量名称").prop("name").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("变量编码").prop("code").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("版本号").prop("version").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("状态").prop("state").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("操作").prop("operate").build());

            List<JSONObject> tableData = new ArrayList<>();
            for (VarProcessVariable item : varProcessVariableList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", item.getLabel());
                jsonObject.put("code", item.getName());
                jsonObject.put("version", "V" + item.getVersion());
                jsonObject.put("state", item.getStatus().getDesc());
                jsonObject.put("id", item.getId());
                tableData.add(jsonObject);
            }

            variableUseOutputVo.setTitle("被其他变量使用");
            variableUseOutputVo.setTableHeader(tableHeader);
            variableUseOutputVo.setTableData(tableData);
            result.add(variableUseOutputVo);
        }
        //被变量清单使用
        List<Long> manifestIdList = varProcessManifestVariableService.list(
                        new LambdaQueryWrapper<VarProcessManifestVariable>().select(VarProcessManifestVariable::getManifestId).eq(VarProcessManifestVariable::getVariableId, variableId))
                .stream().map(VarProcessManifestVariable::getManifestId).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(manifestIdList)) {
            List<VarProcessManifest> manifestList = varProcessManifestService.list(
                    new LambdaQueryWrapper<VarProcessManifest>()
                            .select(VarProcessManifest::getId, VarProcessManifest::getCategoryId, VarProcessManifest::getState, VarProcessManifest::getCategoryId, VarProcessManifest::getVarManifestName)
                            .in(VarProcessManifest::getId, manifestIdList).eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            List<Long> categoryIdList = manifestList.stream().map(VarProcessManifest::getCategoryId).collect(Collectors.toList());
            Map<Long, String> categotyMap = varProcessCategoryService.list(new LambdaQueryWrapper<VarProcessCategory>()
                            .in(VarProcessCategory::getId, categoryIdList))
                    .stream().collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName));

            VariableUseOutputVo variableUseOutputVo = new VariableUseOutputVo();
            //创建表头
            List<VariableUseOutputVo.TableHeader> tableHeader = new ArrayList<>();
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("变量清单名称").prop("name").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("变量清单分类").prop("category").build());
            tableHeader.add(VariableUseOutputVo.TableHeader.builder().label("状态").prop("state").build());

            List<JSONObject> tabletDataList = new ArrayList<>();
            for (VarProcessManifest manifest : manifestList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", manifest.getVarManifestName());
                jsonObject.put("category", categotyMap.get(manifest.getCategoryId()));
                jsonObject.put("state", manifest.getState().getDesc());
                jsonObject.put("id", manifest.getId());
                tabletDataList.add(jsonObject);
            }
            variableUseOutputVo.setTitle("被变量清单使用");
            variableUseOutputVo.setTableHeader(tableHeader);
            variableUseOutputVo.setTableData(tabletDataList);
            result.add(variableUseOutputVo);
        }
        log.info("spaceId->:{}", varProcessSpaceId);
        return result;
    }

    /**
     * variableProperties
     *
     * @param inputDto inputDto
     * @return VariablePropertiesOutputDto
     */
    public VariablePropertiesOutputDto variableProperties(VariablePropertiesInputDto inputDto) {

        //变量详情
        VariableDetailQueryInputDto variableDetailQueryInputDto = new VariableDetailQueryInputDto();
        BeanUtils.copyProperties(inputDto, variableDetailQueryInputDto);
        VariableDetailOutputDto variableDetail = variableDetail(variableDetailQueryInputDto);
        List<TabDto> properties = new ArrayList<>();
        //属性信息
        properties.add(buildPropertyPanel(variableDetail, inputDto));

        //引用信息
        properties.add(buildRefPanelInfo(variableDetail));

        //生命周期
        properties.add(buildLifecyclePanelInfo(variableDetail));

        return VariablePropertiesOutputDto.builder().properties(properties).build();
    }

    /**
     * variableCompare
     *
     * @param variableCompareInputDto variableCompareInputDto
     * @return VariableCompareOutputDto
     */
    public VariableCompareOutputDto variableCompare(VariableCompareInputDto variableCompareInputDto) {

        VariableDetailOutputDto sourceVariableDetailInfo = variableDetail(VariableDetailQueryInputDto.builder()
                .variableId(variableCompareInputDto.getSourceVariableId()).build());
        VariableCompareDetailOutputDto sourceVariableDetailOutInfo = new VariableCompareDetailOutputDto();
        BeanUtils.copyProperties(sourceVariableDetailInfo, sourceVariableDetailOutInfo);
        VariableDetailOutputDto targetVariableDetailInfo = variableDetail(VariableDetailQueryInputDto.builder()
                .variableId(variableCompareInputDto.getTargetVariabletId()).build());
        VariableCompareDetailOutputDto targetVariableDetailOutInfo = new VariableCompareDetailOutputDto();
        BeanUtils.copyProperties(targetVariableDetailInfo, targetVariableDetailOutInfo);

        return VariableCompareOutputDto.builder().sourceVariableDetailInfo(sourceVariableDetailOutInfo)
                .targetVariableDetailInfo(targetVariableDetailOutInfo).build();
    }

    /**
     * versionList
     *
     * @param variableId variableId
     * @return VarProcessVariable List
     */
    public List<VarProcessVariable> versionList(Long variableId) {
        // 根据 ID 查询变量
        VarProcessVariable variable = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                .select(VarProcessVariable::getVarProcessSpaceId, VarProcessVariable::getIdentifier)
                .eq(VarProcessVariable::getId, variableId));
        // 查询拥有相同标识的其他版本变量
        return varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                .select(VarProcessVariable::getId, VarProcessVariable::getIdentifier, VarProcessVariable::getVersion)
                .eq(VarProcessVariable::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                .eq(VarProcessVariable::getIdentifier, variable.getIdentifier())
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
    }

    /**
     * buildPropertyPanel
     *
     * @param variableDetail variableDetail
     * @param inputDto       inputDto
     * @return TabDto
     */
    private TabDto buildPropertyPanel(VariableDetailOutputDto variableDetail, VariablePropertiesInputDto inputDto) {
        List<PanelDto> list = new ArrayList<>();
        // 1、版本来源
        List<Content> baseContents = new ArrayList<>();
        if (variableDetail.getParentId() != null && variableDetail.getParentId() > 0) {
            VarProcessVariable variableEntity = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getVersion)
                    .eq(VarProcessVariable::getId, variableDetail.getParentId()));
            baseContents.add(Content.builder().label("版本来源").value("创建于 V" + variableEntity.getVersion()).build());
        } else {
            baseContents.add(Content.builder().label("版本来源").value("新建").build());
        }
        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("版本来源");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        basePanel.setDatas(baseContents);
        list.add(basePanel);

        // 2、修改记录
        if (!inputDto.getShowType().equals(NumberUtils.INTEGER_ONE)) {
            PanelDto<List<VarProcessVariableSaveSub>> selfSavePanel = new PanelDto();
            List<VarProcessVariableSaveSub> versionSaveList = varProcessVariableSaveSubService.list(
                    new QueryWrapper<VarProcessVariableSaveSub>().lambda()
                            .eq(VarProcessVariableSaveSub::getVariableId, variableDetail.getId())
                            .orderByDesc(VarProcessVariableSaveSub::getUpdatedTime)
            ).stream().peek(item -> {
                Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(Arrays.asList(item.getCreatedUser(), item.getUpdatedUser()));
                item.setCreatedUser(userFullNameMap.getOrDefault(item.getCreatedUser(), MagicStrings.EMPTY_STRING));
                item.setUpdatedUser(userFullNameMap.getOrDefault(item.getUpdatedUser(), MagicStrings.EMPTY_STRING));
            }).collect(Collectors.toList());

            selfSavePanel.setTitle("修改记录");
            selfSavePanel.setType(LocalDataTypeEnum.log.getCode());
            selfSavePanel.setDatas(versionSaveList);
            list.add(selfSavePanel);
        }

        //3、测试结果
        PanelDto<List<Content>> testInfoPanel = getTestInfoPanel(variableDetail);
        list.add(testInfoPanel);

        return TabDto.builder().name("属性信息").content(list).build();
    }

    /**
     * getTestInfoPanel
     *
     * @param variableDetail variableDetail
     * @return 测试信息 Panel
     */
    private PanelDto<List<Content>> getTestInfoPanel(VariableDetailOutputDto variableDetail) {
        // 查找当前未删除的测试数据集 ID (var_process_test_variable.id)
        // 由于相同标识的变量共享测试数据集, 故需要使用标识作为查询条件
        List<VarProcessTest> availableTestDatasetList = varProcessTestVariableService.list(Wrappers.<VarProcessTest>lambdaQuery()
                .select(VarProcessTest::getId)
                .eq(VarProcessTest::getVarProcessSpaceId, variableDetail.getSpaceId())
                .eq(VarProcessTest::getIdentifier, variableDetail.getIdentifier())
                .eq(VarProcessTest::getTestType, TestVariableTypeEnum.VAR.getCode())
                .eq(VarProcessTest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        List<Long> availableTestDatasetIdList = availableTestDatasetList.stream()
                .map(VarProcessTest::getId)
                .collect(Collectors.toList());
        // 变量最近测试结果
        List<VarProcessTestResults> recentTestResultList = null;
        if (!CollectionUtils.isEmpty(availableTestDatasetIdList)) {
            // 存在未删除的测试数据集: 查找最近测试结果
            recentTestResultList = varProcessTestVariableResultsService.list(Wrappers.<VarProcessTestResults>lambdaQuery()
                    .eq(VarProcessTestResults::getTestType, TestVariableTypeEnum.VAR.getCode())
                    .eq(VarProcessTestResults::getVariableId, variableDetail.getId())
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
            if (StringUtils.isEmpty(recentTestDataSetExpectedTableHeader) || TABLE_HEADER_FIELD.equals(recentTestDataSetExpectedTableHeader)) {
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
        // 创建测试信息 Panel
        PanelDto<List<Content>> testInfoPanel = new PanelDto<>();
        testInfoPanel.setTitle("测试结果");
        testInfoPanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> recentTestInfoContentList = new ArrayList<>();
        recentTestInfoContentList.add(Content.builder().label("最后测试通过率").value(recentTestPassRate).url(null).build());
        testInfoPanel.setDatas(recentTestInfoContentList);
        return testInfoPanel;
    }

    /**
     * buildRefPanelInfo
     *
     * @param variableDetail variableDetail
     * @return TabDto
     */
    private TabDto buildRefPanelInfo(VariableDetailOutputDto variableDetail) {
        List<PanelDto> list = new ArrayList<>();

        // 引用的变量模板信息
        List<Content> funcList = Lists.newArrayList();
        List<VarProcessFunction> functionList = varProcessVariableFunctionService.getFunctionByVariableList(variableDetail.getId());
        if (!CollectionUtils.isEmpty(functionList)) {
            functionList.forEach(v -> {
                if (v.getFunctionType() != FunctionTypeEnum.TEMPLATE) {
                    return;
                }
                Content content = new Content();
                String url = "/template/components/template-detail?isAdd=look&spaceId=" + variableDetail.getSpaceId() + "&functionId=" + v.getId();
                content.setValue(v.getName());
                content.setUrl(url);
                funcList.add(content);
            });
        }
        // DEC-2510: https://jira.wisecotech.com/browse/DEC-2510
        if (!CollectionUtils.isEmpty(funcList)) {
            list.add(PanelDto.builder()
                    .title("引用的变量模板信息")
                    .type(LocalDataTypeEnum.cell.getCode())
                    .datas(funcList)
                    .build());
        }

        // 引用的数据模型信息
        List<Content> varList = Lists.newArrayList();
        List<VarProcessVariableVar> variableVars = varProcessVariableVarService.list(
                new QueryWrapper<VarProcessVariableVar>().lambda()
                        .eq(VarProcessVariableVar::getVariableId, variableDetail.getId())
                        .eq(VarProcessVariableVar::getIsSelf, 1)
        );
        if (!CollectionUtils.isEmpty(variableVars)) {
            variableVars.forEach(v -> {
                Content content = new Content();
                String varPath = v.getVarPath();
                content.setValue(varPath.substring(varPath.indexOf(".") + 1) + "_" + v.getVarName());
                varList.add(content);
            });
        }
        // DEC-2510: https://jira.wisecotech.com/browse/DEC-2510
        if (!CollectionUtils.isEmpty(varList)) {
            list.add(PanelDto.builder()
                    .title("引用的数据模型信息")
                    .type(LocalDataTypeEnum.cell.getCode())
                    .datas(varList)
                    .build());
        }

        // 引用的其它变量信息
        List<Content> refVarContents = Lists.newArrayList();
        List<VarProcessVariable> refVarList = varProcessVariableService.getVariablesByVariableId(variableDetail.getId());
        if (!CollectionUtils.isEmpty(refVarList)) {
            refVarList.forEach(item -> {
                Content content = new Content();
                String url = "/indicator/define/detail?isAdd=look&componentType=variable&spaceId=" + variableDetail.getSpaceId() + "&variableId=" + item.getId();
                content.setUrl(url);
                content.setValue(String.format("%s(%s)", item.getName(), item.getLabel()));
                refVarContents.add(content);
            });
        }
        if (!CollectionUtils.isEmpty(refVarContents)) {
            list.add(PanelDto.builder()
                    .title("引用的其它变量信息")
                    .type(LocalDataTypeEnum.cell.getCode())
                    .datas(refVarContents)
                    .build());
        }

        return TabDto.builder().name("引用信息").content(list).build();
    }

    /**
     * 构建变量的生命周期信息
     *
     * @param variableDetail 变量内容 DTO
     * @return tab对象
     */
    private TabDto buildLifecyclePanelInfo(VariableDetailOutputDto variableDetail) {

        List<VarProcessVariableLifecycle> lifecycleList = varProcessVariableLifecycleService.list(
                new QueryWrapper<VarProcessVariableLifecycle>().lambda()
                        .eq(VarProcessVariableLifecycle::getVariableId, variableDetail.getId())
                        .orderByDesc(VarProcessVariableLifecycle::getCreatedTime));

        //表格头
        List<TableContent.TableHeadInfo> tableHead = new ArrayList<>();
        tableHead.add(TableContent.TableHeadInfo.builder().lable("状态").key("status").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作类型").key("operation").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作人").key("operaUserName").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作时间").key("operaTime").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("备注").key("description").build());
        //表格内容
        List<JSONObject> tableData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(lifecycleList)) {
            lifecycleList.forEach(lifecycle -> {
                JSONObject json = new JSONObject();
                json.put("status", lifecycle.getStatus().getDesc());
                json.put("operation", VariableActionTypeEnum.getStatus(lifecycle.getActionType()).getDesc());
                String fullName = userService.getFullNameByUserName(lifecycle.getCreatedUser());
                if (!StringUtils.isEmpty(fullName)) {
                    json.put("operaUserName", fullName);
                }
                json.put("operaTime", lifecycle.getCreatedTime());
                json.put("description", lifecycle.getDescription());
                tableData.add(json);
            });
        }

        //组装列表内容
        TableContent tableContent = TableContent.builder()
                .tableHead(tableHead)
                .tableData(tableData)
                .build();
        List<PanelDto> panelDtoList = new ArrayList<>();
        panelDtoList.add(PanelDto.builder()
                .title("生命周期")
                .type(LocalDataTypeEnum.LIFECYCLE.getCode())
                .datas(tableContent)
                .build());

        return TabDto.builder().name("生命周期").content(panelDtoList).build();
    }

    /**
     * 获取变量清单对应的变量(公共服务,给监控报表使用)
     *
     * @param inputDto 输入实体类对象
     * @return 变量清单对应的变量
     */
    public List<VariableListVo> getVariables(SingleVariableQueryInputDto inputDto) {
        // 1.定义结果
        List<VariableListVo> result = null;
        // 2.获取结果
        result = this.getVariablesWithoutManifest(inputDto);
        return result;
    }

    /**
     * 如果没有传入变量清单, 就用这个函数
     *
     * @param inputDto 输入实体类对象
     * @return 变量的列表
     */
    private List<VariableListVo> getVariablesWithoutManifest(SingleVariableQueryInputDto inputDto) {
        // 1.先获取tag对应的变量Id集合
        Long groupIdByTagId = null;
        if (inputDto.getTagId() != null) {
            groupIdByTagId = varProcessVariableTagService.getGroupIdByTagId(inputDto.getTagId());
        } else if (inputDto.getGroupId() != null) {
            groupIdByTagId = inputDto.getGroupId();
        }
        Set<Long> variableIdsByTag = varProcessVariableTagService.getVariableIds(groupIdByTagId);
        Set<Long> variableIdsByOther = varProcessVariableService.variableIdsByOther();
        // 2.获取variableIds
        List<Long> variableIds = this.getVariablesByOther(groupIdByTagId, variableIdsByTag, variableIdsByOther);
        // 3.获取create_user集合
        List<String> users = null;
        if (inputDto.getDeptId() != null) {
            List<UserSmallDTO> userSmallDtoList = userService.findUserSmallByDeptId(inputDto.getDeptId().intValue());
            if (!CollectionUtils.isEmpty(userSmallDtoList)) {
                users = userSmallDtoList.stream().map(UserSmallDTO::getUsername).collect(Collectors.toList());
            }
        }
        // 4.查询
        List<VarProcessVariable> variables = varProcessVariableService.getVariableList(variableIds, inputDto.getCategoryId(), inputDto.getDataType(), users, inputDto.getKeywords(), inputDto.getOrder());
        // 5.转换结果并返回
        List<VariableListVo> result = new ArrayList<>();
        for (VarProcessVariable item : variables) {
            String category = varProcessCategoryService.getById(item.getCategoryId()).getName();
            String createDept = deptService.getDeptCodeByUserName(item.getCreatedUser());
            result.add(new VariableListVo(item.getId(), item.getLabel(), item.getName(), category, item.getDataType(), item.getVersion(), createDept));
        }
        return result;
    }

    /**
     * 求两个Set集合的交集
     *
     * @param tagId  tagId
     * @param tag    tag带来的Set集合
     * @param others 其他集合
     * @return 变量的id集合
     */
    private List<Long> getVariablesByOther(Long tagId, Set<Long> tag, Set<Long> others) {
        List<Long> result = new ArrayList<>();
        if (tagId != null && CollectionUtils.isEmpty(tag)) {
            // 1.如果tagId不为空,且tag集合为空,则返回的就是空
            result.add(null);
        } else if (tagId != null) {
            // 2.如果tagId不为空,且tag集合也不为空,则不管others如何
            tag.retainAll(others);
            result.addAll(tag);
            if (CollectionUtils.isEmpty(result)) {
                result.add(null);
            }
        } else if (CollectionUtils.isEmpty(others)) {
            // 3.如果tagId为空,且others也为空
            result.add(null);
        } else {
            // 4.如果tagId为空,但others不为空
            result.addAll(others);
        }
        return result;
    }

    /**
     * 获取流式变量模板枚举list
     *
     * @return list
     */
    public List<SceneListSimpleOutputVO.ProcessTemplateOutputDto> findProcessTemplates() {
        return StreamProcessTemplateEnum.findProcessTemplates();
    }

    /**
     * 获取流式变量计算函数枚举list
     *
     * @return list
     */
    public List<SceneListSimpleOutputVO.CalculateFunctionOutputDto> findCalculateFunctions() {
        return StreamProcessCalFunctionEnum.findCalculateFunctions();
    }

    /**
     * 获取流式变量计算函数枚举list
     * @param dataType 数据类型
     * @return list
     */
    public List<StreamProcessFilterConditionCmpEnum> finComparisonsByDataType(String dataType) {
        DataVariableTypeEnum dataTypeEnum = DataVariableTypeEnum.getMessageEnum(dataType);
        switch (dataTypeEnum) {
            case INT_TYPE:
            case DOUBLE_TYPE:
                return StreamProcessFilterConditionCmpEnum.getComparisonsForNumber();
            case DATE_TYPE:
            case DATETIME_TYPE:
                return StreamProcessFilterConditionCmpEnum.getComparisonsForDate();
            case STRING_TYPE:
                return StreamProcessFilterConditionCmpEnum.getComparisonsForString();
            default:
                return new ArrayList<>();
        }
    }
}
