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
package com.wiseco.var.process.app.server.service.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 * @since : 2023-02-21 13:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "方法DTO")
public class MethodDTO implements Serializable {

    @Schema(description = "method编号", type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

    @Schema(description = "class编号", type = "String", example = "0005DAF3E2D7B200")
    private String classIdentifier;

    @NotEmpty(message = "方法名不能为空")
    @Schema(description = "方法名", required = true, type = "String", example = "setCode")
    private String name;

    @Schema(description = "显示名", required = true, type = "String", example = "setCode")
    private String label;

    @Schema(description = "特征符", required = true, type = "String", example = "")
    private String characters;

    @Schema(description = "返回值类型", required = true, type = "String", example = "void")
    private String returnValueJavaType;

    @Schema(description = "返回值类型", required = true, type = "String", example = "void")
    private String returnValueWrlType;

    @Schema(description = "方法返回值是否数组：0=否，1=是", required = true, type = "Integer", example = "1")
    private Integer returnValueIsArray;

    @Schema(description = "归属类", required = true, type = "String", example = "claim.model.ClaimSummary")
    private String classCanonicalName;

    @Schema(description = "修饰符", required = true, type = "Integer", example = "1")
    private Integer modifier;

    @Schema(description = "模板", required = true, type = "String", example = "计算赔付方式B的赔付金额，<{$1,一个数值>,<{$2,一个字符>")
    private String template;

    @Schema(description = "编译后的模板", required = true, type = "String", example = "计算赔付方式B的赔付金额，<{$1,一个数值>,<{$2,一个字符>")
    private String compileTemplate;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0=停用，1=启用", required = true, type = "Integer", example = "1")
    private Integer status;

    @NotNull(message = "导入状态不能为空")
    @Schema(description = "导入状态：0=未导入，1=已导入", required = true, type = "Integer", example = "1")
    private Integer importStatus;

    @Schema(description = "参数", required = true, type = "List", example = "[]")
    private List<ParameterDTO> parameters;

}
