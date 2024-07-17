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
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestDataTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestExcelFileEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 测试导出数据工具类
 * 用于组件测试、策略测试结果的 Excel 导出
 *
 * @author wangxianli
 * @author Zhaoxiong Chen (modified)
 * @since 2021/11/10
 */
@Slf4j
public class TestTableExportUtil {
    /**
     * 定义主工作表 (master) 不同类别数据的显示顺序
     *
     * @see TestTableEnum
     */
    private static final TestTableEnum[] DATA_CATEGORY_SEQUENCE = {TestTableEnum.EXPECT, TestTableEnum.INPUT, TestTableEnum.RESULTS};

    /**
     * 导出测试明细
     *
     * @param header   表头 <p>key: 工作表 worksheet 名称 (master, input.*, ...), value: 表头详情</p>
     * @param dataList 结果数据
     * @param expectHeader  预期结果表头
     * @return 经过拼接的 Excel 工作表数据
     */
    public static List<TestExcelDto> exportTestDetail(Map<String, List<Map<String, Object>>> header, String expectHeader, List<JSONObject> dataList) {

        // 0. 结果储存准备
        // 变量中英文名对照: key - index (变量全名), value - label (中文名)
        Map<String, String> variableIndexLabelMap = new HashMap<>(MagicNumbers.TEN);
        // 工作簿表头: key - 工作表名称 (按顺序排列), value - 工作表表头信息
        Map<String, List<Map<String, Object>>> tableTreeSortMap = new LinkedHashMap<>();

        // 1. 处理表头
        // 获取主表表头
        List<Map<String, Object>> masterHeaderList = header.get(TestTableEnum.MASTER.getCode());

        // 正在处理 测试集明细数据
        // 添加主表到工作簿
        tableTreeSortMap.put(TestTableEnum.MASTER.getCode(), masterHeaderList);

        // 表头排序, 填写变量中英文对照表
        getHeaderSortList(header, masterHeaderList, tableTreeSortMap, variableIndexLabelMap);

        // 预期结果表头
        if (StringUtils.isNotEmpty(expectHeader)) {
            Map<String, List<Map<String, Object>>> expectHeaderMap = JSON.parseObject(expectHeader,
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {
                    });
            if (expectHeaderMap.size() > 0) {
                List<Map<String, Object>> expectHeaderList = expectHeaderMap.get(TestTableEnum.EXPECT.getCode());
                tableTreeSortMap.put(TestTableEnum.EXPECT.getCode(), expectHeaderList);

                // 表头排序
                getHeaderSortList(expectHeaderMap, expectHeaderList, tableTreeSortMap, variableIndexLabelMap);
            }
        }

        // 2. 处理数据
        return processExcelTestSheetData(tableTreeSortMap, variableIndexLabelMap, dataList, TestDataTypeEnum.TEST_DETAIL.getCode());

    }

    /**
     * 导出测试结果数据
     *
     * @param header   表头 <p>key: 工作表 worksheet 名称 (master, input.*, ...), value: 表头详情</p>
     * @param dataList  数据列表
     * @return 经过拼接的 Excel 工作表数据
     */
    public static List<TestExcelDto> exportTestResultsData(Map<String, List<Map<String, Object>>> header, List<JSONObject> dataList) {
        if (header == null || header.size() == 0) {
            return new ArrayList<>();
        }
        // 0. 结果储存准备
        // 变量中英文名对照: key - index (变量全名), value - label (中文名)
        Map<String, String> variableIndexLabelMap = new HashMap<>(MagicNumbers.TEN);
        // 工作簿表头: key - 工作表名称 (按顺序排列), value - 工作表表头信息
        Map<String, List<Map<String, Object>>> worksheetSortedHeaderMap = new LinkedHashMap<>();

        // 1. 处理表头
        // 获取主表表头
        List<Map<String, Object>> masterHeaderList = header.get(TestTableEnum.MASTER.getCode());

        // 展开表头对象, 填写变量中英文对照表
        fillVariableIndexLabelMap(header, masterHeaderList, variableIndexLabelMap);

        // 添加排序后的主表表头到工作簿, key: 工作簿页关系, value: 经过排序的表头 List
        worksheetSortedHeaderMap.put(TestTableEnum.MASTER.getCode(), sortMasterHeader(masterHeaderList));

        // 添加剩余的工作表
        for (Map.Entry<String, List<Map<String, Object>>> sheetHeader : header.entrySet()) {
            if (!sheetHeader.getKey().equals(TestTableEnum.MASTER.getCode())) {
                worksheetSortedHeaderMap.put(sheetHeader.getKey(), sheetHeader.getValue());
            }
        }

        // 2. 处理数据
        return processExcelTestSheetData(worksheetSortedHeaderMap, variableIndexLabelMap, dataList, TestDataTypeEnum.EXECUTE_RESULT.getCode());
    }

