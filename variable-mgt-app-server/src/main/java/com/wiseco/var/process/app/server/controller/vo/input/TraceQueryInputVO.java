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

import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Trace日志
 * @author wuweikang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trace日志 列表查询vo")
public class TraceQueryInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716363L;

    @Schema(description = "引擎流水号")
    @NotNull(message = "引擎流水号不能为空")
    private Long engineSerialNo;

    @Schema(description = "节点类型")
    private String traceNodeType;

    @Schema(description = "节点状态 0.异常 1.正常")
    private Integer nodeState;

    @Schema(description = "耗时时间（始）")
    private Long startTime;

    @Schema(description = "耗时时间（止）")
    private Long endTime;

    @Schema(description = "节点名称")
    private String nodeName;
}
