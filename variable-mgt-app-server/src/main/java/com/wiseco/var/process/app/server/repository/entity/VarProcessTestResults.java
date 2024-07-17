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
 * 变量测试执行结果
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_test_results")
public class VarProcessTestResults extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 测试数据集ID，test_variable表ID
     */
    @TableField("test_id")
    private Long testId;

    /**
     * 测试类型：1-变量，2-公共函数，3-清单
     */
    @TableField("test_type")
    private Integer testType;

    /**
     * 测试执行的变量ID
     */
    @TableField("variable_id")
    private Long variableId;

    /**
     * 测试执行版本
     */
    @TableField("change_num")
    private String changeNum;

    /**
     * 执行测试时间
     */
    @TableField("test_time")
    private Timestamp testTime;

    /**
     * 测试总数
     */
    @TableField("data_count")
    private Integer dataCount;

    /**
     * 测试执行正常数
     */
    @TableField("execute_normal_count")
    private Integer executeNormalCount;

    /**
     * 测试执行异常数
     */
    @TableField("execute_exception_count")
    private Integer executeExceptionCount;

    /**
     * 预期结果一致
     */
    @TableField("execute_resulteq_count")
    private Integer executeResulteqCount;

    /**
     * 预期结果不一致
     */
    @TableField("execute_resultneq_count")
    private Integer executeResultneqCount;

    /**
     * 测试成功率
     */
    @TableField("success_rate")
    private String successRate;

    /**
     * 测试执行耗时
     */
    @TableField("execute_time")
    private Long executeTime;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

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
