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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.wiseco.decision.engine.var.runtime.context.TraceLog;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 测试执行结果 DTO
 *
 * @author wangxianli
 */
@Schema(description = "测试执行结果DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestExecuteResultDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    /**
     * 数据测试明细Id
     */
    private Integer dataId;

    // 统计数据部分
    /**
     * 预期和执行结果一致 记录数
     */
    private int resultsEq;
    /**
     * 预期和执行结果不一致 记录数
     */
    private int resultsNe;
    /**
     * 执行正常 记录数
     */
    private int normal;
    /**
     * 执行异常 记录数
     */
    private int exception;

    // 执行, 对比状态 Flag 部分
    /**
     * 执行是否正常 Flag
     *
     * @see com.wiseco.decision.common.business.enums.TestExecStatusEnum
     */
    private int executionStatus;

    /**
     * 预期和执行结果比较状态 Flag
     *
     * @see com.wiseco.decision.common.business.enums.TestResultDiffStatusEnum
     */
    private int comparisonStatus;

    /**
     * 执行耗时
     */
    private long executionTime;

    /**
     * 测试批号
     */
    private String batchNo;

    /**
     * (策略/组件) 测试请求流水号
     */
    private String testSerialNo;

    /**
     * 输入
     */
    private String inputContent;

    /**
     * 预期结果
     */
    private String expectContent;

    /**
     * 执行结果内容：实际结果
     */

    private String resultsContent;

    /**
     * 执行结果内容：原始内容，包含输入、输出、引擎、外部服务、公共决策模块
     * JSON 反序列化为 String
     */

    private String originalContent;

    /**
     * 执行结果内容：对比实际结果内容
     * JSON 反序列化为 String
     */
    private String comparisonContent;

    /**
     * debug信息
     * JSON 反序列化为 String
     */
    private String debugInfo;

    /**
     * 实际结果表头
     */
    private JSONObject resultsHeader;

    /**
     * 测试执行异常内容
     */
    private String exceptionMsg;

    /**
     * trace信息
     */
    private List<TraceLog> traceLogs;
}
