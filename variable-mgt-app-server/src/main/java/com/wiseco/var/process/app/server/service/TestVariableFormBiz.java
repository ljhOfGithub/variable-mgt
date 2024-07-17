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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.pboc2.Pboc2Convertor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.TestDataGenerationUtil;
import com.wiseco.var.process.app.server.commons.test.TestExcelUtils;
import com.wiseco.var.process.app.server.commons.test.TestExecuteUtil;
import com.wiseco.var.process.app.server.commons.test.TestFormUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableDataUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableExportUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableHeaderUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableImportUtil;
import com.wiseco.var.process.app.server.commons.test.dto.TestEventExcelDataDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormDictDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormPathOutputDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestGenerateRulesDto;
import com.wiseco.var.process.app.server.commons.util.StringPool;
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
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.StrComVarFlagEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestDataSourceEnum;
import com.wiseco.var.process.app.server.enums.test.TestDetailDataFieldsEnum;
import com.wiseco.var.process.app.server.enums.test.TestExcelFileEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestRules;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVar;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestData;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoPreviewTestDataHeader;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessLog;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wiseco.var.process.app.server.service.dto.TestTableHeaderDto;
import com.wiseco.var.process.app.server.service.dto.VariableBaseDetailDto;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.multipleimpl.PreTestDataHeaderService;
import com.wiseco.var.process.app.server.service.multipleimpl.PreTestDataService;
import com.wiseco.var.process.app.server.service.multipleimpl.ProductDataService;
import com.wiseco.var.process.app.server.service.multipleimpl.VarProcessTestVariableDataService;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 变量测试 业务类
 * @author wangxianli
 * @since 2022/6/13
 */
@Service
@Slf4j
public class TestVariableFormBiz extends TestVariablePrivate {

    public static final String XML = "<?xml";
    public static final String DOCUMENT = "<Document>";
    public static final String PREFIX = "{";
    @Autowired
    private VarProcessTestService varProcessTestVariableService;

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VariableVarBiz variableVarBiz;

    @Autowired
    private VarProcessTestRulesService varProcessTestVariableRulesService;

    @Autowired
    private VarProcessTestVariableDataService varProcessTestVariableDataService;

    @Autowired
    private ProductDataService productDataService;

    @Autowired
    private VariableCompileBiz variableCompileBiz;

    @Autowired
    private PreTestDataService previewTestDataService;

    @Autowired
    private PreTestDataHeaderService previewTestDataHeaderService;

