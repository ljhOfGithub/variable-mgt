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
 * 监控报表状态的枚举
 */

@Getter
@AllArgsConstructor
public enum ReportFromStateEnum {

    /**
     * <ul>
     *     <li>UP: 启用</li>
     *     <li>DOWN: 停用</li>
     *     <li>EDIT: 编辑中</li>
     * </ul>
     */

    UP("启用"),

    DOWN("停用"),

    EDIT("编辑中");

    private String desc;

    /**
     * 根据枚举获取枚举
     * @param input 报表的种类枚举
     * @return 报表的种类枚举
     */
    public ReportFromStateEnum get(ReportFromStateEnum input) {
        for (ReportFromStateEnum stateEnum : ReportFromStateEnum.values()) {
            if (stateEnum.equals(input)) {
                return stateEnum;
            }
        }
        return null;
    }
}
