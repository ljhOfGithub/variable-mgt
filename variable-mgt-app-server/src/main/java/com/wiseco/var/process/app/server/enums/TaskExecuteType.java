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
 * @since  2022/10/17 15:28
 */
@Getter
@AllArgsConstructor
public enum TaskExecuteType {
    /**
     * 启动后执行一次
     */
    ONCE(1),
    /**
     * 定时任务
     */
    TIMING(2);

    private Integer code;

    /**
     * 根据code获取枚举
     *
     * @param excuteType 枚举code
     * @return TaskExecuteType
     */
    public static TaskExecuteType getByCode(Integer excuteType) {
        if (excuteType == null) {
            return null;
        }
        TaskExecuteType[] values = TaskExecuteType.values();
        for (TaskExecuteType value : values) {
            if (value.getCode().intValue() == excuteType) {
                return value;
            }
        }
        return null;
    }
}
