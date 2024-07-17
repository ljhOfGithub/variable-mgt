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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * (变量空间用) 变量树查询入参 DTO
 *
 * @author liaody
 * @author Zhaoxiong Chen
 * @since 2021/10/21 19:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "(变量空间用) 变量树查询输入参数")
public class VarsTreeInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空。")
    private Long spaceId;

    @Schema(description = "变量类型", required = true, example = "[\"input\", \"externalData\", \"internalData\"]")
    @NotNull(message = "变量类型为空")
    private List<String> positionList;

    @Schema(description = "数据类型", required = true, example = "[\"int\", \"double\", \"boolean\", \"string\", \"date\", \"datetime\", \"object\", \"array\"]")
    @NotNull(message = "数据类型为空")
    private List<String> varTypeList;

    @Schema(description = "变量路径", example = "全路径:parameters.complex")
    private String variablePath;

    @Schema(description = "缓存sessionId", example = "缓存路径")
    private String sessionId;
}
