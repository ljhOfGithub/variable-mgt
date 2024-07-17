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
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 变量空间属性入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间属性输入参数")
public class VarProcessSpaceAttributeInputDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "空间 ID")
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "空间名称")
    @NotBlank(message = "变量空间名称不能为空")
    @Size(min = 2, max = 50, message = "空间名称长度为2-50位。")
    private String name;

    @Schema(description = "描述")
    @Size(max = 100, message = "描述不得超过100字。")
    private String description;
}
