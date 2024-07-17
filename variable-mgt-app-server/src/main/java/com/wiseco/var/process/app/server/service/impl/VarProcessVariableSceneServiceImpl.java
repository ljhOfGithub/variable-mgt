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
import com.wiseco.var.process.app.server.repository.VarProcessVariableSceneMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableScene;
import com.wiseco.var.process.app.server.service.VarProcessVariableSceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 变量-变量模板关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessVariableSceneServiceImpl extends ServiceImpl<VarProcessVariableSceneMapper, VarProcessVariableScene> implements
        VarProcessVariableSceneService {

    @Autowired
    private VarProcessVariableSceneMapper varProcessVariableSceneMapper;

    @Override
    public int countUseVariables(Long id) {
        return varProcessVariableSceneMapper.countUseVariables(id);
    }

    @Override
    public Set<Long> findUsedScenes() {
        return varProcessVariableSceneMapper.findUsedScenes();
    }
}