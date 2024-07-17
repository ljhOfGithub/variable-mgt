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

import com.wiseco.boot.commons.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 外数执行记录查询 VO
 * @author wuweikang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "外数执行记录查询 VO")
public class BacktrackingTaskOutsideDataQueryVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "外部服务ID")
    @NotNull
    private Long serviceId;

    @Schema(description = "任务批次号")
    @NotEmpty
    private String batchNo;

    @Schema(description = "查询状态")
    private Integer callSuccess;

    @Schema(description = "是否查得")
    private Integer businessSuccess;

    @Schema(description = "内部流水号=主体唯一标识")
    private String businessSerialNo;

    @Schema(description = "外部调用流水号")
    private String decisionSerialNo;

    @Schema(description = "调用时间(起)")
    private String reuqestStartDateFrom;

    @Schema(description = "调用时间(止)")
    private String reuqestStartDateTo;

    @Schema(description = "响应时长(起)")
    private Integer costMillisecondFrom;

    @Schema(description = "响应时长(止)")
    private Integer costMillisecondTo;

    @Schema(description = "重试次数(起)")
    private Integer tryTimesFrom;

    @Schema(description = "重试次数(止)")
    private Integer tryTimesTo;

}
