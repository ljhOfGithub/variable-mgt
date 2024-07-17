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
package com.wiseco.var.process.app.server.job;

import lombok.Data;

/**
 * @author  ycc
 * @since  2022/10/21 15:29
 */
@Data
public class BaseJobConfig {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务执行计划的cron表达式
     */
    private String jobCron;

    /**
     * 任务信息
     * @param jobName 任务名
     * @param jobCron 任务corn表达式
     */
    public BaseJobConfig(String jobName, String jobCron) {
        this.jobName = jobName;
        this.jobCron = jobCron;
    }
}
