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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.VarProcessTestMapper;
import com.wiseco.var.process.app.server.repository.VarProcessTestVariableResultMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResult;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlVarProcessTestVariableResultServiceImpl extends ServiceImpl<VarProcessTestVariableResultMapper, VarProcessTestVariableResult> implements VarProcessTestVariableResultService {

    @Autowired
    private VarProcessTestVariableResultMapper  varProcessTestVariableResultMapper;

    @Autowired
    private VarProcessTestMapper varProcessTestMapper;

    @Override
    public void deleteAllByResultId(Long resultId) {
        varProcessTestVariableResultMapper.delete(Wrappers.<VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId,resultId));
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultIdAndExecutionStatus(Long resultId, Integer executionStatus, Integer pageIndex, Integer pageSize) {
        LambdaQueryWrapper<VarProcessTestVariableResult> queryWrapper = Wrappers.<com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId, resultId)
                .eq(VarProcessTestVariableResult::getExecutionStatus, executionStatus)
                .orderByAsc(VarProcessTestVariableResult::getId);
        Page<VarProcessTestVariableResult> varProcessTestVariableResultPage = varProcessTestVariableResultMapper.selectPage(new Page<>(pageIndex + MagicNumbers.ONE, pageSize), queryWrapper);
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResultPage.getRecords());
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
        LambdaQueryWrapper<VarProcessTestVariableResult> queryWrapper = Wrappers.<com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResult>lambdaQuery()
                .select(VarProcessTestVariableResult::getId, VarProcessTestVariableResult::getDataId, VarProcessTestVariableResult::getTestSerialNo,
                        VarProcessTestVariableResult::getExecutionTime, VarProcessTestVariableResult::getExecutionStatus, VarProcessTestVariableResult::getExpectContent,
                        VarProcessTestVariableResult::getComparisonStatus, VarProcessTestVariableResult::getExecutionStatus)
                .eq(VarProcessTestVariableResult::getResultId, resultId)
                .eq(VarProcessTestVariableResult::getExecutionStatus, executionStatus)
                .eq(VarProcessTestVariableResult::getComparisonStatus, MagicNumbers.TWO)
                .orderByAsc(VarProcessTestVariableResult::getId);
        Page<VarProcessTestVariableResult> varProcessTestVariableResultPage = varProcessTestVariableResultMapper.selectPage(new Page<>(pageIndex + MagicNumbers.ONE, pageSize), queryWrapper);
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResultPage.getRecords());
    }

    @Override
    public Integer countByResultIdAndExecutionStatus(Long resultId, Integer executionStatus) {
        return Integer.valueOf(varProcessTestVariableResultMapper.selectCount(Wrappers.<VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId,resultId)
                .eq(VarProcessTestVariableResult::getExecutionStatus,executionStatus)).toString());
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultId(Long resultId) {
        List<VarProcessTestVariableResult> varProcessTestVariableResults = varProcessTestVariableResultMapper.selectList(Wrappers.<VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId, resultId));
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResults);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultIdAndExecutionStatus(Long resultId, Integer executionStatus) {
        List<VarProcessTestVariableResult> varProcessTestVariableResults = varProcessTestVariableResultMapper.selectList(Wrappers.<VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId, resultId)
                .eq(VarProcessTestVariableResult::getExecutionStatus,executionStatus));
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResults);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findAllByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus) {
        List<VarProcessTestVariableResult> varProcessTestVariableResults = varProcessTestVariableResultMapper.selectList(Wrappers.<VarProcessTestVariableResult>lambdaQuery()
                .eq(VarProcessTestVariableResult::getResultId, resultId)
                .eq(VarProcessTestVariableResult::getComparisonStatus,comparisonStatus));
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResults);
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultIdAndComparisonStatus(Long resultId, Integer comparisonStatus, Integer pageIndex, Integer pageSize) {
        LambdaQueryWrapper<VarProcessTestVariableResult> queryWrapper = Wrappers.<com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResult>lambdaQuery()
                .select(VarProcessTestVariableResult::getId, VarProcessTestVariableResult::getDataId, VarProcessTestVariableResult::getTestSerialNo,
                        VarProcessTestVariableResult::getExecutionTime, VarProcessTestVariableResult::getExecutionStatus, VarProcessTestVariableResult::getExpectContent,
                        VarProcessTestVariableResult::getComparisonStatus, VarProcessTestVariableResult::getExecutionStatus)
                .eq(VarProcessTestVariableResult::getResultId, resultId)
                .eq(VarProcessTestVariableResult::getComparisonStatus, comparisonStatus)
                .orderByAsc(VarProcessTestVariableResult::getId);
        Page<VarProcessTestVariableResult> varProcessTestVariableResultPage = varProcessTestVariableResultMapper.selectPage(new Page<>(pageIndex + MagicNumbers.ONE, pageSize), queryWrapper);
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResultPage.getRecords());
    }

    @Override
    public List<MongoVarProcessTestVariableResult> findPageByResultId(Long resultId, Integer pageIndex, Integer pageSize) {
        LambdaQueryWrapper<VarProcessTestVariableResult> queryWrapper = Wrappers.<com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableResult>lambdaQuery()
                .select(VarProcessTestVariableResult::getId, VarProcessTestVariableResult::getDataId, VarProcessTestVariableResult::getTestSerialNo,
                        VarProcessTestVariableResult::getExecutionTime, VarProcessTestVariableResult::getExecutionStatus, VarProcessTestVariableResult::getExpectContent,
                        VarProcessTestVariableResult::getComparisonStatus)
                .orderByAsc(VarProcessTestVariableResult::getId)
                .eq(VarProcessTestVariableResult::getResultId, resultId);
        Page<VarProcessTestVariableResult> varProcessTestVariableResultPage = varProcessTestVariableResultMapper.selectPage(new Page<>(pageIndex + MagicNumbers.ONE, pageSize), queryWrapper);
        return convertMysqlEntityToMongoEntity(varProcessTestVariableResultPage.getRecords());
    }

    /**
     * 通过testSerialNo获取测试结果右边的详情
     * @param testSerialNo 组件测试请求流水号
     * @return 测试结果右边的详情
     */
    @Override
    public MongoVarProcessTestVariableResult getTestResultDetail(String testSerialNo) {
        // 1.构造查询条件
        LambdaQueryWrapper<VarProcessTestVariableResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessTestVariableResult::getTestSerialNo, testSerialNo);
        // 2.开始查询
        VarProcessTestVariableResult item = varProcessTestVariableResultMapper.selectOne(wrapper);
        return MongoVarProcessTestVariableResult.builder()
                        .testId(item.getTestId()).resultId(item.getResultId()).dataId(item.getDataId())
                        .batchNo(item.getBatchNo()).testSerialNo(item.getTestSerialNo()).createdTime(item.getCreatedTime()).executionStatus(item.getExecutionStatus())
                        .comparisonStatus(item.getComparisonStatus()).inputContent(item.getInputContent()).expectContent(item.getExpectContent()).resultsContent(item.getResultsContent())
                        .originalContent(item.getOriginalContent()).comparisonContent(item.getComparisonContent()).exceptionMsg(item.getExceptionMsg()).debugInfo(item.getDebugInfo())
                        .executionTime(item.getExecutionTime()).build();
    }

    @Override
    public void saveOrUpdateBatch(Long resultId, List<MongoVarProcessTestVariableResult> resultList) {
        super.saveOrUpdateBatch(resultList.stream().map(item -> EntityConverter.convertEntity(item,VarProcessTestVariableResult.class)).collect(Collectors.toList()));
    }

    /**
     * 通过测试流水号，获取对应的组件类型(1——变量；2——公共方法+数据预处理+变量模板；3——变量清单)
     * @param serialNo 流水号
     * @return 组件类型
     */
    @Override
    public Integer getTestTypeBySerialNo(String serialNo) {
        // 1.直接获取这个流水号对应的测试类型
        return varProcessTestMapper.getTestTypeBySerialNo(serialNo);
    }

    /**
     * 将mysql实体list转成mongoDb实体list
     *
     * @param varProcessTestVariableResults list
     * @return mongoDb实体list
     */
    private List<MongoVarProcessTestVariableResult> convertMysqlEntityToMongoEntity(List<VarProcessTestVariableResult> varProcessTestVariableResults) {
        return varProcessTestVariableResults.stream().map(item -> EntityConverter.convertEntity(item, MongoVarProcessTestVariableResult.class)).collect(Collectors.toList());
    }
}
