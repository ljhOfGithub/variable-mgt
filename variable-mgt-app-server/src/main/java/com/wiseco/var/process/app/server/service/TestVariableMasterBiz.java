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

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.TestExcelUtils;
import com.wiseco.var.process.app.server.commons.test.TestFormUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableDataUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableExportUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableHeaderUtil;
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestCollectUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestDetailInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.TestFormUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TestDetailOutputDto;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestDataUpdateTypeEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.TestCollectAndResultsDto;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wiseco.var.process.app.server.service.dto.VariableBaseDetailDto;
import com.wiseco.var.process.app.server.service.multipleimpl.VarProcessTestVariableDataService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wangxianli
 * @since 2021/11/30
 */
@Service
@Slf4j
public class TestVariableMasterBiz extends TestVariablePrivate {

    @Autowired
    private VarProcessTestService varProcessTestVariableService;

    @Autowired
    private UserService userService;

    /**
     * 基于 MongoDB/mysql 访问测试数据集和执行结果的相关服务
     */
    @Resource
    private VarProcessTestVariableDataService varProcessTestVariableDataService;

    /**
     * 分页查询测试数据集及其测试结果的业务逻辑
     *
     * @param inputDto 前端送过来的实体对象
     * @return 分页查询的结果
     */
    public IPage<TestCollectAndResultsDto> findTestList(TestCollectInputDto inputDto) {

        // 1.获取变量的基础信息
        VariableBaseDetailDto variableBaseDetail = getVariableBaseDetail(inputDto.getTestType(), inputDto.getId());

        // 2.分页设置
        Page<TestCollectAndResultsDto> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());

        // 3.分页查询测试数据集
        IPage<TestCollectAndResultsDto> pageByVariable = varProcessTestVariableService.findPageByVariableIdAndIdentifier(page, inputDto.getSpaceId(),
                inputDto.getTestType(), inputDto.getId(), variableBaseDetail.getIdentifier());

        // 4.对结果集进行后处理
        if (!CollectionUtils.isEmpty(pageByVariable.getRecords())) {
            List<String> userNames = pageByVariable.getRecords().stream().flatMap(item -> Stream.of(item.getCreatedUser(), item.getUpdatedUser())).distinct().collect(Collectors.toList());
            Map<String, String> userMap = userService.findFullNameMapByUserNames(userNames);
            // 4.1 遍历分页查询的结果集
            for (TestCollectAndResultsDto dto : pageByVariable.getRecords()) {
                dto.setCreatedUser(userMap.get(dto.getCreatedUser()));
                if (StringUtils.isEmpty(dto.getSuccessRate())) {
                    dto.setResultId(null);
                    dto.setExecuteTime(null);
                    dto.setUpdatedUser(null);
                    dto.setChangeNum(null);
                    dto.setTestTime(null);
                } else {
                    String defaultNa = CommonConstant.DEFAULT_TEST_NA;
                    if (org.springframework.util.StringUtils.isEmpty(dto.getSuccessRate())) {
                        dto.setSuccessRate(defaultNa);
                    }
                    if (!defaultNa.equals(dto.getSuccessRate())) {
                        // 测试数据集设置预期结果: 向测试成功率追加 "%"
                        dto.setSuccessRate(dto.getSuccessRate() + "%");
                    }
                    if (!StringUtils.isEmpty(dto.getUpdatedUser())) {
                        dto.setUpdatedUser(userMap.get(dto.getUpdatedUser()));
                    }
                }
            }
        }

