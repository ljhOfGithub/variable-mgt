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
package com.wiseco.var.process.app.server.service.datamodel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataModelKeywordEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.auth.common.UserDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceListRestOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceRespRestOutputDto;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewNextInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddSqlReturnVarCheckInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelCopyInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelDeletInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelOperateCheckInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelUpdateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelVersionInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ManifestForRealTimeServiceVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.JsonSchemaFieldEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicOperateTypeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicSpaceTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.VarProcessInternalDataMapper;
import com.wiseco.var.process.app.server.repository.VarProcessOutsideRefMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.SysDynamicServiceBiz;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.converter.VariableDataModelConverter;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelManifestUseVo;
import com.wiseco.var.process.app.server.service.dto.VariableDataModelRealTimeServiceUseVo;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestDataModelService;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

@Slf4j
@Service
public class DataModelSaveBiz {
    @Resource
    private VarProcessSpaceService varProcessSpaceService;
    @Resource
    private VariableDataModelConverter variableDataModelConverter;
    @Resource
    private VarProcessDataModelService varProcessDataModelService;
    @Resource
    private VarProcessInternalDataService varProcessInternalDataService;
    @Resource
    private VarProcessOutsideRefService varProcessOutsideServiceRefService;
    @Resource
    private VarProcessInternalDataMapper varProcessInternalDataMapper;
    @Resource
    private VarProcessOutsideRefMapper varProcessOutsideRefMapper;
    @Resource
    private OutsideService outsideService;
    @Resource
    private DataModelViewBiz dataModelViewBiz;
    @Resource
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Resource
    private VarProcessServiceManifestService varProcessServiceManifestService;
    @Resource
    private VarProcessBatchBacktrackingMapper varProcessBatchBacktrackingMapper;
    @Resource
    private SysDynamicServiceBiz sysDynamicServiceBiz;

