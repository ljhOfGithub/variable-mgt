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
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 批量回溯-流程信息
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
@TableName("var_process_batch_backtracking_lifecycle")
public class VarProcessBatchBacktrackingLifecycle extends BaseEntity {

    /**
     * Column: backtracking_id
     * Type: INT
     * Remark: 函数ID
     */
    private Long backtrackingId;

    /**
     * Column: status
     * Type: TINYINT(3)
     * Default value: 1
     * Remark: 状态 1:编辑中 , 2:上架,  3:下架
     */
    private FlowStatusEnum status;

    /**
     * Column: action_type
     * Type: TINYINT(3)
     * Default value: 1
     * Remark: 状态 1:新建 , 2:上架,  3:下架
     */
    private FlowActionTypeEnum actionType;

    /**
     * Column: description
     * Type: VARCHAR(256)
     * Remark: 描述
     */
    private String description;

    /**
     * Column: created_user
     * Type: VARCHAR(24)
     * Remark: 创建用户
     */
    private String createdUser;

    /**
     * Column: updated_user
     * Type: VARCHAR(24)
     * Remark: 更新用户
     */
    private String updatedUser;

}
