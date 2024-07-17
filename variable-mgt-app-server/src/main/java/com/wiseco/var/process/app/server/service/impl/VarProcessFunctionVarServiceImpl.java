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
import com.wiseco.var.process.app.server.repository.VarProcessFunctionVarMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.service.VarProcessFunctionVarService;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionUsageDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 公共函数-引用数据模型变量关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessFunctionVarServiceImpl extends ServiceImpl<VarProcessFunctionVarMapper, VarProcessFunctionVar> implements
        VarProcessFunctionVarService {

    @Autowired
    private VarProcessFunctionVarMapper varProcessFunctionVarMapper;

    @Override
    public List<VariableUseVarPathDto> getVarUseList(Long spaceId) {
        return varProcessFunctionVarMapper.getVarUseList(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getFunctionTempDataModelVarUseList(Long spaceId) {
        return varProcessFunctionVarMapper.getFunctionTempDataModelVarUseList(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getFunctionPrepDataModelVarUseList(Long spaceId) {
        return varProcessFunctionVarMapper.getFunctionPrepDataModelVarUseList(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getFunctionFunctionDataModelVarUseList(Long spaceId) {
        return varProcessFunctionVarMapper.getFunctionFunctionDataModelVarUseList(spaceId);
    }

    @Override
    public List<VariableUseVarPathDto> getVarFunctionPrepUseList(String objectName) {
        return varProcessFunctionVarMapper.getVarFunctionPrepUseList(objectName);
    }

    @Override
    public List<VariableFunctionUsageDto> getPreProcessLogicAndProcessedExtendedProperties(Long spaceId) {
        return varProcessFunctionVarMapper.getPreProcessLogicProcessedExtendedProperties(spaceId);
    }
}
