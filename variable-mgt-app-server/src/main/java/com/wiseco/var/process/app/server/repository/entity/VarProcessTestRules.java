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
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * <p>
 * 变量-在线生成测试数据规则
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_test_rules")
public class VarProcessTestRules extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 变量全路径 input.application.age
     */
    @TableField("var_path")
    private String varPath;

    /**
     * 变量类型：int、double、string、boolean、date、datetime
     */
    @TableField("var_type")
    private String varType;

    /**
     * 生成方式
     */
    @TableField("generate_mode")
    private String generateMode;

    /**
     * 生成规则
     */
    @TableField("generate_rule")
    private String generateRule;

    /**
     * 生成规则描述
     */
    @TableField("generate_rule_desc")
    private String generateRuleDesc;

    /**
     * 规则公式
     */
    @TableField("generate_rule_formula")
    private String generateRuleFormula;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Timestamp updatedTime;

}
