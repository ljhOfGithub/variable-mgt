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
package com.wiseco.var.process.app.server.controller.monitoring;

import cn.hutool.core.bean.BeanUtil;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.ReportFormCreateInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.ReportFormDeleteInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ReportFormDuplicationInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ReportFormSearchInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.ReportFormStatusInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ReportFormDetailOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ReportFormListOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ReportFormsOutputVo;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormDeleteInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormDuplicationInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormSearchInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormStatusInputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormDetailOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormListOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormsOutputDto;
import com.wiseco.var.process.app.server.service.monitoring.VarProcessReportFormServiceBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * 监控报表的Controller
 */

@RestController
@RequestMapping("/monitoring/reportForm")
@Slf4j
@Tag(name = "监控报表")
@Validated
public class ReportFormController {

    @Autowired
    private VarProcessReportFormServiceBiz varProcessReportFormServiceBiz;

    /**
     * 添加监控报表
     * @param inputVo 输入实体类对象
     * @return 新的监控报表ID
     */
    @PostMapping("/addReportForm")
    @Operation(summary = "添加监控报表, 返回值是新监控报表的ID")
    public APIResult<Long> addReportForm(@Validated @RequestBody ReportFormCreateInputVO inputVo) {
        // 1.转换Bean类
        ReportFormCreateInputDto inputDto = new ReportFormCreateInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.调用业务逻辑层的函数，返回结果
        Long result = varProcessReportFormServiceBiz.addReportForm(inputDto);
        return APIResult.success(result);
    }

    /**
     * 报表管理处的报表查询
     * @param inputVo 输入实体类对象
     * @return 报表的列表
     */
    @GetMapping("/searchReportForm")
    @Operation(summary = "报表管理处的报表查询")
    public APIResult<ReportFormListOutputVo> searchReportForm(ReportFormSearchInputVo inputVo) {
        // 1.转换Bean类
        ReportFormSearchInputDto inputDto = new ReportFormSearchInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.调用业务逻辑层的函数, 返回结果
        ReportFormListOutputDto reportFormListOutputDto = varProcessReportFormServiceBiz.searchReportForm(inputDto);
        ReportFormListOutputVo result = new ReportFormListOutputVo();
        BeanUtil.copyProperties(reportFormListOutputDto, result);
        return APIResult.success(result);
    }

    /**
     * 删除监控报表
     * @param inputVo 输入实体类对象
     * @return 删除结果
     */
    @PostMapping("/deleteReportForm")
    @Operation(summary = "删除监控报表, true-删除成功, false-删除失败")
    public APIResult<Boolean> deleteReportForm(@Validated @RequestBody ReportFormDeleteInputVo inputVo) {
        // 1.调用业务逻辑层的函数，完成删除操作
        ReportFormDeleteInputDto inputDto = new ReportFormDeleteInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        Boolean result = varProcessReportFormServiceBiz.deleteReportForm(inputDto);
        // 2.返回业务逻辑层的执行结果
        return APIResult.success(result);
    }

    /**
     * 复制监控报表
     * @param inputVo 输入实体类对象
     * @return 新监控报表的Id
     */
    @PostMapping("/duplicateReportForm")
    @Operation(summary = "复制监控报表, 返回新监控报表的Id")
    public APIResult<Long> duplicateReportForm(@Validated @RequestBody ReportFormDuplicationInputVo inputVo) {
        // 1.转换Bean类，并调用业务逻辑层的函数
        ReportFormDuplicationInputDto inputDto = new ReportFormDuplicationInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        Long result = varProcessReportFormServiceBiz.duplicateReportForm(inputDto);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 查看详情
     * @param reportFormId 监控报表的Id
     * @return 详情信息
     */
    @GetMapping("/getDetail")
    @Operation(summary = "查看监控报表的详情")
    public APIResult<ReportFormDetailOutputVo> getDetail(@Parameter(description = "监控报表的Id") @RequestParam("reportFormId") @NotNull(message = "监控报表的Id不能为空") Long reportFormId) {
        // 1.调用业务逻辑层的函数，获取结果
        ReportFormDetailOutputDto detail = varProcessReportFormServiceBiz.getDetail(reportFormId);
        ReportFormDetailOutputVo result = new ReportFormDetailOutputVo();
        BeanUtil.copyProperties(detail, result);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 启用监控报表
     * @param inputVo 输入实体类对象
     * @return 启用后的结果
     */
    @PostMapping("/enableReportForm")
    @Operation(summary = "启用监控报表")
    public APIResult<Boolean> enableReportForm(@Validated @RequestBody ReportFormStatusInputVo inputVo) {
        // 1.调用业务逻辑层的函数
        ReportFormStatusInputDto inputDto = new ReportFormStatusInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        Boolean result = varProcessReportFormServiceBiz.enableReportForm(inputDto);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 停用监控报表
     * @param inputVo 输入实体类对象
     * @return 停用后的结果
     */
    @PostMapping("/disableReportForm")
    @Operation(summary = "停用监控报表")
    public APIResult<Boolean> disableReportForm(@Validated @RequestBody ReportFormStatusInputVo inputVo) {
        // 1.调用业务逻辑层的函数
        ReportFormStatusInputDto inputDto = new ReportFormStatusInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        Boolean result = varProcessReportFormServiceBiz.disableReportForm(inputDto);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 获取报表列表
     * @return 报表列表
     */
    @GetMapping("/getReportFormList")
    @Operation(summary = "获取报表列表")
    public APIResult<ReportFormsOutputVo> getReportFormList() {
        // 1.调用业务逻辑层的函数
        ReportFormsOutputDto outputDto = varProcessReportFormServiceBiz.getReportFormList();
        ReportFormsOutputVo result = new ReportFormsOutputVo();
        BeanUtil.copyProperties(outputDto, result);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 保存监控报表的更新
     * @param inputVo 输入实体类对象
     * @return 原对象的ID
     */
    @PostMapping("/saveUpdate")
    @Operation(summary = "保存监控报表的更新")
    public APIResult<Long> saveUpdate(@Validated @RequestBody ReportFormCreateInputVO inputVo) {
        // 1.转换Bean类
        ReportFormCreateInputDto inputDto = new ReportFormCreateInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.调用业务逻辑层的接口，返回结果
        Long result = varProcessReportFormServiceBiz.saveUpdate(inputDto);
        return APIResult.success(result);
    }

    /**
     * 预览监控图表
     * @param inputVo 入参
     * @return 图表jSON
     */
    @PostMapping("/previewDiagram")
    @Operation(summary = "预览监控图表")
    public APIResult<MonitoringDiagramOutputVo> previewDiagram(@Validated @RequestBody ReportFormCreateInputVO inputVo) {
        // 1.转换Bean类
        ReportFormCreateInputDto inputDto = new ReportFormCreateInputDto();
        BeanUtil.copyProperties(inputVo, inputDto);
        // 2.调用业务逻辑层的接口，返回结果
        MonitoringDiagramOutputVo result = varProcessReportFormServiceBiz.previewDiagram(inputDto);
        return APIResult.success(result);
    }

    /**
     * 报表查看
     * @param id 报表的ID
     * @return Echarts数据
     */
    @GetMapping("/viewReportForm/{id}")
    @Operation(summary = "报表查看")
    public APIResult<MonitoringDiagramOutputVo> viewReportForm(@PathVariable @NotNull(message = "监控报表的ID不能为空") Long id) {
        // 1.调用业务逻辑层的函数
        MonitoringDiagramOutputVo result = varProcessReportFormServiceBiz.viewReportForm(id);
        // 2.返回结果
        return APIResult.success(result);
    }
}
