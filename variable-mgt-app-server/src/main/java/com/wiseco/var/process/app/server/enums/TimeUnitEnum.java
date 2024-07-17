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


@Getter
@AllArgsConstructor
public enum TimeUnitEnum {

    /**
     * 类型(String)
     * <ul>
     *     <li></li>
     * </ul>
     */

    HOUR("小时"),

    DAY("天"),

    WEEK("周"),

    MONTH("月");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public TimeUnitEnum get(TimeUnitEnum input) {
        for (TimeUnitEnum unitEnum : TimeUnitEnum.values()) {
            if (unitEnum.equals(input)) {
                return unitEnum;
            }
        }
        return null;
    }
}
