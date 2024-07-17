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
import com.wiseco.var.process.app.server.repository.entity.SysDynamic;
import com.wiseco.var.process.app.server.service.dto.SysDynamicQueryDto;

import java.util.List;

/**
 * <p>
 * 系统动态表 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-03-02
 */
public interface SysDynamicService extends IService<SysDynamic> {
    /**
     * findListByCreatedTime
     *
     * @param queryDto 入参
     * @return IPage
     */
    List<SysDynamic> findListByCreatedTime(SysDynamicQueryDto queryDto);

    /**
     * findList
     *
     * @param page 分页参数
     * @param list 列表
     * @return IPage
     */
    IPage<SysDynamic> findList(Page page, List<Long> list);

    /**
     * findMainSysDynamic
     *
     * @param page 分页参数
     * @param queryDto 入参
     * @return IPage
     */
    IPage<SysDynamic> findMainSysDynamic(Page page, SysDynamicQueryDto queryDto);

    /**
     * findSysDynamic
     *
     * @param page 分页参数
     * @param queryDto 入参
     * @return IPage
     */
    IPage<SysDynamic> findSysDynamic(Page page, SysDynamicQueryDto queryDto);
}
