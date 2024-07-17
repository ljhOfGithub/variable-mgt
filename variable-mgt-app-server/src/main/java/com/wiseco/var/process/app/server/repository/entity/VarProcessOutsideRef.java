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
 * 变量-外部服务引入对象表
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
@TableName("var_process_outside_ref")
public class VarProcessOutsideRef extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 外部服务ID
     */
    @TableField("outside_service_id")
    private Long outsideServiceId;

    /**
     * 数据模型ID
     */
    @TableField("data_model_id")
    private Long dataModelId;

    /**
     * 接收对象名称
     */
    @TableField("name")
    private String name;

    /**
     * 接收对象中文名称
     */
    @TableField("name_cn")
    private String nameCn;

    /**
     * 是否使用外数根对象
     */
    @TableField("use_root_object_flag")
    private Integer isUseRootObject;

    /**
     * 外部数据入参结构
     */
    @TableField("input_parameter_bindings")
    private String inputParameterBindings;

    /**
     * 外部数据出参结构
     */
    @TableField("output_parameter_bindings")
    private String outputParameterBindings;

    /**
     * 创建用户
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * 编辑用户
     */
    @TableField("updated_user")
    private String updatedUser;

    /**
     * 外部服务CODE
     */
    @TableField("outside_service_code")
    private String outsideServiceCode;

    /**
     * 外部服务NAME
     */
    @TableField("outside_service_name")
    private String outsideServiceName;


}
