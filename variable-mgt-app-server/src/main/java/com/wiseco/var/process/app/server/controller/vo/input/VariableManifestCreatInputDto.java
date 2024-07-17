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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Schema(description = "新增变量清单版本输入参数")
public class VariableManifestCreatInputDto implements Serializable {

    private static final long serialVersionUID = 4784418045556309633L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotEmpty(message = "变量清单名称不能为空")
    @Schema(description = "变量清单名称", required = true)
    @Size(max = 100, message = "名称不能超过100个字符")
    private String name;

    @NotNull
    @Schema(description = "变量清单类别ID", required = true)
    private Long categoryId;

    @Schema(description = "版本说明")
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    //    @NotNull(message = "实时服务 ID 不能为空")
    //    @Schema(description = "实时服务 ID", required = true, position = 1)
    //    private Long              serviceId;

}
