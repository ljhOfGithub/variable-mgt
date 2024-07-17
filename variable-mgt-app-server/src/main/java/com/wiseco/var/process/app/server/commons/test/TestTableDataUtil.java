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

import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.test.dto.TestResultDto;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestDetailDataFieldsEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 测试数据表格数据处理工具类
 *
 * @author wangxianli
 * @since 2021/12/30
 */
public class TestTableDataUtil {

    private static final String SPOT = ".";

    /**
     * 拆分表头：获得输入、预期结果
     *
     * @param sourceheader 源头部
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject splitHeader(Map<String, List<Map<String, Object>>> sourceheader) {

        JSONObject targetheader = new JSONObject();

        //非预期结果表头
        JSONObject newheader = new JSONObject();
        //预期结果表头
        JSONObject expectHeader = new JSONObject();

        Set<Map.Entry<String, List<Map<String, Object>>>> headerEntries = sourceheader.entrySet();
        for (Map.Entry<String, List<Map<String, Object>>> entry : headerEntries) {
            String key = entry.getKey();
            List<Map<String, Object>> valueList = entry.getValue();

            //预期结果
            JSONArray expectHeaderList = new JSONArray();

            //非预期结果
            JSONArray headerList = new JSONArray();

            for (Map<String, Object> masterMap : valueList) {
                if (InputExpectTypeEnum.EXPECT.getCode().equals(String.valueOf(masterMap.get("fieldType")))) {
                    expectHeaderList.add(masterMap);
                } else {
                    headerList.add(masterMap);
                }
            }

            //预期结果
            if (!expectHeaderList.isEmpty()) {
                if (key.equals(TestTableEnum.MASTER.getCode())) {
                    expectHeader.put(TestTableEnum.EXPECT.getCode(), expectHeaderList);
                } else {
                    expectHeader.put(key, expectHeaderList);
                }

            }

            //非预期结果
            newheader.put(key, headerList);
        }

        targetheader.put(TestTableEnum.INPUT.getCode(), newheader);

        if (expectHeader.size() > 0) {
            targetheader.put(TestTableEnum.EXPECT.getCode(), expectHeader);
        }

        return targetheader;

    }

    /**
     * 拆分测试集明细数据：获得输入、预期结果
     *
     * @param sourceDataMap 源数据map
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject splitData(JSONObject sourceDataMap) {

        JSONObject returnData = new JSONObject();

        JSONObject inputData = new JSONObject();
        JSONObject inputMasterData = new JSONObject();

        JSONObject expectData = new JSONObject();
        JSONObject expectedMasterData = new JSONObject();
        for (Map.Entry<String, Object> entry : sourceDataMap.entrySet()) {
            if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                JSONObject masterObj = sourceDataMap.getJSONObject(entry.getKey());
                for (Map.Entry<String, Object> objectEntry : masterObj.entrySet()) {
                    if (objectEntry.getKey().startsWith(TestTableEnum.EXPECT.getCode())) {
                        expectedMasterData.put(objectEntry.getKey().replaceFirst(TestTableEnum.EXPECT.getCode() + SPOT, ""), objectEntry.getValue());
                    } else {
                        inputMasterData.put(objectEntry.getKey(), objectEntry.getValue());
                    }
                }
                if (expectedMasterData.size() > 0) {
                    expectedMasterData.put("id", masterObj.get("id"));
                    expectedMasterData.put("parentId", masterObj.get("parentId"));
                }

            } else if (entry.getKey().startsWith(TestTableEnum.EXPECT.getCode())) {
                JSONArray jsonArray = sourceDataMap.getJSONArray(entry.getKey());
                JSONArray expectNewArray = new JSONArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject expectNewData = new JSONObject();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    for (Map.Entry<String, Object> objectEntry : jsonObject.entrySet()) {
                        if (objectEntry.getKey().startsWith(TestTableEnum.EXPECT.getCode())) {
                            expectNewData.put(objectEntry.getKey().replaceFirst(TestTableEnum.EXPECT.getCode() + SPOT, ""), objectEntry.getValue());
                        } else {
                            expectNewData.put(objectEntry.getKey(), objectEntry.getValue());
                        }
                    }
                    expectNewArray.add(expectNewData);
                }
                expectData.put(entry.getKey().replaceFirst(TestTableEnum.EXPECT.getCode() + SPOT, ""), expectNewArray);
            } else {
                inputData.put(entry.getKey(), entry.getValue());
            }
        }
        if (inputMasterData.size() > 0) {
            inputData.put(TestTableEnum.MASTER.getCode(), inputMasterData);
        }

        if (expectedMasterData.size() > 0) {
            expectData.put(TestTableEnum.MASTER.getCode(), expectedMasterData);
        }

        if (inputData.size() > 0) {
            returnData.put(TestTableEnum.INPUT.getCode(), inputData);
        }
        if (expectData.size() > 0) {
            returnData.put(TestTableEnum.EXPECT.getCode(), expectData);
        }

        return returnData;
    }

    /**
     * 从原始数据中提取预期结果数据
     *
     * @param sourceDataMap 源数据map
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject getExpectData(JSONObject sourceDataMap) {

        JSONObject expectedMapData = new JSONObject();
        JSONObject expectedMasterData = new JSONObject();
        for (Map.Entry<String, Object> entry : sourceDataMap.entrySet()) {
            if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                JSONObject masterObj = sourceDataMap.getJSONObject(entry.getKey());
                for (Map.Entry<String, Object> objectEntry : masterObj.entrySet()) {
                    if (objectEntry.getKey().startsWith(TestTableEnum.EXPECT.getCode())) {
                        expectedMasterData.put(objectEntry.getKey(), objectEntry.getValue());
                    }
                }
            } else if (entry.getKey().startsWith(TestTableEnum.EXPECT.getCode())) {
                expectedMapData.put(entry.getKey(), entry.getValue());
            }
        }
        if (expectedMasterData.size() > 0) {
            expectedMapData.put(TestTableEnum.EXPECT.getCode(), expectedMasterData);
        }

        return expectedMapData;
    }

    /**
     * 合并表头：输入、预期结果、实际结果
     *
     * @param inputHeader  输入
     * @param expectHeader 预期结果
     * @param resultHeader 结果头部
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject mergeHeader(JSONObject inputHeader, JSONObject expectHeader, JSONObject resultHeader) {
        // 1.定义结果
        JSONObject newHeader = new JSONObject();
        // 2.获取预期结果的key
        Set<String> keySet = inputHeader.keySet();
        for (String key : keySet) {
            JSONArray newValueList = new JSONArray();
            newValueList.addAll(inputHeader.getJSONArray(key));
            newHeader.put(key, newValueList);
        }
        // 3.预期结果
        if (expectHeader.size() > 0) {
            JSONArray maserList = new JSONArray();
            if (newHeader.containsKey(TestTableEnum.MASTER.getCode())) {
                maserList = newHeader.getJSONArray(TestTableEnum.MASTER.getCode());
            }
            if (expectHeader.containsKey(TestTableEnum.EXPECT.getCode())) {
                JSONArray expectMasterList = expectHeader.getJSONArray(TestTableEnum.EXPECT.getCode());
                maserList.addAll(expectMasterList);
            }
            newHeader.put(TestTableEnum.MASTER.getCode(), maserList);
            Set<String> expectKeySet = expectHeader.keySet();
            for (String key : expectKeySet) {

                if (!key.equals(TestTableEnum.EXPECT.getCode()) && key.startsWith(TestTableEnum.EXPECT.getCode())) {
                    newHeader.put(key, expectHeader.getJSONArray(key));

                }
            }

        }
        // 4.实际结果
        if (resultHeader.size() > 0) {

            JSONArray maserList = new JSONArray();
            if (newHeader.containsKey(TestTableEnum.MASTER.getCode())) {
                maserList = newHeader.getJSONArray(TestTableEnum.MASTER.getCode());
            }
            if (resultHeader.containsKey(TestTableEnum.RESULTS.getCode())) {
                JSONArray resultMasterList = resultHeader.getJSONArray(TestTableEnum.RESULTS.getCode());
                maserList.addAll(resultMasterList);
            }
            newHeader.put(TestTableEnum.MASTER.getCode(), maserList);

            Set<String> resultKeySet = resultHeader.keySet();
            for (String key : resultKeySet) {

                if (!key.equals(TestTableEnum.RESULTS.getCode())) {
                    newHeader.put(key, resultHeader.getJSONArray(key));
                }
            }
        }
        // 5.过滤(保留)
        return newHeader;
    }

    /**
     * 过滤新的header
     * @param newHeader 新的header
     * @return 更新的header
     */
    public static JSONObject filterNewHeader(JSONObject newHeader) {
        // 1.先去掉master,获取JSONArray
        JSONArray jsonArray = newHeader.getJSONArray(MagicStrings.MASTER);
        JSONObject result = new JSONObject();
        // 2.使用Set集合保存已经出现过的index,然后做筛选
        Set<String> set = new HashSet<>();
        for (int i = 0; i < jsonArray.size();) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 2.1 如果index中包含了results. 则这是一个输出结果
            if (jsonObject.get(MagicStrings.INDEX).toString().contains(MagicStrings.RESULT_DOT)) {
                if (set.contains(jsonObject.get(MagicStrings.INDEX).toString().substring(MagicStrings.RESULT_DOT.length()))) {
                    jsonArray.remove(i);
                    continue;
                }
            } else {
                // 2.2 否则,如果set里面包含了已经出现的index对应的value, 则也删除它
                if (set.contains(jsonObject.get(MagicStrings.INDEX).toString())) {
                    jsonArray.remove(i);
                    continue;
                }
            }
            set.add(jsonObject.get(MagicStrings.INDEX).toString());
            i++;
        }
        // 3.返回结果
        result.put(MagicStrings.MASTER, jsonArray);
        return result;
    }

    /**
     * 将json数据转换成全路径的数据和表头
     *
     * @param id                 行号
     * @param jsonObject         当前行结果
     * @param dataModelHeaderDto 数据模型表头
     * @return JSONObject
     */
    public static JSONObject transHeaderValueMap(String id, JSONObject jsonObject, JSONObject dataModelHeaderDto) {

        //数据
        JSONObject dataMap = transValuePathMap(id, jsonObject);

        //表头
        JSONObject headerMap = buildHeaderMap(dataMap, dataModelHeaderDto);

        JSONObject ret = new JSONObject();
        ret.put(TestHeaderValueEnum.HEADER.getCode(), headerMap);
        ret.put(TestHeaderValueEnum.VALUE.getCode(), dataMap);
        return ret;
    }

    /**
     * 将json数据转换成全路径的数据
     *
     * @param dataId     单条数据在数据集中的序号
     * @param jsonObject 输入 JSON 数据
     * @return JSONObject
     */
    public static JSONObject transValuePathMap(String dataId, JSONObject jsonObject) {

        //数据
        JSONObject dataKeyPathMap = new JSONObject();
        buildKeyPath(jsonObject, dataKeyPathMap, "");

        JSONObject dataMap = new JSONObject();
        JSONObject resultsMap = buildResultsMap(dataKeyPathMap, dataMap, dataId);
        resultsMap.put("id", dataId);

        dataMap.put(TestTableEnum.RESULTS.getCode(), resultsMap);

        return dataMap;
    }

    /**
     * 将JSONObject中key替换为全路径
     *
     * @param jsonObject json对象
     * @param dataMap dataMap
     * @param parentKey 父key
     */
    private static void buildKeyPath(JSONObject jsonObject, JSONObject dataMap, String parentKey) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String currentKey = "";
            if (!StringUtils.isEmpty(parentKey)) {
                currentKey = parentKey + SPOT + entry.getKey();
            } else {
                currentKey = entry.getKey();
            }

            Object value = entry.getValue();

            if (value instanceof JSONObject) {

                buildKeyPath((JSONObject) value, dataMap, currentKey);

            } else if (value instanceof JSONArray) {
                JSONArray valueArr = (JSONArray) value;
                boolean isObjectArr = true;

                for (int i = 0; i < valueArr.size(); i++) {

                    Object tmpObj = valueArr.get(i);
                    if (!(tmpObj instanceof JSONObject)) {
                        isObjectArr = false;
                        break;
                    }

                }
                if (!isObjectArr) {

                    dataMap.put(currentKey, org.apache.commons.lang3.StringUtils.join(valueArr.iterator(), ","));
                } else {
                    JSONArray jsonArray = new JSONArray();

                    dataMap.put(currentKey, jsonArray);
                    for (int i = 0; i < valueArr.size(); i++) {
                        JSONObject dataArrMap = new JSONObject();
                        buildKeyPath(valueArr.getJSONObject(i), dataArrMap, currentKey);
                        jsonArray.add(dataArrMap);
                    }
                }

            } else {
                if (null == value) {
                    dataMap.put(currentKey, "");
                } else {
                    if (value.getClass().getFields().length > 0) {
                        if (value.getClass() == Double.class) {
                            dataMap.put(currentKey, new BigDecimal(String.valueOf(value)).toPlainString());
                        } else {
                            dataMap.put(currentKey, String.valueOf(value));
                        }
                    } else {
                        dataMap.put(currentKey, "");
                    }
                }
            }
        }
    }

    /**
     * @param jsonObject
     * @param dataMap
     * @param parentId
     * @return JSONObject
     */
    private static JSONObject buildResultsMap(JSONObject jsonObject, JSONObject dataMap, String parentId) {
        JSONObject currentMap = new JSONObject();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONArray) {
                currentMap.put(entry.getKey(), "...");

                JSONArray valueArr = (JSONArray) value;
                JSONArray jsonArray = new JSONArray();
                int startId = 0;
                if (dataMap.containsKey(entry.getKey())) {
                    jsonArray = dataMap.getJSONArray(entry.getKey());
                    startId = jsonArray.size();
                }
                dataMap.put(entry.getKey(), jsonArray);
                for (int i = 0; i < valueArr.size(); i++) {
                    startId++;
                    JSONObject jsonObject1 = buildResultsMap(valueArr.getJSONObject(i), dataMap, parentId + "_" + startId);

                    jsonObject1.put("id", parentId + "_" + startId);
                    jsonObject1.put("parentId", parentId);
                    jsonArray.add(jsonObject1);
                }

            } else {
                currentMap.put(entry.getKey(), value);
            }
        }
        return currentMap;

    }

    private static JSONObject buildHeaderMap(JSONObject dataMap, JSONObject dataModelHeaderDto) {
        // 新建空表头对象
        JSONObject headerMap = new JSONObject();

        Set<String> keySet = dataMap.keySet();
        for (String key : keySet) {
            //获取数组中的jsonObject并集
            JSONObject jsonObjectTmp = new JSONObject();

            if (key.equals(TestTableEnum.RESULTS.getCode())) {
                jsonObjectTmp.putAll(dataMap.getJSONObject(key));
            } else {
                JSONArray jsonArray = dataMap.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObjectTmp.putAll(jsonObject);

                }

            }

            //组装表头数据
            JSONArray headerList = new JSONArray();
            Set<String> subKeySet = jsonObjectTmp.keySet();
            for (String subKey : subKeySet) {
                if ("id".equals(subKey) || "parentId".equals(subKey)) {
                    // 跳过 ID (测试结果序号) 和父级 ID (对象类型数据)
                    continue;
                }
                if (!dataModelHeaderDto.containsKey(subKey)) {
                    continue;
                }
                JSONObject originalJsonObject = dataModelHeaderDto.getJSONObject(subKey);
                JSONObject targetJsonObject = JSONObject.parseObject(originalJsonObject.toJSONString());
                targetJsonObject.put("index", TestTableEnum.RESULTS.getCode() + SPOT + originalJsonObject.getString("index"));
                targetJsonObject.put("fieldType", InputExpectTypeEnum.RESULTS.getCode());
                headerList.add(targetJsonObject);
            }
            if (key.equals(TestTableEnum.RESULTS.getCode())) {
                headerMap.put(key, headerList);
            } else {
                headerMap.put(TestTableEnum.RESULTS.getCode() + SPOT + key, headerList);
            }

        }

        return headerMap;
    }

    /**
     * 重置测试数据明细ID
     *
     * @param data JSON数据
     * @param newId 新的Id
     */
    public static void resetIdByJsonData(JSONObject data, int newId) {
        if (data == null || data.size() == 0) {
            return;
        }
        Set<String> keySet = data.keySet();
        for (String key : keySet) {
            if (key.equals(TestTableEnum.MASTER.getCode())) {
                JSONObject jsonObject = data.getJSONObject(key);
                jsonObject.put("id", newId);
            } else {
                JSONArray jsonArray = data.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String tmpSubString = id.substring(id.indexOf("_"));

                    jsonObject.put("id", newId + tmpSubString);

                    String parentId = jsonObject.getString("parentId");
                    if (parentId.contains("_")) {
                        tmpSubString = parentId.substring(parentId.indexOf("_"));
                        jsonObject.put("parentId", newId + tmpSubString);
                    } else {
                        jsonObject.put("parentId", newId);
                    }
                }
            }
        }
    }

    /**
     * 加工测试数据表头、删除多余字段
     *
     * @param jsonObject JSON对象
     */
    public static void resetTestDetailHeader(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keySet().iterator();
        while (iterator.hasNext()) {
            JSONArray objects = jsonObject.getJSONArray(iterator.next());
            for (int i = 0; i < objects.size(); i++) {
                JSONObject subJsonObj = objects.getJSONObject(i);

                Iterator<String> subIterator = subJsonObj.keySet().iterator();
                while (subIterator.hasNext()) {
                    String subKey = subIterator.next();
                    TestDetailDataFieldsEnum code = TestDetailDataFieldsEnum.getCode(subKey);
                    if (code == null) {
                        subIterator.remove();

                    }
                }

            }
        }
    }

    /**
     * 加工测试数据明细、删除多余字段
     *
     * @param jsonObject JSON对象
     */
    public static void resetTestDetailData(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TestTableEnum.MASTER.getCode().equals(key)) {
                continue;
            }
            JSONArray objects = jsonObject.getJSONArray(key);
            for (int i = 0; i < objects.size(); i++) {
                JSONObject subJsonObj = objects.getJSONObject(i);
                Iterator<String> subIterator = subJsonObj.keySet().iterator();
                while (subIterator.hasNext()) {
                    String subKey = subIterator.next();
                    if ("index".equals(subKey)) {
                        subIterator.remove();

                    }
                }

            }

        }
    }

    /*  */

    /**
     * 获取测试数据明细ID
     */
    /*
    public static int getIdByJsonData(JSONObject data) {
     int id = 0;
     Set<String> keySet = data.keySet();
     for (String key : keySet) {
         if (key.equals(TestTableEnum.MASTER.getCode())) {
             JSONObject jsonObject = data.getJSONObject(key);
             id = Integer.parseInt(jsonObject.getString("id"));
         }
     }

     return id;
    }*/

    /**
     * 合并数据：输入数据、预期结果、实际输出结果、原始结果
     *
     * @param dataId        测试数据集单条数据序号
     * @param inputContent  测试结果 输入内容
     * @param expectContent 测试结果 预期内容
     * @return JSONObject
     */
    public static JSONObject mergeData(Integer dataId, String inputContent, String expectContent) {
        // 获取并转换输入内容
        JSONObject inputJsonData = new JSONObject();
        if (!StringUtils.isEmpty(inputContent)) {
            inputJsonData = JSONObject.parseObject(inputContent);
        }
        // 获取并转换预期内容
        JSONObject expectJsonData = new JSONObject();
        if (!StringUtils.isEmpty(expectContent)) {
            expectJsonData = JSONObject.parseObject(expectContent);
        }

        JSONObject resultJson = new JSONObject();
        if (inputJsonData != null && inputJsonData.size() > 0) {

            resultJson.putAll(inputJsonData);
        }

        if (expectJsonData != null && expectJsonData.size() > 0) {
            JSONObject expectResultJson = jsonObjectKeyAddToPrefix(expectJsonData, TestTableEnum.EXPECT.getCode());

            if (!resultJson.containsKey(TestTableEnum.MASTER.getCode())) {
                resultJson.putAll(expectResultJson);
            } else {
                for (Map.Entry<String, Object> entry : expectResultJson.entrySet()) {
                    if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                        resultJson.getJSONObject(entry.getKey()).putAll(expectResultJson.getJSONObject(entry.getKey()));

                    } else {
                        resultJson.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        //考虑输入和输出都没填写的情况
        if (resultJson.size() == 0) {
            JSONObject masterData = new JSONObject();
            masterData.put("id", dataId);
            resultJson.put(TestTableEnum.MASTER.getCode(), masterData);
        }

        return resultJson;
    }

    /**
     * 合并数据：输入数据、预期结果、实际输出结果、原始结果
     *
     * @param testResultDto 测试结果集
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject mergeData(TestResultDto testResultDto) {
        JSONObject resultJson = mergeData(testResultDto.getDataId(), testResultDto.getInputContent(), testResultDto.getExpectContent());

        if (!StringUtils.isEmpty(testResultDto.getResultsContent())) {
            JSONObject resultsJsonData = JSONObject.parseObject(testResultDto.getResultsContent());
            if (resultsJsonData != null && resultsJsonData.size() > 0) {
                JSONObject outputResultJson = jsonObjectKeyAddToPrefix(resultsJsonData, TestTableEnum.RESULTS.getCode());
                if (!resultJson.containsKey(TestTableEnum.MASTER.getCode())) {
                    resultJson.putAll(outputResultJson);
                } else {
                    for (Map.Entry<String, Object> entry : outputResultJson.entrySet()) {
                        if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                            resultJson.getJSONObject(entry.getKey()).putAll(outputResultJson.getJSONObject(entry.getKey()));

                        } else {
                            resultJson.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }

        JSONObject resultsObject = new JSONObject();
        // 测试批号
        resultsObject.put("batchNo", testResultDto.getBatchNo());
        // 策略, 组件测试序列号
        resultsObject.put("testSerialNo", testResultDto.getTestSerialNo());
        //执行状态
        resultsObject.put("executeStatus", testResultDto.getExecuteStatus());
        //对比状态
        resultsObject.put("resultStatus", testResultDto.getResultStatus());

        if (!StringUtils.isEmpty(testResultDto.getExceptionMsg())) {
            resultsObject.put("exceptionMsg", testResultDto.getExceptionMsg());
        }

        //原始输入输出
        if (!StringUtils.isEmpty(testResultDto.getOriginalContent())) {

            JSONObject originalJsonData = JSONObject.parseObject(testResultDto.getOriginalContent());
            if (originalJsonData != null && originalJsonData.size() > 0) {

                resultsObject.put("response", originalJsonData);
            }
        }

        if (!StringUtils.isEmpty(testResultDto.getComparisonContent())) {
            JSONObject comparisonJsonData = JSONObject.parseObject(testResultDto.getComparisonContent());
            if (comparisonJsonData != null && comparisonJsonData.size() > 0) {
                resultsObject.putAll(comparisonJsonData);
            }
        }
        resultJson.put(TestTableEnum.RESULTS.getCode(), resultsObject);

        return resultJson;
    }

    /**
     * 根据预期结果表头，组装预期结果数据
     *
     * @param expectData 期望的数据
     * @param expectHeader 期望的头部
     * @return JSONObject
     */
    public static JSONObject fillExpectDataByHeader(JSONObject expectData, JSONObject expectHeader) {
        JSONObject targetExpectData = new JSONObject();
        if (expectHeader.size() == 0) {
            return targetExpectData;
        }
        Set<String> keySet = expectHeader.keySet();
        for (String key : keySet) {
            JSONArray headerArray = expectHeader.getJSONArray(key);
            if (key.equals(TestTableEnum.EXPECT.getCode())) {
                JSONObject masterData = new JSONObject();
                JSONObject expectMasterData = new JSONObject();
                if (expectData.containsKey(TestTableEnum.MASTER.getCode())) {
                    expectMasterData = expectData.getJSONObject(TestTableEnum.MASTER.getCode());
                }
                for (int i = 0; i < headerArray.size(); i++) {
                    JSONObject headerJsonObject = headerArray.getJSONObject(i);
                    String index = headerJsonObject.getString("index");
                    if (expectMasterData.containsKey(index)) {
                        if (headerJsonObject.getInteger("isArr").equals(NumberUtils.INTEGER_ONE)) {
                            //数组
                            String string = expectMasterData.getString(index);
                            String[] split = string.split(",");
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.addAll(Arrays.asList(split));
                            masterData.put(index, jsonArray);
                        } else {
                            masterData.put(index, expectMasterData.get(index));
                        }
                    } else {
                        masterData.put(index, "");
                    }
                }
                targetExpectData.put(TestTableEnum.MASTER.getCode(), masterData);

            } else {
                if (!expectData.containsKey(key)) {
                    targetExpectData.put(key, new JSONArray());
                } else {
                    JSONArray dataArray = expectData.getJSONArray(key);
                    JSONArray targetDataArray = new JSONArray();
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject dataJsonObject = dataArray.getJSONObject(i);
                        JSONObject subData = new JSONObject();
                        for (int j = 0; j < headerArray.size(); j++) {
                            JSONObject headerJsonObject = headerArray.getJSONObject(j);
                            String index = headerJsonObject.getString("index");

                            if (dataJsonObject.containsKey(index)) {
                                if (headerJsonObject.getInteger("isArr").equals(NumberUtils.INTEGER_ONE)) {
                                    //数组
                                    String string = dataJsonObject.getString(index);
                                    String[] split = string.split(",");
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray.addAll(Arrays.asList(split));
                                    subData.put(index, jsonArray);
                                } else {
                                    subData.put(index, dataJsonObject.get(index));
                                }

                            } else {
                                subData.put(index, "");
                            }
                        }
                        subData.put("id", dataJsonObject.get("id"));
                        subData.put("parentId", dataJsonObject.get("parentId"));
                        targetDataArray.add(subData);
                    }
                    targetExpectData.put(key, targetDataArray);
                }
            }
        }
        return targetExpectData;
    }


    /**
     * 合并数据追加前缀标识：预期结果、实际结果
     *
     * @param originalJsonData 原始的JSON数据
     * @param prefix 前缀
     * @return com.wisecotech.json.JSONObject
     */
    public static JSONObject jsonObjectKeyAddToPrefix(JSONObject originalJsonData, String prefix) {
        JSONObject targetJsonData = new JSONObject();
        for (Map.Entry<String, Object> entry : originalJsonData.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                if (value.getClass().getFields().length == 0) {
                    continue;
                }
                JSONObject targetSingleObject = singleJsonObjectPrefix((JSONObject) value, prefix);
                targetJsonData.put(entry.getKey(), targetSingleObject);
            } else {
                JSONArray valueArr = (JSONArray) value;
                if (valueArr == null || valueArr.size() == 0) {
                    continue;
                }
                String index = prefix + "." + entry.getKey();
                //数组处理
                JSONArray originalJsonArray = originalJsonData.getJSONArray(entry.getKey());
                JSONArray targetSingleObjectArray = new JSONArray();
                for (int i = 0; i < originalJsonArray.size(); i++) {
                    JSONObject targetSingleObject = singleJsonObjectPrefix(originalJsonArray.getJSONObject(i), prefix);
                    targetSingleObjectArray.add(targetSingleObject);
                }
                targetJsonData.put(index, targetSingleObjectArray);
            }
        }
        return targetJsonData;

    }

    private static JSONObject singleJsonObjectPrefix(JSONObject originalSingleObject, String prefix) {
        JSONObject targetSingleObject = new JSONObject();
        for (Map.Entry<String, Object> entry : originalSingleObject.entrySet()) {
            if (StringUtils.isEmpty(originalSingleObject.getString(entry.getKey()))) {
                continue;
            }
            if ("id".equals(entry.getKey()) || "parentId".equals(entry.getKey())) {
                targetSingleObject.put(entry.getKey(), entry.getValue());
            } else {
                String index = prefix + "." + entry.getKey();
                targetSingleObject.put(index, entry.getValue());
            }
        }
        return targetSingleObject;
    }

}
