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
package com.wiseco.var.process.app.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.permission.RPCAccess;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableExecuteParam;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.FunctionBiz;
import com.wiseco.var.process.app.server.service.VarCompileVarRefBiz;
import com.wiseco.var.process.app.server.service.VarProcessCompileVarService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionReferenceService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.VariableVarBiz;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tempCompilerVarData")
@Slf4j
public class TempCompilerVarController {

    @Resource
    private VarProcessVariableService varProcessVariableService;
    @Resource
    private VarProcessFunctionService varProcessFunctionService;
    @Resource
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;
    @Resource
    private FunctionBiz functionBiz;
    @Autowired
    private VariableCompileBiz variableCompileBiz;
    @Autowired
    private VariableVarBiz variableVarBiz;
    @Autowired
    private VarCompileVarRefBiz varCompileVarRefBiz;
    @Autowired
    private VarProcessCompileVarService varProcessCompileVarService;

    /**
     *
     * @param param 请求参数
     * @return 返回
     */
    @GetMapping("/functionDdata")
    @Operation(summary = "维护历史数据")
    @RPCAccess
    public APIResult<String> offlineLibVarExecute(@RequestBody VariableExecuteParam param) {
        List<VarProcessFunction> functionList = varProcessFunctionService.list(new
                QueryWrapper<VarProcessFunction>().lambda()
                .in(VarProcessFunction::getStatus, Arrays.asList(FlowStatusEnum.UP, FlowStatusEnum.DOWN, FlowStatusEnum.UNAPPROVED,
                        FlowStatusEnum.REFUSE))
                .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        List<VarProcessFunction> orderFunctionList = new ArrayList<>();
        List<VarProcessFunctionReference> functionReferenceList = varProcessFunctionReferenceService.list();
        Map<Long, List<VarProcessFunctionReference>> usebComponentMap = functionReferenceList.stream()
                .collect(Collectors.groupingBy(VarProcessFunctionReference::getFunctionId));
        Map<String, Long> useByaComponentMap = functionReferenceList.stream()
                .collect(Collectors.toMap(data -> (data.getUseByFunctionId() + "_" + data.getFunctionId()),
                        data -> data.getUseByFunctionId()));

        List<Long> orderComponentId = new ArrayList<>();
        orderComponent(usebComponentMap, useByaComponentMap, orderComponentId);

        int i = 0;
        Map<Long, VarProcessFunction> functionIdMap = functionList.stream().collect(Collectors.toMap(data -> (data.getId()),
                data -> data));
        for (Long data : orderComponentId) {
            if (functionIdMap.containsKey(data)) {
                VarProcessFunction varProcessFunction = functionIdMap.get(data);
                orderFunctionList.add(varProcessFunction);
                functionIdMap.remove(data);
                log.info("strComponent:{}---componentId:{}, identifier:{}", i++, varProcessFunction.getId(),
                        varProcessFunction.getIdentifier());
            }
        }
        for (Map.Entry<Long, VarProcessFunction> entry : functionIdMap.entrySet()) {
            VarProcessFunction varProcessFunction = entry.getValue();
            orderFunctionList.add(varProcessFunction);
            log.info("strComponent:{}---componentId:{}, identifier:{}", i++, varProcessFunction.getId(),
                    varProcessFunction.getIdentifier());
        }
        StringBuilder errorMsg = new StringBuilder();
        int num = 0;
        for (VarProcessFunction varProcessFunction : orderFunctionList) {
            try {
                List<VarProcessCompileVar> compileVarList = varProcessCompileVarService.list(new QueryWrapper<VarProcessCompileVar>().lambda()
                        .eq(VarProcessCompileVar::getInvokId, varProcessFunction.getId())
                        .eq(VarProcessCompileVar::getInvokType, VarTypeEnum.FUNCTION));
                if (compileVarList != null && compileVarList.size() != 0) {
                    continue;
                }
                FunctionSaveInputDto inputDto = FunctionSaveInputDto.builder()
                        .id(varProcessFunction.getId())
                        .spaceId(varProcessFunction.getVarProcessSpaceId())
                        .name(varProcessFunction.getName())
                        .functionType(varProcessFunction.getFunctionType())
                        .functionDataType(DataTypeEnum.getEnum(varProcessFunction.getFunctionDataType()))
                        .prepObjectName(varProcessFunction.getPrepObjectName())
                        .description(varProcessFunction.getDescription())
                        .content(JSONObject.parseObject(varProcessFunction.getContent()))
                        // .mapContent(varProcessFunction.getma)
                        .functionEntry(varProcessFunction.getFunctionEntryContent())
                        .categoryId(varProcessFunction.getCategoryId())
                        .handleType(varProcessFunction.getHandleType())
                        .identifier(varProcessFunction.getIdentifier())
                        .build();
                functionBiz.saveFunction(inputDto);
                num++;
                log.info(MessageFormat.format("------------------执行完第{}个函数",num));
            } catch (Exception e) {
                errorMsg.append(new StringBuilder(MessageFormat.format("检入异常，需手动检入：{0}->函数名：{1},id={2},identifier={3}\n",
                        varProcessFunction.getFunctionType().getDesc(), varProcessFunction.getName(),
                        varProcessFunction.getId(), varProcessFunction.getIdentifier())));
                log.error(MessageFormat.format("当前函数检入异常，需要手动检入：{0}->函数名：{1}", varProcessFunction.getFunctionType().getDesc(),
                        varProcessFunction.getName()));
            }
        }
        return APIResult.success(errorMsg.toString());
    }

    /**
     * 补充变量的关联数据
     * @return 返回数据
     */
    @GetMapping("/variableData")
    @Operation(summary = "维护历史数据")
    @RPCAccess
    public APIResult<String> offlineLibVarExecute() {
        List<VarProcessVariable> variableList = varProcessVariableService.list(new
                QueryWrapper<VarProcessVariable>().lambda()
                .in(VarProcessVariable::getStatus, Arrays.asList(VariableStatusEnum.DOWN, VariableStatusEnum.UP,
                        VariableStatusEnum.REFUSE, VariableStatusEnum.UNAPPROVED)));
        StringBuilder errorMsg = new StringBuilder();
        int num = 0;
        for (VarProcessVariable variable : variableList) {
            try {
                Long spaceId = variable.getVarProcessSpaceId();
                Long variableId = variable.getId();
                List<VarProcessCompileVar> compileVarList = varProcessCompileVarService.list(new QueryWrapper<VarProcessCompileVar>().lambda().eq(VarProcessCompileVar::getInvokId, variable.getId())
                        .eq(VarProcessCompileVar::getInvokType, VarTypeEnum.VAR));
                if (compileVarList != null && compileVarList.size() != 0) {
                    continue;
                }
                //申请上架，保存使用到的数据模型属性和变量模型之间的关系
                VariableCompileOutputDto compile = variableCompileBiz.compile(TestVariableTypeEnum.VAR, spaceId, variableId, variable.getContent());
                String classData = variableCompileBiz.compileSingleVar(TestVariableTypeEnum.VAR, spaceId, variableId, variable.getContent());
                // 保存关联的业务数据
                VarCompileResult compileResultVo = compile.getCompileResultVo();
                if (compileResultVo == null || compileResultVo.getSyntaxInfo() == null) {
                    continue;
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
                saveCompileVar(spaceId, variableId, VarTypeEnum.VAR, varProcessCompileVars);
            } catch (Exception e) {
                errorMsg.append(new StringBuilder(MessageFormat.format("检入异常，需手动检入：->变量名：{0},id={1},identifier={2}\n",
                        variable.getName(), variable.getId(), variable.getIdentifier())));
                log.error(MessageFormat.format("检入异常，需手动检入：->变量名：{0},id={1},identifier={2}\n",
                        variable.getName(), variable.getId(), variable.getIdentifier()));
            }
            num++;
            log.info(MessageFormat.format("------------------执行完第{0}个变量",String.valueOf(num)));

        }
        return APIResult.success(errorMsg.toString());
    }

    private void saveCompileVar(Long spaceId, Long invokId, VarTypeEnum varTypeEnum, List<VarProcessCompileVar> varProcessCompileVars) {
        varProcessCompileVarService.remove(
                new QueryWrapper<VarProcessCompileVar>().lambda()
                        .eq(VarProcessCompileVar::getInvokId, invokId)
                        .eq(VarProcessCompileVar::getInvokType, varTypeEnum.name())
        );
        if (CollectionUtils.isEmpty(varProcessCompileVars)) {
            return;
        }
        varProcessCompileVarService.saveBatch(varProcessCompileVars);
    }

    private void orderComponent(Map<Long, List<VarProcessFunctionReference>> usebComponentMap, Map<String, Long>
            useByaComponentMap, List<Long> orderComponentId) {
        if (usebComponentMap == null || usebComponentMap.isEmpty()) {
            return;
        }
        // 创建迭代器
        Iterator<Long> keyIterator = usebComponentMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            // 遍历 key, 避免 ConcurrentModificationException
            Long key = keyIterator.next();
            boolean useBy = useByaComponentMap.values().contains(key);

            if (!useBy) {
                orderComponentId.add(key);

                List<String> useByComponentIdToList = usebComponentMap.get(key).stream().map(data ->
                        data.getUseByFunctionId() + "_" + data.getFunctionId()).collect(Collectors.toList());

                useByComponentIdToList.stream().forEach(data -> {
                    if (useByaComponentMap.containsKey(data)) {
                        useByaComponentMap.remove(data);
                    }
                });
                keyIterator.remove();
            }
        }
        orderComponent(usebComponentMap, useByaComponentMap, orderComponentId);
    }
}
