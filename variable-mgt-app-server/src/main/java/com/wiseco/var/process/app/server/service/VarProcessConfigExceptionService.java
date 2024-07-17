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
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionQueryDto;

/**
 * <p>
 * 变量空间-变量异常值配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

public interface VarProcessConfigExceptionService extends IService<VarProcessConfigExcept> {
    /**
     * getConfigExceptionValueList
     *
     * @param page page
     * @param queryDto queryDto
     * @return 异常值
     */
    IPage<VarProcessConfigExceptionDto> getConfigExceptionValueList(Page page, VarProcessConfigExceptionQueryDto queryDto);
}
