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
package com.wiseco.var.process.app.server.service.multipleimpl;

import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResult;

import java.util.List;

public interface VarProcessTestVariableResultService {
    /**
     * 根据resultId批量删除
     *
     * @param resultId 结果id
     */
    void deleteAllByResultId(Long resultId);

    /**
     * 根据执行结果 ID 和引擎执行状态码 分页查询
     *
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @param pageIndex       页码
     * @param pageSize        每页数据量
     * @return 分页查询结果
     */
    List<MongoVarProcessTestVariableResult> findPageByResultIdAndExecutionStatus(Long resultId, Integer executionStatus, Integer pageIndex, Integer pageSize);

    /**
     * 根据执行结果 ID 和引擎执行状态码 分页查询(查询正常执行执行和异常执行)
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @param pageIndex       页码
     * @param pageSize        每页数据量
     * @return 分页查询结果
     */
    List<MongoVarProcessTestVariableResult> findPageByResultIdInNormalAndExecutionStatus(Long resultId, Integer executionStatus, Integer pageIndex, Integer pageSize);

    /**
     * 根据结果ID和执行状态统计数据
     *
     * @param resultId 结果id
     * @param executionStatus 执行状态
     * @return 查询结果
     */
    Integer countByResultIdAndExecutionStatus(Long resultId, Integer executionStatus);

    /**
     * 根据执行结果 ID 全量查询
     *
     * @param resultId 执行结果 ID
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultId(Long resultId);

    /**
     * 根据执行结果 ID 和引擎执行状态码查询
     *
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultIdAndExecutionStatus(Long resultId, Integer executionStatus);

    /**
     * 根据执行结果 ID 和预期结果对比状态码查询
     *
     * @param resultId         执行结果 ID
     * @param comparisonStatus 预期结果对比状态码
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableResult> findAllByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus);

    /**
     * 根据执行结果 ID 和预期结果对比状态码 分页查询
     *
     * @param resultId         执行结果 ID
     * @param comparisonStatus 执行结果状态
     * @param pageIndex        页码
     * @param pageSize         每页数据量
     * @return 分页查询结果
     */
    List<MongoVarProcessTestVariableResult> findPageByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus, Integer pageIndex, Integer pageSize);
    /**
     * 根据执行结果 ID 分页查询
     *
     * @param resultId  执行结果 ID
     * @param pageIndex 页码
     * @param pageSize  每页数据量
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableResult> findPageByResultId(Long resultId, Integer pageIndex, Integer pageSize);

    /**
     * 通过testSerialNo获取测试结果右边的详情
     * @param testSerialNo 组件测试请求流水号
     * @return 测试结果右边的详情
     */
    MongoVarProcessTestVariableResult getTestResultDetail(String testSerialNo);

    /**
     * 批量保存或更新测试执行结果
     *
     * @param resultId   执行结果 ID
     * @param resultList 测试执行结果列表
     */
    void saveOrUpdateBatch(Long resultId, List<MongoVarProcessTestVariableResult> resultList);

    /**
     * 通过测试流水号，获取对应的组件类型(1——变量；2——公共方法+数据预处理+变量模板；3——变量清单)
     * @param serialNo 流水号
     * @return 组件类型
     */
    Integer getTestTypeBySerialNo(String serialNo);
}
