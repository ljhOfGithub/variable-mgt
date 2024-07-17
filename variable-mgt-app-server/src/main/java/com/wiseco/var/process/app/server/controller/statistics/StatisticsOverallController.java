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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsConfigCreationInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsReferenceFromMonitoringValueInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsReferenceValueInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingStatisticsResultPageOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.StatisticsConfigDetailOutputVO;
import com.wiseco.var.process.app.server.service.statistics.StatisticsOverallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/statistics/overall")
@Slf4j
@Tag(name = "统计分析-整体分析")
public class StatisticsOverallController {

    @Resource
    private StatisticsOverallService statisticsOverallService;

    /**
     * 调用量统计-整体分析-(IV参数设置)选择Y指标下拉列表
     * @param varProcessManifestId 变量清单的Id
     * @return 指标名称的列表
     */
    @GetMapping("/getColumns")
    @Operation(summary = "调用量统计-整体分析-(IV参数设置)选择Y指标下拉列表")
    public APIResult<List<String>> getColumns(@Parameter(description = "变量清单的Id") @RequestParam("varProcessManifestId") Long varProcessManifestId) {
        return APIResult.success(statisticsOverallService.getColumns(varProcessManifestId));
    }


    /**
     * 调用量统计-整体分析-分组分析字段下拉列表
     * @param varProcessManifestId 变量清单的Id
     * @param varProcessManifestColRole 变量清单的列角色col_role
     * @return 分组变量名称的列表
     */
    @GetMapping("/getGroupField")
    @Operation(summary = "调用量统计-整体分析-分组字段")
    public APIResult<List<String>> getGroupField(@Parameter(description = "变量清单的Id") @RequestParam("varProcessManifestId") Long varProcessManifestId,
                                                 @Parameter(description = "分组字段allow = {\"GROUP\",\"TARGET\"}") @RequestParam("varProcessManifestColRole") String varProcessManifestColRole) {
        return APIResult.success(statisticsOverallService.getGroupField(varProcessManifestId, varProcessManifestColRole));
    }

    /**
     * 调用量统计-整体分析-(IV参数设置)来源于实时服务选择good、bad标签值下拉列表
     * @param varProcessServiceId 实时服务Id
     * @param varProcessManifestId 变量清单Id
     * @param fieldName Y指标的名称(指标的名称)
     * @return 服务的唯一字段列表
     */
    @GetMapping("/getServiceUnicode")
    @Operation(summary = "调用量统计-整体分析-(IV参数设置)来源于实时服务选择good、bad标签值下拉列表")
    public APIResult<List<Object>> getServiceUnicode(@Parameter(description = "实时服务Id") @RequestParam("varProcessServiceId") Long varProcessServiceId,
                                                     @Parameter(description = "变量清单Id") @RequestParam("varProcessManifestId") Long varProcessManifestId,
                                                     @Parameter(description = "Y指标的名称(指标的名称)") @RequestParam("fieldName") String fieldName) {
        return APIResult.success(statisticsOverallService.getServiceUnicode(varProcessServiceId, varProcessManifestId, fieldName));
    }

    /**
     * 监控规则-IV参数设置-来源于实时服务选择good、bad标签值下拉列表
     *
     * @param serviceName          服务名称
     * @param serviceVersion       服务版本
     * @param varProcessManifestId 变量清单Id
     * @param fieldName            Y指标的名称(指标的名称)
     * @return 服务的唯一字段列表
     */
    @GetMapping("/getServiceUnicodeFromMonitoring")
    @Operation(summary = "监控规则-IV参数设置-来源于实时服务选择good、bad标签值下拉列表")
    public APIResult<List<Object>> getServiceUnicodeFromMonitoring(@Parameter(description = "实时服务名称") @RequestParam("serviceName") String serviceName,
                                                     @Parameter(description = "实时服务版本") @RequestParam("serviceVersion") Integer serviceVersion,
                                                     @Parameter(description = "变量清单Id") @RequestParam("varProcessManifestId") Long varProcessManifestId,
                                                     @Parameter(description = "Y指标的名称(指标的名称)") @RequestParam("fieldName") String fieldName) {
        return APIResult.success(statisticsOverallService.getServiceUnicodeFromMonitoring(serviceName, serviceVersion, varProcessManifestId, fieldName));
    }

    /**
     * 调用量统计-整体分析-(来源于内部数据表)选择good、bad标签值下拉列表
     * @param fieldName Y指标的名称(内部数据表的字段)
     * @param tableName 内部数据表的名称
     * @return 内部数据表的唯一字段列表
     */
    @GetMapping("/getInternalUnicode")
    @Operation(summary = "调用量统计-整体分析-来源于内部数据表选择good、bad标签值下拉列表")
    public APIResult getInternalUnicode(@RequestParam("fieldName") String fieldName,@RequestParam("tableName") String tableName) {
        return APIResult.success(statisticsOverallService.getInternalUnicode(fieldName,tableName));
    }

    /**
     * 添加整体分析配置(对应于前端的确定按钮)
     * @param inputVO 入参
     * @return 静态配置Id
     */
    @PostMapping("/add")
    @Operation(summary = "添加整体分析配置")
    public APIResult<Long> createStatisticsConfig(@RequestBody StatisticsConfigCreationInputVO inputVO) {
        Long aLong = statisticsOverallService.addStatisticsConfigHandler(inputVO);
        return APIResult.success(aLong);
    }

