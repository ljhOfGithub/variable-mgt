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

import com.wiseco.decision.common.enums.BaseEnum;
import lombok.Getter;

/**
 * @author Asker.J
 * @since 2022/10/27
 */

/**
 * 分隔符枚举类
 */
public enum SplitEnum implements BaseEnum {
    /**
     * COMMA
     */
    COMMA(",", "逗号"),
    /**
     * TAB
     */
    TAB("\t", "TAB"),
    /**
     * LINE
     */
    LINE("\n", "换行"),
    /**
     * PIPE
     */
    PIPE("|", "竖线"),
    /**
     * SINGLE_QUOTES
     */
    SINGLE_QUOTES("\'", "单引号"),
    /**
     * DOUBLE_QUOTES
     */
    DOUBLE_QUOTES("\"", "双引号"),
    /**
     * SOH
     */
    SOH("\001", "SOH"),
    /**
     * STX
     */
    STX(
            "\002",
            "STX"), ETX(
            "\003",
            "ETX"), OTHER(
            "",
            "其他");
    @Getter
    private String code;
    @Getter
    private String desc;

    SplitEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 枚举还原
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.SplitEnum
     */
    public static SplitEnum getByEnumName(String name) {
        for (SplitEnum splitEnum : SplitEnum.values()) {
            if (splitEnum.name().equals(name)) {
                return splitEnum;
            }
        }
        return null;
    }
}
