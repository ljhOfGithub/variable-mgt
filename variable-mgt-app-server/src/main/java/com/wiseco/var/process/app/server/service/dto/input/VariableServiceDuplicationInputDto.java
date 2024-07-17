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
 * 实时服务复制的入参实体(业务逻辑层)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "实时服务复制的入参实体(业务逻辑层)")
public class VariableServiceDuplicationInputDto implements Serializable {

    private static final long serialVersionUID = 1000046156832342348L;

    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @Schema(description = "原实时服务的ID", required = true, example = "1")
    private Long serviceId;

    @Schema(description = "服务名称", required = true)
    private String serviceName;

    @Schema(description = "服务编码", required = true)
    private String serviceCode;

    @Schema(description = "服务分类的ID", required = true)
    private Long categoryId;

    @Schema(description = "复制出的实时服务的描述", required = true)
    private String description;
}
