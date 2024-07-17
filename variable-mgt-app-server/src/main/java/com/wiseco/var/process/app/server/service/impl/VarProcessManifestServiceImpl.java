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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.boot.commons.lang.StringUtils;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.controller.vo.input.VarModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestListOutputVo;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessDataModelMapper;
import com.wiseco.var.process.app.server.repository.VarProcessManifestFunctionMapper;
import com.wiseco.var.process.app.server.repository.VarProcessManifestMapper;
import com.wiseco.var.process.app.server.repository.VarProcessVariableVarMapper;
import com.wiseco.var.process.app.server.repository.entity.BaseEntity;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.dto.ManifestListQueryDto;
import com.wiseco.var.process.app.server.service.dto.ServiceUsingManifestDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量清单表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessManifestServiceImpl extends ServiceImpl<VarProcessManifestMapper, VarProcessManifest> implements VarProcessManifestService {

    private static final String RAW_DATA = "rawData";
    @Autowired
    VarProcessVariableVarMapper varProcessVariableVarMapper;

    @Autowired
    VarProcessDataModelMapper varProcessDataModelMapper;
    @Autowired
    private VarProcessManifestMapper varProcessManifestMapper;
    @Autowired
    private VarProcessManifestFunctionMapper    varProcessManifestFunctionMapper;

    @Override
    public IPage<VarProcessManifest> getManifestList(Page<ManifestListOutputVo> page, ManifestListQueryDto queryDto) {
        return varProcessManifestMapper.getManifestList(page, queryDto);
    }

    @Override
    public int getMaximumManifestVersionWithinService(Long serviceId) {
        List<VarProcessManifest> upToDateManifestList = varProcessManifestMapper.selectList(Wrappers.<VarProcessManifest>lambdaQuery()
                        .eq(VarProcessManifest::getServiceId, serviceId)
                        .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .orderByDesc(VarProcessManifest::getVersion)
//                .last(" LIMIT 1")
        );
        return CollectionUtils.isEmpty(upToDateManifestList) ? 0 : upToDateManifestList.get(0).getVersion();
    }

    /**
     * 获取服务下启用的变量清单
     *
     * @param serviceId 实时服务 ID
     * @return 变量清单实体类
     */
    @Override
    public VarProcessManifest getEnabledManifestWithinService(Long serviceId) {
        List<VarProcessManifest> varProcessManifestList = varProcessManifestMapper.selectList(Wrappers.<VarProcessManifest>lambdaQuery()
                        .eq(VarProcessManifest::getServiceId, serviceId)
                        .eq(VarProcessManifest::getState, VarProcessManifestStateEnum.UP.getCode())
                        .eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
//                .last("LIMIT 1")
        );
        return CollectionUtils.isEmpty(varProcessManifestList) ? null : varProcessManifestList.get(0);
    }

    @Override
    public List<ServiceUsingManifestDto> findUsingService(Long manifestId,Long spaceId) {
        return varProcessManifestMapper.findUsingService(manifestId,spaceId);
    }

    /**
     * 根据服务ID获取它关联的变量清单名称
     *
     * @param serviceId 服务ID
     * @return 服务ID所关联的变量清单名称
     */
    @Override
    public List<String> getManifestNameByServiceId(Long serviceId) {
        return varProcessManifestMapper.getManifestNameByServiceId(serviceId);
    }

    @Override
    public List<VarProcessDataModel> getModelsByVariableIds(VarModelInputDto varModelInputDto) {
        List<Long> varIdList = varModelInputDto.getVarIdList();
        if (CollectionUtils.isEmpty(varIdList)) {
            return new ArrayList<>();
        }
        Long spaceId = varModelInputDto.getSpaceId();

        //根据varIdList查询变量使用到的数据模型路径pathList
        List<String> pathList = getPathes(varIdList);
        if (pathList.isEmpty()) {
            //            throw new BizException("引用数据模型为空！");
            return new ArrayList<>();
        }

        //将pathlist剪切成namelist
        List<String> nameList = getNamesByPathes(pathList);

        //通过namelist查询数据模型相关信息
        return getModelsByNames(spaceId, nameList);
    }

    @Override
    public List<VarProcessDataModel> getModelsByNames(Long spaceId, List<String> nameList) {

        nameList = nameList.stream().distinct().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nameList)) {
            return new ArrayList<>();
        }
        return varProcessDataModelMapper.findMaxVersionListByNames(spaceId, nameList);
    }

    @Override
    public List<String> getPathes(List<Long> varIdList) {
        return varProcessVariableVarMapper.selectList(Wrappers.<VarProcessVariableVar>lambdaQuery()
                        .select(VarProcessVariableVar::getVarPath)
                        .in(VarProcessVariableVar::getVariableId, varIdList))
                .stream()
                .map(VarProcessVariableVar::getVarPath)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getNamesByPathes(List<String> pathList) {
        List<String> nameList = pathList.stream().map(item -> {
            String[] path = item.split("\\.");
            if (RAW_DATA.equals(path[0])) {
                return path[1];
            }
            return null;
        }).collect(Collectors.toList());
        if (nameList.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到符合条件的数据模型！");
        }
        return nameList;
    }

    @Override
    public List<VarProcessManifest> getUpManifest(Long spaceId, List<Long> excludedList,List<String> deptCodes, List<String> userNames) {
        LambdaQueryWrapper<VarProcessManifest> queryWrapper = Wrappers.<VarProcessManifest>lambdaQuery().eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .select(VarProcessManifest::getId, VarProcessManifest::getCategoryId, VarProcessManifest::getVarManifestName)
                .eq(VarProcessManifest::getState, VarProcessManifestStateEnum.UP);
        if (!CollectionUtils.isEmpty(excludedList)) {
            queryWrapper.notIn(VarProcessManifest::getId, excludedList);
        }

        if (!CollectionUtils.isEmpty(deptCodes)) {
            queryWrapper.in(VarProcessManifest::getDeptCode, deptCodes);
        }
        if (!CollectionUtils.isEmpty(userNames)) {
            queryWrapper.in(VarProcessManifest::getCreatedUser, userNames);
        }

        return varProcessManifestMapper.selectList(queryWrapper);
    }

    @Override
    public List<Long> getCallListIds(String callName) {
        List<VarProcessManifest> ids = Collections.emptyList();
        LambdaQueryWrapper<VarProcessManifest> varProcessManifestLambdaQueryWrapper = new LambdaQueryWrapper<>();
        varProcessManifestLambdaQueryWrapper.select(VarProcessManifest::getId);
        if (!StringUtils.isEmpty(callName)) {
            varProcessManifestLambdaQueryWrapper.eq(VarProcessManifest::getVarManifestName, callName);
            ids = varProcessManifestMapper.selectList(varProcessManifestLambdaQueryWrapper);
        }
        if (CollectionUtils.isEmpty(ids)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未找到对应清单id");
        }
        return ids.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    @Override
    public List<VarProcessManifest> findManifestUsingFunc(Long funcId, Long spaceId) {
        return varProcessManifestFunctionMapper.findManifestUsingFunc(funcId,spaceId);
    }

}
