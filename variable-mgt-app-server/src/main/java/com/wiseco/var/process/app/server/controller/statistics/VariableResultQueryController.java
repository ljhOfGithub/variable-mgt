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
package com.wiseco.var.process.app.server.controller.statistics;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.feign.VarProcessConsumerFeign;
import com.wiseco.var.process.app.server.controller.vo.input.ConditionSettingSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DataViewInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableResultDetailQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableResultListQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ConditionSettingListOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DataViewOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultDatagramQueryOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultListQueryOutputDto;
import com.wiseco.var.process.app.server.service.VariableResultQueryBiz;
import com.wiseco.var.process.app.server.service.dto.PagedQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 指标结果查询 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/22
 */
@RestController
@RequestMapping("/variableResultQuery")
@Tag(name = "查询统计-结果查询")
@Validated
public class VariableResultQueryController {

    @Autowired
    private VariableResultQueryBiz variableResultQueryBiz;

    @Autowired
    private VarProcessConsumerFeign varProcessConsumerFeign;

    /**
     * saveConditionSetting
     *
     * @param inputDto
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/conditionSetting/save")
    @Operation(summary = "保存查询条件，表头列")
    public APIResult saveConditionSetting(@Parameter(description = "保存查询条件，表头列 入参DTO") @Validated @RequestBody ConditionSettingSaveInputDto inputDto) {
        variableResultQueryBiz.saveConditionSetting(inputDto);
        return APIResult.success();
    }

    /**
     * listConditionSetting
     *
     * @param manifestId
     * @param settingType
     * @return com.wiseco.boot.commons.web.APIResult<com.wiseco.outside.service.server.dto.output.ConditionSettingListOutputDto>
     */
    @GetMapping("/conditionSetting/list")
    @Operation(summary = "查询条件，表头列查询")
    public APIResult<ConditionSettingListOutputDto> listConditionSetting(@Parameter(name = "manifestId", description = "清单id", in = ParameterIn.QUERY)
                                                                         @RequestParam(value = "manifestId", required = false) Long manifestId,
                                                                         @Parameter(name = "settingType", description = "配置类型，0：结果查询，1：数据查看", in = ParameterIn.QUERY)
                                                                         @RequestParam(value = "settingType", required = false) Integer settingType) {
        if (settingType == null) {
            settingType = MagicNumbers.ZERO;
        }
        return APIResult.success(variableResultQueryBiz.listConditionSetting(manifestId, settingType));
    }

    /**
     * 实时服务的调用记录查询
     *
     * @param inputDto 前端发送过来的输入实体
     * @return 报文
     */
    @PostMapping("/datagram")
    @Operation(summary = "查询统计-结果查询-查询记录-报文查看")
    public APIResult<VariableResultDatagramQueryOutputDto> getResultDatagram(@RequestBody VariableResultDetailQueryInputDto inputDto) {
        return varProcessConsumerFeign.getRestCallRecordMessage(inputDto.getEngineSerialNo());
    }

    /**
     * 实时结果查询
     *
     * @param inputDto 输入实体类对象
     * @return 实时结果
     */
    @PostMapping("/realTimeList")
    @Operation(summary = "查询统计-结果查询-查询记录")
    public APIResult<PagedQueryResult<VariableResultListQueryOutputDto>> getRealTimeResultList(@RequestBody @Validated VariableResultListQueryInputDto inputDto) {
        // 1.获取经过权限处理的实时服务(具体的)的ID集合
        List<Long> serviceIds = variableResultQueryBiz.getVersionServiceIds();
        inputDto.getBuiltInParam().setServiceIds(serviceIds);
        // 2.远程调用，返回结果
        return varProcessConsumerFeign.findRestCallRecordList(inputDto);
    }


    /**
     * 获取所有被调用过的实时服务列表
     *
     * @return 实时服务器列表
     */
    @GetMapping("/realTimeServiceList")
    @Operation(summary = "获取所有被调用过的实时服务列表")
    public APIResult<List<ServiceListOutputVo>> getRealTimesService() {
        return APIResult.success(variableResultQueryBiz.getRealTimeService());
    }

