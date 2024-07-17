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
 * 启停用枚举类
 */
public enum EnableDisableEnum {
    /**
     * 停用
     */
    DISABLE(0, "停用"),
    /**
     * 启用
     */
    ENABLE(1, "启用");

    private Integer value;

    private String desc;
}
