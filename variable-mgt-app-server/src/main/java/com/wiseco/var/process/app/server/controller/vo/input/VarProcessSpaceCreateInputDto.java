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
 * 变量空间创建入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间创建输入参数")
public class VarProcessSpaceCreateInputDto implements Serializable {

    private static final long serialVersionUID = 8321299945103096678L;

    @Schema(description = "空间编码")
    @NotBlank(message = "请填写空间编码。")
    @Pattern(regexp = "^[a-zA-Z]\\w{1,51}$", message = "空间编码支持大小写字母、数字、下划线组合，首位为字母。")
    @Size(min = 2, max = 50, message = "空间编码长度为2-50位。")
    private String code;

    @Schema(description = "空间名称")
    @NotBlank(message = "请填写空间名称。")
    @Size(min = 2, max = 50, message = "空间名称长度为2-50位。")
    private String name;

    @Schema(description = "空间创建方式：是否为“复制已有空间”", example = "false")
    @NotNull(message = "请指定空间创建方式。")
    private Boolean createByDuplicate;

    @Schema(description = "被复制的空间 ID", nullable = true)
    private Long id;

    @Schema(description = "描述", nullable = true)
    @Size(max = 100, message = "描述不得超过100字。")
    private String description;
}
