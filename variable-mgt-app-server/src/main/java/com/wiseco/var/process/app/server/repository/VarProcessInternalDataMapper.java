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
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 内部数据表 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-21
 */
@Mapper
public interface VarProcessInternalDataMapper extends BaseMapper<VarProcessInternalData> {

    /**
     * 查询分页数据
     *
     * @param page     分页
     * @param spaceId  空间id
     * @param keywords 关键字
     * @return 分页结果
     */
    IPage<VarProcessInternalData> findPageList(Page page, @Param("spaceId") Long spaceId, @Param("keywords") String keywords);

    /**
     * 通过标识查询内部数据
     *
     * @param spaceId    空间id
     * @param identifier 编号
     * @return 内部数据表
     */
    List<VarProcessInternalData> findByIdentifier(@Param("spaceId") Long spaceId, @Param("identifier") String identifier);
}
