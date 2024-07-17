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
package com.wiseco.var.process.app.server.repository.mongodb.repository;

import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 组件测试执行结果 仓库接口
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@MessageCondition.OnMongoMessageEnabled
public interface VarProcessTestVariableResultRepository extends MongoRepository<MongoVarProcessTestVariableResult, Long> {

    /**
     * 根据执行结果 ID 全量查询
     *
     * @param resultId 执行结果 ID
     * @return         查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultId(Long resultId);

    /**
     * 根据执行结果 ID 分页查询
     *
     * @param resultId 执行结果 ID
     * @param pageable 分页配置
     * @return         Page 类型的分页查询结果
     */
    Page<MongoVarProcessTestVariableResult> findPageByResultId(Long resultId, Pageable pageable);

    /**
     * 根据执行结果 ID 和引擎执行状态码查询
     *
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @return                查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultIdAndExecutionStatus(Long resultId, Integer executionStatus);

    /**
     * 根据执行结果 ID 和预期结果对比状态码查询
     *
     * @param resultId         执行结果 ID
     * @param comparisonStatus 预期结果对比状态码
     * @return                 查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus);

    /**
     * 根据执行结果 ID 和引擎执行状态查询
     *
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @param pageable        分页配置
     * @return                Page 类型的分页查询结果
     */
    Page<MongoVarProcessTestVariableResult> findPageByResultIdAndExecutionStatus(Long resultId, Integer executionStatus, Pageable pageable);

    /**
     * 根据执行结果 ID 和预期结果对比状态码查询
     *
     * @param resultId         执行结果 ID
     * @param comparisonStatus 预期结果对比状态码
     * @param pageable         分页配置
     * @return                 Page 类型的分页查询结果
     */
    Page<MongoVarProcessTestVariableResult> findPageByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus, Pageable pageable);

    /**
     * 根据执行结果 ID 删除全量删除对应记录
     *
     * @param resultId 执行结果 ID
     */
    void deleteAllByResultId(Long resultId);

    /**
     * 根据结果ID和执行状态统计数据
     * @param resultId 结果id
     * @param executionStatus 执行状态
     * @return count
     */
    Integer countByResultIdAndExecutionStatus(Long resultId, Integer executionStatus);
}
