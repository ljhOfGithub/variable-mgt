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
package com.wiseco.var.process.app.server.controller.vo;

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "变量引用 出参Vo")
public class ManifestUsingOutputVo {

    @Schema(description = "标题", example = "变量使用")
    private String title;

    @Schema(description = "表头", example = "null")
    private List<com.wiseco.var.process.app.server.controller.vo.output.VariableUseOutputVo.TableHeader> tableHeader;

    @Schema(description = "内容", example = "null")
    private List<JSONObject> tableData;

    @SuperBuilder
    @Schema(description = "表头信息")
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class TableHeader {
        @Schema(description = "标题", example = "")
        private String label;
        @Schema(description = "字段名称", example = "")
        private String prop;
    }

}
