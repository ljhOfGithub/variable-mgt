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

import com.wiseco.var.process.app.server.controller.vo.ReportFormItemVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 报表列表输出Vo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "报表列表输出Vo")
public class ReportFormsOutputVo implements Serializable {

    private static final long serialVersionUID = -643963494283464817L;

    @Schema(description = "服务报表的列表")
    List<ReportFormItemVo> serviceReportForm;

    @Schema(description = "指标报表的列表")
    List<ReportFormItemVo> variableReportForm;

    @Schema(description = "指标对比分析报表的列表")
    List<ReportFormItemVo> variableCompareReportFrom;
}
