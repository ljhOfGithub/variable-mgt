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
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.service.dto.OperationButton;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单列表Vo")
public class ManifestListOutputVo implements Serializable {

    private static final long SERIA_VERSIONUID = 8799865904865173992L;

    @Schema(description = "变量清单id")
    private Long id;

    @Schema(description = "变量清单名称")
    private String name;

    @Schema(description = "变量清单分类")
    private String category;

    @Schema(description = "状态：0-编辑中，1-测试中，2-待审核，3-审核拒绝…")
    private VarProcessManifestStateEnum status;

    @Schema(description = "是否测试：0-否；1-是")
    private Boolean tested;

    @Schema(description = "是否使用：0-否；1-是")
    private Boolean used;

    @Schema(description = "创建部门名称")
    private String deptName;

    @Schema(description = "创建人")
    private String createdUser;

    @Schema(description = "最近编辑人")
    private String updatedUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "最近编辑时间")
    private Date updatedTime;

    @Schema(description = "审核拒绝信息")
    private String approDescription;

    @Schema(description = "按钮", example = "null")
    private List<OperationButton> operationButton;

    @Schema(description = "变量数量")
    private Long varAmount;
}
