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

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.security.SessionUser;
import com.wiseco.var.process.app.server.VarProcessApp;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.feign.VarProcessConsumerFeign;
import com.wiseco.var.process.app.server.controller.feign.dto.DownLoadDataDto;
import com.wiseco.var.process.app.server.controller.vo.input.ConditionSettingSaveDto;
import com.wiseco.var.process.app.server.controller.vo.input.ConditionSettingSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DataViewInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ConditionSettingListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ConditionSettingOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.QueryParamOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.enums.ManifestTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorization;
import com.wiseco.var.process.app.server.repository.entity.VarProcessLogConditionSetting;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishingVariableDTO;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestSubBiz;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum.USABLE;

/**
 * 变量结果查询 接口实现
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@Slf4j
@Service
public class VariableResultQueryBiz {

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Autowired
    private VarProcessConsumerFeign varProcessConsumerFeign;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;

    @Autowired
    private AuthService authService;

    @Autowired
    private VarProcessLogConditionSettingService conditionSettingService;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;

    private List<VarProcessLogConditionSetting> initedConditionSettingList = Lists.newArrayList();

    @Autowired
    private VariableManifestSubBiz variableManifestSubBiz;

    @PostConstruct
    void init() {
        try (InputStream inputStream = VarProcessApp.class.getClassLoader().getResourceAsStream(File.separator + "template" + File.separator + "initConditionSetting.json5")) {
            String initConditionSettingStr = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            this.initedConditionSettingList = JSON.parseArray(initConditionSettingStr, VarProcessLogConditionSetting.class);
        } catch (Exception e) {
            log.error("读取自定义查询条件、结果表头列 基本配置失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * saveConditionSetting
     *
     * @param inputDto
     */
    public void saveConditionSetting(ConditionSettingSaveInputDto inputDto) {
        List<ConditionSettingSaveDto> dtoList = inputDto.getDtoList();
        if (!CollectionUtils.isEmpty(dtoList)) {
            List<VarProcessLogConditionSetting> conditionSettingList = conditionSettingService.list(new LambdaQueryWrapper<VarProcessLogConditionSetting>()
                    .eq(VarProcessLogConditionSetting::getUserName, SessionContext.getSessionUser().getUsername()));
            Map<Long, VarProcessLogConditionSetting> idConditionSettingMap = conditionSettingList.stream().collect(Collectors.toMap(VarProcessLogConditionSetting::getId, item -> item));
            boolean updateQuery = MagicNumbers.ZERO == inputDto.getUpdateType();
            // 更新查询条件列
            List<VarProcessLogConditionSetting> updateList = Lists.newArrayList();
            dtoList.forEach(dto -> {
                VarProcessLogConditionSetting conditionSetting = new VarProcessLogConditionSetting();
                BeanUtils.copyProperties(dto, conditionSetting);
                if (updateQuery) {
                    // 更新查询条件
                    conditionSetting.setQueryDisplay(dto.getDisplay());
                    conditionSetting.setQueryWeight(MagicNumbers.INTEGER_ONE.equals(dto.getDisplay()) ? dto.getWeight() : null);
                    // 强制更新 结果表头列权重，不设值该列在update时会被置为null
                    conditionSetting.setColumnWeight(idConditionSettingMap.get(dto.getId()).getColumnWeight());
                } else {
                    // 更新结果表头列
                    conditionSetting.setColumnDisplay(dto.getDisplay());
                    conditionSetting.setColumnWeight(MagicNumbers.INTEGER_ONE.equals(dto.getDisplay()) ? dto.getWeight() : null);
                    // 强制更新 查询条件列权重，不设值该列在update时会被置为null
                    conditionSetting.setQueryWeight(idConditionSettingMap.get(dto.getId()).getQueryWeight());
                    conditionSetting.setIsLock(dto.getIsLock());
                    conditionSetting.setIsLockHead(dto.getIsLockHead());
                }
                updateList.add(conditionSetting);
            });
            conditionSettingService.updateBatchById(updateList);
        }
    }

    /**
     * listConditionSetting
     *
     * @param manifestId
     * @param settingType
     * @return com.wiseco.var.process.app.server.controller.vo.output.ConditionSettingListOutputDto
     */
    public ConditionSettingListOutputDto listConditionSetting(Long manifestId, Integer settingType) {
        SessionUser sessionUser = SessionContext.getSessionUser();
        ConditionSettingListOutputDto outputDto = new ConditionSettingListOutputDto();
        List<ConditionSettingOutputDto> builtInHead = Lists.newArrayList();
        List<ConditionSettingOutputDto> customHead = Lists.newArrayList();
        // 前端页面展示查询参数列表
        List<QueryParamOutputDto> queryParamList = Lists.newArrayList();
        // 前端页面展示可搜索变量列表
        List<QueryParamOutputDto> searchableVariableList = Lists.newArrayList();
        List<VarProcessLogConditionSetting> conditionSettingList = conditionSettingService.list(new LambdaQueryWrapper<VarProcessLogConditionSetting>()
                .eq(VarProcessLogConditionSetting::getUserName, sessionUser.getUsername())
                .eq(VarProcessLogConditionSetting::getManifestId, MagicNumbers.MINUS_INT_1)
                .eq(VarProcessLogConditionSetting::getSettingType, settingType));
        if (CollectionUtils.isEmpty(conditionSettingList)) {
            // 初始化当前用户的查询列表信息（内置和8个固定自定义查询条件）,添加settingType的查询条件，分为settingType=0和settingType=1的情况；
            conditionSettingList = initConditionSettingByUsername(sessionUser.getUsername(), settingType);
        }
        // 自定义查询参数列表
        Map<String, String> customVarNameAndVarTypeMap = Maps.newHashMap();
        List<VarProcessLogConditionSetting> variableConditionSettingList = Lists.newArrayList();
        if (manifestId != null) {
            // 清单下索引变量筛选, 添加settingType用于判断是否添加可搜索变量
            variableConditionSettingList = getVariableConditionSettingList(manifestId, sessionUser, customVarNameAndVarTypeMap, settingType);
        }
        // 内置和8个自定义查询条件
        conditionSettingList.forEach(item -> {
            ConditionSettingOutputDto conditionSettingOutputDto = new ConditionSettingOutputDto();
            BeanUtils.copyProperties(item, conditionSettingOutputDto);
            // 设置标识是否清单的可搜索变量的字段为否
            conditionSettingOutputDto.setIsManiSearchVar(MagicNumbers.ZERO);
            boolean isBuiltInParam = MagicNumbers.INTEGER_ONE.equals(item.getIsBuiltIn());
            if (isBuiltInParam) {
                builtInHead.add(conditionSettingOutputDto);
            } else {
                customHead.add(conditionSettingOutputDto);
            }
            // 内置参数、自定义参数 筛选 兼容数据查看页面
            boolean isQueryParam = MagicNumbers.INTEGER_ONE.equals(item.getQueryDisplay())
                    && (isBuiltInParam || (customVarNameAndVarTypeMap.containsKey(item.getVarName()) || manifestId == null || settingType.equals(MagicNumbers.INTEGER_ONE)));
            if (isQueryParam) {
                QueryParamOutputDto queryParam = QueryParamOutputDto.builder()
                        .category(isBuiltInParam ? MagicStrings.BUILT_IN_PARAM : MagicStrings.CUSTOM_PARAM)
                        .varName(item.getVarName()).label(item.getVarNameCn())
                        .type(customVarNameAndVarTypeMap.getOrDefault(item.getVarName(), item.getVarDataType()))
                        .weight(item.getQueryWeight()).build();
                queryParam.setVarFullPath(queryParam.getCategory() + StringPool.DOT + queryParam.getVarName());
                if (MagicStrings.SELECT.toLowerCase().equals(queryParam.getType())) {
                    // 下拉框属性需要填充
                    fillQueryParamSelectList(queryParam);
                }
                queryParamList.add(queryParam);
            }
        });
        // 清单下的可搜索变量
        variableConditionSettingList.forEach(item -> {
            // 添加可搜索变量
            ConditionSettingOutputDto conditionSettingOutputDto = new ConditionSettingOutputDto();
            BeanUtils.copyProperties(item, conditionSettingOutputDto);
            // 设置标识是否清单的可搜索变量的字段为是
            conditionSettingOutputDto.setIsManiSearchVar(MagicNumbers.INTEGER_ONE);
            customHead.add(conditionSettingOutputDto);
            boolean isQueryParam = MagicNumbers.INTEGER_ONE.equals(item.getQueryDisplay());
            if (isQueryParam) {
                QueryParamOutputDto queryParam = QueryParamOutputDto.builder()
                        .category(MagicStrings.VAR_PARAM).varName(item.getVarName()).label(item.getVarNameCn())
                        .type(item.getVarDataType()).weight(item.getQueryWeight()).build();
                queryParam.setVarFullPath(queryParam.getCategory() + StringPool.DOT + queryParam.getVarName());
                if (Objects.equals(item.getSettingType(), MagicNumbers.INTEGER_ONE)) {
                    searchableVariableList.add(queryParam);
                } else {
                    queryParamList.add(queryParam);
                }
            }
        });
        outputDto.setBuiltInHead(builtInHead);
        outputDto.setCustomHead(customHead);
        outputDto.setQueryParamList(queryParamList);
        outputDto.setSearchableVariableList(searchableVariableList);
        return outputDto;
    }

    private List<VarProcessLogConditionSetting> getVariableConditionSettingList(Long manifestId, SessionUser sessionUser, Map<String, String> customParamNameAndVarTypeMap, Integer settingType) {
        List<VarProcessLogConditionSetting> variableConditionSettingList;
        variableConditionSettingList = conditionSettingService.list(new LambdaQueryWrapper<VarProcessLogConditionSetting>()
                .eq(VarProcessLogConditionSetting::getUserName, sessionUser.getUsername())
                .eq(VarProcessLogConditionSetting::getManifestId, manifestId)
                //根据用户名和清单id查找设置记录，如果settingType是0则筛选settingType为null的json，否则筛选settingType为1的json
                .eq(VarProcessLogConditionSetting::getSettingType, settingType == MagicNumbers.ZERO ? null : MagicNumbers.INTEGER_ONE));
        if (CollectionUtils.isEmpty(variableConditionSettingList)) {
            // 1. 初始化清单下所有变量的自定义查询条件信息
            variableConditionSettingList = initVariableConditionSettingByUsername(manifestId, sessionUser.getUsername(), settingType);
        }
        // 2. 查询变量清单下所有数据模型的信息
        List<VarProcessManifestDataModel> manifestDataModelList = varProcessManifestDataModelService.list(new LambdaQueryWrapper<VarProcessManifestDataModel>()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId));
        manifestDataModelList.forEach(manifestDataModel -> {
            List<VariableManifestDataModelMappingVo.QueryCondition> customQueryConditionList = JSON.parseArray(manifestDataModel.getModelQueryCondition(), VariableManifestDataModelMappingVo.QueryCondition.class);
            if (!CollectionUtils.isEmpty(customQueryConditionList)) {
                // 供后续判断是否展示此筛选项
                // 当选择了清单后，需要判断清单下所有数据模型中的配置了自定义查询条件的选项，与用户配置的自定义查询参数配置做一个交集，满足的则添加到前端的筛选项里
                customParamNameAndVarTypeMap.putAll(customQueryConditionList.stream().collect(Collectors.toMap(VariableManifestDataModelMappingVo.QueryCondition::getMappingCode, VariableManifestDataModelMappingVo.QueryCondition::getVarType)));
            }
        });
        return variableConditionSettingList;
    }

    private void fillQueryParamSelectList(QueryParamOutputDto queryParam) {
        List<Map<String, Object>> selectList = Lists.newArrayList();
        switch (queryParam.getVarName()) {
            case MagicStrings.CALLER:
                for (String caller : callerList()) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("code", caller);
                    map.put("name", caller);
                    selectList.add(map);
                }
                break;
            case MagicStrings.RESULT_STATUS:
                Map<String, Object> success = Maps.newHashMap();
                success.put("code", MagicStrings.SUCCESS);
                success.put("name", MagicStrings.SUCCESS);
                selectList.add(success);
                Map<String, Object> fail = Maps.newHashMap();
                fail.put("code", MagicStrings.FAILD);
                fail.put("name", MagicStrings.FAILD);
                selectList.add(fail);
                break;
            case MagicStrings.MANIFEST_TYPE:
                selectList = ManifestTypeEnum.getManifestTypeList();
                break;
            case MagicStrings.SERVICE_ID:
            case MagicStrings.MANIFEST_ID:
            default:
                break;
        }
        queryParam.setSelectList(selectList);
    }

    private List<VarProcessLogConditionSetting> initVariableConditionSettingByUsername(Long manifestId, String username, Integer settingType) {
        // 查询变量清单下所有变量信息
        List<VariableManifestPublishingVariableDTO> variableDtoList = varProcessManifestVariableService.getPublishingVariableInfo((long) MagicNumbers.ONE, manifestId);
        List<VarProcessLogConditionSetting> variableConditionSettingList = variableDtoList.stream()
                .filter(variableDto -> {
                    // 判断是否可作为可搜索的变量，如果是查询记录则通过筛选
                    if (settingType == MagicNumbers.ZERO) {
                        return true;
                    }
                    // 数据库中可能是null 不能通过筛选
                    if (variableDto.getIsIndex() == null) {
                        return false;
                    }
                    // 如果是数据查看并且是可搜索变量则通过筛选
                    return variableDto.getIsIndex();
                })
                .map(variableDto -> {
                    VarProcessLogConditionSetting conditionSetting = new VarProcessLogConditionSetting();
                    conditionSetting.setVarName(variableDto.getName());
                    conditionSetting.setVarNameCn(variableDto.getLabel());
                    conditionSetting.setVarDataType(variableDto.getDataType());
                    conditionSetting.setQueryDisplay(MagicNumbers.ZERO);
                    conditionSetting.setColumnDisplay(MagicNumbers.ZERO);
                    conditionSetting.setIsLock(MagicNumbers.ZERO);
                    conditionSetting.setIsLockHead(MagicNumbers.ZERO);
                    conditionSetting.setVarType(MagicNumbers.TWO);
                    conditionSetting.setUserName(username);
                    // 与清单绑定
                    conditionSetting.setManifestId(manifestId);
                    // 设置settingType
                    conditionSetting.setSettingType(settingType);
                    // 设置alwaysSelected默认值
                    conditionSetting.setAlwaysSelected(MagicNumbers.ZERO);
                    return conditionSetting;
                }).collect(Collectors.toList());
        conditionSettingService.saveBatch(variableConditionSettingList);
        return variableConditionSettingList;
    }

    private List<VarProcessLogConditionSetting> initConditionSettingByUsername(String username, Integer settingType) {
        List<VarProcessLogConditionSetting> collect = initedConditionSettingList.stream()
                //添加filter根据settingType筛选，如果传入的settingType是0，则挑选settingType为null的json，否则挑选settingType为1的json
                .filter(item -> {
                    if (settingType == MagicNumbers.ZERO) {
                        return item.getSettingType() == null;
                    } else {
                        return Objects.equals(item.getSettingType(), settingType);
                    }
                }).map(item -> {
                    VarProcessLogConditionSetting conditionSetting = new VarProcessLogConditionSetting();
                    BeanUtils.copyProperties(item, conditionSetting);
                    conditionSetting.setUserName(username);
                    return conditionSetting;
                }).collect(Collectors.toList());
        conditionSettingService.saveBatch(collect);
        return collect;
    }

    /**
     * 获取调用方
     *
     * @return List
     */
    public List<String> callerList() {
        return authorizationService.list(Wrappers.<VarProcessAuthorization>lambdaQuery()
                .select(VarProcessAuthorization::getCaller)
                .eq(VarProcessAuthorization::getDeleteFlag, USABLE.getCode()))
                .stream().map(VarProcessAuthorization::getCaller).distinct().collect(Collectors.toList());
    }

    /**
     * 获取所有被调用过的实时服务列表
     *
     * @return 所有被调用过的实时服务列表
     */
    public List<ServiceListOutputVo> getRealTimeService() {
        // 1.获取所有被调用过的服务列表(serviceId, serviceName)
        Map<String, String> serviceIdNameMap = varProcessConsumerFeign.getRestServices();
        List<Long> serviceIds = serviceIdNameMap.keySet().stream().map(Long::parseLong).collect(Collectors.toList());
        // 2.添加权限
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        // 3.填充结果
        List<ServiceListOutputVo> outputVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(serviceIds)) {
            Map<Long, List<VarProcessServiceVersion>> serviceVersionMap = varProcessServiceVersionService.list(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                    .in(VarProcessServiceVersion::getId, serviceIds)
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessServiceVersion::getDeptCode, roleDataAuthority.getDeptCodes())
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessServiceVersion::getCreatedUser, roleDataAuthority.getUserNames())
                    .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode())).stream().collect(Collectors.groupingBy(VarProcessServiceVersion::getServiceId));
            List<VarProcessRealtimeService> services = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                    .eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode())
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessRealtimeService::getDeptCode, roleDataAuthority.getDeptCodes())
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessRealtimeService::getCreatedUser, roleDataAuthority.getUserNames())
                    .in(!CollectionUtils.isEmpty(serviceVersionMap.keySet()), VarProcessRealtimeService::getId, serviceVersionMap.keySet()));
            services.forEach(service -> {
                ServiceListOutputVo outputVo = new ServiceListOutputVo();
                outputVo.setServiceCode(service.getServiceCode());
                outputVo.setServiceName(service.getServiceName());
                outputVo.setCalledVersions(serviceVersionMap.get(service.getId()).stream()
                        .map(version -> Collections.singletonMap(version.getServiceVersion(), version.getId()))
                        .collect(Collectors.toList()));
                outputVoList.add(outputVo);
            });
        }
        return outputVoList;
    }

    /**
     * 获取启用的实时服务list
     *
     * @return Map
     */
    public List<ServiceListOutputVo> getUpServiceList() {
        List<ServiceListOutputVo> outputVoList = new ArrayList<>();

        Map<Long, List<VarProcessServiceVersion>> serviceVersionMap = varProcessServiceVersionService.list(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                .eq(VarProcessServiceVersion::getState, VarProcessServiceStateEnum.ENABLED)
                .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode())).stream().collect(Collectors.groupingBy(VarProcessServiceVersion::getServiceId));

        if (!CollectionUtils.isEmpty(serviceVersionMap.keySet())) {
            List<VarProcessRealtimeService> services = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                    .eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode()).in(VarProcessRealtimeService::getId, serviceVersionMap.keySet()));
            services.forEach(service -> {
                ServiceListOutputVo outputVo = new ServiceListOutputVo();
                outputVo.setServiceCode(service.getServiceCode());
                outputVo.setServiceName(service.getServiceName());
                outputVo.setCalledVersions(serviceVersionMap.get(service.getId()).stream()
                        .map(version -> Collections.singletonMap(version.getServiceVersion(), version.getId()))
                        .collect(Collectors.toList()));
                outputVoList.add(outputVo);
            });
        }
        return outputVoList;
    }


    /**
     * 根据实时服务id和清单角色获取清单list
     *
     * @param serviceId    实时服务id
     * @param manifestRole 清单角色
     * @return List
     */
    public List<Map<String, Object>> getCallList(Long serviceId, Short manifestRole) {
        // 1.如果实时服务的ID为空,就返回空的列表
        if (serviceId == null) {
            return Collections.emptyList();
        }
        // 2.构筑查询条件
        LambdaQueryWrapper<VarProcessServiceManifest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(VarProcessServiceManifest::getServiceId, serviceId);
        if (manifestRole != null) {
            lambdaQueryWrapper.eq(VarProcessServiceManifest::getManifestRole, manifestRole);
        }
        // 3.查询出结果
        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(lambdaQueryWrapper);
        List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());
        // 4.组装并调整结果
        if (CollectionUtils.isEmpty(manifestIds)) {
            return Collections.emptyList();
        }
        List<VarProcessManifest> list = varProcessManifestService.list(new LambdaQueryWrapper<VarProcessManifest>()
                .select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName)
                .eq(VarProcessManifest::getDeleteFlag, USABLE.getCode())
                .in(VarProcessManifest::getId, manifestIds));
        return list.stream().map(manifest -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", manifest.getId());
            map.put("name", manifest.getVarManifestName());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 根据实时服务id获取清单map
     *
     * @param serviceId 实时服务id
     * @return map
     */
    public Map<Long, String> getManifestIdNameMap(Long serviceId) {
        if (serviceId == null) {
            return Collections.emptyMap();
        }

        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, serviceId));
        List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(manifestIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<VarProcessManifest> varProcessManifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        varProcessManifestLambdaQueryWrapper.eq(VarProcessManifest::getDeleteFlag, USABLE.getCode());
        varProcessManifestLambdaQueryWrapper.in(VarProcessManifest::getId, manifestIds);
        varProcessManifestLambdaQueryWrapper.select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName);
        return varProcessManifestService.list(varProcessManifestLambdaQueryWrapper)
                .stream().collect(Collectors.toMap(VarProcessManifest::getId, VarProcessManifest::getVarManifestName, (k1, k2) -> k2));
    }

    /**
     * 校验入参
     *
     * @param dataViewInputDto 入参
     */
    private void checkInput(DataViewInputDto dataViewInputDto) {
        if (com.wiseco.boot.commons.lang.StringUtils.isEmpty(dataViewInputDto.getBuiltInParam().getRealTimeServiceName())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND, "实时服务不能为空！");
        }
        if (com.wiseco.boot.commons.lang.StringUtils.isEmpty(dataViewInputDto.getBuiltInParam().getCallList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "清单不能为空！");
        }
        if (dataViewInputDto.getBuiltInParam().getStartDate() == null && dataViewInputDto.getBuiltInParam().getEndDate() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "调用时间段开始与结束时间不能为空！");
        }
    }


    /**
     * 获取字段对应的类型
     *
     * @param callList 清单
     * @return List
     */
    public List<TableFieldVO> getFiledType(String callList) {
        if (StringUtils.isEmpty(callList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "清单不能为空");
        }
        return varProcessConsumerFeign.getFiledType(callList);
    }

    /**
     * 下载数据
     *
     * @param dataViewInputDto dataViewInputDto
     * @param response         response
     */
    public void downLoadData(DataViewInputDto dataViewInputDto, HttpServletResponse response) {
        //校验入参
        checkInput(dataViewInputDto);

        //获取下载数据
        DownLoadDataDto downLoadDataDto = varProcessConsumerFeign.fetchDownLoadData(dataViewInputDto);

        try {
            exportToCsv(downLoadDataDto.getRecord(), downLoadDataDto.getFieldMap(), downLoadDataDto.getFileName(), response);
        } catch (Exception e) {
            log.info("下载数据失败---------->{}", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "下载数据失败！");
        }
    }

    /**
     * 导出csv文件
     *
     * @param records  表数据
     * @param fieldMap 表字段<字段 : 字段类型>
     * @param csvName  导出文件名
     * @param response http响应
     */
    private void exportToCsv(List<Map<String, Object>> records, Map<String, String> fieldMap, String csvName, HttpServletResponse response) {
        List<String> lines = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            lines.add(StringUtils.arrayToCommaDelimitedString(fieldMap.keySet().toArray(new String[0])));
        } else {
            //首行
            lines.add(StringUtils.arrayToCommaDelimitedString(records.get(0).keySet().toArray(new String[0])));

            for (Map<String, Object> recordMap : records) {
                String[] convertedValues = new String[recordMap.values().size()];
                int i = 0;
                for (Map.Entry<String, Object> entry : recordMap.entrySet()) {
                    String columnName = entry.getKey();
                    if (recordMap.get(columnName) == null) {
                        convertedValues[i] = null;
                        i++;
                        continue;
                    }

                    if (!StringUtils.isEmpty(recordMap.get(columnName))) {
                        convertedValues[i] = String.valueOf(recordMap.get(columnName)) + "\t";
                    } else {
                        convertedValues[i] = "";
                    }
                    i++;
                }
                lines.add(StringUtils.arrayToCommaDelimitedString(convertedValues));
            }

        }

        CsvWriter writer;
        try {
            response.setContentType("application/csv;charset=GBK");
            response.setCharacterEncoding("GBK");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(csvName, "UTF-8"));
            writer = CsvUtil.getWriter(response.getWriter());
            writer.write(lines);
            response.flushBuffer();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 获取经过权限处理的实时服务(具体的)的ID集合
     *
     * @return 经过权限处理的实时服务(具体的)的ID集合
     */
    public List<Long> getVersionServiceIds() {
        // 1.添加权限
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        // 2.先查所有被调用过的服务列表
        Map<String, String> serviceIdNameMap = varProcessConsumerFeign.getRestServices();
        List<Long> serviceVersionIds = serviceIdNameMap.keySet().stream().map(Long::parseLong).collect(Collectors.toList());
        // 3.填充结果
        List<Long> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(serviceVersionIds)) {
            List<Long> serviceIds = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                    .select(VarProcessRealtimeService::getId)
                    .eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode())
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessRealtimeService::getDeptCode, roleDataAuthority.getDeptCodes())
                    .in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessRealtimeService::getCreatedUser, roleDataAuthority.getUserNames())
            ).stream().map(VarProcessRealtimeService::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(serviceIds)) {
                return result;
            }
            List<VarProcessServiceVersion> list = varProcessServiceVersionService.list(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                    .select(VarProcessServiceVersion::getId)
                    .in(VarProcessServiceVersion::getId, serviceVersionIds)
                    .in(VarProcessServiceVersion::getServiceId, serviceIds)
                    .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode()));
            list.forEach(item -> {
                result.add(item.getId());
            });
        }
        return result;
    }

}
