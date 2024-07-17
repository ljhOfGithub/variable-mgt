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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.job.backtracking.BacktrackingProcessor;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author  ycc
 * @since  2023/1/12 17:18
 */
public class BaseJobInfoFactory {
    protected static final Map<String, BaseJobConfig> PROCESSOR_JOB_CONFIG;

    static {
        /** 数据导入 **/
        //动态数据导入失败重试，{0}为configId，cron来自页面设置
        Map<String, BaseJobConfig> map = new HashMap<>(MagicNumbers.EIGHT);
        map.put(BacktrackingProcessor.class.getSimpleName(),new BaseJobConfig("backtracking-task-retry-job-{0}-{1}", ""));
        PROCESSOR_JOB_CONFIG = Collections.unmodifiableMap(map);
    }

    /**
     * 获取任务处理器
     * @param jobProcessor 工作的处理器
     * @return BaseJobConfig
     */
    public static BaseJobConfig getBaseJobConfig(Class<? extends BasicProcessor> jobProcessor) {
        return PROCESSOR_JOB_CONFIG.get(jobProcessor.getSimpleName());
    }
}
