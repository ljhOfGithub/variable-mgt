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

import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author: xiewu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "根据变量路径查询变量或公共函数 返回对象")
public class VariableDataModelManifestUseVo {

    /**
     * 变量空间 ID
     */
    @Schema(description = "变量空间ID", example = "1")
    private Long varProcessSpaceId;

    /**
     * 变量清单类别ID
     */
    @Schema(description = "变量清单类别ID", example = "1")
    private String varManifestName;

    /**
     * 变量清单类别ID
     */
    @Schema(description = "变量清单类别ID", example = "1")
    private Long parentCategoryId;

    /**
     * 实时服务 ID
     */
    @Schema(description = "实时服务ID", example = "1")
    private Long serviceId;

    /**
     * 接口版本
     */
    @Schema(description = "接口版本", example = "1")
    private Integer version;

    /**
     * 状态 0: 编辑中, 1: 测试中, 2: 待审核, 3: 审核拒绝, 4: 启用中, 5: 启用, 6: 停用, 7: 发布失败
     */
    @Schema(description = "状态", example = "1")
    private VarProcessManifestStateEnum state;

    /**
     * 流水号
     */
    @Schema(description = "流水号", example = "1")
    private String serialNo;

    /**
     * 版本说明
     */
    @Schema(description = "版本说明", example = "1")
    private String description;

    /**
     * 选择发布的变量请求结构 JSON Schema 快照
     */
    @Schema(description = "选择发布的变量请求结构 JSON Schema 快照", example = "1")
    private String schemaSnapshot;

    /**
     * 删除标志 0-已删除, 1-可用
     */
    @Schema(description = "删除标志 0-已删除, 1-可用", example = "1")
    private Integer deleteFlag;

    /**
     * 父变量清单ID
     */
    @Schema(description = "父变量清单ID", example = "15000")
    private Long parentManifestId;

    /**
     * 内容
     */
    @Schema(description = "内容", example = "123456")
    private String content;

    /**
     * 创建人
     */
    @Schema(description = "创建人", example = "admin")
    private String createdUser;

    /**
     * 更新人
     */
    @Schema(description = "更新人", example = "admin")
    private String updatedUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "1")
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "1")
    private Timestamp updatedTime;

    /**
     * TODO 部门Id
     */
    @Schema(description = "部门Id", example = "1")
    private Long deptId;

    @Schema(description = "分类", example = "1")
    private String allClass;

    @Schema(description = "变量清单ID", example = "1")
    private String id;
}
