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
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 实时服务表
 *
 * @author Zhaoxiong Chen
 * @since 2022-08-24
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_service")
public class VarProcessService extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 服务编码
     */
    @TableField("code")
    private String code;

    /**
     * 服务名称
     */
    @TableField("name")
    private String name;

    /**
     * 类型, 1-实时, 2-批量
     */
    @TableField("type")
    private Integer type;

    /**
     * 服务地址
     */
    @TableField("url")
    private String url;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
     * 状态,默认为EDITING
     */
    @TableField("state")
    private VarProcessServiceStateEnum state;

    /**
     * 类型ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 流水号
     */
    @TableField("serial_no")
    private String serialNo;

    /**
     * 手动选择 的入参对象 Json快照
     */
    @TableField("schema_snapshot")
    private String schemaSnapshot;

    /**
     * 创建部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 是否启用了trace,true-启用,false-未启用
     */
    @TableField("enable_trace")
    private Boolean enableTrace;
}
