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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量结果报文出参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量结果报文查看输出参数")
public class VariableResultDatagramQueryOutputDto implements Serializable {

    private static final long serialVersionUID = -1059909638844489773L;

    @Schema(description = "请求报文")
    private String request;

    @Schema(description = "引擎使用数据")
    private String rawData;

    @Schema(description = "响应报文")
    private String response;

    @Schema(description = "异常报文")
    private String errorMessage;
}
