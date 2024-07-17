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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessInternalDataMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 内部数据表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-21
 */
@Service
public class VarProcessInternalDataServiceImpl extends ServiceImpl<VarProcessInternalDataMapper, VarProcessInternalData> implements
        VarProcessInternalDataService {

    @Autowired
    private VarProcessInternalDataMapper varProcessInternalDataMapper;

    @Override
    public IPage<VarProcessInternalData> findPageList(Page page, Long spaceId, String keywords) {
        return varProcessInternalDataMapper.findPageList(page, spaceId, keywords);
    }

    @Override
    public List<VarProcessInternalData> findByIdentifier(Long spaceId, String identifier) {
        return varProcessInternalDataMapper.findByIdentifier(spaceId, identifier);
    }

}
