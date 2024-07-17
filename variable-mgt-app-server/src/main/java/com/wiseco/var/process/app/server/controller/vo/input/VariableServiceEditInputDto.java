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
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 实时服务编辑入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/26
 */
@Data
@Schema(description = "实时服务编辑输入参数")
public class VariableServiceEditInputDto implements Serializable {

    private static final long serialVersionUID = 5687102833080031411L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID")
    private Long spaceId;

    @NotNull(message = "待编辑的实时服务 ID 不能为空")
    @Schema(description = "待编辑的实时服务 ID")
    private Long serviceId;

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 50, message = "服务名称不能超过 50 个字符")
    @Schema(description = "新服务名称")
    private String name;

    @NotBlank(message = "服务编码不能为空")
    @Size(max = 50, message = "服务编码不能超过 50 个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z\\d-]{0,49}$", message = "服务编码只能包含大小写字母、数字和中横线")
    @Schema(description = "服务编码", required = true)
    private String code;

    @NotNull(message = "需要指定服务类型")
    @Min(value = 1, message = "请选择正确的服务类型")
    @Max(value = 2, message = "请选择正确的服务类型")
    @Schema(description = "新服务类型")
    private Integer type;

    @Size(max = 100, message = "描述不能超过 100 个字符")
    @Schema(description = "描述")
    private String description;
}
