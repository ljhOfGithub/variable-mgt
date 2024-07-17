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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: wangxianli
 */
@Schema(description = "测试规则数据保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestVariableRulesInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", required = true, example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "变量全路径", example = "input.application.channelNo")
    private String varPath;

    @Schema(description = "字典名称", example = "")
    private String varName;

    @Schema(description = "数据类型", example = "type")
    private String varType;

    @Schema(description = "生成方式", example = "enum-枚举，random-随机，logic-逻辑依赖，custom-自定义")
    private String generateMode;

    @Schema(description = "生成规则", example = "")
    private String generateRule;

    @Schema(description = "生成规则描述", example = "")
    private String generateRuleDesc;

    @Schema(description = "生成规则公式", example = "")
    private String generateRuleFormula;
}
