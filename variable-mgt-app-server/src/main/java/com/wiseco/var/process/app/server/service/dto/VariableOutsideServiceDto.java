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
import lombok.Data;

/**
 * @author wangxianli
 */
@Data
@Schema(description = "变量外部服务Dto")
public class VariableOutsideServiceDto {

    @Schema(description = "变量ID", example = "1")
    private Long variableId;

    @Schema(description = "变量名称", example = "1")
    private String variableName;

    @Schema(description = "变量中文名", example = "1")
    private String variableLabel;

    @Schema(description = "数据类型", example = "1")
    private String dataType;

    @Schema(description = "服务名称", example = "1")
    private String serviceName;

    @Schema(description = "服务编码", example = "1")
    private String serviceCode;

    @Schema(description = "外部服务引入内容", example = "1")
    private String inputParameterBindings;

}
