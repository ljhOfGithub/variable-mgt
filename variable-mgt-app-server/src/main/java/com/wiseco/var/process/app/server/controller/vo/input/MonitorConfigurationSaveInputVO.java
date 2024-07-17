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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.enums.AlertGradeEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfComparisonOperatorsEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfStateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTimeUnitEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import com.wiseco.var.process.app.server.enums.TriggerRuleEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控预警配置保存入参
 *
 * @author wuweikang
 */
@Schema(description = "监控预警配置保存入参")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class MonitorConfigurationSaveInputVO implements Serializable {

    @Schema(description = "Id")
    private Long id;

    @Schema(description = "监控类型", required = true)
    @NotNull(message = "监控类型不能为空")
    private MonitoringConfTypeEnum monitoringType;

    @Schema(description = "规则名称", required = true)
    @NotEmpty(message = "规则名称不能为空")
    private String confName;

    @Schema(description = "服务名称")
    @NotEmpty(message = "服务名称不能为空")
    private String serviceName;

    @Schema(description = "服务版本")
    @NotNull(message = "服务版本不能为空")
    private Integer serviceVersion;

    @Schema(description = "清单Id,保存时传入")
    private Long manifestId;

    @Schema(description = "清单名称，用于查看回显")
    private String manifestName;

    @Schema(description = "状态")
    private MonitoringConfStateEnum monitoringState;

    @Schema(description = "规则描述")
    @Length(max = 500, message = "规则描述长度不大于500")
    private String confDesc;

    @Schema(description = "参数配置", required = true)
    @NotNull(message = "参数设置不能为空")
    @Valid
    private ParamConfiguration paramConfiguration;

    @Schema(description = "触发条件", required = true)
    @NotNull(message = "触发条件不能为空")
    private TriggerCondition triggerCondition;

    @Schema(description = "告警信息", required = true)
    @NotNull(message = "告警信息不能为空")
    @Valid
    private AlertInfo alertInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "参数配置")
    @ToString
    public static class ParamConfiguration implements Serializable {

        @Schema(description = "时间", required = true)
        @NotNull(message = "时间维度数值不能为空")
        private Integer time;

        @Schema(description = "时间单位", required = true)
        @NotNull(message = "时间维度单位不能为空")
        private MonitoringConfTimeUnitEnum timeUnit;

        @Schema(description = "监控指标", required = true)
        @NotNull(message = "监控指标不能为空")
        private MonitoringConfTargetEnum monitoringTarget;

        @Schema(description = "监控对象")
        @Valid
        private List<MonitoringObject> monitoringObjectList;

        @Schema(description = "特殊值参数配置,监控指标不为缺失率时必填")
        @Valid
        private List<ConfigSpecialMappingVo> specialMappingVoList;

        @Schema(description = "psi计算参数配置,当监控指标为psi时必填")
        @Valid
        private PsiConfig psiMappingVo;

        @Schema(description = "iv值计算参数设置")
        @Valid
        private ConfigIvMappingVo ivMappingVo;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "监控对象")
    @ToString
    public static class MonitoringObject implements Serializable {
        @Schema(description = "identifier", required = true)
        @NotEmpty(message = "变量identifier不能为空")
        private String identifier;

        @Schema(description = "变量名称", required = true)
        @NotEmpty(message = "变量名称不能为空")
        private String variableName;

        @Schema(description = "变量编码", required = true)
        @NotEmpty(message = "变量编码不能为空")
        private String variableCode;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "PSI设置")
    @ToString
    public static class PsiConfig implements Serializable {
        @Schema(description = "基准数据 true/基准指标false", required = true)
        private Boolean baseIndexFlag;

        @Schema(description = "时间范围 1.上一小时同分种 2.上一天同分钟 3.指定时间范围 选择时间范围作为基准时不能为空")
        private Integer timeFrame;

        @Schema(description = "开始时间  选择指定时间范围作为基准时不能为空", example = "1970-01-01 00:00:00")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime startTime;

        @Schema(description = "结束时间  选择指定时间范围作为基准时不能为空")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime endTime;

        @Schema(description = "基准分组指标", example = "基准分组指标 选择基准指标时不能为空")
        private String baseIndex;

        @Schema(description = "基准数据项", example = "唯一值 选择基准指标时不能为空")
        private String baseIndexVal;

        @Schema(description = "基准指标调用时间段", example = "true 与设置的时间维度段一致 /false 所有时间段  选择基准指标时不能为空")
        private Boolean baseIndexCallDate;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "触发条件")
    @ToString
    public static class TriggerCondition implements Serializable {
        @Schema(description = "触发规则", required = true)
        @NotNull(message = "触发规则不能为空")
        private TriggerRuleEnum triggerRule;

        @Schema(description = "触发条件", required = true)
        @NotNull(message = "触发条件不能为空")
        private List<TriggerConditionDetail> conditionList;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "触发条件详情")
    @ToString
    public static class TriggerConditionDetail implements Serializable {
        @Schema(description = "比较符", required = true)
        private MonitoringConfComparisonOperatorsEnum comparisonOperators;

        @Schema(description = "右值类型", required = true)
        private String rightValueType;

        @Schema(description = "右值数值", required = true)
        private String rightValue;

        @Schema(description = "响应码")
        private String responseCode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "告警信息")
    @ToString
    public static class AlertInfo implements Serializable {

        @Schema(description = "告警等级", required = true)
        @NotNull(message = "告警等级不能为空")
        private AlertGradeEnum alertGrade;

        @Schema(description = "监控频率时间", required = true)
        @NotNull(message = "监控频率时间不能为空")
        private Integer monitorFrequencyTime;

        @Schema(description = "监控频率时间单位", required = true)
        @NotNull(message = "监控频率时间单位不能为空")
        private MonitoringConfTimeUnitEnum monitorFrequencyTimeUnit;

        @Schema(description = "有效期-起", required = true)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @NotNull(message = "有效期-起不能为空")
        private LocalDateTime startDate;

        @Schema(description = "有效期-止", required = true)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @NotNull(message = "有效期-止不能为空")
        private LocalDateTime endDate;

        @Schema(description = "告警内容", required = true)
        @Length(max = 500, message = "告警内容长度不大于500")
        @NotEmpty(message = "告警内容不能为空")
        private String alertMessage;

        @Schema(description = "沉默时间设置")
        @Valid
        private SilentConf silentConf;

        @Schema(description = "恢复提醒设置")
        @Valid
        private RestoreRemindConf restoreRemindConf;

        @Schema(description = "短信通知用户列表", required = true)
        @Valid
        private List<UserInfo> userListByNote;

        @Schema(description = "邮件通知用户列表", required = true)
        @Valid
        private List<UserInfo> userListByEmail;

        @Schema(description = "是否发送飞书消息")
        private Boolean feishuEnabled;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "沉默设置")
    @ToString
    public static class SilentConf implements Serializable {
        @Schema(description = "启用沉默")
        private Boolean enableSilentConf = true;

        @Schema(description = "沉默时间,启用则必填")
        private Integer silentTime;

        @Schema(description = "监控频率时间单位，启用则必填")
        private MonitoringConfTimeUnitEnum silentTimeUnit;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "恢复提醒设置")
    @ToString
    public static class RestoreRemindConf implements Serializable {
        @Schema(description = "启用恢复提醒")
        private Boolean enableRestoreRemindConf = true;

        @Schema(description = "正常次数，启用则必填")
        private Integer normalCount;

        @Schema(description = "恢复提醒内容，启用则必填", required = true)
        @Length(max = 500, message = "恢复提醒内容长度不大于500")
        private String restoreRemindMessage;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "用户信息")
    @ToString
    public static class UserInfo implements Serializable {
        @Schema(description = "用户Id", required = true)
        private Integer userId;

        @Schema(description = "用户名", required = true)
        private String username;

        @Schema(description = "用户姓名", required = true)
        private String fullName;

        @Schema(description = "部门", required = true)
        private String deptName;
    }
}
