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

import com.wiseco.var.process.app.server.enums.AlertGradeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 监控预警结果分页输出
 *
 * @author wangxiansheng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "监控预警结果分页输出")
public class MonitoringResultPageOutputVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;


    @Schema(description = "规则名称")
    private String confName;

    @Schema(description = "监控指标")
    private String monitoringTarget;

    @Schema(description = "实时服务")
    private String serviceName;

    @Schema(description = "版本")
    private Integer serviceVersion;

    @Schema(description = "告警等级")
    private AlertGradeEnum alertGrade;

    @Schema(description = "告警类型")
    private Integer messageType;

    @Schema(description = "告警内容")
    private String alertMessage;

    @Schema(description = "告警时间")
    private String alertDate;


    @Schema(description = "指标名称")
    private String monitoringTargetName;

    @Schema(description = "变量清单")
    private String manifestName;




}
