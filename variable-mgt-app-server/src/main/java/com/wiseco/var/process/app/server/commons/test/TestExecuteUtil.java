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

import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModelArrEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.test.TestDetailDataFieldsEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 测试执行相关工具类
 *
 * @author wangxianli
 * @since 2022/2/16
 */
@Slf4j
public class TestExecuteUtil {

    private static final String RESULT = "result";
    private static final String DIFF_RESULT = "diffResult";
    private static final String SPOT = ".";
    private static final String FIELD_ID = "id";
    private static final String FIELD_PARENT_ID = "parentId";
    private static final String FIELD_INDEX = "index";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_IS_ARR = "isArr";
    private static final String FIELD_VALUE = "value";
    private static final String DOUBLE_LINE = "--";

    /**
     * 加工原始数据,匹配最新的组件字段
     *
     * @param sourceJson 源json
     * @param inputHeader 输入的头部
     * @return JSONObject JSON对象
     */
    public static JSONObject filterUselessFields(JSONObject sourceJson, JSONObject inputHeader) {

        //提取表头索引index
        Set<String> headerIndexMap = getHeaderIndexMap(inputHeader);
        if (inputHeader.size() == 0) {
            headerIndexMap.add(TestTableEnum.MASTER.getCode());
        }

        //过滤表头不存在的数据
        Iterator<String> sourceJsonIterator = sourceJson.keySet().iterator();

        while (sourceJsonIterator.hasNext()) {
            String key = sourceJsonIterator.next();
            if (!headerIndexMap.contains(key)) {
                sourceJsonIterator.remove();
                continue;
            }
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                removeJsonData(sourceJson.getJSONObject(key), headerIndexMap);
            } else {

                JSONArray values = sourceJson.getJSONArray(key);
                for (int i = 0; i < values.size(); i++) {
                    removeJsonData(values.getJSONObject(i), headerIndexMap);
                }
            }
        }

