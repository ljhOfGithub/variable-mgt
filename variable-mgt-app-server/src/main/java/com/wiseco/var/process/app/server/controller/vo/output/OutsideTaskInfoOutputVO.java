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

import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "调用外数信息")
public class OutsideTaskInfoOutputVO implements Serializable {

    private static final long          serialVersionUID = 8799865908944973992L;

    @Schema(description = "外数名称")
    private String                     outsideServiceName;

    @Schema(description = "取值方式")
    private BacktrackingOutsideCallStrategyEnum outsideServiceStrategy;

    @Schema(description = "调用量")
    private Long                       callSize;

    @Schema(description = "成功率")
    private String                     successRate;

    @Schema(description = "查得率")
    private String                     findRate;

    @Schema(description = "最大响应时间")
    private Long                       maximumResponseTime;

    @Schema(description = "最小响应时间")
    private Long                       minimumResponseTime;

    @Schema(description = "平均响应时间")
    private String                       averageResponseTime;

    @Schema(description = "外部服务id")
    private Long                       outsideServiceId;

    @Schema(description = "外部服务编码")
    private String                     outsideServiceCode;

}
