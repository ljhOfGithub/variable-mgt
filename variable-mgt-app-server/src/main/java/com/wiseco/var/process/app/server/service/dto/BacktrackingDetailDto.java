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

import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 批量回溯详情实体类
 * </p>
 *
 * @author wiseco
 * @since 2023-08-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量回溯详情 DTO")
public class BacktrackingDetailDto implements Serializable {
    private Long id;
    /**
     * Remark: 任务名称
     */
    private String name;

    /**
     * Remark: 变量清单id
     */
    private Long manifestId;

    /**
     * Remark: 变量清单名称
     */
    private String manifestName;

    /**
     * Remark: 变量数
     */
    private Integer variableSize;

    /**
     * Remark: 触发方式
     */
    private BatchBacktrackingTriggerTypeEnum triggerType;

    /**
     * Remark: 描述
     */
    private String description;

    /**
     * Remark: 主体唯一标识
     */
    private String serialNo;

    /**
     * Remark: 当前状态
     */
    private FlowStatusEnum status;

    /**
     * Remark: 删除标识 0:已删除 1:可用
     */
    private Byte deleteFlag;

    /**
     * Remark: 创建用户
     */
    private String createdUser;

    /**
     * Remark: 更新用户
     */
    private String updatedUser;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 更新时间
     */
    private String updatedTime;

    /**
     * Remark: 创建部门
     */
    private String deptCode;

    /**
     * Remark: 创建部门名称
     */
    private String deptName;

    /**
     * Remark: 输出类型
     */
    private String outputType;

    /**
     * Remark: 输出信息
     */
    private String outputInfo;

    /**
     * Remark: 任务信息
     */
    private String taskInfo;

    private BacktrackingTaskStatusEnum taskStatus;

    private String startTime;

    private Integer sortOrder;

    private Boolean enableTrace;
}
