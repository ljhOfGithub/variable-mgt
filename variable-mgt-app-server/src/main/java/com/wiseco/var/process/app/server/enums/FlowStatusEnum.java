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
 * 流程状态 枚举类
 *
 * @author wangxianli
 * @since 2022/6/10
 */
@AllArgsConstructor
@Getter
public enum FlowStatusEnum {
    /**
     * 流程状态
     */

    EDIT("编辑中"),

    UP("启用"),

    DOWN("停用"),

    UNAPPROVED("待审核"),

    REFUSE("审核拒绝"),

    DELETE("已删除");

    private String desc;

    /**
     * 获取状态枚举
     *
     * @param str 枚举名
     * @return FlowStatusEnum
     */
    public static FlowStatusEnum getStatustr(String str) {
        for (FlowStatusEnum statusEnum : FlowStatusEnum.values()) {
            if (statusEnum.name().equals(str)) {
                return statusEnum;
            }
        }
        return null;
    }
}
