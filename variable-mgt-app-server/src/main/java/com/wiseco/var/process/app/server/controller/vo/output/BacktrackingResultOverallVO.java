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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author xupei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量回溯结果 整体情况VO")
public class BacktrackingResultOverallVO implements Serializable {

    @Schema(description = "任务执行记录")
    IPage<TaskInfo> taskInfoPage;

    @Schema(description = "调用外数信息")
    List<OutsideTaskInfo> outsideTaskInfos;

    @Schema(description = "选择任务编号")
    List<String> codes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "任务执行记录")
    public static class TaskInfo implements Serializable {

        private static final long serialVersionUID = 8799865908944973992L;

        @Schema(description = "任务id")
        private Long id;

        @Schema(description = "批量回溯id")
        private Long backtrackingId;

        @Schema(description = "执行状态")
        private BacktrackingTaskStatusEnum status;

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
        private Integer averageResponseTime;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "调用外数信息")
    public static class OutsideTaskInfo implements Serializable {
        @Schema(description = "外数名称")
        private String outsideServiceName;

        @Schema(description = "取值方式")
        private BacktrackingOutsideCallStrategyEnum outsideServiceStrategy;

        @Schema(description = "调用量")
        private Long callSize;

        @Schema(description = "成功率")
        private Long successRate;

        @Schema(description = "查得率")
        private Long findRate;

        @Schema(description = "最大响应时间")
        private Long maximumResponseTime;

        @Schema(description = "最小响应时间")
        private Long minimumResponseTime;

        @Schema(description = "平均响应时间")
        private Long averageResponseTime;

        @Schema(description = "外部服务id")
        private Long outsideServiceId;

        @Schema(description = "外部服务编码")
        private String outsideServiceCode;

    }
}
