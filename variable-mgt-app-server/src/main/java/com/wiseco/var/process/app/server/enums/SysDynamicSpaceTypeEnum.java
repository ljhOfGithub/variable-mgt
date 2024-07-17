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
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum SysDynamicSpaceTypeEnum {

    /**
     * 领域
     */
    DOMAIN("domain","领域"),

    EXTERNAL_SERVICE("external","外部服务"),

    VARIABLE("variable","变量空间"),

    SYSLOG("syslog","系统日志"),;


    private String code;
    private String name;

    /**
     * getCodeEnum
     * @param code String
     * @return SysDynamicSpaceTypeEnum
     */
    public static SysDynamicSpaceTypeEnum getCodeEnum(String code) {
        for (SysDynamicSpaceTypeEnum sysDynamicSpaceTypeEnum : SysDynamicSpaceTypeEnum.values()) {
            if (sysDynamicSpaceTypeEnum.getCode().equals(code)) {
                return sysDynamicSpaceTypeEnum;
            }
        }
        return null;
    }
}
