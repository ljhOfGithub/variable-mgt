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
package com.wiseco.var.process.app.server.service.backtracking.async;

import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xupei
 */
@Slf4j
@Service
public class AsyncBacktrackingExecute {

    @Resource
    BacktrackingTaskBiz backtrackingTaskBiz;

    /**
     * 手动异步执行
     *
     * @param param 任务参数
     */
    @Async
    public void asyncExecute(BacktrackingTaskParam param) {
        log.info("BacktrackingTask手动执行开始");
        backtrackingTaskBiz.executeTask(param);
        log.info("BacktrackingTask手动执行结束");
    }
}
