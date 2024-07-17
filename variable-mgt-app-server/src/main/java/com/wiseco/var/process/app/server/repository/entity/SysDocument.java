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
 * 系统帮助文档表
 * </p>
 *
 * @author wangxianli
 * @since 2022-03-13
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_document")
public class SysDocument extends BaseEntity {
    private static final long serialVersionUID = -3671288302211383679L;
    /**
     * 排序：从小到大
     */
    @TableField("cat_id")
    private Long catId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 排序：从小到大
     */
    @TableField("sort_code")
    private Integer sortCode;

    /**
     * 是否推荐至主页 0-否 1-是
     */
    @TableField("recommend_flag")
    private Integer recommendFlag;

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
