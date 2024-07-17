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
 * 变量位置枚举
 *
 * @author wiseco
 */
@AllArgsConstructor
@Getter
public enum PositionVarEnum {
    /**
     * input
     */
    INPUT("input"),
    OUTPUT("output"),
    ENGINE_VARS("engineVars"),
    EXTERNAL_DATA("externalData"),
    COMMON_DATA("commonData"),
    BLAZE_DATA("blazeData"),
    EXTERNAL_VARS("externalVars"),
    PARAMETERS("parameters"),
    PARAMETERS_IN("in"),
    PARAMETERS_OUT("out"),
    LOCAL_VARS("localVars"),
    INTERNAL_DATA("InternalData"),
    RAW_DATA("rawData"),
    VARS("vars"),
    // 决策执行更新的输入数据 (对应扩展字段 extend: 1)
    UPDATED_INPUT("updatedInput");

    private String name;

    /**
     * fromName
     * @param name String
     * @return PositionVarEnum
     */
    public static PositionVarEnum fromName(String name) {
        for (PositionVarEnum location : values()) {
            if (location.getName().equals(name)) {
                return location;
            }
        }
        return null;
    }


}
