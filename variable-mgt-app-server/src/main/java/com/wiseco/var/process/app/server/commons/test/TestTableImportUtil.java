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
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.dto.TestEventExcelDataDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelContentDto;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.enums.test.TestExcelFileEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 策略测试、组件测试Excel导出
 *
 * @author wangxianli
 * @since 2022/7/5
 */
@Slf4j
public class TestTableImportUtil {

    /**
     * 前端表单保留字 Set
     * <p>前端使用下述定义的保留字以优化前端性能</p>
     * <p>需识别并避免这些关键词作为变量路径, 查找数据模型定义</p>
     */
    private static final Set<String> FRONT_END_FORM_RESERVED_WORDS_SET = new HashSet<>(Arrays.asList("rowKey", "_X_ROW_KEY", "isEdit"));

    private static final String FUNCTION_RETURN_VARIABLE_PATH = "functionReturn";

    private static final Integer DEFAULT_SHEET_NAME_ID_SET_MAP_SIZE = 10;


    /**
     * 异常信息模板
     */
    private static final String EXCEPTION_MESSAGE_DUPLICATE_ID_IN_SHEET = "工作页“{0}”存在重复 id 值 {1}，请检查上传文件内容";

    private static final String EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH = "变量 {0} 填写值 {1} 类型错误，应为 {2} 类型";

    private static final String EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH_WITH_PROMPT = "变量 {0} 填写值 {1} 类型错误，应为 {2} 类型 {3}";

    private static final String EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_UNDEFINED = "变量 {0} 类型 {1} 暂不支持";

    //======================导入Excel=================================//


