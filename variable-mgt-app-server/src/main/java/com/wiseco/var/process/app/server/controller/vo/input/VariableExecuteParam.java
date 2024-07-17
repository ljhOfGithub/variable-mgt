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
import java.io.Serializable;

/**
 * 变量执行参数
 *
 * @author zhangyang
 * @since 2024/3/28
 */
@Data
@Schema(description = "变量执行参数")
public class VariableExecuteParam implements Serializable {

    private static final long serialVersionUID = 7786278302679481035L;

    @Schema(description = "服务code")
    @NotNull(message = "服务code 不能为空")
    private String serviceCode;

    @Schema(description = "变量请求参数")
    private String requestContent;

    @Schema(description = "参数格式", example = "JSON/XML")
    private String msgFormat;

    @Schema(description = "离线包文件夹", example = "D:\\varService")
    @NotNull(message = "离线包文件夹不能为空")
    private String libPath;

}
