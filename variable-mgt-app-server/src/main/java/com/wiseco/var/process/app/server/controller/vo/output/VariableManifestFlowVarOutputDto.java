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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程中加工的变量列表 DTO
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流程中加工的变量列表")
public class VariableManifestFlowVarOutputDto implements Serializable {

    private static final long serialVersionUID = 6644386232861612508L;

    @Schema(description = "变量标识符")
    private String identifier;

    @Schema(description = "变量编码")
    private String name;

    @Schema(description = "变量中文名")
    private String label;

    @Schema(description = "变量分类")
    private String category;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "创建部门")
    private String createdDept;
}
