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
package com.wiseco.var.process.app.server.controller.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.dto.DomainDataVersionCompareOutputDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.decision.engine.var.runtime.api.InternalDataService;
import com.wiseco.var.process.app.server.controller.vo.input.DataVersionCompareInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.InternalDataServiceVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelViewInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.OutsideServerGetDataModelOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeGetBySourceTypeOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelVarUseOutputVo;
import com.wiseco.var.process.app.server.service.datamodel.DataModelViewBiz;
import com.wisecoprod.starterweb.pojo.ApiResult;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 变量加工数据模型 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
@RestController
@RequestMapping("/variableDataModel")
@Slf4j
@Tag(name = "数据模型view")
public class DataModelViewController {

    @Resource
    private DataModelViewBiz dataModelViewBiz;

    @Resource
    @Qualifier("internalDataServiceImpl")
    private InternalDataService internalDataServiceImpl;

    /**
     * 数据模型列表
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/dataModelList")
    @Operation(summary = "数据模型列表")
    public APIResult<IPage<VariableDataModelListOutputVO>> getDataModelList(@Validated VariableDataModelQueryInputVO inputVO) {
        log.info("数据模型列表，inputVO:{}", inputVO);
        return APIResult.success(dataModelViewBiz.getDataModelList(inputVO));
    }

    /**
     * 获取最大版本的数据模型对象
     *
     * @param inputDto 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/objectList")
    @Operation(summary = "获取最大版本的数据模型对象")
    public APIResult<List<String>> getObjectList(@Validated @RequestBody VariableDataModelQueryInputVO inputDto) {
        return APIResult.success(dataModelViewBiz.getObjectList(inputDto.getSpaceId()));
    }

    /**
     * 根据数据模型来源类型获取数据模型最新版本列表
     *
     * @param sourcetype 来源类型
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/getDataModelBySourceType")
    @Operation(summary = "根据数据模型来源类型获取数据模型最新版本列表")
    public APIResult<List<VariableDataModeGetBySourceTypeOutputVO>> getDataModelBySourceType(@RequestParam("sourcetype") @NotNull(message = "数据模型来源类型不能为空") @Parameter(description = "数据模型来源类型") String sourcetype) {
        List<VariableDataModeGetBySourceTypeOutputVO> dataModels = dataModelViewBiz.getDataModelMaxVersionList(sourcetype);
        return APIResult.success(dataModels);
    }

    /**
     * 内部数据的入参获取
     *
     * @param objectName 对象名称
     * @param version 版本
     * @param dataModelId 数据模型id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/getInsideDataEntering")
    @Operation(summary = "内部数据的入参获取")
    public APIResult<List<VariableDataModeViewOutputVo.InsideInputVO>> getInsideDataEntering(@RequestParam(value = "objectName") @Parameter(description = "对象名") String objectName,
                                                                                             @RequestParam(value = "version") @Parameter(description = "版本") Integer version,
                                                                                             @RequestParam(value = "dataModelId") @Parameter(description = "数据模型ID") Long dataModelId) {
        return APIResult.success(dataModelViewBiz.getInsideDataEntering(objectName, version, dataModelId));
    }

    /**
     * 外数服务的入参获取
     *
     * @param outId 外数Id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/getOutsideServerEntering")
    @Operation(summary = "外数服务的入参获取")
    public APIResult<JSONArray> getOutsideServerEntering(@RequestParam(value = "outId") @Parameter(description = "外数服务ID") Long outId) {
        return APIResult.success(dataModelViewBiz.getOutsideServerEntering(outId));
    }

    /**
     * 获取外数服务入参
     * @param outId 外数id
     * @param modelId 数据模型id
     * @return 入参jsonArray
     */
    @GetMapping("/getOutsideServiceInputParams")
    @Operation(summary = "获取外数服务入参")
    public APIResult<JSONArray> getOutsideServiceInputParams(@RequestParam(value = "outId") @Parameter(description = "外数服务ID") Long outId,
                                                             @RequestParam(value = "modelId") @Parameter(description = "数据模型id")Long modelId) {
        return APIResult.success(dataModelViewBiz.getOutsideServiceInputParams(outId,modelId));
    }

    /**
     * 外数服务对应的数据模型列表
     *
     * @param outName 外数名
     * @param manifestId 清单id
     * @param outCode code
     * @param outId 外数id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/getOutsideServerDataModelList")
    @Operation(summary = "外数服务对应的数据模型列表")
    public APIResult<List<OutsideServerGetDataModelOutputVO>> getOutsideServerDataModelList(@RequestParam(value = "outName") @Parameter(description = "对象名") String outName,
                                                                                            @RequestParam(value = "outId") @Parameter(description = "外数id") Long outId,
                                                                                            @RequestParam(value = "outCode") @Parameter(description = "外数code") String outCode,
                                                                                            @RequestParam(value = "manifestId") @Parameter(description = "清单id") Long manifestId) {
        return APIResult.success(dataModelViewBiz.getOutsideServerDataModelList(outName, manifestId, outCode));
    }

    /**
     * 数据模型查看
     *
     * @param inputVo 输入
     * @return VariableDataModeViewOutputVo 输出
     */
    @PostMapping("/dataModelView")
    @Operation(summary = "数据模型查看")
    public APIResult<VariableDataModeViewOutputVo> dataModelView(@RequestBody VariableDataModelViewInputVo inputVo) {
        VariableDataModeViewOutputVo treeDtoList = dataModelViewBiz.dataModelView(inputVo);
        return APIResult.success(treeDtoList);
    }

