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

import com.wiseco.var.process.app.server.enums.MonitoringConfOperateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wuweikang
 */
@Schema(description = "监控预警配置状态修改入参")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class MonitoringAlterConfUpdateStateInputVo implements Serializable {

    @Schema(description = "id", required = true)
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(description = "operate", required = true)
    @NotNull(message = "操作不能为空")
    private MonitoringConfOperateEnum actionType;
}
