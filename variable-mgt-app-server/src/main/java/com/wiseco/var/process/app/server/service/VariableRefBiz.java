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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceListRestOutputDto;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestFunctionService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestInternalService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestOutsideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: wangxianli
 */
@Service
public class VariableRefBiz {

    @Autowired
    private VarProcessVariableService varProcessVariableService;

    @Autowired
    private VarProcessVariableReferenceService varProcessVariableReferenceService;

    @Autowired
    private VarProcessVariableFunctionService varProcessVariableFunctionService;

    @Autowired
    private VarProcessFunctionService varProcessFunctionService;

    @Autowired
    private VarProcessManifestOutsideService varProcessManifestOutsideServiceService;

    @Autowired
    private VarProcessFunctionReferenceService varProcessFunctionReferenceService;

    @Autowired
    private VarProcessVariableExceptionService varProcessVariableExceptionValueService;

    @Autowired
    private VarProcessFunctionExceptionService varProcessFunctionExceptionValueService;

    @Autowired
    private VarProcessManifestFunctionService varProcessManifestFunctionService;

    @Autowired
    private VarProcessManifestInternalService varProcessManifestInternalDataService;

    @Autowired
    private VarProcessCompileVarService varProcessCompileVarService;

    @Resource
    private OutsideService outsideService;

