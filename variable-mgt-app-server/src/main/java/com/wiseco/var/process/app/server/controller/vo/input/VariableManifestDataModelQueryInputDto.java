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

/**
 * 变量清单数据模型查询入参 DTO
 *
 * @author wangxianli
 * @since 2022/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单数据模型查询输入参数")
public class VariableManifestDataModelQueryInputDto implements Serializable {

    private static final long serialVersionUID = 8346990056380363852L;

    @Schema(description = "变量空间 ID", required = true, example = "1")
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "变量清单 ID")
    private Long manifestId;
}
