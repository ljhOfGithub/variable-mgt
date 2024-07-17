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

/**
 * @author Asker.J
 * @since 2022/10/31
 */

/**
 * 开关枚举
 */
public enum SwitchEnum {
    /**
     * START
     */
    START,
    /**
     * STOP
     */
    STOP;

    /**
     * getByEnumName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.SwitchEnum
     */
    public static SwitchEnum getByEnumName(String name) {
        for (SwitchEnum switchEnum : SwitchEnum.values()) {
            if (switchEnum.name().equals(name)) {
                return switchEnum;
            }
        }
        return null;
    }
}
