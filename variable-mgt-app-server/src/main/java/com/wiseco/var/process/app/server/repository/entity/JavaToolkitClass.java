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
import org.apache.ibatis.type.BlobTypeHandler;

/**
 * <p>
 * java工具类-class类表
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("java_toolkit_class")
public class JavaToolkitClass extends BaseEntity {

    /**
     * class编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * jar包编号
     */
    @TableField("jar_identifier")
    private String jarIdentifier;

    /**
     * 类名称
     */
    @TableField("name")
    private String name;

    /**
     * 类名称显示名
     */
    @TableField("label")
    private String label;
    /**
     * jar文件
     */
    @TableField(value = "file", typeHandler = BlobTypeHandler.class)
    private byte[] file;
    /**
     * 规范的类名称
     */
    @TableField("canonical_name")
    private String canonicalName;

    /**
     * class类型：1=class，2=abstract class，3=interface，4=enum
     */
    @TableField("class_type")
    private Integer classType;

    /**
     * class业务类型：1=class有属性也有方法，2=class有属性无方法，3=class无属性有方法，4=class无属性无方法
     */
    @TableField("class_biz_type")
    private Integer classBizType;

    /**
     * 修饰符
     */
    @TableField("modifier")
    private Integer modifier;

    /**
     * 归属包
     */
    @TableField("jar_name")
    private String jarName;

    /**
     * 属性的jsonschema
     */
    @TableField("attribute_json_schema")
    private String attributeJsonSchema;

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
