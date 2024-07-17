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
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.ExcelDomainInterfaceUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.dto.DsServiceInterfaceDocumentOutputDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DsServiceInterfaceSheetNameEnum;
import com.decision.jsonschema.util.model.ExportExcelJsonSchemaModel4DsServiceInterface;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.auth.common.UserDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.security.SessionUser;
import com.wiseco.boot.user.DepartmentClient;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.commons.util.ExcelExportUtil;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.ServiceBasicConfigVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceDataModelMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ServiceManifestMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceAddVersionInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceVersionListInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableServiceConfigInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionInfoOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestDocumentOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableServiceConfigOutputVo;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.ManifestPublishStateEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicSpaceTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessManifestDocumentStatusCodeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceActionEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.BaseEntity;
import com.wiseco.var.process.app.server.repository.entity.VarProcessAuthorizationService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceCycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.CacheEventSendService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.ServiceQueryDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VarProcessServiceVersionInfo;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestClassService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestInternalService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestOutsideService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import static com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum.USABLE;

@Service
@Slf4j
@RefreshScope
public class ServiceBiz {

    public static final int EXCEL_MAX_CHARS = 32767;
    public static final int NON_DISABLED_VERSION = 1;
    public static final int ALL_VERSION = 2;
    public static final String XML = "XML";

    @Autowired
    private VarProcessRealtimeServiceService varProcessRealtimeServiceService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private VarProcessCategoryService varProcessCategoryService;

    @Autowired
    private VarProcessCategoryBiz varProcessCategoryBiz;

    @Autowired
    private UserService userService;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Autowired
    private VarProcessServiceCycleService varProcessServiceCycleService;

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private DepartmentClient departmentClient;

    @Autowired
    private SysDynamicServiceBiz sysDynamicServiceBiz;

    @Autowired
    private VarProcessAuthorizationServiceService varProcessAuthorizationServiceService;

    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;

    @Autowired
    private VarProcessDictService varProcessDictService;

    @Autowired
    private VarProcessDataModelService varProcessDataModelService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CacheEventSendService cacheEventSendService;

    @Resource
    private VarProcessManifestClassService varProcessManifestClassService;
    @Resource
    private VarProcessManifestOutsideService varProcessManifestOutsideService;

    @Resource
    private VarProcessManifestInternalService varProcessManifestInternalService;

    @Resource
    private DmAdapter dmAdapter;

    @Value("${gateway.url}")
    private String nacosGatewayUrl;


