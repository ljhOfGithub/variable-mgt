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

import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * controller层中，统计分析配置创建入参VO
 *
 * @author yaoshun
 * @since 2023/9/12
 */
@Data
@Schema(description = "controller层中，统计分析配置创建入参VO")
public class BacktrackingOverallInputVO implements Serializable {

    private static final long serialVersionUID = 2013346166832342249L;

    @Schema(description = "批量回溯ID")
    private Long backtrackingId;

    @Schema(description = "批次号", required = true)
    private List<String> batchNumberList;

    @Schema(description = "分析指标", required = true)
    private AnalysisIndexMappingVo indexMappingVo;

    @Schema(description = "iv值计算参数设置", required = true)
    private ConfigIvMappingVo ivMappingVo;

    @Schema(description = "psi计算参数配置", required = true)
    private ConfigPsiMappingVo psiMappingVo;

    @Schema(description = "特殊值参数配置", required = true)
    private List<ConfigSpecialMappingVo> specialMappingVoList;

}
