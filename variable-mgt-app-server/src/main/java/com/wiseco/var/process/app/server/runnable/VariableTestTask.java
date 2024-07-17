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
import com.wiseco.decision.engine.base.AbstractExternalData;
import com.wiseco.decision.engine.base.AbstractParameters;
import com.wiseco.decision.engine.base.AbstractRawData;
import com.wiseco.decision.engine.base.AbstractVars;
import com.wiseco.decision.engine.java.component.Parameter;
import com.wiseco.decision.engine.var.enums.ServiceRuntimeEnvEnum;
import com.wiseco.decision.engine.var.runtime.basecls.AbstractVar;
import com.wiseco.decision.engine.var.runtime.context.ServiceContext;
import com.wiseco.decision.engine.var.runtime.context.VarContext;
import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.engine.var.transform.component.compiler.dataBuilder.vo.AbstractVarBaseDataVo;
import com.wiseco.decision.engine.var.transform.component.compiler.dataBuilder.vo.VarCompileInfoVo;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.test.TestExecuteUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableDataUtil;
import com.wiseco.var.process.app.server.controller.vo.input.TestExecuteInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestExecuteResultDto;
import com.wiseco.var.process.app.server.enums.ComponentParameterDirectionEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.TestTaskSourceDataDto;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import com.wisecotech.json.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;
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
public class VariableTestTask {

    private TestTaskSourceDataDto sourceDataDto;

    private TestExecuteInputDto executeInputDto;

    /**
     * 可变测试任务
     * @param sourceDataDto   sourceDataDto
     * @param executeInputDto executeInputDto
     */
    public VariableTestTask(TestTaskSourceDataDto sourceDataDto, TestExecuteInputDto executeInputDto) {
        this.sourceDataDto = sourceDataDto;
        this.executeInputDto = executeInputDto;
    }

    /**
     * 执行变量的测试
     * @return 变量的测试结果
     */
    public TestExecuteResultDto call() {
        TestExecuteResultDto resultDto = new TestExecuteResultDto();
        try {
            log.info(">>>变量测试任务启动");
            resultDto = executeTest();
        } catch (Exception e) {
            log.error("变量测试异常：", e);
        } finally {
            log.info(">>>变量测试任务结束");
        }
        return resultDto;
    }

