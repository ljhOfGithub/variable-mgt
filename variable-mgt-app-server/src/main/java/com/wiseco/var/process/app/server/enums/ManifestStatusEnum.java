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
 * 变量清单状态
 *
 * @author wangxsiansheng
 */
@AllArgsConstructor
@Getter
public enum ManifestStatusEnum {
    /**
     * 变量清单状态枚举
     */

    EDIT(0, "编辑中"),

    TESTING(1, "测试中"),

    UNAPPROVED(2, "待审核"),

    REFUSE(3, "审核拒绝"),

    UPING(4, "启用中"),

    UP(5, "启用"),

    DOWN(6, "停用"),

    PUBLISHFAILED(7, "发布失败");
    //状态 0: 编辑中, 1: 测试中, 2: 待审核, 3: 审核拒绝, 4: 启用中, 5: 启用, 6: 停用, 7: 发布失败

    private Integer code;
    private String desc;

    /**
     * 根据code获取状态
     *
     * @param code 枚举code
     * @return ManifestStatusEnum
     */
    public static ManifestStatusEnum getStatus(Integer code) {
        for (ManifestStatusEnum statusEnum : ManifestStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 根据name获取状态
     *
     * @param str 枚举name
     * @return ManifestStatusEnum
     */
    public static ManifestStatusEnum getStatustr(String str) {
        for (ManifestStatusEnum statusEnum : ManifestStatusEnum.values()) {
            if (statusEnum.name().equals(str)) {
                return statusEnum;
            }
        }
        return null;
    }

}
