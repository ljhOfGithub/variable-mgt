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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableScene;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * <p>
 * 变量-引用函数关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableSceneMapper extends BaseMapper<VarProcessVariableScene> {

    /**
     * 根据场景id查询使用它的变量数
     * @param id 场景id
     * @return 使用它的变量数
     */
    @Select("select count(1) from var_process_variable_scene vpvs join var_process_variable vpv on vpv.id = vpvs.variable_id and vpv.delete_flag = 1 where vpvs.scene_id = #{id}")
    int countUseVariables(@Param("id") Long id);

    /**
     * 查询被使用的场景
     * @return set
     */
    @Select("select distinct vpvs.scene_id from var_process_variable_scene vpvs join var_process_variable vpv on vpv.id = vpvs.variable_id and vpv.delete_flag = 1")
    Set<Long> findUsedScenes();
}
