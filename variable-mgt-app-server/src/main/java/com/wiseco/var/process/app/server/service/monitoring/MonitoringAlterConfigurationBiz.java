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
package com.wiseco.var.process.app.server.service.monitoring;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationCopyInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringConfigurationPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringConfigurationPageOutputVO;
import com.wiseco.var.process.app.server.enums.MonitoringConfNotifyMethodEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfOperateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfStateEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.job.monitoringalter.MonitoringAlterProcessor;
import com.wiseco.var.process.app.server.job.param.MonitoringAlterTaskParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertConf;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.VarProcessMonitoringAlertConfService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.MonitoringConfigurationPageQueryDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tech.powerjob.common.response.JobInfoDTO;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum.USABLE;

/**
 * 监控配置管理
 *
 * @author wiseco
 */
@Slf4j
@Service
public class MonitoringAlterConfigurationBiz {

    @Resource
    private VarProcessMonitoringAlertConfService varProcessMonitoringAlertConfService;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private UserService userService;

    @Resource
    private WisecoJobOperateUtil jobOperateUtil;

    @Autowired
    @Qualifier(value = "remoteCacheClient")
    private CacheClient cacheClient;

    @Autowired
    private AuthService authService;

    private static final String JOB_NAME = "monitoring_alter_";

