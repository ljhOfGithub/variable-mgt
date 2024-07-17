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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签 DTO")
public class VarProcessConfigTagDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "标签id", example = "1")
    private Long tagId;

    @Schema(description = "组id", example = "1")
    private Long groupId;

    @Schema(description = "标签名称", example = "身份")
    private String tagName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}
