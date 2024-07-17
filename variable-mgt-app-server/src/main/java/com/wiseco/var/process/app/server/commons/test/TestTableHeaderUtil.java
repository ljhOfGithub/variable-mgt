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
import com.wiseco.boot.commons.exception.ServiceException;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 测试数据表格表头工具类
 *
 * @author wangxianli
 * @since 2021/12/30
 */
public class TestTableHeaderUtil {

    private static final String SPOT = ".";

    /**
     * 获取测试数据表头及结构
     *
     * @param formDataList 数据列表
     * @param dataModel 数据模型
     * @param id id
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject getTestData(List<TestFormDto> formDataList, Map<String, DomainDataModelTreeDto> dataModel, int id) {

        if (CollectionUtils.isEmpty(formDataList)) {
            return null;
        }
        String idStr = String.valueOf(id);
        Map<String, Map<String, TestFormDto>> tableHeaderMap = new HashMap<>(MagicNumbers.TEN);

        //数据模型的变量
        List<TestFormDto> inputList = new ArrayList<>();

        //参数
        List<TestFormDto> paramList = new ArrayList<>();

        //自定义函数
        List<TestFormDto> functionList = new ArrayList<>();

        for (TestFormDto dto : formDataList) {
            if (dto.getName().startsWith(PositionVarEnum.PARAMETERS.getName()) || dto.getName().startsWith(PositionVarEnum.LOCAL_VARS.getName())) {
                paramList.add(dto);
            } else if (dto.getName().equals(CommonConstant.CUSTOM_FUNCTION_RETURN_NAME)
                    || dto.getName().equals(CommonConstant.COMMON_FUNCTION_RETURN_NAME)
                    || dto.getName().equals(CommonConstant.VARIABLE_RETURN_NAME)) {
                functionList.add(dto);
            } else {
                inputList.add(dto);
            }
        }

        if (inputList.size() > 0) {
            //非参数本地变量处理
            tableHeaderMap = handleInputData(inputList, dataModel, idStr);
        }

        if (paramList.size() > 0) {
            //参数本地变量处理
            handleParamData(paramList, tableHeaderMap, dataModel, idStr);

        }
        if (functionList.size() > 0) {
            //自定义函数
            handleBaseType(functionList, tableHeaderMap, idStr);
        }

        //返回
        return handleResultData(tableHeaderMap);
    }

    /**
     * 处理返回结果
     *
     * @param tableHeaderMap 表头映射
     * @return com.wisecotech.json.JSONObject
     */
    private static JSONObject handleResultData(Map<String, Map<String, TestFormDto>> tableHeaderMap) {

        JSONObject resultMap = new JSONObject();

        JSONObject headerMapObj = new JSONObject();

        JSONObject valueMapObj = new JSONObject();

        Set<Map.Entry<String, Map<String, TestFormDto>>> entries = tableHeaderMap.entrySet();
        for (Map.Entry<String, Map<String, TestFormDto>> entry : entries) {
            String key = entry.getKey();
            Map<String, TestFormDto> value = entry.getValue();

            Set<Map.Entry<String, TestFormDto>> subEntries = value.entrySet();

            JSONArray headerListTmp = new JSONArray();
            JSONObject valueTmp = new JSONObject();

            for (Map.Entry<String, TestFormDto> subEntrie : subEntries) {

                TestFormDto subValue = subEntrie.getValue();
                String subKey = subValue.getIndex();
                //表头赋值
                JSONObject headerTmpDto = new JSONObject();
                headerTmpDto.put("index", subKey);
                headerTmpDto.put("name", subValue.getName());
                headerTmpDto.put("isArr", subValue.getIsArr());
                headerTmpDto.put("label", subValue.getLabel());
                headerTmpDto.put("type", subValue.getType());
                headerTmpDto.put("fieldType", subValue.getFieldType());

                headerListTmp.add(headerTmpDto);

                //value赋值
                valueTmp.put(subKey, subValue.getValue());
                valueTmp.put("id", subValue.getId());
                valueTmp.put("parentId", subValue.getParentId());

            }
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                valueMapObj.put(key, valueTmp);
            } else {
                JSONArray valueTmpList = new JSONArray();
                valueTmpList.add(valueTmp);
                valueMapObj.put(key, valueTmpList);
            }

            headerMapObj.put(key, headerListTmp);
        }
        resultMap.put(TestHeaderValueEnum.HEADER.getCode(), headerMapObj);
        resultMap.put(TestHeaderValueEnum.VALUE.getCode(), valueMapObj);

