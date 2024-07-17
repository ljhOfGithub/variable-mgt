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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 实时服务-决策领域授权新增信息入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/4
 */
@Data
@Schema(description = "实时服务授权新增信息输入参数")
public class VariableServiceAuthorizationSaveInputDto implements Serializable {

    private static final long serialVersionUID = -89492625926588368L;

    private Long        id;

    @Schema(description = "调用方")
    @NotEmpty
    @Size(max = 100, message = "调用方名称不能超过100个字符")
    private String      caller;

    @Schema(description = "所属部门")
    @NotEmpty
    private String      callerDept;

    @Schema(description = "授权说明")
    @Size(max = 500, message = "授权说明不能超过500个字符")
    private String      details;
}
