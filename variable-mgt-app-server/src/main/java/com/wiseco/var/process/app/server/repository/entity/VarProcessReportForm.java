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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.ReportFromStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


/**
 * 监控预警的报表
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_report_form")
public class VarProcessReportForm extends BaseEntity {

    private static final long serialVersionUID = -4711315394992853006L;

    /**
     * 报表名称(不可以重复)
     */
    @TableField("report_form_name")
    private String name;

    /**
     * 报表分类: SERVICE(服务报表)，SINGLE_VARIABLE_ANALYZE(单指标分析报表), VARIABLE_COMPARE_ANALYZE(指标对比分析报表)
     */
    @TableField("report_form_category")
    private ReportFormCategoryEnum category;

    /**
     * 时间范围的开始时间
     */
    @TableField("start_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 时间范围的结束时间
     */
    @TableField("end_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 监控对象
     */
    @TableField("monitor_object")
    private String monitorObject;

    /**
     * 变量清单(只有选择了单指标分析和指标对比分析时, 才允许赋值)
     */
    @TableField("manifests")
    private String manifests;

    /**
     * 监控指标
     */
    @TableField("monitor_indicator")
    private String monitorIndicator;

    /**
     * 展示维度(时间范围、监控对象、变量清单)
     */
    @TableField("display_dimension")
    private String displayDimension;

    /**
     * 报表类型: LINE_CHART(折线图)，AREA_CHART(面积图)，HISTOGRAM(柱状图)，TOP_CHART(TOP图)，RING_CHART(环形图)，TABLE(表格)
     */
    @TableField("report_form_type")
    private ReportFormTypeEnum type;

    /**
     * 报表排序
     */
    @TableField("report_form_order")
    private Integer reportFormOrder;

    /**
     * 报表状态, UP——启用, DOWN——停用, EDIT——编辑中
     */
    @TableField("state")
    private ReportFromStateEnum state;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 创建部门的code
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 创建用户
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * 更新用户
     */
    @TableField("updated_user")
    private String updatedUser;
}
