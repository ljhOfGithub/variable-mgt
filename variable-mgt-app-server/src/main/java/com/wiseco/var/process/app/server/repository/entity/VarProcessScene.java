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
import com.wiseco.var.process.app.server.enums.SceneStateEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("var_process_scene")
public class VarProcessScene extends BaseEntity {

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 数据源
     */
    @TableField(value = "data_source")
    private String dataSource;

    /**
     * 数据模型对象名称
     */
    @TableField(value = "data_model_name")
    private String dataModelName;

    /**
     * 变量角色定义
     */
    @TableField(value = "var_roles")
    private String varRoles;

    /**
     * 逻辑删除
     */
    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    /**
     * 状态
     */
    @TableField(value = "state")
    private SceneStateEnum state;

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

    /**
     * 创建部门
     */
    @TableField("dept_code")
    private String deptCode;
}

