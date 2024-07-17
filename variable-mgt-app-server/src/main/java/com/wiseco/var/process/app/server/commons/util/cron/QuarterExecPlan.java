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
package com.wiseco.var.process.app.server.commons.util.cron;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每季度执行计划
 */
@Data
@Schema(description = "每季度执行计划")
@AllArgsConstructor
@NoArgsConstructor
public class QuarterExecPlan {
    @Schema(description = "季度序号,1,2,3,4 1是第一季度(1~3月份)，依此类推")
    private Integer quarterSeq;
    @Schema(description = "该季度中的执行月")
    private Integer monthInQuarter;
    @Schema(description = "该执行月中的执行日")
    private Integer dayInMonth;
}
