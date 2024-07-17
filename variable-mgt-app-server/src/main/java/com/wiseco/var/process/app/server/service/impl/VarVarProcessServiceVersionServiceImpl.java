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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.security.SessionUser;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListCriteria;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceVersionListInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.enums.VarProcessServiceActionEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.repository.VarProcessRealtimeServiceMapper;
import com.wiseco.var.process.app.server.repository.VarProcessServiceCycleMapper;
import com.wiseco.var.process.app.server.repository.VarProcessServiceVersionMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceCycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessServiceVersionInfo;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 变量服务版本表 服务实现类
 */
@Service
public class VarVarProcessServiceVersionServiceImpl extends ServiceImpl<VarProcessServiceVersionMapper, VarProcessServiceVersion> implements VarProcessServiceVersionService {

    @Autowired
    private VarProcessServiceVersionMapper varProcessServiceVersionMapper;

    @Autowired
    private VarProcessRealtimeServiceMapper varProcessRealtimeServiceMapper;

    @Autowired
    private VarProcessServiceCycleMapper varProcessServiceCycleMapper;

    @Override
    public List<VarProcessServiceVersionInfo> findServiceInfos(List<Long> serviceIds) {
        return varProcessServiceVersionMapper.findServiceInfos(serviceIds);
    }

