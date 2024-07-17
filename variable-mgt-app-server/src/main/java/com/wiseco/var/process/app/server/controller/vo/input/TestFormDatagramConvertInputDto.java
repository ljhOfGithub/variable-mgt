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

import javax.validation.constraints.NotNull;

/**
 * 测试在线表单填写报文转换入参 DTO
 * JSON/XML 报文 to 表单数据
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/15
 */
@Data
@Schema(description = "测试在线表单填写报文转换输入参数。JSON/XML 报文 -> 表单数据")
public class TestFormDatagramConvertInputDto {

    @Schema(description = "变量空间 ID", required = true, example = "1")
    @NotNull(message = "变量空间 ID 不能为空")
    private Long spaceId;

    @Schema(description = "测试类型: 1 变量, 2 公共函数, 3 服务接口", example = "1")
    private Integer testType;

    @Schema(description = "变量 ID / 公共函数 ID / 接口 ID", example = "1")
    private Long id;

    @Schema(description = "数据明细在数据集中的序号(此项可为空, 如果为空则后端设置序号默认值为 1)", example = "1")
    private Integer dataId;

    @Schema(description = "报文字符串")
    private String datagram;
}
