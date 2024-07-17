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
package com.wiseco.var.process.app.server.controller.function;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.FlowUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionCopyInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.FunctionSaveInputDto;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.FunctionBiz;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/publicMethod")
@Slf4j
@Tag(name =  "公共方法")
@LoggableClass(param = "publicMethod")
public class VarProcessPublicMethodController {

    @Resource
    private FunctionBiz functionBiz;


    /**
     * 添加保存
     *
     * @param inputDto 输入实体类
     * @return 添加公共函数后的Id
     */
    @PostMapping("/saveFunction")
    @Operation(summary = "添加保存")
    @LoggableDynamicValue(params = {"functionType"})
    @LoggableMethod(value = "添加%s[%s]",params = {"functionType","name"}, type = LoggableMethodTypeEnum.CREATE)
    public APIResult<Long> saveFunction(@Validated @RequestBody FunctionSaveInputDto inputDto) {
        return APIResult.success(functionBiz.saveFunction(inputDto));
    }

    /**
     * 编辑保存
     *
     * @param inputDto 输入实体类
     * @return 编辑公共函数后的Id
     */
    @PostMapping("/editFunction")
    @Operation(summary = "编辑保存")
    @LoggableDynamicValue(params = {"functionType"})
    @LoggableMethod(value = "编辑%s[%s]",params = {"functionType","name"}, type = LoggableMethodTypeEnum.EDIT)
    public APIResult<Long> editFunction(@Validated @RequestBody FunctionSaveInputDto inputDto) {
        return APIResult.success(functionBiz.saveFunction(inputDto));
    }

    /**
     * 复制
     *
     * @param inputDto 输入实体类对象
     * @return 复制公共函数后的Id
     */
    @PostMapping("/copyFunction")
    @Operation(summary = "复制")
    @LoggableDynamicValue(params = {"var_process_function","copyId"})
    @LoggableMethod(value = "复制%s[%s]为[%s]",params = {"copyId","functionType","name"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copyFunction(@RequestBody FunctionCopyInputDto inputDto) {
        return APIResult.success(functionBiz.copyFunction(inputDto));
    }

    /**
     * 删除
     *
     * @param spaceId 变量空间Id
     * @param functionId 公共函数Id
     * @return 删除公共函数后的结果
     */
    @GetMapping("/deleteFunction")
    @Operation(summary = "删除")
    @LoggableDynamicValue(params = {"var_process_function","functionId"})
    @LoggableMethod(value = "删除%s[%s]",params = {"functionId"}, type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteFunction(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, @Parameter(description = "公共预处理模板id") @RequestParam("functionId") Long functionId) {
        functionBiz.updateStatus(FlowUpdateStatusInputDto.builder().functionId(functionId).spaceId(spaceId).actionType(FlowActionTypeEnum.DELETE)
                .build());
        return APIResult.success();
    }

    /**
     * 启用停用
     *
     * @param inputDto 输入实体类对象
     * @return 启用公共函数后的结果
     */
    @PostMapping("/updateEnableFunction/{actionType}")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "启用停用")
    public APIResult updateUpFunction(@RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.updateStatus(inputDto);
        return APIResult.success();
    }

    /**
     * 修改状态
     *
     * @param inputDto 输入实体类对象
     * @return 修改状态（提交、审核）后的结果
     */
    @PostMapping("/updateStatus/{actionType}")
    @LoggableDynamicValue(params = {"var_process_function", "functionId"})
    @LoggableMethod(value = "%s%s[%s]", params = {"functionId", "actionType"}, type = LoggableMethodTypeEnum.UPDATE_STATUS)
    @Operation(summary = "修改状态（提交、审核）")
    public APIResult updateStatus(@RequestBody FlowUpdateStatusInputDto inputDto) {
        functionBiz.updateStatus(inputDto);
        return APIResult.success();
    }
}
