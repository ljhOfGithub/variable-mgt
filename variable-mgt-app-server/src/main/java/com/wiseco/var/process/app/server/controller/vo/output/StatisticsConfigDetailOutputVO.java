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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 返回给前端的配置详情实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "返回给前端的配置详情实体类")
public class StatisticsConfigDetailOutputVO implements Serializable {

    private static final long serialVersionUID = 4111201259607250922L;

    @Schema(description = "统计分析ID", required = true)
    private Long statisticsConfigId;

    @Schema(description = "实时服务ID", required = true)
    private Long varProcessServiceId;

    @Schema(description = "变量清单ID", required = true)
    private Long varProcessManifestId;

    @Schema(description = "开始时间", example = "1970-01-01 ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @Schema(description = "结束时间", example = "2000-01-01 ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;

    @Schema(description = "分析指标", required = true)
    private AnalysisIndexMappingVo indexMappingVo;

    @Schema(description = "iv值计算参数设置", required = true)
    private ConfigIvMappingVo ivMappingVo;

    @Schema(description = "psi计算参数配置", required = true)
    private ConfigPsiMappingVo psiMappingVo;

    @Schema(description = "特殊值参数配置", required = true)
    private List<ConfigSpecialMappingVo> specialMappingVoList;
}
