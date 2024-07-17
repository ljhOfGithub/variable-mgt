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
package com.wiseco.var.process.app.server.service.engine;

import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarFunctionCompileQueryResultDto;

public interface IVarProcessCompileVarProvider {

    /**
     * 递归查下使用组件变量信息
     * 
     * @param spaceId 空间id
     * @param manifestId 清单id
     * @param varTypeEnum 组件类型
     * @param identifier identifier
     * @param isDirect 是否直接使用
     * @return 查下组件变量信息
     */
    VarFunctionCompileQueryResultDto getAllIdentifiersAndVarPathes(Long spaceId, Long manifestId,
        VarTypeEnum varTypeEnum, String identifier, boolean isDirect);
}
