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

import java.io.Serializable;

/**
 * 变量外部服务列表出参 DTO
 *
 * @author kangyk
 * @since 2022/08/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量外部服务列表输出参数")
public class VariableOutsideServiceReferenceListOutputDto implements Serializable {

    private static final long serialVersionUID = -1592333970406413349L;

    @Schema(description = "外部服务名称")
    private String serviceName;

    @Schema(description = "发布变量数")
    private Integer publishVariableNumber;

    @Schema(description = "服务状态 0: 编辑中, 1: 发布中, 2: 已发布, 3: 发布失败, 4: 已停用")
    private Integer serviceStatus;

    @Schema(description = "变量清单版本")
    private String version;

}
