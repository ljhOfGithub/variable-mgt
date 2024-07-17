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
 * 监控报表的PSI枚举
 */

@Getter
@AllArgsConstructor
public enum ReportFormPsiEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li>DATETIME_SCOPE_DATA: 选择时间范围数据作为基准</li>
     *     <li>BASIC_INDICATOR: 选择基准指标</li>
     *     <li>MANIFEST: 选择清单</li>
     * </ul>
     */

    DATETIME_SCOPE_DATA("选择时间范围数据作为基准"),

    BASIC_INDICATOR("选择基准指标"),

    MANIFEST("选择清单");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public ReportFormPsiEnum get(ReportFormPsiEnum input) {
        for (ReportFormPsiEnum psiEnum : ReportFormPsiEnum.values()) {
            if (psiEnum.equals(input)) {
                return psiEnum;
            }
        }
        return null;
    }
}
