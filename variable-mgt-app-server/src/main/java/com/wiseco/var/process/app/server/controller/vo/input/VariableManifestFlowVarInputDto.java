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
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 流程信息 DTO
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "变量加工输入参数")
public class VariableManifestFlowVarInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 4784418045566309633L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotNull(message = "变量清单ID不能为空")
    @Schema(description = "变量清单ID", required = true)
    private Long manifestId;

    @Schema(description = "变量名/中文名-模糊查询关键字", example = "null")
    private String keywords;

    @Schema(description = "排除的identifier,多个", example = "null")
    private List<String> excludeList;

    @Schema(description = "创建部门-查询关键字")
    private String deptId;

    @Schema(description = "模板分类Id-查询关键字")
    private Long categoryId;

    @Schema(description = "数据类型-查询关键字")
    private String dataType;

    @Schema(description = "标签ID-查询关键字", example = "1")
    private Long tagId;

    @Schema(description = "标签组id-查询关键字")
    private Long groupId;

    @Schema(description = "排序字段", example = "label_asc")
    private String order;
}
