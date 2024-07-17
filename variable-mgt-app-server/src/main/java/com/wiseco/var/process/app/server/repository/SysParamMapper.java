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
import com.wiseco.var.process.app.server.repository.entity.SysParam;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统参数表 Mapper 接口
 * </p>
 *
 * @author liaody
 * @since 2022-02-14
 */
public interface SysParamMapper extends BaseMapper<SysParam> {

    /**
     * 分页查询系统参数表
     *
     * @param page              分页
     * @param queryNameOrNameCn 名称
     * @return SysParam
     */
    IPage<SysParam> findSysParamPage(Page page, @Param("queryNameOrNameCn") String queryNameOrNameCn);

}
