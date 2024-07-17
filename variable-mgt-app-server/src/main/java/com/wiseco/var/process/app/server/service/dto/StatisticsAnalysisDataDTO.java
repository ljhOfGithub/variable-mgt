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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计分析数据DTO
 * @author wuweikang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsAnalysisDataDTO {

    @Schema(description = "调用时间")
    private String callTime;

    @Schema(description = "调用量")
    private Integer totalCallNumber;

    @Schema(description = "成功量")
    private Integer successCallNumber;

    @Schema(description = "失败量")
    private Integer failCallNumber;

    @Schema(description = "最大用时(ms)")
    private Integer            maxResponseTime;

    @Schema(description = "平均用时(ms)")
    private Integer            avgResponseTime;

    @Schema(description = "最小用时(ms)")
    private Integer            minResponseTime;
}
