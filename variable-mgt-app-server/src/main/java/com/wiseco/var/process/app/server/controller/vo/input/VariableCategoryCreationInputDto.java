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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 变量类别创建入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量类别创建输入参数")
public class VariableCategoryCreationInputDto implements Serializable {

    private static final long serialVersionUID = -5619301435572238109L;

    @Schema(description = "变量空间 ID")
    @NotNull(message = "变量空间 ID 不能为空。")
    private Long spaceId;

    @Schema(description = "变量类别名称")
    @NotEmpty(message = "变量类别名称不能为空。")
    private String categoryName;
}
