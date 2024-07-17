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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trace日志列表出参vo")
public class TraceListOutputVO implements Serializable {

    private static final long serialVersionUID = 17900212400L;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "变量名称")
    private String variableName;

    @Schema(description = "执行时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "执行耗时")
    private Long   duration;

    @Schema(description = "节点状态 1.正常 2.异常")
    private Integer nodeState;

    @Schema(description = "异常描述")
    private String exceptionInfo;

    @Schema(description = "备注")
    private String remark;
}
