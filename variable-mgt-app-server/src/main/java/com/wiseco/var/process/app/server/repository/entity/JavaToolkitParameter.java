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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * java工具类-parameter表
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("java_toolkit_parameter")
public class JavaToolkitParameter extends BaseEntity {

    /**
     * parameter编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * method编号
     */
    @TableField("method_identifier")
    private String methodIdentifier;

    /**
     * 参数名
     */
    @TableField("name")
    private String name;

    /**
     * java类型
     */
    @TableField("java_type")
    private String javaType;

    /**
     * wrl类型
     */
    @TableField("wrl_type")
    private String wrlType;

    /**
     * 参数索引
     */
    @TableField("idx")
    private Integer idx;

    /**
     * 是否数组：0=否，1=是
     */
    @TableField("is_array")
    private Integer isArray;

    /**
     * 删除标识 0:已删除 1:可用
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
