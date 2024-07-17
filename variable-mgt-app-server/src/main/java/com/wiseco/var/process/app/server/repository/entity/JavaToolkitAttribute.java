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
 * java工具类-attribute表
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("java_toolkit_attribute")
public class JavaToolkitAttribute extends BaseEntity {

    /**
     * attribute编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * class编号
     */
    @TableField("class_identifier")
    private String classIdentifier;

    /**
     * 属性名
     */
    @TableField("name")
    private String name;

    /**
     * 显示名
     */
    @TableField("label")
    private String label;

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
     * 属性类型是否数组：0=否，1=是
     */
    @TableField("type_is_array")
    private Integer typeIsArray;

    /**
     * 修饰符
     */
    @TableField("modifier")
    private Integer modifier;

    /**
     * 访问：read/write，readonly
     */
    @TableField("access")
    private String access;

    /**
     * 属性来源：1=字段，2=方法
     */
    @TableField("source_type")
    private Integer sourceType;

    /**
     * 状态：0=停用，1=启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 导入状态：0=未导入，1=已导入
     */
    @TableField("import_status")
    private Integer importStatus;

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
