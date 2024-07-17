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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.PreviewTestDataMapper;
import com.wiseco.var.process.app.server.repository.entity.PreviewTestData;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlPreviewTestDataServiceImpl extends ServiceImpl<PreviewTestDataMapper,
        PreviewTestData> implements PreTestDataService {

    @Autowired
    private PreviewTestDataMapper   previewTestDataMapper;

    @Override
    public void saveAll(List<MongoPreviewTestData> newEntityList) {
        List<PreviewTestData> mysqlDataList = newEntityList.stream().map(item -> EntityConverter.convertEntity(item, PreviewTestData.class)).collect(Collectors.toList());
        super.saveBatch(mysqlDataList);
    }

    @Override
    public List<MongoPreviewTestData> findPageByUuid(String uuid, Integer page, Integer size) {
        Page<PreviewTestData> previewTestDataPage = previewTestDataMapper.selectPage(new Page<>(page + MagicNumbers.ONE, size), Wrappers.<PreviewTestData>lambdaQuery().eq(PreviewTestData::getUuid, uuid).orderByDesc(PreviewTestData::getCreatedTime));
        return previewTestDataPage.getRecords().stream().map(item -> EntityConverter.convertEntity(item, MongoPreviewTestData.class)).collect(Collectors.toList());
    }

    @Override
    public void removeAllByUuid(String uuid) {
        previewTestDataMapper.delete(Wrappers.<PreviewTestData>lambdaQuery().eq(PreviewTestData::getUuid,uuid));
    }

    @Override
    public Integer countByUuid(String uuid) {
        long count = super.count(Wrappers.<PreviewTestData>lambdaQuery().eq(PreviewTestData::getUuid, uuid));
        return Integer.valueOf(String.valueOf(count));
    }
}
