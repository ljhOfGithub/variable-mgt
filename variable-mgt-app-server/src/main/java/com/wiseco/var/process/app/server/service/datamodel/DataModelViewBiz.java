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
package com.wiseco.var.process.app.server.service.datamodel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.DataModelUtils;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.dto.DomainDataVersionCompareOutputDto;
import com.decision.jsonschema.util.enums.JsonSchemaFieldEnum;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.controller.vo.input.DataVersionCompareInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelViewInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestForRealTimeServiceVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutsideServerGetDataModelOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeGetBySourceTypeOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableRefServiceManifestOutputDto;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.VarProcessInternalDataMapper;
import com.wiseco.var.process.app.server.repository.VarProcessOutsideRefMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionVarService;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wiseco.var.process.app.server.service.VarProcessVariableVarService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelManifestUseVo;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelRealTimeServiceUseVo;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelServiceDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVarService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Zhaoxiong Chen
 * @since 2022/6/14
 */
@Slf4j
@Service
public class DataModelViewBiz {
    @Resource
    private VarProcessSpaceService varProcessSpaceService;
    @Resource
    private VarProcessDataModelService varProcessDataModelService;
    @Resource
    private VarProcessInternalDataService varProcessInternalDataService;
    @Resource
    private VarProcessInternalDataMapper varProcessInternalDataMapper;
    @Resource
    private VarProcessOutsideRefMapper varProcessOutsideRefMapper;
    @Resource
    private OutsideService outsideService;
    @Resource
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Resource
    private BacktrackingService backtrackingService;
    @Resource
    private VarProcessServiceManifestService varProcessServiceManifestService;
    @Resource
    private VarProcessBatchBacktrackingMapper varProcessBatchBacktrackingMapper;
    @Resource
    private VarProcessVariableVarService varProcessVariableVarService;
    @Resource
    private VarProcessFunctionVarService varProcessFunctionVarService;
    @Resource
    private VarProcessManifestVarService varProcessManifestVarService;
    @Resource
    private AuthService authService;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;
    @Resource
    private UserService userService;
    @Resource
    private DbOperateService dbOperateService;
    private static final String STRING_ONE = "1";
    @Resource
    private DeptService deptService;
    /**
     * getDataModelList
     *
     * @param inputDto 入参dto
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelListOutputVO IPage
     */
    public IPage<VariableDataModelListOutputVO> getDataModelList(VariableDataModelQueryInputVO inputDto) {
        IPage<VariableDataModelListOutputVO> outputDto = new Page<>();
        // 分页设置
        Page<VarProcessDataModel> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        // 排序字段驼峰转下划线
        if (inputDto.getSortKey() != null) {
            String[] words = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(inputDto.getSortKey());
            String underscore = org.apache.commons.lang3.StringUtils.join(words, "_").toLowerCase();
            inputDto.setSortKey(underscore);
        }
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return outputDto;
        }
        BeanUtils.copyProperties(roleDataAuthority,inputDto);
        IPage<VarProcessDataModel> pageList = varProcessDataModelService.findPageList(page, inputDto);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return outputDto;
        }
        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);
        //子版本
        List<String> objNameList = pageList.getRecords().stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
        List<VarProcessDataModel> childrenList = varProcessDataModelService.findListByObjectName(inputDto.getSpaceId(), objNameList);
        Map<String, List<VarProcessDataModel>> childrenMap = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(childrenList)) {
            childrenMap = childrenList.stream().collect(Collectors.groupingBy(VarProcessDataModel::getObjectName));
        }
        //服务
        List<VariableDataModelServiceDto> services = varProcessDataModelService.findServiceListByObjectName(inputDto.getSpaceId(), objNameList);
        Map<String, List<VariableDataModelServiceDto>> serviceListMap = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(childrenList)) {
            serviceListMap = services.stream().collect(Collectors.groupingBy(VariableDataModelServiceDto::getObjectName));
        }

        //查询所有被使用的数据模型Set
        Set<Long> usedModelSet = varProcessDataModelService.findAllUsedDatModelIds();
        List<VariableDataModelListOutputVO> list = new ArrayList<>();
        generateResultList(inputDto, pageList, userMap, childrenMap, serviceListMap,usedModelSet, list);
        outputDto.setCurrent(pageList.getCurrent());
        outputDto.setSize(pageList.getSize());
        outputDto.setTotal(pageList.getTotal());
        outputDto.setPages(pageList.getPages());
        outputDto.setRecords(list);
        return outputDto;
    }

    private void generateResultList(VariableDataModelQueryInputVO inputDto, IPage<VarProcessDataModel> pageList, Map<String, String> userMap, Map<String, List<VarProcessDataModel>> chlidrenMap, Map<String, List<VariableDataModelServiceDto>> serviceListMap,Set<Long> usedModelSet, List<VariableDataModelListOutputVO> list) {
        Set<String> usersInMainPage = new HashSet<>();
        Set<String> deptCodeSet = new HashSet<>();
        Set<String> userNameSet = new HashSet<>();
        getDeptCodeSetAndUserNameList(pageList, chlidrenMap, deptCodeSet, userNameSet);
        //部门code:部门名称
        Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(new ArrayList<>(deptCodeSet));
        //用户英文名：用户中文名
        Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(new ArrayList<>(userNameSet));
        //参考接口 /variable/variableList
        for (VarProcessDataModel dataModel : pageList.getRecords()) {
            VariableDataModelListOutputVO dto = new VariableDataModelListOutputVO();
            BeanUtils.copyProperties(dataModel, dto);
            dto.setSourceType(dataModel.getObjectSourceType());
            dto.setCreatedDept(dataModel.getCreatedDeptName());
            usersInMainPage.add(dataModel.getCreatedUser());
            usersInMainPage.add(dataModel.getUpdatedUser());
            dto.setUsed(usedModelSet.contains(dataModel.getId()));
            dto.setCreatedDept(deptMap.getOrDefault(dataModel.getCreatedDept(),MagicStrings.EMPTY_STRING));
            //改为rpc调用，通过部门的code，得到部门的中文名称
            usersInMainPage.add(userFullNameMap.getOrDefault(dataModel.getCreatedUser(),MagicStrings.EMPTY_STRING));
            //改为rpc调用，通过用户的英文名，得到用户的中文名
            usersInMainPage.add(userFullNameMap.getOrDefault(dataModel.getUpdatedUser(),MagicStrings.EMPTY_STRING));
            //通过数据模型的的名称查询是不是被使用 是的话设置apply为1
            List<VariableDataModelVarUseOutputVo> applyList = getDataModelUseList(inputDto.getSpaceId(), dataModel.getObjectName(), dataModel.getVersion(), dataModel.getId());
            if (!CollectionUtils.isEmpty(applyList)) {
                dto.setUsed(Boolean.TRUE);
            } else {
                dto.setUsed(Boolean.FALSE);
            }
            dto.setSourceInfo(dataModel.getObjectSourceInfo());
            //子版本列表
            List<VariableDataModelListOutputVO> versionList = new ArrayList<>();
            if (chlidrenMap.containsKey(dataModel.getObjectName())) {
                List<VarProcessDataModel> childList = chlidrenMap.get(dataModel.getObjectName());
                for (VarProcessDataModel child : childList) {
                    versionList.add(converterDataModel(userMap, child, serviceListMap,usedModelSet));
                }
            }
            for (VariableDataModelListOutputVO dto1 : versionList) {
                usersInMainPage.add(dto1.getCreatedUser());
                usersInMainPage.add(dto1.getUpdatedUser());
            }
            dto.setVersionList(versionList);
            //服务列表
            setServiceList(serviceListMap, dataModel, dto);
            list.add(dto);
        }
        // 循环调用，解决userClient.getNameMapByUserNames()方法只能用10个大小的list

        List<String> users = new ArrayList<>(usersInMainPage);
        Map<String, String> nameMapByUserNames = userService.findFullNameMapByUserNames(users);
        List<VarProcessDataModel> records = pageList.getRecords();
        for (int i = 0; i < list.size(); i++) {
            VarProcessDataModel varProcessDataModel = records.get(i);
            list.get(i).setCreatedUser(nameMapByUserNames.getOrDefault(varProcessDataModel.getCreatedUser(), MagicStrings.EMPTY_STRING));
            list.get(i).setUpdatedUser(nameMapByUserNames.getOrDefault(varProcessDataModel.getUpdatedUser(), MagicStrings.EMPTY_STRING));
            List<VariableDataModelListOutputVO> versionList = list.get(i).getVersionList();
            for (VariableDataModelListOutputVO variableDataModelListOutputVO : versionList) {
                String createdUser = variableDataModelListOutputVO.getCreatedUser();
                String updatedUser = variableDataModelListOutputVO.getUpdatedUser();
                variableDataModelListOutputVO.setCreatedUser(nameMapByUserNames.getOrDefault(createdUser, MagicStrings.EMPTY_STRING));
                variableDataModelListOutputVO.setUpdatedUser(nameMapByUserNames.getOrDefault(updatedUser, MagicStrings.EMPTY_STRING));
            }
        }
    }

    private static void getDeptCodeSetAndUserNameList(IPage<VarProcessDataModel> pageList, Map<String, List<VarProcessDataModel>> chlidrenMap, Set<String> deptCodeSet, Set<String> userNameSet) {
        pageList.getRecords().forEach(item -> {
            if (item.getCreatedDept() != null) {
                deptCodeSet.add(item.getCreatedDept());
            }
            userNameSet.add(item.getCreatedUser());
            userNameSet.add(item.getUpdatedUser());

            if (chlidrenMap.containsKey(item.getCreatedDept())) {
                List<VarProcessDataModel> varProcessDataModels = chlidrenMap.get(item.getCreatedDept());
                varProcessDataModels.forEach(sub -> {
                    if (sub.getCreatedDept() != null) {
                        deptCodeSet.add(sub.getCreatedDept());
                    }
                    userNameSet.add(sub.getCreatedUser());
                    userNameSet.add(sub.getUpdatedUser());
                });
            }
        });
    }


    private void setServiceList(Map<String, List<VariableDataModelServiceDto>> serviceListMap, VarProcessDataModel dataModel, VariableDataModelListOutputVO dto) {
        List<VariableRefServiceManifestOutputDto> serviceList = new ArrayList<>();
        if (serviceListMap.containsKey(dataModel.getObjectName())) {
            List<VariableDataModelServiceDto> childList = serviceListMap.get(dataModel.getObjectName());
            for (VariableDataModelServiceDto serviceDto : childList) {
                if (!dataModel.getVersion().equals(serviceDto.getObjectVersion())) {
                    continue;
                }
                serviceList.add(VariableRefServiceManifestOutputDto.builder().name(serviceDto.getName()).version(serviceDto.getVersion()).state(serviceDto.getState()).varNums(serviceDto.getVarNums()).build());
            }
        }
        dto.setServiceList(serviceList);
    }

    private VariableDataModelListOutputVO converterDataModel(Map<String, String> userMap, VarProcessDataModel dataModel, Map<String, List<VariableDataModelServiceDto>> serviceListMap,Set<Long> usedModelSet) {
        VariableDataModelListOutputVO childDto = new VariableDataModelListOutputVO();
        BeanUtils.copyProperties(dataModel, childDto);
        childDto.setUsed(usedModelSet.contains(dataModel.getId()));
        childDto.setCreatedUser(userMap.getOrDefault(dataModel.getCreatedUser(), dataModel.getCreatedUser()));
        childDto.setUpdatedUser(userMap.getOrDefault(dataModel.getUpdatedUser(), dataModel.getUpdatedUser()));
        //服务列表
        setServiceList(serviceListMap, dataModel, childDto);
        return childDto;
    }

    /**
     * getObjectList
     *
     * @param spaceId 空间id
     * @return java.lang.String
     */
    public List<String> getObjectList(Long spaceId) {
        //组合数据模型
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionList(spaceId,roleDataAuthority);
        //排序
        //重写compare方法
        dataModelList.sort((a, b) -> {
            String valA = a.getObjectName();
            String valB = b.getObjectName();
            return valA.toUpperCase().compareTo(valB.toUpperCase());
        });
        return dataModelList.stream().map(VarProcessDataModel::getObjectName).collect(Collectors.toList());
    }

    /**
     * getDataModelMaxVersionList
     *
     * @param sourceType 数据来源
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeGetBySourceTypeOutputVO List
     */
    public List<VariableDataModeGetBySourceTypeOutputVO> getDataModelMaxVersionList(String sourceType) {
        if (!EnumSet.of(VarProcessDataModelSourceType.INSIDE_DATA, VarProcessDataModelSourceType.OUTSIDE_SERVER).contains(VarProcessDataModelSourceType.valueOf(sourceType))) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "数据模型来源类型错误");
        }

        List<VarProcessDataModel> dataModelList = varProcessDataModelService.getDataModelMaxVersionList(sourceType);
        List<VariableDataModeGetBySourceTypeOutputVO> dataModeGetBySourceTypeOutputVos = new ArrayList<>();

        //拿到map<object_name,internalData>
        Map<String, VarProcessInternalData> nameIdentifierMap = varProcessInternalDataService.list(Wrappers.<VarProcessInternalData>lambdaQuery()
                .select(VarProcessInternalData::getId, VarProcessInternalData::getName, VarProcessInternalData::getIdentifier)
                .eq(VarProcessInternalData::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().collect(Collectors.toMap(VarProcessInternalData::getObjectName, item -> item, (k1, k2) -> k2));

        for (VarProcessDataModel dataModel : dataModelList) {
            VariableDataModeGetBySourceTypeOutputVO variableDataModeGetBySourceTypeOutputVO = new VariableDataModeGetBySourceTypeOutputVO();
            variableDataModeGetBySourceTypeOutputVO.setDataModelId(dataModel.getId());
            variableDataModeGetBySourceTypeOutputVO.setObjectName(dataModel.getObjectName());
            variableDataModeGetBySourceTypeOutputVO.setObjectLabel(dataModel.getObjectLabel());
            variableDataModeGetBySourceTypeOutputVO.setVersion(dataModel.getVersion());
            variableDataModeGetBySourceTypeOutputVO.setInternalDataName(nameIdentifierMap.get(dataModel.getObjectName()).getName());
            variableDataModeGetBySourceTypeOutputVO.setIdentifier(nameIdentifierMap.get(dataModel.getObjectName()).getIdentifier());

            DomainDataModelTreeDto treeDto = DataModelUtils.getDomainDataModelTreeOutputDto(dataModel.getContent());
            VariableDataModeGetBySourceTypeOutputVO.ParameterBinding binding = new VariableDataModeGetBySourceTypeOutputVO.ParameterBinding();

            binding.setCnName(dataModel.getObjectLabel());
            binding.setIsArr(0);
            binding.setMapping(treeDto.getName());
            binding.setName(treeDto.getName());
            binding.setType("object");
            binding.setUseRootObjectFlag(Integer.parseInt(treeDto.getIsRefRootNode()));
            variableDataModeGetBySourceTypeOutputVO.setParameterBinding(binding);
            dataModeGetBySourceTypeOutputVos.add(variableDataModeGetBySourceTypeOutputVO);
        }

        return dataModeGetBySourceTypeOutputVos;
    }

    /**
     * getInsideDataEntering
     *
     * @param objectName 数据模型名称
     * @param version 版本
     * @param dataModelId 数据模型id
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo.InsideInputVO List
     */
    public List<VariableDataModeViewOutputVo.InsideInputVO> getInsideDataEntering(String objectName, Integer version, Long dataModelId) {
        QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_model_id", dataModelId);
        queryWrapper.eq("object_name", objectName);
        queryWrapper.select("id", "content");
        VarProcessInternalData data = varProcessInternalDataMapper.selectOne(queryWrapper);
        if (data == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "数据模型不存在");
        }
        VariableDataModeViewOutputVo.DataModelInsideDataVO insideDataVO = JSON.parseObject(data.getContent(), VariableDataModeViewOutputVo.DataModelInsideDataVO.class);

        return insideDataVO.getInput();
    }

    /**
     * getOutsideServerEntering
     *
     * @param outId 外数Id
     * @return com.wisecotech.json.JSONArray
     */
    public JSONArray getOutsideServerEntering(Long outId) {
        final OutsideServiceDetailRestOutputDto data = outsideService.getOutsideServiceDetailRestById(outId);
        String entering = data.getReq().getRequestParam();

        JSONArray jsonArray = JSON.parseArray(entering);
        List<String> result = new ArrayList<>();
        jsonArray.forEach(json -> {
            JSONObject jsonObject = JSON.parseObject(json.toString());
            if (MagicStrings.VAR_INCOM.equals(jsonObject.get(MagicStrings.VALUE_TYPE))) {
                result.add(jsonObject.toString());
            }
        });

        return JSON.parseArray(result.toString());
    }

    /**
     * getOutsideServerDataModelList
     *
     * @param outName 外部服务名称
     * @param manifestId 清单id
     * @param outCode 外部服务编码
     * @return com.wiseco.var.process.app.server.controller.vo.output.OutsideServerGetDataModelOutputVO List
     */
    public List<OutsideServerGetDataModelOutputVO> getOutsideServerDataModelList(String outName, Long manifestId, String outCode) {
        List<String> objectNames = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                .select(VarProcessManifestDataModel::getObjectName)
                .eq(VarProcessManifestDataModel::getManifestId, manifestId)).stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(objectNames)) {
            return new ArrayList<>();
        }
        List<OutsideServerGetDataModelOutputVO> outsideServerDataModelList = new ArrayList<>();
        LambdaQueryWrapper<VarProcessOutsideRef> queryWrapperOutside = new LambdaQueryWrapper<>();
        queryWrapperOutside.select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getName, VarProcessOutsideRef::getDataModelId, VarProcessOutsideRef::getNameCn, VarProcessOutsideRef::getIsUseRootObject);
        queryWrapperOutside.eq(VarProcessOutsideRef::getOutsideServiceCode, outCode);
        queryWrapperOutside.in(VarProcessOutsideRef::getName,objectNames);
        List<VarProcessOutsideRef> outsideRefDataList = varProcessOutsideRefMapper.selectList(queryWrapperOutside);
        List<String> nameList = outsideRefDataList.stream().map(VarProcessOutsideRef::getName).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nameList)) {
            return outsideServerDataModelList;
        }
        //获取最新版本数据模型map
        List<VarProcessDataModel> maxModels = varProcessDataModelService.findMaxVersionModelsByNames(nameList);
        if (CollectionUtils.isEmpty(maxModels)) {
            return outsideServerDataModelList;
        }
        Map<String, VarProcessDataModel> modelNameMap = maxModels.stream().collect(Collectors.toMap(VarProcessDataModel::getObjectName, model -> model, (k1, k2) -> k2));
        for (VarProcessOutsideRef varProcessOutsideRef : outsideRefDataList) {
            OutsideServerGetDataModelOutputVO outsideServerDataModel = new OutsideServerGetDataModelOutputVO();
            VarProcessDataModel dataModel = modelNameMap.get(varProcessOutsideRef.getName());
            if (dataModel == null || !Objects.equals(varProcessOutsideRef.getDataModelId(), dataModel.getId())) {
                continue;
            }
            outsideServerDataModel.setDataModelId(varProcessOutsideRef.getDataModelId());
            outsideServerDataModel.setObjectName(dataModel.getObjectName());
            outsideServerDataModel.setObjectLabel(dataModel.getObjectLabel());
            outsideServerDataModel.setVersion(dataModel.getVersion());
            outsideServerDataModel.setOutParameterBinding(populateOutParameterBinding(varProcessOutsideRef));
            outsideServerDataModelList.add(outsideServerDataModel);
        }

        return outsideServerDataModelList;
    }

    /**
     * 填充引擎需要使用的信息
     *
     * @param outsideRef 数据模型-外数关系
     * @return com.wiseco.var.process.app.server.controller.vo.output.OutsideServerGetDataModelOutputVO.OutParameterBinding
     */
    private OutsideServerGetDataModelOutputVO.OutParameterBinding populateOutParameterBinding(VarProcessOutsideRef outsideRef) {
        OutsideServerGetDataModelOutputVO.OutParameterBinding binding = new OutsideServerGetDataModelOutputVO.OutParameterBinding();
        binding.setCnName(outsideRef.getNameCn());
        // 确认外数返回的对象是否都是非数组类型
        binding.setIsArr(0);
        binding.setMapping(outsideRef.getName());
        binding.setName(outsideRef.getName());
        // 确认外数返回的对象都是"object"
        binding.setType("object");
        binding.setUseRootObjectFlag(outsideRef.getIsUseRootObject());

        return binding;
    }

    /**
     * dataModelView
     *
     * @param inputVo 输入
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo
     */
    public VariableDataModeViewOutputVo dataModelView(VariableDataModelViewInputVo inputVo) {
        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getOne(Wrappers.<VarProcessSpace>lambdaQuery().select(VarProcessSpace::getId).eq(VarProcessSpace::getId,inputVo.getSpaceId()));
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        //查询数据模型
        VarProcessDataModel dataModel = varProcessDataModelService.getById(inputVo.getDataModelId());
        if (null == dataModel) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        if (!dataModel.getVarProcessSpaceId().equals(inputVo.getSpaceId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        //将数据模型中的content转换为treeDto的操作
        //获取空间下使用的全部变量
        Set<String> varPathMap = inputVo.getWithUseInfo() ? getUsedVarList(inputVo.getSpaceId()) : new HashSet<>();
        DomainDataModelTreeDto inputData = DomainModelTreeEntityUtils.transferDataModelTreeDto(dataModel.getContent(), varPathMap);

        //新建返回VO进行并填充
        VariableDataModeViewOutputVo variableDataModeViewOutputVo = new VariableDataModeViewOutputVo();
        variableDataModeViewOutputVo.setSpaceId(dataModel.getVarProcessSpaceId());
        variableDataModeViewOutputVo.setDataModelId(dataModel.getId());
        variableDataModeViewOutputVo.setObjectName(dataModel.getObjectName());
        variableDataModeViewOutputVo.setObjectLabel(dataModel.getObjectLabel());
        variableDataModeViewOutputVo.setSourceType(dataModel.getObjectSourceType());
        variableDataModeViewOutputVo.setVersion(dataModel.getVersion());

        // 使用 MyBatis-Plus 的 selectOne 方法查询数据
        QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_model_id", inputVo.getDataModelId());
        queryWrapper.select("id", "content");
        VarProcessInternalData data = varProcessInternalDataMapper.selectOne(queryWrapper);
        if (data != null) {
            variableDataModeViewOutputVo.setInsideData(JSON.parseObject(data.getContent(), VariableDataModeViewOutputVo.DataModelInsideDataVO.class));
        }

        // 使用 MyBatis-Plus 的 selectOne 方法查询数据
        VarProcessOutsideRef outsideRefData = varProcessOutsideRefMapper.selectOne(Wrappers.<VarProcessOutsideRef>lambdaQuery()
                .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getIsUseRootObject, VarProcessOutsideRef::getOutsideServiceId,
                        VarProcessOutsideRef::getOutsideServiceCode, VarProcessOutsideRef::getOutsideServiceName)
                .eq(VarProcessOutsideRef::getDataModelId, inputVo.getDataModelId()));
        if (outsideRefData != null) {
            boolean isUseRootObject;
            if (outsideRefData.getIsUseRootObject() == 1) {
                isUseRootObject = true;
            } else {
                isUseRootObject = false;
            }
            //外部服务相关填充
            VariableDataModeViewOutputVo.DataModelOutsideServerVO dataModelOutsideServerVO = new VariableDataModeViewOutputVo.DataModelOutsideServerVO();
            dataModelOutsideServerVO.setOutId(String.valueOf(outsideRefData.getOutsideServiceId()));
            dataModelOutsideServerVO.setOutCode(outsideRefData.getOutsideServiceCode());
            dataModelOutsideServerVO.setOutName(outsideRefData.getOutsideServiceName());
            dataModelOutsideServerVO.setIsUseRootObject(isUseRootObject);
            variableDataModeViewOutputVo.setOutsideServer(dataModelOutsideServerVO);
        }
        variableDataModeViewOutputVo.setDomainDataModelTreeDto(inputData);
        return variableDataModeViewOutputVo;
    }

    /**
     * 查询空间下所有被使用的变量
     * @param spaceId 空间id
     * @return set
     */
    private Set<String> getUsedVarList(Long spaceId) {
        Set<String> varPathMap = getUseVarList(spaceId);

        List<VariableUseVarPathDto> varUseList = new ArrayList<>();
        //实时服务
        List<VariableUseVarPathDto> serviceUseList = varProcessServiceVersionService.getVarUseList(spaceId);
        if (!CollectionUtils.isEmpty(serviceUseList)) {
            varUseList.addAll(serviceUseList);
        }
        //批量回溯
        List<VariableUseVarPathDto> backTrackingUseList = backtrackingService.getVarUseList(spaceId);
        if (!CollectionUtils.isEmpty(backTrackingUseList)) {
            varUseList.addAll(backTrackingUseList);
        }
        //清单流程直接使用
        List<VariableUseVarPathDto> manifestSelfUseList = varProcessManifestVarService.getSelfVarUseList(spaceId);
        if (!CollectionUtils.isEmpty(backTrackingUseList)) {
            varUseList.addAll(manifestSelfUseList);
        }
        Set<String> varRefList = new HashSet<>();
         varPathMap.addAll(assembleVarPath(varRefList, varUseList));
         return varPathMap;
    }

    /**
     * 获取空间下使用的全部变量
     *
     * @param spaceId 空间id
     * @return java.lang.String Set
     */
    public Set<String> getUseVarList(Long spaceId) {
        Set<String> varRefList = new HashSet<>();
        List<VariableUseVarPathDto> varUseList = varProcessVariableVarService.getVarUseList(spaceId);
        if (CollectionUtils.isEmpty(varUseList)) {
            varUseList = new ArrayList<>();
        }
        List<VariableUseVarPathDto> functionUseList = varProcessFunctionVarService.getVarUseList(spaceId);
        if (!CollectionUtils.isEmpty(functionUseList)) {
            varUseList.addAll(functionUseList);
        }
        List<VariableUseVarPathDto> manifestUseList = varProcessManifestVarService.getVarUseList(spaceId);
        if (!CollectionUtils.isEmpty(manifestUseList)) {
            varUseList.addAll(manifestUseList);
        }
        return assembleVarPath(varRefList, varUseList);
    }

    private static Set<String> assembleVarPath(Set<String> varRefList, List<VariableUseVarPathDto> varUseList) {
        if (!CollectionUtils.isEmpty(varUseList)) {
            for (VariableUseVarPathDto modelIdDto : varUseList) {
                if (modelIdDto.getVarPath().startsWith(PositionVarEnum.RAW_DATA.getName())) {
                    varRefList.add(modelIdDto.getVarPath().replaceFirst(PositionVarEnum.RAW_DATA.getName() + ".", ""));
                } else if (modelIdDto.getVarPath().startsWith(PositionVarEnum.PARAMETERS.getName()) || modelIdDto.getVarPath().startsWith(PositionVarEnum.LOCAL_VARS.getName())) {
                    if (StringUtils.isEmpty(modelIdDto.getParameterType())) {
                        continue;
                    }
                    DataVariableBasicTypeEnum nameEnum = DataVariableBasicTypeEnum.getNameEnum(modelIdDto.getParameterType());
                    if (nameEnum != null) {
                        continue;
                    }
                    String[] paramSplit = modelIdDto.getVarPath().split("\\.");
                    String targetVarPath = modelIdDto.getVarPath().replaceFirst(paramSplit[0] + "." + paramSplit[1], modelIdDto.getParameterType());
                    targetVarPath = targetVarPath.replaceFirst(PositionVarEnum.RAW_DATA.getName() + ".", "");
                    varRefList.add(targetVarPath);
                }
            }
        }
        return varRefList;
    }

    /**
     * 获取数据模型使用列表
     *
     * @param spaceId     变量空间Id
     * @param objectName  对象名
     * @param version     版本号
     * @param dataModelId 数据模型Id
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo List
     */
    public List<VariableDataModelVarUseOutputVo> getDataModelUseList(Long spaceId, String objectName, Integer version, Long dataModelId) {
        List<VarProcessDataModel> varProcessDataModelList = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).eq(VarProcessDataModel::getVarProcessSpaceId, spaceId).eq(VarProcessDataModel::getObjectName, objectName).eq(VarProcessDataModel::getVersion, version));
        if (CollectionUtils.isEmpty(varProcessDataModelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型");
        }
        //wxs以下为数据模型的变量清单使用
        List<VariableDataModelVarUseOutputVo> outputDtos = new ArrayList<>();
        List<VariableDataModelManifestUseVo> manifestList = varProcessManifestDataModelService.getManifestUseMapping(spaceId, objectName, version);
        if (!CollectionUtils.isEmpty(manifestList)) {
            VariableDataModelVarUseOutputVo useDto = VariableDataModelVarUseOutputVo.builder().title("被变量清单使用").build();
            List<VariableDataModelVarUseOutputVo.TableHeader> tableHeaderList = new ArrayList<>();
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("name").label("变量清单名称").build());
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("allClass").label("变量清单分类").build());
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("status").label("状态").build());
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("operate").label("操作").build());
            useDto.setTableHeader(tableHeaderList);

            List<JSONObject> contentList = new ArrayList<>();
            for (VariableDataModelManifestUseVo dataModelManifest : manifestList) {
                JSONObject content = new JSONObject();
                content.put("name", dataModelManifest.getVarManifestName());
                content.put("allClass", dataModelManifest.getAllClass());
                content.put("status", dataModelManifest.getState().getDesc());
                content.put("operate", "1");
                content.put("id", dataModelManifest.getId());
                contentList.add(content);
            }
            useDto.setTableData(contentList);
            outputDtos.add(useDto);
        }
        //wxs以上为数据模型的变量清单使用
        //wxs以下为数据模型的批量回溯使用
        VariableDataModelVarUseOutputVo backtrackingUseDto = VariableDataModelVarUseOutputVo.builder().title("被批量回溯任务使用").build();
        List<VariableDataModelVarUseOutputVo.TableHeader> backtrackingTableHeader = new ArrayList<>();
        backtrackingTableHeader.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("name").label("任务名称").build());
        backtrackingTableHeader.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("state").label("任务状态").build());
        backtrackingTableHeader.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("operate").label("操作").build());
        backtrackingUseDto.setTableHeader(backtrackingTableHeader);

        List<JSONObject> backtrackingContentList = new ArrayList<>();
        List<String> manifestIds = manifestList.stream().map(VariableDataModelManifestUseVo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(manifestIds)) {
            List<VarProcessBatchBacktracking> batchBacktrackingList = varProcessBatchBacktrackingMapper.selectList(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                    .select(VarProcessBatchBacktracking::getName, VarProcessBatchBacktracking::getStatus, VarProcessBatchBacktracking::getId)
                    .in(VarProcessBatchBacktracking::getManifestId, manifestIds)
                    .orderByAsc(VarProcessBatchBacktracking::getManifestId));
            addBacktrackingContentList(backtrackingContentList, batchBacktrackingList);
        }
        backtrackingUseDto.setTableData(backtrackingContentList);
        if (!CollectionUtils.isEmpty(backtrackingUseDto.getTableData())) {
            outputDtos.add(backtrackingUseDto);
        }
        //wxs以上为数据模型的批量回溯使用
        //wxs以下为数据模型的实时服务使用
        dataModelByServiceUsed(spaceId, objectName, version, outputDtos);
        //wxs以上为数据模型的预处理逻辑使用
        return outputDtos;
    }

    private void addBacktrackingContentList(List<JSONObject> backtrackingContentList, List<VarProcessBatchBacktracking> dataList) {
        if (!CollectionUtils.isEmpty(dataList)) {
            for (VarProcessBatchBacktracking data : dataList) {
                if (data.getStatus() != FlowStatusEnum.DELETE) {
                    JSONObject content = new JSONObject();
                    content.put("name", data.getName());
                    content.put("state", data.getStatus().getDesc());
                    content.put("operate", 1);
                    content.put("id", data.getId());
                    backtrackingContentList.add(content);
                }
            }
        }
    }

    private void dataModelByServiceUsed(Long spaceId, String objectName, Integer version, List<VariableDataModelVarUseOutputVo> outputDtos) {
        VariableDataModelVarUseOutputVo useDto = builderUseDto();

        List<ManifestForRealTimeServiceVO> serviceIdList = varProcessManifestDataModelService.getManifestForRealTimeService(spaceId, objectName, version);
        List<JSONObject> contentList = new ArrayList<>();
        for (ManifestForRealTimeServiceVO manifestForRealTimeServiceVO : serviceIdList) {
            VariableDataModelRealTimeServiceUseVo dataModelManifest = varProcessServiceManifestService.getRealTimeServiceUseMapping(manifestForRealTimeServiceVO.getServiceId(), manifestForRealTimeServiceVO.getManifestId());
            if (dataModelManifest != null) {
                JSONObject content = setContent(dataModelManifest);
                String getCurrentExecuteCountSql = "SELECT count(*) FROM var_process_log WHERE interface_type = 1 and service_id = " + dataModelManifest.getId();
                long currentExecuteCount = dbOperateService.queryForLong(getCurrentExecuteCountSql, Long.class);
                content.put("currentExecuteCount", currentExecuteCount);
                content.put("operate", 1);
                content.put("id", dataModelManifest.getId());
                contentList.add(content);
            }
        }
        useDto.setTableData(contentList);
        if (!CollectionUtils.isEmpty(useDto.getTableData())) {
            outputDtos.add(useDto);
        }

        //wxs以上为数据模型的实时服务使用
        //wxs以下为数据模型的预处理逻辑使用
        List<VariableUseVarPathDto> functionQueryList = varProcessFunctionVarService.getVarFunctionPrepUseList(objectName);
        if (!CollectionUtils.isEmpty(functionQueryList)) {
            //        根据模型的id查出模型
//        遍历这个数组进行预处理匹配 匹配成功的返回
            VariableDataModelVarUseOutputVo varUseDto = VariableDataModelVarUseOutputVo.builder().title("被预处理逻辑使用").build();
            List<VariableDataModelVarUseOutputVo.TableHeader> tableHeaderList = new ArrayList<>();
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("name").label("逻辑名称").build());
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("statustr").label("状态").build());
            tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("operate").label("操作").build());
            varUseDto.setTableHeader(tableHeaderList);
            List<JSONObject> contentList1 = new ArrayList<>();
            for (VariableUseVarPathDto dto : functionQueryList) {
                JSONObject content = new JSONObject();
                content.put("name", dto.getName());
                content.put("statustr", Objects.requireNonNull(FlowStatusEnum.getStatustr(dto.getStatustr())).getDesc());
                content.put("operate", "1");
                content.put("id", dto.getId());
                contentList1.add(content);
                varUseDto.setTableData(contentList1);
            }
            if (!CollectionUtils.isEmpty(varUseDto.getTableData())) {
                outputDtos.add(varUseDto);
            }
        }
    }

    private VariableDataModelVarUseOutputVo builderUseDto() {
        VariableDataModelVarUseOutputVo useDto = VariableDataModelVarUseOutputVo.builder().title("被实时服务使用").build();
        List<VariableDataModelVarUseOutputVo.TableHeader> tableHeaderList1 = new ArrayList<>();
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("name").label("服务名称").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("code").label("服务编码").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("allClass").label("服务分类").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("manifestRole").label("角色").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("state").label("状态").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("currentExecuteCount").label("已执行笔数").build());
        tableHeaderList1.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("operate").label("操作").build());
        useDto.setTableHeader(tableHeaderList1);
        return useDto;
    }

    private JSONObject setContent(VariableDataModelRealTimeServiceUseVo dataModelManifest) {
        JSONObject content = new JSONObject();
        content.put("name", dataModelManifest.getName());
        content.put("code", dataModelManifest.getCode());
        content.put("allClass", dataModelManifest.getAllClass());
        if (STRING_ONE.equals(dataModelManifest.getManifestRole())) {
            content.put("manifestRole", "主清单");
        } else {
            content.put("manifestRole", "异步加工");
        }
        content.put("state", VarProcessServiceStateEnum.valueOf(dataModelManifest.getState()).getDesc());
        return content;
    }

    private VariableDataModelVarUseOutputVo getVariableDataModelVarUseOutputVo(Map<Long, VariableUseVarPathDto> varUseMap, VariableDataModelVarUseOutputVo varUseDto, List<VariableDataModelVarUseOutputVo.TableHeader> tableHeaderList) {
        tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("statustr").label("状态").build());
        tableHeaderList.add(VariableDataModelVarUseOutputVo.TableHeader.builder().prop("operate").label("操作").build());
        varUseDto.setTableHeader(tableHeaderList);
        List<JSONObject> contentList = new ArrayList<>();

        Set<Map.Entry<Long, VariableUseVarPathDto>> entries = varUseMap.entrySet();
        for (Map.Entry<Long, VariableUseVarPathDto> entry : entries) {
            VariableUseVarPathDto dto = entry.getValue();
            JSONObject content = new JSONObject();
            content.put("name", dto.getName());
            content.put("allClass", dto.getAllClass());
            content.put("statustr", Objects.requireNonNull(FlowStatusEnum.getStatustr(dto.getStatustr())).getDesc());
            content.put("operate", "1");
            content.put("id", dto.getId());
            contentList.add(content);
        }
        varUseDto.setTableData(contentList);
        return varUseDto;
    }

    /**
     * getDataModel
     *
     * @param spaceId 空间Id
     * @param dataModelId 数据模型Id
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto List
     */
    public List<DomainDataModelTreeDto> getDataModel(Long spaceId, Long dataModelId) {
        List<DomainDataModelTreeDto> outputList = new ArrayList<>();
        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getById(dataModelId);
        if (null == dataModel) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        if (!dataModel.getVarProcessSpaceId().equals(spaceId)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        Set<String> varPathMap = getUseVarList(spaceId);
        DomainDataModelTreeDto inputData = DomainModelTreeEntityUtils.transferDataModelTreeDto(dataModel.getContent(), varPathMap);
        outputList.add(inputData);

        return outputList;
    }

    /**
     * findVariableDataModelCompareVersion
     * @param inputDto 输入
     * @return com.decision.jsonschema.util.dto.DomainDataVersionCompareOutputDto
     */
    public DomainDataVersionCompareOutputDto findVariableDataModelCompareVersion(DataVersionCompareInputVO inputDto) {
        if (inputDto.getDataModelIdList().size() < NumberUtils.INTEGER_TWO || inputDto.getDataModelIdList().size() > NumberUtils.INTEGER_TWO) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对比版本的数据模型必须等于2个！");
        }
        List<VarProcessDataModel> variableDataModelList = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().in(VarProcessDataModel::getId, inputDto.getDataModelIdList()).orderByAsc(VarProcessDataModel::getVersion));
        if (CollectionUtils.isEmpty(variableDataModelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据模型id数据不存在！");
        }
        if (variableDataModelList.size() < NumberUtils.INTEGER_TWO) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "存在没有数据的数据模型id！");
        }
        VarProcessDataModel minVariableDataModel = variableDataModelList.get(NumberUtils.BYTE_ZERO);
        VarProcessDataModel maxVariableDataModel = variableDataModelList.get(NumberUtils.BYTE_ONE);
        DomainDataModelTreeDto minVersionTreeDto = DataModelUtils.getDomainDataModelTreeOutputDto(minVariableDataModel.getContent());
        DomainDataModelTreeDto maxVersionTreeDto = DataModelUtils.getDomainDataModelTreeOutputDto(maxVariableDataModel.getContent());
        DomainDataVersionCompareOutputDto outputDto = new DomainDataVersionCompareOutputDto();
        outputDto.setMinVersion("版本" + minVariableDataModel.getVersion());
        outputDto.setMaxVersion("版本" + maxVariableDataModel.getVersion());

        Set<String> minSet = new HashSet<>();
        Set<String> maxSet = new HashSet<>();
        DataModelUtils.findDomainDataModelCompareVersion(minVersionTreeDto, maxVersionTreeDto, minSet, maxSet, outputDto);

        List<DomainDataModelTreeDto> minTreeDtoList = new ArrayList<>();
        minTreeDtoList.add(minVersionTreeDto);
        List<DomainDataModelTreeDto> maxTreeDtoList = new ArrayList<>();
        maxTreeDtoList.add(maxVersionTreeDto);
        //仅显示差异的
        if (inputDto.getIsDiff() != null && inputDto.getIsDiff().equals(NumberUtils.INTEGER_ONE)) {
            if (CollectionUtils.isEmpty(minSet)) {
                minTreeDtoList = new ArrayList<>();
            } else {
                minTreeDtoList = DataModelUtils.resetPackageModelTree(minSet, minTreeDtoList);
            }
            if (CollectionUtils.isEmpty(maxSet)) {
                maxTreeDtoList = new ArrayList<>();
            } else {
                maxTreeDtoList = DataModelUtils.resetPackageModelTree(maxSet, maxTreeDtoList);
            }
        }
        outputDto.setMinTreeDtoList(minTreeDtoList);
        outputDto.setMaxTreeDtoList(maxTreeDtoList);

        return outputDto;
    }

    /**
     * 获取所有数据模型引用的所有内部数据表名
     * @return 所有数据模型引用的所有内部数据表名
     */
    public List<String> getAllInternalDataTableName() {
        QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "content");
        List<VarProcessInternalData> data = varProcessInternalDataMapper.selectList(queryWrapper);
        List<String> insideDataTableNameList = new ArrayList<>();
        if (data != null) {
            for (VarProcessInternalData varProcessInternalData : data) {
                if (varProcessInternalData != null) {
                    VariableDataModeViewOutputVo.DataModelInsideDataVO modelInsideDataVo = JSON.parseObject(varProcessInternalData.getContent(), VariableDataModeViewOutputVo.DataModelInsideDataVO.class);
                    if (modelInsideDataVo != null) {
                        //如果是SQL方式直接获取内部表名
                        if (modelInsideDataVo.getInsideDataType() == VarProcessDataModeInsideDataType.SQL) {
                            List<String> sqlInsideTableNameList = modelInsideDataVo.getSqlTableNames();
                            insideDataTableNameList.addAll(sqlInsideTableNameList);
                        }
                        //如果是源表获取的话，先获取表映射中的内部数据表名，如果存在children，再获取children中的表映射中的内部数据表名，由于只有一层children所以不需要嵌套
                        if (modelInsideDataVo.getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                            List<VariableDataModeViewOutputVo.InsideOutputVO> insideOutputVOList = modelInsideDataVo.getTableOutput();
                            for (VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO : insideOutputVOList) {
                                insideDataTableNameList.add(insideOutputVO.getTableConfigs().getTableName());
                                if (CollectionUtils.isEmpty(insideOutputVO.getChildren())) {
                                    List<VariableDataModeViewOutputVo.InsideOutputVO> children = insideOutputVO.getChildren();
                                    for (VariableDataModeViewOutputVo.InsideOutputVO children1 : children) {
                                        insideDataTableNameList.add(children1.getTableConfigs().getTableName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return insideDataTableNameList;
    }

    /**
     * 根据数据模型名称拿到最大版本
     * @param objectName 数据模型
     * @return VariableDataModeViewOutputVo
     */
    public VariableDataModeViewOutputVo getMaxDataModelViewByName(String objectName) {
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionModelsByNames(Collections.singletonList(objectName));
        if (!CollectionUtils.isEmpty(dataModelList)) {
            return dataModelView(VariableDataModelViewInputVo.builder().dataModelId(dataModelList.get(0).getId()).spaceId(1L).withUseInfo(false).build());
        }
        return null;
    }

    /**
     * 根据数据模型名称拿到最大版本
     * @param objectName 数据模型
     * @return VariableDataModeViewOutputVo
     */
    public DomainDataModelTreeDto getModelObjectChildren(String objectName) {
        DomainDataModelTreeDto ret = null;
        final String[] split = objectName.split("\\.");
        final VarProcessDataModel dataModel = varProcessDataModelService.findByDataModelName(split[1]);
        if (split.length == MagicNumbers.TWO) {
            ret = DomainModelTreeEntityUtils.getDomainModelTree(dataModel.getContent());
        } else {
            JSONObject jsonNode = JSON.parseObject(dataModel.getContent());
            for (int i = MagicNumbers.TWO; i < split.length; i++) {
                final String nodeName = split[i];
                if ("array".equals(jsonNode.getString("type"))) {
                    jsonNode = jsonNode.getJSONObject("items").getJSONObject("properties").getJSONObject(nodeName);
                } else {
                    jsonNode = jsonNode.getJSONObject("properties").getJSONObject(nodeName);
                }
                jsonNode.put(JsonSchemaFieldEnum.TITLE_FIELD.getMessage(), nodeName);
            }
            ret = DomainModelTreeEntityUtils.getDomainModelTree(jsonNode.toJSONString());
        }
        addRawData(ret, objectName.substring(0, objectName.lastIndexOf(".") + 1));

        return ret;
    }

    private void addRawData(DomainDataModelTreeDto tree, String pre) {
        tree.setValue(pre + tree.getValue());
        if (tree.getChildren() != null && !tree.getChildren().isEmpty()) {
            for (DomainDataModelTreeDto child : tree.getChildren()) {
                addRawData(child, pre);
            }
        }
    }

    /**
     * 获取外数服务入参
     * @param outId 外数id
     * @param modelId 数据模型id
     * @return 入参jsonArray
     */
    public JSONArray getOutsideServiceInputParams(Long outId,Long modelId) {
        if (modelId == null) {
            return getOutsideServerEntering(outId);
        } else {
            VarProcessOutsideRef varProcessOutsideRef = varProcessOutsideRefMapper.selectOne(Wrappers.<VarProcessOutsideRef>lambdaQuery()
                    .eq(VarProcessOutsideRef::getDataModelId,modelId)
                    .select(VarProcessOutsideRef::getInputParameterBindings));
            if (varProcessOutsideRef != null && !StringUtils.isEmpty(varProcessOutsideRef.getInputParameterBindings())) {
                return JSONObject.parseArray(varProcessOutsideRef.getInputParameterBindings());
            }
            return new JSONArray();
        }
    }

    /**
     * 获取外部传入数据模型
     * @return map
     */
    public Map<String, String> findExternalParamDataModels() {
        List<VarProcessDataModel> dataModelMaxVersionList = varProcessDataModelService.getDataModelMaxVersionList(VarProcessDataModelSourceType.OUTSIDE_PARAM.name());
        TreeMap<String, String> resultMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        dataModelMaxVersionList.forEach(item -> resultMap.put(item.getObjectName(),item.getObjectLabel()));
        return resultMap;
    }
}
