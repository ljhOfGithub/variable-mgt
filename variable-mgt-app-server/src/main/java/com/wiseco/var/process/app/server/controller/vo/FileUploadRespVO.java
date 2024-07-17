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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.repository.entity.SysOss;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Asker.J
 * @since 2022/4/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分片数据预览上传结果返回")
public class FileUploadRespVO {

    @Schema(description = "oss存储唯一标识")
    private String                 uploadId;

    @Schema(description = "文件名")
    private String                 fileName;

    @Schema(description = "是否完成上传")
    private Boolean                 isFinish;

    @Schema(description = "mysql中的文件id")
    private Long                 fileId;

    @Schema(description = "mysql中的文件实体")
    private SysOss                 sysOss;





}
