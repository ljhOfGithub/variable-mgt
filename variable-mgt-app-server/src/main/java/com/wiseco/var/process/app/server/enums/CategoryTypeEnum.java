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
 * @author xiongzhewen
 * 分类对象枚举类
 */

@Getter
@AllArgsConstructor
public enum CategoryTypeEnum {

    /**
     * 变量分类
     */
    VARIABLE("变量分类"),

    /**
     * 标签
     */
    TAG("标签"),

    /**
     * 变量模板分类
     */
    VARIABLE_TEMPLATE("变量模版分类"),

    /**
     * 公共方法分类
     */
    FUNCTION("公共方法分类"),

    /**
     * 变量清单分类
     */
    MANIFEST("变量清单分类"),

    /**
     * 实时服务分类
     */
    SERVICE("实时服务分类");

    private String desc;

    /**
     * 获取枚举值
     *
     * @param input CategoryTypeEnum
     * @return CategoryTypeEnum
     */
    public CategoryTypeEnum get(CategoryTypeEnum input) {
        for (CategoryTypeEnum c : CategoryTypeEnum.values()) {
            if (c.equals(input)) {
                return c;
            }
        }
        return null;
    }
}