    /**
     * saveVariableReference
     *
     * @param spaceId spaceId
     * @param variableId variableId
     * @param vars vars
     */
    public void saveVariableReference(Long spaceId, Long variableId, Set<String> vars) {
        varProcessVariableReferenceService.remove(new QueryWrapper<VarProcessVariableReference>().lambda()
                .eq(VarProcessVariableReference::getUseByVariableId, variableId)
        );
        if (CollectionUtils.isEmpty(vars)) {
            return;
        }

        List<VarProcessVariableReference> list = new ArrayList<>();

        List<String> codeList = new ArrayList<>(vars);
        List<VarProcessVariable> variableList = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(VarProcessVariable::getIdentifier, VarProcessVariable::getId)
                        .in(VarProcessVariable::getIdentifier, codeList)
                        .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .orderByDesc(VarProcessVariable::getVersion)
        );
        Map<String, List<VarProcessVariable>> variableMap = variableList.stream().collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));
        Set<Map.Entry<String, List<VarProcessVariable>>> entries = variableMap.entrySet();

        for (Map.Entry<String, List<VarProcessVariable>> entry : entries) {
            VarProcessVariable variable = entry.getValue().get(0);
            list.add(VarProcessVariableReference.builder()
                    .varProcessSpaceId(spaceId)
                    .variableId(variable.getId())
                    .useByVariableId(variableId)
                    .build());
        }

        varProcessVariableReferenceService.saveBatch(list);
    }

    /**
     * saveVariableRefFunction
     *
     * @param spaceId spaceId
     * @param variableId variableId
     * @param functionList functionList
     */
    public void saveVariableRefFunction(Long spaceId, Long variableId, Set<String> functionList) {

        varProcessVariableFunctionService.remove(new QueryWrapper<VarProcessVariableFunction>().lambda()
                .eq(VarProcessVariableFunction::getVariableId, variableId)
        );
        if (CollectionUtils.isEmpty(functionList)) {
            return;
        }

        List<VarProcessVariableFunction> list = new ArrayList<>();

        List<String> codeList = new ArrayList<>(functionList);
        List<VarProcessFunction> functions = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(
                                VarProcessFunction::getId,
                                VarProcessFunction::getVarProcessSpaceId,
                                VarProcessFunction::getIdentifier,
                                VarProcessFunction::getFunctionType,
                                VarProcessFunction::getName,
                                VarProcessFunction::getPrepObjectName,
                                VarProcessFunction::getStatus,
                                VarProcessFunction::getFunctionDataType
                        )
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .in(VarProcessFunction::getIdentifier, codeList));

        functions.forEach(out -> list.add(VarProcessVariableFunction.builder()
                .varProcessSpaceId(spaceId)
                .variableId(variableId)
                .functionId(out.getId())
                .build()));

        varProcessVariableFunctionService.saveBatch(list);
    }

    /**
     * saveFunctionRef
     *
     * @param spaceId 变量空间Id
     * @param functionId 公共函数Id
     * @param functionList 公共函数列表
     */
    public void saveFunctionRef(Long spaceId, Long functionId, Set<String> functionList) {

        varProcessFunctionReferenceService.remove(new QueryWrapper<VarProcessFunctionReference>().lambda()
                .eq(VarProcessFunctionReference::getUseByFunctionId, functionId)
        );
        if (CollectionUtils.isEmpty(functionList)) {
            return;
        }

        List<VarProcessFunctionReference> list = new ArrayList<>();

        List<String> codeList = new ArrayList<>(functionList);
        List<VarProcessFunction> functions = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(
                                VarProcessFunction::getId,
                                VarProcessFunction::getVarProcessSpaceId,
                                VarProcessFunction::getIdentifier,
                                VarProcessFunction::getFunctionType,
                                VarProcessFunction::getName,
                                VarProcessFunction::getPrepObjectName,
                                VarProcessFunction::getStatus,
                                VarProcessFunction::getFunctionDataType
                        )
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .in(VarProcessFunction::getIdentifier, codeList));

        functions.forEach(out -> list.add(VarProcessFunctionReference.builder()
                .varProcessSpaceId(spaceId)
                .useByFunctionId(functionId)
                .functionId(out.getId())
                .createdUser(SessionContext.getSessionUser().getUsername())
                .build()));

        varProcessFunctionReferenceService.saveBatch(list);
    }

    /**
     * saveVariableExceptionValue
     *
     * @param spaceId spaceId
     * @param variableId variableId
     * @param exceptionList exceptionList
     */
    public void saveVariableExceptionValue(Long spaceId, Long variableId, Set<String> exceptionList) {
        varProcessVariableExceptionValueService.remove(
                new QueryWrapper<VarProcessVariableExcept>().lambda()
                        .eq(VarProcessVariableExcept::getVariableId, variableId)
        );

        if (CollectionUtils.isEmpty(exceptionList)) {
            return;
        }
        List<VarProcessVariableExcept> list = new ArrayList<>();
        for (String val : exceptionList) {
            list.add(
                    VarProcessVariableExcept.builder()
                            .varProcessSpaceId(spaceId)
                            .variableId(variableId)
                            .exceptionValueCode(val)
                            .createdUser(SessionContext.getSessionUser().getUsername())
                            .build()
            );
        }
        varProcessVariableExceptionValueService.saveBatch(list);

    }

    /**
     * saveFunctionExceptionValue
     *
     * @param spaceId spaceId
     * @param functionId functionId
     * @param exceptionList exceptionList
     */
    public void saveFunctionExceptionValue(Long spaceId, Long functionId, Set<String> exceptionList) {
        varProcessFunctionExceptionValueService.remove(
                new QueryWrapper<VarProcessFunctionExcept>().lambda()
                        .eq(VarProcessFunctionExcept::getFunctionId, functionId)
        );

        if (CollectionUtils.isEmpty(exceptionList)) {
            return;
        }
        List<VarProcessFunctionExcept> list = new ArrayList<>();
        for (String val : exceptionList) {
            list.add(
                    VarProcessFunctionExcept.builder()
                            .varProcessSpaceId(spaceId)
                            .functionId(functionId)
                            .exceptionValueCode(val)
                            .createdUser(SessionContext.getSessionUser().getUsername())
                            .build()
            );
        }
        varProcessFunctionExceptionValueService.saveBatch(list);

    }

    /**
     * saveManifestOutsideService
     *
     * @param externalServiceInfos 外部服务编码 list
     * @param spaceId      空间id
     * @param manifestId   清单id
     */
    public void saveManifestOutsideService(Set<VarSyntaxInfo.ExternalServiceInfo> externalServiceInfos, Long spaceId, Long manifestId) {
        varProcessManifestOutsideServiceService.remove(
                new QueryWrapper<VarProcessManifestOutside>().lambda()
                        .eq(VarProcessManifestOutside::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(externalServiceInfos)) {
            return;
        }

        Map<String, String> codeAuthCodeMap = externalServiceInfos.stream().collect(Collectors.toMap(VarSyntaxInfo.ExternalServiceInfo::getServiceCode, VarSyntaxInfo.ExternalServiceInfo::getAuthCode, (k1, k2) -> k2));
        List<String> codeList = new ArrayList<>(codeAuthCodeMap.keySet());

        List<OutsideServiceListRestOutputDto> outsideServiceList = outsideService.findOutsideServiceByCodes(codeList);

        List<VarProcessManifestOutside> list = new ArrayList<>();
        for (OutsideServiceListRestOutputDto outsideServiceDto : outsideServiceList) {
            list.add(
                    VarProcessManifestOutside.builder()
                            .varProcessSpaceId(spaceId)
                            .manifestId(manifestId)
                            .outsideServiceId(outsideServiceDto.getId())
                            .outsideAuthCode(codeAuthCodeMap.get(outsideServiceDto.getCode()))
                            .createdUser(SessionContext.getSessionUser().getUsername())
                            .build()
            );

        }
        if (!CollectionUtils.isEmpty(list)) {
            varProcessManifestOutsideServiceService.saveBatch(list);
        }
    }

    /**
     * saveManifestFunction
     *
     * @param identifiers identifiers
     * @param spaceId spaceId
     * @param manifestId manifestId
     */
    public void saveManifestFunction(Set<String> identifiers, Long spaceId, Long manifestId) {
        varProcessManifestFunctionService.remove(
                new QueryWrapper<VarProcessManifestFunction>().lambda()
                        .eq(VarProcessManifestFunction::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(identifiers)) {
            return;
        }
        List<VarProcessFunction> functionList = varProcessFunctionService.list(
                new QueryWrapper<VarProcessFunction>().lambda()
                        .select(
                                VarProcessFunction::getId,
                                VarProcessFunction::getVarProcessSpaceId,
                                VarProcessFunction::getIdentifier,
                                VarProcessFunction::getFunctionType,
                                VarProcessFunction::getName,
                                VarProcessFunction::getPrepObjectName,
                                VarProcessFunction::getStatus,
                                VarProcessFunction::getFunctionDataType
                        )
                        .eq(VarProcessFunction::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .in(VarProcessFunction::getIdentifier, new ArrayList<>(identifiers))
        );

        List<VarProcessManifestFunction> list = new ArrayList<>();
        for (VarProcessFunction function : functionList) {
            VarProcessManifestFunction build = VarProcessManifestFunction.builder()
                    .varProcessSpaceId(spaceId)
                    .manifestId(manifestId)
                    .identifier(function.getIdentifier())
                    .version(1)
                    .createdUser(SessionContext.getSessionUser().getUsername())
                    .build();
            list.add(build);
        }
        varProcessManifestFunctionService.saveBatch(list);

    }

    /**
     * saveManifestInternalData
     *
     * @param identifiers identifiers
     * @param spaceId spaceId
     * @param manifestId manifestId
     */
    public void saveManifestInternalData(Set<String> identifiers, Long spaceId, Long manifestId) {
        varProcessManifestInternalDataService.remove(
                new QueryWrapper<VarProcessManifestInternal>().lambda()
                        .eq(VarProcessManifestInternal::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(identifiers)) {
            return;
        }

        List<VarProcessManifestInternal> list = new ArrayList<>();
        for (String id : identifiers) {
            VarProcessManifestInternal build = VarProcessManifestInternal.builder()
                    .varProcessSpaceId(spaceId)
                    .manifestId(manifestId)
                    .identifier(id)
                    .createdUser(SessionContext.getSessionUser().getUsername())
                    .build();
            list.add(build);
        }
        varProcessManifestInternalDataService.saveBatch(list);

    }

    /**
     * 保存组件编译使用变量组件信息
     * @param spaceId 空间id
     * @param invokId 调用上游组件id
     * @param varTypeEnum 组件类型
     * @param varProcessCompileVars 使用组件变量信息列表
     */
    public void saveCompileVar(Long spaceId, Long invokId, VarTypeEnum varTypeEnum, List<VarProcessCompileVar> varProcessCompileVars) {
        varProcessCompileVarService.remove(
                new QueryWrapper<VarProcessCompileVar>().lambda()
                        .eq(VarProcessCompileVar::getInvokId, invokId)
                        .eq(VarProcessCompileVar::getInvokType, varTypeEnum.name())
        );
        if (CollectionUtils.isEmpty(varProcessCompileVars)) {
            return;
        }
        varProcessCompileVarService.saveBatch(varProcessCompileVars);
    }
}
