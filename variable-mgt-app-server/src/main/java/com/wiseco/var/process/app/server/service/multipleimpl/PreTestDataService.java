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

import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestData;

import java.util.List;

public interface PreTestDataService {

    /**
     * 批量保存测试预览数据
     *
     * @param newEntityList 新数据实体类 List
     */
    void saveAll(List<MongoPreviewTestData> newEntityList);

    /**
     * 分页查询测试预览数据
     *
     * @param uuid 测试预览数据 UUID
     * @param page 页码
     * @param size 每页记录数
     * @return 数据实体类 List
     */
    List<MongoPreviewTestData> findPageByUuid(String uuid, Integer page, Integer size);

    /**
     * 根据 UUID 批量删除测试预览数据
     *
     * @param uuid 测试预览数据 UUID
     */
    void removeAllByUuid(String uuid);

    /**
     * 根据 UUID 查询预览数据集数量
     *
     * @param uuid 测试预览数据 UUID
     * @return 预览数据集数量
     */
    Integer countByUuid(String uuid);
}
