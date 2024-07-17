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
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量查询 DTO")
public class VariableQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "空间ID", example = "1", required = true)
    @NotNull(message = "空间Id不能为空")
    private Long spaceId;

    @Schema(description = "变量分类", example = "null")
    private List<Long> categoryIdList;

    @Schema(description = "数据类型", example = "null")
    private List<String> dataTypeList;

    @Schema(description = "状态 1-编辑中，2-启用，3-停用,4-待审核,5-审核拒绝", example = "EDIT")
    private List<VariableStatusEnum> statusList;

    @Schema(description = "是否使用", example = "true")
    private Boolean used;

    @Schema(description = "变量名或中文名", example = "征信")
    private String keywords;

    @Schema(description = "标签组Id", example = "1")
    private Long groupId;

    @Schema(description = "标签ID", example = "1")
    private Long tagId;

    @Schema(description = "是否测试", example = "true")
    private Boolean tested;

    @Schema(description = "创建部门code", example = "1")
    private String deptId;

    @Schema(description = "排序字段", example = "label_asc")
    private String order;

    @Schema(description = "变量模版名", example = "变量模版")
    private String varTemplateKeyWord;

    @Schema(description = "加工方式",allowableValues = {"REALTIME","STREAM"},nullable = true)
    private ProcessingMethodEnum processingMethod;
}
