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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.wiseco.decision.engine.var.runtime.context.TraceLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 测试执行结果 DTO
 *
 * @author wangxianli
 */
@Schema(description = "测试执行结果DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingExecuteResultDto implements Serializable {
    /**
     * code
     */
    private String code;
    /**
     * 主体唯一标识
     */
    private String serialNo;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 请求数据
     */
    private String requestJson;
    /**
     * 引擎使用数据
     */
    private String engineJson;
    /**
     * 结果数据
     */
    private String responseJson;
    /**
     * 测试执行异常内容
     */
    private String exceptionInfo;
    /**
     * 任务开始时间
     */
    private long startTime;
    /**
     * 任务结束时间
     */
    private long endTime;
    /**
     * trace
     */
    private List<TraceLog> traceLogs;
}
