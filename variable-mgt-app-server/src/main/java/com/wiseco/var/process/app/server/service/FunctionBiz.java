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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wiseco.auth.common.UserDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.commons.util.biz.FunctionEntryUtil;
import com.wiseco.var.process.app.server.controller.vo.input.FlowUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCacheContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.FunctionDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionLifecycleMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionCache;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionSaveSub;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.service.datamodel.DataModelViewBiz;
import com.wiseco.var.process.app.server.service.dto.FunctionContentDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestFunctionService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangxianli
 */
@Slf4j
@Service
public class FunctionBiz {

    @Resource
    private VarProcessFunctionService varProcessFunctionService;

    @Resource
    private VarProcessVariableService varProcessVariableService;

    @Resource
    private VarProcessVariableFunctionService varProcessVariableFunctionService;

    @Resource
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;

    @Resource
    private VarProcessFunctionCacheService varProcessFunctionCacheService;

    @Resource
    private VarProcessFunctionSaveSubService varProcessFunctionSaveSubService;

    @Resource
    private SysDynamicServiceBiz sysDynamicServiceBiz;

    @Resource
    private FunctionContentBiz functionContentBiz;

    @Resource
    private VariableCompileBiz variableCompileBiz;

    @Autowired
    private FunctionVarBiz functionVarBiz;

    @Autowired
    private VariableRefBiz variableRefBiz;

    @Resource
    private CommonTemplateBiz commonTemplateBiz;

    @Resource
    private VarProcessFunctionVarService varProcessFunctionVarService;

    @Resource
    private VarProcessManifestFunctionService varProcessManifestFunctionService;

    @Resource
    private UserComponetCodebaseRecordService userComponetCodebaseRecordService;

    @Resource
    VarProcessFunctionLifecycleMapper varProcessFunctionLifecycleMapper;

    @Resource
    private VarProcessParamService varProcessParamService;

    @Resource
    private VarProcessDataModelService varprocessDataModelService;

    @Resource
    private DataModelViewBiz dataModelViewBiz;

    @Autowired
    private VarCompileVarRefBiz varCompileVarRefBiz;