    @Autowired
    private VarProcessTestVarService varProcessTestVariableVarService;
    /**
     * 递归组合数据
     * @param inputValueMasterMap 输入数据 master 部分 Map
     * @param inputValueMapList   输入数据 Map
     * @param formDtoMap          表单结构 Map
     * @return 报文 JSON Object
     */
    private static JSONObject transferMapToFormJsonObject(Map<String, Object> inputValueMasterMap,
                                                          Map<String, List<Map<String, Object>>> inputValueMapList,
                                                          Map<String, TestFormDto> formDtoMap) {
        JSONObject mapResult = new JSONObject(true);
        //按key中含点的数量升序
        Map<String, Object> targetMap = new TreeMap<>(new Comparator<String>() {
            public int compare(String key1, String key2) {
                int keyCount1 = org.springframework.util.StringUtils.countOccurrencesOf(key1, ".");
                int keyCount2 = org.springframework.util.StringUtils.countOccurrencesOf(key2, ".");
                return (keyCount1 >= keyCount2) ? 1 : MagicNumbers.MINUS_INT_1;
            }
        });
        targetMap.putAll(inputValueMasterMap);
        for (Map.Entry<String, Object> masterMapEntry : targetMap.entrySet()) {
            String key = masterMapEntry.getKey();
            Object value = masterMapEntry.getValue();
            if (inputValueMapList.containsKey(key)) {
                JSONObject retTmpMap = new JSONObject();
                retTmpMap.put(TestDetailDataFieldsEnum.NAME.getCode(), key);
                retTmpMap.put(TestDetailDataFieldsEnum.IS_ARR.getCode(), DomainModelArrEnum.YES.getCode());
                retTmpMap.put(TestDetailDataFieldsEnum.TYPE.getCode(), DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
                List<JSONObject> valueList = new ArrayList<>();
                List<Map<String, Object>> maps = inputValueMapList.get(key);
                for (Map<String, Object> map : maps) {
                    if (String.valueOf(inputValueMasterMap.get(TestExcelFileEnum.ID.getCode())).equals(
                            String.valueOf(map.get(TestExcelFileEnum.PARENT_ID.getCode())))) {
                        JSONObject jsonObject = transferMapToFormJsonObject(map, inputValueMapList, formDtoMap);
                        if (jsonObject == null) {
                            continue;
                        }
                        valueList.add(jsonObject);
                    }
                }
                retTmpMap.put("value", valueList);
                if (valueList.size() > 0) {
                    mapResult.put(key, retTmpMap);
                }
            } else {
                if (!key.contains(StringPool.DOT) || !formDtoMap.containsKey(key)) {
                    continue;
                }
                TestFormDto testFormDto = formDtoMap.get(key);
                JSONObject retTmpMap = new JSONObject();
                retTmpMap.put(TestDetailDataFieldsEnum.NAME.getCode(), key);
                retTmpMap.put(TestDetailDataFieldsEnum.IS_ARR.getCode(), testFormDto.getIsArr());
                retTmpMap.put(TestDetailDataFieldsEnum.TYPE.getCode(), testFormDto.getType());
                retTmpMap.put("value", value);
                mapResult.put(key, retTmpMap);
            }
        }
        return mapResult;
    }
    /**
     * 测试检查
     * @param inputDto 输入实体类对象
     * @return 测试检查结果
     */
    public VariableCompileOutputDto checkTest(TestVariableCheckInputDto inputDto) {
        String content = null;
        if (inputDto.getContent() != null) {
            content = inputDto.getContent().toJSONString();
        } else {
            if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
                VarProcessManifest varProcessManifest = varProcessManifestService.getById(inputDto.getId());
                content = varProcessManifest.getContent();
            }
            if (org.springframework.util.StringUtils.isEmpty(content)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "流程内容不能为空");
            }
        }
        VariableCompileOutputDto compileResultVo = variableCompileBiz.validate(TestVariableTypeEnum.getCode(inputDto.getTestType()),
                inputDto.getSpaceId(), inputDto.getId(), content);
        if (compileResultVo.isState()) {
            variableVarBiz.saveTestVar(inputDto.getSpaceId(), TestVariableTypeEnum.getCode(inputDto.getTestType()), inputDto.getId(),
                    compileResultVo.getCompileResultVo());
        }
        compileResultVo.setCompileResultVo(null);
        return compileResultVo;
    }
    /**
     * 获取输入输出参数
     * @param inputDto 测试集查询输入参数 DTO
     * @return 结果实体类
     */
    public TestFormOutputDto getFormData(TestFormInputDto inputDto) {
        // 1.查询策略获取domian_data_model_id
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间！");
        }
        // 2.获取数据模型
        Map<String, List<TestFormDictDto>> dictDetails = getDictDetails(inputDto.getSpaceId());
        // 3.输入变量input_content  输出变量output_content 引擎变量 engine_vars 外部服务externalData 公共决策commonData
        Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());

        // 4.引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        TestFormOutputDto outputDto = new TestFormOutputDto();
        Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
        modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());
        // 5.引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            JSONObject varsJsonSchema = getVarsJsonSchema(inputDto.getId());
            if (varsJsonSchema != null && varsJsonSchema.size() > 0) {
                modelVarsMap.put(PositionVarEnum.VARS.getName(), varsJsonSchema.toJSONString());
            }
        }
        // 6.获取输入变量
        List<TestFormDto> formDtoList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModel);
        if (!CollectionUtils.isEmpty(formDtoList)) {
            List<TestFormPathOutputDto> formInputDto = TestFormUtil.transferComVarsToFrom(formDtoList, modelVarsMap, dictDetails, new HashMap<>(MagicNumbers.EIGHT));
            outputDto.setInput(formInputDto);
        }
        // 7.获取预期结果表头
        List<TestFormPathOutputDto> expectToForm = null;
        if (inputDto.getTestId() != null && inputDto.getTestId() > 0) {
            VarProcessTest varProcessTest = varProcessTestVariableService.getById(inputDto.getTestId());
            List<TestFormDto> outputFormDtoList = getVariableVarOutput(inputDto.getTestType(), inputDto.getId(), dataModel);
            //追加预期结果
            if (!StringUtils.isEmpty(varProcessTest.getTableHeaderField()) && !CollectionUtils.isEmpty(outputFormDtoList)) {
                List<TestFormDto> expectFormList = TestFormUtil.transferComponentExpectToForm(outputFormDtoList,
                        JSON.parseObject(varProcessTest.getTableHeaderField()));
                if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
                    DomainDataModelTreeDto dto = outputFormToModelTree(expectFormList);
                    expectToForm = new ArrayList<>();
                    TestFormUtil.recursionModelTreeToForm(dto, dictDetails, new HashMap<>(MagicNumbers.EIGHT), expectToForm);
                } else {
                    expectToForm = TestFormUtil.transferComVarsToFrom(expectFormList, modelVarsMap, dictDetails, new HashMap<>(MagicNumbers.EIGHT));
                }
                List<String> expectList = TestFormUtil.transferFormToStringList(expectFormList);
                outputDto.setExpectHeader(expectList);
            }
        }
        // 8.获取输入数据、输出数据和预期结果变量
        if (!StringUtils.isEmpty(inputDto.getDataId())) {
            MongoVarProcessTestVariableData mongoVarProcessTestVariableData = varProcessTestVariableDataService.findOneByTestDataId(inputDto.getTestId(),
                    Integer.parseInt(inputDto.getDataId()));
            if (mongoVarProcessTestVariableData != null) {
                if (!StringUtils.isEmpty(mongoVarProcessTestVariableData.getInputContent())) {
                    JSONObject content = JSONObject.parseObject(mongoVarProcessTestVariableData.getInputContent());
                    outputDto.setInputValue(content);
                }
                if (!CollectionUtils.isEmpty(expectToForm) && !StringUtils.isEmpty(mongoVarProcessTestVariableData.getExpectContent())) {
                    JSONObject content = JSONObject.parseObject(mongoVarProcessTestVariableData.getExpectContent());
                    outputDto.setExpectValue(content);
                }
            }
        }
        outputDto.setExpect(expectToForm);
        return outputDto;
    }
    /**
     * 转换表单数据为报文
     * @param inputDto 输入实体类对象
     * @return 报文
     */
    public String convertFormDataToDatagram(TestFormDataConvertInputDto inputDto) {
        // 获取输入数据 JSON Object
        JSONObject inputValue = inputDto.getInputValue();
        if (CollectionUtils.isEmpty(inputValue)) {
            return "{}";
        }

        // 获取输入数据表单结构 DTO List
        VarProcessSpace space = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (null == space) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间，无法将表单数据转换为 JSON 报文。");
        }
        // 获取测试所需全部数据的树形结构 DTO, key: 变量全路径, value: 树形结构 DTO
        Map<String, DomainDataModelTreeDto> dataModelTreeDtoMap = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        // 测试类型为 "变量测试": 追加 "引用衍生变量"
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (!CollectionUtils.isEmpty(varsDataModelMap)) {
                dataModelTreeDtoMap.putAll(varsDataModelMap);
            }
        }
        // 获取表单结构 DTO List
        List<TestFormDto> formDtoList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModelTreeDtoMap);
        // 将数据表单结构 DTO List 转换为 Map, key: 变量全路径, value: 表单结构 DTO
        Map<String, TestFormDto> formDtoMap = formDtoList.stream().collect(Collectors.toMap(TestFormDto::getName, x -> x));
        // 转换输入数据格式: 从表单格式改为引擎使用的报文格式
        // 转换输入数据 JSON 为 Map, key: 输入数据 key (master, object_path_1, object_path_2, ...), value: 输入数据 JSON List ({master}})
        Map<String, List<Map<String, Object>>> inputValueMapList = TestExecuteUtil.transferJsonToMapList(inputValue);
        // 提取输入数据主表格内容 (master)
        Map<String, Object> inputValueMasterMap = inputValueMapList.get(TestTableEnum.MASTER.getCode()).get(0);
        JSONObject result = transferMapToFormJsonObject(inputValueMasterMap, inputValueMapList, formDtoMap);
        // 空白报文
        JSONObject datagram = new JSONObject(true);
        Map<String, String> nodeIdMap = new HashMap<>(MagicNumbers.TEN);
        Map<String, Boolean> arrayFlagMap = new HashMap<>(MagicNumbers.TEN);
        TestExecuteUtil.buildTargetJsonObject(result, datagram, null, nodeIdMap, arrayFlagMap);
        TestExecuteUtil.removeJsonEmptyValue(datagram);
        return datagram.toString();
    }

    /**
     * 转换报文为表单数据
     * @param inputDto 输入实体类对象
     * @return JSON对象
     * @throws JAXBException 异常
     * @throws JsonProcessingException 异常
     */
    public JSONObject convertDatagramToFormDataValue(TestFormDatagramConvertInputDto inputDto) throws JAXBException, JsonProcessingException {
        // 读取数据 ID (默认值为 1)
        String dataId = null == inputDto.getDataId() ? "1" : String.valueOf(inputDto.getDataId());
        // 0. 预处理报文
        JSONObject datagramJsonObject = preprocessInputDatagram(inputDto.getDatagram().trim());
        // 1. 将输入报文 JSON Object 转换为输入在线填写表单数据, 以 results 作为数据值 JSON Object key
        // e.g. "results": {"${var_full_path}": ${var_full_path_value}}
        JSONObject flattenedDatagramJsonObject = TestTableDataUtil.transValuePathMap(dataId, datagramJsonObject);
        // 将数据值 JSON Object key 从 results 改为 master
        if (!CollectionUtils.isEmpty(flattenedDatagramJsonObject)) {
            flattenedDatagramJsonObject = TestExecuteUtil.transferResultData(flattenedDatagramJsonObject);
        }
        // 2. 过滤表单结构中定义的报文变量
        // 获取测试所需全部数据的树形结构 DTO Map, key: 变量全路径, value: 树形结构 DTO
        Map<String, DomainDataModelTreeDto> dataModelTreeDtoMap = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(),
                inputDto.getId());

        Map<String, DomainDataModelTreeDto> varsDataModelMap = new HashMap<>();
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            // 测试类型为 "变量测试": 追加 "引用衍生变量"
            varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (!CollectionUtils.isEmpty(varsDataModelMap)) {
                dataModelTreeDtoMap.putAll(varsDataModelMap);
            }
        }

        // parameter信息
        List<VarProcessTestVar> paramList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.INPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
                .eq(VarProcessTestVar::getTestType, inputDto.getTestType())
                .eq(VarProcessTestVar::getVariableId, inputDto.getId()));

        // 获取表单结构中定义的变量路径集合, 添加特殊变量路径 master, id 和 parentId
        Set<String> definedVariablePathSet = new HashSet<>(dataModelTreeDtoMap.keySet());
        definedVariablePathSet.add("master");
        definedVariablePathSet.add("id");
        definedVariablePathSet.add("parentId");
        for(VarProcessTestVar param : paramList){
            definedVariablePathSet.add(param.getVarPath());
            //如果是数组，则添加下属属性
            if(param.getIsArray()==1){
                DomainDataModelTreeDto dto = dataModelTreeDtoMap.get(param.getParameterType());
                List<String> nameList = dto.getChildren().stream().map(DomainDataModelTreeDto::getName).map(item -> param.getVarPath()+"."+item).collect(Collectors.toList());
                definedVariablePathSet.addAll(nameList);
            }
        }
