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
 * 测试数据自动生成规则类型
 *
 * @author wangxianli
 * @date 2021/12/10
 */
@AllArgsConstructor
@Getter
public enum GenerateTypeEnum {

    /**
     * 枚举
     */
    ENUM("enum", "枚举"),

    /**
     * 随机
     */
    RANDOM("random", "随机"),

    /**
     * 逻辑依赖
     */
    LOGIC("logic", "逻辑依赖"),

    /**
     * 自定义
     */
    CUSTOM("custom", "自定义"),;

    private String code;
    private String message;

    /**
     * getCodeEnum
     * @param code String
     * @return GenerateTypeEnum
     */
    public static GenerateTypeEnum getCodeEnum(String code) {
        for (GenerateTypeEnum generateTypeEnum : GenerateTypeEnum.values()) {
            if (generateTypeEnum.getCode().equals(code)) {
                return generateTypeEnum;
            }
        }
        return null;
    }

    /**
     * getMessageEnum
     * @param message String
     * @return GenerateTypeEnum
     */
    public static GenerateTypeEnum getMessageEnum(String message) {
        for (GenerateTypeEnum generateTypeEnum : GenerateTypeEnum.values()) {
            if (generateTypeEnum.getMessage().equals(message)) {
                return generateTypeEnum;
            }
        }
        return null;
    }
}
