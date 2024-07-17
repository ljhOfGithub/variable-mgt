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
import lombok.NoArgsConstructor;

/**
 * 变量类型表的枚举类
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum VarProcessCategoryTypeEnum {

    /**
     * enabled字段的两种状态
     */
    DISABLED(0), ENABLED(1),

    /**
     * delete_flag字段的两种状态
     */
    DELETED(0), UNDELETED(1),

    /**
     * category_type字段的几种状态：变量
     */
    VARIABLE,

    /**
     * category_type字段的几种状态：变量模板
     */
    VARIABLE_TEMPLATE,

    /**
     * category_type字段的几种状态：公共方法
     */
    FUNCTION,

    /**
     * category_type字段的几种状态：变量清单
     */
    MANIFEST,

    /**
     * category_type字段的几种状态：实时服务
     */
    SERVICE;

    /**
     * 状态码
     */
    private Integer code;
}
