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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量-标签关系表 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-28
 */
public interface VarProcessVariableTagMapper extends BaseMapper<VarProcessVariableTag> {

    /**
     * 根据分组id查询标签组
     *
     * @param spaceId 空间id
     * @param groupId 标签组id
     * @return 标签数
     */
    @Select("select count(vpv.id) from var_process_variable vpv\n" + "INNER JOIN var_process_variable_tag vpvt on vpvt.variable_id = vpv.id\n"
            + "WHERE vpv.delete_flag = 1 and vpv.var_process_space_id = #{spaceId} and vpvt.tag_group_id = #{groupId}")
    int countTagGroup(@Param("spaceId") Long spaceId, @Param("groupId") Long groupId);

    /**
     * 根据标签名查询标签信息
     *
     * @param spaceId 空间id
     * @param tagName 标签名
     * @return 标签数
     */
    @Select("select count(vpv.id) from var_process_variable vpv\n" + "INNER JOIN var_process_variable_tag vpvt on vpvt.variable_id = vpv.id\n"
            + "WHERE vpv.delete_flag = 1 and vpv.var_process_space_id = #{spaceId} and vpvt.tag_name = #{tagName}")
    int countTag(@Param("spaceId") Long spaceId, @Param("tagName") String tagName);

    /**
     * 通过groupId, 查出变量的Id
     * @param groupId 标签的Id
     * @return 变量的Id集合
     */
    List<Long> variables(@Param("groupId") Long groupId);

    /**
     * 通过tagId，查出它所属的groupId
     * @param tagId tagId
     * @return groupId
     */
    Long getGroupIdByTagId(@Param("tagId") Long tagId);
}
