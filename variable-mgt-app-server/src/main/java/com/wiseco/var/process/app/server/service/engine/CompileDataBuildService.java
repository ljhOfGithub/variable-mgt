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
package com.wiseco.var.process.app.server.service.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.engine.var.enums.VarFunctionSubTypeEnum;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.engine.var.transform.enums.VarStatusEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessConfigDefaultService;
import com.wiseco.var.process.app.server.service.VarProcessConfigExceptionService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.VariableDataProviderBiz;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

/**
 * <p>
 * compose engine data
 * </p>
 *
 * @author chimeng
 * @since 2023/8/1
 */
@Slf4j
@Service
public class CompileDataBuildService {

    @Resource
    private VarProcessConfigExceptionService varProcessConfigExceptionValueService;
    @Resource
    private VarProcessConfigDefaultService varProcessConfigDefaultService;
    @Autowired
    private List<IComponentDataBuildService> componentDataBuildServices;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VariableDataProviderBiz variableDataProviderBiz;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;

    /**
     * 异常值配置组装
     *
     * @param data 编译代码数据
     */
    private void buildExceptionDefaultValue(VarCompileData data) {

        List<VarProcessConfigExcept> exceptionValueList = varProcessConfigExceptionValueService.list();
        Map<String, String> exceptionDefaultValueLst = exceptionValueList.stream()
                .collect(Collectors.toMap(VarProcessConfigExcept::getExceptionValueCode, VarProcessConfigExcept::getExceptionValue, (v1, v2) -> v2));

        data.setExceptionDefaultValueLst(exceptionDefaultValueLst);
    }

    /**
     * 基础数据类型默认值设置
     *
     * @param data    编译代码数据
     * @param spaceId 空间ID
     */
    private void buildTypeDefaultValue(VarCompileData data, Long spaceId) {

        List<VarProcessConfigDefault> defaultValueList = varProcessConfigDefaultService.list(
                Wrappers.<VarProcessConfigDefault>lambdaQuery().eq(VarProcessConfigDefault::getVarProcessSpaceId, spaceId));
        Map<String, String> normalDefaultValueLst = defaultValueList.stream()
                .filter(f -> !StringUtils.isEmpty(f.getDefaultValue()))
                .collect(Collectors.toMap(VarProcessConfigDefault::getDataType, VarProcessConfigDefault::getDefaultValue));

        data.setNormalDefaultValueLst(normalDefaultValueLst);
    }

    /**
     * 组件数据组装
     *
     * @param data        编译数据
     * @param type        测试的组件类别
     * @param space       空间
     * @param componentId 组件ID
     */
    private void buildComponentData(VarCompileData data,
                                    TestVariableTypeEnum type,
                                    VarProcessSpace space,
                                    Long componentId) {
        componentDataBuildServices.forEach(f -> f.buildComponentData(data, type, space, componentId));
    }

    /**
     * 编译
     *
     * @param space 变量空间
     * @param type 变量测试类型
     * @param componentId 组件Id
     * @param content 内容
     * @return 变量编译后的结果
     */
    public VarCompileData build(VarProcessSpace space, TestVariableTypeEnum type, Long componentId, String content) {

        VarCompileData data = new VarCompileData();
        data.setContent(content);
        // 异常默认值
        buildExceptionDefaultValue(data);
        // 类型默认值
        buildTypeDefaultValue(data, space.getId());
        // 组件数据
        buildComponentData(data, type, space, componentId);

        return data;
    }


