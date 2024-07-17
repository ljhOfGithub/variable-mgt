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
import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

/**
 * <p>
 * 变量清单使用的变量表
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
@TableName("var_process_manifest_variable")
public class VarProcessManifestVariable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间 ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 变量清单 ID
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * 变量 ID
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 是否输出, 对应前端勾选框
     * 0: 未勾选 1: 勾选
     */
    @TableField("output_flag")
    private Integer outputFlag;

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
     * 创建时间
     */
    @TableField("created_time")
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Timestamp updatedTime;

    /**
     * 是否索引字段
     */
    @TableField("is_index")
    private Boolean isIndex;

    /**
     * 列角色
     */
    @TableField("col_role")
    private ColRoleEnum colRole;

}
