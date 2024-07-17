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
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 变量生命周期表
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
@TableName("var_process_variable_lifecycle")
public class VarProcessVariableLifecycle extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量ID
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 状态 1:编辑中 , 2:上架,  3:下架
     */
    @TableField("status")
    private VariableStatusEnum status;

    /**
     * 状态 1:新建 , 2:上架,  3:下架
     */
    @TableField("action_type")
    private Integer actionType;

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
