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
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataModelKeywordEnum;
import com.decision.jsonschema.util.enums.DomainModelTypeEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceRespRestOutputDto;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewNextInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelUpdateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelViewInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.enums.JsonSchemaFieldEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModeInsideDataType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessInternalDataMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DataModelPreviewBiz {
    @Resource
    private VarProcessDataModelService varProcessDataModelService;
    @Resource
    private OutsideService outsideService;
    @Resource
    private DataModelViewBiz dataModelViewBiz;
    @Resource
    private DataModelSaveBiz dataModelSaveBiz;
    @Resource
    private VarProcessInternalDataMapper varProcessInternalDataMapper;

    @Autowired
    private DmAdapter dmAdapter;

    private static final String MATCHES_LETTER = "^[a-zA-Z0-9][a-zA-Z0-9_]{0,100}$";
    private static final Pattern FIND_VALUE_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

    /**
     * 保存和预览
     *
     * @param inputVo 输入实体类对象
     * @return 保存和预览添加数据模型DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableDataModeViewOutputVo savaAndNextPreview(VariableDataModelAddNewNextInputVo inputVo) {
        dmAdapter.modifyGroupOptFlagOfConfigJdbc();
        VariableDataModeViewOutputVo dataModelViewOutputVo;
        switch (inputVo.getSourceType()) {
            case OUTSIDE_PARAM:
                dataModelViewOutputVo = outsideParamAndInsideLogicSavaAndNext(inputVo);
                break;
            case INSIDE_LOGIC:
                dataModelViewOutputVo = outsideParamAndInsideLogicSavaAndNext(inputVo);
                break;
            case INSIDE_DATA:
                dataModelViewOutputVo = insideDataSavaAndNext(inputVo);
                break;
            case OUTSIDE_SERVER:
                dataModelViewOutputVo = outsideServerSavaAndNext(inputVo);
                break;
            default:
                throw new IllegalArgumentException("未知的数据源类型" + inputVo.getSourceType());
        }
        return dataModelViewOutputVo;
    }

    /**
     * outsideParamAndInsideLogicSavaAndNext
     *
     * @param inputVo 输入
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableDataModeViewOutputVo outsideParamAndInsideLogicSavaAndNext(VariableDataModelAddNewNextInputVo inputVo) {
        if (inputVo.getDataModelId() == null) {
            List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
            }
            DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
            if (dataModelKeywordEnum != null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
            }
            if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
            }
            //获取数据结构
            DomainDataModelTreeDto dataModelTreeVo = new DomainDataModelTreeDto();
            if (VarProcessDataModelSourceType.OUTSIDE_PARAM.equals(inputVo.getSourceType())) {
                DataModelTreeVo dataModelTreeVo1 = outsideParamPreview(inputVo);
                dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
                dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
            } else {
                DataModelTreeVo dataModelTreeVo1 = insideLogicPreview(inputVo);
                dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
                dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
            }
            //保存数据模型
            VariableDataModelAddNewInputVo variableDataModelAddNewInputVo = new VariableDataModelAddNewInputVo();
            variableDataModelAddNewInputVo.setSpaceId(1L);
            VariableDataModelAddNewNextInputVo firstPageInfo = new VariableDataModelAddNewNextInputVo();
            firstPageInfo.setObjectName(inputVo.getObjectName());
            firstPageInfo.setObjectLabel(inputVo.getObjectLabel());
            firstPageInfo.setSourceType(inputVo.getSourceType());
            variableDataModelAddNewInputVo.setFirstPageInfo(firstPageInfo);
            variableDataModelAddNewInputVo.setContent(dataModelTreeVo);
            Long dataModelId = dataModelSaveBiz.addDataModel(variableDataModelAddNewInputVo);
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            VariableDataModeViewOutputVo viewOutputVoList = dataModelViewBiz.dataModelView(viewInputVo);
            return viewOutputVoList;
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getById(inputVo.getDataModelId());
        //对象名和对象中文名都相等，返回原有数据
        if (dataModel.getObjectName().equals(inputVo.getObjectName()) && dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(inputVo.getDataModelId());
            VariableDataModeViewOutputVo viewOutputVoList = dataModelViewBiz.dataModelView(viewInputVo);
            return viewOutputVoList;
        } else if (dataModel.getObjectName().equals(inputVo.getObjectName()) && !dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
            if (dataModel.getObjectName().equals(inputVo.getObjectName()) && !dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
                List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
                if (!CollectionUtils.isEmpty(list)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象中文名在数据模型中已存在！");
                }
            }
            //以下保存数据模型相关内容
            Set<String> varPathMap = dataModelViewBiz.getUseVarList(1L);
            DomainDataModelTreeDto inputData = DomainModelTreeEntityUtils.transferDataModelTreeDto(dataModel.getContent(), varPathMap);
            inputData.setName(inputVo.getObjectName());
            inputData.setLabel(inputVo.getObjectLabel());
            inputData.setDescribe(inputVo.getObjectLabel());
            VariableDataModelUpdateInputVo variableDataModelUpdateInputVo = new VariableDataModelUpdateInputVo();
            variableDataModelUpdateInputVo.setDataModelId(inputVo.getDataModelId());
            variableDataModelUpdateInputVo.setSpaceId(1L);
            VariableDataModelAddNewNextInputVo firstPageInfo = new VariableDataModelAddNewNextInputVo();
            firstPageInfo.setObjectName(inputVo.getObjectName());
            firstPageInfo.setObjectLabel(inputVo.getObjectLabel());
            firstPageInfo.setSourceType(inputVo.getSourceType());
            variableDataModelUpdateInputVo.setFirstPageInfo(firstPageInfo);
            variableDataModelUpdateInputVo.setContent(inputData);
            Long dataModelId = dataModelSaveBiz.updateDataModel(variableDataModelUpdateInputVo);
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            VariableDataModeViewOutputVo viewOutputVoList = dataModelViewBiz.dataModelView(viewInputVo);
            return viewOutputVoList;
        } else {
            return getVariableDataModeViewOutputVo(inputVo, dataModel);
        }
    }

    private void dataModelTreeVoConvertToDomainDataModelTreeDto(DataModelTreeVo dataModelTreeVo, DomainDataModelTreeDto domainDataModelTreeDto) {
        BeanUtils.copyProperties(dataModelTreeVo, domainDataModelTreeDto);
        if (!CollectionUtils.isEmpty(dataModelTreeVo.getChildren())) {
            List<DomainDataModelTreeDto> domainDataModelTreeDtos = new ArrayList<>();
            for (DataModelTreeVo child : dataModelTreeVo.getChildren()) {
                DomainDataModelTreeDto domainDataModelTreeDto1 = new DomainDataModelTreeDto();
                dataModelTreeVoConvertToDomainDataModelTreeDto(child, domainDataModelTreeDto1);
                domainDataModelTreeDtos.add(domainDataModelTreeDto1);
            }
            domainDataModelTreeDto.setChildren(domainDataModelTreeDtos);
        }
    }

    private DataModelTreeVo outsideParamPreview(VariableDataModelAddNewNextInputVo inputDto) {
        DataModelTreeVo dataModelTreeVo = new DataModelTreeVo();
        dataModelTreeVo.setName(inputDto.getObjectName());
        dataModelTreeVo.setLabel(inputDto.getObjectName() + "-" + inputDto.getObjectLabel());
        dataModelTreeVo.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        dataModelTreeVo.setValue(inputDto.getObjectName());
        dataModelTreeVo.setDescribe(inputDto.getObjectLabel());
        return dataModelTreeVo;
    }

    private DataModelTreeVo insideLogicPreview(VariableDataModelAddNewNextInputVo inputDto) {
        DataModelTreeVo dataModelTreeVo = outsideParamPreview(inputDto);
        dataModelTreeVo.setIsExtend("1");
        return dataModelTreeVo;
    }


    /**
     * insideDataSavaAndNext
     *
     * @param inputVo 输入
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableDataModeViewOutputVo insideDataSavaAndNext(VariableDataModelAddNewNextInputVo inputVo) {
        //新建数据模型
        if (inputVo.getDataModelId() == null) {
            List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
            }
            //关键字校验
            DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
            if (dataModelKeywordEnum != null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
            }
            if (inputVo.getInsideData().getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                tableOutputVariableRuleIdentification(inputVo.getInsideData().getTableOutput());
            }
//            VarProcessDataModel dataModel = new VarProcessDataModel();
            //获取数据结构
            DomainDataModelTreeDto dataModelTreeVo = new DomainDataModelTreeDto();
            DataModelTreeVo dataModelTreeVo1 = insideDataPreview(inputVo);
            dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
            dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
            //保存数据模型
            VariableDataModelAddNewInputVo variableDataModelAddNewInputVo = new VariableDataModelAddNewInputVo();
            variableDataModelAddNewInputVo.setSpaceId(1L);
            VariableDataModelAddNewNextInputVo firstPageInfo = new VariableDataModelAddNewNextInputVo();
            firstPageInfo.setObjectName(inputVo.getObjectName());
            firstPageInfo.setObjectLabel(inputVo.getObjectLabel());
            firstPageInfo.setInsideData(inputVo.getInsideData());
            firstPageInfo.setSourceType(inputVo.getSourceType());
            firstPageInfo.setOutsideServer(inputVo.getOutsideServer());
            variableDataModelAddNewInputVo.setFirstPageInfo(firstPageInfo);
            variableDataModelAddNewInputVo.setContent(dataModelTreeVo);
            Long dataModelId = dataModelSaveBiz.addDataModel(variableDataModelAddNewInputVo);
            //调用查看回显数据模型所有
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            return dataModelViewBiz.dataModelView(viewInputVo);
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getById(inputVo.getDataModelId());
        if (dataModel == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "该数据模型不存在");
        }
        if (!inputVo.getIsChange()) {
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(inputVo.getDataModelId());
            return dataModelViewBiz.dataModelView(viewInputVo);
        } else {
            if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
            }
            if (!dataModel.getObjectName().equals(inputVo.getObjectName()) || !dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
                List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
                if (!CollectionUtils.isEmpty(list)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
                }
                //关键字校验
                DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
                if (dataModelKeywordEnum != null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
                }
            }
            QueryWrapper<VarProcessInternalData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("data_model_id", inputVo.getDataModelId());
            VarProcessInternalData data = varProcessInternalDataMapper.selectOne(queryWrapper);
            VariableDataModeViewOutputVo.DataModelInsideDataVO oldInsideDataVo = JSON.parseObject(data.getContent(), VariableDataModeViewOutputVo.DataModelInsideDataVO.class);
            //判断层级是否匹配
            Boolean change = determineLayerChanged(inputVo,oldInsideDataVo);
            if (inputVo.getInsideData().getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                tableOutputVariableRuleIdentification(inputVo.getInsideData().getTableOutput());
            }
            Long dataModelId = reconstructOutsideServerTree(inputVo, dataModel,change);
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            return dataModelViewBiz.dataModelView(viewInputVo);
        }
    }

    private Boolean determineLayerChanged(VariableDataModelAddNewNextInputVo inputVo,VariableDataModeViewOutputVo.DataModelInsideDataVO oldInsideDataVo) {
        Boolean change;
        if (inputVo.getInsideData().getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
            if (oldInsideDataVo.getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                change = false;
            } else {
                if (oldInsideDataVo.getSqlIsArray()) {
                    change = true;
                } else {
                    change = false;
                }
            }
        } else {
            if (oldInsideDataVo.getInsideDataType() == VarProcessDataModeInsideDataType.TABLE) {
                if (inputVo.getInsideData().getSqlIsArray()) {
                    change = true;
                } else {
                    change = false;
                }
            } else {
                if (inputVo.getInsideData().getSqlIsArray().equals(oldInsideDataVo.getSqlIsArray())) {
                    change = false;
                } else {
                    change = true;
                }
            }
        }
        return change;
    }

    private void tableOutputVariableRuleIdentification(List<VariableDataModelAddNewNextInputVo.InsideOutputVO> tableOutputList) {
        for (VariableDataModelAddNewNextInputVo.InsideOutputVO tableOutput : tableOutputList) {
            if (!tableOutput.getObjectName().matches(MATCHES_LETTER)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, tableOutput.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
            }
            if (!CollectionUtils.isEmpty(tableOutput.getChildren())) {
                tableOutputVariableRuleIdentification(tableOutput.getChildren());
            }
        }
    }

    private DataModelTreeVo insideDataPreview(VariableDataModelAddNewNextInputVo inputDto) {
        DataModelTreeVo dataModelTreeVo = new DataModelTreeVo();
        switch (inputDto.getInsideData().getInsideDataType()) {
            case TABLE:
                //这一步是验证数据
                validInternalData(inputDto);
                dataModelTreeVo = combinedDataModel(inputDto);
                break;
            case SQL:
                recursionDataModelSqlType(dataModelTreeVo, inputDto, null);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.DATA_TYPE_IS_EMPTY, "内部数据类型必须进行选择不能为空");
        }
        return dataModelTreeVo;
    }

    /**
     * 组装数据模型
     *
     * @param inputDto
     * @return 决策领域树形结构实体复制版本实体
     */
    private DataModelTreeVo combinedDataModel(VariableDataModelAddNewNextInputVo inputDto) {
        DataModelTreeVo dataModelTreeVo = new DataModelTreeVo();
        VariableDataModelAddNewNextInputVo.InsideOutputVO output = inputDto.getInsideData().getTableOutput().get(0);
        recursionDataModel(dataModelTreeVo, output, null);
        return dataModelTreeVo;
    }


    /**
     * 递归转换变量空间内部数据返回数据
     *
     * @param dataModelTreeVo 树形结构 DTO
     * @param output          内部数据返回数据
     * @param parentValue     父级变量路径
     */
    public static void recursionDataModel(DataModelTreeVo dataModelTreeVo, VariableDataModelAddNewNextInputVo.InsideOutputVO output, String parentValue) {
        //设置最外边一层的属性
        dataModelTreeVo.setName(output.getObjectName());
        dataModelTreeVo.setLabel(output.getObjectName() + "-" + output.getObjectLabel());
        dataModelTreeVo.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        if (StringUtils.isEmpty(parentValue)) {
            dataModelTreeVo.setValue(output.getObjectName());
        } else {
            dataModelTreeVo.setValue(parentValue + "." + output.getObjectName());
        }
        dataModelTreeVo.setIsArr(output.getIsArr());
        dataModelTreeVo.setDescribe(output.getObjectLabel());
        List<DataModelTreeVo> childrenList = new ArrayList<>();

        if (output.getTableConfigs() != null && !CollectionUtils.isEmpty(output.getTableConfigs().getFieldMapping())) {
            List<VariableDataModelAddNewNextInputVo.FieldMappingVO> fieldMapping = output.getTableConfigs().getFieldMapping();
            for (VariableDataModelAddNewNextInputVo.FieldMappingVO fieldMappingDto : fieldMapping) {
                DataModelTreeVo dto = new DataModelTreeVo();
                dto.setName(fieldMappingDto.getMappingName());
                dto.setLabel(fieldMappingDto.getMappingName() + "-" + fieldMappingDto.getMappingLabel());
                dto.setType(fieldMappingDto.getMappingDataType());
                dto.setValue(dataModelTreeVo.getValue() + "." + fieldMappingDto.getMappingName());
                dto.setIsArr("0");
                dto.setDescribe(fieldMappingDto.getMappingLabel());
                childrenList.add(dto);
            }
        }

        if (!CollectionUtils.isEmpty(output.getChildren())) {
            List<VariableDataModelAddNewNextInputVo.InsideOutputVO> children = output.getChildren();
            for (VariableDataModelAddNewNextInputVo.InsideOutputVO outputDto : children) {
                DataModelTreeVo childDataModelTreeDto = new DataModelTreeVo();
                recursionDataModel(childDataModelTreeDto, outputDto, dataModelTreeVo.getValue());
                childrenList.add(childDataModelTreeDto);
            }
        }

        dataModelTreeVo.setChildren(childrenList);

    }

    /**
     * SQL取值方式-生成树形结构
     *
     * @param dataModelTreeVo 数据模型树
     * @param output 数据模型相关参数
     * @param parentValue 上层值
     */
    public static void recursionDataModelSqlType(DataModelTreeVo dataModelTreeVo, VariableDataModelAddNewNextInputVo output, String parentValue) {
        List<Object> useList = verifySqlAvailableParameters(output.getInsideData());
        if (useList.size() == 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "请检查是否存在未剔除的参数");
        }
        //返回多条数据结构为三层
        if (output.getInsideData().getSqlIsArray()) {
            //设置第一层
            dataModelTreeVo.setName(output.getObjectName());
            dataModelTreeVo.setLabel(output.getObjectName() + "-" + output.getObjectLabel());
            dataModelTreeVo.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            if (StringUtils.isEmpty(parentValue)) {
                dataModelTreeVo.setValue(output.getObjectName());
            } else {
                dataModelTreeVo.setValue(parentValue + "." + output.getObjectName());
            }
            dataModelTreeVo.setDescribe(output.getObjectLabel());
            dataModelTreeVo.setIsRefRootNode("1");

            DataModelTreeVo two = new DataModelTreeVo();
            List<DataModelTreeVo> twoList = new ArrayList<>();
            VariableDataModelAddNewNextInputVo.InsideSqlOutputVO twoLever = output.getInsideData().getSqlOutput().get(0).getChildren().get(0);
            two.setName(twoLever.getObjectName());
            two.setLabel(twoLever.getObjectName() + "-" + twoLever.getObjectLabel());
            two.setType("object");
            two.setValue(dataModelTreeVo.getValue() + "." + twoLever.getObjectName());
            two.setDescribe(twoLever.getObjectLabel());
            two.setIsArr("1");
            //设置第二层和第三层
            List<DataModelTreeVo> childrenList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(output.getInsideData().getSqlOutput().get(0).getChildren().get(0).getChildren())) {
                for (VariableDataModelAddNewNextInputVo.InsideSqlOutputVO sqlOutputDto : output.getInsideData().getSqlOutput().get(0).getChildren().get(0).getChildren()) {
                    if ("0".equals(sqlOutputDto.getIsDelete())) {
                        DataModelTreeVo childDataModelTreeDto = new DataModelTreeVo();
                        childDataModelTreeDto.setName(sqlOutputDto.getObjectName());
                        childDataModelTreeDto.setLabel(sqlOutputDto.getObjectName() + "-" + sqlOutputDto.getObjectLabel());
                        childDataModelTreeDto.setType(sqlOutputDto.getDataType().toString());
                        childDataModelTreeDto.setValue(two.getValue() + "." + sqlOutputDto.getObjectName());
                        childDataModelTreeDto.setDescribe(sqlOutputDto.getObjectLabel());
                        childrenList.add(childDataModelTreeDto);
                    }

                }
            }
            two.setChildren(childrenList);
            twoList.add(two);
            dataModelTreeVo.setChildren(twoList);
        } else {
            //返回单条数据结构为两层
            dataModelTreeVo.setName(output.getObjectName());
            dataModelTreeVo.setLabel(output.getObjectName() + "-" + output.getObjectLabel());
            dataModelTreeVo.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
            if (StringUtils.isEmpty(parentValue)) {
                dataModelTreeVo.setValue(output.getObjectName());
            } else {
                dataModelTreeVo.setValue(parentValue + "." + output.getObjectName());
            }
            dataModelTreeVo.setDescribe(output.getObjectLabel());
            dataModelTreeVo.setIsRefRootNode("1");
            //设置第二层
            List<DataModelTreeVo> childrenList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(output.getInsideData().getSqlOutput().get(0).getChildren())) {
                for (VariableDataModelAddNewNextInputVo.InsideSqlOutputVO sqlOutputDto : output.getInsideData().getSqlOutput().get(0).getChildren()) {
                    if ("0".equals(sqlOutputDto.getIsDelete())) {
                        DataModelTreeVo childDataModelTreeDto = new DataModelTreeVo();
                        childDataModelTreeDto.setName(sqlOutputDto.getObjectName());
                        childDataModelTreeDto.setLabel(sqlOutputDto.getObjectName() + "-" + sqlOutputDto.getObjectLabel());
                        childDataModelTreeDto.setType(sqlOutputDto.getDataType().toString());
                        childDataModelTreeDto.setValue(dataModelTreeVo.getValue() + "." + sqlOutputDto.getObjectName());
                        childDataModelTreeDto.setDescribe(sqlOutputDto.getObjectLabel());
                        childrenList.add(childDataModelTreeDto);
                    }

                }
            }
            dataModelTreeVo.setChildren(childrenList);
        }
    }

    /**
     * 验证SQL取数的可用参数是否不为空
     * @param inputVo 数据模型入参
     * @return 可用参数List
     */
    public static List<Object> verifySqlAvailableParameters(VariableDataModelAddNewNextInputVo.DataModelInsideDataVO inputVo) {
        ArrayList<Object> useList = new ArrayList<>();
        if (inputVo.getSqlIsArray()) {
            for (VariableDataModelAddNewNextInputVo.InsideSqlOutputVO sqlOutputDto : inputVo.getSqlOutput().get(0).getChildren().get(0).getChildren()) {
                if ("0".equals(sqlOutputDto.getIsDelete())) {
                    useList.add(sqlOutputDto.getObjectName());
                }
            }
        } else {
            for (VariableDataModelAddNewNextInputVo.InsideSqlOutputVO sqlOutputDto : inputVo.getSqlOutput().get(0).getChildren()) {
                if ("0".equals(sqlOutputDto.getIsDelete())) {
                    useList.add(sqlOutputDto.getObjectName());
                }
            }

        }
        return useList;
    }

    private void validInternalData(VariableDataModelAddNewNextInputVo inputDto) {
        if (inputDto.getInsideData() == null || CollectionUtils.isEmpty(inputDto.getInsideData().getInput())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "入参信息和返回数据不能为空");
        }
        //入参信息
        Set<String> inputSetList = new HashSet<>();
        List<VariableDataModelAddNewNextInputVo.InsideInputVO> inputDtoList = inputDto.getInsideData().getInput();
        for (VariableDataModelAddNewNextInputVo.InsideInputVO input : inputDtoList) {
            if (StringUtils.isEmpty(input.getName()) || StringUtils.isEmpty(input.getLabel()) || StringUtils.isEmpty(input.getDataType())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "入参信息填写不完整");
            }

            inputSetList.add(input.getName());
        }

        //返回数据
        List<VariableDataModelAddNewNextInputVo.InsideOutputVO> outputDtoList = inputDto.getInsideData().getTableOutput();
        for (VariableDataModelAddNewNextInputVo.InsideOutputVO outputDto : outputDtoList) {
            recursionValidInternalData(inputSetList, outputDto);
        }

    }


    /**
     * 递归验证内部数据
     *
     * @param inputSetList
     * @param outputDto
     */
    private void recursionValidInternalData(Set<String> inputSetList, VariableDataModelAddNewNextInputVo.InsideOutputVO outputDto) {

        if (StringUtils.isEmpty(outputDto.getObjectName()) || StringUtils.isEmpty(outputDto.getObjectLabel()) || StringUtils.isEmpty(outputDto.getIsArr()) || StringUtils.isEmpty(outputDto.getIsMapping())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NOT_DEFINE, "返回数据中的对象填写不完整");
        }

        //映射表
        Integer isMapping = Integer.parseInt(outputDto.getIsMapping());
        if (CollectionUtils.isEmpty(outputDto.getChildren()) && NumberUtils.INTEGER_ZERO.equals(isMapping)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NO_MAPPING_TABLE, "返回数据中的叶子节点对象[" + outputDto.getObjectName() + "]没有映射表");
        }
        if (NumberUtils.INTEGER_ONE.equals(isMapping)) {

            if (outputDto.getTableConfigs() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NO_MAPPING_TABLE, "返回数据中的对象[" + outputDto.getObjectName() + "]没有映射表");

            } else {
                VariableDataModelAddNewNextInputVo.TableConfigsVO tableConfigs = outputDto.getTableConfigs();
                if (StringUtils.isEmpty(tableConfigs.getTableName()) || StringUtils.isEmpty(tableConfigs.getConditions()) || CollectionUtils.isEmpty(tableConfigs.getFieldMapping())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NO_MAPPING_TABLE, "返回数据中的对象[" + outputDto.getObjectName() + "]映射表信息填写不完整。");
                } else {
                    //条件验证
                    Set<String> valueByRegx = findValueByRegx(tableConfigs.getConditions());
                    if (CollectionUtils.isEmpty(valueByRegx)) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NOT_DEFINE, "返回数据中的对象[" + outputDto.getObjectName() + "]条件设置没有使用定义的入参");
                    }

                    List<String> msgList = new ArrayList<>();
                    for (String str : valueByRegx) {
                        if (!inputSetList.contains(str)) {
                            msgList.add(str);
                        }
                    }
                    if (!CollectionUtils.isEmpty(msgList)) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NOT_DEFINE, "返回数据中的对象[" + outputDto.getObjectName() + "]条件设置使用的入参[" + org.apache.commons.lang3.StringUtils.join(msgList, ",") + "]没有定义");
                    }

                    //字段映射验证
                    List<VariableDataModelAddNewNextInputVo.FieldMappingVO> fieldMapping = tableConfigs.getFieldMapping();
                    for (VariableDataModelAddNewNextInputVo.FieldMappingVO mappingDto : fieldMapping) {
                        if (StringUtils.isEmpty(mappingDto.getName())
                                || StringUtils.isEmpty(mappingDto.getDataType())
                                || StringUtils.isEmpty(mappingDto.getColumnType())
                                || StringUtils.isEmpty(mappingDto.getMappingName())
                                || StringUtils.isEmpty(mappingDto.getMappingLabel())
                                || StringUtils.isEmpty(mappingDto.getMappingDataType())) {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_RETURN_DATA_NO_MAPPING_TABLE, "返回数据中的对象[" + outputDto.getObjectName() + "]字段映射信息填写不完整");
                        }
                    }
                }
            }

        }

        //子项
        if (!CollectionUtils.isEmpty(outputDto.getChildren())) {
            for (VariableDataModelAddNewNextInputVo.InsideOutputVO child : outputDto.getChildren()) {
                recursionValidInternalData(inputSetList, child);
            }
        }
    }


    private VariableDataModeViewOutputVo getVariableDataModeViewOutputVo(VariableDataModelAddNewNextInputVo inputVo, VarProcessDataModel dataModel) {
        //重构数据结构
        if (!dataModel.getObjectName().equals(inputVo.getObjectName()) && !dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
            List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
            }
            //关键字校验
            DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
            if (dataModelKeywordEnum != null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
            }
        }

        if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
        }
        DomainDataModelTreeDto dataModelTreeVo = new DomainDataModelTreeDto();
        if (VarProcessDataModelSourceType.OUTSIDE_PARAM.equals(inputVo.getSourceType())) {
            DataModelTreeVo dataModelTreeVo1 = outsideParamPreview(inputVo);
            dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
            dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
        } else {
            DataModelTreeVo dataModelTreeVo1 = insideLogicPreview(inputVo);
            dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
            dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
        }

        //更新数据结构
        VariableDataModelUpdateInputVo variableDataModelUpdateInputVo = new VariableDataModelUpdateInputVo();
        variableDataModelUpdateInputVo.setDataModelId(inputVo.getDataModelId());
        variableDataModelUpdateInputVo.setSpaceId(1L);
        VariableDataModelAddNewNextInputVo firstPageInfo = new VariableDataModelAddNewNextInputVo();
        firstPageInfo.setObjectName(inputVo.getObjectName());
        firstPageInfo.setObjectLabel(inputVo.getObjectLabel());
        firstPageInfo.setSourceType(inputVo.getSourceType());
        variableDataModelUpdateInputVo.setFirstPageInfo(firstPageInfo);
        variableDataModelUpdateInputVo.setContent(dataModelTreeVo);
        Long dataModelId = dataModelSaveBiz.updateDataModel(variableDataModelUpdateInputVo);

        //调用数据查看回显
        VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
        viewInputVo.setSpaceId(1L);
        viewInputVo.setDataModelId(dataModelId);
        return dataModelViewBiz.dataModelView(viewInputVo);
    }

    /**
     * outsideServerSavaAndNext
     *
     * @param inputVo 输入
     * @return com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo
     */
    @Transactional(rollbackFor = Exception.class)
    public VariableDataModeViewOutputVo outsideServerSavaAndNext(VariableDataModelAddNewNextInputVo inputVo) {
        //新建数据模型
        if (inputVo.getDataModelId() == null) {
            List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
            if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
            }
            if (!CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
            }
            //关键字校验
            DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
            if (dataModelKeywordEnum != null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
            }
            //获取数据结构
            DomainDataModelTreeDto dataModelTreeVo = new DomainDataModelTreeDto();
            DataModelTreeVo dataModelTreeVo1 = outsideServerPreview(inputVo);
            dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, dataModelTreeVo);
            dataModelTreeVo.setDescribe(inputVo.getObjectLabel());
            //保存数据模型
            VariableDataModelAddNewInputVo variableDataModelAddNewInputVo = new VariableDataModelAddNewInputVo();
            variableDataModelAddNewInputVo.setSpaceId(1L);
            VariableDataModelAddNewNextInputVo firstPageInfo = new VariableDataModelAddNewNextInputVo();
            firstPageInfo.setSourceType(inputVo.getSourceType());
            firstPageInfo.setObjectName(inputVo.getObjectName());
            firstPageInfo.setObjectLabel(inputVo.getObjectLabel());
            firstPageInfo.setInsideData(inputVo.getInsideData());
            firstPageInfo.setOutsideServer(inputVo.getOutsideServer());
            variableDataModelAddNewInputVo.setFirstPageInfo(firstPageInfo);
            variableDataModelAddNewInputVo.setContent(dataModelTreeVo);
            Long dataModelId = dataModelSaveBiz.addDataModel(variableDataModelAddNewInputVo);
            //调用查看回显数据模型所有
            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            return dataModelViewBiz.dataModelView(viewInputVo);
        } else {
            VarProcessDataModel dataModel = varProcessDataModelService.getById(inputVo.getDataModelId());
            Long dataModelId = null;
            //对象名、对象中文名都相等，拉取最新外数重构数据模型,不需要进行校验
            if (dataModel.getObjectName().equals(inputVo.getObjectName()) && dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
                dataModelId = reconstructOutsideServerTree(inputVo, dataModel,false);
                //对象名都相等，对象中文名不相等，改变对象中文名和改变数据结构中第一层的对应对象中文名
            } else if (dataModel.getObjectName().equals(inputVo.getObjectName()) && !dataModel.getObjectLabel().equals(inputVo.getObjectLabel())) {
                //对象名相等，对象中文名不相等，校验中文名，拉取最新外数重构数据模型
                List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
                if (!CollectionUtils.isEmpty(list)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象中文名在数据模型中已存在！");
                }
                if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
                }
                //将数据模型中的content转换为treeDto的操作
                dataModelId = reconstructOutsideServerTree(inputVo, dataModel,false);
            } else {
                //对象名，对象中文名不相等，校验两者，拉取最新外数重构数据模型
                if (!inputVo.getObjectName().matches(MATCHES_LETTER)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, inputVo.getObjectName() + "变量定义错误，变量只能大小写字母、数字、下划线组合,并且首位为字母");
                }
                //重构数据结构
                List<VarProcessDataModel> list = varProcessDataModelService.list(new QueryWrapper<VarProcessDataModel>().lambda().select(VarProcessDataModel::getId).and(i -> i.eq(VarProcessDataModel::getObjectName, inputVo.getObjectName()).or().eq(VarProcessDataModel::getObjectLabel, inputVo.getObjectLabel())).eq(VarProcessDataModel::getVarProcessSpaceId, 1));
                if (!CollectionUtils.isEmpty(list)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_EXISTS, "对象名称或对象中文名在数据模型中已存在！");
                }
                //关键字校验
                DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVo.getObjectName().toLowerCase());
                if (dataModelKeywordEnum != null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "对象名称不允许使用关键字" + inputVo.getObjectName());
                }
                dataModelId = reconstructOutsideServerTree(inputVo, dataModel,false);
            }

            VariableDataModelViewInputVo viewInputVo = new VariableDataModelViewInputVo();
            viewInputVo.setSpaceId(1L);
            viewInputVo.setDataModelId(dataModelId);
            return dataModelViewBiz.dataModelView(viewInputVo);
        }
    }

    private DataModelTreeVo outsideServerPreview(VariableDataModelAddNewNextInputVo inputDto) {
        final OutsideServiceDetailRestOutputDto data = outsideService.getOutsideServiceDetailRestById(inputDto.getOutsideServer().getOutId());
        final OutsideServiceRespRestOutputDto resp = data.getResp();
        Assert.notNull(resp, "外部服务响应数据结构未定义。");
        JSONObject outsideServiceRespJsonSchema = JSON.parseObject(resp.getDataContent(), Feature.OrderedField);
        if (Boolean.TRUE.equals(inputDto.getOutsideServer().getIsUseRootObject())) {
            outsideServiceRespJsonSchema.put(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage(), outsideServiceRespJsonSchema.getJSONObject(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage()).getJSONObject(inputDto.getObjectName()).getJSONObject(JsonSchemaFieldEnum.PROPERTIES_FIELD.getMessage()));
        }
        final DomainModelTree domainModelTree = DomainModelTreeUtils.jsonObjectConvertDomainModelTree(outsideServiceRespJsonSchema);
        DataModelTreeVo dataModelTreeVo = new DataModelTreeVo();
        domainModelTreeConvertToDataModelTreeVo(domainModelTree, dataModelTreeVo);

        dataModelTreeVo.setName(inputDto.getObjectName());
        dataModelTreeVo.setLabel(inputDto.getObjectLabel());
        dataModelTreeVo.setValue(inputDto.getObjectName());
        dataModelTreeVo.setType(DomainModelTypeEnum.OBJECT_DOMAIN_MODE_TYPE.getMessage());
        dataModelTreeVo.setDescribe(inputDto.getObjectLabel());
        return dataModelTreeVo;
    }

    private void domainModelTreeConvertToDataModelTreeVo(DomainModelTree domainModelTree, DataModelTreeVo dataModelTreeVo) {
        BeanUtils.copyProperties(domainModelTree, dataModelTreeVo);
        if (!CollectionUtils.isEmpty(domainModelTree.getChildren())) {
            List<DataModelTreeVo> dataModelTreeVos = new ArrayList<>();
            for (DomainModelTree child : domainModelTree.getChildren()) {
                DataModelTreeVo dataModelTreeVo1 = new DataModelTreeVo();
                domainModelTreeConvertToDataModelTreeVo(child, dataModelTreeVo1);
                dataModelTreeVos.add(dataModelTreeVo1);
            }
            dataModelTreeVo.setChildren(dataModelTreeVos);
        }
    }

    /**
     * 根据正则表达式获取指定字符之间的值
     *
     * @param str
     * @return Set<String>
     */
    private Set<String> findValueByRegx(String str) {

        Matcher m = FIND_VALUE_PATTERN.matcher(str);
        Set<String> matchStrs = new HashSet<>();
        while (m.find()) {
            matchStrs.add(m.group(1));
        }
        return matchStrs;
    }


    /**
     * 将另一个数据模型树状结构中的扩展数据添加到当前数据模型树中
     * @param currentTree 当前数据模型树
     * @param anotherTree 另一个数据模型树
     * @return 新的数据模型树
     */
    public DomainDataModelTreeDto addDomainModelTreeExtendData(DomainDataModelTreeDto currentTree,DomainDataModelTreeDto anotherTree) {

        List<DomainDataModelTreeDto> currentTreeChildren = currentTree.getChildren();
        List<DomainDataModelTreeDto> anotherTreeChildren = anotherTree.getChildren();

        // 用 Set 来跟踪已经存在的 currentTree的name
        Set<String> existingField2Values = new HashSet<>();
        for (DomainDataModelTreeDto child : currentTreeChildren) {
            existingField2Values.add(child.getName());
        }

        for (DomainDataModelTreeDto child : anotherTreeChildren) {
            // 如果 anotherTree 中的某个子对象的 isExtend 为 "1"，并且 name 不重复，则添加到 currentTree 的 children 中
            if (MagicStrings.ONE.equals(child.getIsExtend()) && !existingField2Values.contains(child.getName())) {
                // 添加到 children 中,添加之前去掉树子结构的非扩展结构
                currentTreeChildren.add(filterChildrenByExtend(child));
                existingField2Values.add(child.getName());
            }
        }

        currentTree.setChildren(currentTreeChildren);

        return currentTree;
    }

    /**
     * 组装模型树新的扩展数据
     * @param inputVo 入参
     * @param dataModel 数据模型
     * @param change 是否挂载扩展数据
     * @return 数据模型ID
     */
    public Long reconstructOutsideServerTree(VariableDataModelAddNewNextInputVo inputVo, VarProcessDataModel dataModel,Boolean change) {
        //获取新的树状结构
        DomainDataModelTreeDto newDataModelTreeVo1 = new DomainDataModelTreeDto();
        DataModelTreeVo dataModelTreeVo1 = null;
        if (VarProcessDataModelSourceType.OUTSIDE_SERVER.equals(inputVo.getSourceType())) {
            dataModelTreeVo1 = outsideServerPreview(inputVo);
        } else if (VarProcessDataModelSourceType.INSIDE_DATA.equals(inputVo.getSourceType())) {
            dataModelTreeVo1 = insideDataPreview(inputVo);
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_TYPE_NOT_SUPPORT, "数据模型类型错误！");
        }
        dataModelTreeVoConvertToDomainDataModelTreeDto(dataModelTreeVo1, newDataModelTreeVo1);

        //获取原先的树状结构
        Set<String> varPathMap = dataModelViewBiz.getUseVarList(1L);
        DomainDataModelTreeDto inputData = DomainModelTreeEntityUtils.transferDataModelTreeDto(dataModel.getContent(), varPathMap);

        //如果更改直接返回新的树状结构
        //如果不更改，传入三层还是两层，将原先树状结构的扩展属性挂载到新的树状结构上
        if (!change) {
            addDomainModelTreeExtendData(newDataModelTreeVo1,inputData);
        }

        //更新
        VariableDataModelUpdateInputVo variableDataModelUpdateInputVo = new VariableDataModelUpdateInputVo();
        variableDataModelUpdateInputVo.setDataModelId(inputVo.getDataModelId());
        variableDataModelUpdateInputVo.setFirstPageInfo(inputVo);
        variableDataModelUpdateInputVo.setSpaceId(1L);
        variableDataModelUpdateInputVo.setContent(newDataModelTreeVo1);
        Long dataModelId = dataModelSaveBiz.updateDataModel(variableDataModelUpdateInputVo);
        return dataModelId;
    }

    /**
     * 删除树状结构中非扩展数据
     * @param child 树状结构
     * @return 非扩展数据树
     */
    public DomainDataModelTreeDto filterChildrenByExtend(DomainDataModelTreeDto child) {
        //如果 child 的 isExtend 字段为“0”，则返回 null
        if (child.getIsExtend().equals(MagicStrings.ZERO)) {
            return null;
        }
        //判断是否有子节点
        if (child.getChildren() != null && !child.getChildren().isEmpty()) {
            //遍历子节点
            for (int i = 0; i < child.getChildren().size(); i++) {
                DomainDataModelTreeDto grandChild = child.getChildren().get(i);
                //如果子节点的 isExtend 字段为“0”，则移除该子节点
                if (MagicStrings.ZERO.equals(grandChild.getIsExtend())) {
                    child.getChildren().remove(grandChild);
                    // 由于移除了一个对象，需要将索引减一
                    i--;
                } else {
                    //递归调用filterChildrenByExtend方法，对子节点的子节点进行过滤
                    filterChildrenByExtend(grandChild);
                }
            }
            if (child.getChildren().size() == 0) {
                child.setChildren(null);
            }
        }
        return child;
    }
}
