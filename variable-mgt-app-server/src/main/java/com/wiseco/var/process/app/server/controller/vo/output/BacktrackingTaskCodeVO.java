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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mingao
 * @since 2023/9/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务批次号(带开始时间) VO")
public class BacktrackingTaskCodeVO {
    @Schema(description = "任务批次号code")
    private String code;

    @Schema(description = "列数据记录")
    private Date startTime;

    /**
     * 生成任务Code
     * @return code
     */
    public String generateTaskCode() {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatTime = outputFormat.format(this.startTime);
        return this.code + " (" + formatTime + ")";
    }
}
