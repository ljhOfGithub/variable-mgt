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

import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.SceneVarRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "变量角色")
public class SceneVarRoleDto {

    @Schema(description = "场景变量角色枚举",allowableValues = {"MATCH_DIMENSION","SERIAL_NUMBER","TIME","EVENT_TYPE"})
    private SceneVarRoleEnum varRoleEnum;
    @Schema(description = "中文描述",allowableValues = {"匹配维度","流水号","时间","事件类型"})
    private String label;
    @Schema(description = "变量数据类型")
    private List<String> varTypeList;

    public String getLabel() {
        return varRoleEnum.getDesc();
    }

    public List<String> getVarTypeList() {
        return varRoleEnum.getVarTypeList().stream().map(DataVariableTypeEnum::getMessage).collect(Collectors.toList());
    }
}
