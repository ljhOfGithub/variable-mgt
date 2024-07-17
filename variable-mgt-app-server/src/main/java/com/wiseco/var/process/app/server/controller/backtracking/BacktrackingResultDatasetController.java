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
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingDatasetQueryVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingMainifestResultVO;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingDatasetBiz;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author xupei
 */
@RestController
@RequestMapping("/backtracking/dataset")
@Slf4j
@Tag(name = "批量回溯结果查看-变量数据查看")
public class BacktrackingResultDatasetController {

    @Resource
    private BacktrackingDatasetBiz backtrackingDatasetBiz;

    /**
     * 分页查询/刷新数据
     *
     * @param queryVo 变量数据查询
     * @return APIResult
     */
    @PostMapping("/pageMainifestData")
    @Operation(summary = "分页查询/刷新数据")
    public APIResult<BacktrackingMainifestResultVO> pageMainifestData(@RequestBody BacktrackingDatasetQueryVO queryVo) {
        return APIResult.success(backtrackingDatasetBiz.pageManifestData(queryVo));
    }

    /**
     * 显示列设置/筛选字段
     *
     * @param backtrackingId  批量回溯id
     * @return APIResult
     */
    @PostMapping("/vars/{backtrackingId}")
    @Operation(summary = "显示列设置/筛选字段")
    @ApiImplicitParam(name = "backtrackingId", value = "批量回溯id", dataType = "Integer", required = true)
    public APIResult<List<TableFieldVO>> getDatasetVars(@PathVariable Integer backtrackingId) {
        return APIResult.success(backtrackingDatasetBiz.getDatasetVars(backtrackingId.longValue()));
    }


    /**
     * getDatasetVars
     *
     * @param backtrackingId 批量回溯id
     * @param type 类型 1.条件筛选列 2.显示列
     * @return com.wiseco.boot.commons.web.APIResult<java.util.List < com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO>>
     */
    @PostMapping("/vars/{backtrackingId}/{type}")
    @Operation(summary = "显示列设置/筛选字段")
    @ApiImplicitParam(name = "backtrackingId", value = "批量回溯id", dataType = "Integer", required = true)
    public APIResult<List<TableFieldVO>> getDatasetVars(@PathVariable Integer backtrackingId, @PathVariable Integer type) {
        return APIResult.success(backtrackingDatasetBiz.getDatasetVars(backtrackingId.longValue(),type));
    }

    /**
     * 批量回溯任务批次号
     *
     * @param backtrackingId 批量回溯id
     * @return APIResult
     */
    @GetMapping("/getBacktrackingTaskIds/{backtrackingId}")
    @Operation(summary = "任务批次号下拉框")
    @ApiImplicitParam(name = "backtrackingId", value = "批量回溯id", dataType = "Integer", required = true)
    public APIResult<List<String>> getBacktrackingTaskIds(@PathVariable Integer backtrackingId) {
        return APIResult.success(backtrackingDatasetBiz.getBacktrackingTaskIds(backtrackingId.longValue()));
    }

    /**
     * 下载数据
     *
     * @param backtrackingId 批量回溯id
     * @param response http响应
     */
    @PostMapping("/download/{backtrackingId}")
    @Operation(summary = "下载数据")
    @ApiImplicitParam(name = "backtrackingId", value = "批量回溯id", dataType = "Integer", required = true)
    public void exportAllData(@PathVariable Integer backtrackingId, HttpServletResponse response) {
        try {
            backtrackingDatasetBiz.exportAll(backtrackingId, response);
        } catch (Exception e) {
            log.error("批量回溯结果查看-变量数据导出失败", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR,"数据导出失败");
        }
    }
}
