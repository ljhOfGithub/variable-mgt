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

import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 变量清单基本信息 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单基本信息")
public class VariableManifestBasicConfigDto implements Serializable {

    private static final long serialVersionUID = -8955607696788415259L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量清单 ID", required = true)
    @NotNull(message = "变量清单 ID 不能为空")
    private Long manifestId;

    @Schema(description = "变量清单名称", required = true)
    @NotNull(message = "变量清单 名称 不能为空")
    @Size(max = 100, message = "名称不能超过100个字符")
    private String name;

    @Schema(description = "清单分类Id", required = true)
    @NotNull(message = "清单分类不能为空")
    private Long categoryId;

    @Schema(description = "变量清单分类", required = true)
    private String category;

    @Schema(description = "版本号：暂时全部为1")
    private Integer version;

    @Schema(description = "版本描述")
    @Size(max = 500, message = "版本描述不能超过500个字符")
    private String description;

    /**
     * @see com.wiseco.decision.common.business.enums.VarProcessInterfaceStateEnum
     */
    @Schema(description = "变量清单状态")
    private VarProcessManifestStateEnum state;
}
