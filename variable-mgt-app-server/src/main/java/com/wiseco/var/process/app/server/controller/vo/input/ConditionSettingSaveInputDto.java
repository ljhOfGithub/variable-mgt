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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/2/27
 * @Time: 10:58
 */
@Schema(description = "保存查询条件，表头列 入参DTO")
@Data
public class ConditionSettingSaveInputDto {

    @Schema(description = "更新类型，0：更新查询条件，1：更新结果表头列", example = "0")
    @NotNull(message = "更新类型不能为空")
    private Integer updateType;

    @Schema(description = "更新条件入参")
    @Valid
    private List<ConditionSettingSaveDto> dtoList;

}
