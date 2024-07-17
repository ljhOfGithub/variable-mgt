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
import com.wiseco.var.process.app.server.enums.MonitoringConfNotifyMethodEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfStateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 监控预警分页输出
 *
 * @author wiseco
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "监控预警分页输出")
public class MonitoringConfigurationPageOutputVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "监控类型")
    private MonitoringConfTypeEnum monitoringType;

    @Schema(description = "规则名称")
    private String confName;

    @Schema(description = "规则描述")
    private String confDesc;

    @Schema(description = "实时服务")
    private String serviceName;

    @Schema(description = "版本")
    private Integer serviceVersion;

    @Schema(description = "变量清单")
    private String manifestName;

    @Schema(description = "指标名称")
    private String variableName;

    @Schema(description = "告警等级")
    private AlertGradeEnum alertGrade;

    @Schema(description = "通知方式")
    private List<MonitoringConfNotifyMethodEnum> notifyMethodList;

    @Schema(description = "监控指标")
    private MonitoringConfTargetEnum monitoringTarget;

    @Schema(description = "状态")
    private MonitoringConfStateEnum monitoringState;

    @Schema(description = "创建人")
    private String createdUser;

    @Schema(description = "最后修改人")
    private String updatedUser;

    @Schema(description = "创建时间")
    private String createdTime;

    @Schema(description = "最后编辑时间")
    private String updatedTime;
}
