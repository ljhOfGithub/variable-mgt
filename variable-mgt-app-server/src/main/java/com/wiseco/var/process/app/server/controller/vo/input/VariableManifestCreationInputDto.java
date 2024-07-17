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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 新增变量接口版本入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/26
 */
@Data
@Schema(description = "新增变量清单版本输入参数")
public class VariableManifestCreationInputDto implements Serializable {

    private static final long serialVersionUID = 4784418045566309633L;

    @NotNull
    @Schema(description = "变量清单名称", required = true)
    private String manifestName;

    @NotNull
    @Schema(description = "变量清单类型", required = true)
    private String manifestType;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotNull(message = "实时服务 ID 不能为空")
    @Schema(description = "实时服务 ID", required = true)
    private Long serviceId;

    @Schema(description = "版本说明")
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    @NotNull(message = "需要选择版本来源")
    @Range(min = 1, max = 2, message = "请选择正确的版本来源")
    @Schema(description = "版本来源 (创建方法)", example = "1: 新建, 2: 复制已有", required = true)
    private Integer createApproach;

    @Schema(description = "被复制接口版本 ID (仅限版本来源为 \"复制已有\" 时填写)")
    private Long archetypeManifestId;
}
