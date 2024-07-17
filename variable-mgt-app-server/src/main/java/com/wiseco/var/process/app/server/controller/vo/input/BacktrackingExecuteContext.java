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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskBiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xu pei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BacktrackingExecuteContext {
    /**
     * 批量回溯信息
     */
    private VarProcessBatchBacktracking varProcessBatchBacktracking;
    /**
     * 任务信息
     */
    private VarProcessBatchBacktrackingTask varProcessBatchBacktrackingTask;
    /**
     * 数据模型使用的外数服务的取值方式映射
     */
    private Map<String, String> outsideServiceStrategyMap;
    /**
     * 总条数
     */
    private Integer total;
    /**
     * 执行进度计数器
     */
    private AtomicInteger counter;
    /**
     * 开启Trace日志
     */
    private Boolean enableTrace;
    /**
     * 结果集表信息
     */
    private BacktrackingTaskBiz.SavaResultDataParam savaResultDataParam;
}
