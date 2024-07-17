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
 * 报表的展示维度枚举
 */

@Getter
@AllArgsConstructor
public enum ReportFormDisplayDimensionEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li>TIME_SCOPE: 时间范围</li>
     *     <li>MONITOR_OBJECT: 监控对象</li>
     *     <li>MANIFEST: 变量清单</li>
     * </ul>
     */

    TIME_SCOPE("时间范围"),

    MONITOR_OBJECT("监控对象"),

    MANIFEST("变量清单");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public ReportFormDisplayDimensionEnum get(ReportFormDisplayDimensionEnum input) {
        for (ReportFormDisplayDimensionEnum displayDimensionEnum : ReportFormDisplayDimensionEnum.values()) {
            if (displayDimensionEnum.equals(input)) {
                return displayDimensionEnum;
            }
        }
        return null;
    }
}
