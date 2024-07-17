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

import com.wiseco.var.process.app.server.controller.vo.StreamProcessContentOutputVO;
import com.wiseco.var.process.app.server.enums.ProcessTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import com.wisecotech.json.JSONObject;
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
@Schema(description = "变量内容 DTO")
public class VariableDetailOutputDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "空间ID", example = "")
    private Long spaceId;

    @Schema(description = "变量ID", example = "")
    private Long id;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "变量名", example = "")
    private String name;

    @Schema(description = "变量中文名", example = "")
    private String label;

    @Schema(description = "数据类型", example = "")
    private String dataType;

    @Schema(description = "版本", example = "1")
    private Integer version;

    @Schema(description = "变量类型Id", example = "1")
    private Long categoryId;

    @Schema(description = "变量分类名称", example = "1")
    private String categoryName;

    @Schema(description = "状态 1-编辑中，2-启用，3-停用,4-待审核,5-审核拒绝", example = "1")
    private VariableStatusEnum status;

    @Schema(description = "父级ID", example = "0")
    private Long parentId;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @Schema(description = "编辑人", example = "张三")
    private String updatedUser;

    @Schema(description = "创建时间", example = "2022-06-08 12:00:00")
    private String createdTime;

    @Schema(description = "最后编辑时间", example = "2022-06-08 12:00:00")
    private String updatedTime;

    @Schema(description = "描述", example = "描述")
    private String description;

    @Schema(description = "变量内容", example = "null")
    private JSONObject content;

    @Schema(description = "流式变量content")
    private StreamProcessContentOutputVO streamProcessContent;

    @Schema(description = "用户标签", example = "1")
    private List<VariableTagDto> tags;

    @Schema(description = "加工方式")
    private ProcessingMethodEnum processingMethod;

    @Schema(description = "实时变量加工方式", example = "ENTRY")
    private ProcessTypeEnum processType;

    @Schema(description = "变量模版ID", example = "1")
    private Long functionId;

    @Schema(description = "变量模板名称",example = "变量模板a")
    private String functionName;

}
