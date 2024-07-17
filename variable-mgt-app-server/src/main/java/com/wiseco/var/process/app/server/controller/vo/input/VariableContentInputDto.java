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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量验证 DTO
 *
 * @author wangxianli
 */
@Schema(description = "变量验证/对比/临时缓存 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableContentInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", example = "1")
    private Long spaceId;

    @Schema(description = "变量ID", example = "1")
    private Long variableId;

    @Schema(description = "变量内容", example = "null")
    private JSONObject content;
}