        return sourceJson;

    }

    /**
     * 提取表头索引index
     *
     * @param inputHeader
     * @return Set<String>
     */
    private static Set<String> getHeaderIndexMap(JSONObject inputHeader) {

        Set<String> headerIndexSet = new HashSet<>();
        Set<String> inputHeaderSet = inputHeader.keySet();
        for (String key : inputHeaderSet) {
            headerIndexSet.add(key);
            JSONArray array = inputHeader.getJSONArray(key);
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                headerIndexSet.add(jsonObject.getString(FIELD_INDEX));
            }

        }
        return headerIndexSet;
    }

    private static void removeJsonData(JSONObject jsonObject, Set<String> headerIndexMap) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            if (FIELD_ID.equals(entry.getKey()) || FIELD_PARENT_ID.equals(entry.getKey())) {
                continue;
            }

            if (!headerIndexMap.contains(entry.getKey())) {
                log.info("headerIndexMap不存在key!");
            }

            if (jsonObject.containsKey(entry.getKey()) && jsonObject.get(entry.getKey()) == null) {
                log.info("jsonObject不存在key!");
            }
        }
    }

    /**
     * 将源数据转换JsonObject
     *
     * @param sourceJsonObj 移除本次测试不使用字段的结构化测试数据, 为带有以下 key 的 JSONObject:
     *                      <p>"master" - 根对象及其子对象属性, "a.b.c.d" 对象类型数组变量路径</p>
     * @param strComVarMap  组件使用变量信息 Map, key: 变量路径, value: 组件使用变量信息
     * @return 决策引擎读取的输入测试数据 (与 REST 调用入参结构类似)
     */
    public static JSONObject transformJsonObject(JSONObject sourceJsonObj, Map<String, TestFormDto> strComVarMap) {
        JSONObject targetJsonObject = new JSONObject();
        if (sourceJsonObj.size() == 0) {
            return targetJsonObject;
        }
        Map<String, List<Map<String, Object>>> sourceJsonMapList = transferJsonToMapList(sourceJsonObj);

        Map<String, Object> masterMap = sourceJsonMapList.get(TestTableEnum.MASTER.getCode()).get(0);

        JSONObject retMap = transferMapToFormJsonObject(masterMap, sourceJsonMapList, strComVarMap);
        log.info("transformJsonObject:{}", retMap.toJSONString());

        Map<String, String> nodeIdMap = new HashMap<>(MagicNumbers.TEN);
        Map<String, Boolean> arrayFlagMap = new HashMap<>(MagicNumbers.TEN);
        buildTargetJsonObject(retMap, targetJsonObject, null, nodeIdMap, arrayFlagMap);
        //删除空值
        removeJsonEmptyValue(targetJsonObject);
        return targetJsonObject;
    }

    /**
     * transferJsonToMapList
     *
     * @param sourceJsonObj 入参
     * @return Map
     */
    public static Map<String, List<Map<String, Object>>> transferJsonToMapList(JSONObject sourceJsonObj) {
        Map<String, List<Map<String, Object>>> sourceMapList = new HashMap<>(MagicNumbers.TEN);
        for (Map.Entry<String, Object> entry : sourceJsonObj.entrySet()) {
            List<Map<String, Object>> list = new ArrayList<>();
            if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                Map<String, Object> masterMap = (Map<String, Object>) entry.getValue();
                list.add(masterMap);
            } else {
                list = (List<Map<String, Object>>) entry.getValue();
            }

            sourceMapList.put(entry.getKey(), list);
        }
        return sourceMapList;
    }

    /**
     * 递归组合数据，输入数据不含参数
     *
     * @param masterMap
     * @param sourceJsonObject
     * @param strComVarMap
     * @return JSONObject
     */
    private static JSONObject transferMapToFormJsonObject(Map<String, Object> masterMap, Map<String, List<Map<String, Object>>> sourceJsonObject,
                                                          Map<String, TestFormDto> strComVarMap) {
        JSONObject mapResult = new JSONObject(true);
        //按key中含点的数量升序
        Map<String, Object> targetMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                return StringUtils.countOccurrencesOf(key1, ".") >= StringUtils.countOccurrencesOf(key2, ".") ? 1 : MagicNumbers.MINUS_INT_1;
            }
        });
        targetMap.putAll(masterMap);
        //组合json数据
        Set<Map.Entry<String, Object>> masterMapEntries = targetMap.entrySet();
        //处理基础类型情况
        for (Map.Entry<String, Object> masterMapEntry : masterMapEntries) {
            String key = masterMapEntry.getKey();
            Object value = masterMapEntry.getValue();
            if (sourceJsonObject.containsKey(key)) {
                continue;
            }
            if (!key.contains(SPOT) || !strComVarMap.containsKey(key)) {
                continue;
            }
            TestFormDto testFormDto = strComVarMap.get(key);
            JSONObject retTmpMap = new JSONObject();
            retTmpMap.put(FIELD_NAME, key);
            retTmpMap.put(FIELD_IS_ARR, testFormDto.getIsArr());
            retTmpMap.put(FIELD_TYPE, testFormDto.getType());
            retTmpMap.put(FIELD_VALUE, value);
            mapResult.put(key, retTmpMap);
        }
        //处理数组情况
        for (Map.Entry<String, Object> masterMapEntry : masterMapEntries) {
            String key = masterMapEntry.getKey();
            if (!sourceJsonObject.containsKey(key)) {
                continue;
            }
            JSONObject retTmpMap = new JSONObject();
            retTmpMap.put(FIELD_NAME, key);
            retTmpMap.put(FIELD_IS_ARR, DomainModelArrEnum.YES.getCode());
            retTmpMap.put(FIELD_TYPE, DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            List<JSONObject> valueList = new ArrayList<>();
            List<Map<String, Object>> maps = sourceJsonObject.get(key);
            //提取所有key
            Map<String, Object> tmpMap = new HashMap<>(MagicNumbers.SIXTEEN);
            for (Map<String, Object> map : maps) {
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    if ("...".equals(String.valueOf(entry.getValue()))) {
                        tmpMap.put(entry.getKey(), entry.getValue());
                    } else {
                        tmpMap.put(entry.getKey(), "");
                    }
                }
            }
            //补充同一对象下的参数
            for (Map<String, Object> map : maps) {
                Set<Map.Entry<String, Object>> entries = tmpMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    if (!map.containsKey(entry.getKey())) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            for (Map<String, Object> map : maps) {
                if (String.valueOf(masterMap.get(FIELD_ID)).equals(String.valueOf(map.get(FIELD_PARENT_ID)))) {
                    JSONObject jsonObject = transferMapToFormJsonObject(map, sourceJsonObject, strComVarMap);
                    if (jsonObject.size() == 0) {
                        continue;
                    }
                    valueList.add(jsonObject);
                }
            }
            retTmpMap.put(FIELD_VALUE, valueList);
            if (!CollectionUtils.isEmpty(valueList)) {
                mapResult.put(key, retTmpMap);
            }
        }
        return mapResult;
    }

    /**
     * 处理参数情况
     *
     * @param targetJsonObject 目标JSON对象
     * @param strComVarMap 字符串比较的map
     */
    public static void handleParamterJson(JSONObject targetJsonObject, Map<String, TestFormDto> strComVarMap) {

        for (Map.Entry<String, TestFormDto> entry : strComVarMap.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("\\.");

            if (!PositionVarEnum.PARAMETERS.getName().equals(split[0])) {
                continue;
            }

            strComVarMap.put(split[0] + SPOT + split[1], entry.getValue());
        }

        Set<Map.Entry<String, Object>> sonJsonObjectEntrySet = targetJsonObject.entrySet();
        for (Map.Entry<String, Object> sonJsonObjectEntry : sonJsonObjectEntrySet) {
            String entryKey = sonJsonObjectEntry.getKey();
            JSONObject entryValue = (JSONObject) sonJsonObjectEntry.getValue();

            if (!PositionVarEnum.PARAMETERS.getName().equals(entryKey)) {
                continue;
            }

            Set<Map.Entry<String, Object>> sonentryValueObjectEntrySet = entryValue.entrySet();
            for (Map.Entry<String, Object> sonentryValueObjectEntry : sonentryValueObjectEntrySet) {
                String sonEntryKey = sonentryValueObjectEntry.getKey();

                Map<String, Object> tmpJson = new HashMap<>(MagicNumbers.TEN);
                TestFormDto testFormDto = strComVarMap.get(entryKey + SPOT + sonEntryKey);

                tmpJson.put(FIELD_TYPE, testFormDto.getParameterType());
                tmpJson.put(FIELD_IS_ARR, testFormDto.getIsParameterArray());
                tmpJson.put(FIELD_VALUE, sonentryValueObjectEntry.getValue());

                entryValue.put(sonEntryKey, tmpJson);

            }

        }

    }

    /**
     * buildTargetJsonObject
     *
     * @param sourceJsonObject 源JSON对象
     * @param targetJsonObject 目标JSON对象
     * @param currentNodeId 当前结点Id
     * @param nodeIdMap 结点Id的map集合
     * @param arrayFlagMap 数组标志的map集合
     */
    public static void buildTargetJsonObject(JSONObject sourceJsonObject, JSONObject targetJsonObject, String currentNodeId,
                                             Map<String, String> nodeIdMap, Map<String, Boolean> arrayFlagMap) {
        Set<Map.Entry<String, Object>> sonJsonObjectEntrySet = sourceJsonObject.entrySet();
        for (Map.Entry<String, Object> sonJsonObjectEntry : sonJsonObjectEntrySet) {
            String entryKey = sonJsonObjectEntry.getKey();
            JSONObject entryValue = (JSONObject) sonJsonObjectEntry.getValue();
            if (entryValue == null) {
                continue;
            }

            boolean isEntryValueArray = isObjectTypedArray(entryValue);
            arrayFlagMap.put(entryKey, isEntryValueArray);

            if (isEntryValueArray) {
                if (entryValue.get(FIELD_VALUE) == null) {
                    continue;
                }

                Boolean arrayFlag = arrayFlagMap.get(entryKey);
                createJsonNodeIfNeeded(targetJsonObject, entryKey, arrayFlag != null ? arrayFlag : false);

                List<JSONObject> valueArray = (List<JSONObject>) entryValue.get(FIELD_VALUE);

                String name = entryValue.getString("name");
                for (int i = 0; i < valueArray.size(); i++) {
                    JSONObject subValueObj = valueArray.get(i);
                    String uuid = UUID.randomUUID().toString();
                    buildTargetJsonObject(subValueObj, targetJsonObject, uuid, nodeIdMap, arrayFlagMap);

                    if (i != valueArray.size() - 1) {
                        Object jsonObject = getSubJsonArrayByPath(targetJsonObject, name);
                        JSONArray jsonArray;
                        if (jsonObject instanceof JSONArray) {
                             jsonArray = (JSONArray) jsonObject;
                        } else {
                            jsonArray = new JSONArray();
                            jsonArray.add(jsonObject);
                        }
                        jsonArray.add(new JSONObject());
                    }
                }
            } else {
                String parentKey = entryKey.substring(0, entryKey.lastIndexOf(SPOT));
                String currentUuid = nodeIdMap.get(parentKey);
                boolean next = (currentUuid != null && !currentUuid.equals(currentNodeId));
                nodeIdMap.put(parentKey, currentNodeId);

                Boolean arrayFlag = arrayFlagMap.get(parentKey);
                Object jsonNode = createJsonNodeIfNeeded(targetJsonObject, parentKey, arrayFlag != null ? arrayFlag : false);

                String name = entryValue.getString(FIELD_NAME);
                String isArr = entryValue.getString(FIELD_IS_ARR);
                String type = entryValue.getString(FIELD_TYPE);
                Object value = entryValue.get(FIELD_VALUE);

                if (jsonNode instanceof JSONArray) {
                    appendJsonArrayValue((JSONArray) jsonNode, name, value, isArr, type, next);
                } else {
                    appendJsonObjectValue((JSONObject) jsonNode, name, value, isArr, type);
                }
            }
        }
    }

    private static Object getSubJsonArrayByPath(Object targetJsonObject, String name) {
        Object lastObject = targetJsonObject;
        String[] nameArray = name.split("\\.");
        for (int i = 0; i < nameArray.length; i++) {
            if (lastObject instanceof JSONArray) {
                JSONArray lastJsonArray = (JSONArray) lastObject;
                JSONObject lastJsonObject = (JSONObject) lastJsonArray.get(lastJsonArray.size() - 1);
                Object jsonObject = (lastJsonObject).get(nameArray[i]);
                lastObject = jsonObject;
            } else {
                Object jsonObject = ((JSONObject) lastObject).get(nameArray[i]);
                lastObject = jsonObject;
            }
        }
        return lastObject;
    }

    private static void appendJsonObjectValue(JSONObject jsonObject, String key, Object value, String isArr, String type) {
        if (jsonObject == null) {
            return;
        }
        //判断是否是数组
        if (key.contains(SPOT)) {
            key = key.substring(key.lastIndexOf(SPOT) + 1);
        }
        if (DomainModelArrEnum.YES.getCode().equals(isArr)) {
            if (!StringUtils.isEmpty(value)) {
                String[] valueSplit = String.valueOf(value).split(",", MagicNumbers.MINUS_INT_1);
                Object[] objects = new Object[valueSplit.length];
                for (int i = 0; i < valueSplit.length; i++) {

                    objects[i] = handleVarTypeData(type, valueSplit[i]);
                }
                jsonObject.put(key, objects);
            } else {
                jsonObject.put(key, new Object[0]);
            }

        } else {
            value = handleVarTypeData(type, value);
            jsonObject.put(key, value);
        }
    }

    private static Object handleVarTypeData(String type, Object value) {
        //type类型：int,double,boolean,string,date,datetime,object
        String valueString = String.valueOf(value);
        if (StringUtils.isEmpty(valueString)) {
            // 空值: 跳过类型转换
            return null;
        }
        if (valueString.length() == NumberUtils.INTEGER_TWO && valueString.contains(DOUBLE_LINE)) {
            // JSON 双横线值: 跳过类型转换
            return null;
        }
        if (TestValidateUtil.isNumeric(String.valueOf(value)) && DataVariableTypeEnum.INT_TYPE.getMessage().equals(type)) {
            value = Integer.parseInt(valueString);
        } else if (TestValidateUtil.isDouble(String.valueOf(value)) && DataVariableTypeEnum.DOUBLE_TYPE.getMessage().equals(type)) {
            value = Double.parseDouble(valueString);
        } else {
            boolean flag = (valueString.equalsIgnoreCase(Boolean.TRUE.toString()) || valueString.equalsIgnoreCase(Boolean.FALSE.toString()))
                    && DataVariableTypeEnum.BOOLEAN_TYPE.getMessage().equals(type);
            if (flag) {
                value = Boolean.parseBoolean(String.valueOf(value));
            }
        }

        return value;

    }

    private static void appendJsonArrayValue(JSONArray jsonArray, String key, Object value, String isArr, String type, boolean next) {
        if (jsonArray == null) {
            return;
        }
        JSONObject lastJsonObject;
        if (!jsonArray.isEmpty()) {
            lastJsonObject = (JSONObject) jsonArray.get(jsonArray.size() - 1);
            if (next) {
                lastJsonObject = new JSONObject();
                jsonArray.add(lastJsonObject);
            }
        } else {
            lastJsonObject = new JSONObject();
            jsonArray.add(lastJsonObject);
        }
        //判断是否是数组
        if (key.contains(SPOT)) {
            key = key.substring(key.lastIndexOf(SPOT) + 1);
        }
        if (DomainModelArrEnum.YES.getCode().equals(isArr)) {
            if (!StringUtils.isEmpty(value)) {
                String[] valueSplit = String.valueOf(value).split(",",  MagicNumbers.MINUS_INT_1);
                Object[] objects = new Object[valueSplit.length];
                for (int i = 0; i < valueSplit.length; i++) {

                    objects[i] = handleVarTypeData(type, valueSplit[i]);
                }
                lastJsonObject.put(key, objects);
            } else {
                lastJsonObject.put(key, new Object[0]);
            }

        } else {
            value = handleVarTypeData(type, value);
            lastJsonObject.put(key, value);
        }
    }

    /**
     * 按需创建 JSON 节点
     *
     * @param targetJsonObject 目标 JSON 对象
     * @param variablePath     变量路径
     * @param isArray          是否数组
     * @return 创建后的 JSON 节点
     */
    private static Object createJsonNodeIfNeeded(@NonNull Object targetJsonObject, String variablePath, boolean isArray) {
        String[] keyArray = variablePath.split("\\.");
        String currentVariablePath = null;
        Object lastObject = targetJsonObject;
        for (int i = 0; i < keyArray.length; i++) {
            // 分割变量路径, 按层级遍历并添加 JSON 对象
            String key = keyArray[i];
            // 拼接正在遍历的变量路径
            currentVariablePath = Objects.nonNull(currentVariablePath) ? currentVariablePath + StringPool.DOT + key : key;
            if (lastObject instanceof JSONArray) {
                JSONArray lastJsonArray = (JSONArray) lastObject;
                if (!lastJsonArray.isEmpty()) {
                    lastObject = lastJsonArray.get(lastJsonArray.size() - 1);
                } else {
                    JSONObject currentObject = new JSONObject();
                    if (isArray && variablePath.equals(currentVariablePath)) {
                        currentObject.put(key, new JSONArray());
                    } else {
                        currentObject.put(key, new JSONObject());
                    }
                    lastJsonArray.add(currentObject);
                    lastObject = currentObject;
                }
            }
            if (null == ((JSONObject) lastObject).get(key)) {
                // 对象不存在
                Object currentObject;
                if (isArray && variablePath.equals(currentVariablePath)) {
                    currentObject = new JSONArray();
                } else {
                    currentObject = new JSONObject();
                }
                ((JSONObject) lastObject).put(key, currentObject);
                lastObject = currentObject;
            } else {
                lastObject = ((JSONObject) lastObject).get(key);
            }
        }
        return lastObject;
    }

    /**
     * 判断是否属于对象类数组
     *
     * @param jsonElement 结构化数据
     *                    <p>e.g. {
     *                    "name": "rawData.aa.aaa",
     *                    "isArr": "1",
     *                    "type": "object",
     *                    "value": []
     *                    }</p>
     * @return true, 如果当前变量属于对象类型数组
     */
    private static boolean isObjectTypedArray(JSONObject jsonElement) {
        return DomainModelArrEnum.YES.getCode().equals(jsonElement.getString(FIELD_IS_ARR))
                && DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(jsonElement.getString(FIELD_TYPE));
    }

    /**
     * 对比预期结果和实际结果
     *
     * @param expectedMapData 期望的map数据
     * @param outputMapData 输出的map数据
     * @param diffResultMap 差异的map集合
     * @return 是否一致 0-一致，1不一致
     */
    public static int diffExpectRealityData(JSONObject expectedMapData, JSONObject outputMapData, JSONObject diffResultMap) {
        int diffResult = 0;
        if (!outputMapData.containsKey(TestTableEnum.RESULTS.getCode())) {
            diffResult = 1;
            return diffResult;
        }
        Set<String> keySet = expectedMapData.keySet();
        for (String key : keySet) {
            // 根据预期结果 key 进行遍历
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                // 分别获取预期结果和实际输出 JSON 对象
                JSONObject expectJsonObj = expectedMapData.getJSONObject(key);
                JSONObject resultsJsonObj = outputMapData.getJSONObject(TestTableEnum.RESULTS.getCode());
                JSONObject resultMap = diffJsonObject(expectJsonObj, resultsJsonObj);
                diffResultMap.put(TestTableEnum.EXPECT.getCode(), resultMap.getJSONObject(RESULT));
                if ("1".equals(resultMap.getString(DIFF_RESULT))) {
                    diffResult = 1;
                }
            } else {
                String resultKey = key.substring(key.indexOf(".") + 1);
                //预期结果数组为空，并且实际结果不存在，则认为是一致的，不需要比较
                JSONArray expectedJsonArray = expectedMapData.getJSONArray(key);
                if (expectedJsonArray.size() == 0 && !outputMapData.containsKey(resultKey)) {
                    continue;
                }
                if (!outputMapData.containsKey(resultKey)) {
                    diffResult = 1;
                    continue;
                }
                JSONArray resultsJsonArray = outputMapData.getJSONArray(resultKey);
                //预期结果和实际结果都是空数组，也认为是一致的
                if (expectedJsonArray.size() == 0 && resultsJsonArray.size() == 0) {
                    continue;
                }
                if (expectedJsonArray.size() == 0) {
                    diffResult = 1;
                    continue;
                }
                Map<String, List<Object>> expectedDataListMap = expectedJsonArray.stream().collect(Collectors.groupingBy(item -> {
                    JSONObject item1 = (JSONObject) item;
                    return item1.getString(FIELD_PARENT_ID);
                }, HashMap::new, Collectors.toList()));
                //实际结果
                Map<String, List<Object>> resultDataListMap = resultsJsonArray.stream().collect(Collectors.groupingBy(item -> {
                    JSONObject item1 = (JSONObject) item;
                    return item1.getString(FIELD_PARENT_ID);
                }, HashMap::new, Collectors.toList()));
                Set<Map.Entry<String, List<Object>>> entries = expectedDataListMap.entrySet();
                for (Map.Entry<String, List<Object>> entry : entries) {
                    String key1 = entry.getKey();
                    if (!resultDataListMap.containsKey(key1)) {
                        diffResult = 1;
                        continue;
                    }
                    List<Object> resultObjects = resultDataListMap.get(key1);
                    List<Object> expectObjects = entry.getValue();
                    if (expectObjects.size() != resultObjects.size()) {
                        diffResult = 1;
                    } else {
                        for (int i = 0; i < expectObjects.size(); i++) {
                            JSONObject expectJsonObj = (JSONObject) expectObjects.get(i);
                            JSONObject resultJsonObj = (JSONObject) resultObjects.get(i);
                            JSONObject resultMap = diffJsonObject(expectJsonObj, resultJsonObj);
                            //比对结果不一致
                            if ("1".equals(resultMap.getString(DIFF_RESULT))) {
                                diffResult = 1;
                            }
                        }
                    }
                    //实际结果处理
                    JSONArray newJsonArr = new JSONArray();
                    handleActualResult(resultObjects, newJsonArr);
                    diffResultMap.put(key, newJsonArr);
                }
            }
        }
        return diffResult;
    }

    /**
     * 实际结果处理
     *
     * @param resultObjects 结果对象List集合
     * @param newJsonArr    JSON数组
     */
    private static void handleActualResult(List<Object> resultObjects, JSONArray newJsonArr) {
        for (int i = 0; i < resultObjects.size(); i++) {
            JSONObject resultJsonObj = (JSONObject) resultObjects.get(i);
            JSONObject targetResultJsonObj = new JSONObject();
            for (Map.Entry<String, Object> entry : resultJsonObj.entrySet()) {
                if (TestDetailDataFieldsEnum.ID.getCode().equals(entry.getKey()) || TestDetailDataFieldsEnum.PARENT_ID.getCode().equals(entry.getKey())) {
                    continue;
                }
                targetResultJsonObj.put(TestTableEnum.EXPECT.getCode() + "." + entry.getKey(), entry.getValue());
            }
            newJsonArr.add(targetResultJsonObj);
        }
    }

    /**
     * 对比预期结果和实际结果-按JsonObject对象对比
     * @param expectJsonObj  预期结果数据
     * @param resultsJsonObj 实际结果数据
     * @return JSONObject
     */
    private static JSONObject diffJsonObject(JSONObject expectJsonObj, JSONObject resultsJsonObj) {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject resultMap = new JSONObject();
        // 1. 设定比较结果默认值为 "一致": 0
        int diffResult = 0;
        // 2.获取预期结果 JSON 所有 key 的集合
        for (Map.Entry<String, Object> entry : expectJsonObj.entrySet()) {
            if (FIELD_ID.equals(entry.getKey()) || FIELD_PARENT_ID.equals(entry.getKey())) {
                // 2.1 跳过对 ID 和父 ID 字段的比较
                continue;
            }
            // 2.2 去除预期结果 key 的 "expect" 前缀, 以便与实际结果比较
            String resultKey = entry.getKey().substring(entry.getKey().indexOf(".") + 1);
            // 2.3 预期结果为空并实际结果不存在变量名，则认为是一致的(某一个输出的预期结果没填，就不管他)
            if (StringUtils.isEmpty(expectJsonObj.get(entry.getKey()))) {
                continue;
            } else {
                if (!resultsJsonObj.containsKey(resultKey) || StringUtils.isEmpty(resultsJsonObj.get(resultKey))) {
                    // 预期结果的 key 在实际结果中缺失或者实际结果为空: 比较结果设为不一致
                    diffResult = 1;
                    continue;
                }
            }
            Object expectValueObj = entry.getValue();
            // 实际结果
            String resultValue = resultsJsonObj.getString(resultKey);
            if (expectValueObj instanceof JSONArray) {
                JSONArray expectValueArr = (JSONArray) expectValueObj;
                String[] resultValueSplit = resultValue.split(",",  MagicNumbers.MINUS_INT_1);
                if (resultValueSplit.length != expectValueArr.size()) {
                    diffResult = 1;
                    resultMap.put(entry.getKey(), resultValue);
                    continue;
                }
                for (int i = 0; i < expectValueArr.size(); i++) {
                    String eachExpectValue = expectValueArr.getString(i);
                    String eachresultValue = resultValueSplit[i];
                    int diffValueResult = diffValue(eachExpectValue, eachresultValue);
                    if (diffValueResult == 1) {
                        diffResult = 1;
                        break;
                    }
                }
            } else {
                // 预期结果
                String expectValue = expectJsonObj.getString(entry.getKey());

                int diffValueResult = diffValue(expectValue, resultValue);
                if (diffValueResult == 1) {
                    diffResult = 1;
                }
            }
            resultMap.put(entry.getKey(), resultValue);
        }
        returnJsonObj.put(RESULT, resultMap);
        returnJsonObj.put(DIFF_RESULT, diffResult);
        return returnJsonObj;
    }

    /**
     * 对比值
     *
     * @param expectValue
     * @param resultValue
     * @return int
     */
    private static int diffValue(String expectValue, String resultValue) {
        boolean isNumericExpect = TestValidateUtil.isNumeric(expectValue);
        boolean isDoubleExpect = TestValidateUtil.isDouble(expectValue);
        boolean isNumericResult = TestValidateUtil.isNumeric(resultValue);
        boolean isDoubleResult = TestValidateUtil.isDouble(resultValue);
        boolean flag = (isNumericExpect || isDoubleExpect) && (isNumericResult || isDoubleResult);
        int diffResult = 0;
        if (flag) {
            // 预期结果和实际结果均为数值: 转换为 double 比较
            double expectedResult = Double.parseDouble(expectValue);
            double actualResult = Double.parseDouble(resultValue);
            if (BigDecimal.valueOf(expectedResult).compareTo(BigDecimal.valueOf(actualResult)) != 0) {
                diffResult = 1;
            }
        } else {
            // 预期结果和实际结果均为字符串
            if (!resultValue.equals(expectValue)) {
                diffResult = 1;
            }
        }
        return diffResult;
    }

    /**
     * 输入数据追加预期结果
     *
     * @param newJsonData 新的JSON数据
     * @param expectedMapData 期望的map数据
     */
    public static void putExpectData(JSONObject newJsonData, JSONObject expectedMapData) {

        JSONObject master = newJsonData.getJSONObject(TestTableEnum.MASTER.getCode());

        Set<String> keySet = expectedMapData.keySet();
        for (String key : keySet) {
            if (key.equals(TestTableEnum.EXPECT.getCode())) {
                master.putAll(expectedMapData.getJSONObject(TestTableEnum.EXPECT.getCode()));
            } else {
                newJsonData.put(key, expectedMapData.getJSONArray(key));
            }
        }

    }

    /**
     * 输入数据追加实际结果
     *
     * @param newJsonData 新的JSON数据
     * @param resultData 结果数据
     */
    public static void putResultData(JSONObject newJsonData, JSONObject resultData) {

        //追加预期结果和执行结果
        JSONObject master = newJsonData.getJSONObject(TestTableEnum.MASTER.getCode());
        for (Map.Entry<String, Object> entry : resultData.entrySet()) {
            if (entry.getValue().equals(TestTableEnum.RESULTS.getCode())) {
                JSONObject jsonObject = resultData.getJSONObject(entry.getKey());
                for (Map.Entry<String, Object> objectEntry : jsonObject.entrySet()) {
                    if (!"id".equals(objectEntry.getKey())) {
                        master.put(TestTableEnum.RESULTS.getCode() + "." + objectEntry.getKey(), objectEntry.getValue());
                    }
                }
            } else {
                JSONArray newArr = new JSONArray();
                JSONArray jsonArray = resultData.getJSONArray(entry.getKey());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject newJsonObj = new JSONObject();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    for (Map.Entry<String, Object> objectEntry : jsonObject.entrySet()) {
                        if ("id".equals(objectEntry.getKey()) || "parentId".equals(objectEntry.getKey())) {
                            newJsonObj.put(objectEntry.getKey(), objectEntry.getValue());
                        } else {
                            newJsonObj.put(TestTableEnum.RESULTS.getCode() + "." + objectEntry.getKey(), objectEntry.getValue());
                        }
                    }
                    newArr.add(newJsonObj);
                }
                newJsonData.put(TestTableEnum.RESULTS.getCode() + "." + entry.getValue(), newArr);
            }
        }
    }

    /**
     * 转换实际结果
     *
     * @param resultData 结果数据
     * @return JSONObject
     */
    public static JSONObject transferResultData(JSONObject resultData) {

        //追加预期结果和执行结果
        JSONObject retJson = new JSONObject();
        for (Map.Entry<String, Object> entry : resultData.entrySet()) {
            if (entry.getKey().equals(TestTableEnum.RESULTS.getCode())) {
                retJson.put(TestTableEnum.MASTER.getCode(), entry.getValue());

            } else {
                retJson.put(entry.getKey(), entry.getValue());
            }
        }

        return retJson;

    }

    /**
     * 组装实际结果表头
     *
     * @param oldResultHeader 旧结果集的头部
     * @param newResultHeader 新结果集的头部
     * @return JSONObject
     */
    public static JSONObject convertResultHeader(JSONObject oldResultHeader, JSONObject newResultHeader) {
        if (newResultHeader == null || newResultHeader.size() == 0) {
            return oldResultHeader;
        }

        if (oldResultHeader.size() == 0) {

            oldResultHeader.putAll(newResultHeader);
            return oldResultHeader;
        }

        Set<String> keySet = newResultHeader.keySet();
        for (String key : keySet) {
            JSONArray newResultArr = newResultHeader.getJSONArray(key);
            if (!oldResultHeader.containsKey(key)) {
                oldResultHeader.put(key, newResultArr);
            } else {
                putResultHeader(oldResultHeader.getJSONArray(key), newResultArr);
            }

        }
        return oldResultHeader;

    }

    /**
     * 追加表头对象
     *
     * @param oldResultArr
     * @param newResultArr
     */
    private static void putResultHeader(JSONArray oldResultArr, JSONArray newResultArr) {

        JSONObject tmpObj = new JSONObject();

        for (int i = 0; i < oldResultArr.size(); i++) {
            tmpObj.put(oldResultArr.getJSONObject(i).getString("index"), oldResultArr.getJSONObject(i));
        }

        for (int i = 0; i < newResultArr.size(); i++) {
            JSONObject newJsonObject = newResultArr.getJSONObject(i);
            if (!tmpObj.containsKey(newJsonObject.getString("index"))) {
                oldResultArr.add(newJsonObject);
            }

        }

    }

    /**
     * 删除JSONObject中的空对象，空值
     *
     * @param jsonObject
     */
    /*public static void removeJsonEmptyValue(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();

            Object value = jsonObject.get(key);

            if (StringUtils.isEmpty(value)) {
                iterator.remove();

            } else if (value instanceof JSONObject) {
                JSONObject subJsonObject = jsonObject.getJSONObject(key);

                if (subJsonObject.size() == 0) {
                    iterator.remove();
                } else {
                    removeJsonEmptyValue((JSONObject) value);
                }

            } else if (value instanceof JSONArray || value instanceof List) {
                //ArrayList 类型，转成jsonArray
                jsonObject.put(key, JSONArray.parseArray(JSONObject.toJSONString(value)));

                JSONArray subArr = jsonObject.getJSONArray(key);
                if (subArr == null || subArr.size() == 0) {
                    iterator.remove();
                } else {
                    for (int i = 0; i < subArr.size(); i++) {
                        Object subObjVal = subArr.get(i);
                        if (!(subObjVal instanceof JSONObject)) {
                            continue;
                        }
                        JSONObject subJsonObject = subArr.getJSONObject(i);
                        removeJsonEmptyValue(subJsonObject);
                    }
                }


            } else if (value.getClass().getFields().length == 0) {
                iterator.remove();

            }
        }

    }*/

    /**
     * 删除 JSONObject 中的空对象, 空值
     * 解决 "多层空对象嵌套无法彻底删除" 的问题
     *
     * @param object JSON 对象
     * @return 经过处理的 JSON 对象子结构是否为空
     */
    public static boolean removeJsonEmptyValue(Object object) {

        if (StringUtils.isEmpty(object)) {
            // 基本属性类型
            return true;
        } else if (object instanceof Map) {
            // Map 类型 (JSONObject)
            JSONObject jsonObject = (JSONObject) object;
            // 创建迭代器
            Iterator<String> keyIterator = jsonObject.keySet().iterator();
            while (keyIterator.hasNext()) {
                // 遍历 JSONObject 中的 key, 避免 ConcurrentModificationException
                Object value = jsonObject.get(keyIterator.next());
                boolean isPropertyEmpty = removeJsonEmptyValue(value);
                if (isPropertyEmpty) {
                    // JSONObject 属性均为空时, 移除 JSONObject
                    keyIterator.remove();
                }
            }

            return jsonObject.isEmpty();
        } else if (object instanceof List) {
            // List 类型 (JSONArray)
            // 数组是否为空 Flag
            boolean isArrayEmpty = true;

            // 创建迭代器
            Iterator<Object> arrayIterator = ((JSONArray) object).iterator();
            while (arrayIterator.hasNext()) {
                Object subObjVal = arrayIterator.next();
                if (subObjVal instanceof JSONObject) {
                    // 遍历 JSON 数组元素
                    boolean isArrayElementEmpty = removeJsonEmptyValue(subObjVal);
                    isArrayEmpty &= isArrayElementEmpty;
                    if (isArrayElementEmpty) {
                        // 数组元素为空: 移除元素
                        arrayIterator.remove();
                    }
                } else {
                    isArrayEmpty = false;
                    break;
                }

            }

            return isArrayEmpty;
        }

        // 不属于三种类型: 视为非空
        return false;
    }

    /**
     * 排除输入变量
     *
     * @param jsonObject JSON对象
     * @param outputVarList 输出的变量列表
     * @param parentKey 父类key
     */
    public static void excludeUnnecessaryVarPath(JSONObject jsonObject, List<String> outputVarList, String parentKey) {

        if (jsonObject == null || jsonObject.size() == 0) {
            return;
        }
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String currentKey = "";
            if (!StringUtils.isEmpty(parentKey)) {
                currentKey = parentKey + "." + key;
            } else {
                currentKey = key;
            }
            if (outputVarList.contains(currentKey)) {
                continue;
            }

            Object value = entry.getValue();

            if (value instanceof JSONObject) {

                excludeUnnecessaryVarPath((JSONObject) value, outputVarList, currentKey);
            } else if (value instanceof JSONArray) {
                JSONArray valueArr = (JSONArray) value;

                for (int i = 0; i < valueArr.size(); i++) {
                    Object subObjVal = valueArr.get(i);
                    if (subObjVal instanceof JSONObject) {

                        JSONObject subJsonObject = valueArr.getJSONObject(i);

                        removeListElement(subJsonObject, outputVarList, currentKey);

                        excludeUnnecessaryVarPath(subJsonObject, outputVarList, currentKey);
                    }

                }
            }
        }
    }

    /**
     * 响应结果中数组移除输入元素的处理
     *
     * @param subJsonObject 子JSON对象
     * @param outputVarList 输出的列表
     * @param currentKey 当前的key
     */
    public static void removeListElement(JSONObject subJsonObject, List<String> outputVarList, String currentKey) {
        if (subJsonObject == null || subJsonObject.size() == 0 || CollectionUtils.isEmpty(outputVarList)) {
            return;
        }

        Set<String> subKeySet = subJsonObject.keySet();

        boolean isExistKey = false;
        for (String subKey : subKeySet) {
            if (!outputVarList.contains(currentKey + "." + subKey)) {
                isExistKey = true;
                break;
            }
        }
        if (isExistKey) {
            for (String subKey : subKeySet) {
                if (outputVarList.contains(currentKey + "." + subKey)) {
                    outputVarList.removeIf(s -> s.equals(currentKey + "." + subKey));
                }
            }
        }

    }

    /**
     * 判断指定 key 是否为变量路径最后一段
     *
     * @param variablePath 变量路径
     * @param variableName 变量名称
     * @return true, 如果指定 key 为变量路径最后一段
     */
    private static boolean isLastSegmentOfVariablePath(String variablePath, String variableName) {
        // 截取变量路径最后一段 (兼容变量路径无点情况)
        String lastSegmentOfVariablePath = variablePath.substring(variablePath.lastIndexOf(StringPool.DOT) + 1);
        return lastSegmentOfVariablePath.equals(variableName);
    }

}
