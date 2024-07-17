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

import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableLifecycle;
import com.wiseco.var.process.app.server.service.dto.OperationButton;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量列表
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量列表 DTO")
public class VariableListOutputDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "变量ID", example = "")
    private Long id;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "变量名/变量编码", example = "")
    private String name;

    @Schema(description = "变量中文名/变量名称", example = "")
    private String label;

    @Schema(description = "数据类型", example = "string")
    private String dataType;

    @Schema(description = "版本", example = "1")
    private Integer version;

    @Schema(description = "变量类型", example = "")
    private String categoryName;

    @Schema(description = "加工方式")
    private String processingMethod;

    @Schema(description = "状态：1-编辑中，2-已上架，3-下架，4-待审核，5-审核拒绝 ", example = "1")
    private VariableStatusEnum status;

    @Schema(description = "是否使用", example = "true")
    private Boolean used;

    @Schema(description = "按钮", example = "null")
    private List<OperationButton> operationButton;

    @Schema(description = "创建部门", example = "软件研发部")
    private String createDept;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @Schema(description = "编辑人", example = "张三")
    private String updatedUser;

    @Schema(description = "创建时间", example = "2022-06-08 12:00:00")
    private String createdTime;

    @Schema(description = "最后编辑时间", example = "2022-06-08 12:00:00")
    private String updatedTime;

    @Schema(description = "子项", example = "null")
    private List<VariableListOutputDto> children;

    @Schema(description = "用户标签", example = "1")
    private List<VariableTagDto> tags;

    @Schema(description = "是否测试：0-否，1-是", example = "null")
    private Boolean tested;

    @Schema(description = "最新的变量生命周期信息，包括审核拒绝的信息、审批人、审批时间")
    private VarProcessVariableLifecycle latestLifecycle;

    @Schema(description = "描述")
    private String description;
}
