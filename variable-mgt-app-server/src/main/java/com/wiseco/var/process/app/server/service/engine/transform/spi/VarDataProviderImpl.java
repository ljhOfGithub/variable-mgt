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
package com.wiseco.var.process.app.server.service.engine.transform.spi;

import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.api.IVarDataProvider;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.var.process.app.server.service.VariableDataProviderBiz;
import com.wisecotech.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class VarDataProviderImpl implements IVarDataProvider {
    /**
     * 查询变量加工变量组件的编译数据
     * 需要根据 type 执行不同的操作
     * 变量 :   使用 spaceId identifier changeNum 查询组装数据
     * 自定义函数 :  spaceId + identifier 查询数据
     * 数据预处理:   待定
     * 外数  spaceId + identifier(外数编码)
     */
    @Autowired
    private VariableDataProviderBiz variableDataProviderBiz;

    @Override
    public VarCompileData getCheckedInVarData(Long spaceId, Long manifestId, VarTypeEnum type, String identifier) {

        return variableDataProviderBiz.varDataProvider(spaceId, manifestId, type, identifier);
    }

    @Override
    public VarCompileData getInterfaceFlowData(Long manifestId) {
        return variableDataProviderBiz.getManifestFlowData(manifestId);
    }

    @Override
    public Map<String, JSONObject> getTemplateConfig(Long spaceId, VarTypeEnum type) {
        return variableDataProviderBiz.getTemplateConfig(spaceId, type);
    }

    @Override
    public Map<String, VarActionHistory> getValidVarActionHistory(Long spaceId, VarTypeEnum type, String identifier) {
        return variableDataProviderBiz.getValidVarActionHistory(spaceId, type, identifier);
    }

    @Override
    public Set<String> getIdentifierSet(Long spaceId, VarTypeEnum type, String identifier) {
        return variableDataProviderBiz.getIdentifierSet(spaceId, type, identifier);
    }

}
