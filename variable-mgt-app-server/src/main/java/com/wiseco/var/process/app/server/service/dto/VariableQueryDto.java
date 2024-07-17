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
package com.wiseco.var.process.app.server.service.dto;

import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量查询 DTO")
public class VariableQueryDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量分类", example = "null")
    private List<Long> categoryIdList;

    @Schema(description = "数据类型", example = "null")
    private List<String> dataTypeList;

    @Schema(description = "状态 1-编辑中，2-启用，3-停用,4-待审核,5-审核拒绝", example = "EDIT")
    private List<VariableStatusEnum> statusList;

    @Schema(description = "是否使用", example = "0")
    private Boolean isUse;

    @Schema(description = "变量名或中文名", example = "征信")
    private String keywords;

    @Schema(description = "需要排查的ID", example = "null")
    private List<Long> idList;

    @Schema(description = "编号", example = "null")
    private List<String> identifierList;

    @Schema(description = "标签组ID", example = "1")
    private Long tagGroupId;

    @Schema(description = "标签名称", example = "1")
    private String tagName;

    @Schema(description = "是否测试", example = "null")
    private Boolean tested;

    @Schema(description = "创建部门code", example = "develop")
    private String deptCode;

    @Schema(description = "排序字段", example = "1")
    private String sortKey;

    @Schema(description = "排序方式", example = "1")
    private String sortType;

    @Schema(description = "变量模版名", example = "变量模版")
    private String varTemplateKeyWord;

    @Schema(description = "部门集合", example = "")
    private List<String> deptCodes;

    @Schema(description = "用户集合", example = "")
    private List<String> userNames;

    @Schema(description = "加工方式",allowableValues = {"REALTIME","STREAM"})
    private ProcessingMethodEnum processingMethod;
}
