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
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.engine.IComponentDataBuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * build var component data
 * </p>
 *
 * @author chimeng
 * @since 2023/8/7
 */

@Slf4j
@Service
public class VarComponentDataBuildServiceImpl implements IComponentDataBuildService {

    @Resource
    private VarProcessVariableService varProcessVariableService;

    @Override
    public void buildComponentData(VarCompileData data, TestVariableTypeEnum type, VarProcessSpace space, Long componentId) {

        if (TestVariableTypeEnum.VAR != type) {
            log.info("type is not var: {}", type);
            return;
        }
        VarProcessVariable variable = varProcessVariableService.getOne(Wrappers.<VarProcessVariable>lambdaQuery()
                .select(VarProcessVariable::getId, VarProcessVariable::getVersion, VarProcessVariable::getIdentifier, VarProcessVariable::getLabel, VarProcessVariable::getName, VarProcessVariable::getDataType, VarProcessVariable::getStatus)
                .eq(VarProcessVariable::getId, componentId));
        data.setVarId(variable.getId());
        data.setChangeNum(variable.getVersion());
        data.setIdentifier(variable.getIdentifier());
        data.setName(variable.getLabel());
        data.setEnName(variable.getName());
        data.setSpaceId(space.getId());
        data.setSpaceCode(space.getCode());
        data.setReturnType(variable.getDataType());
        data.setType(VarTypeEnum.VAR);
        data.setVarStatus(Objects.equals(VariableStatusEnum.EDIT, variable.getStatus()) ? VarStatusEnum.CHECK_OUT : VarStatusEnum.CHECK_IN);
    }

}
