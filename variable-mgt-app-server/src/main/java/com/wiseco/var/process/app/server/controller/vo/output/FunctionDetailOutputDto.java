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

import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 公共函数内容DTO
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公共函数内容 DTO")
public class FunctionDetailOutputDto implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "空间ID", example = "")
    private Long spaceId;

    @Schema(description = "公共函数ID", example = "")
    private Long id;

    @Schema(description = "编号", example = "")
    private String identifier;

    @Schema(description = "函数名", example = "")
    private String name;

    @Schema(description = "函数类型", example = "")
    private FunctionTypeEnum functionType;

    @Schema(description = "数据类型", example = "")
    private DataTypeEnum functionDataType;

    @Schema(description = "预处理对象", example = "")
    private String prepObjectName;

    @Schema(description = "状态 1-编辑中，2-启用，3-停用", example = "1")
    private FlowStatusEnum status;

    @Schema(description = "版本", example = "1")
    private Integer version;

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

    @Schema(description = "内容", example = "null")
    private JSONObject content;

    @Schema(description = "变量模版词条内容", example = "{}")
    private String functionEntryContent;

    @Schema(description = "模板分类id")
    private Long categoryId;

    @Schema(description = "模板分类")
    private String categoryName;

    @Schema(description = "处理方式")
    private FunctionHandleTypeEnum handleType;

}
