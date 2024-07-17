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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessConfigExceptionMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.service.VarProcessConfigExceptionService;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 变量空间-变量异常值配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

@Service
public class VarProcessConfigExceptionServiceImpl extends ServiceImpl<VarProcessConfigExceptionMapper, VarProcessConfigExcept> implements
        VarProcessConfigExceptionService {

    @Autowired
    private VarProcessConfigExceptionMapper varProcessConfigExceptionMapper;

    @Override
    public IPage<VarProcessConfigExceptionDto> getConfigExceptionValueList(Page page, VarProcessConfigExceptionQueryDto queryDto) {
        return varProcessConfigExceptionMapper.getConfigExceptionValueList(page, queryDto);
    }
}
