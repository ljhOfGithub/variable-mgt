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
 * <p>
 * 变量空间文档表
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-20
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("var_process_document")
public class VarProcessDocument extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 文件原名称
     */
    @TableField("name")
    private String name;

    /**
     * 预览文件名
     */
    @TableField("pre_view_name")
    private String preViewName;

    /**
     * 文档类型：manifest-变量清单，variable-变量，function-公共函数
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 后缀
     */
    @TableField("suffix")
    private String suffix;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private String fileSize;

    /**
     * OSS文件路径
     */
    @TableField("file_path")
    private String filePath;

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
