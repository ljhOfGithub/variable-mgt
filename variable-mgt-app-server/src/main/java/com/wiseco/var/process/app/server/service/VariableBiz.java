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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.java.common.ComponentKeyConstant;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.compiler.vo.var.VarEntryDataVo;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.controller.vo.StreamProcessContentInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCacheContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableContentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessPeriodEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VariableActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableCache;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableSaveSub;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableScene;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import com.wiseco.var.process.app.server.service.dto.FunctionContentDto;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VariableBiz {
    private static final String IS_AUDIT_PARAM_CODE = "var_review";
    @Autowired
    VarProcessParamService varProcessParamService;
    @Autowired
    VarProcessContentService varProcessContentService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessVariableLifecycleService varProcessVariableLifecycleService;
    @Autowired
    private VarProcessVariableCacheService varProcessVariableCacheService;
    @Autowired
    private VarProcessVariableSaveSubService varProcessVariableSaveSubService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private SysDynamicServiceBiz sysDynamicServiceBiz;
    @Autowired
    private VariableCompileBiz variableCompileBiz;
    @Autowired
    private UserComponetCodebaseRecordService userComponetCodebaseRecordService;
    @Autowired
    private VarProcessVariableTagService varProcessVariableTagService;
    @Autowired
    private VarProcessConfigTagService varProcessConfigTagService;
    @Autowired
    private VarProcessConfigDefaultService varProcessConfigDefaultValueService;
    @Autowired
    private VariableConfigDefaultValueBiz variableConfigDefaultValueBiz;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;
    @Autowired
    private VariableVarBiz variableVarBiz;
    @Autowired
    private VariableRefBiz variableRefBiz;
    @Autowired
    private VarProcessManifestService varProcessManifestService;
    @Autowired
    private VarProcessVariableFunctionService varProcessVariableFunctionService;
    @Autowired
    private VarProcessVariableReferenceService           varProcessVariableReferenceService;
    @Autowired
    private VarCompileVarRefBiz varCompileVarRefBiz;
    @Autowired
    private VarProcessVariableSceneService varProcessVariableSceneService;
    /**
     * saveVariable
     * @param inputDto inputDto
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveVariable(VariableSaveInputDto inputDto) {
        //验证
        checkSaveVariable(inputDto);
        //保存数据
        VarProcessVariable varProcessVariable = VarProcessVariable.builder().varProcessSpaceId(inputDto.getSpaceId()).name(inputDto.getName()).label(inputDto.getLabel())
                .categoryId(inputDto.getCategoryId()).updatedTime(new Date()).updatedUser(SessionContext.getSessionUser().getUsername()).processingMethod(inputDto.getProcessingMethod()).processType(inputDto.getProcessType())
                .functionId(inputDto.getFunctionId()).dataType(inputDto.getDataType()).description(inputDto.getDescription()).build();
        if (inputDto.getContent() != null) {
            varProcessVariable.setContent(inputDto.getContent().toJSONString());
        }
        String operateType = SysDynamicOperateTypeEnum.ADD.getName();
        if (inputDto.getId() != null && inputDto.getId() > 0) {
            //修改
            update(inputDto, varProcessVariable);
            operateType = SysDynamicOperateTypeEnum.EDIT.getName();
        } else {
            //新增
            insert(inputDto, varProcessVariable);
        }
        //保存临时记录
        varProcessVariableSaveSubService.save(VarProcessVariableSaveSub.builder().variableId(varProcessVariable.getId()).content(JSONObject.toJSONString(inputDto))
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
        );
        //保存标签
        if (!CollectionUtils.isEmpty(inputDto.getTags())) {
            List<VariableTagDto> tags = inputDto.getTags();
            List<VarProcessVariableTag> tagList = new ArrayList<>();
            for (VariableTagDto tagDto : tags) {
                tagList.add(VarProcessVariableTag.builder().varProcessSpaceId(inputDto.getSpaceId()).variableId(varProcessVariable.getId()).tagGroupId(tagDto.getGroupId()).tagName(tagDto.getTagName())
                        .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
                );
            }
            varProcessVariableTagService.saveBatch(tagList);
        }

        // 流式变量
        if (ProcessingMethodEnum.STREAM == inputDto.getProcessingMethod()) {
            saveVariableScene(inputDto, varProcessVariable);
        }

        //保存动态
        saveDynamic(inputDto.getSpaceId(), varProcessVariable.getId(), operateType, "");
        //更新代码块使用次数
        if (ObjectUtils.isNotEmpty(inputDto.getUserCodeBlockId())) {
            userComponetCodebaseRecordService.updateUseTimes(inputDto.getUserCodeBlockId());
        }
        return varProcessVariable.getId();
    }

    private void saveVariableScene(VariableSaveInputDto inputDto, VarProcessVariable varProcessVariable) {

        if (inputDto.getId() == null && !inputDto.getIsFileImport()) {
            return;
        }

        //移除旧的
        varProcessVariableSceneService.remove(Wrappers.<VarProcessVariableScene>lambdaUpdate().eq(VarProcessVariableScene::getVariableId, varProcessVariable.getId()));

        StreamProcessContentInputVO streamContent = inputDto.getStreamProcessContent();
        VarProcessVariableScene variableScene = new VarProcessVariableScene();
        variableScene.setVariableId(varProcessVariable.getId());
        BeanUtils.copyProperties(streamContent,variableScene);
        if (StreamProcessPeriodEnum.PREVIOUS == streamContent.getCalculatePeriod().getPeriodDescriptor()) {
            variableScene.setCurrentIncluded(false);
        }
        variableScene.setCalculatePeriod(JSON.toJSONString(streamContent.getCalculatePeriod()));
        variableScene.setFilterConditionInfo(JSON.toJSONString(streamContent.getFilterConditionInfo()));
        varProcessVariableSceneService.save(variableScene);
    }

    private void insert(VariableSaveInputDto inputDto, VarProcessVariable varProcessVariable) {
        List<VarProcessVariable> identifierList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getId)
                        .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessVariable::getIdentifier, inputDto.getIdentifier())
        );
        //如果identifier重复或者为空则新建 否则就保留
        if (!CollectionUtils.isEmpty(identifierList) || inputDto.getIdentifier() == null ||  inputDto.getIdentifier().isEmpty()) {
            String identifier = GenerateIdUtil.generateId();
            varProcessVariable.setIdentifier(identifier);
        } else {
            varProcessVariable.setIdentifier(inputDto.getIdentifier());
        }
        varProcessVariable.setCreatedUser(SessionContext.getSessionUser().getUsername());
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            varProcessVariable.setDeptCode(department.getCode());
        }
        //新增版本为1
        varProcessVariable.setVersion(NumberUtils.INTEGER_ONE);
        varProcessVariableService.save(varProcessVariable);
        //生命周期
        varProcessVariableLifecycleService.save(
                VarProcessVariableLifecycle.builder().variableId(varProcessVariable.getId()).actionType(VariableActionTypeEnum.ADD.getCode()).status(VariableActionTypeEnum.ADD.getStatusEnum())
                        .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
        );
    }

    private void update(VariableSaveInputDto inputDto, VarProcessVariable varProcessVariable) {
        varProcessVariable.setId(inputDto.getId());
        // 使用词条
        varProcessVariable.setProcessType(inputDto.getProcessType());
        if (ProcessTypeEnum.ENTRY == varProcessVariable.getProcessType()) {
            varProcessVariable.setFunctionId(inputDto.getFunctionId());
            // 词条变化校验
            if (entryTemplateChangedCheck(varProcessVariable.getContent())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_CHANGED, "变量模版已被修改，无法保存.");
            }
        }
        varProcessVariableService.updateById(varProcessVariable);
        //移除标签
        varProcessVariableTagService.remove(
                new QueryWrapper<VarProcessVariableTag>().lambda()
                        .eq(VarProcessVariableTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessVariableTag::getVariableId, inputDto.getId())
        );
    }

    private void checkSaveVariable(VariableSaveInputDto inputDto) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (varProcessSpace == null) {
            log.error("变量空间不存在,spaceId:{}", inputDto.getSpaceId());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "变量空间不存在");
        }
        if (inputDto.getId() != null && inputDto.getId() > 0) {
            VarProcessVariable variableEntity = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getIdentifier)
                    .eq(VarProcessVariable::getId, inputDto.getId()));
            //检查变量名是否重复
            List<VarProcessVariable> nameList = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda().select(VarProcessVariable::getId)
                            .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariable::getName, inputDto.getName())
                            .ne(VarProcessVariable::getIdentifier, variableEntity.getIdentifier())
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(nameList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量编码已存在，不允许重复");
            }
            //检查变量中文名是否重复
            List<VarProcessVariable> labelList = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda()
                            .select(VarProcessVariable::getId)
                            .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariable::getLabel, inputDto.getLabel())
                            .ne(VarProcessVariable::getIdentifier, variableEntity.getIdentifier())
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(labelList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量名称已存在，不允许重复");
            }
        } else {
            //检查变量名是否重复
            List<VarProcessVariable> list = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda()
                            .select(VarProcessVariable::getId)
                            .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariable::getName, inputDto.getName())
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量编码已存在，不允许重复");
            }
            //检查变量中文名是否重复
            List<VarProcessVariable> labelList = varProcessVariableService.list(
                    new QueryWrapper<VarProcessVariable>().lambda()
                            .select(VarProcessVariable::getId)
                            .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessVariable::getLabel, inputDto.getLabel())
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(labelList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量名称已存在，不允许重复");
            }
        }
    }

    /**
     * 变量模版批量生成变量，单笔插入
     *
     * @param spaceId           空间id，现系统固定为1
     * @param functionId        变量模版id
     * @param varName           变量名（对应生成变量原型里的变量编码code）
     * @param varLabel          变量中文名（对应生成变量原型里的变量名称name）
     * @param categoryId        变量类别（对应生成变量原型里的变量分类variableType）
     * @param userInputValueMap 用户输入的入参信息map 入参下标位置（如：1/2/3）,入参参数值（如：D1/R1）
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDefaultVariable(Long spaceId, Long functionId, String varName, String varLabel, Long categoryId,
                                   Map<String, String> userInputValueMap) {
        VarProcessFunction function = varProcessFunctionService.getById(functionId);
        //保存数据
        VarProcessVariable varProcessVariable = new VarProcessVariable();
        varProcessVariable.setVarProcessSpaceId(spaceId);
        varProcessVariable.setName(varName);
        varProcessVariable.setLabel(varLabel);
        varProcessVariable.setCategoryId(categoryId);
        varProcessVariable.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessVariable.setDataType(function.getFunctionDataType());
        varProcessVariable.setDescription("");
        // 根据变量模版和用户输入的参数信息，生成变量的content
        varProcessVariable.setContent(varProcessContentService.buildEntireEntryContent(varProcessVariable.getName(),
                varProcessVariable.getDataType(), function.getFunctionEntryContent(), userInputValueMap).toJSONString());
        varProcessVariable.setIdentifier(GenerateIdUtil.generateId());
        varProcessVariable.setCreatedUser(SessionContext.getSessionUser().getUsername());
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            varProcessVariable.setDeptCode(department.getCode());
        }
        //新增版本为1
        varProcessVariable.setVersion(NumberUtils.INTEGER_ONE);
        // 变量类型，默认词条
        varProcessVariable.setProcessType(ProcessTypeEnum.ENTRY);
        varProcessVariable.setFunctionId(functionId);
        varProcessVariableService.save(varProcessVariable);
        //生命周期
        varProcessVariableLifecycleService.save(VarProcessVariableLifecycle.builder().variableId(varProcessVariable.getId())
                .actionType(VariableActionTypeEnum.ADD.getCode()).status(VariableActionTypeEnum.ADD.getStatusEnum())
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        //保存动态
        saveDynamic(spaceId, varProcessVariable.getId(), SysDynamicOperateTypeEnum.ADD.getName(), "");
    }

    /**
     * upgradeVersionVariable
     * @param spaceId spaceId
     * @param variableId variableId
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long upgradeVersionVariable(Long spaceId, Long variableId) {
        VarProcessVariable variableEntity = varProcessVariableService.getById(variableId);
        if (variableEntity.getStatus().equals(VariableStatusEnum.EDIT)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "变量状态非上架或下架，不允许升级版本。");
        }
        VarProcessVariable newVariable = new VarProcessVariable();
        BeanUtils.copyProperties(variableEntity, newVariable);
        newVariable.setId(null);
        newVariable.setCreatedTime(null);
        newVariable.setUpdatedTime(null);
        newVariable.setCreatedUser(SessionContext.getSessionUser().getUsername());
        newVariable.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            newVariable.setDeptCode(department.getCode());
        }
        newVariable.setStatus(VariableStatusEnum.EDIT);
        newVariable.setParentId(variableEntity.getId());
        //获取当前编号的最大版本
        Integer newVersion = getMaxVersion(variableEntity.getVarProcessSpaceId(), variableEntity.getIdentifier()) + 1;
        newVariable.setVersion(newVersion);
        // 加工方式
        newVariable.setProcessingMethod(variableEntity.getProcessingMethod());
        newVariable.setProcessType(variableEntity.getProcessType());
        newVariable.setFunctionId(variableEntity.getFunctionId());
        varProcessVariableService.save(newVariable);
        //生命周期
        varProcessVariableLifecycleService.save(
                VarProcessVariableLifecycle.builder().variableId(newVariable.getId()).actionType(VariableActionTypeEnum.ADD.getCode()).status(VariableActionTypeEnum.ADD.getStatusEnum())
                        .description("").createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        //保存标签
        List<VarProcessVariableTag> orginList = varProcessVariableTagService.list(new QueryWrapper<VarProcessVariableTag>().lambda()
                .eq(VarProcessVariableTag::getVarProcessSpaceId, spaceId).eq(VarProcessVariableTag::getVariableId, variableId)
        );
        List<VariableTagDto> tags = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orginList)) {
            List<VarProcessVariableTag> tagList = new ArrayList<>();
            for (VarProcessVariableTag tagDto : orginList) {
                tagList.add(VarProcessVariableTag.builder().varProcessSpaceId(spaceId).variableId(newVariable.getId()).tagGroupId(tagDto.getTagGroupId()).tagName(tagDto.getTagName())
                        .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());

                tags.add(VariableTagDto.builder().groupId(tagDto.getTagGroupId()).tagName(tagDto.getTagName()).build());
            }
            varProcessVariableTagService.saveBatch(tagList);
        }
        if (ProcessingMethodEnum.STREAM == variableEntity.getProcessingMethod()) {
            List<VarProcessVariableScene> variableScenes = varProcessVariableSceneService.list(Wrappers.<VarProcessVariableScene>lambdaQuery().eq(VarProcessVariableScene::getVariableId, variableEntity.getId()));
            variableScenes.forEach(item -> {
                item.setId(null);
                item.setVariableId(newVariable.getId());
            });
            varProcessVariableSceneService.saveBatch(variableScenes);
        }
        //保存临时记录
        VariableSaveInputDto saveContent = VariableSaveInputDto.builder().spaceId(newVariable.getVarProcessSpaceId()).id(newVariable.getId()).categoryId(newVariable.getCategoryId())
                .dataType(newVariable.getDataType()).name(newVariable.getName()).label(newVariable.getLabel()).description(newVariable.getDescription()).tags(tags)
                .content(JSONObject.parseObject(newVariable.getContent())).processType(newVariable.getProcessType()).functionId(newVariable.getFunctionId()).build();

        varProcessVariableSaveSubService.save(VarProcessVariableSaveSub.builder().variableId(newVariable.getId()).content(JSONObject.toJSONString(saveContent))
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        //保存动态
        saveDynamic(spaceId, newVariable.getId(), SysDynamicOperateTypeEnum.ADD.getName(), "");
        return newVariable.getId();
    }

    /**
     * copyVariable
     * @param inputDto inputDto
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long copyVariable(VariableCopyInputDto inputDto) {
        VarProcessVariable originalVariable = varProcessVariableService.getById(inputDto.getCopyId());
        //检查变量名是否重复
        List<VarProcessVariable> list = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda()
                .select(VarProcessVariable::getId)
                .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                .eq(VarProcessVariable::getName, inputDto.getName())
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量名已存在，不允许重复");
        }
        //检查变量名是否重复
        List<VarProcessVariable> labelList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getId)
                        .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessVariable::getLabel, inputDto.getLabel())
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        if (!CollectionUtils.isEmpty(labelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_EXISTS, "变量中文名已存在，不允许重复");
        }
        VarProcessVariable varProcessVariable = VarProcessVariable.builder().varProcessSpaceId(inputDto.getSpaceId()).name(inputDto.getName()).label(inputDto.getLabel())
                .categoryId(originalVariable.getCategoryId()).dataType(originalVariable.getDataType()).description(originalVariable.getDescription()).version(NumberUtils.INTEGER_ONE)
                .processType(originalVariable.getProcessType()).functionId(originalVariable.getFunctionId()).identifier(GenerateIdUtil.generateId()).processingMethod(originalVariable.getProcessingMethod())
                .updatedUser(SessionContext.getSessionUser().getUsername()).createdUser(SessionContext.getSessionUser().getUsername()).build();
        DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
        if (department != null) {
            varProcessVariable.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
        }
        if (originalVariable.getContent() != null) {
            varProcessVariable.setContent(originalVariable.getContent());
        }
        varProcessVariableService.save(varProcessVariable);
        //生命周期
        varProcessVariableLifecycleService.save(
                VarProcessVariableLifecycle.builder().variableId(varProcessVariable.getId()).actionType(VariableActionTypeEnum.ADD.getCode())
                        .status(VariableActionTypeEnum.ADD.getStatusEnum()).description("").createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername()).build()
        );
        //保存标签
        List<VarProcessVariableTag> orginList = varProcessVariableTagService.list(
                new QueryWrapper<VarProcessVariableTag>().lambda().eq(VarProcessVariableTag::getVarProcessSpaceId, originalVariable.getVarProcessSpaceId())
                        .eq(VarProcessVariableTag::getVariableId, originalVariable.getId())
        );
        List<VariableTagDto> tags = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orginList)) {
            List<VarProcessVariableTag> tagList = new ArrayList<>();
            for (VarProcessVariableTag tagDto : orginList) {
                tagList.add(VarProcessVariableTag.builder().varProcessSpaceId(originalVariable.getVarProcessSpaceId()).variableId(varProcessVariable.getId()).tagGroupId(tagDto.getTagGroupId())
                        .tagName(tagDto.getTagName()).createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
                tags.add(VariableTagDto.builder().groupId(tagDto.getTagGroupId()).tagName(tagDto.getTagName()).build());
            }
            varProcessVariableTagService.saveBatch(tagList);
        }
        if (ProcessingMethodEnum.STREAM == originalVariable.getProcessingMethod()) {
            List<VarProcessVariableScene> variableScenes = varProcessVariableSceneService.list(Wrappers.<VarProcessVariableScene>lambdaQuery().eq(VarProcessVariableScene::getVariableId, originalVariable.getId()));
            variableScenes.forEach(item -> {
                item.setVariableId(varProcessVariable.getId());
                item.setId(null);
            });
            varProcessVariableSceneService.saveBatch(variableScenes);
        }
        //保存临时记录
        VariableSaveInputDto saveContent = VariableSaveInputDto.builder().spaceId(varProcessVariable.getVarProcessSpaceId()).id(varProcessVariable.getId())
                .categoryId(varProcessVariable.getCategoryId()).dataType(varProcessVariable.getDataType()).tags(tags).name(varProcessVariable.getName())
                .label(varProcessVariable.getLabel()).description(varProcessVariable.getDescription()).content(JSONObject.parseObject(varProcessVariable.getContent()))
                .processType(varProcessVariable.getProcessType()).functionId(varProcessVariable.getFunctionId()).build();
        varProcessVariableSaveSubService.save(VarProcessVariableSaveSub.builder().variableId(varProcessVariable.getId()).content(JSONObject.toJSONString(saveContent))
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        //保存动态
        saveDynamic(inputDto.getSpaceId(), varProcessVariable.getId(), SysDynamicOperateTypeEnum.COPY.getName(), originalVariable.getName() + "V" + originalVariable.getVersion());
        return varProcessVariable.getId();
    }

    /**
     * checkVariable
     * @param inputDto inputDto
     * @return VariableCompileOutputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableCompileOutputDto checkVariable(VariableContentInputDto inputDto) {
        VarProcessVariable variable = varProcessVariableService.getById(inputDto.getVariableId());
        String content;
        if (inputDto.getContent() == null || inputDto.getContent().size() == 0) {
            content = variable.getContent();
        } else {
            content = inputDto.getContent().toJSONString();
        }
        if (content == null || JSON.parseObject(content).isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_INVALID_CONTENT, "变量逻辑不能为空");
        }
        if (entryTemplateChangedCheck(content)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_CHANGED, "变量模版已被修改，无法验证.");
        }
        return variableCompileBiz.validate(TestVariableTypeEnum.VAR, inputDto.getSpaceId(), inputDto.getVariableId(), content);
    }

    /**
     * updateStatus
     * @param inputDto inputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(VariableUpdateStatusInputDto inputDto) {
        VarProcessVariable variable = varProcessVariableService.getById(inputDto.getVariableId());
        //参数校验
        if (ObjectUtils.isEmpty(variable)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, "参数错误");
        }
        //区分用户操作类型
        Integer actionType = inputDto.getActionType();
        //操作枚举
        VariableActionTypeEnum statusEnum = VariableActionTypeEnum.getStatus(actionType);
        //校验是否允许下架
        if (actionType.equals(VariableActionTypeEnum.DOWN.getCode())) {
            // 下架前校验
            validateVarDisable(variable);
        } else if (actionType.equals(VariableActionTypeEnum.SUMMIT.getCode())) {
            if (!variableConfigDefaultValueBiz.checkExist(VariableConfigDefaultValueCheckInputDto.builder()
                    .varProcessSpaceId(inputDto.getSpaceId()).dataType(variable.getDataType()).build())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_DEFAULT_VALUE_NOT_CONFIG, "该变量数据类型对应的缺失值定义没有设置，无法提交");
            }
            if (entryTemplateChangedCheck(variable.getContent())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_CHANGED, "变量模版已被修改，无法提交.");
            }
            //申请上架，保存使用到的数据模型属性和变量模型之间的关系
            VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.VAR, inputDto.getSpaceId(), inputDto.getVariableId(), variable.getContent());
            // 上架前校验
            validateApplyUp(variable, compile);
            String classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.VAR, inputDto.getSpaceId(), inputDto.getVariableId(), variable.getContent());
            // 保存关联的业务数据
            saveVariableRef(inputDto.getSpaceId(), inputDto.getVariableId(), compile.getCompileResultVo(), classData);
            //变量审核按钮关闭，直接启用
            if (!varProcessParamService.getParamStatus(IS_AUDIT_PARAM_CODE)) {
                //审核通过，上架：清除临时记录
                varProcessVariableSaveSubService.remove(
                        new QueryWrapper<VarProcessVariableSaveSub>().lambda().eq(VarProcessVariableSaveSub::getVariableId, inputDto.getVariableId())
                );
                statusEnum = VariableActionTypeEnum.RE_ENABLE;
            }
        } else if (actionType.equals(VariableActionTypeEnum.APPROVED.getCode())) {
            //审核通过，上架：清除临时记录
            varProcessVariableSaveSubService.remove(
                    new QueryWrapper<VarProcessVariableSaveSub>().lambda().eq(VarProcessVariableSaveSub::getVariableId, inputDto.getVariableId())
            );
        }
        //更新变量数据
        VarProcessVariable variableSaveDto = new VarProcessVariable();
        variableSaveDto.setId(variable.getId());
        variableSaveDto.setStatus(statusEnum.getStatusEnum());
        variableSaveDto.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        variableSaveDto.setUpdatedTime(new Date());
        varProcessVariableService.updateById(variableSaveDto);
        // 确定生命周期内容为 "描述" 或 "审批意见"
        String lifecycleDescription = inputDto.getDescription();
        if (VariableActionTypeEnum.APPROVED == statusEnum || VariableActionTypeEnum.REFUSE == statusEnum) {
            // 操作为 "审核通过" 或 "审核拒绝": 记录审批意见
            lifecycleDescription = inputDto.getApproDescription();
        }
        //生命周期
        varProcessVariableLifecycleService.save(
                VarProcessVariableLifecycle.builder()
                        .variableId(inputDto.getVariableId())
                        .actionType(actionType)
                        .status(statusEnum.getStatusEnum())
                        .description(lifecycleDescription)
                        .createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername())
                        .build()
        );
        //动态记录类型
        String operate = SysDynamicOperateNameEnum.getActionTypeEnum(actionType).actionName;
        //保存动态
        saveDynamic(inputDto.getSpaceId(), variable.getId(), operate, "");
    }

    /**
     * 更新单个状态
     * @param inputDto 输入实体类对象
     * @param securityContext 安全上下文环境对象
     * @param sra ServletRequestAttributes对象
     * @return 异步任务(变量编译)结果
     */
    @Async
    public CompletableFuture<VariableCompileOutputDto> singleUpdateStatus(VariableUpdateStatusInputDto inputDto, SecurityContext securityContext, ServletRequestAttributes sra) {
        log.info("批量更新变量状态作业开始: {}", inputDto);
        SecurityContextHolder.setContext(securityContext);
        RequestContextHolder.setRequestAttributes(sra);

        VariableCompileOutputDto result = new VariableCompileOutputDto();
        result.setVariableId(inputDto.getVariableId());
        try {
            if (VariableActionTypeEnum.DELETE.getCode().intValue() == inputDto.getActionType().intValue()) {
                // 删除操作
                deleteVariable(inputDto.getSpaceId(), inputDto.getVariableId());
            } else if (VariableActionTypeEnum.SUMMIT.getCode().intValue() == inputDto.getActionType().intValue()) {
                // 提交操作
                checkVariable(VariableContentInputDto.builder().spaceId(inputDto.getSpaceId()).variableId(inputDto.getVariableId()).build());
                updateStatus(inputDto);
            } else {
                // 其他更新操作
                updateStatus(inputDto);
            }
            result.setState(true);
        } catch (Exception e) {
            log.info("批量更新变量状态作业失败: {}, e: {}", inputDto, e);
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            result.setErrorMessageList(errors);
            result.setState(false);
        }
        RequestContextHolder.resetRequestAttributes();
        log.info("批量更新变量状态作业结束: {}, result: {}", inputDto, result);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 修改状态校验
     *
     * @param inputDto inputDto
     * @return String
     */
    public String validateUpdateStatus(VariableUpdateStatusInputDto inputDto) {
        String result;
        VarProcessVariable variable = varProcessVariableService.getById(inputDto.getVariableId());
        //参数校验
        if (ObjectUtils.isEmpty(variable)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, "未查询到变量相关信息");
        }
        //操作枚举
        VariableActionTypeEnum actionTypeEnum = VariableActionTypeEnum.getStatus(inputDto.getActionType());
        switch (actionTypeEnum) {
            case SUMMIT:
                if (!variableConfigDefaultValueBiz.checkExist(VariableConfigDefaultValueCheckInputDto.builder()
                        .varProcessSpaceId(inputDto.getSpaceId()).dataType(variable.getDataType()).build())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_DEFAULT_VALUE_NOT_CONFIG, "该变量数据类型对应的缺失值定义没有设置，无法提交");
                }
                if (entryTemplateChangedCheck(variable.getContent())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_CHANGED, "变量模版已被修改，无法提交");
                }
                //申请上架，保存使用到的数据模型属性和变量模型之间的关系
                VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.VAR, inputDto.getSpaceId(),
                        inputDto.getVariableId(), variable.getContent());
                // 上架前校验
                validateApplyUp(variable, compile);
                result = "确认提交？";
                break;
            case DELETE:
                List<VarProcessManifestVariable> list = varProcessManifestVariableService.getManifestUseVariableList(variable.getVarProcessSpaceId(),
                        variable.getId());
                if (!CollectionUtils.isEmpty(list)) {
                    result = "该变量版本已经被变量清单使用，确认删除？";
                } else {
                    result = "确认删除？";
                }
                break;
            case RE_ENABLE:
                variableRenableCheck(variable);
                result = "确认启用？";
                break;
            case APPROVED:
                result = "确认审核通过？";
                break;
            case REFUSE:
                result = "确认审核拒绝？";
                break;
            case DOWN:
                validateVarDisable(variable);
                result = "确认停用？";
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "不支持的变量操作类型");
        }
        return result;
    }

    /**
     * variableRenableCheck
     * @param variable variable
     */
    private void variableRenableCheck(VarProcessVariable variable) {
        List<Long> functionIds = varProcessVariableFunctionService.list(Wrappers.<VarProcessVariableFunction>lambdaQuery()
                        .select(VarProcessVariableFunction::getFunctionId)
                        .eq(VarProcessVariableFunction::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                        .eq(VarProcessVariableFunction::getVariableId, variable.getId())).stream()
                .map(VarProcessVariableFunction::getFunctionId)
                .distinct()
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(functionIds)) {
            List<VarProcessFunction> functionList = varProcessFunctionService.list(Wrappers.<VarProcessFunction>lambdaQuery()
                    .select(VarProcessFunction::getId, VarProcessFunction::getDeleteFlag,VarProcessFunction::getStatus,VarProcessFunction::getName)
                    .in(VarProcessFunction::getId, functionIds)
                    .eq(VarProcessFunction::getFunctionType, FunctionTypeEnum.TEMPLATE));
            for (VarProcessFunction varProcessFunction : functionList) {
                if (varProcessFunction.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "引用的变量模板【" + varProcessFunction.getName() + "】不存在");
                }
                if (!varProcessFunction.getStatus().equals(FlowStatusEnum.UP)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "引用的变量模板【" + varProcessFunction.getName() + "】未启用");
                }
            }
        }

        //校验引用的变量
        List<Long> usingVariableIds = varProcessVariableReferenceService.list(Wrappers.<VarProcessVariableReference>lambdaQuery()
                        .select(VarProcessVariableReference::getVariableId)
                        .eq(VarProcessVariableReference::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                        .eq(VarProcessVariableReference::getUseByVariableId, variable.getId()))
                .stream().map(VarProcessVariableReference::getVariableId).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(usingVariableIds)) {
            List<VarProcessVariable> variableList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getDeleteFlag, VarProcessVariable::getStatus, VarProcessVariable::getName)
                    .in(VarProcessVariable::getId, usingVariableIds)
                    .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

            for (VarProcessVariable item : variableList) {
                if (item.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, "引用的变量【" + item.getName() + "】不存在");
                }
                if (!item.getStatus().equals(VariableStatusEnum.UP)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, "引用的变量【" + item.getName() + "】未启用");
                }
            }
        }
    }

    /**
     * cacheContent
     * @param inputDto inputDto
     * @return String  String
     */
    @Transactional(rollbackFor = Exception.class)
    public String cacheContent(VariableCacheContentInputDto inputDto) {
        if (StringUtils.isEmpty(inputDto.getSessionId())) {
            inputDto.setSessionId(GenerateIdUtil.generateId());
            varProcessVariableCacheService.save(VarProcessVariableCache.builder()
                    .variableId(inputDto.getVariableId())
                    .sessionId(inputDto.getSessionId())
                    .content(inputDto.getContent().toJSONString())
                    .build());
        } else {
            varProcessVariableCacheService.update(new UpdateWrapper<VarProcessVariableCache>().lambda()
                    .eq(VarProcessVariableCache::getSessionId, inputDto.getSessionId())
                    .set(VarProcessVariableCache::getContent, inputDto.getContent().toJSONString()));
        }
        return inputDto.getSessionId();
    }

    /**
     * restoreVersion
     * @param inputDto inputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void restoreVersion(VariableDetailQueryInputDto inputDto) {
        //将临时记录添加为最新的记录
        VarProcessVariableSaveSub saveContent = varProcessVariableSaveSubService.getById(inputDto.getContentId());
        //更新变量数据
        VariableSaveInputDto variableSaveInputDto = JSONObject.parseObject(saveContent.getContent(), VariableSaveInputDto.class);
        VarProcessVariable variableSaveDto = new VarProcessVariable();
        variableSaveDto.setId(inputDto.getVariableId());
        variableSaveDto.setName(variableSaveInputDto.getName());
        variableSaveDto.setLabel(variableSaveInputDto.getLabel());
        variableSaveDto.setCategoryId(variableSaveInputDto.getCategoryId());
        variableSaveDto.setContent(JSONObject.toJSONString(variableSaveInputDto.getContent()));
        variableSaveDto.setDescription(variableSaveInputDto.getDescription());
        variableSaveDto.setDataType(variableSaveInputDto.getDataType());
        variableSaveDto.setProcessType(variableSaveInputDto.getProcessType());
        variableSaveDto.setFunctionId(variableSaveInputDto.getFunctionId());
        variableSaveDto.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessVariableService.updateById(variableSaveDto);
        //移除标签
        varProcessVariableTagService.remove(
                new QueryWrapper<VarProcessVariableTag>().lambda()
                        .eq(VarProcessVariableTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessVariableTag::getVariableId, inputDto.getVariableId())
        );
        //保存标签
        if (!CollectionUtils.isEmpty(variableSaveInputDto.getTags())) {
            List<String> tagNames = variableSaveInputDto.getTags().stream().map(VariableTagDto::getTagName).collect(Collectors.toList());

            List<VarProcessConfigTag> tagList = varProcessConfigTagService.list(
                    new QueryWrapper<VarProcessConfigTag>().lambda()
                            .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                            .in(VarProcessConfigTag::getName, tagNames)
            );
            if (!CollectionUtils.isEmpty(tagList)) {
                List<VarProcessVariableTag> tags = new ArrayList<>();
                for (VarProcessConfigTag tagDto : tagList) {
                    tags.add(
                            VarProcessVariableTag.builder().varProcessSpaceId(inputDto.getSpaceId()).variableId(inputDto.getVariableId()).tagGroupId(tagDto.getGroupId()).tagName(tagDto.getName())
                                    .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
                    );
                }
                varProcessVariableTagService.saveBatch(tags);
            }
        }
        //保存临时记录
        varProcessVariableSaveSubService.save(
                VarProcessVariableSaveSub.builder().variableId(inputDto.getVariableId()).content(saveContent.getContent())
                        .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
        );
    }

    /**
     * deleteVariable
     * @param spaceId spaceId
     * @param variableId variableId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVariable(Long spaceId, Long variableId) {
        //更新为删除
        VarProcessVariable variableSaveDto = new VarProcessVariable();
        variableSaveDto.setId(variableId);
        variableSaveDto.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        variableSaveDto.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessVariableService.updateById(variableSaveDto);
        //删除和公共方法的映射关系
        varProcessVariableFunctionService.remove(
                new LambdaQueryWrapper<VarProcessVariableFunction>().eq(VarProcessVariableFunction::getVariableId, variableId));
        //删除和其他变量的映射关系
        varProcessVariableReferenceService.remove(
                new LambdaQueryWrapper<VarProcessVariableReference>().eq(VarProcessVariableReference::getUseByVariableId, variableId));
        //删除和场景引用关系
        varProcessVariableSceneService.remove(
                new LambdaQueryWrapper<VarProcessVariableScene>().eq(VarProcessVariableScene::getVariableId, variableId));
        //保存动态
        saveDynamic(spaceId, variableId, SysDynamicOperateTypeEnum.DELETE.getName(), "");
    }

    /**
     * processLogicCheck
     * @param variableId variableId
     * @return boolean
     */
    public boolean processLogicCheck(Long variableId) {
        VarProcessVariable variable = varProcessVariableService.getById(variableId);
        // 只校验词条情况下的变化
        if (ProcessTypeEnum.ENTRY != variable.getProcessType()) {
            return false;
        }
        return entryTemplateChangedCheck(variable.getContent());
    }

    /**
     * entryTemplateChangedCheck
     * @param varContent varContent
     * @return boolean
     */
    public boolean entryTemplateChangedCheck(String varContent) {
        JSONObject contentJson = JSONObject.parseObject(varContent);
        // 获取变量的旧词条信息，它的参数下标从1开始
        JSONObject entryBodyJson = contentJson.getJSONObject(ComponentKeyConstant.specificDataKey).getJSONObject(ComponentKeyConstant.bodyKey);
        VarEntryDataVo varEntryDataVo = JSONObject.parseObject(entryBodyJson.toJSONString(), VarEntryDataVo.class);
        if (!ProcessTypeEnum.ENTRY.name().equalsIgnoreCase(varEntryDataVo.getProcessType())) {
            return false;
        }
        List<VarEntryDataVo.paramPartMeta> oldSortedParams = varEntryDataVo.getParts().stream()
                .filter(p -> p.getParamIndex() != null && p.getParamIndex() > 0)
                .sorted(Comparator.comparing(VarEntryDataVo.paramPartMeta::getParamIndex))
                .collect(Collectors.toList());

        // 获取最新词条function信息，它的参数下标从0开始
        Long entryFunctionId = varEntryDataVo.getFunctionId();
        if (entryFunctionId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "使用的变量模版信息中未设置变量模版ID.");
        }
        VarProcessFunction entryFunction = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getContent, VarProcessFunction::getFunctionDataType)
                .eq(VarProcessFunction::getId, entryFunctionId));
        FunctionContentDto functionContentDto = JSONObject.parseObject(entryFunction.getContent(), FunctionContentDto.class);
        List<FunctionContentDto.LocalVar> newParameters = functionContentDto.getBaseData().getDataModel().getParameters();
        newParameters.sort(Comparator.comparingInt(FunctionContentDto.LocalVar::getIndex));
        // 检查变量的返回类型和使用的变量模版返回数据类型是否一致
        String varReturnType = contentJson.getJSONObject(ComponentKeyConstant.baseDataKey).getString(ComponentKeyConstant.dataTypeKey);
        if (!varReturnType.equalsIgnoreCase(entryFunction.getFunctionDataType())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "使用的变量模版数据返回类型与变量返回数据类型不一致.");
        }
        // 校验返回值类型是否发生变化
        if (!entryFunction.getFunctionDataType().equalsIgnoreCase(varEntryDataVo.getReturnType())) {
            return true;
        }
        // 校验参数个数是否发生变化
        if (newParameters.size() != oldSortedParams.size()) {
            return true;
        }
        // 校验参数位置和类型是否发生变化
        for (int i = 0; i < newParameters.size(); i++) {
            FunctionContentDto.LocalVar newParam = newParameters.get(i);
            VarEntryDataVo.paramPartMeta oldParam = oldSortedParams.get(i);
            if (!newParam.getType().equalsIgnoreCase(oldParam.getParamDataType())) {
                return true;
            }
        }
        return false;
    }

    private Integer getMaxVersion(Long spaceId, String identifier) {
        return varProcessVariableService.getMaxVersion(spaceId, identifier);
    }

    /**
     * 保存动态
     * @param spaceId spaceId
     * @param variableId variableId
     * @param operateType operateType
     * @param parentVariableName parentVariableName
     */
    public void saveDynamic(Long spaceId, Long variableId, String operateType, String parentVariableName) {
        //记录系统动态: 在[..]下 动作  + 类型 + ： + 名称
        VarProcessVariable entity = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                .select(VarProcessVariable::getName, VarProcessVariable::getVersion)
                .eq(VarProcessVariable::getId, variableId));
        String businessDesc = entity.getName() + "V" + entity.getVersion();
        if (!StringUtils.isEmpty(parentVariableName)) {
            businessDesc += " 复制于变量版本" + parentVariableName;
        }
        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().varSpaceId(spaceId).operateType(operateType)
                .typeEnum(SysDynamicBusinessBucketEnum.VARIABLE_ADMIN).businessId(variableId).businessDesc(businessDesc).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }


    /**
     * 保存变量相关业务信息
     * @param spaceId spaceId
     * @param variableId variableId
     * @param compileResultVo compileResultVo
     * @param classData classData
     */
    private void saveVariableRef(Long spaceId, Long variableId, VarCompileResult compileResultVo, String classData) {
        if (compileResultVo == null || compileResultVo.getSyntaxInfo() == null) {
            return;
        }
        //使用的变量
        variableVarBiz.saveVarClass(spaceId, variableId, compileResultVo, classData);


        //编译通过后，后端对比校验数据
        VarSyntaxInfo syntaxInfo = compileResultVo.getSyntaxInfo();
        List<VarProcessCompileVar> varProcessCompileVars = new ArrayList<>();
        Map<String, VarActionHistory> actionHistorys = new HashMap<>(MagicNumbers.INT_64);
        Set<String> allIdentifierList = new HashSet<>();
        if (syntaxInfo != null && !CollectionUtils.isEmpty(syntaxInfo.getCallInfo())) {
            varCompileVarRefBiz.analyzeComponentVar(spaceId, variableId, VarTypeEnum.VAR, syntaxInfo, varProcessCompileVars, actionHistorys, allIdentifierList);
        }
        //使用的组件变量列表（有序）
        variableRefBiz.saveCompileVar(spaceId, variableId, VarTypeEnum.VAR, varProcessCompileVars);
        //使用的变量
        variableVarBiz.saveVar(spaceId, variableId, actionHistorys);
        //变量嵌套
        variableRefBiz.saveVariableReference(spaceId, variableId, compileResultVo.getSyntaxInfo().getVarIdentifierSet());
        //变量引用变量模板
        variableRefBiz.saveVariableRefFunction(spaceId, variableId, compileResultVo.getSyntaxInfo().getVarFunctionIdentifierSet());
        //保存异常值
        variableRefBiz.saveVariableExceptionValue(spaceId, variableId, compileResultVo.getSyntaxInfo().getVarExceptionDefaultValueCodeSet());
    }

    /**
     * checkRuleVariable
     * @param functionId functionId
     * @param name name
     * @param identifier identifier
     * @return VarProcessVariable
     */
    public VarProcessVariable checkRuleVariable(Long functionId, String name, String identifier) {
        //通过变量名称跟变量模板id，是否存在
        return varProcessVariableService.checkRuleVariable(functionId, name, identifier);
    }

    /**
     * 校验名称重复性
     * @param names
     * @return name list
     */
    public List<String> checkNameRepeat(List<String> names) {
        return varProcessVariableService.checkNameRepeat(names);
    }

    /**
     * 校验编码重复性
     * @param codes
     * @return code list
     */
    public List<String> checkCodeRepeat(List<String> codes) {
        return varProcessVariableService.checkCodeRepeat(codes);
    }

    @Getter
    public enum SysDynamicOperateNameEnum {
        //      操作类型：2-上架，3-下架，4-下架，5-下架，6-审核通过，7-审核拒绝，8-退回编辑
        VAR_APPLY_UP(2, SysDynamicOperateTypeEnum.VAR_APPLY_UP.getName()), VAR_DOWN(3, SysDynamicOperateTypeEnum.VAR_DOWN.getName()), VAR_UP(
                4,
                SysDynamicOperateTypeEnum.VAR_UP
                        .getName()), APPROVED(
                6,
                SysDynamicOperateTypeEnum.APPROVED
                        .getName()), REFUSE(
                7,
                SysDynamicOperateTypeEnum.REFUSE
                        .getName()), RETURN_EDIT(
                8,
                SysDynamicOperateTypeEnum.RETURN_EDIT
                        .getName());
        private final Integer actionType;
        private final String actionName;
        SysDynamicOperateNameEnum(Integer actionType, String actionName) {
            this.actionType = actionType;
            this.actionName = actionName;
        }

        /**
         * getActionTypeEnum
         * @param actionType actionType
         * @return SysDynamicOperateNameEnum
         */
        public static SysDynamicOperateNameEnum getActionTypeEnum(Integer actionType) {
            for (SysDynamicOperateNameEnum sysDynamicOperateNameEnum : SysDynamicOperateNameEnum.values()) {
                if (sysDynamicOperateNameEnum.getActionType().equals(actionType)) {
                    return sysDynamicOperateNameEnum;
                }
            }
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "非法操作类型");
        }
    }

    private void validateApplyUp(VarProcessVariable variable, VariableCompileOutputDto variableCompileOutputDto) {
        // 编译错误
        if (!variableCompileOutputDto.isState()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, variableCompileOutputDto.getErrorMessageList().get(0));
        }
        // 变量是否已校验通过
        VariableCompileOutputDto validateRes = variableCompileBiz.validate(TestVariableTypeEnum.VAR,
                variable.getVarProcessSpaceId(), variable.getId(), variable.getContent());
        if (!validateRes.isState()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_COMPILE_VALIDATE_FAILED, "变量验证未通过");
        }
        // 变量返回值缺失值是否已配置
        List<VarProcessConfigDefault> varProcessConfigDefaultList = varProcessConfigDefaultValueService.list(new QueryWrapper<VarProcessConfigDefault>().lambda()
                .eq(VarProcessConfigDefault::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                .eq(VarProcessConfigDefault::getDataType, variable.getDataType()));
        if (CollectionUtils.isEmpty(varProcessConfigDefaultList) || varProcessConfigDefaultList.get(0).getDefaultValue() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, String.format("该变量对应的【%s】类型的缺失值定义未配置，无法启用", variable.getDataType()));
        }
        // 新增校验：引用的变量模版校验  从变量解析结果中获取引用的变量模版列表
        Set<String> functionIdentifierList = variableCompileOutputDto.getCompileResultVo().getSyntaxInfo().getVarFunctionIdentifierSet();
        List<String> codeList = new ArrayList<>(functionIdentifierList);
        if (CollectionUtils.isEmpty(codeList)) {
            return;
        }
        List<VarProcessFunction> functions = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda()
                .select(VarProcessFunction::getDeleteFlag, VarProcessFunction::getStatus, VarProcessFunction::getName)
                .in(VarProcessFunction::getIdentifier, codeList));
        for (VarProcessFunction function : functions) {
            if (function.getDeleteFlag().intValue() == DeleteFlagEnum.DELETED.getCode().intValue()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, String.format("该变量版本引用的变量模版【%s】已经被删除，无法启用", function.getName()));
            }
            if (function.getStatus() == FlowStatusEnum.DOWN) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, String.format("该变量版本引用的变量模版【%s】已经被停用，无法启用该变量", function.getName()));
            }
        }

    }

    private void validateVarDisable(VarProcessVariable variable) {
        // 被编辑中/停用状态的变量清单引用才能停用
        List<Long> manifestIds = varProcessManifestVariableService.list(Wrappers.<VarProcessManifestVariable>lambdaQuery()
                        .eq(VarProcessManifestVariable::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                        .eq(VarProcessManifestVariable::getVariableId, variable.getId())).stream()
                .map(VarProcessManifestVariable::getManifestId)
                .distinct()
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(manifestIds)) {
            List<VarProcessManifest> manifests = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getState)
                    .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .in(VarProcessManifest::getId, manifestIds));
            if (!CollectionUtils.isEmpty(manifests)) {
                for (VarProcessManifest item : manifests) {
                    if (VarProcessManifestStateEnum.EDIT != item.getState() && VarProcessManifestStateEnum.DOWN != item.getState()) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, "该变量版本已经被非编辑中/停用状态的变量清单使用，不允许停用");
                    }
                }
            }
        }
        //校验被其他变量引用
        List<Long> useByVariableIds = varProcessVariableReferenceService.list(Wrappers.<VarProcessVariableReference>lambdaQuery()
                        .select(VarProcessVariableReference::getUseByVariableId)
                        .eq(VarProcessVariableReference::getVarProcessSpaceId, variable.getVarProcessSpaceId())
                        .eq(VarProcessVariableReference::getVariableId, variable.getId()))
                .stream().map(VarProcessVariableReference::getUseByVariableId).distinct().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(useByVariableIds)) {
            List<VarProcessVariable> variableList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                    .select(VarProcessVariable::getStatus)
                    .in(VarProcessVariable::getId, useByVariableIds)
                    .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            List<VariableStatusEnum> statusList = Arrays.asList(VariableStatusEnum.EDIT, VariableStatusEnum.DOWN);
            for (VarProcessVariable item : variableList) {
                if (!statusList.contains(item.getStatus())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_STATUS_NO_MATCH, "该变量已被非编辑中/停用状态的变量引用，不允许停用");
                }
            }
        }
    }


}
