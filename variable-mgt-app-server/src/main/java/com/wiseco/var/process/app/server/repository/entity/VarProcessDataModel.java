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
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 变量空间-数据模型
 * </p>
 *
 * @author wangxianli
 * @since 2022-08-25
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("var_process_data_model")
public class VarProcessDataModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 对象名称
     */
    @TableField("object_name")
    private String objectName;

    /**
     * 对象中文名
     */
    @TableField("object_label")
    private String objectLabel;

    /**
     * 对象来源类型
     */
    @TableField("object_source_type")
    private VarProcessDataModelSourceType objectSourceType;

    /**
     * 来源表/外部服务
     */
    @TableField("object_source_info")
    private String objectSourceInfo;

    /**
     * 数据模型jsonschema
     */
    @TableField("content")
    private String content;

    /**
     * 原始数据数量
     */
    @TableField("source_property_num")
    private Integer sourcePropertyNum;

    /**
     * 扩展数据数量
     */
    @TableField("extend_property_num")
    private Integer extendPropertyNum;

    /**
     * 版本号,从1开始
     */
    @TableField("version")
    private Integer version;

    /**
     * 创建部门
     */
    @TableField("created_dept")
    private String createdDept;

    /**
     * 创建部门
     */
    @TableField("created_dept_name")
    private String createdDeptName;

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
