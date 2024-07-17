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
package com.wiseco.var.process.app.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试组件的tab枚举
 */
@Getter
@AllArgsConstructor
public enum TestResultDetailTabEnum {

    /**
     * 测试组件的tab枚举
     * <ul>
     *     <li>EXPECTATION_RESULT_COMPARE: 预期结果对比</li>
     *     <li>REQUEST_DATA: 请求数据</li>
     *     <li>ENGINE_USED_DATA: 引擎使用数据</li>
     *     <li>OUTPUT_RESULT: 输出结果</li>
     *     <li>EXCEPTION_MESSAGE: 异常信息</li>
     *     <li>TRACE_DETAIL: trace详情</li>
     *     <li>DEBUG_INFO: debug信息</li>
     * </ul>
     */

    EXPECTATION_RESULT_COMPARE("预期结果对比"),

    REQUEST_DATA("请求数据"),

    ENGINE_USED_DATA("引擎使用数据"),

    OUTPUT_RESULT("输出结果"),

    EXCEPTION_MESSAGE("异常信息"),

    TRACE_DETAIL("trace详情"),

    DEBUG_INFO("debug信息");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 实时服务的状态枚举
     * @return 实时服务的状态枚举
     */
    public TestResultDetailTabEnum get(TestResultDetailTabEnum input) {
        for (TestResultDetailTabEnum stateEnum : TestResultDetailTabEnum.values()) {
            if (stateEnum.equals(input)) {
                return stateEnum;
            }
        }
        return null;
    }
}
