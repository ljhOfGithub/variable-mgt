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

import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 测试在线表单填写表单数据转换入参 DTO
 * 表单数据 to JSON 报文
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/19
 */
@Data
@Schema(description = "测试在线表单填写表单数据转换输入参数。表单数据 -> JSON 报文")
public class TestFormDataConvertInputDto {

    @Schema(description = "变量空间 ID", required = true, example = "1")
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "测试类型: 1 变量, 2 公共函数, 3 服务接口", example = "1")
    private Integer testType;

    @Schema(description = "变量 ID / 公共函数 ID / 接口 ID", example = "1")
    private Long id;

    @Schema(description = "输入数据", example = "null")
    private JSONObject inputValue;
}
