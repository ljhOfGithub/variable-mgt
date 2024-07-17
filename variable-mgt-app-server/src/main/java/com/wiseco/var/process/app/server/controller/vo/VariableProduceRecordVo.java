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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "生成变量数据")
public class VariableProduceRecordVo implements Serializable {
    @Schema(description = "生成指记录id")
    private Long id;
    @Schema(description = "方案名")
    private String planName;
    @Schema(description = "变量名称")
    private String name;
    @Schema(description = "变量编号")
    private String identifier;
    @Schema(description = "数据类型：string、int、double、date、datetime、boolean")
    private String dataType;
    @Schema(description = "变量分类")
    private String variableType;
    @Schema(description = "参数内容")
    private String paramJson;
}
