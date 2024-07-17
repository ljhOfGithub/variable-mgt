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

import com.wiseco.var.process.app.server.enums.ReportFormDisplayDimensionEnum;
import com.wiseco.var.process.app.server.enums.TimeUnitEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 展示维度
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "展示维度")
public class DisplayDimensionVo implements Serializable {

    private static final long serialVersionUID = 5215470385200040328L;

    @NotNull(message = "展示维度不能为空")
    @Schema(description = "展示维度, TIME_SCOPE(时间范围), MONITOR_OBJECT(监控对象, 当报表分类为服务报表和指标对比分析报表时可选), MANIFEST(变量清单, 当报表分类为单指标分析报表时可选)")
    private ReportFormDisplayDimensionEnum displayDimension;

    @Schema(description = "时间刻度, HOUR——小时, DAY——天, WEEK——周, MONTH——月, 当展示维度选择了时间维度时可以赋值")
    private TimeUnitEnum timeUnit;

    @NotNull(message = "展示TOP不能为空")
    @Schema(description = "展示TOP, 非必选, 展示前5、10，20的数据(要么不传，要么传数字)")
    private Integer displayTop;
}
