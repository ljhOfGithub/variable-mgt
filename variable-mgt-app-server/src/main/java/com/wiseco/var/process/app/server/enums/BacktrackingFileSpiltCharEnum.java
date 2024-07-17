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
 * @author wuweikang
 */
@Getter
@AllArgsConstructor
public enum BacktrackingFileSpiltCharEnum {
    /**
     * 逗号
     */
    COMMA(",", "逗号"),
    /**
     * TAB
     */
    TAB("\t", "TAB"),
    /**
     * LINE
     */
    LINE("\n", "换行"),
    /**
     * PIPE
     */
    PIPE("\\|", "竖线"),
    /**
     * SINGLE_QUOTES
     */
    SINGLE_QUOTES("'", "单引号"),
    /**
     * DOUBLE_QUOTES
     */
    DOUBLE_QUOTES("\"", "双引号"),
    /**
     * SOH
     */
    SOH("\001", "SOH"),
    /**
     * STX
     */
    STX("\002", "STX"),
    ETX("\003", "ETX"),
    OTHER("", "其他");

    private final String code;
    private final String desc;
}
