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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPrepInputDto;
import com.wiseco.var.process.app.server.repository.VarProcessFunctionMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.dto.FunctionDetailDto;
import com.wiseco.var.process.app.server.service.dto.FunctionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionPrepDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 公共函数表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessFunctionServiceImpl extends ServiceImpl<VarProcessFunctionMapper, VarProcessFunction> implements VarProcessFunctionService {
    @Autowired
    private VarProcessFunctionMapper varProcessFunctionMapper;

    @Override
    public IPage<FunctionDetailDto> findFunctionList(IPage page, FunctionQueryDto queryDto) {
        return varProcessFunctionMapper.findFunctionList(page, queryDto);
    }

    @Override
    public List<VariableFunctionPrepDto> getPrepList(VariableManifestFlowPrepInputDto inputDto, List<String> mappingObjectList, String sortedKey,
                                                     String sortMethod) {
        String orderKey = sortedKey + " " + sortMethod;
        return varProcessFunctionMapper.getPrepList(inputDto, mappingObjectList, orderKey);
    }

    @Override
    public List<VarProcessFunction> getUseableOnlineFunction(Long spaceId) {
        return varProcessFunctionMapper.getUseableOnlineFunction(spaceId);
    }

    @Override
    public List<VarProcessFunctionDto> getFunctionListBySpaceId(Long spaceId) {
        return varProcessFunctionMapper.getFunctionListBySpaceId(spaceId);
    }

    @Override
    public List<VarProcessFunctionDto> selectFunctions(Long spaceId, String functionType, String functionStatus, List<String> deptCodes, List<String> userNames) {
        return varProcessFunctionMapper.selectFunctions(spaceId, functionType, functionStatus, deptCodes,userNames);
    }

    @Override
    public List<VarProcessFunctionDto> selectFunctionsNew(Long spaceId, String functionType, String functionStatus, String functionDataType, List<String> deptCodes, List<String> userNames) {
        return varProcessFunctionMapper.selectFunctionsNew(spaceId, functionType, functionStatus, functionDataType,deptCodes,userNames);
    }

}
