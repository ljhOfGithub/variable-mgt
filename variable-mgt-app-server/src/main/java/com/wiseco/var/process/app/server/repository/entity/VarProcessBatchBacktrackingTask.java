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

import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * Table: var_process_batch_backtracking_task
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_batch_backtracking_task")
public class VarProcessBatchBacktrackingTask extends BaseEntity {

    /**
     * Column: backtracking_id
     * Type: INT
     * Remark: 函数ID
     */
    private Long                       backtrackingId;

    /**
     * Column: status
     * Type: VARCHAR(255)
     * Default value: 1
     * Remark: 状态
     */
    private BacktrackingTaskStatusEnum status;

    /**
     * Column: code
     * Type: VARCHAR(255)
     * Remark: 编号
     */
    private String                     code;

    /**
     * Column: start_time
     * Type: DATETIME
     * Default value: CURRENT_TIMESTAMP
     * Remark: 开始时间
     */
    private Date                       startTime;

    /**
     * Column: end_time
     * Type: DATETIME
     * Default value: CURRENT_TIMESTAMP
     * Remark: 结束时间
     */
    private Date                       endTime;

    /**
     * Column: completion
     * Type: VARCHAR(20)
     * Remark: 完成情况
     */
    private String                     completion;

    /**
     * Column: success
     * Type: VARCHAR(20)
     * Remark: 成功率
     */
    private String                     success;

    /**
     * Column: maximum_response_time
     * Type: INT
     * Remark: 最大相应时间
     */
    private Integer                    maximumResponseTime;

    /**
     * Column: minimum_response_time
     * Type: INT
     * Remark: 最小相应时间
     */
    private Integer                    minimumResponseTime;

    /**
     * Column: average_response_time
     * Type: INT
     * Remark: 平均相应时间
     */
    private Float                    averageResponseTime;

    /**
     * Column: created_time
     * Type: DATETIME
     * Default value: CURRENT_TIMESTAMP
     * Remark: 创建时间
     */
    private Date                       createdTime;

    /**
     * Column: updated_time
     * Type: DATETIME
     * Default value: CURRENT_TIMESTAMP
     * Remark: 更新时间
     */
    private Date                       updatedTime;

    /**
     * Column: created_user
     * Type: VARCHAR(24)
     * Remark: 创建用户
     */
    private String                     createdUser;

    /**
     * 错误信息
     */
    private String                     errorMessage;

    /**
     * 取值文件全路径（只有拼接YYYYMMdd时才插入）
     */
    private String                     dataGetFileFullPath;

    /**
     * 结果文件路径
     */
    private String                     resultFileInfo;
}
