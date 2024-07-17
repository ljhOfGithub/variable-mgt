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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 变量清单-内部/外部服务对象绑定结构校验入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/6
 */
@Data
@Schema(description = "变量清单-内部/外部服务对象绑定结构校验输入参数")
public class VariableManifestInsideOutsideBindObjStructValidInputDto implements Serializable {

    private static final long serialVersionUID = -8705750446424978438L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量清单ID", required = true)
    @NotNull(message = "变量清单ID不能为空")
    private Long manifestId;

    @Schema(description = "数据模型对象名称", required = true)
    @NotBlank(message = "数据模型对象名称不能为空")
    private String dataModelObjectName;

    @Schema(description = "对象绑定配置", required = true, example = "pboc.Document")
    @NotBlank(message = "对象绑定配置不能为空")
    private String objectBindingConfig;

    @Schema(description = "对象绑定数据来源", required = true, example = "0: 内部数据, 1: 外部数据")
    @NotNull(message = "对象绑定数据来源不能为空")
    private Integer objectBindingSource;
}
