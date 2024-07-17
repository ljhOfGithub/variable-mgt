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
package com.wiseco.var.process.app.server.event.obj;

import com.wiseco.var.process.app.server.event.BaseEvent;
import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;

/**
 * 定时任务执行失败事件
 * @author wuweikang
 * @since 2023/10/07
 */
public class BacktrackingTaskFailEvent  extends BaseEvent<BacktrackingTaskParam> {

    private static final long            serialVersionUID = 1L;

    private final BacktrackingTaskParam param;

    /**
     * 定时任务执行失败事件
     * @param source 批量回溯任务源
     */
    public BacktrackingTaskFailEvent(BacktrackingTaskParam source) {
        super(source);
        this.param = source;
    }

    /**
     * 获取参数
     * @return BacktrackingTaskParam
     */
    public BacktrackingTaskParam getParam() {
        return param;
    }
}
