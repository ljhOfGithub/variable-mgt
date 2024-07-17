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

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.boot.commons.util.CollectionUtils;
import com.wiseco.boot.commons.util.ObjectUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.threadpool.concurrent.core.WisecoThreadPoolExecutor;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.IdentityGenerator;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.var.runtime.context.TraceLog;
import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.engine.var.transform.component.compiler.dataBuilder.dataParser.JavaVarCompileDataBuilderEntry;
import com.wiseco.decision.engine.var.transform.component.compiler.dataBuilder.vo.VarVo;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.decision.model.engine.VarDto;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.test.TestExcelUtils;
import com.wiseco.var.process.app.server.commons.test.TestExecuteUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableDataUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableExportUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableHeaderUtil;
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestExecuteOutputDto;
import com.wiseco.var.process.app.server.commons.test.dto.TestResultDto;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectResultInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestExecuteInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestCollectOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestExecuteResultDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestResultsOutputDto;
import com.wiseco.var.process.app.server.enums.OutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.TestResultDetailTabEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.TraceBusinessTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.test.TestExecStatusEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestResultDiffStatusEnum;
import com.wiseco.var.process.app.server.enums.test.TestResultsQueryTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestResults;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableResult;
import com.wiseco.var.process.app.server.runnable.ManifestTestTask;
import com.wiseco.var.process.app.server.runnable.VariableTestTask;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wiseco.var.process.app.server.service.dto.TestTaskSourceDataDto;
import com.wiseco.var.process.app.server.service.dto.VariableBaseDetailDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.output.TestResultDetailOutputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wiseco.var.process.app.server.service.multipleimpl.VarProcessTestVariableDataService;
import com.wiseco.var.process.app.server.service.multipleimpl.VarProcessTestVariableResultHeaderService;
import com.wiseco.var.process.app.server.service.multipleimpl.VarProcessTestVariableResultService;
import com.wiseco.var.process.engine.compiler.ServiceExporter;
import com.wiseco.var.process.engine.compiler.VarEngineDelegator;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 测试执行
 *
 * @author wangxianli
 * @since 2021/11/30
 */
@Service
@Slf4j
public class TestVariableExecuteBiz extends TestVariablePrivate {

    @Autowired
    private VarProcessTestService varProcessTestVariableService;

    @Autowired
    private VarProcessTestResultsService varProcessTestVariableResultsService;

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Resource(name = "variable-test-pool")
    private WisecoThreadPoolExecutor variableTestPool;

    /**
     * 基于 MongoDB/Mysql 访问测试数据集和执行结果的相关服务
     */
    @Autowired
    private VarProcessTestVariableDataService varProcessTestVariableDataService;

    @Autowired
    private VarProcessTestVariableResultService varProcessTestVariableResultService;

    @Autowired
    private VarProcessTestVariableResultHeaderService varProcessTestVariableResultHeaderService;

    @Autowired
    private VarEngineDelegator varEngineDelegator;

    @Autowired
    private ServiceExporter serviceExporter;

    @Autowired
    private VariableDataProviderBiz variableDataProviderBiz;

    @Autowired
    private JavaVarCompileDataBuilderEntry dataBuilder;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Resource
    private DbOperateService dbOperateService;

    private static final String TRACE_TABLE_NAME = "var_process_trace_log";

