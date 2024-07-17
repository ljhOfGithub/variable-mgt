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
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户上传的 PMML 模型, 解析结果输出 DTO
 *
 * @author liaody
 * @since 2022/2/9
 */
@Schema(description = "变量空间文档上传输出 DTO")
@Data
@Builder
public class VariableDocumentUploadOutputDto implements Serializable {

    private static final long serialVersionUID = -1622328266536044985L;

    @Schema(description = "文件名称", required = true)
    private String fileName;

    @Schema(description = "文件上传的路径", required = true)
    private String filePath;

    @Schema(description = "文件大小", required = true)
    private String fileSize;

    @Schema(description = "文件后缀", required = true)
    private String fileSuffix;

    @Schema(description = "文件预览名称", required = true)
    private String preViewName;
}
