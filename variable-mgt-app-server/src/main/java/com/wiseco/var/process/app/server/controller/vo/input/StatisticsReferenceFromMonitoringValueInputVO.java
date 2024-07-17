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

import com.wiseco.var.process.app.server.enums.MonitoringConfTimeUnitEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mingao
 * @since 2023/10/19
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "监控——统计分析基准指标数据项查询 VO")
public class StatisticsReferenceFromMonitoringValueInputVO {

    @Schema(description = "实时服务名称", required = true)
    private String serviceName;

    @Schema(description = "实时服务版本", required = true)
    private Integer serviceVersion;

    @Schema(description = "变量清单ID", required = true)
    private Long varProcessManifestId;

    @Schema(description = "指标名称")
    private String indexName;

    @Schema(description = "基准指标调用时间段", example = "true 与设置时间维度一致/false 所有时间段")
    private Boolean baseIndexCallDate;

    @Schema(description = "时间,与设置时间维度一致时必传")
    private Integer time;

    @Schema(description = "时间单位,与设置时间维度一致时必传")
    private MonitoringConfTimeUnitEnum timeUnit;
}
