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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("var_process_authorization")
public class VarProcessAuthorization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 调用方
     */
    @TableField("caller")
    private String      caller;

    /**
     * 所属部门
     */
    @TableField("caller_dept")
    private String      callerDept;

    /**
     * 授权说明
     */
    @TableField("details")
    private String      details;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean      enabled;

    /**
     * 创建部门
     */
    @TableField("created_dept")
    private String      createdDept;

    /**
     * 授权码
     */
    @TableField("authorization_code")
    private String      authorizationCode;

    /**
     * 创建人
     */
    @TableField("created_user")
    private String      createdUser;

    /**
     * 修改人
     */
    @TableField("updated_user")
    private String      updatedUser;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer     deleteFlag;

    /**
     * 授权码上次更新时间
     */
    @TableField("auth_code_updated_time")
    private Date        authCodeUpdatedTime;
}
