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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.repository.VarProcessManifestVariableMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.ManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessManifestVariableDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessVariableDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestPublishingVariableDTO;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 变量清单使用变量表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessManifestVariableServiceImpl extends ServiceImpl<VarProcessManifestVariableMapper, VarProcessManifestVariable> implements
        VarProcessManifestVariableService {

    @Autowired
    private VarProcessManifestVariableMapper varProcessManifestVariableMapper;

    @Override
    public List<VarProcessVariable> getVariableList(Long spaceId, List<Long> variableIds) {
        return varProcessManifestVariableMapper.getVariableList(spaceId, variableIds);
    }

    @Override
    public List<VarProcessManifestVariableDto> getByManifestId(Long spaceId, Long manifestId) {
        return varProcessManifestVariableMapper.getByManifestId(spaceId, manifestId);
    }

    @Override
    public List<VarProcessManifestVariable> getManifestVariableList(Long spaceId) {
        return varProcessManifestVariableMapper.getManifestVariableList(spaceId);
    }

    @Override
    public List<VarProcessVariable> getVariableFlow(VariableFlowQueryDto variableFlowQueryDto) {
        return varProcessManifestVariableMapper.getVariableFlow(variableFlowQueryDto);
    }

    @Override
    public List<VarProcessVariable> getVariableListInFlow(VariableFlowQueryDto variableFlowQueryDto) {
        return varProcessManifestVariableMapper.getVariableListInFlow(variableFlowQueryDto);
    }

    @Override
    public VarProcessVariable getVariableByIdentifier(Long spaceId, Long manifestId, String identifier) {
        return varProcessManifestVariableMapper.getVariableByIdentifier(spaceId, manifestId, identifier);
    }

    @Override
    public List<VarProcessManifestVariableDto> getCacheManifestVariableList(String date) {
        return varProcessManifestVariableMapper.getCacheManifestVariableList(date);
    }

    @Override
    public List<VarProcessVariableDto> getVariableListByManifestId(Long spaceId, Long manifestId) {
        return varProcessManifestVariableMapper.getVariableListByManifestId(spaceId, manifestId);
    }

    @Override
    public List<VarProcessVariableDto> getVariableInfosByManifestId(Long spaceId, Long manifestId) {
        return varProcessManifestVariableMapper.getVariableInfosByManifestId(spaceId, manifestId);
    }

    @Override
    public List<VarProcessManifestVariable> getManifestUseVariableList(Long spaceId, Long variableId) {
        return varProcessManifestVariableMapper.getManifestUseVariableList(spaceId, variableId);
    }

    @Override
    public List<VariableManifestPublishingVariableDTO> getPublishingVariableInfo(Long spaceId, Long manifestId) {
        return varProcessManifestVariableMapper.getPublishingVariableInfo(spaceId, manifestId);
    }

    @Override
    public Map<Long, Long> findVariableAmount(List<Long> manifestIds) {
        Map<Long, Long> resultMap = new HashMap<>(MagicNumbers.EIGHT);
        if (CollectionUtils.isEmpty(manifestIds)) {
            return resultMap;
        }
        QueryWrapper<VarProcessManifestVariable> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("manifest_id", "count(*) as count")
                .in("manifest_id", manifestIds)
                .groupBy("manifest_id");
        List<Map<String, Object>> maps = varProcessManifestVariableMapper.selectMaps(queryWrapper);

        maps.stream().forEach(map -> {
            resultMap.put(Long.parseLong(DmAdapter.mapGetIgnoreCase(map, "manifest_id").toString()), Long.parseLong(DmAdapter.mapGetIgnoreCase(map, "count") == null ? "0" : DmAdapter.mapGetIgnoreCase(map, "count").toString()));
        });
        return resultMap;
    }

    @Override
    public List<ManifestVariableDto> findmanifestVariables(List<Long> manifestIds) {
        if (CollectionUtils.isEmpty(manifestIds)) {
            return new ArrayList<>();
        }
        return varProcessManifestVariableMapper.findmanifestVariables(manifestIds);
    }

    @Override
    public Set<Long> findUsedVariables(Long spaceId) {
        return varProcessManifestVariableMapper.findUsedVariables();
    }

    @Override
    public VarProcessVariable getManifestVariableByIdentifier(long manifestId, String identifier) {
        return varProcessManifestVariableMapper.getManifestVariableByIdentifier(manifestId, identifier);
    }
}
