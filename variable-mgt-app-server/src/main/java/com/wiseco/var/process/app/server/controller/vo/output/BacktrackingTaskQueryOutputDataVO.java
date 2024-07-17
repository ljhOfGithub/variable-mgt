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

import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 执行记录查询 VO
 * @author wuweikang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "执行记录查询 VO")
public class BacktrackingTaskQueryOutputDataVO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "主体唯一标识")
    private String serialNo;

    @Schema(description = "调用时间")
    private String startTime;

    @Schema(description = "执行状态")
    private BacktrackingTaskStatusEnum taskStatus;

    @Schema(description = "响应时长")
    private Long responseTime;

    @Schema(description = "结果表的code")
    private String resultCode;

}
