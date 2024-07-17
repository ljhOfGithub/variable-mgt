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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.wiseco.var.process.app.server.enums.SceneCmpSymbolEnum;
import com.wiseco.var.process.app.server.enums.SceneVarRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "场景保存dto")
public class SceneSaveInputVO implements Serializable {

    @Schema(description = "场景id,编辑时传入",nullable = true)
    private Long id;

    @NotEmpty(message = "请输入场景名称")
    @Schema(description = "场景名称")
    private String name;

    @NotEmpty(message = "请输入场景编码")
    @Schema(description = "场景编码")
    private String code;

    @NotEmpty(message = "请选择数据源")
    @Schema(description = "数据源")
    private String dataSource;

    @NotEmpty(message = "请选择数据模型")
    @Schema(description = "数据模型名称")
    private String dataModelName;

    @Schema(description = "变量角色：key-角色枚举；value-变量路径varPath")
    private Map<SceneVarRoleEnum,List<String>> varRoles;

    @Size(min = 1,message = "至少定义一个事件")
    @Valid
    @Schema(description = "事件")
    private List<SceneEventDto> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneEventDto {

        @NotEmpty(message = "事件信息填写不完整，请检查")
        @Schema(description = "事件名称")
        private String eventName;

        @NotNull(message = "事件信息填写不完整，请检查")
        @Schema(description = "比较符",allowableValues = {"IN","EQUAL"})
        private SceneCmpSymbolEnum sceneCmpSymbol;

        @Size(min = 1,message = "事件信息填写不完整，请检查")
        @Schema(description = "事件码值")
        private List<String> codeValue;
    }
}
