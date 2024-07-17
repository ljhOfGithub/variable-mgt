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
package com.wiseco.var.process.app.server.service.engine.compiledata;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.wiseco.decision.engine.var.enums.VarFunctionSubTypeEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.engine.IComponentDataBuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * build function component data
 * </p>
 *
 * @author chimeng
 * @since 2023/8/7
 */

@Slf4j
@Service
public class FunctionComponentDataBuildServiceImpl implements IComponentDataBuildService {

    private final Map<FunctionTypeEnum, VarFunctionSubTypeEnum> functionTypes = Maps.newHashMap();
    @Resource
    private VarProcessFunctionService varProcessFunctionService;

    @PostConstruct
    void init() {
        functionTypes.put(FunctionTypeEnum.TEMPLATE, VarFunctionSubTypeEnum.VAR_TEMPLATE);
        functionTypes.put(FunctionTypeEnum.FUNCTION, VarFunctionSubTypeEnum.PUBLIC_METHOD);
        functionTypes.put(FunctionTypeEnum.PREP, VarFunctionSubTypeEnum.PRE_PROCESS);
    }

    @Override
    public void buildComponentData(VarCompileData data, TestVariableTypeEnum type, VarProcessSpace space, Long componentId) {

        if (TestVariableTypeEnum.FUNCTION != type) {
            log.info("type is not function: {}", type);
            return;
        }
        VarProcessFunction function = varProcessFunctionService.getOne(Wrappers.<VarProcessFunction>lambdaQuery()
                .select(VarProcessFunction::getId, VarProcessFunction::getIdentifier, VarProcessFunction::getName,
                        VarProcessFunction::getVarProcessSpaceId, VarProcessFunction::getFunctionDataType, VarProcessFunction::getStatus)
                .eq(VarProcessFunction::getId, componentId));
        data.setVarId(function.getId());
        data.setChangeNum(1);
        data.setIdentifier(function.getIdentifier());
        data.setName(function.getName());
        data.setSpaceId(function.getVarProcessSpaceId());
        data.setSpaceCode(space.getCode());
        data.setReturnType(function.getFunctionDataType());
        data.setType(VarTypeEnum.FUNCTION);
        data.setVarStatus(getVarStatus(function.getStatus()));
        data.setVarFunctionSubType(functionTypes.get(function.getFunctionType()));
    }

    private VarStatusEnum getVarStatus(FlowStatusEnum status) {

        return status == FlowStatusEnum.EDIT ? VarStatusEnum.CHECK_OUT : VarStatusEnum.CHECK_IN;
    }
}
