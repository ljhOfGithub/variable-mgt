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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.VarProcessVariableMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDeployContentOverviewDto;
import com.wiseco.var.process.app.server.service.dto.VariableMaximumListedVersionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessVariableServiceImpl extends ServiceImpl<VarProcessVariableMapper, VarProcessVariable> implements VarProcessVariableService {

    @Autowired
    private VarProcessVariableMapper varProcessVariableMapper;

    @Override
    public IPage<VariableDetailDto> findVariableMaxVersionList(Page page, VariableQueryDto queryDto) {
        return varProcessVariableMapper.findVariableMaxVersionList(page, queryDto);
    }

    @Override
    public List<VarProcessVariable> pageQueryVariableMaximumListedVersion(VariableMaximumListedVersionQueryDto queryDto) {
        return varProcessVariableMapper.getVariableMaximumListedVersion(queryDto);
    }

    @Override
    public List<VarProcessVariable> getList(VariableQueryDto queryDto) {
        return varProcessVariableMapper.getList(queryDto);
    }

    @Override
    public Integer getMaxVersion(Long spaceId, String identifier) {
        return varProcessVariableMapper.getMaxVersion(spaceId, identifier);
    }

    @Override
    public List<VarProcessVariable> findManifestOutputVariableList(Long manifestId) {
        return varProcessVariableMapper.findManifestOutputVariableList(manifestId);
    }

    @Override
    public IPage<VariableManifestDeployContentOverviewDto> getManifestDeployContentPage(Page<VariableManifestDeployContentOverviewDto> pageConfig,
                                                                                        Long manifestId) {
        return varProcessVariableMapper.getManifestDeployContentPage(pageConfig, manifestId);
    }

    /**
     * 根据原变量的ID，获取它下面所有最新版本的发布变量ID
     *
     * @param archetypeManifestId 原变量的ID
     * @return 有最新版本的发布变量ID
     */
    @Override
    public List<Long> getNewVersionOfVariables(Long archetypeManifestId) {
        return varProcessVariableMapper.getNewVersionOfVariables(archetypeManifestId);
    }

    @Override
    public List<VarProcessVariable> getVariablesByVariableId(Long variableId) {
        return varProcessVariableMapper.getVariablesByVariableId(variableId);
    }

    @Override
    public VarProcessVariable checkRuleVariable(Long functionId, String name, String identifier) {
        return lambdaQuery()
                .select(VarProcessVariable::getId)
                .eq(VarProcessVariable::getDeleteFlag, 1)
                .and(query -> query.eq(VarProcessVariable::getName, identifier)
                        .or()
                        .eq(VarProcessVariable::getLabel, name)).last("limit 1").one();
    }

    /**
     * 条件+分页查询变量
     * @param variableIds 可能的目标变量
     * @param categoryId 分类Id
     * @param dataType 数据类型
     * @param users 可能的创建人
     * @param keyword 关键词(用于变量名称/编码的模糊查询)
     * @param order 排序
     * @return 变量列表
     */
    @Override
    public List<VarProcessVariable> getVariableList(List<Long> variableIds, Long categoryId, String dataType, List<String> users, String keyword, String order) {
        return varProcessVariableMapper.getVariableList(variableIds, categoryId, dataType, users, keyword, order);
    }

    /**
     * 获取所有启用的实时服务下, 所关联的启用变量清单, 然后根据这些变量清单，获取所有启用的变量Id
     * @return 变量列表
     */
    @Override
    public Set<Long> variableIdsByOther() {
        return new HashSet<>(varProcessVariableMapper.variableIdsByOther());
    }

    @Override
    public List<String> checkNameRepeat(List<String> names) {
        return CollectionUtils.isEmpty(names) ? new ArrayList<>()
                : varProcessVariableMapper.selectList(Wrappers.<VarProcessVariable>lambdaQuery().select(VarProcessVariable::getLabel)
                .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).in(VarProcessVariable::getLabel,names)).stream().map(VarProcessVariable::getLabel)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> checkCodeRepeat(List<String> codes) {
        return CollectionUtils.isEmpty(codes) ? new ArrayList<>()
                : varProcessVariableMapper.selectList(Wrappers.<VarProcessVariable>lambdaQuery().select(VarProcessVariable::getName)
                        .eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).in(VarProcessVariable::getName,codes)).stream().map(VarProcessVariable::getName)
                .distinct().collect(Collectors.toList());
    }
}