    /**
     * 编辑整体分析配置
     *
     * @param inputVO 入参
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/update")
    @Operation(summary = "编辑整体分析配置")
    public APIResult editStatisticsConfig(@RequestBody StatisticsConfigCreationInputVO inputVO) {
        statisticsOverallService.editStatisticsConfigHandler(inputVO);
        return APIResult.success("操作成功!");
    }

    /**
     * 获取整体分析配置详情
     *
     * @param varProcessServiceId varProcessServiceId
     * @param varProcessManifestId varProcessManifestId
     * @return StatisticsConfigDetailOutputVO
     */
    @GetMapping("/detail")
    @Operation(summary = "获取整体分析配置详情")
    public APIResult<StatisticsConfigDetailOutputVO> getDetail(@RequestParam("varProcessServiceId") Long varProcessServiceId,
                                                               @RequestParam("varProcessManifestId") Long varProcessManifestId) {
        return APIResult.success(statisticsOverallService.getConfigDetail(varProcessServiceId, varProcessManifestId));
    }

    /**
     * 统计分析结果记录列表
     *
     * @param queryVo 入参
     * @return APIResult
     */
    @PostMapping("/result/page")
    @Operation(summary = "统计分析结果记录列表")
    public APIResult<BacktrackingStatisticsResultPageOutputVO> getResultPage(@RequestBody VarProcessStatisticsResultQueryVO queryVo) {
        return APIResult.success(statisticsOverallService.getResultPage(queryVo));
    }

    /**
     * 统计分析计算
     *
     * @param varProcessServiceId varProcessServiceId
     * @param varProcessManifestId varProcessManifestId
     * @return 无
     */
    @GetMapping("/calculate")
    @Operation(summary = "统计分析计算")
    public APIResult calculate(@RequestParam("varProcessServiceId") Long varProcessServiceId,
                               @RequestParam("varProcessManifestId") Long varProcessManifestId) {
        statisticsOverallService.calculateIndexHandler(varProcessServiceId, varProcessManifestId);
        return APIResult.success("计算成功！");
    }

    /**
     * 导出统计结果
     *
     * @param queryVo 入参
     * @param response 入参
     */
    @PostMapping("/export")
    @Operation(summary = "导出统计结果")
    public void exportResult(@RequestBody VarProcessStatisticsResultQueryVO queryVo, HttpServletResponse response) {
        statisticsOverallService.export(queryVo, response);
    }

    /**
     * 获取基准值
     * @param queryVo 指标回溯统计基准值查询 VO
     * @return APIResult 基准值集合
     */
    @PostMapping("/getReferenceValue")
    @Operation(summary = "获取基准值")
    public APIResult<List<String>> getReferenceValue(@RequestBody StatisticsReferenceValueInputVO queryVo) {
        return APIResult.success(statisticsOverallService.getReferenceValue(queryVo));
    }


    /**
     * 指标监控——获取基准值
     * @param queryVo 指标回溯统计基准值查询 VO
     * @return APIResult 基准值集合
     */
    @PostMapping("/getReferenceValueFromMonitoring")
    @Operation(summary = "指标监控——获取基准值")
    public APIResult<List<String>> getReferenceValueFromMonitoring(@RequestBody StatisticsReferenceFromMonitoringValueInputVO queryVo) {
        return APIResult.success(statisticsOverallService.getReferenceValueFromMonitoring(queryVo));
    }

    /**
     * 获取基准数据项
     * @param serviceId 实时服务的Id
     * @param manifestId 变量清单的Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param baseIndexCallDate 基准指标的调用时间段 true/与设置的时间维度一致; false/所有时间段
     * @param indexName 基准分组指标的下拉值
     * @return 基准数据项
     */
    @GetMapping("/getBasicValueItem")
    @Operation(summary = "获取基准数据项")
    public APIResult<List<String>> getBasicValueItem(@RequestParam("serviceId") @Parameter(description = "实时服务的Id", example = "10000", required = true) @NotNull(message = "实时服务Id不能为空") Long serviceId,
                                                     @RequestParam("manifestId") @Parameter(description = "变量清单的Id", example = "20000", required = true) @NotNull(message = "变量清单Id不能为空") Long manifestId,
                                                     @RequestParam("startDateTime") @Parameter(description = "开始时间", example = "2023-10-23 15:11:20", required = true) @JsonDeserialize(using = LocalDateTimeDeserializer.class) @JsonSerialize(using = LocalDateTimeSerializer.class) @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") LocalDateTime startDateTime,
                                                     @RequestParam("endDateTime") @Parameter(description = "结束时间", example = "2023-11-05 17:20:40", required = true) @JsonDeserialize(using = LocalDateTimeDeserializer.class) @JsonSerialize(using = LocalDateTimeSerializer.class) @DateTimeFormat(pattern =  "yyyy-MM-dd HH:mm:ss") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") LocalDateTime endDateTime,
                                                     @RequestParam("baseIndexCallDate") @Parameter(description = "基准指标的调用时间段 true/与设置的时间维度一致; false/所有时间段", example = "true", required = true) @NotNull(message = "基准指标的调用时间段不能为空") Boolean baseIndexCallDate,
                                                     @RequestParam("indexName") @Parameter(description = "基准分组指标的下拉值", example = "indicator", required = true) @NotNull(message = "基准分组指标不能为空") String indexName) {
        // 1.调用业务逻辑层的函数
        List<String> result = statisticsOverallService.getBasicValueItem(serviceId, manifestId, startDateTime, endDateTime, baseIndexCallDate, indexName);
        // 2.返回结果
        return APIResult.success(result);
    }

}
