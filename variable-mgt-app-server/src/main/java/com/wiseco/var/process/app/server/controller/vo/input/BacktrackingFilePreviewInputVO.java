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

import com.wiseco.var.process.app.server.enums.BacktrackingFileImportTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileSpiltCharEnum;
import com.wiseco.var.process.app.server.enums.CharsetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wuweikang
 */
@Schema(description = "批量回溯文件预览入参")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingFilePreviewInputVO implements Serializable {

    private static final long serialVersionUID = 8275238326146994605L;

    @Schema(description = "添加方式")
    @NotNull(message = "添加方式不能为空")
    private BacktrackingFileImportTypeEnum dataFileType;

    @Schema(description = "分隔符")
    @NotNull(message = "分隔符不能为空")
    private BacktrackingFileSpiltCharEnum split;

    @Schema(description = "分隔符_其他")
    private String splitKey;

    @Schema(description = "列引号")
    private String quoteChar;

    @Schema(description = "文件编码")
    @NotNull(message = "文件编码不能为空")
    private CharsetType charsetType;

    @Schema(description = "起始行")
    @NotNull(message = "起始行不能为空")
    private Integer startLine;

    @Schema(description = "有无表头")
    @NotNull(message = "有无表头不能为空")
    private Boolean includeHeader;

    @Schema(description = "文件服务器ID：添加方式为文件服务器上传时传入")
    private Long ftpServerId;

    @Schema(description = "文件服务器文件目录：添加方式为文件服务器上传时传入")
    private String directory;

    @Schema(description = "数据文件名称：添加方式为文件服务器上传时传入")
    private String fileName;

    @Schema(description = "本地数据文件ID，添加方式为本地上传：编辑时传入")
    private Long localFileId;

    @Schema(description = "本地文件，添加方式为本地上传：添加时传入")
    private MultipartFile file;

    @ModelAttribute("file")
    public MultipartFile getFile() {
        return file;
    }
}
