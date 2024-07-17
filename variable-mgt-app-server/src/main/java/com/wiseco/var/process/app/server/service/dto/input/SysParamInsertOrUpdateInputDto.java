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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 新建或修改策略参数
 *
 * @author: zhouxiuxiu
 * @since: 2021/11/8 14:25
 */
@Data
@Schema(description = "新建或修改系统参数 入参DTO")
public class SysParamInsertOrUpdateInputDto {

    @Schema(description = "id：为空表示新增，不为空表示修改", example = "1")
    private Long id;

    @Schema(description = "参数名", example = "fico-Url")
    @NotBlank(message = "参数名不能为空")
    @Size(min = 1, max = 50, message = "参数名不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z\\d()_]{0,49}$", message = "参数名只能包含大小写字母、数字、英文括号、下划线")
    private String paramName;

    @Schema(description = "参数中文名", example = "FICO分调用-请求地址")
    @NotBlank(message = "参数中文名不能为空")
    @Size(min = 1, max = 50, message = "参数中文名不能超过50个字符")
    private String paramNameCn;

    @Schema(description = "数据类型：int、double、string、boolean、date、datetime", example = "string")
    @NotEmpty(message = "数据类型不能为空！")
    private String dataType;

    @Schema(description = "参数类型：1内置参数 2自定义参数", example = "2")
    private Integer paramType;

    @Schema(description = "参数值", example = "https://xxx:80xx/xxx/xxx")
    @NotEmpty(message = "参数值不能为空！")
    private String paramValue;
}
