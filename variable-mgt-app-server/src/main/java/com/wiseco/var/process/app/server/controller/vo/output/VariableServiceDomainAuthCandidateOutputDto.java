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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.decision.common.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 可被实时服务授权的领域出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "可被实时服务授权的领域输出参数")
public class VariableServiceDomainAuthCandidateOutputDto implements Serializable {

    private static final long serialVersionUID = 1897461968390442146L;

    @Schema(description = "决策领域 ID")
    private Long domainId;

    @Schema(description = "决策领域名称")
    private String domainName;

    @Schema(description = "决策领域编码")
    private String domainCode;

    @JsonFormat(pattern = DateUtil.FORMAT_LONG)
    @Schema(description = "决策领域创建时间")
    private Date createdTime;
}
