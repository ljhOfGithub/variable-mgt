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
 * 数据模型复制输入参数 入参 DTO
 *
 * @author wangxianli
 * @since 2022/8/30
 */
@Data
@Schema(description = "数据模型复制输入参数")
public class VariableDataModelCopyInputDto implements Serializable {

    @Schema(description = "变量空间 ID", example = "90000000")
    @NotNull(message = "空间ID不能为空")
    private Long spaceId;

    @Schema(description = "数据模型ID", example = "1")
    @NotNull(message = "数据模型ID不能为空")
    private Long dataModelId;

    @Schema(description = "对象名", example = "征信")
    @NotBlank(message = "对象名不能为空")
    private String objectName;

    @Schema(description = "对象中文名", example = "征信")
    @NotBlank(message = "对象中文名不能为空")
    private String objectLabel;

}
