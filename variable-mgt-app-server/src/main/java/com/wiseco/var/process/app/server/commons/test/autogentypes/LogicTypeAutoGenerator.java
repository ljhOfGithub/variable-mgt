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
package com.wiseco.var.process.app.server.commons.test.autogentypes;

import com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;

/**
 * 逻辑依赖 测试数据在线自动生成器
 * TODO
 *
 * @author Zhaoxiong Chen
 * @since 2022/1/7
 */
public class LogicTypeAutoGenerator implements TestDataAutoGenerator {

    /**
     * LogicTypeAutoGenerator
     *
     * @param generationRuleExpression 生成规则表达式
     */
    public LogicTypeAutoGenerator(String generationRuleExpression) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "逻辑依赖尚未完成");
    }

    @Override
    public String getValue() {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "逻辑依赖尚未完成");
    }

}
