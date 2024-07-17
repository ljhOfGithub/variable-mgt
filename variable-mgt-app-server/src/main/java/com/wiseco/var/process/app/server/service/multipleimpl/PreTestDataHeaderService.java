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

import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestDataHeader;

/**
 * 测试预览数据表头 接口
 */
public interface PreTestDataHeaderService {

    /**
     * 保存测试预览数据表头
     * 
     * @param newEntity 新数据实体类
     */
    void saveOne(MongoPreviewTestDataHeader newEntity);

    /**
     * 根据 UUID 查询测试预览数据表头
     * 
     * @param uuid 测试预览数据 UUID
     * @return 测试预览数据表头 实体类
     */
    MongoPreviewTestDataHeader getOneByUuid(String uuid);

    /**
     * 根据 UUID 删除测试预览数据表头
     * 
     * @param uuid 测试预览数据 UUID
     */
    void removeOneByUuid(String uuid);
}
