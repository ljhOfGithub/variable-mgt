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

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigDefaultValueQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableConfigDefaultValueOutputDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VariableConfigDefaultValueBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 缺失值设置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */

@RestController
@RequestMapping("/variableConfigDefaultValue")
@Slf4j
@Tag(name = "缺失值设置服务")
@Validated
@LoggableClass(param = "variableConfigDefaultValue")
public class VariableConfigDefaultValueController {

    @Autowired
    private VariableConfigDefaultValueBiz variableConfigDefaultValueBiz;

    /**
     * 缺失值列表
     *
     * @param inputDto 输入实体类对象
     * @return 缺失值列表
     */
    @PostMapping("/list")
    @Operation(summary = "缺失值列表")
    public APIResult<List<VariableConfigDefaultValueOutputDto>> getVariableServiceList(@RequestBody VariableConfigDefaultValueQueryInputDto inputDto) {
        return APIResult.success(variableConfigDefaultValueBiz.getConfigDefaultValueList(inputDto));
    }

    /**
     * 编辑缺失值
     *
     * @param inputDto 输入实体类对象
     * @return 编辑缺失值后的结果
     */
    @PostMapping("/updateConfigDefaultValue")
    @Operation(summary = "编辑缺失值")
    @LoggableDynamicValue(params = {"var_process_config_default","id"})
    @LoggableMethod(value = "将数据类型[%s]的缺失值从[%s]修改为[%s]",params = {"id","defaultValue"}, type = LoggableMethodTypeEnum.DEFAULT_VALUE)
    public APIResult<Boolean> updateConfigDefaultValue(@RequestBody VariableConfigDefaultValueInputDto inputDto) {
        return APIResult.success(variableConfigDefaultValueBiz.updateConfigDefaultValue(inputDto));
    }

    /**
     * 检查数据类型是否已配置
     *
     * @param inputDto 输入实体类对象
     * @return 检查数据类型是否已配置的结果
     */
    @PostMapping("/checkExist")
    @Operation(summary = "检查数据类型是否已配置")
    public APIResult<Boolean> checkExist(@RequestBody VariableConfigDefaultValueCheckInputDto inputDto) {
        return APIResult.success(variableConfigDefaultValueBiz.checkExist(inputDto));
    }

}
