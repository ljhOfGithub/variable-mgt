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
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Schema(description = "更新实时服务中变量清单的执行次数Vo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceManifestUpdateInputVo implements Serializable {

    private static final long serialVersionUID = -887148151786425803L;

    @NotNull(message = "实时服务的ID不能为空")
    @Schema(description = "实时服务的ID", example = "10000")
    private Long serviceId;

    @NotNull(message = "变量清单的ID不能为空")
    @Schema(description = "变量清单的ID", example = "20000")
    private Long manifestId;
}
