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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBasicConfigVo implements Serializable {

    private static final long serialVersionUID = -8955607736888415259L;

    @Schema(description = "空间id")
    private Long spaceId;

    @Schema(description = "服务id")
    @NotNull(message = "服务id不能为空")
    private Long serviceId;

    @Schema(description = "版本id")
    @NotNull(message = "请传入版本id")
    private Long versionId;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "服务名")
    @NotNull(message = "服务名不能为空")
    @Size(max = 100, message = "服务名称不能超过 100 个字符")
    private String serviceName;

    @Schema(description = "服务编码")
    @NotBlank(message = "服务编码不能为空")
    @Size(max = 100, message = "服务编码不能超过 100 个字符")
    @Pattern(regexp = "^[@:a-zA-Z_\\-]{1}[@:0-9a-zA-Z_\\-]{0,99}$", message = "服务编码只能包含大小写字母、数字、中横线、下划线、冒号和@符号")
    private String code;

    @Schema(description = "服务分类")
    @NotNull(message = "服务分类不能为空")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String category;

    @Schema(description = "服务描述")
    private String description;

    @Schema(description = "开启Trace")
    @NotNull(message = "Trace日志是否开启不能为空")
    private Boolean enableTrace;

    @Schema(description = "报文格式")
    private ServiceMsgFormatEnum messageFormat;
}
