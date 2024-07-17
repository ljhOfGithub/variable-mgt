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
package com.wiseco.var.process.app.server.service.dto.innerdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import com.wiseco.var.process.app.server.commons.enums.TimeUnit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据集基本信息
 *
 * @author Asker.J
 * @since 2022/10/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "任务基本信息")
public class TaskInfoDto {

    @Schema(description = "执行频率:DAY,MONTH,FIXED", required = true)
    private JobExecuteFrequency executionFrequency;

    @Schema(description = "开始时间", required = true)
    private String startTime;

    @Schema(description = "是否创建后生效", required = true)
    private Boolean isValidAfterCreated;

    @Schema(description = "生效时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startDate;

    @Schema(description = "失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endDate;

    @Schema(description = "失败是否重试：0不重试，1重试", example = "null", required = true)
    private Integer isRetry;

    @Schema(description = "失败重试次数,  非重试任务传null")
    private Integer retryCount;

    @Schema(description = "重试时间间隔, 非重试任务传null")
    private Integer retryInterval;

    @Schema(description = "重试时间间隔单位, 非重试任务传null")
    private TimeUnit retryIntervalUnit;

    @Schema(description = "每月执行日，executeFrequency为EVERY_MONTH时必填")
    private String dayInMonth;

    @Schema(description = "目标执行月，executeFrequency为TARGET时必填")
    private List<String> targetMonths;

    @Schema(description = "目标执行日，executeFrequency为TARGET时必填")
    private List<String> targetDays;
}
