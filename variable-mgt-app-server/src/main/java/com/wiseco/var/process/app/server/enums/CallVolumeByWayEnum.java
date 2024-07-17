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

import lombok.Getter;

@Getter
public enum CallVolumeByWayEnum {
    /**
     * 按照数量
     */
    NUMBER("number"),

    /**
     * 按照比例
     */
    RATIO("ratio");

    private String name;

    CallVolumeByWayEnum(String name) {
        this.name = name;
    }

    /**
     * 从名称中获取Enum
     *
     * @param name 枚举名
     * @return CallVolumeByWayEnum
     */
    public static CallVolumeByWayEnum getEnumFromName(String name) {
        for (CallVolumeByWayEnum item : CallVolumeByWayEnum.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

}
