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
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.VariablePlanInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRulePreviewDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleQueryDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariablePlan;

import java.util.List;

public interface VariablePlanService extends IService<VarProcessVariablePlan> {

    /**
     * 添加变量方法
     * @param variablePlanInputDtoList 变量方案 DTO的list集合
     * @return 添加变量方法的结果
     */
    APIResult addVariablePlan(List<VariablePlanInputDto> variablePlanInputDtoList);

    /**
     * 获取变量方案
     * @param inputDto 输入实体类对象
     * @return 变量方案
     */
    APIResult getVariablePlan(VariableRuleQueryDto inputDto);

    /**
     * 添加变量规则
     * @param dto 输入实体类对象
     * @return 添加变量规则的结果
     */
    APIResult addVariableRule(VariableRuleInputDto dto);

//    APIResult getVariableRule(VariableRuleQueryDto inputDto);
//
//    void exportVariableRule(VariableRuleQueryDto dto, HttpServletResponse response);
//
//    APIResult deleteRuleRecordById(Long id);

    /**
     * 保存变量规则
     * @param dto 输入实体类对象
     * @return 保存变量规则的结果
     */
    APIResult saveVariableRule(VariableRuleInputDto dto);

    /**
     * 通过一系列id删除变量方案
     * @param ids id的list集合
     * @return 通过一系列id删除变量方案的结果
     */
    APIResult deleteVariablePlanByIds(List<String> ids);

    /**
     * 预览变量规则
     * @param dto 输入实体类对象
     * @return 变量规则
     */
    APIResult previewVariableRule(VariableRulePreviewDto dto);
}
