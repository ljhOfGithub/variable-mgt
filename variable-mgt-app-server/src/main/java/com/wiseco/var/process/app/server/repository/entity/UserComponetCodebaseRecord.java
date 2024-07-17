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
 * 代码块配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_componet_codebase_record")
public class UserComponetCodebaseRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 保存来源
     * 1-策略保存，2-变量加工保存
     */
    @TableField("source_type")
    private Integer sourceType;
    /**
     * 代码块中文名
     */
    @TableField("code_block_name")
    private String codeBlockName;
    /**
     * 代码块内容
     */
    @TableField("code_block_content")
    private String codeBlockContent;
    /**
     * 代码块描述
     */
    @TableField("code_block_describe")
    private String codeBlockDescribe;
    /**
     * 使用次数
     */
    @TableField("code_block_use_times")
    private Integer codeBlockUseTimes;
    /**
     * 删除状态
     */
    @TableField("delete_flag")
    private Integer deleteFlag;
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
