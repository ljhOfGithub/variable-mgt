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

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.repository.VarProcessServiceManifestMapper;
import com.wiseco.var.process.app.server.repository.VarProcessVariableMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.dto.ServiceManifestName;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelRealTimeServiceUseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VarProcessServiceManifestServiceImpl extends ServiceImpl<VarProcessServiceManifestMapper, VarProcessServiceManifest> implements
        VarProcessServiceManifestService {

    @Autowired
    private VarProcessServiceManifestMapper varProcessServiceManifestMapper;

    @Autowired
    private VarProcessVariableMapper varProcessVariableMapper;

    @Override
    public VariableDataModelRealTimeServiceUseVo getRealTimeServiceUseMapping(Long serviceId, Long manifestId) {
        return varProcessServiceManifestMapper.getRealTimeServiceUseMapping(serviceId, manifestId);

    }

    @Override
    public List<VarProcessServiceVersion> getServiceState(Long id) {
        return varProcessServiceManifestMapper.getServiceState(id);
    }

    @Override
    public List<VarProcessServiceManifest> getRefsByServiceIdAndManifestIds(Long servicdId, List<Long> manifestIds) {
        if (CollectionUtils.isEmpty(manifestIds)) {
            return new ArrayList<>();
        }
        return varProcessServiceManifestMapper.selectList(Wrappers.<VarProcessServiceManifest>lambdaQuery()
                .eq(VarProcessServiceManifest::getServiceId, servicdId)
                .in(VarProcessServiceManifest::getManifestId, manifestIds));
    }

    /**
     * 获取实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     * @param variableIds 传入的变量Id的集合(单指标分析时传一个, 指标对比分析时传多个)
     * @return 实时服务-变量清单的名称集合(给单指标分析和指标对比分析报表调用)
     */
    @Override
    public List<ServiceManifestNameVo> getServiceManifestName(List<Long> variableIds) {
        return varProcessServiceManifestMapper.getServiceManifestName(variableIds);
    }

    /**
     * 获取单个变量对应的变量清单映射信息
     * @param variableId 变量Id
     * @return 单个变量对应的变量清单映射信息
     */
    @Override
    public List<ServiceManifestNameVo> getVariableAndManifestMapping(Long variableId) {
        return varProcessServiceManifestMapper.getVariableAndManifestMapping(variableId);
    }

    @Override
    public Set<Long> findUsedManifests(Long spaceId) {
        return varProcessServiceManifestMapper.findUsedManifests(spaceId);
    }

    @Override
    public Map<Long, List<String>> findManifestNameMap(List<Long> serviceIds) {
        if (CollectionUtils.isEmpty(serviceIds)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }
        List<ServiceManifestName> serviceManifestNames = varProcessServiceManifestMapper.findManifestNames(serviceIds);
        return serviceManifestNames.stream()
                .filter(name -> name.getManifestName() != null)
                .collect(Collectors.groupingBy(ServiceManifestName::getServiceVersionId,
                        Collectors.mapping(ServiceManifestName::getManifestName, Collectors.toList())));
    }

    @Override
    public List<ServiceManifestDetailOutputVo> getManifestDetail(List<Long> manifests) {
        return varProcessServiceManifestMapper.getManifestDetail(manifests);
    }

    @Override
    public List<VarProcessVariable> findManifestOutputVariableList(Long serviceId) {
        Long manifestId = varProcessServiceManifestMapper.findMainManifestByServiceId(serviceId);
        if (manifestId == null) {
            return new ArrayList<>();
        }
        return varProcessVariableMapper.findVariablesByManifest(manifestId);
    }
}
