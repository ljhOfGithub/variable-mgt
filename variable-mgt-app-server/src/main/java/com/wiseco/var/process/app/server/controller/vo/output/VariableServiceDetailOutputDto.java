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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实时服务详情查询结果输出参数
 *
 * @author Zhaoxiong Chen
 * @since 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实时服务详情查询结果输出参数")
public class VariableServiceDetailOutputDto {

    @Schema(description = "变量空间 ID")
    private Long varProcessSpaceId;

    @Schema(description = "服务编码")
    private String code;

    @Schema(description = "服务名称")
    private String name;

    @Schema(description = "服务类型(1: 实时 2: 批量)")
    private Integer type;

    @Schema(description = "服务地址")
    private String url;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否允许编辑服务 Flag(true 条件：服务下没有变量清单，或者变量清单的状态都是“编辑中”)")
    private Boolean editFlag;
}
