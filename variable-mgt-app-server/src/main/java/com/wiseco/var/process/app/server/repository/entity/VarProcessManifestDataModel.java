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
 * 变量清单-数据模型映射表
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
@TableName("var_process_manifest_data_model")
public class VarProcessManifestDataModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 变量清单ID
     */
    @TableField("manifest_id")
    private Long manifestId;

    /**
     * 数据模型对象名称
     */
    @TableField("object_name")
    private String objectName;

    /**
     * 数据模型对象版本
     */
    @TableField("object_version")
    private Integer objectVersion;

    /**
     * 数据来源：1-外部传入，2-内部数据，3-外部服务，4-内部逻辑计算
     */
    @TableField("source_type")
    private Integer sourceType;

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
     * 查询条件映射
     */
    @TableField("model_query_condition")
    private String modelQueryCondition;

}
