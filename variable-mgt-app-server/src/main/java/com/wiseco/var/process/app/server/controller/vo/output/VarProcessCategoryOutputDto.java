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

import com.wiseco.var.process.app.server.controller.vo.input.HasOrderNo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量类别出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量类别输出参数")
public class VarProcessCategoryOutputDto implements Serializable,HasOrderNo {

    private static final long serialVersionUID = 8240823771891283410L;

    @Schema(description = "类别 ID")
    private Long categoryId;

    @Schema(description = "父级类别 ID")
    private Long parentId;

    @Schema(description = "父级分类名称")
    private String parentName;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "子分类集合")
    private List<VarProcessCategoryOutputDto> children;

    @Schema(description = "是否使用：true:使用 ")
    private Boolean isUse;

    @Schema(description = "状态：1-启用；0-停用")
    private Integer enabled;

    @Schema(description = "最后编辑人", required = true, example = "NA,-9999")
    private String updatedUser;

    @Schema(description = "编辑时间", required = true, example = "2022-08-31 10:00:0")
    private String updatedTime;

    @Schema(description = "顺序数字",example = "0")
    private Integer orderNo;

}
