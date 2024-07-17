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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 指定变量所有已上架版本查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/24
 */
@Data
@Schema(description = "指定变量所有已上架版本查询输入参数")
public class DesignatedVariableAllListedVersionsQueryInputDto implements Serializable {

    private static final long serialVersionUID = 5264587196490195766L;

    @Schema(description = "变量空间 ID", required = true)
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量标识符列表", required = true)
    @NotEmpty(message = "变量标识符列表不能为空")
    private List<String> identifierList;
}
