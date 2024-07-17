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
 * 是/否枚举
 *
 * @author: fudengkui
 * @since: 2023-01-30 10:49
 */

/**
 * 枚举
 */
@Getter
@AllArgsConstructor
public enum YesNoEnum {
    /**
     * NO
     */
    NO(0, "0", "否"),
    /**
     * YES
     */
    YES(1, "1", "是");

    private Integer value;

    private String strValue;

    private String desc;

}
