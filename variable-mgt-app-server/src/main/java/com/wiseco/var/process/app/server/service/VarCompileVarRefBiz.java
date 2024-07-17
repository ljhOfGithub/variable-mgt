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
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.engine.java.common.enums.DataValuePrefixEnum;
import com.wiseco.decision.engine.java.common.enums.EngineComponentInvokeMetaTypeEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.SyntaxInfo;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class VarCompileVarRefBiz {

    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessCompileVarService varProcessCompileVarService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    /**
     * 分析引擎编译结果 放到 strComCompileVarList 和  varUsedTable
     * 注意varUsedTable的存放，A调用B的情况，不要将B的参数和本地变量 functionReturn这些操作放进来
     * @param spaceId 空间id
     * @param componentId 指标id、公共函数id,清单id
     * @param componentType 组件类型
     * @param lexicalData 组件编译结果对象
     * @param varProcessCompileVarList 组件编译结果中callinfo列表转换的strComCompileVar对象列表
     * @param varUsedTable 变量穿透分析的执行操作记录
     * @param allIdentifierList 变量穿透分析的所有组件信息
     */
    public void analyzeComponentVar(long spaceId, long componentId, VarTypeEnum componentType, VarSyntaxInfo lexicalData, List<VarProcessCompileVar> varProcessCompileVarList,
                                    Map<String, VarActionHistory> varUsedTable, Set<String> allIdentifierList) {
        if (lexicalData != null && !CollectionUtils.isEmpty(lexicalData.getCallInfo())) {
            //引擎编译对象处理
            convertToStrComCompileVar(spaceId, componentId, componentType, lexicalData.getCallInfo(),varProcessCompileVarList);
            //定义变量穿透分析结果Map  key是变量全路径 value是变量穿透分析的读写执行顺序
            //已穿透分析过的组件id集合
            HashSet<String> analyzedComponentIdSet = new HashSet<>();
            analyzedComponentIdSet.add(componentId + "");
            //穿透分析
            deepAnalyzeVar(componentType, varProcessCompileVarList, varUsedTable, analyzedComponentIdSet,allIdentifierList, true);
        }
    }

    /**
     * callInfo对象List转StrComCompileVar对象list
     * @param spaceId 空间id
     * @param componentId 组件id
     * @param componentType 组件类型
     * @param callInfoList 直接调用信息
     * @param varProcessCompileVarList 直接调用信息保存数据
     */
    private void convertToStrComCompileVar(Long spaceId, long componentId, VarTypeEnum componentType, List<SyntaxInfo.CallInfo> callInfoList, List<VarProcessCompileVar> varProcessCompileVarList) {
        //解析CallInfo获取varProcessCompileVarList
        for (int i = 0; i < callInfoList.size(); i++) {
            SyntaxInfo.CallInfo callInfo = callInfoList.get(i);
            VarTypeEnum varTypeEnum = VarTypeEnum.FUNCTION;
            if (callInfo instanceof VarSyntaxInfo.VarCallInfo) {
                varTypeEnum = ((VarSyntaxInfo.VarCallInfo) callInfo).getVarType();
            }
            //存放变量全路径
            String value = callInfo.getValue();
            if (EngineComponentInvokeMetaTypeEnum.COMPONENT.name().equals(callInfo.getType().name())) {
                if (varTypeEnum == VarTypeEnum.FUNCTION) {
                    //组件需要查出component_id
                    VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(new QueryWrapper<VarProcessFunction>().lambda()
                            .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                            .eq(VarProcessFunction::getIdentifier, callInfo.getValue())
                            .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
                    value = varProcessFunction.getId() + "";
                } else if (varTypeEnum == VarTypeEnum.VAR) {
                    //组件需要查出component_id
                    VarProcessVariable varProcessVariable = varProcessManifestVariableService.getManifestVariableByIdentifier(componentId, callInfo.getValue());
                    value = varProcessVariable.getId() + "";
                }
            }

            //添加到待存库list
            varProcessCompileVarList.add(VarProcessCompileVar.builder()
                    .invokId(componentId)
                            .invokType(componentType.name())
                    .serialNo(i)
                    .callType(callInfo.getType().name())
                            .callComponentType(callInfo.getType() == EngineComponentInvokeMetaTypeEnum.COMPONENT ? varTypeEnum.name() : null)
                    .value(value)
                    //临时存储单个组件的读写顺序，后面会被穿透分析结果覆盖
                    .actionHistory(callInfo.getAction() == null ? null : callInfo.getAction().getActionHistory())
                    .varType(callInfo.getAction() == null ? null : callInfo.getAction().getVarType())
                    .isArray(callInfo.getAction() == null ? null : Integer.parseInt(callInfo.getAction().getIsArr()))
                    .isExtend(callInfo.getAction() == null ? null : StringUtils.isEmpty(callInfo.getAction().getIsExtend()) ? null : Integer.parseInt(callInfo.getAction().getIsExtend()))
                    .varName(callInfo.getAction() == null ? null : callInfo.getAction().getLabel())
                    .isParameterArray(callInfo.getAction() == null ? null : (callInfo.getAction().getIsParameterArray() == null ? null : Integer.parseInt(callInfo.getAction().getIsParameterArray())))
                    .parameterLabel(callInfo.getAction() == null ? null : callInfo.getAction().getParameterLabel())
                    .parameterType(callInfo.getAction() == null ? null : callInfo.getAction().getParameterType())
                            .createdUser(SessionContext.getSessionUser().getUsername())
                    .build());
        }
    }

    /**
     *
     * 组件变量穿透分析
     * @param componentType 当前组件类型
     * @param varProcessCompileVarList 引擎编译得到的单个组件内的变量信息
     * @param actionHistoryMap 变量穿透分析结果Map key是变量全路径 value是变量穿透分析的读写执行顺序
     * @param analyzedComponentIdSet 当前已穿透分析的组件id列表 记录该值用来避免循环依赖
     * @param allIdentifierList 变量穿透分析的所有组件信息
     * @param isDirect 是否直接引用
     */
    private void deepAnalyzeVar(VarTypeEnum componentType, List<VarProcessCompileVar> varProcessCompileVarList, Map<String, VarActionHistory> actionHistoryMap, Set<String> analyzedComponentIdSet,
                                Set<String> allIdentifierList, boolean isDirect) {
        for (VarProcessCompileVar varProcessCompileVar : varProcessCompileVarList) {
            if (EngineComponentInvokeMetaTypeEnum.COMPONENT.name().equals(varProcessCompileVar.getCallType()) && !analyzedComponentIdSet.contains(varProcessCompileVar.getValue())) {
                //组件类型
                analyzedComponentIdSet.add(varProcessCompileVar.getValue());
                List<VarProcessCompileVar> refStrComCompileVarList = varProcessCompileVarService.list(Wrappers.<VarProcessCompileVar>lambdaQuery()
                        .eq(VarProcessCompileVar::getInvokId, varProcessCompileVar.getValue())
                                .eq(VarProcessCompileVar::getInvokType, varProcessCompileVar.getCallComponentType())
                        .orderByAsc(VarProcessCompileVar::getInvokId, VarProcessCompileVar::getSerialNo));

                if (CollectionUtils.isEmpty(refStrComCompileVarList)) {
                    log.warn("未找到依赖组件的变量信息,检查依赖组件是否已编译 componentId=" + varProcessCompileVar.getInvokId());
                    continue;
                }
                String identifier = null;
                if (VarTypeEnum.FUNCTION.name().equals(varProcessCompileVar.getCallComponentType())) {
                    VarProcessFunction function = varProcessFunctionService.getOne(new QueryWrapper<VarProcessFunction>().lambda().eq(VarProcessFunction::getId, varProcessCompileVar.getValue()));
                    identifier = function.getIdentifier();
                } else {
                    VarProcessVariable variable = varProcessVariableService.getOne(new QueryWrapper<VarProcessVariable>().lambda().eq(VarProcessVariable::getId, varProcessCompileVar.getValue()));
                    identifier = variable.getIdentifier();
                }
                allIdentifierList.add(identifier);
                //递归分析依赖组件的变量
                deepAnalyzeVar(VarTypeEnum.valueOf(varProcessCompileVar.getCallComponentType()), refStrComCompileVarList, actionHistoryMap, analyzedComponentIdSet,allIdentifierList, false);

            } else if (EngineComponentInvokeMetaTypeEnum.VAR.name().equals(varProcessCompileVar.getCallType())) {
                String varName = varProcessCompileVar.getValue();
                //不是直接引用的变量，过滤一些变量
                boolean isSpecialPath = varName.toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase())
                        || varName.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())
                        || varName.toLowerCase().startsWith(DataValuePrefixEnum.FUNCTIONRETURN.name().toLowerCase())
                        || varName.toLowerCase().startsWith(DataValuePrefixEnum.RESULT.name().toLowerCase())
                        || varName.toLowerCase().startsWith(DataValuePrefixEnum.RETURN.name().toLowerCase());
                if (!isDirect && isSpecialPath) {
                    continue;
                }
                VarActionHistory oldHistory = actionHistoryMap.get(varName);
                if (oldHistory == null) {
                    actionHistoryMap.put(varProcessCompileVar.getValue(), convertVarActionHistory(actionHistoryMap, varProcessCompileVar,isDirect));
                } else {
                    oldHistory.setActionHistory(oldHistory.getActionHistory() + varProcessCompileVar.getActionHistory());
                    actionHistoryMap.put(varProcessCompileVar.getValue(), oldHistory);
                }
            }
        }
    }

    /**
     * strComCompileVar转换成VarActionHistory 在此完成相同变量的操作顺序actionHistory的拼接
     * @param actionHistoryMap 操作变量信息
     * @param strComCompileVar 查询变量使用信息
     * @param isDirect 是否直接引用
     * @return 变量操作信息
     */
    private VarActionHistory convertVarActionHistory(Map<String, VarActionHistory> actionHistoryMap, VarProcessCompileVar strComCompileVar,boolean isDirect) {
        //拼接变量操作
        String newActionHistory = "";
        if (actionHistoryMap.get(strComCompileVar.getValue()) == null) {
            newActionHistory = strComCompileVar.getActionHistory();
        } else {
            VarActionHistory originHistory = actionHistoryMap.get(strComCompileVar.getValue());
            //记录变量的历史操作  优化:第一个操作符必须拼 相同操作不需要拼 wr都存在的就不需要拼接了 因此最终可能存在以下4中情况 w r wr rw
            Set<String> originActionHistorySet = Arrays.stream(originHistory.getActionHistory().split("")).collect(Collectors.toSet());
            StringBuilder newActionHistoryBuilder = new StringBuilder();

            Arrays.stream(strComCompileVar.getActionHistory().split("")).forEach(i -> {
                if (!originActionHistorySet.contains(i)) {
                    //如果操作记录中不存在才需要记录
                    newActionHistoryBuilder.append(i);
                    originActionHistorySet.add(i);
                }
            });

            newActionHistory = newActionHistoryBuilder.toString();
        }

        VarActionHistory varActionHistory = new VarActionHistory();
        varActionHistory.setActionHistory(newActionHistory);
        varActionHistory.setVarType(strComCompileVar.getVarType());
        varActionHistory.setIsArr(strComCompileVar.getIsArray().toString());
        varActionHistory.setLabel(strComCompileVar.getVarName());
        varActionHistory.setIsDirect(isDirect);
        varActionHistory.setParameterType(strComCompileVar.getParameterType());
        varActionHistory.setParameterLabel(strComCompileVar.getParameterLabel());
        varActionHistory.setIsParameterArray(strComCompileVar.getIsParameterArray() == null ? null : strComCompileVar.getIsParameterArray().toString());
        varActionHistory.setIsThisVar(false);
        return varActionHistory;
    }

}
