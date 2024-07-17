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
 * 变量空间测试类型
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum TestVariableTypeEnum {
    /**
     * 变量空间测试类型枚举
     */

    VAR(1, "变量"),

    FUNCTION(2, "公共函数"),

    MANIFEST(3, "变量清单");

    private Integer code;
    private String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举code
     * @return TestVariableTypeEnum
     */
    public static TestVariableTypeEnum getCode(Integer code) {
        for (TestVariableTypeEnum statusEnum : TestVariableTypeEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
