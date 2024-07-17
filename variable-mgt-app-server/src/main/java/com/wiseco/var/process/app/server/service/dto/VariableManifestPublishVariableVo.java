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

import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量清单-变量发布信息 VO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量清单-变量发布信息")
public class VariableManifestPublishVariableVo implements Serializable {

    private static final long serialVersionUID = 6644386232861612508L;

    @Schema(description = "变量标识符")
    private String identifier;

    @Schema(description = "变量名")
    private String name;

    @Schema(description = "变量中文名")
    private String label;

    @Schema(description = "变量分类")
    private String category;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "创建部门")
    private String dept;

    @Schema(description = "用户选择的版本")
    private VersionInfo selectedVersionInfo;

    @Schema(description = "变量全部上架版本列表")
    private List<VersionInfo> listedVersionInfoList;

    @Schema(description = "是否输出")
    private Integer outputFlag;

    @Schema(description = "是否搜索条件")
    private Boolean isIndex;

    @Schema(description = "列角色",allowableValues = {"GENERAL","TARGET","GROUP"},nullable = true)
    private ColRoleEnum colRole;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "实时服务输出变量信息-版本")
    public static class VersionInfo implements Serializable {

        private static final long serialVersionUID = -2520775674894100443L;

        @Schema(description = "变量 ID")
        private Long variableId;

        @Schema(description = "变量版本号")
        private Integer version;
    }
}
