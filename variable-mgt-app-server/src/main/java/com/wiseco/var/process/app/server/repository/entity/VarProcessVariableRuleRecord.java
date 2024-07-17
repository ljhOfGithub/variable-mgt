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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_variable_rule_record")
public class VarProcessVariableRuleRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 变量模版id
     */
    @TableField("function_id")
    private Long functionId;

    /**
     * 生成变量id
     */
    @TableField("variable_rule_id")
    private Long variableRuleId;

    /**
     * 方案名
     */
    @TableField("plan_name")
    private String planName;

    /**
     * 变量名称
     */
    @TableField("name")
    private String name;

    /**
     * 变量编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 变量分类
     */
    @TableField("variable_type")
    private Long variableType;
    /**
     * 数据类型：string、int、double、date、datetime、boolean
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 参数内容
     */
    @TableField("param_json")
    private String paramJson;

    /**
     * 参数内容
     */
    @TableField("del_flag")
    private Integer delFlag;

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
