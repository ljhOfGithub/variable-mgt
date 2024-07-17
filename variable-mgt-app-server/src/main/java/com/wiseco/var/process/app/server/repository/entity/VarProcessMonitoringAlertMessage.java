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
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author wuweikang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_monitoring_alert_message")
public class VarProcessMonitoringAlertMessage extends BaseEntity {
    /**
     * 监控类型：指标/服务
     */
    @TableField("monitoring_type")
    private MonitoringConfTypeEnum monitoringType;

    /**
     * 消息类型 1.告警2.恢复提醒
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 规则id
     */
    @TableField("monitoring_alert_conf_id")
    private Long monitoringAlertConfId;

    /**
     *  规则名称
     */
    @TableField("conf_name")
    private String confName;

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
     * 监控对象名称
     */
    @TableField("monitoring_target_name")
    private String monitoringTargetName;

    /**
     * 告警等级
     */
    @TableField("alert_grade")
    private AlertGradeEnum alertGrade;

    /**
     * 告警内容
     */
    @TableField("alert_message")
    private String alertMessage;

    /**
     * 告警时间
     */
    @TableField("alert_date")
    private LocalDateTime alertDate;
}