    /**
     * 使用事件模式导入Excel数据
     *
     * @param inputStream 输入流
     * @return com.wiseco.var.process.app.server.commons.test.dto.TestEventExcelDataDto
     */
    public static TestEventExcelDataDto eventReadExcelData(InputStream inputStream) {
        com.wiseco.var.process.app.server.commons.test.TestExcelEventReaderUtil readerUtil = new com.wiseco.var.process.app.server.commons.test.TestExcelEventReaderUtil();
        try {
            //存放表格表头
            Map<String, Map<Integer, String>> headerMap = new HashMap<>(MagicNumbers.SIXTEEN);
            //主表数据
            List<Map<String, String>> masterMapData = new ArrayList<>();
            //附表数据
            List<TestExcelContentDto> scheduleListData = new ArrayList<>();
            //读取数据
            readerUtil.readExcel(inputStream, new TestExcelEventReaderUtil.ProcessRowsInterface() {
                @Override
                public void setRows(String sheetName, int sheetIndex, int curRow, Map<Integer, String> titles, Map<String, Object> cellMap) {
                    if (TestTableEnum.MASTER.getMessage().equals(sheetName)) {
                        sheetName = TestTableEnum.MASTER.getCode();

                    } else if (TestTableEnum.EXPECT.getMessage().equals(sheetName)) {
                        sheetName = TestTableEnum.EXPECT.getCode();
                    } else {
                        //表名
                        String[] sheetNameSplit = sheetName.split("-");

                        sheetName = sheetNameSplit[0];
                    }
                    //将值object转string
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    Set<Map.Entry<String, Object>> entries = cellMap.entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        rowMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    if (TestTableEnum.MASTER.getCode().equals(sheetName)) {
                        masterMapData.add(rowMap);
                    } else {
                        TestExcelContentDto contentDto = new TestExcelContentDto();
                        contentDto.setSheetName(sheetName);
                        contentDto.setRowMapData(rowMap);
                        scheduleListData.add(contentDto);
                    }
                    headerMap.put(sheetName, titles);
                }
            });
            //存放附表表格数据
            Map<String, Map<String, List<Map<String, String>>>> targetSheduleMapData = new HashMap<>(MagicNumbers.SIXTEEN);
            if (!CollectionUtils.isEmpty(scheduleListData)) {
                Map<String, List<TestExcelContentDto>> scheduleMapData = scheduleListData.stream().collect(Collectors.groupingBy(TestExcelContentDto::getSheetName));
                Set<Map.Entry<String, List<TestExcelContentDto>>> entries = scheduleMapData.entrySet();
                for (Map.Entry<String, List<TestExcelContentDto>> entry : entries) {
                    Map<String, List<Map<String, String>>> scheduleMap = builderScheduleData(entry.getKey(), entry.getValue());
                    targetSheduleMapData.put(entry.getKey(), scheduleMap);
                }
            }
            //组合要保存的数据
            List<String> list = builderEventExcelData(masterMapData, targetSheduleMapData);
            TestEventExcelDataDto outDto = new TestEventExcelDataDto();
            outDto.setHeaderMap(headerMap);
            outDto.setDataList(list);
            return outDto;
        } catch (Exception e) {
            log.error("文件导入异常: {}", e.getMessage());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR,"导入异常");
        }
    }

    private static List<String> builderEventExcelData(List<Map<String, String>> masterMapData,
                                                      Map<String, Map<String, List<Map<String, String>>>> targetSheduleMapData) {

        List<String> dataList = new ArrayList<>();

        for (Map<String, String> rowData : masterMapData) {

            Map<String, Object> rowMapTmp = new HashMap<>(MagicNumbers.SIXTEEN);

            if (targetSheduleMapData.size() > 0) {

                String id = String.valueOf(rowData.get("id"));

                //处理输入
                Set<Map.Entry<String, String>> entries = rowData.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    if (targetSheduleMapData.containsKey(key) && targetSheduleMapData.get(key).containsKey(id)) {
                        List<Map<String, String>> subList = targetSheduleMapData.get(key).get(id);
                        rowMapTmp.put(key, subList);
                        recursiveMatchSubdata(subList, targetSheduleMapData, rowMapTmp);

                        rowData.put(key, "...");
                    }
                }

                //处理预期结果
                if (targetSheduleMapData.containsKey(TestTableEnum.EXPECT.getCode())
                        && targetSheduleMapData.get(TestTableEnum.EXPECT.getCode()).containsKey(id)) {
                    Map<String, String> expectMap = targetSheduleMapData.get(TestTableEnum.EXPECT.getCode()).get(id).get(0);

                    Set<Map.Entry<String, String>> expectMapEntries = expectMap.entrySet();
                    for (Map.Entry<String, String> entry : expectMapEntries) {
                        String key = entry.getKey();
                        if (targetSheduleMapData.containsKey(key) && targetSheduleMapData.get(key).containsKey(id)) {
                            List<Map<String, String>> subList = targetSheduleMapData.get(key).get(id);
                            rowMapTmp.put(key, subList);

                            recursiveMatchSubdata(subList, targetSheduleMapData, rowMapTmp);

                            expectMap.put(key, "...");
                        }
                    }

                    rowData.putAll(expectMap);

                }
            }

            //处理master主表
            rowMapTmp.put(TestTableEnum.MASTER.getCode(), rowData);

            dataList.add(JSONObject.toJSONString(rowMapTmp));
        }

        return dataList;

    }

    private static Map<String, List<Map<String, String>>> builderScheduleData(String sheetName, List<TestExcelContentDto> scheduleListData) {

        //一张表数据
        Map<String, List<Map<String, String>>> linkData = new LinkedHashMap<>();

        for (TestExcelContentDto contentDto : scheduleListData) {
            Map<String, String> rowData = contentDto.getRowMapData();
            //分组
            List<Map<String, String>> linkDataSub = new ArrayList<>();

            Map<String, String> rowDataNew = new HashMap<>(MagicNumbers.TEN);
            Set<Map.Entry<String, String>> entries = rowData.entrySet();

            //预期结果主表
            if (TestTableEnum.EXPECT.getCode().equals(sheetName)) {

                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    if (!"id".equals(key)) {
                        rowDataNew.put(TestTableEnum.EXPECT.getCode() + "." + key, value);
                    } else {
                        rowDataNew.put(key, value);
                    }
                }

                String id = String.valueOf(rowDataNew.get("id"));
                if (linkData.containsKey(id)) {
                    linkDataSub = linkData.get(id);
                }
                linkDataSub.add(rowDataNew);
                linkData.put(String.valueOf(rowDataNew.get("id")), linkDataSub);
            } else if (sheetName.startsWith(TestTableEnum.EXPECT.getCode())) {
                //预期结果数组
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    if (!"id".equals(key) && !"parentId".equals(key)) {
                        rowDataNew.put(TestTableEnum.EXPECT.getCode() + "." + key, value);
                    } else {
                        rowDataNew.put(key, value);
                    }
                }

                String parentId = String.valueOf(rowDataNew.get("parentId"));
                if (linkData.containsKey(parentId)) {
                    linkDataSub = linkData.get(parentId);
                }

                linkDataSub.add(rowDataNew);
                linkData.put(parentId, linkDataSub);
            } else {
                //输入数据
                String parentId = String.valueOf(rowData.get("parentId"));
                if (linkData.containsKey(parentId)) {
                    linkDataSub = linkData.get(parentId);
                }
                linkDataSub.add(rowData);
                linkData.put(parentId, linkDataSub);
            }

        }
        return linkData;
    }

    /**
     * 递归匹配组装数据
     *
     * @param sourceMapList
     * @param subMapData
     * @param targetRowMapTmp
     */
    private static void recursiveMatchSubdata(List<Map<String, String>> sourceMapList,
                                              Map<String, Map<String, List<Map<String, String>>>> subMapData, Map<String, Object> targetRowMapTmp) {

        for (Map<String, String> map : sourceMapList) {

            String id = map.get("id");
            //处理输入
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                if (subMapData.containsKey(key) && subMapData.get(key).containsKey(id)) {

                    List<Map<String, String>> sourceList = new ArrayList<>();
                    if (targetRowMapTmp.containsKey(key)) {
                        sourceList = (List<Map<String, String>>) targetRowMapTmp.get(key);
                    }
                    List<Map<String, String>> list = subMapData.get(key).get(id);
                    sourceList.addAll(list);
                    targetRowMapTmp.put(key, sourceList);

                    recursiveMatchSubdata(list, subMapData, targetRowMapTmp);
                    map.put(key, "...");

                }
            }

        }
    }

    /**
     * 遍历并处理导入 Excel 数据
     *
     * @param dataList                         导入 Excel 数据
     * @param varPathDomainDataModelTreeDtoMap 数据模型定义 Map, key: 变量路径, value: 数据模型定义
     * @param contentShouldBeReplaced          是否替换内容 flag
     * @param actions                          对值的操作
     * @return 导入 Excel 数据替换内容或原始内容
     */
    public static List<String> traverseAndProcessImportedExcelValue(List<String> dataList,
                                                                    Map<String, DomainDataModelTreeDto> varPathDomainDataModelTreeDtoMap,
                                                                    boolean contentShouldBeReplaced,
                                                                    BiFunction<String, DomainDataModelTreeDto, String>... actions) {
        List<String> replacedDataList = new ArrayList<>(dataList.size());
        for (String dataListItem : dataList) {
            // 反序列化一行数据至 JSON
            JSONObject sheet = JSON.parseObject(dataListItem);
            // 遍历 Excel 页
            for (Map.Entry<String, Object> sheetEntry : sheet.entrySet()) {
                traverseAndProcessValueHelper(sheetEntry, varPathDomainDataModelTreeDtoMap, contentShouldBeReplaced, actions);
            }

            if (contentShouldBeReplaced) {
                // 为节约内存, 按需填充数据
                replacedDataList.add(sheet.toJSONString());
            }
        }

        if (contentShouldBeReplaced) {
            return replacedDataList;
        } else {
            return dataList;
        }
    }


    /**
     * 遍历并处理数据辅助方法
     * <p>适用于导入 Excel 数据工作簿页 / 在线填写表单子表格</p>
     *
     * @param content                          内容 (Excel 工作簿页 / 在线填写表单子表格)
     * @param varPathDomainDataModelTreeDtoMap 数据模型定义 Map, key: 变量路径, value: 数据模型定义
     * @param contentShouldBeReplaced          是否替换内容 flag
     * @param actions                          对值的操作
     */
    public static void traverseAndProcessValueHelper(Map.Entry<String, Object> content,
                                                     Map<String, DomainDataModelTreeDto> varPathDomainDataModelTreeDtoMap,
                                                     boolean contentShouldBeReplaced,
                                                     BiFunction<String, DomainDataModelTreeDto, String>... actions) {
        List<Map<String, Object>> variablePathValueObjectList;
        if (TestTableEnum.MASTER.getCode().equals(content.getKey())) {
            // 测试数据主表
            variablePathValueObjectList = Collections.singletonList((Map<String, Object>) content.getValue());
        } else {
            // 测试数据附表 (对象类型数组)
            variablePathValueObjectList = ((List<Object>) content.getValue()).stream()
                    .map(o -> (Map<String, Object>) o)
                    .collect(Collectors.toList());
        }
        variablePathValueObjectList.forEach(variablePathValueJsonObject -> {
            // 遍历指定 Excel 页全部变量和值
            for (Map.Entry<String, Object> variablePathValueObjectEntry : variablePathValueJsonObject.entrySet()) {
                String variablePath = variablePathValueObjectEntry.getKey();
                // 特殊处理空值, 避免将 null 转换为字符串 "null"
                String variableValue = null != variablePathValueObjectEntry.getValue() ? String.valueOf(variablePathValueObjectEntry.getValue()) : null;
                if (TestExcelFileEnum.ID.getCode().equals(variablePath)
                        || TestExcelFileEnum.PARENT_ID.getCode().equals(variablePath)
                        || FRONT_END_FORM_RESERVED_WORDS_SET.contains(variablePath)
                        || FUNCTION_RETURN_VARIABLE_PATH.equals(variablePath)) {
                    // 不处理编号定位字段 id / parentId / 前端保留字及 functionReturn
                    continue;
                }

                // 去除预期结果 key 的 "expect" 前缀 (如有)
                if (variablePath.startsWith(TestTableEnum.EXPECT.getCode() + StringPool.DOT)) {
                    variablePath = variablePath.substring(variablePath.indexOf(StringPool.DOT) + 1);
                }

                DomainDataModelTreeDto domainDataModelTreeDto = varPathDomainDataModelTreeDtoMap.get(variablePath);
                if (Objects.isNull(domainDataModelTreeDto)) {
                    log.warn("Data model definition not found for variable \"{}\".", variablePath);
                    log.warn("Actions will not be taken on certain variable.");
                    continue;
                }

                String processedVariableValue = variableValue;
                if ("1".equals(domainDataModelTreeDto.getIsArr())) {
                    // 数组
                    String[] variableValueSegments = variableValue.split(StringPool.COMMA, MagicNumbers.MINUS_INT_1);
                    List<String> processedVariableValueSegments = new ArrayList<>(variableValueSegments.length);
                    for (String variableValueSegment : variableValueSegments) {
                        for (BiFunction<String, DomainDataModelTreeDto, String> action : actions) {
                            variableValueSegment = action.apply(variableValueSegment, domainDataModelTreeDto);
                        }

                        processedVariableValueSegments.add(variableValueSegment);
                    }

                    processedVariableValue = org.apache.commons.lang3.StringUtils.join(processedVariableValueSegments, StringPool.COMMA);
                } else {
                    // 常规数值
                    for (BiFunction<String, DomainDataModelTreeDto, String> action : actions) {
                        processedVariableValue = action.apply(variableValue, domainDataModelTreeDto);
                    }
                }

                if (contentShouldBeReplaced) {
                    variablePathValueObjectEntry.setValue(processedVariableValue);
                }
            }
        });
    }

    /**
     * 检查基本类型数据 BiFunction
     */
    public static class ValuePrimitiveDataTypeValidationBiFunction implements BiFunction<String, DomainDataModelTreeDto, String> {

        @Override
        public String apply(String value, DomainDataModelTreeDto domainDataModelTreeDto) {
            if (StringUtils.isEmpty(value)) {
                // Excel 支持导入空值
                return value;
            }

            DataVariableTypeEnum valueDataVariableType = DataVariableTypeEnum.getEnumByIdenticalMessage(domainDataModelTreeDto.getType());

            switch (valueDataVariableType) {
                case INT_TYPE:
                    try {
                        Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH,
                                domainDataModelTreeDto.getValue(), value, DataVariableTypeEnum.INT_TYPE.getMessage()));
                    }

                    break;
                case DOUBLE_TYPE:
                    try {
                        Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH,
                                domainDataModelTreeDto.getValue(), value, DataVariableTypeEnum.DOUBLE_TYPE.getMessage()));
                    }

                    break;
                case DATE_TYPE:
                    try {
                        DateUtil.parseStrToDate(value, DateUtil.FORMAT_SHORT);
                    } catch (Exception e) {
                        throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH_WITH_PROMPT,
                                domainDataModelTreeDto.getValue(), value, DataVariableTypeEnum.DATE_TYPE.getMessage(), StringPool.LEFT_BRACKET + DateUtil.FORMAT_SHORT + StringPool.RIGHT_BRACKET));
                    }

                    break;
                case DATETIME_TYPE:
                    try {
                        DateUtil.parseStrToDate(value, DateUtil.FORMAT_LONG);
                    } catch (Exception e) {
                        throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH_WITH_PROMPT,
                                domainDataModelTreeDto.getValue(), value, DataVariableTypeEnum.DATETIME_TYPE.getMessage(), StringPool.LEFT_BRACKET + DateUtil.FORMAT_LONG + StringPool.RIGHT_BRACKET));
                    }

                    break;
                case BOOLEAN_TYPE:
                    // 转换输入值为小写后比较内容
                    value = value.toLowerCase();
                    if (!Arrays.asList(StringPool.TRUE, StringPool.FALSE).contains(value)) {
                        throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_MISMATCH,
                                domainDataModelTreeDto.getValue(), value, DataVariableTypeEnum.BOOLEAN_TYPE.getMessage()));
                    }

                    break;
                default:
                    // 其他基本变量类型: 不检查
                    break;
            }

            return value;
        }
    }

    /**
     * 规范处理基本类型数据 BiFunction
     */
    public static class ValuePrimitiveDataTypeRegulationBiFunction implements BiFunction<String, DomainDataModelTreeDto, String> {

        @Override
        public String apply(String value, DomainDataModelTreeDto domainDataModelTreeDto) {
            if (StringUtils.isEmpty(value)) {
                // Excel 支持导入空值
                return value;
            }

            DataVariableTypeEnum valueDataVariableType = DataVariableTypeEnum.getEnumByIdenticalMessage(domainDataModelTreeDto.getType());
            if (Objects.isNull(valueDataVariableType)) {
                // 变量类型未定义
                throw new ServiceException(MessageFormat.format(EXCEPTION_MESSAGE_TEMPLATE_VARIABLE_TYPE_UNDEFINED,
                        domainDataModelTreeDto.getValue(), value));
            }

            switch (valueDataVariableType) {
                case BOOLEAN_TYPE:
                    // 转换输入值为小写
                    return value.toLowerCase();
                default:
                    // 其他基本变量类型: 不处理
                    return value;
            }
        }
    }
}
