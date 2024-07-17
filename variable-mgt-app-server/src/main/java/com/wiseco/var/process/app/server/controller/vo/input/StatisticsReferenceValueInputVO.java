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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author mingao
 * @since 2023/10/19
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "统计分析基准指标数据项查询 VO")
public class StatisticsReferenceValueInputVO {

    @Schema(description = "实时服务ID", required = true)
    private Long varProcessServiceId;

    @Schema(description = "变量清单ID", required = true)
    private Long varProcessManifestId;

    @Schema(description = "开始时间", example = "1970-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束时间", example = "2000-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "基准指标调用时间段", example = "true 与分析设置时间段一致/false 所有时间段")
    private Boolean baseIndexCallDate;

    @Schema(description = "指标名称")
    private String indexName;
}