    /**
     * 工作簿页数据处理
     *
     * @param worksheetSortedHeaderMap 工作簿页 - 经过排序的表头 Map
     * @param variableIndexLabelMap    变量中英文名对照 Map, 包含拼接的预期结果表头
     * @param dataList                 测试结果列表 (不包含前置的, 与预期结果并列的实际结果)
     * @param fileType                 文件类型编码 {@link TestDataTypeEnum}
     * @return Excel 数据 DTO List
     */
    private static List<TestExcelDto> processExcelTestSheetData(Map<String, List<Map<String, Object>>> worksheetSortedHeaderMap,
                                                                Map<String, String> variableIndexLabelMap, List<JSONObject> dataList, String fileType) {
        Set<Map.Entry<String, List<Map<String, Object>>>> tableTreeSortEntries = worksheetSortedHeaderMap.entrySet();

        // 初始化工作簿页: key - 工作表名, value - 二维表格结构
        Map<String, List<List<String>>> sheetTableMap = initiateWorksheet(tableTreeSortEntries);

        // 获取表头及其对应字段数据类型, key: headerList / typeList, value: 工作簿页 - 表头或类型信息 List Map
        // 工作簿页 - 表头或类型信息 List Map, key: 工作簿页层级 (e.g. master), value: 表头或类型信息 List
        Map<String, Map<String, List<String>>> headerKeyAndTypeList = getHeaderKeyAndTypeList(tableTreeSortEntries, fileType);
        // 表头
        Map<String, List<String>> headerListMap = headerKeyAndTypeList.get("headerList");
        // 字段数据类型
        Map<String, List<String>> typeListMap = headerKeyAndTypeList.get("typeList");

        // 获取数据，填充sheetKeyList
        for (JSONObject data : dataList) {
            Map<String, List<Map<String, Object>>> dataMap = transJsonObjectToMapList(data);

            //处理输入数据sheet，包括预期结果和实际结果
            dataMap = appendExpectedActualResultComparison(dataMap, headerListMap, fileType);

            //处理数据，将数据按照表头header组合
            Set<Map.Entry<String, List<Map<String, Object>>>> dataMapEntries = dataMap.entrySet();
            for (Map.Entry<String, List<Map<String, Object>>> entryData : dataMapEntries) {
                // key: Excel sheet name
                // valueMap, key: variable path, value: actual value
                String key = entryData.getKey();
                List<Map<String, Object>> valueMapList = entryData.getValue();

                //忽略实际结果 (原始信息)
                if (TestTableEnum.RESULTS.getCode().equals(key)) {
                    continue;
                }

                //按表头字段组装sheet数据
                List<String> headerList = headerListMap.getOrDefault(key,new ArrayList<>());
                List<List<String>> contentRowList = sheetTableMap.getOrDefault(key,new ArrayList<>());
                for (Map<String, Object> valueMap : valueMapList) {
                    List<String> columList = new ArrayList<>();
                    Map<String, List<Map<String, Object>>> finalDataMap = dataMap;
                    headerList.forEach(header -> {
                        if (org.springframework.util.StringUtils.isEmpty(valueMap.get(header))) {
                            columList.add("");
                        } else if (finalDataMap.containsKey(header)) {
                            columList.add(String.valueOf(valueMap.get("id")));
                        } else {
                            columList.add(String.valueOf(valueMap.get(header)));
                        }
                    });
                    contentRowList.add(columList);
                }
            }
        }

        // 得到Excel各sheet需要的dto
        return getExcelSheetList(tableTreeSortEntries, sheetTableMap, typeListMap, variableIndexLabelMap, fileType);
    }

