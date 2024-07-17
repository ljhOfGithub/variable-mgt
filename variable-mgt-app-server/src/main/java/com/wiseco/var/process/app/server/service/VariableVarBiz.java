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
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.engine.java.common.VarOperatorActionEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.DataValuePrefixEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 相关变量入库
 *
 * @author wangxianli
 */
@Component
@Slf4j
public class VariableVarBiz {

    @Autowired
    private VarProcessVariableVarService varProcessVariableVarService;

    @Autowired
    private VarProcessTestVarService varProcessTestVariableVarService;

    @Autowired
    private VarProcessVariableClassService varProcessVariableClassService;
    @Autowired
    private VarCompileVarRefBiz varCompileVarRefBiz;

    /**
     * saveVarClass
     * @param spaceId spaceId
     * @param variableId variableId
     * @param compileResultVo compileResultVo
     * @param classData classData
     */
    public void saveVarClass(Long spaceId, Long variableId, VarCompileResult compileResultVo, String classData) {

        //删除现有的记录
        varProcessVariableClassService.remove(
                new QueryWrapper<VarProcessVariableClass>()
                        .lambda()
                        .eq(VarProcessVariableClass::getVariableId, variableId)
        );

        varProcessVariableClassService.save(
                VarProcessVariableClass.builder()
                        .varProcessSpaceId(spaceId)
                        .variableId(variableId)
                        .classData(classData)
                        .javaData(compileResultVo.getScript())
                        .build()
        );
    }

