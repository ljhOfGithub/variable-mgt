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
 * 变量导入Excel表头枚举
 */
@AllArgsConstructor
@Getter
public enum VariableExcelHeadEnum {
    /**
     * 变量导入Excel表头
     */
    VARIABLE_NAME("变量名称"), VARIABLE_CODE("变量编码"), VARIABLE_CLASS("变量分类"), DATE_TYPE("数据类型"), DESCRIPTION("描述");
    private String headName;

    /**
     * 根据name获取枚举
     *
     * @param name 名称
     * @return VariableExcelHeadEnum
     */
    public static VariableExcelHeadEnum get(String name) {
        if (name == null) {
            return null;
        }
        for (VariableExcelHeadEnum headEnum : VariableExcelHeadEnum.values()) {
            if (headEnum.getHeadName().equals(name)) {
                return headEnum;
            }
        }
        return null;
    }
}
