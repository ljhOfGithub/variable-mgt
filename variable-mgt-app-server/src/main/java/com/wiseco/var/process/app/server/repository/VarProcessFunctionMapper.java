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
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPrepInputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.service.dto.FunctionDetailDto;
import com.wiseco.var.process.app.server.service.dto.FunctionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionPrepDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 公共函数表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionMapper extends BaseMapper<VarProcessFunction> {

    /**
     * 分页查询公共函数
     *
     * @param page     分页设置
     * @param queryDto 查询参数 DTO
     * @return 公共函数详情 DTO 分页封装
     */
    IPage<FunctionDetailDto> findFunctionList(IPage page, @Param("queryDto") FunctionQueryDto queryDto);

    /**
     * 获取预处理信息
     *
     * @param inputDto    查询参数
     * @param objectNames 对象名称
     * @param orderKey    排序
     * @return 预处理Dto
     */
    @Select({
            "<script>",
            "SELECT id as functionId,name,prep_object_name prepObjectName,identifier,created_dept as dept from var_process_function \n"
                    + "WHERE function_type='PREP' and delete_flag=1 and status='UP' and var_process_space_id=#{inputDto.spaceId} and prep_object_name in "
                    + "<foreach collection='objectNames' item='oName' open='(' separator=',' close=')'>#{oName}</foreach>"
                    + "<if test='inputDto.objectName!=null'>  AND prep_object_name=#{inputDto.objectName}</if>"
                    + "<if test='inputDto.deptId!=null and inputDto.deptId!=\"\"'>  AND created_dept_code=#{inputDto.deptId}</if>"
                    + "<if test='inputDto.prepName!=null'>  AND name  like CONCAT(CONCAT('%', #{inputDto.prepName}), '%') </if>"
                    + "<if test='inputDto.excludeList!=null and inputDto.excludeList.size!=0'> "
                    + " AND identifier NOT IN "
                    + "<foreach collection='inputDto.excludeList' item='excludedIdentifier' open='(' separator=',' close=')'>#{excludedIdentifier}</foreach> </if>"
                    + "<if test='orderKey != \"name asc\" '> order by ${orderKey},name asc </if>"
                    + "<if test='orderKey == \"name asc\" '> order by name asc</if>"
                    + " ", "</script>"})
    List<VariableFunctionPrepDto> getPrepList(@Param("inputDto") VariableManifestFlowPrepInputDto inputDto,
                                              @Param("objectNames") List<String> objectNames, @Param("orderKey") String orderKey);

    /**
     * 获取可用公共函数
     *
     * @param spaceId 空间id
     * @return 公共函数List
     */
    List<VarProcessFunction> getUseableOnlineFunction(@Param("spaceId") Long spaceId);

    /**
     * 通过空间id获取函数信息
     *
     * @param spaceId 空间id
     * @return 公共函数列表
     */
    @Select("SELECT vpf.*,vpfc.class_data from var_process_function vpf\n" + "LEFT JOIN var_process_function_class vpfc on vpf.id=vpfc.function_id\n"
            + "where vpf.var_process_space_id = #{spaceId} and status ='UP' and delete_flag = 1 ")
    List<VarProcessFunctionDto> getFunctionListBySpaceId(@Param("spaceId") Long spaceId);

    /**
     * 通过空间id、函数类型，函数状态获取函数信息
     *
     * @param spaceId        空间id
     * @param functionType   函数类型
     * @param functionStatus 函数状态
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 公共函数列表
     */
    List<VarProcessFunctionDto> selectFunctions(@Param("spaceId") Long spaceId, @Param("functionType") String functionType,
                                                @Param("functionStatus") String functionStatus, @Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);

    /**
     * 通过空间id、函数类型、函数状态、函数数据类型获取函数信息
     *
     * @param spaceId          空间id
     * @param functionType     函数类型
     * @param functionStatus   函数状态
     * @param functionDataType 函数数据类型
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 公共函数列表
     */
    List<VarProcessFunctionDto> selectFunctionsNew(@Param("spaceId") Long spaceId, @Param("functionType") String functionType, @Param("functionStatus") String functionStatus,
                                                   @Param("functionDataType") String functionDataType,  @Param("deptCodes") List<String> deptCodes, @Param("userNames") List<String> userNames);

    /**
     * 查询被预处理设为处理对象的数据模型名称
     * @return list
     */
    @Select("select distinct vpf.prep_object_name from var_process_function vpf where vpf.function_type = 'PREP' and delete_flag = 1 ")
    List<String> findDataModelNameUsedByPrep();
}
