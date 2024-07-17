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
import org.apache.commons.lang3.StringUtils;

/**
 * @author ycc
 * @since 2023/1/6 11:35
 */
@AllArgsConstructor
@Getter
public enum TimeUnit {

    /**
     * 时
     */
    HOUR(1),

    /**
     * 分
     */
    MINUTE(2),

    /**
     * 秒
     */
    SECOND(3);

    private int code;

    /**
     * 获取时间code
     * @param code code
     * @return TimeUnit枚举
     */
    public static TimeUnit getByCode(int code) {
        TimeUnit[] values = TimeUnit.values();
        for (TimeUnit timeUnit : values) {
            if (timeUnit.code == code) {
                return timeUnit;
            }
        }
        return null;
    }

    /**
     * 获取枚举名
     * @param retrySpanUnit 枚举名
     * @return TimeUnit
     */
    public static TimeUnit getByName(String retrySpanUnit) {
        if (StringUtils.isEmpty(retrySpanUnit)) {
            return null;
        }
        TimeUnit[] values = TimeUnit.values();
        for (TimeUnit timeUnit : values) {
            if (timeUnit.name().equals(retrySpanUnit)) {
                return timeUnit;
            }
        }
        return null;
    }
}
