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

import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "使用该变量清单的批量回溯任务Vo")
public class BackTrackingUsingManifestVo implements Serializable {

    private static final Long SERIA_VERSIONUID = 8799865904865174856L;

    private Long taskId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务状态")
    private FlowStatusEnum state;
}
