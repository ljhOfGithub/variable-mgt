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
public enum CallVolumeByTimeEnum {
    /**
     * 当天
     */
    TODAY("today"),

    /**
     * 昨天
     */
    YESTERDAY("yesterday"),

    /**
     * 最近七天
     */
    LAST_SEVEN_DAYS("lastSevenDays"),

    /**
     * 最近30天
     */
    LAST_THIRTY_DAYS("lastThirtyDays"),

    /**
     * 最近3个月
     */
    LAST_TRIMESTER("lastTrimester");

    private String name;

    CallVolumeByTimeEnum(String name) {
        this.name = name;
    }

    /**
     * 从名称中获取Enum
     *
     * @param criterion 标准
     * @return CallVolumeByTimeEnum
     */
    public static CallVolumeByTimeEnum getEnumFromName(String criterion) {
        CallVolumeByTimeEnum callVolumeByTimeEnum = TODAY;
        for (CallVolumeByTimeEnum item : CallVolumeByTimeEnum.values()) {
            if (item.getName().equals(criterion)) {
                callVolumeByTimeEnum = item;
                break;
            }
        }
        return callVolumeByTimeEnum;
    }
}
