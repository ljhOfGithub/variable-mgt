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
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTagGroup;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagGroupDto;

import java.util.List;

/**
 * <p>
 * 变量标签组配置 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
public interface VarProcessConfigTagGroupService extends IService<VarProcessConfigTagGroup> {

    /**
     * 获取标签组
     * @param page 分页
     * @param spaceId 空间ID
     * @param keywords 关键词
     * @param idList id集合
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 标签组
     */
    IPage<VarProcessConfigTagGroupDto> getList(Page page, Long spaceId, String keywords, List<Long> idList,List<String> deptCodes,List<String> userNames);

    /**
     * 获取标签树
     * @param spaceId 空间ID
     * @param keywords 关键词
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 标签树
     */
    List<VarProcessConfigTagGroupDto> getTagTrees(Long spaceId, String keywords,List<String> deptCodes,List<String> userNames);

    /**
     * 获取当前最大order_no
     * @return integer
     */
    Integer getMaxOrderNo();
}