    /**
     * saveFunction
     * @param inputDto 入参
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveFunction(FunctionSaveInputDto inputDto) {
        if (inputDto.getFunctionType() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "函数类型不能为空");
        }
        VarProcessFunction varProcessFunction = new VarProcessFunction();
        varProcessFunction.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessFunction.setName(inputDto.getName());
        varProcessFunction.setFunctionDataType(inputDto.getFunctionDataType().getDesc());
        varProcessFunction.setFunctionType(inputDto.getFunctionType());
        varProcessFunction.setPrepObjectName(inputDto.getPrepObjectName());
        varProcessFunction.setCategoryId(inputDto.getCategoryId());
        varProcessFunction.setHandleType(inputDto.getHandleType());
        if (inputDto.getContent() != null) {
            varProcessFunction.setContent(inputDto.getContent().toJSONString());
        }
        varProcessFunction.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessFunction.setDescription(inputDto.getDescription());
        varProcessFunction.setUpdatedTime(new Date());
        String operateType = SysDynamicOperateTypeEnum.ADD.getName();
        //修改
        if (inputDto.getId() != null && inputDto.getId() > 0) {
            updateFunctionOperation(inputDto, varProcessFunction);
            //操作类型
            operateType = SysDynamicOperateTypeEnum.EDIT.getName();
        } else {
            saveFunctionOperation(inputDto, varProcessFunction);
            // 保存生命周期
            VarProcessFunctionLifecycle lifecycle = new VarProcessFunctionLifecycle();
            lifecycle.setFunctionId(varProcessFunction.getId());
            lifecycle.setStatus(FlowStatusEnum.EDIT);
            lifecycle.setActionType(FlowActionTypeEnum.ADD);
            lifecycle.setDescription(null);
            lifecycle.setCreatedUser(SessionContext.getSessionUser().getUsername());
            lifecycle.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            lifecycle.setCreatedTime(new Date());
            varProcessFunctionLifecycleMapper.insert(lifecycle);
        }
        // 2.变量模版词条
        if (FunctionTypeEnum.TEMPLATE == inputDto.getFunctionType() && inputDto.getContent() != null && !inputDto.getContent().isEmpty()) {
            FunctionContentDto functionContentDto = JSONObject.parseObject(inputDto.getContent().toJSONString(), FunctionContentDto.class);
            List<FunctionContentDto.LocalVar> parameters = functionContentDto.getBaseData().getDataModel().getParameters();
            // 生成业务词条字符串 & 词条校验 & 生成词条content
            String functionEntry = FunctionEntryUtil.buildBusinessEntry(parameters, inputDto.getName());
            FunctionEntryUtil.functionEntryCheck(functionEntry, parameters);
            String functionEntryContent = FunctionEntryUtil.buildFunctionEntryContent(functionEntry, parameters, varProcessFunction.getId(), varProcessFunction.getIdentifier(), varProcessFunction.getFunctionDataType());
            varProcessFunction.setFunctionEntryContent(functionEntryContent);
            varProcessFunctionService.updateById(varProcessFunction);
        }
        // 3.保存临时记录
        inputDto.setFunctionEntry(varProcessFunction.getFunctionEntryContent());
        varProcessFunctionSaveSubService.save(VarProcessFunctionSaveSub.builder().functionId(varProcessFunction.getId()).content(JSONObject.toJSONString(inputDto)).createdUser(SessionContext.getSessionUser().getUsername()).build());
        // 4.保存动态
        saveDynamic(inputDto.getSpaceId(), varProcessFunction.getId(), operateType, "");
        // 5.更新代码块使用次数
        if (!ObjectUtils.isEmpty(inputDto.getUserCodeBlockId())) {
            userComponetCodebaseRecordService.updateUseTimes(inputDto.getUserCodeBlockId());
        }
        return varProcessFunction.getId();
    }

    /**
     * 更新公共函数操作
     * @param inputDto 输入实体
     * @param varProcessFunction 公共函数实体
     */
    private void updateFunctionOperation(FunctionSaveInputDto inputDto, VarProcessFunction varProcessFunction) {
        if (inputDto.getFunctionType() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "处理方式不能为空");
        }
        VarProcessFunction functionDetail = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery().select(VarProcessFunction::getIdentifier, VarProcessFunction::getStatus).eq(VarProcessFunction::getId, inputDto.getId()));
        //检查公共函数名是否重复
        List<VarProcessFunction> list = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda().select(VarProcessFunction::getId).eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessFunction::getFunctionType, inputDto.getFunctionType()).eq(VarProcessFunction::getName, inputDto.getName()).ne(VarProcessFunction::getId, inputDto.getId()).eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_EXISTS, "名称已存在，不允许重复");
        }
        // identifier校验
        String curIdentifier = StringUtils.isEmpty(inputDto.getIdentifier()) ? functionDetail.getIdentifier() : inputDto.getIdentifier();
        List<VarProcessFunction> sameIdentifierFunctions = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda().select(VarProcessFunction::getId, VarProcessFunction::getName).eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessFunction::getIdentifier, curIdentifier).eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).ne(VarProcessFunction::getId, inputDto.getId()));
        if (!CollectionUtils.isEmpty(sameIdentifierFunctions)) {
            List<String> sameIdentifierNames = sameIdentifierFunctions.stream().map(VarProcessFunction::getName).collect(Collectors.toList());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_EXISTS, String.format("已存在相同identifier(%s)的变量模版：%s", curIdentifier, String.join(",", sameIdentifierNames)));
        }
        //上架后编辑，调用编译校验
        if (functionDetail.getStatus() == FlowStatusEnum.UP) {
            VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.FUNCTION, inputDto.getSpaceId(), inputDto.getId(), varProcessFunction.getContent());
            if (!compile.isState()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, String.join(";", compile.getErrorMessageList()));
            }
            String classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.FUNCTION, inputDto.getSpaceId(), inputDto.getId(), varProcessFunction.getContent());
            //保存关联的业务数据
            saveFunctionRef(inputDto.getSpaceId(), inputDto.getId(), compile.getCompileResultVo(), classData);
            //获取函数模板
            JSONObject functionTemplate = commonTemplateBiz.buildFunctionTemplate(VarTemplateTypeEnum.COMMON_FUNCTION.getCode(), varProcessFunction.getName(), functionDetail.getIdentifier(), varProcessFunction.getContent(), DataTypeEnum.getEnum(varProcessFunction.getFunctionDataType()));
            varProcessFunction.setFunctionTemplateContent(functionTemplate.toJSONString());
        }
        varProcessFunction.setId(inputDto.getId());
        Optional.ofNullable(inputDto.getIdentifier()).ifPresent(varProcessFunction::setIdentifier);
        varProcessFunctionService.updateById(varProcessFunction);
    }

    /**
     * 保存公共函数操作
     *
     * @param inputDto           输入实体
     * @param varProcessFunction 公共函数实体
     */
    private void saveFunctionOperation(FunctionSaveInputDto inputDto, VarProcessFunction varProcessFunction) {
        //检查公共函数名是否重复
        List<VarProcessFunction> list = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda().select(VarProcessFunction::getId).eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessFunction::getFunctionType, inputDto.getFunctionType()).eq(VarProcessFunction::getName, inputDto.getName()).eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_EXISTS, "名称已存在，不允许重复");
        }
        List<VarProcessFunction> identifierList = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda().select(VarProcessFunction::getId).eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessFunction::getFunctionType, inputDto.getFunctionType()).eq(VarProcessFunction::getIdentifier, inputDto.getIdentifier()));
        //如果identifier重复或者为空则新建 否则就保留
        if (!CollectionUtils.isEmpty(identifierList) || inputDto.getIdentifier() == null ||  inputDto.getIdentifier().isEmpty()) {
            String identifier = GenerateIdUtil.generateId();
            varProcessFunction.setIdentifier(identifier);
        } else {
            varProcessFunction.setIdentifier(inputDto.getIdentifier());
        }

        varProcessFunction.setCreatedUser(SessionContext.getSessionUser().getUsername());
        final UserDTO user = SessionContext.getSessionUser().getUser();
        if (user != null && user.getDepartment() != null) {
            varProcessFunction.setCreatedDeptCode(user.getDepartment().getCode());
            varProcessFunction.setCreatedDept(user.getDepartment().getName());
        }
        varProcessFunctionService.save(varProcessFunction);
    }

    /**
     * copyFunction
     * @param inputDto 入参
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long copyFunction(FunctionCopyInputDto inputDto) {
        VarProcessFunction originalFunction = varProcessFunctionService.getById(inputDto.getCopyId());
        // 1.检查公共函数名是否重复
        List<VarProcessFunction> list = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(VarProcessFunction::getId)
                        .eq(VarProcessFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessFunction::getFunctionType, inputDto.getFunctionType())
                        .eq(VarProcessFunction::getName, inputDto.getName())
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_EXISTS, originalFunction.getFunctionType().getDesc() + "名称已存在，不允许重复");
        }
        // 2.构建新的entity
        VarProcessFunction varProcessFunction = new VarProcessFunction();
        varProcessFunction.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessFunction.setName(inputDto.getName());
        varProcessFunction.setFunctionDataType(originalFunction.getFunctionDataType());
        varProcessFunction.setFunctionType(originalFunction.getFunctionType());
        varProcessFunction.setPrepObjectName(originalFunction.getPrepObjectName());
        if (originalFunction.getContent() != null) {
            varProcessFunction.setContent(originalFunction.getContent());
        }
        varProcessFunction.setDescription(originalFunction.getDescription());
        varProcessFunction.setCategoryId(originalFunction.getCategoryId());
        varProcessFunction.setHandleType(originalFunction.getHandleType());
        String identifier = GenerateIdUtil.generateId();
        varProcessFunction.setIdentifier(identifier);
        varProcessFunction.setStatus(FlowStatusEnum.EDIT);
        varProcessFunction.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessFunction.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        final UserDTO user = SessionContext.getSessionUser().getUser();
        if (user != null && user.getDepartment() != null) {
            varProcessFunction.setCreatedDeptCode(user.getDepartment().getCode());
            varProcessFunction.setCreatedDept(user.getDepartment().getName());
        }
        // 3.词条处理
        if (!StringUtils.isEmpty(originalFunction.getFunctionEntryContent())) {
            JSONObject entryJson = JSONObject.parseObject(originalFunction.getFunctionEntryContent());
            entryJson.put("identifier", identifier);
            varProcessFunction.setFunctionEntryContent(entryJson.toJSONString());
        }
        varProcessFunctionService.save(varProcessFunction);
        // 4.保存临时记录
        FunctionSaveInputDto saveContent = FunctionSaveInputDto.builder()
                .spaceId(varProcessFunction.getVarProcessSpaceId())
                .id(varProcessFunction.getId())
                .functionDataType(DataTypeEnum.getEnum(varProcessFunction.getFunctionDataType()))
                .functionType(varProcessFunction.getFunctionType())
                .name(varProcessFunction.getName())
                .description(varProcessFunction.getDescription())
                .content(JSONObject.parseObject(varProcessFunction.getContent()))
                .functionEntry(varProcessFunction.getFunctionEntryContent())
                .build();
        varProcessFunctionSaveSubService.save(
                VarProcessFunctionSaveSub.builder()
                        .functionId(varProcessFunction.getId())
                        .content(JSONObject.toJSONString(saveContent))
                        .createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername())
                        .build()
        );
        // 5.保存动态
        saveDynamic(inputDto.getSpaceId(), varProcessFunction.getId(), SysDynamicOperateTypeEnum.COPY.getName(), originalFunction.getName());
        // 6.保存生命周期
        VarProcessFunctionLifecycle lifecycle = new VarProcessFunctionLifecycle();
        lifecycle.setFunctionId(varProcessFunction.getId());
        lifecycle.setStatus(FlowStatusEnum.EDIT);
        lifecycle.setActionType(FlowActionTypeEnum.ADD);
        lifecycle.setDescription(null);
        lifecycle.setCreatedUser(SessionContext.getSessionUser().getUsername());
        lifecycle.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        lifecycle.setCreatedTime(new Date());
        varProcessFunctionLifecycleMapper.insert(lifecycle);
        return varProcessFunction.getId();
    }

    /**
     * checkFunction
     * @param inputDto  入参
     * @return VariableCompileOutputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableCompileOutputDto checkFunction(FunctionContentInputDto inputDto) {

        VarProcessFunction variable = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery().select(VarProcessFunction::getContent).eq(VarProcessFunction::getId, inputDto.getFunctionId()));

        String content = null;
        final JSONObject contentObj = inputDto.getContent();
        if (contentObj == null || contentObj.size() == 0) {
            content = variable.getContent();
        } else {
            content = contentObj.toJSONString();
        }

        return variableCompileBiz.validate(TestVariableTypeEnum.FUNCTION, inputDto.getSpaceId(), inputDto.getFunctionId(), content);

    }

    /**
     * validateStatusUpdate
     *
     * @param inputDto 入参
     * @return String
     */
    public String validateStatusUpdate(FlowUpdateStatusInputDto inputDto) {

        VarProcessFunction function = varProcessFunctionService.getById(inputDto.getFunctionId());

        if (function == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "未查询到该公共函数");
        }

        String check = null;
        switch (inputDto.getActionType()) {
            case DOWN:
                if (FunctionTypeEnum.FUNCTION == function.getFunctionType()) {
                    check = checkFunction(function, inputDto.getActionType());
                } else if (FunctionTypeEnum.PREP == function.getFunctionType()) {
                    checkStatusDown(inputDto, function);
                    check = "确认停用？";
                }
                break;
            case UP:
                if (FunctionTypeEnum.FUNCTION == function.getFunctionType()) {
                    checkFunctionUp(function);
                } else if (FunctionTypeEnum.PREP == function.getFunctionType()) {
                    checkPreUp(function);
                }
                return "确认启用？";
            case DELETE:
                return deleteMessage(inputDto, function);
            default:
        }

        return check;
    }

    private void checkPreUp(VarProcessFunction function) {
        checkDataModel(function);
        checkFunctionUp(function);
    }

    /**
     * 校验数据模型
     * @param function 公共函数
     */
    public void checkDataModel(VarProcessFunction function) {
        List<VarProcessFunctionVar> vars = varProcessFunctionVarService.list(
                new QueryWrapper<VarProcessFunctionVar>().lambda()
                        .eq(VarProcessFunctionVar::getFunctionId, function.getId())
                        .eq(VarProcessFunctionVar::getIsSelf, 1)
        );
        if (vars.isEmpty()) {
            return;
        }
        List<VarProcessFunctionVar> dataModels = new ArrayList<>();
        for (VarProcessFunctionVar dataModel : vars) {
            char letter = 'w';
            if (dataModel.getActionHistory().contains(String.valueOf(letter))) {
                dataModels.add(dataModel);
            }
        }
        for (VarProcessFunctionVar dataModel : dataModels) {
            List<String> node = getDataModelTree(dataModel.getVarPath());
            if (node.get(0).equals("rawData")) {
                VarProcessDataModel fatherModel = varprocessDataModelService.findByDataModelName(node.get(MagicNumbers.ONE));
                if (fatherModel != null) {
                    Set<String> varPathMap = dataModelViewBiz.getUseVarList(fatherModel.getVarProcessSpaceId());
                    DomainDataModelTreeDto treeData = DomainModelTreeEntityUtils.transferDataModelTreeDto(fatherModel.getContent(), varPathMap);
                    List<DomainDataModelTreeDto> children = treeData.getChildren();
                    if (children.isEmpty() && fatherModel.getExtendPropertyNum() == 0) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_EXTEND, "赋值变量【" + node.get(MagicNumbers.ONE) + "】不是扩展数据");
                    }
                    for (int i = MagicNumbers.TWO; i < node.size(); i++) {
                        cycleChildren(children,node,i);
                        children = cycleChildren(children,node,i);
                    }

                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "赋值变量【" + node.get(MagicNumbers.ONE) + "】不存在");
                }
            }
        }
    }

    private List<String> getDataModelTree(String varPath) {
        // 获取除了小数点的单词
        String[] words = varPath.split("\\.");
        List<String> wordList = new ArrayList<>();
        for (String word : words) {
            wordList.add(word);
        }
        return wordList;
    }

    private List<DomainDataModelTreeDto> cycleChildren(List<DomainDataModelTreeDto> children, List<String> node, int i) {
        List<DomainDataModelTreeDto> childrenList = null;
        String index = node.get(i);
        Optional<DomainDataModelTreeDto> optionalChild = children.stream().filter(c -> c.getName().equals(index)).findFirst();
        if (optionalChild.isPresent()) {
            DomainDataModelTreeDto child = optionalChild.get();
            List<DomainDataModelTreeDto> cycleChildren = child.getChildren();
            if (cycleChildren != null && cycleChildren.size() > 0) {
                childrenList = cycleChildren;
            } else {
                if (i == node.size() - 1) {
                    if (MagicStrings.ZERO.equals(child.getIsExtend())) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_EXTEND, "赋值变量【" + index + "】不是扩展数据");
                    }
                    childrenList = null;
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "赋值变量【" + index + "】不存在");
                }
            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "赋值变量【" + index + "】不存在");
        }
        return childrenList;
    }



    private void checkFunctionUp(VarProcessFunction function) {
        List<VarProcessFunctionReference> variableFunctions = varProcessFunctionReferenceService.list(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getUseByFunctionId, function.getId())
        );
        List<Long> functionIds = variableFunctions.stream().map(VarProcessFunctionReference::getFunctionId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(functionIds)) {
            List<VarProcessFunction> functionList = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(VarProcessFunction::getDeleteFlag, VarProcessFunction::getStatus, VarProcessFunction::getName)
                            .in(VarProcessFunction::getId, functionIds)
            );

            for (VarProcessFunction varProcessFunction : functionList) {
                if (function.getFunctionType() == FunctionTypeEnum.PREP && !function.getContent().contains(varProcessFunction.getName())) {
                    return;
                }
                if (varProcessFunction.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "引用的公共方法【" + varProcessFunction.getName() + "】不存在");
                }
                if (varProcessFunction.getStatus() != FlowStatusEnum.UP) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "引用的公共方法【" + varProcessFunction.getName() + "】未启用");
                }
            }
        }
    }

    /**
     * 公共方法删除校验
     *
     * @param inputDto
     * @param function
     * @return java.lang.String
     */
    private String deleteMessage(FlowUpdateStatusInputDto inputDto, VarProcessFunction function) {
        if (function.getFunctionType() == FunctionTypeEnum.FUNCTION) {
            List<VarProcessFunctionReference> list = varProcessFunctionReferenceService.list(Wrappers.<VarProcessFunctionReference>lambdaQuery()
                    .eq(VarProcessFunctionReference::getVarProcessSpaceId, inputDto.getSpaceId())
                    .eq(VarProcessFunctionReference::getUseByFunctionId, inputDto.getFunctionId()));
            if (!CollectionUtils.isEmpty(list)) {
                return "该" + function.getFunctionType().getDesc() + "已经被使用，确认删除？";
            }
        }
        return "确认删除？";
    }

    /**
     * updateStatus
     * @param inputDto  入参
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(FlowUpdateStatusInputDto inputDto) {
        VarProcessFunction function = varProcessFunctionService.getById(inputDto.getFunctionId());
        switch (inputDto.getActionType()) {
            case DOWN:
                checkStatusDown(inputDto, function);
                break;
            case SUBMIT:
                checkStatusSubmit(inputDto, function);
                checkApprovedSystem(inputDto, function);
                break;
            case APPROVED:
                checkDescriptionLength(inputDto);
                checkStatusApproved(inputDto, function);
                //清除临时记录
                varProcessFunctionSaveSubService.remove(
                        new QueryWrapper<VarProcessFunctionSaveSub>().lambda().eq(VarProcessFunctionSaveSub::getFunctionId, inputDto.getFunctionId()));
                break;
            case UP:
                checkStatusSubmit(inputDto, function);
                //清除临时记录
                varProcessFunctionSaveSubService.remove(
                        new QueryWrapper<VarProcessFunctionSaveSub>().lambda().eq(VarProcessFunctionSaveSub::getFunctionId, inputDto.getFunctionId())
                );
                break;
            case DELETE:
                varProcessFunctionReferenceService.remove(
                        new QueryWrapper<VarProcessFunctionReference>().lambda().eq(VarProcessFunctionReference::getUseByFunctionId, inputDto.getFunctionId())
                );
                function.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
                break;
            case REFUSE:
                checkDescriptionLength(inputDto);
                break;
            case RETURN_EDIT:
            default:
        }

        checkApprovedSystem(inputDto, function);
        function.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        function.setUpdatedTime(new Date());
        varProcessFunctionService.updateById(function);

        //保存动态
        saveDynamic(inputDto.getSpaceId(), function.getId(), inputDto.getActionType().getDesc(), "");

        //生命周期
        varProcessFunctionLifecycleMapper.insert(
                VarProcessFunctionLifecycle.builder()
                        .functionId(inputDto.getFunctionId())
                        .actionType(inputDto.getActionType())
                        .status(inputDto.getActionType().getNextStatus())
                        .description(inputDto.getDescription())
                        .createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername())
                        .build()
        );
    }

    private void checkApprovedSystem(FlowUpdateStatusInputDto inputDto, VarProcessFunction function) {
        if (inputDto.getActionType().equals(FlowActionTypeEnum.SUBMIT)) {
            VarProcessFunction functionEntity = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery().select(VarProcessFunction::getFunctionType).eq(VarProcessFunction::getId, inputDto.getFunctionId()));
            if (functionEntity == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "未找到公共函数对象");
            }
            FunctionTypeEnum functionType = functionEntity.getFunctionType();
            Boolean approveSystem;
            if (functionType.equals(FunctionTypeEnum.PREP)) {
                approveSystem = varProcessParamService.getParamStatus("process_logic_review");
            } else if (functionType.equals(FunctionTypeEnum.FUNCTION)) {
                approveSystem = varProcessParamService.getParamStatus("public_method_review");
            } else {
                approveSystem = varProcessParamService.getParamStatus("var_template_review");
            }
            if (approveSystem) {
                function.setStatus(inputDto.getActionType().getNextStatus());
            } else {
                function.setStatus(FlowActionTypeEnum.UP.getNextStatus());
            }
        } else {
            function.setStatus(inputDto.getActionType().getNextStatus());
        }

    }

    private void checkDescriptionLength(FlowUpdateStatusInputDto inputDto) {
        if (inputDto.getDescription().length() > MagicNumbers.INT_500) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "输入字符不得超过500个字符");
        }
    }

    /**
     * 预处理删除逻辑校验
     * @param inputDto 入参
     * @return String
     */
    @Transactional(rollbackFor = Exception.class)
    public String checkPreDelete(FlowUpdateStatusInputDto inputDto) {
        VarProcessFunction function = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getStatus, VarProcessFunction::getIdentifier)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));
        String message = null;
        switch (function.getStatus()) {
            case EDIT:
                message = "确认删除？";
                break;
            case DOWN:
                message = checkManifestUse(function);
                break;
            default:
        }
        return message;
    }

    private String checkManifestUse(VarProcessFunction function) {
        List<VarProcessManifest> manifestList = varProcessManifestFunctionService.getFunctionListByIdentifier(function.getIdentifier());
        String message = null;
        if (!CollectionUtils.isEmpty(manifestList)) {
            message = "该预处理逻辑已经被变量清单使用，确认删除？";
        } else {
           message = "确认删除？";

        }
        return message;
    }


    /**
     * 删除变量模板校验
     * @param inputDto 入参
     * @return String
     */
    public String checkDeleteVariableTemplate(FlowUpdateStatusInputDto inputDto) {
        VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getStatus)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));
        if (varProcessFunction.getStatus() == FlowStatusEnum.DOWN || varProcessFunction.getStatus() == FlowStatusEnum.EDIT) {
            //查询被变量使用的模板
            List<VarProcessVariableFunction> variableFunctions = varProcessVariableFunctionService.list(
                    new QueryWrapper<VarProcessVariableFunction>().lambda()
                            .eq(VarProcessVariableFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariableFunction::getFunctionId, inputDto.getFunctionId())
            );
            if (CollectionUtils.isEmpty(variableFunctions)) {
                return "确认删除？";
            } else {
                return "该变量模版已经被变量使用，确认删除？";
            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该变量模板尚未停用，不可删除!");
        }
    }

    private void checkStatusApproved(FlowUpdateStatusInputDto inputDto, VarProcessFunction function) {
        //清除临时记录
        varProcessFunctionSaveSubService.remove(new QueryWrapper<VarProcessFunctionSaveSub>().lambda().eq(VarProcessFunctionSaveSub::getFunctionId, inputDto.getFunctionId()));
    }

    private void checkStatusSubmit(FlowUpdateStatusInputDto inputDto, VarProcessFunction function) {
        //申请上架
        VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.FUNCTION, inputDto.getSpaceId(), inputDto.getFunctionId(),
                function.getContent());
        log.info("编译结果：{}", compile);

        if (!compile.isState()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, compile.getErrorMessageList().get(0));
        }
        String classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.FUNCTION, inputDto.getSpaceId(), inputDto.getFunctionId(),
                function.getContent());

        //保存关联的业务数据
        saveFunctionRef(inputDto.getSpaceId(), inputDto.getFunctionId(), compile.getCompileResultVo(), classData);

        //获取函数模板
        JSONObject functionTemplate = commonTemplateBiz.buildFunctionTemplate(VarTemplateTypeEnum.COMMON_FUNCTION.getCode(), function.getName(),
                function.getIdentifier(), function.getContent(), DataTypeEnum.getEnum(function.getFunctionDataType()));
        function.setFunctionTemplateContent(functionTemplate.toJSONString());
    }

    private void checkStatusDown(FlowUpdateStatusInputDto inputDto, VarProcessFunction function) {
        //被变量使用
        List<VarProcessVariableFunction> variableFunctionList = varProcessVariableFunctionService.list(
                new QueryWrapper<VarProcessVariableFunction>().lambda()
                        .eq(VarProcessVariableFunction::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessVariableFunction::getFunctionId, inputDto.getFunctionId())
        );
        if (!CollectionUtils.isEmpty(variableFunctionList)) {
            List<Long> varIds = variableFunctionList.stream().map(VarProcessVariableFunction::getVariableId).collect(Collectors.toList());
            List<VarProcessVariable> list = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda()
                            .select(VarProcessVariable::getId)
                            .in(VarProcessVariable::getId, varIds)
                            .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该" + function.getFunctionType().getDesc() + "已经被启用状态的变量使用，不允许停用");
            }
        }

        // 被变量清单使用
        List<VarProcessManifest> manifestList = varProcessManifestFunctionService.getFunctionListByIdentifier(function.getIdentifier());
        if (!CollectionUtils.isEmpty(manifestList)) {
            boolean hasValidState = manifestList.stream()
                    .anyMatch(item -> item.getState() == VarProcessManifestStateEnum.UP
                            || item.getState() == VarProcessManifestStateEnum.UNAPPROVED
                            || item.getState() == VarProcessManifestStateEnum.REFUSE);
            if (hasValidState) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "已被非编辑中/停用状态的变量清单引用，不允许停用");
            }
        }

        // 被变量模版/预处理逻辑/公共方法使用
        final List<VarProcessFunctionReference> functionReferenceList = varProcessFunctionReferenceService.list(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getVarProcessSpaceId, function.getVarProcessSpaceId())
                        .eq(VarProcessFunctionReference::getFunctionId, function.getId())
        );
        final List<Long> longList = functionReferenceList.stream().map(VarProcessFunctionReference::getUseByFunctionId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(longList)) {
            final List<VarProcessFunction> list = varProcessFunctionService.list(
                    new QueryWrapper<VarProcessFunction>().lambda()
                            .select(VarProcessFunction::getStatus)
                            .in(VarProcessFunction::getId, longList)
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(list)) {
                for (VarProcessFunction usedFunction : list) {
                    if (FlowStatusEnum.UP.equals(usedFunction.getStatus())) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该" + function.getFunctionType().getDesc() + "已经被启用状态的变量模版/预处理逻辑/公共方法使用，不允许停用");
                    }
                }
            }
        }

    }

    /**
     * compareContent
     * @param inputDto 入参
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean compareContent(FunctionSaveInputDto inputDto) {

        FunctionDetailQueryInputDto queryInputDto = new FunctionDetailQueryInputDto();
        queryInputDto.setSpaceId(inputDto.getSpaceId());
        queryInputDto.setFunctionId(inputDto.getId());
        FunctionDetailOutputDto variableDetail = functionContentBiz.functionDetail(queryInputDto);

        String cDesc = StringUtils.isEmpty(inputDto.getDescription()) ? "" : inputDto.getDescription();
        String sDesc = StringUtils.isEmpty(variableDetail.getDescription()) ? "" : variableDetail.getDescription();

        if (!variableDetail.getName().equals(inputDto.getName()) || !variableDetail.getFunctionType().equals(inputDto.getFunctionType())
                || !sDesc.equals(cDesc)) {
            return Boolean.FALSE;
        }

        String currentContent = "";
        if (null != inputDto.getContent()) {
            currentContent = inputDto.getContent().toJSONString();
        }

        JsonParser parser = new JsonParser();
        //把str1解析成json对象
        JsonObject obj = (JsonObject) parser.parse(currentContent);
        JsonParser parser1 = new JsonParser();
        //把str2解析成json对象
        JsonObject obj1 = new JsonObject();
        if (!StringUtils.isEmpty(variableDetail.getContent())) {
            obj1 = (JsonObject) parser1.parse(variableDetail.getContent().toJSONString());
        }

        if (obj.equals(obj1)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * cacheContent
     *
     * @param inputDto 入参
     * @return String
     */
    @Transactional(rollbackFor = Exception.class)
    public String cacheContent(FunctionCacheContentInputDto inputDto) {
        if (StringUtils.isEmpty(inputDto.getSessionId())) {
            inputDto.setSessionId(GenerateIdUtil.generateId());

            varProcessFunctionCacheService.save(VarProcessFunctionCache.builder()
                    .functionId(inputDto.getFunctionId())
                    .sessionId(inputDto.getSessionId())
                    .content(inputDto.getContent().toJSONString())
                    .build());
        } else {
            varProcessFunctionCacheService.update(new UpdateWrapper<VarProcessFunctionCache>().lambda()
                    .eq(VarProcessFunctionCache::getSessionId, inputDto.getSessionId())
                    .set(VarProcessFunctionCache::getContent, inputDto.getContent().toJSONString())
                    .set(VarProcessFunctionCache::getUpdatedTime, new Date()));
        }

        return inputDto.getSessionId();
    }

    /**
     * restoreVersion
     * @param inputDto 入参
     */
    @Transactional(rollbackFor = Exception.class)
    public void restoreVersion(FunctionDetailQueryInputDto inputDto) {

        //将临时记录添加为最新的记录
        VarProcessFunctionSaveSub saveContent = varProcessFunctionSaveSubService.getById(inputDto.getContentId());

        //更新变量数据
        FunctionSaveInputDto variableSaveInputDto = JSON.parseObject(saveContent.getContent(), FunctionSaveInputDto.class);

        VarProcessFunction variableSaveDto = new VarProcessFunction();
        variableSaveDto.setId(inputDto.getFunctionId());
        variableSaveDto.setName(variableSaveInputDto.getName());
        variableSaveDto.setFunctionDataType(variableSaveInputDto.getFunctionDataType().getDesc());
        variableSaveDto.setFunctionType(variableSaveInputDto.getFunctionType());
        variableSaveDto.setContent(JSON.toJSONString(variableSaveInputDto.getContent()));
        variableSaveDto.setDescription(variableSaveInputDto.getDescription() == null ? "" : variableSaveInputDto.getDescription());

        variableSaveDto.setCategoryId(variableSaveInputDto.getCategoryId());
        variableSaveDto.setHandleType(variableSaveInputDto.getHandleType());
        variableSaveDto.setFunctionEntryContent(variableSaveInputDto.getFunctionEntry());
        variableSaveDto.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        variableSaveDto.setUpdatedTime(new Date());
        varProcessFunctionService.updateById(variableSaveDto);

        varProcessFunctionSaveSubService.save(VarProcessFunctionSaveSub.builder().functionId(inputDto.getFunctionId())
                .content(saveContent.getContent()).createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername()).build());

    }

    /**
     * validatedFunction
     * @param inputDto 入参
     */
    public void validatedFunction(FlowUpdateStatusInputDto inputDto) {
        VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getId, VarProcessFunction::getVarProcessSpaceId, VarProcessFunction::getFunctionType, VarProcessFunction::getIdentifier)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));

        FlowActionTypeEnum typeEnum = inputDto.getActionType();
        if (typeEnum == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "操作类型入参错误");
        }
        if (FlowActionTypeEnum.DOWN.equals(typeEnum) || FlowActionTypeEnum.DELETE.equals(typeEnum)) {
            // 下架, 删除操作
            checkFunction(varProcessFunction, typeEnum);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING, "是否确认" + typeEnum.getDesc() + "？");
        }
    }

    /**
     * 校验公共函数操作
     *
     * @param varProcessFunction 公共函数实体类
     * @param typeEnum           函数操作类型枚举类
     * @return String            校验结果
     * @throws VariableMgtBusinessServiceException 睿信自定义异常
     */
    private String checkFunction(VarProcessFunction varProcessFunction, FlowActionTypeEnum typeEnum) throws VariableMgtBusinessServiceException {
        if (varProcessFunction.getFunctionType() == FunctionTypeEnum.PREP) {
            //预处理逻辑
            Set<Integer> manifestUsePrepFunction = getManifestStatusUsePrepFunction(varProcessFunction);
            //删除
            if (typeEnum.equals(FlowActionTypeEnum.DELETE)) {
                if (!CollectionUtils.isEmpty(manifestUsePrepFunction)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该预处理逻辑已被变量清单使用，不允许" + typeEnum.getDesc() + "。");
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING, "是否确认删除？");
                }
            } else {
                if (!manifestUsePrepFunction.contains(VarProcessManifestStateEnum.EDIT.getCode())
                        && !manifestUsePrepFunction.contains(VarProcessManifestStateEnum.REFUSE.getCode())
                        && !manifestUsePrepFunction.contains(VarProcessManifestStateEnum.DOWN.getCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该预处理逻辑已被实时服务接口使用，不允许" + typeEnum.getDesc() + "。");
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING, "该预处理逻辑已被“编辑中/审核拒绝/停用”的实时服务接口使用，确认" + typeEnum.getDesc() + "？");

                }
            }

        } else if (varProcessFunction.getFunctionType().equals(FunctionTypeEnum.TEMPLATE) && typeEnum.equals(FlowActionTypeEnum.DELETE)) {
            //变量模板
            boolean isUse = isUse(varProcessFunction.getVarProcessSpaceId(), varProcessFunction.getId());
            if (isUse) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING, "该变量模版已经被变量使用，确认删除？");
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_WARNING, "确认删除？");
            }

        } else if (varProcessFunction.getFunctionType() == FunctionTypeEnum.FUNCTION) {
            //公共方法
            Set<VarProcessFunction> referencingFunction = getReferencingFunction(varProcessFunction.getVarProcessSpaceId(), varProcessFunction.getId());
            if (!CollectionUtils.isEmpty(referencingFunction)) {
                Set<VarProcessFunction> inoperatableStatus = referencingFunction.stream().filter(item -> item.getStatus() != FlowStatusEnum.DOWN && item.getStatus() != FlowStatusEnum.EDIT).collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(inoperatableStatus)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "该公共方法已被“非编辑中/停用状态”状态的" + new ArrayList<>(inoperatableStatus).get(0).getFunctionType().getDesc() + "使用，不允许" + typeEnum.getDesc() + "。");
                }
                return "该公共方法已被“编辑中/停用”状态的数据预处理/变量模版/公共方法/变量清单使用，确认" + typeEnum.getDesc() + "？";
            }
        }

        return "确认" + typeEnum.getDesc() + "?";
    }

    /**
     * 变量使用变量模板对应的变量状态
     *
     * @param spaceId
     * @param functionId
     * @return true:被使用 false:未被使用
     */
    private boolean isUse(Long spaceId, Long functionId) {
        List<VarProcessVariableFunction> variableFunctions = varProcessVariableFunctionService.list(
                new QueryWrapper<VarProcessVariableFunction>().lambda()
                        .eq(VarProcessVariableFunction::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessVariableFunction::getFunctionId, functionId)
        );
        return !CollectionUtils.isEmpty(variableFunctions);
    }

    /**
     * 获取引用该公共方法的公共函数
     *
     * @param spaceId
     * @param functionId
     * @return java.util.Set<com.wiseco.var.process.app.server.repository.entity.VarProcessFunction>
     */
    private Set<VarProcessFunction> getReferencingFunction(Long spaceId, Long functionId) {

        List<VarProcessFunctionReference> variableFunctions = varProcessFunctionReferenceService.list(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessFunctionReference::getFunctionId, functionId)
        );
        if (CollectionUtils.isEmpty(variableFunctions)) {
            return new HashSet<>();
        }
        List<Long> varIds = variableFunctions.stream().map(VarProcessFunctionReference::getUseByFunctionId).collect(Collectors.toList());
        List<VarProcessFunction> variableList = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .in(VarProcessFunction::getId, varIds)
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );

        if (CollectionUtils.isEmpty(variableList)) {
            return new HashSet<>();
        }

        return new HashSet<>(variableList);
    }

    /**
     * 查询预处理对象被实时服务接口使用的服务接口状态
     *
     * @param varProcessFunction
     * @return Set<Integer>
     */
    private Set<Integer> getManifestStatusUsePrepFunction(VarProcessFunction varProcessFunction) {

        List<VarProcessManifest> manifestMappingObject = varProcessManifestFunctionService.getFunctionListByIdentifier(varProcessFunction.getIdentifier());
        if (CollectionUtils.isEmpty(manifestMappingObject)) {
            return new HashSet<>();
        }
        return manifestMappingObject.stream().map(item -> item.getState().getCode()).collect(Collectors.toSet());

    }

    private void saveDynamic(Long spaceId, Long functionId, String operateType, String parentFunctionName) {

        //记录系统动态: 在[..]下 动作  + 类型 + ： + 名称
        VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getName, VarProcessFunction::getFunctionType)
                .eq(VarProcessFunction::getId, functionId));

        String businessDesc = varProcessFunction.getName();
        if (!StringUtils.isEmpty(parentFunctionName)) {
            String desc = varProcessFunction.getFunctionType().getDesc();
            businessDesc += " 复制于" + desc + parentFunctionName;
        }

        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().varSpaceId(spaceId).operateType(operateType)
                .typeEnum(SysDynamicBusinessBucketEnum.VARIABLE_FUNCTION).businessId(functionId).businessDesc(businessDesc).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }

    private void saveFunctionRef(Long spaceId, Long functionId, VarCompileResult compileResultVo, String classData) {

        if (compileResultVo == null || compileResultVo.getSyntaxInfo() == null) {
            return;
        }

        //class内容
        functionVarBiz.saveVarClass(spaceId, functionId, compileResultVo, classData);


        //编译通过后，后端对比校验数据
        VarSyntaxInfo syntaxInfo = compileResultVo.getSyntaxInfo();
        List<VarProcessCompileVar> varProcessCompileVars = new ArrayList<>();
        Map<String, VarActionHistory> actionHistorys = new HashMap<>(MagicNumbers.INT_64);
        Set<String> allIdentifierList = new HashSet<>();
        if (syntaxInfo != null && !CollectionUtils.isEmpty(syntaxInfo.getCallInfo())) {
            varCompileVarRefBiz.analyzeComponentVar(spaceId, functionId, VarTypeEnum.FUNCTION, syntaxInfo, varProcessCompileVars, actionHistorys, allIdentifierList);
        }
        //使用的变量
        functionVarBiz.saveVar(spaceId, functionId, actionHistorys);

        //使用的组件变量列表（有序）
        variableRefBiz.saveCompileVar(spaceId, functionId, VarTypeEnum.FUNCTION, varProcessCompileVars);

        //公共函数间引用
        variableRefBiz.saveFunctionRef(spaceId, functionId, compileResultVo.getSyntaxInfo().getVarFunctionIdentifierSet());

        //保存异常值
        variableRefBiz.saveFunctionExceptionValue(spaceId, functionId, compileResultVo.getSyntaxInfo().getVarExceptionDefaultValueCodeSet());

    }
}
