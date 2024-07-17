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
public enum FunctionProviderEnum {
    /**
     * function_provider_number
     */
    FUNCTION_NUMBER_PROVIDER("number", "function_provider_number"),
    FUNCTION_STRING_PROVIDER("string", "function_provider_string"),
    FUNCTION_BOOL_PROVIDER("boolean", "function_provider_bool"),
    FUNCTION_DATE_PROVIDER("date", "function_provider_date"),
    FUNCTION_OBJECT_PROVIDER("object", "function_provider_object"),

    FUNCTION_ARRAY_PROVIDER("array", "function_provider_array"),
    FUNCTION_ARRAY_OBJECT_PROVIDER("object", "function_provider_array_object"),
    FUNCTION_ARRAY_STRING_PROVIDER("string", "function_provider_array_string"),
    FUNCTION_ARRAY_NUMBER_PROVIDER("number", "function_provider_array_number"),
    FUNCTION_ARRAY_DATE_PROVIDER("date", "function_provider_array_date"),
    FUNCTION_ARRAY_BOOL_PROVIDER("bool", "function_provider_array_bool"),;

    private String dataType;
    private String providerName;

    /**
     * fromName
     * @param name String
     * @return FunctionProviderEnum
     */
    public static FunctionProviderEnum fromName(String name) {
        for (FunctionProviderEnum provider : values()) {
            if (provider.name().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
