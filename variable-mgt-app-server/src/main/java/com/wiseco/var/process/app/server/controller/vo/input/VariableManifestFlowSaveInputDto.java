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
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 流程信息 DTO
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "流程信息输入参数")
public class VariableManifestFlowSaveInputDto implements Serializable {

    private static final long serialVersionUID = 4784418045566309633L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotNull(message = "变量清单ID不能为空")
    @Schema(description = "变量清单ID", required = true)
    private Long manifestId;

    @Schema(description = "流程内容", example = "null")
    private JSONObject content;

}
