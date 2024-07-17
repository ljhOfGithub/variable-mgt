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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 变量清单可用版本出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/28
 */
@Data
@Builder
@Schema(description = "变量清单可用版本输出参数")
public class VariableManifestAvailableVersionOutputDto implements Serializable {

    private static final long serialVersionUID = 1707286140734791680L;

    @Schema(description = "变量清单ID")
    private Long manifestId;

    @Schema(description = "接口版本")
    private Integer version;
}
