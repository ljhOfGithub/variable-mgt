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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于显示变量信息的Vo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用于显示变量信息的Vo")
@EqualsAndHashCode
public class ReportFormVariableMappingVo implements Serializable {

    private static final long serialVersionUID = -6307931108016445998L;

    @Schema(description = "变量的Id", example = "10000")
    private Long variableId;

    @Schema(description = "变量的Code", example = "code")
    private String variableCode;

    @Schema(description = "变量的名称", example = "变量A")
    private String variableName;
}
