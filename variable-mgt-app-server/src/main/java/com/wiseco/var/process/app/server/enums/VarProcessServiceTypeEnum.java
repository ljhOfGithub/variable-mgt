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

/**
 * 实时服务类型枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
public enum VarProcessServiceTypeEnum {

    // 服务类型 - 1: 实时 2: 批量
    REAL_TIME(1, "实时"), BATCH(2, "批量");

    private final Integer code;
    private final String desc;

    VarProcessServiceTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据枚举code获取枚举
     *
     * @param code 枚举code
     * @return VarProcessServiceTypeEnum
     */
    public static VarProcessServiceTypeEnum getTypeEnum(Integer code) {
        for (VarProcessServiceTypeEnum typeEnum : VarProcessServiceTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
