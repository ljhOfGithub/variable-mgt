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
import com.wiseco.var.process.app.server.service.dto.VariableMaximumListedVersionQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 所有变量最大已上架版本记录查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @see VariableMaximumListedVersionQueryDto
 * @since 2022/6/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "所有变量最大已上架版本记录查询输入参数")
public class VariableMaximumListedVersionQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 7694809743863616457L;

    @Schema(description = "空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量名称/中文名模糊搜索关键字")
    private String keywords;

    @Schema(description = "变量类型-用于查询的关键字")
    private Long categoryId;

    @Schema(description = "变量数据类型-查询关键字")
    private String varDataType;

    @Schema(description = "标签id-查询关键字")
    private Long tagId;

    @Schema(description = "标签组id-查询关键字")
    private Long groupId;

    @Schema(description = "部门id-查询关键字")
    private String deptId;

    @Schema(description = "排除的 (用户已选择的) 变量标识列表")
    private List<String> excludedIdentifierList;

    @Schema(description = "排序关键字")
    private String order;
}
