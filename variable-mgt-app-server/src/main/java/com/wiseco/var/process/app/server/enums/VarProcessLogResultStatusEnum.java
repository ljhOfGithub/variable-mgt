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
 * 变量加工 ClickHouse 日志记录调用状态 枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/23
 */
@Getter
@AllArgsConstructor
public enum VarProcessLogResultStatusEnum {
    /**
     * 变量加工 ClickHouse 日志记录调用状态 枚举
     */

    FAIL(0, "失败"),

    SUCCESS(1, "成功");

    private final Integer code;

    private final String description;

    /**
     * 通过编码获取枚举类
     *
     * @param code 编码
     * @return 枚举类
     */
    public static VarProcessLogResultStatusEnum getEnumFromCode(int code) {
        for (VarProcessLogResultStatusEnum statusEnum : VarProcessLogResultStatusEnum.values()) {
            if (code == statusEnum.getCode()) {
                return statusEnum;
            }
        }
        return null;
    }
}
