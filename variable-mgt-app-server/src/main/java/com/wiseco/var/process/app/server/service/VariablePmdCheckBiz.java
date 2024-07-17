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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.auth.common.DepartmentCriteria;
import com.wiseco.auth.common.DepartmentDTO;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.auth.common.UserSmallDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.user.DepartmentClient;
import com.wiseco.boot.user.UserClient;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceAuthorizationInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceAuthorizationSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceAuthorizationOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionVo;
import com.wiseco.var.process.app.server.controller.vo.output.VarSimpleServiceOutputDto;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceActionEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.BaseEntity;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorization;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorizationService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceCycle;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.CacheEventSendService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: liusiyu
 * @since: 2023/9/30
 */
@Service
@Slf4j
public class VariablePmdCheckBiz {
    public static final int EXCEL_MAX_CHARS = 32767;
    @Autowired
    private VarProcessServiceCycleService varProcessServiceCycleService;
    @Autowired
    @Qualifier("internalJdbcTemplate")
    private JdbcTemplate internalJdbcTemplate;
    @Resource
    private DepartmentClient departmentClient;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private VarProcessAuthorizationServiceService varProcessAuthorizationServiceService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CacheEventSendService cacheEventSendService;

    /**
     * getSpaceSimpleService
     *
     * @param spaceId 空间Id
     * @return java.util.List
     */
    public List<VarSimpleServiceOutputDto> getSpaceSimpleService(Long spaceId) {
        List<VarSimpleServiceOutputDto> resultList = new ArrayList<>();
        List<ServiceInfoDto> allServiceInfos = varProcessServiceVersionService.findAllServiceInfos(null,null);
        if (!CollectionUtils.isEmpty(allServiceInfos)) {
            allServiceInfos.forEach(s -> resultList.add(VarSimpleServiceOutputDto.builder().id(s.getId()).name(s.getName()).build()));
        }
        return resultList;
    }


    private static void changeCodeIntoName(List<DomainDataModelTreeDto> requestTreeDtoList, Map<String, String> dicCodeNameMap) {
        if (requestTreeDtoList == null) {
            return;
        }

        Iterator<DomainDataModelTreeDto> iterator = requestTreeDtoList.iterator();
        while (iterator.hasNext()) {
            DomainDataModelTreeDto treeDto = iterator.next();
            if ("1".equals(treeDto.getIsExtend())) {
                iterator.remove();
                continue;
            }

            if (treeDto.getEnumName() != null) {
                treeDto.setEnumName(dicCodeNameMap.getOrDefault(treeDto.getEnumName(), ""));
            }
            treeDto.setLabel(treeDto.getDescribe());
            if (!CollectionUtils.isEmpty(treeDto.getChildren())) {
                changeCodeIntoName(treeDto.getChildren(), dicCodeNameMap);
            }
        }
    }

