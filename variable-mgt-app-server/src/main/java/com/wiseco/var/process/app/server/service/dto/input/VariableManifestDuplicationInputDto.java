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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量复制的时候，service层输入的形参
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量复制的时候，service层输入的形参")
public class VariableManifestDuplicationInputDto implements Serializable {

    private static final long serialVersionUID = 4784412045566109633L;

    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @Schema(description = "实时服务 ID", required = true)
    private Long serviceId;

    @Schema(description = "版本来源 (创建方法)", example = "1: 新建, 2: 复制已有", required = true)
    private Integer createApproach;

    @Schema(description = "被复制变量清单的ID(仅限版本来源为 \"复制已有\" 时填写)")
    private Long archetypeManifestId;

    @Schema(description = "副本变量清单的新名称")
    private String manifestNewName;
}
