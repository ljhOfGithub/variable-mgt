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
package com.wiseco.var.process.app.server.controller.backtracking;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOverallInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsReferenceValueQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingConfigDetailOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingStatisticsResultPageOutputVO;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingOverallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 批量回溯-整体分析
 * @author wuweikang
 */
@RestController
@RequestMapping("/backtracking/overall")
@Slf4j
@Tag(name = "批量回溯-整体分析")
public class BacktrackingOverallController {

    @Resource
    private BacktrackingOverallService backtrackingOverallService;

    /**
     * 获取批次号
     *
     * @param backtrackingId 批量回溯id
     * @return APIResult
     */
    @GetMapping("/getBatchNo")
    @Operation(summary = "获取批次号")
    public APIResult<List<String>> getBatchNo(@RequestParam("backtrackingId") Long backtrackingId) {
        return APIResult.success(backtrackingOverallService.getBatchNo(backtrackingId));
    }

    /**
     * 调用量统计-整体分+
     * 析-选择Y指标下拉列表
     *
     * @param backtrackingId 批量回溯id
     * @return 指标名称 String List
     */
    @GetMapping("/getColumns")
    @Operation(summary = "调用量统计-整体分析-选择Y指标下拉列表")
    public APIResult<List<String>> getColumns(@RequestParam("backtrackingId") Long backtrackingId) {
        return APIResult.success(backtrackingOverallService.getColumns(backtrackingId));
    }

    /**
     * 调用量统计-整体分析-选择good、bad标签值下拉列表
     *
     * @param queryVo 查询参数
     * @return APIResult
     */
    @PostMapping("/getBacktrackingUnicode")
    @Operation(summary = "调用量统计-整体分析-来源于指标回溯选择good、bad标签值下拉列表")
    public APIResult<List<Object>> getBacktrackingUnicode(@RequestBody BacktrackingStatisticsReferenceValueQueryVO queryVo) {
        return APIResult.success(backtrackingOverallService.getInternalDataUnicode(queryVo));
    }

    /**
     * 调用量统计-整体分析-选择good、bad标签值下拉列表
     *
     * @param queryVo 查询参数
     * @return APIResult
     */
    @PostMapping("/getInternalDataUnicode")
    @Operation(summary = "调用量统计-整体分析-来源于内部数据表选择good、bad标签值下拉列表")
    public APIResult<List<Object>> getInternalDataUnicode(@RequestBody BacktrackingStatisticsReferenceValueQueryVO queryVo) {
        return APIResult.success(backtrackingOverallService.getInternalDataUnicode(queryVo));
    }

    /**
     * 获取基准值
     *
     * @param queryVo 指标回溯统计基准值查询 VO
     * @return APIResult 基准值集合
     */
    @PostMapping("/getReferenceValue")
    @Operation(summary = "获取基准值")
    public APIResult<List<Object>> getReferenceValue(@RequestBody BacktrackingStatisticsReferenceValueQueryVO queryVo) {
        return APIResult.success(backtrackingOverallService.getInternalDataUnicode(queryVo));
    }

    /**
     * 提交分析，保存配置和结果
     *
     * @param inputVO 分析参数、分页查询信息
     * @return APIResult
     */
    @PostMapping("/submit")
    @Operation(summary = "提交分析，保存配置和结果")
    public APIResult<Long> submit(@RequestBody BacktrackingOverallInputVO inputVO) {
        Long id = backtrackingOverallService.submit(inputVO);
        return APIResult.success(id);
    }

    /**
     * 编辑分析配置
     *
     * @param inputVO 入参
     * @return 给前端的JSON
     */
    @PostMapping("/update")
    @Operation(summary = "编辑分析配置")
    public APIResult editConfig(@RequestBody BacktrackingOverallInputVO inputVO) {
        backtrackingOverallService.editConfig(inputVO);
        return APIResult.success("操作成功!");
    }

    /**
     * 获取配置详情
     *
     * @param backtrackingId 批量回溯id
     * @return StatisticsConfigDetailOutputVO
     */
    @GetMapping("/detail")
    @Operation(summary = "获取配置详情")
    public APIResult<BacktrackingConfigDetailOutputVO> getDetail(@RequestParam("backtrackingId") Long backtrackingId) {
        return APIResult.success(backtrackingOverallService.getConfigDetail(backtrackingId));
    }

    /**
     * 分析结果记录列表
     *
     * @param queryVo 指标回溯统计结果查询 VO
     * @return APIResult
     */
    @PostMapping("/result/page")
    @Operation(summary = "分析结果记录列表")
    public APIResult<BacktrackingStatisticsResultPageOutputVO> getResultPage(@RequestBody BacktrackingStatisticsResultQueryVO queryVo) {
        return APIResult.success(backtrackingOverallService.getResultPage(queryVo));
    }

    /**
     * 根据backtrackingId查看变量清单信息
     *
     * @param backtrackingId 批量回溯id
     * @return APIResult
     */
    @GetMapping("/calculate")
    @Operation(summary = "根据backtrackingId查看变量清单信息")
    public APIResult calculate(@RequestParam("backtrackingId") Long backtrackingId) {
        backtrackingOverallService.calculateIndex(backtrackingId);
        return APIResult.success("计算成功！");
    }

    /**
     * 导出统计结果
     *
     * @param queryVo 指标回溯统计结果查询
     * @param response http响应
     */
    @PostMapping("/export")
    @Operation(summary = "导出统计结果")
    public void exportResult(@RequestBody BacktrackingStatisticsResultQueryVO queryVo, HttpServletResponse response) {
        backtrackingOverallService.export(queryVo, response);
    }

}
