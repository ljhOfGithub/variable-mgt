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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变量空间引入的外部服务信息 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间引入的外部服务信息")
public class VariableSpaceReferencedOutsideServiceInfoDto {

    @Schema(description = "外部服务 ID")
    private Long id;

    @Schema(description = "外部服务名称")
    private String name;

    @Schema(description = "服务编码")
    private String code;

    @Schema(description = "服务类型")
    private Integer type;

    @Schema(description = "数据缓存期 (0表示不缓存)")
    private Integer dataCache;

    @Schema(description = "缓存单位: 1: 天, 2: 小时, 3: 秒")
    private Integer dataCacheType;

    @Schema(description = "服务状态: 0: 停用, 1: 启用")
    private Integer state;

    @Schema(description = "引入状态: 0: 未引入, 1: 已引入")
    private Integer refStatus;
}
