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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.service.dto.TestCollectAndResultsDto;

import java.util.List;

/**
 * <p>
 * 变量测试数据集 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
public interface VarProcessTestService extends IService<VarProcessTest> {
    /**
     * 分页查询测试数据集
     *
     * @param page page
     * @param spaceId spaceId
     * @param testType testType
     * @param variableId variableId
     * @param identifier identifier
     * @return 测试集返回数据
     */
    IPage<TestCollectAndResultsDto> findPageByVariableIdAndIdentifier(IPage<TestCollectAndResultsDto> page, Long spaceId, Integer testType,
                                                                      Long variableId, String identifier);

    /**
     * 根据变量标识查询数据集最大序号
     *
     * @param spaceId spaceId
     * @param identifier identifier
     * @return 最大序号
     */
    Integer findMaxSeqNoByIdentifier(Long spaceId, String identifier);

    /**
     * 获取测试变量清单
     *
     * @return 变量清单Id
     */
    List<Long> getTestedManifests();
}
