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

import java.io.Serializable;

/**
 * @author: fudengkui
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSingleRuleResultDTO implements Serializable {

    @Schema(description = "规则组件ID", type = "Long", example = "1", required = true)
    private Long componentId;

    @Schema(description = "规则名称", type = "String", example = "1", required = true)
    private String ruleName;

    @Schema(description = "规则编码", type = "String", example = "1", required = true)
    private String ruleCode;

    @Schema(description = "目录ID", type = "Long", example = "1", required = true)
    private Long directoryId;

    @Schema(description = "目录名称", type = "String", example = "1", required = true)
    private String directoryName;

    @Schema(description = "组件编号", type = "String", example = "1", required = true)
    private String identifier;

    @Schema(description = "描述", type = "String", example = "描述内容")
    private String description;

    @Schema(description = "策略ID", type = "Long", example = "1", required = true)
    private Long createInStrategyId;

    @Schema(description = "创建时间", type = "String", example = "2022-10-12 10:10:11", required = true)
    private String createTime;

}
