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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量间引用关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableReferenceMapper extends BaseMapper<VarProcessVariableReference> {

    /**
     * 变量间引用关系表
     * @param spaceId 变量空间Id
     * @return List
     */
    @Select("SELECT distinct(variable_id) from var_process_variable_reference")
    List<VarProcessVariableReference> getVariableReferenceList(Long spaceId);

    /**
     * 查询所有被其他变量引用的变量
     * @param spaceId 空间id
     * @return set
     */
    @Select("select distinct vpvr.variable_id  from var_process_variable_reference vpvr where vpvr.var_process_space_id = #{spaceId}")
    Set<Long> findUsedVariables(@Param("spaceId") Long spaceId);
}
