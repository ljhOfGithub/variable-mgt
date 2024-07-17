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
package com.wiseco.var.process.app.server.service.dto;

import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacktrackingTaskDetailDto implements Serializable {

    @Schema(description = "执行状态")
    private BacktrackingTaskStatusEnum status;

    @Schema(description = "任务id")
    private Long id;

    @Schema(description = "批量回溯id")
    private Long backtrackingId;

    @Schema(description = "任务编号")
    private String code;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "耗时")
    private Long duration;

    @Schema(description = "完成情况")
    private String completion;

    @Schema(description = "成功率")
    private String success;

    @Schema(description = "最大响应时间")
    private Integer maximumResponseTime;

    @Schema(description = "最小响应时间")
    private Integer minimumResponseTime;

    @Schema(description = "平均响应时间")
    private Float                    averageResponseTime;

    @Schema(description = "结果文件信息")
    private String                     resultFileInfo;

}
