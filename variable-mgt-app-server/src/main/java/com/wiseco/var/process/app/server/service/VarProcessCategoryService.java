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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryQueryDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变量类型表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessCategoryService extends IService<VarProcessCategory> {

    /**
     * 获取指定变量空间内变量类别 ID 和名称 Map
     *
     * @param spaceId 变量空间 ID
     * @return 变量类别名称 Map, key: categoryId, value: categoryName
     */
    Map<Long, String> getCategoryNameMap(Long spaceId);

    /**
     * 获取指定变量空间内变量类别列表 ID 和名称 Map
     *
     * @param page     变量空间 ID
     * @param queryDto 查询条件
     * @return 变量类别名称 Map, key: categoryId, value: categoryName
     */
    IPage<VarProcessCategoryDto> getCategoryList(Page page, VarProcessCategoryQueryDto queryDto);

    /**
     * 获取特定类型的分类 id 名称 map
     *
     * @param categoryType 分类对象
     * @return 分类名称 Map, key: categoryId, value: categoryName
     */
    Map<Long, String> getCategoryNameMap(CategoryTypeEnum categoryType);

    /**
     * 获取特定类型的分类map
     *
     * @param categoryType 分类对象
     * @return 分类名称 Map, key: categoryId, value: category
     */
    List<VarProcessCategory> getCategoryListByType(CategoryTypeEnum categoryType);

    /**
     * 判断分类是否是目标分类及其子分类
     * @param targetCatId 目标分类
     * @param category 分类
     * @param categoryMap 分类map
     * @return true or false
     */
    boolean containsSubCat(Long targetCatId, VarProcessCategory category,Map<Long, VarProcessCategory> categoryMap);
}
