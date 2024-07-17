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
import com.wiseco.var.process.app.server.repository.entity.SysParam;

/**
 * <p>
 * 系统参数表 服务类
 * </p>
 *
 * @author liaody
 * @since 2022-02-14
 */
public interface SysParamService extends IService<SysParam> {
    /**
     * findSysParamPage
     *
     * @param page 分页参数
     * @param queryNameOrNameCn 入参
     * @return IPage
     */
    IPage<SysParam> findSysParamPage(Page page, String queryNameOrNameCn);

}
