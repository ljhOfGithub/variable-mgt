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
package com.wiseco.var.process.app.server.service.manifest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.boot.commons.lang.StringUtils;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.engine.java.common.VarOperatorActionEnum;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.transform.enums.DataValuePrefixEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 相关变量入库
 *
 * @author wangxianli
 */
@Component
@Slf4j
public class VariableManifestVarBiz {

    @Autowired
    private VarProcessManifestVarService varProcessManifestVarService;

    /**
     * 保存变量清单
     * @param spaceId spaceId
     * @param manifestId manifestId
     * @param actionHistorys actionHistorys
     */
    public void saveVar(Long spaceId, Long manifestId, Map<String, VarActionHistory> actionHistorys) {
        //删除现有的记录
        varProcessManifestVarService.remove(
                new QueryWrapper<VarProcessManifestVar>()
                        .lambda()
                        .eq(VarProcessManifestVar::getManifestId, manifestId)
        );
        if (actionHistorys == null) {
            return;
        }
        List<VarProcessManifestVar> vars = new ArrayList<>();
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
            VarProcessManifestVar strComVar = VarProcessManifestVar.builder().manifestId(manifestId).actionHistory(varInfo.getActionHistory()).build();
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
            varProcessManifestVarService.saveBatch(vars);
        }
    }
}
