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

/**
 * @author wuweikang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "监控告警配置分页输入参数")
public class MonitoringConfigurationPageInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;
    /**
     * 监控类型
     */
    @Schema(description = "监控类型", required = true)
    @NotNull(message = "监控类型不能为空")
    private MonitoringConfTypeEnum monitoringType;
    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;
    /**
     * 服务版本
     */
    @Schema(description = "服务版本")
    private Integer serviceVersion;
    /**
     * 清单名称
     */
    @Schema(description = "清单名称")
    private String manifestName;
    /**
     * 监控指标
     */
    @Schema(description = "监控指标")
    private MonitoringConfTargetEnum monitoringTarget;
    /**
     * 告警等级
     */
    @Schema(description = "告警等级")
    private AlertGradeEnum alertGrade;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private MonitoringConfStateEnum monitoringState;
    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String confName;
    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "label_asc")
    private String order;
}
