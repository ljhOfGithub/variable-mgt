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

import com.wiseco.var.process.app.server.enums.SysDynamicSpaceTypeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
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
@Schema(description = "动态信息保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VariableDynamicSaveInputDto implements Serializable {

    private static final long serialVersionUID = 8275238326146990604L;

    /**
     * @see SysDynamicSpaceTypeEnum
     */
    @Schema(description = "空间类型", example = "1")
    private String spaceType;

    @Schema(description = "变量空间ID", required = true, example = "1")
    @NotNull(message = "变量空间ID不能为空")
    private Long varSpaceId;

    /**
     * 操作类型：提交测试、创建、编辑、添加、
     *
     * @see SysDynamicOperateTypeEnum
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

    /**
     * 业务描述
     */
    @Schema(description = "业务相关内容", example = "1")
    private String businessDesc;

    @Schema(description = "用户名", example = "1")
    private String userName;

}
