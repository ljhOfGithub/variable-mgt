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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: wangxianli
 */
@Schema(description = "数据模型内部SQL识别入参")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableDataModelAddSqlReturnVarCheckInputVo implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "参数list", required = true, example = "1")
    @NotNull(message = "参数list不能为空")
    private List<String> paramList;

    @Schema(description = "表list", required = true, example = "1")
    @NotNull(message = "表list不能为空")
    private List<String> tableList;

    @Schema(description = "SQL语句", required = true, example = "1")
    @NotNull(message = "SQL语句不能为空")
    private String sql;

    @Schema(description = "对象名称")
    @NotBlank(message = "对象名称不能为空")
    private String objectName;

    @Schema(description = "对象中文名")
    @NotBlank(message = "对象中文名不能为空")
    private String objectLabel;

    @Schema(description = "是否返回多条")
    private Boolean sqlIsArray;


}
