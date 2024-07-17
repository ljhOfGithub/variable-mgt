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

import com.wiseco.var.process.app.server.enums.BacktrackingDataTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileTypeEnum;
import com.wiseco.var.process.app.server.enums.test.BacktrackingFileSpliteTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xupei
 */
@Data
@Schema(description = "输出配置参数（文件）")
public class BacktrackingOutputFile implements Serializable {
    @Schema(description = "结果文件命名")
    private String fileName;
    @Schema(description = "ftp服务器数据id")
    private Long ftpServerId;
    @Schema(description = "数据格式")
    private BacktrackingDataTypeEnum dataType;
    @Schema(description = "输出文件格式")
    private BacktrackingFileTypeEnum fileType;
    @Schema(description = "成功标志文件")
    private Boolean okFileFlag;
    @Schema(description = "输出路径-指定")
    private String defaultPath;
    @Schema(description = "输出路径-填写")
    private String filePath;
    @Schema(description = "文件拆分方式")
    private BacktrackingFileSpliteTypeEnum fileSplitType;
    @Schema(description = "文件行数")
    private Integer fileSize;
}
