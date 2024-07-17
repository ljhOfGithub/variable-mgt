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
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionQueryDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 变量空间-变量异常值配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

public interface VarProcessConfigExceptionMapper extends BaseMapper<VarProcessConfigExcept> {

    /**
     * 变量异常值配置
     *
     * @param page     分页
     * @param queryDto 查询参数
     * @return VarProcessConfigExceptionDto
     */
    IPage<VarProcessConfigExceptionDto> getConfigExceptionValueList(Page page, @Param("queryDto") VarProcessConfigExceptionQueryDto queryDto);
}
