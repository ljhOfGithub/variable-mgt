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

import java.util.Arrays;
import java.util.List;


@Getter
@AllArgsConstructor
public enum StreamProcessPeriodEnum {

    /**
     * 流式变量统计周期枚举
     */
    RECENT("最近", Arrays.asList(TimeUnitEnum.SECOND,TimeUnitEnum.MINUTE,TimeUnitEnum.HOUR,TimeUnitEnum.DAY)),
    PREVIOUS("上一",Arrays.asList(TimeUnitEnum.MINUTE,TimeUnitEnum.HOUR,TimeUnitEnum.DAY)),
    CURRENT("当前",Arrays.asList(TimeUnitEnum.MINUTE,TimeUnitEnum.HOUR,TimeUnitEnum.DAY));

    private String desc;
    private List<TimeUnitEnum> timeUnit;

    @Getter
    @AllArgsConstructor
    public enum TimeUnitEnum {
        /**
         * 时间刻度
         */
        SECOND,MINUTE,HOUR,DAY;
    }
}
