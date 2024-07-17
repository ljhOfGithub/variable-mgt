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

import java.util.List;

@Schema(description = "变量内容生成")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableConvertVo {
    @Schema(description = "参数名称")
    private String label;

    @Schema(description = "名称'")
    private String name;

    @Schema(description = "编码'")
    private String code;

    @Schema(description = "参数值")
    private String value;

    @Schema(description = "参数值类型")
    private String type;

    private List<Variable> variableList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "变量参数")
    public static class Variable {
        @Schema(description = "参数名称")
        private String label;

        @Schema(description = "名称'")
        private String name;

        @Schema(description = "编码'")
        private String code;

        @Schema(description = "参数值")
        private String value;

        @Schema(description = "参数值类型")
        private String type;

        @Schema(description = "参数下标'")
        private String index;

    }
}
