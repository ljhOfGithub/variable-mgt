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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 变量清单-生命周期表
 *
 * @author Zhaoxiong Chen
 * @since 2022-08-24
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_manifest_cycle")
public class VarProcessManifestCycle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 接口ID
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * 操作: 0: 新建 1: 提交测试, 2: 申请发布，3：审核通过，4：审核拒绝，5：退回编辑，6：停用，7：重新启用，8：删除
     */
    @TableField("operation")
    private Integer operation;

    /**
     * 状态 0: 编辑中, 1: 测试中, 2: 待审核, 3: 审核拒绝, 4: 启用，5：停用，6：发布失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("memo")
    private String memo;

    /**
     * 操作人 (姓名)
     */
    @TableField("operator_full_name")
    private String operatorFullName;

    /**
     * 操作人 (用户名)
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * 更新用户
     */
    @TableField("updated_user")
    private String updatedUser;

}
