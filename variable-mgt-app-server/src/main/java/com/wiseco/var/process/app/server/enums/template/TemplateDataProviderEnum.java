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

import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * TemplateDataProviderEnum
 *
 * @author xinpgzhewen
 */
@Getter
@AllArgsConstructor
public enum TemplateDataProviderEnum {
    /**
     * data_provider_bool
     */
    DATA_PROVIDER_BOOL("data_provider_bool", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage())),
    DATA_PROVIDER_DATE("data_provider_date", Arrays.asList(DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage())),
    DATA_PROVIDER_STRING("data_provider_string", Arrays.asList(DataVariableTypeEnum.STRING_TYPE.getMessage())),
    DATA_PROVIDER_NUMBER("data_provider_number", Arrays.asList(DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_LOCAL("data_provider_local", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage(), DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.STRING_TYPE.getMessage(), DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE("data_provider_leftValue", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage(), DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.STRING_TYPE.getMessage(), DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_STRING("data_provider_leftValue_string", Arrays.asList(DataVariableTypeEnum.STRING_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_NUMBER("data_provider_leftValue_number", Arrays.asList(DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_BOOL("data_provider_leftValue_bool", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_DATE("data_provider_leftValue_date", Arrays.asList(DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_OBJECT("data_provider_leftValue_object", Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage())),
    DATA_PROVIDER_OBJECT("data_provider_object", Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage())),


    DATA_PROVIDER_LEFTVALUE_ARRAY("data_provider_leftValue_array", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage(), DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.STRING_TYPE.getMessage(), DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage(), DataVariableTypeEnum.OBJECT_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_ARRAY_STRING("data_provider_leftValue_array_string", Arrays.asList(DataVariableTypeEnum.STRING_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_ARRAY_NUMBER("data_provider_leftValue_array_number", Arrays.asList(DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_ARRAY_BOOL("data_provider_leftValue_array_bool", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_ARRAY_DATE("data_provider_leftValue_array_date", Arrays.asList(DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage())),
    DATA_PROVIDER_LEFTVALUE_ARRAY_OBJECT("data_provider_leftValue_array_object", Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY("data_provider_array", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage(), DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.STRING_TYPE.getMessage(), DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage(), DataVariableTypeEnum.OBJECT_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY_OBJECT("data_provider_array_object", Arrays.asList(DataVariableTypeEnum.OBJECT_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY_NUMBER("data_provider_array_number", Arrays.asList(DataVariableTypeEnum.INT_TYPE.getMessage(), DataVariableTypeEnum.DOUBLE_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY_BOOL("data_provider_array_bool", Arrays.asList(DataVariableTypeEnum.BOOLEAN_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY_DATE("data_provider_array_date", Arrays.asList(DataVariableTypeEnum.DATE_TYPE.getMessage(), DataVariableTypeEnum.DATETIME_TYPE.getMessage())),
    DATA_PROVIDER_ARRAY_STRING("data_provider_array_string", Arrays.asList(DataVariableTypeEnum.STRING_TYPE.getMessage())),;

    private String providerName;
    private List<String> typeList;

    /**
     * fromName
     * @param name String
     * @return TemplateDataProviderEnum
     */
    public static TemplateDataProviderEnum fromName(String name) {
        for (TemplateDataProviderEnum provider : values()) {
            if (provider.name().equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}