    /**
     * 服务列表
     *
     * @param inputVO 入参
     * @return page
     */
    public Page<RestServiceListOutputVO> serviceList(ServiceListInputVO inputVO) {
        dmAdapter.modifyGroupOptFlagOfConfigJdbc();
        Page<RestServiceListOutputVO> resultVoPage = new Page<>(inputVO.getCurrentPage(), inputVO.getPageSize());

        ServiceQueryDto serviceQueryDto = new ServiceQueryDto();
        BeanUtils.copyProperties(inputVO, serviceQueryDto);
        serviceQueryDto.setKeyWord(inputVO.getServiceNameOrServiceCode());

        //* 获取分类Map<分类id，分类>
        Map<Long, VarProcessCategory> categoryMap = varProcessCategoryService.list(Wrappers.<VarProcessCategory>lambdaQuery()
                        .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessCategory::getEnabled, 1).eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.SERVICE))
                .stream().collect(Collectors.toMap(VarProcessCategory::getId, item -> item, (key1, key2) -> key1));
        //获取分类下所有分类的ids
        Long categoryId = inputVO.getServiceCategoryId();
        if (categoryId != null) {
            List<Long> categoryIdList = new ArrayList<>();
            categoryIdList.add(categoryId);
            VarProcessCategoryBiz.getCategoriesUndered(categoryMap, categoryIdList, categoryId);
            serviceQueryDto.setCategoryIds(categoryIdList);
        }

        //构造权限过滤条件
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        serviceQueryDto.setDeptCodes(roleDataAuthority.getDeptCodes());
        serviceQueryDto.setUserNames(roleDataAuthority.getUserNames());

        //查询基本信息
        resultVoPage.setCountId("findServiceBasicInfoCount");
        Page<VarProcessRealtimeService> serviceBasicInfos = varProcessRealtimeServiceService.findServiceBasicInfo(resultVoPage, serviceQueryDto);
        List<VarProcessRealtimeService> basicInfoQueryRecords = serviceBasicInfos.getRecords();
        if (CollectionUtils.isEmpty(basicInfoQueryRecords)) {
            return resultVoPage;
        }

        List<RestServiceListOutputVO> resultVoList = assembleListOutputVos(categoryMap, basicInfoQueryRecords);

        BeanUtils.copyProperties(serviceBasicInfos, resultVoPage);
        resultVoPage.setRecords(resultVoList);
        return resultVoPage;
    }

    @NotNull
    private List<RestServiceListOutputVO> assembleListOutputVos(Map<Long, VarProcessCategory> categoryMap, List<VarProcessRealtimeService> basicInfoQueryRecords) {
        List<RestServiceListOutputVO> resultVoList = new ArrayList<>();

        // 获取 REST 服务网关 URL
        String spaceCode = varProcessSpaceService.getById(1L).getCode();
        String spaceUrl = nacosGatewayUrl + MessageFormat.format("/{0}/", spaceCode);

        List<Long> serviceIds = basicInfoQueryRecords.stream().map(VarProcessRealtimeService::getId).collect(Collectors.toList());
        Map<Long, VarProcessServiceVersionInfo> serviceVersionInfoMap = varProcessServiceVersionService.findServiceInfos(serviceIds)
                .stream().collect(Collectors.toMap(VarProcessServiceVersionInfo::getServiceId, item -> item));

        basicInfoQueryRecords.forEach(serviceBasicInfo -> {
            VarProcessServiceVersionInfo versionInfo = serviceVersionInfoMap.get(serviceBasicInfo.getId());
            boolean hasVersionInfo = versionInfo != null;
            int upCount = hasVersionInfo ? versionInfo.getUpCount() : 0;
            int downCount = hasVersionInfo ? versionInfo.getDownCount() : 0;
            int maxVersion = hasVersionInfo ? versionInfo.getMaxVersion() : 0;
            int versionCount = hasVersionInfo ? versionInfo.getVersionCount() : 0;
            resultVoList.add(RestServiceListOutputVO.builder().serviceId(serviceBasicInfo.getId()).serviceName(serviceBasicInfo.getServiceName()).serviceCode(serviceBasicInfo.getServiceCode()).serviceCategoryId(serviceBasicInfo.getCategoryId())
                    .serviceCategoryName(categoryMap.get(serviceBasicInfo.getCategoryId()) == null ? null : categoryMap.get(serviceBasicInfo.getCategoryId()).getName())
                    .enableTrace(serviceBasicInfo.getEnableTrace()).messageFormat(serviceBasicInfo.getMessageFormat()).url(spaceUrl + serviceBasicInfo.getServiceCode())
                    .hasAlreadyDeploy(hasVersionInfo && (upCount + downCount > 0)).hasEnabled(hasVersionInfo && (upCount > 0))
                    .maxVersion(maxVersion).versionCount(versionCount).build());
        });
        return resultVoList;
    }

    /**
     * 新建实时服务
     *
     * @param inputDto 入参
     * @return 实时服务identifier
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveVariableService(ServiceSaveInputVO inputDto) {
        // 校验名称及编码重复性
        checkServiceCodeName(inputDto);

        SessionUser user = SessionContext.getSessionUser();

        VarProcessRealtimeService service = new VarProcessRealtimeService();
        if (inputDto.getId() != null) {
            service = varProcessRealtimeServiceService.getById(inputDto.getId());
        } else {
            service.setSpaceId(inputDto.getSpaceId());
            service.setDeleteFlag(USABLE.getCode());
            service.setCreatedUser(user.getUsername());
            service.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
        }
        service.setServiceCode(inputDto.getCode());
        service.setServiceName(inputDto.getName());
        service.setCategoryId(inputDto.getCategoryId());
        service.setMessageFormat(inputDto.getMessageFormat());
        service.setEnableTrace(inputDto.getEnableTrace());
        service.setUpdatedUser(user.getUsername());
        service.setUpdatedTime(new Date());

        varProcessRealtimeServiceService.saveOrUpdate(service);

        if (inputDto.getId() == null) {
            // 创建版本 1
            String deptCode = null;
            DepartmentSmallDTO smallDepartmentByUserName = departmentClient.findSmallDepartmentByUserName(user.getUsername());
            if (smallDepartmentByUserName != null) {
                deptCode = smallDepartmentByUserName.getCode();
            }
            VarProcessServiceVersion serviceVersion = VarProcessServiceVersion.builder().serviceId(service.getId()).serviceVersion(MagicNumbers.ONE).state(VarProcessServiceStateEnum.EDITING)
                    .createdUser(user.getUsername()).updatedUser(user.getUsername()).deptCode(deptCode).deleteFlag(USABLE.getCode()).build();
            varProcessServiceVersionService.save(serviceVersion);
            recordServiceCycle(serviceVersion.getId(), VarProcessServiceActionEnum.CREATE, VarProcessServiceStateEnum.EDITING, null);
        }

        cacheEventSendService.realtimeServiceChange();
        return service.getId();
    }

    private void checkServiceCodeName(ServiceSaveInputVO inputDto) {
        long duplicateCode = varProcessRealtimeServiceService.count(Wrappers.<VarProcessRealtimeService>lambdaQuery().eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode())
                .eq(VarProcessRealtimeService::getServiceCode, inputDto.getCode())
                .ne(!StringUtils.isEmpty(inputDto.getId()), VarProcessRealtimeService::getId, inputDto.getId()));
        if (duplicateCode != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_EXISTS, MessageFormat.format("已经存在编码为[{0}]的服务", inputDto.getCode()));
        }
        long duplicateName = varProcessRealtimeServiceService.count(Wrappers.<VarProcessRealtimeService>lambdaQuery().eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode())
                .eq(VarProcessRealtimeService::getServiceName, inputDto.getName())
                .ne(!StringUtils.isEmpty(inputDto.getId()), VarProcessRealtimeService::getId, inputDto.getId()));
        if (duplicateName != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_EXISTS, MessageFormat.format("已经存在名称为[{0}]的服务", inputDto.getName()));
        }
    }

    /**
     * 删除实时服务
     *
     * @param id 服务id
     */
    public void deleteVariableService(Long id) {
        VarProcessRealtimeService service = varProcessRealtimeServiceService.getById(id);
        //校验
        deleteCheck(service);

        service.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        varProcessRealtimeServiceService.updateById(service);
        cacheEventSendService.realtimeServiceChange();
    }

    /**
     * 删除校验
     *
     * @param service 服务
     */
    protected void deleteCheck(VarProcessRealtimeService service) {
        if (service == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND);
        }
        //校验
        long countAuthorizations = varProcessAuthorizationServiceService.count(Wrappers.<VarProcessAuthorizationService>lambdaQuery()
                .eq(VarProcessAuthorizationService::getServiceCode, service.getServiceCode()));
        if (countAuthorizations != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该服务已有调用方，无法删除");
        }

        long countVersions = varProcessServiceVersionService.count(Wrappers.<VarProcessServiceVersion>lambdaQuery().eq(VarProcessServiceVersion::getServiceId, service.getId())
                .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode()));
        if (countVersions != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "存在服务版本数据无法删除");
        }
    }

    /**
     * 保存系统动态
     *
     * @param operateTypeEnum 系统动态操作类型枚举
     * @param spaceId         空间 ID
     * @param serviceName     服务名称
     */
    private void saveDynamic(SysDynamicOperateTypeEnum operateTypeEnum, Long spaceId, String serviceName) {
        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().spaceType(SysDynamicSpaceTypeEnum.VARIABLE.getCode())
                .varSpaceId(spaceId).operateType(operateTypeEnum.getName()).typeEnum(SysDynamicBusinessBucketEnum.VARIABLE_SERVICE).businessId(spaceId)
                .businessDesc(serviceName).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }

    /**
     * 记录实时服务-生命周期
     *
     * @param serviceId        实时服务ID
     * @param actionEnum       行为枚举
     * @param serviceStateEnum 操作后的状态枚举
     * @param description      日志
     */
    public void recordServiceCycle(Long serviceId, VarProcessServiceActionEnum actionEnum, VarProcessServiceStateEnum serviceStateEnum, String description) {
        VarProcessServiceCycle newServiceCycle = VarProcessServiceCycle.builder().serviceId(serviceId).operation(actionEnum.getCode().shortValue())
                .status(serviceStateEnum).description(description).createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername()).build();
        varProcessServiceCycleService.save(newServiceCycle);
    }

    /**
     * 添加实时服务的版本(重新创建)
     *
     * @param inputDto 输入VO
     * @return 新实时服务的版本ID
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long addVersionByCreate(ServiceAddVersionInputVO inputDto) {
        // 1.获取当前实时服务(与code相关的)的最大版本
        Integer maxVersion = varProcessServiceVersionService.getMaxVersionByServiceId(inputDto.getId());
        // 2.组装实体类
        UserDTO user = SessionContext.getSessionUser().getUser();
        VarProcessServiceVersion entity = VarProcessServiceVersion.builder()
                .serviceId(inputDto.getId()).serviceVersion(maxVersion).deptCode(user.getDepartment().getCode()).state(VarProcessServiceStateEnum.EDITING)
                .serialNo(null).deleteFlag(USABLE.getCode()).description(inputDto.getDescription()).createdUser(user.getUsername()).updatedUser(user.getUsername())
                .build();
        // 3.数据库入库+记录生命周期
        varProcessServiceVersionService.save(entity);
        recordServiceCycle(entity.getId(), VarProcessServiceActionEnum.CREATE, VarProcessServiceStateEnum.EDITING, null);
        return entity.getId();
    }

    /**
     * 添加实时服务的版本(复制已有版本)
     *
     * @param inputDto 输入VO
     * @return 新实时服务的版本ID
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long addVersionByCopy(ServiceAddVersionInputVO inputDto) {
        // 1.获取当前实时服务(与code相关的)的最大版本
        Integer maxVersion = varProcessServiceVersionService.getMaxVersionByServiceId(inputDto.getId());
        // 2.先复制实体类
        UserDTO user = SessionContext.getSessionUser().getUser();
        VarProcessServiceVersion originalService = varProcessServiceVersionService.getById(inputDto.getCopiedServiceId());
        if (originalService == null || originalService.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND, "复制的服务版本已经被删除或不存在");
        }
        originalService.setId(null);
        originalService.setServiceId(inputDto.getId());
        originalService.setServiceVersion(maxVersion);
        originalService.setState(VarProcessServiceStateEnum.EDITING);
        originalService.setDescription(inputDto.getDescription());
        originalService.setCreatedUser(user.getUsername());
        originalService.setUpdatedUser(user.getUsername());
        originalService.setCreatedTime(null);
        originalService.setUpdatedTime(null);
        varProcessServiceVersionService.save(originalService);
        // 3.再复制实时服务-变量清单的关系
        List<VarProcessServiceManifest> list = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, inputDto.getCopiedServiceId()));
        if (!CollectionUtils.isEmpty(list)) {
            for (VarProcessServiceManifest serviceManifest : list) {
                Date now = new Date();
                Short usable = (short) (now.before(serviceManifest.getValidTime()) || (!ObjectUtil.isEmpty(serviceManifest.getInvalidTime()) && now.after(serviceManifest.getInvalidTime())) ? 0 : 1);
                serviceManifest.setId(null);
                serviceManifest.setServiceId(originalService.getId());
                serviceManifest.setCurrentExecuteCount(Long.valueOf(String.valueOf(MagicNumbers.ZERO)));
                serviceManifest.setCreatedUser(user.getUsername());
                serviceManifest.setUpdatedUser(user.getUsername());
                serviceManifest.setCreatedTime(null);
                serviceManifest.setUpdatedTime(null);
                serviceManifest.setUsable(usable);
                serviceManifest.setManifestPublishState(ManifestPublishStateEnum.TOBEPUBLISHED);
            }
            varProcessServiceManifestService.saveBatch(list);
        }
        // 4.记录生命周期
        recordServiceCycle(originalService.getId(), VarProcessServiceActionEnum.CREATE, VarProcessServiceStateEnum.EDITING, null);
        return originalService.getId();
    }

    /**
     * 分页查询服务所有版本
     *
     * @param inputDto 入参
     * @return list
     */
    public Page<ServiceVersionInfoOutputVo> versionList(ServiceVersionListInputVO inputDto) {

        Page<ServiceVersionInfoOutputVo> resultPage = new Page<>();

        List<VarProcessServiceVersion> queryRecords;
        if (inputDto.getScene() == NON_DISABLED_VERSION) {
            //非停用版本
            queryRecords = varProcessServiceVersionService.findNonDisabledVersionsByServiceId(inputDto);
        } else if (inputDto.getScene() == ALL_VERSION) {
            //所有版本，分页
            Page<VarProcessServiceVersion> queryPage = varProcessServiceVersionService.page(new Page<>(inputDto.getCurrentNo(), inputDto.getSize()), Wrappers.<VarProcessServiceVersion>lambdaQuery()
                    .eq(VarProcessServiceVersion::getServiceId, inputDto.getServiceId()).eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode())
                    .orderByDesc(VarProcessServiceVersion::getServiceVersion));
            BeanUtils.copyProperties(queryPage, resultPage);
            queryRecords = queryPage.getRecords();
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "请传入正确的scene");
        }

        //组装返回数据
        List<ServiceVersionInfoOutputVo> outputVos = assembleVersionInfoVos(queryRecords);
        resultPage.setRecords(outputVos);
        return resultPage;
    }

    @NotNull
    private List<ServiceVersionInfoOutputVo> assembleVersionInfoVos(List<VarProcessServiceVersion> queryRecords) {
        //用户名map
        List<String> userNames = queryRecords.stream().flatMap(obj -> Stream.of(obj.getCreatedUser(), obj.getUpdatedUser()))
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userNames);

        //部门map
        List<String> deptCodes = queryRecords.stream().map(VarProcessServiceVersion::getDeptCode).collect(Collectors.toList());
        Map<String, String> departmentCodeNameMap = deptService.findDeptMapByDeptCodes(deptCodes);

        // 最近一次拒绝审批信息Map集合
        List<VarProcessServiceCycle> lifecycleList = varProcessServiceCycleService.list(new QueryWrapper<VarProcessServiceCycle>().lambda()
                .eq(VarProcessServiceCycle::getStatus, VarProcessServiceStateEnum.REJECTED));
        Map<Long, VarProcessServiceCycle> latestLifecycleMap = lifecycleList.stream()
                .collect(Collectors.toMap(
                        VarProcessServiceCycle::getServiceId,
                        lifecycle -> lifecycle,
                        (existing, replacement) -> existing.getId() > replacement.getId() ? existing : replacement
                ));

        Map<Long, List<String>> manifestNameMap = varProcessServiceManifestService.findManifestNameMap(queryRecords.stream().map(VarProcessServiceVersion::getId).collect(Collectors.toList()));

        List<ServiceVersionInfoOutputVo> outputVos = queryRecords.stream().map(serviceVersion -> {
            ServiceVersionInfoOutputVo serviceVersionInfoOutputVo = ServiceVersionInfoOutputVo.builder().versionId(serviceVersion.getId()).description(serviceVersion.getDescription())
                    .version("V" + serviceVersion.getServiceVersion()).updatedTime(serviceVersion.getUpdatedTime()).createTime(serviceVersion.getCreatedTime())
                    .updateUser(fullNameMap.getOrDefault(serviceVersion.getUpdatedUser(), "")).createdUser(fullNameMap.getOrDefault(serviceVersion.getCreatedUser(), ""))
                    .manifestNames(manifestNameMap.getOrDefault(serviceVersion.getId(), new ArrayList<>())).state(serviceVersion.getState())
                    .createDepartment(departmentCodeNameMap.getOrDefault(serviceVersion.getDeptCode(), "")).build();
            if (serviceVersion.getState() == VarProcessServiceStateEnum.REJECTED) {
                VarProcessServiceCycle serviceCycle = latestLifecycleMap.get(serviceVersion.getId());
                if (serviceCycle != null && VarProcessServiceStateEnum.REJECTED == serviceVersion.getState()) {
                    JSONObject desc = new JSONObject();
                    desc.put("审核人", fullNameMap.getOrDefault(serviceCycle.getCreatedUser(), ""));
                    desc.put("审核时间", serviceCycle.getCreatedTime());
                    desc.put("拒绝原因", serviceCycle.getDescription());
                    serviceVersionInfoOutputVo.setApproDescription(desc.toJSONString());
                }
            }
            return serviceVersionInfoOutputVo;
        }).collect(Collectors.toList());
        return outputVos;
    }

    /**
     * 删除服务版本
     *
     * @param id 版本id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(Long id) {
        // 1.更新实时服务的删除状态
        SessionUser sessionUser = SessionContext.getSessionUser();
        VarProcessServiceVersion serviceVersion = varProcessServiceVersionService.getById(id);
        serviceVersion.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
        serviceVersion.setUpdatedUser(sessionUser.getUsername());
        serviceVersion.setDeptCode(sessionUser.getUser().getDepartment().getCode());
        serviceVersion.setUpdatedTime(new Date());
        varProcessServiceVersionService.updateById(serviceVersion);
        // 2.记录生命周期
        recordServiceCycle(id, VarProcessServiceActionEnum.DELETE, null, null);
        // 3.删除清单引用关系
        LambdaQueryWrapper<VarProcessServiceManifest> serviceManifestWrapper = new LambdaQueryWrapper<>();
        serviceManifestWrapper.eq(VarProcessServiceManifest::getServiceId, id);
        varProcessServiceManifestService.remove(serviceManifestWrapper);
    }

    /**
     * 获取服务配置信息: 基本信息+引用的变量清单信息+数据模型信息+流水号
     *
     * @param versionId 服务版本id
     * @return 服务信息VO
     */
    public VariableServiceConfigOutputVo versionDetail(Long versionId) {
        // 1.设置基本信息&流水号绑定
        VarProcessServiceVersion version = varProcessServiceVersionService.getById(versionId);
        VarProcessRealtimeService service = varProcessRealtimeServiceService.getById(version.getServiceId());
        ServiceBasicConfigVo basicConfig = ServiceBasicConfigVo.builder()
                .spaceId(service.getSpaceId()).serviceId(service.getId()).serviceName(service.getServiceName()).code(service.getServiceCode())
                .versionId(version.getId()).version(version.getServiceVersion()).categoryId(service.getCategoryId())
                .description(version.getDescription()).enableTrace(service.getEnableTrace()).messageFormat(service.getMessageFormat()).build();
        Map<Long, String> categoryNameMap = varProcessCategoryService.getCategoryNameMap(service.getSpaceId());
        basicConfig.setCategory(categoryNameMap.getOrDefault(service.getCategoryId(), null));
        VariableServiceConfigOutputVo outputVo = new VariableServiceConfigOutputVo();
        outputVo.setServiceBasicConfig(basicConfig);
        outputVo.setSerialNumberBinding(version.getSerialNo());
        // 2.查询使用的变量清单信息
        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery().eq(VarProcessServiceManifest::getServiceId, basicConfig.getVersionId()));
        List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());
        List<ServiceManifestMappingVo> manifestMappingVos = assembleServiceManifestMappingVos(versionId, serviceManifestList, manifestIds);
        outputVo.setServiceManifestMappings(manifestMappingVos);
        // 3.查询数据模型&手动添加的入参对象相关信息
        List<ServiceDataModelMappingVo> dataModelMappingVos = serviceDataModelMappings(manifestIds);
        outputVo.setServiceDataModelMappings(dataModelMappingVos);
        return outputVo;
    }

    List<ServiceDataModelMappingVo> serviceDataModelMappings(List<Long> manifestIds) {
        ArrayList<ServiceDataModelMappingVo> dataModelMappingVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(manifestIds)) {
            List<ServiceDataModelMappingVo> dependentDataModelVos = varProcessManifestDataModelService.getDataModelInfos(manifestIds).stream()
                    .map(modelEntity -> new ServiceDataModelMappingVo(modelEntity.getId(), modelEntity.getObjectName(), modelEntity.getObjectLabel(), modelEntity.getVersion(), 1, 0)).collect(Collectors.toList());
            dataModelMappingVos.addAll(dependentDataModelVos);
        }
        return dataModelMappingVos;
    }

    /**
     * 组装服务清单映射vo
     *
     * @param serviceId           服务id
     * @param serviceManifestList list
     * @param manifestIds         清单id list
     * @return list
     */
    protected List<ServiceManifestMappingVo> assembleServiceManifestMappingVos(Long serviceId, List<VarProcessServiceManifest> serviceManifestList, List<Long> manifestIds) {
        //获取清单详情
        List<ServiceManifestMappingVo> manifestMappingVos = new ArrayList<>();
        if (!org.springframework.util.CollectionUtils.isEmpty(manifestIds)) {
            Map<Long, ServiceManifestDetailOutputVo> manifestDetailMap = varProcessServiceManifestService.getManifestDetail(manifestIds).stream().collect(Collectors.toMap(ServiceManifestDetailOutputVo::getManifestId, item -> item));
            manifestMappingVos = serviceManifestList.stream()
                    .map(item -> {
                                ServiceManifestDetailOutputVo detail = manifestDetailMap.get(item.getManifestId());
                                ServiceManifestMappingVo mappingVo = ServiceManifestMappingVo.builder()
                                        .manifestId(item.getManifestId()).manifestName(detail == null ? null : item.getManifestRole().equals((short) MagicNumbers.ONE) ? "（" + MagicStrings.ZHU + "）" + detail.getManifestName() : "（" + MagicStrings.ASYNC + "）" + detail.getManifestName())
                                        .description(detail == null ? null : detail.getDescription()).countVariable(detail == null ? null : detail.getCountVariable())
                                        .role(item.getManifestRole()).immediateEffect(ObjectUtils.nullSafeEquals(item.getImmediateEffect(), 1))
                                        .validTime(item.getValidTime()).invalidTime(item.getInvalidTime())
                                        .currentExecuteCount(item.getCurrentExecuteCount())
//                                        .totalExecuteCount(item.getTotalExecuteCount())
                                        .manifestPublishState(item.getManifestPublishState())
                                        .build();
                                if (1 != mappingVo.getRole()) {
                                    mappingVo.setTotalExecuteCount(item.getTotalExecuteCount());
                                }
                                return mappingVo;
                            }
                    ).collect(Collectors.toList());
            //进行排序
            sortServiceManifestMapping(manifestMappingVos);
        }
        return manifestMappingVos;
    }

    private void sortServiceManifestMapping(List<ServiceManifestMappingVo> manifestMappingVos) {
        manifestMappingVos.sort((vo1, vo2) -> {
            // 首先按照role字段排序，1在0前面
            int roleCompare = Integer.compare((vo2.getRole() != null) ? vo2.getRole() : Integer.MIN_VALUE,
                    (vo1.getRole() != null) ? vo1.getRole() : Integer.MIN_VALUE);
            // 如果role相同，根据validTime字段排序
            if (roleCompare == 0) {
                if (vo1.getValidTime() == null && vo2.getValidTime() == null) {
                    return 0;
                } else if (vo1.getValidTime() == null) {
                    return MagicNumbers.MINUS_INT_1;
                } else if (vo2.getValidTime() == null) {
                    return 1;
                } else {
                    return vo1.getValidTime().compareTo(vo2.getValidTime());
                }
            }
            return roleCompare;
        });
    }

    /**
     * 获取实时服务及版本号
     *
     * @return list
     */
    public List<ServiceListOutputVo> findServiceListWithVersions() {
        List<ServiceListOutputVo> outputVoList = new ArrayList<>();

        List<VarProcessRealtimeService> services = varProcessRealtimeServiceService.list(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                .eq(VarProcessRealtimeService::getDeleteFlag, USABLE.getCode()));
        Map<Long, List<VarProcessServiceVersion>> serviceVersionMap = varProcessServiceVersionService.list(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode())
                .orderByDesc(VarProcessServiceVersion::getServiceVersion)).stream().collect(Collectors.groupingBy(VarProcessServiceVersion::getServiceId));
        services.forEach(service -> {
            List<Map<Integer, Long>> versions = serviceVersionMap.getOrDefault(service.getId(), new ArrayList<>()).stream().map(version -> {
                Map<Integer, Long> map = new HashMap<>(MagicNumbers.EIGHT);
                map.put(version.getServiceVersion(), version.getId());
                return map;
            }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(versions)) {
                ServiceListOutputVo outputVo = new ServiceListOutputVo();
                outputVo.setServiceCode(service.getServiceCode());
                outputVo.setServiceName(service.getServiceName());
                outputVo.setCalledVersions(versions);
                outputVoList.add(outputVo);
            }
        });
        return outputVoList;
    }

    /**
     * 更新服务版本信息
     *
     * @param inputDto 入参
     * @return 版本id
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long updateVersion(VariableServiceConfigInputVo inputDto) {
        ServiceBasicConfigVo serviceBasicConfig = inputDto.getServiceBasicConfig();
        //更新流水号及描述信息
        if (StringUtils.isEmpty(serviceBasicConfig.getDescription())) {
            serviceBasicConfig.setDescription("");
        }
        SessionUser sessionUser = SessionContext.getSessionUser();
        varProcessServiceVersionService.update(Wrappers.<VarProcessServiceVersion>lambdaUpdate()
                .eq(VarProcessServiceVersion::getId, serviceBasicConfig.getVersionId())
                .set(VarProcessServiceVersion::getSerialNo, inputDto.getSerialNumberBinding())
                .set(VarProcessServiceVersion::getDescription, serviceBasicConfig.getDescription())
                .set(VarProcessServiceVersion::getUpdatedUser, sessionUser.getUsername()));
        // 2.更新服务-变量清单引用信息
        // 2.1先获取旧的引用信息
        List<Long> oldManifestIds = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getVarProcessSpaceId, serviceBasicConfig.getSpaceId())
                .eq(VarProcessServiceManifest::getServiceId, serviceBasicConfig.getVersionId())
        ).stream().map(VarProcessServiceManifest::getManifestId).collect(Collectors.toList());
        // 2.2再获取新的引用信息
        List<Long> newManifestIds = inputDto.getServiceManifestMappings()
                .stream()
                .map(ServiceManifestMappingVo::getManifestId)
                .collect(Collectors.toList());
        // 2.3获取要删除的引用信息
        List<Long> deleteManifestIds = new ArrayList<>();
        for (Long item : oldManifestIds) {
            if (!newManifestIds.contains(item)) {
                // 新的引用信息有，但旧的引用信息没有的
                deleteManifestIds.add(item);
            }
        }
        List<ServiceManifestMappingVo> serviceManifestMappings = getServiceManifestMappingVos(inputDto, serviceBasicConfig, deleteManifestIds);
        // 2.8分开处理，新的manifestId插入，不变的manifestId就更新
        Date now = new Date();
        Long minusOne = Long.valueOf(String.valueOf(MagicNumbers.MINUS_INT_1));
        for (ServiceManifestMappingVo item : serviceManifestMappings) {
            Short usable = (short) (now.before(item.getValidTime()) || (!ObjectUtil.isEmpty(item.getInvalidTime()) && now.after(item.getInvalidTime())) ? 0 : 1);
            if (!oldManifestIds.contains(item.getManifestId())) {
                // 新的变量清单--插入
                VarProcessServiceManifest object = VarProcessServiceManifest.builder()
                        .serviceId(serviceBasicConfig.getVersionId()).manifestId(item.getManifestId()).manifestRole(item.getRole()).varProcessSpaceId(serviceBasicConfig.getSpaceId())
                        .immediateEffect(0).totalExecuteCount(item.getTotalExecuteCount() == null ? minusOne : item.getTotalExecuteCount())
                        .validTime(item.getValidTime()).invalidTime(item.getInvalidTime())
                        .currentExecuteCount(0L).createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).usable(usable)
                        .build();
                varProcessServiceManifestService.save(object);
            } else {
                // 老的变量清单--更新
                VarProcessServiceManifest one = varProcessServiceManifestService.getOne(Wrappers.<VarProcessServiceManifest>lambdaQuery().eq(VarProcessServiceManifest::getServiceId, serviceBasicConfig.getVersionId()).eq(VarProcessServiceManifest::getManifestId, item.getManifestId()));
                if (!one.getManifestRole().equals(item.getRole())) {
                    one.setCurrentExecuteCount(0L);
                }
                one.setManifestRole(item.getRole());
                one.setImmediateEffect(0);
                one.setValidTime(item.getValidTime());
                one.setInvalidTime(item.getInvalidTime());
                one.setTotalExecuteCount(item.getTotalExecuteCount() == null ? minusOne : item.getTotalExecuteCount());
                one.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                one.setUsable(usable);
                one.setServiceId(serviceBasicConfig.getVersionId());
                one.setManifestId(item.getManifestId());
                varProcessServiceManifestService.updateById(one);
            }
        }
        // 状态是不变的
        return serviceBasicConfig.getVersionId();
    }

    /**
     * 实时服务中的变量清单预处理
     *
     * @param inputVo            前端发送过来的输入实体
     * @param serviceBasicConfig 实时服务基本配置
     * @param deleteManifestIds  要删除的变量清单Id列表
     * @return 经过预处理后的实时服务中的变量清单
     */
    private List<ServiceManifestMappingVo> getServiceManifestMappingVos(VariableServiceConfigInputVo inputVo, ServiceBasicConfigVo serviceBasicConfig, List<Long> deleteManifestIds) {
        // 2.4删除旧的引用信息
        deleteManifestIds.forEach(manifestId -> varProcessServiceManifestService.remove(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getVarProcessSpaceId, serviceBasicConfig.getSpaceId())
                .eq(VarProcessServiceManifest::getServiceId, serviceBasicConfig.getVersionId())
                .eq(VarProcessServiceManifest::getManifestId, manifestId)
        ));
        List<ServiceManifestMappingVo> serviceManifestMappings = inputVo.getServiceManifestMappings();
        // 2.5如果有立即生效，就生成时间
        for (ServiceManifestMappingVo item : serviceManifestMappings) {
            if (item.getImmediateEffect()) {
                // 立即生效
                item.setValidTime(new Date());
            }
        }
        // 2.6先按照role升序排序，再按照validTime升序排序
        serviceManifestMappings.sort(new Comparator<ServiceManifestMappingVo>() {
            @Override
            public int compare(ServiceManifestMappingVo o1, ServiceManifestMappingVo o2) {
                // 先role
                int roleComparison = Short.compare(o1.getRole(), o2.getRole());
                if (roleComparison != 0) {
                    return roleComparison;
                }
                // role相同，就validTime
                return o1.getValidTime().compareTo(o2.getValidTime());
            }
        });
        // 2.7处理serviceManifestMappings的失效时间
        for (int i = 0; i < serviceManifestMappings.size(); i++) {
            if (serviceManifestMappings.get(i).getRole().equals((short) 1)) {
                if (i + 1 <= serviceManifestMappings.size() - 1) {
                    if (!ObjectUtils.isEmpty(serviceManifestMappings.get(i + 1))) {
                        serviceManifestMappings.get(i).setInvalidTime(serviceManifestMappings.get(i + 1).getValidTime());
                    }
                } else {
                    // 没有下一个，就是无限制了
                    serviceManifestMappings.get(i).setInvalidTime(null);
                }
            }
        }
        return serviceManifestMappings;
    }

    /**
     * 获取接口文档
     *
     * @param serviceId 服务id
     * @return 接口文档
     */
    public VariableManifestDocumentOutputDto getInterfaceDocument(Long serviceId) {
        // 查询服务
        VarProcessRealtimeService service = varProcessRealtimeServiceService.getById(serviceId);
        if (null == service) {
            return null;
        }

        // 0. 基本信息
        VariableManifestDocumentOutputDto outputDto = new VariableManifestDocumentOutputDto();
        outputDto.setBasicInfo(getManifestDocumentBasicInfo(service));

        // 5. 返回状态码
        List<VariableManifestDocumentOutputDto.ResponseCodeVo> responseCodeVoList = new LinkedList<>();
        for (VarProcessManifestDocumentStatusCodeEnum codeEnum : VarProcessManifestDocumentStatusCodeEnum.values()) {
            VariableManifestDocumentOutputDto.ResponseCodeVo responseCodeVo = new VariableManifestDocumentOutputDto.ResponseCodeVo(codeEnum.getType(), codeEnum.getCode(), codeEnum.getDescription());
            responseCodeVoList.add(responseCodeVo);
        }
        outputDto.setResponseCode(responseCodeVoList);

        //获取字典map<code,name>
        Map<String, String> dicCodeNameMap = varProcessDictService.list(Wrappers.<VarProcessDict>lambdaQuery().select(VarProcessDict::getCode, VarProcessDict::getName).eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().collect(Collectors.toMap(VarProcessDict::getCode, VarProcessDict::getName, (k1, k2) -> k2));

        //获取启用的版本
        VarProcessServiceVersion enabledVersion = varProcessServiceVersionService.getOne(Wrappers.<VarProcessServiceVersion>lambdaQuery().eq(VarProcessServiceVersion::getServiceId, serviceId)
                .eq(VarProcessServiceVersion::getState, VarProcessServiceStateEnum.ENABLED)
                .eq(VarProcessServiceVersion::getDeleteFlag, USABLE.getCode()));
        Assert.notNull(enabledVersion, "该服务不存在启用版本，无法查看接口文档");

        // 请求参数和请求示例
        generateInterfaceRequest(enabledVersion.getId(), outputDto, dicCodeNameMap);

        //返回参数和返回示例
        generateInterfaceResponse(enabledVersion.getId(), outputDto, dicCodeNameMap);

        return outputDto;
    }

    /**
     * 构建接口文档基本信息
     * <p>TODO: dataFormat, requestMethod and protocol options are currently hard-coded</p>
     *
     * @param service 实时服务实体类
     * @return 变量清单文档 - 基本信息 VO
     */
    private VariableManifestDocumentOutputDto.BasicInfoVo getManifestDocumentBasicInfo(VarProcessRealtimeService service) {
        VarProcessSpace space = varProcessSpaceService.getById(service.getSpaceId());
        return VariableManifestDocumentOutputDto.BasicInfoVo.builder().name(service.getServiceName()).type(VarProcessServiceTypeEnum.REAL_TIME.getDesc()).dataFormat(service.getMessageFormat().name())
                .url(nacosGatewayUrl + MessageFormat.format("/{0}/{1}", space.getCode(), service.getServiceCode())).requestMethod("POST").protocol("HTTP").build();
    }

    /**
     * 获取服务接口返回参数及示例
     *
     * @param outputDto
     * @param dicCodeNameMap
     * @param serviceId
     */
    private void generateInterfaceResponse(Long serviceId, VariableManifestDocumentOutputDto outputDto, Map<String, String> dicCodeNameMap) {
        // 3. 返回结果
        // 查询服务使用的变量 List
        List<VarProcessVariable> variableList = varProcessServiceManifestService.findManifestOutputVariableList(serviceId);
        // 返回结果树形结构 List
        List<DomainDataModelTreeDto> responseTreeDtoList = new ArrayList<>(MagicNumbers.THREE);
        changeCodeIntoName(responseTreeDtoList, dicCodeNameMap);
        // 返回结果响应码
        DomainDataModelTreeDto responseCode = DomainDataModelTreeDto.builder().name("code").value("code").label("响应码").type(DataVariableTypeEnum.STRING_TYPE.getMessage()).isArr("0").isEmpty("0").build();
        responseTreeDtoList.add(responseCode);
        // 返回结果提示信息
        DomainDataModelTreeDto responseMessage = DomainDataModelTreeDto.builder().name("message").value("message").label("提示信息").type(DataVariableTypeEnum.STRING_TYPE.getMessage()).isArr("0").isEmpty("0").build();
        responseTreeDtoList.add(responseMessage);
        // 返回结果数据
        DomainDataModelTreeDto responseData = DomainDataModelTreeDto.builder().name("data").value("data").label("数据").type(DataVariableTypeEnum.OBJECT_TYPE.getMessage()).isArr("0").isEmpty("0").build();
        responseTreeDtoList.add(responseData);
        List<DomainDataModelTreeDto> responseDataChildrenList = new LinkedList<>();
        for (VarProcessVariable publishingVariable : variableList) {
            // 将待发布变量 List 添加到返回结果数据
            DomainDataModelTreeDto responseDataChild = DomainDataModelTreeDto.builder().name(publishingVariable.getName()).value(publishingVariable.getName()).label(publishingVariable.getLabel()).type(publishingVariable.getDataType()).isArr("0").isEmpty("0").build();
            responseDataChildrenList.add(responseDataChild);
        }
        responseData.setChildren(responseDataChildrenList);
        outputDto.setResponseStructure(responseTreeDtoList);

        // 4. 返回示例
        JSONObject responseSampleDatagramData = new JSONObject(true);
        for (DomainDataModelTreeDto treeDto : responseDataChildrenList) {
            // 遍历返回结果数据树形结构, 组装返回示例报文 JSON Object
            responseSampleDatagramData.put(treeDto.getName(), generateSampleDatagram(treeDto));
        }
        JSONObject responseSampleDatagram = new JSONObject(true);
        responseSampleDatagram.put("code", "200");
        responseSampleDatagram.put("message", "操作成功");
        responseSampleDatagram.put("data", responseSampleDatagramData);
        outputDto.setResponseSample(responseSampleDatagram);
    }

    /**
     * 获取服务接口请求参数和示例
     *
     * @param dicCodeNameMap
     * @param serviceId
     * @param outputDto
     */
    private void generateInterfaceRequest(Long serviceId, VariableManifestDocumentOutputDto outputDto, Map<String, String> dicCodeNameMap) {
        // 1.请求参数
        // 拿到使用的数据模型list
        List<DomainDataModelTreeDto> requestTreeDtoList;
        List<Long> manifestIds = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery().eq(VarProcessServiceManifest::getServiceId, serviceId)).stream().filter(Objects::nonNull).map(VarProcessServiceManifest::getManifestId).collect(Collectors.toList());
        List<ServiceDataModelMappingVo> dataModelMappingVoList = serviceDataModelMappings(manifestIds);
        List<Long> modelIds = dataModelMappingVoList.stream().filter(Objects::nonNull).map(ServiceDataModelMappingVo::getId).collect(Collectors.toList());
        if (!org.springframework.util.CollectionUtils.isEmpty(modelIds)) {
            List<VarProcessDataModel> models = varProcessDataModelService.list(Wrappers.<VarProcessDataModel>lambdaQuery().in(VarProcessDataModel::getId, modelIds).eq(VarProcessDataModel::getObjectSourceType, VarProcessDataModelSourceType.OUTSIDE_PARAM));

            //将数据模型的content转换成树结构放入请求参数list
            requestTreeDtoList = models.stream().map(model -> DomainModelTreeEntityUtils.transferDataModelTreeDto(model.getContent(), new HashSet<>())).collect(Collectors.toList());

            changeCodeIntoName(requestTreeDtoList, dicCodeNameMap);
            outputDto.setRequestStructure(requestTreeDtoList);
            // 2.请求示例
            JSONObject requestSampleDatagram = new JSONObject(true);
            for (DomainDataModelTreeDto treeDto : requestTreeDtoList) {
                // 遍历并处理所有属性, 组装请求示例 JSON Object
                requestSampleDatagram.put(treeDto.getName(), generateSampleDatagram(treeDto));
            }

            if (outputDto.getBasicInfo().getDataFormat().equalsIgnoreCase(StringPool.XML)) {

                cn.hutool.json.JSONObject rootObject = JSONUtil.createObj().set("input", requestSampleDatagram);
                // 将 JSONObject 转换为 XML 字符串
                String xmlStr = cn.hutool.json.XML.toXml(rootObject);
                outputDto.setRequestSampleStr(xmlStr);
            } else {
                outputDto.setRequestSampleStr(requestSampleDatagram.toString());
            }

            outputDto.setRequestSample(requestSampleDatagram);
        }
    }

    private void changeCodeIntoName(List<DomainDataModelTreeDto> requestTreeDtoList, Map<String, String> dicCodeNameMap) {
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
            if (!org.springframework.util.CollectionUtils.isEmpty(treeDto.getChildren())) {
                changeCodeIntoName(treeDto.getChildren(), dicCodeNameMap);
            }
        }
    }

    /**
     * 根据领域数据模型树形结构 DTO 生成示例报文
     * <p>适用于请求示例/返回示例生成</p>
     *
     * @param treeDto 领域数据模型树形结构 DTO (type 字段需为 object)
     * @return 示例报文随机数据
     * <p>"treeDto.getName()": ${random_generated_sample_value}</p>
     */
    private Object generateSampleDatagram(DomainDataModelTreeDto treeDto) {
        // 将数据类型转换为枚举类
        DataVariableTypeEnum typeEnum = DataVariableTypeEnum.getMessageEnum(treeDto.getType());
        if (null == typeEnum) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "暂时不支持变量类型 " + treeDto.getType());
        }
        if (!StringPool.ONE.equals(treeDto.getIsArr())) {
            // 生成随机数据
            if (DataVariableTypeEnum.OBJECT_TYPE != typeEnum) {
                // 非对象类型: 生成随机样例数据
                return generateSampleDatagramRandomValue(typeEnum);
            } else if (!org.springframework.util.CollectionUtils.isEmpty(treeDto.getChildren())) {
                // 对象类型: 递归调用
                // 示例报文 JSON Object
                JSONObject childSampleDatagram = new JSONObject(true);
                // 遍历 TreeDto 属性
                for (DomainDataModelTreeDto child : treeDto.getChildren()) {
                    childSampleDatagram.put(child.getName(), generateSampleDatagram(child));
                }
                return childSampleDatagram;
            }
        } else {
            // 生成随机数据数组
            JSONArray randomValueArray = new JSONArray();
            int arraySize = RandomUtils.nextInt(1, MagicNumbers.FIVE);
            for (int i = 0; i < arraySize; i++) {
                if (DataVariableTypeEnum.OBJECT_TYPE != typeEnum) {
                    // 非对象类型: 生成随机样例数据
                    randomValueArray.add(generateSampleDatagramRandomValue(typeEnum));
                } else if (!org.springframework.util.CollectionUtils.isEmpty(treeDto.getChildren())) {
                    // 对象类型: 递归调用
                    // 示例报文 JSON Object
                    JSONObject childSampleDatagram = new JSONObject(true);
                    // 遍历 TreeDto 属性
                    for (DomainDataModelTreeDto child : treeDto.getChildren()) {
                        childSampleDatagram.put(child.getName(), generateSampleDatagram(child));
                    }
                    randomValueArray.add(childSampleDatagram);
                }
            }
            return randomValueArray;
        }
        return null;
    }

    /**
     * 根据数据类型为示例报文生成随机数据
     *
     * @param typeEnum 数据类型枚举类
     * @return 不同类型的随机数据
     */
    private Object generateSampleDatagramRandomValue(DataVariableTypeEnum typeEnum) {
        Object randomValue = null;
        switch (typeEnum) {
            case INT_TYPE:
                randomValue = RandomUtils.nextInt(0, MagicNumbers.INT_101);
                break;
            case DOUBLE_TYPE:
                randomValue = RandomUtils.nextDouble(0, MagicNumbers.INT_9999);
                break;
            case BOOLEAN_TYPE:
                randomValue = RandomUtils.nextBoolean();
                break;
            case STRING_TYPE:
                randomValue = RandomStringUtils.randomAlphanumeric(MagicNumbers.FIVE);
                break;
            case DATE_TYPE:
                randomValue = DateUtil.getNow(DateUtil.FORMAT_SHORT);
                break;
            case DATETIME_TYPE:
                randomValue = DateUtil.getNow(DateUtil.FORMAT_LONG);
                break;
            default:
                return randomValue;
        }
        return randomValue;
    }

    /**
     * 导出接口文档excel
     *
     * @param serviceId 服务id
     * @param response  响应
     */
    public void exportInterfaceExcel(Long serviceId, HttpServletResponse response) {
        //获取接口文档内容
        VariableManifestDocumentOutputDto interfaceDocument = getInterfaceDocument(serviceId);
        if (null == interfaceDocument) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "未查询到接口文档。");
        }
        //将dto转换成可处理的类型
        DsServiceInterfaceDocumentOutputDto dsInterfaceDocumentDto = convertDocumentDto(interfaceDocument);
        //待转换为 Excel 的接口文档 JSONSchema 列表
        List<ExportExcelJsonSchemaModel4DsServiceInterface> exportExcelJsonSchemaModelList = new ArrayList<>();

        //基本信息
        ExportExcelJsonSchemaModel4DsServiceInterface basicInfoVo = ExcelDomainInterfaceUtils.setExportExcelBasicInfoVo(dsInterfaceDocumentDto.getBasicInfoVo(), DsServiceInterfaceSheetNameEnum.BASIC_INFO_VO.getDescribe());
        //请求参数
        ExportExcelJsonSchemaModel4DsServiceInterface requestStructure = ExcelDomainInterfaceUtils.setExportExcelRequestStructure(dsInterfaceDocumentDto.getRequestStructure(), DsServiceInterfaceSheetNameEnum.REQUEST_STRUCTURE.getDescribe());
        //请求示例
        ExportExcelJsonSchemaModel4DsServiceInterface requestSample = ExcelDomainInterfaceUtils.setExportExcelDataSample(dsInterfaceDocumentDto.getRequestSample(), DsServiceInterfaceSheetNameEnum.REQUEST_SAMPLE.getDescribe());
        //返回结果
        ExportExcelJsonSchemaModel4DsServiceInterface responseStructure = ExcelDomainInterfaceUtils.setsetExportExcelResponseStructure(dsInterfaceDocumentDto.getResponseStructure(), DsServiceInterfaceSheetNameEnum.RESPONSE_STRUCTURE.getDescribe());
        //返回示例
        ExportExcelJsonSchemaModel4DsServiceInterface responseSample = ExcelDomainInterfaceUtils.setExportExcelDataSample(dsInterfaceDocumentDto.getResponseSample(), DsServiceInterfaceSheetNameEnum.RESPONSE_SAMPLE.getDescribe());
        //返回状态码
        ExportExcelJsonSchemaModel4DsServiceInterface responseCode = ExcelDomainInterfaceUtils.setExportExcelResponseCode(dsInterfaceDocumentDto.getResponseCode(), DsServiceInterfaceSheetNameEnum.RESPONSE_CODE.getDescribe());

        exportExcelJsonSchemaModelList.add(basicInfoVo);
        exportExcelJsonSchemaModelList.add(requestStructure);

        if (XML.equalsIgnoreCase(interfaceDocument.getBasicInfo().getDataFormat())) {
            requestSample.getTableValues()[0][0] = interfaceDocument.getRequestSampleStr();
        }

        splitTableValues(exportExcelJsonSchemaModelList, requestSample, responseStructure);
        splitTableValues(exportExcelJsonSchemaModelList, responseSample, responseCode);

        //组装excel文件名
        VarProcessRealtimeService service = varProcessRealtimeServiceService.getById(serviceId);

        String datePattern = "yyyyMMddHHmmss";
        String formattedDate = new SimpleDateFormat(datePattern).format(new Date());
        String fileName = service.getServiceName() + "_" + service.getServiceCode() + "_" + formattedDate + ".xls";

        // 导出Excel
        try {
            //导出excel数据
            HSSFWorkbook hssfWorkbook = ExcelDomainInterfaceUtils.getExportExcelHssfWorkbook(exportExcelJsonSchemaModelList);
            ExcelExportUtil.setResponseHeader(response, fileName);
            OutputStream outputStream = response.getOutputStream();
            hssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("Exception encountered during exporting data model Excel file: ", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出接口文档 Excel 文件异常。");
        }
    }

    /**
     * 请求示例和响应示例过长截取
     * @param exportExcelJsonSchemaModelList
     * @param requestSample
     * @param responseStructure
     */
    private void splitTableValues(List<ExportExcelJsonSchemaModel4DsServiceInterface> exportExcelJsonSchemaModelList, ExportExcelJsonSchemaModel4DsServiceInterface requestSample, ExportExcelJsonSchemaModel4DsServiceInterface responseStructure) {
        if (requestSample.getTableValues()[0][0].length() >= EXCEL_MAX_CHARS) {
            String[] stringArrays = com.wiseco.var.process.app.server.commons.util.StringUtils.splitString(requestSample.getTableValues()[0][0], EXCEL_MAX_CHARS - 1);
            requestSample.getTableValues()[0] = stringArrays;
        }
        exportExcelJsonSchemaModelList.add(requestSample);
        exportExcelJsonSchemaModelList.add(responseStructure);
    }

    private static DsServiceInterfaceDocumentOutputDto convertDocumentDto(VariableManifestDocumentOutputDto source) {

        DsServiceInterfaceDocumentOutputDto target = new DsServiceInterfaceDocumentOutputDto();

        //拷贝能用copyProperties拷贝的属性
        BeanUtils.copyProperties(source, target);

        //拷贝基本信息
        VariableManifestDocumentOutputDto.BasicInfoVo basicInfo = source.getBasicInfo();
        target.setBasicInfoVo(DsServiceInterfaceDocumentOutputDto.BasicInfoVo.builder().name(basicInfo.getName()).type(basicInfo.getType()).dataFormat(basicInfo.getDataFormat())
                .url(basicInfo.getUrl()).requestMethod(basicInfo.getRequestMethod()).protocol(basicInfo.getProtocol()).build());

        //处理responseCode字段
        if (source.getResponseCode() != null) {
            List<DsServiceInterfaceDocumentOutputDto.ResponseCodeVo> responseCodeVoList = new ArrayList<>();
            for (VariableManifestDocumentOutputDto.ResponseCodeVo sourceResponseCodeVo : source.getResponseCode()) {
                DsServiceInterfaceDocumentOutputDto.ResponseCodeVo targetResponseCodeVo = new DsServiceInterfaceDocumentOutputDto.ResponseCodeVo();
                BeanUtils.copyProperties(sourceResponseCodeVo, targetResponseCodeVo);
                responseCodeVoList.add(targetResponseCodeVo);
            }
            target.setResponseCode(responseCodeVoList);
        }

        return target;
    }

    /**
     * 服务保存校验
     *
     * @param inputVo 入参vo
     */
    public void saveServiceVersionCheck(VariableServiceConfigInputVo inputVo) {
        if (org.springframework.util.CollectionUtils.isEmpty(inputVo.getServiceManifestMappings())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未选择变量清单，不允许保存");
        }
        // 校验是否有主清单
        List<ServiceManifestMappingVo> masterManifests = inputVo.getServiceManifestMappings().stream().filter(item -> item.getRole() != null
                && item.getRole() == 1).collect(Collectors.toList());
        if (org.springframework.util.CollectionUtils.isEmpty(masterManifests)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "变量清单信息中不存在”主清单”信息，不允许保存");
        }
        // 校验主清单生效时间
        for (ServiceManifestMappingVo manifest : masterManifests) {
            for (ServiceManifestMappingVo submanifest : masterManifests) {
                if (!manifest.equals(submanifest) && manifest.getRole().equals((short) 1) && submanifest.getRole().equals((short) 1)) {
                    // 都是主清单
                    boolean flag1 = manifest.getImmediateEffect().equals(true) && submanifest.getImmediateEffect().equals(true);
                    boolean flag2 = (manifest.getValidTime() != null && submanifest.getValidTime() != null && manifest.getValidTime().equals(submanifest.getValidTime()));
                    if (flag1 || flag2) {
                        // 都是立即生效,生效时间都一样
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "主清单[" + manifest.getManifestName() + "]和[" + submanifest.getManifestName() + "]的生效期重复，不允许保存");
                    }
                }
            }
        }
        // 校验主体唯一标识
        if (StringUtils.isEmpty(inputVo.getSerialNumberBinding())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "主体唯一标识不能为空!");
        }
    }

    /**
     * 根据serviceId查看它的生命周期
     *
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
     *
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
        if (!org.springframework.util.CollectionUtils.isEmpty(varProcessServiceCycles)) {
            varProcessServiceCycles.forEach(lifeCycle -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", lifeCycle.getStatus().getDesc());
                jsonObject.put("operation", Objects.requireNonNull(VarProcessServiceActionEnum.getAction(Integer.valueOf(lifeCycle.getOperation()))).getDesc());
                String fullName = userService.getFullNameByUserName(lifeCycle.getCreatedUser());
                if (!StringUtils.isEmpty(fullName)) {
                    jsonObject.put("operaUserName", fullName);
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
     * downloadDeployFile
     * @param versionId
     * @return Map<String, Object>
     */
    public Map<String, Object> downloadDeployFile(Long versionId) {
        Map<String, Object> dataMap = new HashMap<>(MagicNumbers.SIXTEEN);
        //实时服务版本
        VarProcessServiceVersion serviceVersion = varProcessServiceVersionService.getById(versionId);
        if (serviceVersion == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "实时服务版本不存在");
        }
        if (!VarProcessServiceStateEnum.ENABLED.equals(serviceVersion.getState())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "实时服务未启用，不支持下载");
        }
        //实时服务
        VarProcessRealtimeService realtimeService = varProcessRealtimeServiceService.getById(serviceVersion.getServiceId());
        if (realtimeService == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "实时服务不存在");
        }
        //实时服务指标清单关联
        QueryWrapper<VarProcessServiceManifest> serviceManifestWrapper = new QueryWrapper<>();
        serviceManifestWrapper.lambda().eq(VarProcessServiceManifest::getServiceId, versionId).eq(VarProcessServiceManifest::getManifestRole, (short) 1);
        List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(serviceManifestWrapper);
        if (CollectionUtils.isEmpty(serviceManifestList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "实时服务不存在主指标清单");
        }
        Long manifestId = serviceManifestList.get(0).getManifestId();
        //若主清单执行流程包含外数或内数，则点击下载部署文件包时提示“该服务主清单的执行流程包含内部数据获取或外部数据调用节点，无法下载部署文件包”；
        long outsideCount = varProcessManifestOutsideService.count(new LambdaQueryWrapper<VarProcessManifestOutside>().eq(VarProcessManifestOutside::getManifestId, manifestId));
        if (outsideCount > MagicNumbers.ZERO) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该服务主清单的执行流程包含内部数据获取或外部数据调用节点，无法下载部署文件包");
        }
        long internalCount = varProcessManifestInternalService.count(new LambdaQueryWrapper<VarProcessManifestInternal>().eq(VarProcessManifestInternal::getManifestId, manifestId));
        if (internalCount > MagicNumbers.ZERO) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该服务主清单的执行流程包含内部数据获取或外部数据调用节点，无法下载部署文件包");
        }
        //指标清单class
        QueryWrapper<VarProcessManifestClass> classWrapper = new QueryWrapper<>();
        classWrapper.lambda().eq(VarProcessManifestClass::getManifestId, manifestId);
        List<VarProcessManifestClass> classList = varProcessManifestClassService.list(classWrapper);
        if (CollectionUtils.isEmpty(classList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "实时服务指标清单不存在class");
        }
        byte[] variableClass = classList.get(0).getVariableClass();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(outputStream)) {
            String varClsJsonContent = new String(variableClass, StandardCharsets.UTF_8);
            Map<String, String> mapContent = JSON.parseObject(varClsJsonContent, Map.class);
            Map<String, byte[]> varClsMap = new HashMap<>(MagicNumbers.INT_64);
            mapContent.forEach((key, value) -> varClsMap.put(key, Base64.getDecoder().decode(value)));
            for (Map.Entry<String, byte[]> mapEntry : varClsMap.entrySet()) {
                String key = mapEntry.getKey();
                byte[] value = mapEntry.getValue();
                out.putNextEntry(new ZipArchiveEntry(key.replace(StringPool.DOT, StringPool.SLASH) + CommonConstant.CLASS_SUFFIX));
                out.write(value);
                out.closeEntry();
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataMap.put("data", outputStream.toByteArray());
        //"varService"-实时服务code-版本ID-清单ID.jar
        String fileName = CommonConstant.VAR_SERVICE + StringPool.DASH + realtimeService.getServiceCode()
                + StringPool.DASH + versionId + StringPool.DASH + manifestId + CommonConstant.JAR_SUFFIX;
        dataMap.put("fileName", fileName);
        return dataMap;
    }
}
