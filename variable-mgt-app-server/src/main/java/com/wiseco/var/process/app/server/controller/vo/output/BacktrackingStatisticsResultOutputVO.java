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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 批量回溯统计结果列表
 *
 * @author wiseco
 * @since 2023/8/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量回溯结果 VO")
public class BacktrackingStatisticsResultOutputVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "指标名称")
    private String indexName;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "缺失值占比")
    private BigDecimal missingRatio;

    @Schema(description = "唯一值数量")
    private Integer uniqueNum;

    @Schema(description = "特殊值占比")
    private BigDecimal specialRatio;

    @Schema(description = "零值占比")
    private BigDecimal zeroRatio;

    @Schema(description = "最小值")
    private Double minimumVal;

    @Schema(description = "最大值")
    private Double maxVal;

    @Schema(description = "均值")
    private Double averageVal;

    @Schema(description = "分位数结果")
    private String percentageResult;

}
