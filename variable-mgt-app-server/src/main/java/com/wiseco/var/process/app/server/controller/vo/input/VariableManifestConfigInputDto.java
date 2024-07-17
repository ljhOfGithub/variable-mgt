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

import com.wiseco.var.process.app.server.service.dto.VariableManifestBasicConfigDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishVariableVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 变量清单配置入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单配置输入参数")
public class VariableManifestConfigInputDto implements Serializable {

    private static final long serialVersionUID = 8346990056380363852L;

    @Valid
    @Schema(description = "接口基本信息")
    private VariableManifestBasicConfigDto basicConfig;

    @Schema(description = "发布变量list")
    private List<VariableManifestPublishVariableVo> variablePublishList;

    @Schema(description = "数据模型绑定")
    private List<VariableManifestDataModelMappingVo> dataModelBindingList;
    //
    //    @Schema(description = "流水号绑定", position = 3)
    //    private String                                    serialNumberBinding;
}
