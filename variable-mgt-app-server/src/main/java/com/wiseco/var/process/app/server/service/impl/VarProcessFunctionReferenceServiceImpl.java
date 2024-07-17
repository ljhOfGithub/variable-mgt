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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionMapper;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionReferenceMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.service.VarProcessFunctionReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 公共函数间引用关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessFunctionReferenceServiceImpl extends ServiceImpl<VarProcessFunctionReferenceMapper, VarProcessFunctionReference> implements
        VarProcessFunctionReferenceService {

    @Autowired
    private VarProcessFunctionReferenceMapper       varProcessFunctionReferenceMapper;

    @Autowired
    private VarProcessFunctionMapper                varProcessFunctionMapper;

    @Override
    public Map<FunctionTypeEnum, List<VarProcessFunction>> findFunctionRef(Long funcid, Long spaceId) {
        List<VarProcessFunctionReference> funcReferenceList = varProcessFunctionReferenceMapper.selectList(
                new QueryWrapper<VarProcessFunctionReference>().lambda()
                        .eq(VarProcessFunctionReference::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessFunctionReference::getFunctionId, funcid)
        );
        if (CollectionUtils.isEmpty(funcReferenceList)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }
        List<Long> funcIds = funcReferenceList.stream().map(VarProcessFunctionReference::getUseByFunctionId).collect(Collectors.toList());
        List<VarProcessFunction> functionList = varProcessFunctionMapper.selectList(Wrappers.<VarProcessFunction>lambdaQuery()
                        .select(VarProcessFunction::getId, VarProcessFunction::getName,VarProcessFunction::getFunctionType)
                        .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                        .in(VarProcessFunction::getId, funcIds)
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
        );
        return functionList.stream().collect(Collectors.groupingBy(VarProcessFunction::getFunctionType));
    }

    @Override
    public Set<Long> findUsedFunctions(Long spaceId) {
        return varProcessFunctionReferenceMapper.findUsedFunctions(spaceId);
    }
}
