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
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.EnableDisableEnum;
import com.wiseco.var.process.app.server.controller.vo.CategoryLabelOutPutVo;
import com.wiseco.var.process.app.server.controller.vo.input.HasOrderNo;
import com.wiseco.var.process.app.server.controller.vo.input.OpeType;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VarProcessCategoryOutputDto;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessCategoryTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.DEFAULT_SPACE_ID;

/**
 * 异常值设置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */
@Slf4j
@Service
public class VarProcessCategoryBiz {

    @Autowired
    private VarProcessCategoryService varProcessCategoryService;

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Autowired
    private VarProcessFunctionService varProcessFunctionService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private UserService userService;

    @Autowired
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;

    @Autowired
    private AuthService authService;
    public static final String LIMIT_1 = " limit 1";

    /**
     * 服务分类树的查询
     *
     * @return 服务分类树
     */
    public List<TreeNode> getServiceCategoryTree() {
        // 1.category的条件查询
        LambdaQueryWrapper<VarProcessCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessCategory::getEnabled, VarProcessCategoryTypeEnum.ENABLED.getCode());
        wrapper.eq(VarProcessCategory::getDeleteFlag, VarProcessCategoryTypeEnum.UNDELETED.getCode());
        wrapper.eq(VarProcessCategory::getCategoryType, VarProcessCategoryTypeEnum.SERVICE.toString());

