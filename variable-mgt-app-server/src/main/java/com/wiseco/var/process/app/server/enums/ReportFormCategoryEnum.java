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
 * 报表的分类枚举
 */

@Getter
@AllArgsConstructor
public enum ReportFormCategoryEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li>SERVICE: 服务报表</li>
     *     <li>SINGLE_VARIABLE_ANALYZE: 单指标分析报表</li>
     *     <li>VARIABLE_COMPARE_ANALYZE: 指标对比分析报表</li>
     * </ul>
     */

    SERVICE("服务报表","service"),

    SINGLE_VARIABLE_ANALYZE("单指标分析报表","singleVariable"),

    VARIABLE_COMPARE_ANALYZE("指标对比分析报表","variableCompare");

    private String desc;

    private String name;

    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public ReportFormCategoryEnum get(ReportFormCategoryEnum input) {
        for (ReportFormCategoryEnum categoryEnum : ReportFormCategoryEnum.values()) {
            if (categoryEnum.equals(input)) {
                return categoryEnum;
            }
        }
        return null;
    }
}
