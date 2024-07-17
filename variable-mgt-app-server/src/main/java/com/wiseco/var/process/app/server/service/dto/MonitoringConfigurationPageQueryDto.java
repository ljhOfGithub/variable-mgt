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

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.AlertGradeEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfStateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author wuweikang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "监控告警配置分页查询参数")
public class MonitoringConfigurationPageQueryDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "监控类型", required = true)
    @NotNull(message = "监控类型不能为空")
    private MonitoringConfTypeEnum monitoringType;

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "服务版本")
    private Integer serviceVersion;

    @Schema(description = "清单名称")
    private String manifestName;

    @Schema(description = "监控指标")
    private MonitoringConfTargetEnum monitoringTarget;

    @Schema(description = "告警等级")
    private AlertGradeEnum alertGrade;

    @Schema(description = "状态")
    private MonitoringConfStateEnum monitoringState;

    @Schema(description = "规则名称")
    private String confName;

    @Schema(description = "排序字段", example = "1")
    private String sortKey;

    @Schema(description = "排序方式", example = "1")
    private String sortType;

    @Schema(description = "部门code集合")
    private List<String> deptCodes;

    @Schema(description = "用户名称集合")
    private List<String> userNames;
}
