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

import javax.validation.constraints.NotNull;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "获取字典项的上级字典 入参DTO")
public class DictDetailsParentListInputDto {

    @Schema(description = "变量空间id", example = "1")
    private Long spaceId;

    @Schema(description = "字典类型id", example = "1")
    @NotNull(message = "字典类型id不能为空！")
    private Long dictId;

    @Schema(description = "字典项编码，为空表示新增，编辑需要传入自身编码", example = "yzf")
    private String code;

}
