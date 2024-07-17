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
 * 公共函数校验入参 DTO
 *
 * @author wangxianli
 */
@Schema(description = "公共函数校验入参 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FunctionValidInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "变量空间ID", example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long spaceId;

    @Schema(description = "公共函数ID", example = "1")
    @NotNull(message = "公共函数ID")
    private Long functionId;

    @Schema(description = "操作类型：1-修改，2-停用，3-删除", example = "1")
    @NotNull(message = "操作类型不能为空")
    private Integer actionType;
}
