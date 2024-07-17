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
package com.wiseco.var.process.app.server.runnable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.decision.engine.var.enums.ServiceRuntimeEnvEnum;
import com.wiseco.decision.engine.var.runtime.context.ServiceContext;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.test.TestExecuteUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableDataUtil;
import com.wiseco.var.process.app.server.controller.vo.input.TestExecuteInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestExecuteResultDto;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.service.dto.TestTaskSourceDataDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import com.wisecotech.json.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 变量执行
 *
 * @author wangxianli
 */
@Slf4j
public class ManifestTestTask {

    private TestTaskSourceDataDto sourceDataDto;

    private TestExecuteInputDto executeInputDto;

    /**
     * 构造器
     * @param sourceDataDto   sourceDataDto
     * @param executeInputDto executeInputDto
     */
    public ManifestTestTask(TestTaskSourceDataDto sourceDataDto, TestExecuteInputDto executeInputDto) {
        this.sourceDataDto = sourceDataDto;
        this.executeInputDto = executeInputDto;

    }

    /**
     * 执行变量清单的测试方法
     * @return 变量清单的测试结果
     */
    public TestExecuteResultDto call() {
        TestExecuteResultDto resultDto = new TestExecuteResultDto();
        try {
            log.info(">>>变量清单测试任务启动");
            resultDto = executeTest();
        } catch (Exception e) {
            log.error("变量清单测试异常：", e);
        } finally {
            log.info(">>>变量清单测试任务结束!");
        }
        return resultDto;
    }

    /**
     * 对单条数据执行测试
     *
     * @return 测试执行结果DTO
     */
    private TestExecuteResultDto executeTest() {
        long startExec = System.currentTimeMillis();
        //预期结果一致：1一致    预期结果不一致：1不一致    正常执行数   异常数     是否正常    预期结果是否一致：0-不一致，1-一致，2-无     异常信息       结果内容
        int resultsEq = 0, resultsNe = 0, normal = 0, exception = 0, isNormal = 1, isResult = MagicNumbers.TWO;
        String exceptionMsg = null;
        String resultContent = null;
        //原始数据      执行时间       比对预期结果      实际结果表头       输入体JSON
        String inputContent = null;
        long executionTime = 0;
        JSONObject diffResultMap = new JSONObject();
        JSONObject resultsHeader = new JSONObject();
        JSONObject inputJsonObject = new JSONObject();
        ServiceContext serviceContext = null;
        try {
            //原始数据      预期结果数据
            JSONObject sourceJson = JSON.parseObject(sourceDataDto.getInputJson());
            JSONObject expectedMapData = new JSONObject();
            if (!StringUtils.isEmpty(sourceDataDto.getExpectJson())) {
                expectedMapData = TestTableDataUtil.jsonObjectKeyAddToPrefix(JSON.parseObject(sourceDataDto.getExpectJson()), TestTableEnum.EXPECT.getCode());
            }
            expectedMapData = TestTableDataUtil.fillExpectDataByHeader(expectedMapData, executeInputDto.getExpectedHeader());
            log.info("变量执行预期结果：" + JSON.toJSONString(expectedMapData));
            //获取最新数据
            JSONObject newJsonData = TestExecuteUtil.filterUselessFields(sourceJson, executeInputDto.getInputHeader());
            log.info("变量测试执行原始数据入参：" + JSON.toJSONString(newJsonData));
            inputContent = newJsonData.toJSONString();
            //变量测试入参数据
            inputJsonObject = TestExecuteUtil.transformJsonObject(newJsonData, executeInputDto.getInputVarMap());
            String inputString = "{}";
            if (inputJsonObject.size() > 0) {
                inputString = inputJsonObject.getJSONObject(PositionVarEnum.RAW_DATA.getName()).toJSONString();
            }
            log.info("变量执行准备数据耗时：" + (System.currentTimeMillis() - startExec));
            long startTimestamp = System.currentTimeMillis();
            serviceContext = executeInputDto.getEngine().executeBackTrackAndManifestTest(inputString, true, executeInputDto.getBatchNo(),
                    sourceDataDto.getSerialNo(), null, sourceDataDto.getCallName(), executeInputDto.getOutsideServiceStrategyMap(), ServiceRuntimeEnvEnum.TEST);
            executeInputDto.getEngine().execute(serviceContext);
            executionTime = System.currentTimeMillis() - startTimestamp;
            log.info("变量执行耗时：time={}", executionTime);
            log.info("变量测试响应结果rawData：{}", JSON.toJSONString(serviceContext.getRawData()));
            log.info("变量测试响应结果output：{}", JSON.toJSONString(serviceContext.getVars()));
            startTimestamp = System.currentTimeMillis();
            //提取变量执行结果数据
            JSONObject outputMap = extractExecutionResult(sourceDataDto.getDataId(), inputJsonObject, serviceContext);
            log.info("提取变量执行结果数据：" + JSON.toJSONString(outputMap));
            if (expectedMapData.size() > 0) {
                //对比结果：0-一致，1-不一致
                int diffResult = TestExecuteUtil.diffExpectRealityData(expectedMapData, outputMap.getJSONObject(TestHeaderValueEnum.VALUE.getCode()),
                        diffResultMap);
                if (diffResult > 0) {
                    resultsNe = 1;
                    isResult = 0;
                } else {
                    resultsEq = 1;
                    isResult = 1;
                }
            }
            //追加实际结果
            JSONObject resultJsonObject = outputMap.getJSONObject(TestHeaderValueEnum.VALUE.getCode());
            if (resultJsonObject != null && resultJsonObject.size() > 0) {
                JSONObject retJsonObject = TestExecuteUtil.transferResultData(resultJsonObject);
                resultContent = retJsonObject.toJSONString();
            }
            resultsHeader = outputMap.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
            log.info("变量执行对比耗时：" + (System.currentTimeMillis() - startTimestamp));
            normal = 1;
        } catch (Exception e) {
            log.error("变量测试数据执行失败", e);
            exceptionMsg = ExceptionUtils.getStackTrace(e);
            exception = 1;
            isNormal = 0;
        }
        log.info("变量执行总耗时：" + (System.currentTimeMillis() - startExec));
        if (isNormal == MagicNumbers.ONE) {
            isResult = (MagicStrings.CURLY_BRACE.equals(sourceDataDto.getExpectJson())) ? MagicNumbers.TWO : isResult;
        }
        return getTestExecuteResultDto(new GetTestExecuteResultParam(resultsEq, resultsNe, normal, exception, isNormal, isResult, exceptionMsg, resultContent, inputContent, executionTime, diffResultMap, resultsHeader, inputJsonObject, serviceContext));
    }

