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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 变量测试执行结果实体类
 */
@Data
@TableName("var_process_test_variable_result")
public class VarProcessTestVariableResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 测试数据集 ID
     */
    @TableField(value = "test_id")
    private Long testId;

    /**
     * 执行结果 ID
     */
    @TableField(value = "result_id")
    private Long resultId;

    /**
     * 单条数据在测试数据集中的序列号
     */
    @TableField(value = "data_id")
    private Integer dataId;

    /**
     * 测试批号
     */
    @TableField(value = "batch_no")
    private String batchNo;

    /**
     * 组件测试请求流水号
     */
    @TableField(value = "test_serial_no")
    private String testSerialNo;

    /**
     * 执行结果状态码：0-执行异常，1-执行正常
     */
    @TableField(value = "execution_status")
    private Integer executionStatus;

    /**
     * 预期结果对比状态码
     */
    @TableField(value = "comparison_status")
    private Integer comparisonStatus;

    /**
     * 执行结果内容：输入
     */
    @TableField(value = "input_content")
    private String inputContent;

    /**
     * 执行结果内容：预期结果
     */
    @TableField(value = "expect_content")
    private String expectContent;

    /**
     * 执行结果内容：实际结果
     */
    @TableField(value = "results_content")
    private String resultsContent;

    /**
     * 执行结果内容：原始内容，包含输入、输出
     */
    @TableField(value = "original_content")
    private String originalContent;

    /**
     * 执行结果内容：对比实际结果内容
     */
    @TableField(value = "comparison_content")
    private String comparisonContent;

    /**
     * 测试执行异常内容
     */
    @TableField(value = "exception_msg")
    private String exceptionMsg;

    /**
     * Debug 信息
     */
    @TableField(value = "debug_info")
    private String debugInfo;

    /**
     * 执行耗时
     */
    @TableField(value = "execution_time")
    private Long executionTime;
}

