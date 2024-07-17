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
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestDataHeader;
import com.wiseco.var.process.app.server.repository.mongodb.repository.PreviewTestDataHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@MessageCondition.OnMongoMessageEnabled
public class MongoPreviewTestDataHeaderServiceImpl implements PreTestDataHeaderService {

    private final PreviewTestDataHeaderRepository previewTestDataHeaderRepository;

    /**
     * constructor
     * @param previewTestDataHeaderRepository PreviewTestDataHeaderRepository
     */
    @Autowired
    public MongoPreviewTestDataHeaderServiceImpl(PreviewTestDataHeaderRepository previewTestDataHeaderRepository) {
        this.previewTestDataHeaderRepository = previewTestDataHeaderRepository;
    }

    /**
     * saveOne
     * @param newEntity 新数据实体类
     */
    public void saveOne(MongoPreviewTestDataHeader newEntity) {
        previewTestDataHeaderRepository.save(newEntity);
    }

    /**
     * getOneByUuid
     * @param uuid 测试预览数据 UUID
     * @return MongoPreviewTestDataHeader
     */
    public MongoPreviewTestDataHeader getOneByUuid(String uuid) {
        // 使用 Query By Example 风格查询 MongoDB
        MongoPreviewTestDataHeader headerProbe = new MongoPreviewTestDataHeader();
        headerProbe.setUuid(uuid);
        Example<MongoPreviewTestDataHeader> headerExample = Example.of(headerProbe);

        return previewTestDataHeaderRepository.findOne(headerExample).orElse(null);
    }

    /**
     * 根据uuid删除
     * @param uuid 测试预览数据 UUID
     */
    public void removeOneByUuid(String uuid) {
        // 设置查询 probe
        MongoPreviewTestDataHeader headerProbe = new MongoPreviewTestDataHeader();
        headerProbe.setUuid(uuid);
        log.info("解决spotbugs: " + headerProbe);
        previewTestDataHeaderRepository.removeByUuid(uuid);
    }
}
