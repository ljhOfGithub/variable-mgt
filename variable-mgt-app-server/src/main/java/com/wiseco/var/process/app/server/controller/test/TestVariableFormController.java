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

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormPathOutputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormDataConvertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormDataInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormDatagramConvertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormSaveExcelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormTreeInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestGenerateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestProducedDataImportInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestProducedDataSearchInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestSampleInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestTemplateDownInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestTemplateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestVariableCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestFormOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestProducedDataSearchOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestTemplateOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.service.TestVariableFormBiz;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * 变量测试表单 控制器
 *
 * @author wangxianli
 * @since 2022/6/15
 */
@RestController
@RequestMapping("/testVariableForm")
@Slf4j
@Tag(name = "变量测试(表单)")
public class TestVariableFormController {

    @Autowired
    private TestVariableFormBiz testVariableFormBiz;

    /**
     * 验证测试
     *
     * @param inputDto 输入实体类对象
     * @return 变量编译验证返回DTO的list
     */
    @PostMapping("/checkTest")
    @Operation(summary = "验证测试")
    public APIResult<VariableCompileOutputDto> checkTest(@RequestBody TestVariableCheckInputDto inputDto) {

        return APIResult.success(testVariableFormBiz.checkTest(inputDto));
    }

