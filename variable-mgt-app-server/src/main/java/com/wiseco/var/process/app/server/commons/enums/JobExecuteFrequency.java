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

import org.apache.commons.lang3.StringUtils;

/**
 * @author ycc
 * @since 2023/2/3 11:14
 */
public enum JobExecuteFrequency {
    /**
     * 每日
     */
    EVERY_DAY,
    /**
     * 每月
     */
    EVERY_MONTH,
    /**
     * 每周
     */
    EVERY_WEEK,
    /**
     * 每季度
     */
    EVERY_QUARTER,
    /**
     * cron表达式
     */
    CRON,
    /**
     * 固定日期
     */
    TARGET;

    JobExecuteFrequency() {
    }

    /**
     * 枚举还原
     * @param name 名称
     * @return JobExecuteFrequency
     */
    public static JobExecuteFrequency getByEnumName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (JobExecuteFrequency frequencyEnum : JobExecuteFrequency.values()) {
            if (frequencyEnum.name().equals(name)) {
                return frequencyEnum;
            }
        }
        return null;
    }
}
