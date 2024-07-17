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
 * 变量状态
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum VarTemplateTypeEnum {


    /**
     * 变量管理
     */
    VAR_PROCESS("varProcess", "变量管理"),

    COMMON_FUNCTION("commonFunction", "公共函数"),

    SERVICE_INTERFACE("serviceInterface", "服务接口发布");

    private String code;
    private String desc;

    /**
     * getCode
     *
     * @param code code
     * @return com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum
     */
    public static VarTemplateTypeEnum getCode(String code) {
        for (VarTemplateTypeEnum statusEnum : VarTemplateTypeEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