    /**
     * 对单条数据执行测试
     *
     * @return 测试执行结果DTO
     */
    private TestExecuteResultDto executeTest() throws JsonProcessingException {
        long startExec = System.currentTimeMillis();
        //resultsEq:预期结果一致：1一致 / resultsNe:预期结果不一致：1不一致 / normal:正常执行数 / exception:异常数 / isNormal:是否正常 /isResult:预期结果是否一致：0-不一致，1-一致，2-无
        int resultsEq = MagicNumbers.ZERO, resultsNe = MagicNumbers.ZERO, normal = MagicNumbers.ZERO, exception = MagicNumbers.ZERO, isNormal = MagicNumbers.ONE, isResult = MagicNumbers.TWO;
        //inputContent:原始数据  inputJsonString:加工后的json入参
        String exceptionMsg = null, resultContent = null, inputContent = null, inputJsonString = null;
        VarContext varContext = null;
        //diffResultMap:比对预期结果  resultsHeader:实际结果表头
        JSONObject diffResultMap = new JSONObject(), resultsHeader = new JSONObject();
        try {
            //原始数据
            JSONObject sourceJson = JSON.parseObject(sourceDataDto.getInputJson());
            //预期结果数据
            JSONObject expectedMapData = new JSONObject();
            if (!StringUtils.isEmpty(sourceDataDto.getExpectJson())) {
                expectedMapData = TestTableDataUtil.jsonObjectKeyAddToPrefix(JSON.parseObject(sourceDataDto.getExpectJson()), TestTableEnum.EXPECT.getCode());
                expectedMapData = TestTableDataUtil.fillExpectDataByHeader(expectedMapData, executeInputDto.getExpectedHeader());
            }
            log.info("变量执行预期结果：testId={},dataId={},data={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), JSON.toJSONString(expectedMapData));
            //获取最新数据
            JSONObject newJsonData = TestExecuteUtil.filterUselessFields(sourceJson, executeInputDto.getInputHeader());
            log.info("变量测试执行原始数据入参：testId={},dataId={},data={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), JSON.toJSONString(newJsonData));
            inputContent = newJsonData.toJSONString();
            //变量测试入参数据
            JSONObject jsonObject = TestExecuteUtil.transformJsonObject(newJsonData, executeInputDto.getInputVarMap());
            inputJsonString = JSON.toJSONString(jsonObject);
            //处理参数情况
            TestExecuteUtil.handleParamterJson(jsonObject, executeInputDto.getInputVarMap());
            log.info("变量测试执行数据入参：testId={},dataId={},data={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), JSON.toJSONString(jsonObject));
            //调用变量测试
            varContext = convertData(jsonObject, executeInputDto.getEngine(), sourceDataDto.getSerialNo());
            varContext.setStartTime(System.currentTimeMillis());
            log.info("x1变量执行准备数据耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), (System.currentTimeMillis() - startExec));
            long startTimestamp = System.currentTimeMillis();
            executeEngin(varContext, startTimestamp);
            startTimestamp = System.currentTimeMillis();
            //提取变量执行结果数据
            JSONObject outputMap = extractExecutionResult(sourceDataDto.getDataId(), varContext, inputJsonString);
            log.info("提取变量执行结果数据：{}", JSON.toJSONString(outputMap));
            log.info("x3提取执行结果耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), (System.currentTimeMillis() - startTimestamp));
            //存在预期结果，进行预期结果对比
            if (expectedMapData.size() > 0) {
                //对比结果：0-一致，1-不一致
                int diffResult = TestExecuteUtil.diffExpectRealityData(expectedMapData, outputMap.getJSONObject(TestHeaderValueEnum.VALUE.getCode()), diffResultMap);
                if (diffResult > 0) {
                    resultsNe = 1;
                    isResult = 0;
                } else {
                    resultsEq = 1;
                    isResult = 1;
                }
            }
            log.info("x4组件执行对比耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), (System.currentTimeMillis() - startTimestamp));
            //追加实际结果
            JSONObject resultJsonObject = outputMap.getJSONObject(TestHeaderValueEnum.VALUE.getCode());
            if (resultJsonObject != null && resultJsonObject.size() > 0) {
                JSONObject retJsonObject = TestExecuteUtil.transferResultData(resultJsonObject);
                resultContent = getNewResultContent(inputContent, retJsonObject.toJSONString());
            }
            //组装实际结果表头
            resultsHeader = outputMap.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
            log.info("x5变量执行对比耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(),
                    (System.currentTimeMillis() - startTimestamp));
            normal = 1;
        } catch (Exception e) {
            log.error("变量测试数据执行失败,varId:{}", executeInputDto.getVarVo().getVarId(), e);
            exceptionMsg = ExceptionUtils.getStackTrace(e);
            exception = 1;
            isNormal = 0;
        }
        log.info("x6组件执行总耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), (System.currentTimeMillis() - startExec));
        if (isNormal == MagicNumbers.ONE) {
            isResult = (MagicStrings.CURLY_BRACE.equals(sourceDataDto.getExpectJson())) ? MagicNumbers.TWO : isResult;
        }
        TestExecuteResultDto resultDto = TestExecuteResultDto.builder().dataId(sourceDataDto.getDataId())
                .batchNo(executeInputDto.getBatchNo()).testSerialNo(sourceDataDto.getSerialNo()).resultsEq(resultsEq).resultsNe(resultsNe).normal(normal).exception(exception).executionStatus(isNormal).comparisonStatus(isResult)
                .executionTime(System.currentTimeMillis() - startExec).inputContent(inputContent).expectContent(sourceDataDto.getExpectJson()).resultsContent(resultContent).resultsHeader(resultsHeader).exceptionMsg(exceptionMsg).build();
        assembleResultDto(inputJsonString, varContext, diffResultMap, resultDto);
        return resultDto;
    }

    private void executeEngin(VarContext varContext, long startTimestamp) throws Exception {
        executeInputDto.getEngine().executeTestVar(varContext, false, executeInputDto.getBatchNo(), sourceDataDto.getSerialNo(),
                ServiceRuntimeEnvEnum.TEST);
        log.info("x2变量执行耗时：testId={},dataId={},times={}", executeInputDto.getTestId(), sourceDataDto.getDataId(), (System.currentTimeMillis() - startTimestamp));
        log.info("变量测试响应结果rawData：{}", JSON.toJSONString(varContext.getServiceContext().getRawData()));
        log.info("变量测试响应结果Parameters：{}", JSON.toJSONString(varContext.getParameters()));
        log.info("变量测试响应结果LocalVars：{}", JSON.toJSONString(varContext.getLocalVars()));
        log.info("变量测试响应结果return：{}", String.valueOf(varContext.getReturnValue()));
    }

    private void assembleResultDto(String inputJsonString, VarContext varContext, JSONObject diffResultMap, TestExecuteResultDto resultDto) throws JsonProcessingException {
        if (diffResultMap.size() > 0) {
            resultDto.setComparisonContent(diffResultMap.toJSONString());
        }
        //组装原始输入输出数据
        JSONObject response = new JSONObject();

        if (!StringUtils.isEmpty(inputJsonString)) {
            response.put(PositionVarEnum.INPUT.getName(), JSONObject.parseObject(inputJsonString, Feature.OrderedField));
        }

        if (varContext != null) {
            if (varContext.getServiceContext() != null && varContext.getServiceContext().getRawData() != null) {
                response.put(PositionVarEnum.RAW_DATA.getName(), varContext.getServiceContext().getRawData());
            }
            if (resultDto.getException() != NumberUtils.INTEGER_ONE) {
                String outputJsonString = JSON.toJSONString(getOutputData(varContext, inputJsonString), SerializerFeature.DisableCircularReferenceDetect);
                response.put(PositionVarEnum.OUTPUT.getName(), JSONObject.parseObject(outputJsonString, Feature.OrderedField));
            }
        }

        resultDto.setOriginalContent(getNewOriginalContent(response.toJSONString()));
        if (varContext != null && varContext.getServiceContext() != null && varContext.getServiceContext().getDebugLog() != null && !varContext.getServiceContext().getDebugLog().isEmpty()) {
            resultDto.setDebugInfo(JSON.toJSONStringWithDateFormat(varContext.getServiceContext().getDebugLog(), DateUtil.FORMAT_LONG));
        }
    }

    /**
     * 测试数据组装
     *
     * @param jsonData
     * @param engine
     * @param testSerialNo
     * @return VarContext
     */
    private VarContext convertData(JSONObject jsonData, Engine engine, String testSerialNo) {
        try {
            JSONObject inputJson = jsonData.getJSONObject(PositionVarEnum.RAW_DATA.getName());
            JSONObject varsJson = jsonData.getJSONObject(PositionVarEnum.VARS.getName());
            JSONObject externalDataJson = jsonData.getJSONObject(PositionVarEnum.EXTERNAL_DATA.getName());
            JSONObject parametersJson = jsonData.getJSONObject(PositionVarEnum.PARAMETERS.getName());
            JSONObject parameters = new JSONObject();
            VarCompileInfoVo compileInfo = executeInputDto.getVarVo().getContent();
            AbstractVarBaseDataVo parameterInfo = null;
            List<Parameter> parameterLst = null;
            boolean flag = false;
            if (compileInfo != null) {
                parameterInfo = compileInfo.getBaseData();
                if (parameterInfo != null) {
                    parameterLst = parameterInfo.getParameters();
                    if (parameterLst != null) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                parameterLst.forEach(parameter -> {
                    if (ComponentParameterDirectionEnum.IN.name().equalsIgnoreCase(parameter.getDirection())) {
                        if (parametersJson != null) {
                            JSONObject parameterObj = parametersJson.getJSONObject(parameter.getName());
                            if (parameterObj != null) {
                                Integer isArr = parameterObj.getInteger("isArr");
                                if (isArr == 0) {
                                    parameters.put(parameter.getName(), parameterObj.getObject("value", Object.class));
                                } else {
                                    parameters.put(parameter.getName(), parameterObj.getJSONArray("value"));
                                }
                            }
                        }
                    } else {
                        if (parametersJson != null) {
                            JSONObject parameterObj = parametersJson.getJSONObject(parameter.getName());
                            if (parameterObj != null) {
                                Integer isArr = parameterObj.getInteger("isArr");
                                if (isArr == 0) {
                                    parameters.put(parameter.getName(), parameterObj.getObject("value", Object.class));
                                } else {
                                    parameters.put(parameter.getName(), parameterObj.getJSONArray("value"));
                                }
                            }
                        }
                    }
                });
            }
            //组装
            VarContext varContext = new VarContext();
            List<AbstractVar> abstractVars = new ArrayList<>(engine.getEntryVars());
            if (parameters != null && !CollectionUtils.isEmpty(abstractVars) && abstractVars.get(0).getParameterCls() != null) {
                AbstractParameters abstractParameters = (AbstractParameters) (JSONObject.parseObject(parameters.toJSONString(), abstractVars.get(0).getParameterCls()));
                varContext.setParameters(abstractParameters);
            }
            ServiceContext serviceContext = ServiceContext.builder()
                    .externalSerialNo(executeInputDto.getBatchNo())
                    .outsideSerialNo(testSerialNo)
                    .build();
            if (engine.getRawDataClass() != null) {
                AbstractRawData rawData = (AbstractRawData) engine.getRawDataClass().newInstance();
                if (inputJson != null) {
                    rawData.parseFillProperty(inputJson.toJSONString());
                }
                serviceContext.setRawData(rawData);
            }
            if (engine.getVarsClass() != null) {
                serviceContext.setVars(varsJson != null ? (AbstractVars) (JSONObject.parseObject(varsJson.toJSONString(), engine.getVarsClass())) : (AbstractVars) engine.getVarsClass().newInstance());
            }
            if (engine.getExternalDataClass() != null) {
                serviceContext.setExternalData(externalDataJson != null ? (AbstractExternalData) (JSONObject.parseObject(externalDataJson.toJSONString(), engine.getExternalDataClass())) : (AbstractExternalData) engine.getExternalDataClass().newInstance());
            }
            varContext.setServiceContext(serviceContext);
            return varContext;
        } catch (Exception e) {
            log.error("测试参数处理出错:", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试参数处理出错");
        }
    }

    /**
     * 提取测试结果
     * @param dataId dataId
     * @param varContext varContext对象
     * @param inputJsonString inputJson的字符串形式
     * @return JSONObject
     * @throws JsonProcessingException Json转换异常
     */
    private JSONObject extractExecutionResult(Integer dataId, VarContext varContext, String inputJsonString) throws JsonProcessingException {

        JSONObject jsonObject = getOutputData(varContext, inputJsonString);

        return TestTableDataUtil.transHeaderValueMap(String.valueOf(dataId), jsonObject, executeInputDto.getDataModelHeaderDto());

    }

    /**
     * 获取输出数据
     * @param varContext varContext对象
     * @param inputJsonString inputJson的字符串形式
     * @return JSONObject
     * @throws JsonProcessingException Json转换异常
     */
    private JSONObject getOutputData(VarContext varContext, String inputJsonString) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject();
        if (varContext.getServiceContext().getRawData() != null) {
            jsonObject.put(PositionVarEnum.RAW_DATA.getName(), JSONObject.toJSON(varContext.getServiceContext().getRawData()));
            // 去掉outputData中的input内容
            jsonObject = filterInputInOutput(jsonObject, inputJsonString);
        }
        if (varContext.getParameters() != null) {
            jsonObject.put(PositionVarEnum.PARAMETERS.getName(), JSONObject.toJSON(varContext.getParameters()));
        }
        if (varContext.getLocalVars() != null) {
            jsonObject.put(PositionVarEnum.LOCAL_VARS.getName(), JSONObject.toJSON(varContext.getLocalVars()));
        }

        //自定义函数
        if (!StringUtils.isEmpty(varContext.getReturnValue())) {
            jsonObject.put(executeInputDto.getReturnKey(), varContext.getReturnValue());
        }
        if (jsonObject.size() == 0) {

            return jsonObject;
        }

        jsonObject = JSONObject.parseObject(JSONObject.toJSONString(jsonObject, SerializerFeature.WriteDateUseDateFormat));
        List<String> outputVarList = executeInputDto.getOutputExcludeVarList();
        TestExecuteUtil.excludeUnnecessaryVarPath(jsonObject, outputVarList, "");
        log.info("需要排除的变量：" + JSONObject.toJSONString(outputVarList));
        log.info("提取的数据：" + JSONObject.toJSONString(jsonObject));

        //删除空值
        TestExecuteUtil.removeJsonEmptyValue(jsonObject);

        return jsonObject;
    }

    /**
     * 过滤结果，获取新的结果
     * @param inputContent 输入
     * @param resultContent 老结果
     * @return 新的结果
     */
    private String getNewResultContent(String inputContent, String resultContent) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(resultContent)) {
            return null;
        }
        // 1.先去掉master
        JSONObject inputJson = JSONObject.parseObject(inputContent);
        JSONObject resultJson = JSONObject.parseObject(resultContent);
        JSONObject input = (JSONObject) inputJson.get(TestTableEnum.MASTER.getCode());
        JSONObject result = (JSONObject) resultJson.get(TestTableEnum.MASTER.getCode());
        // 2.定义结果
        JSONObject finalResult = new JSONObject();
        JSONObject temp = new JSONObject();
        // 3.遍历
        if (result != null) {
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                if (input != null) {
                    if (!entry.getKey().equals(MagicStrings.ID) && input.containsKey(entry.getKey()) && input.get(entry.getKey()).toString().equals(entry.getValue().toString())) {
                        continue;
                    }
                }
                temp.put(entry.getKey(), entry.getValue());
            }
        }
        finalResult.put(TestTableEnum.MASTER.getCode(), temp);
        return finalResult.toJSONString();
    }

    /**
     * 过滤原始输入输出, 得到新的输入和输出
     * @param originalContent 原始的输入和输出
     * @return 新的输入和输出
     */
    private String getNewOriginalContent(String originalContent) {
        // 1.获取originalContent对应的JSON
        JSONObject json = JSONObject.parseObject(originalContent);
        // 2.分离成input和output
        JSONObject input = (JSONObject) json.get(TestTableEnum.INPUT.getCode());
        JSONObject output = (JSONObject) json.get(TestTableEnum.OUTPUT.getCode());
        List<String> deleteKeys = new ArrayList<>();
        if (output != null) {
            for (Map.Entry<String, Object> entry : output.entrySet()) {
                if (input.containsKey(entry.getKey()) && entry.getValue().toString().equals(input.get(entry.getKey()).toString())) {
                    deleteKeys.add(entry.getKey());
                }
            }
            // 3.删除相同的key-value
            for (String key : deleteKeys) {
                output.remove(key);
            }
        }
        json.put(TestTableEnum.OUTPUT.getCode(), output);
        return json.toJSONString();
    }

    /**
     * 过滤原始输入输出, 得到新的输入和输出
     * @param jsonObject 原始的输出
     * @param inputJsonString 原始的输入
     * @return 新的输入和输出
     */
    private JSONObject filterInputInOutput(JSONObject jsonObject, String inputJsonString) {
        // 1.先把inputJsonString转换成JSONObject
        JSONObject inputJsonObject = JSONObject.parseObject(inputJsonString);
        // 2.然后分别去掉它们的RAW_DATA
        JSONObject input = inputJsonObject.getJSONObject(MagicStrings.RAW_DATA);
        JSONObject output = jsonObject.getJSONObject(MagicStrings.RAW_DATA);
        // 3.开始过滤
        JSONObject newOutput = new JSONObject();
        if (output != null) {
            for (Map.Entry<String, Object> entry : output.entrySet()) {
                if (input != null) {
                    if (!input.containsKey(entry.getKey()) || !input.get(entry.getKey()).toString().equals(entry.getValue().toString())) {
                        newOutput.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        jsonObject.clear();
        jsonObject.put(MagicStrings.RAW_DATA, newOutput);
        return jsonObject;
    }
}
