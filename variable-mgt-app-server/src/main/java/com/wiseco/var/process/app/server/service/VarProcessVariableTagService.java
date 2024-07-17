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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;

import java.util.Set;

/**
 * <p>
 * 变量-标签关系表 服务类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
public interface VarProcessVariableTagService extends IService<VarProcessVariableTag> {

    /**
     * 统计标签组使用数
     *
     * @param spaceId spaceId
     * @param groupId groupId
     * @return int
     */
    int countTagGroup(Long spaceId, Long groupId);

    /**
     * 统计标签使用数
     *
     * @param spaceId spaceId
     * @param tagName tagName
     * @return int
     */
    int countTag(Long spaceId, String tagName);

    /**
     * 通过groupId, 查出变量的Id
     * @param groupId 标签的Id
     * @return 变量的Id集合
     */
    Set<Long> getVariableIds(Long groupId);

    /**
     * 通过tagId，查出它所属的groupId
     * @param tagId tagId
     * @return groupId
     */
    Long getGroupIdByTagId(Long tagId);
}
