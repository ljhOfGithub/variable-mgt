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
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionPropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSelectInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionSelectOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionUseListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionLifecycleMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionSaveSub;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestResults;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.FunctionDetailDto;
import com.wiseco.var.process.app.server.service.dto.FunctionQueryDto;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestFunctionService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class FunctionContentBiz {

    public static final String STRING_NULL = "null";
    public static final String STRING_BRACE = "{}";
    private static final String STATUS_CODE = "status";
    private static final String CATEGORY_NAME_CODE = "categoryName";
    @Resource
    private VarProcessFunctionLifecycleMapper varProcessFunctionLifecycleMapper;
    @Resource
    private UserService userService;
    @Resource
    private VarProcessFunctionService varProcessFunctionService;
    @Resource
    private VarProcessManifestService varProcessManifestService;
    @Resource
    private VarProcessVariableFunctionService varProcessVariableFunctionService;
    @Resource
    private VarProcessVariableService varProcessVariableService;
    @Resource
    private VarProcessFunctionSaveSubService varProcessFunctionSaveSubService;
    @Resource
    private VarProcessFunctionVarService varProcessFunctionVarService;
    @Resource
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;
    @Resource
    private VarProcessManifestFunctionService varProcessManifestFunctionService;
    @Resource
    private VarProcessTestService varProcessTestService;
    @Resource
    private VarProcessTestResultsService varProcessTestResultsService;
    @Resource
    private VarProcessCategoryService varProcessCategoryService;
    @Resource
    private AuthService authService;

    /**
     * getFunctionList
     *
     * @param inputVO 入参
     * @return IPage
     */
    public IPage<FunctionListOutputVO> getFunctionList(FunctionQueryInputVO inputVO) {
        Page<FunctionListOutputVO> page = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());
        FunctionQueryDto functionQueryDto = new FunctionQueryDto();
        BeanUtils.copyProperties(inputVO, functionQueryDto);
        functionQueryDto.setFunctionType(inputVO.getFunctionType().name());
        //构造排序条件
        String order = inputVO.getOrder();
        if (StringUtils.isEmpty(order)) {
            functionQueryDto.setSortKey("updated_time");
            functionQueryDto.setSortType("DESC");
        } else {
            String sortType = order.substring(order.indexOf("_") + 1);
            String sortKey = order.substring(0, order.indexOf("_"));
            sortKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortKey);
            functionQueryDto.setSortKey(sortKey);
            functionQueryDto.setSortType(sortType);
        }
        //查询分类
        LambdaQueryWrapper<VarProcessCategory> wrapper = new QueryWrapper<VarProcessCategory>().lambda()
                .eq(VarProcessCategory::getVarProcessSpaceId, inputVO.getSpaceId());
        if (!ObjectUtils.isEmpty(inputVO.getFunctionType())) {
            wrapper.eq(VarProcessCategory::getCategoryType, inputVO.getFunctionType() == FunctionTypeEnum.TEMPLATE
                    ? CategoryTypeEnum.VARIABLE_TEMPLATE
                    : CategoryTypeEnum.FUNCTION);
        }
        List<VarProcessCategory> categoryList = varProcessCategoryService.list(wrapper);
        //构造分类查询条件 查询分类及其子分类
        Map<Long, List<VarProcessCategory>> categoryListMap = categoryList.stream().collect(Collectors.groupingBy(VarProcessCategory::getParentId));
        Set<Long> newCategorySet = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        if (!StringUtils.isEmpty(inputVO.getCategoryId())) {
            queue.add(inputVO.getCategoryId());
            while (!queue.isEmpty()) {
                Long categoryId = queue.poll();
                newCategorySet.add(categoryId);
                if (categoryListMap.containsKey(categoryId)) {
                    for (VarProcessCategory category : categoryListMap.get(categoryId)) {
                        queue.add(category.getId());
                    }
                }
            }
            functionQueryDto.setCategoryIdList(new ArrayList<>(newCategorySet));
        }
        RoleDataAuthorityDTO roleDataAuthority = inputVO.getFunctionType() == FunctionTypeEnum.FUNCTION
                ? authService.getAllAuthority()
                : authService.getRoleDataAuthority();
        if (com.wiseco.var.process.app.server.commons.util.ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return page;
        }
        BeanUtils.copyProperties(roleDataAuthority, functionQueryDto);
        IPage<FunctionDetailDto> pageList = varProcessFunctionService.findFunctionList(page, functionQueryDto);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return page;
        }
        Map<Long, String> categoryMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName));
        List<Long> functionIdList = pageList.getRecords().stream().filter(item -> item.getStatus() == FlowStatusEnum.REFUSE).map(FunctionDetailDto::getId).collect(Collectors.toList());
        List<VarProcessFunctionLifecycle> varProcessFunctionLifecycles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(functionIdList)) {
            varProcessFunctionLifecycles  = varProcessFunctionLifecycleMapper.selectList(new LambdaQueryWrapper<VarProcessFunctionLifecycle>().in(VarProcessFunctionLifecycle::getFunctionId, functionIdList).eq(VarProcessFunctionLifecycle::getStatus, FlowStatusEnum.REFUSE));
        }
        Map<Long, VarProcessFunctionLifecycle> lifecycleMap = varProcessFunctionLifecycles.stream().collect(Collectors.toMap(VarProcessFunctionLifecycle::getFunctionId, item -> item, (k1, k2) -> k2));
        Map<String, String> userNameMap = getUserNameMap(pageList, varProcessFunctionLifecycles);
        List<FunctionListOutputVO> list = assembleOutputVos(pageList, categoryMap, lifecycleMap, userNameMap);
        page.setTotal(pageList.getTotal());
        page.setPages(pageList.getPages());
        page.setRecords(list);
        return page;
    }

    @NotNull
    private List<FunctionListOutputVO> assembleOutputVos(IPage<FunctionDetailDto> pageList, Map<Long, String> categoryMap, Map<Long, VarProcessFunctionLifecycle> lifecycleMap, Map<String, String> userNameMap) {
        List<FunctionListOutputVO> list = new ArrayList<>();
        for (FunctionDetailDto varProcessFunction : pageList.getRecords()) {
            FunctionListOutputVO outputVO = new FunctionListOutputVO();
            BeanUtils.copyProperties(varProcessFunction, outputVO);
            outputVO.setCategoryName(categoryMap.get(varProcessFunction.getCategoryId()));
            outputVO.setCreatedUser(userNameMap.get(outputVO.getCreatedUser()));
            outputVO.setUpdatedUser(userNameMap.get(outputVO.getUpdatedUser()));
            if (outputVO.getStatus() == FlowStatusEnum.REFUSE) {
                VarProcessFunctionLifecycle lifecycle = lifecycleMap.get(outputVO.getId());
                JSONObject desc = new JSONObject();
                desc.put("审核人", userNameMap.get(lifecycle.getCreatedUser()));
                desc.put("审核时间", lifecycle.getCreatedTime());
                desc.put("拒绝原因", lifecycle.getDescription());
                outputVO.setStatusDescription(desc.toJSONString());
            }
            list.add(outputVO);
        }
        return list;
    }

    @NotNull
    private Map<String, String> getUserNameMap(IPage<FunctionDetailDto> pageList, List<VarProcessFunctionLifecycle> varProcessFunctionLifecycles) {
        Set<String> userName = new HashSet<>();
        pageList.getRecords().forEach(item -> {
            userName.add(item.getCreatedUser());
            userName.add(item.getUpdatedUser());
        });
        userName.addAll(varProcessFunctionLifecycles.stream().map(VarProcessFunctionLifecycle::getCreatedUser).collect(Collectors.toSet()));
        return userService.findFullNameMapByUserNames(new ArrayList<>(userName));
    }

    /**
     * functionSelect
     *
     * @param inputDto 入参
     * @return List
     */
    public List<FunctionSelectOutputVO> functionSelect(FunctionSelectInputVO inputDto) {
        List<VarProcessFunctionDto> varProcessFunctionDtos;
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (inputDto.getFunctionDataType() == null) {
            varProcessFunctionDtos = varProcessFunctionService.selectFunctions(inputDto.getSpaceId(), inputDto.getFunctionType().name(), inputDto
                    .getFunctionStatus().name(),roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        } else {
            varProcessFunctionDtos = varProcessFunctionService.selectFunctionsNew(inputDto.getSpaceId(), inputDto.getFunctionType().name(), inputDto
                    .getFunctionStatus().name(), inputDto.getFunctionDataType(),roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        }
        List<FunctionSelectOutputVO> result = new ArrayList<>();
        for (VarProcessFunctionDto dto : varProcessFunctionDtos) {
            FunctionSelectOutputVO vo = new FunctionSelectOutputVO();
            BeanUtils.copyProperties(dto, vo);
            result.add(vo);
        }
        return result;
    }

    /**
     * getUseVariableList
     *
     * @param spaceId    空间id
     * @param functionId 公共函数id
     * @return List
     */
    public List<VariableDetailDto> getUseVariableList(Long spaceId, Long functionId) {
        return varProcessVariableFunctionService.getUseVariableList(spaceId, functionId);
    }

    /**
     * functionDetail
     *
     * @param inputDto 入参
     * @return FunctionDetailOutputDto
     */
    public FunctionDetailOutputDto functionDetail(FunctionDetailQueryInputDto inputDto) {
        FunctionDetailOutputDto outputDto = new FunctionDetailOutputDto();

        VarProcessFunction function = varProcessFunctionService.getById(inputDto.getFunctionId());
        if (function == null || function.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "函数信息不存在或已删除");
        }

        outputDto.setSpaceId(function.getVarProcessSpaceId());
        outputDto.setId(function.getId());
        outputDto.setName(function.getName());
        outputDto.setIdentifier(function.getIdentifier());
        outputDto.setParentId(function.getParentId());
        outputDto.setFunctionType(function.getFunctionType());
        outputDto.setFunctionDataType(DataTypeEnum.getEnum(function.getFunctionDataType()));
        outputDto.setDescription(function.getDescription());
        outputDto.setCreatedTime(DateUtil.parseDateToStr(function.getCreatedTime(), DateUtil.FORMAT_LONG));
        outputDto.setCreatedUser(function.getCreatedUser());
        outputDto.setUpdatedUser(function.getUpdatedUser());
        outputDto.setUpdatedTime(DateUtil.parseDateToStr(function.getUpdatedTime(), DateUtil.FORMAT_LONG));
        outputDto.setStatus(function.getStatus());
        outputDto.setPrepObjectName(function.getPrepObjectName());
        outputDto.setCategoryId(function.getCategoryId());
        if (!StringUtils.isEmpty(function.getContent()) && !STRING_NULL.equals(function.getContent())) {
            outputDto.setContent(JSON.parseObject(function.getContent()));
        }
        if (!StringUtils.isEmpty(inputDto.getContentId())) {
            VarProcessFunctionSaveSub content = varProcessFunctionSaveSubService.getById(inputDto.getContentId());
            FunctionSaveInputDto variableSaveInputDto = JSON.parseObject(content.getContent(), FunctionSaveInputDto.class);
            outputDto.setName(variableSaveInputDto.getName());
            outputDto.setFunctionType(variableSaveInputDto.getFunctionType());
            outputDto.setContent(variableSaveInputDto.getContent());
            outputDto.setDescription(variableSaveInputDto.getDescription());
        }
        outputDto.setFunctionEntryContent(function.getFunctionEntryContent());
        if (function.getCategoryId() != null) {
            VarProcessCategory category = varProcessCategoryService.getById(function.getCategoryId());
            outputDto.setCategoryName(category.getName());
        }
        outputDto.setHandleType(function.getHandleType());
        return outputDto;
    }

    /**
     * 数据预处理+变量模板+公共方法的属性信息
     *
     * @param inputDto 入参
     * @return 输出DTO
     */
    public VariablePropertiesOutputDto functionProperties(FunctionPropertiesInputDto inputDto) {
        // 1.获取数据预处理或变量模板或公共函数的详情
        FunctionDetailQueryInputDto variableDetailQueryInputDto = new FunctionDetailQueryInputDto();
        BeanUtils.copyProperties(inputDto, variableDetailQueryInputDto);
        FunctionDetailOutputDto functionDetail = functionDetail(variableDetailQueryInputDto);
        List<TabDto> properties = new ArrayList<>();
        // 2.属性信息
        properties.add(buildPropertyPanel(functionDetail));
        // 3.引用信息
        properties.add(buildRefPanelInfo(functionDetail));
        // 4.生命周期
        properties.add(buildLifeCyclePanelInfo(functionDetail.getId()));
        return VariablePropertiesOutputDto.builder().properties(properties).build();
    }

    /**
     * 获取生命周期的面板信息
     *
     * @param id 数据预处理或者变量模板或者公共方法的ID
     * @return 生命周期的面板信息
     */
    private TabDto buildLifeCyclePanelInfo(Long id) {
        // 1.先获取这个数据预处理或者变量模板或者公共方法的变动记录
        List<VarProcessFunctionLifecycle> varProcessFunctionLifecycles = varProcessFunctionLifecycleMapper.selectList(Wrappers.<VarProcessFunctionLifecycle>lambdaQuery()
                .eq(VarProcessFunctionLifecycle::getFunctionId, id)
                .orderByDesc(VarProcessFunctionLifecycle::getCreatedTime));
        // 2.生成表格头
        List<TableContent.TableHeadInfo> tableHead = new ArrayList<>();
        tableHead.add(TableContent.TableHeadInfo.builder().lable("状态").key("status").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作类型").key("operation").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作人").key("operaUserName").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作时间").key("operaTime").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("备注").key("description").build());
        // 3.生成表格内容
        List<JSONObject> tableData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(varProcessFunctionLifecycles)) {
            varProcessFunctionLifecycles.forEach(lifeCycle -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", lifeCycle.getStatus().getDesc());
                jsonObject.put("operation", lifeCycle.getActionType().getDesc());
                String fullName = userService.getFullNameByUserName(lifeCycle.getCreatedUser());
                jsonObject.put("operaUserName", fullName);
                jsonObject.put("operaTime", DateUtil.parseDateToStr(lifeCycle.getCreatedTime(), MagicStrings.DATE_TIME_FORMAT));
                jsonObject.put("description", lifeCycle.getDescription());
                tableData.add(jsonObject);
            });
        }
        // 4.组装列表内容
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
     * useList
     *
     * @param inputDto 入参
     * @return List
     */
    public List<FunctionUseListOutputDto> useList(FunctionInputDto inputDto) {
        List<FunctionUseListOutputDto> outputDto = new ArrayList<>();

        VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                        .select(VarProcessFunction::getFunctionType, VarProcessFunction::getIdentifier)
                        .eq(VarProcessFunction::getId, inputDto.getFunctionId()));

        if (varProcessFunction.getFunctionType() == FunctionTypeEnum.FUNCTION) {
            //公共方法
            functionUseList(inputDto, outputDto);

        } else if (varProcessFunction.getFunctionType() == FunctionTypeEnum.TEMPLATE) {
            //变量模板
            templateUseList(inputDto, outputDto);
        } else if (varProcessFunction.getFunctionType() == FunctionTypeEnum.PREP) {
            //预处理
            prepUseList(inputDto, outputDto, varProcessFunction);
        }
        return outputDto;
    }


    /**
     * 获取预处理使用情况
     *
     * @param inputDto
     * @param outputDto
     * @param varProcessFunction
     */
    private void prepUseList(FunctionInputDto inputDto, List<FunctionUseListOutputDto> outputDto, VarProcessFunction varProcessFunction) {
        List<VarProcessManifestFunction> referenceList = varProcessManifestFunctionService.list(
                new QueryWrapper<VarProcessManifestFunction>().lambda()
                        .eq(VarProcessManifestFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessManifestFunction::getIdentifier, varProcessFunction.getIdentifier())
        );


        List<FunctionUseListOutputDto.HeaderDto> interfaceHeaders = new ArrayList<>(MagicNumbers.FOUR);
        interfaceHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量清单名称").code("varIManifestName").build());
        interfaceHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量清单分类").code(CATEGORY_NAME_CODE).build());
        interfaceHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code("state").build());


        FunctionUseListOutputDto interfaceBuild = FunctionUseListOutputDto.builder()
                .title("被变量清单使用")
                .header(interfaceHeaders)
                .build();


        List<JSONObject> contentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(referenceList)) {
            List<Long> interfaceIds = referenceList.stream().map(VarProcessManifestFunction::getManifestId).collect(Collectors.toList());
            List<VarProcessManifest> list = varProcessManifestService.list(
                    new QueryWrapper<VarProcessManifest>().lambda()
                            .select(
                                    VarProcessManifest::getVarManifestName,
                                    VarProcessManifest::getCategoryId,
                                    VarProcessManifest::getState,
                                    VarProcessManifest::getId
                            )
                            .in(VarProcessManifest::getId, interfaceIds)
                            .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );

            for (VarProcessManifest interfaces : list) {
                String categoryName = varProcessCategoryService.getById(interfaces.getCategoryId()).getName();
                JSONObject content = new JSONObject();
                content.put("varIManifestName", interfaces.getVarManifestName());
                content.put(CATEGORY_NAME_CODE, categoryName);
                content.put("state", VarProcessManifestStateEnum.getStateEnum(interfaces.getState().getCode()).getDesc());
                content.put("id", interfaces.getId());
                contentList.add(content);
            }
        }
        interfaceBuild.setContent(contentList);
        outputDto.add(interfaceBuild);
    }


    /**
     * 获取变量模板使用情况
     *
     * @param inputDto
     * @param outputDto
     */
    private void templateUseList(FunctionInputDto inputDto, List<FunctionUseListOutputDto> outputDto) {
        List<VarProcessVariableFunction> referenceList = varProcessVariableFunctionService.list(
                new QueryWrapper<VarProcessVariableFunction>().lambda()
                        .eq(VarProcessVariableFunction::getFunctionId, inputDto.getFunctionId())
        );
        List<FunctionUseListOutputDto.HeaderDto> tmpHeader = new ArrayList<>(MagicNumbers.TWO);
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量名称").code("label").build());
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量编码").code("varName").build());
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("版本号").code("version").build());
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code(STATUS_CODE).build());
        FunctionUseListOutputDto build = FunctionUseListOutputDto.builder()
                .title("使用查看")
                .header(tmpHeader)
                .build();

        List<JSONObject> contentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(referenceList)) {
            List<Long> varIds = referenceList.stream().map(VarProcessVariableFunction::getVariableId).collect(Collectors.toList());
            List<VarProcessVariable> list = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda()
                            .select(
                                    VarProcessVariable::getId,
                                    VarProcessVariable::getName,
                                    VarProcessVariable::getLabel,
                                    VarProcessVariable::getVersion,
                                    VarProcessVariable::getStatus
                            )
                            .in(VarProcessVariable::getId, varIds)
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            for (VarProcessVariable variable : list) {
                JSONObject content = new JSONObject();
                content.put("id", variable.getId());
                content.put("varName", variable.getName());
                content.put("label", variable.getLabel());
                content.put("version", "V" + variable.getVersion());
                content.put(STATUS_CODE, variable.getStatus().getDesc());
                contentList.add(content);
            }

        }
        build.setContent(contentList);
        outputDto.add(build);
    }


    /**
     * 获取公共方法使用情况
     *
     * @param inputDto
     * @param outputDto
     */
    private void functionUseList(FunctionInputDto inputDto, List<FunctionUseListOutputDto> outputDto) {
        List<VarProcessFunctionReference> funcReferenceList = varProcessFunctionReferenceService.list(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessFunctionReference::getFunctionId, inputDto.getFunctionId())
        );

        if (!CollectionUtils.isEmpty(funcReferenceList)) {
            List<Long> funcIds = funcReferenceList.stream().map(VarProcessFunctionReference::getUseByFunctionId).collect(Collectors.toList());
            List<VarProcessFunction> list = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(
                                    VarProcessFunction::getId,
                                    VarProcessFunction::getIdentifier,
                                    VarProcessFunction::getFunctionType,
                                    VarProcessFunction::getName,
                                    VarProcessFunction::getPrepObjectName,
                                    VarProcessFunction::getStatus,
                                    VarProcessFunction::getFunctionDataType,
                                    VarProcessFunction::getCategoryId
                            )
                            .eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                            .in(VarProcessFunction::getId, funcIds)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            Map<FunctionTypeEnum, List<VarProcessFunction>> functionMap = list.stream().collect(Collectors.groupingBy(VarProcessFunction::getFunctionType));

            if (functionMap.containsKey(FunctionTypeEnum.PREP)) {

                //预处理逻辑
                FunctionUseListOutputDto prepBuild = prepUseFunction(functionMap);
                outputDto.add(prepBuild);
            }

            //变量模板
            if (functionMap.containsKey(FunctionTypeEnum.TEMPLATE)) {
                FunctionUseListOutputDto tmpBuild = templateUseFunction(functionMap);
                outputDto.add(tmpBuild);
            }

            //公共方法
            if (functionMap.containsKey(FunctionTypeEnum.FUNCTION)) {
                FunctionUseListOutputDto funcBuild = functionUseFunction(functionMap);
                outputDto.add(funcBuild);
            }
        }

        //被变量清单使用
        VarProcessFunction func = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getIdentifier)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));
        List<Long> manifestIds = varProcessManifestFunctionService.list(Wrappers.<VarProcessManifestFunction>lambdaQuery()
                        .eq(VarProcessManifestFunction::getIdentifier, func.getIdentifier())).stream()
                .map(VarProcessManifestFunction::getManifestId).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(manifestIds)) {
            List<VarProcessManifest> manifests = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName, VarProcessManifest::getCategoryId, VarProcessManifest::getState)
                    .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .in(VarProcessManifest::getId, manifestIds));
            outputDto.add(manifestUseFunction(manifests));
        }
    }


    private FunctionUseListOutputDto manifestUseFunction(List<VarProcessManifest> manifests) {
        List<JSONObject> contentList = new ArrayList<>();
        List<FunctionUseListOutputDto.HeaderDto> funcHeader = new ArrayList<>(MagicNumbers.TWO);
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量清单名称").code("name").build());
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("变量清单分类").code(CATEGORY_NAME_CODE).build());
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code(STATUS_CODE).build());
        FunctionUseListOutputDto funcBuild = FunctionUseListOutputDto.builder()
                .title("被变量清单使用")
                .header(funcHeader)
                .build();
        Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(CategoryTypeEnum.MANIFEST);
        for (VarProcessManifest manifest : manifests) {
            JSONObject content = new JSONObject();
            content.put("name", manifest.getVarManifestName());
            content.put(CATEGORY_NAME_CODE, categoryNameMap.getOrDefault(manifest.getCategoryId(), ""));
            content.put(STATUS_CODE, manifest.getState().getDesc());
            content.put("id", manifest.getId());
            contentList.add(content);
        }
        funcBuild.setContent(contentList);
        return funcBuild;
    }

    /**
     * 使用该公共方法的公共方法list
     *
     * @param functionMap
     * @return FunctionUseListOutputDto
     */
    private FunctionUseListOutputDto functionUseFunction(Map<FunctionTypeEnum, List<VarProcessFunction>> functionMap) {
        List<JSONObject> contentList = new ArrayList<>();
        List<FunctionUseListOutputDto.HeaderDto> funcHeader = new ArrayList<>(MagicNumbers.TWO);
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("方法名称").code("name").build());
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("方法分类").code(CATEGORY_NAME_CODE).build());
        funcHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code(STATUS_CODE).build());
        FunctionUseListOutputDto funcBuild = FunctionUseListOutputDto.builder()
                .title("被其他公共方法使用")
                .header(funcHeader)
                .build();

        List<VarProcessFunction> varProcessFunctions = functionMap.get(FunctionTypeEnum.FUNCTION);
        Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(CategoryTypeEnum.FUNCTION);
        for (VarProcessFunction function : varProcessFunctions) {
            JSONObject content = new JSONObject();
            content.put("name", function.getName());
            content.put(CATEGORY_NAME_CODE, categoryNameMap.getOrDefault(function.getCategoryId(), ""));
            content.put(STATUS_CODE, function.getStatus().getDesc());
            content.put("id", function.getId());
            contentList.add(content);
        }
        funcBuild.setContent(contentList);
        return funcBuild;
    }


    /**
     * 使用该公共方法的变量模板list
     *
     * @param functionMap
     * @return FunctionUseListOutputDto
     */
    private FunctionUseListOutputDto templateUseFunction(Map<FunctionTypeEnum, List<VarProcessFunction>> functionMap) {
        List<JSONObject> contentList = new ArrayList<>();
        List<FunctionUseListOutputDto.HeaderDto> tmpHeader = new ArrayList<>(MagicNumbers.TWO);
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("模版名称").code("name").build());
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("模版分类").code(CATEGORY_NAME_CODE).build());
        tmpHeader.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code(STATUS_CODE).build());
        FunctionUseListOutputDto tmpBuild = FunctionUseListOutputDto.builder()
                .title("被变量模版使用")
                .header(tmpHeader)
                .build();

        List<VarProcessFunction> varProcessFunctions = functionMap.get(FunctionTypeEnum.TEMPLATE);
        for (VarProcessFunction function : varProcessFunctions) {
            String categoryName = StringPool.EMPTY;
            if (function.getCategoryId() != null) {
                categoryName = varProcessCategoryService.getById(function.getCategoryId()).getName();
            }
            JSONObject content = new JSONObject();
            content.put("name", function.getName());
            content.put(CATEGORY_NAME_CODE, categoryName);
            content.put(STATUS_CODE, function.getStatus().getDesc());
            content.put("id", function.getId());
            contentList.add(content);
        }
        tmpBuild.setContent(contentList);
        return tmpBuild;
    }


    /**
     * 使用该公共方法的预处理逻辑list
     *
     * @param functionMap
     * @return FunctionUseListOutputDto
     */
    private FunctionUseListOutputDto prepUseFunction(Map<FunctionTypeEnum, List<VarProcessFunction>> functionMap) {
        List<JSONObject> contentList = new ArrayList<>();
        List<FunctionUseListOutputDto.HeaderDto> prepHeaders = new ArrayList<>(MagicNumbers.THREE);
        prepHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("逻辑名称").code("name").build());
        prepHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("处理对象").code("prepObjectName").build());
        prepHeaders.add(FunctionUseListOutputDto.HeaderDto.builder().label("状态").code(STATUS_CODE).build());

        FunctionUseListOutputDto prepBuild = FunctionUseListOutputDto.builder()
                .title("被预处理逻辑使用")
                .header(prepHeaders)
                .build();

        List<VarProcessFunction> varProcessFunctions = functionMap.get(FunctionTypeEnum.PREP);
        for (VarProcessFunction function : varProcessFunctions) {
            JSONObject content = new JSONObject();
            content.put("name", function.getName());
            content.put("prepObjectName", function.getPrepObjectName());
            content.put(STATUS_CODE, function.getStatus().getDesc());
            content.put("id", function.getId());
            contentList.add(content);
        }
        prepBuild.setContent(contentList);
        return prepBuild;
    }

    private TabDto buildPropertyPanel(FunctionDetailOutputDto variableDetail) {
        List<PanelDto> list = new ArrayList<>();

        PanelDto<List<VarProcessFunctionSaveSub>> selfSavePanel = new PanelDto();
        List<VarProcessFunctionSaveSub> versionSaveList = varProcessFunctionSaveSubService.list(
                new QueryWrapper<VarProcessFunctionSaveSub>().lambda()
                        .select(VarProcessFunctionSaveSub::getId, VarProcessFunctionSaveSub::getCreatedUser, VarProcessFunctionSaveSub::getUpdatedTime)
                        .eq(VarProcessFunctionSaveSub::getFunctionId, variableDetail.getId())
                        .orderByDesc(VarProcessFunctionSaveSub::getUpdatedTime)
        );
        //表格头
        List<TableContent.TableHeadInfo> tableHead = new ArrayList<>();
        tableHead.add(TableContent.TableHeadInfo.builder().lable("修改时间").key("updatedTime").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("修改人").key("updatedUser").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作").key("operation").build());
        //表格内容
        List<JSONObject> tableData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(versionSaveList)) {
            versionSaveList.stream().forEach(version -> {
                JSONObject json = new JSONObject();
                json.put("contentId", version.getId());
                json.put("updatedTime", DateUtil.parseDateToStr(version.getUpdatedTime(), DateUtil.FORMAT_LONG));
                json.put("updatedUser",  userService.getFullNameByUserName(version.getCreatedUser()));
                json.put("operation", "查看");
                tableData.add(json);
            });
        }
        //组装列表内容
        TableContent tableContent = TableContent.builder()
                .tableHead(tableHead)
                .tableData(tableData)
                .build();
        list.add(selfSavePanel.builder()
                .title("修改记录")
                .type(LocalDataTypeEnum.log.getCode())
                .datas(tableContent)
                .build());
        // 创建测试信息 Panel
        PanelDto<List<Content>> testInfoPanel = testInfo(variableDetail);
        list.add(testInfoPanel);
        return TabDto.builder().name("属性信息").content(list).build();
    }


    private PanelDto<List<Content>> testInfo(FunctionDetailOutputDto variableDetail) {
        // 查找当前未删除的测试数据集 ID (var_process_test_variable.id),由于相同标识的公共函数共享测试数据集, 故需要使用标识作为查询条件
        List<VarProcessTest> availableTestDatasetList = varProcessTestService.list(Wrappers.<VarProcessTest>lambdaQuery()
                .select(VarProcessTest::getId)
                .eq(VarProcessTest::getVarProcessSpaceId, variableDetail.getSpaceId())
                .eq(VarProcessTest::getIdentifier, variableDetail.getIdentifier())
                .eq(VarProcessTest::getTestType, TestVariableTypeEnum.FUNCTION.getCode())
                .eq(VarProcessTest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        List<Long> availableTestDatasetIdList = availableTestDatasetList.stream()
                .map(VarProcessTest::getId)
                .collect(Collectors.toList());
        // 公共方法最近测试结果
        List<VarProcessTestResults> recentTestResultList = null;
        if (!CollectionUtils.isEmpty(availableTestDatasetIdList)) {
            // 存在未删除的测试数据集: 查找最近测试结果
            recentTestResultList = varProcessTestResultsService.list(Wrappers.<VarProcessTestResults>lambdaQuery()
                    .eq(VarProcessTestResults::getTestType, TestVariableTypeEnum.FUNCTION.getCode())
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
            VarProcessTest recentTestDataSet = varProcessTestService.getById(recentTestResult.getTestId());
            if (null == recentTestDataSet) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "未查询到组件最后测试结果对应的测试数据集，请检查数据完整性。");
            }
            String recentTestDataSetExpectedTableHeader = recentTestDataSet.getTableHeaderField();
            if (StringUtils.isEmpty(recentTestDataSetExpectedTableHeader) || STRING_BRACE.equals(recentTestDataSetExpectedTableHeader)) {
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
        testInfoPanel.setTitle("测试信息");
        testInfoPanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> recentTestInfoContentList = new ArrayList<>();
        recentTestInfoContentList.add(Content.builder().label("最后测试通过率").value(recentTestPassRate).url(null).build());
        testInfoPanel.setDatas(recentTestInfoContentList);

        return testInfoPanel;
    }

    private TabDto buildRefPanelInfo(FunctionDetailOutputDto variableDetail) {
        List<PanelDto> list = new ArrayList<>();

        List<VarProcessFunctionReference> referenceList = varProcessFunctionReferenceService.list(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getVarProcessSpaceId, variableDetail.getSpaceId())
                        .eq(VarProcessFunctionReference::getUseByFunctionId, variableDetail.getId())
        );

        if (!CollectionUtils.isEmpty(referenceList)) {
            List<Content> useVarList = Lists.newArrayList();
            List<Long> funcIds = referenceList.stream().map(VarProcessFunctionReference::getFunctionId).collect(Collectors.toList());
            List<VarProcessFunction> functionList = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(
                                    VarProcessFunction::getId,
                                    VarProcessFunction::getVarProcessSpaceId,
                                    VarProcessFunction::getIdentifier,
                                    VarProcessFunction::getFunctionType,
                                    VarProcessFunction::getName,
                                    VarProcessFunction::getPrepObjectName,
                                    VarProcessFunction::getStatus,
                                    VarProcessFunction::getFunctionDataType
                            )
                            .eq(VarProcessFunction::getVarProcessSpaceId, variableDetail.getSpaceId())
                            .in(VarProcessFunction::getId, funcIds)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(functionList)) {
                functionList.forEach(v -> {
                    Content content = new Content();
                    String url = "/process/publicFunction/detail?type=read&spaceId=" + v.getVarProcessSpaceId() + "&functionId=" + v.getId() + "&componentType=publicFunction&functionType=commonFunction";
                    content.setUrl(url);
                    content.setValue(v.getName());
                    useVarList.add(content);
                });
                list.add(PanelDto.builder()
                        .title("引用的公共方法")
                        .type(LocalDataTypeEnum.cell.getCode())
                        .datas(useVarList)
                        .build());
            }
        }

        //数据模型
        List<Content> varList = Lists.newArrayList();
        List<VarProcessFunctionVar> variableVars = varProcessFunctionVarService.list(
                new QueryWrapper<VarProcessFunctionVar>().lambda()
                        .eq(VarProcessFunctionVar::getFunctionId, variableDetail.getId())
                        .eq(VarProcessFunctionVar::getIsSelf, 1)
        );
        if (!CollectionUtils.isEmpty(variableVars)) {
            variableVars.forEach(v -> {
                Content content = new Content();
                if (variableDetail.getFunctionType() == FunctionTypeEnum.PREP) {
                    String action = v.getActionHistory();
                    char[] charArray = action.toCharArray();
                    char a = getLabel(charArray);
                    content.setLabel(String.valueOf(a));
                } else {
                    content.setLabel(null);
                }
                content.setValue(v.getVarPath() + "_" + v.getVarName());
                varList.add(content);
            });
            list.add(PanelDto.builder()
                    .title("引用数据模型信息")
                    .type(LocalDataTypeEnum.cell.getCode())
                    .datas(varList)
                    .build());
        }


        return TabDto.builder().name("引用信息").content(list).build();
    }

    private char getLabel(char[] action) {
        boolean containsR = false;
        boolean containsW = false;

        for (char c : action) {
            if (c == 'r') {
                containsR = true;
            } else if (c == 'w') {
                containsW = true;
            }
        }

        if (containsR && containsW) {
            return 'r';
        } else {
            return action[0];
        }
    }
}
