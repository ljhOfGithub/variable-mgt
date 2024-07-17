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
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

/**
 * <p>
 * 变量清单表
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
@TableName("var_process_manifest")
public class VarProcessManifest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间 ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 唯一标识符
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 变量清单名称
     */
    @TableField("var_manifest_name")
    private String varManifestName;

    /**
     * 变量清单类别ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 实时服务 ID
     */
    @TableField("service_id")
    private Long serviceId;

    /**
     * 接口版本
     */
    @TableField("version")
    private Integer version;

    /**
     * 状态 0: 编辑中, 1: 测试中, 2: 待审核, 3: 审核拒绝, 4: 启用中, 5: 启用, 6: 停用, 7: 发布失败
     */
    @TableField("state")
    private VarProcessManifestStateEnum state;

    /**
     * 流水号
     */
    @TableField("serial_no")
    private String serialNo;

    /**
     * 版本说明
     */
    @TableField("description")
    private String description;

    /**
     * 选择发布的变量请求结构 JSON Schema 快照
     */
    @TableField("schema_snapshot")
    private String schemaSnapshot;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 被复制的变量清单 ID (仅限新清单版本从已有版本复制时填写)
     */
    @TableField("parent_manifest_id")
    private Long parentManifestId;

    /**
     * 流程内容
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
     * 部门Id
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 部门code
     */
    @TableField("dept_code")
    private String deptCode;
}
