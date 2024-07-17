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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "文件分片上传")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileFtpUploadDTO {
    @Schema(description = "ftp 服务器id")
    private Long               ftpServerId;

    @Schema(description = "ftp 文件路径")
    private String             filePath;

    @Schema(description = "新增：原文件名称")
    private String             fileName;

    @Schema(description = "文件大小")
    private Long             fileSize;

    @Schema(description = "oss文件路径：当文件传输完成后返回(上传文件，前端不需要赋值)")
    private String             ossUploadPath;

    @Schema(description = "是否预览")
    private Boolean            bPreview;

}
