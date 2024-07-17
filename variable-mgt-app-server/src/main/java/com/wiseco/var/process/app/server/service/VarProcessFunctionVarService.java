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
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionUsageDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;

import java.util.List;

/**
 * <p>
 * 公共函数-引用数据模型变量关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionVarService extends IService<VarProcessFunctionVar> {
    /**
     * 根据变量路径查询变量或公共函数
     *
     * @param spaceId 变量空间Id
     * @return 变量或公共函数
     */
    List<VariableUseVarPathDto> getVarUseList(Long spaceId);

    /**
     * 获取函数模板的数据模型变量树
     *
     * @param spaceId 变量空间Id
     * @return 函数模板的数据模型变量树
     */
    List<VariableUseVarPathDto> getFunctionTempDataModelVarUseList(Long spaceId);

    /**
     * 获取变量函数的预使用列表
     *
     * @param objectName 对象名
     * @return 根据变量路径查询变量或公共函数的返回对象Dto的list
     */
    List<VariableUseVarPathDto> getVarFunctionPrepUseList(String objectName);

    /**
     * 获取公共函数预使用的数据模型树
     *
     * @param spaceId 变量空间Id
     * @return 根据变量路径查询变量或公共函数的返回对象Dto的list
     */
    List<VariableUseVarPathDto> getFunctionPrepDataModelVarUseList(Long spaceId);

    /**
     * 获取公共函数的数据模型使用列表
     *
     * @param spaceId 变量空间Id
     * @return 公共函数的数据模型使用列表
     */
    List<VariableUseVarPathDto> getFunctionFunctionDataModelVarUseList(Long spaceId);

    /**
     * 查询预处理逻辑及其处理的扩展数据
     *
     * @param spaceId 变量空间 ID
     * @return 变量-公共函数使用情况 DTO List
     */
    List<VariableFunctionUsageDto> getPreProcessLogicAndProcessedExtendedProperties(Long spaceId);
}