    /**
     * 获取实时服务器列表
     *
     * @return 实时服务器列表
     */
    @GetMapping("/getUpServiceList")
    @Operation(summary = "获取所有启用的实时服务器列表")
    public APIResult<List<ServiceListOutputVo>> getUpServiceList() {
        return APIResult.success(variableResultQueryBiz.getUpServiceList());
    }

    /**
     * 获取调用清单列表
     *
     * @param realTimeServiceName 实时服务Id
     * @param manifestType        清单角色
     * @return 调用清单列表
     */
    @GetMapping("/callList")
    @Operation(summary = "获取调用清单列表")
    public APIResult<List<Map<String, Object>>> getCallList(@Parameter(description = "实时服务的Id") @RequestParam("realTimeServiceName") Long realTimeServiceName,
                                                            @Parameter(description = "清单的角色, 1——主清单；0——异步清单") @RequestParam("manifestType") Short manifestType) {
        List<Map<String, Object>> resList = variableResultQueryBiz.getCallList(realTimeServiceName, manifestType);
        return APIResult.success(resList);
    }

    /**
     * 获取实时服务下的清单
     *
     * @param serviceId 实时服务Id
     * @return 清单map
     */
    @GetMapping("/callMap")
    @Operation(summary = "获取实时服务下的清单")
    public APIResult<Map<Long, String>> getManifestIdNameMap(@NotNull(message = "服务id不能为空") @RequestParam("serviceId") Long serviceId) {
        return APIResult.success(variableResultQueryBiz.getManifestIdNameMap(serviceId));
    }

    /**
     * 数据查询
     *
     * @param dataViewInputDto 输入实体类对象
     * @return 数据查询出参
     */
    @PostMapping("/dataView")
    @Operation(summary = "查询统计-结果查询-数据查看")
    public APIResult<DataViewOutputDto> getDataView(@RequestBody DataViewInputDto dataViewInputDto) {
        // 1.获取经过权限处理的实时服务(具体的)的ID集合
        List<Long> serviceIds = variableResultQueryBiz.getVersionServiceIds();
        dataViewInputDto.getBuiltInParam().setServiceIds(serviceIds);
        dataViewInputDto.getBuiltInParam().setExternalSerialNo(dataViewInputDto.getBuiltInParam().getPrincipalUniqueIdentification());
        // 2.调用consumer
        return varProcessConsumerFeign.getDataView(dataViewInputDto);
    }

    /**
     * 调用方列表
     *
     * @return 调用方列表
     */
    @GetMapping("/callerList")
    @Operation(summary = "调用方列表")
    public APIResult<List<String>> getDataView() {
        List<String> callerList = variableResultQueryBiz.callerList();
        return APIResult.success(callerList);
    }

    /**
     * 获取字段类型
     *
     * @param callList 调用清单名称
     * @return 字段类型
     */
    @GetMapping("/filedType")
    @Operation(summary = "获取字段类型")
    public APIResult<List<TableFieldVO>> getFiledType(String callList) {
        List<TableFieldVO> result = variableResultQueryBiz.getFiledType(callList);
        return APIResult.success(result);
    }

    /**
     * 获取查询条件/显示列
     *
     * @param manifestId 清单id
     * @param condition  1-查询条件；2-显示列
     * @return 字段名称+类型
     */
    @GetMapping("/findTabField")
    @Operation(summary = "获取查询条件/显示列")
    public APIResult<List<TableFieldVO>> findTabField(@RequestParam("manifestId") @NotNull(message = "清单id不能为空") @Parameter(description = "清单id", required = true) Long manifestId,
                                                      @RequestParam("condition") @NotNull @Parameter(description = "1-查询条件；0-显示列", required = true) Integer condition) {
        return APIResult.success(varProcessConsumerFeign.findTabField(manifestId, condition));
    }

    /**
     * 下载数据
     *
     * @param dataViewInputDto 结果查询数据查看入参DTO
     * @param response         HttpServletResponse对象
     */
    @PostMapping("/downLoadData")
    @Operation(summary = "查询统计-结果查询-数据查看-下载数据")
    public void downLoadData(@RequestBody DataViewInputDto dataViewInputDto, HttpServletResponse response) {
        variableResultQueryBiz.downLoadData(dataViewInputDto, response);
    }

}