    /**
     * 保存
     *
     * @param inputVO 入参
     * @return ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(MonitorConfigurationSaveInputVO inputVO) {
        VarProcessMonitoringAlertConf conf;
        if (null == inputVO.getId()) {
            conf = addVarProcessMonitoringAlertConf(inputVO);
        } else {
            conf = updateVarProcessMonitoringAlertConf(inputVO);
        }
        varProcessMonitoringAlertConfService.saveOrUpdate(conf);

        return conf.getId();
    }

    private VarProcessMonitoringAlertConf addVarProcessMonitoringAlertConf(MonitorConfigurationSaveInputVO inputVO) {

        List<VarProcessMonitoringAlertConf> list = varProcessMonitoringAlertConfService.list(new LambdaQueryWrapper<VarProcessMonitoringAlertConf>()
                .select(VarProcessMonitoringAlertConf::getId)
                .eq(VarProcessMonitoringAlertConf::getConfName, inputVO.getConfName()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "规则名称重复");
        }

        VarProcessMonitoringAlertConf conf = new VarProcessMonitoringAlertConf();
        conf.setMonitoringType(inputVO.getMonitoringType());
        conf.setConfName(inputVO.getConfName());
        conf.setServiceName(inputVO.getServiceName());
        conf.setServiceVersion(inputVO.getServiceVersion());
        if (inputVO.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
            VarProcessManifest varProcessManifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName)
                    .eq(VarProcessManifest::getId, inputVO.getManifestId()));
            if (varProcessManifest == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "清单不存在");
            }
            conf.setManifestName(varProcessManifest.getVarManifestName());
        }
        conf.setConfDesc(inputVO.getConfDesc());
        conf.setMonitoringTarget(inputVO.getParamConfiguration().getMonitoringTarget());
        conf.setAlertGrade(inputVO.getAlertInfo().getAlertGrade());
        conf.setMonitoringState(MonitoringConfStateEnum.EDIT);
        conf.setParamConfigurationInfo(JSONObject.toJSONString(inputVO.getParamConfiguration()));
        conf.setTriggerCondition(JSONObject.toJSONString(inputVO.getTriggerCondition()));
        conf.setAlertInfo(JSONObject.toJSONString(inputVO.getAlertInfo()));
        conf.setCreatedUser(SessionContext.getSessionUser().getUsername());
        conf.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        conf.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
        return conf;
    }

    private VarProcessMonitoringAlertConf updateVarProcessMonitoringAlertConf(MonitorConfigurationSaveInputVO inputVO) {
        VarProcessMonitoringAlertConf conf = varProcessMonitoringAlertConfService.getById(inputVO.getId());
        if (conf == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该规则不存在");
        }

        if (conf.getMonitoringState() == MonitoringConfStateEnum.UP) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "启用状态不允许编辑");
        }

        List<VarProcessMonitoringAlertConf> list = varProcessMonitoringAlertConfService.list(new LambdaQueryWrapper<VarProcessMonitoringAlertConf>()
                .select(VarProcessMonitoringAlertConf::getId)
                .eq(VarProcessMonitoringAlertConf::getConfName, inputVO.getConfName()));
        if (!CollectionUtils.isEmpty(list) && !Objects.equals(list.get(0).getId(), inputVO.getId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "规则名称重复");
        }

        conf.setMonitoringType(inputVO.getMonitoringType());
        conf.setConfName(inputVO.getConfName());
        conf.setServiceName(inputVO.getServiceName());
        conf.setServiceVersion(inputVO.getServiceVersion());
        if (inputVO.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
            VarProcessManifest varProcessManifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getId, VarProcessManifest::getVarManifestName)
                    .eq(VarProcessManifest::getId, inputVO.getManifestId()));
            if (varProcessManifest == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "清单不存在");
            }
            conf.setManifestName(varProcessManifest.getVarManifestName());
        }
        conf.setConfDesc(inputVO.getConfDesc());
        conf.setMonitoringTarget(inputVO.getParamConfiguration().getMonitoringTarget());
        conf.setAlertGrade(inputVO.getAlertInfo().getAlertGrade());
        conf.setMonitoringState(MonitoringConfStateEnum.EDIT);
        conf.setParamConfigurationInfo(JSONObject.toJSONString(inputVO.getParamConfiguration()));
        conf.setTriggerCondition(JSONObject.toJSONString(inputVO.getTriggerCondition()));
        conf.setAlertInfo(JSONObject.toJSONString(inputVO.getAlertInfo()));
        conf.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        conf.setUpdatedTime(new Date());
        return conf;
    }


    /**
     * 复制规则
     *
     * @param inputVO 入参
     * @return id
     */
    public Long copy(MonitorConfigurationCopyInputVO inputVO) {
        VarProcessMonitoringAlertConf baseConf = varProcessMonitoringAlertConfService.getById(inputVO.getCopyId());
        if (baseConf == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该规则不存在");
        }

        List<VarProcessMonitoringAlertConf> list = varProcessMonitoringAlertConfService.list(new LambdaQueryWrapper<VarProcessMonitoringAlertConf>()
                .select(VarProcessMonitoringAlertConf::getId)
                .eq(VarProcessMonitoringAlertConf::getConfName, inputVO.getConfName()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "规则名称重复");
        }

        VarProcessMonitoringAlertConf copyConf = new VarProcessMonitoringAlertConf();
        BeanUtils.copyProperties(baseConf, copyConf);
        copyConf.setId(null);
        copyConf.setConfName(inputVO.getConfName());
        copyConf.setMonitoringState(MonitoringConfStateEnum.EDIT);
        copyConf.setCreatedUser(SessionContext.getSessionUser().getUsername());
        copyConf.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        copyConf.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
        copyConf.setCreatedTime(new Date());
        copyConf.setUpdatedTime(new Date());
        varProcessMonitoringAlertConfService.save(copyConf);
        return copyConf.getId();
    }

