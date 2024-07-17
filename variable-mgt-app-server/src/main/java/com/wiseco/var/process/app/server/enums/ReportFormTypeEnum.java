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
 * 报表的类型枚举
 */

@Getter
@AllArgsConstructor
public enum ReportFormTypeEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li>LINE_CHART: 折线图</li>
     *     <li>AREA_CHART: 面积图</li>
     *     <li>HISTOGRAM: 柱状图</li>
     *     <li>TOP_CHART: TOP图</li>
     *     <li>RING_CHART: 环形图</li>
     *     <li>TABLE: 表格</li>
     * </ul>
     */

    LINE_CHART("折线图"),

    AREA_CHART("面积图"),

    HISTOGRAM("柱状图"),

    TOP_CHART("TOP图"),

    RING_CHART("环形图"),

    TABLE("表格");

    private String desc;


    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public ReportFormTypeEnum get(ReportFormTypeEnum input) {
        for (ReportFormTypeEnum typeEnum : ReportFormTypeEnum.values()) {
            if (typeEnum.equals(input)) {
                return typeEnum;
            }
        }
        return null;
    }
}
