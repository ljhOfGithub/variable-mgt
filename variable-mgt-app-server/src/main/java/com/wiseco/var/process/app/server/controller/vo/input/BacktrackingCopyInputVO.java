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

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Schema(description = "批量回溯复制DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingCopyInputVO implements Serializable {

    @Schema(description = "来源变量的ID", example = "1")
    private Long copyId;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "名称", example = "abc")
    @NotEmpty(message = "名称不能为空")
    private String name;

}
