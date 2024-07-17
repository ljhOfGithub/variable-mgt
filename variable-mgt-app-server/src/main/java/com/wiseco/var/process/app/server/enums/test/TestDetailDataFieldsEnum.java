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
package com.wiseco.var.process.app.server.enums.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试明细数据字段枚举类
 *
 * @author wangxianli
 */
@Getter
@AllArgsConstructor
public enum TestDetailDataFieldsEnum {

    /**
     * 主信息编号
     */
    ID("id", "主信息编号"),

    PARENT_ID("parentId", "父级信息编号"),

    INDEX("index", "索引"),

    NAME("name", "字段名"),

    LABEL("label", "中文描述"),

    TYPE("type", "数据类型"),

    IS_ARR("isArr", "是否数组"),

    /**
     * 字段类型：0-输入，1-预期结果，2-输出
     */
    FIELD_TYPE("fieldType", "字段类型");

    private String code;
    private String message;

    /**
     * getCode
     *
     * @param code code
     * @return com.wiseco.var.process.app.server.enums.test.TestDetailDataFieldsEnum
     */
    public static TestDetailDataFieldsEnum getCode(String code) {
        for (TestDetailDataFieldsEnum location : TestDetailDataFieldsEnum.values()) {
            if (location.getCode().equals(code)) {
                return location;
            }
        }
        return null;
    }

}
