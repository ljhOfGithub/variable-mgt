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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

/**
 * <p>
 * 基础配置 - 通用参配置数
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
@TableName("var_process_param")
@Validated
public class VarProcessParameter extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    @TableField("param_name")
    @Schema(description = "参数名", required = true)
    private String paramName;

    /**
     * 参数Code
     */
    @TableField("param_code")
    private String paramCode;

    /**
     * 是否启用：0不启用 | 1启用
     */
    @TableField("is_enabled")
    @Schema(description = "是否启用", required = true, example = "1 启用 | 0 禁用")
    private Integer isEnabled;

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

}
