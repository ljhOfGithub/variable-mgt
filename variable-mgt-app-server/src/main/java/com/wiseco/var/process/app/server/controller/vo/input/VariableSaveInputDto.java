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

import com.wiseco.var.process.app.server.controller.vo.StreamProcessContentInputVO;
import com.wiseco.var.process.app.server.enums.ProcessTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.service.dto.VariableTagDto;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 变量保存输入参数 DTO
 *
 * @author wangxianli
 */
@Schema(description = "变量保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableSaveInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量ID：有值（修改数据），null（新增）", example = "1")
    private Long id;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量名", required = true, example = "abc")
    @NotEmpty(message = "变量名不能为空")
    private String name;

    @Schema(description = "中文名", required = true, example = "人行征信评分卡")
    @NotEmpty(message = "变量中文名不能为空")
    private String label;

    @Schema(description = "变量类型", example = "string")
    private String dataType;

    @Schema(description = "变量分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "描述", example = "描述")
    private String description;

    @Schema(description = "内容", example = "null")
    private JSONObject content;

    @Schema(description = "流式变量内容")
    private StreamProcessContentInputVO streamProcessContent;

    @Schema(description = "是否json文件导入", example = "1")
    private Boolean isFileImport;

    @Schema(description = "用户使用代码块id", example = "1")
    private Long userCodeBlockId;

    @Schema(description = "用户标签", example = "1")
    private List<VariableTagDto> tags;

    @Schema(description = "加工方式 实时/流式", allowableValues = {"REALTIME","STREAM"})
    private ProcessingMethodEnum processingMethod;

    @Schema(description = "加工方式", example = "WRL")
    private ProcessTypeEnum processType;

    @Schema(description = "变量模版ID", example = "1")
    private Long functionId;

    @Schema(description = "公共方法唯一标识符", example = "1")
    private String identifier;

}
