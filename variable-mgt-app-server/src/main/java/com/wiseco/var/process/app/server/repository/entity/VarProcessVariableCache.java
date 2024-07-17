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

import java.sql.Timestamp;

/**
 * <p>
 * 变量-临时保存表
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
@TableName("var_process_variable_cache")
public class VarProcessVariableCache extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 当前会话id
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 组件id
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 当前编辑内容
     */
    @TableField("content")
    private String content;

    @TableField("created_user")
    private String createdUser;

    @TableField("created_time")
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Timestamp updatedTime;

}