    /**
     * 删除配置
     *
     * @param id id
     * @return java.lang.Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        VarProcessMonitoringAlertConf varProcessMonitoringAlertConf = varProcessMonitoringAlertConfService.getById(id);
        if (varProcessMonitoringAlertConf != null) {
            varProcessMonitoringAlertConfService.removeById(id);
            //删除定时任务
            removeTask(varProcessMonitoringAlertConf);
            //删除缓存
            removeCache(varProcessMonitoringAlertConf);

        }
        return true;
    }


    /**
     * 修改状态
     *
     * @param id         id
     * @param actionType actionType
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(Long id, MonitoringConfOperateEnum actionType) {
        VarProcessMonitoringAlertConf conf = varProcessMonitoringAlertConfService.getById(id);
        if (conf == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该规则不存在");
        }
        switch (actionType) {
            case DOWN:
                if (conf.getMonitoringState() != MonitoringConfStateEnum.DOWN) {
                    conf.setMonitoringState(MonitoringConfStateEnum.DOWN);
                    //删除定时任务
                    removeTask(conf);
                    removeCache(conf);
                }
                break;
            case UP:
                if (conf.getMonitoringState() != MonitoringConfStateEnum.UP) {
                    conf.setMonitoringState(MonitoringConfStateEnum.UP);
                    //保存定时任务
                    saveJob(conf);
                }
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的操作");
        }
        conf.setUpdatedTime(new Date());
        varProcessMonitoringAlertConfService.updateById(conf);
        return true;
    }


    /**
     * 分页查询
     *
     * @param inputVO 入参
     * @return 分页数据
     */
    public IPage<MonitoringConfigurationPageOutputVO> getPage(MonitoringConfigurationPageInputVO inputVO) {
        Page<MonitoringConfigurationPageOutputVO> resultPage = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());

