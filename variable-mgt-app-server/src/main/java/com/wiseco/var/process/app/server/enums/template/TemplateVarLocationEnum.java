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

import java.util.ArrayList;
import java.util.List;

/**
 * 模板变量位置枚举
 *
 * @author wiseco
 */
@Getter
@AllArgsConstructor
public enum TemplateVarLocationEnum {
    /**
     * input
     */
    INPUT("input"),
    OUTPUT("output"),
    ENGINE_VARS("engineVars"),
    PARAMETER_INPUT("parameter_input"),
    PARAMETER_OUTPUT("parameter_output"),
    PARAMETERS("parameters"),
    LOCAL_VARS("localVars"),
    EXTERNAL_DATA("externalData"),
    COMMON_DATA("commonData"),
    BLAZE_DATA("blazeData"),
    EXTERNAL_VARS("externalVars"),
    INTERNAL_DATA("internalData"),
    RAW_DATA("rawData"),
    PARAMETER("parameter");

    private final String displayName;

    /**
     * fromName
     *
     * @param name name
     * @return com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum
     */
    public static TemplateVarLocationEnum fromName(String name) {
        for (TemplateVarLocationEnum location : values()) {
            if (location.name().equalsIgnoreCase(name)) {
                return location;
            }
        }
        return null;
    }

    /**
     * fromDisplayName
     *
     * @param displayName String
     * @return TemplateVarLocationEnum
     */
    public static TemplateVarLocationEnum fromDisplayName(String displayName) {
        for (TemplateVarLocationEnum location : values()) {
            if (location.getDisplayName().equalsIgnoreCase(displayName)) {
                return location;
            }
        }
        return null;
    }

    /**
     * templateVarLocationEnumList
     *
     * @return list
     */
    public static List<String> templateVarLocationEnumList() {
        List<String> list = new ArrayList<>();
        for (TemplateVarLocationEnum location : values()) {
            list.add(location.getDisplayName());
        }
        return list;
    }
}
