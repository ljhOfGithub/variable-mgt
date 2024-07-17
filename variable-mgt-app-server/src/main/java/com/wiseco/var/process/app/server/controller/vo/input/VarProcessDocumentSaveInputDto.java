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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxianli
 */
@Schema(description = "变量空间文档保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarProcessDocumentSaveInputDto implements Serializable {
    private static final long serialVersionUID = 3482976006398038566L;

    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * 变量空间ID
     */
    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    /**
     * 资源ID
     */
    @Schema(description = "资源ID：变量清单ID", example = "1")
    private Long resourceId;

    @Schema(description = "文档类型：manifest-变量清单，variable-变量，function-公共函数", example = "manifest")
    private String fileType;

    @Schema(description = "描述", example = "1")
    private String description;

}
