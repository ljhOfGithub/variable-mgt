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
 * 数据模型Json导入 入参 Vo
 *
 * @author wangxiansheng
 * @since 2023/12/20
 */
@Data
@Schema(description = "数据模型Json导入参数")
public class VariableDataModelJsonImportInputVo implements Serializable {

    @Schema(description = "变量空间 ID", example = "90000000")
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "数据模型ID", example = "90000000")
    @NotNull(message = "数据模型ID不能为空")
    private Long dataModelId;

    @Schema(description = "json数据", example = "0")
    @NotBlank (message = "json数据不能为空")
    private String json;

}
