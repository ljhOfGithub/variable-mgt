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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 指标
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "指标")
public class IndicatorMappingVo implements Serializable {

    private static final long serialVersionUID = 5387852831359735854L;

    @NotNull(message = "监控指标不能为空")
    @Schema(description = "监控指标, CALL_VOLUME——调用量(服务报表时选择), FAILURE_RATE——失败率(服务报表时选择), MAX_RESPONSE_TIME——最大响应时间(服务报表时选择), "
            + "AVG_RESPONSE_TIME——平均响应时间(服务报表时选择), RESPONSE_CODE_RATIO——响应码占比(服务报表时选择), MISSING_RATIO——缺失率(指标报表时选择)"
            + "SPECIAL_RATIO——特殊值占比(指标报表时选择), PSI——psi(指标报表时选择), IV——iv(指标报表时选择)")
    private MonitorIndicatorEnum monitorIndicatorEnum;

    @Size(max = 10, message = "指标显示名称不能超过 10 个字符")
    @Schema(description = "指标显示名称")
    private String displayName = MagicStrings.EMPTY_STRING;

    @Schema(description = "响应码, 报表分类为服务报表且监控指标选择了响应码占比时赋值, 用英文的逗号分隔")
    private String responseCode;

    @Schema(description = "特殊值占比, 报表分类为单指标分析报表和指标对比分析报表, 且监控指标选择了特殊值占比、PSI和IV时赋值")
    private List<ConfigSpecialMappingVo> specialMappingVoList;

    @Schema(description = "psi, 报表分类为单指标分析报表和指标对比分析报表, 且监控指标选择了PSI时赋值")
    private ReportFormPsiMappingVo psiMappingVo;

    @Schema(description = "iv, 报表分类为单指标分析报表和指标对比分析报表, 且监控指标选择了IV时赋值")
    private ConfigIvMappingVo ivMappingVo;
}
