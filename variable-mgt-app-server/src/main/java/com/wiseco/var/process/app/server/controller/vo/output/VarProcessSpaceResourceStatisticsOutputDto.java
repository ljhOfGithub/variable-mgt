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
 * 变量空间资源概览出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量空间资源概览输出参数")
public class VarProcessSpaceResourceStatisticsOutputDto implements Serializable {

    private static final long serialVersionUID = -2935808923529279001L;

    @Schema(description = "上架变量数")
    private Integer listedVariableNumber;

    @Schema(description = "变量总数")
    private Integer totalVariableNumber;

    @Schema(description = "已发布变量数")
    private Integer releasedVariableNumber;

    @Schema(description = "对外服务数")
    private Long releasedServiceNumber;

    @Schema(description = "引用外部服务数")
    private Integer referencedExternalServiceNumber;
}
