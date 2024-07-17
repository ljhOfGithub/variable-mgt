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
 * 变量清单-引用数据模型变量关系表
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-14
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_manifest_var")
public class VarProcessManifestVar extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量清单ID
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * 变量全路径 input.application.age
     */
    @TableField("var_path")
    private String varPath;

    /**
     * 变量中文名称
     */
    @TableField("var_name")
    private String varName;

    /**
     * 变量类型：int、double、string、boolean、date、datetime
     */
    @TableField("var_type")
    private String varType;

    /**
     * 是否是本组件使用变量 0:非本组件 1:本组件
     */
    @TableField("is_self")
    private Integer isSelf;

    /**
     * 是否是array类型 0:不是array类型 1:是array类型
     */
    @TableField("is_array")
    private Integer isArray;

    /**
     * 是否扩展数据：0-否，1-是
     */
    @TableField("is_extend")
    private Integer isExtend;

    /**
     * 参数或本地变量中文名
     */
    @TableField("parameter_label")
    private String parameterLabel;

    /**
     * 参数类型
     */
    @TableField("parameter_type")
    private String parameterType;

    /**
     * 参数是否是array类型 0:不是array类型 1:是array类型
     */
    @TableField("is_parameter_array")
    private Integer isParameterArray;

    /**
     * 测试标识  0:不需要测试数据 1:输入测试数据 2:输出测试数据 3:输入和输出
     */
    @TableField("test_flag")
    private Integer testFlag;

    /**
     * 读写操作记录 r 读 w 写
     */
    @TableField("action_history")
    private String actionHistory;

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
