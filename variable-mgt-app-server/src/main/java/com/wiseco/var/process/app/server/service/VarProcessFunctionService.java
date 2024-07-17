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
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPrepInputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.service.dto.FunctionDetailDto;
import com.wiseco.var.process.app.server.service.dto.FunctionQueryDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessFunctionDto;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionPrepDto;

import java.util.List;

/**
 * <p>
 * 公共函数表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionService extends IService<VarProcessFunction> {

    /**
     * 分页查询公共函数
     *
     * @param page     分页设置
     * @param queryDto 查询参数 DTO
     * @return 公共函数详情 DTO 分页封装
     */
    IPage<FunctionDetailDto> findFunctionList(IPage page, FunctionQueryDto queryDto);

    /**
     * getPrepList
     *
     * @param inputDto 输入
     * @param mappingObjectList 映射对象列表
     * @param sortedKey 排序字段
     * @param sortMethod 排序方法
     * @return List
     */
    List<VariableFunctionPrepDto> getPrepList(VariableManifestFlowPrepInputDto inputDto, List<String> mappingObjectList, String sortedKey,
                                              String sortMethod);

    /**
     * 获取可用的在线公共函数
     *
     * @param spaceId 变量空间Id
     * @return 公共函数列表
     */
    List<VarProcessFunction> getUseableOnlineFunction(Long spaceId);

    /**
     * 通过变量空间Id获取公共函数list
     *
     * @param spaceId 变量空间Id
     * @return 公共函数列表
     */
    List<VarProcessFunctionDto> getFunctionListBySpaceId(Long spaceId);

    /**
     * selectFunctions
     *
     * @param spaceId 变量空间Id
     * @param functionType 函数类型
     * @param functionStatus 函数状态
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 公共函数列表
     */
    List<VarProcessFunctionDto> selectFunctions(Long spaceId, String functionType, String functionStatus, List<String> deptCodes, List<String> userNames);

    /**
     * 查询公共函数
     *
     * @param spaceId 变量空间Id
     * @param functionType 函数类型
     * @param functionStatus 函数状态
     * @param functionDataType 函数数据类型
     * @param deptCodes 部门集合
     * @param userNames 用户集合
     * @return 公共函数列表
     */
    List<VarProcessFunctionDto> selectFunctionsNew(Long spaceId, String functionType, String functionStatus, String functionDataType, List<String> deptCodes, List<String> userNames);
}