    /**
     * 查询数据模型引用
     *
     * @param spaceId 变量空间Id
     * @param objectName 对象名
     * @param version 版本
     * @param dataModelId 数据模型Id
     * @return 数据模型引用
     */
    @GetMapping("/getDataModelUseList")
    @Operation(summary = "查询数据模型引用")
    public APIResult<List<VariableDataModelVarUseOutputVo>> getDataModelUseList(@RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空。") @Parameter(description = "变量空间 ID") Long spaceId,
                                                                                @RequestParam("objectName") @NotNull(message = "对象名不能为空。") @Parameter(description = "对象名") String objectName,
                                                                                @RequestParam("version") @NotNull(message = "版本不能为空。") @Parameter(description = "版本") Integer version,
                                                                                @RequestParam("dataModelId") @NotNull(message = "数据模型ID不能为空") @Parameter(description = "数据模型ID") Long dataModelId) {
        return APIResult.success(dataModelViewBiz.getDataModelUseList(spaceId, objectName, version, dataModelId));
    }

    /**
     * 获取数据模型对象信息
     *
     * @param spaceId 变量空间Id
     * @param dataModelId 数据模型Id
     * @return 数据模型对象
     */
    @GetMapping("/getDataModel")
    @Operation(summary = "获取数据模型对象信息")
    public APIResult<List<DomainDataModelTreeDto>> getDataModel(@RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空。") @Parameter(description = "变量空间 ID") Long spaceId,
                                                                @RequestParam("dataModelId") @NotNull(message = "数据模型ID不能为空。") @Parameter(description = "数据模型ID") Long dataModelId) {
        List<DomainDataModelTreeDto> treeDtoList = dataModelViewBiz.getDataModel(spaceId, dataModelId);
        return APIResult.success(treeDtoList);
    }

    /**
     * 数据模型版本比较
     *
     * @param inputDto 输入实体类对象
     * @return 数据模型版本比较输出参数
     */
    @PostMapping("/findVariableDataModelCompareVersion")
    @Operation(summary = "数据模型版本比较")
    public APIResult<DomainDataVersionCompareOutputDto> findVariableDataModelCompareVersion(@RequestBody DataVersionCompareInputVO inputDto) {
        DomainDataVersionCompareOutputDto outputDto = dataModelViewBiz.findVariableDataModelCompareVersion(inputDto);
        return APIResult.success(outputDto);
    }

    /**
     * 获取所有数据模型引用的内部数据表
     *
     * @return 内部数据表
     */
    @GetMapping("/getAllInternalDataTableName")
    @Operation(summary = "获取所有数据模型引用的内部数据表")
    public APIResult<List<String>> getAllInternalDataTableName() {
        return APIResult.success(dataModelViewBiz.getAllInternalDataTableName());
    }

    /**
     * 测试获取内部数据
     *
     * @param inputVo 输入实体类对象
     * @return 内部数据
     */
    @PostMapping("/testGetData")
    @Operation(summary = "测试获取内部数据")
    public APIResult<JSONObject> testGetData(@RequestBody InternalDataServiceVo inputVo) {
        return APIResult.success(internalDataServiceImpl.getInternalData(inputVo.getSpaceId(), inputVo.getIdentifier(), inputVo.getParams()));
    }

    /**
     * 数据模型查看
     *
     * @param objectName 数据模型名称
     * @return VariableDataModeViewOutputVo 输出
     */
    @GetMapping("/getdataModelViewByName")
    @Operation(summary = "数据模型查看")
    public APIResult<VariableDataModeViewOutputVo> getdataModelView(@Schema(description = "数据模型名称") @RequestParam("objectName") String objectName) {
        VariableDataModeViewOutputVo treeDtoList = dataModelViewBiz.getMaxDataModelViewByName(objectName);
        return APIResult.success(treeDtoList);
    }

    /**
     * 根据数据模型对象的name，查询子集
     *
     * @param objectName 数据模型对象名称
     * @return VariableDataModeViewOutputVo 输出
     */
    @GetMapping("/getModelObjectChildren")
    @Operation(summary = "获取数据模型对象子节点信息")
    public APIResult<DomainDataModelTreeDto> getModelObjectChildren(@Schema(description = "对象名称") @RequestParam("objectName") String objectName) {
        DomainDataModelTreeDto treeDto = dataModelViewBiz.getModelObjectChildren(objectName);
        return APIResult.success(treeDto);
    }

    /**
     * 获取外部传入数据模型
     * @return map
     */
    @GetMapping("/findExternalParamDataModels")
    @Operation(summary = "获取外部传入数据模型：key-数据模型名称；value-中文名")
    public ApiResult<Map<String,String>> findExternalParamDataModels() {
        return ApiResult.success(dataModelViewBiz.findExternalParamDataModels());
    }

}
