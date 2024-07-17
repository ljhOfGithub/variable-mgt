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

import com.wiseco.var.process.app.server.service.dto.VariableManifestDeployContentOverviewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量清单发布数据预览出参 DTO
 * {@link VariableManifestDeployContentOverviewDto}
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单发布数据预览输出参数")
public class VariableManifestDeployContentOverviewOutputDto implements Serializable {

    private static final long serialVersionUID = 8911067816278871757L;

    @Schema(description = "变量名称")
    private String name;

    @Schema(description = "变量中文名")
    private String label;

    @Schema(description = "变量分类")
    private String category;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "版本号", example = "1")
    private Integer version;

    @Schema(description = "版本状态")
    private String status;
}
