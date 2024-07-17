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
 * 实时服务调用类型 (方法) 枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/23
 */
@Getter
@AllArgsConstructor
public enum VariableServiceInvokeMethodEnum {

    /**
     * 外部调用
     */
    REST(1, "外部调用"),

    /**
     * 决策领域调用
     */
    DOMAIN(2, "决策领域调用"),

    /**
     * 策略模拟
     */
    SIMULATE_STRATEGY(3, "策略模拟");

    private final Integer code;

    private final String methodDescription;

    /**
     * 根据调用方式编码获取枚举类
     *
     * @param code 调用方式编码
     * @return 调用类型 (方法) 枚举类
     */
    public static VariableServiceInvokeMethodEnum getMethodEnumFromCode(int code) {
        for (VariableServiceInvokeMethodEnum methodEnum : VariableServiceInvokeMethodEnum.values()) {
            if (code == methodEnum.getCode()) {
                return methodEnum;
            }
        }

        return null;
    }
}
