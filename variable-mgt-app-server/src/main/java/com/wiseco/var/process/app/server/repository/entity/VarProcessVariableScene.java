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
import com.wiseco.var.process.app.server.enums.StreamProcessCalFunctionEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessTemplateEnum;
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
@TableName("var_process_variable_scene")
public class VarProcessVariableScene extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量ID
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 场景ID
     */
    @TableField("scene_id")
    private Long sceneId;

    /**
     * 变量模板
     */
    @TableField("process_template")
    private StreamProcessTemplateEnum processTemplate;

    /**
     * 计算函数
     */
    @TableField("calculate_function")
    private StreamProcessCalFunctionEnum calculateFunction;

    /**
     * 事件id
     */
    @TableField("event_id")
    private Long eventId;

    /**
     * 匹配维度
     */
    @TableField("match_dimension")
    private String matchDimension;

    /**
     * 统计对象
     */
    @TableField("calculate_var")
    private String calculateVar;

    /**
     * 统计周期
     */
    @TableField("calculate_period")
    private String calculatePeriod;

    /**
     * 是否包含本笔 默认false
     */
    @TableField("current_included")
    private Boolean currentIncluded;

    /**
     * 过滤条件
     */
    @TableField("filter_condition_info")
    private String filterConditionInfo;

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
