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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRule;

public interface VariableRuleService extends IService<VarProcessVariableRule> {
    /**
     * 查询生成变量规则
     *
     * @param functionId 公共函数id
     * @return 变量规则类
     */
    VarProcessVariableRule getOneByFunctionId(Long functionId);
}