    @Override
    public List<VarProcessServiceVersion> findNonDisabledVersionsByServiceId(ServiceVersionListInputVO inputDto) {
        return varProcessServiceVersionMapper.findNonDisabledVersionsByServiceId(inputDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateState(VarProcessServiceVersion versionEntity, VarProcessServiceActionEnum actionType, String desContent) {
        // 1.更新实时服务（某个具体的）的状态
        SessionUser sessionUser = SessionContext.getSessionUser();
        versionEntity.setState(actionType.getSusequentState());
        versionEntity.setUpdatedUser(sessionUser.getUsername());
        versionEntity.setUpdatedTime(new Date());
        varProcessServiceVersionMapper.updateById(versionEntity);
        // 2.新版本启用后停用之前启用的版本
        if (versionEntity.getState().equals(VarProcessServiceStateEnum.ENABLED)) {
            List<VarProcessServiceVersion> list = varProcessServiceVersionMapper.selectList(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                    .eq(VarProcessServiceVersion::getServiceId, versionEntity.getServiceId())
                    .eq(VarProcessServiceVersion::getState, VarProcessServiceStateEnum.ENABLED)
                    .eq(VarProcessServiceVersion::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .ne(VarProcessServiceVersion::getId, versionEntity.getId()));
            for (VarProcessServiceVersion item : list) {
                item.setState(VarProcessServiceStateEnum.DISABLED);
                item.setDeptCode(sessionUser.getUser().getDepartment().getCode());
                item.setUpdatedUser(sessionUser.getUsername());
                item.setUpdatedTime(new Date());
                varProcessServiceVersionMapper.updateById(item);
                VarProcessServiceCycle serviceCycle = VarProcessServiceCycle.builder().serviceId(item.getId()).operation(VarProcessServiceActionEnum.DISABLE.getCode().shortValue())
                        .status(item.getState()).description(MagicStrings.EMPTY_STRING).createdUser(sessionUser.getUsername())
                        .updatedUser(sessionUser.getUsername()).build();
                varProcessServiceCycleMapper.insert(serviceCycle);
            }
        }
        // 3.记录生命周期
        VarProcessServiceCycle newServiceCycle = VarProcessServiceCycle.builder().serviceId(versionEntity.getId()).operation(actionType.getCode().shortValue())
                .status(versionEntity.getState()).description(desContent).createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername()).build();
        varProcessServiceCycleMapper.insert(newServiceCycle);
    }

    @Override
    public List<VariableUseVarPathDto> getVarUseList(Long spaceId) {
        return varProcessServiceVersionMapper.getVarUseList(spaceId);
    }

    @Override
    public Long findUpVersionIdByCode(String serviceCode) {
        return varProcessServiceVersionMapper.findUpVersionIdByCode(serviceCode);
    }

    @Override
    public Page<RestServiceListOutputVO> findUpServicePage(ServiceListCriteria criteria) {
        if (!CollectionUtils.isEmpty(criteria.getCodeList())) {
            //移除"null"
            criteria.setCodeList(criteria.getCodeList().stream()
                    .filter(value -> !"null".equals(value))
                    .collect(Collectors.toList()));
        }

        if (!CollectionUtils.isEmpty(criteria.getNotInCodeList())) {
            //移除"null"
            criteria.setNotInCodeList(criteria.getNotInCodeList().stream()
                    .filter(value -> !"null".equals(value))
                    .collect(Collectors.toList()));
        }

        return varProcessServiceVersionMapper.findUpServicePage(new Page<>(criteria.getCurrentNo(), criteria.getSize()), criteria);
    }

    @Override
    public VarProcessServiceVersion findServiceByNameAndVersion(String serviceName, Integer serviceVersion) {
        VarProcessRealtimeService service = varProcessRealtimeServiceMapper.selectOne(Wrappers.<VarProcessRealtimeService>lambdaQuery()
                .select(VarProcessRealtimeService::getId)
                .eq(VarProcessRealtimeService::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(VarProcessRealtimeService::getServiceName, serviceName));

        return Optional.ofNullable(service)
                .flatMap(s -> Optional.ofNullable(varProcessServiceVersionMapper.selectOne(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                        .eq(VarProcessServiceVersion::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(VarProcessServiceVersion::getServiceId, s.getId())
                        .eq(VarProcessServiceVersion::getServiceVersion, serviceVersion))))
                .orElse(null);
    }

    @Override
    public List<Long> findServiceIdListByState(List<VarProcessServiceStateEnum> stateEnums) {
        List<VarProcessServiceVersion> versionList = new ArrayList<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(stateEnums)) {
            versionList = varProcessServiceVersionMapper.selectList(Wrappers.<VarProcessServiceVersion>lambdaQuery()
                    .select(VarProcessServiceVersion::getId)
                    .eq(VarProcessServiceVersion::getDeleteFlag,DeleteFlagEnum.USABLE.getCode())
                    .in(VarProcessServiceVersion::getState,stateEnums));
        }
        return versionList.stream().map(VarProcessServiceVersion::getId).collect(Collectors.toList());
    }

    @Override
    public List<ServiceInfoDto> findserviceListByVersionIds(List<Long> serviceIds) {
        if (CollectionUtils.isEmpty(serviceIds)) {
            return new ArrayList<>();
        }
        return varProcessServiceVersionMapper.findsServiceListByVersionIds(serviceIds);
    }

    @Override
    public List<ServiceInfoDto> findServiceListByState(List<VarProcessServiceStateEnum> states, List<String> deptCodes, List<String> userNames) {
        if (CollectionUtils.isEmpty(states)) {
            return new ArrayList<>();
        }
        return varProcessServiceVersionMapper.findServiceListByState(states,deptCodes,userNames);
    }

    @Override
    public List<Long> findServiceVersionIdsByName(String serviceName) {
        return varProcessServiceVersionMapper.findServiceVersionIdsByName(serviceName).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<ServiceInfoDto> findAllServiceInfos(List<String> deptCodes,List<String> userNames) {
        return varProcessServiceVersionMapper.findAllServiceInfos(deptCodes,userNames);
    }

    /**
     * 通过实时服务(与code相关的)的ID，获取这一组的最高版本号
     * @param serviceId 实时服务(与code相关的)ID
     * @return 最高版本号
     */
    @Override
    public Integer getMaxVersionByServiceId(Long serviceId) {
        // 1.构造查询条件
        LambdaQueryWrapper<VarProcessServiceVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessServiceVersion::getServiceId, serviceId);
        wrapper.eq(VarProcessServiceVersion::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        wrapper.select(VarProcessServiceVersion::getServiceVersion);
        // 2.开始查询
        List<VarProcessServiceVersion> varProcessServiceVersions = varProcessServiceVersionMapper.selectList(wrapper);
        // 3.找出最大版本
        int result = MagicNumbers.ZERO;
        for (VarProcessServiceVersion item : varProcessServiceVersions) {
            if (result < item.getServiceVersion()) {
                result = item.getServiceVersion();
            }
        }
        return Integer.parseInt(String.valueOf(result + MagicNumbers.ONE));
    }

    @Override
    public List<ServiceInfoDto> findUpVersionByCode(String serviceCode) {
        return varProcessServiceVersionMapper.findUpVersionByCode(serviceCode);
    }
}
