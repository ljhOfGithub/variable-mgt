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
import com.wiseco.boot.commons.BeanCopyUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.enums.VarUpdateActionEnum;
import com.wiseco.var.process.app.server.controller.vo.input.VariableBatchSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableBatchUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableBatchUpdateOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Gmm
 * @since 2023/10/12
 */
@Service
@Slf4j
public class VariableBatchBiz {

    @Autowired
    private VariableBiz variableBiz;

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Autowired
    private VarProcessVariableTagService varProcessVariableTagService;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * batch saveVariable
     *
     * @param inputDto inputDto
     * @return VariableBatchUpdateOutputDto
     */
    public VariableBatchUpdateOutputDto batchSaveVariable(VariableBatchSaveInputDto inputDto) {

        List<String> errorMessageList = new ArrayList<>();
        List<String> warnMessageList = new ArrayList<>();
        if (CollectionUtils.isEmpty(inputDto.getVariableIdList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "变量ID列表为空.");
        }

        VarUpdateActionEnum updateAction = inputDto.getUpdateAction();
        switch (updateAction) {
            case IMPORT:
            case EXPORT:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "暂未支持此操作类型：" + updateAction);
            case TAGS:
                actTags(inputDto, errorMessageList);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不合法的更新操作类型：" + updateAction);
        }

        // 响应
        return VariableBatchUpdateOutputDto.builder()
                .errorMessageList(errorMessageList)
                .state(errorMessageList.isEmpty())
                .warnMessageList(warnMessageList)
                .build();

    }

    /**
     * 批量打标操作
     *
     * @param inputDto         inputDto
     * @param errorMessageList errorMessageList
     */
    private void actTags(VariableBatchSaveInputDto inputDto, List<String> errorMessageList) {
        List<Long> variableIdList = inputDto.getVariableIdList();
        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            // 批量移除标签
            varProcessVariableTagService.remove(
                    new QueryWrapper<VarProcessVariableTag>().lambda()
                            .eq(VarProcessVariableTag::getVarProcessSpaceId, inputDto.getSpaceId())
                            .in(VarProcessVariableTag::getVariableId, variableIdList)
            );
            // 批量新增保存标签
            if (!CollectionUtils.isEmpty(inputDto.getTags())) {
                List<VariableTagDto> tags = inputDto.getTags();
                List<VarProcessVariableTag> tagList = new ArrayList<>();
                for (Long variableId : variableIdList) {
                    for (VariableTagDto tagDto : tags) {
                        tagList.add(VarProcessVariableTag.builder().varProcessSpaceId(inputDto.getSpaceId()).variableId(variableId).tagGroupId(tagDto.getGroupId()).tagName(tagDto.getTagName())
                                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()
                        );
                    }
                }
                varProcessVariableTagService.saveBatch(tagList);
            }
            // 批量保存动态
            variableIdList.forEach(varId -> variableBiz.saveDynamic(inputDto.getSpaceId(), varId, SysDynamicOperateTypeEnum.EDIT.getName(), ""));
            // 手动提交事务
            platformTransactionManager.commit(transaction);
        } catch (Exception e) {
            platformTransactionManager.rollback(transaction);
            log.error("批量编辑保存变量-操作执行异常：{}", e);
            errorMessageList.add("操作执行异常：" + e.getMessage());
        }
    }

    /**
     * 批量修改状态
     *
     * @param inputDto 输入实体类对象
     * @return 变量编译验证返回DTO
     */
    public VariableBatchUpdateOutputDto batchUpdateStatus(VariableBatchUpdateStatusInputDto inputDto) {

        // 设置子线程共享
        SecurityContext securityContext = SecurityContextHolder.getContext();
        ServletRequestAttributes sra = (ServletRequestAttributes) (RequestContextHolder.getRequestAttributes());

        // 获取所有变量ID和名称映射
        Map<Long, String> varMap = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getId, VarProcessVariable::getLabel)
                        .eq(VarProcessVariable::getVarProcessSpaceId, inputDto.getSpaceId())
        ).stream().collect(Collectors.toMap(VarProcessVariable::getId, VarProcessVariable::getLabel));


        List<CompletableFuture<VariableCompileOutputDto>> futures = new ArrayList<>();

        for (Long variableId : inputDto.getVariableIdList()) {
            VariableUpdateStatusInputDto single = (VariableUpdateStatusInputDto) BeanCopyUtils.copy(inputDto, VariableUpdateStatusInputDto.class);
            single.setVariableId(variableId);
            CompletableFuture<VariableCompileOutputDto> future = variableBiz.singleUpdateStatus(single, securityContext, sra);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        List<VariableCompileOutputDto> execResult = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        List<String> errorMessageList = new ArrayList<>();
        List<String> warnMessageList = new ArrayList<>();
        execResult.forEach(e -> {
            if (!e.isState()) {
                String varName = varMap.getOrDefault(e.getVariableId(), String.valueOf(e.getVariableId()));
                String varErrorMsg = String.format("变量[%s]批量操作失败：%s", varName, e.getErrorMessageList().get(0));
                errorMessageList.add(varErrorMsg);
                if (e.getWarnMessageList() != null && !e.getWarnMessageList().isEmpty()) {
                    String varWarnMsg = String.format("变量[%s]批量操作失败：%s", varName, e.getWarnMessageList().get(0));
                    warnMessageList.add(varWarnMsg);
                }
            }
        });

        log.info("所有batchUpdateStatus执行完毕...");
        return VariableBatchUpdateOutputDto.builder()
                .errorMessageList(errorMessageList)
                .state(errorMessageList.isEmpty())
                .warnMessageList(warnMessageList)
                .build();
    }

}
