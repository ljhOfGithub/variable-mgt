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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 变量空间导出输入参数 DTO
 *
 * @author wangxianli
 * @since 2022/11/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间导出输入参数")
public class VarProcessExportVerifyInputDto implements Serializable {

    private static final long serialVersionUID = 1110529399900558632L;

    @Schema(description = "变量空间ID", example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "导出类别：1-导出最新已上架的变量，2-导出所有变量，3-导出空间", example = "1")
    @NotBlank(message = "导出类别不能为空")
    private String type;

}
