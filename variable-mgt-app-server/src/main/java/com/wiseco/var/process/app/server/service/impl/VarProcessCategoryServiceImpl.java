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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.repository.VarProcessCategoryMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.service.VarProcessCategoryService;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量类型表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessCategoryServiceImpl extends ServiceImpl<VarProcessCategoryMapper, VarProcessCategory> implements VarProcessCategoryService {

    @Autowired
    private VarProcessCategoryMapper varProcessCategoryMapper;

    @Override
    public Map<Long, String> getCategoryNameMap(Long spaceId) {
        return varProcessCategoryMapper.selectList(new QueryWrapper<VarProcessCategory>().lambda()
                        .eq(VarProcessCategory::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream()
                .collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName));
    }

    @Override
    public IPage<VarProcessCategoryDto> getCategoryList(Page page, VarProcessCategoryQueryDto queryDto) {
        return varProcessCategoryMapper.getCategoryList(page, queryDto);
    }

    @Override
    public Map<Long, String> getCategoryNameMap(CategoryTypeEnum categoryType) {
        return varProcessCategoryMapper.selectList(Wrappers.<VarProcessCategory>lambdaQuery()
                        .eq(VarProcessCategory::getCategoryType, categoryType)).stream()
                .collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName, (k1, k2) -> k2));
    }

    @Override
    public List<VarProcessCategory> getCategoryListByType(CategoryTypeEnum categoryType) {
        return varProcessCategoryMapper.selectList(Wrappers.<VarProcessCategory>lambdaQuery()
                .eq(VarProcessCategory::getCategoryType, categoryType));
    }

    @Override
    public boolean containsSubCat(Long targetCatId, VarProcessCategory category, Map<Long, VarProcessCategory> categoryMap) {
        if (category == null) {
            return false;
        } else if (Objects.equals(category.getId(), targetCatId)) {
            return true;
        } else {
            return containsSubCat(targetCatId,categoryMap.get(category.getParentId()),categoryMap);
        }
    }
}
