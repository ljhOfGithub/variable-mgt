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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量清单发布入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单发布输入参数")
public class VariableManifestDeployApplyInputDto implements Serializable {

    private static final long serialVersionUID = 1110529399900558632L;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量清单 ID")
    private Long manifestId;

    @Schema(description = "目标环境 ID")
    private Long targetConfigId;

    @Schema(description = "上线类型：0-领域，1-策略", example = "1")
    private Integer type;
}
