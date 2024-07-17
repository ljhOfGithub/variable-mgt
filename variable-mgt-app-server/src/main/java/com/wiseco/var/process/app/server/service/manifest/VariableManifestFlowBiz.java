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
package com.wiseco.var.process.app.server.service.manifest;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DataModelUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.decision.jsonschema.util.enums.DomainModelSheetNameEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceAuthOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.ManifestModelMatchTreeInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowDataModelInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowDetailInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPrepInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowPropertiesInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableManifestFlowVarInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableCompileOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeGetBySourceTypeOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowDetailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowPrepOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableManifestFlowVarOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariablePropertiesOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableinternalDataOutputVo;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.JsonObjectNodeUtils;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.ManifestFlowNodeTypeEnum;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.template.TemplateUnitTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestSaveSub;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.CommonGlobalDataBiz;
import com.wiseco.var.process.app.server.service.VarProcessCategoryService;
import com.wiseco.var.process.app.server.service.VarProcessConfigTagService;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessInternalDataService;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.common.DeptService;
import com.wiseco.var.process.app.server.service.common.OutsideService;
import com.wiseco.var.process.app.server.service.dto.CompareTreeDto;
import com.wiseco.var.process.app.server.service.dto.Content;
import com.wiseco.var.process.app.server.service.dto.LinkMsgDto;
import com.wiseco.var.process.app.server.service.dto.OutsideServiceJsonParamDto;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableFunctionPrepDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import com.wiseco.var.process.app.server.service.dto.input.StaticTreeInputDto;
import com.wiseco.var.process.app.server.service.dto.input.TreeVarBaseArrayInputDto;
import com.wiseco.var.process.app.server.service.dto.output.VariableOutsideServiceOutputDto;
import com.wiseco.var.process.app.server.service.engine.VariableCompileBiz;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import com.wisecotech.json.SerializerFeature;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 变量清单 流程信息
 *
 * @author wangxianli
 * @since 2022/9/14
 */
@Slf4j
@Service
public class VariableManifestFlowBiz {

    static final String STRING_BODY = "body";
    static final String OUT_PARAMETER_BINDINGS = "out_parameter_bindings";
    private static final int FLOW_NODE_IDENTIFIER_LENGTH = 16;
    public static final String OBJECT = "object";
    private static SecureRandom randomNum = null;

    static {
        try {
            randomNum = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            randomNum = new SecureRandom();
        }
    }

    @Autowired
    private VarProcessManifestService varProcessManifestService;
    @Autowired
    private VarProcessManifestSaveSubService varProcessManifestSaveContentService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessFunctionService varProcessFunctionService;
    @Autowired
    private VarProcessCategoryService varProcessCategoryService;
    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;
    @Autowired
    private VarProcessManifestDataModelService varProcessManifestDataModelService;
    @Autowired
    private VarProcessInternalDataService varProcessInternalDataService;
    @Autowired
    private VariableCompileBiz variableCompileBiz;
    @Autowired
    private VariableManifestSupportBiz variableManifestSupport;
    @Resource
    private OutsideService outsideService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private VarProcessOutsideRefService varProcessOutsideRefService;
    @Autowired
    private CommonGlobalDataBiz commonGlobalDataBiz;
    @Autowired
    private VarProcessDataModelService varProcessDataModelService;
    @Autowired
    private VarProcessConfigTagService varProcessConfigTagService;

    /**
     * 获取流程的详情
     * @param inputDto 前端发送过来的实体
     * @return 流程信息Dto
     */
    public VariableManifestFlowDetailOutputDto flowDetail(VariableManifestFlowDetailInputDto inputDto) {
        VariableManifestFlowDetailOutputDto outputDto = new VariableManifestFlowDetailOutputDto();
        outputDto.setNodeType(ManifestFlowNodeTypeEnum.FLOW.getCode());
        if (inputDto.getContentId() == null) {
            VarProcessManifest varProcessManifest = varProcessManifestService.getById(inputDto.getManifestId());
            if (StringUtils.isEmpty(varProcessManifest.getContent())) {
                return outputDto;
            }

            outputDto.setContent(JSON.parseObject(varProcessManifest.getContent()));
        } else {
            VarProcessManifestSaveSub saveContent = varProcessManifestSaveContentService.getById(inputDto.getContentId());
            if (saveContent == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_INVALID_CONTENT,"未查询到临时记录");
            }
            if (StringUtils.isEmpty(saveContent.getContent())) {
                return outputDto;
            }
            outputDto.setContent(JSON.parseObject(saveContent.getContent()));
        }