    /**
     * executeTest
     *
     * @param testId     入参
     * @param spaceId    空间ID
     * @param variableId 入参
     * @return TestExecuteOutputDto
     */
    public TestExecuteOutputDto executeTest(Long testId, Long spaceId, Long variableId) {
        // 1.获取开始时间戳
        long startTimestamp = System.currentTimeMillis();
        // 2.查询数据集信息
        VarProcessTest varProcessTest = varProcessTestVariableService.getById(testId);
        if (varProcessTest == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "测试数据集不存在!");
        }
        // 3.查询变量或公共函数信息(根据类型获取，传入了id)
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(varProcessTest.getTestType(), variableId);
        if (null == variableBaseDetail) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.VARIABLE_NOT_FOUND, "未查询到变量信息！");
        }
        // 4.查询变量空间是否为空
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        // 5.创建测试结果记录
        Long resultsId = saveTestResultData(variableId, varProcessTest);
        // 6.定义响应值
        TestExecuteOutputDto testExecuteOutputDto;
        if (varProcessTest.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            // 6.1 如果是变量清单测试(填充state、message和stackMessage)
            testExecuteOutputDto = manifestExecute(varProcessTest, variableBaseDetail, resultsId);
        } else {
            // 6.2 如果是变量和公共函数测试(填充state、message和stackMessage)
            testExecuteOutputDto = variableExcute(varProcessTest, varProcessSpace, variableBaseDetail, resultsId);
        }
        // 7.计算测试时长
        long executeTime = System.currentTimeMillis() - startTimestamp;
        VarProcessTestResults updateTest = new VarProcessTestResults();
        updateTest.setId(resultsId);
        updateTest.setExecuteTime(executeTime);
        // 8.更新变量测试结果
        varProcessTestVariableResultsService.updateById(updateTest);
        testExecuteOutputDto.setResultId(resultsId);

        return testExecuteOutputDto;
    }

    /**
     * 变量清单测试
     * @param varProcessTest 变量测试数据集
     * @param variableBaseDetail 变量或公共函数信息
     * @param resultsId 变量测试结果Id
     * @return 测试执行返回DTO对象
     */
    private TestExecuteOutputDto manifestExecute(VarProcessTest varProcessTest, VariableBaseDetailDto variableBaseDetail, Long resultsId) {
        // 1.初始化容器
        long startEng = System.currentTimeMillis();
        Engine testEngine = null;
        // 2.定义响应值
        TestExecuteOutputDto testExecuteOutputDto = new TestExecuteOutputDto();
        testExecuteOutputDto.setState(true);
        try {
            Set<VarDto> varDefSet = new HashSet<>();
            VariableFlowQueryDto variableFlowQueryDto = new VariableFlowQueryDto();
            variableFlowQueryDto.setSpaceId(variableBaseDetail.getSpaceId());
            variableFlowQueryDto.setManifestId(variableBaseDetail.getId());
            // 2.1 获取变量清单所关联的变量,遍历
            List<VarProcessVariable> variableFlow = varProcessManifestVariableService.getVariableFlow(variableFlowQueryDto);
            for (VarProcessVariable variable : variableFlow) {
                VarDto varDto = new VarDto();
                varDto.setName(variable.getName());
                varDto.setType(variable.getDataType());
                varDto.setVersion(variable.getVersion());
                varDefSet.add(varDto);
            }
            // 2.1 获取测试的引擎变量
            testEngine = serviceExporter.getServiceInterfaceEngine(variableBaseDetail.getId(), varDefSet);
        } catch (Throwable e) {
            log.error("创建变量空间测试容器失败", e);
            testExecuteOutputDto.setState(false);
            testExecuteOutputDto.setMessage(e.getMessage());
            testExecuteOutputDto.setStackMessage(ExceptionUtils.getStackTrace(e));
            return testExecuteOutputDto;
        }
        log.info("创建变量空间测试容器耗时：" + (System.currentTimeMillis() - startEng));
        // 3.组装测试执行入参
        TestExecuteInputDto executeInputDto = buildManifestExecuteInput(varProcessTest, variableBaseDetail.getSpaceId(), variableBaseDetail.getId(), resultsId, testEngine);
        // 4.调用"执行测试"方法
        String callName = variableBaseDetail.getName();
        List<CompletableFuture<TestExecuteResultDto>> futures = callManifestTest(varProcessTest.getId(), resultsId, executeInputDto, callName);
        // 5.保存测试结果
        TestExecuteResultDto testExecuteResultDto = saveTestResults(varProcessTest.getTestType(), resultsId, executeInputDto, futures);
        // 6.更新执行结果
        updateTestResult(varProcessTest, testExecuteResultDto, variableBaseDetail, executeInputDto, resultsId);
        // 7.保存trace
        saveTraceLog(futures);
        return testExecuteOutputDto;
    }

    private void saveTraceLog(List<CompletableFuture<TestExecuteResultDto>> futures) {
        List<String> columns = Arrays.asList("node_name", "node_type", "variable_name", "start_time", "end_time", "duration", "node_state", "exception_info",
                "manifest_id", "engine_serial_no", "trace_type", "business_type","interface_query_state","interface_query_result","outside_service_strategy");
        List<List<String>> dataList = new ArrayList<>();
        try {
            for (Future<TestExecuteResultDto> future : futures) {
                TestExecuteResultDto testExecuteResultDto = future.get();
                if (!CollectionUtils.isEmpty(testExecuteResultDto.getTraceLogs())) {
                    for (TraceLog traceLog : testExecuteResultDto.getTraceLogs()) {
                        ArrayList<String> data = new ArrayList<>();
                        data.add(traceLog.getNodeName());
                        data.add(traceLog.getNodeType());
                        data.add(traceLog.getVariableName());
                        data.add(String.valueOf(traceLog.getStartTime().getTime()));
                        data.add(String.valueOf(traceLog.getEndTime().getTime()));
                        data.add(String.valueOf(traceLog.getEndTime().getTime() - traceLog.getStartTime().getTime()));
                        data.add(traceLog.getNodeState() == null ? null : traceLog.getNodeState().toString());
                        data.add(traceLog.getExceptionMsg());
                        data.add(traceLog.getManifestId() == null ? null : traceLog.getManifestId().toString());
                        data.add(testExecuteResultDto.getTestSerialNo());
                        if (StringUtils.isEmpty(traceLog.getVariableName())) {
                            data.add("1");
                        } else {
                            data.add("2");
                        }
                        data.add(TraceBusinessTypeEnum.MANIFEST_TEST.name());
                        data.add(traceLog.getInterfaceQueryState());
                        data.add(traceLog.getInterfaceQueryResult());
                        data.add(traceLog.getOutsideServiceStrategy());
                        dataList.add(data);
                    }
                }
            }

        } catch (InterruptedException e) {
            log.error("等待线程执行完成异常：", e);
            Thread.currentThread().interrupt();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        } catch (ExecutionException e) {
            log.error("测试执行异常: ", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        }

        if (!CollectionUtils.isEmpty(dataList)) {
            dbOperateService.batchInsert(TRACE_TABLE_NAME,columns,dataList);
        }
    }
    /**
     * 执行变量/公共方法测试
     * @param varProcessTest 测试数据集
     * @param varProcessSpace 变量空间
     * @param variableBaseDetail 变量/公共函数的基础信息
     * @param resultsId 测试数据集的测试结果Id
     * @return 测试结果的输出Dto
     */
    private TestExecuteOutputDto variableExcute(VarProcessTest varProcessTest, VarProcessSpace varProcessSpace, VariableBaseDetailDto variableBaseDetail, Long resultsId) {
        // 1.定义响应值
        TestExecuteOutputDto testExecuteOutputDto = new TestExecuteOutputDto();
        testExecuteOutputDto.setState(true);
        // 2.初始化组装数据
        VarTypeEnum typeEnum = null;
        if (varProcessTest.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            // 2.1 变量
            typeEnum = VarTypeEnum.VAR;
        } else if (varProcessTest.getTestType().equals(TestVariableTypeEnum.FUNCTION.getCode())) {
            // 2.2 公共函数
            typeEnum = VarTypeEnum.FUNCTION;
        }
        // 3.定义变量的编译数据对象
        VarCompileData varCompileData = variableDataProviderBiz.varDataProviderByIdentifierAndChangeNum(variableBaseDetail.getSpaceId(), typeEnum, variableBaseDetail.getIdentifier(), variableBaseDetail.getVersion());
        VarVo varVo = dataBuilder.build(varCompileData, null);
        // 4.初始化容器
        long startEng = System.currentTimeMillis();
        Engine testEngine = null;
        try {
            testEngine = varEngineDelegator.initServiceEngine(ObjectUtils.clone(varProcessSpace, com.wiseco.decision.model.engine.VarProcessSpace.class), varCompileData);
        } catch (Throwable e) {
            log.error("创建变量空间测试容器失败", e);
            testExecuteOutputDto.setState(false);
            testExecuteOutputDto.setMessage(e.getMessage());
            testExecuteOutputDto.setStackMessage(ExceptionUtils.getStackTrace(e));
            return testExecuteOutputDto;
        }
        log.info("创建变量空间测试容器耗时：" + (System.currentTimeMillis() - startEng));
        // 5.组装测试执行入参
        TestExecuteInputDto executeInputDto = buildExecuteInput(varProcessTest, variableBaseDetail.getSpaceId(), variableBaseDetail.getId(), resultsId, testEngine, varVo);
        // 6.调用"执行测试"方法
        List<CompletableFuture<TestExecuteResultDto>> futures = callExecuteTest(varProcessTest.getId(), resultsId, executeInputDto);
        // 7.保存测试结果
        TestExecuteResultDto testExecuteResultDto = saveTestResults(varProcessTest.getTestType(), resultsId, executeInputDto, futures);
        // 8.更新执行结果
        updateTestResult(varProcessTest, testExecuteResultDto, variableBaseDetail, executeInputDto, resultsId);
        return testExecuteOutputDto;
    }

    private void updateTestResult(VarProcessTest varProcessTest, TestExecuteResultDto testExecuteResultDto, VariableBaseDetailDto variableBaseDetail,
                                  TestExecuteInputDto executeInputDto, Long resultsId) {

        // 4. 保存测试执行结果信息
        // 计算并更新测试成功率: 测试成功率 = 预期一致数 / 测试总数
        BigDecimal successRate;
        if (null == varProcessTest.getDataCount() || 0 == varProcessTest.getDataCount()) {
            // 规避 NullPointerException & ArithmeticException
            successRate = BigDecimal.ZERO;
        } else {
            successRate = new BigDecimal(testExecuteResultDto.getResultsEq())
                    .divide(new BigDecimal(varProcessTest.getDataCount()), MagicNumbers.SIX, RoundingMode.HALF_UP).multiply(new BigDecimal(MagicNumbers.ONE_HUNDRED))
                    .setScale(MagicNumbers.TWO, RoundingMode.HALF_UP);
        }
        String successRateValue = String.valueOf(successRate);
        if (executeInputDto.getExpectedHeader() == null || executeInputDto.getExpectedHeader().size() == 0) {
            successRateValue = CommonConstant.DEFAULT_TEST_NA;
        }
        VarProcessTestResults updateTest = new VarProcessTestResults();
        updateTest.setId(resultsId);
        // 变量或公共函数版本加上前缀 V
        if (variableBaseDetail.getVersion() == null) {
            updateTest.setChangeNum("V1");
        } else {
            updateTest.setChangeNum("V" + variableBaseDetail.getVersion());
        }

        updateTest.setExecuteNormalCount(testExecuteResultDto.getNormal());
        updateTest.setExecuteExceptionCount(testExecuteResultDto.getException());
        updateTest.setExecuteResulteqCount(testExecuteResultDto.getResultsEq());
        updateTest.setExecuteResultneqCount(testExecuteResultDto.getResultsNe());
        updateTest.setSuccessRate(successRateValue);

        updateTest.setTestTime(new Timestamp(System.currentTimeMillis()));
        updateTest.setBatchNo(executeInputDto.getBatchNo());
        updateTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());

        varProcessTestVariableResultsService.updateById(updateTest);
    }

    /**
     * 创建测试结果记录
     *
     * @param variableId
     * @param varProcessTest
     * @return 测试结果记录ID
     */
    private Long saveTestResultData(Long variableId, VarProcessTest varProcessTest) {
        // 1.查询变量或公共函数执行结果元数据是否存在
        VarProcessTestResults varProcessTestResults = varProcessTestVariableResultsService.getOne(new QueryWrapper<VarProcessTestResults>().lambda().eq(VarProcessTestResults::getTestId, varProcessTest.getId()).eq(VarProcessTestResults::getVariableId, variableId));
        // 2.获取执行结果ID
        Long resultsId;
        if (varProcessTestResults == null) {
            // 不存在执行结果元数据: 新建空白记录, 保存至数据库
            String userName = SessionContext.getSessionUser().getUsername();
            VarProcessTestResults test = new VarProcessTestResults();
            test.setTestId(varProcessTest.getId());
            test.setDataCount(varProcessTest.getDataCount());
            test.setVariableId(variableId);
            test.setCreatedUser(userName);
            test.setUpdatedUser(userName);
            test.setChangeNum("0");
            test.setTestType(varProcessTest.getTestType());
            test.setTestTime(new Timestamp(System.currentTimeMillis()));
            varProcessTestVariableResultsService.save(test);

            resultsId = test.getId();
        } else {
            resultsId = varProcessTestResults.getId();
        }
        return resultsId;
    }

    /**
     * 组装执行入参
     *
     * @param varProcessTest
     * @param spaceId
     * @param variableId
     * @param resultsId
     * @param testEngine
     * @param varVo
     * @return TestExecuteInputDto
     */
    private TestExecuteInputDto buildExecuteInput(VarProcessTest varProcessTest, Long spaceId, Long variableId, Long resultsId, Engine testEngine,
                                                  VarVo varVo) {
        //获取数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(spaceId, varProcessTest.getTestType(), variableId);

        //引用衍生变量
        if (varProcessTest.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(variableId);
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }

        }

        //获取变量或公共函数的输入
        List<TestFormDto> inputDataList = getVariableVarInput(varProcessTest.getTestType(), variableId, dataModel);
        Map<String, TestFormDto> inputVarMap = new ConcurrentHashMap<>(MagicNumbers.TEN);
        if (!CollectionUtils.isEmpty(inputDataList)) {
            for (TestFormDto testFormDto : inputDataList) {
                inputVarMap.put(testFormDto.getName(), testFormDto);

            }
        }

        //获取变量或公共函数输出需要排除的变量
        List<String> outputExcludeVarList = getOutputExcludeVarList(varProcessTest.getTestType(), variableId);

        //获取变量或公共函数的表头map
        JSONObject dataModelHeaderDto = getDataModelHeaderDto(varProcessTest.getTestType(), variableId, dataModel);

        //获取最新表头
        JSONObject testData = TestTableHeaderUtil.getTestData(inputDataList, dataModel, 1);
        JSONObject inputHeader = new JSONObject();
        if (testData != null) {
            inputHeader = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
        }

        //获取预期结果表头
        JSONObject expectHeader = new JSONObject();
        if (!StringUtils.isEmpty(varProcessTest.getTableHeaderField())) {
            List<TestFormDto> outputFormDtoList = getVariableVarOutput(varProcessTest.getTestType(), variableId, dataModel);
            expectHeader = TestTableHeaderUtil.resetComponentExpectHeader(outputFormDtoList, JSON.parseObject(varProcessTest.getTableHeaderField()));
        }

        TestExecuteInputDto executeInputDto = new TestExecuteInputDto();
        executeInputDto.setInputVarMap(inputVarMap);
        executeInputDto.setOutputExcludeVarList(outputExcludeVarList);
        executeInputDto.setInputHeader(inputHeader);
        executeInputDto.setExpectedHeader(expectHeader);
        // 实际结果表头设为空值, 供测试执行时填写
        executeInputDto.setResultHeader(new JSONObject());
        executeInputDto.setDataModelHeaderDto(dataModelHeaderDto);
        executeInputDto.setBatchNo(String.valueOf(IdentityGenerator.nextId()));

        executeInputDto.setTestId(varProcessTest.getId());
        executeInputDto.setResultId(resultsId);
        executeInputDto.setEngine(testEngine);
        executeInputDto.setVarVo(varVo);
        if (varProcessTest.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            executeInputDto.setReturnKey(CommonConstant.VARIABLE_RETURN_NAME);
        } else if (varProcessTest.getTestType().equals(TestVariableTypeEnum.FUNCTION.getCode())) {
            executeInputDto.setReturnKey(CommonConstant.COMMON_FUNCTION_RETURN_NAME);
        }
        return executeInputDto;
    }

    /**
     * 调用引擎, 执行测试
     * @param testId          测试数据集 ID
     * @param resultId        执行结果 ID
     * @param executeInputDto 执行输入参数
     * @return 测试执行结果DTO
     */
    private List<CompletableFuture<TestExecuteResultDto>> callExecuteTest(Long testId, Long resultId, TestExecuteInputDto executeInputDto) {
        // 1.清除历史数据
        long strattime = System.currentTimeMillis();
        varProcessTestVariableResultService.deleteAllByResultId(resultId);
        log.info("删除之前的测试数据结果耗时：" + (System.currentTimeMillis() - strattime));
        // 2.创建多个有返回值的任务
        List<CompletableFuture<TestExecuteResultDto>> futures = new ArrayList<>();
        strattime = System.currentTimeMillis();
        // 3.查询测试数据
        List<MongoVarProcessTestVariableData> pageByTestId = varProcessTestVariableDataService.findAllByTestId(testId);
        int size = pageByTestId.size();
        if (size == 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "没有可执行的测试明细数据");
        }
        try {
            for (MongoVarProcessTestVariableData mongoVarProcessTestVariableData : pageByTestId) {
                // 3.1分配变量或公共函数测试请求流水号
                String testVariableSerialNo = String.valueOf(IdentityGenerator.nextId());
                TestTaskSourceDataDto sourceDataDto = TestTaskSourceDataDto.builder().dataId(mongoVarProcessTestVariableData.getDataId())
                        .inputJson(mongoVarProcessTestVariableData.getInputContent()).expectJson(mongoVarProcessTestVariableData.getExpectContent())
                        .serialNo(testVariableSerialNo).build();
                // 3.3执行任务并获取 Future 对象
                VariableTestTask task = new VariableTestTask(sourceDataDto, executeInputDto);
                Supplier<TestExecuteResultDto> supplier = task::call;
                CompletableFuture<TestExecuteResultDto> future = CompletableFuture.supplyAsync(supplier, variableTestPool);
                futures.add(future);
            }
            CompletableFuture<Void> allResults = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            allResults.join();
        } catch (Exception e) {
            log.error("等待线程执行完成异常：", e);
            Thread.currentThread().interrupt();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        }
        log.info("测试线程执行耗时：", (System.currentTimeMillis() - strattime));
        return futures;
    }

    /**
     * 组装接口测试执行入参
     *
     * @param varProcessTest
     * @param spaceId
     * @param manifestId
     * @param resultsId
     * @param testEngine
     * @return TestExecuteInputDto
     */
    private TestExecuteInputDto buildManifestExecuteInput(VarProcessTest varProcessTest, Long spaceId, Long manifestId, Long resultsId,
                                                          Engine testEngine) {
        //获取数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(spaceId, varProcessTest.getTestType(), manifestId);

        //获取变量或公共函数的输入
        List<TestFormDto> inputDataList = getVariableVarInput(TestVariableTypeEnum.MANIFEST.getCode(), manifestId, dataModel);
        Map<String, TestFormDto> inputVarMap = new ConcurrentHashMap<>(MagicNumbers.TEN);
        if (!CollectionUtils.isEmpty(inputDataList)) {
            for (TestFormDto testFormDto : inputDataList) {
                inputVarMap.put(testFormDto.getName(), testFormDto);

            }
        }

        //获取变量或公共函数的表头map
        JSONObject dataModelHeaderDto = getManifestOutputDataModelHeaderDto(manifestId);

        //获取最新表头
        JSONObject testData = TestTableHeaderUtil.getTestData(inputDataList, dataModel, 1);
        JSONObject inputHeader = new JSONObject();
        if (testData != null) {
            inputHeader = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
        }

        //获取预期结果表头
        JSONObject expectHeader = new JSONObject();
        if (!StringUtils.isEmpty(varProcessTest.getTableHeaderField())) {
            List<TestFormDto> outputFormDtoList = getManifestOutputVariable(manifestId);
            expectHeader = TestTableHeaderUtil.resetComponentExpectHeader(outputFormDtoList, JSON.parseObject(varProcessTest.getTableHeaderField()));
        }

        //获取外数模型调用策略
        Map<String, String> outsideServiceStrategyMap = getOutsideServiceStrategyMap(manifestId);

        TestExecuteInputDto executeInputDto = new TestExecuteInputDto();
        executeInputDto.setInputVarMap(inputVarMap);

        executeInputDto.setInputHeader(inputHeader);
        executeInputDto.setExpectedHeader(expectHeader);
        // 实际结果表头设为空值, 供测试执行时填写
        executeInputDto.setResultHeader(new JSONObject());
        executeInputDto.setDataModelHeaderDto(dataModelHeaderDto);
        executeInputDto.setBatchNo(String.valueOf(IdentityGenerator.nextId()));

        executeInputDto.setTestId(varProcessTest.getId());
        executeInputDto.setResultId(resultsId);
        executeInputDto.setEngine(testEngine);
        executeInputDto.setOutsideServiceStrategyMap(outsideServiceStrategyMap);
        return executeInputDto;
    }

    /**
     * 获取数据模型使用的外数服务的取值方式映射
     *
     * @param manifestId Id
     * @return 数据模型名称 : 取值方式
     */
    private Map<String, String> getOutsideServiceStrategyMap(Long manifestId) {
        List<VarProcessManifestDataModel> dataModelList = varProcessManifestDataModelService.list(new LambdaQueryWrapper<VarProcessManifestDataModel>()
                .eq(VarProcessManifestDataModel::getManifestId, manifestId)
                .eq(VarProcessManifestDataModel::getSourceType, VarProcessDataModelSourceType.OUTSIDE_SERVER.getCode()));
        if (CollectionUtils.isEmpty(dataModelList)) {
            return null;
        }
        return dataModelList.stream().collect(Collectors.toMap(VarProcessManifestDataModel::getObjectName, item -> OutsideCallStrategyEnum.MOCK_CACHE_INTERFACE.name(), (k1, k2) -> k1));
    }

    /**
     * 调用引擎, 执行测试
     * @param testId          测试数据集 ID
     * @param resultId        执行结果 ID
     * @param executeInputDto 执行输入参数
     * @param callName        调用名称
     * @return 测试执行结果DTO
     */
    private List<CompletableFuture<TestExecuteResultDto>> callManifestTest(Long testId, Long resultId, TestExecuteInputDto executeInputDto, String callName) {
        // 1.清除历史数据
        long strattime = System.currentTimeMillis();
        varProcessTestVariableResultService.deleteAllByResultId(resultId);
        log.info("删除之前的测试数据结果耗时：" + (System.currentTimeMillis() - strattime));
        // 2.创建多个有返回值的任务
        List<CompletableFuture<TestExecuteResultDto>> futures = new ArrayList<>();
        strattime = System.currentTimeMillis();
        // 3.查询测试数据
        List<MongoVarProcessTestVariableData> pageByTestId = varProcessTestVariableDataService.findAllByTestId(testId);
        int size = pageByTestId.size();
        if (size == 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "没有可执行的测试明细数据");
        }
        // 4.开始执行每一行数据
        try {
            for (MongoVarProcessTestVariableData mongoVarProcessTestVariableData : pageByTestId) {
                // 4.1 分配变量或公共函数测试请求流水号
                String testVariableSerialNo = String.valueOf(IdentityGenerator.nextId());
                TestTaskSourceDataDto sourceDataDto = TestTaskSourceDataDto.builder().dataId(mongoVarProcessTestVariableData.getDataId())
                        .inputJson(mongoVarProcessTestVariableData.getInputContent()).expectJson(mongoVarProcessTestVariableData.getExpectContent())
                        .callName(callName).serialNo(testVariableSerialNo).build();
                // 4.2 执行任务并获取 Future 对象
                ManifestTestTask task = new ManifestTestTask(sourceDataDto, executeInputDto);
                Supplier<TestExecuteResultDto> supplier = task::call;
                CompletableFuture<TestExecuteResultDto> future = CompletableFuture.supplyAsync(supplier, variableTestPool);
                futures.add(future);
            }
            CompletableFuture<Void> allResults = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            allResults.join();
        } catch (Exception e) {
            log.error("等待线程执行完成异常：", e);
            Thread.currentThread().interrupt();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        }
        log.info("测试线程执行耗时：", (System.currentTimeMillis() - strattime));
        return futures;
    }

    /**
     * 保存测试结果到MongoDB/mysql
     * @param resultId 结果Id
     * @param executeInputDto 执行的输入Dto
     * @param resultList 结果集
     * @param testType 测试类型
     * @return 测试执行结果Dto
     */
    private TestExecuteResultDto saveTestResults(Integer testType, Long resultId, TestExecuteInputDto executeInputDto, List<CompletableFuture<TestExecuteResultDto>> resultList) {

        // 1.执行结果统计数据
        // 1.1 预期结果一致
        int resultsEq = 0;
        // 1.2 预期结果不一致
        int resultsNe = 0;
        // 1.3 正常执行数
        int normal = 0;
        // 1.4 异常数
        int exception = 0;
        // 1.5 实际结果表头
        JSONObject resultsHeader = new JSONObject();
        // 2.结果
        List<MongoVarProcessTestVariableResult> resultDocumentList = new ArrayList<>();
        try {
            for (Future<TestExecuteResultDto> future : resultList) {
                TestExecuteResultDto testExecuteResultDto = future.get();
                // 2.1 更新统计数据
                if (testExecuteResultDto.getExecutionStatus() == MagicNumbers.ONE && testExecuteResultDto.getComparisonStatus() == MagicNumbers.ZERO) {
                    resultsNe++;
                    normal++;
                }
                if (testExecuteResultDto.getExecutionStatus() == MagicNumbers.ONE && testExecuteResultDto.getComparisonStatus() == MagicNumbers.ONE) {
                    resultsEq++;
                    normal++;
                }
                if (testExecuteResultDto.getExecutionStatus() == MagicNumbers.ONE && testExecuteResultDto.getComparisonStatus() == MagicNumbers.TWO) {
                    normal++;
                }
                if (testExecuteResultDto.getExecutionStatus() == MagicNumbers.ZERO) {
                    exception++;
                }
                // 2.2 处理实际结果表头
                resultsHeader = TestExecuteUtil.convertResultHeader(resultsHeader, testExecuteResultDto.getResultsHeader());
                // 2.3 新建测试执行结果记录, 添加到列表
                MongoVarProcessTestVariableResult mongoVarProcessTestVariableResult = MongoVarProcessTestVariableResult.builder()
                        .testId(executeInputDto.getTestId()).resultId(executeInputDto.getResultId()).dataId(testExecuteResultDto.getDataId())
                        .batchNo(executeInputDto.getBatchNo()).testSerialNo(testExecuteResultDto.getTestSerialNo()).createdTime(new Date())
                        .executionStatus(testExecuteResultDto.getExecutionStatus()).comparisonStatus(testExecuteResultDto.getComparisonStatus())
                        .inputContent(testExecuteResultDto.getInputContent()).expectContent(testExecuteResultDto.getExpectContent())
                        .resultsContent(testExecuteResultDto.getResultsContent()).originalContent(testExecuteResultDto.getOriginalContent())
                        .comparisonContent(testExecuteResultDto.getComparisonContent()).exceptionMsg(testExecuteResultDto.getExceptionMsg())
                        .debugInfo(testExecuteResultDto.getDebugInfo()).executionTime(testExecuteResultDto.getExecutionTime()).build();
                resultDocumentList.add(mongoVarProcessTestVariableResult);
            }
        } catch (InterruptedException e) {
            log.error("等待线程执行完成异常：", e);
            Thread.currentThread().interrupt();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        } catch (ExecutionException e) {
            log.error("测试执行异常: ", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "测试执行异常");
        }
        long startExec = System.currentTimeMillis();
        varProcessTestVariableResultService.saveOrUpdateBatch(executeInputDto.getResultId(), resultDocumentList);
        log.info("变量或公共函数执行保存数据耗时：" + (System.currentTimeMillis() - startExec));
        // 3.组装并保存新表头
        long strattime = System.currentTimeMillis();
        JSONObject newHeaderData = TestTableDataUtil.mergeHeader(executeInputDto.getInputHeader(), executeInputDto.getExpectedHeader(), resultsHeader);
        varProcessTestVariableResultHeaderService.saveOrUpdateHeader(resultId, newHeaderData.toJSONString());
        log.info("组装并保存新表头数据耗时：" + (System.currentTimeMillis() - strattime));
        // 4.返回结果(预期结果一致、预期结果不一致、正常执行数、异常数)
        TestExecuteResultDto retDto = new TestExecuteResultDto();
        retDto.setResultsEq(resultsEq);
        retDto.setResultsNe(resultsNe);
        retDto.setNormal(normal);
        retDto.setException(exception);
        return retDto;
    }

    /**
     * 展示测试结果
     *
     * @param inputDto 前端发送过来的输入实体
     * @return 测试结果
     */
    public TestResultsOutputDto executeResultDataPage(TestCollectResultInputDto inputDto) {
        // 1.定义结果集
        TestResultsOutputDto outputDto = new TestResultsOutputDto();
        if (inputDto.getId() == null || inputDto.getId() == 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "测试结果查询ID未传入");
        }
        // 2.获取执行结果, 测试数据集记录
        Long resultId = inputDto.getId();
        VarProcessTestResults varProcessTestResults = varProcessTestVariableResultsService.getById(inputDto.getId());
        if (varProcessTestResults == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "未查询到测试结果记录");
        }
        VarProcessTest varProcessTest = varProcessTestVariableService.getById(varProcessTestResults.getTestId());
        if (varProcessTest == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "未查询到测试集记录");
        }
        // 3.初始化测试数据集输出参数: 拷贝测试数据集和执行结果->设置变量或公共函数名称->设置测试数据集输出参数
        TestCollectOutputDto collectOutputDto = this.getTestCollectOutputDto(varProcessTestResults, varProcessTest);
        outputDto.setTestCollect(collectOutputDto);
        // 4.获取执行结果表头, 设置输出参数
        String headerByResultId = varProcessTestVariableResultHeaderService.findHeaderByResultId(resultId);
        if (StringUtils.isEmpty(headerByResultId)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "测试数据集的结果表头不存在!");
        }
        outputDto.setTableHeaderField(TestTableHeaderUtil.headerSort(JSON.parseObject(headerByResultId)));
        // 5.测试明细和结果记录数初始化
        this.getDataList(inputDto, varProcessTestResults, varProcessTest, outputDto);
        return outputDto;
    }

    /**
     * 获取测试数据集详情
     * @param varProcessTestResults 测试数据集的结果对象
     * @param varProcessTest 测试数据集的对象
     * @return 测试数据集详情dto
     */
    private TestCollectOutputDto getTestCollectOutputDto(VarProcessTestResults varProcessTestResults, VarProcessTest varProcessTest) {
        TestCollectOutputDto collectOutputDto = new TestCollectOutputDto();
        BeanUtils.copyProperties(varProcessTest, collectOutputDto);
        BeanUtils.copyProperties(varProcessTestResults, collectOutputDto);
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(varProcessTestResults.getTestType(), varProcessTestResults.getVariableId());
        collectOutputDto.setSourceTestName(variableBaseDetail.getName());
        return collectOutputDto;
    }

    /**
     * 获取dataList结果集和总记录数
     * @param inputDto 输入实体类对象
     * @param varProcessTestResults 测试数据集的结果对象
     * @param varProcessTest 测试数据集的对象
     * @param outputDto 返回给前端的dto
     */
    private void getDataList(TestCollectResultInputDto inputDto, VarProcessTestResults varProcessTestResults, VarProcessTest varProcessTest, TestResultsOutputDto outputDto) {
        // 1.将状态转变成code的枚举 0-全部, 1-正常, 2-异常, 3-预期一致, 4-预期不一致
        TestResultsQueryTypeEnum code = TestResultsQueryTypeEnum.getCode(inputDto.getState());
        if (code == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "查询条件的状态不正确!");
        }
        // 2.分类处理
        List<MongoVarProcessTestVariableResult> resultList = null;
        Integer dataCount = MagicNumbers.ZERO;
        switch (code) {
            case ALL:
                resultList = varProcessTestVariableResultService.findPageByResultId(varProcessTestResults.getId(), inputDto.getPage(), inputDto.getSize());
                dataCount = varProcessTest.getDataCount();
                break;
            case NORMAL:
                resultList = varProcessTestVariableResultService.findPageByResultIdInNormalAndExecutionStatus(varProcessTestResults.getId(), Integer.valueOf(TestExecStatusEnum.NORMAL.getCode()), inputDto.getPage(), inputDto.getSize());
                dataCount = varProcessTestResults.getExecuteNormalCount();
                break;
            case EXCEPTION:
                resultList = varProcessTestVariableResultService.findPageByResultIdInNormalAndExecutionStatus(varProcessTestResults.getId(), Integer.valueOf(TestExecStatusEnum.EXCEPTION.getCode()), inputDto.getPage(), inputDto.getSize());
                dataCount = varProcessTestResults.getExecuteExceptionCount();
                break;
            case CONSISTENT:
                resultList = varProcessTestVariableResultService.findPageByResultIdAndComparisonStatus(varProcessTestResults.getId(), Integer.valueOf(TestResultDiffStatusEnum.CONSISTENT.getCode()), inputDto.getPage(), inputDto.getSize());
                dataCount = varProcessTestResults.getExecuteResulteqCount();
                break;
            case INCONSISTENT:
                resultList = varProcessTestVariableResultService.findPageByResultIdAndComparisonStatus(varProcessTestResults.getId(), Integer.valueOf(TestResultDiffStatusEnum.INCONSISTENT.getCode()), inputDto.getPage(), inputDto.getSize());
                dataCount = varProcessTestResults.getExecuteResultneqCount();
                break;
            default:
                resultList = new ArrayList<>();
                break;
        }
        // 3.从测试数据集中提取内容
        List<Map<String, Object>> newDataList = new ArrayList<>();
        for (MongoVarProcessTestVariableResult result : resultList) {
            // 3.1 设置测试数据明细
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", result.getDataId());
            map.put("testSerialNo", result.getTestSerialNo());
            map.put("executionTime", result.getExecutionTime());
            if (result.getExecutionStatus() == MagicNumbers.ONE) {
                map.put("resultStatus", (MagicStrings.CURLY_BRACE.equals(result.getExpectContent())) ? Integer.valueOf(String.valueOf(MagicNumbers.TWO)) : result.getComparisonStatus());
            } else {
                map.put("resultStatus", result.getComparisonStatus());
            }
            map.put("executeStatus", result.getExecutionStatus());
            newDataList.add(map);
        }
        // 4.setter
        outputDto.setNewDataList(newDataList);
        outputDto.setTotalNums(dataCount);
    }

    /**
     * 根据testSerialNo和testType获取测试结果的详情(右侧)
     * @param testSerialNo 组件测试请求流水号
     * @param testType 测试类型
     * @return 测试结果的详情JSON字符串
     */
    public TestResultDetailOutputDto displayExpectationCompare(String testSerialNo, Integer testType) {
        // 1.先获取测试结果详情
        MongoVarProcessTestVariableResult testResultDetail = varProcessTestVariableResultService.getTestResultDetail(testSerialNo);
        TestResultDto item = TestResultDto.builder()
                .dataId(testResultDetail.getDataId()).batchNo(testResultDetail.getBatchNo()).testSerialNo(testResultDetail.getTestSerialNo()).inputContent(testResultDetail.getInputContent())
                .expectContent(testResultDetail.getExpectContent()).resultsContent(testResultDetail.getResultsContent()).originalContent(testResultDetail.getOriginalContent()).comparisonContent(testResultDetail.getComparisonContent())
                .resultStatus(testResultDetail.getComparisonStatus()).executeStatus(testResultDetail.getExecutionStatus()).exceptionMsg(testResultDetail.getExceptionMsg())
                .build();
        JSONObject jsonObject = TestTableDataUtil.mergeData(item);
        if (!StringUtils.isEmpty(testResultDetail.getDebugInfo())) {
            jsonObject.put("debugInfo", JSON.parseArray(testResultDetail.getDebugInfo()));
        }
        // 2.根据上面的json获得预期结果对比
        List<Map<String, Object>> comparisons = getComparisons(jsonObject, testType);
        return new TestResultDetailOutputDto(comparisons);
    }

    /**
     * 获取测试组件的tab名称列表
     * @param testSerialNo 流水号
     * @return tab名称列表
     */
    public List<Map<String, String>> getTestResultTabEnum(String testSerialNo) {
        // 1.通过testSerialNo获取它对应的组件类型(因为变量清单存在trace详情)
        Integer testType = varProcessTestVariableResultService.getTestTypeBySerialNo(testSerialNo);
        // 2.通过testSerialNo获取它这一行数据的测试结果
        MongoVarProcessTestVariableResult testResultDetail = varProcessTestVariableResultService.getTestResultDetail(testSerialNo);
        TestResultDto item = TestResultDto.builder()
                .dataId(testResultDetail.getDataId()).batchNo(testResultDetail.getBatchNo()).testSerialNo(testResultDetail.getTestSerialNo()).inputContent(testResultDetail.getInputContent())
                .expectContent(testResultDetail.getExpectContent()).resultsContent(testResultDetail.getResultsContent()).originalContent(testResultDetail.getOriginalContent()).comparisonContent(testResultDetail.getComparisonContent())
                .resultStatus(testResultDetail.getComparisonStatus()).executeStatus(testResultDetail.getExecutionStatus()).exceptionMsg(testResultDetail.getExceptionMsg())
                .build();
        JSONObject jsonObject = TestTableDataUtil.mergeData(item);
        if (!StringUtils.isEmpty(testResultDetail.getDebugInfo())) {
            jsonObject.put("debugInfo", JSON.parseArray(testResultDetail.getDebugInfo()));
        }
        JSONObject jsonResults = jsonObject.getJSONObject(MagicStrings.RESULTS);
        JSONObject jsonResponse = jsonResults.getJSONObject(MagicStrings.RESPONSE);
        // 3.开始判断是否需要添加预期结果对比
        List<Map<String, String>> result = new ArrayList<>();
        boolean flag = (testResultDetail.getExecutionStatus().equals(MagicNumbers.ONE) && testResultDetail.getComparisonStatus().equals(MagicNumbers.ZERO))
                || (testResultDetail.getExecutionStatus().equals(MagicNumbers.ONE) && testResultDetail.getComparisonStatus().equals(MagicNumbers.ONE));
        if (flag) {
            Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
            map.put("label", TestResultDetailTabEnum.EXPECTATION_RESULT_COMPARE.getDesc());
            map.put("code", TestResultDetailTabEnum.EXPECTATION_RESULT_COMPARE.toString());
            result.add(map);
        }
        // 4.判断是否要添加请求数据
        if (jsonResponse.getJSONObject(MagicStrings.INPUT) != null) {
            if (!jsonResponse.getJSONObject(MagicStrings.INPUT).toJSONString().equals(MagicStrings.CURLY_BRACE)) {
                Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
                map.put("label", TestResultDetailTabEnum.REQUEST_DATA.getDesc());
                map.put("code", TestResultDetailTabEnum.REQUEST_DATA.toString());
                result.add(map);
            }
        }
        // 5.判断是否需要添加引擎使用数据
        if (jsonResponse.getJSONObject(MagicStrings.RAW_DATA) != null) {
            if (!jsonResponse.getJSONObject(MagicStrings.RAW_DATA).toJSONString().equals(MagicStrings.CURLY_BRACE)) {
                Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
                map.put("label", TestResultDetailTabEnum.ENGINE_USED_DATA.getDesc());
                map.put("code", TestResultDetailTabEnum.ENGINE_USED_DATA.toString());
                result.add(map);
            }
        }
        // 6.判断是否需要添加输出结果
        if (jsonResponse.getJSONObject(MagicStrings.OUTPUT) != null) {
            if (!jsonResponse.getJSONObject(MagicStrings.OUTPUT).toJSONString().equals(MagicStrings.CURLY_BRACE)) {
                Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
                map.put("label", TestResultDetailTabEnum.OUTPUT_RESULT.getDesc());
                map.put("code", TestResultDetailTabEnum.OUTPUT_RESULT.toString());
                result.add(map);
            }
        }
        // 7.判断是否需要添加异常信息
        if (jsonResults.get(MagicStrings.EXCEPTION_MSG) != null) {
            if (!jsonResults.get(MagicStrings.EXCEPTION_MSG).toString().equals(MagicStrings.EMPTY_STRING)) {
                Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
                map.put("label", TestResultDetailTabEnum.EXCEPTION_MESSAGE.getDesc());
                map.put("code", TestResultDetailTabEnum.EXCEPTION_MESSAGE.toString());
                result.add(map);
            }
        }
        // 8.判断是否需要添加debug信息
        if (!StringUtils.isEmpty(testResultDetail.getDebugInfo())) {
            Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
            map.put("label", TestResultDetailTabEnum.DEBUG_INFO.getDesc());
            map.put("code", TestResultDetailTabEnum.DEBUG_INFO.toString());
            result.add(map);
        }
        // 9.判断是否需要添加trace详情
        if (testType.equals(MagicNumbers.THREE)) {
            Map<String, String> map = new LinkedHashMap<>(MagicNumbers.TWO);
            map.put("label", TestResultDetailTabEnum.TRACE_DETAIL.getDesc());
            map.put("code", TestResultDetailTabEnum.TRACE_DETAIL.toString());
            result.add(map);
        }
        return result;
    }

    /**
     * 按照tab的枚举，展示测试组件详情的内容
     * @param testSerialNo 组件测试请求流水号
     * @param tabEnum tab的枚举
     * @return 测试组件某个tab详情的内容
     */
    public String getTestResultDetailByTab(String testSerialNo, TestResultDetailTabEnum tabEnum) {
        // 1.先获取测试结果详情
        MongoVarProcessTestVariableResult testResultDetail = varProcessTestVariableResultService.getTestResultDetail(testSerialNo);
        TestResultDto item = TestResultDto.builder()
                .dataId(testResultDetail.getDataId()).batchNo(testResultDetail.getBatchNo()).testSerialNo(testResultDetail.getTestSerialNo()).inputContent(testResultDetail.getInputContent())
                .expectContent(testResultDetail.getExpectContent()).resultsContent(testResultDetail.getResultsContent()).originalContent(testResultDetail.getOriginalContent()).comparisonContent(testResultDetail.getComparisonContent())
                .resultStatus(testResultDetail.getComparisonStatus()).executeStatus(testResultDetail.getExecutionStatus()).exceptionMsg(testResultDetail.getExceptionMsg())
                .build();
        JSONObject jsonObject = TestTableDataUtil.mergeData(item);
        if (!StringUtils.isEmpty(testResultDetail.getDebugInfo())) {
            jsonObject.put("debugInfo", JSON.parseArray(testResultDetail.getDebugInfo()));
        }
        // 2.根据枚举值，来分别获取不同的JSON
        String result = null;
        JSONObject jsonResults = jsonObject.getJSONObject(MagicStrings.RESULTS);
        JSONObject jsonResponse = jsonResults.getJSONObject(MagicStrings.RESPONSE);
        switch (tabEnum) {
            case REQUEST_DATA:
                result = jsonResponse.getJSONObject(MagicStrings.INPUT).toJSONString();
                break;
            case ENGINE_USED_DATA:
                result = jsonResponse.getJSONObject(MagicStrings.RAW_DATA).toJSONString();
                break;
            case OUTPUT_RESULT:
                result = jsonResponse.getJSONObject(MagicStrings.OUTPUT).toJSONString();
                break;
            case EXCEPTION_MESSAGE:
                result = jsonResults.get(MagicStrings.EXCEPTION_MSG).toString();
                break;
            case DEBUG_INFO:
                result = testResultDetail.getDebugInfo();
                break;
            default:
                result = MagicStrings.EMPTY_STRING;
        }
        return result;
    }

    /**
     * 获取新的预期结果比对
     * @param jsonObject jsonObject对象
     * @param testType 测试数据集的类型
     * @return 新的预期结果比对
     */
    private static List<Map<String, Object>> getComparisons(JSONObject jsonObject, Integer testType) {
        // 1.定义最终的结果和预期结果对比的实体类
        List<Map<String, Object>> comparisons = new ArrayList<>();
        JSONObject object = (JSONObject) jsonObject.get(TestTableEnum.MASTER.getCode());
        Map<String, Object> output = new LinkedHashMap<>();
        Map<String, Object> expect = new LinkedHashMap<>();
        // 2.根据类型，获取实际输出和期望
        if (testType.equals(MagicNumbers.THREE)) {
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                String key = entry.getKey();
                if (key.contains(MagicStrings.PREFIX_RESULT_OUTPUT)) {
                    int index = key.lastIndexOf(MagicStrings.RESULT_DOT) + MagicStrings.RESULT_DOT.length();
                    String subKey = key.substring(index);
                    output.put(subKey, entry.getValue());
                }
                if (key.contains(MagicStrings.PREFIX_EXPECT_OUTPUT)) {
                    int index = key.lastIndexOf(MagicStrings.EXPECT_DOT) + MagicStrings.EXPECT_DOT.length();
                    String subKey = key.substring(index);
                    expect.put(subKey, entry.getValue());
                }
            }
        } else {
            for (Map.Entry<String, Object> entry : object.entrySet()) {
                String key = entry.getKey();
                if (key.contains(MagicStrings.RESULT_DOT)) {
                    int index = key.lastIndexOf(MagicStrings.RESULT_DOT) + MagicStrings.RESULT_DOT.length();
                    String subKey = key.substring(index);
                    output.put(subKey, entry.getValue());
                }
                if (key.contains(MagicStrings.EXPECT_DOT)) {
                    int index = key.lastIndexOf(MagicStrings.EXPECT_DOT) + MagicStrings.EXPECT_DOT.length();
                    String subKey = key.substring(index);
                    expect.put(subKey, entry.getValue());
                }
            }
        }
        // 3.进行对比,获取最终的结果
        for (Map.Entry<String, Object> entry : expect.entrySet()) {
            Map<String, Object> comparison = new LinkedHashMap<>();
            String key = entry.getKey();
            comparison.put(MagicStrings.INDICATOR_NAME, key);
            Object left = output.get(key), right = entry.getValue();
            comparison.put(MagicStrings.ACTUAL_RESULT, left);
            comparison.put(MagicStrings.EXCEPT_RESULT, right);
            if (left == null || right == null) {
                comparison.put(MagicStrings.COMPARISON_RESULT, MagicStrings.NOT_EQUAL);
            } else if (left.equals(right)) {
                comparison.put(MagicStrings.COMPARISON_RESULT, MagicStrings.EQUAL);
            } else {
                comparison.put(MagicStrings.COMPARISON_RESULT, compareArrResults(left, right)
                        ? MagicStrings.EQUAL : MagicStrings.NOT_EQUAL);

            }
            comparisons.add(comparison);
        }
        return comparisons;
    }

    /**
     * 比较两个数组字符串是否数值上相等
     * eg: [3.00,2.00,1.00] & [3,2,1]
     * @param left
     * @param right
     * @return true or false
     */
    private static Boolean compareArrResults(Object left, Object right) {
        String[] leftArr = left.toString().split(StringPool.COMMA);
        String[] rightArr = right.toString().split(StringPool.COMMA);
        if (leftArr.length != rightArr.length) {
            return false;
        }

        for (int i = 0; i < leftArr.length; i++) {
            String leftStr = leftArr[i];
            String rightStr = rightArr[i];

            if (!NumberUtil.isNumber(leftStr) || !NumberUtil.isNumber(rightStr)) {
                return false;
            }

            BigDecimal leftNumber = new BigDecimal(leftStr);
            BigDecimal rightNumber = new BigDecimal(rightStr);

            int comparisonResult = leftNumber.compareTo(rightNumber);

            if (comparisonResult != MagicNumbers.ZERO) {
                return false;
            }
        }
        return true;
    }

    /**
     * 导出测试结果
     *
     * @param state    状态
     * @param id       结果集ID
     * @param response 响应实体
     */
    public void downExecuteResult(String state, Long id, HttpServletResponse response) {
        // 1.获取响应头
        String resultsHeader = varProcessTestVariableResultHeaderService.findHeaderByResultId(id);
        // 2.把header转换成map集合
        Map<String, List<Map<String, Object>>> header = JSONObject.parseObject(resultsHeader,
                new TypeReference<Map<String, List<Map<String, Object>>>>() {
                });
        // 3.根据不同的状态进行不同状态的查询
        TestResultsQueryTypeEnum code = TestResultsQueryTypeEnum.getCode(state);
        if (code == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "查询条件参数不正确");
        }
        // 4.查询MongoDB/mysql，获取结果集
        List<MongoVarProcessTestVariableResult> resultList = Collections.emptyList();
        switch (code) {
            case ALL:
                resultList = varProcessTestVariableResultService.findAllByResultId(id);
                break;
            case NORMAL:
                resultList = varProcessTestVariableResultService.findAllByResultIdAndExecutionStatus(id,
                        Integer.valueOf(TestExecStatusEnum.NORMAL.getCode()));
                break;
            case EXCEPTION:
                resultList = varProcessTestVariableResultService.findAllByResultIdAndExecutionStatus(id,
                        Integer.valueOf(TestExecStatusEnum.EXCEPTION.getCode()));
                break;
            case CONSISTENT:
                resultList = varProcessTestVariableResultService.findAllByResultIdAndComparisonStatus(id,
                        Integer.valueOf(TestResultDiffStatusEnum.CONSISTENT.getCode()));
                break;
            case INCONSISTENT:
                resultList = varProcessTestVariableResultService.findAllByResultIdAndComparisonStatus(id,
                        Integer.valueOf(TestResultDiffStatusEnum.INCONSISTENT.getCode()));
                break;
            default:
                break;
        }
        // 5.把结果集转换成JSON的list
        List<JSONObject> dataList = new ArrayList<>();
        for (MongoVarProcessTestVariableResult result : resultList) {
            JSONObject data = TestTableDataUtil.mergeData(TestResultDto.builder().dataId(result.getDataId()).batchNo(result.getBatchNo())
                    .testSerialNo(result.getTestSerialNo()).inputContent(result.getInputContent()).expectContent(result.getExpectContent())
                    .resultsContent(result.getResultsContent()).originalContent(result.getOriginalContent()).comparisonContent(result.getComparisonContent())
                    .resultStatus(result.getComparisonStatus()).executeStatus(result.getExecutionStatus()).exceptionMsg(result.getExceptionMsg()).build());
            dataList.add(data);
        }
        // 6.写入到Excel的List集合
        List<TestExcelDto> list = TestTableExportUtil.exportTestResultsData(header, dataList);
        // 7.导出excel数据
        SXSSFWorkbook xssfWorkbook = null;
        OutputStream outputStream = null;
        try {
            xssfWorkbook = TestExcelUtils.getExportExcelWb(list);

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
            String fileName = simpleDateFormat.format(date);
            TestExcelUtils.setResponseHeader(response, fileName + "_testResult.xlsx");
            outputStream = response.getOutputStream();
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
     * transferExpect
     *
     * @param id 入参
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void transferExpect(Long id) {
        VarProcessTestResults varProcessTestResults = varProcessTestVariableResultsService.getById(id);
        VarProcessTest originalVarProcessTest = varProcessTestVariableService.getOne(Wrappers.<VarProcessTest>lambdaQuery()
                .select(VarProcessTest::getId, VarProcessTest::getIdentifier, VarProcessTest::getVariableId,
                        VarProcessTest::getSource, VarProcessTest::getTestType, VarProcessTest::getVarProcessSpaceId)
                .eq(VarProcessTest::getId, varProcessTestResults.getTestId()));
        int dataCount = varProcessTestVariableResultService.countByResultIdAndExecutionStatus(id,
                Integer.valueOf(TestExecStatusEnum.NORMAL.getCode()));
        if (dataCount == 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未查询到正常执行的数据，不允许保存为预期结果。");
        }

        //获取处理预期结果表头
        String headerByResultId = varProcessTestVariableResultHeaderService.findHeaderByResultId(id);
        JSONObject originalHeaderObj = JSONObject.parseObject(headerByResultId);
        JSONObject targetExpectHeader = TestTableHeaderUtil.transferResultToExpect(originalHeaderObj);

        //保存数据集
        VarProcessTest varProcessTest = new VarProcessTest();
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME);
        varProcessTest.setIdentifier(originalVarProcessTest.getIdentifier());
        varProcessTest.setVariableId(originalVarProcessTest.getVariableId());
        varProcessTest.setSource(originalVarProcessTest.getSource());
        varProcessTest.setDataCount(dataCount);
        varProcessTest.setTestType(originalVarProcessTest.getTestType());

        varProcessTest.setTableHeaderField(JSONObject.toJSONString(targetExpectHeader));
        varProcessTest.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTest.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setVarProcessSpaceId(originalVarProcessTest.getVarProcessSpaceId());
        varProcessTestVariableService.save(varProcessTest);

        int seqNo = getTestCount(originalVarProcessTest.getVarProcessSpaceId(), varProcessTest.getIdentifier());
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTest.setSeqNo(seqNo);
        varProcessTestVariableService.updateById(varProcessTest);

        int pageSize = MagicNumbers.THOUSAND;
        int pages = dataCount % pageSize == 0 ? dataCount / pageSize : dataCount / pageSize + 1;

        for (int page = 0; page < pages; page++) {
            List<MongoVarProcessTestVariableResult> resultList = varProcessTestVariableResultService.findPageByResultIdAndExecutionStatus(id,
                    Integer.valueOf(TestExecStatusEnum.NORMAL.getCode()), page, pageSize);

            List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = new ArrayList<>();
            for (MongoVarProcessTestVariableResult result : resultList) {

                // 创建策略测试数据文档并添加到列表
                MongoVarProcessTestVariableData testStrategyData = MongoVarProcessTestVariableData.builder().testId(varProcessTest.getId())
                        .dataId(result.getDataId()).createdTime(new Date()).inputContent(result.getInputContent())
                        .expectContent(result.getResultsContent()).build();

                mongoVarProcessTestVariableDataList.add(testStrategyData);
            }
            varProcessTestVariableDataService.saveBatch(mongoVarProcessTestVariableDataList);
        }
    }

}
