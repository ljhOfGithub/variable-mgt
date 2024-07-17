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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessCategoryQueryDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 变量类型表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessCategoryMapper extends BaseMapper<VarProcessCategory> {

    /**
     * 获取变量类型
     *
     * @param page     分页
     * @param queryDto 查询参数
     * @return VarProcessCategoryDto
     */
    IPage<VarProcessCategoryDto> getCategoryList(Page page, @Param("queryDto") VarProcessCategoryQueryDto queryDto);
}
