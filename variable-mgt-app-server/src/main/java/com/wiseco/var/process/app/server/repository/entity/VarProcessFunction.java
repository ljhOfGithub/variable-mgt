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
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 公共函数表
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
@TableName("var_process_function")
public class VarProcessFunction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 父级函数ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 函数名
     */
    @TableField("name")
    private String name;

    /**
     * 函数类型:变量模板，公共方法
     */
    @TableField("function_type")
    private FunctionTypeEnum functionType;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态 编辑中，启用，停用
     */
    @TableField("status")
    private FlowStatusEnum status;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 自定义函数返回数据类型：int、double、string、boolean、date、datetime
     */
    @TableField("function_data_type")
    private String functionDataType;

    /**
     * 预处理对象
     */
    @TableField("prep_object_name")
    private String prepObjectName;

    /**
     * 检入后生产模板内容
     */
    @TableField("function_template_content")
    private String functionTemplateContent;

    /**
     * 函数内容
     */
    @TableField("content")
    private String content;

    /**
     * 创建部门
     */
    @TableField("created_dept")
    private String createdDept;

    /**
     * 创建部门code
     */
    @TableField("created_dept_code")
    private String createdDeptCode;

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
     * 模板分类
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 处理方式
     */
    @TableField("handle_type")
    private FunctionHandleTypeEnum handleType;

    /**
     * 保存后根据词条内容生成该json串
     */
    @TableField("function_entry_content")
    private String functionEntryContent;

    /**
     * 是否生成变量
     */
    @TableField("variable_created")
    private Integer variableCreated;

}
