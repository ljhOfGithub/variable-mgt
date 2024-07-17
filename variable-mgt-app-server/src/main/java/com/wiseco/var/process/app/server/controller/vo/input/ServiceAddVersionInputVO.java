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

@Schema(description = "服务新增版本VO")
@Data
public class ServiceAddVersionInputVO {

    @Schema(description = "服务id")
    @NotNull(message = "请传入服务id")
    private Long id;

    @Schema(description = "服务版本号")
    private Integer version;

    @Schema(description = "复制的服务id,复制已有版本时传入,重新创建则不传入")
    private Long copiedServiceId;

    @Schema(description = "描述")
    private String description;
}