        return pageByVariable;
    }

    /**
     * 获取测试数据的明细
     *
     * @param inputDto 前端的输入实体
     * @return  TestDetailOutputDto
     */
    public TestDetailOutputDto findTestDetailList(TestDetailInputDto inputDto) {
        // 1.定义返回实体
        TestDetailOutputDto outputDto = new TestDetailOutputDto();
        // 2.根据数据集ID查询数据集信息
        VarProcessTest varProcessTest = varProcessTestVariableService.getById(inputDto.getTestId());
        JSONObject header = new JSONObject();
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(inputDto.getSpaceId(), inputDto.getTestType(), inputDto.getId());
        // 2.1引用衍生变量
        if (inputDto.getTestType().equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(inputDto.getId());
            if (varsDataModelMap != null && varsDataModelMap.size() > 0) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        // 2.2改成获取最新的变量作为表头
        List<TestFormDto> strComVar = getVariableVarInput(inputDto.getTestType(), inputDto.getId(), dataModel);
        // 2.3追加预期结果
        if (!StringUtils.isEmpty(varProcessTest.getTableHeaderField())) {
            List<TestFormDto> outputFormDtoList;
            if (inputDto.getTestType().equals(TestVariableTypeEnum.MANIFEST.getCode())) {
                outputFormDtoList = getManifestOutputVariable(inputDto.getId());
            } else {
                outputFormDtoList = getVariableVarOutput(inputDto.getTestType(), inputDto.getId(), dataModel);
            }
            List<TestFormDto> expectFormList = TestFormUtil.transferComponentExpectToForm(outputFormDtoList,
                    JSON.parseObject(varProcessTest.getTableHeaderField()));
            if (!CollectionUtils.isEmpty(expectFormList)) {
                strComVar.addAll(expectFormList);
            }
        }
        if (!CollectionUtils.isEmpty(strComVar)) {
            JSONObject testData = TestTableHeaderUtil.getTestData(strComVar, dataModel, 1);
            header = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
        }
        outputDto.setTableHeaderField(TestTableHeaderUtil.headerSort(header));
        List<String> contentList = new ArrayList<>();
        // 3.遍历并添加测试数据集分页查询内容
        List<MongoVarProcessTestVariableData> pageList = varProcessTestVariableDataService.findPageByTestId(varProcessTest.getId(),
                inputDto.getPage(), inputDto.getSize());
        // 3.2转化为JSON
        for (MongoVarProcessTestVariableData mongoVarProcessTestVariableData : pageList) {
            JSONObject mergeData = TestTableDataUtil.mergeData(mongoVarProcessTestVariableData.getDataId(), mongoVarProcessTestVariableData.getInputContent(),
                    mongoVarProcessTestVariableData.getExpectContent());
            contentList.add(mergeData.toJSONString());
        }
        outputDto.setDataList(contentList);
        outputDto.setTotalNums(varProcessTest.getDataCount());
        return outputDto;
    }

    /**
     * 更新详情
     * @param inputDto 输入实体类对象
     */
    public void updateDetails(TestFormUpdateInputDto inputDto) {
        // 从入参 DTO 获取数据集 ID 和选中的数据行序列号
        Long testId = inputDto.getTestId();
        Integer dataId = Integer.valueOf(inputDto.getDataId());
        //根据数据集ID查询数据集信息
        VarProcessTest varProcessTest = varProcessTestVariableService.getOne(Wrappers.<VarProcessTest>lambdaQuery().select(VarProcessTest::getId, VarProcessTest::getDataCount).eq(VarProcessTest::getId, testId));
        //处理预期结果
        JSONObject targetExpectHeader = new JSONObject();
        if (!CollectionUtils.isEmpty(inputDto.getExpectHeader())) {
            targetExpectHeader = tansferExpectHeader(inputDto.getTestType(), inputDto.getSpaceId(), inputDto.getId(), inputDto.getExpectHeader());
        }
        VarProcessTest varProcessTestUpdated = new VarProcessTest();
        varProcessTestUpdated.setId(varProcessTest.getId());
        varProcessTestUpdated.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        if (TestDataUpdateTypeEnum.DELETE.getCode().equals(inputDto.getUpdateType())) {
            // 删除指定单条数据
            varProcessTestUpdated.setDataCount(varProcessTest.getDataCount() - 1);
            varProcessTestVariableDataService.deleteOneByTestDataId(testId, dataId);
        } else if (TestDataUpdateTypeEnum.UPDATE.getCode().equals(inputDto.getUpdateType())) {
            // 更新指定单条数据
            varProcessTestUpdated.setTableHeaderField(targetExpectHeader.toJSONString());
            // 重新组装表单数据JSON
            JSONObject resultJson = TestFormUtil.mergeFormData(inputDto.getInputData(), inputDto.getExpectData(), String.valueOf(dataId));
            String inputContent = null;
            String expectContent = null;
            if (resultJson.containsKey(TestTableEnum.INPUT.getCode())) {
                inputContent = resultJson.getJSONObject(TestTableEnum.INPUT.getCode()).toJSONString();
            }
            if (resultJson.containsKey(TestTableEnum.EXPECT.getCode())) {
                expectContent = resultJson.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString();
            }
            varProcessTestVariableDataService.updateOneByTestDataId(testId, dataId, inputContent, expectContent);
        } else {
            // 添加, 复制指定单条数据
            varProcessTestUpdated.setDataCount(varProcessTest.getDataCount() + 1);
            // 从 /mysql 获取当前数据集最大的序列号
            Integer maxDataId = varProcessTestVariableDataService.findMaxDataIdByTestId(testId);
            if (maxDataId == null) {
                maxDataId = 0;
            }
            dataId = maxDataId + 1;
            // 修改输入数据集单条数据 JSON 的相关 ID
            JSONObject resultJson = new JSONObject();
            if (TestDataUpdateTypeEnum.ADD.getCode().equals(inputDto.getUpdateType())) {
                varProcessTestUpdated.setTableHeaderField(targetExpectHeader.toJSONString());
                // 重新组装表单数据JSON
                resultJson = TestFormUtil.mergeFormData(inputDto.getInputData(), inputDto.getExpectData(), String.valueOf(dataId));
                String inputContent = null;
                String expectContent = null;
                if (resultJson.containsKey(TestTableEnum.INPUT.getCode())) {
                    inputContent = resultJson.getJSONObject(TestTableEnum.INPUT.getCode()).toJSONString();
                }
                if (resultJson.containsKey(TestTableEnum.EXPECT.getCode())) {
                    expectContent = resultJson.getJSONObject(TestTableEnum.EXPECT.getCode()).toJSONString();
                }
                // 将一条数据明细保存至 MongoDB/mysql
                varProcessTestVariableDataService.saveOne(testId, dataId, inputContent, expectContent);
            } else {
                MongoVarProcessTestVariableData mongoVarProcessTestVariableData = varProcessTestVariableDataService.findOneByTestDataId(
                        inputDto.getTestId(), Integer.parseInt(inputDto.getDataId()));
                String inputContent = null;
                String expectContent = null;
                if (!StringUtils.isEmpty(mongoVarProcessTestVariableData.getInputContent())) {
                    resultJson = JSONObject.parseObject(mongoVarProcessTestVariableData.getInputContent());
                    TestTableDataUtil.resetIdByJsonData(resultJson, dataId);
                    inputContent = resultJson.toJSONString();
                }
                if (!StringUtils.isEmpty(mongoVarProcessTestVariableData.getExpectContent())) {
                    resultJson = JSONObject.parseObject(mongoVarProcessTestVariableData.getExpectContent());
                    TestTableDataUtil.resetIdByJsonData(resultJson, dataId);
                    expectContent = resultJson.toJSONString();
                }
                // 将一条数据明细保存至 MongoDB/mysql
                varProcessTestVariableDataService.saveOne(testId, dataId, inputContent, expectContent);
            }

        }
        varProcessTestVariableService.updateById(varProcessTestUpdated);
    }

    /**
     * 导出数据的明细
     *
     * @param spaceId  变量空间ID
     * @param testId   测试集ID
     * @param testType 测试类型：1-变量，2-公共函数，3-服务接口
     * @param id       变量/公共函数ID/接口ID
     * @param response 响应体
     */
    public void downTestDetails(Long spaceId, Long testId, Integer testType, Long id, HttpServletResponse response) {
        // 1.查询数据集信息
        VarProcessTest varProcessTest = varProcessTestVariableService.getById(testId);
        if (null == varProcessTest) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "未查询到相关数据集明细！");
        }
        // 2.获取数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getDataModelMapBySpaceId(spaceId, testType, id);
        // 3.引用衍生变量
        if (testType.equals(TestVariableTypeEnum.VAR.getCode())) {
            Map<String, DomainDataModelTreeDto> varsDataModelMap = getVarsDataModelMap(id);
            if (!CollectionUtils.isEmpty(varsDataModelMap)) {
                dataModel.putAll(varsDataModelMap);
            }
        }
        // 4.获取表头信息
        Map<String, List<Map<String, Object>>> header = new HashMap<>(MagicNumbers.EIGHT);
        List<TestFormDto> strComVar = getVariableVarInputInExcel(testType, id, dataModel);
        if (!CollectionUtils.isEmpty(strComVar)) {
            JSONObject testData = TestTableHeaderUtil.getTestData(strComVar, dataModel, 1);
            JSONObject headerJsonObj = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
            headerJsonObj = TestTableHeaderUtil.headerSort(headerJsonObj);
            header = TestTableHeaderUtil.transferJsonToMap(headerJsonObj);
        }
        // 5.查询测试数据,并组装为JSON对象
        List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList = varProcessTestVariableDataService.findAllByTestId(varProcessTest
                .getId());
        List<JSONObject> dataList = new ArrayList<>();
        for (MongoVarProcessTestVariableData data : mongoVarProcessTestVariableDataList) {
            dataList.add(TestTableDataUtil.mergeData(data.getDataId(), data.getInputContent(), data.getExpectContent()));
        }
        // 6.组装下载数据
        List<TestExcelDto> list = TestTableExportUtil.exportTestDetail(header, varProcessTest.getTableHeaderField(), dataList);
        // 7.导出excel数据
        SXSSFWorkbook xssfWorkbook = null;
        OutputStream outputStream = null;
        try {
            xssfWorkbook = TestExcelUtils.getExportExcelWb(list);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
            String fileName = simpleDateFormat.format(date);
            TestExcelUtils.setResponseHeader(response, fileName + "-testData.xlsx");
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
     * 通过Id，逻辑删除Test数据
     * @param ids id的数组
     */
    public void updateDeleteFlagTestById(Integer[] ids) {


        // 更新删除标识 (逻辑删除)
        varProcessTestVariableService.update(new UpdateWrapper<VarProcessTest>()
                .lambda()
                .in(VarProcessTest::getId, Arrays.asList(ids))
                .set(VarProcessTest::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));

        // 在 MongoDB 删除相关测试数据 (物理删除)
        // TODO: 实现 MySQL test_* 和 test_*_results 记录的物理删除
        // testVariableDataMongoService.deleteAllByTestId(id);
    }

    /**
     * 通过Id复制测试对象
     * @param testId 测试Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyTestById(Long testId) {
        // 1. 记录复制: RDBMS
        // 根据数据集 ID 查询数据集信息
        VarProcessTest test = varProcessTestVariableService.getById(testId);

        if (null == test) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "数据集信息不存在！");
        }

        // 保存数据集
        VarProcessTest varProcessTest = new VarProcessTest();
        varProcessTest.setVarProcessSpaceId(test.getVarProcessSpaceId());
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME);
        varProcessTest.setIdentifier(test.getIdentifier());
        varProcessTest.setVariableId(test.getVariableId());
        varProcessTest.setTestType(test.getTestType());
        varProcessTest.setSource(test.getSource());
        varProcessTest.setDataCount(test.getDataCount());
        varProcessTest.setTableHeaderField(test.getTableHeaderField());
        varProcessTest.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        varProcessTest.setCreatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessTest.setRemark(test.getRemark());
        varProcessTestVariableService.save(varProcessTest);

        // 更新测试集
        int seqNo = getTestCount(test.getVarProcessSpaceId(), test.getIdentifier());
        varProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        varProcessTest.setSeqNo(seqNo);
        varProcessTestVariableService.updateById(varProcessTest);

        // 2. 数据复制: MongoDB
        varProcessTestVariableDataService.duplicateDataSetByTestId(testId, varProcessTest.getId());
    }

    /**
     * 更新数据集
     *
     * @param testCollectUpdateInputDto 前端传过来的实体对象
     */
    public void updateTestById(TestCollectUpdateInputDto testCollectUpdateInputDto) {

        // 1.修改数据集
        VarProcessTest varProcessTest = new VarProcessTest();
        varProcessTest.setName(testCollectUpdateInputDto.getName());
        varProcessTest.setRemark(testCollectUpdateInputDto.getRemark());
        varProcessTest.setId(testCollectUpdateInputDto.getId());
        varProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());

        // 2.保存修改
        varProcessTestVariableService.updateById(varProcessTest);
    }

    /**
     * 合并测试数据集
     * @param idArray id的数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void mergeTest(Integer[] idArray) {
        // 转换测试数据集 ID 数组为列表, 对列表进行排序
        List<Integer> testIdList = Arrays.asList(idArray);
        Collections.sort(testIdList);

        // 从 RDBMS 查询策略测试数据集记录
        List<VarProcessTest> varProcessTestList = varProcessTestVariableService.listByIds(testIdList);
        if (varProcessTestList.isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.TEST_RESULT_NOT_FOUND, "数据集信息不存在！");
        }

        // 仅允许相同来源数据进行合并
        Set<String> sourceSet = new HashSet<>();
        varProcessTestList.forEach(testVariable -> sourceSet.add(testVariable.getSource()));
        if (sourceSet.size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择合并的数据集存来源不同, 不允许合并！");
        }

        //合并预期结果表头字段
        int totalDataCount = 0;
        JSONObject expectHeaderJson = new JSONObject();
        for (VarProcessTest test : varProcessTestList) {
            totalDataCount += test.getDataCount();
            if (StringUtils.isEmpty(test.getTableHeaderField())) {
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(test.getTableHeaderField());
            if (expectHeaderJson.size() > 0) {
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    if (!expectHeaderJson.containsKey(entry.getKey())) {
                        expectHeaderJson.put(entry.getKey(), entry.getValue());
                    } else {
                        JSONArray expectHeaderArray = expectHeaderJson.getJSONArray(entry.getKey());
                        List<String> expectIndexList = new ArrayList<>();
                        for (int i = 0; i < expectHeaderArray.size(); i++) {
                            expectIndexList.add(expectHeaderArray.getJSONObject(i).getString("index"));
                        }
                        JSONArray sourceJsonArray = jsonObject.getJSONArray(entry.getKey());
                        for (int i = 0; i < sourceJsonArray.size(); i++) {
                            JSONObject sourceJsonObject = sourceJsonArray.getJSONObject(i);
                            if (!expectIndexList.contains(sourceJsonObject.getString("index"))) {
                                expectHeaderArray.add(sourceJsonObject);
                            }

                        }
                    }
                }
            } else {
                expectHeaderJson.putAll(jsonObject);
            }
        }

        // 保存合并后的测试数据集记录
        VarProcessTest mergedVarProcessTest = new VarProcessTest();
        mergedVarProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME);
        mergedVarProcessTest.setIdentifier(varProcessTestList.get(0).getIdentifier());
        mergedVarProcessTest.setVariableId(varProcessTestList.get(0).getVariableId());
        mergedVarProcessTest.setTestType(varProcessTestList.get(0).getTestType());
        mergedVarProcessTest.setSource(varProcessTestList.get(0).getSource());
        mergedVarProcessTest.setDataCount(totalDataCount);
        if (expectHeaderJson.size() > 0) {
            mergedVarProcessTest.setTableHeaderField(JSON.toJSONString(expectHeaderJson));
        }
        mergedVarProcessTest.setDeleteFlag(DeleteFlagEnum.USABLE.getCode());
        mergedVarProcessTest.setCreatedUser(SessionContext.getSessionUser().getUsername());
        mergedVarProcessTest.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        mergedVarProcessTest.setVarProcessSpaceId(varProcessTestList.get(0).getVarProcessSpaceId());
        varProcessTestVariableService.save(mergedVarProcessTest);

        int seqNo = getTestCount(mergedVarProcessTest.getVarProcessSpaceId(), mergedVarProcessTest.getIdentifier());
        mergedVarProcessTest.setName(CommonConstant.DEFAULT_TEST_NAME + seqNo);
        mergedVarProcessTest.setSeqNo(seqNo);
        varProcessTestVariableService.updateById(mergedVarProcessTest);

        List<MongoVarProcessTestVariableData> mergedMongoVarProcessTestVariableDataList = getVarProcessTestVariableDataList(varProcessTestList, mergedVarProcessTest);
        // 批量保存合并的数据集列表
        varProcessTestVariableDataService.saveBatch(mergedMongoVarProcessTestVariableDataList);
    }

    /**
     * getVarProcessTestVariableDataList
     * @param varProcessTestList
     * @param mergedVarProcessTest
     * @return VarProcessTestVariableData List
     */
    private List<MongoVarProcessTestVariableData> getVarProcessTestVariableDataList(List<VarProcessTest> varProcessTestList, VarProcessTest mergedVarProcessTest) {
        // 更新 MongoDB 中的数据记录
        // 记录合并的数据集明细 ID
        int mergedDataIdCounter = 1;
        // 合并的数据集记录列表
        List<MongoVarProcessTestVariableData> mergedMongoVarProcessTestVariableDataList = new ArrayList<>();
        // 遍历待合并的子数据集记录实体对象
        for (VarProcessTest test : varProcessTestList) {
            // 根据 testId 从 MongoDB 查找待合并的子数据集
            Long testId = Long.parseLong(String.valueOf(test.getId()));
            List<MongoVarProcessTestVariableData> subTestDataSet = varProcessTestVariableDataService.findAllByTestId(testId);
            for (MongoVarProcessTestVariableData mongoVarProcessTestVariableData : subTestDataSet) {
                // 序列化数据内容明细, 重置序列号
                String inputContent = null;
                if (!StringUtils.isEmpty(mongoVarProcessTestVariableData.getInputContent())) {
                    JSONObject revisedInputContent = JSON.parseObject(mongoVarProcessTestVariableData.getInputContent());
                    TestTableDataUtil.resetIdByJsonData(revisedInputContent, mergedDataIdCounter);

                    inputContent = revisedInputContent.toJSONString();
                }

                String expectContent = null;
                if (!StringUtils.isEmpty(mongoVarProcessTestVariableData.getExpectContent())) {
                    JSONObject revisedExpectContent = JSON.parseObject(mongoVarProcessTestVariableData.getExpectContent());
                    TestTableDataUtil.resetIdByJsonData(revisedExpectContent, mergedDataIdCounter);
                    expectContent = revisedExpectContent.toJSONString();
                }
                // 创建新的变量或公共函数测试数据集实体, 设定新的属性, 添加到列表
                MongoVarProcessTestVariableData mergedMongoVarProcessTestVariableData = MongoVarProcessTestVariableData.builder().testId(mergedVarProcessTest.getId()).dataId(mergedDataIdCounter).createdTime(new Date()).inputContent(inputContent).expectContent(expectContent).build();
                mergedMongoVarProcessTestVariableDataList.add(mergedMongoVarProcessTestVariableData);

                // 更新合并数据集明细 ID
                mergedDataIdCounter++;
            }
        }
        return mergedMongoVarProcessTestVariableDataList;
    }
}
