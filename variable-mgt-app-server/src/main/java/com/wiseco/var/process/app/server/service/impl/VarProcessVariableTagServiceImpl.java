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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessVariableTagMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import com.wiseco.var.process.app.server.service.VarProcessVariableTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量-标签关系表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
@Service
public class VarProcessVariableTagServiceImpl extends ServiceImpl<VarProcessVariableTagMapper, VarProcessVariableTag> implements
        VarProcessVariableTagService {

    @Autowired
    private VarProcessVariableTagMapper varProcessVariableTagMapper;

    @Override
    public int countTagGroup(Long spaceId, Long groupId) {
        return varProcessVariableTagMapper.countTagGroup(spaceId, groupId);
    }

    @Override
    public int countTag(Long spaceId, String tagName) {
        return varProcessVariableTagMapper.countTag(spaceId, tagName);
    }

    /**
     * 通过tagId, 查出变量的Id
     * @param groupId 标签的Id
     * @return 变量的Id集合
     */
    @Override
    public Set<Long> getVariableIds(Long groupId) {
        List<Long> variables = varProcessVariableTagMapper.variables(groupId);
        return new HashSet<>(variables);
    }

    /**
     * 通过tagId，查出它所属的groupId
     * @param tagId tagId
     * @return groupId
     */
    @Override
    public Long getGroupIdByTagId(Long tagId) {
        return varProcessVariableTagMapper.getGroupIdByTagId(tagId);
    }
}
