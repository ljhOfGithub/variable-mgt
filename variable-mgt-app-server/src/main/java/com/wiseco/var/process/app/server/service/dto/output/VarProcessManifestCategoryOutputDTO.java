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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.jar.Manifest;

/**
 * 变量清单分类的输出结构
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "变量清单分类的输出结构")
public class VarProcessManifestCategoryOutputDTO {

    @Schema(description = "主键", required = true, example = "1", hidden = false)
    private Long id;

    @Schema(description = "分类名称", required = true, example = "1号分类", hidden = false)
    private String name;

    @Schema(description = "父类ID", required = true, example = "0", hidden = false)
    private Long parentId;

    @Schema(description = "子分类集合", required = true, example = "list", hidden = false)
    private List<VarProcessManifestCategoryOutputDTO> children;

    @Schema(description = "变量清单", required = true, example = "manifestList", hidden = false)
    private List<Manifest> manifestList;
}
