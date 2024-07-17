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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description: 睿信查询条件、结果表头设置
 * @author: liusiyu
 * @DateTime: 2024-02-27 10:43:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("var_process_log_condition_setting")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessLogConditionSetting extends BaseEntity {

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 变量名
     */
    @TableField("var_name")
    private String varName;

    /**
     * 变量中文名
     */
    @TableField("var_name_cn")
    private String varNameCn;

    /**
     * 变量所属，0查询条件，1结果表头，2都存在
     */
    @TableField("var_type")
    private Integer varType;

    /**
     * 变量数据类型
     */
    @TableField("var_data_type")
    private String varDataType;

    /**
     * 查询条件列是否展示，1是，0否
     */
    @TableField("query_display")
    private Integer queryDisplay;

    /**
     * 查询条件列排序权重，越小越靠前
     * FieldStrategy.IGNORED : 忽略判断，设值为 null 就更新成 null
     */
    @TableField(value = "query_weight", updateStrategy = FieldStrategy.IGNORED)
    private Integer queryWeight;

    /**
     * 结果表头列是否展示，1是，0否
     */
    @TableField("column_display")
    private Integer columnDisplay;

    /**
     * 结果表头列排序权重，越小越靠前
     */
    @TableField(value = "column_weight", updateStrategy = FieldStrategy.IGNORED)
    private Integer columnWeight;

    /**
     * 是否锁定，1是，0否
     */
    @TableField("is_lock")
    private Integer isLock;

    /**
     * 是否锁定在列头，1是，0否
     */
    @TableField("is_lock_head")
    private Integer isLockHead;

    /**
     * 是否是内置参数
     */
    @TableField("is_built_in")
    private Integer isBuiltIn;

    /**
     * 清单id
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * 配置类型，0：结果查询，1：数据查看
     */
    @TableField("setting_type")
    private Integer settingType;

    /**
     * 是否可以取消选中，0：可以取消选中，1：不可取消选中
     */
    @TableField("always_selected")
    private Integer alwaysSelected;

}