    /**
     * 获取某个实时服务中，调用成功的次数
     * @param params 传入的参数
     * @return 某个实时服务的执行次数
     */
    private Long countLog(Object[] params) {
        // 1.定义SQL语句
        String sql = "SELECT COUNT(*) FROM var_process_log WHERE interface_type = 1 and service_id = ? and result_status = ?";
        // 2.查询并返回
        return internalJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    /**
     * 提供给监控报表的公共接口, 用于获取服务名称(版本)的列表
     * @return 服务名称的列表
     */
    public List<Map<String, Object>> getServiceNameList() {
        // 1.查出实时服务的名称+启用的版本号
        List<VarProcessServiceStateEnum> states = new ArrayList<>();
        states.add(VarProcessServiceStateEnum.ENABLED);
        states.add(VarProcessServiceStateEnum.DISABLED);
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<ServiceInfoDto> services = varProcessServiceVersionService.findServiceListByState(states,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        // 2.准备结果的返回
        if (CollectionUtils.isEmpty(services)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (ServiceInfoDto service : services) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", service.getId());
            item.put("name", service.getName() + MagicStrings.LEFT_BRACKET + service.getVersion() + MagicStrings.RIGHT_BRACKET);
            item.put("state", service.getState());
            result.add(item);
        }
        return result;
    }

    /**
     * 提供给监控规则的公共接口, 用于获取服务名称(版本)的列表
     * @return 服务名称的列表
     */
    public List<ServiceVersionVo> getServiceNameAndVersionList() {
        // 1.查出实时服务的名称+启用的版本号
        List<VarProcessServiceStateEnum> states = Collections.singletonList(VarProcessServiceStateEnum.ENABLED);
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<ServiceInfoDto> services = varProcessServiceVersionService.findServiceListByState(states,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        // 2.填充返回的实体类
        List<ServiceVersionVo> result = new ArrayList<>();
        for (ServiceInfoDto item : services) {
            result.add(new ServiceVersionVo(item.getName(), item.getVersion(), null));
        }
        for (ServiceVersionVo dto : result) {
            List<Integer> versions = getVersions(dto.getServiceName());
            if (!versions.contains(dto.getVersion())) {
                versions.add(dto.getVersion());
            }
            Collections.sort(versions);
            dto.setVersions(versions);
        }
        // 4.返回结果
        return result;
    }

    /**
     * 获取某个实时服务有调用记录的所有版本号
     * @param name 服务的名称
     * @return 版本号集合
     */
    public List<Integer> getVersions(String name) {
        // 1.定义SQL语句
        String sql = "SELECT DISTINCT interface_version FROM var_process_log WHERE interface_type = 1 AND service_name = ?";
        // 2.查询并返回
        return internalJdbcTemplate.queryForList(sql, Integer.class, name);
    }

    /**
     * 授权列表
     * @param inputDto 入参
     * @return page
     */
    public Page<ServiceAuthorizationOutputVo> listAuthorization(ServiceAuthorizationInputVo inputDto) {
        LambdaQueryWrapper<VarProcessAuthorization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VarProcessAuthorization::getDeleteFlag,DeleteFlagEnum.USABLE.getCode())
                .eq(!StringUtils.isEmpty(inputDto.getCallerDept()), VarProcessAuthorization::getCallerDept,inputDto.getCallerDept())
                .eq(inputDto.getEnabled() != null, VarProcessAuthorization::getEnabled,inputDto.getEnabled())
                .eq(!StringUtils.isEmpty(inputDto.getCreatedDept()), VarProcessAuthorization::getCreatedDept,inputDto.getCreatedDept())
                .like(!StringUtils.isEmpty(inputDto.getCaller()), VarProcessAuthorization::getCaller,inputDto.getCaller())
                .orderByDesc(VarProcessAuthorization::getUpdatedTime);

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()),VarProcessAuthorization::getCreatedDept, roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()),VarProcessAuthorization::getCreatedUser, roleDataAuthority.getUserNames());

        Page<VarProcessAuthorization> queryPage = authorizationService.page(new Page<>(inputDto.getCurrentNo(), inputDto.getSize()), queryWrapper);

        List<String> userNames = queryPage.getRecords().stream().flatMap(obj -> Stream.of(obj.getCreatedUser(), obj.getUpdatedUser()))
                .distinct()
                .collect(Collectors.toList());
        Map<String,String> fullNameMap = userService.findFullNameMapByUserNames(userNames);

        //处理部门字段
       List<DepartmentDTO> departmentList = departmentClient.findDepartmentList(new DepartmentCriteria());
        Map<String, String> deptMap = convertDeptTreeToMap(departmentList);

