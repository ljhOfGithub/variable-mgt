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
public class FileUploadDTO {
    @Schema(description = "新增：原文件名称")
    private String             fileName;

    @Schema(description = "oss文件路径：当文件传输完成后返回(上传文件，前端不需要赋值)")
    private String             ossFilePath;

    @Schema(description = "oss唯一标识：分片上传时的同一文件标识")
    private String             uploadId;

    @Schema(description = "文件总的分片数量")
    private Integer            chunkTotal;

    @Schema(description = "当前分片，从1开始")
    private Integer            chunkCurrent;

    @Schema(description = "md5")
    private String            md5;

    @Schema(description = "type")
    private String            type;
}
