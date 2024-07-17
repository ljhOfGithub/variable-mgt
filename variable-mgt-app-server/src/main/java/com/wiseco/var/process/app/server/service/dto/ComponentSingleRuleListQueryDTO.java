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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSingleRuleListQueryDTO implements Serializable {

    /**
     * 策略ID
     */
    @NotNull(message = "策略ID不能为空")
    @Schema(description = "策略ID", type = "Long", required = true, example = "1001")
    private Long strategyId;

    /**
     * 规则名称/编码
     */
    @Schema(description = "规则名称", type = "String", example = "1")
    private String nameOrCode;

    /**
     * 已经被选中的规则identifier列表（被选中的规则不能再次被引入）
     */
    @Schema(description = "已经被选中的规则identifier列表（被选中的规则不能再次被引入）", type = "List", example = "1001")
    private List<String> selectedRuleIds;

    /**
     * 页码
     */
    @NotNull(message = "每页数量不能为空")
    @Schema(description = "页码", type = "Long", example = "1", required = true)
    private Long pageNo;

    /**
     * 每页数量
     */
    @NotNull(message = "每页数量不能为空")
    @Schema(description = "每页数量", type = "Long", example = "1", required = true)
    private Long pageSize;

}
