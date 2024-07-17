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
 * 字段排序的枚举
 */

@Getter
@AllArgsConstructor
public enum OrderEnum {

    /**
     * 排序方式(String类型)
     * <ul>
     *     <li>DESC: 降序排序</li>
     *     <li>ASC: 升序排序</li>
     * </ul>
     */

    DESC("降序排序"),

    ASC("升序排序");

    private String description;

    /**
     * 根据输入的枚举获取枚举
     *
     * @param input OrderEnum值
     * @return OrderEnum
     */
    public OrderEnum get(OrderEnum input) {
        for (OrderEnum item : OrderEnum.values()) {
            if (item.equals(input)) {
                return item;
            }
        }
        return null;
    }
}
