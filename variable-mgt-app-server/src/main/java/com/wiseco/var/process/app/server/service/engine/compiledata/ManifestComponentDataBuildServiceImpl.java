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
import com.google.common.collect.Sets;
import com.wiseco.decision.engine.var.enums.VarFunctionSubTypeEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VariableDataProviderBiz;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.engine.IComponentDataBuildService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
public class ManifestComponentDataBuildServiceImpl implements IComponentDataBuildService {

    private final Map<FunctionTypeEnum, VarFunctionSubTypeEnum> functionTypes = Maps.newHashMap();
    @Resource
    private VarProcessManifestService varProcessManifestService;
    @Resource
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Resource
    private VarProcessFunctionService varProcessFunctionService;
    @Resource
    private VariableDataProviderBiz variableDataProviderBiz;

    @PostConstruct
    void init() {
        functionTypes.put(FunctionTypeEnum.TEMPLATE, VarFunctionSubTypeEnum.VAR_TEMPLATE);
        functionTypes.put(FunctionTypeEnum.FUNCTION, VarFunctionSubTypeEnum.PUBLIC_METHOD);
        functionTypes.put(FunctionTypeEnum.PREP, VarFunctionSubTypeEnum.PRE_PROCESS);
    }

    @Override
    public void buildComponentData(VarCompileData data, TestVariableTypeEnum type, VarProcessSpace space, Long componentId) {

        if (TestVariableTypeEnum.MANIFEST != type) {
            log.info("type is not manifest: {}", type);
            return;
        }
        VarProcessManifest manifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getId, VarProcessManifest::getVersion, VarProcessManifest::getVarProcessSpaceId)
                .eq(VarProcessManifest::getId, componentId));
        data.setVarId(manifest.getId());
        data.setChangeNum(manifest.getVersion());
        data.setIdentifier(String.valueOf(manifest.getId()));
        data.setSpaceId(manifest.getVarProcessSpaceId());
        data.setSpaceCode(space.getCode());
        data.setType(VarTypeEnum.MAINFLOW);
        Set<VarCompileData> varContents = Sets.newHashSet();
        varContents.addAll(buildManifest2VarData(space.getId(), componentId, space.getCode()));
        varContents.addAll(buildCommonFunctionData(space.getId(), space.getCode()));
        data.setVarContents(varContents);
    }

    /**
     * buildManifest2VarData
     *
     * @param spaceId
     * @param componentId
     * @param spaceCode
     * @return java.util.Set<com.wiseco.decision.engine.var.transform.component.data.VarCompileData>
     */
    private Set<VarCompileData> buildManifest2VarData(Long spaceId, Long componentId, String spaceCode) {

        List<VarProcessVariableDto> variables = varProcessManifestVariableService.getVariableListByManifestId(spaceId, componentId);
        return variables.stream().map(variable ->
                VarCompileData.builder()
                        .varId(variable.getId())
                        .identifier(variable.getIdentifier())
                        .changeNum(variable.getVersion())
                        .varStatus(VarStatusEnum.CHECK_IN)
                        .name(variable.getLabel())
                        .enName(variable.getName())
                        .returnType(variable.getDataType())
                        .type(VarTypeEnum.VAR)
                        .spaceId(spaceId)
                        .spaceCode(spaceCode)
                        .content(variable.getContent())
                        .javaCls(obtainJavaCls(variable.getClassData(), VarTypeEnum.VAR, spaceId, variable.getId(), variable.getContent()))
                        .build()
        ).collect(Collectors.toSet());

    }

    private Set<VarCompileData> buildCommonFunctionData(Long spaceId, String spaceCode) {

        List<VarProcessFunctionDto> functionList = varProcessFunctionService.getFunctionListBySpaceId(spaceId);
        // 返回结果
        return functionList.stream().map(function ->
                VarCompileData.builder()
                        .varId(function.getId())
                        .identifier(function.getIdentifier())
                        // 目前没有版本,默认1
                        .changeNum(1)
                        .varStatus(VarStatusEnum.CHECK_IN)
                        .name(function.getName())
                        .returnType(function.getFunctionDataType())
                        .type(VarTypeEnum.FUNCTION)
                        .varFunctionSubType(functionTypes.get(function.getFunctionType()))
                        .spaceId(spaceId)
                        .spaceCode(spaceCode)
                        .content(function.getContent())
                        .javaCls(obtainJavaCls(function.getClassData(), VarTypeEnum.FUNCTION, spaceId, function.getId(), function.getContent()))
                        .build()
        ).collect(Collectors.toSet());
    }

    private String obtainJavaCls(String classData, VarTypeEnum type, Long spaceId, Long componentId, String content) {
        if (StringUtils.isEmpty(classData)) {
            return variableDataProviderBiz.saveClassData(type, spaceId, componentId, content);
        }
        return classData;
    }
}