    private static final String MATCHES_LETTER = "^[a-zA-Z0-9][a-zA-Z0-9_]{0,100}$";
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)}", Pattern.DOTALL);
    private static final Pattern FIND_VALUE_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

    /**
     * addDataModel
     *
     * @param inputDto 输入
     * @return java.lang.Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addDataModel(VariableDataModelAddNewInputVo inputDto) {
        List<VarProcessDataModel> list = varProcessDataModelService.list(
                new QueryWrapper<VarProcessDataModel>().lambda()
                        .select(VarProcessDataModel::getId)
                        .and(i -> i.eq(VarProcessDataModel::getObjectName, inputDto.getFirstPageInfo().getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputDto.getFirstPageInfo().getObjectLabel()))
                        .eq(VarProcessDataModel::getVarProcessSpaceId, inputDto.getSpaceId())
        );
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
        }
        //关键字校验
        DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputDto.getFirstPageInfo().getObjectName().toLowerCase());
        if (dataModelKeywordEnum != null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputDto.getFirstPageInfo().getObjectName());
        }

        VarProcessDataModel dataModel = saveDataModel(inputDto);
        switch (inputDto.getFirstPageInfo().getSourceType()) {
            case INSIDE_DATA:
                saveInsideData(inputDto, dataModel);
                break;
            case OUTSIDE_SERVER:
                saveOutsideData(inputDto, dataModel);
                break;
            case OUTSIDE_PARAM:
            case INSIDE_LOGIC:
                // do nothing
                break;
            default:
                throw new IllegalArgumentException("未知的数据源类型" + inputDto.getFirstPageInfo().getSourceType());
        }
        saveSpaceDataModel(inputDto.getSpaceId(), null);

        //动态
        saveDynamic(inputDto.getSpaceId(), SysDynamicOperateTypeEnum.ADD.getName(), inputDto.getFirstPageInfo().getObjectName());

        return dataModel.getId();
    }

    private VarProcessDataModel saveDataModel(VariableDataModelAddNewInputVo inputDto) {
        VarProcessDataModel dataModel = new VarProcessDataModel();
        dataModel.setVarProcessSpaceId(inputDto.getSpaceId());
        dataModel.setVersion(NumberUtils.INTEGER_ONE);
        dataModel.setObjectName(inputDto.getFirstPageInfo().getObjectName());
        dataModel.setObjectLabel(inputDto.getFirstPageInfo().getObjectLabel());
        dataModel.setObjectSourceType(inputDto.getFirstPageInfo().getSourceType());

        if (StringUtils.isEmpty(inputDto.getContent())) {
            dataModel.setContent("{\"title\": \"" + inputDto.getFirstPageInfo().getObjectName() + "\",\"description\": \"" + inputDto.getFirstPageInfo().getObjectLabel() + "\",\"type\": \"object\"}");
        } else {
            DomainModelTree dataModelTree = DomainModelTreeEntityUtils.beanCopyDomainModelTree(inputDto.getContent());
            //校验同一级下，不允许同名 以及 扩展数据下不允许定义原始数据
            try {
                DomainModelTreeEntityUtils.checkVarSparceModelTree(dataModelTree, dataModelTree.getName());
            } catch (com.wiseco.decision.common.exception.ServiceException originalException) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, originalException.getMessage().replace("Internal Server Error:", ""));
            }
            // 校验数据类型是否合法 DataVariableTypeEnum
            DomainModelTreeEntityUtils.verifyDataVariableType(dataModelTree);

            int sourcePropertyNum = DomainModelTreeUtils.domainModelTreeNumByType(dataModelTree, "0");
            if (sourcePropertyNum > 0) {
                sourcePropertyNum -= 1;
            }
            int extendPropertyNum = DomainModelTreeUtils.domainModelTreeNumByType(dataModelTree, "1");
            // 设置原始数据数量+扩展数据数量+大JSON(数据模型)
            dataModel.setSourcePropertyNum(sourcePropertyNum);
            dataModel.setExtendPropertyNum(extendPropertyNum);
            JSONObject inputJson = DomainModelTreeUtils.domainModelTreeConvertJsonObject(dataModelTree);
            dataModel.setContent(inputJson.toJSONString());
        }

        dataModel.setCreatedUser(SessionContext.getSessionUser().getUsername());
        dataModel.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        final UserDTO user = SessionContext.getSessionUser().getUser();
        if (user != null && user.getDepartment() != null) {
            dataModel.setCreatedDept(user.getDepartment().getCode());
            dataModel.setCreatedDeptName(user.getDepartment().getName());
        }
        varProcessDataModelService.save(dataModel);

        return dataModel;
    }

    private void saveOutsideData(VariableDataModelAddNewInputVo inputDto, VarProcessDataModel dataModel) {
        // 1. 将外部服务响应参数挂载至变量空间数据模型
        // 挂载数据源: 外部服务响应数据结构 JSONSchema properties 字段

        final OutsideServiceDetailRestOutputDto outsideInfo = outsideService.getOutsideServiceDetailRestById(inputDto.getFirstPageInfo().getOutsideServer().getOutId());
        //更新数据模型object_source_info字段
        dataModel.setObjectSourceInfo(inputDto.getFirstPageInfo().getOutsideServer().getOutName());
        varProcessDataModelService.updateById(dataModel);

        // 2. 新增引入信息并保存至数据库
        VarProcessOutsideRef outsideRef = new VarProcessOutsideRef();
        outsideRef.setVarProcessSpaceId(inputDto.getSpaceId());
        outsideRef.setOutsideServiceId(inputDto.getFirstPageInfo().getOutsideServer().getOutId());
        outsideRef.setOutsideServiceCode(inputDto.getFirstPageInfo().getOutsideServer().getOutCode());
        outsideRef.setOutsideServiceName(inputDto.getFirstPageInfo().getOutsideServer().getOutName());
        outsideRef.setDataModelId(dataModel.getId());
        outsideRef.setName(inputDto.getFirstPageInfo().getObjectName());
        outsideRef.setNameCn(inputDto.getFirstPageInfo().getObjectLabel());
        outsideRef.setInputParameterBindings(outsideInfo.getReq().getRequestParam());
        //出参
        JSONObject outsideServiceRespJsonSchema = JSON.parseObject(outsideInfo.getResp().getDataContent(), Feature.OrderedField);
        outsideRef.setOutputParameterBindings(dealOutputParameterBindings(outsideServiceRespJsonSchema, inputDto.getFirstPageInfo().getObjectName(), inputDto.getFirstPageInfo().getObjectLabel()));
        outsideRef.setIsUseRootObject(Boolean.TRUE.equals(inputDto.getFirstPageInfo().getOutsideServer().getIsUseRootObject()) ? 1 : 0);
        outsideRef.setCreatedUser(SessionContext.getSessionUser().getUsername());
        outsideRef.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessOutsideServiceRefService.save(outsideRef);
    }

    /**
     * referencedOutsideService
     * @param outId 外数Id
     * @return Boolean
     */
    public Boolean referencedOutsideService(Long outId) {
        //根据外数服务的id匹配var_process_outside_ref中的outside_service_id,如果查询结果为空返回flase
        List<VarProcessOutsideRef> varProcessOutsideRefList = varProcessOutsideServiceRefService.list(
                new QueryWrapper<VarProcessOutsideRef>().lambda()
                        .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getDataModelId)
                        .eq(VarProcessOutsideRef::getOutsideServiceId, outId)
        );
        if (CollectionUtils.isEmpty(varProcessOutsideRefList)) {
            return false;
        }
        List<VarProcessDataModel> varProcessDataModelList = new ArrayList<>();
        for (VarProcessOutsideRef varProcessOutsideRef : varProcessOutsideRefList) {
            VarProcessDataModel varProcessDataModel = varProcessDataModelService.getOne(Wrappers.<VarProcessDataModel>lambdaQuery()
                    .select(VarProcessDataModel::getId)
                    .eq(VarProcessDataModel::getId, varProcessOutsideRef.getDataModelId()));
            if (varProcessDataModel != null) {
                varProcessDataModelList.add(varProcessDataModel);
            }

        }
        if (CollectionUtils.isEmpty(varProcessDataModelList)) {
            return false;
        }

        //匹配成功返回true匹配不成功返回flase
        // TODO
        return true;
    }

    /**
     * dealOutputParameterBindings
     *
     * @param outsideServiceRespJsonSchema 外数
     * @param receiverObjectName 接收对象名称
     * @param receiverObjectLabel 接收对象标签
     * @return String
     */
    public String dealOutputParameterBindings(JSONObject outsideServiceRespJsonSchema, String receiverObjectName, String receiverObjectLabel) {
        outsideServiceRespJsonSchema.put("title", receiverObjectName);
        outsideServiceRespJsonSchema.put("description", receiverObjectLabel);
        return JSON.toJSONString(outsideServiceRespJsonSchema);
    }

    /**
     * dealInputParameterBindings
     *
     * @param inputParameterBindings 输入
     * @return String
     */
    public String dealInputParameterBindings(String inputParameterBindings) {
        JSONArray inputParameterBindingsJsonArray = JSON.parseArray(inputParameterBindings);
        JSONArray inputArray = new JSONArray();
        for (Object o : inputParameterBindingsJsonArray) {
            JSONObject tempJsonObject = JSON.parseObject(o.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", tempJsonObject.getString("param_name"));
            jsonObject.put("cnName", tempJsonObject.getString("param_desc"));
            jsonObject.put("type", tempJsonObject.getString("param_type"));
            jsonObject.put("isArr", 0);
            inputArray.add(jsonObject);
        }
        //以上设置子对象的属性

        return JSON.toJSONString(inputArray);
    }

    private void saveInsideData(VariableDataModelAddNewInputVo inputDto, VarProcessDataModel dataModel) {
        if (inputDto.getFirstPageInfo().getInsideData().getInsideDataType() != null) {
            //以下进行保存数据到内部表var_process_internal_data
            VarProcessInternalData data = new VarProcessInternalData();
            data.setVarProcessSpaceId(inputDto.getSpaceId());
            data.setDataModelId(dataModel.getId());
            data.setObjectName(inputDto.getFirstPageInfo().getObjectName());
            data.setName(inputDto.getFirstPageInfo().getObjectLabel());
            data.setObjectLabel(inputDto.getFirstPageInfo().getObjectLabel());
            data.setDataModelId(dataModel.getId());
            data.setIdentifier(GenerateIdUtil.generateId());
            data.setDataType(inputDto.getFirstPageInfo().getInsideData().getInsideDataType());
            data.setContent(JSON.toJSONString(inputDto.getFirstPageInfo().getInsideData()));
            data.setCreatedUser(SessionContext.getSessionUser().getUsername());
            data.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            varProcessInternalDataService.save(data);

            //设置数据模型引用的内部数据表
            //获取数据模型引用的内部数据表
            List<String> insideDataTableName = new ArrayList<>();
            if (inputDto.getFirstPageInfo().getInsideData().getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                List<VariableDataModelAddNewNextInputVo.InsideOutputVO> insideOutputVOList = inputDto.getFirstPageInfo().getInsideData().getTableOutput();
                for (VariableDataModelAddNewNextInputVo.InsideOutputVO insideOutputVO : insideOutputVOList) {
                    insideDataTableName.add(insideOutputVO.getTableConfigs().getTableName());
                    if (CollectionUtils.isEmpty(insideOutputVO.getChildren())) {
                        List<VariableDataModelAddNewNextInputVo.InsideOutputVO> children = insideOutputVO.getChildren();
                        for (VariableDataModelAddNewNextInputVo.InsideOutputVO children1 : children) {
                            insideDataTableName.add(children1.getTableConfigs().getTableName());
                        }
                    }
                }
            } else {
                insideDataTableName.addAll(inputDto.getFirstPageInfo().getInsideData().getSqlTableNames());
            }

            //以上进行保存数据到内部表var_process_internal_data
            // 设置来源表/外部服务
            List<String> sourceInfoList = new ArrayList<>();
            final List<VariableDataModelAddNewNextInputVo.InsideOutputVO> tableOutput = inputDto.getFirstPageInfo().getInsideData().getTableOutput();
            //TABLE方式中获取所有用到的表名字
            processTableOutput(tableOutput, sourceInfoList);

            //SQL方式中获取所有用到的表名字
            final List<String> sqlTableNames = inputDto.getFirstPageInfo().getInsideData().getSqlTableNames();
            if (sqlTableNames != null && !sqlTableNames.isEmpty()) {
                sourceInfoList.addAll(sqlTableNames);
            }
            //组装所有表名用逗号隔开
            String sourceInfo = sourceInfoList.stream().distinct().collect(Collectors.joining(","));
            if (!StringUtils.isEmpty(sourceInfo)) {
                dataModel.setObjectSourceInfo(sourceInfo);
                varProcessDataModelService.updateById(dataModel);
            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.DATA_SOURCE_IS_EMPTY, "内部数据来源不能为空！");
        }
    }

    /**
     * processTableOutput
     *
     * @param tableOutput    tableOutput
     * @param sourceInfoList sourceInfoList
     */
    private void processTableOutput(List<VariableDataModelAddNewNextInputVo.InsideOutputVO> tableOutput, List<String> sourceInfoList) {
        if (!CollectionUtils.isEmpty(tableOutput)) {
            for (VariableDataModelAddNewNextInputVo.InsideOutputVO table : tableOutput) {
                final String tableName = table.getTableConfigs().getTableName();
                if (tableName != null && !tableName.isEmpty()) {
                    sourceInfoList.add(tableName);
                }
                // Process children
                processTableOutput(table.getChildren(), sourceInfoList);
            }
        }
    }

    /**
     * 更新空间数据模型
     *
     * @param spaceId
     * @param excludeObjectName
     */
    private void saveSpaceDataModel(Long spaceId, String excludeObjectName) {
        //组合数据模型
        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionList(spaceId,roleDataAuthorityDTO);
        JSONObject jsonObject = variableDataModelConverter.dataModelObjectToTree(dataModelList, excludeObjectName);
        varProcessSpaceService.update(new UpdateWrapper<VarProcessSpace>().lambda()
                .set(VarProcessSpace::getInputData, jsonObject.toJSONString())
                .eq(VarProcessSpace::getId, spaceId));
    }

    /**
     * updateDataModel
     *
     * @param inputVo 输入
     * @return java.lang.Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long updateDataModel(VariableDataModelUpdateInputVo inputVo) {
        dataModelVariableRuleIdentification(inputVo.getContent());
        // 更新的逻辑 不管数据模型来源是什么，数据模型都得更新。
        // 如果来源是内部数据则需要保存在内部数据表中，如果是外部服务，则需要保存在外部服务的表中  其他的就直接保存模型
        VarProcessDataModel dataModel = varProcessDataModelService.getOne(Wrappers.<VarProcessDataModel>lambdaQuery()
                .select(VarProcessDataModel::getId, VarProcessDataModel::getObjectName)
                .eq(VarProcessDataModel::getId, inputVo.getDataModelId()));
        DomainModelTree dataModelTree = DomainModelTreeEntityUtils.beanCopyDomainModelTree(inputVo.getContent());
        //校验同一级下，不允许同名 以及 扩展数据下不允许定义原始数据
        try {
            DomainModelTreeEntityUtils.checkVarSparceModelTree(dataModelTree, dataModelTree.getName());
        } catch (com.wiseco.decision.common.exception.ServiceException originalException) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, originalException.getMessage().replace("Internal Server Error:", ""));
        }
        // 校验数据类型是否合法 DataVariableTypeEnum
        DomainModelTreeEntityUtils.verifyDataVariableType(dataModelTree);

        JSONObject inputJson = DomainModelTreeUtils.domainModelTreeConvertJsonObject(dataModelTree);
        int sourcePropertyNum = DomainModelTreeUtils.domainModelTreeNumByType(dataModelTree, "0");
        if (sourcePropertyNum > 0) {
            sourcePropertyNum -= 1;
        }
        int extendPropertyNum = DomainModelTreeUtils.domainModelTreeNumByType(dataModelTree, "1");
        //sourceInfo表示用的表或者服务
        String sourceInfo = null;
        if (inputVo.getFirstPageInfo().getSourceType() == VarProcessDataModelSourceType.INSIDE_DATA) {
            // 设置来源表/外部服务
            List<String> sourceInfoList = new ArrayList<>();
            final List<VariableDataModelAddNewNextInputVo.InsideOutputVO> tableOutput = inputVo.getFirstPageInfo().getInsideData().getTableOutput();
            //TABLE方式中获取所有用到的表名字
            processTableOutput(tableOutput, sourceInfoList);
            //SQL方式中获取所有用到的表名字
            final List<String> sqlTableNames = inputVo.getFirstPageInfo().getInsideData().getSqlTableNames();
            if (sqlTableNames != null && !sqlTableNames.isEmpty()) {
                sourceInfoList.addAll(sqlTableNames);
            }
            //组装所有表名用逗号隔开
            sourceInfo = sourceInfoList.stream().distinct().collect(Collectors.joining(","));
        }
        if (inputVo.getFirstPageInfo().getSourceType() == VarProcessDataModelSourceType.OUTSIDE_SERVER) {
            sourceInfo = inputVo.getFirstPageInfo().getOutsideServer().getOutName();
        }
        //更新数据模型表
        varProcessDataModelService.update(new UpdateWrapper<VarProcessDataModel>().lambda().eq(VarProcessDataModel::getId, inputVo.getDataModelId()).set(VarProcessDataModel::getContent, inputJson.toJSONString()).set(VarProcessDataModel::getSourcePropertyNum, sourcePropertyNum).set(VarProcessDataModel::getExtendPropertyNum, extendPropertyNum).set(VarProcessDataModel::getObjectSourceInfo, sourceInfo).set(VarProcessDataModel::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(VarProcessDataModel::getObjectName, inputVo.getFirstPageInfo().getObjectName()).set(VarProcessDataModel::getObjectLabel, inputVo.getFirstPageInfo().getObjectLabel()));
        if (inputVo.getFirstPageInfo().getSourceType() == VarProcessDataModelSourceType.INSIDE_DATA) {
            varProcessInternalDataService.update(new UpdateWrapper<VarProcessInternalData>().lambda().eq(VarProcessInternalData::getDataModelId, inputVo.getDataModelId()).set(VarProcessInternalData::getContent, JSONObject.toJSONString(inputVo.getFirstPageInfo().getInsideData())).set(VarProcessInternalData::getUpdatedUser, SessionContext.getSessionUser().getUsername()).set(VarProcessInternalData::getObjectName, inputVo.getFirstPageInfo().getObjectName()).set(VarProcessInternalData::getObjectLabel, inputVo.getFirstPageInfo().getObjectLabel()).set(VarProcessInternalData::getName, inputVo.getFirstPageInfo().getObjectLabel()).set(VarProcessInternalData::getDataType, inputVo.getFirstPageInfo().getInsideData().getInsideDataType()));
        }
        if (inputVo.getFirstPageInfo().getSourceType() == VarProcessDataModelSourceType.OUTSIDE_SERVER) {
            // 1. 将外部服务响应参数挂载至变量空间数据模型
            // 挂载数据源: 外部服务响应数据结构 JSONSchema properties 字段
            final OutsideServiceDetailRestOutputDto outsideInfo = outsideService.getOutsideServiceDetailRestById(inputVo.getFirstPageInfo().getOutsideServer().getOutId());
            JSONObject outsideServiceRespJsonSchema = JSON.parseObject(outsideInfo.getResp().getDataContent(), Feature.OrderedField);
            // 2. 新增引入信息并保存至数据库
            varProcessOutsideServiceRefService.update(new UpdateWrapper<VarProcessOutsideRef>().lambda().eq(VarProcessOutsideRef::getDataModelId, inputVo.getDataModelId())
                    .set(VarProcessOutsideRef::getVarProcessSpaceId, inputVo.getSpaceId())
                    .set(VarProcessOutsideRef::getOutsideServiceId, outsideInfo.getId())
                    .set(VarProcessOutsideRef::getName, inputVo.getFirstPageInfo().getObjectName())
                    .set(VarProcessOutsideRef::getNameCn, inputVo.getFirstPageInfo().getObjectLabel())
                    .set(VarProcessOutsideRef::getInputParameterBindings, outsideInfo.getReq().getRequestParam())
                    .set(VarProcessOutsideRef::getOutputParameterBindings, dealOutputParameterBindings(outsideServiceRespJsonSchema, inputVo.getFirstPageInfo().getObjectName(), inputVo.getFirstPageInfo().getObjectLabel()))
                    .set(VarProcessOutsideRef::getIsUseRootObject, inputVo.getFirstPageInfo().getOutsideServer().getIsUseRootObject() ? 1 : 0)
                    .set(VarProcessOutsideRef::getOutsideServiceCode,inputVo.getFirstPageInfo().getOutsideServer().getOutCode())
                    .set(VarProcessOutsideRef::getOutsideServiceName,inputVo.getFirstPageInfo().getOutsideServer().getOutName())
                    .set(VarProcessOutsideRef::getUpdatedUser, SessionContext.getSessionUser().getUsername()));
        }
        //组装并保存数据模型
        saveSpaceDataModel(inputVo.getSpaceId(), null);
        //动态
        saveDynamic(inputVo.getSpaceId(), SysDynamicOperateTypeEnum.EDIT.getName(), dataModel.getObjectName());
        return dataModel.getId();
    }

    private void dataModelVariableRuleIdentification(DomainDataModelTreeDto domainDataModelTreeDto) {
        if (!domainDataModelTreeDto.getName().matches(MATCHES_LETTER)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, domainDataModelTreeDto.getName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
        }
        if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
            for (DomainDataModelTreeDto domainDataModelTreeDto1 : domainDataModelTreeDto.getChildren()) {
                dataModelVariableRuleIdentification(domainDataModelTreeDto1);
            }
        }
    }

    /**
     * 获取所有数据模型引用的所有内部数据表名
     *
     * @return List String  所有数据模型引用的所有内部数据表名
     */
    public List<String> getAllInternalDataTableName() {

        QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "content");
        List<VarProcessInternalData> data = varProcessInternalDataMapper.selectList(queryWrapper);
        List<String> insideDataTableNameList = new ArrayList<>();
        if (data != null) {
            for (VarProcessInternalData varProcessInternalData : data) {
                if (varProcessInternalData != null) {
                    VariableDataModeViewOutputVo.DataModelInsideDataVO dataModelInsideDataVo1 = JSON.parseObject(varProcessInternalData.getContent(),
                            VariableDataModeViewOutputVo.DataModelInsideDataVO.class);
                    if (dataModelInsideDataVo1 != null) {
                        //如果是SQL方式直接获取内部表名
                        if (dataModelInsideDataVo1.getInsideDataType() == VarProcessDataModeInsideDataType.SQL) {
                            List<String> sqlInsideTableNameList = dataModelInsideDataVo1.getSqlTableNames();
                            for (String sqlInsideTableName : sqlInsideTableNameList) {
                                insideDataTableNameList.add(sqlInsideTableName);
                            }
                        }
                        //如果是源表获取的话，先获取表映射中的内部数据表名，如果存在children，再获取children中的表映射中的内部数据表名，由于只有一层children所以不需要嵌套
                        if (dataModelInsideDataVo1.getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                            List<VariableDataModeViewOutputVo.InsideOutputVO> insideOutputVOList = dataModelInsideDataVo1.getTableOutput();
                            for (VariableDataModeViewOutputVo.InsideOutputVO insideOutputVO : insideOutputVOList) {
                                insideDataTableNameList.add(insideOutputVO.getTableConfigs().getTableName());
                                if (CollectionUtils.isEmpty(insideOutputVO.getChildren())) {
                                    List<VariableDataModeViewOutputVo.InsideOutputVO> children = insideOutputVO.getChildren();
                                    for (VariableDataModeViewOutputVo.InsideOutputVO children1 : children) {
                                        insideDataTableNameList.add(children1.getTableConfigs().getTableName());
                                    }
                                }
                            }

                        }

                    }
                }
            }

        }

        return insideDataTableNameList;
    }

    /**
     * copyDataModel
     *
     * @param inputVo 输入
     * @return java.lang.Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long copyDataModel(VariableDataModelCopyInputVo inputVo) {
        VarProcessDataModel dataModel = varProcessDataModelService.getById(inputVo.getDataModelId());
        if (dataModel == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型");
        }

        List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, inputVo.getSpaceId()));
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名已存在！");
        }
        //关键字校验
        DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
        if (dataModelKeywordEnum != null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
        }
        DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(JSONObject.parseObject(dataModel.getContent()));
        domainModelTree.setDescribe(inputVo.getObjectLabel());
        domainModelTree.setLabel(inputVo.getObjectName() + "-" + inputVo.getObjectLabel());
        domainModelTree.setName(inputVo.getObjectName());

        JSONObject jsonObject = DomainModelTreeUtils.domainModelTreeConvertJsonObject(domainModelTree);

        UserDTO user = SessionContext.getSessionUser().getUser();
        dataModel.setVersion(NumberUtils.INTEGER_ONE);
        dataModel.setId(null);
        dataModel.setObjectName(inputVo.getObjectName());
        dataModel.setObjectLabel(inputVo.getObjectLabel());
        dataModel.setCreatedDept(user.getDepartment().getCode());
        dataModel.setCreatedDeptName(user.getDepartment().getName());
        dataModel.setCreatedUser(SessionContext.getSessionUser().getUsername());
        dataModel.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        dataModel.setCreatedTime(null);
        dataModel.setUpdatedTime(null);
        dataModel.setContent(JSONObject.toJSONString(jsonObject));
        varProcessDataModelService.save(dataModel);

        //内部数据表复制
        if (dataModel.getObjectSourceType() == VarProcessDataModelSourceType.INSIDE_DATA) {

            //wxs根据数据模型ID查出对应的内部数据表中数据
            QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_model_id", inputVo.getDataModelId());
            VarProcessInternalData data = varProcessInternalDataMapper.selectOne(queryWrapper);
            //wxs根据数据模型ID查出对应的内部数据表中数据
            data.setId(null);
            data.setIdentifier(GenerateIdUtil.generateId());
            data.setDataModelId(dataModel.getId());
            data.setObjectName(dataModel.getObjectName());
            data.setObjectLabel(data.getObjectLabel());
            varProcessInternalDataService.save(data);

        }
        //外部数据表复制
        if (dataModel.getObjectSourceType() == VarProcessDataModelSourceType.OUTSIDE_SERVER) {
            //根据数据模型ID查出对应的外部服务表中数据
            QueryWrapper<VarProcessOutsideRef> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_model_id", inputVo.getDataModelId());
            VarProcessOutsideRef data = varProcessOutsideRefMapper.selectOne(queryWrapper);

            //保存新数据模型对应的内部数据表信息
            data.setDataModelId(dataModel.getId());
            data.setName(dataModel.getObjectName());
            data.setNameCn(dataModel.getObjectLabel());
            data.setId(null);
            varProcessOutsideServiceRefService.save(data);
            //动态

        }

        //组装并保存数据模型
        saveSpaceDataModel(inputVo.getSpaceId(), null);
        saveDynamic(inputVo.getSpaceId(), SysDynamicOperateTypeEnum.EDIT.getName(), dataModel.getObjectName());

        return dataModel.getId();
    }

    /**
     * deleteDataModel
     *
     * @param inputVo 输入
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataModel(VariableDataModelDeletInputVo inputVo) {
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).eq(VarProcessDataModel::getVarProcessSpaceId, inputVo.getSpaceId()).eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()));
        if (CollectionUtils.isEmpty(dataModelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型");
        }
        //删除对应的数据模型
        varProcessDataModelService.remove(new QueryWrapper<VarProcessDataModel>().lambda().eq(VarProcessDataModel::getVarProcessSpaceId, inputVo.getSpaceId()).eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).eq(VarProcessDataModel::getId, inputVo.getDataModelId()));
        //如果SourceType为INSIDE_DATA，删除对应内部数据
        if (inputVo.getSourceType().equals(VarProcessDataModelSourceType.INSIDE_DATA.toString())) {
            varProcessInternalDataService.remove(new QueryWrapper<VarProcessInternalData>().lambda().eq(VarProcessInternalData::getDataModelId, inputVo.getDataModelId()).eq(VarProcessInternalData::getVarProcessSpaceId, inputVo.getSpaceId()).eq(VarProcessInternalData::getObjectName, inputVo.getObjectName()));
        }
        //如果SourceType为OUTSIDE_SERVER，删除对应外数服务数据
        if (inputVo.getSourceType().equals(VarProcessDataModelSourceType.OUTSIDE_SERVER.toString())) {
            varProcessOutsideServiceRefService.remove(new QueryWrapper<VarProcessOutsideRef>().lambda().eq(VarProcessOutsideRef::getDataModelId, inputVo.getDataModelId()).eq(VarProcessOutsideRef::getVarProcessSpaceId, inputVo.getSpaceId()).eq(VarProcessOutsideRef::getName, inputVo.getObjectName()));
        }
        //组装并保存数据模型
        saveSpaceDataModel(inputVo.getSpaceId(), null);

        //保存动态
        saveDynamic(inputVo.getSpaceId(), SysDynamicOperateTypeEnum.DELETE.getName(), inputVo.getObjectName());
    }

    /**
     * upDataModelVersion
     *
     * @param inputVo 输入
     * @return java.lang.Long
     */
    @Transactional(rollbackFor = Exception.class)
    public Long upDataModelVersion(VariableDataModelVersionInputVo inputVo) {
        //查出原先的数据模型
        VarProcessDataModel dataModel = getDataModel(inputVo.getObjectName(), varProcessDataModelService);

        //保存原先的DataModelId，后续使用这个id来查询相关内部和外部数据
        Long oldDataModelId = dataModel.getId();
        //保存新版本的数据模型
        UserDTO userDTO = SessionContext.getSessionUser().getUser();
        dataModel.setVersion(dataModel.getVersion() + 1);
        dataModel.setId(null);
        dataModel.setCreatedDept(userDTO.getDepartment().getCode());
        dataModel.setCreatedDeptName(userDTO.getDepartment().getName());
        dataModel.setCreatedUser(userDTO.getUsername());
        dataModel.setUpdatedUser(userDTO.getUsername());
        dataModel.setCreatedTime(null);
        dataModel.setUpdatedTime(null);
        varProcessDataModelService.save(dataModel);

        //如果SourceType为INSIDE_DATA，查出原先内部数据表数据，新建一条数据，设置新的modelId，其他的和原先数据一样
        if (dataModel.getObjectSourceType() == VarProcessDataModelSourceType.INSIDE_DATA) {
            //根据数据模型ID查出对应的内部数据表中数据
            QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_model_id", oldDataModelId);
            VarProcessInternalData data = varProcessInternalDataMapper.selectOne(queryWrapper);

            //保存新数据模型对应的内部数据表信息
            data.setDataModelId(dataModel.getId());
            data.setId(null);
            varProcessInternalDataService.save(data);
            //动态
            saveDynamic(inputVo.getSpaceId(), SysDynamicOperateTypeEnum.EDIT.getName(), dataModel.getObjectName());
            return dataModel.getId();
        }
        if (dataModel.getObjectSourceType() == VarProcessDataModelSourceType.OUTSIDE_SERVER) {
            //根据数据模型ID查出对应的外部服务表中数据
            QueryWrapper<VarProcessOutsideRef> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_model_id", oldDataModelId);
            VarProcessOutsideRef data = varProcessOutsideRefMapper.selectOne(queryWrapper);

            //保存新数据模型对应的内部数据表信息
            data.setDataModelId(dataModel.getId());
            data.setId(null);
            varProcessOutsideServiceRefService.save(data);
            //动态
            saveDynamic(inputVo.getSpaceId(), SysDynamicOperateTypeEnum.EDIT.getName(), dataModel.getObjectName());
            return dataModel.getId();
        }
        return dataModel.getId();
    }

    /**
     * 数据模型编辑前的校验
     *
     * @param inputVo 数据模型操作校验输入参数
     * @return 是否可以编辑的String，message为null进入编辑，不为空根据message进行提示
     */
    public String dataModelEditCheck(VariableDataModelOperateCheckInputVo inputVo) {
        VarProcessDataModel varProcessDataModel = getDataModel(inputVo.getObjectName(), varProcessDataModelService);
        String message = "该数据模型已被非编辑状态的";
        //数据模型被变量清单使用
        List<VariableDataModelManifestUseVo> manifestList = varProcessManifestDataModelService.getManifestUseMapping(inputVo.getSpaceId(), inputVo.getObjectName(), varProcessDataModel.getVersion());
        if (!CollectionUtils.isEmpty(manifestList)) {
            manifestList = manifestList.stream().filter(f -> !(f.getState().equals(VarProcessManifestStateEnum.EDIT))).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(manifestList)) {
                message = message + "变量清单";
            }
        }
        //数据模型被实时服务使用
        List<ManifestForRealTimeServiceVO> serviceIdList = varProcessManifestDataModelService.getManifestForRealTimeService(inputVo.getSpaceId(), inputVo.getObjectName(), varProcessDataModel.getVersion());

        List<VariableDataModelRealTimeServiceUseVo> realTimeServiceList = new ArrayList<>();
        for (ManifestForRealTimeServiceVO manifestForRealTimeServiceVO : serviceIdList) {
            VariableDataModelRealTimeServiceUseVo dataModelManifest = varProcessServiceManifestService.getRealTimeServiceUseMapping(manifestForRealTimeServiceVO.getServiceId(), manifestForRealTimeServiceVO.getManifestId());
            if (dataModelManifest != null) {
                realTimeServiceList.add(dataModelManifest);
            }
        }
        if (!CollectionUtils.isEmpty(realTimeServiceList)) {
            realTimeServiceList = realTimeServiceList.stream().filter(f -> !(f.getState().equals(VarProcessServiceStateEnum.EDITING.toString()))).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(realTimeServiceList)) {
                message = message + "、实时服务";
            }
        }
        //数据模型被批量回溯使用
        List<VarProcessBatchBacktracking> batchBacktracking = new ArrayList<>();
        for (VariableDataModelManifestUseVo variableDataModelManifestUseVo : manifestList) {
            QueryWrapper<VarProcessBatchBacktracking> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "status");
            queryWrapper.eq("manifest_id", variableDataModelManifestUseVo.getId());
            List<VarProcessBatchBacktracking> dataList = varProcessBatchBacktrackingMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(dataList)) {
                batchBacktracking.addAll(dataList);
            }
        }
        if (!CollectionUtils.isEmpty(batchBacktracking)) {
            batchBacktracking = batchBacktracking.stream().filter(f -> !(f.getStatus().equals(FlowStatusEnum.EDIT))).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(batchBacktracking)) {
                message = message + "、批量回溯";
            }
        }
        message = message + "引用，不允许修改，是否创建新的可编辑版本？";
        if (message.length() < MagicNumbers.THIRTY_FIVE) {
            message = null;
        }
        return message;
    }

    /**
     * 数据模型删除前的校验
     *
     * @param inputVo 数据模型操作校验输入参数
     * @return 通过返回String判断是否可以删除
     */
    public String dataModelDeleteCheck(VariableDataModelOperateCheckInputVo inputVo) {
        VarProcessDataModel varProcessDataModel = getDataModel(inputVo.getObjectName(), varProcessDataModelService);
        List<VariableDataModelVarUseOutputVo> applyList = dataModelViewBiz.getDataModelUseList(inputVo.getSpaceId(), inputVo.getObjectName(), varProcessDataModel.getVersion(), varProcessDataModel.getId());
        if (!CollectionUtils.isEmpty(applyList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_STATUS_NO_MATCH, "该数据模型对象已被使用，不允许删除!");
        } else {
            return "确认删除？";
        }
    }

    private VarProcessDataModel getDataModel(String objectName, VarProcessDataModelService varProcessDataModelService) {
        List<VarProcessDataModel> varProcessDataModelList = varProcessDataModelService.list(
                new QueryWrapper<VarProcessDataModel>().lambda()
                        .eq(VarProcessDataModel::getVarProcessSpaceId, CommonConstant.DEFAULT_SPACE_ID)
                        .eq(VarProcessDataModel::getObjectName, objectName)
                        .orderByDesc(VarProcessDataModel::getVersion)
        );
        if (CollectionUtils.isEmpty(varProcessDataModelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型");
        }
        return varProcessDataModelList.get(0);
    }

    /**
     * 保存动态
     *
     * @param spaceId
     * @param operateType
     * @param name
     */
    private void saveDynamic(Long spaceId, String operateType, String name) {
        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().spaceType(SysDynamicSpaceTypeEnum.VARIABLE.getCode()).varSpaceId(spaceId).operateType(operateType).typeEnum(SysDynamicBusinessBucketEnum.VARIABLE_DATA_MODEL).businessId(spaceId).businessDesc(name).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }

    /**
     * getOutsideList
     *
     * @return com.wiseco.outside.service.rpc.dto.output.OutsideServiceListRestOutputDto List
     */
    public List<OutsideServiceListRestOutputDto> getOutsideList() {
        return outsideService.getOutsideList();
    }

    /**
     * getOutsideDetailRest
     *
     * @param id 外部服务id
     * @return Map
     */
    public Map<String, String> getOutsideDetailRest(Long id) {
        final OutsideServiceDetailRestOutputDto data = outsideService.getOutsideServiceDetailRestById(id);
        log.info("getOutsideDetailRest:{}", data);

        //判断外数有没有根
        //入参
        if (null == data.getReq()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_OUTSIDE_NOT_DEFINE, "外部服务请求数据结构未定义。");
        }

        final OutsideServiceRespRestOutputDto resp = data.getResp();
        if (null == resp) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_OUTSIDE_NOT_DEFINE, "外部服务响应数据结构未定义。");
        }

        //获取外数输出结构体
        JSONObject outsideServiceRespJsonSchema = JSON.parseObject(resp.getDataContent()).getJSONObject(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage());

        final Set<String> keySet = outsideServiceRespJsonSchema.keySet();
        if (keySet.size() != 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_OUTSIDE_NO_ROOT_OBJ, "该外数没有根对象");
        }
        final String receiverObjectName = (String) keySet.toArray()[0];

        final String receiverObjectLabel = outsideServiceRespJsonSchema.getJSONObject(receiverObjectName).getString("description");

        Map<String, String> ret = new HashMap<>(MagicNumbers.EIGHT);
        ret.put("receiverObjectName", receiverObjectName);
        ret.put("receiverObjectLabel", receiverObjectLabel);
        return ret;
    }

    /**
     * 内部数据-sql取数-识别数据
     *
     * @param inputVo 入参Vo
     * @return String
     */
    public List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO> getSqlReturnVar(VariableDataModelAddSqlReturnVarCheckInputVo inputVo) {
        //解析SQL中的参数列表和表明列表
        List<String> foundParams = extractParameters(inputVo.getSql());
        List<String> foundTables = extractTables(inputVo.getSql());
        //判断参数和表是否有不存在的情况
        checkAndDisplayNotFoundElements(foundParams, inputVo.getParamList(), 0);
        checkAndDisplayNotFoundElements(foundTables, inputVo.getTableList(), 1);

        //识别数据
        if (!inputVo.getSql().isEmpty()) {
            List<String> aliasFields = new ArrayList<>();

            // 使用正则表达式匹配AS子句
            String regex = "\\bAS\\s+(\\w+)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputVo.getSql());

            // 查找并提取自命名字段
            while (matcher.find()) {
                String aliasField = matcher.group(1);
                if (!aliasField.matches(MATCHES_LETTER)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, aliasField + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
                }
                aliasFields.add(aliasField);
            }
            //根据是否返回多条来判断返回三层还是两层
            List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO> output = new ArrayList<>();
            if (inputVo.getSqlIsArray()) {
                VariableDataModelAddNewNextInputVo.InsideSqlOutputVO one = new VariableDataModelAddNewNextInputVo.InsideSqlOutputVO();
                one.setObjectName(inputVo.getObjectName());
                one.setObjectLabel(inputVo.getObjectLabel());
                one.setDataType("object");
                VariableDataModelAddNewNextInputVo.InsideSqlOutputVO two = new VariableDataModelAddNewNextInputVo.InsideSqlOutputVO();
                two.setObjectName(inputVo.getObjectName());
                two.setObjectLabel(inputVo.getObjectLabel());
                two.setDataType("object");
                two.setIsArr("1");

                List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO> three = new ArrayList<>();
                for (int i = 0; i < aliasFields.size(); i++) {
                    VariableDataModelAddNewNextInputVo.InsideSqlOutputVO data = new VariableDataModelAddNewNextInputVo.InsideSqlOutputVO();
                    data.setObjectName(aliasFields.get(i));
                    three.add(data);
                }
                two.setChildren(three);
                List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO> twoArray = new ArrayList<>();
                twoArray.add(two);
                one.setChildren(twoArray);
                output.add(one);
                return output;
            } else {

                VariableDataModelAddNewNextInputVo.InsideSqlOutputVO one = new VariableDataModelAddNewNextInputVo.InsideSqlOutputVO();
                one.setObjectName(inputVo.getObjectName());
                one.setObjectLabel(inputVo.getObjectLabel());
                one.setDataType("object");
                List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO> two = new ArrayList<>();
                for (int i = 0; i < aliasFields.size(); i++) {
                    VariableDataModelAddNewNextInputVo.InsideSqlOutputVO data = new VariableDataModelAddNewNextInputVo.InsideSqlOutputVO();
                    data.setObjectName(aliasFields.get(i));
                    two.add(data);
                }
                one.setChildren(two);
                output.add(one);
                return output;
            }
        } else {
            log.info("SQL语句不能为空");
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NO_SQL, "SQL语句不能为空");
        }

    }

    /**
     * 内部数据-sql取数-识别sql中使用的表
     *
     * @param sql 入参
     * @return List
     */
    private List<String> extractTables(String sql) {
        List<String> tables = new ArrayList<>();
        String[] lines = sql.split("\\n");
        boolean nextTokenIsTable = false;

        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            for (String token : tokens) {
                if (nextTokenIsTable) {
                    tables.add(token);
                    nextTokenIsTable = false;
                }
                if (token.equalsIgnoreCase("FROM") || token.equalsIgnoreCase("JOIN")) {
                    nextTokenIsTable = true;
                }
            }
        }
        return tables;
    }

    /**
     * 内部数据-sql取数-识别sql中使用的参数
     *
     * @param sql 入参
     * @return List
     */
    private List<String> extractParameters(String sql) {
        List<String> params = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(sql);
        while (matcher.find()) {
            String param = matcher.group(1);
            params.add(param);
        }
        return params;
    }

    /**
     * 内部数据-sql取数-判断是否在对应的参数表和使用表中
     *
     * @param foundElements
     * @param comparisonList
     * @param type
     */
    private void checkAndDisplayNotFoundElements(List<String> foundElements, List<String> comparisonList, Integer type) {
        for (String element : foundElements) {
            if (!comparisonList.contains(element)) {
                if (type == 0) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NO_SQL_PARAM, element + "在参数信息中没有定义");

                } else if (type == 1) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NO_SQL_TABLE, element + "在引用的内部数据表中没有选择");
                } else {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "未知类型");
                }
            }
        }
    }


}
