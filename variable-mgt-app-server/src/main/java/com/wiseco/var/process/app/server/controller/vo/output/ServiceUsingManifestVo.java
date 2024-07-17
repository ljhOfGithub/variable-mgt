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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "使用该变量清单的服务Vo")
public class ServiceUsingManifestVo implements Serializable {

    private static final Long SERIA_VERSIONUID = 8799865904865178456L;

    private Long serviceId;

    @Schema(description = "服务名称")
    private String name;

    @Schema(description = "服务编码")
    private String code;

    @Schema(description = "服务分类")
    private String category;

    @Schema(description = "变量清单在当前服务中的角色")
    private String manifestRole;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "已执行笔数")
    private Long excutedCount;
}
