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
package com.wiseco.var.process.app.server.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代码库来源枚举
 */
@AllArgsConstructor
@Getter
public enum CodeBaseResourceTypeEnum {
    /**
     * 领域策略
     */
    STRATEGY(1,"领域策略"),

    /**
     * 空间变量
     */
    VARIABLE(2,"空间变量");

    private int code;
    private String desc;

    /**
     * getCodeEnum
     * @param code
     * @return CodeBaseResourceTypeEnum
     */
    public static CodeBaseResourceTypeEnum getCodeEnum(Integer code) {
        for (CodeBaseResourceTypeEnum codeBaseResourceTypeEnum : CodeBaseResourceTypeEnum.values()) {
            if (codeBaseResourceTypeEnum.getCode() == code) {
                return codeBaseResourceTypeEnum;
            }
        }
        return null;
    }
}
