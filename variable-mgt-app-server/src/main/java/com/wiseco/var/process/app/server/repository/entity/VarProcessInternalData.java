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
import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 内部数据表
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-21
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_internal_data")
public class VarProcessInternalData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 内部数据名称
     */
    @TableField("name")
    private String name;

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
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 内容：入参和返回数据
     */
    @TableField("content")
    private String content;

    /**
     * 保存至数据模型标识
     */
    @TableField("data_model_flag")
    private Integer dataModelFlag;

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
     * 数据模型ID
     */
    @TableField("data_model_id")
    private Long dataModelId;

    /**
     * 内部数据来源类型
     */
    @TableField("data_type")
    private VarProcessDataModeInsideDataType dataType;

}