        //数据权限过滤
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        wrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessCategory::getDeptCode, roleDataAuthority.getDeptCodes());
        wrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessCategory::getCreatedUser, roleDataAuthority.getUserNames());
        List<VarProcessCategory> varProcessCategories = varProcessCategoryService.list(wrapper);

        // 2.转换为树形结构
        return doVarProcessCategoryTreeNodes(varProcessCategories);
    }

    /**
     * getManifestTree
     * @return List
     */
    public List<TreeNode> getManifestTree() {
        // 1.先创建出要返回的List<ManifestCategoryTreeNode>
        List<TreeNode> allNodes = new ArrayList<>();
        // 2.先查询出所有的变量清单分类
        LambdaQueryWrapper<VarProcessCategory> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(VarProcessCategory::getEnabled, EnableDisableEnum.ENABLE.getValue());
        categoryLambdaQueryWrapper.eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        categoryLambdaQueryWrapper.eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.MANIFEST);
        //数据权限过滤
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        categoryLambdaQueryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessCategory::getDeptCode, roleDataAuthority.getDeptCodes());
        categoryLambdaQueryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessCategory::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessCategory> categoryList = varProcessCategoryService.list(categoryLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(categoryList)) {
            // 变量清单分类为空，无法对其进行选择
            return null;
        }
        // 3.对变量清单分类进行预处理
        for (VarProcessCategory item : categoryList) {
            TreeNode node = new TreeNode();
            node.setId(item.getId());
            node.setName(item.getName());
            node.setDisabled(true);
            node.setParentId(item.getParentId());
            node.setChildren(null);
            allNodes.add(node);
        }
        // 4.查出所有变量清单分类下面的可用变量清单
        List<Long> categoryId = categoryList.stream()
                .map(VarProcessCategory::getId)
                .collect(Collectors.toList());
        // 5.根据上面的id集合，查询出所有的变量清单
        LambdaQueryWrapper<VarProcessManifest> manifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        manifestLambdaQueryWrapper.select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName, VarProcessManifest::getCategoryId);
        manifestLambdaQueryWrapper.eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        manifestLambdaQueryWrapper.in(VarProcessManifest::getCategoryId, categoryId);
        manifestLambdaQueryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessManifest::getDeptCode, roleDataAuthority.getDeptCodes());
        manifestLambdaQueryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessManifest::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessManifest> manifestList = varProcessManifestService.list(manifestLambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(manifestList)) {
            // 变量清单集合不为空
            for (VarProcessManifest item : manifestList) {
                TreeNode node = new TreeNode();
                node.setId(item.getId());
                node.setName(item.getVarManifestName());
                node.setDisabled(false);
                node.setParentId(item.getCategoryId());
                node.setChildren(null);
                allNodes.add(node);
            }
        }
        // 6.获取所有的根节点
        List<TreeNode> rootNodes = getRootNodes(allNodes);
        // 7.构建出一片森林
        List<TreeNode> forest = buildForest(rootNodes, allNodes);
        // 8.后处理
        for (TreeNode node : forest) {
            dfsSetChildren(node);
        }
        // 9.返回
        return forest;
    }

    /**
     * saveOrUpdateCategory
     * @param inputDto inputDto
     * @return Long
     */
    public Long saveOrUpdateCategory(VarProcessCategoryInputDto inputDto) {
        Long zero = Long.valueOf(String.valueOf(MagicNumbers.ZERO));
        Long minusOne = Long.valueOf(String.valueOf(MagicNumbers.MINUS_INT_1));
        //停用校验
        if (0 == inputDto.getEnabled()) {
            suspendCheck(inputDto);
        }
        VarProcessCategory value = new VarProcessCategory();
        value.setName(inputDto.getCategoryName());
        value.setEnabled(inputDto.getEnabled());
        value.setParentId(inputDto.getParentId() == null ? zero : inputDto.getParentId());
        value.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        value.setUpdatedTime(new Date());
        value.setVarProcessSpaceId(inputDto.getVarProcessSpaceId());
        value.setCategoryType(inputDto.getCategoryType());
        //校验同一级别下，变量分类名称不能重复

        List<VarProcessCategory> varProcessCategoryDtoListByParentId = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda()
                .eq(VarProcessCategory::getDeleteFlag, 1)
                .eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getVarProcessSpaceId())
                .eq(VarProcessCategory::getCategoryType, inputDto.getCategoryType())
                .eq(VarProcessCategory::getParentId, inputDto.getParentId() == null ? zero : inputDto.getParentId())
                .orderByDesc(VarProcessCategory::getOrderNo));

        varProcessCategoryDtoListByParentId.forEach(
                s -> {
                    if (s.getName().equals(inputDto.getCategoryName()) && !s.getId().equals(inputDto.getId())) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"同一级别下，分类名称不能重复");
                    }
                }
        );
        if (ObjectUtils.isNotEmpty(inputDto.getId())) {
            //编辑

            //数据校验
            if (inputDto.getId().equals(inputDto.getParentId())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"不能选择分类自身");
            }
            //获取当前id下的子级
            List<VarProcessCategory> varProcessCategoryDtoList = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda()
                    .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getVarProcessSpaceId()));

            VarProcessCategory varProcessCategory = varProcessCategoryService.getById(inputDto.getId());

            findCategoryListChildrenContainParentId(VarProcessCategoryOutputDto.builder().categoryId(varProcessCategory.getId()).parentId(varProcessCategory.getParentId()).build(), getTreeNodes(varProcessCategoryDtoList), inputDto.getParentId());

            value.setId(inputDto.getId());
            VarProcessCategory cat = varProcessCategoryService.getById(inputDto.getId());
            if (!Objects.equals(cat.getParentId(), inputDto.getParentId())) {
                VarProcessCategory category = varProcessCategoryDtoListByParentId.isEmpty() ? null : varProcessCategoryDtoListByParentId.get(0);
                int orderNo = (category != null && category.getOrderNo() != null) ? category.getOrderNo() + 1 : 0;
                value.setOrderNo(orderNo);
            }
            return !varProcessCategoryService.updateById(value) ? minusOne : inputDto.getId();
        } else {
            value.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
        }
        //新增
        value.setCreatedUser(SessionContext.getSessionUser().getUsername());
        Integer maxNo = varProcessCategoryDtoListByParentId.stream()
                .map(VarProcessCategory::getOrderNo)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);
        value.setOrderNo(maxNo == null ? 0 : maxNo + 1);
        varProcessCategoryService.save(value);
        return value.getId();
    }

    /**
     * 停用时校验分类是否被使用
     *
     * @param inputDto inputDto
     */
    private void suspendCheck(VarProcessCategoryInputDto inputDto) {
        long useCount = 0L;
        switch (inputDto.getCategoryType()) {
            case VARIABLE:
                useCount = varProcessVariableService.count(Wrappers.<VarProcessVariable>lambdaQuery()
                        .eq(VarProcessVariable::getCategoryId, inputDto.getId())
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
                break;
            case VARIABLE_TEMPLATE:
                useCount = varProcessFunctionService.count(Wrappers.<VarProcessFunction>lambdaQuery()
                        .eq(VarProcessFunction::getCategoryId, inputDto.getId())
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.TEMPLATE));
                break;
            case FUNCTION:
                useCount = varProcessFunctionService.count(Wrappers.<VarProcessFunction>lambdaQuery()
                        .eq(VarProcessFunction::getCategoryId, inputDto.getId())
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.FUNCTION));
                break;
            case MANIFEST:
                useCount = varProcessManifestService.count(Wrappers.<VarProcessManifest>lambdaQuery()
                        .eq(VarProcessManifest::getCategoryId, inputDto.getId())
                        .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
                break;
            case SERVICE:
                useCount = varProcessRealtimeServiceService.count(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                        .eq(VarProcessRealtimeService::getCategoryId, inputDto.getId())
                        .eq(VarProcessRealtimeService::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
                break;
            default:
                break;
        }

        if (useCount != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该分类已经被使用，不允许停用");
        }
    }

    /**
     * getCategoryLabel
     * @return List
     */
    public List<CategoryLabelOutPutVo> getCategoryLabel() {
        List<CategoryLabelOutPutVo> outPutVos = new ArrayList<>();
        CategoryTypeEnum[] categoryTypes = CategoryTypeEnum.values();
        for (CategoryTypeEnum categoryType : categoryTypes) {
            outPutVos.add(new CategoryLabelOutPutVo(categoryType, categoryType.getDesc()));
        }
        return outPutVos;
    }

    /**
     * deleteCategory
     * @param inputDto inputDto
     * @return Boolean
     */
    public Boolean deleteCategory(VarProcessCategoryUpdateInputDto inputDto) {
        //判断是否有子类

        VarProcessCategory category = varProcessCategoryService.getById(inputDto.getCategoryId());
        if (category == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"请检查输入的分类id是否存在");
        }
        Set<Long> usedCategories = getUsedCategories(category.getVarProcessSpaceId(), category.getCategoryType());
        if (usedCategories.contains(inputDto.getCategoryId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该分类已被使用，不允许删除");
        }
        //判断子分类是否被使用
        Set<Long> subSet = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda()
                .eq(VarProcessCategory::getDeleteFlag, 1)
                .eq(VarProcessCategory::getParentId, inputDto.getCategoryId())).stream().map(VarProcessCategory::getId).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(subSet)) {
            usedCategories.retainAll(subSet);
            if (!CollectionUtils.isEmpty(usedCategories)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该分类下存在被使用的子分类，不允许删除");
            }
        }

        VarProcessCategory value = new VarProcessCategory();
        value.setId(inputDto.getCategoryId());
        value.setDeleteFlag(0);
        value.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        return varProcessCategoryService.updateById(value);
    }

    /**
     * checkDeleteCategory
     * @param inputDto inputDto
     * @return String
     */
    public String checkDeleteCategory(VarProcessCategoryCheckInputDto inputDto) {
        //判断分类是否被使用
        VarProcessCategory category = varProcessCategoryService.getById(inputDto.getId());
        if (category == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"请检查输入的分类id是否存在");
        }
        Set<Long> usedCategories = getUsedCategories(category.getVarProcessSpaceId(), category.getCategoryType());
        if (inputDto.getOperationType() == 1 && usedCategories.contains(inputDto.getId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING,"该分类已被使用，确认编辑?");
        }

        if (inputDto.getOperationType() == MagicNumbers.TWO) {

            if (usedCategories.contains(inputDto.getId())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该分类已被使用，不允许删除");
            }
            //判断子分类是否被使用
            Map<Long, VarProcessCategory> categoryMap = varProcessCategoryService.list(Wrappers.<VarProcessCategory>lambdaQuery().eq(VarProcessCategory::getVarProcessSpaceId, DEFAULT_SPACE_ID).eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessCategory::getEnabled, 1).eq(VarProcessCategory::getCategoryType, category.getCategoryType()))
                    .stream().collect(Collectors.toMap(VarProcessCategory::getId, item -> item, (key1, key2) -> key1));
            List<Long> categoryIdList = new ArrayList<>();
            getCategoriesUndered(categoryMap, categoryIdList, inputDto.getId());
            if (!CollectionUtils.isEmpty(categoryIdList)) {
                usedCategories.retainAll(categoryIdList);
                if (!CollectionUtils.isEmpty(usedCategories)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该分类下存在被使用的子分类，不允许删除");
                }
            }
        }
        return "确认删除？";
    }

    /**
     * 获取分类下所有子分类
     * @param categoryMap map
     * @param categoryIdList list
     * @param categoryId 分类id
     */
    public static void getCategoriesUndered(Map<Long, VarProcessCategory> categoryMap, List<Long> categoryIdList, Long categoryId) {
        for (Map.Entry<Long, VarProcessCategory> categoryEntry : categoryMap.entrySet()) {
            if (Objects.equals(categoryEntry.getValue().getParentId(), categoryId)) {
                categoryIdList.add(categoryEntry.getValue().getId());
                getCategoriesUndered(categoryMap, categoryIdList, categoryEntry.getKey());
            }
        }
    }

    /**
     * getCategoryTree
     * @param categoryType 类别
     * @param varProcessSpaceId 空间Id
     * @param enabled 判断
     * @return List
     */
    public List<TreeNode> getCategoryTree(CategoryTypeEnum categoryType, Long varProcessSpaceId, Boolean enabled) {

        LambdaQueryWrapper<VarProcessCategory> wrapper = new QueryWrapper<VarProcessCategory>().lambda()
                .eq(VarProcessCategory::getDeleteFlag, 1)
                .eq(VarProcessCategory::getVarProcessSpaceId, varProcessSpaceId)
                .eq(VarProcessCategory::getCategoryType, categoryType);
        if (enabled != null) {
            wrapper.eq(VarProcessCategory::getEnabled, enabled);
        }

        //数据权限过滤
        RoleDataAuthorityDTO roleDataAuthority = authService.getAllAuthority();
        wrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessCategory::getDeptCode, roleDataAuthority.getDeptCodes());
        wrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessCategory::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessCategory> varProcessCategoryDtoList = varProcessCategoryService.list(wrapper);

        return doVarProcessCategoryTreeNodes(varProcessCategoryDtoList);
    }

    private List<TreeNode> doVarProcessCategoryTreeNodes(List<VarProcessCategory> list) {
        // 将数据整理成 TreeNode
        List<TreeNode> treeNodes = new ArrayList<>();
        for (VarProcessCategory item : list) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(item.getId());
            treeNode.setParentId(item.getParentId());
            treeNode.setName(item.getName());
            treeNode.setOrderNo(item.getOrderNo());
            treeNodes.add(treeNode);
        }

        // 声明存储新的TreeNodes变量
        List<TreeNode> newTreeNodes = new ArrayList<>();
        for (TreeNode treeNode : treeNodes) {
            // 从1级分类开始，一个一个往下找
            if (treeNode.getParentId() == 0) {
                newTreeNodes.add(findChildren(treeNode, treeNodes));
            }
        }
        // 返回最后结果
        return newTreeNodes;
    }

    private TreeNode findChildren(TreeNode cNode, List<TreeNode> nodeList) {
        for (TreeNode node : nodeList) {
            if (cNode.getId().equals(node.getParentId())) {
                if (null == cNode.getChildren()) {
                    cNode.setChildren(new ArrayList<>());
                }
                cNode.getChildren().add(findChildren(node, nodeList));
            }
        }
        return cNode;
    }

    private VarProcessCategoryOutputDto findCategoryListChildrenContainParentId(VarProcessCategoryOutputDto cNode,
                                                                                List<VarProcessCategoryOutputDto> nodeList, Long sonCategoryId) {
        for (VarProcessCategoryOutputDto node : nodeList) {
            if (cNode.getCategoryId().equals(node.getParentId())) {
                if (node.getCategoryId().equals(sonCategoryId)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"不能选择自身下的子级");
                }
                if (null == cNode.getChildren()) {
                    cNode.setChildren(new ArrayList<>());
                }
                cNode.getChildren().add(findCategoryListChildrenContainParentId(node, nodeList, sonCategoryId));
            }
        }
        return cNode;
    }

    /**
     * 获取被使用的分类集合
     *
     * @param spaceId spaceId
     * @param categoryType categoryType
     * @return Set
     */
    private Set<Long> getUsedCategories(Long spaceId, CategoryTypeEnum categoryType) {
        Set<Long> usedCategories = new HashSet<>();
        switch (categoryType) {
            case VARIABLE:
                usedCategories = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                                .select(VarProcessVariable::getCategoryId)
                                .eq(VarProcessVariable::getDeleteFlag, 1))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(VarProcessVariable::getCategoryId)
                        .collect(Collectors.toSet());
                break;
            case VARIABLE_TEMPLATE:
                usedCategories = varProcessFunctionService.list(Wrappers.<VarProcessFunction>lambdaQuery()
                                .select(VarProcessFunction::getCategoryId)
                                .eq(VarProcessFunction::getDeleteFlag, 1)
                                .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.TEMPLATE))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(VarProcessFunction::getCategoryId)
                        .collect(Collectors.toSet());
                break;
            case FUNCTION:
                usedCategories = varProcessFunctionService.list(Wrappers.<VarProcessFunction>lambdaQuery()
                                .select(VarProcessFunction::getCategoryId)
                                .eq(VarProcessFunction::getDeleteFlag, 1)
                                .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.FUNCTION))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(VarProcessFunction::getCategoryId)
                        .collect(Collectors.toSet());
                break;
            case MANIFEST:
                usedCategories = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                                .select(VarProcessManifest::getCategoryId)
                                .eq(VarProcessManifest::getDeleteFlag, 1))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(VarProcessManifest::getCategoryId)
                        .collect(Collectors.toSet());
                break;
            case SERVICE:
                usedCategories = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                                .select(VarProcessRealtimeService::getCategoryId)
                                .eq(VarProcessRealtimeService::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                        .stream()
                        .filter(Objects::nonNull)
                        .map(VarProcessRealtimeService::getCategoryId)
                        .collect(Collectors.toSet());
                break;
            default:
                break;
        }
        return usedCategories;
    }

    private Map<Long, String> getCategoryNameMapByCategoryId(Long spaceId, CategoryTypeEnum categoryType) {

        List<VarProcessCategory> list = varProcessCategoryService.list(
                new QueryWrapper<VarProcessCategory>().lambda()
                        .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessCategory::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessCategory::getCategoryType, categoryType));
        return list.stream()
                .collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName));

    }

    private List<VarProcessCategoryOutputDto> getTreeNodes(List<VarProcessCategory> list) {

        // 将数据整理成 TreeNode
        List<VarProcessCategoryOutputDto> treeNodes = new ArrayList<>();
        for (VarProcessCategory item : list) {
            VarProcessCategoryOutputDto treeNode = new VarProcessCategoryOutputDto();
            treeNode.setCategoryId(item.getId());
            treeNode.setCategoryName(item.getName());
            treeNode.setParentId(item.getParentId() != null ? item.getParentId() : null);
            treeNode.setUpdatedUser(item.getUpdatedUser());
            treeNode.setUpdatedTime(DateUtil.parseDateToStr(item.getUpdatedTime(), DateUtil.FORMAT_LONG));
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * getCategoryList
     * @param inputDto 输入
     * @return List
     */
    public List<VarProcessCategoryOutputDto> getCategoryList(VarProcessCategoryQueryInputDto inputDto) {
        List<VarProcessCategoryOutputDto> rootNodes = new ArrayList<>();

        LambdaQueryWrapper<VarProcessCategory> queryWrapper = new QueryWrapper<VarProcessCategory>().lambda()
                .eq(VarProcessCategory::getCategoryType, inputDto.getCategoryType())
                .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(VarProcessCategory::getVarProcessSpaceId, inputDto.getVarProcessSpaceId());
        if (!StringUtils.isEmpty(inputDto.getCategoryName())) {
            queryWrapper.like(VarProcessCategory::getName, inputDto.getCategoryName().trim());
        }

        //数据权限
        RoleDataAuthorityDTO roleDataAuthority = authService.getAllAuthority();
        if (com.wiseco.var.process.app.server.commons.util.ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return rootNodes;
        }
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()),VarProcessCategory::getDeptCode,roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()),VarProcessCategory::getUpdatedUser,roleDataAuthority.getUserNames());

        List<VarProcessCategory> varProcessCategoryDtoList = varProcessCategoryService.list(queryWrapper);

       // 获取用户名姓名 Map
        List<String> userNames = varProcessCategoryDtoList.stream().map(VarProcessCategory::getUpdatedUser).collect(Collectors.toList());
        Map<String,String> fullNameMap = userService.findFullNameMapByUserNames(userNames);

        // 筛选所有没有父级的变量分类
        List<VarProcessCategory> parentList = varProcessCategoryDtoList.stream().filter(f -> (f.getParentId() == 0)).collect(Collectors.toList());
        // 获取被使用的分类id集合
        Set<Long> usedCategories = getUsedCategories(inputDto.getVarProcessSpaceId(), inputDto.getCategoryType());
        // 获取分类id-name映射
        Map<Long, String> categoryNameMap = getCategoryNameMapByCategoryId(inputDto.getVarProcessSpaceId(), inputDto.getCategoryType());

        // 建树
        for (VarProcessCategory varProcessCategory : parentList) {
            //将varProcessCategory对象变成树节点
            VarProcessCategoryOutputDto treeNode = toTreeNode(rootNodes, usedCategories, categoryNameMap, varProcessCategory,fullNameMap);
            //递归构建树结构
            buildCategoryNodes(varProcessCategoryDtoList, varProcessCategory, treeNode.getChildren(), usedCategories, categoryNameMap,fullNameMap);
        }

        sortByOrderNoAsc(rootNodes);
        return rootNodes;
    }

    private static <T extends HasOrderNo> void sortByOrderNoDesc(List<T> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (T node : nodes) {
            sortByOrderNoDesc(node.getChildren());
        }
        nodes.sort(Comparator.comparing(HasOrderNo::getOrderNo, Comparator.reverseOrder()));
    }

    private static <T extends HasOrderNo> void sortByOrderNoAsc(List<T> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (T node : nodes) {
            sortByOrderNoAsc(node.getChildren());
        }
        nodes.sort(Comparator.comparing(HasOrderNo::getOrderNo));
    }

    /**
     * 将varProcessCategory对象调整成树节点
     *
     * @param rootNodes rootNodes
     * @param usedCategories usedCategories
     * @param categoryNameMap categoryNameMap
     * @param varProcessCategory varProcessCategory
     * @param fullNameMap 用户名map
     * @return com.wiseco.var.process.app.server.controller.vo.output.VarProcessCategoryOutputDto
     */
    private VarProcessCategoryOutputDto toTreeNode(List<VarProcessCategoryOutputDto> rootNodes, Set<Long> usedCategories,
                                                   Map<Long, String> categoryNameMap, VarProcessCategory varProcessCategory,Map<String,String> fullNameMap) {
        VarProcessCategoryOutputDto treeNode = VarProcessCategoryOutputDto.builder().categoryId(varProcessCategory.getId())
                .categoryName(varProcessCategory.getName()).parentId(varProcessCategory.getParentId() != null ? varProcessCategory.getParentId() : null)
                .orderNo(varProcessCategory.getOrderNo()).updatedUser(fullNameMap.getOrDefault(varProcessCategory.getUpdatedUser(),""))
                .updatedTime(DateUtil.parseDateToStr(varProcessCategory.getUpdatedTime(), DateUtil.FORMAT_LONG))
                .isUse(usedCategories.contains(varProcessCategory.getId())).enabled(varProcessCategory.getEnabled()).children(new ArrayList<>()).build();
        treeNode.setParentName(categoryNameMap.getOrDefault(varProcessCategory.getParentId(), ""));
        rootNodes.add(treeNode);
        return treeNode;
    }

    /**
     * 组装树结构
     *
     * @param varProcessCategoryDtoList varProcessCategoryDtoList
     * @param parentCategory parentCategory
     * @param childList childList
     * @param usedCategories usedCategories
     * @param categoryNameMap categoryNameMap
     * @param fullNameMap 用户名map
     */
    private void buildCategoryNodes(List<VarProcessCategory> varProcessCategoryDtoList, VarProcessCategory parentCategory,
                                    List<VarProcessCategoryOutputDto> childList, Set<Long> usedCategories, Map<Long, String> categoryNameMap,
                                    Map<String,String> fullNameMap) {
        for (VarProcessCategory varProcessCategory : varProcessCategoryDtoList) {
            if (!parentCategory.getId().equals(varProcessCategory.getParentId())) {
                continue;
            }
            VarProcessCategoryOutputDto treeNode = toTreeNode(childList, usedCategories, categoryNameMap, varProcessCategory,fullNameMap);
            buildCategoryNodes(varProcessCategoryDtoList, varProcessCategory, treeNode.getChildren(), usedCategories, categoryNameMap,fullNameMap);

        }
    }

    /**
     * 构建出一棵树
     * @param node 树的根结点,disabled必须为false
     * @param nodeList 所有的结点
     * @return 一棵树
     */
    public TreeNode buildTree(List<TreeNode> nodeList, TreeNode node) {
        // 1.保存结点的子结点
        List<TreeNode> childTree = new ArrayList<>();
        // 2.递归构建
        for (TreeNode item : nodeList) {
            if (item.getParentId().equals(node.getId()) && node.getDisabled()) {
                // 父子关系 + 父结点的disabled必须为true
                childTree.add(buildTree(nodeList, item));
            }
        }
        node.setChildren(childTree);
        return node;
    }

    /**
     * 构建出一片深林
     * @param rootNodes 所有的根结点
     * @param allNodes  所有的结点
     * @return 一片森林
     */
    public List<TreeNode> buildForest(List<TreeNode> rootNodes, List<TreeNode> allNodes) {
        // 1.所有的树的根结点
        List<TreeNode> roots = new ArrayList<>();
        // 2.根据根结点建树
        for (TreeNode node : rootNodes) {
            node = buildTree(allNodes, node);
            roots.add(node);
        }
        return roots;
    }

    /**
     * 获取所有的根结点
     * @param allNodes 所有的结点
     * @return 所有的根结点集合
     */
    public List<TreeNode> getRootNodes(List<TreeNode> allNodes) {
        // 1.保存所有的根结点数据
        List<TreeNode> rootNodeList = new ArrayList<>();
        // 2.查询出每一个根结点
        for (TreeNode node : allNodes) {
            if (node.getParentId().equals(0L) && node.getDisabled()) {
                // 一级结点+分类
                rootNodeList.add(node);
            }
        }
        return rootNodeList;
    }

    /**
     * 通过深度优先遍历，设置子树为[]的结点为null
     * @param root 根结点
     */
    public void dfsSetChildren(TreeNode root) {
        // 1.先设置根结点
        List<TreeNode> children = root.getChildren();
        if (children.size() == 0) {
            root.setChildren(null);
            return;
        }
        // 2.深度优先遍历子结点
        for (TreeNode node : children) {
            dfsSetChildren(node);
        }
    }

    /**
     * 获取分类树及分类下启用的变量清单
     *
     * @param spaceId 空间Id
     * @param excludedList 条件清单
     * @return List
     */
    public List<TreeNode> getCategoryTreeWithManifest(Long spaceId, List<Long> excludedList) {

        //获取分类树
        List<TreeNode> categoryTree = getCategoryTree(CategoryTypeEnum.MANIFEST, spaceId, true);

        sortByOrderNoAsc(categoryTree);

        //获取启用的清单List
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<VarProcessManifest> upManifests = varProcessManifestService.getUpManifest(spaceId, excludedList,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());

        if (CollectionUtils.isEmpty(upManifests)) {
            return new ArrayList<>();
        }

        //将数据转成TreeNode
        Map<Long, List<TreeNode>> manifestNodes = new HashMap<>(MagicNumbers.EIGHT);
        upManifests.stream().map(manifest -> {
            TreeNode treeNode = new TreeNode();
            treeNode.setName(manifest.getVarManifestName());
            treeNode.setId(manifest.getId());
            treeNode.setParentId(manifest.getCategoryId());
            treeNode.setDisabled(false);
            return treeNode;
        }).forEach(item -> {
            if (!manifestNodes.containsKey(item.getParentId())) {
                manifestNodes.put(item.getParentId(), new ArrayList<>());
            }
            manifestNodes.get(item.getParentId()).add(item);
        });

        //遍历分类树,将节点挂到分类下
        for (TreeNode treeNode : categoryTree) {
            addChild(treeNode, manifestNodes);
        }
        //移除没有清单的分类树
        prune(categoryTree);
        return categoryTree;
    }

    /**
     * addChild
     * @param treeNode treeNode
     * @param manifestNodes manifestNodes
     */
    public void addChild(TreeNode treeNode, Map<Long, List<TreeNode>> manifestNodes) {
        if (null == treeNode) {
            return;
        }
        treeNode.setDisabled(true);
        List<TreeNode> childrens = treeNode.getChildren();
        if (null == childrens) {
            childrens = new ArrayList<>();
        }
        for (TreeNode subNode : childrens) {
            addChild(subNode, manifestNodes);
        }
        childrens.addAll(manifestNodes.getOrDefault(treeNode.getId(), new ArrayList<>()));
        treeNode.setChildren(childrens);
    }

    /**
     * 递归剪枝
     * @param categoryTree categoryTree
     */
    public void prune(List<TreeNode> categoryTree) {
        if (CollectionUtils.isEmpty(categoryTree)) {
            return;
        }
        Iterator<TreeNode> iterator = categoryTree.iterator();
        while (iterator.hasNext()) {
            TreeNode treeNode = iterator.next();
            // 递归处理子节点
            prune(treeNode.getChildren());
            if (BooleanUtils.isTrue(treeNode.disabled) && CollectionUtils.isEmpty(treeNode.children)) {
                // 删除没有挂清单的分类
                iterator.remove();
            }
        }
    }

    /**
     * 上移/下移分类
     * @param categoryId 分类id
     * @param opeType {@link OpeType}
     * @return true or false
     */
    public Boolean moveCategory(Long categoryId, OpeType opeType) {
        Long zero = Long.parseLong(String.valueOf(MagicNumbers.ZERO));
        VarProcessCategory category = varProcessCategoryService.getById(categoryId);
        LambdaQueryWrapper<VarProcessCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VarProcessCategory::getDeleteFlag, 1)
                .eq(VarProcessCategory::getVarProcessSpaceId, category.getVarProcessSpaceId())
                .eq(VarProcessCategory::getCategoryType, category.getCategoryType())
                .eq(VarProcessCategory::getParentId, category.getParentId() == null ? zero : category.getParentId());

        Integer orderNo = category.getOrderNo();
        if (OpeType.UP.equals(opeType)) {
            queryWrapper.lt(VarProcessCategory::getOrderNo,orderNo).orderByDesc(VarProcessCategory::getOrderNo);
        } else {
            queryWrapper.gt(VarProcessCategory::getOrderNo,orderNo).orderByAsc(VarProcessCategory::getOrderNo);
        }
        Optional<VarProcessCategory> option = varProcessCategoryService.list(queryWrapper).stream().findFirst();
        option.ifPresent(cat2 -> {
            Integer targetNo = cat2.getOrderNo();
            cat2.setOrderNo(orderNo);
            category.setOrderNo(targetNo);
            varProcessCategoryService.updateBatchById(Arrays.asList(category,cat2));
        });

        return true;
    }

    /**
     * 分类节点Bean
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeNode implements HasOrderNo {
        // 分类名
        private String name;
        // 分类id
        private Long id;
        // 父分类id
        private Long parentId;
        //排序字段
        private Integer orderNo;
        // 子分类集合
        private List<TreeNode> children;
        //true-分类-不可选；false-清单-可选
        private Boolean disabled;
    }
}
