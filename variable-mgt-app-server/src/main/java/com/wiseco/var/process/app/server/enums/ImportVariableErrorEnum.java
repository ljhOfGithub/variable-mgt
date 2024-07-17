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

@AllArgsConstructor
@Getter
public enum ImportVariableErrorEnum {
    /**
     * 变量导入Excel表头枚举
     */
    VARIABLE_NAME_NONE("变量名称缺失"), VARIABLE_CODE_NONE("变量编码缺失"), VARIABLE_CLASS_NONE("变量分类缺失"), DATE_TYPE_NONE("数据类型缺失"), VARIABLE_NAME_REPEAT(
            "变量名称重复"), VARIABLE_CODE_REPEAT(
            "变量编码重复"), VARIABLE_CLASS_NOT_EXIST(
            "变量分类不存在"), DATE_TYPE_NOT_RIGHTFUL(
            "数据类型不匹配");

    private String errorName;
}
