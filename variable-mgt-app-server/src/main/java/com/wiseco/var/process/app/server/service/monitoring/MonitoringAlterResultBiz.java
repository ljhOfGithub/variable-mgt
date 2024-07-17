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
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringResultPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringResultPageOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionVo;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.VarProcessMonitoringAlertResultService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.VariablePmdCheckBiz;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum.USABLE;


/**
 * 监控配置管理
 *
 * @author wiseco
 */
@Slf4j
@Service
public class MonitoringAlterResultBiz {

    @Resource
    private VarProcessMonitoringAlertResultService varProcessMonitoringAlertResultService;

    @Resource
    private VariablePmdCheckBiz variablePmdCheckBiz;

    @Resource
    private VarProcessServiceManifestService varProcessServiceManifestService;

    @Resource
    private VarProcessManifestService varProcessManifestService;

    @Resource
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Resource
    private AuthService authService;

    /**
     * 预警结果分页查询
     *
     * @param inputVO 入参
     * @return 预警结果分页数据
     */
    public IPage<MonitoringResultPageOutputVO> getResultPage(MonitoringResultPageInputVO inputVO) {
        String sortedKey = inputVO.getOrder();
        if (StringUtils.isEmpty(sortedKey)) {
            inputVO.setSortedKey("alert_date");
            inputVO.setSortMethod("desc");
        } else {
            inputVO.setSortMethod(sortedKey.substring(sortedKey.indexOf("_") + 1));
            inputVO.setSortedKey("alert_date");
        }
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        inputVO.setDeptCodes(roleDataAuthority.getDeptCodes());
        inputVO.setUserNames(roleDataAuthority.getUserNames());

        Page<MonitoringResultPageOutputVO> resultPage = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());

        IPage<MonitoringResultPageOutputVO> pageList = varProcessMonitoringAlertResultService.getResultPage(resultPage, inputVO);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return resultPage;
        }


        resultPage.setRecords(pageList.getRecords());
        resultPage.setTotal(pageList.getTotal());
        resultPage.setCurrent(pageList.getTotal());
        return resultPage;
    }

    /**
     * 获取所有的实时服务名称和启动的版本编号以及所有调用过的版本号
     * @return 实时服务List
     */
    public List<ServiceVersionVo> getAllServiceName() {
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<ServiceInfoDto> services = varProcessServiceVersionService.findAllServiceInfos(roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        Map<String, List<ServiceInfoDto>> serviceMaps = services.stream().collect(Collectors.groupingBy(ServiceInfoDto::getName));
        // 2.填充返回的实体类
        List<ServiceVersionVo> result = new ArrayList<>();
        serviceMaps.forEach((serviceName,v) -> {
            List<ServiceInfoDto> servicesEnabledList = v.stream().filter(item -> item.getState() == VarProcessServiceStateEnum.ENABLED).collect(Collectors.toList());
            if (servicesEnabledList.size() > 1) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "服务[" + serviceName + "]存在多个启用的版本号");
            } else if (servicesEnabledList.size() == 0) {
                result.add(new ServiceVersionVo(serviceName, null, null));
            } else {
                result.add(new ServiceVersionVo(serviceName, servicesEnabledList.get(0).getVersion(), null));

            }
        });
        // 3.获取对应的有调用记录的版本号
        for (ServiceVersionVo dto : result) {
            List<Integer> versions = variablePmdCheckBiz.getVersions(dto.getServiceName());
            if (dto.getVersion() != null && !versions.contains(dto.getVersion())) {
                versions.add(dto.getVersion());
            }
            if (versions != null && versions.size() > 0) {
                Collections.sort(versions);
            }

            dto.setVersions(versions);
        }
        // 4.返回结果
        return result;
    }

    /**
     * 获取实施服务的变量清单
     * @param serviceName 实时服务名称
     * @param version 实时服务版本
     * @return 变量清单
     */
    public List<String> getAllManifest(String serviceName, Integer version) {
        //如果实施服务名称和版本号都不为空，则根据这两个值直接查询变量清单，如果版本为空则查询所有版本涉及到的变量清单并且去重
        if (serviceName != null && version != null) {
            VarProcessServiceVersion varProcessService = varProcessServiceVersionService.findServiceByNameAndVersion(serviceName, version);
            List<VarProcessServiceManifest> serviceManifestList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                    .eq(VarProcessServiceManifest::getServiceId, varProcessService.getId()));
            List<Long> manifestIds = serviceManifestList.stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());

            if (CollectionUtils.isEmpty(manifestIds)) {
                return Collections.emptyList();
            }

            LambdaQueryWrapper<VarProcessManifest> varProcessManifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
            varProcessManifestLambdaQueryWrapper.eq(VarProcessManifest::getDeleteFlag, USABLE.getCode());
            varProcessManifestLambdaQueryWrapper.in(VarProcessManifest::getId, manifestIds);
            varProcessManifestLambdaQueryWrapper.select(VarProcessManifest::getVarManifestName);
            return varProcessManifestService.list(varProcessManifestLambdaQueryWrapper).stream().map(VarProcessManifest::getVarManifestName).distinct().collect(Collectors.toList());
        } else {
            List<Long> varProcessServiceIdList = varProcessServiceVersionService.findServiceVersionIdsByName(serviceName);

            List<Long> serviceManifestIdList = varProcessServiceManifestService.list(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                    .in(VarProcessServiceManifest::getServiceId, varProcessServiceIdList)).stream().map(VarProcessServiceManifest::getManifestId).distinct().collect(Collectors.toList());

            if (CollectionUtils.isEmpty(serviceManifestIdList)) {
                return Collections.emptyList();
            }

            LambdaQueryWrapper<VarProcessManifest> varProcessManifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
            varProcessManifestLambdaQueryWrapper.in(VarProcessManifest::getId, serviceManifestIdList);
            varProcessManifestLambdaQueryWrapper.select(VarProcessManifest::getVarManifestName);
            return varProcessManifestService.list(varProcessManifestLambdaQueryWrapper).stream().map(VarProcessManifest::getVarManifestName).distinct().collect(Collectors.toList());
        }





    }
}
