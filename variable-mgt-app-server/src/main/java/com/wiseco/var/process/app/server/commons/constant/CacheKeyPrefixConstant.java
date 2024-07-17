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
package com.wiseco.var.process.app.server.commons.constant;

/**
 * @author ycc
 * @since 2023/3/27 14:28
 */
public class CacheKeyPrefixConstant {

    /**
     * 批量回溯任务重试次数缓存记录
     */
    public static final String BACKTRACKING_TASK_RETRY_NUM = "mgt_backtracking_task_data_retry_num_";
    /**
     * 变量导入KEY
     */
    public static final String VAR_IMPORT = "VAR_IMPORT_";

    /**
     * key:正常次数
     */
    public static final String VAR_MONITORING_ALTER_RESTORE_COUNT = "VAR:MonitoringAlter:RestoreCount:";

    /**
     * key:最后一次发送短信/邮件的时间
     */
    public static final String VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE = "VAR:MonitoringAlter:LastSendMessageDate:";

    /**
     * 定时任务
     */
    public static final String CACHE_KEY_PRI_FEX = "variable_mgt:job:";

    /**
     * 变量清单的复制前缀
     */
    public static final String MANIFEST_DUPLICATE_PREFIX = "manifest_duplicate_prefix:";
    /**
     * 批量回溯消息发送情况，用于判断是否可暂停
     */
    public static final String BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE = "BACKTRACKING_TASK_MESSAGE_SEND_COMPLETE:";
}
