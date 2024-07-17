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
 * java工具类-method表
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("java_toolkit_method")
public class JavaToolkitMethod extends BaseEntity {

    /**
     * method编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * class编号
     */
    @TableField("class_identifier")
    private String classIdentifier;

    /**
     * 方法名
     */
    @TableField("name")
    private String name;

    /**
     * 显示名
     */
    @TableField("label")
    private String label;

    /**
     * 特征符
     */
    @TableField("characters")
    private String characters;

    /**
     * 返回值java类型
     */
    @TableField("return_value_java_type")
    private String returnValueJavaType;

    /**
     * 返回值wrl类型
     */
    @TableField("return_value_wrl_type")
    private String returnValueWrlType;

    /**
     * 方法返回值是否数组：0=否，1=是
     */
    @TableField("return_value_is_array")
    private Integer returnValueIsArray;

    /**
     * 类名称
     */
    @TableField("class_canonical_name")
    private String classCanonicalName;

    /**
     * 修饰符
     */
    @TableField("modifier")
    private Integer modifier;

    /**
     * 模板
     */
    @TableField("template")
    private String template;

    /**
     * 编译后的模板
     */
    @TableField("compile_template")
    private String compileTemplate;

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
