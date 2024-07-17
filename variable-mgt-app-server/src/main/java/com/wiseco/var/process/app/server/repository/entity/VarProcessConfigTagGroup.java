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
 * 变量标签组配置
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("var_process_config_tag_group")
public class VarProcessConfigTagGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 标签组名称
     */
    @TableField("group_name")
    private String groupName;

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
     * 顺序数字
     */
    @TableField("order_no")
    private Integer orderNo;

    /**
     * 部门编码
     */
    @TableField("dept_code")
    private String deptCode;
}
