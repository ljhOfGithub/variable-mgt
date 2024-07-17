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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.decision.jsonschema.util.enums.JsonSchemaFieldEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.commons.BeanCopyUtils;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.test.TestFormUtil;
import com.wiseco.var.process.app.server.commons.test.TestTableHeaderUtil;
import com.wiseco.var.process.app.server.commons.test.dto.TestFormDictDto;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.DomainModeTypeEnum;
import com.wiseco.var.process.app.server.enums.InputExpectTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.StrComVarFlagEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.test.TestHeaderValueEnum;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.converter.VariableDataModelConverter;
import com.wiseco.var.process.app.server.service.dto.DictDetailsDto;
import com.wiseco.var.process.app.server.service.dto.TestFormDto;
import com.wiseco.var.process.app.server.service.dto.VariableBaseDetailDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestSupportBiz;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

/**
 * @author wangxianli
 * @since 2021/11/30
 */
@Service
@Slf4j
public class TestVariablePrivate {
    private static final String INT_1 = "1";
    private static final String INT_0 = "0";
    private static final String REPLACEMENT = "";
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;
    @Autowired
    private VarProcessTestVarService varProcessTestVariableVarService;
    @Autowired
    private VarProcessTestService varProcessTestVariableService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessManifestService varProcessManifestService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VariableDataModelConverter variableDataModelConverter;
    @Autowired
    private VarProcessDictService varProcessDictService;
    @Autowired
    private VariableManifestSupportBiz variableManifestSupportBiz;

    private static void handleDataModelMap(String name, DomainDataModelTreeDto dto, Map<String, DomainDataModelTreeDto> map) {
        List<DomainDataModelTreeDto> children = dto.getChildren();

        //保留原始数据
        if (INT_0.equals(dto.getIsExtend())) {
            map.put(name, dto);
        }

        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (DomainDataModelTreeDto treeDto : children) {
            handleDataModelMap(name + "." + treeDto.getName(), treeDto, map);
        }

    }