//        paramList.forEach(param -> {
//            definedVariablePathSet.add(param.getVarPath());
//        });

        // 过滤表单结构中定义的报文变量
        filterDefinedInputValue(flattenedDatagramJsonObject, definedVariablePathSet,dataModelTreeDtoMap);
        // 使用 JSON Object 封装所有表单数据, 将 key 设为 inputValue, 与前端表单保持一致
        JSONObject inputValueJsonObject = new JSONObject();
        inputValueJsonObject.put("inputValue", flattenedDatagramJsonObject);
        return inputValueJsonObject;
    }

    /**
     * 预处理输入报文
     * @param datagram 输入报文 String
     * @return 报文转换结果 JSON Object
     * @throws JAXBException           XML 解析异常
     * @throws JsonProcessingException XML 解析异常
     */
    private JSONObject preprocessInputDatagram(String datagram) throws JAXBException, JsonProcessingException {
        if (StringUtils.isEmpty(datagram)) {
            // 用户输入报文为空: 将报文初始化为 "{}", 防止方法返回的数据为空 JSON Object
            datagram = "{}";
        }
        // 根据输入报文格式进行不同处理
        if (datagram.startsWith(XML) || datagram.startsWith(DOCUMENT)) {
            // 输入报文格式为标准 XML 或人行征信 XML: 调用工具类转换为 JSON String
            datagram = Pboc2Convertor.xml2json(datagram);
            // 将输入报文结构封装于 rawData.Document 下, 使所有变量路径为 rawData.Document.*
            JSONObject datagramJsonObject = new JSONObject(true);
            datagramJsonObject.put(PositionVarEnum.RAW_DATA.getName(), new JSONObject(true));
            datagramJsonObject.getJSONObject(PositionVarEnum.RAW_DATA.getName()).put("Document", JSON.parseObject(datagram, Feature.OrderedField));
            return datagramJsonObject;
        } else if (datagram.startsWith(PREFIX)) {
            // 输入报文格式为 JSON
            JSONObject datagramJsonObject = JSON.parseObject(datagram, Feature.OrderedField);
            if (!datagramJsonObject.containsKey(PositionVarEnum.RAW_DATA.getName()) && !datagramJsonObject.containsKey(PositionVarEnum.PARAMETERS.getName())) {
                // 输入报文不包含根 rawData: 将当前内容封装, 作为 key rawData 对应的 value
                JSONObject datagramJsonObjectWithRawDataRoot = new JSONObject(true);
                datagramJsonObjectWithRawDataRoot.put(PositionVarEnum.RAW_DATA.getName(), datagramJsonObject);
                datagramJsonObject = datagramJsonObjectWithRawDataRoot;
            }
            return datagramJsonObject;
        } else {
            // 输入报文格式非 JSON, XML: 抛出异常
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "报文格式不正确，请输入标准 JSON 或人行征信中心 XML 格式报文。");
        }
    }

    /**
     * 过滤表单结构中定义的报文变量
     * @param inputValue             转换自报文的表单输入数据
     * @param definedVariablePathSet 表单结构中定义的变量路径集合
     * @param dataModelTreeDtoMap 模型树map
     */
    private void filterDefinedInputValue(JSONObject inputValue, Set<String> definedVariablePathSet,Map<String, DomainDataModelTreeDto> dataModelTreeDtoMap) {
        Iterator<Map.Entry<String, Object>> iterator = inputValue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (!definedVariablePathSet.contains(entry.getKey())) {
                // 移除表单未定义的报文变量
                iterator.remove();
                continue;
            }

            if (entry.getValue() instanceof JSONArray) {
                // 遍历 JSONArray 全部元素
                JSONArray entryValueJsonArray = (JSONArray) entry.getValue();
                for (Object entryValueJsonArrayElement : entryValueJsonArray) {
                    // 递归调用
                    filterDefinedInputValue((JSONObject) entryValueJsonArrayElement, definedVariablePathSet, dataModelTreeDtoMap);
                }
            } else if (entry.getValue() instanceof JSONObject) {
                // 递归调用
                filterDefinedInputValue((JSONObject) entry.getValue(), definedVariablePathSet,dataModelTreeDtoMap);
            } else {
                // 特殊处理空值, 避免将 null 转换为字符串 "null"
                String variableValue = null != entry.getValue() ? String.valueOf(entry.getValue()) : null;
                // JSONArray 元素不属于对象类型: 中止遍历
                TestTableImportUtil.ValuePrimitiveDataTypeValidationBiFunction action = new TestTableImportUtil.ValuePrimitiveDataTypeValidationBiFunction();
                if (dataModelTreeDtoMap.get(entry.getKey()) != null) {
                    entry.setValue(action.apply(variableValue, dataModelTreeDtoMap.get(entry.getKey())));
                }
            }
        }
    }
    /**
     * 获取变量树
     * @param inputDto 输入实体类对象
     * @return 变量树
     */
    public List<DomainDataModelTreeDto> formExpectVarsTree(TestFormTreeInputDto inputDto) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间！");
        }
        if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            //实时服务接口
            List<TestFormDto> formDtoList = getManifestOutputVariable(inputDto.getId());
            if (!CollectionUtils.isEmpty(inputDto.getExcludeVarList())) {
                formDtoList = formDtoList.stream().filter(f -> !(inputDto.getExcludeVarList().contains(f.getName()))).collect(Collectors.toList());

            }
            List<DomainDataModelTreeDto> outputList = new ArrayList<>();
            outputList.add(outputFormToModelTree(formDtoList));
            return outputList;
        } else {
            Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());
            List<TestFormDto> formDtoList = getVariableVarOutputWithoutObjectArr(inputDto.getTestType(), inputDto.getId(), dataModel);
            if (CollectionUtils.isEmpty(formDtoList)) {
                return new ArrayList<>();
            }
            Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
            modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());
            if (!CollectionUtils.isEmpty(inputDto.getExcludeVarList())) {
                formDtoList = formDtoList.stream().filter(f -> !(inputDto.getExcludeVarList().contains(f.getName()))).collect(Collectors.toList());

            }
            return TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
        }
    }
    /**
     * 获取期望的表单数据
     * @param inputDto 输入实体类对象
     * @return 表单路径字段结构出参Dto的list集合
     */
    public List<TestFormPathOutputDto> formExpectData(TestFormTreeInputDto inputDto) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间！");
        }
        //数据模型
        Map<String, List<TestFormDictDto>> dictDetails = getDictDetails(inputDto.getSpaceId());
        if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            //实时服务接口
            List<TestFormDto> formDtoList = getManifestOutputVariable(inputDto.getId());
            if (!CollectionUtils.isEmpty(inputDto.getIncludeVarList())) {
                formDtoList = formDtoList.stream().filter(f -> (inputDto.getIncludeVarList().contains(f.getName()))).collect(Collectors.toList());

            }
            DomainDataModelTreeDto dto = outputFormToModelTree(formDtoList);

            List<TestFormPathOutputDto> resultList = new ArrayList<>();
            TestFormUtil.recursionModelTreeToForm(dto, dictDetails, new HashMap<>(MagicNumbers.EIGHT), resultList);
            return resultList;
        } else {
            Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());
            List<TestFormDto> formDtoList = getVariableVarOutput(inputDto.getTestType(), inputDto.getId(), dataModel);
            if (CollectionUtils.isEmpty(formDtoList)) {
                return new ArrayList<>();
            }
            Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
            modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());

            return TestFormUtil.transferComponentModelTreeToFormByInclude(formDtoList, modelVarsMap, inputDto.getIncludeVarList(), dictDetails, new HashMap<>(MagicNumbers.EIGHT));
        }

    }

    /**
     * 在线保存表单
     * @param inputDto 前端发送过来的实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveFormData(TestFormDataInputDto inputDto) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间！");
        }
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(inputDto.getTestType(), inputDto.getId());
        //保存数据集
        VarProcessTest varProcessTest = new VarProcessTest();
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME);
        varProcessTest.setIdentifier(variableBaseDetail.getIdentifier());
        varProcessTest.setVariableId(inputDto.getId());
        varProcessTest.setTestType(inputDto.getTestType());
        varProcessTest.setSource(TestDataSourceEnum.INPUT.getMessage());
        varProcessTest.setDataCount(1);
        if (!CollectionUtils.isEmpty(inputDto.getExpectHeader())) {
            Map<String, DomainDataModelTreeDto> dataModel = new HashMap<>(MagicNumbers.EIGHT);
            List<DomainDataModelTreeDto> dataModelTreeDtos = new ArrayList<>();
            if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
                //实时服务接口
                List<TestFormDto> formDtoList = getManifestOutputVariable(inputDto.getId());
                DomainDataModelTreeDto outputModelTree = outputFormToModelTree(formDtoList);
                DomainModelTreeEntityUtils.handleDataModelMap(outputModelTree.getName(), outputModelTree, dataModel);
                dataModelTreeDtos.add(outputModelTree);
            } else {
                dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());
                List<TestFormDto> formDtoList = getVariableVarOutput(inputDto.getTestType(), inputDto.getId(), dataModel);
                Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
                modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());
                dataModelTreeDtos = TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
            }
            //加工测试数据表头
            List<TestFormDto> formDataList = TestFormUtil.transferFormExpectHeader(dataModelTreeDtos, inputDto.getExpectHeader());
            JSONObject testData = TestTableHeaderUtil.getTestData(formDataList, dataModel, 1);
            JSONObject expectHeaderObject = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
            JSONObject targetExpectHeader = new JSONObject();
            for (Map.Entry<String, Object> entry : expectHeaderObject.entrySet()) {
                if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                    targetExpectHeader.put(TestTableEnum.EXPECT.getCode(), entry.getValue());
                } else {
                    targetExpectHeader.put(entry.getKey(), entry.getValue());
                }
            }
            varProcessTest.setTableHeaderField(JSONObject.toJSONString(targetExpectHeader));
        }
        varProcessTest.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTest.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessTestVariableService.save(varProcessTest);

        int seqNo = getTestCount(inputDto.getSpaceId(), varProcessTest.getIdentifier());
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTest.setSeqNo(seqNo);
        varProcessTestVariableService.updateById(varProcessTest);
        //加工原始数据、删除多余字段
        //处理表单数据
        JSONObject resultJson = TestFormUtil.mergeFormData(inputDto.getInputData(), inputDto.getExpectData(), "1");
        String inputContent = null;
        String expectContent = null;
        if (resultJson.containsKey(TestTableEnum.INPUT.getCode())) {
            inputContent = resultJson.getJSONObject(TestTableEnum.INPUT.getCode()).toJSONString();
        }
        if (resultJson.containsKey(TestTableEnum.EXPECT.getCode())) {
            expectContent = resultJson.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString();
        }
        // 保存表单数据至 MongoDB/mysql
        varProcessTestVariableDataService.saveOne(varProcessTest.getId(), 1, inputContent, expectContent);
    }
    /**
     * 获取规则模型树
     * @param inputDto 输入实体类对象
     * @return 规则模型树
     */
    public List<TestFormPathOutputDto> getRuleModelTree(TestInputDto inputDto) {
        List<TestFormPathOutputDto> resultList = new ArrayList<>();
        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();
        if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            //实时服务接口
            DomainDataModelTreeDto inputDataModel = getManifestInputDataModel(inputDto.getId());
            treeDtoList.add(inputDataModel);
        } else {
            VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
            if (null == varProcessSpace) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间！");
            }
            Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());
            //引用衍生变量
            if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
                Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
                if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                    dataModel.putAll(varsDataModelMap);
                }
            }
            List<TestFormDto> formDtoList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModel);
            if (CollectionUtils.isEmpty(formDtoList)) {
                return new ArrayList<>();
            }
            Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
            modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());
            //引用衍生变量
            if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
                JSONObject varsJsonSchema = getVarsJsonSchema(inputDto.getId());
                if (varsJsonSchema != null && varsJsonSchema.size() > 0) {
                    modelVarsMap.put(PositionVarEnum.VARS.getName(), varsJsonSchema.toJSONString());
                }
            }
            treeDtoList = TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
        }
        for (DomainDataModelTreeDto treeDto : treeDtoList) {
            TestFormUtil.recursionModelTreeToRuleConfigObject(treeDto, resultList);
        }
        return resultList;
    }

    /**
     * 生成样本数据
     * @param inputDto 输入实体类对象
     * @return 样本数据
     */
    public TestDetailOutputDto generateSampleData(TestGenerateInputDto inputDto) {
        // 0. 确定测试数据变量自动生成规则
        // 获取数据模型 Map, key: 数据模型位置, value: 领域数据模型树形结构 DTO
        Map<String, DomainDataModelTreeDto> dataModelMap = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        //引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModelMap.putAll(varsDataModelMap);
            }
        }
        // 为输入变量配置测试数据自动生成规则
        List<TestGenerateRulesDto> testGenerateRulesDtoList = configGenerationRulesForInputVariables(inputDto, dataModelMap);
        // 1. 生成样例数据和表头
        TestDataGenerationUtil testDataGenerationUtil = new TestDataGenerationUtil();
        Pair<JSONObject, List<String>> generatedHeaderDataPair = testDataGenerationUtil.generateData(CommonConstant.SAMPLE_DATA_GENERATION_SIZE,
                testGenerateRulesDtoList, dataModelMap, 1);
        // 样例数据表头
        String generatedHeader = generatedHeaderDataPair.getKey().toJSONString();
        // 样例数据 List
        List<String> generatedDataList = generatedHeaderDataPair.getValue();
        // 2. 保存样例数据
        // 生成样例数据集 UUID
        String sampleDataSetUuid = UUID.randomUUID().toString();
        // 保存样例数据表头
        List<MongoPreviewTestData> mongoPreviewTestDataList = new ArrayList<>();
        for (String generatedData : generatedDataList) {
            MongoPreviewTestData mongoPreviewTestData = new MongoPreviewTestData();
            mongoPreviewTestData.setUuid(sampleDataSetUuid);
            mongoPreviewTestData.setDataContent(generatedData);
            mongoPreviewTestData.setCreatedTime(new Date());
            mongoPreviewTestDataList.add(mongoPreviewTestData);
        }
        previewTestDataService.saveAll(mongoPreviewTestDataList);
        // 保存样例数据
        MongoPreviewTestDataHeader mongoPreviewTestDataHeader = new MongoPreviewTestDataHeader();
        mongoPreviewTestDataHeader.setUuid(sampleDataSetUuid);
        mongoPreviewTestDataHeader.setHeaderContent(generatedHeader);
        mongoPreviewTestDataHeader.setCreatedTime(new Date());
        previewTestDataHeaderService.saveOne(mongoPreviewTestDataHeader);
        return TestDetailOutputDto.builder().uuid(sampleDataSetUuid).totalNums(CommonConstant.SAMPLE_DATA_GENERATION_SIZE).build();
    }
    /**
     * 生成并存储测试数据
     * @param inputDto 输入实体类对象
     */
    public void generateAndStoreTestData(TestGenerateInputDto inputDto) {
        // 0. 限制数据自动生成行数
        // 前端设定的数据自动生成行数
        int generateAmount = inputDto.getGenerateNums();
        // 限制前端显示的数据行数
        if (generateAmount > CommonConstant.DEFAULT_TEST_SAMPLES_NUMS) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据自动生成数量设定不能超过 " + CommonConstant.DEFAULT_TEST_SAMPLES_NUMS + " 条。");
        }
        // 1. 确定测试数据变量自动生成规则
        // 获取数据模型 Map, key: 数据模型位置, value: 领域数据模型树形结构 DTO
        Map<String, DomainDataModelTreeDto> dataModelMap = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        //引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModelMap.putAll(varsDataModelMap);
            }
        }
        // 为输入变量配置测试数据自动生成规则
        List<TestGenerateRulesDto> testGenerateRulesDtoList = configGenerationRulesForInputVariables(inputDto, dataModelMap);
        // 2. 生成测试数据集元信息, 保存至 MySQL
        // 获取变量/公共函数基本信息
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(inputDto.getTestType(), inputDto.getId());
        // 获取下一个测试数据集序号 (用于拼接名称 "测试数据集 X")
        Integer seqNo = varProcessTestVariableService.findMaxSeqNoByIdentifier(inputDto.getSpaceId(), variableBaseDetail.getIdentifier()) + 1;
        VarProcessTest varProcessTestDataSetMetaInfo = new VarProcessTest();
        varProcessTestDataSetMetaInfo.setIdentifier(variableBaseDetail.getIdentifier());
        varProcessTestDataSetMetaInfo.setSeqNo(seqNo);
        varProcessTestDataSetMetaInfo.setTestType(inputDto.getTestType());
        varProcessTestDataSetMetaInfo.setVariableId(inputDto.getId());
        varProcessTestDataSetMetaInfo.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTestDataSetMetaInfo.setRemark("");
        varProcessTestDataSetMetaInfo.setSource(TestDataSourceEnum.AUTO.getMessage());
        varProcessTestDataSetMetaInfo.setDataCount(generateAmount);
        // 不需要设置 "预期结果表头字段", 由于自动生成测试数据无法生成预期结果
        varProcessTestDataSetMetaInfo.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTestDataSetMetaInfo.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTestDataSetMetaInfo.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTestDataSetMetaInfo.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessTestVariableService.save(varProcessTestDataSetMetaInfo);
        // 3. 批量生成, 保存测试数据集至 MongoDB/mysql
        // 确定批次数量
        int batchNumber = generateAmount / CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE;
        int generateAmountReminder = generateAmount % CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE;
        if (generateAmountReminder > 0) {
            // 自动生成数据行数无法被批次大小整除: 新增批次数量
            batchNumber++;
        }
        // 测试数据集序列号 (dataId)
        int dataSetSequenceNumber = 1;
        for (int i = 0; i < batchNumber; i++) {
            // 确定当前批次数据生成数量
            int currentBatchSize = CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE;
            if (i == batchNumber - 1 && generateAmountReminder > 0) {
                // 最后批次不完整: 手动设置生成数量
                currentBatchSize = generateAmountReminder;
            }
            // 获取自动生成测试数据
            TestDataGenerationUtil testDataGenerationUtil = new TestDataGenerationUtil();
            Pair<JSONObject, List<String>> generatedHeaderDataPair = testDataGenerationUtil.generateData(currentBatchSize, testGenerateRulesDtoList,
                    dataModelMap, dataSetSequenceNumber);
            // 保存测试数据
            List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = new ArrayList<>(currentBatchSize);
            for (String generatedData : generatedHeaderDataPair.getValue()) {
                MongoVarProcessTestVariableData mongoVarProcessTestVariableData = MongoVarProcessTestVariableData.builder()
                        .testId(varProcessTestDataSetMetaInfo.getId()).dataId(dataSetSequenceNumber).createdTime(new Date()).inputContent(generatedData)
                        // 自动生成测试数据无法生成预期结果
                        .expectContent(null).build();
                mongoVarProcessTestVariableDataList.add(mongoVarProcessTestVariableData);
                dataSetSequenceNumber++;
            }
            varProcessTestVariableDataService.saveBatch(mongoVarProcessTestVariableDataList);
        }
        // 4. 根据样例数据集 UUID 删除预览数据
        if (!StringUtils.isEmpty(inputDto.getSampleDataSetUuid())) {
            previewTestDataHeaderService.removeOneByUuid(inputDto.getSampleDataSetUuid());
            previewTestDataService.removeAllByUuid(inputDto.getSampleDataSetUuid());
        }
    }
    /**
     * 为输入变量配置测试数据自动生成规则
     * @param inputDto            测试自动生成数据配置入参 DTO
     * @param dataModelTreeDtoMap 数据模型树形结构 Map, key: 数据模型位置, value: 领域数据模型树形结构 DTO
     * @return 测试数据变量自动生成规则 List
     */
    private List<TestGenerateRulesDto> configGenerationRulesForInputVariables(TestGenerateInputDto inputDto, Map<String, DomainDataModelTreeDto> dataModelTreeDtoMap) {
        // 从数据模型分析输入变量
        List<TestFormDto> testFormDtoList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModelTreeDtoMap);
        if (CollectionUtils.isEmpty(testFormDtoList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "没有输入变量，不能在线自动生成数据。");
        }
        // 获取测试数据生成规则 List
        List<VarProcessTestRules> rulesList = varProcessTestVariableRulesService.list(new QueryWrapper<VarProcessTestRules>().lambda()
                .eq(VarProcessTestRules::getVarProcessSpaceId, inputDto.getSpaceId())
        );
        // 将生成规则 List 转换为 Map, key: ${变量路径}_${变量类型}, value: 数据生成规则 DTO
        Map<String, VarProcessTestRules> rulesMap = rulesList.stream().collect(Collectors.toMap(x -> x.getVarPath() + "_" + x.getVarType(), Function.identity(), (e1, e2) -> e1));
        // 为每个输入变量设置生成规则
        List<TestGenerateRulesDto> testGenerateRulesDtoList = new ArrayList<>();
        for (TestFormDto testFormDto : testFormDtoList) {
            String index = testFormDto.getName() + "_" + testFormDto.getType();
            if (!rulesMap.containsKey(index)) {
                continue;
            }
            TestGenerateRulesDto generateInputDto = new TestGenerateRulesDto();
            BeanUtils.copyProperties(testFormDto, generateInputDto);

            VarProcessTestRules testRules = rulesMap.get(index);
            generateInputDto.setGenerateMode(testRules.getGenerateMode());
            generateInputDto.setGenerateRule(testRules.getGenerateRule());
            generateInputDto.setGenerateRuleFormula(testRules.getGenerateRuleFormula());
            generateInputDto.setGenerateRuleDesc(testRules.getGenerateRuleDesc());
            testGenerateRulesDtoList.add(generateInputDto);
        }
        if (CollectionUtils.isEmpty(testGenerateRulesDtoList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "请先配置自动生成规则。");
        }
        return testGenerateRulesDtoList;
    }
    /**
     * 获取测试数据明细响应
     * @param inputDto 输入实体类对象
     * @return 测试数据明细响应DTO
     */
    public TestDetailOutputDto sampleDataPage(TestSampleInputDto inputDto) {
        // 表头查询
        MongoPreviewTestDataHeader entity = previewTestDataHeaderService.getOneByUuid(inputDto.getUuid());
        if (null == entity) {
            // 临时表表头信息不存在: 所有临时数据已被清除
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "临时表数据已被清除，无法继续查询。");
        }
        Map<String, Object> tableHeaderField = TestTableHeaderUtil.headerSort(JSON.parseObject(entity.getHeaderContent()));
        // 数据分页查询
        List<MongoPreviewTestData> mongoPreviewTestDataList = previewTestDataService.findPageByUuid(inputDto.getUuid(), inputDto.getPage(), inputDto.getSize());
        List<String> dataList = mongoPreviewTestDataList.stream().map(MongoPreviewTestData::getDataContent).collect(Collectors.toList());
        // 新建输出参数对象并返回
        return TestDetailOutputDto.builder()
                .uuid(inputDto.getUuid())
                .tableHeaderField(tableHeaderField)
                .dataList(dataList)
                // 分页页数由前端计算, 后端仅提供总记录数
                .totalNums(previewTestDataService.countByUuid(inputDto.getUuid()))
                .build();
    }
    /**
     * 获取模板的Excel表
     * @param inputDto 输入实体类对象
     * @return 下载Excel模板DTO
     */
    public TestTemplateOutputDto getTemplateFormList(TestTemplateInputDto inputDto) {
        TestTemplateOutputDto outputDto = new TestTemplateOutputDto();
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getSpaceId());
        Map<String, DomainDataModelTreeDto> dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());
        //引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
        modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());
        //引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            JSONObject varsJsonSchema = getVarsJsonSchema(inputDto.getId());
            if (varsJsonSchema != null && varsJsonSchema.size() > 0) {
                modelVarsMap.put(PositionVarEnum.VARS.getName(), varsJsonSchema.toJSONString());
            }
        }
        List<TestFormDto> formDtoList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModel);
        if (!CollectionUtils.isEmpty(formDtoList)) {
            List<DomainDataModelTreeDto> treeDtoList = TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
            outputDto.setInput(treeDtoList);
        }
        //输出
        if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            //实时服务接口
            formDtoList = getManifestOutputVariable(inputDto.getId());
            if (!CollectionUtils.isEmpty(formDtoList)) {
                DomainDataModelTreeDto outputModelTree = outputFormToModelTree(formDtoList);
                List<DomainDataModelTreeDto> outputDtoList = new ArrayList<>();
                outputDtoList.add(outputModelTree);
                outputDto.setOutput(outputDtoList);
            }
        } else {
            formDtoList = getVariableVarOutput(inputDto.getTestType(), inputDto.getId(), dataModel);
            if (!CollectionUtils.isEmpty(formDtoList)) {
                List<DomainDataModelTreeDto> treeDtoList = TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
                outputDto.setOutput(treeDtoList);
            }
        }
        return outputDto;
    }
    /**
     * 导出模板
     * @param inputDto 输入实体类对象
     * @param response HttpServletResponse
     */
    public void downExcelTemplate(TestTemplateDownInputDto inputDto, HttpServletResponse response) {
        // 1.获取这个测试组件对应的数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        // 2.引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        List<TestFormDto> formDataList = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModel);
        //预期结果
        List<DomainDataModelTreeDto> formData = inputDto.getFormData();
        if (!CollectionUtils.isEmpty(formData)) {
            for (DomainDataModelTreeDto treeDto : formData) {
                TestFormDto testFormDto = new TestFormDto();
                testFormDto.setName(treeDto.getValue());
                testFormDto.setLabel(treeDto.getDescribe());
                testFormDto.setType(treeDto.getType());
                testFormDto.setIsParameterArray(!StringUtils.isEmpty(treeDto.getIsParameterArray()) ? Integer.parseInt(treeDto.getIsParameterArray())
                        : 0);
                testFormDto.setParameterType(treeDto.getParameterType());
                testFormDto.setIsArr(Integer.parseInt(treeDto.getIsArr()));
                testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));

                formDataList.add(testFormDto);
            }
        }
        //组装模板数据
        List<TestExcelDto> list = TestTableExportUtil.exportTemplateData(formDataList, dataModel);
        //导出excel数据
        SXSSFWorkbook xssfWorkbook = null;
        try {
            xssfWorkbook = TestExcelUtils.getExportExcelWb(list);

            TestExcelUtils.setResponseHeader(response, "模板下载.xlsx");
            OutputStream outputStream = response.getOutputStream();
            xssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("导出Excel文件异常: {}", e.getMessage());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出Excel文件异常！");
        } finally {
            if (xssfWorkbook != null) {
                // 删除临时文件
                xssfWorkbook.dispose();
            }
            try {
                if (xssfWorkbook != null) {
                    xssfWorkbook.close();
                }
            } catch (IOException e) {
                log.error("导出Excel文件异常: {}", e.getMessage());
            }
        }
    }

    /**
     * 导入Excel表格
     * @param inputDto 前端传过来的实体对象
     * @param file     文件对象
     * @return 测试数据明细响应(包含了uuid 、 测试明细表头 、 测试明细和总记录数)
     */
    public TestDetailOutputDto importExcel(TestCollectInputDto inputDto, MultipartFile file) {
        // 1.获取数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        // 2.引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        // 3.测试预览数据 UUID
        String uuid = UUID.randomUUID().toString();
        try {
            TestEventExcelDataDto testEventExcelDataDto = TestTableImportUtil.eventReadExcelData(file.getInputStream());
            Map<String, Map<Integer, String>> headerMap = testEventExcelDataDto.getHeaderMap();

            // 校验导入 Excel 数据变量类型, 转换数值
            List<String> dataList = TestTableImportUtil.traverseAndProcessImportedExcelValue(testEventExcelDataDto.getDataList(),
                    dataModel,
                    true,
                    new TestTableImportUtil.ValuePrimitiveDataTypeValidationBiFunction(),
                    new TestTableImportUtil.ValuePrimitiveDataTypeRegulationBiFunction());

            // 总生成记录数
            int totalNums = dataList.size();
            int i = 1;
            List<MongoPreviewTestData> excelImportedPreviewDataBuffer = new ArrayList<>(CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE);
            for (String dataStr : dataList) {
                // 批量保存 Excel 导入数据至 MongoDB/mysql
                MongoPreviewTestData excelImportedPreviewData = new MongoPreviewTestData();
                excelImportedPreviewData.setUuid(uuid);
                excelImportedPreviewData.setDataContent(dataStr);
                excelImportedPreviewData.setCreatedTime(new Date());
                excelImportedPreviewDataBuffer.add(excelImportedPreviewData);
                if (i == totalNums || CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE == excelImportedPreviewDataBuffer.size()) {
                    // 遍历至最后一行数据或满足批次数量: 保存并清除 List
                    previewTestDataService.saveAll(excelImportedPreviewDataBuffer);
                    excelImportedPreviewDataBuffer.clear();
                }
                i++;
            }
            //处理Excel导入表头信息
            handleImportDataHeader(uuid, inputDto, headerMap, dataModel);
            // 定义返回实体
            TestDetailOutputDto outputDto = new TestDetailOutputDto();
            outputDto.setUuid(uuid);
            outputDto.setTotalNums(totalNums);
            return outputDto;
        } catch (IOException ioe) {
            log.error("测试数据集文件导入异常: {}", ioe.getMessage());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "测试数据集文件导入异常");
        }
    }
    /**
     * 处理Excel导入表头信息
     * @param uuid
     * @param inputDto
     * @param headerMap
     * @param dataModel
     */
    private void handleImportDataHeader(String uuid, TestCollectInputDto inputDto, Map<String, Map<Integer, String>> headerMap,
                                        Map<String, DomainDataModelTreeDto> dataModel) {
        List<TestFormDto> varList = getVariableVar(inputDto.getTestType(), inputDto.getId(), dataModel);
        Map<String, TestFormDto> comVarMapTmp = new HashMap<>(MagicNumbers.TEN);
        for (TestFormDto dto : varList) {
            comVarMapTmp.put(dto.getName(), dto);
        }
        Map<String, List<TestTableHeaderDto>> headerMapObj = new HashMap<>(MagicNumbers.TEN);
        //处理预期结果
        if (headerMap.size() > 0) {
            //主表
            Map<Integer, String> masterStringMap = headerMap.get(TestTableEnum.MASTER.getCode());
            List<TestTableHeaderDto> masterList = handleExcelHeader(masterStringMap, dataModel, comVarMapTmp, InputExpectTypeEnum.INPUT.getCode());
            //预期结果主表
            if (headerMap.containsKey(TestTableEnum.EXPECT.getCode())) {
                Map<Integer, String> expectStringMap = headerMap.get(TestTableEnum.EXPECT.getCode());
                List<TestTableHeaderDto> expectList = handleExcelHeader(expectStringMap, dataModel, comVarMapTmp,
                        InputExpectTypeEnum.EXPECT.getCode());
                masterList.addAll(expectList);
            }
            headerMapObj.put(TestTableEnum.MASTER.getCode(), masterList);
            Set<Map.Entry<String, Map<Integer, String>>> headerMapEntries = headerMap.entrySet();
            for (Map.Entry<String, Map<Integer, String>> headerMapEntry : headerMapEntries) {
                String key = headerMapEntry.getKey();
                if (key.equals(TestTableEnum.MASTER.getCode()) || key.equals(TestTableEnum.EXPECT.getCode())) {
                    continue;
                }
                String fieldType;
                if (key.startsWith(TestTableEnum.EXPECT.getCode())) {
                    fieldType = InputExpectTypeEnum.EXPECT.getCode();

                } else {
                    fieldType = InputExpectTypeEnum.INPUT.getCode();
                }
                List<TestTableHeaderDto> tmpList = handleExcelHeader(headerMapEntry.getValue(), dataModel, comVarMapTmp, fieldType);
                headerMapObj.put(key, tmpList);
            }
        }
        // 保存表头信息至 MongoDB/mysql
        MongoPreviewTestDataHeader previewDataHeader = new MongoPreviewTestDataHeader();
        previewDataHeader.setUuid(uuid);
        previewDataHeader.setHeaderContent(JSON.toJSONString(headerMapObj));
        previewDataHeader.setCreatedTime(new Date());
        previewTestDataHeaderService.saveOne(previewDataHeader);
    }
    /**
     * 保存样本数据
     * @param inputDto 输入实体类对象
     */
    public void saveSampleData(TestFormSaveExcelInputDto inputDto) {
        // 0. 生成测试数据集元信息, 保存至 MySQL
        // 获取变量/公共函数基本信息
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(inputDto.getTestType(), inputDto.getId());
        // 从 Excel 导入的记录总数
        int importedAmount = previewTestDataService.countByUuid(inputDto.getUuid());
        // 获取下一个测试数据集序号 (用于拼接名称 "测试数据集 X")
        Integer seqNo = varProcessTestVariableService.findMaxSeqNoByIdentifier(inputDto.getSpaceId(), variableBaseDetail.getIdentifier()) + 1;
        // 测试数据集元信息
        VarProcessTest varProcessTestDataSetMetaInfo = new VarProcessTest();
        varProcessTestDataSetMetaInfo.setIdentifier(variableBaseDetail.getIdentifier());
        varProcessTestDataSetMetaInfo.setSeqNo(seqNo);
        varProcessTestDataSetMetaInfo.setTestType(inputDto.getTestType());
        varProcessTestDataSetMetaInfo.setVariableId(inputDto.getId());
        varProcessTestDataSetMetaInfo.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTestDataSetMetaInfo.setRemark("");
        varProcessTestDataSetMetaInfo.setSource(TestDataSourceEnum.FILE.getMessage());
        varProcessTestDataSetMetaInfo.setDataCount(importedAmount);
        varProcessTestDataSetMetaInfo.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTestDataSetMetaInfo.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTestDataSetMetaInfo.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        // 获取预期结果表头
        String testPreviewHeaderContent = previewTestDataHeaderService.getOneByUuid(inputDto.getUuid()).getHeaderContent();
        Map<String, List<Map<String, Object>>> originalHeader = JSON.parseObject(testPreviewHeaderContent,
                new TypeReference<Map<String, List<Map<String, Object>>>>() {
                });
        // 拆分表头
        JSONObject targetHeader = TestTableDataUtil.splitHeader(originalHeader);
        if (targetHeader.containsKey(TestTableEnum.EXPECT.getCode())) {
            varProcessTestDataSetMetaInfo.setTableHeaderField(targetHeader.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString());
        }
        varProcessTestDataSetMetaInfo.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessTestVariableService.save(varProcessTestDataSetMetaInfo);
        // 1. 批量获取预览数据集, 保存测试数据集至 MongoDB/mysql
        // 确定批次数量
        int batchNumber = importedAmount / CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE;
        int importAmountReminder = importedAmount % CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE;
        if (importAmountReminder > 0) {
            // 导入数据行数无法被批次大小整除: 新增批次数量
            batchNumber++;
        }
        // 测试数据集序列号 (dataId)
        int dataSetSequenceNumber = 1;
        for (int i = 0; i < batchNumber; i++) {
            // 分页查询 Excel 导入的测试预览数据, (分页查询页码数起始值为 0)
            List<MongoPreviewTestData> previewDataList = previewTestDataService.findPageByUuid(inputDto.getUuid(), i,
                    CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE);
            // 将测试预览数据转换为测试数据
            List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = new ArrayList<>(CommonConstant.TEST_DATA_GENERATION_BATCH_SIZE);
            for (MongoPreviewTestData previewData : previewDataList) {
                // 拆分输入数据和预期结果数据
                JSONObject jsonObject = JSON.parseObject(previewData.getDataContent());
                Integer dataId = dataSetSequenceNumber;
                if (jsonObject.containsKey(TestTableEnum.MASTER.getCode())) {
                    dataId = jsonObject.getJSONObject(TestTableEnum.MASTER.getCode()).getInteger("id");
                }
                JSONObject splitContent = TestTableDataUtil.splitData(jsonObject);
                String inputContent = null;
                String expectContent = null;
                if (splitContent.containsKey(TestTableEnum.INPUT.getCode())) {
                    inputContent = splitContent.getJSONObject(TestTableEnum.INPUT.getCode()).toJSONString();
                }
                if (splitContent.containsKey(TestTableEnum.EXPECT.getCode())) {
                    expectContent = splitContent.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString();
                }
                MongoVarProcessTestVariableData mongoVarProcessTestVariableData = MongoVarProcessTestVariableData.builder()
                        .testId(varProcessTestDataSetMetaInfo.getId()).dataId(dataId).createdTime(new Date()).inputContent(inputContent)
                        .expectContent(expectContent).build();
                mongoVarProcessTestVariableDataList.add(mongoVarProcessTestVariableData);
                dataSetSequenceNumber++;
            }
            varProcessTestVariableDataService.saveBatch(mongoVarProcessTestVariableDataList);
        }
        // 2. 根据预览数据集 UUID 删除预览数据
        previewTestDataHeaderService.removeOneByUuid(inputDto.getUuid());
        previewTestDataService.removeAllByUuid(inputDto.getUuid());
    }
    /**
     * 将Excel表头转换为表格表头
     * @param headerStringMap
     * @param dataModel
     * @param comVarMapTmp
     * @param fieldType
     * @return TestTableHeaderDto List
     */
    private List<TestTableHeaderDto> handleExcelHeader(Map<Integer, String> headerStringMap, Map<String, DomainDataModelTreeDto> dataModel,
                                                       Map<String, TestFormDto> comVarMapTmp, String fieldType) {
        List<TestTableHeaderDto> list = new ArrayList<>();
        if (headerStringMap == null || headerStringMap.size() == 0) {
            return list;
        }
        Set<Map.Entry<Integer, String>> entries = headerStringMap.entrySet();
        for (Map.Entry<Integer, String> entry : entries) {
            String name = entry.getValue();
            if ("id".equals(name) || "parentId".equals(name)) {
                continue;
            }
            TestTableHeaderDto headerTmpDto = new TestTableHeaderDto();
            if (fieldType.equals(InputExpectTypeEnum.EXPECT.getCode())) {
                headerTmpDto.setIndex(TestTableEnum.EXPECT.getCode() + "." + name);
            } else {
                headerTmpDto.setIndex(name);
            }
            headerTmpDto.setName(name);
            headerTmpDto.setFieldType(Integer.parseInt(fieldType));
            if (comVarMapTmp.containsKey(name)) {
                TestFormDto testFormDto = comVarMapTmp.get(name);
                headerTmpDto.setIsArr(testFormDto.getIsArr());
                headerTmpDto.setLabel(testFormDto.getLabel());
                headerTmpDto.setType(testFormDto.getType());
            } else if (dataModel.containsKey(name)) {
                DomainDataModelTreeDto treeDto = dataModel.get(name);
                headerTmpDto.setIsArr(Integer.parseInt(treeDto.getIsArr()));
                headerTmpDto.setLabel(treeDto.getDescribe());
                headerTmpDto.setType(treeDto.getType());
            } else {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "数据模型未找到【" + name + "】");
            }
            list.add(headerTmpDto);
        }
        return list;
    }
    /**
     * 查询产生的数据
     * @param inputDto 输入实体类对象
     * @return 生产数据查询出参DTO
     */
    public TestProducedDataSearchOutputDto queryProducedData(TestProducedDataSearchInputDto inputDto) {
        VarProcessManifest varProcessManifest = varProcessManifestService.getById(inputDto.getManifestId());
        Map<String, DomainDataModelTreeDto> dataModel = getManifestDataModel(inputDto.getManifestId());
        dataModel.putAll(outputDataModelMap(inputDto.getManifestId()));
        //查询str_com_var表 输入
        List<TestFormDto> formDtoList = getManifestInputVariable(inputDto.getManifestId());
        formDtoList.addAll(getManifestOutputVariable(inputDto.getManifestId()));
        JSONObject testData = TestTableHeaderUtil.getTestData(formDtoList, dataModel, 1);
        if (testData == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "数据明细下获取表头失败！");
        }
        JSONObject header = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
        header = TestTableHeaderUtil.headerSort(header);
        // 1. 将 "生产数据导入查询条件输入参数 DTO" 保存为 Map
        // 分析精确匹配条件
        Map<String, String> exactConditions = new HashMap<>(MagicNumbers.FOUR);
        exactConditions.put("spaceId", String.valueOf(varProcessManifest.getVarProcessSpaceId()));
        if (StringUtils.isNotEmpty(inputDto.getExecutionStatus())) {
            exactConditions.put("resultStatus", inputDto.getExecutionStatus());
        }
        // 分析日期范围匹配条件
        Map<String, Pair<String, String>> dateConditions = new HashMap<>(1);
        // 格式化日期定义为 yyyy-MM-dd HH:mm:ss
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String intervalStartDateString = inputDto.getExecutionIntervalStart() != null ? format.format(inputDto.getExecutionIntervalStart()) : null;
        String intervalEndDateString = inputDto.getExecutionIntervalEnd() != null ? format.format(inputDto.getExecutionIntervalEnd()) : null;
        dateConditions.put("requestTime", new Pair<>(intervalStartDateString, intervalEndDateString));
        // 2. 从 MongoDB/mysql 分页查询 ServiceLog 记录
        // 获取查询记录 ServiceLog
        List<MongoVarProcessLog> serviceLogList = productDataService.selectByCondition(exactConditions, dateConditions,
                MongoVarProcessLog.class, inputDto.getPageNo(), inputDto.getPageSize(), "requestTime");
        if (CollectionUtils.isEmpty(serviceLogList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未查询到历史数据");
        }
        // 查询结果记录总行数
        long totalDocumentAmount = productDataService.countByCondition(exactConditions, dateConditions);
        // 每页记录行数
        long documentAmountPerPage = 0;
        if (!CollectionUtils.isEmpty(serviceLogList) && 0L != Long.valueOf(inputDto.getPageSize())) {
            documentAmountPerPage = totalDocumentAmount / Long.valueOf(inputDto.getPageSize()) + 1;
        }
        List<String> recordsList = new ArrayList<>();
        int i = 1;
        for (MongoVarProcessLog mongoVarProcessLog : serviceLogList) {
            recordsList.add(buildDataJsonObject(String.valueOf(i), mongoVarProcessLog).toJSONString());
            i++;
        }
        return TestProducedDataSearchOutputDto.builder().totalNums(totalDocumentAmount).totSize(documentAmountPerPage)
                .pageNum(Long.valueOf(inputDto.getPageNo())).tableHeaderField(header).dataList(recordsList).build();
    }
    /**
     * 导入生成的数据
     * @param inputDto 输入实体类对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void importProducedData(TestProducedDataImportInputDto inputDto) {
        VarProcessManifest varProcessManifest = varProcessManifestService.getById(inputDto.getManifestId());
        // 1. 从 MongoDB/mysql 查询相关 ServiceLog
        // 将 "生产数据导入查询条件输入参数 DTO" 保存为 MongoDB/mysql 精确匹配条件 Map
        Map<String, String> exactConditions = new HashMap<>(MagicNumbers.FOUR);
        exactConditions.put("spaceId", String.valueOf(varProcessManifest.getVarProcessSpaceId()));
        if (StringUtils.isNotEmpty(inputDto.getExecutionStatus())) {
            exactConditions.put("resultStatus", inputDto.getExecutionStatus());
        }
        // 分析日期范围匹配条件
        Map<String, Pair<String, String>> dateConditions = new HashMap<>(1);
        // 格式化日期定义为 yyyy-MM-dd HH:mm:ss
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String intervalStartDateString = inputDto.getExecutionIntervalStart() != null ? format.format(inputDto.getExecutionIntervalStart()) : null;
        String intervalEndDateString = inputDto.getExecutionIntervalEnd() != null ? format.format(inputDto.getExecutionIntervalEnd()) : null;
        dateConditions.put("requestTime", new Pair<>(intervalStartDateString, intervalEndDateString));

        // 利用分页方式获取查询指定 ServiceLog 记录数量
        List<MongoVarProcessLog> serviceLogList = productDataService.selectByCondition(exactConditions, dateConditions,
                MongoVarProcessLog.class, 1, inputDto.getDataCount(), "requestTime");
        if (CollectionUtils.isEmpty(serviceLogList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未查询到历史数据");
        }
        // 2. 保存来自 ServiceLog 的测试数据集信息至 RDBMS
        //保存数据集
        VarProcessTest varProcessTest = new VarProcessTest();
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME);
        varProcessTest.setIdentifier(String.valueOf(varProcessManifest.getIdentifier()));
        varProcessTest.setVariableId(varProcessManifest.getId());
        varProcessTest.setTestType(TestVariableTypeEnum.MANIFEST.getCode());
        varProcessTest.setSource(TestDataSourceEnum.PROD.getMessage());
        varProcessTest.setDataCount(serviceLogList.size());

        JSONObject expectHeaderMap = getExpectHeader(inputDto);
        if (expectHeaderMap.size() > 0) {
            varProcessTest.setTableHeaderField(JSONObject.toJSONString(expectHeaderMap));
        }
        varProcessTest.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTest.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setVarProcessSpaceId(varProcessManifest.getVarProcessSpaceId());
        varProcessTestVariableService.save(varProcessTest);
        int seqNo = getTestCount(varProcessManifest.getVarProcessSpaceId(), varProcessTest.getIdentifier());
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTest.setSeqNo(seqNo);
        varProcessTestVariableService.updateById(varProcessTest);
        // 3. 保存 ServiceLog 查询结果至 MongoDB/mysql
        // 初始化数据集数据序列号为1
        int dataId = 1;
        List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = new ArrayList<>();
        for (MongoVarProcessLog mongoVarProcessLog : serviceLogList) {
            // 转换生产数据记录 DTO 为 JSON 字符串
            JSONObject testContentJson = importBuildDataJsonObject(String.valueOf(dataId), mongoVarProcessLog);
            // 创建策略测试数据文档并添加到列表
            MongoVarProcessTestVariableData mongoVarProcessTestVariableData = MongoVarProcessTestVariableData.builder().testId(varProcessTest.getId())
                    .dataId(dataId).createdTime(new Date()).inputContent(testContentJson.getJSONObject(PositionVarEnum.INPUT.getName()).toJSONString())
                    .build();
            if (testContentJson.containsKey(TestTableEnum.EXPECT.getCode())) {
                mongoVarProcessTestVariableData.setExpectContent(testContentJson.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString());
            }
            mongoVarProcessTestVariableDataList.add(mongoVarProcessTestVariableData);
            dataId++;
        }
        varProcessTestVariableDataService.saveBatch(mongoVarProcessTestVariableDataList);
    }

    /**
     * 组装带有"预期结果"部分的表头信息
     *
     * @param inputDto 生产数据导入保存参数 DTO
     * @return 表头信息
     */
    private JSONObject getExpectHeader(TestProducedDataImportInputDto inputDto) {
        List<TestFormDto> manifestOutputVariable = getManifestOutputVariable(inputDto.getManifestId());
        JSONArray headerListTmp = new JSONArray();
        for (TestFormDto formDto : manifestOutputVariable) {
            //表头赋值
            JSONObject headerTmpDto = new JSONObject();
            headerTmpDto.put("index", formDto.getName());
            headerTmpDto.put("name", formDto.getName());
            headerTmpDto.put("isArr", formDto.getIsArr());
            headerTmpDto.put("label", formDto.getLabel());
            headerTmpDto.put("type", formDto.getType());
            headerTmpDto.put("fieldType", formDto.getFieldType());
            headerListTmp.add(headerTmpDto);
        }
        JSONObject newHeader = new JSONObject();
        if (headerListTmp.size() > 0) {
            newHeader.put(TestTableEnum.EXPECT.getCode(), headerListTmp);
        }

        return newHeader;

    }

    /**
     * 转换决策服务报文日志为数据集内容 JSON
     *
     * @param id            测试数据集内的数据序列号
     * @param mongoVarProcessLog 决策报文日志 DTO
     * @return 数据集内容 JSON
     */
    private JSONObject buildDataJsonObject(String id, MongoVarProcessLog mongoVarProcessLog) {
        // 创建空 JSON 对象, 保存处理最终结果
        JSONObject dataJsonObject = new JSONObject();
        // 将请求报文作为"输入"部分添加到结果 JSON
        dataJsonObject.put(PositionVarEnum.RAW_DATA.getName(), JSON.parseObject(mongoVarProcessLog.getRawData()));
        // 将响应报文作为"预期输出"部分, 添加到结果 JSON (如有)
        if (!StringUtils.isEmpty(mongoVarProcessLog.getResponseJson())) {
            JSONObject tmpJson = new JSONObject();
            tmpJson.put(PositionVarEnum.OUTPUT.getName(), JSON.parseObject(mongoVarProcessLog.getResponseJson()));
            dataJsonObject.put(TestTableEnum.EXPECT.getCode(), tmpJson);
        }
        // 清除导入生产数据的空值
        TestExecuteUtil.removeJsonEmptyValue(dataJsonObject);
        // 重新组织结果 JSON 对象结构
        JSONObject data = TestTableDataUtil.transValuePathMap(id, dataJsonObject);
        JSONObject newData = new JSONObject();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            if (key.equals(TestTableEnum.RESULTS.getCode())) {
                newData.put(TestTableEnum.MASTER.getCode(), entry.getValue());
            } else {
                newData.put(key, entry.getValue());
            }
        }
        return newData;
    }

    /**
     * 转换决策服务报文日志为数据集内容 JSON
     *
     * @param id            测试数据集内的数据序列号
     * @param mongoVarProcessLog 决策报文日志 DTO
     * @return 数据集内容 JSON
     */
    private JSONObject importBuildDataJsonObject(String id, MongoVarProcessLog mongoVarProcessLog) {
        // 创建空 JSON 对象, 保存处理最终结果
        JSONObject inputJsonObject = new JSONObject();
        // 将请求报文作为"输入"部分添加到结果 JSON
        inputJsonObject.put(PositionVarEnum.RAW_DATA.getName(), JSON.parseObject(mongoVarProcessLog.getRequestJson()));
        // 清除导入生产数据的空值
        TestExecuteUtil.removeJsonEmptyValue(inputJsonObject);
        // 重新组织结果 JSON 对象结构
        JSONObject inputData = TestTableDataUtil.transValuePathMap(id, inputJsonObject);
        JSONObject newInputData = new JSONObject();
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            String key = entry.getKey();
            if (key.equals(TestTableEnum.RESULTS.getCode())) {
                newInputData.put(TestTableEnum.MASTER.getCode(), entry.getValue());
            } else {
                newInputData.put(key, entry.getValue());
            }
        }
        JSONObject expectJsonObject = new JSONObject();
        // 将响应报文作为"预期输出"部分, 添加到结果 JSON (如有)
        if (!StringUtils.isEmpty(mongoVarProcessLog.getResponseJson())) {

            expectJsonObject.put(PositionVarEnum.OUTPUT.getName(), JSON.parseObject(mongoVarProcessLog.getResponseJson()));

        }
        // 清除导入生产数据的空值
        TestExecuteUtil.removeJsonEmptyValue(expectJsonObject);
        // 重新组织结果 JSON 对象结构
        JSONObject expectData = TestTableDataUtil.transValuePathMap(id, expectJsonObject);
        JSONObject newExpectData = new JSONObject();
        for (Map.Entry<String, Object> entry : expectData.entrySet()) {
            String key = entry.getKey();
            if (key.equals(TestTableEnum.RESULTS.getCode())) {
                newExpectData.put(TestTableEnum.MASTER.getCode(), entry.getValue());
            } else {
                newExpectData.put(key, entry.getValue());
            }
        }
        JSONObject newData = new JSONObject();
        newData.put(TestTableEnum.INPUT.getCode(), newInputData);
        if (newExpectData.size() > 0) {
            newData.put(TestTableEnum.EXPECT.getCode(), newExpectData);
        }
        return newData;
    }
}
