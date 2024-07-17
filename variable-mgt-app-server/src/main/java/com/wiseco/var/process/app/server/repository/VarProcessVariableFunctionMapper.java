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
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量-引用函数关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableFunctionMapper extends BaseMapper<VarProcessVariableFunction> {

    /**
     * 获取使用变量列表
     *
     * @param spaceId    空间id
     * @param functionId 函数id
     * @return 变量详情列表
     */
    @Select("select vpv.id, vpv.var_process_space_id, vpv.parent_id, vpv.identifier, vpv.name, vpv.label, vpv.data_type, vpv.version from var_process_variable_function vvf\n"
            + "INNER JOIN var_process_variable vpv on vvf.variable_id = vpv.id\n"
            + "WHERE vpv.var_process_space_id = #{spaceId} and vpv.delete_flag = 1 and vvf.function_id = #{functionId} order by vpv.version desc")
    List<VariableDetailDto> getUseVariableList(@Param("spaceId") Long spaceId, @Param("functionId") Long functionId);

    /**
     * 获取函数变量列表
     *
     * @param variableId 变量id
     * @return 函数列表
     */
    @Select("select vpf.* from var_process_variable_function vvf\n" + "INNER JOIN var_process_function vpf on vvf.function_id = vpf.id\n"
            + "WHERE  vvf.variable_id = #{variableId}")
    List<VarProcessFunction> getFunctionByVariableList(@Param("variableId") Long variableId);

    /**
     * 查询变量使用的变量模板
     *
     * @param variableIdList 变量 ID List
     * @return 公共函数表实体类 List
     */
    List<VarProcessFunction> getVariableUtilizedVariableTemplate(@Param("variableIdList") List<Long> variableIdList);

    /**
     * 获取所有被使用的变量模板 id set
     * @param spaceId 空间id
     * @return set
     */
    @Select("select distinct vpvf.function_id from var_process_variable_function vpvf \n"
            + "join var_process_function vpf on vpf.id = vpvf.function_id \n"
            + "where vpvf.var_process_space_id = #{spaceId} and vpf.delete_flag = 1 and vpf.function_type = 'TEMPLATE'")
    Set<Long> findUsedFunctions(@Param("spaceId") Long spaceId);
}
