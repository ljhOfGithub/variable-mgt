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

import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 变量清单调用流水号可绑定数据结构查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单调用流水号可绑定数据结构查询输入参数")
public class VariableManifestSerialNoLinkableDataModelInputDto implements Serializable {

    private static final long serialVersionUID = -6934216824828164178L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "数据模型绑定配置", required = true)
    @NotEmpty(message = "数据模型绑定配置列表不能为空")
    private List<VariableManifestDataModelMappingVo> dataModelBinding;
}
