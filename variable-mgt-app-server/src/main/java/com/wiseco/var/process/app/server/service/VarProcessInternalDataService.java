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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;

import java.util.List;

/**
 * <p>
 * 内部数据表 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-21
 */
public interface VarProcessInternalDataService extends IService<VarProcessInternalData> {
    /**
     * 根据空间ID和关键字查询内部数据
     *
     * @param page 分页参数
     * @param spaceId 变量空间Id
     * @param keywords 关键字
     * @return 内部数据
     */
    IPage<VarProcessInternalData> findPageList(Page page, Long spaceId, String keywords);

    /**
     * 通过唯一标识符寻找内部数据list
     *
     * @param spaceId 变量空间Id
     * @param identifier 唯一标识符
     * @return 内部数据List
     */
    List<VarProcessInternalData> findByIdentifier(Long spaceId, String identifier);
}