        return resultMap;
    }

    /**
     * 输入数据处理：不含参数，遍历
     *
     * @param formDataList 数据列表
     * @param treeDtoMap 树形dto映射
     * @param id id
     * @return Map
     */
    private static Map<String, Map<String, TestFormDto>> handleInputData(List<TestFormDto> formDataList,
                                                                         Map<String, DomainDataModelTreeDto> treeDtoMap, String id) {
        Map<String, Map<String, TestFormDto>> header = new TreeMap<>();

        for (TestFormDto testFormDto : formDataList) {

            handleInputEachData(testFormDto, header, treeDtoMap, id);

        }

        return header;

    }

    /**
     * 输入数据处理：不含参数，单个属性
     *
     * @param testFormDto 测试TestForm对象
     * @param header 表头
     * @param treeDtoMap 树形dto map
     * @param id id
     */
    private static void handleInputEachData(TestFormDto testFormDto, Map<String, Map<String, TestFormDto>> header,
                                            Map<String, DomainDataModelTreeDto> treeDtoMap, String id) {
        List<String> list = splitKey(testFormDto.getName(), treeDtoMap);

        String master = TestTableEnum.MASTER.getCode();
        int i = 0;
        String tmpId = null;
        String parentKey = null;
        for (String str : list) {

            if (!treeDtoMap.containsKey(str)) {
                throw new ServiceException("未找到【" + str + "】");
            }
            DomainDataModelTreeDto treeDto = treeDtoMap.get(str);
            TestFormDto testFormDtoValue = new TestFormDto();

            //预期结果index追加前缀expect
            if (InputExpectTypeEnum.EXPECT.getCode().equals(String.valueOf(testFormDto.getFieldType()))) {
                testFormDtoValue.setIndex(TestTableEnum.EXPECT.getCode() + SPOT + str);
            } else {
                testFormDtoValue.setIndex(str);
            }

            testFormDtoValue.setLabel(treeDto.getDescribe());
            testFormDtoValue.setIsArr(Integer.parseInt(treeDto.getIsArr()));
            testFormDtoValue.setType(treeDto.getType());

            testFormDtoValue.setFieldType(testFormDto.getFieldType());

            if (list.size() == (i + 1)) {

                testFormDtoValue.setValue(testFormDto.getValue());
            } else {
                //数组:使用...占位
                testFormDtoValue.setValue("...");
            }

            if (i == 0) {
                testFormDtoValue.setId(id);
                testFormDtoValue.setParentId("0");
                testFormDtoValue.setName(str);

                Map<String, TestFormDto> masterMap = new TreeMap<>();
                if (header.containsKey(master)) {
                    masterMap = header.get(master);
                }

                masterMap.put(testFormDtoValue.getIndex(), testFormDtoValue);
                header.put(master, masterMap);

            } else {
                testFormDtoValue.setId(id + "_" + tmpId);
                testFormDtoValue.setParentId(tmpId);
                testFormDtoValue.setName(treeDto.getName());

                Map<String, TestFormDto> map = new TreeMap<>();
                if (header.containsKey(parentKey)) {
                    map = header.get(parentKey);
                }

                map.put(testFormDtoValue.getIndex(), testFormDtoValue);
                header.put(parentKey, map);

            }
            parentKey = testFormDtoValue.getIndex();
            tmpId = testFormDtoValue.getId();
            i++;
        }
    }

    /**
     * 输入数据情况获取所有的key，遇到数组则生成一个key
     *
     * @param originalKey 原始key
     * @param treeDtoMap 决策领域树形结构实体map
     * @return List
     */
    private static List<String> splitKey(String originalKey, Map<String, DomainDataModelTreeDto> treeDtoMap) {

        List<String> resultKeyList = new ArrayList<>();
        String[] keyArray = originalKey.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keyArray) {
            String tempKey;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(SPOT).append(key);
                tempKey = stringBuilder.toString();
            } else {
                stringBuilder.append(key);
                tempKey = key;
            }
            if (!treeDtoMap.containsKey(tempKey)) {
                continue;
            }
            DomainDataModelTreeDto treeDto = treeDtoMap.get(tempKey);
            if (isArrayDto(treeDto.getType(), treeDto.getIsArr())) {
                resultKeyList.add(tempKey);
            }

        }
        if (CollectionUtils.isEmpty(resultKeyList)) {
            resultKeyList.add(originalKey);
        }

        if (!resultKeyList.contains(originalKey)) {
            resultKeyList.add(originalKey);
        }

        return resultKeyList;
    }


    /**
     * 参数本地变量处理
     *
     * @param dataList 数据list
     * @param header 头部
     * @param dataModel 数据模型
     * @param id id
     */
    private static void handleParamData(List<TestFormDto> dataList, Map<String, Map<String, TestFormDto>> header,
                                        Map<String, DomainDataModelTreeDto> dataModel, String id) {

        //基础类型
        List<TestFormDto> baseParamList = new ArrayList<>();

        //引用类型
        List<TestFormDto> refParamList = new ArrayList<>();
        for (TestFormDto testFormDto : dataList) {
            DataVariableBasicTypeEnum nameEnum = DataVariableBasicTypeEnum.getNameEnum(testFormDto.getParameterType());
            if (nameEnum != null) {
                baseParamList.add(testFormDto);

            } else {
                refParamList.add(testFormDto);
            }

        }
        if (!baseParamList.isEmpty()) {
            handleBaseType(baseParamList, header, id);
        }
        if (!refParamList.isEmpty()) {
            handleParamRefType(refParamList, header, dataModel, id);
        }

    }

    private static void handleBaseType(List<TestFormDto> dataList, Map<String, Map<String, TestFormDto>> header, String id) {
        for (TestFormDto testFormDto : dataList) {

            String key = testFormDto.getName();

            //基础类型
            TestFormDto testFormDtoValue = new TestFormDto();
            //预期结果index追加前缀expect
            if (InputExpectTypeEnum.EXPECT.getCode().equals(String.valueOf(testFormDto.getFieldType()))) {
                testFormDtoValue.setIndex(TestTableEnum.EXPECT.getCode() + SPOT + key);
            } else {
                testFormDtoValue.setIndex(key);
            }
            testFormDtoValue.setId(id);
            testFormDtoValue.setParentId("0");
            testFormDtoValue.setName(key);
            testFormDtoValue.setLabel(testFormDto.getLabel());
            testFormDtoValue.setIsArr(testFormDto.getIsArr());
            testFormDtoValue.setType(testFormDto.getType());
            testFormDtoValue.setValue(testFormDto.getValue());
            testFormDtoValue.setFieldType(testFormDto.getFieldType());

            Map<String, TestFormDto> masterMap = new TreeMap<>();
            if (header.containsKey(TestTableEnum.MASTER.getCode())) {
                masterMap = header.get(TestTableEnum.MASTER.getCode());
            }

            masterMap.put(testFormDtoValue.getIndex(), testFormDtoValue);
            header.put(TestTableEnum.MASTER.getCode(), masterMap);

        }
    }


    /**
     * 引用类型
     *
     * @param dataList 数据list
     * @param header 头部
     * @param dataModel 数据模型
     * @param id id
     */
    private static void handleParamRefType(List<TestFormDto> dataList, Map<String, Map<String, TestFormDto>> header,
                                           Map<String, DomainDataModelTreeDto> dataModel, String id) {
        Map<String, Map<String, TestFormDto>> paramHeader = new HashMap<>(MagicNumbers.SIXTEEN);

        for (TestFormDto testFormDto : dataList) {
            handleParamRefEach(testFormDto, paramHeader, dataModel, id);
        }
        //合并到输入数据master
        Map<String, TestFormDto> masterMap = new TreeMap<>();
        if (header.containsKey(TestTableEnum.MASTER.getCode())) {
            masterMap = header.get(TestTableEnum.MASTER.getCode());
        }

        masterMap.putAll(paramHeader.get(TestTableEnum.MASTER.getCode()));
        header.put(TestTableEnum.MASTER.getCode(), masterMap);

        Set<Map.Entry<String, Map<String, TestFormDto>>> entries = paramHeader.entrySet();
        for (Map.Entry<String, Map<String, TestFormDto>> entry : entries) {

            if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                continue;
            }
            header.put(entry.getKey(), entry.getValue());
        }

    }

    /**
     * 引用类型-单个数据处理
     *
     * @param testFormDto 测试TestForm对象
     * @param paramHeader param标题
     * @param dataModel 数据模型
     * @param id id
     */
    private static void handleParamRefEach(TestFormDto testFormDto, Map<String, Map<String, TestFormDto>> paramHeader,
                                           Map<String, DomainDataModelTreeDto> dataModel, String id) {
        //引用类型
        String[] split = testFormDto.getName().split("\\.");
        String paramKey = split[0] + "." + split[1];
        String newKey = testFormDto.getName().replace(paramKey, testFormDto.getParameterType());

        List<String> list = splitParamKey(newKey, testFormDto.getParameterType(), testFormDto, dataModel);
        int i = 0;
        String tmpId = null;
        String parentKey = null;
        for (String str : list) {
            String index = str.replace(testFormDto.getParameterType(), paramKey);

            DomainDataModelTreeDto treeDto = dataModel.get(str);
            if (treeDto == null) {
                throw new ServiceException("数据模型未找到" + str);
            }

            TestFormDto testFormDtoValue = new TestFormDto();
            //预期结果index追加前缀expect
            if (InputExpectTypeEnum.EXPECT.getCode().equals(String.valueOf(testFormDto.getFieldType()))) {
                testFormDtoValue.setIndex(TestTableEnum.EXPECT.getCode() + SPOT + index);
            } else {
                testFormDtoValue.setIndex(index);
            }

            testFormDtoValue.setName(index);
            testFormDtoValue.setLabel(treeDto.getDescribe());

            if (testFormDto.getParameterType().equals(str)) {
                testFormDtoValue.setIsArr(testFormDto.getIsParameterArray());
            } else {
                testFormDtoValue.setIsArr(Integer.parseInt(treeDto.getIsArr()));
            }

            testFormDtoValue.setType(treeDto.getType());

            testFormDtoValue.setValue(testFormDto.getValue());
            testFormDtoValue.setFieldType(testFormDto.getFieldType());

            if (list.size() == (i + 1)) {

                testFormDtoValue.setValue(testFormDto.getValue());
            } else {
                testFormDtoValue.setValue("...");

            }

            if (i == 0) {
                testFormDtoValue.setId(id);
                testFormDtoValue.setParentId("0");
                testFormDtoValue.setName(index);

                Map<String, TestFormDto> masterMap = new TreeMap<>();
                if (paramHeader.containsKey(TestTableEnum.MASTER.getCode())) {
                    masterMap = paramHeader.get(TestTableEnum.MASTER.getCode());
                }

                masterMap.put(testFormDtoValue.getIndex(), testFormDtoValue);
                paramHeader.put(TestTableEnum.MASTER.getCode(), masterMap);

            } else {
                testFormDtoValue.setId(id + "_" + tmpId);
                testFormDtoValue.setParentId(tmpId);
                testFormDtoValue.setName(treeDto.getName());

                Map<String, TestFormDto> map = new TreeMap<>();
                if (paramHeader.containsKey(parentKey)) {
                    map = paramHeader.get(parentKey);
                }
                map.put(testFormDtoValue.getIndex(), testFormDtoValue);
                paramHeader.put(parentKey, map);

            }
            parentKey = testFormDtoValue.getIndex();
            tmpId = testFormDtoValue.getId();
            i++;
        }
    }

    /**
     * 参数本地变量情况获取所有的key，遇到数组则生成一个key
     *
     * @param originalKey 原始key
     * @param parameterType 参数类型
     * @param testFormDto 测试TestForm对象
     * @param treeDtoMap 决策领域树形结构实体map
     * @return List
     */
    private static List<String> splitParamKey(String originalKey, String parameterType, TestFormDto testFormDto,
                                              Map<String, DomainDataModelTreeDto> treeDtoMap) {

        List<String> resultKeyList = new ArrayList<>();

        String[] keyArray = originalKey.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        //当前参数引用类型父级标识
        boolean isParameterParent = true;
        for (String key : keyArray) {

            String tempKey;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(".").append(key);
                tempKey = stringBuilder.toString();
            } else {
                stringBuilder.append(key);
                tempKey = key;
            }
            if (!treeDtoMap.containsKey(tempKey)) {
                continue;
            }
            DomainDataModelTreeDto treeDto = treeDtoMap.get(tempKey);
            if (parameterType.equals(tempKey)) {
                isParameterParent = false;
            }
            String isArr = treeDto.getIsArr();
            if (isParameterParent) {
                //父级是否数组都置为否
                isArr = DomainModelArrEnum.NO.getCode();
            }

            //如果是参数引用对象，则使用参数设置的是否数组的值
            if (parameterType.equals(tempKey)) {
                isArr = String.valueOf(testFormDto.getIsParameterArray());
            }
            if (isArrayDto(treeDto.getType(), isArr)) {
                resultKeyList.add(tempKey);
            }

        }
        if (CollectionUtils.isEmpty(resultKeyList)) {
            resultKeyList.add(originalKey);
        }

        if (!resultKeyList.contains(originalKey)) {
            resultKeyList.add(originalKey);
        }

        return resultKeyList;
    }

    /**
     * 测试数据表头排序
     *
     * @param originalHeader 原始表头
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject headerSort(JSONObject originalHeader) {
        JSONObject targetHeader = new JSONObject();
        Set<String> keySet = originalHeader.keySet();
        for (String key : keySet) {
            JSONArray jsonArr = originalHeader.getJSONArray(key);

            //快速排序，重写compare方法，完成按指定字段比较，完成排序
            Collections.sort(jsonArr, new Comparator<Object>() {

                //重写compare方法
                @Override
                public int compare(Object a, Object b) {

                    JSONObject aJson = (JSONObject) a;
                    JSONObject bJson = (JSONObject) b;
                    String valA = aJson.getString("index");
                    String valB = bJson.getString("index");

                    //升序
                    return valA.compareTo(valB);

                }
            });

            targetHeader.put(key, jsonArr);

        }

        return targetHeader;
    }

    /**
     * 表头：json转map
     *
     * @param originalJsonObject 原始jsnobject
     * @return Map
     */
    public static Map<String, List<Map<String, Object>>> transferJsonToMap(JSONObject originalJsonObject) {
        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        Set<String> keySet = originalJsonObject.keySet();
        for (String key : keySet) {
            List<Map<String, Object>> list = new ArrayList<>();
            JSONArray jsonArray = originalJsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                Map<String, Object> objectMap = new HashMap<>(MagicNumbers.SIXTEEN);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    objectMap.put(entry.getKey(), entry.getValue());
                }
                list.add(objectMap);
            }
            map.put(key, list);
        }
        return map;
    }

    /**
     * 根据类型object和是否数组标识判断是否是数组
     *
     * @param type 类型
     * @param isArr 是否数组
     * @return boolean
     */
    private static boolean isArrayDto(String type, String isArr) {
        return DomainModelArrEnum.YES.getCode().equals(isArr) && DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(type);
    }

    /**
     * 从测试执行结果的表头中将实际表头转成预期结果表头
     *
     * @param originalHeaderObj 原始表头对象
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject transferResultToExpect(JSONObject originalHeaderObj) {
        JSONObject targetExpectHeader = new JSONObject();
        Set<String> keySet = originalHeaderObj.keySet();

        for (String key : keySet) {
            String targetKey = null;
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                targetKey = TestTableEnum.EXPECT.getCode();
            } else if (key.startsWith(TestTableEnum.RESULTS.getCode())) {
                targetKey = key.replaceFirst(TestTableEnum.RESULTS.getCode(), TestTableEnum.EXPECT.getCode());
            } else {
                continue;
            }
            JSONArray targetJsonArray = new JSONArray();
            JSONArray originalJsonArray = originalHeaderObj.getJSONArray(key);
            for (int i = 0; i < originalJsonArray.size(); i++) {
                JSONObject jsonObject = originalJsonArray.getJSONObject(i);
                if (!jsonObject.getString("fieldType").equals(InputExpectTypeEnum.RESULTS.getCode())) {
                    continue;
                }
                jsonObject.put("fieldType", InputExpectTypeEnum.EXPECT.getCode());
                jsonObject.put("index", jsonObject.getString("index").replaceFirst(TestTableEnum.RESULTS.getCode(), TestTableEnum.EXPECT.getCode()));
                targetJsonArray.add(jsonObject);
            }
            if (targetJsonArray.size() > 0) {
                targetExpectHeader.put(targetKey, targetJsonArray);
            }

        }

        return targetExpectHeader;
    }

    /**
     * 组件测试预期结果表头重置
     *
     * @param formList 表单list
     * @param originalExpectHeader 预期结果表头
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject resetComponentExpectHeader(List<TestFormDto> formList, JSONObject originalExpectHeader) {

        JSONObject targetHeader = new JSONObject();
        if (CollectionUtils.isEmpty(formList)) {
            return targetHeader;
        }
        List<String> varPathList = new ArrayList<>();
        for (TestFormDto formDto : formList) {

            varPathList.add(TestTableEnum.EXPECT.getCode() + "." + formDto.getName());
        }

        Set<String> includeList = splitVarPathKey(varPathList);

        Set<String> keySet = originalExpectHeader.keySet();
        for (String key : keySet) {
            JSONArray targetJsonArray = new JSONArray();
            JSONArray jsonArray = originalExpectHeader.getJSONArray(key);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (includeList.contains(jsonObject.getString("index"))) {
                    targetJsonArray.add(jsonObject);
                }
            }
            if (targetJsonArray.size() > 0) {
                targetHeader.put(key, targetJsonArray);
            }

        }

        return targetHeader;

    }

    /**
     * 拆分变量
     *
     * @param varPathList 变量路径list
     * @return Set
     */
    public static Set<String> splitVarPathKey(List<String> varPathList) {
        Set<String> list = new HashSet<>();

        for (String index : varPathList) {

            String[] split = index.split("\\.");
            String parentKey = "";
            for (int i = 0; i < split.length; i++) {
                String currentKey = null;
                if (StringUtils.isEmpty(parentKey)) {
                    currentKey = split[i];

                } else {
                    currentKey = parentKey + "." + split[i];

                }
                parentKey = currentKey;

                list.add(currentKey);
            }
        }

        return list;

    }

    /**
     * resetStrategyExpectHeader
     *
     * @param originalExpectHeader 原始预期表头
     * @param dataModel 数据模型
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject resetStrategyExpectHeader(JSONObject originalExpectHeader, Map<String, DomainDataModelTreeDto> dataModel) {

        JSONObject targetExpectHeader = new JSONObject();

        Set<String> expectKeySet = originalExpectHeader.keySet();
        for (String key : expectKeySet) {

            JSONArray jsonArray = originalExpectHeader.getJSONArray(key);
            JSONArray newJsonArr = new JSONArray();
            //判断变量是否存在，不存在则不作为预期结果表头
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String index = jsonObject.getString("index");
                String name = index.substring(index.indexOf(".") + 1);
                if (!dataModel.containsKey(name)) {
                    continue;
                }
                newJsonArr.add(jsonObject);
            }
            if (newJsonArr.size() > 0) {
                targetExpectHeader.put(key, newJsonArr);
            }

        }

        return targetExpectHeader;
    }

}
