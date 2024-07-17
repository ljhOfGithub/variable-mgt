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

/**
 * @author: wangxianli
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "获取字典项数据 入参DTO")
public class QueryDictInputDto {

    @Schema(description = "空间id", example = "1")
    @NotNull(message = "空间id不能为空！")
    private Long spaceId;

    @Schema(description = "变量全路径", example = "1")
    @NotNull(message = "变量全路径不能为空！")
    private String varFullPath;

    @Schema(description = "字典类型编码", example = "sex")
    @NotNull(message = "字典类型编码不能为空")
    private String code;

}
