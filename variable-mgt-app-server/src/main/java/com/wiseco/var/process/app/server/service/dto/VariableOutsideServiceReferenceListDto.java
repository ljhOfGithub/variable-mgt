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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "变量外部服务Dto")
public class VariableOutsideServiceReferenceListDto {

    @Schema(description = "外部服务 ID")
    private Long id;

    @Schema(description = "外部服务名称")
    private String name;

    @Schema(description = "外部服务状态")
    private Integer state;

    @Schema(description = "变量清单版本")
    private Integer version;
}