    /**
     * saveVar
     * @param spaceId spaceId
     * @param variableId variableId
     * @param actionHistorys compileResultVo
     */
    public void saveVar(Long spaceId, Long variableId, Map<String, VarActionHistory> actionHistorys) {
        //删除现有的记录
        varProcessVariableVarService.remove(
                new QueryWrapper<VarProcessVariableVar>()
                        .lambda()
                        .eq(VarProcessVariableVar::getVariableId, variableId)
        );
        if (actionHistorys == null) {
            return;
        }
        List<VarProcessVariableVar> vars = new ArrayList<>();

        for (Map.Entry<String, VarActionHistory> actionHistory : actionHistorys.entrySet()) {
            String dataValue = actionHistory.getKey();
            VarActionHistory varInfo = actionHistory.getValue();
            if (varInfo == null) {
                continue;
            }
            //如果是公共函数或者变量返回值则不记录到var表
            if (dataValue.equals(CommonConstant.COMMON_FUNCTION_RETURN_NAME) || dataValue.equals(CommonConstant.VARIABLE_RETURN_NAME)) {
                continue;
            }
            if ("void".equals(varInfo.getVarType())) {
                continue;
            }
            String actions = varInfo.getActionHistory();
            int testFlag = 0;
            // 写操作是需要判断的结果，读操作是需要使用的变量，需要预先赋值
            if (actions.toLowerCase().startsWith(VarOperatorActionEnum.READ.getFlag())) {
                testFlag += 1;
            }
            if (actions.toLowerCase().contains(VarOperatorActionEnum.WRITE.getFlag())) {
                testFlag += MagicNumbers.TWO;
            }
            String label = varInfo.getLabel();
            VarProcessVariableVar strComVar = VarProcessVariableVar.builder().variableId(variableId).actionHistory(varInfo.getActionHistory()).build();
            strComVar.setVarPath(dataValue);
            strComVar.setVarName(label);
            strComVar.setVarType(varInfo.getVarType());
            if (StringUtils.hasText(varInfo.getIsArr())) {
                strComVar.setIsArray(Integer.parseInt(varInfo.getIsArr()));
            }
            if (StringUtils.hasText(varInfo.getParameterType())) {
                strComVar.setParameterType(varInfo.getParameterType());
            }

            if (StringUtils.hasText(varInfo.getIsParameterArray())) {
                strComVar.setIsParameterArray(Integer.parseInt(varInfo.getIsParameterArray()));
            }

            strComVar.setParameterLabel(varInfo.getParameterLabel());

            //本地变量一律认为是输出变量
            if (dataValue.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())) {
                testFlag = MagicNumbers.TWO;
            }

            strComVar.setIsExtend(varInfo.getIsExtend() != null ? Integer.parseInt(varInfo.getIsExtend()) : 0);

            strComVar.setTestFlag(testFlag);
            //变量是否直接引用
            strComVar.setIsSelf(varInfo.getIsDirect() != null && varInfo.getIsDirect() ? 1 : 0);

            vars.add(strComVar);

        }
        //从新入库保存
        if (!vars.isEmpty()) {
            //处理数据:如果父级对象是输出，则属性都是输出
            convertVarsData(vars);
            varProcessVariableVarService.saveBatch(vars);
        }
    }

    /**
     * saveTestVar
     * @param spaceId spaceId
     * @param type  type
     * @param variableId variableId
     * @param compileResultVo compileResultVo
     */
    public void saveTestVar(Long spaceId, TestVariableTypeEnum type, Long variableId, VarCompileResult compileResultVo) {
        //删除现有的记录
        varProcessTestVariableVarService.remove(
                new QueryWrapper<VarProcessTestVar>().lambda()
                        .eq(VarProcessTestVar::getVariableId, variableId)
                        .eq(VarProcessTestVar::getTestType, type.getCode())
        );
        //编译通过后，后端对比校验数据
        VarSyntaxInfo syntaxInfo = compileResultVo.getSyntaxInfo();
        List<VarProcessCompileVar> varProcessCompileVars = new ArrayList<>();
        Map<String, VarActionHistory> actionHistorys = new HashMap<>(MagicNumbers.INT_64);
        Set<String> allIdentifierList = new HashSet<>();
        if (syntaxInfo != null && !CollectionUtils.isEmpty(syntaxInfo.getCallInfo())) {
            varCompileVarRefBiz.analyzeComponentVar(spaceId, variableId, VarTypeEnum.VAR, syntaxInfo, varProcessCompileVars, actionHistorys, allIdentifierList);
        }
        List<VarProcessTestVar> vars = new ArrayList<>();
        for (Map.Entry<String, VarActionHistory> actionHistory : actionHistorys.entrySet()) {
            String dataValue = actionHistory.getKey();
            VarActionHistory varInfo = actionHistory.getValue();
            if (varInfo == null) {
                continue;
            }
            //如果是公共函数返回值则不记录到var表
            if (type.equals(TestVariableTypeEnum.VAR) && dataValue.equals(CommonConstant.COMMON_FUNCTION_RETURN_NAME)) {
                continue;
            }
            if ("void".equals(varInfo.getVarType())) {
                continue;
            }
            String actions = varInfo.getActionHistory();
            int testFlag = 0;
            // 写操作是需要判断的结果，读操作是需要使用的变量，需要预先赋值
            if (actions.toLowerCase().startsWith(VarOperatorActionEnum.READ.getFlag())) {
                testFlag += 1;
            }
            if (actions.toLowerCase().contains(VarOperatorActionEnum.WRITE.getFlag())) {
                testFlag += MagicNumbers.TWO;
            }
            String label = varInfo.getLabel();
            VarProcessTestVar strComVar = VarProcessTestVar.builder().variableId(variableId).actionHistory(varInfo.getActionHistory()).build();
            strComVar.setVarPath(dataValue);strComVar.setVarName(label);strComVar.setVarType(varInfo.getVarType());
            if (StringUtils.hasText(varInfo.getIsArr())) {
                strComVar.setIsArray(Integer.parseInt(varInfo.getIsArr()));
            }
            if (StringUtils.hasText(varInfo.getParameterType())) {
                strComVar.setParameterType(varInfo.getParameterType());
            }
            if (StringUtils.hasText(varInfo.getIsParameterArray())) {
                strComVar.setIsParameterArray(Integer.parseInt(varInfo.getIsParameterArray()));
            }
            strComVar.setParameterLabel(varInfo.getParameterLabel());
            //本地变量一律认为是输出变量
            if (dataValue.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())) {
                testFlag = MagicNumbers.TWO;
            }
            strComVar.setIsExtend(varInfo.getIsExtend() != null ? Integer.parseInt(varInfo.getIsExtend()) : 0);
            //扩展数据默认为0
            if (type.equals(TestVariableTypeEnum.MANIFEST) && strComVar.getIsExtend().equals(NumberUtils.INTEGER_ONE)) {
                testFlag = 0;
            }
            strComVar.setTestFlag(testFlag);
            //变量是否直接引用
            strComVar.setIsSelf(varInfo.getIsDirect() != null && varInfo.getIsDirect() ? 1 : 0);
            strComVar.setTestType(type.getCode());
            if (testFlag == MagicNumbers.TWO && !varInfo.getActionHistory().toLowerCase().contains(VarOperatorActionEnum.WRITE.getFlag())) {
                strComVar.setActionHistory(VarOperatorActionEnum.WRITE.getFlag());
            }
            vars.add(strComVar);
        }
        //从新入库保存
        if (!vars.isEmpty()) {
            //处理数据:如果父级对象是输出，则属性都是输出
            convertTestVarsData(vars);
            varProcessTestVariableVarService.saveBatch(vars);
        }
    }

    private void convertVarsData(List<VarProcessVariableVar> varsList) {
        //如果父级对象是输出，则属性都是输出
        List<VarProcessVariableVar> objectVars = varsList.stream().filter(f -> DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(f.getVarType())
                && f.getTestFlag() == MagicNumbers.TWO).collect(Collectors.toList());


        if (CollectionUtils.isEmpty(objectVars)) {
            return;
        }

        Set<String> objectVarSet = objectVars.stream().map(VarProcessVariableVar::getVarPath).collect(Collectors.toSet());

        for (VarProcessVariableVar componentVar : varsList) {
            //输入
            if (componentVar.getTestFlag() == 1) {
                for (String setString : objectVarSet) {

                    if (componentVar.getVarPath().startsWith(setString + ".")) {
                        componentVar.setTestFlag(MagicNumbers.TWO);
                        if (!componentVar.getActionHistory().toLowerCase().contains(VarOperatorActionEnum.WRITE.getFlag())) {
                            componentVar.setActionHistory(VarOperatorActionEnum.WRITE.getFlag());
                        }
                        break;
                    }
                }
            }
        }

    }

    private void convertTestVarsData(List<VarProcessTestVar> varsList) {
        //如果父级对象是输出，则属性都是输出
        List<VarProcessTestVar> objectVars = varsList.stream().filter(f -> DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(f.getVarType())
                && f.getTestFlag() == MagicNumbers.TWO).collect(Collectors.toList());


        if (CollectionUtils.isEmpty(objectVars)) {
            return;
        }

        Set<String> objectVarSet = objectVars.stream().map(VarProcessTestVar::getVarPath).collect(Collectors.toSet());

        for (VarProcessTestVar componentVar : varsList) {
            //输入
            if (componentVar.getTestFlag() == 1) {
                for (String setString : objectVarSet) {

                    if (componentVar.getVarPath().startsWith(setString + ".")) {
                        componentVar.setTestFlag(MagicNumbers.TWO);
                        if (!componentVar.getActionHistory().toLowerCase().contains(VarOperatorActionEnum.WRITE.getFlag())) {
                            componentVar.setActionHistory(VarOperatorActionEnum.WRITE.getFlag());
                        }
                        break;
                    }
                }
            }
        }

    }
}
