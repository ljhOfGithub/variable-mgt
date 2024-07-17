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
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 批量回溯
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_batch_backtracking")
public class VarProcessBatchBacktracking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * Column: name
     * Type: VARCHAR(255)
     * Remark: 任务名称
     */
    @TableField("name")
    private String name;

    /**
     * Column: manifest_id
     * Type: INT
     * Remark: 变量清单id
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * Column: variable_size
     * Type: INT
     * Remark: 变量数
     */
    @TableField("variable_size")
    private Integer variableSize;

    /**
     * Column: trigger_type
     * Type: VARCHAR(255)
     * Remark: 触发方式
     */
    @TableField("trigger_type")
    private BatchBacktrackingTriggerTypeEnum triggerType;

    /**
     * Column: enable_trace
     * Type: Boolean
     * Remark: 开启trace
     */
    @Schema(description = "开启trace")
    private Boolean enableTrace;

    /**
     * Column: description
     * Type: VARCHAR(255)
     * Remark: 描述
     */
    @TableField("description")
    private String description;

    /**
     * Column: serial_no
     * Type: VARCHAR(255)
     * Remark: 主体唯一标识
     */
    @TableField("serial_no")
    private String serialNo;

    /**
     * Column: status
     * Type: VARCHAR(255)
     * Remark: 当前状态
     */
    @TableField("status")
    private FlowStatusEnum status;

    /**
     * Column: delete_flag
     * Type: TINYINT(3)
     * Default value: 1
     * Remark: 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * Column: created_user
     * Type: VARCHAR(24)
     * Remark: 创建用户
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * Column: updated_user
     * Type: VARCHAR(24)
     * Remark: 更新用户
     */
    @TableField("updated_user")
    private String updatedUser;

    /**
     * Column: dept_code
     * Type: VARCHAR(255)
     * Remark: 创建部门
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * Column: dept_name
     * Type: VARCHAR(255)
     * Remark: 创建部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * Column: output_info
     * Type: JSON(0)
     * Remark: 输出信息
     */
    @TableField("output_info")
    private String outputInfo;

    /**
     * Column: task_info
     * Type: JSON(0)
     * Remark: 任务信息
     */
    @TableField("task_info")
    private String taskInfo;

    /**
     * Column: result_table
     * Remark: 结果表名
     */
    @TableField("result_table")
    private String resultTable;

    /**
     * Column: result_table_desc
     * Remark: 结果表中文名
     */
    @TableField("result_table_desc")
    private String resultTableDesc;

    /**
     * Column: data_get_type_info
     * Remark: 设置取值方式
     */
    @TableField("data_get_type_info")
    private String dataGetTypeInfo;

    /**
     * Column: output_Type
     * Remark: 输出方式
     */
    @TableField("output_Type")
    private String outputType;
}
