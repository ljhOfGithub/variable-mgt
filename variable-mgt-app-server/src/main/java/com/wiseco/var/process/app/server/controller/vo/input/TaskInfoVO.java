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

import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import com.wiseco.var.process.app.server.commons.enums.TimeUnit;
import com.wiseco.var.process.app.server.enums.TaskExecuteType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ycc
 */
@Data
@Schema(description = "批量预测任务配置信息")
public class TaskInfoVO {
    @Schema(description = "执行方式")
    @NotNull(message = "任务执行方式未选择")
    private TaskExecuteType executeType;

    @Schema(description = "执行频率")
    private JobExecuteFrequency executeFrequency;
    @Schema(description = "每月执行日，executeFrequency为EVERY_MONTH时必填")
    private Integer dayInMonth;
    @Schema(description = "目标执行月，executeFrequency为TARGET时必填")
    private List<Integer> targetMonths;
    @Schema(description = "目标执行日，executeFrequency为TARGET时必填")
    private List<Integer> targetDays;
    @Schema(description = "执行时间：格式，小时:分钟")
    private String executeTime;

    @Schema(description = "是否创建后生效")
    private Boolean isValidAfterCreated;
    @Schema(description = "定时任务生效时间")
    private String startTime;
    @Schema(description = "是否无过期时间")
    private Boolean isNoExpireTime;
    @Schema(description = "定时任务过期时间")
    private String expireTime;
    @Schema(description = "失败是否重试")
    private Boolean isRetry;
    @Schema(description = "失败重试次数")
    private Integer retryTimes;
    @Schema(description = "重试时间间隔")
    private Integer retryInterval;
    @Schema(description = "重试时间间隔单位")
    private TimeUnit retryIntervalUnit;
}
