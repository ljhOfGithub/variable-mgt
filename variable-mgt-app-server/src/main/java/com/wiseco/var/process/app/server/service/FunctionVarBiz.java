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
import com.wiseco.decision.engine.var.transform.component.compiler.VarCompileResult;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.DataValuePrefixEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
public class FunctionVarBiz {

    @Autowired
    private VarProcessFunctionVarService varProcessFunctionVarService;
    @Autowired
    private VarProcessFunctionClassService varProcessFunctionClassService;


    /**
     * saveVar
     * @param spaceId 空间id
     * @param functionId  入参
     * @param actionHistorys 入参
     */
    public void saveVar(Long spaceId, Long functionId, Map<String, VarActionHistory> actionHistorys) {
        //删除现有的记录
        varProcessFunctionVarService.remove(
                new QueryWrapper<VarProcessFunctionVar>().lambda()
                        .eq(VarProcessFunctionVar::getFunctionId, functionId));
        if (actionHistorys == null) {
            return;
        }
        List<VarProcessFunctionVar> vars = new ArrayList<>();
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
            VarProcessFunctionVar strComVar = VarProcessFunctionVar.builder().functionId(functionId).actionHistory(varInfo.getActionHistory()).build();
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
            //扩展数据默认为0
            if (strComVar.getIsExtend().equals(NumberUtils.INTEGER_ONE) && testFlag == 1) {
                testFlag = 0;
            }
            strComVar.setTestFlag(testFlag);
            //变量是否直接引用
            strComVar.setIsSelf(varInfo.getIsDirect() != null && varInfo.getIsDirect() ? 1 : 0);
            vars.add(strComVar);
        }
        //从新入库保存
        if (!vars.isEmpty()) {
            convertTestVarsData(vars);
            varProcessFunctionVarService.saveBatch(vars);
        }
    }

    /**
     * saveVarClass
     * @param spaceId 空间ID
     * @param functionId 入参
     * @param compileResultVo 入参
     * @param classData 入参
     */
    public void saveVarClass(Long spaceId, Long functionId, VarCompileResult compileResultVo, String classData) {

        //删除现有的记录
        varProcessFunctionClassService.remove(
                new QueryWrapper<VarProcessFunctionClass>()
                        .lambda()
                        .eq(VarProcessFunctionClass::getFunctionId, functionId)
        );

        varProcessFunctionClassService.save(
                VarProcessFunctionClass.builder()
                        .varProcessSpaceId(spaceId)
                        .functionId(functionId)
                        .classData(classData)
                        .javaData(compileResultVo.getScript())
                        .build()
        );
    }

    private void convertTestVarsData(List<VarProcessFunctionVar> varsList) {
        //如果父级对象是输出，则属性都是输出
        List<VarProcessFunctionVar> objectVars = varsList.stream().filter(f -> DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(f.getVarType())
                && f.getTestFlag() == MagicNumbers.TWO).collect(Collectors.toList());


        if (CollectionUtils.isEmpty(objectVars)) {
            return;
        }

        Set<String> objectVarSet = objectVars.stream().map(VarProcessFunctionVar::getVarPath).collect(Collectors.toSet());

        for (VarProcessFunctionVar componentVar : varsList) {
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
