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

import java.io.Serializable;

/**
 * 变量上架 DTO
 *
 * @author wangxianli
 */
@Schema(description = "生成变量 vo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableProduceInputVo implements Serializable {

    private static final long serialVersionUID = 8799865908944973993L;

    @Schema(description = "参数名称")
    private String label;

    @Schema(description = "标签名称")
    private String param;

    @Schema(description = "名称'")
    private String name;

    @Schema(description = "编码'")
    private String code;

    @Schema(description = "参数值")
    private String value;

    @Schema(description = "参数值类型")
    private String dataType;

    @Schema(description = "参数下标'")
    private String index;

}