    /**
     * 组装数据模型变量平铺map
     * @param spaceId 空间id
     * @param rawContent raw数据模型
     * @param internalContent 内部数据源数据模型
     * @param externalContent 外数数据模型
     * @return 平铺的数据模型信息
     */
    public Map<String, DomainDataModelTreeDto> getStrategyPropertyEditorMap(Long spaceId, String rawContent, String internalContent, String externalContent) {

        Map<String, DomainDataModelTreeDto> strategyPropertyEditorMap = new HashMap<>(MagicNumbers.THOUSAND_AND_TWENTY_FOUR);
        //rowData
        if (rawContent != null) {
            strategyPropertyEditorMap.putAll(DomainModelTreeEntityUtils.getDataModelTreeMapByConent(rawContent));
        }
        //internal
        if (internalContent != null) {
            strategyPropertyEditorMap.putAll(DomainModelTreeEntityUtils.getDataModelTreeMapByConent(internalContent));
        }
        // external
        if (externalContent != null) {
            strategyPropertyEditorMap.putAll(DomainModelTreeEntityUtils.getDataModelTreeMapByConent(externalContent));
        }

        //组装vars变量
        fillVarsMap(spaceId, strategyPropertyEditorMap);
        return strategyPropertyEditorMap;
    }

    private void fillVarsMap(Long spaceId, Map<String, DomainDataModelTreeDto> strategyPropertyEditorMap) {
        //获取已上架的变量清单
        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        List<VarProcessVariable> varList = fillVariableList(spaceId, roleDataAuthorityDTO);

        if (CollectionUtils.isEmpty(varList)) {
            return;
        }
        varList.stream().forEach(var -> {
            String varPath = MessageFormat.format("vars.{0}", var.getName());
            strategyPropertyEditorMap.put(varPath, DomainDataModelTreeDto.builder()
                    .isUse("0")
                    .isEmpty("0")
                    .name(var.getName())
                    .describe(var.getLabel())
                    .isArr("0")
                    .label(var.getName() + "-" + var.getLabel())
                    .type(var.getDataType())
                    .value(varPath)
                    .identifier(var.getIdentifier())
                    .build());
        });
    }