    /**
     * JSONObject转Map
     *
     * @param sourceJson sourceJson
     * @return java.util.Map
     */
    public static Map<String, List<Map<String, Object>>> transJsonObjectToMapList(JSONObject sourceJson) {
        Map<String, List<Map<String, Object>>> dataMap = new HashMap<>(MagicNumbers.TEN);
        for (Map.Entry<String, Object> entry : sourceJson.entrySet()) {
            List<Map<String, Object>> tmpMapList = new ArrayList<>();
            if (TestTableEnum.MASTER.getCode().equals(entry.getKey()) || TestTableEnum.RESULTS.getCode().equals(entry.getKey())) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                tmpMapList.add(map);
            } else {
                tmpMapList = (List<Map<String, Object>>) entry.getValue();

            }
            dataMap.put(entry.getKey(), tmpMapList);
        }
        return dataMap;

    }

    /**
     * 处理 Excel 下载数据的 "预期结果" 和 "实际结果" 对比, 位于表格起始部分
     * <p>预期结果: 变量路径前缀为 expect.* </p>
     * <p>实际结果: 变量路径前缀为 results.expect.*, 与 * 内容相同</p>
     *
     * @param dataMap       测试结果列表 (不包含前置的, 与预期结果并列的实际结果)
     * @param headerKeyList 表头变量路径列表
     * @param fileType      文件类型编码 {@link TestDataTypeEnum}
     * @return 添加过实际结果的测试结果列表
     */
    public static Map<String, List<Map<String, Object>>> appendExpectedActualResultComparison(Map<String, List<Map<String, Object>>> dataMap,
                                                                                              Map<String, List<String>> headerKeyList, String fileType) {
        //处理输入数据和预期结果
        Map<String, Object> masterMap = new HashMap<>(MagicNumbers.SIXTEEN);
        if (dataMap.containsKey(TestTableEnum.MASTER.getCode())) {
            masterMap = dataMap.get(TestTableEnum.MASTER.getCode()).get(0);
        }
        // 处理预期结果, 将实际结果放入 masterMap
        if (TestDataTypeEnum.EXECUTE_RESULT.getCode().equals(fileType)) {
            // 读取未处理的测试结果记录
            Map<String, Object> resultsMap = dataMap.get(TestTableEnum.RESULTS.getCode()).get(0);
            JSONObject rawResultExpect = (JSONObject) resultsMap.get(TestTableEnum.EXPECT.getCode());
            if (null != rawResultExpect) {
                // 处理未处理的测试结果 - 预期结果
                for (Map.Entry<String, Object> rawResultExpectEntry : rawResultExpect.entrySet()) {
                    // 为预期结果添加对应实际结果前缀增加前缀 "results"
                    masterMap.put(TestTableEnum.RESULTS.getCode() + "." + rawResultExpectEntry.getKey(), rawResultExpectEntry.getValue());
                }
            }
            // 补充未设置预期结果的实际结果, 目的: 在对比区域的展示实际结果
            // 追加的实际结果 Map, key: 变量路径, value: 变量值
            Map<String, Object> additionalActualResultMap = new HashMap<>(MagicNumbers.SIXTEEN);
            for (Map.Entry<String, Object> masterMapEntry : masterMap.entrySet()) {
                String variablePath = masterMapEntry.getKey();
                if (!variablePath.startsWith(TestTableEnum.RESULTS.getCode())
                        || variablePath.startsWith(TestTableEnum.RESULTS.getCode() + "." + TestTableEnum.EXPECT.getCode())) {
                    // 跳过不是 "输出结果" 的变量路径
                    continue;
                }
                // 当前遍历的变量为 "输出结果" results.*
                // 恢复输出结果对应的输入内容变量路径 (去除 results.*) 前缀
                String inputVariablePath = variablePath.replaceFirst(TestTableEnum.RESULTS.getCode() + ".", "");
                // 拼接待补充的实际结果变量路径
                String additionalActualResultVariablePath = TestTableEnum.RESULTS.getCode() + "." + TestTableEnum.EXPECT.getCode() + "."
                        + inputVariablePath;
                if (!masterMap.containsKey(additionalActualResultVariablePath)) {
                    // 拼接的变量路径不存在: 置入追加内容 Map
                    additionalActualResultMap.put(additionalActualResultVariablePath, masterMapEntry.getValue());
                }
            }
            // 全量追加内容, 避免 ConcurrentModificationException
            masterMap.putAll(additionalActualResultMap);
        }
        Map<String, Object> masterTmpMap = new HashMap<>(MagicNumbers.TEN);
        Map<String, Object> retTmpMap = new HashMap<>(MagicNumbers.TEN);
        for (Map.Entry<String, Object> entry : masterMap.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            if (TestExcelFileEnum.ID.getCode().equals(key) || TestExcelFileEnum.PARENT_ID.getCode().equals(key)) {
                masterTmpMap.put(key, value);
                if (headerKeyList.containsKey(TestTableEnum.EXPECT.getCode())) {
                    retTmpMap.put(key, value);
                }
            } else {
                //处理实际结果
                if (TestDataTypeEnum.EXECUTE_RESULT.getCode().equals(fileType)) {
                    masterTmpMap.put(key, value);
                } else {

                    if (key.startsWith(TestTableEnum.EXPECT.getCode())) {
                        retTmpMap.put(key, value);
                    } else {
                        masterTmpMap.put(key, value);
                    }
                }
            }
        }
        List<Map<String, Object>> masterTmpList = new ArrayList<>();
        masterTmpList.add(masterTmpMap);
        dataMap.put(TestTableEnum.MASTER.getCode(), masterTmpList);
        //存在预期结果的情况
        if (headerKeyList.containsKey(TestTableEnum.EXPECT.getCode())) {
            List<Map<String, Object>> retTmpList = new ArrayList<>();
            retTmpList.add(retTmpMap);
            dataMap.put(TestTableEnum.EXPECT.getCode(), retTmpList);
        }
        return dataMap;
    }

    /**
     * 记录变量中英文名称对照
     * 对数组类型表头进行递归解析
     *
     * @param headerMap             表头, 带有多个工作表
     * @param headerList            特定工作表表头
     * @param variableIndexLabelMap 变量中英文名对照
     */
    private static void fillVariableIndexLabelMap(Map<String, List<Map<String, Object>>> headerMap, List<Map<String, Object>> headerList,
                                                  Map<String, String> variableIndexLabelMap) {
        if (CollectionUtils.isEmpty(headerList)) {
            return;
        }
        for (Map<String, Object> header : headerList) {
            String headerIndex = String.valueOf(header.get("index"));

            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(header.get("type"))
                    && DomainModelArrEnum.YES.getCode().equals(String.valueOf(header.get("isArr")))) {
                // 对于数组类型 (type == object && isArray == 1), 递归调用方法, 以访问每个数组元素
                fillVariableIndexLabelMap(headerMap, headerMap.get(String.valueOf(headerIndex)), variableIndexLabelMap);
            }

            // 记录中英文变量名对照
            variableIndexLabelMap.put(String.valueOf(headerIndex), String.valueOf(header.get("label")));
            // 特殊处理预期结果英文变量名: 添加前缀
            if (InputExpectTypeEnum.EXPECT.getCode().equals(String.valueOf(header.get("fieldType")))) {
                // 预期结果
                variableIndexLabelMap.put(String.valueOf(headerIndex), header.get("label") + "-" + TestTableEnum.EXPECT.getMessage());
                // 预期结果对应的实际结果
                variableIndexLabelMap.put(TestTableEnum.RESULTS.getCode() + "." + headerIndex,
                        header.get("label") + "-" + TestTableEnum.RESULTS.getMessage());
            }
        }
    }

    /**
     * 工作表表头排序
     * <p>排序依据: 预期对比 -> 输入 -> 实际结果</p>
     *
     * @param headerList 工作表表头对象序列
     * @return 经过排序的工作表表头对象序列
     * @see TestTableEnum
     */
    private static List<Map<String, Object>> sortMasterHeader(List<Map<String, Object>> headerList) {
        // 1. 主表表头对象归类
        Map<TestTableEnum, List<Map<String, Object>>> headerListMap = new EnumMap<>(TestTableEnum.class);
        for (TestTableEnum type : TestTableEnum.values()) {
            headerListMap.put(type, new ArrayList<>());
        }
        List<Map<String, Object>> sortedHeaderList = new ArrayList<>();
        if (CollectionUtils.isEmpty(headerList)) {
            return sortedHeaderList;
        }
        for (Map<String, Object> header : headerList) {
            // 表头对应的数据类别
            String headerIndex = String.valueOf(header.get("index"));
            String headerCategory = headerIndex.substring(0, headerIndex.indexOf('.'));

            if (TestTableEnum.EXPECT.getCode().equals(headerCategory)) {
                headerListMap.get(TestTableEnum.EXPECT).add(header);
            } else if (TestTableEnum.RESULTS.getCode().equals(headerCategory)) {
                headerListMap.get(TestTableEnum.RESULTS).add(header);
            } else {
                // 其他类型
                headerListMap.get(TestTableEnum.INPUT).add(header);
            }
        }

        // 2. 对表头按照类别重新排序
        for (TestTableEnum type : TestTableExportUtil.DATA_CATEGORY_SEQUENCE) {
            sortedHeaderList.addAll(headerListMap.get(type));
        }

        return sortedHeaderList;
    }


    /**
     * 初始化工作簿页
     * 获取工作表sheetlist
     *
     * @param tableTreeSortEntries
     * @return java.util.Map<java.lang.String, java.util.List < java.util.List < java.lang.String>>>
     */
    private static Map<String, List<List<String>>> initiateWorksheet(Set<Map.Entry<String, List<Map<String, Object>>>> tableTreeSortEntries) {
        Map<String, List<List<String>>> sheetMapList = new LinkedHashMap<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : tableTreeSortEntries) {
            String key = entry.getKey();

            sheetMapList.put(key, new ArrayList<>());

        }

        return sheetMapList;
    }


    /**
     * 获取 表头headerlist、表头字段类型typelist
     *
     * @param tableTreeSortEntries
     * @param fileType
     * @return java.util.Map<java.lang.String, java.util.Map < java.lang.String, java.util.List < java.lang.String>>>
     */
    private static Map<String, Map<String, List<String>>> getHeaderKeyAndTypeList(Set<Map.Entry<String, List<Map<String, Object>>>> tableTreeSortEntries,
                                                                                  String fileType) {
        Map<String, Map<String, List<String>>> retMap = new HashMap<>(MagicNumbers.SIXTEEN);
        // 表头
        Map<String, List<String>> headerListMap = new LinkedHashMap<>();
        // 字段数据类型
        Map<String, List<String>> typeListMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : tableTreeSortEntries) {
            String key = entry.getKey();
            List<Map<String, Object>> valueList = entry.getValue();
            List<String> keyList = new ArrayList<>();
            List<String> typeList = new ArrayList<>();

            if (TestTableEnum.MASTER.getCode().equals(key) || TestTableEnum.EXPECT.getCode().equals(key)) {

                keyList.add(TestExcelFileEnum.ID.getCode());
                typeList.add(DataVariableTypeEnum.STRING_TYPE.getMessage());
            } else {
                keyList.add(TestExcelFileEnum.ID.getCode());
                keyList.add(TestExcelFileEnum.PARENT_ID.getCode());

                typeList.add(DataVariableTypeEnum.STRING_TYPE.getMessage());
                typeList.add(DataVariableTypeEnum.STRING_TYPE.getMessage());
            }

            if (!CollectionUtils.isEmpty(valueList)) {
                for (Map<String, Object> map : valueList) {

                    String index = String.valueOf(map.get("index"));
                    keyList.add(index);

                    String type = String.valueOf(map.get("type"));
                    typeList.add(type);

                    //实际结果index索引
                    if (TestDataTypeEnum.EXECUTE_RESULT.getCode().equals(fileType)
                            && String.valueOf(map.get("fieldType")).equals(InputExpectTypeEnum.EXPECT.getCode())) {
                        keyList.add(TestTableEnum.RESULTS.getCode() + "." + index);
                        typeList.add(type);
                    }

                }
            }

            headerListMap.put(key, keyList);
            typeListMap.put(key, typeList);

        }

        retMap.put("headerList", headerListMap);
        retMap.put("typeList", typeListMap);

        return retMap;
    }

    /**
     * 表头数据排序，并得到表头label中文描述map
     *
     * @param header
     * @param masterHeaderList
     * @param tableTreeSortMap
     * @param labelMap
     */
    private static void getHeaderSortList(Map<String, List<Map<String, Object>>> header, List<Map<String, Object>> masterHeaderList,
                                          Map<String, List<Map<String, Object>>> tableTreeSortMap, Map<String, String> labelMap) {
        if (!CollectionUtils.isEmpty(masterHeaderList)) {
            for (Map<String, Object> map : masterHeaderList) {
                //数组，找到对应的表头
                if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(String.valueOf(map.get("type")))
                        && DomainModelArrEnum.YES.getCode().equals(String.valueOf(map.get("isArr")))) {
                    List<Map<String, Object>> indexList = header.get(String.valueOf(map.get("index")));
                    tableTreeSortMap.put(String.valueOf(map.get("index")), indexList);
                    getHeaderSortList(header, indexList, tableTreeSortMap, labelMap);
                }
                labelMap.put(String.valueOf(map.get("index")), String.valueOf(map.get("label")));
            }
        }
    }

    /**
     * 获取Excel工作表需要的dto数据
     *
     * @param tableTreeSortEntries
     * @param sheetMapList
     * @param typeKeyList
     * @param labelMap
     * @param fileType
     * @return java.util.List<com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto>
     */
    private static List<TestExcelDto> getExcelSheetList(Set<Map.Entry<String, List<Map<String, Object>>>> tableTreeSortEntries,
                                                        Map<String, List<List<String>>> sheetMapList, Map<String, List<String>> typeKeyList,
                                                        Map<String, String> labelMap, String fileType) {
        List<TestExcelDto> list = new ArrayList<>();
        int i = 1;
        // Excel 表头结构组装
        for (Map.Entry<String, List<Map<String, Object>>> entry : tableTreeSortEntries) {
            String key = entry.getKey();
            List<Map<String, Object>> valueList = entry.getValue();
            //表头
            List<String> titleList = new ArrayList<>();
            List<String> keyList = new ArrayList<>();
            if (TestTableEnum.MASTER.getCode().equals(key) || TestTableEnum.EXPECT.getCode().equals(key)) {
                titleList.add(TestExcelFileEnum.ID.getMessage());
                keyList.add(TestExcelFileEnum.ID.getCode());
            } else {
                titleList.add(TestExcelFileEnum.ID.getMessage());
                titleList.add(TestExcelFileEnum.PARENT_ID.getMessage());
                keyList.add(TestExcelFileEnum.ID.getCode());
                keyList.add(TestExcelFileEnum.PARENT_ID.getCode());
            }
            if (!CollectionUtils.isEmpty(valueList)) {
                for (Map<String, Object> map : valueList) {
                    //实际结果
                    String index = String.valueOf(map.get("index"));
                    String label = String.valueOf(map.get("label"));
                    String name = String.valueOf(map.get("name"));
                    if (TestDataTypeEnum.EXECUTE_RESULT.getCode().equals(fileType)) {
                        if (String.valueOf(map.get("fieldType")).equals(InputExpectTypeEnum.EXPECT.getCode())) {
                            // 预期结果双层表头
                            // 第一行内容为变量路径 - 变量中文名
                            titleList.add(index + "-" + label);
                            titleList.add(index + "-" + label);
                            // 第二行内容为 "预期结果" / "实际结果"
                            keyList.add(TestTableEnum.EXPECT.getMessage());
                            keyList.add(TestTableEnum.RESULTS.getMessage());
                        } else {
                            // 其他类型双层表头
                            // 第一行内容为变量路径
                            titleList.add(label);
                            // 第二行内容为变量中文名
                            keyList.add(index);
                        }
                    } else {
                        titleList.add(label);
                        if (TestTableEnum.EXPECT.getCode().equals(key)) {
                            keyList.add(name);
                        } else if (key.startsWith(TestTableEnum.EXPECT.getCode())) {
                            keyList.add(index.substring(TestTableEnum.EXPECT.getCode().length() + 1));
                        } else {
                            keyList.add(index);
                        }
                    }
                }
            }
            TestExcelDto testExcelDto = new TestExcelDto();
            // 设置工作簿页名称
            if (TestTableEnum.MASTER.getCode().equals(key)) {
                testExcelDto.setTableName(TestTableEnum.MASTER.getMessage());
                testExcelDto.setSheetName(TestTableEnum.MASTER.getMessage());
            } else if (TestTableEnum.EXPECT.getCode().equals(key)) {
                testExcelDto.setTableName(TestTableEnum.EXPECT.getMessage());
                testExcelDto.setSheetName(TestTableEnum.EXPECT.getMessage());
            } else {
                testExcelDto.setTableName(key + "-" + labelMap.get(key));
                testExcelDto.setSheetName("附" + i + "-" + labelMap.get(key));
                i++;
            }
            testExcelDto.setKeyList(keyList);
            testExcelDto.setTitleList(titleList);
            testExcelDto.setValueList(sheetMapList.get(key));
            testExcelDto.setTypeList(typeKeyList.get(key));
            list.add(testExcelDto);
        }
        return list;
    }

    /**
     * 导出模板数据处理
     *
     * @param formDataList 模板数据
     * @param dataModel 数据模型
     * @return java.util.List
     */
    public static List<TestExcelDto> exportTemplateData(List<TestFormDto> formDataList, Map<String, DomainDataModelTreeDto> dataModel) {
        //获取表头
        Map<String, Object> testData = TestTableHeaderUtil.getTestData(formDataList, dataModel, 1);
        if (testData == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "在线表单获取表头失败！");
        }
        Map<String, List<Map<String, Object>>> header = (Map<String, List<Map<String, Object>>>) testData.get(TestHeaderValueEnum.HEADER.getCode());
        //获取主表
        List<Map<String, Object>> master = header.get(TestTableEnum.MASTER.getCode());
        //按fieldType字段类型分组：0-输入，1-预期结果，2-实际结果
        TreeMap<String, List<Map<String, Object>>> dataMapListMap = master.stream().collect(Collectors.groupingBy(item -> {
            return String.valueOf(item.get("fieldType"));

        }, TreeMap::new, Collectors.toList()));
        //组装后的表头数据
        Map<String, List<Map<String, Object>>> linkTreeMap = new LinkedHashMap<>();
        //中文描述label map
        Map<String, String> labelMap = new HashMap<>(MagicNumbers.SIXTEEN);
        //输入
        List<Map<String, Object>> inputMapList = dataMapListMap.get(InputExpectTypeEnum.INPUT.getCode());
        linkTreeMap.put(TestTableEnum.INPUT.getCode(), inputMapList);
        // 表头排序, 填写变量中英文对照表
        getHeaderSortList(header, inputMapList, linkTreeMap, labelMap);
        //预期结果
        if (dataMapListMap.containsKey(InputExpectTypeEnum.EXPECT.getCode())) {
            List<Map<String, Object>> expectMapList = dataMapListMap.get(InputExpectTypeEnum.EXPECT.getCode());
            linkTreeMap.put(TestTableEnum.EXPECT.getCode(), expectMapList);
            // 表头排序
            getHeaderSortList(header, expectMapList, linkTreeMap, labelMap);
        }
        //组装Excel需要的表头数据
        List<TestExcelDto> list = new ArrayList<>();
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = linkTreeMap.entrySet();
        int i = 1;
        int j = 1;
        for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
            String key = entry.getKey();
            List<Map<String, Object>> valueList = entry.getValue();
            //表头
            List<String> titleList = new ArrayList<>();
            List<String> keyList = new ArrayList<>();
            //输入数据主表和预期结果主表
            if (TestTableEnum.INPUT.getCode().equals(key) || TestTableEnum.EXPECT.getCode().equals(key)) {
                titleList.add(TestExcelFileEnum.ID.getMessage());
                keyList.add(TestExcelFileEnum.ID.getCode());
            } else {
                titleList.add(TestExcelFileEnum.ID.getMessage());
                titleList.add(TestExcelFileEnum.PARENT_ID.getMessage());
                keyList.add(TestExcelFileEnum.ID.getCode());
                keyList.add(TestExcelFileEnum.PARENT_ID.getCode());
            }
            extractedTitleData(key, valueList, titleList, keyList);
            TestExcelDto testExcelDto = new TestExcelDto();
            testExcelDto.setSheetName(key);
            if (TestTableEnum.INPUT.getCode().equals(key)) {
                testExcelDto.setTableName(TestTableEnum.INPUT.getMessage());
                testExcelDto.setSheetName(TestTableEnum.INPUT.getMessage());
            } else if (TestTableEnum.EXPECT.getCode().equals(key)) {
                testExcelDto.setTableName(TestTableEnum.EXPECT.getMessage());
                testExcelDto.setSheetName(TestTableEnum.EXPECT.getMessage());
            } else if (key.startsWith(TestTableEnum.EXPECT.getCode())) {
                testExcelDto.setTableName(key + "-" + labelMap.get(key));
                testExcelDto.setSheetName(TestTableEnum.EXPECT.getShortmsg() + j + "-" + labelMap.get(key));
                j++;
            } else {
                testExcelDto.setTableName(key + "-" + labelMap.get(key));
                testExcelDto.setSheetName(TestTableEnum.INPUT.getShortmsg() + i + "-" + labelMap.get(key));
                i++;
            }
            testExcelDto.setKeyList(keyList);
            testExcelDto.setTitleList(titleList);
            list.add(testExcelDto);
        }
        return list;
    }

    private static void extractedTitleData(String key, List<Map<String, Object>> valueList, List<String> titleList, List<String> keyList) {
        if (!CollectionUtils.isEmpty(valueList)) {
            for (Map<String, Object> map : valueList) {
                titleList.add((String) map.get("label"));
                if (TestTableEnum.EXPECT.getCode().equals(key)) {
                    keyList.add((String) map.get("name"));
                } else if (key.startsWith(TestTableEnum.EXPECT.getCode())) {
                    String index = (String) map.get("index");
                    keyList.add(index.substring(index.indexOf(".") + 1));
                } else {
                    keyList.add((String) map.get("index"));
                }
            }
        }
    }
}
