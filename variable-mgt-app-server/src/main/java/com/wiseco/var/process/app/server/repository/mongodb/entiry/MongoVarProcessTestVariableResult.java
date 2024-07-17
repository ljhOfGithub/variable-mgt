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
package com.wiseco.var.process.app.server.repository.mongodb.entiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 变量测试执行结果 实体类
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "var_process_test_variable_result")
@CompoundIndexes({
        @CompoundIndex(name = "resultId_1_executionStatus_1", def = "{'resultId': 1, 'executionStatus': 1}"),
        @CompoundIndex(name = "resultId_1_comparisonStatus_1", def = "{'resultId': 1, 'comparisonStatus': 1}")
})
public class MongoVarProcessTestVariableResult implements Serializable {

    private static final long serialVersionUID = -6374685856937850808L;

    /**
     * 主键 ID
     */
    @Id
    @Field(order = 0)
    private ObjectId id;

    /**
     * 测试数据集 ID
     * 对应 MySQL 表格 test_component.id
     */
    @Field(order = 1)
    private Long testId;

    /**
     * 执行结果 ID
     * 对应 MySQL 表格 test_component_results.id
     */
    @Field(order = 2)
    @Indexed(direction = IndexDirection.ASCENDING)
    private Long resultId;

    /**
     * 单条数据 (数据明细) 在测试数据集中的序列号
     */
    @Field(order = 3)
    private Integer dataId;

    /**
     * 测试批号
     */
    @Field(order = 4)
    private String batchNo;

    /**
     * 组件测试请求流水号
     */
    @Field(order = 5)
    private String testSerialNo;

    /**
     * 创建时间
     */
    @Field(order = 6)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 执行结果状态码：0-执行异常，1-执行正常
     * @see com.wiseco.decision.common.business.enums.TestExecStatusEnum
     */
    @Field(order = 7)
    private Integer executionStatus;

    /**
     * 预期结果对比状态码：
     * @see com.wiseco.decision.common.business.enums.TestResultDiffStatusEnum
     */
    @Field(order = 8)
    private Integer comparisonStatus;


    /**
     * 执行结果内容：输入
     * JSON 反序列化为 String
     */
    @Field(order = 9)
    private String inputContent;

    /**
     * 执行结果内容：预期结果
     * JSON 反序列化为 String
     */
    @Field(order = 10)
    private String expectContent;

    /**
     * 执行结果内容：实际结果
     * JSON 反序列化为 String
     */
    @Field(order = 11)
    private String resultsContent;

    /**
     * 执行结果内容：原始内容，包含输入、输出
     * JSON 反序列化为 String
     */
    @Field(order = 12)
    private String originalContent;

    /**
     * 执行结果内容：对比实际结果内容
     * JSON 反序列化为 String
     */
    @Field(order = 13)
    private String comparisonContent;

    /**
     * 测试执行异常内容
     */
    @Field(order = 14)
    private String exceptionMsg;

    /**
     * debug信息
     * JSON 反序列化为 String
     */
    @Field(order = 15)
    private String debugInfo;

    /**
     * 执行耗时
     */
    @Field(order = 16)
    private long executionTime;
}
