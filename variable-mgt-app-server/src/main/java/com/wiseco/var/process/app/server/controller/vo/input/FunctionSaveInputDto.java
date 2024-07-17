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

import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 公共函数保存输入参数 DTO
 *
 * @author wangxianli
 */
@Schema(description = "公共函数保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FunctionSaveInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量ID：有值（修改数据），null（新增）", example = "1")
    private Long id;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "名称", required = true, example = "abc")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "函数类型:1-变量模板，2-公共方法,3-数据预处理", example = "1")
    @NotNull(message = "函数类型不能为空")
    private FunctionTypeEnum functionType;

    @Schema(description = "返回数据类型：void、int、double、string、boolean、date、datetime或引用对象", example = "string")
    private DataTypeEnum functionDataType;

    @Schema(description = "预处理对象", example = "pboc")
    private String prepObjectName;

    @Schema(description = "描述", example = "描述")
    private String description;

    @Schema(description = "内容(WRL)", example = "null")
    private JSONObject content;

    @Schema(description = "内容(直接映射)", example = "null")
    private FunctionPreDto mapContent;

    @Schema(description = "用户使用代码块id", example = "1")
    private Long userCodeBlockId;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "词条字符串", example = "计算xxx,<$1,输入>")
    private String functionEntry;

    @Schema(description = "模板分类Id")
    private Long categoryId;

    @Schema(description = "处理方式")
    private FunctionHandleTypeEnum handleType;

    @Schema(description = "公共方法唯一标识符", example = "1")
    private String identifier;
}