    /**
     * 获取最终的结果
     *
     * @param getTestExecuteResultParam 输入参数
     * @return 测试结果
     */
    private TestExecuteResultDto getTestExecuteResultDto(GetTestExecuteResultParam getTestExecuteResultParam) {
        TestExecuteResultDto resultDto = new TestExecuteResultDto();
        resultDto.setDataId(sourceDataDto.getDataId());
        resultDto.setBatchNo(executeInputDto.getBatchNo());
        resultDto.setTestSerialNo(sourceDataDto.getSerialNo());
        resultDto.setResultsEq(getTestExecuteResultParam.getResultsEq());
        resultDto.setResultsNe(getTestExecuteResultParam.getResultsNe());
        resultDto.setNormal(getTestExecuteResultParam.getNormal());
        resultDto.setException(getTestExecuteResultParam.getException());
        resultDto.setExecutionStatus(getTestExecuteResultParam.getIsNormal());
        resultDto.setComparisonStatus(getTestExecuteResultParam.getIsResult());
        resultDto.setExecutionTime(getTestExecuteResultParam.getExecutionTime());
        resultDto.setInputContent(getTestExecuteResultParam.getInputContent());
        resultDto.setExpectContent(sourceDataDto.getExpectJson());
        resultDto.setResultsContent(getTestExecuteResultParam.getResultContent());
        resultDto.setResultsHeader(getTestExecuteResultParam.getResultsHeader());
        if (getTestExecuteResultParam.getDiffResultMap().size() > 0) {
            resultDto.setComparisonContent(getTestExecuteResultParam.getDiffResultMap().toJSONString());
        }
        resultDto.setExceptionMsg(getTestExecuteResultParam.getExceptionMsg());
        //组装原始输入输出数据
        JSONObject response = new JSONObject();
        if (getTestExecuteResultParam.getInputJsonObject().size() > 0) {
            response.put(PositionVarEnum.INPUT.getName(), getTestExecuteResultParam.getInputJsonObject().getJSONObject(PositionVarEnum.RAW_DATA.getName()));
        } else {
            response.put(PositionVarEnum.INPUT.getName(), getTestExecuteResultParam.getInputJsonObject());
        }

        ServiceContext serviceContext = getTestExecuteResultParam.getServiceContext();
        if (serviceContext != null) {
            resultDto.setTraceLogs(serviceContext.getTraceLogs());

            response.put(PositionVarEnum.RAW_DATA.getName(), serviceContext.getRawData());

            if (resultDto.getException() != NumberUtils.INTEGER_ONE) {
                response.put(PositionVarEnum.OUTPUT.getName(), serviceContext.getVars());
            }

            if (getTestExecuteResultParam.getServiceContext().getDebugLog() != null && !serviceContext.getDebugLog().isEmpty()) {
                resultDto.setDebugInfo(JSON.toJSONStringWithDateFormat(serviceContext.getDebugLog(), DateUtil.FORMAT_LONG));
            }
        }
        resultDto.setOriginalContent(response.toJSONString());
        return resultDto;
    }

