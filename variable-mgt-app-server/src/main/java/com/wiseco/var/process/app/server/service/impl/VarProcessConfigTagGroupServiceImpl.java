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
import com.wiseco.var.process.app.server.repository.VarProcessConfigTagGroupMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTagGroup;
import com.wiseco.var.process.app.server.service.VarProcessConfigTagGroupService;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量标签组配置 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
@Service
public class VarProcessConfigTagGroupServiceImpl extends ServiceImpl<VarProcessConfigTagGroupMapper, VarProcessConfigTagGroup> implements
        VarProcessConfigTagGroupService {

    @Autowired
    private VarProcessConfigTagGroupMapper varProcessConfigTagGroupMapper;

    @Override
    public IPage<VarProcessConfigTagGroupDto> getList(Page page, Long spaceId, String keywords, List<Long> idList,List<String> deptCodes,List<String> userNames) {
        return varProcessConfigTagGroupMapper.getList(page, spaceId, keywords, idList,deptCodes,userNames);
    }

    @Override
    public List<VarProcessConfigTagGroupDto> getTagTrees(Long spaceId, String keywords,List<String> deptCodes,List<String> userNames) {
        return varProcessConfigTagGroupMapper.getTagTrees(spaceId, keywords,deptCodes,userNames);
    }

    @Override
    public Integer getMaxOrderNo() {
        return varProcessConfigTagGroupMapper.getMaxOrderNo();
    }
}
