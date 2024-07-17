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
 * 系统参数表
 * </p>
 *
 * @author liaody
 * @since 2022-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_param")
public class SysParam extends BaseEntity {

    private static final long serialVersionUID = -375564342125244459L;
    /**
     * 参数名
     */
    @TableField("param_name")
    private String paramName;

    /**
     * 参数中文名
     */
    @TableField("param_name_cn")
    private String paramNameCn;

    /**
     * 数据类型：int、double、string、boolean、date、datetime
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 参数类型：1内置参数 2自定义参数
     */
    @TableField("param_type")
    private Integer paramType;

    /**
     * 内置参数类型：0普通参数 1环境变量
     */
    @TableField("in_param_type")
    private Integer inParamType;

    /**
     * 参数值
     */
    @TableField("param_value")
    private String paramValue;

    /**
     * 自定义系统参数使用标识 0:内置类型不显示 1:未使用 2:已使用
     */
    @TableField("used_flag")
    private Integer usedFlag;

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
