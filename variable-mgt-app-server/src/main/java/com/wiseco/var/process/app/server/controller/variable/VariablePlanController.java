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
package com.wiseco.var.process.app.server.controller.variable;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.VariablePlanInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRulePreviewDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableRuleQueryDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VariablePlanService;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/variablePlan")
@Slf4j
@Tag(name = "变量方案，生成变量")
@LoggableClass(param = "variablePlan")
public class VariablePlanController {

    @Resource
    private VariableCompileBiz  variableCompileBiz;

    @Autowired
    private VariablePlanService variablePlanService;

    /**
     * 生成变量查询参数名称
     * @param dto 输入实体类对象
     * @return 变量模板参数名称
     */
    @PostMapping("/getParameters")
    @Operation(summary = "查询变量模板参数名称")
    public APIResult getParameters(@RequestBody VariableRuleQueryDto dto) {
        return variableCompileBiz.getParameters(dto);

    }

    /**
     * 添加变量方案
     * @param variablePlanInputDtoList 输入实体类对象
     * @return 添加变量方案后的结果
     */
    @PostMapping("/addVariablePlan")
    @Operation(summary = "添加变量方案")
    public APIResult addVariablePlan(@RequestBody List<VariablePlanInputDto> variablePlanInputDtoList) {
        return variablePlanService.addVariablePlan(variablePlanInputDtoList);
    }

    /**
     * 删除变量方案
     * @param ids 变量方案id的集合
     * @return 删除变量方案后的结果
     */
    @PostMapping("/deleteVariablePlanByIds")
    @Operation(summary = "删除变量方案")
    public APIResult deleteVariablePlanByIds(@RequestBody List<String> ids) {
        return variablePlanService.deleteVariablePlanByIds(ids);
    }

    /**
     * 查询变量方案
     * @param dto 输入实体类对象
     * @return 变量方案
     */
    @PostMapping("/getVariablePlan")
    @Operation(summary = "查询变量方案")
    public APIResult getVariablePlan(@RequestBody VariableRuleQueryDto dto) {
        return variablePlanService.getVariablePlan(dto);
    }

    /**
     * 变量预览
     * @param dto 输入实体类对象
     * @return 变量预览
     */
    @PostMapping("/previewVariableRule")
    @Operation(summary = "变量预览")
    public APIResult previewVariableRule(@RequestBody VariableRulePreviewDto dto) {
        return variablePlanService.previewVariableRule(dto);
    }

    /**
     * 生成变量
     * @param dto 输入实体类对象
     * @return 生成变量后的结果
     */
    @PostMapping("/addVariableRule")
    @Operation(summary = "生成变量")
    @LoggableDynamicValue(params = {"var_process_function","functionId"})
    @LoggableMethod(value = "使用变量模板[%s]生成变量",params = {"functionId"}, type = LoggableMethodTypeEnum.GENERATE_VARS)
    public APIResult addVariableRule(@RequestBody VariableRuleInputDto dto) {
        log.info("addVariableRule test");
        return variablePlanService.addVariableRule(dto);
    }

    /**
     * 保存生成变量
     * @param dto 输入实体类对象
     * @return 保存生成变量后的结果
     */
    @PostMapping("/saveVariableRule")
    @Operation(summary = "保存生成变量")
    public APIResult saveVariableRule(@RequestBody VariableRuleInputDto dto) {
        return variablePlanService.saveVariableRule(dto);
    }
}
