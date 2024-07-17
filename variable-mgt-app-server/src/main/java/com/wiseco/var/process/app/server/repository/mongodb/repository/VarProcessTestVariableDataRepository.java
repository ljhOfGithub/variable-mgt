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
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * 组件测试数据集 仓库接口
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@MessageCondition.OnMongoMessageEnabled
public interface VarProcessTestVariableDataRepository extends MongoRepository<MongoVarProcessTestVariableData, Long> {

    /**
     * 根据测试数据集 ID 分页查询
     *
     * @param testId   测试数据集 ID
     * @param pageable 分页配置
     * @return Page 类型的分页查询结果
     */
    Page<MongoVarProcessTestVariableData> findPageByTestId(Long testId, Pageable pageable);

    /**
     * 根据测试数据集 ID 全量查询
     *
     * @param testId 测试数据集 ID
     * @return 查询结果
     */
    List<MongoVarProcessTestVariableData> findAllByTestId(Long testId);

    /**
     * 根据测试数据集 ID 和数据明细序列号单条查询
     *
     * @param testId 测试数据集 ID
     * @param dataId 数据明细序列号
     * @return 查询结果
     */
    Optional<MongoVarProcessTestVariableData> findByTestIdAndDataId(Long testId, Integer dataId);

    /**
     * 根据数据集 ID 删除测试数据集的全部数据
     *
     * @param testId 数据集 ID
     */
    void deleteAllByTestId(Long testId);

    /**
     * 根据测试数据集 ID 查询最大数据
     *
     * @param testId 测试数据集 ID
     * @return 查询结果
     */
    MongoVarProcessTestVariableData findTopByTestIdOrderByDataIdDesc(Long testId);
}
