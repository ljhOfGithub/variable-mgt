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
package com.wiseco.var.process.app.server.controller.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.test.dto.TestExecuteOutputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectResultInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestDetailInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestResultDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.TestResultsOutputDto;
import com.wiseco.var.process.app.server.enums.TestResultDetailTabEnum;
import com.wiseco.var.process.app.server.service.TestVariableExecuteBiz;
import com.wiseco.var.process.app.server.service.TestVariableMasterBiz;
import com.wiseco.var.process.app.server.service.dto.TestCollectAndResultsDto;
import com.wiseco.var.process.app.server.service.dto.output.TestResultDetailOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 变量测试
 * @author wangxianli
 * @since  2021/11/30
 */
@RestController
@RequestMapping("/testVariableMaster")
@Slf4j
@Tag(name = "变量测试(主界面)")
public class TestVariableMasterController {
    @Autowired
    private TestVariableMasterBiz testVariableMasterBiz;

    @Autowired
    private TestVariableExecuteBiz testVariableExecuteBiz;

    /**
     * 获取数据集
     *
     * @param inputDto 测试集查询DTO
     * @return IPage
     */
    @GetMapping("/testList")
    @Operation(summary = "获取数据集")
    public APIResult<IPage<TestCollectAndResultsDto>> findTestList(TestCollectInputDto inputDto) {
        return APIResult.success(testVariableMasterBiz.findTestList(inputDto));

    }

    /**
     * 获取数据明细
     *
     * @param inputDto 前端发送过来的输入实体
     * @return TestDetailOutputDto
     */
    @GetMapping("/testDetails")
    @Operation(summary = "获取数据明细")
    public APIResult<TestDetailOutputDto> findStrComponentTestList(TestDetailInputDto inputDto) {
        return APIResult.success(testVariableMasterBiz.findTestDetailList(inputDto));
    }

    /**
     * 修改数据明细
     *
     * @param inputDto inputDto
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/updateDetails")
    @Operation(summary = "修改数据明细")
    public APIResult updateDetails(@RequestBody TestFormUpdateInputDto inputDto) {
        testVariableMasterBiz.updateDetails(inputDto);
        return APIResult.success();

    }

    /**
     * 导出数据的明细
     *
     * @param spaceId  变量空间ID
     * @param testId   测试集ID
     * @param testType 测试类型：1-变量，2-公共函数，3-服务接口
     * @param id       变量/公共函数ID/接口ID
     * @param response 响应体
     */
    @GetMapping("/downTestDetails")
    @Operation(summary = "导出数据明细")
    public void downTestDetails(@RequestParam("spaceId") @Parameter(description = "变量空间ID") @NotNull(message = "变量空间ID不能为空") Long spaceId,
                                @RequestParam("testId") @Parameter(description = "测试集ID") @NotNull(message = "测试集ID不能为空") Long testId,
                                @RequestParam("testType") @Parameter(description = "测试类型：1-变量，2-公共函数，3-服务接口") Integer testType,
                                @RequestParam("id") @Parameter(description = "变量/公共函数ID/接口ID") Long id, HttpServletResponse response) {
        try {
            testVariableMasterBiz.downTestDetails(spaceId, testId, testType, id, response);
        } catch (Exception e) {
            log.error("导出模板异常：", e);
        }
    }

    /**
     * 删除数据集
     *
     * @param ids ids
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/updateDeleteFlag")
    @Operation(summary = "删除数据集")
    public APIResult updateDeleteFlagTestById(@RequestBody Integer[] ids) {
        testVariableMasterBiz.updateDeleteFlagTestById(ids);
        return APIResult.success();
    }

    /**
     * 编辑数据集
     *
     * @param testCollectUpdateInputDto 前端输入的实体对象
     * @return 无
     */
    @PostMapping("/updateTest")
    @Operation(summary = "修改数据集")
    public APIResult updateTestById(@RequestBody @Validated TestCollectUpdateInputDto testCollectUpdateInputDto) {
        testVariableMasterBiz.updateTestById(testCollectUpdateInputDto);
        return APIResult.success();
    }

    /**
     * 复制数据集
     *
     * @param id 数据集ID
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/copyTest")
    @Operation(summary = "复制数据集")
    public APIResult copyTestById(@Parameter(description = "数据集ID") @RequestParam("id") Long id) {
        testVariableMasterBiz.copyTestById(id);
        return APIResult.success();
    }

    /**
     * 合并数据集
     *
     * @param ids ids
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/mergeTest")
    @Operation(summary = "合并数据集")
    public APIResult mergeTest(@RequestBody Integer[] ids) {
        testVariableMasterBiz.mergeTest(ids);
        return APIResult.success();
    }

    /**
     * 执行测试
     * @param testId 数据集ID
     * @param spaceId 变量空间ID
     * @param variableId 变量/公共函数ID/接口ID
     * @return TestExecuteOutputDto
     */
    @GetMapping("/executeTest")
    @Operation(summary = "执行测试")
    public APIResult<TestExecuteOutputDto> executeTest(@Parameter(description = "数据集ID") @RequestParam("testId") Long testId,
                                                       @Parameter(description = "变量空间ID") @RequestParam("spaceId") Long spaceId,
                                                       @Parameter(description = "变量/公共函数ID/接口ID") @RequestParam("id") Long variableId) {
        return APIResult.success(testVariableExecuteBiz.executeTest(testId, spaceId, variableId));
    }

