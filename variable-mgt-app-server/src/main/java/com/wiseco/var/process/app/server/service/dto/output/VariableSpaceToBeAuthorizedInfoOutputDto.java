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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 可授权的变量空间信息出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "可授权的变量空间信息输出参数")
public class VariableSpaceToBeAuthorizedInfoOutputDto implements Serializable {

    private static final long serialVersionUID = 4781291259687256900L;

    @Schema(description = "变量空间 ID")
    private Long spaceId;

    @Schema(description = "变量空间名称")
    private String spaceName;

    @Schema(description = "变量空间编码")
    private String spaceCode;

    @Schema(description = "变量空间创建时间")
    private String createdTime;
}
