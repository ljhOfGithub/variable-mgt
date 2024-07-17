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
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.FlowUpdateStatusInputDto;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class VarProcessTemplateCheckBiz {

    @Resource
    private VarProcessFunctionService varProcessFunctionService;

    @Resource
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;

    @Resource
    private VarProcessVariableFunctionService varProcessVariableFunctionService;

    @Resource
    private VarProcessVariableService varProcessVariableService;

    /**
     * validateStatusUpdate
     *
     * @param inputDto 入参
     * @return String
     */
    public String validateStatusUpdate(FlowUpdateStatusInputDto inputDto) {

        VarProcessFunction function = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getId, VarProcessFunction::getFunctionType, VarProcessFunction::getContent)
                .eq(VarProcessFunction::getId, inputDto.getFunctionId()));

        if (function == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "未查询到该公共函数");
        }

        String check = null;
        switch (inputDto.getActionType()) {
            case DOWN:
                checkDown(inputDto);
                check = "确认停用？";
                break;
            case UP:
                checkUp(function);
                check = "确认启用？";
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "操作有误!");
        }
        return check;
    }


    private void checkUp(VarProcessFunction function) {
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
                if (function.getFunctionType() == FunctionTypeEnum.TEMPLATE && !function.getContent().contains(varProcessFunction.getName())) {
                    return;
                }
                if (varProcessFunction.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "引用的公共方法【" + varProcessFunction.getName() + "】不存在");
                }
                if (varProcessFunction.getStatus() != FlowStatusEnum.UP) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_NOT_FOUND, "引用的公共方法【" + varProcessFunction.getName() + "】未启用");
                }
            }
        }
    }



    private void checkDown(FlowUpdateStatusInputDto inputDto) {
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
                            .and(wrapper -> wrapper
                                    .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                                    .or()
                                    .eq(VarProcessVariable::getStatus, VariableStatusEnum.REFUSE)
                                    .or()
                                    .eq(VarProcessVariable::getStatus, VariableStatusEnum.UNAPPROVED)
                            )
                            .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
            );
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.FUNCTION_STATUS_NO_MATCH, "已被非编辑中/停用状态的变量引用，不允许停用");
            }
        }
    }

}
