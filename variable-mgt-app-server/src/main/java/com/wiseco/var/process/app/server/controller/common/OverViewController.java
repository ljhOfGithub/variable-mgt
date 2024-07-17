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
package com.wiseco.var.process.app.server.controller.common;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.output.OverViewNameAndQuantityOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.OverViewQuantityAndStatusOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.OverviewTargetRankingOutputVo;
import com.wiseco.var.process.app.server.enums.CallVolumeByTimeEnum;
import com.wiseco.var.process.app.server.service.OverViewBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wangxiansheng
 */
@RestController
@RequestMapping("/overView")
@Slf4j
@Tag(name = "睿信概览")
public class OverViewController {

    @Resource
    private OverViewBiz overViewBiz;


    /**
     * 睿信概览统计
     * @return  所有统计数量与状态
     */
    @GetMapping("/getOverViewBizQuantityAndStatus")
    @Operation(description = "概览统计数量与状态")
    public APIResult<OverViewQuantityAndStatusOutputVo> getOverViewBizQuantityAndStatus() {
        return APIResult.success(overViewBiz.getOverViewBizQuantityAndStatus());
    }

    /**
     * 睿信概览变量数量与状态统计
     * @return  睿信概览变量数量与状态统计
     */
    @GetMapping("/getVariableQuantityAndStatus")
    @Operation(description = "睿信概览变量数量与状态统计")
    public APIResult<OverViewQuantityAndStatusOutputVo> getVariableQuantityAndStatus() {
        return APIResult.success(overViewBiz.getVariableQuantityAndStatus());
    }

    /**
     * 睿信概览变量清单数量与状态统计
     * @return  睿信概览变量清单数量与状态统计
     */
    @GetMapping("/getManifestQuantityAndStatus")
    @Operation(description = "睿信概览变量清单数量与状态统计")
    public APIResult<OverViewQuantityAndStatusOutputVo> getManifestQuantityAndStatus() {
        return APIResult.success(overViewBiz.getManifestQuantityAndStatus());
    }

    /**
     * 睿信概览实时服务数量与状态统计
     * @return  睿信概览实时服务数量与状态统计
     */
    @GetMapping("/getServiceQuantityAndStatus")
    @Operation(description = "睿信概览实时服务数量与状态统计")
    public APIResult<OverViewQuantityAndStatusOutputVo> getServiceQuantityAndStatus() {
        return APIResult.success(overViewBiz.getServiceQuantityAndStatus());
    }

    /**
     * 睿信概览批量回溯数量与状态统计
     * @return  睿信概览批量回溯数量与状态统计
     */
    @GetMapping("/getBatchBacktrackingQuantityAndStatus")
    @Operation(description = "睿信概览批量回溯数量与状态统计")
    public APIResult<OverViewQuantityAndStatusOutputVo> getBatchBacktrackingQuantityAndStatus() {
        return APIResult.success(overViewBiz.getBatchBacktrackingQuantityAndStatus());
    }

    /**
     * 睿信概览预处理与变量模板数量与状态统计
     * @return  睿信概览预处理与变量模板数量与状态统计
     */
    @GetMapping("/getFunctionQuantityAndStatus")
    @Operation(description = "睿信概览预处理与变量模板数量与状态统计")
    public APIResult<OverViewQuantityAndStatusOutputVo> getFunctionQuantityAndStatus() {
        return APIResult.success(overViewBiz.getFunctionQuantityAndStatus());
    }

    /**
     * 获取实时服务的调用量和平均响应时间
     * @param timeEnum 时间限制的枚举
     * @return 睿信概览统计数量与状态
     */
    @GetMapping("/getServiceCallAndAvgResponse")
    @Operation(description = "获取实时服务的调用量和平均响应时间")
    public APIResult<List<OverViewNameAndQuantityOutputVo>> getServiceCallAndAvgResponse(@RequestParam("timeEnum") @NotNull(message = "时间限制不能为空") @Parameter(description = "时间刻度, TODAY(今天), YESTERDAY(昨天), LAST_SEVEN_DAYS(最近七天), LAST_THIRTY_DAYS(最近30天), LAST_TRIMESTER(最近3个月)") CallVolumeByTimeEnum timeEnum) {
        // 1.调用业务逻辑层的函数
        List<OverViewNameAndQuantityOutputVo> result = overViewBiz.getServiceCallAndAvgResponse(timeEnum);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 获取PSI统计
     * @param order  排序字段
     * @return PSI统计
     */
    @GetMapping("/getOverviewTargetPsiRanking")
    @Operation(description = "概览统计PSI排名")
    public APIResult<List<OverviewTargetRankingOutputVo>> getOverviewTargetPsiRanking(String order) {
        return APIResult.success(overViewBiz.getOverviewTargetPsiRanking(order));
    }

    /**
     * 获取IV统计
     * @param order  排序字段
     * @return IV统计
     */
    @GetMapping("/getOverviewTargetIvRanking")
    @Operation(description = "概览统计IV排名")
    public APIResult<List<OverviewTargetRankingOutputVo>> getOverviewTargetIvRanking(String order) {
        return APIResult.success(overViewBiz.getOverviewTargetIvRanking(order));
    }

    /**
     * 获取缺失值统计
     * @param order  排序字段
     * @return 缺失值统计
     */
    @GetMapping("/getOverviewTargetMrRanking")
    @Operation(description = "概览统计缺失值排名")
    public APIResult<List<OverviewTargetRankingOutputVo>> getOverviewTargetMrRanking(String order) {
        return APIResult.success(overViewBiz.getOverviewTargetMrRanking(order));
    }

}
