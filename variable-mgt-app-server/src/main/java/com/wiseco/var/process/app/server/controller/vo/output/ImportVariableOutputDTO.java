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
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "变量 Excel 导入输出参数")
public class ImportVariableOutputDTO implements Serializable {
    private static final long serialVersionUID = 4658703533891557640L;

    @Schema(description = "导入的变量集合")
    private List<Long> variableDetailIds;

    @Schema(description = "文件总行数")
    private Integer totalRows;

    @Schema(description = "校验通过行数")
    private Integer validRows;

    @Schema(description = "校验失败行数")
    private Integer inValidRows;

    @Schema(description = "错误详情描述")
    private List<ErrorInfo> failReason;

    @Schema(description = "本次请求的标志Id,继续导入时传入")
    private String identifier;

    @Data
    public static class ErrorInfo {
        @Schema(description = "错误描述")
        private String desc;
        @Schema(description = "错误详情")
        private String details;
    }
}
