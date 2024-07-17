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
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 流程信息 DTO
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@Data
@Schema(description = "流程属性输入参数")
public class VariableManifestFlowPropertiesInputDto implements Serializable {

    private static final long serialVersionUID = 4784418045566309633L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotNull(message = "变量清单ID不能为空")
    @Schema(description = "变量清单ID", required = true)
    private Long manifestId;

    @Schema(description = "节点ID:传节点ID代表查节点数据", example = "10001")
    private String nodeId;

    @Schema(description = "节点类型:flow,var,pre_process, service, split|parallel,internal_data", example = "task")
    private String nodeType;

    @Schema(description = "保存内容Id", example = "1")
    private Long contentId;
}
