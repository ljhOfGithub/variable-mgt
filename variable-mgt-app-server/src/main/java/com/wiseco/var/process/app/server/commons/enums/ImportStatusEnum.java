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
package com.wiseco.var.process.app.server.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: fudengkui
 */
@Getter
@AllArgsConstructor
/**
 * 导入状态枚举类
 */
public enum ImportStatusEnum {
    /**
     * 未导入
     */
    NOT_IMPORTED(0, "未导入"),
    /**
     * 已导入
     */
    IMPORTED(1, "已导入"),
    /**
     * 重复类，不可导入
     */
    REPEAT(2, "重复类，不可导入");

    private Integer status;

    private String desc;

}
