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

import java.sql.Timestamp;

/**
 * <p>
 * 引擎基础函数
 * </p>
 *
 * @author liaody
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("engine_function")
public class EngineFunction extends BaseEntity {

    private static final long serialVersionUID = 5692275043789509109L;
    /**
     * 函数名称
     */
    @TableField("name")
    private String name;

    /**
     * 函数标签
     */
    @TableField("label")
    private String label;

    /**
     * 分组 math,convert
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 分组中文名称
     */
    @TableField("group_label")
    private String groupLabel;
    /**
     * 函数返回类型是否是数组，0不是，1是数组
     */
    @TableField("is_array")
    private Integer isArray;

    /**
     * 函数返回类型 number(int,double),string,date,datetime,boolean,array
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 是否需要模板 0不需要，1需要
     */
    @TableField("template_need")
    private Integer templateNeed;

    /**
     * 模板内容
     */
    @TableField("template_content")
    private String templateContent;

    /**
     * 排序：从小到大
     */
    @TableField("sort_order")
    private Integer sortOrder;

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

}
