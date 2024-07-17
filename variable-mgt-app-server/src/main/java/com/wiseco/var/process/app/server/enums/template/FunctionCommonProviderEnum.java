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
package com.wiseco.var.process.app.server.enums.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhouxiuxiu
 */
@Getter
@AllArgsConstructor
public enum FunctionCommonProviderEnum {

    /**
     * function_common_provider
     */
    FUNCTION_COMMON_PROVIDER("void", "function_common_provider", 2),
    FUNCTION_COMMON_NUMBER_PROVIDER("number", "function_common_provider_number", 2),
    FUNCTION_COMMON_STRING_PROVIDER("string", "function_common_provider_string", 2),
    FUNCTION_COMMON_BOOL_PROVIDER("boolean", "function_common_provider_bool", 2),
    FUNCTION_COMMON_DATE_PROVIDER("date", "function_common_provider_date", 2),
    FUNCTION_VARTEMPLATE_PROVIDER("void", "function_vartemplate_provider", 1),
    FUNCTION_VARTEMPLATE_NUMBER_PROVIDER("number", "function_vartemplate_provider_number", 1),
    FUNCTION_VARTEMPLATE_STRING_PROVIDER("string", "function_vartemplate_provider_string", 1),
    FUNCTION_VARTEMPLATE_BOOL_PROVIDER("boolean", "function_vartemplate_provider_bool", 1),
    FUNCTION_VARTEMPLATE_DATE_PROVIDER("date", "function_vartemplate_provider_date", 1),;

    private String dataType;
    private String providerName;
    /**
     *  函数类型:1-变量模板，2-公共方法
     */
    private Integer functionType;

    /**
     * fromName
     * @param name String
     * @return FunctionCommonProviderEnum
     */
    public static FunctionCommonProviderEnum fromName(String name) {
        for (FunctionCommonProviderEnum provider : values()) {
            if (provider.name().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
