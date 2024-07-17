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
package com.wiseco.var.process.app.server.service.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fudengkui
 * @since : 2023-02-23 10:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "参数DTO")
public class ParameterDTO implements Serializable {

    @Schema(description = "parameter编号", type = "String", example = "0005DAF3E2D7B200")
    private String identifier;

    @Schema(description = "class编号", type = "String", example = "0005DAF3E2D7B200")
    private String methodIdentifier;

    @Schema(description = "参数名", required = true, type = "String", example = "setCode")
    private String name;

    @Schema(description = "java类型", required = true, type = "String", example = "string")
    private String javaType;

    @Schema(description = "wrl类型", required = true, type = "String", example = "string")
    private String wrlType;

    @Schema(description = "参数索引", required = true, type = "Integer", example = "1")
    private Integer idx;

    @Schema(description = "是否数组：0=否，1=是", required = true, type = "Integer", example = "1")
    private Integer isArray;

}
