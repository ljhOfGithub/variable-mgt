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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringResultPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringResultPageOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceVersionVo;
import com.wiseco.var.process.app.server.service.monitoring.MonitoringAlterResultBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wiseco
 */
@RestController
@RequestMapping("/monitoring/result")
@Slf4j
@Tag(name = "监控预警结果")
@Validated
public class MonitoringAlterResultsController {

    @Resource
    private MonitoringAlterResultBiz monitoringAlterResultBiz;


    /**
     * 监控预警结果列表
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/page")
    @Operation(summary = "监控预警结果列表")
    public APIResult<IPage<MonitoringResultPageOutputVO>> page(@Validated MonitoringResultPageInputVO inputVO) {

        return APIResult.success(monitoringAlterResultBiz.getResultPage(inputVO));
    }

    /**
     * 获取实施服务及相关版本
     * @return  实施服务及相关版本
     */
    @GetMapping("/getAllServiceName")
    @Operation(summary = "实时服务及相关版本")
    public APIResult<List<ServiceVersionVo>> getAllServiceName() {
        return APIResult.success(monitoringAlterResultBiz.getAllServiceName());
    }

    /**
     * 获取实施服务的变量清单
     * @param serviceName 实时服务名称
     * @param version 实时服务版本
     * @return 变量清单
     */
    @GetMapping("/getAllManifest")
    @Operation(summary = "获取实时服务的变量清单")
    public APIResult<List<String>> getAllManifest(@RequestParam("serviceName") @NotEmpty(message = "服务名称不能为空") String serviceName,
                                                  @RequestParam("version") Integer version) {
        return APIResult.success(monitoringAlterResultBiz.getAllManifest(serviceName,version));
    }


}
