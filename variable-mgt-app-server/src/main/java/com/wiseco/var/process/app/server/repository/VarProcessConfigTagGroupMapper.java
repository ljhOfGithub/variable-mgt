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
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTagGroup;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagGroupDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量标签组配置 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
public interface VarProcessConfigTagGroupMapper extends BaseMapper<VarProcessConfigTagGroup> {

    /**
     * 变量标签组配置表
     * @param page 分页
     * @param spaceId 空间id
     * @param keywords 关键字
     * @param idList id列表
     * @param deptCodes 部门集合
     * @param userNames 用户结合
     * @return 标签组 DTO的List集合
     */
    IPage<VarProcessConfigTagGroupDto> getList(Page page, @Param("spaceId") Long spaceId, @Param("keywords") String keywords,
                                               @Param("idList") List<Long> idList,@Param("deptCodes") List<String> deptCodes,@Param("userNames") List<String> userNames);

    /**
     * 获取变量标签树
     * @param spaceId 空间id
     * @param keywords 关键字
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 标签组 DTO的List集合
     */
    List<VarProcessConfigTagGroupDto> getTagTrees(@Param("spaceId") Long spaceId, @Param("keywords") String keywords,@Param("deptCodes") List<String> deptCodes,@Param("userNames") List<String> userNames);

    /**
     * 获取当前最大order_no
     * @return Integer
     */
    @Select("select MAX(order_no) from var_process_config_tag_group vpctg")
    Integer getMaxOrderNo();
}
