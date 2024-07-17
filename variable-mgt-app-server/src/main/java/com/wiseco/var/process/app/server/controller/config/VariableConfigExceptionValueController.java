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
package com.wiseco.var.process.app.server.controller.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigExceptionValueInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigExceptionValueQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableConfigExceptionValueOutputDto;
import com.wiseco.var.process.app.server.service.VariableConfigExceptionValueBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常值设置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */

@RestController
@RequestMapping("/variableConfigExceptionValue")
@Slf4j
@Tag(name = "异常值设置服务")
@Validated
public class VariableConfigExceptionValueController {

    @Autowired
    private VariableConfigExceptionValueBiz variableConfigExceptionValueBiz;

    /**
     * 异常值列表接口查询
     *
     * @param inputDto 输入实体类对象
     * @return 异常值列表接口分页查询的结果
     */
    @PostMapping("/list")
    @Operation(summary = "异常值列表接口查询")
    public APIResult<IPage<VariableConfigExceptionValueOutputDto>> getConfigExceptionValueList(@RequestBody VariableConfigExceptionValueQueryInputDto inputDto) {
        return APIResult.success(variableConfigExceptionValueBiz.getConfigExceptionValueList(inputDto));
    }

    /**
     * 保存或修改异常值配置
     *
     * @param inputDto 输入实体类对象
     * @return 保存或修改异常值配置后的Id
     */
    @PostMapping("/saveOrUpdateConfigExceptionValue")
    @Operation(summary = "保存或修改异常值配置")
    public APIResult<Long> saveOrUpdateConfigExceptionValue(@RequestBody VariableConfigExceptionValueInputDto inputDto) {
        return APIResult.success(variableConfigExceptionValueBiz.saveOrUpdateConfigExceptionValue(inputDto));
    }

    /**
     * 删除异常值配置
     *
     * @param inputDto 输入实体类对象
     * @return 删除异常值配置后的Id
     */
    @PostMapping("/delete")
    @Operation(summary = "删除异常值配置")
    public APIResult<Boolean> delete(@RequestBody VariableConfigExceptionValueInputDto inputDto) {
        return APIResult.success(variableConfigExceptionValueBiz.delete(inputDto));
    }

    /**
     * 检查是否可以删除异常值
     *
     * @param inputDto 输入实体类对象
     * @return 检查是否可以删除异常值的结果
     */
    @PostMapping("/checkDeleteExceptionValue")
    @Operation(summary = "检查是否可以删除异常值")
    public APIResult checkDeleteExceptionValue(@RequestBody VarProcessCategoryCheckInputDto inputDto) {
        variableConfigExceptionValueBiz.checkDeleteExceptionValue(inputDto);
        return APIResult.success();
    }

}