    protected VariableBaseDetailDto getVariableBaseDetail(Integer testType, Long id) {

        VariableBaseDetailDto detailDto = new VariableBaseDetailDto();

        if (testType.equals(TestVariableTypeEnum.VAR.getCode())) {
            // 获取变量内容
            VarProcessVariable variable = varProcessVariableService.getById(id);
            BeanUtils.copyProperties(variable, detailDto);

            detailDto.setSpaceId(variable.getVarProcessSpaceId());
            detailDto.setName(variable.getLabel());

        } else if (testType.equals(TestVariableTypeEnum.FUNCTION.getCode())) {
            // 获取变量内容
            VarProcessFunction function = varProcessFunctionService.getById(id);
            BeanUtils.copyProperties(function, detailDto);

            detailDto.setDataType(function.getFunctionDataType());
            detailDto.setSpaceId(function.getVarProcessSpaceId());
        } else if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            // 获取变量内容
            VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                    .select(VarProcessManifest::getId, VarProcessManifest::getDeleteFlag, VarProcessManifest::getVarProcessSpaceId, VarProcessManifest::getIdentifier, VarProcessManifest::getVarManifestName, VarProcessManifest::getVersion, VarProcessManifest::getState)
                    .eq(VarProcessManifest::getId, id));
            if (manifestEntity == null || manifestEntity.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "变量清单不存在!");
            }
            detailDto.setSpaceId(manifestEntity.getVarProcessSpaceId());
            detailDto.setId(id);
            //服务ID
            detailDto.setIdentifier(manifestEntity.getIdentifier());
            detailDto.setLabel(manifestEntity.getVarManifestName());
            detailDto.setVersion(manifestEntity.getVersion());
            detailDto.setName(manifestEntity.getVarManifestName());
            detailDto.setStatus(manifestEntity.getState().getCode());
        }

        return detailDto;

    }

    /**
     * 根据变量空间, 测试类型和测试对象 ID 获取使用的数据模型
     *
     * @param spaceId  变量空间 ID
     * @param testType 测试类型 {@link TestVariableTypeEnum}
     * @param id       测试对象 ID (变量, 公共函数或变量清单)
     * @return key: 全路径, value: 树形结构 DTO
     */
    protected Map<String, DomainDataModelTreeDto> getDataModelMapBySpaceId(Long spaceId, Integer testType, Long id) {
        Map<String, DomainDataModelTreeDto> dataModel = new HashMap<>(MagicNumbers.EIGHT);
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            dataModel = getIncomeDataModel(id);
            dataModel.putAll(outputDataModelMap(id));

        } else {
            VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);

            DomainDataModelTreeDto inputDomainModelTree = DomainModelTreeEntityUtils.getDomainModelTree(varProcessSpace.getInputData());
            DomainModelTreeEntityUtils.handleDataModelMap(inputDomainModelTree.getName(), inputDomainModelTree, dataModel);
        }

        return dataModel;
    }

    protected Set<String> getVariableExtendVar(Integer testType, Long varId) {
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .eq(VarProcessTestVar::getTestType, testType)
                .eq(VarProcessTestVar::getVariableId, varId));

        if (CollectionUtils.isEmpty(varList)) {
            return new HashSet<>();
        }
        Set<String> list = new HashSet<>();
        for (VarProcessTestVar variableVar : varList) {
            //是否扩展数据
            if (variableVar.getIsExtend().equals(NumberUtils.INTEGER_ZERO)
                    || !variableVar.getActionHistory().contains("w")
                    || !variableVar.getVarPath().startsWith(PositionVarEnum.RAW_DATA.getName())) {
                continue;
            }
            String[] paths = variableVar.getVarPath().split("\\.");
            list.add(paths[1]);

        }

        return list;

    }

    /**
     * 输入输出
     *
     * @param varId 入参
     * @param testType 入参
     * @param dataModel 数据模型
     * @return TestFormDto List
     */
    protected List<TestFormDto> getVariableVar(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            List<TestFormDto> formDtoList = getManifestInputVariable(varId);
            formDtoList.addAll(getManifestOutputVariable(varId));
            return formDtoList;
        }
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .ne(VarProcessTestVar::getTestFlag, StrComVarFlagEnum.NOT_TEST_DATE.getCode()).eq(VarProcessTestVar::getTestType, testType).eq(VarProcessTestVar::getVariableId, varId));
        if (CollectionUtils.isEmpty(varList)) {
            return new ArrayList<>();
        }
        //临时变量路径存储list，用于去重
        List<String> tmpVarPaths = new ArrayList<>();
        List<TestFormDto> list = new ArrayList<>();
        for (VarProcessTestVar varProcessTestVar : varList) {
            String testFlag = String.valueOf(varProcessTestVar.getTestFlag());
            //object类型，需要获取对象下的所有属性都，只考虑输入，不考虑输出
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {
                //既是输入，又是输出
                if (StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode().equals(testFlag)) {
                    objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()), list, tmpVarPaths, true);
                } else {
                    if (StrComVarFlagEnum.INPUT.getCode().equals(testFlag)) {
                        if (!StringUtils.isEmpty(varProcessTestVar.getParameterType())) {
                            //参数本地变量为对象
                            objectParamToVarPathList(new ObjectParamToVarPath(dataModel, varProcessTestVar.getVarPath(), varProcessTestVar.getParameterLabel(), varProcessTestVar.getParameterType(), varProcessTestVar.getIsParameterArray(), list, tmpVarPaths, true), Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                        } else {
                            objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()), list, tmpVarPaths, true);
                        }
                    }
                }
            } else {
                TestFormDto testFormDto = TestFormDto.builder().name(varProcessTestVar.getVarPath()).parameterLabel(varProcessTestVar.getParameterLabel())
                        .parameterType(varProcessTestVar.getParameterType()).isParameterArray(varProcessTestVar.getIsParameterArray()).build();
                if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                    testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
                } else {
                    testFormDto.setLabel(varProcessTestVar.getVarName());
                }
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                    testFormDto.setType(varProcessTestVar.getParameterType());
                    testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());
                } else {
                    testFormDto.setType(varProcessTestVar.getVarType());
                    testFormDto.setIsArr(varProcessTestVar.getIsArray());
                }
                //既是输入，又是输出
                if (StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode().equals(testFlag)) {
                    if (!tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode())) {
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                        list.add(testFormDto);
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode());
                    }
                    if (!tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode())) {
                        TestFormDto testFormExpectDto = new TestFormDto();
                        BeanUtils.copyProperties(testFormDto, testFormExpectDto);
                        testFormExpectDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));
                        list.add(testFormExpectDto);
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode());
                    }
                } else {
                    if (StrComVarFlagEnum.INPUT.getCode().equals(testFlag)) {
                        if (tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode())) {
                            continue;
                        }
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode());
                    } else if (StrComVarFlagEnum.OUTPUT.getCode().equals(testFlag)) {
                        if (tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode())) {
                            continue;
                        }
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode());
                    }
                    list.add(testFormDto);
                }
            }
        }
        return list;
    }

    /**
     * 输入
     *
     * @param varId 入参
     * @param testType 入参
     * @param dataModel 数据模型
     * @return TestFormDto List
     */
    protected List<TestFormDto> getVariableVarInput(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            return getManifestInputVariable(varId);
        }

        List<TestFormDto> list = new ArrayList<>();
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.INPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
                .eq(VarProcessTestVar::getTestType, testType)
                .eq(VarProcessTestVar::getVariableId, varId));

        if (CollectionUtils.isEmpty(varList)) {
            return list;
        }


        int fieldType = Integer.parseInt(InputExpectTypeEnum.INPUT.getCode());
        //临时变量路径存储list，用于去重
        List<String> tmpVarPaths = new ArrayList<>();
        String tmpKey;

        for (VarProcessTestVar varProcessTestVar : varList) {
            tmpKey = varProcessTestVar.getVarPath() + fieldType;
            if (tmpVarPaths.contains(tmpKey)) {
                continue;
            }

            //object类型，需要获取对象下的所有属性都
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType())) {
                    //参数本地变量为对象
                    objectParamToVarPathList(new ObjectParamToVarPath(dataModel, varProcessTestVar.getVarPath(), varProcessTestVar.getParameterLabel(), varProcessTestVar.getParameterType(), varProcessTestVar.getIsParameterArray(), list, tmpVarPaths, false), fieldType);
                } else {

                    objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), fieldType, list, tmpVarPaths, false);
                }

            } else {
                tmpVarPaths.add(tmpKey);

                //定义返回的dto
                TestFormDto testFormDto = new TestFormDto();
                testFormDto.setName(varProcessTestVar.getVarPath());
                if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                    testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
                } else {
                    testFormDto.setLabel(varProcessTestVar.getVarName());
                }

                testFormDto.setParameterType(varProcessTestVar.getParameterType());
                testFormDto.setIsParameterArray(varProcessTestVar.getIsParameterArray());
                testFormDto.setParameterLabel(varProcessTestVar.getParameterLabel());
                //参数或者本地变量
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                    //基础类型
                    testFormDto.setType(varProcessTestVar.getParameterType());
                    testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());

                } else {
                    testFormDto.setType(varProcessTestVar.getVarType());
                    testFormDto.setIsArr(varProcessTestVar.getIsArray());
                }

                testFormDto.setFieldType(fieldType);
                list.add(testFormDto);

            }

        }

        return list;
    }

    /**
     * 获取测试组件(用于导出excel)
     * @param varId 入参
     * @param testType 入参
     * @param dataModel 数据模型
     * @return TestFormDto List
     */
    protected List<TestFormDto> getVariableVarInputInExcel(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        // 1.如果是变量清单，则用其他的方式
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            return getManifestInputVariable(varId);
        }
        // 2.如果是变量、公共方法、变量模板、数据预处理
        List<TestFormDto> list = new ArrayList<>();
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.INPUT.getCode(), StrComVarFlagEnum.OUTPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
                .eq(VarProcessTestVar::getTestType, testType)
                .eq(VarProcessTestVar::getVariableId, varId));
        if (CollectionUtils.isEmpty(varList)) {
            return list;
        }
        // 3.如果这个查出来的集合(测试组件和变量的关系)存在
        int fieldType = Integer.parseInt(InputExpectTypeEnum.INPUT.getCode());
        // 4.临时变量路径存储list，用于去重
        List<String> tmpVarPaths = new ArrayList<>();
        String tmpKey;

        for (VarProcessTestVar varProcessTestVar : varList) {
            tmpKey = varProcessTestVar.getVarPath() + fieldType;
            if (tmpVarPaths.contains(tmpKey)) {
                continue;
            }

            //object类型，需要获取对象下的所有属性都
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType())) {
                    //参数本地变量为对象
                    objectParamToVarPathList(new ObjectParamToVarPath(dataModel, varProcessTestVar.getVarPath(), varProcessTestVar.getParameterLabel(), varProcessTestVar.getParameterType(), varProcessTestVar.getIsParameterArray(), list, tmpVarPaths, false), fieldType);
                } else {

                    objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), fieldType, list, tmpVarPaths, false);
                }

            } else {
                tmpVarPaths.add(tmpKey);

                //定义返回的dto
                TestFormDto testFormDto = new TestFormDto();
                testFormDto.setName(varProcessTestVar.getVarPath());
                if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                    testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
                } else {
                    testFormDto.setLabel(varProcessTestVar.getVarName());
                }

                testFormDto.setParameterType(varProcessTestVar.getParameterType());
                testFormDto.setIsParameterArray(varProcessTestVar.getIsParameterArray());
                testFormDto.setParameterLabel(varProcessTestVar.getParameterLabel());
                //参数或者本地变量
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                    //基础类型
                    testFormDto.setType(varProcessTestVar.getParameterType());
                    testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());

                } else {
                    testFormDto.setType(varProcessTestVar.getVarType());
                    testFormDto.setIsArr(varProcessTestVar.getIsArray());
                }

                testFormDto.setFieldType(fieldType);
                list.add(testFormDto);

            }

        }

        return list;
    }

    /**
     * 获取变量中的变量输出
     * @param varId 变量Id
     * @param testType 测试类型
     * @param dataModel 数据模型
     * @return 变量中的变量输出
     */
    protected List<TestFormDto> getVariableVarOutput(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {

        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {

            return getManifestOutputVariable(varId);
        }

        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.OUTPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
                .eq(VarProcessTestVar::getTestType, testType)
                .eq(VarProcessTestVar::getVariableId, varId));

        if (CollectionUtils.isEmpty(varList)) {
            return new ArrayList<>();
        }

        int fieldType = Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode());
        //临时变量路径存储list，用于去重
        List<String> tmpVarPaths = new ArrayList<>();
        String tmpKey;
        List<TestFormDto> list = new ArrayList<>();
        for (VarProcessTestVar varProcessTestVar : varList) {
            tmpKey = varProcessTestVar.getVarPath() + fieldType;
            if (tmpVarPaths.contains(tmpKey)) {
                continue;
            }

            //object类型，忽略
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {

                continue;
            }
            tmpVarPaths.add(tmpKey);

            //定义返回的dto
            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(varProcessTestVar.getVarPath());
            if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
            } else {
                testFormDto.setLabel(varProcessTestVar.getVarName());
            }

            testFormDto.setParameterType(varProcessTestVar.getParameterType());
            testFormDto.setIsParameterArray(varProcessTestVar.getIsParameterArray());
            testFormDto.setParameterLabel(varProcessTestVar.getParameterLabel());
            //参数或者本地变量
            if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                //基础类型
                testFormDto.setType(varProcessTestVar.getParameterType());
                testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());

            } else {
                testFormDto.setType(varProcessTestVar.getVarType());
                testFormDto.setIsArr(varProcessTestVar.getIsArray());
            }

            testFormDto.setFieldType(fieldType);
            list.add(testFormDto);

        }

        return list;
    }

    /**
     * 获取变量中的变量输出（不包含数组对象及其下面的属性）
     * @param varId 变量Id
     * @param testType 测试类型
     * @param dataModel 数据模型
     * @return 变量中的变量输出
     */
    public List<TestFormDto> getVariableVarOutputWithoutObjectArr(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            return getManifestOutputVariable(varId);
        }
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.OUTPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
                .eq(VarProcessTestVar::getTestType, testType)
                .eq(VarProcessTestVar::getVariableId, varId));
        if (CollectionUtils.isEmpty(varList)) {
            return new ArrayList<>();
        }
        int fieldType = Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode());
        //临时变量路径存储list，用于去重
        List<String> tmpVarPaths = new ArrayList<>();
        String tmpKey;
        List<TestFormDto> list = new ArrayList<>();
        for (VarProcessTestVar varProcessTestVar : varList) {
            tmpKey = varProcessTestVar.getVarPath() + fieldType;
            if (tmpVarPaths.contains(tmpKey)) {
                continue;
            }
            //object类型，忽略
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {
                continue;
            }
            Set<String> includeSetList = TestFormUtil.splitVarPathKey(Collections.singletonList(varProcessTestVar.getVarPath()));
            //不显示数组对象类型及其下面的子属性
            boolean containObjectArr = includeSetList.stream().anyMatch(item -> {
                DomainDataModelTreeDto treeDto = dataModel.get(item);
                return treeDto != null && ((Objects.equals(treeDto.getIsArr(), "1") && treeDto.getType().equals("object")));
            });
            if (containObjectArr) {
                continue;
            }
            tmpVarPaths.add(tmpKey);
            //定义返回的dto
            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(varProcessTestVar.getVarPath());
            if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
            } else {
                testFormDto.setLabel(varProcessTestVar.getVarName());
            }
            testFormDto.setParameterType(varProcessTestVar.getParameterType());
            testFormDto.setIsParameterArray(varProcessTestVar.getIsParameterArray());
            testFormDto.setParameterLabel(varProcessTestVar.getParameterLabel());
            //参数或者本地变量
            if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                //基础类型
                testFormDto.setType(varProcessTestVar.getParameterType());
                testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());
            } else {
                testFormDto.setType(varProcessTestVar.getVarType());
                testFormDto.setIsArr(varProcessTestVar.getIsArray());
            }
            testFormDto.setFieldType(fieldType);
            list.add(testFormDto);
        }
        return list;
    }

    private void objectParamToVarPathList(ObjectParamToVarPath objectParamToVarPath, int fieldType) {
        if (objectParamToVarPath.getDataModel() == null || !objectParamToVarPath.getDataModel().containsKey(objectParamToVarPath.getParameterType())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "数据模型没有查询到【" + objectParamToVarPath.getParameterType() + "】");
        }
        //引用类型
        String[] split = objectParamToVarPath.getObjectVarPath().split("\\.");
        String paramKey = split[0] + "." + split[1];
        String newKey = objectParamToVarPath.getObjectVarPath().replaceFirst(paramKey, objectParamToVarPath.getParameterType());
        if (objectParamToVarPath.getDataModel() == null || !objectParamToVarPath.getDataModel().containsKey(newKey)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "数据模型没有查询到【" + newKey + "】");
        }
        List<String> varPathList = new ArrayList<>();
        DomainDataModelTreeDto domainDataModelTreeDto = objectParamToVarPath.getDataModel().get(newKey);
        if (objectParamToVarPath.isAllPath()) {
            dataModelTransforAllVarPath(domainDataModelTreeDto, varPathList);
        } else {
            dataModelTransforVarPath(domainDataModelTreeDto, varPathList);
        }

        String tmpKey;
        for (String varPath : varPathList) {
            DomainDataModelTreeDto treeDto = objectParamToVarPath.getDataModel().get(varPath);
            String newVarPath = varPath.replaceFirst(objectParamToVarPath.getParameterType(), paramKey);
            tmpKey = newVarPath + fieldType;
            if (objectParamToVarPath.getTmpVarPaths().contains(tmpKey)) {
                continue;
            }
            objectParamToVarPath.getTmpVarPaths().add(tmpKey);

            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setParameterLabel(objectParamToVarPath.getParameterLabel());
            testFormDto.setName(newVarPath);
            testFormDto.setLabel(treeDto.getDescribe());
            testFormDto.setType(treeDto.getType());
            testFormDto.setIsArr(Integer.parseInt(treeDto.getIsArr()));
            testFormDto.setParameterType(objectParamToVarPath.getParameterType());
            testFormDto.setIsParameterArray(objectParamToVarPath.getIsParameterArray());
            testFormDto.setFieldType(fieldType);
            objectParamToVarPath.getList().add(testFormDto);
        }
    }

    private void objectToVarPathList(Map<String, DomainDataModelTreeDto> dataModel, String objectVarPath, int fieldType, List<TestFormDto> list,
                                     List<String> tmpVarPaths, boolean isAllPath) {
        if (dataModel == null || !dataModel.containsKey(objectVarPath)) {
            return;
        }
        List<String> varPathList = new ArrayList<>();
        DomainDataModelTreeDto domainDataModelTreeDto = dataModel.get(objectVarPath);

        if (isAllPath) {
            dataModelTransforAllVarPath(domainDataModelTreeDto, varPathList);
        } else {
            dataModelTransforVarPath(domainDataModelTreeDto, varPathList);
        }
        String tmpKey;
        for (String varPath : varPathList) {
            DomainDataModelTreeDto treeDto = dataModel.get(varPath);
            if (treeDto == null) {
                continue;
            }
            tmpKey = varPath + fieldType;
            if (tmpVarPaths.contains(tmpKey)) {
                continue;
            }
            tmpVarPaths.add(tmpKey);

            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(varPath);
            testFormDto.setLabel(treeDto.getDescribe());
            testFormDto.setType(treeDto.getType());
            testFormDto.setIsArr(Integer.parseInt(treeDto.getIsArr()));
            testFormDto.setParameterType(null);
            testFormDto.setIsParameterArray(0);
            testFormDto.setFieldType(fieldType);
            list.add(testFormDto);
        }
    }

    private void dataModelTransforAllVarPath(DomainDataModelTreeDto contentDto, List<String> varPathList) {
        varPathList.add(contentDto.getValue());
        List<DomainDataModelTreeDto> children = contentDto.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            for (DomainDataModelTreeDto treeDto : children) {

                dataModelTransforAllVarPath(treeDto, varPathList);
            }
        }

    }

    private void dataModelTransforVarPath(DomainDataModelTreeDto contentDto, List<String> varPathList) {

        List<DomainDataModelTreeDto> children = contentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            varPathList.add(contentDto.getValue());
            return;
        }
        for (DomainDataModelTreeDto treeDto : children) {

            dataModelTransforVarPath(treeDto, varPathList);
        }
    }

    /**
     * 统计组件测试表记录数
     *
     * @param spaceId 变量空间Id
     * @param identifier  标识
     * @return 测试表记录数
     */
    protected int getTestCount(Long spaceId, String identifier) {
        Integer maxSeqNoByIdentifier = varProcessTestVariableService.findMaxSeqNoByIdentifier(spaceId, identifier);
        return maxSeqNoByIdentifier + 1;
    }

    /**
     * 获取输出参数需要排除的变量
     *
     * @param varId 变量Id
     * @param testType 类型
     * @return 需要排除的变量
     */
    protected List<String> getOutputExcludeVarList(Integer testType, Long varId) {

        Set<String> inputSet = new HashSet<>();

        List<VarProcessTestVar> inputDataList = varProcessTestVariableVarService.list(
                new QueryWrapper<VarProcessTestVar>().lambda().eq(VarProcessTestVar::getTestType, testType)
                        .eq(VarProcessTestVar::getVariableId, varId)
                        .eq(VarProcessTestVar::getTestFlag, StrComVarFlagEnum.INPUT.getCode())
        );

        if (!CollectionUtils.isEmpty(inputDataList)) {
            inputSet = inputDataList.stream().map(VarProcessTestVar::getVarPath).collect(Collectors.toSet());
        }

        //输出
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(
                new QueryWrapper<VarProcessTestVar>().lambda().eq(VarProcessTestVar::getTestType, testType)
                        .eq(VarProcessTestVar::getVariableId, varId)
                        .in(VarProcessTestVar::getTestFlag, Arrays.asList(StrComVarFlagEnum.OUTPUT.getCode(), StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode()))
        );

        if (CollectionUtils.isEmpty(varList)) {
            return new ArrayList<>(inputSet);
        }

        List<String> outputVarPathList = varList.stream().map(VarProcessTestVar::getVarPath).collect(Collectors.toList());
        Set<String> outputVarSetList = TestFormUtil.splitVarPathKey(outputVarPathList);

        //输出变量不在输入的可以排除
        List<String> outputVarList = new ArrayList<>();
        for (String key : inputSet) {

            boolean isStartsWith = true;
            for (String outVarPath : outputVarPathList) {
                if (key.startsWith(outVarPath)) {
                    isStartsWith = false;
                    break;
                }
            }
            if (isStartsWith && !outputVarSetList.contains(key)) {
                outputVarList.add(key);
            }
        }

        return outputVarList;

    }

    /**
     * @param varId 变量Id
     * @param dataModel 数据模型对象
     * @param testType 测试类型
     * @return JSONObject
     */
    protected JSONObject getDataModelHeaderDto(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        JSONObject dataModelHeaderDto = new JSONObject();

        //处理数据模型
        Set<Map.Entry<String, DomainDataModelTreeDto>> entries = dataModel.entrySet();
        for (Map.Entry<String, DomainDataModelTreeDto> entry : entries) {
            String key = entry.getKey();
            DomainDataModelTreeDto value = entry.getValue();

            JSONObject tmpJsonObj = new JSONObject();
            tmpJsonObj.put("index", key);
            tmpJsonObj.put("name", key);
            tmpJsonObj.put("isArr", value.getIsArr());
            tmpJsonObj.put("label", value.getDescribe());
            tmpJsonObj.put("type", value.getType());

            dataModelHeaderDto.put(key, tmpJsonObj);
        }

        //处理组件用到的属性
        List<TestFormDto> testVariableVar = getVariableAllVar(testType, varId, dataModel);
        JSONObject testData = TestTableHeaderUtil.getTestData(testVariableVar, dataModel, 1);

        if (testData == null || testData.size() == 0 || !testData.containsKey(TestHeaderValueEnum.HEADER.getCode())) {
            return dataModelHeaderDto;
        }

        JSONObject header = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());

        Set<String> keySet = header.keySet();
        for (String key : keySet) {
            JSONArray valueList = header.getJSONArray(key);
            for (int i = 0; i < valueList.size(); i++) {
                JSONObject jsonObject = valueList.getJSONObject(i);
                if (jsonObject.getString("index").startsWith(TestTableEnum.EXPECT.getCode())) {
                    String index = jsonObject.getString("index").substring(jsonObject.getString("index").indexOf(".") + 1);
                    jsonObject.put("index", index);
                    dataModelHeaderDto.put(index, jsonObject);
                } else {
                    dataModelHeaderDto.put(jsonObject.getString("index"), jsonObject);
                }

            }
        }
        return dataModelHeaderDto;
    }

    /**
     * 组件用的所有变量
     *
     * @param varId 变量Id
     * @param testType 测试类型
     * @param dataModel 数据模型对象
     * @return TestFormDto List
     */
    protected List<TestFormDto> getVariableAllVar(Integer testType, Long varId, Map<String, DomainDataModelTreeDto> dataModel) {
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(new QueryWrapper<VarProcessTestVar>().lambda()
                .ne(VarProcessTestVar::getTestFlag, StrComVarFlagEnum.NOT_TEST_DATE.getCode()).eq(VarProcessTestVar::getTestType, testType).eq(VarProcessTestVar::getVariableId, varId));
        if (CollectionUtils.isEmpty(varList)) {
            return new ArrayList<>();
        }
        List<String> tmpVarPaths = new ArrayList<>();
        List<TestFormDto> list = new ArrayList<>();
        for (VarProcessTestVar varProcessTestVar : varList) {
            String testFlag = String.valueOf(varProcessTestVar.getTestFlag());
            //object类型，需要获取对象下的所有属性都，只考虑输入，不考虑输出
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equalsIgnoreCase(varProcessTestVar.getVarType())) {
                //既是输入，又是输出
                if (StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode().equals(testFlag)) {
                    objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()), list, tmpVarPaths, false);
                    objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()), list, tmpVarPaths, false);
                } else {
                    int tmpType;
                    if (StrComVarFlagEnum.INPUT.getCode().equals(testFlag)) {
                        tmpType = Integer.parseInt(InputExpectTypeEnum.INPUT.getCode());
                    } else {
                        tmpType = Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode());
                    }
                    if (!StringUtils.isEmpty(varProcessTestVar.getParameterType())) {
                        //参数本地变量为对象
                        objectParamToVarPathList(new ObjectParamToVarPath(dataModel, varProcessTestVar.getVarPath(), varProcessTestVar.getParameterLabel(), varProcessTestVar.getParameterType(), varProcessTestVar.getIsParameterArray(), list, tmpVarPaths, false), tmpType);
                    } else {
                        objectToVarPathList(dataModel, varProcessTestVar.getVarPath(), tmpType, list, tmpVarPaths, false);
                    }
                }
            } else {
                TestFormDto testFormDto = TestFormDto.builder().name(varProcessTestVar.getVarPath()).parameterType(varProcessTestVar.getParameterType())
                        .parameterLabel(varProcessTestVar.getParameterLabel()).isParameterArray(varProcessTestVar.getIsParameterArray()).build();
                if (dataModel != null && dataModel.containsKey(varProcessTestVar.getVarPath())) {
                    testFormDto.setLabel(dataModel.get(varProcessTestVar.getVarPath()).getDescribe());
                } else {
                    testFormDto.setLabel(varProcessTestVar.getVarName());
                }
                //参数或者本地变量
                if (!StringUtils.isEmpty(varProcessTestVar.getParameterType()) && DataVariableBasicTypeEnum.getNameEnum(varProcessTestVar.getParameterType()) != null) {
                    testFormDto.setType(varProcessTestVar.getParameterType());
                    testFormDto.setIsArr(varProcessTestVar.getIsParameterArray());
                } else {
                    testFormDto.setType(varProcessTestVar.getVarType());
                    testFormDto.setIsArr(varProcessTestVar.getIsArray());
                }
                //既是输入，又是输出
                if (StrComVarFlagEnum.INPUT_AND_OUTPUT.getCode().equals(testFlag)) {
                    if (!tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode())) {
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                        list.add(testFormDto);
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode());
                    }
                    if (!tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode())) {
                        TestFormDto testFormExpectDto = new TestFormDto();
                        BeanUtils.copyProperties(testFormDto, testFormExpectDto);
                        testFormExpectDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));
                        list.add(testFormExpectDto);
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode());
                    }
                } else {
                    if (StrComVarFlagEnum.INPUT.getCode().equals(testFlag)) {
                        if (tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode())) {
                            continue;
                        }
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.INPUT.getCode());
                    } else if (StrComVarFlagEnum.OUTPUT.getCode().equals(testFlag)) {
                        if (tmpVarPaths.contains(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode())) {
                            continue;
                        }
                        testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));
                        tmpVarPaths.add(varProcessTestVar.getVarPath() + InputExpectTypeEnum.EXPECT.getCode());
                    }
                    list.add(testFormDto);
                }
            }
        }
        return list;
    }

    //===================================实时服务接口=====================//

    protected JSONObject tansferExpectHeader(Integer testType, Long spaceId, Long varId, List<String> expectHeader) {
        Map<String, DomainDataModelTreeDto> dataModel = new HashMap<>(MagicNumbers.EIGHT);
        List<DomainDataModelTreeDto> dataModelTreeDtos = new ArrayList<>();
        if (testType.equals(TestVariableTypeEnum.MANIFEST.getCode())) {
            //实时服务接口
            List<TestFormDto> formDtoList = getManifestOutputVariable(varId);

            DomainDataModelTreeDto outputModelTree = outputFormToModelTree(formDtoList);

            DomainModelTreeEntityUtils.handleDataModelMap(outputModelTree.getName(), outputModelTree, dataModel);
            dataModelTreeDtos.add(outputModelTree);
        } else {
            VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);

            dataModel = DomainModelTreeEntityUtils.getDataModelTreeMapByConent(varProcessSpace.getInputData());

            List<TestFormDto> formDtoList = getVariableVarOutput(testType, varId, dataModel);

            Map<String, String> modelVarsMap = new LinkedHashMap<>(MagicNumbers.EIGHT);
            modelVarsMap.put(PositionVarEnum.RAW_DATA.getName(), varProcessSpace.getInputData());

            dataModelTreeDtos = TestFormUtil.transferComVarsToModelTreeDto(formDtoList, modelVarsMap);
        }
        //加工测试数据表头
        List<TestFormDto> formDataList = TestFormUtil.transferFormExpectHeader(dataModelTreeDtos, expectHeader);

        JSONObject testData = TestTableHeaderUtil.getTestData(formDataList, dataModel, 1);
        JSONObject expectHeaderObject = testData.getJSONObject(TestHeaderValueEnum.HEADER.getCode());
        JSONObject targetExpectHeader = new JSONObject();
        for (Map.Entry<String, Object> entry : expectHeaderObject.entrySet()) {
            if (entry.getKey().equals(TestTableEnum.MASTER.getCode())) {
                targetExpectHeader.put(TestTableEnum.EXPECT.getCode(), entry.getValue());
            } else {
                targetExpectHeader.put(entry.getKey(), entry.getValue());
            }
        }

        return targetExpectHeader;
    }

    /**
     * 实时服务获取数据模型输入数据
     *
     * @param manifestId 变量清单Id
     * @return TestFormDto List
     */
    public List<TestFormDto> getManifestInputVariable(Long manifestId) {
        // 1.输入数据模型
        Map<String, DomainDataModelTreeDto> dataModel = getIncomeDataModel(manifestId);

        // 2.如果数据模型为空或者size等于0就返回空的列表
        if (dataModel == null || dataModel.size() == 0) {
            return new ArrayList<>();
        }

        // 3.定义返回的数据
        List<TestFormDto> list = new ArrayList<>();

        // 4.获取数据模型的所有key的集合
        Set<Map.Entry<String, DomainDataModelTreeDto>> entries = dataModel.entrySet();

        // 5.遍历数据模型的所有key的集合
        for (Map.Entry<String, DomainDataModelTreeDto> entry : entries) {
            DomainDataModelTreeDto dataModelTreeDto = entry.getValue();
            if (DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage().equals(dataModelTreeDto.getType())) {
                continue;
            }

            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(dataModelTreeDto.getValue());
            testFormDto.setLabel(dataModelTreeDto.getDescribe());
            testFormDto.setType(dataModelTreeDto.getType());
            testFormDto.setIsArr(Integer.parseInt(dataModelTreeDto.getIsArr()));
            testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.INPUT.getCode()));

            list.add(testFormDto);
        }
        return list;
    }

    /**
     * 实时服务获取数据模型输出数据
     *
     * @param manifestId 变量清单Id
     * @return TestFormDto List
     */
    public List<TestFormDto> getManifestOutputVariable(Long manifestId) {
        List<TestFormDto> list = new ArrayList<>();

        List<Long> variableIds = getManifestVariableIds(manifestId);

        List<VarProcessVariable> varProcessVariables = varProcessVariableService.list(
                new QueryWrapper<VarProcessVariable>().lambda()
                        .select(
                                VarProcessVariable::getName,
                                VarProcessVariable::getLabel,
                                VarProcessVariable::getDataType
                        )
                        .in(VarProcessVariable::getId, variableIds)

        );

        for (VarProcessVariable variable : varProcessVariables) {
            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(PositionVarEnum.OUTPUT.getName() + "." + variable.getName());

            testFormDto.setLabel(variable.getLabel());

            testFormDto.setType(variable.getDataType());
            testFormDto.setIsArr(0);

            testFormDto.setFieldType(Integer.parseInt(InputExpectTypeEnum.EXPECT.getCode()));

            list.add(testFormDto);
        }

        return list;
    }

    /**
     * 输出数据模型map集合
     * @param manifestId 变量清单的Id
     * @return 数据模型map集合
     */
    public Map<String, DomainDataModelTreeDto> outputDataModelMap(Long manifestId) {

        List<TestFormDto> formDtoList = getManifestOutputVariable(manifestId);

        Map<String, DomainDataModelTreeDto> dataModel = new HashMap<>(MagicNumbers.EIGHT);
        DomainDataModelTreeDto outputModelTree = outputFormToModelTree(formDtoList);
        DomainModelTreeEntityUtils.handleDataModelMap(outputModelTree.getName(), outputModelTree, dataModel);

        return dataModel;
    }

    /**
     * 输出模型树的表单
     * @param list 测试TestForm对象list集合
     * @return 模型树的表单
     */
    public DomainDataModelTreeDto outputFormToModelTree(List<TestFormDto> list) {
        DomainDataModelTreeDto dto = new DomainDataModelTreeDto();
        dto.setName(DomainModelSheetNameEnum.OUTPUT.getMessage());
        dto.setDescribe(DomainModelSheetNameEnum.OUTPUT.getDescribe());
        dto.setLabel(DomainModelSheetNameEnum.OUTPUT.getMessage() + "-" + DomainModelSheetNameEnum.OUTPUT.getDescribe());
        dto.setValue(DomainModelSheetNameEnum.OUTPUT.getMessage());
        dto.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (TestFormDto testFormDto : list) {
            String name = testFormDto.getName().replaceFirst(DomainModelSheetNameEnum.OUTPUT.getMessage() + ".", REPLACEMENT);
            DomainDataModelTreeDto child = new DomainDataModelTreeDto();
            child.setName(name);
            child.setDescribe(testFormDto.getLabel());
            child.setLabel(child.getName() + "-" + testFormDto.getLabel());
            child.setValue(testFormDto.getName());
            child.setType(testFormDto.getType());
            children.add(child);
        }
        dto.setChildren(children);
        return dto;
    }

    /**
     * 获取输入数据，只有原始数据
     * @param manifestId 变量清单Id
     * @return 输入数据
     */
    public DomainDataModelTreeDto getManifestInputDataModel(Long manifestId) {

        List<VarProcessDataModel> dataModelList = varProcessDataModelService.listDataModelSpecificVersion(manifestId,
                VarProcessDataModelSourceType.OUTSIDE_PARAM.getCode());
        JSONObject jsonObject = variableDataModelConverter.dataModelObjectToTree(dataModelList, null);
        DomainModelTree originalModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);

        DomainModelTree targetChildModelTree = new DomainModelTree();
        removeDataModelTreeDtoByExtend(originalModelTree, targetChildModelTree);
        DomainDataModelTreeDto domainDataModelTreeDto = new DomainDataModelTreeDto();
        DomainModelTreeEntityUtils.beanCopyDomainDataModelTreeDto(targetChildModelTree, domainDataModelTreeDto);

        return domainDataModelTreeDto;

    }

    /**
     * 移除扩展数据
     *
     * @param originalModelTree 原数据模型结构
     * @param targetModelTree   目标数据模型结构
     */
    private void removeDataModelTreeDtoByExtend(DomainModelTree originalModelTree, DomainModelTree targetModelTree) {
        BeanCopyUtils.copy(originalModelTree, targetModelTree);
        List<DomainModelTree> children = originalModelTree.getChildren();
        if (INT_1.equals(originalModelTree.getIsExtend()) || CollectionUtils.isEmpty(children)) {
            return;
        }
        List<DomainModelTree> targetChildren = new ArrayList<>();

        for (DomainModelTree treeDto : children) {
            if (INT_1.equals(treeDto.getIsExtend())) {
                continue;
            }
            DomainModelTree targetChildModelTree = new DomainModelTree();
            removeDataModelTreeDtoByExtend(treeDto, targetChildModelTree);
            targetChildren.add(targetChildModelTree);

        }
        targetModelTree.setChildren(targetChildren);
    }

    /**
     * 获取外部传入(含原始数据)
     *
     * @param manifestId 变量清单Id
     * @return Map
     */
    public Map<String, DomainDataModelTreeDto> getIncomeDataModel(Long manifestId) {

        List<VarProcessDataModel> dataModelList = getManifestInputDataModel(manifestId,
                VarProcessDataModelSourceType.OUTSIDE_PARAM.getCode());
        JSONObject jsonObject = variableDataModelConverter.dataModelObjectToTree(dataModelList, null);
        DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);
        DomainDataModelTreeDto domainDataModelTreeDto = new DomainDataModelTreeDto();
        DomainModelTreeEntityUtils.beanCopyDomainDataModelTreeDto(domainModelTree, domainDataModelTreeDto);

        Map<String, DomainDataModelTreeDto> map = new LinkedHashMap<>(MagicNumbers.EIGHT);
        handleDataModelMap(domainDataModelTreeDto.getName(), domainDataModelTreeDto, map);

        return map;

    }

    /**
     * 获取外部传入和外数调用(含原始数据和扩展数据)
     *
     * @param manifestId 变量清单Id
     * @return Map
     */
    public Map<String, DomainDataModelTreeDto> getManifestDataModel(Long manifestId) {

        List<VarProcessDataModel> dataModelList = getManifestInputDataModel(manifestId, null);
        JSONObject jsonObject = variableDataModelConverter.dataModelObjectToTree(dataModelList, null);
        DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(jsonObject);
        DomainDataModelTreeDto domainDataModelTreeDto = new DomainDataModelTreeDto();
        DomainModelTreeEntityUtils.beanCopyDomainDataModelTreeDto(domainModelTree, domainDataModelTreeDto);

        Map<String, DomainDataModelTreeDto> map = new LinkedHashMap<>(MagicNumbers.EIGHT);
        DomainModelTreeEntityUtils.handleDataModelMap(domainDataModelTreeDto.getName(), domainDataModelTreeDto, map);

        return map;

    }

    /**
     * 获取变量清单的数据模型头部
     * @param manifestId 变量清单Id
     * @return 变量清单的数据模型头部
     */
    protected JSONObject getManifestOutputDataModelHeaderDto(Long manifestId) {

        JSONObject dataModelHeaderDto = new JSONObject();
        //处理rawData数据模型
        Map<String, DomainDataModelTreeDto> manifestDataModel = getManifestDataModel(manifestId);

        Set<Map.Entry<String, DomainDataModelTreeDto>> rawDataEntries = manifestDataModel.entrySet();
        for (Map.Entry<String, DomainDataModelTreeDto> entry : rawDataEntries) {
            String key = entry.getKey();
            DomainDataModelTreeDto value = entry.getValue();

            JSONObject tmpJsonObj = new JSONObject();
            tmpJsonObj.put("index", key);
            tmpJsonObj.put("name", key);
            tmpJsonObj.put("isArr", value.getIsArr());
            tmpJsonObj.put("label", value.getDescribe());
            tmpJsonObj.put("type", value.getType());

            dataModelHeaderDto.put(key, tmpJsonObj);
        }

        //处理输出数据模型
        Map<String, DomainDataModelTreeDto> dataModel = outputDataModelMap(manifestId);

        Set<Map.Entry<String, DomainDataModelTreeDto>> entries = dataModel.entrySet();
        for (Map.Entry<String, DomainDataModelTreeDto> entry : entries) {
            String key = entry.getKey();
            DomainDataModelTreeDto value = entry.getValue();

            JSONObject tmpJsonObj = new JSONObject();
            tmpJsonObj.put("index", key);
            tmpJsonObj.put("name", key);
            tmpJsonObj.put("isArr", value.getIsArr());
            tmpJsonObj.put("label", value.getDescribe());
            tmpJsonObj.put("type", value.getType());

            dataModelHeaderDto.put(key, tmpJsonObj);
        }

        return dataModelHeaderDto;
    }

    private List<VarProcessDataModel> getManifestInputDataModel(Long manifestId, Integer sourceType) {

        VarProcessManifest manifestEntity = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .select(VarProcessManifest::getState, VarProcessManifest::getVarProcessSpaceId)
                .eq(VarProcessManifest::getId, manifestId));
        if (manifestEntity == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到变量清单");
        }
        //非编辑中，使用绑定的数据模型版本
        if (!manifestEntity.getState().getCode().equals(VarProcessManifestStateEnum.EDIT.getCode())) {
            return varProcessDataModelService.listDataModelSpecificVersion(manifestId, sourceType);

        }

        //编辑中，使用数据模型最大的版本
        List<VarProcessDataModel> bindingDataModelList = new LinkedList<>();


        List<VarProcessManifestDataModel> mappingList = variableManifestSupportBiz.getVariableManifestDto(manifestId).getDataModelMappingList();
        if (sourceType != null) {
            mappingList = mappingList.stream().filter(item -> item.getSourceType().equals(sourceType)).collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(mappingList)) {
            return bindingDataModelList;
        }

        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionList(manifestEntity.getVarProcessSpaceId(),roleDataAuthorityDTO);

        if (sourceType == null) {
            return dataModelList;
        }

        Set<String> objectNameSourceTypeMap = mappingList.stream()
                .map(VarProcessManifestDataModel::getObjectName)
                .collect(Collectors.toSet());

        for (VarProcessDataModel dataModel : dataModelList) {

            if (objectNameSourceTypeMap.contains(dataModel.getObjectName())) {
                // 数据模型对象版本匹配: 添加到结果 List
                bindingDataModelList.add(dataModel);
            }
        }

        return bindingDataModelList;
    }

    /**
     * 根据接口ID获取变量清单列表ID
     * @param manifestId 变量清单Id
     * @return 获取变量清单列表ID
     */
    public List<Long> getManifestVariableIds(Long manifestId) {
        List<VarProcessManifestVariable> variableList = varProcessManifestVariableService.list(
                new QueryWrapper<VarProcessManifestVariable>().lambda()
                        .eq(VarProcessManifestVariable::getManifestId, manifestId)
        );
        if (CollectionUtils.isEmpty(variableList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "未查询到发布变量清单");
        }
        return variableList.stream().map(VarProcessManifestVariable::getVariableId).collect(Collectors.toList());
    }

    /**
     * 组合变量所引用变量的JsonSchema
     * @param variableId 变量Id
     * @return 组合变量所引用变量的JsonSchema
     */
    public JSONObject getVarsJsonSchema(Long variableId) {
        JSONObject jsonObject = new JSONObject();
        List<TestFormDto> formDtoList = getVarsFormList(variableId);
        if (CollectionUtils.isEmpty(formDtoList)) {
            return jsonObject;
        }
        jsonObject.put(JsonSchemaFieldEnum.TITLE_FIELD.getMessage(), DomainModelSheetNameEnum.VARS.getMessage());
        jsonObject.put(JsonSchemaFieldEnum.DESCRIPTION_FIELD.getMessage(), DomainModelSheetNameEnum.VARS.getDescribe());
        jsonObject.put(JsonSchemaFieldEnum.TYPE_FIELD.getMessage(), DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        JSONObject propertiesObject = new JSONObject();
        for (TestFormDto formDto : formDtoList) {
            JSONObject item = new JSONObject();
            item.put(JsonSchemaFieldEnum.DESCRIPTION_FIELD.getMessage(), formDto.getLabel());
            item.put(JsonSchemaFieldEnum.TYPE_FIELD.getMessage(), formDto.getType());

            String key = formDto.getName().split("\\.")[1];
            propertiesObject.put(key, item);
        }
        jsonObject.put(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage(), propertiesObject);
        return jsonObject;

    }

    /**
     * 获取引用变量数据模型Map结构
     *
     * @param variableId 变量Id
     * @return Map
     */
    public Map<String, DomainDataModelTreeDto> getVarsDataModelMap(Long variableId) {

        List<TestFormDto> formDtoList = getVarsFormList(variableId);

        Map<String, DomainDataModelTreeDto> dataModel = new HashMap<>(MagicNumbers.EIGHT);
        if (!CollectionUtils.isEmpty(formDtoList)) {
            DomainDataModelTreeDto outputModelTree = getVarsFormToModelTree(formDtoList);
            DomainModelTreeEntityUtils.handleDataModelMap(outputModelTree.getName(), outputModelTree, dataModel);
        }

        return dataModel;
    }

    /**
     * 获取引用变量数据模型
     * @param list 测试TestForm对象的list集合
     * @return 引用变量的数据模型
     */
    public DomainDataModelTreeDto getVarsFormToModelTree(List<TestFormDto> list) {

        DomainDataModelTreeDto dto = new DomainDataModelTreeDto();
        dto.setName(DomainModelSheetNameEnum.VARS.getMessage());
        dto.setDescribe(DomainModelSheetNameEnum.VARS.getDescribe());
        dto.setLabel(DomainModelSheetNameEnum.VARS.getMessage() + "-" + DomainModelSheetNameEnum.VARS.getDescribe());
        dto.setValue(DomainModelSheetNameEnum.VARS.getMessage());
        dto.setType(DomainModeTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        List<DomainDataModelTreeDto> children = new ArrayList<>();
        for (TestFormDto testFormDto : list) {
            String name = testFormDto.getName().split("\\.")[1];
            DomainDataModelTreeDto child = new DomainDataModelTreeDto();
            child.setName(name);
            child.setDescribe(testFormDto.getLabel());
            child.setLabel(child.getName() + "-" + testFormDto.getLabel());
            child.setValue(testFormDto.getName());
            child.setType(testFormDto.getType());
            children.add(child);
        }
        dto.setChildren(children);
        return dto;
    }

    /**
     * 获取引用变量的form对象
     * @param variableId 变量Id
     * @return 测试TestForm对象的list集合
     */
    public List<TestFormDto> getVarsFormList(Long variableId) {
        List<TestFormDto> list = new ArrayList<>();
        List<VarProcessTestVar> varList = varProcessTestVariableVarService.list(
                new QueryWrapper<VarProcessTestVar>().lambda()
                        .eq(VarProcessTestVar::getTestType, TestVariableTypeEnum.VAR.getCode())
                        .eq(VarProcessTestVar::getVariableId, variableId)
        );

        if (CollectionUtils.isEmpty(varList)) {
            return list;
        }
        int fieldType = Integer.parseInt(InputExpectTypeEnum.INPUT.getCode());
        for (VarProcessTestVar varProcessTestVar : varList) {
            if (!varProcessTestVar.getVarPath().startsWith(DomainModelSheetNameEnum.VARS.getMessage())) {
                continue;
            }
            TestFormDto testFormDto = new TestFormDto();
            testFormDto.setName(varProcessTestVar.getVarPath());
            testFormDto.setLabel(varProcessTestVar.getVarName());
            testFormDto.setType(varProcessTestVar.getVarType());
            testFormDto.setIsArr(varProcessTestVar.getIsArray());

            testFormDto.setFieldType(fieldType);
            list.add(testFormDto);
        }

        return list;
    }

    /**
     * 查询数据字典
     *
     * @param spaceId 变量空间Id
     * @return 数据字典
     */
    protected Map<String, List<TestFormDictDto>> getDictDetails(Long spaceId) {
        List<DictDetailsDto> dictDetails = varProcessDictService.getDictDetails(spaceId);

        Map<String, List<TestFormDictDto>> domainDictMap = new HashMap<>(MagicNumbers.EIGHT);
        if (CollectionUtils.isEmpty(dictDetails)) {
            return domainDictMap;
        }

        Map<String, List<DictDetailsDto>> dictDetailsMap = dictDetails.stream().collect(Collectors.groupingBy(DictDetailsDto::getDictCode));

        Set<Map.Entry<String, List<DictDetailsDto>>> entries = dictDetailsMap.entrySet();
        for (Map.Entry<String, List<DictDetailsDto>> entry : entries) {

            List<TestFormDictDto> list = new ArrayList<>();
            for (DictDetailsDto dictDetailsDto : entry.getValue()) {
                TestFormDictDto dto = new TestFormDictDto();
                dto.setCode(dictDetailsDto.getCode());
                dto.setName(dictDetailsDto.getName());
                list.add(dto);
            }
            domainDictMap.put(entry.getKey(), list);
        }
        return domainDictMap;

    }

    private static final class ObjectParamToVarPath {
        private final Map<String, DomainDataModelTreeDto> dataModel;
        private final String objectVarPath;
        private final String parameterLabel;
        private final String parameterType;
        private final int isParameterArray;
        private final List<TestFormDto> list;
        private final List<String> tmpVarPaths;
        private final boolean isAllPath;

        private ObjectParamToVarPath(Map<String, DomainDataModelTreeDto> dataModel, String objectVarPath, String parameterLabel, String parameterType, int isParameterArray, List<TestFormDto> list, List<String> tmpVarPaths, boolean isAllPath) {
            this.dataModel = dataModel;
            this.objectVarPath = objectVarPath;
            this.parameterLabel = parameterLabel;
            this.parameterType = parameterType;
            this.isParameterArray = isParameterArray;
            this.list = list;
            this.tmpVarPaths = tmpVarPaths;
            this.isAllPath = isAllPath;
        }

        public Map<String, DomainDataModelTreeDto> getDataModel() {
            return dataModel;
        }

        public String getObjectVarPath() {
            return objectVarPath;
        }

        public String getParameterLabel() {
            return parameterLabel;
        }

        public String getParameterType() {
            return parameterType;
        }

        public int getIsParameterArray() {
            return isParameterArray;
        }

        public List<TestFormDto> getList() {
            return list;
        }

        public List<String> getTmpVarPaths() {
            return tmpVarPaths;
        }

        public boolean isAllPath() {
            return isAllPath;
        }
    }
}
