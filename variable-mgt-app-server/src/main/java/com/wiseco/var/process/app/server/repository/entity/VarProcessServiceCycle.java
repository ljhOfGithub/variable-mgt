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
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 实时服务-生命周期表
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@TableName(value = "var_process_service_cycle")
public class VarProcessServiceCycle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 服务ID
     */
    @TableField(value = "service_id")
    private Long serviceId;

    /**
     * 操作: 0-新增，1-提交，2-审核通过，3-审核拒绝，4-退回编辑，5-停用，6-(重新)启用，7-删除
     */
    @TableField(value = "operation")
    private Short operation;

    /**
     * 状态: EDITING-编辑中；PENDING_REVIEW-待审核；ENABLED-启用；DISABLED-停用；REJECTED-审核拒绝
     */
    @TableField("status")
    private VarProcessServiceStateEnum status;

    /**
     * 备注
     */
    @TableField("description")
    private String description;

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