    /**
     * 查询已上架的最大版本的变量信息
     *
     * @param spaceId 变量空间Id
     * @param roleDataAuthority 用户的数据权限DTO
     * @return 上架的最大版本的变量信息
     */
    private List<VarProcessVariable> fillVariableList(Long spaceId, RoleDataAuthorityDTO roleDataAuthority) {
        if (roleDataAuthority == null) {
            return null;
        }
        List<VarProcessVariable> varList = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda()
                .select(
                        VarProcessVariable::getId,
                        VarProcessVariable::getVarProcessSpaceId,
                        VarProcessVariable::getCategoryId,
                        VarProcessVariable::getIdentifier,
                        VarProcessVariable::getName,
                        VarProcessVariable::getLabel,
                        VarProcessVariable::getDataType,
                        VarProcessVariable::getVersion
                )
                .eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                .eq(VarProcessVariable::getStatus, VariableStatusEnum.UP)
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()),
                        VarProcessVariable::getDeptCode, roleDataAuthority.getDeptCodes())
                .in(!ALL_PERMISSION.equals(roleDataAuthority.getType()) && !CollectionUtils.isEmpty(roleDataAuthority.getUserNames()),
                        VarProcessVariable::getCreatedUser, roleDataAuthority.getUserNames()));

        if (CollectionUtils.isEmpty(varList)) {
            return null;
        }
        List<VarProcessVariable> maxVersionVarList = new ArrayList<>();
        Map<String, List<VarProcessVariable>> map = varList.stream().collect(Collectors.groupingBy(b -> b.getIdentifier()));
        for (Map.Entry<String, List<VarProcessVariable>> entry : map.entrySet()) {
            List<VarProcessVariable> varProcessVariables = map.get(entry.getKey());
            maxVersionVarList.add(varProcessVariables.stream().max(Comparator.comparing(VarProcessVariable::getVersion)).get());
        }
        return maxVersionVarList;
    }


    /**
     * 组装依赖其他变量信息
     * @param type 当前组件类型
     * @param spaceCode 变量空间code
     * @param variableId 组件id
     * @param spaceId 空间id
     * @param checkContent 组件信息
     * @param varCompileData 编译使用上下文
     */
    public void assembleCompileData(TestVariableTypeEnum type, String spaceCode, Long variableId, Long spaceId, String checkContent, VarCompileData varCompileData) {
        log.info("变量编译入参 variableId:{},compileData:{}", variableId, varCompileData);
        Set<VarCompileData> varContents = new HashSet<>();
        if (type.equals(TestVariableTypeEnum.MANIFEST)) {
            varCompileData.setInterfaceId(variableId);
            varContents = getVarContents(spaceCode, variableId, spaceId);
        }
        List<VarProcessFunctionDto> functionList = varProcessFunctionService.getFunctionListBySpaceId(spaceId);
        if (!CollectionUtils.isEmpty(functionList)) {
            for (VarProcessFunctionDto function : functionList) {
                String curContent = function.getId().equals(variableId) ? checkContent : function.getContent();
                VarFunctionSubTypeEnum varFunctionSubType = getVarFunctionSubTypeEnum(function);
                VarCompileData data = VarCompileData.builder().varId(function.getId()).identifier(function.getIdentifier()).changeNum(1).varStatus(VarStatusEnum.CHECK_IN)
                        .name(function.getName()).returnType(function.getFunctionDataType()).type(VarTypeEnum.FUNCTION).varFunctionSubType(varFunctionSubType)
                        .spaceId(spaceId).spaceCode(spaceCode).content(curContent).build();
                if (StringUtils.isEmpty(function.getClassData())) {
                    String classData = variableDataProviderBiz.saveClassData(VarTypeEnum.FUNCTION, spaceId, function.getId(), curContent);
                    data.setJavaCls(classData);
                } else {
                    data.setJavaCls(function.getClassData());
                }
                varContents.add(data);
            }
        }
        
        //补充空间内可用得变量数据
        List<VarProcessVariable> variableLst = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda().eq(VarProcessVariable::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (!CollectionUtils.isEmpty(variableLst)) {
            for (VarProcessVariable variable : variableLst) {
                varContents.add(VarCompileData.builder().varId(variable.getId()).identifier(variable.getIdentifier()).changeNum(variable.getVersion()).name(variable.getLabel())
                        .enName(variable.getName()).type(VarTypeEnum.VAR).spaceId(spaceId).content(variable.getContent()).returnType(variable.getDataType())
                        .build());
            }
        }
        
        
        varCompileData.setVarContents(varContents);
    }


    private static VarFunctionSubTypeEnum getVarFunctionSubTypeEnum(VarProcessFunctionDto function) {
        VarFunctionSubTypeEnum varFunctionSubType = null;
        if (function.getFunctionType() == FunctionTypeEnum.TEMPLATE) {
            varFunctionSubType = VarFunctionSubTypeEnum.VAR_TEMPLATE;
        } else if (function.getFunctionType() == FunctionTypeEnum.FUNCTION) {
            varFunctionSubType = VarFunctionSubTypeEnum.PUBLIC_METHOD;
        } else if (function.getFunctionType() == FunctionTypeEnum.PREP) {
            varFunctionSubType = VarFunctionSubTypeEnum.PRE_PROCESS;
        }
        return varFunctionSubType;
    }

    private Set<VarCompileData> getVarContents(String spaceCode, Long variableId, Long spaceId) {
        Set<VarCompileData> varContents = new HashSet<>();
        List<VarProcessVariableDto> variableList = varProcessManifestVariableService.getVariableListByManifestId(spaceId, variableId);
        for (VarProcessVariableDto variable : variableList) {
            VarCompileData data = VarCompileData.builder()
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
                    .build();
            if (StringUtils.isEmpty(variable.getClassData())) {
                String classData = variableDataProviderBiz.saveClassData(VarTypeEnum.VAR, spaceId, variable.getId(), variable.getContent());
                data.setJavaCls(classData);
            } else {
                data.setJavaCls(variable.getClassData());
            }
            varContents.add(data);
        }
        return varContents;
    }

}