    /**
     * 展示测试结果(左侧明细和上面的结果数字)
     * @param inputDto 前端发送过来的输入实体
     * @return 测试结果
     */
    @GetMapping("/executeResult")
    @Operation(summary = "展示测试结果(左侧明细和上面的结果数字)")
    public APIResult<TestResultsOutputDto> executeResultDataPage(TestCollectResultInputDto inputDto) {
        return APIResult.success(testVariableExecuteBiz.executeResultDataPage(inputDto));
    }

    /**
     * 展示预期结果对比
     * @param testSerialNo 组件测试请求流水号
     * @param testType 测试类型, 查看左侧明细时也要传的参数; 测试数据集的类型, 1-变量定义; 2-数据预处理、变量模板、公共方法; 3.变量清单
     * @return 返回给前端的List
     */
    @GetMapping("/displayExpectationCompare")
    @Operation(summary = "展示预期结果对比")
    public APIResult<TestResultDetailOutputVo> displayExpectationCompare(@RequestParam("testSerialNo") @NotBlank(message = "组件测试请求流水号不能为空") @Schema(description = "组件测试请求流水号") String testSerialNo,
                                                                   @RequestParam("testType") @NotNull(message = "测试类型不能为空") @Schema(description = "测试类型, 查看左侧明细时也要传的参数; 测试数据集的类型, 1-变量定义; 2-数据预处理、变量模板、公共方法; 3.变量清单") Integer testType) {
        // 1.调用业务逻辑层的函数
        TestResultDetailOutputDto testResultDetail = testVariableExecuteBiz.displayExpectationCompare(testSerialNo, testType);
        // 2.转换Bean类，返回给前端
        TestResultDetailOutputVo result = new TestResultDetailOutputVo();
        BeanUtils.copyProperties(testResultDetail, result);
        return APIResult.success(result);
    }

    /**
     * 获取测试组件中展示详情的tab
     * @param testSerialNo 测试流水号
     * @return 测试组件中展示详情的tab
     */
    @GetMapping("/getTestResultTabEnum")
    @Operation(summary = "获取测试组件中展示详情的tab")
    public APIResult<List<Map<String, String>>> getTestResultTabEnum(@RequestParam("testSerialNo") @NotBlank(message = "组件测试请求流水号不能为空") @Schema(description = "组件测试请求流水号") String testSerialNo) {
        // 1.调用业务逻辑层的函数
        List<Map<String, String>> result = testVariableExecuteBiz.getTestResultTabEnum(testSerialNo);
        // 2.返回结果给前端
        return APIResult.success(result);
    }

    /**
     * 按照tab的枚举，展示测试组件详情的内容
     * @param testSerialNo 组件测试请求流水号
     * @param tabEnum tab的枚举
     * @return 测试组件某个tab详情的内容
     */
    @GetMapping("/getTestResultDetailByTab")
    @Operation(summary = "按照tab的枚举，展示测试组件详情的内容")
    public APIResult<String> getTestResultDetailByTab(@RequestParam("testSerialNo") @NotBlank(message = "组件测试请求流水号不能为空") @Schema(description = "组件测试请求流水号") String testSerialNo,
                                                      @RequestParam("tabEnum") @NotNull(message = "tab的枚举不能为空") @Schema(description = "tab的枚举, REQUEST_DATA-请求数据; ENGINE_USED_DATA-引擎使用数据; OUTPUT_RESULT-输出结果; EXCEPTION_MESSAGE-异常信息") TestResultDetailTabEnum tabEnum) {
        // 1.调用业务逻辑层的函数
        String result = testVariableExecuteBiz.getTestResultDetailByTab(testSerialNo, tabEnum);
        // 2.返回结果
        return APIResult.success(result, "操作成功");
    }

    /**
     * 导出测试结果
     *
     * @param state    状态
     * @param id       结果集ID
     * @param response 响应实体
     */
    @GetMapping("/downExecuteResult")
    @Operation(summary = "导出测试结果")
    public void downExecuteResult(@RequestParam("state") @Parameter(description = "状态: 0-全部，1-正常，2-异常，3-预期一致，4-预期不一致") @NotNull(message = "状态不能为空") String state,
                                  @RequestParam("id") @Parameter(description = "测试结果集的ID") @NotNull(message = "测试结果集的ID不能为空") Long id, HttpServletResponse response) {
        try {
            testVariableExecuteBiz.downExecuteResult(state, id, response);
        } catch (Exception e) {
            log.error("导出测试数据：", e);
        }
    }

    /**
     * 转换为预期结果
     *
     * @param id id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/transferExpect/{id}")
    @Operation(summary = "转换为预期结果")
    public APIResult transferExpect(@PathVariable("id") Long id) {
        testVariableExecuteBiz.transferExpect(id);
        return APIResult.success();
    }

}
