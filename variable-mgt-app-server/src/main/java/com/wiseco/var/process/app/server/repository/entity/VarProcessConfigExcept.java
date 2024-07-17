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
 * 变量空间-变量异常值配置
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
@TableName("var_process_config_exception")
public class VarProcessConfigExcept extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;
    /**
     * 异常值标识符
     */
    @TableField("exception_value_code")
    private String exceptionValueCode;
    /**
     * 变量类型：int、double、string、boolean、date、datetime
     */
    @TableField("data_type")
    private String dataType;
    /**
     * 异常值:例如:NA,-9999
     */
    @TableField("exception_value")
    private String exceptionValue;
    /**
     * 类型:1:内置异常 2:自定义
     */
    @TableField("exception_type")
    private Integer exceptionType;
    /**
     * 删除状态 0:已删除 1:未删除
     */
    @TableField("delete_flag")
    private Integer deleteFlag;
    /**
     * 异常说明
     */
    @TableField("exception_explain")
    private String exceptionExplain;
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
