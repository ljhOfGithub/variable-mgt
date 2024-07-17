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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.wiseco.var.process.app.server.service.dto.OperationButton;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 实时服务授权出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实时服务授权输出参数")
public class VariableServiceAuthorizationOutputDto implements Serializable {

    private static final long serialVersionUID = 5475235405850961792L;

    @Schema(description = "服务授权记录 ID")
    private Long recordId;

    @Schema(description = "领域 ID")
    private Long domainId;

    @Schema(description = "领域编码")
    private String domainCode;

    @Schema(description = "领域名称")
    private String domainName;

    @Schema(description = "授权时间")
    private String authorizeTime;

    @Schema(description = "是否引入-使用此字段判断授权是否允许被删除")
    private Boolean referencedFlag;

    @Schema(description = "首次引入")
    private String firstReferenceTime;

    @Schema(description = "按钮")
    private List<OperationButton> operationButtons;
}
