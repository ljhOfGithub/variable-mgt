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

import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Schema(description = "新建实时服务Vo")
@Data
public class ServiceSaveInputVO implements Serializable {

    private static final long serialVersionUID = 2013346166832342248L;

    @NotNull(message = "变量空间 ID 不能为空")
    @Schema(description = "变量空间 ID", required = true)
    private Long spaceId;

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称不能超过 100 个字符")
    @Schema(description = "服务名称", required = true)
    private String name;

    @NotBlank(message = "实时服务的编码不能为空")
    @Size(max = 100, message = "实时服务的编码不能超过 100 个字符")
    @Pattern(regexp = "^[@:a-zA-Z_\\-]{1}[@:0-9a-zA-Z_\\-]{0,99}$", message = "服务编码只能包含大小写字母、数字、中横线、下划线、冒号和@符号")
    @Schema(description = "服务编码", required = true)
    private String code;

    @Schema(description = "实时服务id，编辑时传入")
    private String id;

    @NotNull(message = "请选择服务分类")
    @Schema(description = "服务分类ID", required = true)
    private Long categoryId;

    @Schema(description = "报文格式",allowableValues = {"JSON","XML"})
    private ServiceMsgFormatEnum messageFormat;

    @Schema(description = "是否开启trace")
    private Boolean enableTrace;
}
