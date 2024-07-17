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
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionUsageDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 公共函数-引用数据模型变量关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionVarMapper extends BaseMapper<VarProcessFunctionVar> {
    //原先的sql语句
    //    @Select("select vpv.id,vpvv.var_path,vpvv.parameter_type,vpv.name,vpv.function_type,vpvv.action_history from var_process_function_var vpvv\n"
    //            + " INNER JOIN var_process_function vpv ON vpvv.function_id=vpv.id "
    //            + " where vpvv.is_self = 1 and vpv.delete_flag=1  and vpv.var_process_space_id = #{spaceId} order by vpv.id desc "
    //
    //    )
    //wxs三表联查

    /**
     * 通过空间id查询公共函数-引用数据模型变量关系表
     *
     * @param spaceId 空间id
     * @return 数据模型变量存取路径返回对象列表
     */
    @Select("select vpv.id,vpvv.var_path,vpvv.parameter_type,vpv.name,vpv.function_type,vpvv.action_history from var_process_function_var vpvv\n"
            + " INNER JOIN var_process_function vpv ON vpvv.function_id=vpv.id"
            + " where vpvv.is_self = 1 and vpv.delete_flag=1  and vpv.var_process_space_id = #{spaceId} order by vpv.id desc ")
    List<VariableUseVarPathDto> getVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 通过空间id查询公共函数-引用数据模型变量关系表
     *
     * @param spaceId 空间id
     * @return 数据模型变量存取路径返回对象列表
     */
    @Select("select vpf.id,vpfv.var_path,vpfv.parameter_type,vpf.name,vpf.function_type,vpf.status as statustr ,vpfv.action_history as allClass from var_process_function_var vpfv\n"
            + " INNER JOIN var_process_function vpf ON vpfv.function_id=vpf.id "
            + " where vpfv.is_self = 1 and vpf.delete_flag=1  and vpf.var_process_space_id = #{spaceId} and vpf.function_type = 'PREP' order by vpf.id desc ")
    List<VariableUseVarPathDto> getFunctionPrepDataModelVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 获取函数模板变量使用信息
     *
     * @param spaceId 空间id
     * @return 数据模型变量存取路径返回对象列表
     */
    @Select("select vpf.id,vpfv.var_path,vpfv.parameter_type,vpf.name,vpf.function_type,vpf.status as statustr ,vpfv.action_history,vpc.name as allClass from var_process_function_var vpfv\n"
            + " INNER JOIN var_process_function vpf ON vpfv.function_id=vpf.id "
            + " INNER JOIN var_process_category vpc ON vpf.category_id=vpc.id "
            + " where vpfv.is_self = 1 and vpf.delete_flag=1  and vpf.var_process_space_id = #{spaceId} and vpf.function_type = 'TEMPLATE' order by vpf.id desc ")
    List<VariableUseVarPathDto> getFunctionTempDataModelVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 获取函数变量使用信息
     *
     * @param spaceId 空间id
     * @return 数据模型变量存取路径返回对象列表
     */
    @Select("select vpf.id,vpfv.var_path,vpfv.parameter_type,vpf.name,vpf.function_type,vpf.status as statustr ,vpfv.action_history from var_process_function_var vpfv\n"
            + " INNER JOIN var_process_function vpf ON vpfv.function_id=vpf.id "
            + " where vpfv.is_self = 1 and vpf.delete_flag=1  and vpf.var_process_space_id = #{spaceId} and vpf.function_type = 'FUNCTION' order by vpf.id desc ")
    List<VariableUseVarPathDto> getFunctionFunctionDataModelVarUseList(@Param("spaceId") Long spaceId);

    /**
     * 获取预处理变量使用信息
     *
     * @param objectName 对象名称
     * @return 数据模型变量存取路径返回对象列表
     */
    @Select("select vpf.id, vpf.name, vpf.status as statustr from var_process_function vpf\n"
            + " where vpf.status != 'DELETE' and vpf.delete_flag=1  and vpf.prep_object_name = #{objectName} and vpf.function_type = 'PREP' order by vpf.id desc ")
    List<VariableUseVarPathDto> getVarFunctionPrepUseList(@Param("objectName") String objectName);

    /**
     * 查询预处理逻辑及其处理的扩展数据
     *
     * @param spaceId 变量空间 ID
     * @return 变量-公共函数使用情况 DTO List
     */
    List<VariableFunctionUsageDto> getPreProcessLogicProcessedExtendedProperties(@Param("spaceId") Long spaceId);
}
