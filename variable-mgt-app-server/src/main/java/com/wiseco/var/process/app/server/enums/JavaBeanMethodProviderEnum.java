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
 * javabean方法
 */
@Getter
@AllArgsConstructor
public enum JavaBeanMethodProviderEnum {
    /**
     * java_bean_method_provider
     */
    JAVA_BEAN_METHOD_PROVIDER("void", 0, "java_bean_method_provider"),
    /**
     * java_bean_method_bool_provider
     */
    JAVA_BEAN_METHOD_BOOL_PROVIDER("boolean", 0, "java_bean_method_bool_provider"),
    /**
     * java_bean_method_string_provider
     */
    JAVA_BEAN_METHOD_STRING_PROVIDER("string", 0, "java_bean_method_string_provider"),
    /**
     * java_bean_method_number_provider
     */
    JAVA_BEAN_METHOD_NUMBER_PROVIDER("int,double", 0, "java_bean_method_number_provider"),
    /**
     * java_bean_method_date_provider
     */
    JAVA_BEAN_METHOD_DATE_PROVIDER("date,datetime", 0, "java_bean_method_date_provider"),
    /**
     * java_bean_method_object_provider
     */
    JAVA_BEAN_METHOD_OBJECT_PROVIDER("object", 0, "java_bean_method_object_provider"),

    /**
     * java_bean_method_array_bool_provider
     */
    JAVA_BEAN_METHOD_ARRAY_BOOL_PROVIDER("boolean", 1, "java_bean_method_array_bool_provider"),
    /**
     * java_bean_method_array_string_provider
     */
    JAVA_BEAN_METHOD_ARRAY_STRING_PROVIDER("string", 1, "java_bean_method_array_string_provider"),
    /**
     * java_bean_method_array_number_provider
     */
    JAVA_BEAN_METHOD_ARRAY_NUMBER_PROVIDER("int,double", 1, "java_bean_method_array_number_provider"),
    /**
     * java_bean_method_array_date_provider
     */
    JAVA_BEAN_METHOD_ARRAY_DATE_PROVIDER("date,datetime", 1, "java_bean_method_array_date_provider"),
    /**
     * java_bean_method_array_object_provider
     */
    JAVA_BEAN_METHOD_ARRAY_OBJECT_PROVIDER("object", 1, "java_bean_method_array_object_provider"),;

    private String dataType;
    private Integer isArray;
    private String providerName;

    /**
     * fromName
     * @param name String
     * @return JavaBeanMethodProviderEnum
     */
    public static JavaBeanMethodProviderEnum fromName(String name) {
        for (JavaBeanMethodProviderEnum provider : values()) {
            if (provider.name().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}

