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

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.ManifestPublishStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_service_manifest")
@Schema(description = "服务和变量清单的关系实体类")
public class VarProcessServiceManifest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 服务ID -> 目前改为存版本id
     */
    @TableField("service_id")
    private Long              serviceId;

    /**
     * 变量清单ID
     */
    @TableField("manifest_id")
    private Long              manifestId;

    /**
     * 变量清单在当前服务中的角色
     */
    @TableField("manifest_role")
    private Short             manifestRole;

    /**
     * 空间ID
     */
    @TableField("var_process_space_id")
    private Long              varProcessSpaceId;

    /**
     * 是否立即生效
     */
    @TableField("immediate_effect")
    private Integer           immediateEffect;

    /**
     * 生效时刻
     */
    @TableField("valid_time")
    private Date              validTime;

    /**
     * 失效时刻
     */
    @TableField(value = "invalid_time", jdbcType = JdbcType.TIMESTAMP, updateStrategy = FieldStrategy.IGNORED)
    private Date invalidTime;

    /**
     * 执行总笔数
     */
    @TableField("total_execute_count")
    private Long              totalExecuteCount;

    /**
     * 当前执行笔数
     */
    @TableField("current_execute_count")
    private Long              currentExecuteCount;

    /**
     * 创建用户
     */
    @TableField("created_user")
    private String            createdUser;

    /**
     * 更新用户
     */
    @TableField("updated_user")
    private String            updatedUser;

    /**
     * 是否可用：1-可用；0-不可用
     */
    @TableField("usable")
    private Short usable;

    /**
     * 发布状态
     */
    @TableField("state")
    private ManifestPublishStateEnum manifestPublishState;
}
