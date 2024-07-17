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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.boot.user.DepartmentClient;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceManifestMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.OutParamsInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.SerialNoLinkableDataModelInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListCriteria;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceParamsDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestAndDataModelInfoVo;
import com.wiseco.var.process.app.server.controller.vo.output.OutSideParamsOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceAuthOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VarProcessServiceDto;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorization;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorizationService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.converter.VariableDataModelConverter;
import com.wiseco.var.process.app.server.service.dto.ManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.OutParamsQueryDto;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.DEFAULT_SPACE_ID;

/**
 * 实时服务 业务实现
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */
@Slf4j
@Service
public class VariableServiceBiz extends ServiceBiz {
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;
    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VariableDataModelConverter variableDataModelConverter;
    @Resource
    private DepartmentClient departmentClient;
    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private VarProcessAuthorizationServiceService varProcessAuthorizationServiceService;
    @Autowired
    private DmAdapter dmAdapter;

    /**
     * getSerialNoLinkableDataModelTree
     * @param inputDto 入参
     * @return DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getSerialNoLinkableDataModelTree(SerialNoLinkableDataModelInputVo inputDto) {
        // 不存在数据模型绑定配置: 无可用流水号绑定变量, 返回空数据模型树形结构
        if (CollectionUtils.isEmpty(inputDto.getDataModelBinding())) {
            return new DomainDataModelTreeDto();
        }
        Map<String, Integer> modelNameVersionMap = inputDto.getDataModelBinding().stream().collect(Collectors.toMap(SerialNoLinkableDataModelInputVo.DataModelBindingDto::getName, SerialNoLinkableDataModelInputVo.DataModelBindingDto::getVersion, (k1, k2) -> k1));
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery().eq(VarProcessDataModel::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessDataModel::getObjectSourceType, VarProcessDataModelSourceType.OUTSIDE_PARAM).in(VarProcessDataModel::getObjectName, modelNameVersionMap.keySet()));
        List<VarProcessDataModel> bindingDataModelList = new LinkedList<>();
        for (VarProcessDataModel dataModel : dataModelList) {
            String dataModelObjectName = dataModel.getObjectName();
            // 数据模型对象版本
            int dataModelObjectVersion = dataModel.getVersion();
            // 数据模型绑定配置指定对象版本
            int bindingObjectVersion = modelNameVersionMap.get(dataModelObjectName);
            if (dataModelObjectVersion == bindingObjectVersion) {
                // 版本一致: 添加到绑定数据模型 List
                bindingDataModelList.add(dataModel);
            }
        }
        // 组装数据模型, 获得 rawData JSON Schema
        JSONObject rawDataJsonObject = variableDataModelConverter.dataModelObjectToTree(bindingDataModelList, null);
        // 转换 JSON Schema 为树形结构
        DomainModelTree rawDataDomainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(rawDataJsonObject);
        // 移除树形结构数组类型项目及扩展数据
        removeDomainModelTreeArrayTypedItem(rawDataDomainModelTree);
        removeDomainModelTreeExtendedData(rawDataDomainModelTree);
        // 转换树形结构至前端展示 DTO
        DomainDataModelTreeDto rawDataDomainDataModelTreeDto = new DomainDataModelTreeDto();
        DomainModelTreeEntityUtils.beanCopyDomainDataModelTreeDto(rawDataDomainModelTree, rawDataDomainDataModelTreeDto);
        // 按照调用流水号支持的数据类型 (string, int & double) 筛选数据模型
        List<String> externalSerialNoLinkableDataTypeList = Arrays.asList("string", "int", "double");
        return DomainModelTreeEntityUtils.beanCopyDynamicTreeOutputDtoByTypeList(rawDataDomainDataModelTreeDto, externalSerialNoLinkableDataTypeList);
    }

    /**
     * 移除指定树形结构实体数组类型项目
     * @param domainModelTree 决策领域树形结构实体
     */
    private void removeDomainModelTreeArrayTypedItem(DomainModelTree domainModelTree) {
        if (CollectionUtils.isEmpty(domainModelTree.getChildren())) {
            // 边界条件: 树形结构实体没有子类
            return;
        }
        for (Iterator<DomainModelTree> childrenIterator = domainModelTree.getChildren().listIterator(); childrenIterator.hasNext(); ) {
            DomainModelTree child = childrenIterator.next();
            if ("1".equals(child.getIsArr())) {
                childrenIterator.remove();
            } else if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(child.getType())) {
                // 对象类型: 递归调用
                removeDomainModelTreeArrayTypedItem(child);
            }
        }
    }

    /**
     * 移除指定树形结构实体扩展数据
     * @param domainModelTree 决策领域树形结构实体
     */
    private void removeDomainModelTreeExtendedData(DomainModelTree domainModelTree) {
        if (CollectionUtils.isEmpty(domainModelTree.getChildren())) {
            // 边界条件: 树形结构实体没有子类
            return;
        }
        for (Iterator<DomainModelTree> childrenIterator = domainModelTree.getChildren().listIterator(); childrenIterator.hasNext(); ) {
            DomainModelTree child = childrenIterator.next();
            if ("1".equals(child.getIsExtend())) {
                childrenIterator.remove();
            } else if (DataVariableTypeEnum.OBJECT_TYPE.getMessage().equals(child.getType())) {
                // 对象类型: 递归调用
                removeDomainModelTreeExtendedData(child);
            }
        }
    }

    /**
     * 获取可选的外部入参对象列表
     * @param inputVo 入参
     * @return 入参对象分页结果
     */
    public IPage<OutSideParamsOutputVo> getOutParams(OutParamsInputVo inputVo) {
        dmAdapter.modifyGroupOptFlagOfConfigJdbc();
        Page<OutSideParamsOutputVo> pageConfig = new Page<>(inputVo.getCurrentNo(), inputVo.getSize());
        //将deptId转成code
        OutParamsQueryDto queryDto = new OutParamsQueryDto();
        BeanUtils.copyProperties(inputVo, queryDto);
        BeanUtils.copyProperties(authService.getRoleDataAuthority(),queryDto);
        if (!ObjectUtils.isEmpty(inputVo.getDeptId())) {
            List<DepartmentSmallDTO> smallByIds = departmentClient.findSmallByIds(Collections.singletonList(inputVo.getDeptId().intValue()));
            queryDto.setDeptCode(CollectionUtils.isEmpty(smallByIds) ? StringPool.EMPTY : smallByIds.get(0).getCode());
        }
        if (!CollectionUtils.isEmpty(inputVo.getExcludedParams())) {
            List<String> dataModelnames = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                    .select(VarProcessDataModel::getObjectName).in(VarProcessDataModel::getId, inputVo.getExcludedParams())).stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
            queryDto.setExcludedParams(dataModelnames);
        } else {
            queryDto.setExcludedParams(null);
        }
        return varProcessDataModelService.findParams(pageConfig, queryDto);
    }

    /**
     * 获取已选择的变量清单的详细信息及数据模型信息
     * @param manifests 清单id list
     * @param independentDataModelIds 入参
     * @return 详细信息
     */
    public ManifestAndDataModelInfoVo getManifestAndDataModelInfo(List<Long> manifests, List<Long> independentDataModelIds) {
        //获取清单详细信息（清单名称、描述及加工变量数）
        if (CollectionUtils.isEmpty(manifests)) {
            return null;
        }
        List<ServiceManifestDetailOutputVo> manifestDetails = varProcessServiceManifestService.getManifestDetail(manifests);
        // 获取新的数据模型相关信息
        List<ServiceDataModelMappingVo> newDataModels = new ArrayList<>();
        //获取手动添加的数据模型信息
        List<VarProcessDataModel> independentModels = new ArrayList<>();
        if (!CollectionUtils.isEmpty(independentDataModelIds)) {
            independentModels = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                    .select(VarProcessDataModel::getId, VarProcessDataModel::getObjectName, VarProcessDataModel::getObjectLabel, VarProcessDataModel::getVersion)
                    .in(VarProcessDataModel::getId, independentDataModelIds));
        }
        List<String> independentModelNames = independentModels.stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
        //获取依赖的数据模型信息
        List<VarProcessDataModel> dependentDataModels = varProcessManifestDataModelService.getDataModelInfos(manifests);
        List<String> dependentDataModelNames = dependentDataModels.stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
        List<ServiceDataModelMappingVo> newDependentDataModels = dependentDataModels.stream()
                .map(modelEntity -> new ServiceDataModelMappingVo(modelEntity.getId(), modelEntity.getObjectName(), modelEntity.getObjectLabel(), modelEntity.getVersion(), 1,
                        independentModelNames.contains(modelEntity.getObjectName()) ? 1 : 0)).collect(Collectors.toList());
        List<ServiceDataModelMappingVo> newIndependentDataModels = independentModels
                .stream()
                .filter(model -> !dependentDataModelNames.contains(model.getObjectName()))
                .map(modelEntity -> new ServiceDataModelMappingVo(modelEntity.getId(), modelEntity.getObjectName(), modelEntity.getObjectLabel(), modelEntity.getVersion(),
                        0, 1))
                .collect(Collectors.toList());
        newDataModels.addAll(newDependentDataModels);
        newDataModels.addAll(newIndependentDataModels);
        return new ManifestAndDataModelInfoVo(manifestDetails, newDataModels);
    }

    /**
     * getManifestByServiceId
     * @param versionId 服务版本id
     * @return List
     */
    public List<ServiceManifestMappingVo> getManifestByServiceId(Long versionId) {
        if (versionId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"请先输入serviceId");
        }
        VarProcessServiceVersion serviceVersionEntity = varProcessServiceVersionService.getById(versionId);
        if (null == serviceVersionEntity) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND,"请检查输入的服务id是否存在");
        }
        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, versionId));
        List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());
        return assembleServiceManifestMappingVos(versionId, serviceManifestList, manifestIds);
    }


    /**
     * 获取实时服务信息list(分页)
     * @param serviceListCriteria 分页查询条件
     * @return 服务list
     */
    public Page<VarProcessServiceDto> findServiceList(ServiceListCriteria serviceListCriteria) {
        Page<RestServiceListOutputVO> queryResult = varProcessServiceVersionService.findUpServicePage(serviceListCriteria);
        Page<VarProcessServiceDto> outputResult = new Page<>(serviceListCriteria.getCurrentNo(), serviceListCriteria.getSize());
        Map<Long,String> spaceMap = varProcessSpaceService.getIdNameMap();
        List<VarProcessServiceDto> outputDtos = new ArrayList<>();
        //tips：这里的serviceId 其实是版本id
        List<Long> serviceIds = queryResult.getRecords().stream().map(RestServiceListOutputVO::getServiceId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serviceIds)) {
            return outputResult;
        }
        List<VarProcessServiceManifest> manifests = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                        .select(VarProcessServiceManifest::getServiceId, VarProcessServiceManifest::getManifestId,
                                VarProcessServiceManifest::getInvalidTime,VarProcessServiceManifest::getValidTime,
                                VarProcessServiceManifest::getManifestRole)
                        .in(VarProcessServiceManifest::getServiceId, serviceIds)).stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Long> manifestIds = manifests.stream().map(VarProcessServiceManifest::getManifestId).collect(Collectors.toList());
        Map<Long, List<VarProcessServiceManifest>> serviceManifestMap = manifests.stream().collect(Collectors.groupingBy(VarProcessServiceManifest::getServiceId));
        // 入参信息
        List<Map<String, Object>> modelContents = varProcessManifestDataModelService.findDataModelContents(manifestIds);
        Map<Long,List<ServiceParamsDto.ParamTreeDto>> manifestModelMap = new HashMap<>(MagicNumbers.EIGHT);
        for (Map<String, Object> modelContent : modelContents) {
            String content = modelContent.get("content").toString();
            DomainDataModelTreeDto treeDto = DomainModelTreeEntityUtils.transferDataModelTreeDto(content, new HashSet<>());
            ServiceParamsDto.ParamTreeDto paramTreeDto = copyTreeProperties(treeDto);
            Long manifestId = Long.valueOf(modelContent.get("manifestId").toString());
            if (manifestModelMap.containsKey(manifestId)) {
                manifestModelMap.get(manifestId).add(paramTreeDto);
            } else {
                manifestModelMap.put(manifestId,new ArrayList<>(Collections.singletonList(paramTreeDto)));
            }
        }
        //出参信息
        Map<Long, List<ManifestVariableDto>> manifestVariableDtosMap = varProcessManifestVariableService.findmanifestVariables(manifestIds).stream().collect(Collectors.groupingBy(ManifestVariableDto::getManifestId));
        Map<Long, List<VarProcessVariable>> manifestVariablesMap = new HashMap<>(MagicNumbers.EIGHT);
        manifestVariableDtosMap.forEach((manifestId,variableDtoList) ->
                manifestVariablesMap.put(manifestId,variableDtoList.stream().map(dto -> {
                    VarProcessVariable varProcessVariable = new VarProcessVariable();
                    BeanUtils.copyProperties(dto,varProcessVariable);
                    return varProcessVariable;
                }).collect(Collectors.toList())));
        Map<Long,List<ServiceParamsDto.ParamTreeDto>> serviceReqParamMap = new HashMap<>(MagicNumbers.EIGHT);
        Map<Long,List<ServiceParamsDto.ParamTreeDto>> serviceRespParamMap = new HashMap<>(MagicNumbers.EIGHT);
        serviceManifestMap.forEach((serviceId,serviceManifests) -> {
            serviceManifests.forEach(manifest -> {
                Long manifestId = manifest.getManifestId();
                //入参信息
                List<ServiceParamsDto.ParamTreeDto> reqParamDtos = manifestModelMap.getOrDefault(manifestId,new ArrayList<>());
                if (serviceReqParamMap.containsKey(serviceId)) {
                    List<ServiceParamsDto.ParamTreeDto> paramTreeDtos = serviceReqParamMap.get(serviceId);
                    paramTreeDtos.addAll(reqParamDtos);
                    serviceReqParamMap.put(serviceId,paramTreeDtos.stream().distinct().collect(Collectors.toList()));
                } else {
                    List<ServiceParamsDto.ParamTreeDto> paramTreeDtos = new ArrayList<>(reqParamDtos);
                    serviceReqParamMap.put(serviceId,paramTreeDtos);
                }
            });
            //出参只展示当前生效主清单
            VarProcessServiceManifest mainManifest = serviceManifests.stream().filter(manifest ->
                    manifest.getManifestRole() == 1 && (manifest.getInvalidTime() == null || manifest.getInvalidTime().after(new Date())))
                    .min(Comparator.comparing(VarProcessServiceManifest::getValidTime)).orElse(null);
            if (mainManifest != null) {
                List<VarProcessVariable> variables = manifestVariablesMap.get(mainManifest.getManifestId());
                List<ServiceParamsDto.ParamTreeDto> respParamDtos = assembleRespParams(variables);
                serviceRespParamMap.put(serviceId,respParamDtos);
            }
        });
        queryResult.getRecords().forEach(service -> {
            VarProcessServiceDto outputDto = new VarProcessServiceDto();
            outputDto.setSpaceName(spaceMap.getOrDefault(DEFAULT_SPACE_ID,StringPool.EMPTY));
            outputDto.setServiceId(service.getServiceId());
            outputDto.setServiceName(service.getServiceName());
            outputDto.setServiceCode(service.getServiceCode());
            outputDto.setRequestStructure(serviceReqParamMap.get(service.getServiceId()));
            outputDto.setResponseStructure(serviceRespParamMap.get(service.getServiceId()));
            outputDtos.add(outputDto);
        });
        outputResult.setRecords(outputDtos);
        outputResult.setTotal(queryResult.getTotal());
        return outputResult;
    }

    /**
     * 获取服务入参出参信息
     * @param serviceCode 服务编码
     * @return 入参出参
     */
    public ServiceParamsDto getServiceParams(String serviceCode) {
        ServiceParamsDto serviceParamsDto = new ServiceParamsDto();
        // 1.请求参数
        // 拿到使用的数据模型list
        List<ServiceParamsDto.ParamTreeDto> requestTreeDtoList;
        Long enabledVersionId = varProcessServiceVersionService.findUpVersionIdByCode(serviceCode);
        List<Long> manifestIds = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, enabledVersionId)).stream()
                .filter(Objects::nonNull).map(VarProcessServiceManifest::getManifestId).collect(Collectors.toList());
        List<ServiceDataModelMappingVo> dataModelMappingVoList = serviceDataModelMappings(manifestIds);
        List<Long> modelIds = dataModelMappingVoList.stream().filter(Objects::nonNull).map(ServiceDataModelMappingVo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(modelIds)) {
            List<VarProcessDataModel> models = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery()
                    .in(VarProcessDataModel::getId, modelIds).eq(VarProcessDataModel::getObjectSourceType, VarProcessDataModelSourceType.OUTSIDE_PARAM));
            //将数据模型的content转换成树结构放入请求参数list
            requestTreeDtoList = models.stream().map(model -> {
                DomainDataModelTreeDto treeDto = DomainModelTreeEntityUtils.transferDataModelTreeDto(model.getContent(), new HashSet<>());
                return copyTreeProperties(treeDto);
            }).collect(Collectors.toList());
            serviceParamsDto.setRequestStructure(requestTreeDtoList);
        }
        //2.响应参数
        // 查询服务使用的变量 List
        List<VarProcessVariable> variableList = varProcessServiceManifestService.findManifestOutputVariableList(enabledVersionId);
        serviceParamsDto.setResponseStructure(assembleRespParams(variableList));
        return serviceParamsDto;
    }

    @NotNull
    private static List<ServiceParamsDto.ParamTreeDto> assembleRespParams(List<VarProcessVariable> variableList) {
        // 返回结果树形结构 List
        List<ServiceParamsDto.ParamTreeDto> responseTreeDtoList = new ArrayList<>(MagicNumbers.THREE);
        //接口状态interfaceQueryState
        ServiceParamsDto.ParamTreeDto interfaceQueryState = ServiceParamsDto.ParamTreeDto.builder().name("interfaceQueryState").describe("接口状态").type(DataVariableTypeEnum.STRING_TYPE.getMessage()).isArr("0").build();
        responseTreeDtoList.add(interfaceQueryState);
        // 返回结果响应码
        ServiceParamsDto.ParamTreeDto responseCode = ServiceParamsDto.ParamTreeDto.builder().name("code").describe("响应码").type(DataVariableTypeEnum.STRING_TYPE.getMessage()).isArr("0").build();
        responseTreeDtoList.add(responseCode);
        // 返回结果提示信息
        ServiceParamsDto.ParamTreeDto responseMessage = ServiceParamsDto.ParamTreeDto.builder().name("message").describe("提示信息").type(DataVariableTypeEnum.STRING_TYPE.getMessage()).isArr("0").build();
        responseTreeDtoList.add(responseMessage);
        // 返回结果数据
        ServiceParamsDto.ParamTreeDto responseData = ServiceParamsDto.ParamTreeDto.builder().name("data").describe("数据").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).isArr("0").build();
        responseTreeDtoList.add(responseData);
        List<ServiceParamsDto.ParamTreeDto> responseDataChildrenList = getResponseDataChildrenList(variableList);
        responseData.setChildren(responseDataChildrenList);
        return responseTreeDtoList;
    }

    @NotNull
    private static List<ServiceParamsDto.ParamTreeDto> getResponseDataChildrenList(List<VarProcessVariable> variableList) {
        List<ServiceParamsDto.ParamTreeDto> responseDataChildrenList = new LinkedList<>();
        if (variableList == null) {
            return responseDataChildrenList;
        }
        for (VarProcessVariable publishingVariable : variableList) {
            // 将待发布变量 List 添加到返回结果数据
            ServiceParamsDto.ParamTreeDto responseDataChild = ServiceParamsDto.ParamTreeDto.builder().name(publishingVariable.getName()).describe(publishingVariable.getLabel()).type(publishingVariable.getDataType()).isArr("0").build();
            responseDataChildrenList.add(responseDataChild);
        }
        return responseDataChildrenList;
    }

    private ServiceParamsDto.ParamTreeDto copyTreeProperties(DomainDataModelTreeDto dmTreeDto) {
        // 如果 isExtend 为 "1"，则忽略该节点
        if (StringPool.ONE.equals(dmTreeDto.getIsExtend())) {
            return null;
        }
        ServiceParamsDto.ParamTreeDto paramTreeDto = new ServiceParamsDto.ParamTreeDto();
        BeanUtils.copyProperties(dmTreeDto, paramTreeDto);
        // 递归复制子树
        if (!CollectionUtils.isEmpty(dmTreeDto.getChildren())) {
            List<ServiceParamsDto.ParamTreeDto> paramChildren = new ArrayList<>();
            for (DomainDataModelTreeDto dmChild : dmTreeDto.getChildren()) {
                ServiceParamsDto.ParamTreeDto paramChild = copyTreeProperties(dmChild);
                if (paramChild != null) {
                    paramChildren.add(paramChild);
                }
            }
            paramTreeDto.setChildren(paramChildren);
        }
        return paramTreeDto;
    }

    /**
     * 校验授权码有效性
     * @param authCode 授权码
     * @param serviceCode 服务code
     * @return validateResult
     */
    public ServiceAuthOutputDto validateAuthCode(String authCode, String serviceCode) {
        VarProcessAuthorization authorization = authorizationService.getOne(Wrappers.<VarProcessAuthorization>lambdaQuery().eq(VarProcessAuthorization::getAuthorizationCode, authCode));

        ServiceAuthOutputDto validateResult = new ServiceAuthOutputDto();
        validateResult.setPass(false);
        if (authorization == null) {
            validateResult.setMessage("授权码不存在");
        } else if (!authorization.getEnabled()) {
            validateResult.setMessage("授权码停用");
        } else {
            List<VarProcessAuthorizationService> auConfig = varProcessAuthorizationServiceService.list(Wrappers.<VarProcessAuthorizationService>lambdaQuery()
                    .eq(VarProcessAuthorizationService::getAuthorizationId, authorization.getId()));
            if (auConfig.stream().noneMatch(auth -> auth.getServiceCode().equals(serviceCode))) {
                validateResult.setMessage("授权码与服务不匹配");
            } else {
                List<ServiceInfoDto> serviceInfos = varProcessServiceVersionService.findUpVersionByCode(serviceCode);

                if (CollectionUtils.isEmpty(serviceInfos)) {
                    validateResult.setMessage("服务不存在或已删除");
                } else if (serviceInfos.stream().noneMatch(service -> service.getState().equals(VarProcessServiceStateEnum.ENABLED))) {
                    validateResult.setMessage("服务未启用");
                } else {
                    validateResult.setMessage("验证成功");
                    validateResult.setPass(true);
                }
            }
        }
        return validateResult;
    }

}
