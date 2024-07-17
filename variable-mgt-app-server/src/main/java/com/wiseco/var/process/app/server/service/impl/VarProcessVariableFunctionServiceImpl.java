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

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.VarProcessVariableFunctionMapper;
import com.wiseco.var.process.app.server.repository.VarProcessVariableMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.service.VarProcessVariableFunctionService;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量-变量模板关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessVariableFunctionServiceImpl extends ServiceImpl<VarProcessVariableFunctionMapper, VarProcessVariableFunction> implements
        VarProcessVariableFunctionService {

    @Autowired
    private VarProcessVariableFunctionMapper varProcessVariableFunctionMapper;

    @Autowired
    private VarProcessVariableMapper            varProcessVariableMapper;

    @Override
    public List<VariableDetailDto> getUseVariableList(Long spaceId, Long functionId) {
        return varProcessVariableFunctionMapper.getUseVariableList(spaceId, functionId);
    }

    @Override
    public List<VarProcessFunction> getFunctionByVariableList(Long variableId) {
        return varProcessVariableFunctionMapper.getFunctionByVariableList(variableId);
    }

    @Override
    public List<VarProcessFunction> getVariableUtilizedVariableTemplate(List<Long> variableIdList) {
        return varProcessVariableFunctionMapper.getVariableUtilizedVariableTemplate(variableIdList);
    }

    @Override
    public List<VarProcessVariable> findVariableUseTemp(Long templateId, Long spaceId) {
        List<Long> variableIds = varProcessVariableFunctionMapper.selectList(Wrappers.<VarProcessVariableFunction>lambdaQuery()
                .eq(VarProcessVariableFunction::getVarProcessSpaceId, spaceId)
                .eq(VarProcessVariableFunction::getFunctionId, templateId)).stream()
                .map(VarProcessVariableFunction::getVariableId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(variableIds)) {
            return new ArrayList<>();
        }
        return varProcessVariableMapper.selectList(Wrappers.<VarProcessVariable>lambdaQuery()
                .select(VarProcessVariable::getId, VarProcessVariable::getLabel, VarProcessVariable::getName, VarProcessVariable::getVersion)
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                .in(VarProcessVariable::getId, variableIds));
    }

    @Override
    public Set<Long> findUsedFunctions(Long spaceId) {
        return varProcessVariableFunctionMapper.findUsedFunctions(spaceId);
    }
}
