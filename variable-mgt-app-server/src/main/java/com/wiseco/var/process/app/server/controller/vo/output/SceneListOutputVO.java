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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.SceneStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "场景列表出参dto")
public class SceneListOutputVO implements Serializable {

    @Schema(description = "场景id")
    private Long id;

    @Schema(description = "场景名称")
    private String name;

    @Schema(description = "场景编码")
    private String code;

    @Schema(description = "数据源")
    private String dataSource;

    @Schema(description = "数据源名称")
    private String dataSourceName;

    @Schema(description = "数据模型名称")
    private String dataModelName;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    private SceneStateEnum state;

    @Schema(description = "状态中文")
    private String stateDesc;

    @Schema(description = "是否使用")
    private Boolean used;

    @Schema(description = "创建部门")
    private String createdDept;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updatedTime;

    @Schema(description = "创建人")
    private String createdUser;

    @Schema(description = "最近编辑人")
    private String updatedUser;

    public String getStateDesc() {
        return state.getDesc();
    }
}