        MonitoringConfigurationPageQueryDto queryDto = new MonitoringConfigurationPageQueryDto();
        BeanUtils.copyProperties(inputVO, queryDto);
        String order = inputVO.getOrder();
        if (StringUtils.isEmpty(order)) {
            queryDto.setSortKey("updated_time");
            queryDto.setSortType("DESC");
        } else {
            String sortType = order.substring(order.indexOf("_") + 1);
            String sortKey = order.substring(0, order.indexOf("_"));
            sortKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortKey);
            queryDto.setSortKey(sortKey);
            queryDto.setSortType(sortType);
        }

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        queryDto.setDeptCodes(roleDataAuthority.getDeptCodes());
        queryDto.setUserNames(roleDataAuthority.getUserNames());
        IPage<VarProcessMonitoringAlertConf> pageList = varProcessMonitoringAlertConfService.getPage(resultPage, queryDto);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return resultPage;
        }
        List<VarProcessMonitoringAlertConf> records = pageList.getRecords();

        List<String> userNameList = records.stream().flatMap(item -> Stream.of(item.getCreatedUser(), item.getUpdatedUser())).distinct().collect(Collectors.toList());
        //查找用户全名
        Map<String, String> userFullNameMap = userService.findFullNameMapByUserNames(userNameList);

        ArrayList<MonitoringConfigurationPageOutputVO> resultList = new ArrayList<>(inputVO.getSize());
        for (VarProcessMonitoringAlertConf conf : records) {
            MonitoringConfigurationPageOutputVO monitoringConfigurationPageOutputVO = new MonitoringConfigurationPageOutputVO();
            BeanUtils.copyProperties(conf, monitoringConfigurationPageOutputVO);
            monitoringConfigurationPageOutputVO.setCreatedTime(DateUtil.parseDateToStr(conf.getCreatedTime(), DateUtil.FORMAT_LONG));
            monitoringConfigurationPageOutputVO.setUpdatedTime(DateUtil.parseDateToStr(conf.getUpdatedTime(), DateUtil.FORMAT_LONG));
            monitoringConfigurationPageOutputVO.setServiceName(conf.getServiceName());
            monitoringConfigurationPageOutputVO.setServiceVersion(conf.getServiceVersion());
            monitoringConfigurationPageOutputVO.setManifestName(conf.getManifestName());

            //告警通知方式、告警等级
            MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSON.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);
            List<MonitoringConfNotifyMethodEnum> notifyMethodList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(alertInfo.getUserListByNote())) {
                notifyMethodList.add(MonitoringConfNotifyMethodEnum.MESSAGE);
            }
            if (!CollectionUtils.isEmpty(alertInfo.getUserListByEmail())) {
                notifyMethodList.add(MonitoringConfNotifyMethodEnum.EMAIL);
            }
            if (BooleanUtils.isTrue(alertInfo.getFeishuEnabled())) {
                notifyMethodList.add(MonitoringConfNotifyMethodEnum.FEI_SHU);
            }
            monitoringConfigurationPageOutputVO.setNotifyMethodList(notifyMethodList);
            monitoringConfigurationPageOutputVO.setAlertGrade(alertInfo.getAlertGrade());

            //指标名称
            if (conf.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
                StringBuilder sb = new StringBuilder();
                MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration =
                        JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class);
                for (MonitorConfigurationSaveInputVO.MonitoringObject monitoringObject : paramConfiguration.getMonitoringObjectList()) {
                    sb.append(monitoringObject.getVariableName()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                monitoringConfigurationPageOutputVO.setVariableName(sb.toString());
            }

            //用户名
            monitoringConfigurationPageOutputVO.setCreatedUser(userFullNameMap.get(conf.getCreatedUser()));
            monitoringConfigurationPageOutputVO.setUpdatedUser(userFullNameMap.get(conf.getUpdatedUser()));
            resultList.add(monitoringConfigurationPageOutputVO);
        }

        resultPage.setRecords(resultList);
        resultPage.setTotal(pageList.getTotal());
        resultPage.setCurrent(pageList.getTotal());
        return resultPage;
    }


    /**
     * 查看详情
     *
     * @param id 配置ID
     * @return 监控配置
     */
    public MonitorConfigurationSaveInputVO view(Long id) {
        final VarProcessMonitoringAlertConf conf = varProcessMonitoringAlertConfService.getById(id);
        if (conf == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "规则不存在");
        }

        MonitorConfigurationSaveInputVO monitorConfigurationSaveInputVO = new MonitorConfigurationSaveInputVO();
        monitorConfigurationSaveInputVO.setId(id);
        monitorConfigurationSaveInputVO.setMonitoringType(conf.getMonitoringType());
        monitorConfigurationSaveInputVO.setConfName(conf.getConfName());
        monitorConfigurationSaveInputVO.setServiceName(conf.getServiceName());
        monitorConfigurationSaveInputVO.setServiceVersion(conf.getServiceVersion());
        if (conf.getMonitoringType() == MonitoringConfTypeEnum.VARIABLE) {
            VarProcessManifest varProcessManifest = varProcessManifestService.getOne(new LambdaQueryWrapper<VarProcessManifest>().
                    select(VarProcessManifest::getId)
                    .eq(VarProcessManifest::getVarManifestName, conf.getManifestName())
                    .eq(VarProcessManifest::getDeleteFlag, USABLE.getCode()));
            if (varProcessManifest == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "清单不存在");
            }
            monitorConfigurationSaveInputVO.setManifestId(varProcessManifest.getId());
            monitorConfigurationSaveInputVO.setManifestName(conf.getManifestName());
        }
        monitorConfigurationSaveInputVO.setMonitoringState(conf.getMonitoringState());
        monitorConfigurationSaveInputVO.setConfDesc(conf.getConfDesc());
        monitorConfigurationSaveInputVO.setParamConfiguration(JSONObject.parseObject(conf.getParamConfigurationInfo(), MonitorConfigurationSaveInputVO.ParamConfiguration.class));
        monitorConfigurationSaveInputVO.setTriggerCondition(JSONObject.parseObject(conf.getTriggerCondition(), MonitorConfigurationSaveInputVO.TriggerCondition.class));
        monitorConfigurationSaveInputVO.setAlertInfo(JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class));
        return monitorConfigurationSaveInputVO;
    }


    /**
     * 根据实时服务id获取清单list
     *
     * @param serviceName 服务名称
     * @param version     版本
     * @return map
     */
    public Map<String, Long> getManifestList(String serviceName, Integer version) {

        VarProcessServiceVersion varProcessService = varProcessServiceVersionService.findServiceByNameAndVersion(serviceName,version);
        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, varProcessService.getId()));
        List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());

        if (CollectionUtils.isEmpty(manifestIds)) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<VarProcessManifest> varProcessManifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        varProcessManifestLambdaQueryWrapper.eq(VarProcessManifest::getDeleteFlag, USABLE.getCode());
        varProcessManifestLambdaQueryWrapper.in(VarProcessManifest::getId, manifestIds);
        varProcessManifestLambdaQueryWrapper.select(VarProcessManifest::getVarManifestName, VarProcessManifest::getId);
        return varProcessManifestService.list(varProcessManifestLambdaQueryWrapper)
                .stream().collect(Collectors.toMap(VarProcessManifest::getVarManifestName, VarProcessManifest::getId));
    }

    /**
     * 创建定时任务
     *
     * @param conf 告警信息
     */
    private void saveJob(VarProcessMonitoringAlertConf conf) {
        MonitorConfigurationSaveInputVO.AlertInfo alertInfo = JSONObject.parseObject(conf.getAlertInfo(), MonitorConfigurationSaveInputVO.AlertInfo.class);
        //定时任务参数
        MonitoringAlterTaskParam monitoringAlterTaskParam = new MonitoringAlterTaskParam(conf.getId());
        //定时任务名称
        String jobName = JOB_NAME + conf.getId() + "_" + conf.getConfName();
        //开始时间
        Long startDate = alertInfo.getStartDate().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //结束时间
        Long endDate = alertInfo.getEndDate().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //生成corn表达式
        String corn = jobOperateUtil.getCronStr(alertInfo.getMonitorFrequencyTimeUnit(), alertInfo.getMonitorFrequencyTime());
        log.info("监控预警：创建定时任务,corn->{}", corn);
        JobInfoDTO job = jobOperateUtil.findJob(jobName);
        if (job == null) {
            jobOperateUtil.addBasicCronJob(jobName, MonitoringAlterProcessor.class, corn, jobName, monitoringAlterTaskParam, startDate, endDate);
        } else {
            jobOperateUtil.updateBasicCronJob(jobName, MonitoringAlterProcessor.class, corn, jobName, monitoringAlterTaskParam, startDate, endDate);
        }
    }


    /**
     * 删除定时任务
     *
     * @param conf 告警信息
     */
    private void removeTask(VarProcessMonitoringAlertConf conf) {
        String jobName = JOB_NAME + conf.getId() + "_" + conf.getConfName();
        jobOperateUtil.deleteJob(jobName);
    }


    private void removeCache(VarProcessMonitoringAlertConf varProcessMonitoringAlertConf) {
        if (varProcessMonitoringAlertConf.getMonitoringType() == MonitoringConfTypeEnum.SERVICE) {
            //删除正常次数缓存
            if (cacheClient.exists(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + varProcessMonitoringAlertConf.getId() + varProcessMonitoringAlertConf.getServiceName())) {
                cacheClient.evict(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + varProcessMonitoringAlertConf.getServiceName());
            }
            //删除最后一次沉默时间
            if (cacheClient.exists(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + varProcessMonitoringAlertConf.getId() + varProcessMonitoringAlertConf.getServiceName())) {
                cacheClient.evict(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + varProcessMonitoringAlertConf.getId() + varProcessMonitoringAlertConf.getServiceName());
            }
        } else {
            MonitorConfigurationSaveInputVO.ParamConfiguration paramConfiguration = JSONObject.parseObject(varProcessMonitoringAlertConf.getParamConfigurationInfo(),
                    MonitorConfigurationSaveInputVO.ParamConfiguration.class);
            for (MonitorConfigurationSaveInputVO.MonitoringObject monitoringObject : paramConfiguration.getMonitoringObjectList()) {
                //删除正常次数缓存
                if (cacheClient.exists(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + varProcessMonitoringAlertConf.getId() + monitoringObject.getVariableName())) {
                    cacheClient.evict(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_RESTORE_COUNT + varProcessMonitoringAlertConf.getId() + monitoringObject.getVariableName());
                }
                //删除最后一次沉默时间
                if (cacheClient.exists(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + varProcessMonitoringAlertConf.getId() + monitoringObject.getVariableName())) {
                    cacheClient.evict(CacheKeyPrefixConstant.VAR_MONITORING_ALTER_LAST_SEND_MESSAGE_DATE + varProcessMonitoringAlertConf.getId() + monitoringObject.getVariableName());
                }
            }
        }
    }
}
