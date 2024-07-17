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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.PreviewTestDataHeaderMapper;
import com.wiseco.var.process.app.server.repository.entity.PreviewTestDataHeader;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestDataHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlPreviewTestDataHeaderServiceImpl implements PreTestDataHeaderService {

    @Autowired
    private PreviewTestDataHeaderMapper previewTestDataHeaderMapper;

    @Override
    public void saveOne(MongoPreviewTestDataHeader newEntity) {
        previewTestDataHeaderMapper.insert(EntityConverter.convertEntity(newEntity, PreviewTestDataHeader.class));
    }

    @Override
    public MongoPreviewTestDataHeader getOneByUuid(String uuid) {
        PreviewTestDataHeader previewTestDataHeader = previewTestDataHeaderMapper.selectOne(Wrappers.<PreviewTestDataHeader>lambdaQuery()
                .eq(PreviewTestDataHeader::getUuid, uuid));
        return EntityConverter.convertEntity(previewTestDataHeader, MongoPreviewTestDataHeader.class);
    }

    @Override
    public void removeOneByUuid(String uuid) {
        previewTestDataHeaderMapper.delete(Wrappers.<PreviewTestDataHeader>lambdaQuery()
                .eq(PreviewTestDataHeader::getUuid, uuid));
    }
}
