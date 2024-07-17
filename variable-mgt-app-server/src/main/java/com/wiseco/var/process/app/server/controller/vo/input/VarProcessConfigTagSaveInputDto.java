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

import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 变量标签配置查询DTO
 *
 * @author wangxianli
 * @since 2022/09/30
 */

@EqualsAndHashCode(callSuper = true)
@Schema(description = "变量标签分组DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VarProcessConfigTagSaveInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", required = true, example = "10000")
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "组ID", required = true, example = "1")
    @NotNull(message = "标签组ID不能为空")
    private Long groupId;

    @Schema(description = "标签名称", required = true, example = "a")
    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    @Schema(description = "排序", required = true, example = "a")
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

}
