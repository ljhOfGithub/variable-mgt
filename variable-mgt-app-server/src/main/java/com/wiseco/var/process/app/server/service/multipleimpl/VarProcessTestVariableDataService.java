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

import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;

import java.util.List;

public interface VarProcessTestVariableDataService {

    /**
     * 根据测试集ID和数据明细ID查询明细
     *
     * @param testId 测试集ID
     * @param dataId 数据明细ID
     * @return VarProcessTestVariableData
     */
    MongoVarProcessTestVariableData findOneByTestDataId(Long testId, Integer dataId);

    /**
     * 向数据集新增一条数据明细
     *
     * @param testId      数据集 ID
     * @param dataId      单条数据在测试数据集中的序列号
     * @param inputContent 输入
     * @param expectContent 预期结果
     */
    void saveOne(Long testId, Integer dataId, String inputContent, String expectContent);

    /**
     * 批量保存测试数据集文档
     *
     * @param mongoVarProcessTestVariableDataList 即将被保存的测试数据集文档
     */
    void saveBatch(List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList);

    /**
     * 根据测试数据集 ID 全量查询
     *
     * @param testId 测试数据集 ID
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableData> findAllByTestId(Long testId);

    /**
     * 根据测试数据集 ID 分页查询
     *
     * @param testId    测试数据集 ID
     * @param pageIndex 页码
     * @param pageSize  每页数据量
     * @return 分页查询结果
     */
    List<MongoVarProcessTestVariableData> findPageByTestId(Long testId, Integer pageIndex, Integer pageSize);

    /**
     * 根据数据集 ID 和序列号删除一条 document
     *
     * @param testId 数据集 ID
     * @param dataId 单条数据在测试数据集中的序列号
     */
    void deleteOneByTestDataId(Long testId, Integer dataId);

    /**
     * 根据数据集 ID 和序列号更新一条测试数据内容
     *
     * @param testId        数据集 ID
     * @param dataId        单条数据在测试数据集中的序列号
     * @param inputContent  输入信息
     * @param expectContent  预期结果
     */
    void updateOneByTestDataId(Long testId, Integer dataId, String inputContent, String expectContent);

    /**
     * 根据数据集 ID 查找当前数据集最大的序列号
     *
     * @param testId 数据集 ID
     * @return 当前数据集最大的序列号
     */
    Integer findMaxDataIdByTestId(Long testId);

    /**
     * 复制测试数据集的全部数据
     *
     * @param testId    被复制的测试数据集 ID
     * @param newTestId 复制后的测试数据集 ID
     */
    void duplicateDataSetByTestId(Long testId, Long newTestId);
}
