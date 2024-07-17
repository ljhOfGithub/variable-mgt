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
package com.wiseco.var.process.app.server.service.dto;

import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUsingManifestDto {
    private Long serviceId;

    @Schema(description = "服务名称")
    private String name;

    @Schema(description = "服务编码")
    private String code;

    @Schema(description = "服务分类")
    private Long categoryId;

    @Schema(description = "变量清单在当前服务中的角色")
    private Integer manifestRole;

    @Schema(description = "状态")
    private VarProcessServiceStateEnum state;

    @Schema(description = "已执行笔数")
    private Long excutedCount;

    @Schema(description = "服务版本号",example = "10")
    private Integer  version;
}
