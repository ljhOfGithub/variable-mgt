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

import com.wiseco.var.process.app.server.enums.SceneCmpSymbolEnum;
import com.wiseco.var.process.app.server.enums.SceneStateEnum;
import com.wiseco.var.process.app.server.enums.SceneVarRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "场景详情dto")
public class SceneDetailOutputVO implements Serializable {

    @Schema(description = "场景名称")
    private String name;

    @Schema(description = "场景编码")
    private String code;

    @Schema(description = "数据源")
    private String dataSource;

    @Schema(description = "数据模型名称")
    private String dataModelName;

    @Schema(description = "状态",allowableValues = {"ENABLED","DISABLED"})
    private SceneStateEnum state;

    @Schema(description = "变量角色：key-角色枚举；value-变量路径varPath")
    private Map<SceneVarRoleEnum, List<String>> varRoles;

    @Schema(description = "事件")
    private List<SceneEventDto> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SceneEventDto {

        @Schema(description = "事件名称")
        private String eventName;

        @Schema(description = "比较符",allowableValues = {"IN","EQUAL"})
        private SceneCmpSymbolEnum sceneCmpSymbol;

        @Schema(description = "比较符label",allowableValues = {"in","="})
        private String sceneCmpSymbolDesc;

        @Schema(description = "事件码值")
        private List<String> codeValue;

        public String getSceneCmpSymbolDesc() {
            return sceneCmpSymbol.getDesc();
        }
    }
}
