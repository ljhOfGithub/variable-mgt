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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * service层中，实时服务创建入参DTO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "service层中，实时服务创建入参DTO")
public class VariableServiceCreationInputDto implements Serializable {

    private static final long serialVersionUID = 2013396166832342240L;

    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @Schema(description = "服务名称", required = true)
    private String name;

    @Schema(description = "服务编码", required = true)
    private String code;

    @Schema(description = "服务类型", required = true)
    private Long categoryId;

    @Schema(description = "服务的描述")
    private String description;
}
