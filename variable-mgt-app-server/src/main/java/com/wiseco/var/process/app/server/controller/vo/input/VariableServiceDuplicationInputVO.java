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
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 实时服务复制的入参实体(控制层)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "实时服务复制的入参实体(控制层)")
public class VariableServiceDuplicationInputVO implements Serializable {

    private static final long serialVersionUID = 1011146156832342348L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotNull(message = "原实时服务的ID不能为空")
    @Schema(description = "原实时服务的ID", required = true, example = "1")
    private Long serviceId;

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称不能超过 100 个字符")
    @Schema(description = "服务名称", required = true)
    private String serviceName;

    @NotBlank(message = "服务编码不能为空")
    @Size(max = 100, message = "服务编码不能超过 100 个字符")
    @Pattern(regexp = "^[@:a-zA-Z_\\-]{1}[@:0-9a-zA-Z_\\-]{0,99}$", message = "服务编码只能包含大小写字母、数字、中横线、下划线、冒号和@符号")
    @Schema(description = "服务编码", required = true)
    private String serviceCode;

    @NotNull(message = "服务分类的ID不可以为空")
    @Schema(description = "服务分类的ID", required = true)
    private Long categoryId;

    @Size(max = 500, message = "实时服务的编码不能超过 500 个字符")
    @Schema(description = "复制出的实时服务的描述", required = true)
    private String description;
}
