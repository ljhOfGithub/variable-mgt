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
package com.wiseco.var.process.app.server.service.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author: xiewu
 * @since: 2021/12/24
 */
@Schema(description = "外部服务系统方法 出参DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SystemMethodOutsideServiceOutputDto {

    @Schema(description = "主键", example = "1")
    private Long id;
    /**
     * 方法编码
     */
    @Schema(description = "方法编码", example = "")
    private String methodCode;

    /**
     * 方法名称
     */
    @Schema(description = "方法名称", example = "")
    private String methodName;

    /**
     * 方法描述
     */
    @Schema(description = "方法描述", example = "")
    private String methodDesc;

    /**
     * 方法路径
     */
    @Schema(description = "方法路径", example = "")
    private String methodPath;
}
