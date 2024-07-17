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
import com.wiseco.var.process.app.server.repository.VarProcessVariableReferenceMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.service.VarProcessVariableReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量间引用关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessVariableReferenceServiceImpl extends ServiceImpl<VarProcessVariableReferenceMapper, VarProcessVariableReference> implements
                                                                                                                                       VarProcessVariableReferenceService {
    @Autowired
    private VarProcessVariableReferenceMapper variableReferenceMapper;

    @Override
    public List<VarProcessVariableReference> getVariableReferenceList(Long spaceId) {
        return variableReferenceMapper.getVariableReferenceList(spaceId);
    }

    @Override
    public Set<Long> findUsedVariables(Long spaceId) {
        return variableReferenceMapper.findUsedVariables(spaceId);
    }
}
