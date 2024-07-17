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
 * 变量测试数据集
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_test")
public class VarProcessTest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 序号，按identifier统计
     */
    @TableField("seq_no")
    private Integer seqNo;

    /**
     * 测试类型：1-变量，2-公共函数，3-数据预处理
     */
    @TableField("test_type")
    private Integer testType;

    /**
     * 变量空间ID
     */
    @TableField("var_process_space_id")
    private Long varProcessSpaceId;

    /**
     * 首次添加时的变量ID
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 测试集名称
     */
    @TableField("name")
    private String name;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 来源：在线自动生成，在线输入，文件导入
     */
    @TableField("source")
    private String source;

    /**
     * 测试数据记录数
     */
    @TableField("data_count")
    private Integer dataCount;

    /**
     * 预期结果表头字段
     */
    @TableField("table_header_field")
    private String tableHeaderField;

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
