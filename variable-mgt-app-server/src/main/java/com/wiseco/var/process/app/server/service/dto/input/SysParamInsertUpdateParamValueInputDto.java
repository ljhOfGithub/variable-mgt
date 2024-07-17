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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 新建或修改策略参数
 *
 * @author: zhouxiuxiu
 * @since: 2021/11/8 14:25
 */
@Data
@Schema(description = "设置系统参数值 入参DTO")
public class SysParamInsertUpdateParamValueInputDto {

    @Schema(description = "id", example = "1")
    @NotNull(message = "id不能为空！")
    private Long id;

    @Schema(description = "参数值", example = "https://xxx:80xx/xxx/xxx")
    @NotEmpty(message = "参数值不能为空！")
    private String paramValue;
}
