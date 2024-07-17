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

import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResult;
import com.wiseco.var.process.app.server.repository.mongodb.repository.VarProcessTestVariableResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@MessageCondition.OnMongoMessageEnabled
public class MongoVarProcessTestVariableResultServiceImpl implements VarProcessTestVariableResultService {
    @Autowired
    VarProcessTestVariableResultRepository varProcessTestVariableResultRepository;

    @Override
    public void deleteAllByResultId(Long resultId) {
        // 批量删除旧执行结果
        varProcessTestVariableResultRepository.deleteAllByResultId(resultId);

    }

    @Override
    public void saveOrUpdateBatch(Long resultId, List<MongoVarProcessTestVariableResult> resultList) {

        // 批量保存新执行结果
        varProcessTestVariableResultRepository.saveAll(resultList);
    }

    /**
     * 通过测试流水号，获取对应的组件类型(1——变量；2——公共方法+数据预处理+变量模板；3——变量清单)
     * @param serialNo 流水号
     * @return 组件类型
     */
    @Override
    public Integer getTestTypeBySerialNo(String serialNo) {
        return null;
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultId(Long resultId) {
        return varProcessTestVariableResultRepository.findAllByResultId(resultId);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultId(Long resultId, Integer pageIndex, Integer pageSize) {
        // 创建分页配置类
        Pageable pageConfig = PageRequest.of(pageIndex, pageSize);
        // 查询并返回结果
        return varProcessTestVariableResultRepository.findPageByResultId(resultId, pageConfig).getContent();
    }

    @Override
    public MongoVarProcessTestVariableResult getTestResultDetail(String testSerialNo) {
        return new MongoVarProcessTestVariableResult();
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultIdAndExecutionStatus(Long resultId, Integer executionStatus) {
        return varProcessTestVariableResultRepository.findAllByResultIdAndExecutionStatus(resultId, executionStatus);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus) {
        return varProcessTestVariableResultRepository.findAllByResultIdAndComparisonStatus(resultId, comparisonStatus);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultIdAndExecutionStatus(Long resultId, Integer executionStatus, Integer pageIndex, Integer pageSize) {
        // 创建分页配置类
        Pageable pageConfig = PageRequest.of(pageIndex, pageSize);
        // 查询并返回结果
        return varProcessTestVariableResultRepository.findPageByResultIdAndExecutionStatus(resultId, executionStatus, pageConfig).getContent();
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus, Integer pageIndex, Integer pageSize) {
        // 创建分页配置类
        Pageable pageConfig = PageRequest.of(pageIndex, pageSize);
        // 查询并返回结果
        return varProcessTestVariableResultRepository.findPageByResultIdAndComparisonStatus(resultId, comparisonStatus, pageConfig).getContent();
    }

    @Override
    public Integer countByResultIdAndExecutionStatus(Long resultId, Integer executionStatus) {

        return varProcessTestVariableResultRepository.countByResultIdAndExecutionStatus(resultId, executionStatus);
    }
    /**
     * 根据执行结果 ID 和引擎执行状态码 分页查询(查询正常执行执行和异常执行)
     * @param resultId        执行结果 ID
     * @param executionStatus 引擎执行状态码
     * @param pageIndex       页码
     * @param pageSize        每页数据量
     * @return 分页查询结果
     */
    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultIdInNormalAndExecutionStatus(Long resultId, Integer executionStatus, Integer pageIndex, Integer pageSize) {
        return null;
    }
}
