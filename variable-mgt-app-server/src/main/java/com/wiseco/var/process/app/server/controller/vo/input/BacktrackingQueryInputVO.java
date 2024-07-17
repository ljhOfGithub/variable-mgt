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
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量回溯查询 VO")
public class BacktrackingQueryInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    @Schema(description = "批量回溯ID")
    private Long id;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "变量清单名称")
    private String manifestName;

    @Schema(description = "变量清单")
    private Long manifestId;

    @Schema(description = "变量数")
    private Integer variableSize;

    @Schema(description = "触发方式：人工，定时")
    private BatchBacktrackingTriggerTypeEnum triggerType;

    @Schema(description = "状态： 编辑中，待审核，启用，停用，审核拒绝")
    private FlowStatusEnum status;

    @Schema(description = "创建部门")
    private String deptCode;

    @Schema(description = "创建部门名称")
    private String deptName;

    @Schema(description = "删除标识")
    private String deleteFlag;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "最后执行状态：-- 处理中，成功，失败")
    private BacktrackingTaskStatusEnum taskStatus;

    @Schema(description = "执行时间", example = "2022-06-08 12:00:00")
    private Date startTime;

    @Schema(description = "排序字段", example = "label_asc")
    private String order;
}
