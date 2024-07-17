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
 * <p>
 * 组件-变量引擎编译引用关系表
 * </p>
 *
 * @author tanxiaohuan
 * @since 2024-01-18
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_compile_var")
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessCompileVar extends BaseEntity {

    private static final long serialVersionUID = -8406491797286983496L;
    /**
     * 组件ID，指标id/公共函数id/清单id
     */
    @TableField("invok_id")
    private Long invokId;

    /**
     * 值见枚举类 VarTypeEnum
     * 组件id对应类型：MAINFLOW-服务决策流程,  VAR-衍生变量,  FUNCTION-公共函数, OUTSIDE_SERVICE-外数
     */
    @TableField("invok_type")
    private String invokType;

    /**
     * 引用的变量或组件序号
     */
    @TableField("serial_no")
    private int serialNo;

    /**
     * 值见枚举类 EngineComponentInvokeMetaTypeEnum
     * 类型 值为 COMPONENT:组件、VAR:变量、TYPE:引用类型
     */
    @TableField("call_type")
    private String callType;

    /**
     * 值见枚举类 VarTypeEnum
     * MAINFLOW-服务决策流程,  VAR-衍生变量,  FUNCTION-公共函数, OUTSIDE_SERVICE-外数
     */
    @TableField("call_component_type")
    private String callComponentType;

    /**
     * 当类型是变量时值为变量路径 var_path
     * 当类型是组件时值为组件id
     */
    @TableField("value")
    private String value;


    /**
     * 引擎返回的当前组件中变量读写操作记录 r 读 w 写
     * 没有做变量的穿透分析
     */
    @TableField("action_history")
    private String actionHistory;
    /**
     * 变量名称 var_name
     */
    @TableField("var_name")
    private String varName;

    /**
     * 变量类型
     */
    @TableField("var_type")
    private String varType;

    /**
     * 是否是array类型 0:不是array类型 1:是array类型
     */
    @TableField("is_array")
    private Integer isArray;

    /**
     * 是否是扩展数据 0:不是  1:是
     */
    @TableField("is_extend")
    private Integer isExtend;

    /**
     * 参数类型
     */
    @TableField("parameter_type")
    private String parameterType;

    /**
     * 参数是否是array类型
     */
    @TableField("is_parameter_array")
    private Integer isParameterArray;

    /**
     * 参数中文名称
     */
    @TableField("parameter_label")
    private String parameterLabel;


    
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
