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
import com.wiseco.var.process.app.server.repository.SysDynamicMapper;
import com.wiseco.var.process.app.server.repository.entity.SysDynamic;
import com.wiseco.var.process.app.server.service.SysDynamicService;
import com.wiseco.var.process.app.server.service.dto.SysDynamicQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统动态表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-03-02
 */
@Service
public class SysDynamicServiceImpl extends ServiceImpl<SysDynamicMapper, SysDynamic> implements SysDynamicService {

    @Autowired
    private SysDynamicMapper sysDynamicMapper;

    @Override
    public List<SysDynamic> findListByCreatedTime(SysDynamicQueryDto queryDto) {
        return sysDynamicMapper.findListByCreatedTime(queryDto.getStartTime(), queryDto.getDynamicCount());
    }

    @Override
    public IPage<SysDynamic> findList(Page page, List<Long> list) {
        return sysDynamicMapper.findList(page, list);
    }

    @Override
    public IPage<SysDynamic> findMainSysDynamic(Page page, SysDynamicQueryDto queryDto) {
        return sysDynamicMapper.findMainSysDynamic(page, queryDto);
    }

    @Override
    public IPage<SysDynamic> findSysDynamic(Page page, SysDynamicQueryDto queryDto) {
        return sysDynamicMapper.findSysDynamic(page, queryDto);
    }
}
