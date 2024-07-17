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
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.service.dto.VariableDetailDto;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量-变量模板关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableFunctionService extends IService<VarProcessVariableFunction> {

    /**
     * 引用的变量信息
     *
     * @param spaceId    空间id
     * @param functionId 公共函数id
     * @return 变量详情类
     */
    List<VariableDetailDto> getUseVariableList(Long spaceId, Long functionId);

    /**
     * 引用的变量模板信息
     *
     * @param variableId 变量id
     * @return 公共函数表实体类 List
     */
    List<VarProcessFunction> getFunctionByVariableList(Long variableId);

    /**
     * 查询变量使用的变量模板
     *
     * @param variableIdList 变量 ID List
     * @return 公共函数表实体类 List
     */
    List<VarProcessFunction> getVariableUtilizedVariableTemplate(List<Long> variableIdList);

    /**
     * 获取使用该变量模板的变量list
     *
     * @param templateId 变量模板id
     * @param spaceId 空间id
     * @return 变量list
     */
    List<VarProcessVariable> findVariableUseTemp(Long templateId,Long spaceId);

    /**
     * 获取所有被使用的变量模板 id set
     * @param spaceId 空间id
     * @return set
     */
    Set<Long> findUsedFunctions(Long spaceId);
}
