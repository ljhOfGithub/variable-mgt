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
package com.wiseco.var.process.app.server.job.backtracking;

import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;

/**
 * 批量回溯定时任务执行
 *
 * @author xupei
 */
@Component
@Slf4j
public class BacktrackingProcessor implements BasicProcessor {

    @Resource
    BacktrackingTaskBiz backtrackingTaskBiz;

    @Override
    public ProcessResult process(TaskContext taskContext) {
        return backtrackingTaskBiz.scheduledExecute(taskContext);
    }
}
