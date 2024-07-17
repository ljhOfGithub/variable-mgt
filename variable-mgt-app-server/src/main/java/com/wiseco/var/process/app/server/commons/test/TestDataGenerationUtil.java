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
package com.wiseco.var.process.app.server.commons.test;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.autogentypes.AbstractRandomDateAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.AbstractRandomDateTimeAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.AbstractRandomDoubleAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.AbstractRandomIntegerAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.CustomTypeAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.EnumTypeAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.autogentypes.LogicTypeAutoGenerator;
import com.wiseco.var.process.app.server.commons.test.dto.TestGenerateRulesDto;
import com.wiseco.var.process.app.server.enums.GenerateTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线数据自动生成服务
 *
 * @author wangxianli
 * @author Zhaoxiong Chen
 * @since 2022/1/10
 */
@Slf4j
public class TestDataGenerationUtil {

    /**
     * 随机数据生成失败 友好提示信息模板
     */
    private static final String FAILED_TO_GENERATE_RANDOM_DATA_MESSAGE = "随机数据生成失败。变量名称：{0}，变量路径：{1}，生成数据类型：{2}，随机数生成方式：{3}";

    /**
     * 生成数据
     *
     * @param generateAmount 生成数据数量
     * @param inputDtoList   数据生成规则输入参数列表
     * @param dataModelMap   数据模型 Map
     * @param initDataId     生成数据起始序列号
     * @return Pair, key: 表头 JSONObject, value: 生成数据 List
     */
    public Pair<JSONObject, List<String>> generateData(int generateAmount, List<TestGenerateRulesDto> inputDtoList,
                                                       Map<String, DomainDataModelTreeDto> dataModelMap, int initDataId) {
        //获取表格列结构
        List<TestFormDto> formDataList = new ArrayList<>();
        Map<String, TestGenerateRulesDto> genMap = new HashMap<>(MagicNumbers.SIXTEEN);
        for (TestGenerateRulesDto generateInputDto : inputDtoList) {
            TestFormDto testFormDto = new TestFormDto();
            BeanUtils.copyProperties(generateInputDto, testFormDto);
            formDataList.add(testFormDto);
            genMap.put(generateInputDto.getName(), generateInputDto);
        }
        JSONObject testData = TestTableHeaderUtil.getTestData(formDataList, dataModelMap, initDataId);
        if (testData == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "自动生成获取表头失败。");
        }
        //表头
        JSONObject testDataHeader = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());

        //主表
        JSONArray masterJsonArray = testDataHeader.getJSONArray(TestTableEnum.MASTER.getCode());
        List<TestGenerateRulesDto> masterDtoList = new ArrayList<>();
        List<String> masterSubDtoList = new ArrayList<>();
        for (int i = 0; i < masterJsonArray.size(); i++) {
            JSONObject itemObj = masterJsonArray.getJSONObject(i);
            if (genMap.containsKey(itemObj.getString("index"))) {
                masterDtoList.add(genMap.get(itemObj.getString("index")));
            } else {
                masterSubDtoList.add(itemObj.getString("index"));
            }
        }
        // 自动生成数据 List
        List<String> generatedDataList = new ArrayList<>();

        // 处理master：获取自动生成的测试数据 Map, key: 变量路径, value: 生成数据 List, size == generateAmount
        Map<String, List<String>> generatedData = this.generateDataByEnumAndRandom(generateAmount, masterDtoList);

        // 1. 组装所有变量生成的数据, 形成表格
        for (int i = 0; i < generateAmount; i++) {
            JSONObject dataJsonObject = new JSONObject();

            JSONObject valueJsonObject = new JSONObject();
            valueJsonObject.put("id", String.valueOf(initDataId));
            valueJsonObject.put("parentId", "0");
            if (!CollectionUtils.isEmpty(masterDtoList)) {
                for (TestGenerateRulesDto generateInputDto : masterDtoList) {
                    assembleJson(generatedData, i, valueJsonObject, generateInputDto);

                }
            }

            if (!CollectionUtils.isEmpty(masterSubDtoList)) {
                for (String str : masterSubDtoList) {
                    valueJsonObject.put(str, "...");
                }
            }
            dataJsonObject.put(TestTableEnum.MASTER.getCode(), valueJsonObject);

            //子表生成数据
            if (!CollectionUtils.isEmpty(masterSubDtoList)) {
                for (String subKey : masterSubDtoList) {

                    generateSubTableData(subKey, testDataHeader.getJSONArray(subKey), testDataHeader, genMap, dataJsonObject,
                            String.valueOf(initDataId));
                }

            }

            String testDataValue = JSON.toJSONString(dataJsonObject);
            generatedDataList.add(testDataValue);

            initDataId++;
        }

        return new Pair<>(testDataHeader, generatedDataList);
    }

    private static void assembleJson(Map<String, List<String>> generatedData, int i, JSONObject valueJsonObject, TestGenerateRulesDto generateInputDto) {
        if (StringUtils.isEmpty(generateInputDto.getGenerateRuleFormula())) {
            // 未配置数据生成规则表达式: 设置数据为空
            valueJsonObject.put(generateInputDto.getName(), "");
        } else if (generateInputDto.getGenerateMode().equals(GenerateTypeEnum.LOGIC.getCode())) {
            // TODO: 逻辑依赖
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "暂时不支持逻辑依赖的测试数据自动生成。");
        } else {
            List<String> list = generatedData.get(generateInputDto.getName());
            valueJsonObject.put(generateInputDto.getName(), list.get(i));

        }
    }

    /**
     * 生成子表数据
     *
     * @param subKey
     * @param dataJsonArray
     * @param testDataHeader
     * @param genMap
     * @param dataJsonObject
     * @param parentId
     */
    private void generateSubTableData(String subKey, JSONArray dataJsonArray, JSONObject testDataHeader, Map<String, TestGenerateRulesDto> genMap,
                                      JSONObject dataJsonObject, String parentId) {
        //数据生成记录数
        int genCount = CommonConstant.TEST_DATA_ARRAY_GENERATION_SIZE;
        List<TestGenerateRulesDto> dataDtoList = new ArrayList<>();
        List<String> dataSubDtoList = new ArrayList<>();
        for (int i = 0; i < dataJsonArray.size(); i++) {
            JSONObject itemObj = dataJsonArray.getJSONObject(i);
            if (genMap.containsKey(itemObj.getString("index"))) {
                dataDtoList.add(genMap.get(itemObj.getString("index")));
            } else {
                dataSubDtoList.add(itemObj.getString("index"));
            }
        }

        // 处理master：获取自动生成的测试数据 Map, key: 变量路径, value: 生成数据 List, size == generateAmount
        Map<String, List<String>> generatedData = this.generateDataByEnumAndRandom(genCount, dataDtoList);

        // 1. 组装所有变量生成的数据, 形成表格
        int initDataId = 1;
        for (int i = 0; i < genCount; i++) {

            JSONObject valueJsonObject = new JSONObject();
            valueJsonObject.put("id", parentId + "_" + initDataId);
            valueJsonObject.put("parentId", parentId);
            if (!CollectionUtils.isEmpty(dataDtoList)) {
                for (TestGenerateRulesDto generateInputDto : dataDtoList) {
                    assembleJson(generatedData, i, valueJsonObject, generateInputDto);

                }
            }

            if (!CollectionUtils.isEmpty(dataSubDtoList)) {
                for (String str : dataSubDtoList) {
                    valueJsonObject.put(str, "...");
                }
            }
            JSONArray dataValueArray = new JSONArray();
            if (dataJsonObject.containsKey(subKey)) {
                dataValueArray = dataJsonObject.getJSONArray(subKey);
            }
            dataValueArray.add(valueJsonObject);
            dataJsonObject.put(subKey, dataValueArray);

            //子表生成数据
            if (!CollectionUtils.isEmpty(dataSubDtoList)) {
                for (String str : dataSubDtoList) {

                    generateSubTableData(str, testDataHeader.getJSONArray(str), testDataHeader, genMap, dataJsonObject,
                            valueJsonObject.getString("id"));
                }

            }

            initDataId++;
        }

    }

    /**
     * 分析输入规则DTO, 生成并格式化自动生成的数据
     * <p>
     * 生成规则表达式举例:
     * <ul>
     * <li>enum    枚举     yzf|0.1;xcjqh|0.1;jt360|0.8  空值用nul</li>
     * <li>random  随机     350,500|0.1;501,650|0.1;651,850|0.1;</li>
     * <li>logic   逻辑依赖  @input.applyInfo.cusName-30+@input.applyInfo.age</li>
     * <li>custom  自定义    generateCode</li>
     * </ul>
     *
     * @param generateNums 需要的自动生成数据行数
     * @param dataList     待生成的数据项 DTO 列表
     * @return 映射, key: 数据项全称, value: 按数量生成的数据项行
     * @see com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator
     */
    private Map<String, List<String>> generateDataByEnumAndRandom(int generateNums, List<TestGenerateRulesDto> dataList) {
        // 结果存储 Map
        Map<String, List<String>> tempDataMap = new HashMap<>(MagicNumbers.SIXTEEN);
        if (CollectionUtils.isEmpty(dataList)) {
            return tempDataMap;
        }

        for (TestGenerateRulesDto generateInputDto : dataList) {
            // 未设置生成规则: 跳过 DTO 分析
            if (StringUtils.isEmpty(generateInputDto.getGenerateRuleFormula())) {
                continue;
            }

            // 数据列: 避免同步错误 java.lang.ArrayIndexOutOfBoundsException
            List<String> column = Collections.synchronizedList(new ArrayList<>());

            // 根据生成规则建立随机数值生成器
            TestDataAutoGenerator generator = this.createAutoGenerator(generateInputDto);

            // 生成指定行数的随机数据
            this.generateAndStoreData(column, generator, generateNums);

            // 添加数据列到 Map
            tempDataMap.put(generateInputDto.getName(), column);
        }

        return tempDataMap;
    }

    /**
     * 生成指定数量的随机数据, 添加数据到列表
     *
     * @param list      数据存储位置
     * @param generator 随机数据生成器
     * @param size      数据生成数量
     */
    private void generateAndStoreData(List<String> list, com.wiseco.var.process.app.server.commons.test.TestDataAutoGenerator generator, int size) {
        for (int i = 0; i < size; i++) {
            list.add(generator.getValue());
        }
    }

    /**
     * 根据规则创建随机数自动生成器
     *
     * @param inputDto 测试自动生成数据 DTO
     * @return 指定的随机数生成器
     */
    private TestDataAutoGenerator createAutoGenerator(TestGenerateRulesDto inputDto) {
        // 自动生成模式
        String generateMode = inputDto.getGenerateMode();
        // 数据类型
        String generateType = inputDto.getType();
        // 规则表达式
        String generateRuleExpression = inputDto.getGenerateRuleFormula();

        TestDataAutoGenerator result;
        if (GenerateTypeEnum.ENUM.getCode().equals(generateMode)) {
            // 枚举
            result = new EnumTypeAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.RANDOM.getCode().equals(generateMode) && DataVariableTypeEnum.INT_TYPE.getMessage().equals(generateType)) {
            // 随机 int
            result = new AbstractRandomIntegerAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.RANDOM.getCode().equals(generateMode) && DataVariableTypeEnum.DOUBLE_TYPE.getMessage().equals(generateType)) {
            // 随机 double
            result = new AbstractRandomDoubleAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.RANDOM.getCode().equals(generateMode) && DataVariableTypeEnum.DATE_TYPE.getMessage().equals(generateType)) {
            // 随机 Date
            result = new AbstractRandomDateAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.RANDOM.getCode().equals(generateMode) && DataVariableTypeEnum.DATETIME_TYPE.getMessage().equals(generateType)) {
            // 随机 DateTime
            result = new AbstractRandomDateTimeAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.LOGIC.getCode().equals(generateMode)) {
            // 逻辑依赖
            result = new LogicTypeAutoGenerator(generateRuleExpression);
        } else if (GenerateTypeEnum.CUSTOM.getCode().equals(generateMode)) {
            // 自定义
            result = new CustomTypeAutoGenerator(generateRuleExpression);
        } else {
            String promptMessage = MessageFormat.format(FAILED_TO_GENERATE_RANDOM_DATA_MESSAGE, inputDto.getLabel(), inputDto.getName(),
                    generateType, generateMode);
            log.warn(promptMessage);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, promptMessage);
        }

        return result;
    }
}
