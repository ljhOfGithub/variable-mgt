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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessVariableRuleMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRule;
import com.wiseco.var.process.app.server.service.VariableRuleService;
import org.springframework.stereotype.Service;

@Service
public class VariableRuleServiceImpl extends ServiceImpl<VarProcessVariableRuleMapper, VarProcessVariableRule> implements VariableRuleService {
    @Override
    public VarProcessVariableRule getOneByFunctionId(Long functionId) {
        return lambdaQuery().eq(VarProcessVariableRule::getFunctionId, functionId).one();

    }
}
