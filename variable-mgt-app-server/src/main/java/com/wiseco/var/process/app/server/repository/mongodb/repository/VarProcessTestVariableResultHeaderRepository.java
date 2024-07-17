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
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResultHeader;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * 组件测试执行结果表头 仓库接口
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@MessageCondition.OnMongoMessageEnabled
public interface VarProcessTestVariableResultHeaderRepository extends MongoRepository<MongoVarProcessTestVariableResultHeader, Long> {

    /**
     * 根据测试数据集 ID 查询
     *
     * @param resultId 测试数据集 ID
     * @return         查询结果
     */
    Optional<MongoVarProcessTestVariableResultHeader> findByResultId(Long resultId);
}
