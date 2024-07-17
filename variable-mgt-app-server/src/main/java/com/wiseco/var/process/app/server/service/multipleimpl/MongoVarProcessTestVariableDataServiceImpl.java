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
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import com.wiseco.var.process.app.server.repository.mongodb.repository.VarProcessTestVariableDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@MessageCondition.OnMongoMessageEnabled
public class MongoVarProcessTestVariableDataServiceImpl implements VarProcessTestVariableDataService {
    @Autowired
    private VarProcessTestVariableDataRepository varProcessTestVariableDataRepository;

    /**
     * 分页查询
     * @param testId    测试数据集 ID
     * @param pageIndex 页码
     * @param pageSize  每页数据量
     * @return list
     */
    public List<MongoVarProcessTestVariableData> findPageByTestId(Long testId, Integer pageIndex, Integer pageSize) {
        // 创建分页配置类
        Pageable pageConfig = PageRequest.of(pageIndex, pageSize);
        // 查询并返回结果
        return varProcessTestVariableDataRepository.findPageByTestId(testId, pageConfig).getContent();
    }

    /**
     * 复制数据集ByTestId
     * @param testId    被复制的测试数据集 ID
     * @param newTestId 复制后的测试数据集 ID
     */
    public void duplicateDataSetByTestId(Long testId, Long newTestId) {
        // 获取测试数据集的全部数据明细
        List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = varProcessTestVariableDataRepository.findAllByTestId(testId);
        // 遍历待复制的测试数据集
        mongoVarProcessTestVariableDataList.forEach(testVariableData -> {
            // 擦除 MongoDB _id
            testVariableData.setId(null);
            // 设置新测试数据集 ID
            testVariableData.setTestId(newTestId);
        });
        // 再次保存全部数据
        varProcessTestVariableDataRepository.saveAll(mongoVarProcessTestVariableDataList);
    }

    /**
     * deleteOneByTestDataId
     * @param testId 数据集 ID
     * @param dataId 单条数据在测试数据集中的序列号
     */
    public void deleteOneByTestDataId(Long testId, Integer dataId) {
        // 获取待删除数据明细
        Optional<MongoVarProcessTestVariableData> dataOptional = varProcessTestVariableDataRepository.findByTestIdAndDataId(testId, dataId);
        // 执行删除操作
        dataOptional.ifPresent(testVariableData -> varProcessTestVariableDataRepository.delete(testVariableData));
    }

    /**
     * deleteAllByTestId
     * @param testId
     */
    public void deleteAllByTestId(Long testId) {
        // 执行删除操作
        varProcessTestVariableDataRepository.deleteAllByTestId(testId);
    }

    /**
     * updateOneByTestDataId
     * @param testId        数据集 ID
     * @param dataId        单条数据在测试数据集中的序列号
     * @param inputContent  输入信息
     * @param expectContent  预期结果
     */
    public void updateOneByTestDataId(Long testId, Integer dataId, String inputContent, String expectContent) {
        // 查找待修改的数据明细
        Optional<MongoVarProcessTestVariableData> dataOptional = varProcessTestVariableDataRepository.findByTestIdAndDataId(testId, dataId);
        if (dataOptional.isPresent()) {
            // 查询结果存在时, 对测试数据做出修改
            MongoVarProcessTestVariableData mongoVarProcessTestVariableData = dataOptional.get();
            mongoVarProcessTestVariableData.setInputContent(inputContent);
            mongoVarProcessTestVariableData.setExpectContent(expectContent);
            varProcessTestVariableDataRepository.save(mongoVarProcessTestVariableData);
        }
    }

    /**
     * findMaxDataIdByTestId
     * @param testId 数据集 ID
     * @return Integer
     */
    public Integer findMaxDataIdByTestId(Long testId) {
        // 获取测试数据集的全部数据明细
        MongoVarProcessTestVariableData mongoVarProcessTestVariableData = varProcessTestVariableDataRepository.findTopByTestIdOrderByDataIdDesc(testId);
        // 遍历查找明细的最大序列号
        Integer maxDataId = 0;
        if (mongoVarProcessTestVariableData != null) {
            maxDataId = mongoVarProcessTestVariableData.getDataId();

        }

        return maxDataId;
    }

    /**
     * saveOne
     * @param testId      数据集 ID
     * @param dataId      单条数据在测试数据集中的序列号
     * @param inputContent 输入
     * @param expectContent 预期结果
     */
    public void saveOne(Long testId, Integer dataId, String inputContent, String expectContent) {
        // 新建并保存数据明细
        MongoVarProcessTestVariableData newDocument = MongoVarProcessTestVariableData.builder()
                .testId(testId)
                .dataId(dataId)
                .createdTime(new Date())
                .inputContent(inputContent)
                .expectContent(expectContent)
                .build();
        varProcessTestVariableDataRepository.save(newDocument);
    }

    @Override
    public void saveBatch(List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList) {
        batchSave(mongoVarProcessTestVariableDataList);
    }

    /**
     * findAllByTestId
     * @param testId 测试数据集 ID
     * @return list
     */
    public List<MongoVarProcessTestVariableData> findAllByTestId(Long testId) {
        return varProcessTestVariableDataRepository.findAllByTestId(testId);
    }

    /**
     * batchSave
     * @param testVariableDataIterable testVariableDataIterable
     */
    public void batchSave(Iterable<MongoVarProcessTestVariableData> testVariableDataIterable) {
        varProcessTestVariableDataRepository.saveAll(testVariableDataIterable);
    }

    /**
     * findOneByTestDataId
     * @param testId 测试集ID
     * @param dataId 数据明细ID
     * @return MongoVarProcessTestVariableData
     */
    public MongoVarProcessTestVariableData findOneByTestDataId(Long testId, Integer dataId) {
        // 获取待删除数据明细
        Optional<MongoVarProcessTestVariableData> dataOptional = varProcessTestVariableDataRepository.findByTestIdAndDataId(testId, dataId);
        if (!dataOptional.isPresent()) {
            return null;
        }
        return dataOptional.get();
    }
}