        return outputDto;
    }

    /**
     * 获取流程的属性
     * @param inputDto 前端发送过来的实体
     * @return 流程属性Dto
     */
    public VariablePropertiesOutputDto flowProperties(VariableManifestFlowPropertiesInputDto inputDto) {
        List<TabDto> properties = new ArrayList<>();
        if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.FLOW.getCode())) {
            //主流程
            properties.add(getFlowSaveContent(inputDto.getManifestId()));
        } else {
            String content = null;
            if (inputDto.getContentId() == null) {
                VarProcessManifest varProcessManifest = varProcessManifestService.getById(inputDto.getManifestId());
                content = varProcessManifest.getContent();
            } else {
                VarProcessManifestSaveSub varProcessManifestSaveSub = varProcessManifestSaveContentService.getById(inputDto.getContentId());
                content = varProcessManifestSaveSub.getContent();
            }

            JSONObject jsonObject = contentResolver(content, inputDto.getNodeId());

            if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.VAR.getCode())) {
                //变量加工
                properties.add(getVarNodeContent(jsonObject, inputDto));
            } else if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.PRE_PROCESS.getCode())) {
                //预处理
                properties.add(getPrepNodeContent(jsonObject));
            } else if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.SERVICE.getCode())) {
                //外部服务
                properties.add(getServiceNodeContent(jsonObject, inputDto.getSpaceId()));
            } else if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.SPLIT.getCode())
                    || inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.PARALLEL.getCode())) {
                //分支
                properties.add(getBranchNodeContent(jsonObject, inputDto.getManifestId()));
            } else if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.INTERNAL_DATA.getCode())) {
                //内部数据
                properties.add(getInternalNodeContent(jsonObject, inputDto.getSpaceId()));
            } else if (inputDto.getNodeType().equals(ManifestFlowNodeTypeEnum.INTERNAL_LOGIC.getCode())) {
                //内部逻辑计算
                properties.add(getInternalLogicNodeContent(jsonObject, inputDto.getSpaceId()));
            }
        }
        return VariablePropertiesOutputDto.builder().properties(properties).build();

    }

    private TabDto getFlowSaveContent(Long manifestId) {

        List<PanelDto> list = new ArrayList<>();
        PanelDto<List<VarProcessManifestSaveSub>> selfSavePanel = new PanelDto();
        List<VarProcessManifestSaveSub> contentList = varProcessManifestSaveContentService.list(
                new QueryWrapper<VarProcessManifestSaveSub>().lambda()
                        .eq(VarProcessManifestSaveSub::getManifestId, manifestId)
                        .orderByDesc(VarProcessManifestSaveSub::getId)
        );
        selfSavePanel.setTitle("保存记录");
        selfSavePanel.setType(LocalDataTypeEnum.log.getCode());

        selfSavePanel.setDatas(contentList);
        list.add(selfSavePanel);

        return TabDto.builder().name("总体流程信息").content(list).build();
    }

    private JSONObject contentResolver(String content, String targetNodeId) {
        JSONObject selectNode = null;
        if (StringUtils.isEmpty(targetNodeId)) {
            return selectNode;
        }

        if (StringUtils.hasText(content)) {
            Pair<Boolean, JsonElement> pair = JsonObjectNodeUtils.nodeFilter(content, targetNodeId);
            if (pair.getKey() && null != pair.getValue()) {
                selectNode = JSONObject.parseObject(pair.getValue().toString());
            }
        }

        return selectNode;
    }

    private TabDto getVarNodeContent(JSONObject targetNode, VariableManifestFlowPropertiesInputDto inputDto) {
        List<PanelDto> list = new ArrayList<>();

        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }

        String label = targetNode.getString("label");
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";
        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> baseContents = new ArrayList<>();
        baseContents.add(Content.builder().label("节点类型").value(ManifestFlowNodeTypeEnum.VAR.getDescription()).build());
        baseContents.add(Content.builder().label("节点名称").value(label).build());
        baseContents.add(Content.builder().label("节点描述").value(annotation).build());
        basePanel.setDatas(baseContents);
        list.add(basePanel);

        PanelDto<TableContent> paramInPanel = new PanelDto();
        paramInPanel.setTitle("加工变量列表");
        paramInPanel.setType(LocalDataTypeEnum.table.getCode());
        TableContent tableContent = new TableContent();
        List<TableContent.TableHeadInfo> tableHeadInfoList = Lists.newArrayList(
                TableContent.TableHeadInfo.builder().lable("变量名").key("name").build(), TableContent.TableHeadInfo.builder().lable("变量中文名").key("label")
                        .build()
        );
        tableContent.setTableHead(tableHeadInfoList);
        List<JSONObject> jsonObjectList = Lists.newArrayList();
        JSONArray input = targetNode.getJSONArray("list");
        if (CollUtil.isNotEmpty(input)) {
            for (int i = 0; i < input.size(); i++) {
                JSONObject jsonObject = input.getJSONObject(i);
                jsonObjectList.add(jsonObject);
            }
        }

        tableContent.setTableData(jsonObjectList);
        paramInPanel.setDatas(tableContent);
        list.add(paramInPanel);

        return TabDto.builder().name("节点信息").content(list).build();

    }

    private TabDto getPrepNodeContent(JSONObject targetNode) {
        List<PanelDto> list = new ArrayList<>();

        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }

        String label = targetNode.getString("label");
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";
        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> baseContents = new ArrayList<>();
        baseContents.add(Content.builder().label("节点类型").value(ManifestFlowNodeTypeEnum.PRE_PROCESS.getDescription()).build());
        baseContents.add(Content.builder().label("节点名称").value(label).build());
        baseContents.add(Content.builder().label("节点描述").value(annotation).build());
        basePanel.setDatas(baseContents);
        list.add(basePanel);

        PanelDto<TableContent> paramInPanel = new PanelDto();
        paramInPanel.setTitle("执行数据预处理逻辑列表");
        paramInPanel.setType(LocalDataTypeEnum.table.getCode());
        TableContent tableContent = new TableContent();
        List<TableContent.TableHeadInfo> tableHeadInfoList = Lists.newArrayList(TableContent.TableHeadInfo.builder().lable("逻辑名称").key("name")
                        .build(), TableContent.TableHeadInfo.builder().lable("处理对象").key("objectName").build()
                // TableContent.TableHeadInfo.builder().lable("版本号").key("version").build()
        );
        tableContent.setTableHead(tableHeadInfoList);
        List<JSONObject> jsonObjectList = Lists.newArrayList();

        JSONArray input = targetNode.getJSONArray("list");
        if (CollUtil.isNotEmpty(input)) {
            for (int i = 0; i < input.size(); i++) {
                JSONObject jsonObject = input.getJSONObject(i);
                //TODO 公共函数版本待实现
                jsonObjectList.add(jsonObject);
            }
        }

        tableContent.setTableData(jsonObjectList);
        paramInPanel.setDatas(tableContent);
        list.add(paramInPanel);

        return TabDto.builder().name("节点信息").content(list).build();

    }

    private TabDto getServiceNodeContent(JSONObject targetNode, Long spaceId) {
        List<PanelDto> list = new ArrayList<>();

        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }

        String label = targetNode.getString("label");
        String serviceName = targetNode.getString("service_name");
        String serviceCode = targetNode.getString("service_code");
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";
        String dataModelObject = StringUtils.hasText(targetNode.getString("objectName")) ? targetNode.getString("objectName") : "无";

        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        if (!StringUtils.isEmpty(serviceCode)) {
            List<Content> baseContents = new ArrayList<>();
            baseContents.add(Content.builder().label("节点类型").value(ManifestFlowNodeTypeEnum.SERVICE.getDescription()).build());
            baseContents.add(Content.builder().label("节点名称").value(label).build());
            baseContents.add(Content.builder().label("外部服务名称").value(serviceName).build());
            baseContents.add(Content.builder().label("数据模型对象").value(dataModelObject).build());
            baseContents.add(Content.builder().label("节点描述").value(annotation).build());
            basePanel.setDatas(baseContents);
        }

        list.add(basePanel);
        PanelDto<TableContent> paramInPanel = new PanelDto();
        paramInPanel.setTitle("入参映射");
        paramInPanel.setType(LocalDataTypeEnum.table.getCode());
        TableContent tableContent = new TableContent();
        List<TableContent.TableHeadInfo> tableHeadInfoList = Lists.newArrayList(
                TableContent.TableHeadInfo.builder().lable("入参").key("name").build(),
                TableContent.TableHeadInfo.builder().lable("赋值").key("mapping").build()
        );
        tableContent.setTableHead(tableHeadInfoList);
        List<JSONObject> jsonObjectList = Lists.newArrayList();
        tableContent.setTableData(jsonObjectList);
        JSONArray input = targetNode.getJSONArray("input_parameter_bindings");
        if (CollUtil.isNotEmpty(input)) {
            input.forEach(x -> jsonObjectList.add(JSON.parseObject(JSON.toJSONString(x, SerializerFeature.WriteMapNullValue))));
        }
        paramInPanel.setDatas(tableContent);
        list.add(paramInPanel);
        PanelDto<TableContent> paramOutPanel = new PanelDto();
        paramOutPanel.setTitle("出参映射");
        paramOutPanel.setType(LocalDataTypeEnum.table.getCode());
        if (targetNode.containsKey(OUT_PARAMETER_BINDINGS) && targetNode.get(OUT_PARAMETER_BINDINGS) instanceof JSONObject) {
            JSONObject out = targetNode.getJSONObject(OUT_PARAMETER_BINDINGS);

            TableContent tableContentOut = new TableContent();
            List<TableContent.TableHeadInfo> tableHeadInfoOutList = Lists.newArrayList(
                    TableContent.TableHeadInfo.builder().lable("出参").key("name").build(),
                    TableContent.TableHeadInfo.builder().lable("数据映射").key("mapping").build()
            );
            tableContentOut.setTableHead(tableHeadInfoOutList);
            List<JSONObject> jsonObjectOutList = Lists.newArrayList();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", out.getString("name"));
            jsonObject.put("mapping", out.getString("mapping"));
            jsonObjectOutList.add(jsonObject);
            tableContentOut.setTableData(jsonObjectOutList);
            paramOutPanel.setDatas(tableContentOut);
            list.add(paramOutPanel);
        }
        return TabDto.builder().name("节点信息").content(list).build();
    }

    private TabDto getInternalNodeContent(JSONObject targetNode, Long spaceId) {
        List<PanelDto> list = new ArrayList<>();
        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }
        String label = targetNode.getString("label");
        String identifier = targetNode.getString("identifier");
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";
        String dataModelObject = StringUtils.hasText(targetNode.getString("objectName")) ? targetNode.getString("objectName") : "无";
        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        if (!StringUtils.isEmpty(identifier)) {
            List<Content> baseContents = new ArrayList<>();
            baseContents.add(Content.builder().label("节点类型").value(ManifestFlowNodeTypeEnum.INTERNAL_DATA.getDescription()).build());
            baseContents.add(Content.builder().label("节点名称").value(label).build());
            baseContents.add(Content.builder().label("数据模型对象").value(dataModelObject).build());
            baseContents.add(Content.builder().label("节点描述").value(annotation).build());
            basePanel.setDatas(baseContents);
        }
        list.add(basePanel);

        PanelDto<TableContent> paramInPanel = new PanelDto();
        paramInPanel.setTitle("入参映射");
        paramInPanel.setType(LocalDataTypeEnum.table.getCode());
        TableContent tableContent = new TableContent();
        List<TableContent.TableHeadInfo> tableHeadInfoList = Lists.newArrayList(
                TableContent.TableHeadInfo.builder().lable("入参").key("name").build(),
                TableContent.TableHeadInfo.builder().lable("赋值").key("mapping").build()
        );
        tableContent.setTableHead(tableHeadInfoList);
        List<JSONObject> jsonObjectList = Lists.newArrayList();
        tableContent.setTableData(jsonObjectList);

        JSONArray input = targetNode.getJSONArray("input_parameter_bindings");
        if (CollUtil.isNotEmpty(input)) {
            input.forEach(x -> jsonObjectList.add(JSON.parseObject(JSON.toJSONString(x, SerializerFeature.WriteMapNullValue))));
        }
        paramInPanel.setDatas(tableContent);
        list.add(paramInPanel);
        return TabDto.builder().name("节点信息").content(list).build();
    }

    private TabDto getInternalLogicNodeContent(JSONObject targetNode, Long manifestId) {
        List<PanelDto> list = new ArrayList<>();

        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }

        // 分支属性信息
        String label = targetNode.getString("label");
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";

        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> baseContents = new ArrayList<>();
        baseContents.add(Content.builder().label("节点类型").value(ManifestFlowNodeTypeEnum.INTERNAL_LOGIC.getDescription()).build());
        baseContents.add(Content.builder().label("节点名称").value(label).build());
        baseContents.add(Content.builder().label("节点备注").value(annotation).build());
        basePanel.setDatas(baseContents);
        list.add(basePanel);

        PanelDto<List<Content>> logicPanel = new PanelDto();
        logicPanel.setTitle("计算逻辑");
        logicPanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> logicContents = new ArrayList<>();
        if (targetNode.containsKey(STRING_BODY) && targetNode.getJSONObject(STRING_BODY) != null && targetNode.getJSONObject(STRING_BODY).size() > 0) {
            logicContents.add(Content.builder().label("已设置").value(JSONObject.toJSONString(targetNode.getJSONObject(STRING_BODY))).build());
        } else {
            logicContents.add(Content.builder().label("未设置").value("").build());
        }

        logicPanel.setDatas(logicContents);
        list.add(logicPanel);

        return TabDto.builder().name("节点信息").content(list).build();

    }

    private TabDto getBranchNodeContent(JSONObject targetNode, Long manifestId) {
        List<PanelDto> list = new ArrayList<>();

        if (null == targetNode) {
            return TabDto.builder().name("节点信息").content(list).build();
        }

        // 分支属性信息
        String label = targetNode.getString("label");
        String branType = "split".equals(targetNode.getString("type")) ? "分支" : "并行分支";
        String annotation = StringUtils.hasText(targetNode.getString("annotation")) ? targetNode.getString("annotation") : "无";

        PanelDto<List<Content>> basePanel = new PanelDto();
        basePanel.setTitle("基本信息");
        basePanel.setType(LocalDataTypeEnum.desc.getCode());
        List<Content> baseContents = new ArrayList<>();
        baseContents.add(Content.builder().label("分支类型").value(branType).build());
        baseContents.add(Content.builder().label("节点名称").value(label).build());
        baseContents.add(Content.builder().label("节点备注").value(annotation).build());
        basePanel.setDatas(baseContents);
        list.add(basePanel);

        PanelDto<List<Content>> branchePanel = new PanelDto();
        branchePanel.setTitle("分支信息");
        branchePanel.setType(LocalDataTypeEnum.desc.getCode());
        JSONArray branches = targetNode.getJSONArray("branches");

        List<Content> varList = Lists.newArrayList();
        if (Objects.nonNull(branches)) {
            for (int i = 0; i < branches.size(); i++) {
                Content content = new Content();
                content.setLabel("分支" + (i + 1));
                content.setValue(branches.getJSONObject(i).getString("label"));
                LinkMsgDto linkMsgDto = new LinkMsgDto(manifestId, null, null, null, "branch");
                linkMsgDto.setBranchContent(branches.getJSONObject(i).toJSONString());
                content.setUrl(JSON.toJSONString(linkMsgDto));
                varList.add(content);
            }
        }

        branchePanel.setDatas(varList);
        list.add(branchePanel);

        return TabDto.builder().name("节点信息").content(list).build();

    }

    /**
     * 流程覆盖
     * @param inputDto 前端发送过来的实体
     */
    public void flowRecovery(VariableManifestFlowDetailInputDto inputDto) {

        VarProcessManifestSaveSub varProcessManifestSaveSub = varProcessManifestSaveContentService.getById(inputDto.getContentId());

        varProcessManifestService.update(
                new UpdateWrapper<VarProcessManifest>().lambda()
                        .set(VarProcessManifest::getContent, varProcessManifestSaveSub.getContent())
                        .eq(VarProcessManifest::getId, inputDto.getManifestId())
        );

        varProcessManifestSaveContentService.save(
                VarProcessManifestSaveSub.builder()
                        .manifestId(inputDto.getManifestId())
                        .content(varProcessManifestSaveSub.getContent())
                        .createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername())
                        .build()
        );
    }

    /**
     * 保存流程
     * @param inputDto 前端发送的实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveFlow(VariableManifestFlowSaveInputDto inputDto) {
        if (inputDto.getContent() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_INVALID_CONTENT,"流程信息不能为空");
        }
        String content = JSONObject.toJSONString(inputDto.getContent());
        varProcessManifestService.update(
                new UpdateWrapper<VarProcessManifest>().lambda()
                        .set(VarProcessManifest::getContent, content)
                        .eq(VarProcessManifest::getId, inputDto.getManifestId())
        );

        //保存到临时内容表
        varProcessManifestSaveContentService.save(
                VarProcessManifestSaveSub.builder()
                        .manifestId(inputDto.getManifestId())
                        .content(content)
                        .createdUser(SessionContext.getSessionUser().getUsername())
                        .updatedUser(SessionContext.getSessionUser().getUsername())
                        .build()
        );
    }

    /**
     * 检查流程
     * @param inputDto 前端发送过来的实体
     * @return 变量编译验证返回DTO
     */
    public VariableCompileOutputDto checkFlow(VariableManifestFlowSaveInputDto inputDto) {
        VarProcessManifest variable = varProcessManifestService.getById(inputDto.getManifestId());
        String content = null;
        if (inputDto.getContent() == null || inputDto.getContent().size() == 0) {
            if (StringUtils.isEmpty(variable.getContent())) {
                VariableCompileOutputDto outputDto = new VariableCompileOutputDto();
                outputDto.setState(false);
                List<String> errorMessageList = new ArrayList<>();
                errorMessageList.add("流程信息不存在");
                outputDto.setErrorMessageList(errorMessageList);
                return outputDto;
            }
            content = variable.getContent();

        } else {

            content = JSONObject.toJSONString(inputDto.getContent());
        }

        return variableCompileBiz.validate(TestVariableTypeEnum.MANIFEST, inputDto.getSpaceId(), inputDto.getManifestId(), content);

    }

    /**
     * 获取数据模型对象的List
     * @param inputDto 前端发送过来的实体
     * @return 数据模型对象的List
     */
    public List<String> dataModelObjectList(VariableManifestFlowDataModelInputDto inputDto) {
        return getMappingObjectList(inputDto.getSpaceId(), inputDto.getManifestId(), inputDto.getSourceType());
    }

    /**
     * 预处理对象查询
     * @param inputDto 前端发送过来的实体
     * @return 预处理逻辑输出Dto
     */
    public List<VariableManifestFlowPrepOutputDto> prepObjectList(VariableManifestFlowPrepInputDto inputDto) {
        List<VariableManifestFlowPrepOutputDto> outputDtoList = new ArrayList<>();
        VariableManifestDto manifestDtoDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
        List<String> mappingObjectList = manifestDtoDto.getDataModelMappingList().stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(mappingObjectList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_STATUS_NO_MATCH,"未查询到变量清单数据模型绑定信息");
        }
        if (StringUtils.isEmpty(inputDto.getObjectName())) {
            inputDto.setObjectName(null);
        }
        if (StringUtils.isEmpty(inputDto.getPrepName())) {
            inputDto.setPrepName(null);
        }

        String order = inputDto.getOrder();
        String sortedKey = "name";
        String sortMethod = "asc";
        if (!StringUtils.isEmpty(order)) {
            sortedKey = order.substring(0, order.indexOf('_'));
            sortMethod = order.substring(order.indexOf('_') + 1);
        }

        List<VariableFunctionPrepDto> prepList = varProcessFunctionService.getPrepList(inputDto, mappingObjectList, sortedKey, sortMethod);
        if (!CollectionUtils.isEmpty(prepList)) {
            for (VariableFunctionPrepDto dto : prepList) {
                outputDtoList.add(VariableManifestFlowPrepOutputDto.builder().identifier(dto.getIdentifier()).name(dto.getName())
                        .objectName(dto.getPrepObjectName())
                        .version(1)
                        .dept(dto.getDept()).build());
            }
        }
        return outputDtoList;
    }

    /**
     * 获取模型对象的List
     * @param spaceId 变量空间的Id
     * @param manifestId 变量清单的Id
     * @param sourceType 来源类型
     * @return 模型对象的List
     */
    private List<String> getMappingObjectList(Long spaceId, Long manifestId, Integer sourceType) {
        List<String> outputList = new ArrayList<>();

        VariableManifestDto manifestDtoDto = variableManifestSupport.getVariableManifestDto(manifestId);
        List<VarProcessManifestDataModel> dataModelMappingList = manifestDtoDto.getDataModelMappingList();
        if (!CollectionUtils.isEmpty(dataModelMappingList)) {
            for (VarProcessManifestDataModel mapping : dataModelMappingList) {
                outputList.add(mapping.getObjectName());
            }
        }
        return outputList;
    }

    /**
     * 获取变量的列表
     * @param inputDto 前端发送过来的输入实体
     * @return 变量列表
     */
    public List<VariableManifestFlowVarOutputDto> variableList(VariableManifestFlowVarInputDto inputDto) {

        List<VariableManifestFlowVarOutputDto> outputDtoList = new ArrayList<>();
        VariableFlowQueryDto variableFlowQueryDto = new VariableFlowQueryDto();
        BeanUtils.copyProperties(inputDto, variableFlowQueryDto);
        if (CollectionUtils.isEmpty(inputDto.getExcludeList())) {
            variableFlowQueryDto.setExcludeList(null);
        }
        //设置排序字段
        String sort = inputDto.getOrder();
        if (!StringUtils.isEmpty(sort)) {
            variableFlowQueryDto.setSortMethod(sort.substring(sort.indexOf("_") + 1));
            String sortedkey = sort.substring(0, sort.indexOf("_"));
            variableFlowQueryDto.setSortKey(sortedkey);
        } else {
            variableFlowQueryDto.setSortKey("label");
            variableFlowQueryDto.setSortMethod("asc");
        }
        if (StringUtils.isEmpty(inputDto.getKeywords())) {
            variableFlowQueryDto.setKeywords(null);
        }
        if (!StringUtils.isEmpty(inputDto.getDeptId())) {
            variableFlowQueryDto.setDeptCode(inputDto.getDeptId());
        }

        //设置标签查询字段
        VarProcessConfigTag tag = varProcessConfigTagService.getById(inputDto.getTagId());
        if (tag != null && tag.getName() != null) {
            variableFlowQueryDto.setTagNames(Collections.singletonList(tag.getName()));
        }
        if (inputDto.getGroupId() != null) {
            List<String> tagNames = varProcessConfigTagService.list(Wrappers.<VarProcessConfigTag>lambdaQuery()
                            .select(VarProcessConfigTag::getName)
                            .eq(VarProcessConfigTag::getGroupId, inputDto.getGroupId())).stream().map(VarProcessConfigTag::getName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tagNames)) {
                return outputDtoList;
            }
            variableFlowQueryDto.setTagNames(tagNames);
        }
        if (CollectionUtils.isEmpty(variableFlowQueryDto.getTagNames())) {
            variableFlowQueryDto.setTagNames(null);
        }
        List<VarProcessCategory> categoryList = varProcessCategoryService.getCategoryListByType(CategoryTypeEnum.VARIABLE);
        Map<Long, VarProcessCategory> categoryMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, cat -> cat, (k1, k2) -> k2));
        // 获取变量类别名称　Map, key: categoryId, value: category name
        Map<Long, String> categoryNameMap = categoryList.stream().collect(Collectors.toMap(VarProcessCategory::getId, VarProcessCategory::getName, (k1, k2) -> k2));

        variableFlowQueryDto.setCategoryIds(categoryList.stream().filter(cat -> varProcessCategoryService.containsSubCat(inputDto.getCategoryId(),cat,categoryMap)).map(VarProcessCategory::getId).collect(Collectors.toList()));
        List<VarProcessVariable> variableFlow = varProcessManifestVariableService.getVariableListInFlow(variableFlowQueryDto);
        if (!CollectionUtils.isEmpty(variableFlow)) {
            Map<String, String> deptMap = deptService.findDeptMapByDeptCodes(variableFlow.stream().map(VarProcessVariable::getDeptCode).collect(Collectors.toList()));
            for (VarProcessVariable variable : variableFlow) {
                outputDtoList.add(
                        VariableManifestFlowVarOutputDto.builder()
                                .name(variable.getName())
                                .label(variable.getLabel())
                                .identifier(variable.getIdentifier())
                                .version(variable.getVersion())
                                .dataType(variable.getDataType())
                                .category(categoryNameMap.get(variable.getCategoryId()))
                                .createdDept(deptMap.get(variable.getDeptCode()) == null ? "" : deptMap.get(variable.getDeptCode()))
                                .build()
                );
            }
        }
        return outputDtoList;
    }

    /**
     * 寻找内部数据的List
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return 内部数据模型Dto的List
     */
    public List<VariableinternalDataOutputVo> findInternalDataList(Long spaceId, Long manifestId) {
        //查找清单使用的内部数据模型
        List<String> modelNames = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                        .select(VarProcessManifestDataModel::getObjectName)
                        .eq(VarProcessManifestDataModel::getVarProcessSpaceId, spaceId)
                        .eq(VarProcessManifestDataModel::getManifestId, manifestId)
                        .eq(VarProcessManifestDataModel::getSourceType, VarProcessDataModelSourceType.INSIDE_DATA.getCode()))
                .stream()
                .map(VarProcessManifestDataModel::getObjectName)
                .collect(Collectors.toList());
        List<VariableinternalDataOutputVo> outputVos = new ArrayList<>();
        if (CollectionUtils.isEmpty(modelNames)) {
            return outputVos;
        }
        List<VarProcessDataModel> dataModelList = varProcessDataModelService.findMaxVersionModelsByNames(modelNames);
        //拿到map<object_name,internalData>
        Map<String, VarProcessInternalData> nameIdentifierMap = varProcessInternalDataService.list(Wrappers.<VarProcessInternalData>lambdaQuery()
                        .select(VarProcessInternalData::getId, VarProcessInternalData::getObjectName, VarProcessInternalData::getName, VarProcessInternalData::getIdentifier)
                        .eq(VarProcessInternalData::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()))
                .stream().collect(Collectors.toMap(VarProcessInternalData::getObjectName, item -> item, (k1, k2) -> k2));
        for (VarProcessDataModel datamodel : dataModelList) {
            VariableinternalDataOutputVo outputVo = new VariableinternalDataOutputVo();
            outputVo.setDataModelId(datamodel.getId());
            outputVo.setObjectName(datamodel.getObjectName());
            outputVo.setObjectLabel(datamodel.getObjectLabel());
            outputVo.setVersion(datamodel.getVersion());
            outputVo.setInternalDataName(nameIdentifierMap.get(datamodel.getObjectName()).getName());
            outputVo.setIdentifier(nameIdentifierMap.get(datamodel.getObjectName()).getIdentifier());
            DomainDataModelTreeDto treeDto = DataModelUtils.getDomainDataModelTreeOutputDto(datamodel.getContent());
            VariableDataModeGetBySourceTypeOutputVO.ParameterBinding binding = new VariableDataModeGetBySourceTypeOutputVO.ParameterBinding();
            binding.setCnName(datamodel.getObjectLabel());
            binding.setIsArr(0);
            binding.setMapping(treeDto.getName());
            binding.setName(treeDto.getName());
            binding.setType("object");
            binding.setUseRootObjectFlag(Integer.parseInt(treeDto.getIsRefRootNode()));
            outputVo.setParameterBinding(binding);
            outputVos.add(outputVo);
        }
        return outputVos;
    }


    /**
     * 生成结点的唯一标识符
     * @return 结点的唯一标识符
     */
    public String generateNodeIdentifier() {
        // Obtain random value from random double decimal part (11 digits) and epoch timestamp
        String randomDoubleDecimalString = String.valueOf(randomNum.nextDouble()).substring(MagicNumbers.THREE, MagicNumbers.FOURTEEN);
        String epochMillisecondString = String.valueOf((new Date()).getTime());
        BigInteger randomValue = new BigInteger(randomDoubleDecimalString + epochMillisecondString);
        // Convert the radix of random value from 10 to 36
        String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
        BigInteger radix = BigInteger.valueOf(digits.length());
        StringBuilder randomLiteralBuilder = new StringBuilder();
        while (randomValue.compareTo(radix.subtract(BigInteger.valueOf(1))) >= 0) {
            int remainder = randomValue.mod(radix).intValue();
            randomLiteralBuilder.append(digits.charAt(remainder));
            randomValue = randomValue.divide(radix);
        }
        randomLiteralBuilder.reverse();
        // Padding 0 on left, extend the length of random literal to 16
        if (randomLiteralBuilder.length() < FLOW_NODE_IDENTIFIER_LENGTH) {
            StringBuilder zeroPrefix = new StringBuilder();
            for (int i = 0; i < FLOW_NODE_IDENTIFIER_LENGTH - randomLiteralBuilder.length(); i++) {
                zeroPrefix.append(NumberUtils.INTEGER_ZERO);
            }
            randomLiteralBuilder.insert(0, zeroPrefix);
        }
        return randomLiteralBuilder.toString();
    }

    /**
     * 寻找变量引用外部服务列表
     * @param spaceId 变量空间Id
     * @param manifestId 变量清单Id
     * @return 变量引用外部服务输出Dto的List
     */
    public List<VariableOutsideServiceOutputDto> findVariableOutsideServiceList(Long spaceId, Long manifestId) {

        List<VariableOutsideServiceOutputDto> outputDtos = new ArrayList<>();

        //获取外部服务列表
        List<String> modelNames = varProcessManifestDataModelService.list(Wrappers.<VarProcessManifestDataModel>lambdaQuery()
                        .select(VarProcessManifestDataModel::getObjectName)
                        .eq(VarProcessManifestDataModel::getManifestId, manifestId)
                        .eq(VarProcessManifestDataModel::getSourceType, VarProcessDataModelSourceType.OUTSIDE_SERVER.getCode()))
                .stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(modelNames)) {
            return outputDtos;
        }
        List<Long> outsideServiceIds = varProcessOutsideRefService.list(Wrappers.<VarProcessOutsideRef>lambdaQuery()
                .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getOutsideServiceId)
                .in(VarProcessOutsideRef::getName, modelNames)).stream().map(VarProcessOutsideRef::getOutsideServiceId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(outsideServiceIds)) {
            return outputDtos;
        }
        List<VarProcessOutsideRef> refList = varProcessOutsideRefService.list(Wrappers.<VarProcessOutsideRef>lambdaQuery()
                .select(VarProcessOutsideRef::getId, VarProcessOutsideRef::getOutsideServiceId, VarProcessOutsideRef::getName, VarProcessOutsideRef::getNameCn, VarProcessOutsideRef::getIsUseRootObject));
        if (CollectionUtils.isEmpty(refList)) {
            return outputDtos;
        }
        Map<Long, VarProcessOutsideRef> refMap = refList.stream().collect(Collectors.toMap(VarProcessOutsideRef::getOutsideServiceId, item -> item, (k1, k2) -> k2));

        for (Long serviceId : outsideServiceIds) {

            //获取外部服务详细信息
            OutsideServiceDetailRestOutputDto detail = outsideService.getOutsideServiceDetailRestById(serviceId);

            if (detail == null || detail.getCode() == null || detail.getName() == null) {
                continue;
            }

            //设置入参
            List<VariableOutsideServiceOutputDto.OutsideServiceVarDto> inputParams = new ArrayList<>();

            String requestParam = detail.getReq().getRequestParam();
            List<OutsideServiceJsonParamDto> outsideServiceJsonParamDto = JSONObject.parseArray(requestParam, OutsideServiceJsonParamDto.class);
            if (!CollectionUtils.isEmpty(outsideServiceJsonParamDto)) {
                for (OutsideServiceJsonParamDto param : outsideServiceJsonParamDto) {
                    VariableOutsideServiceOutputDto.OutsideServiceVarDto inputParam = new VariableOutsideServiceOutputDto.OutsideServiceVarDto();
                    inputParam.setOutsideServiceId(detail.getReq().getOutsideServiceId());
                    inputParam.setVarName(param.getParamName());
                    inputParam.setDescription(param.getParamDesc());
                    inputParam.setFieldType(param.getParamType());
                    inputParam.setIsArr("0");
                    inputParams.add(inputParam);
                }
            }

            //设置出参
            VarProcessOutsideRef refObject = refMap.get(serviceId);
            VariableOutsideServiceOutputDto.OutsideServiceRefObjectDto refObjectDto = new VariableOutsideServiceOutputDto.OutsideServiceRefObjectDto();
            if (null != refObject) {
                refObjectDto = VariableOutsideServiceOutputDto.OutsideServiceRefObjectDto.builder()
                        .refObjectName(refObject.getName())
                        .refObjectNameCn(refObject.getNameCn())
                        .useRootObjectFlag(refObject.getIsUseRootObject())
                        .build();
            }

            VariableOutsideServiceOutputDto outPutDto = VariableOutsideServiceOutputDto.builder().outsideServiceId(detail.getId())
                    .outsideServiceName(detail.getName()).outsideServiceCode(detail.getCode())
                    .outsideServiceVarList(inputParams).outsideServiceRefObjectDto(refObjectDto).build();
            outputDtos.add(outPutDto);
        }

        return outputDtos;
    }

    /**
     * 寻找变量匹配树
     * @param inputDto 前端发送过来的实体
     * @return 变量匹配树
     */
    public List<DomainDataModelTreeDto> findDataVariable(ManifestModelMatchTreeInputDto inputDto) {
        List<DataVariableTypeEnum> dataVarTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inputDto.getVarTypeList())) {
            inputDto.getVarTypeList().stream().forEach(e -> dataVarTypeList.add(DataVariableTypeEnum.getMessageEnum(e)));
        } else {
            dataVarTypeList.add(DataVariableTypeEnum.getMessageEnum(inputDto.getVarType()));
        }
        //变量清单使用的数据模型&变量为0，直接返回
        List<DomainDataModelTreeDto> treeDtoList = new ArrayList<>();
        VariableManifestDto manifestDto = variableManifestSupport.getVariableManifestDto(inputDto.getManifestId());
        List<VarProcessManifestDataModel> dataModelMappingList = manifestDto.getDataModelMappingList();
        List<VarProcessManifestVariable> variableMappingList = manifestDto.getVariablePublishList();
        if (CollectionUtils.isEmpty(variableMappingList) && CollectionUtils.isEmpty(dataModelMappingList)) {
            return treeDtoList;
        }
        Boolean isArray = inputDto.getIsArrAy();
        Long spaceId = inputDto.getSpaceId();
        List<String> positionList = new ArrayList<>();
        positionList.add(DomainModelSheetNameEnum.RAW_DATA.getMessage());
        List<String> typeList = new ArrayList<>();
        for (DataVariableTypeEnum typeEnum : dataVarTypeList) {
            typeList.add(typeEnum.getMessage());
        }
        if (null != isArray && isArray) {
            treeDtoList = commonGlobalDataBiz.findTreeVarBaseArray(TreeVarBaseArrayInputDto.builder()
                    .type(TemplateUnitTypeEnum.SPACE_VARIABLE.getType()).spaceId(spaceId).positionList(positionList).typeList(typeList).build());
        } else {
            treeDtoList = commonGlobalDataBiz.findStaticTree(StaticTreeInputDto.builder().type(TemplateUnitTypeEnum.SPACE_VARIABLE.getType())
                    .spaceId(spaceId).positionList(positionList).typeList(typeList).build());
        }
        DomainDataModelTreeDto rawData = treeDtoList.get(0);
        List<DomainDataModelTreeDto> children = rawData.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return treeDtoList;
        }

        //列表非空时，添加数据前先排序
        for (DomainDataModelTreeDto domainDataModelTreeDto : children) {
            if (domainDataModelTreeDto != null) {
                List<DomainDataModelTreeDto> childrenList = domainDataModelTreeDto.getChildren();
                if (childrenList != null) {
                    Collections.sort(domainDataModelTreeDto.getChildren(), Comparator.comparing(o -> o.getName().toUpperCase()));
                }
            }
        }

        //筛选出可选(指标清单使用)的数据模型
        Set<String> objectNameSet = dataModelMappingList.stream().map(VarProcessManifestDataModel::getObjectName).collect(Collectors.toSet());
        List<DomainDataModelTreeDto> newChildren = new ArrayList<>();

        for (DomainDataModelTreeDto treeDto : children) {
            String path = treeDto.getValue();
            // 使用 split 方法将字符串按照 "." 分割成数组
            String[] parts = path.split("\\.");
            if ("rawData".equals(parts[0]) && objectNameSet.contains(parts[1])) {
                // 不可选自身
                if (StringUtils.isEmpty(inputDto.getObjectName()) || !parts[1].equals(inputDto.getObjectName())) {
                    newChildren.add(treeDto);
                }
            }
        }
        treeDtoList.get(0).setChildren(newChildren);
        assembleVarsTreeDtos(treeDtoList, variableMappingList, typeList);
        return treeDtoList;
    }

    private List<DomainDataModelTreeDto> pruneTreeBasicType(List<DomainDataModelTreeDto> treeDtos, CompareTreeDto paramTree) {
        List<DomainDataModelTreeDto> prunedChildren = new ArrayList<>();

        if (CollectionUtils.isEmpty(treeDtos)) {
            return prunedChildren;
        }
        for (DomainDataModelTreeDto object : treeDtos) {
            if ("object".equals(object.getType())) {
                // 递归处理子节点
                List<DomainDataModelTreeDto> prunedSubtree = pruneTreeBasicType(object.getChildren(), paramTree);
                if (!CollectionUtils.isEmpty(prunedSubtree)) {
                    object.setChildren(prunedSubtree);
                    object.setValue("rawData." + object.getValue());
                    prunedChildren.add(object);
                }
            } else if (paramTree.getType().equals(object.getType()) && paramTree.getIsArr().equals("1".equals(object.getIsArr()))) {
                object.setValue("rawData." + object.getValue());
                prunedChildren.add(object);
            }
        }
        return prunedChildren;
    }

    private List<DomainDataModelTreeDto> pruneTreeObject(List<DomainDataModelTreeDto> treeDtos, CompareTreeDto paramTree) {
        List<DomainDataModelTreeDto> prunedChildren = new ArrayList<>();

        if (CollectionUtils.isEmpty(treeDtos)) {
            return prunedChildren;
        }
        for (DomainDataModelTreeDto object : treeDtos) {
            if (!"object".equals(object.getType())) {
                continue;
            }
            CompareTreeDto modelTree = buildCompareTreeDto(object);

            // 递归处理子节点
            List<DomainDataModelTreeDto> prunedSubtree = pruneTreeObject(object.getChildren(), paramTree);
            object.setChildren(prunedSubtree);

            if (!CollectionUtils.isEmpty(prunedSubtree) || compareTree(modelTree, paramTree)) {
                object.setValue("rawData." + object.getValue());
                prunedChildren.add(object);
            }
        }
        return prunedChildren;
    }

    private CompareTreeDto buildCompareTreeDto(DomainDataModelTreeDto dmTreeDto) {
        CompareTreeDto dto = new CompareTreeDto();
        dto.setType(dmTreeDto.getType());
        dto.setIsArr("1".equals(dmTreeDto.getIsArr()));
        dto.setName(dmTreeDto.getName());
        dto.setChildren(new ArrayList<>());
        if (!CollectionUtils.isEmpty(dmTreeDto.getChildren())) {
            for (DomainDataModelTreeDto subTree : dmTreeDto.getChildren()) {
                CompareTreeDto childDto = buildCompareTreeDto(subTree);
                dto.getChildren().add(childDto);
            }
        }
        return dto;
    }

    private CompareTreeDto buildCompareTreeDto(JsonNode jsonNode) {
        CompareTreeDto dto = new CompareTreeDto();
            dto.setType(jsonNode.get("paramType").asText());
            dto.setIsArr("1".equals(jsonNode.get("isArr").asText()));
            dto.setChildren(new ArrayList<>());

            JsonNode childrenNode = jsonNode.get("children");
            if (childrenNode != null && childrenNode.isArray()) {
                for (JsonNode childNode : childrenNode) {
                    CompareTreeDto childDto = buildCompareTreeDto(childNode);
                    dto.getChildren().add(childDto);
                }
            }
        return dto;
    }

    private boolean compareTree(CompareTreeDto node1, CompareTreeDto node2) {
        // 如果节点都为 null，则认为相等
        if (node1 == null || node2 == null) {
            return node1 == null && node2 == null;
        }
        sortTree(node1);
        sortTree(node2);

        if (!Objects.equals(node1.getType(), node2.getType()) || !Objects.equals(node1.getIsArr(), node2.getIsArr())) {
            return false;
        }

        List<CompareTreeDto> children1 = node1.getChildren();
        List<CompareTreeDto> children2 = node2.getChildren();

        if (children1.size() != children2.size()) {
            return false;
        }

        for (int i = 0; i < children1.size(); i++) {
            CompareTreeDto child1 = children1.get(i);
            CompareTreeDto child2 = children2.get(i);

            if (!compareTree(child1, child2)) {
                return false;
            }
        }
        return true;
    }

    private static void sortTree(CompareTreeDto node) {
        // 对节点的子节点进行排序
        node.getChildren().sort(Comparator.comparing(CompareTreeDto::getType));

        // 递归对子节点进行排序
        for (CompareTreeDto child : node.getChildren()) {
            sortTree(child);
        }
    }

    private void assembleVarsTreeDtos(List<DomainDataModelTreeDto> treeDtoList, List<VarProcessManifestVariable> variableMappingList, List<String> typeList) {
        if (!CollectionUtils.isEmpty(variableMappingList)) {
            List<VarProcessVariable> variableList = varProcessVariableService.list(Wrappers.<VarProcessVariable>lambdaQuery()
                    .in(VarProcessVariable::getId, variableMappingList.stream().map(VarProcessManifestVariable::getVariableId).distinct().collect(Collectors.toList())));
            List<VarProcessVariable> typeVarList = variableList.stream().filter(b -> typeList.contains(b.getDataType())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(typeVarList)) {
                List<DomainDataModelTreeDto> child = new ArrayList<>();
                typeVarList.forEach(var -> child.add(DomainDataModelTreeDto.builder()
                        .isUse("0")
                        .isEmpty("0")
                        .name(var.getName())
                        .describe(var.getLabel())
                        .isArr("0")
                        .label(var.getName() + "-" + var.getLabel())
                        .type(var.getDataType())
                        .value(MessageFormat.format("vars.{0}", var.getName()))
                        .identifier(var.getIdentifier())
                        .build()));
                treeDtoList.add(DomainDataModelTreeDto.builder()
                        .name(DomainModelSheetNameEnum.VARS.getMessage())
                        .value(DomainModelSheetNameEnum.VARS.getMessage())
                        .label("vars-变量")
                        .describe("变量")
                        .isArr("0")
                        .isEmpty("0")
                        .type("object")
                        .children(child)
                        .build());
            }
            }
    }

    /**
     * 验证外数授权码
     * @param authCode 授权码
     * @param outCode 外数code
     * @return true or false
     */
    public Boolean validateOutsideAuthCode(String authCode, String outCode) {
        //调用外数接口
        try {
            OutsideServiceAuthOutputDto validateResult = outsideService.validateOutsideAuthCode(authCode, outCode).getData();
            if (!validateResult.getPass()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_OUTSIDE_CHECK_FAIL, validateResult.getMessage());
            }
        } catch (FeignException e) {
            log.error("fail while remote calling outside service : ", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_OUTSIDE_CHECK_FAIL, e.getMessage());
        }
        return true;
    }
}
