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
@Schema(description = "变量组件引用关系列表返回对象")
public class VariableComponentRefInfoDto implements Serializable {

    @Schema(description = "策略id", example = "")
    private Long strategyId;

    @Schema(description = "组件id", example = "1")
    private Long componentId;

    @Schema(description = "组件名称", example = "组件")
    private String componentName;

    @Schema(description = "组件类型", example = "rule")
    private String componentType;

}
