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
package com.wiseco.var.process.app.server.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.AlertGradeEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfStateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 监控告警配置
 * </p>
 *
 * @author wiseco
 * @since 睿信2.3
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_monitoring_alert_conf")
public class VarProcessMonitoringAlertConf extends BaseEntity {
    /**
     * 监控类型：指标/服务
     */
    @TableField("monitoring_type")
    private MonitoringConfTypeEnum monitoringType;

    /**
     *  规则名称
     */
    @TableField("conf_name")
    private String confName;

    /**
     * 描述
     */
    @TableField("conf_desc")
    private String confDesc;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 服务版本
     */
    @TableField("service_version")
    private Integer serviceVersion;

    /**
     * 清单名称
     */
    @TableField("manifest_name")
    private String manifestName;

    /**
     * 监控指标
     */
    @TableField("monitoring_target")
    private MonitoringConfTargetEnum monitoringTarget;

    /**
     * 告警等级
     */
    @TableField("alert_grade")
    private AlertGradeEnum alertGrade;

    /**
     * 状态
     */
    @TableField("monitoring_state")
    private MonitoringConfStateEnum monitoringState;

    /**
     * 参数信息
     */
    @TableField("param_configuration_info")
    private String paramConfigurationInfo;

    /**
     *  触发条件信息
     */
    @TableField("trigger_condition_info")
    private String triggerCondition;

    /**
     * 告警信息
     */
    @TableField("alert_info")
    private String alertInfo;

    /**
     * 创建人
     */
    @TableField(value = "created_user")
    private String createdUser;

    /**
     * 最后修改人
     */
    @TableField(value = "updated_user")
    private String updatedUser;

    /**
     * 创建部门
     */
    @TableField(value = "dept_code")
    private String deptCode;

}
