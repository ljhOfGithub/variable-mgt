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
import lombok.experimental.SuperBuilder;

/**
 * 变量服务版本表
 */
@EqualsAndHashCode(callSuper = true)
@TableName("var_process_service_version")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VarProcessServiceVersion extends BaseEntity {

    /**
     * 服务id
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 版本
     */
    @TableField("service_version")
    private Integer serviceVersion;

    /**
     * 创建部门code
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 状态: EDITING-编辑中；PENDING_REVIEW-待审核；ENABLED-启用；DISABLED-停用；REJECTED-审核拒绝
     */
    @TableField("state")
    private VarProcessServiceStateEnum state;

    /**
     * 流水号
     */
    @TableField("serial_no")
    private String serialNo;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 描述
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
