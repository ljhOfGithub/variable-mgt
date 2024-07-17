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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author: liaody
 * @since: 2021/10/21
 */
@Schema(description = "外部服务状态修改 入参DTO")
@Data
public class UpdateOutsideServiceStageInputDto {

    @Schema(description = "主键", required = true, example = "1")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "服务状态:0待发布 1已发布 2已停用", required = true, example = "1")
    private Integer state;

}
