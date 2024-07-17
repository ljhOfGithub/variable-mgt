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

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceUpdateInputVO;
import com.wiseco.var.process.app.server.enums.VarProcessManifestActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VarProcessParamTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceActionEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.common.CacheEventSendService;
import com.wiseco.var.process.app.server.service.dto.input.ServiceManifestUpdateInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceOperateBiz extends ServiceBiz {

    @Autowired
    private VariableManifestBiz variableManifestBiz;

    @Autowired
    private VarProcessParamService varProcessParamService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;

    @Autowired
    private VarProcessServiceService varProcessServiceService;

    @Autowired
    private CacheEventSendService cacheEventSendService;

    @Resource(name = "remoteCacheClient")
    private CacheClient cacheClient;

    public static final String REST_MANIFEST_CURRENT_EXECUTE_COUNT = "rest_manifest_currentExecuteCount_";

    /**
     * 编辑中：提交、删除；
     * 审核中：审核；
     * 审核拒绝：退回编辑；
     * 启用：停用；
     * 停用：启用
     * @param inputDto 入参dto
     */
    public void updateState(VariableServiceUpdateInputVO inputDto) {
        VarProcessServiceVersion versionEntity = varProcessServiceVersionService.getById(inputDto.getVersionId());
        VarProcessRealtimeService serviceEntity = varProcessRealtimeServiceService.getById(versionEntity.getServiceId());
        if (serviceEntity == null || serviceEntity.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND,"该实时服务已经被删除或不存在!");
        }
        // 2.获取操作类型
        VarProcessServiceActionEnum actionType = VarProcessServiceActionEnum.getAction(inputDto.getActionType());
        if (actionType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION,"未查找到对应的操作！");
        }
        // 3.获取引用的变量清单的id list
        List<VarProcessServiceManifest> list = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, inputDto.getVersionId()));
        List<Long> manifestIds = list.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());
        String desContent = inputDto.getApproDescription();
        // 4.是否启用审核
        Boolean serviceVerify = varProcessParamService.getParamStatus(VarProcessParamTypeEnum.REAL_TIME_SERVICE_REVIEW.getCode());
        // 5.校验+处理
        switch (actionType) {
            case SUBMIT_REVIEW:
                submitCheck(serviceEntity,versionEntity, list, manifestIds);
                // 未开启审核按钮 则直接启用
                if (!serviceVerify) {
                    variableManifestBiz.enableVariableManifest(inputDto.getVersionId(), VarProcessManifestActionTypeEnum.APPROVE, inputDto.getApproDescription());
                    cacheEventSendService.serviceVersionChange();
                    actionType = VarProcessServiceActionEnum.APPROVE;
                }
                break;
            case APPROVE:
                if (!serviceVerify) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务审核未打开，不可以进行审核操作!");
                }
                enableCheck(manifestIds);
                // 启用变量清单
                variableManifestBiz.enableVariableManifest(inputDto.getVersionId(), VarProcessManifestActionTypeEnum.APPROVE, inputDto.getApproDescription());
                cacheEventSendService.serviceVersionChange();
                break;
            case REJECT:
                if (!serviceVerify) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务审核未打开，不可以进行审核操作!");
                }
                break;
            case BACK_EDIT:
                //退回编辑 无校验
                break;
            case RENABLE:
                enableCheck(manifestIds);
                cacheEventSendService.serviceVersionChange();
                break;
            case DISABLE:
                //发布缓存事件
                cacheEventSendService.serviceVersionChange();
                break;
            default:
                break;
        }
        //更新状态
        varProcessServiceVersionService.updateState(versionEntity, actionType, desContent);
    }


    /**
     * 提交处理的流程
     * @param serviceEntity 服务实体
     * @param versionEntity 版本实体
     * @param list          实时服务-变量清单关系list
     * @param manifestIds   变量清单list
     */
    private void submitCheck(VarProcessRealtimeService serviceEntity, VarProcessServiceVersion versionEntity, List<VarProcessServiceManifest> list, List<Long> manifestIds) {
        // ①.先校验实体类的几个重要属性是不是为空
        if (StringUtils.isEmpty(serviceEntity.getServiceName()) || StringUtils.isEmpty(serviceEntity.getServiceCode()) || ObjectUtil.isEmpty(serviceEntity.getCategoryId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务的名称或者编码或者分类出现异常，请重新配置。");
        }
        // ②.检查实时服务里面有没有变量清单
        if (CollectionUtils.isEmpty(manifestIds)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"当前实时服务没有配置任何的变量清单，请重新配置。");
        }
        // ③.验证这些变量清单是否存在,且没有被停用
        enableCheck(manifestIds);
        // ④.验证这些变量清单里面是否有主清单
        boolean maniManifestExist = false;
        for (VarProcessServiceManifest item : list) {
            if (item.getManifestRole().equals((short) 1)) {
                maniManifestExist = true;
                break;
            }
        }
        if (!maniManifestExist) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该实时服务中没有主清单，请检查!");
        }
        // ⑤.验证各个主清单的生效期是否相同
        for (VarProcessServiceManifest item : list) {
            for (VarProcessServiceManifest obj : list) {
                if (item.getManifestRole().equals((short) 1) && obj.getManifestRole().equals((short) 1) && !item.equals(obj)) {
                    boolean flag = (item.getImmediateEffect().equals(1) && obj.getImmediateEffect().equals(1)) || (item.getValidTime().equals(obj.getValidTime()));
                    if (flag) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"多个主清单的生效时间不可以一样，请检查!");
                    }
                }
            }
        }
        // ⑥.检查服务的入参信息是否存在
        List<ServiceDataModelMappingVo> serviceDataModelMappingVos = serviceDataModelMappings(manifestIds);
        if (CollectionUtils.isEmpty(serviceDataModelMappingVos)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"实时服务的服务入参信息为空，请检查!");
        }
        // ⑦.检查主体唯一标识是否存在
        if (StringUtils.isEmpty(versionEntity.getSerialNo())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"实时服务的主体唯一标识为空，请检查!");
        }
    }

    /**
     * 实时服务提交/启用时，需要判断使用的变量清单是否存在，并处于启用状态：
     * 1）如果不存在，则提示“该服务引用的变量清单【XXXXXV1】已经被删除，无法启用”；
     * 2）如果存在，但是处于停用状态，则提示“该服务引用的变量清单【XXXXXV1】已经被停用，无法启用该服务”；
     * @param manifestIds 清单id list
     */
    public void enableCheck(List<Long> manifestIds) {
        // 1.先找出变量清单
        List<VarProcessManifest> manifests = new ArrayList<>();
        if (!CollectionUtils.isEmpty(manifestIds)) {
            manifests = varProcessManifestService.list(Wrappers.<VarProcessManifest>lambdaQuery()
                    .in(VarProcessManifest::getId, manifestIds));
        }
        // 2.判断变量清单是否已经被删除
        String inexistManifests = manifests.stream()
                .filter(item -> item.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode()))
                .map(VarProcessManifest::getVarManifestName)
                .collect(Collectors.joining(","));
        if (!StringUtils.isEmpty(inexistManifests)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND,"该服务引用的变量清单【" + inexistManifests + "】不存在");
        }
        // 3.判断变量清单是否已经被停用了
        String disabledManifests = manifests.stream()
                .filter(item -> !(item.getDeleteFlag().equals(DeleteFlagEnum.USABLE.getCode()) && item.getState().equals(VarProcessManifestStateEnum.UP)))
                .map(VarProcessManifest::getVarManifestName)
                .collect(Collectors.joining(","));
        if (!StringUtils.isEmpty(disabledManifests)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH,"该服务引用的变量清单【" + disabledManifests + "】未启用");
        }
    }

    /**
     * 实时服务重启(启用)处理过程
     * @param inputDto      前端发送过来的实体
     * @param serviceEntity 实时服务实体
     * @param list          实时服务-变量清单关系
     * @param manifestIds   变量清单Id集合
     */
    private void renableProcess(VariableServiceUpdateInputDto inputDto, VarProcessService serviceEntity, List<VarProcessServiceManifest> list, List<Long> manifestIds) {
        // ①.先校验实体类的几个重要属性是不是为空
        if (StringUtils.isEmpty(serviceEntity.getName()) || StringUtils.isEmpty(serviceEntity.getCode()) || ObjectUtil.isEmpty(serviceEntity.getCategoryId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务的名称或者编码或者分类出现异常，请重新配置。");
        }
        // ②.检查实时服务里面有没有变量清单
        if (CollectionUtils.isEmpty(manifestIds)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND,"当前实时服务没有配置任何的变量清单，请重新配置。");
        }
        // ③.验证这些变量清单是否存在,且没有被停用
        enableCheck(manifestIds);
        // ④.验证这些变量清单里面是否有主清单
        boolean maniManifestExist = false;
        for (VarProcessServiceManifest item : list) {
            if (item.getManifestRole().equals((short) 1)) {
                maniManifestExist = true;
                break;
            }
        }
        if (!maniManifestExist) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该实时服务中没有主清单，请检查!");
        }
        // ⑤.验证各个主清单的生效期是否相同
        for (VarProcessServiceManifest item : list) {
            for (VarProcessServiceManifest obj : list) {
                boolean flag = item.getManifestRole().equals((short) 1) && obj.getManifestRole().equals((short) 1) && !item.equals(obj);
                if (flag) {
                    flag = (item.getImmediateEffect().equals(1) && obj.getImmediateEffect().equals(1)) || (item.getValidTime().equals(obj.getValidTime()));
                    if (flag) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"多个主清单的生效时间不可以一样，请检查!");
                    }
                }
            }
        }
        // ⑥.检查服务的入参信息是否存在
        List<ServiceDataModelMappingVo> serviceDataModelMappingVos = serviceDataModelMappings(manifestIds);
        if (CollectionUtils.isEmpty(serviceDataModelMappingVos)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"实时服务的服务入参信息为空，请检查!");
        }
        // ⑦.检查主体唯一标识是否存在
        if (StringUtils.isEmpty(serviceEntity.getSerialNo())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"实时服务的主体唯一标识为空，请检查!");
        }
        // ⑧.修改状态，如果与他关联的变量清单发布成功了，就直接启用，否则抛出异常
        variableManifestBiz.enableVariableManifest(inputDto.getServiceId(), VarProcessManifestActionTypeEnum.APPROVE, inputDto.getApproDescription());
    }

    /**
     * 删除校验
     * @param id 服务id
     */
    public void validateDeleteService(Long id) {
        VarProcessRealtimeService service = varProcessRealtimeServiceService.getById(id);
        //校验
        deleteCheck(service);
    }

    /**
     * 更改实时服务版本状态 校验接口
     *
     * @param inputDto 入参
     * @return message
     */
    public String validateUpdateState(VariableServiceUpdateInputVO inputDto) {
        VarProcessServiceVersion versionEntity = varProcessServiceVersionService.getById(inputDto.getVersionId());
        VarProcessRealtimeService serviceEntity = varProcessRealtimeServiceService.getById(versionEntity.getServiceId());
        if (serviceEntity == null || serviceEntity.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND,"该实时服务版本信息已经被删除或不存在!");
        }
        // 2.获取操作类型
        VarProcessServiceActionEnum actionType = VarProcessServiceActionEnum.getAction(inputDto.getActionType());
        if (actionType == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION,"未查找到对应的操作！");
        }
        // 3.获取引用的变量清单的id list
        List<VarProcessServiceManifest> list = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, inputDto.getVersionId()));
        List<Long> manifestIds = list.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());
        // 是否启用审核
        Boolean serviceVerify = varProcessParamService.getParamStatus(VarProcessParamTypeEnum.REAL_TIME_SERVICE_REVIEW.getCode());
        // 4.校验+处理
        switch (actionType) {
            case SUBMIT_REVIEW:
                submitCheck(serviceEntity,versionEntity, list, manifestIds);
                if (!serviceVerify) {
                    return "当前服务未开启审核，提交后将直接启用，确认提交？";
                }
                break;
            case APPROVE:
                if (!serviceVerify) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务审核未打开，不可以进行审核操作!");
                }
                enableCheck(manifestIds);
                break;
            case REJECT:
                if (!serviceVerify) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"实时服务审核未打开，不可以进行审核操作!");
                }
                break;
            case BACK_EDIT:
                //退回编辑 无校验
                break;
            case RENABLE:
                enableCheck(manifestIds);
                break;
            case DISABLE:
                //停用 无校验？？
                break;
            default:
                break;
        }

        return String.format("确认%s该实时服务版本V%s？",actionType.getDesc(),versionEntity.getServiceVersion());
    }

    /**
     * 根据serviceId和manifestId来更新实时服务中变量清单的执行次数
     * @param inputDto serviceId和manifestId的实体类
     * @return 执行是否成功
     */
    public Boolean updateServiceAndManifest(ServiceManifestUpdateInputDto inputDto) {
        // 1.更新执行笔数
        LambdaUpdateWrapper<VarProcessServiceManifest> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(VarProcessServiceManifest::getServiceId, inputDto.getServiceId());
        wrapper.eq(VarProcessServiceManifest::getManifestId, inputDto.getManifestId());
        wrapper.setSql("current_execute_count = current_execute_count + 1");
        boolean update = varProcessServiceManifestService.update(wrapper);

        // 2.更新缓存执行笔数
        VarProcessServiceManifest varProcessServiceManifest = varProcessServiceManifestService.getOne(new LambdaQueryWrapper<VarProcessServiceManifest>()
                .eq(VarProcessServiceManifest::getServiceId, inputDto.getServiceId())
                .eq(VarProcessServiceManifest::getManifestId, inputDto.getManifestId())
                .select(VarProcessServiceManifest::getId,VarProcessServiceManifest::getCurrentExecuteCount));
        cacheClient.put(REST_MANIFEST_CURRENT_EXECUTE_COUNT + varProcessServiceManifest.getId(),varProcessServiceManifest.getCurrentExecuteCount());
        return update;
    }

    /**
     * 迁移数据
     */
    public void migrateData() {
        List<VarProcessService> oldServices = varProcessServiceService.list(Wrappers.<VarProcessService>lambdaQuery().eq(VarProcessService::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        Map<String, List<VarProcessService>> oldServiceMap = oldServices.stream().collect(Collectors.groupingBy(VarProcessService::getCode));
        List<VarProcessRealtimeService> newServiceList = new ArrayList<>();
        List<VarProcessServiceVersion> newVersionList = new ArrayList<>();
        oldServiceMap.forEach((k,v) -> {
            VarProcessService service = v.get(0);
            newServiceList.add(VarProcessRealtimeService.builder().spaceId(service.getVarProcessSpaceId()).serviceCode(service.getCode()).serviceName(service.getName()).categoryId(service.getCategoryId()).deleteFlag(DeleteFlagEnum.USABLE.getCode())
                    .id(service.getId()).enableTrace(service.getEnableTrace()).messageFormat(ServiceMsgFormatEnum.JSON).createdUser(service.getCreatedUser()).updatedUser(service.getUpdatedUser()).build());

            v.forEach(version -> newVersionList.add(VarProcessServiceVersion.builder().id(version.getId()).serviceId(service.getId()).serviceVersion(version.getVersion()).deptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode())
                    .state(version.getState()).serialNo(version.getSerialNo()).deleteFlag(DeleteFlagEnum.USABLE.getCode()).createdUser(version.getCreatedUser()).updatedUser(version.getCreatedUser()).build()));
        });

        varProcessRealtimeServiceService.saveOrUpdateBatch(newServiceList);
        varProcessServiceVersionService.saveOrUpdateBatch(newVersionList);
    }
}
