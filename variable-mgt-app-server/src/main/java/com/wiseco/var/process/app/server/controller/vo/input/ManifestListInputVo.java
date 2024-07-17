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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单列表获取入参")
public class ManifestListInputVo extends PageDTO implements Serializable {

    private static final long serialVersionUID = 8847955908944973992L;

    @Schema(description = "空间ID", example = "90013000")
    private Long spaceId;

    @Schema(description = "查询条件-变量清单分类")
    private Long categoryId;

    @Schema(description = "查询条件-状态：EDIT-编辑中，UNAPPROVED-待审核，REFUSE-审核拒绝，UP-启用，DOWN-停用", example = "EDIT")
    private VarProcessManifestStateEnum status;

    @Schema(description = "查询条件-是否测试：0-否；1-是")
    private Boolean tested;

    @Schema(description = "查询条件-是否使用：0-否；1-是")
    private Boolean used;

    @Schema(description = "查询条件-创建部门(code)")
    private String deptId;

    @Schema(description = "模糊查询条件-变量清单名称")
    private String keywords;

    @Schema(description = "排序关键字")
    private String order;
}