        List<ServiceAuthorizationOutputVo> outputVos = new ArrayList<>();
        queryPage.getRecords().forEach(entity -> {
            ServiceAuthorizationOutputVo outputVo = new ServiceAuthorizationOutputVo();
            BeanUtils.copyProperties(entity,outputVo);
            outputVo.setCallerDeptCode(entity.getCallerDept());
            outputVo.setCreatedUser(fullNameMap.getOrDefault(entity.getCreatedUser(),""));
            outputVo.setUpdatedUser(fullNameMap.getOrDefault(entity.getUpdatedUser(),""));
            outputVo.setCreatedDept(deptMap.getOrDefault(entity.getCreatedDept(),""));
            outputVo.setCallerDept(deptMap.getOrDefault(entity.getCallerDept(),""));
            outputVos.add(outputVo);
        });

        Page<ServiceAuthorizationOutputVo> resultPage = new Page<>();
        resultPage.setCurrent(queryPage.getCurrent());
        resultPage.setSize(queryPage.getSize());
        resultPage.setTotal(queryPage.getTotal());
        resultPage.setRecords(outputVos);
        return resultPage;
    }

    Map<String, String> convertDeptTreeToMap(List<DepartmentDTO> departmentList) {
        Map<String, String> departmentMap = new HashMap<>(MagicNumbers.EIGHT);
        for (DepartmentDTO department : departmentList) {
            // 将部门对象加入到Map中，键为部门的id
            departmentMap.put(department.getCode(), department.getName());
            // 递归处理子部门
            if (department.getChildren() != null && !department.getChildren().isEmpty()) {
                Map<String, String> childrenMap = convertDeptTreeToMap(department.getChildren());
                // 将子部门的Map合并到当前部门的Map中
                departmentMap.putAll(childrenMap);
            }
        }
        return departmentMap;
    }

    /**
     * 保存授权
     * @param inputDto 入参
     * @return id
     */
    public Long saveAuthorization(VariableServiceAuthorizationSaveInputDto inputDto) {

        //校验：调用方不允许重复，提示“调用方已存在”；
        List<VarProcessAuthorization> list = authorizationService.list(Wrappers.<VarProcessAuthorization>lambdaQuery()
                .eq(VarProcessAuthorization::getCaller,inputDto.getCaller())
                .eq(VarProcessAuthorization::getDeleteFlag,DeleteFlagEnum.USABLE.getCode())
                .ne(inputDto.getId() != null,VarProcessAuthorization::getId,inputDto.getId()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"调用方已存在");
        }

        VarProcessAuthorization serviceAuthorization = new VarProcessAuthorization();
        BeanUtils.copyProperties(inputDto,serviceAuthorization);
        serviceAuthorization.setUpdatedUser(SessionContext.getSessionUser().getUsername());

        if (inputDto.getId() != null) {
            serviceAuthorization.setId(inputDto.getId());

            VarProcessAuthorization authorization = authorizationService.getById(inputDto.getId());
            serviceAuthorization.setEnabled(authorization == null ? null : authorization.getEnabled());
        } else {
            serviceAuthorization.setAuthorizationCode(GenerateIdUtil.generateRandomCode(inputDto.getCaller()));
            serviceAuthorization.setCreatedUser(serviceAuthorization.getUpdatedUser());
            DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
            if (department != null) {
                serviceAuthorization.setCreatedDept(department.getCode());
            }
        }

        serviceAuthorization.setUpdatedTime(new Date());
        authorizationService.saveOrUpdate(serviceAuthorization);

        if (BooleanUtils.isTrue(serviceAuthorization.getEnabled())) {
            // 启用状态更新缓存
            cacheEventSendService.authorizationChange();
        }
        return serviceAuthorization.getId();
    }

    /**
     * 删除授权
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthorization(Long id) {
        removeAuthorizationCheck(id);
        authorizationService.update(Wrappers.<VarProcessAuthorization>lambdaUpdate()
                .eq(VarProcessAuthorization::getId, id)
                .set(VarProcessAuthorization::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));
        //物理删除授权引用服务关系
        varProcessAuthorizationServiceService.remove(Wrappers.<VarProcessAuthorizationService>lambdaQuery()
                .eq(VarProcessAuthorizationService::getAuthorizationId, id));
        cacheEventSendService.authorizationChange();
    }

    /**
     * 获取简单服务列表(启用)
     * @param spaceId 空间id
     * @param excludeCodes 排除的codelist
     * @param keyWord 服务名称\编码搜索
     * @param size 页面大小
     * @param currentNo 当前页码
     * @return Page
     */
    public Page<VarSimpleServiceOutputDto> findSimpleUpServiceList(Long spaceId, String keyWord,List<String> excludeCodes,int currentNo,int size) {
        //数据权限控制
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        return varProcessRealtimeServiceService.findSimpleUpServiceList(new Page<>(currentNo,size),keyWord,excludeCodes,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
    }

    /**
     * 保存授权配置
     * @param authorizationId 授权id
     * @param serviceList 服务编码list
     */
    public void saveAuthorizationConfig(Long authorizationId,List<String> serviceList) {
        varProcessAuthorizationServiceService.remove(Wrappers.<VarProcessAuthorizationService>lambdaQuery()
                .eq(VarProcessAuthorizationService::getAuthorizationId,authorizationId));

        List<VarProcessAuthorizationService> newList = serviceList.stream().map(serviceCode -> {
            VarProcessAuthorizationService varProcessAuthorizationService = new VarProcessAuthorizationService();
            varProcessAuthorizationService.setAuthorizationId(authorizationId);
            varProcessAuthorizationService.setServiceCode(serviceCode);
            varProcessAuthorizationService.setCreatedUser(SessionContext.getSessionUser().getUsername());
            varProcessAuthorizationService.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            return varProcessAuthorizationService;
        }).collect(Collectors.toList());

        authorizationService.update(Wrappers.<VarProcessAuthorization>lambdaUpdate()
                .eq(VarProcessAuthorization::getId,authorizationId).set(VarProcessAuthorization::getUpdatedTime,new Date())
                .set(VarProcessAuthorization::getUpdatedUser,SessionContext.getSessionUser().getUsername()));
        varProcessAuthorizationServiceService.saveBatch(newList);
        cacheEventSendService.authorizationConfigChange();
    }

    /**
     * 获取授权服务配置
     * @param authorizationId 授权id
     * @return page
     */
    public List<VarSimpleServiceOutputDto> getAuthorizationConfig(Long authorizationId) {
        List<String> serviceCodes = varProcessAuthorizationServiceService.list(Wrappers.<VarProcessAuthorizationService>lambdaQuery()
                        .eq(VarProcessAuthorizationService::getAuthorizationId, authorizationId)).stream()
                .map(VarProcessAuthorizationService::getServiceCode).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(serviceCodes)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<VarProcessRealtimeService> queryWrapper = new QueryWrapper<VarProcessRealtimeService>().lambda()
                .eq(VarProcessRealtimeService::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .in(VarProcessRealtimeService::getServiceCode,serviceCodes)
                .select(VarProcessRealtimeService::getId, VarProcessRealtimeService::getServiceName, VarProcessRealtimeService::getServiceCode);
        List<VarProcessRealtimeService> queryResult = varProcessRealtimeServiceService.list(queryWrapper);
        return queryResult.stream().map(s -> VarSimpleServiceOutputDto.builder().id(s.getId()).name(s.getServiceName()).code(s.getServiceCode()).build()).collect(Collectors.toList());
    }

    /**
     * 更新授权启用状态
     * @param id 授权id
     * @param actionType 1：启用；0：停用
     * @return 授权id
     */
    public Long updateAuthorization(Long id, Integer actionType) {
        authorizationService.update(Wrappers.<VarProcessAuthorization>lambdaUpdate()
                .eq(VarProcessAuthorization::getId, id)
                .eq(VarProcessAuthorization::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .set(VarProcessAuthorization::getEnabled, actionType == 1)
                .set(VarProcessAuthorization::getUpdatedUser,SessionContext.getSessionUser().getUsername()));
        cacheEventSendService.authorizationChange();
        return id;
    }

    /**
     * 若调用方已产生调用记录，不能删除，提示文案：“该服务已产生调用记录，禁止删除”
     * @param id 授权id
     */
    public void removeAuthorizationCheck(Long id) {
        VarProcessAuthorization authorization = authorizationService.getById(id);
        Assert.notNull(authorization,"未查询到调用方相关信息");
        Object[] params = new Object[]{authorization.getCaller()};
        String sql = "select count(*) from var_process_log vpl where caller = ? ";
        Long count = internalJdbcTemplate.queryForObject(sql, params, Long.class);
        if (count != null && count != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该调用方已产生调用记录，无法删除");
        }
    }

    /**
     * 根据serviceId查看它的生命周期
     * @param serviceId 实时服务ID
     * @return 返回的生命周期结构
     */
    public List<TabDto> getServiceProperties(Long serviceId) {
        // 1.定义返回体
        List<TabDto> result = new ArrayList<>();
        // 2.获取生命周期
        result.add(buildLifeCyclePanelInfo(serviceId));
        // 3.返回结果
        return result;
    }

    /**
     * 获取生命周期的面板信息
     * @param id 实时服务的Id
     * @return 实时服务的生命周期信息
     */
    private TabDto buildLifeCyclePanelInfo(Long id) {
        // 1.先获取这个实时服务对应的变动记录
        List<VarProcessServiceCycle> varProcessServiceCycles = varProcessServiceCycleService.list(Wrappers.<VarProcessServiceCycle>lambdaQuery()
                .eq(VarProcessServiceCycle::getServiceId, id)
                .ne(VarProcessServiceCycle::getOperation, VarProcessServiceActionEnum.EDIT.getCode())
                .orderByDesc(BaseEntity::getCreatedTime));
        // 2.生成表格头
        List<TableContent.TableHeadInfo> tableHead = new ArrayList<>();
        tableHead.add(TableContent.TableHeadInfo.builder().lable("状态").key("status").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作类型").key("operation").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作人").key("operaUserName").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作时间").key("operaTime").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("备注").key("description").build());
        // 3.生成表格内容
        List<JSONObject> tableData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(varProcessServiceCycles)) {
            varProcessServiceCycles.forEach(lifeCycle -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", lifeCycle.getStatus().getDesc());
                jsonObject.put("operation", Objects.requireNonNull(VarProcessServiceActionEnum.getAction(Integer.valueOf(lifeCycle.getOperation()))).getDesc());
                UserSmallDTO user = userClient.getUser(lifeCycle.getCreatedUser());
                if (user != null) {
                    jsonObject.put("operaUserName", user.getFullname());
                } else {
                    jsonObject.put("operaUserName", null);
                }
                jsonObject.put("operaTime", DateUtil.parseDateToStr(lifeCycle.getCreatedTime(), MagicStrings.DATE_TIME_FORMAT));
                jsonObject.put("description", lifeCycle.getDescription());
                tableData.add(jsonObject);
            });
        }
        // 4.组装列表内容
        TableContent tableContent = TableContent.builder()
                .tableHead(tableHead)
                .tableData(tableData)
                .build();
        List<PanelDto> panelDtoList = new ArrayList<>();
        panelDtoList.add(PanelDto.builder()
                .title("生命周期")
                .type(LocalDataTypeEnum.LIFECYCLE.getCode())
                .datas(tableContent)
                .build());
        return TabDto.builder().name("生命周期").content(panelDtoList).build();
    }

    /**
     * 查看授权码
     * @param id 授权id
     * @return 授权码
     */
    public String getAuthCode(Long id) {
        VarProcessAuthorization auth = authorizationService.getOne(Wrappers.<VarProcessAuthorization>lambdaQuery()
                .eq(VarProcessAuthorization::getId, id)
                .eq(VarProcessAuthorization::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (auth == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_AUTH_NOT_FOUND,"调用方不存在");
        }
        return auth.getAuthorizationCode();
    }
}
