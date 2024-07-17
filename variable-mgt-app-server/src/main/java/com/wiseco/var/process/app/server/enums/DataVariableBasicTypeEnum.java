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
 * 变量数据基础类型
 * @author wangxianli
 * @since : 2021/10/25
 */
@AllArgsConstructor
@Getter
public enum DataVariableBasicTypeEnum {

    /**
     * int
     */
    INT_TYPE("int"),
    /**
     * double
     */
    DOUBLE_TYPE("double"),
    /**
     * boolean
     */
    BOOLEAN_TYPE("boolean"),
    /**
     * string
     */
    STRING_TYPE("string"),
    /**
     * date
     */
    DATE_TYPE("date"),
    /**
     * datetime
     */
    DATETIME_TYPE("datetime"),;

    private String name;

    /**
     * getNameEnum
     * @param name String
     * @return DataVariableBasicTypeEnum
     */
    public static DataVariableBasicTypeEnum getNameEnum(String name) {
        for (DataVariableBasicTypeEnum dataVariableTypeEnum : DataVariableBasicTypeEnum.values()) {
            if (dataVariableTypeEnum.getName().equalsIgnoreCase(name)) {
                return dataVariableTypeEnum;
            }
        }
        return null;
    }
}

