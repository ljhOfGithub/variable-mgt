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
import com.wiseco.var.process.app.server.enums.ProcessTypeEnum;
import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 变量表
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
@TableName("var_process_variable")
public class VarProcessVariable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 父级变量ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 变量名
     */
    @TableField("name")
    private String name;

    /**
     * 变量中文名
     */
    @TableField("label")
    private String label;

    /**
     * 数据类型
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 变量类型ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 部门code
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 版本
     */
    @TableField("version")
    private Integer version;

    /**
     * 变量状态 1-编辑中，2-启用，3-停用,4-待审核,5-审核拒绝
     */
    @TableField("status")
    private VariableStatusEnum status;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 变量内容
     */
    @TableField("content")
    private String content;

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
     * 变量加工方式
     */
    @TableField("processing_method")
    private ProcessingMethodEnum processingMethod;

    /**
     * 实时变量具体加工方式
     */
    @TableField("process_type")
    private ProcessTypeEnum processType;

    /**
     * 变量模版id（加工方式=ENTRY时有值）
     */
    @TableField("function_id")
    private Long functionId;
}
