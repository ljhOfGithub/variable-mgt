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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class CallVolumeDownLoadDto implements Serializable {

    private static final long serialVersionUID = 8275238326146990605L;

    @Schema(description = "实时服务id")
    private String serviceId;

    @Schema(description = "时间类型(当天、昨天、最近七天、最近三十天、最近三个月)")
    private String whichTime;

    @Schema(description = "方式类型(按数量、按比例)")
    private String whichWay = "";

    @Schema(description = "附件")
    private MultipartFile file;
    @ModelAttribute("file")
    public MultipartFile getFile() {
        return file;
    }
}