    /**
     * 获取在线表单
     *
     * @param inputDto 前端发送过来的输入实体
     * @return 表单结构出参Dto的list
     */
    @PostMapping("/formData")
    @Operation(summary = "获取在线表单")
    public APIResult<TestFormOutputDto> getFormData(@RequestBody TestFormInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.getFormData(inputDto));
    }

    /**
     * 将表单数据转换为 JSON 报文
     *
     * @param inputDto 输入实体类对象
     * @return 表单数据转换为后的JSON报文
     */
    @PostMapping("/convertFormDataToDatagram")
    @Operation(summary = "将表单数据转换为 JSON 报文", description =  "需要提供表单输入数据 inputValue")
    public APIResult<String> convertFormDataToDatagram(@RequestBody TestFormDataConvertInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.convertFormDataToDatagram(inputDto), "操作成功");
    }

    /**
     * 将 JSON/XML 报文转换为表单数据数值
     * @param inputDto 输入实体
     * @return JSON/XML报文转换后的表单数据数值
     * @throws JAXBException JAXBException
     * @throws JsonProcessingException JsonProcessingException
     */
    @PostMapping("/convertDatagramToFormData")
    @Operation(summary = "将 JSON/XML 报文转换为表单数据数值", description =  "仅提供表单输入数据 inputValue")
    public APIResult<JSONObject> convertDatagramToFormData(@RequestBody TestFormDatagramConvertInputDto inputDto) throws JAXBException,
            JsonProcessingException {
        return APIResult.success(testVariableFormBiz.convertDatagramToFormDataValue(inputDto));
    }

    /**
     * 获取预期变量树
     *
     * @param inputDto 输入实体
     * @return 预期变量树
     */
    @PostMapping("/formExpectVarsTree")
    @Operation(summary = "获取预期变量树")
    public APIResult<List<DomainDataModelTreeDto>> formExpectVarsTree(@RequestBody TestFormTreeInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.formExpectVarsTree(inputDto));
    }

    /**
     * 转换预期表单数据
     *
     * @param inputDto 输入实体类
     * @return 预期表单数据
     */
    @PostMapping("/formExpectData")
    @Operation(summary = "转换预期表单数据")
    public APIResult<List<TestFormPathOutputDto>> formExpectData(@RequestBody TestFormTreeInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.formExpectData(inputDto));
    }

    /**
     * 保存在线表单
     *
     * @param inputDto 输入实体类
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/saveFormData")
    @Operation(summary = "保存在线表单")
    public APIResult saveFormData(@RequestBody TestFormDataInputDto inputDto) {
        testVariableFormBiz.saveFormData(inputDto);
        return APIResult.success();
    }

    /**
     * 获取规则配置数据对象
     *
     * @param inputDto 输入实体类
     * @return 规则配置数据对象
     */
    @PostMapping("/ruleModelTree")
    @Operation(summary = "获取规则配置数据对象")
    public APIResult<List<TestFormPathOutputDto>> getRuleModelTree(@RequestBody TestInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.getRuleModelTree(inputDto));
    }

    /**
     * 生成样例数据
     *
     * @param inputDto 输入实体类
     * @return 测试数据明细响应DTO
     */
    @PostMapping("/generateSampleData")
    @Operation(summary = "生成样例数据")
    public APIResult<TestDetailOutputDto> generateSampleData(@RequestBody TestGenerateInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.generateSampleData(inputDto));
    }

    /**
     * 生成并保存测试数据
     *
     * @param inputDto 输入实体类
     * @return 生成并保存测试数据的结果
     */
    @PostMapping("/generateAndStoreTestData")
    @Operation(summary = "生成并保存测试数据")
    public APIResult<String> generateAndStoreTestData(@RequestBody TestGenerateInputDto inputDto) {
        testVariableFormBiz.generateAndStoreTestData(inputDto);
        return APIResult.success("测试数据已生成并保存。");
    }

    /**
     * 样本数据分页查询
     * @param inputDto 输入实体类
     * @return 样本数据分页查询的结果
     */
    @GetMapping("/sampleDataPage")
    @Operation(summary = "样本数据分页查询")
    public APIResult<TestDetailOutputDto> sampleDataPage(TestSampleInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.sampleDataPage(inputDto));
    }

    /**
     * 获取模板变量
     *
     * @param inputDto 输入实体类
     * @return 模板变量
     */
    @GetMapping("/templateFormList")
    @Operation(summary = "获取模板变量")
    public APIResult<TestTemplateOutputDto> getTemplateFormList(TestTemplateInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.getTemplateFormList(inputDto));
    }

    /**
     * 导出模板
     *
     * @param inputDto 输入实体类
     * @param response HttpServletResponse对象
     */
    @PostMapping("/downExcelTemplate")
    @Operation(summary = "导出模板")
    public void downExcelTemplate(@RequestBody TestTemplateDownInputDto inputDto, HttpServletResponse response) {
        try {
            testVariableFormBiz.downExcelTemplate(inputDto, response);
        } catch (Exception e) {
            log.error("导出模板异常：", e);

        }
    }

    /**
     * 导入Excel表格
     *
     * @param inputDto 前端传过来的实体对象
     * @param file     文件对象
     * @return 测试数据明细响应(包含了uuid 、 测试明细表头 、 测试明细和总记录数)
     */
    @PostMapping("/importExcel")
    @Operation(summary = "导入数据")
    public APIResult<TestDetailOutputDto> importExcel(TestCollectInputDto inputDto, @RequestParam("file") MultipartFile file) {
        return APIResult.success(testVariableFormBiz.importExcel(inputDto, file));
    }

    /**
     * 保存 Excel 样本数据
     *
     * @param inputDto 输入实体类
     * @return 保存Excel样本数据的结果
     */
    @PostMapping("/saveSampleData")
    @Operation(summary = "保存 Excel 样本数据")
    public APIResult<String> saveSampleData(@RequestBody TestFormSaveExcelInputDto inputDto) {
        testVariableFormBiz.saveSampleData(inputDto);
        return APIResult.success("Excel 样本数据保存成功。");
    }

    /**
     * 获取生产数据
     *
     * @param inputDto 输入实体类
     * @return 生产数据
     */
    @PostMapping("/queryProducedData")
    @Operation(summary = "获取生产数据")
    public APIResult<TestProducedDataSearchOutputDto> queryProducedData(@RequestBody TestProducedDataSearchInputDto inputDto) {
        return APIResult.success(testVariableFormBiz.queryProducedData(inputDto));
    }

    /**
     * 导入生产数据
     *
     * @param inputDto 生产数据导入保存参数 DTO
     * @return 导入生产数据的结果
     */
    @PostMapping("/importProducedData")
    @Operation(summary = "导入生产数据")
    public APIResult importProducedData(@RequestBody TestProducedDataImportInputDto inputDto) {
        testVariableFormBiz.importProducedData(inputDto);
        return APIResult.success();
    }
}
