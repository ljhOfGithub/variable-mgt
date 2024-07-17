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
package com.wiseco.var.process.app.server.service.dto.input;

import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wangxianli
 * @since 2022/3/2
 */
@Schema(description = "测试集查询DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SysDynamicSaveInputDto implements Serializable {

    private static final long serialVersionUID = 8275238326146990604L;

    @Schema(description = "空间类型", example = "1")
    private String spaceType;

    @Schema(description = "领域ID", required = true, example = "1")
    @NotNull(message = "领域ID不能为空")
    private Long domainId;

    @Schema(description = "操作空间类型", example = "1")
    private String operateSpaceType;

    @Schema(description = "操作空间对应的业务ID", example = "1")
    private Long operateSpaceId;

    /**
     * 操作类型：提交测试、创建、编辑、添加、
     */
    @Schema(description = "操作类型", required = true, example = "1")
    @NotNull(message = "操作类型不能为空")
    private String operateType;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", required = true, example = "1")
    private SysDynamicBusinessBucketEnum typeEnum;

    /**
     * 业务Id：结合业务类型，用于跳转URL的拼接
     */
    @Schema(description = "业务Id", required = true, example = "1")
    @NotNull(message = "业务Id不能为空")
    private Long businessId;

    @Schema(description = "组件编号", example = "1")
    private String identifier;

    /**
     * 业务描述
     */
    @Schema(description = "业务相关内容", example = "1")
    private String businessDesc;

    /**
     * 权限业务ID，同permission_role_resources的resources_id
     */
    @Schema(description = "权限业务ID", required = true, example = "1")
    @NotNull(message = "权限业务ID不能为空")
    private Long permissionResourcesId;

    /**
     * 策略ID，策略下的业务操作需要传入
     */
    @Schema(description = "策略ID", required = true, example = "1")
    private Long strategyId;

    /**
     * 业务描述
     */
    @Schema(description = "用户名", example = "1")
    private String userName;

    /**
     * 日志状态
     */
    @Schema(description = "日志状态：0-失败，1-成功", example = "1")
    private Integer dynamicStatus;

}
