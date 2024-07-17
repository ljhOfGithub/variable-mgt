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

@AllArgsConstructor
@Getter
public enum VarMathSymbolTypeEnum {
    /**
     * 包括
     */
    INCLUDE("include"),
    /**
     * 大于
     */
    MORE_THAN(">"),
    /**
     * 小于
     */
    LESS_THAN("<"),

    /**
     * 大于等于
     */
    GREATER_EQUAL(">="),
    /**
     * 小于等于
     */
    LESS_EQUAL("<="),

    /**
     * 等于
     */
    EQUAL("="),

    /**
     * 为空
     */
    EMPTY("empty");

    private String desc;
}
