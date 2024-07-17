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
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestData;
import com.wiseco.var.process.app.server.repository.mongodb.repository.PreviewTestDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@MessageCondition.OnMongoMessageEnabled
public class MongoPreviewTestDataServiceImpl implements PreTestDataService {
    private final PreviewTestDataRepository previewTestDataRepository;

    /**
     * constructor
     * @param previewTestDataRepository PreviewTestDataRepository
     */
    @Autowired
    public MongoPreviewTestDataServiceImpl(PreviewTestDataRepository previewTestDataRepository) {
        this.previewTestDataRepository = previewTestDataRepository;
    }

    /**
     *
     * @param newEntityList 新数据实体类 List
     */
    public void saveAll(List<MongoPreviewTestData> newEntityList) {
        previewTestDataRepository.saveAll(newEntityList);
    }

    @Override
    public List<MongoPreviewTestData> findPageByUuid(String uuid, Integer page, Integer size) {
        // 设置查询 probe
        MongoPreviewTestData dataProbe = new MongoPreviewTestData();
        dataProbe.setUuid(uuid);
        Example<MongoPreviewTestData> dataExample = Example.of(dataProbe);
        // 分页设置
        Pageable pageConfig = PageRequest.of(page, size);

        return previewTestDataRepository.findAll(dataExample, pageConfig).getContent();
    }

    /**
     * removeAllByUuid
     * @param uuid 测试预览数据 UUID
     */
    public void removeAllByUuid(String uuid) {
        previewTestDataRepository.removeAllByUuid(uuid);
    }

    /**
     * countByUuid
     * @param uuid 测试预览数据 UUID
     * @return count
     */
    public Integer countByUuid(String uuid) {
        return previewTestDataRepository.countByUuid(uuid);
    }
}