    /**
     * 提取变量执行结果数据
     *
     * @param dataId
     * @param inputJsonObject
     * @param serviceContext
     * @return JSONObject
     * @throws JsonProcessingException
     */
    private JSONObject extractExecutionResult(Integer dataId, JSONObject inputJsonObject, ServiceContext serviceContext)
            throws JsonProcessingException {

        JSONObject jsonObject = new JSONObject();
        if (serviceContext.getVars() == null) {
            return jsonObject;
        }
        JSONObject output = JSONObject.parseObject(JSONObject.toJSONString(serviceContext.getVars(), SerializerFeature.WriteDateUseDateFormat));
        jsonObject.put(PositionVarEnum.OUTPUT.getName(), output);

        jsonObject.putAll(getRawDataJson(inputJsonObject, serviceContext));

        //删除空数据
        TestExecuteUtil.removeJsonEmptyValue(jsonObject);

        return TestTableDataUtil.transHeaderValueMap(String.valueOf(dataId), jsonObject, executeInputDto.getDataModelHeaderDto());

    }

    private JSONObject getRawDataJson(JSONObject inputJsonObject, ServiceContext serviceContext) {
        JSONObject jsonObject = new JSONObject();

        if (serviceContext.getRawData() == null) {
            return jsonObject;
        }

        jsonObject.put(PositionVarEnum.RAW_DATA.getName(), JSONObject.toJSON(serviceContext.getRawData()));

        List<String> excludeKeyList = new ArrayList<>();

        //获取排除的key
        getExcludePath(inputJsonObject, excludeKeyList, "");

        //排除
        excludeUnnecessaryVarPath(jsonObject, excludeKeyList, "");

        log.info("需要排除的变量：{}", JSONObject.toJSONString(excludeKeyList));
        log.info("提取的数据：{}", jsonObject);

        return jsonObject;
    }

    private void getExcludePath(JSONObject jsonObject, List<String> excludeKeyList, String parentKey) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String currentKey = "";
            if (!StringUtils.isEmpty(parentKey)) {
                currentKey = parentKey + "." + entry.getKey();
            } else {
                currentKey = entry.getKey();
            }
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                getExcludePath((JSONObject) value, excludeKeyList, currentKey);
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
                    excludeKeyList.add(currentKey);
                }
            } else {
                excludeKeyList.add(currentKey);
            }
        }
    }

    /**
     * 排除输入变量
     *
     * @param jsonObject
     * @param outputVarList
     * @param parentKey
     */
    private void excludeUnnecessaryVarPath(JSONObject jsonObject, List<String> outputVarList, String parentKey) {
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
                        excludeUnnecessaryVarPath(valueArr.getJSONObject(i), outputVarList, currentKey);
                    }
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class GetTestExecuteResultParam {
        private final int resultsEq;
        private final int resultsNe;
        private final int normal;
        private final int exception;
        private final int isNormal;
        private final int isResult;
        private final String exceptionMsg;
        private final String resultContent;
        private final String inputContent;
        private final long executionTime;
        private final JSONObject diffResultMap;
        private final JSONObject resultsHeader;
        private final JSONObject inputJsonObject;
        private final ServiceContext serviceContext;
    }
}
