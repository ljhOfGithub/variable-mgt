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
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationCopyInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MonitorConfigurationSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringAlterConfDeleteInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringAlterConfUpdateStateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.MonitoringConfigurationPageInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringConfigurationPageOutputVO;
import com.wiseco.var.process.app.server.service.monitoring.MonitoringAlterConfigurationBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author wiseco
 */
@RestController
@RequestMapping("/monitoring/configuration")
@Slf4j
@Tag(name = "监控预警配置")
public class MonitoringAlterConfigurationController {

    @Resource
    private MonitoringAlterConfigurationBiz monitoringAlterConfigurationBiz;

    /**
     * 添加监控预警配置
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/save")
    @Operation(summary = "保存监控预警配置")
    public APIResult<Long> save(@Validated @RequestBody MonitorConfigurationSaveInputVO inputVO) {
        return APIResult.success(monitoringAlterConfigurationBiz.save(inputVO));
    }


    /**
     * 复制监控预警配置
     *
     * @param inputVO 入参
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/copy")
    @Operation(summary = "复制监控预警配置")
    public APIResult<Long> copy(@Validated @RequestBody MonitorConfigurationCopyInputVO inputVO) {
        return APIResult.success(monitoringAlterConfigurationBiz.copy(inputVO));
    }

    /**
     * 删除监控预警配置
     *
     * @param monitoringAlterConfDeleteInputVo 入参
     * @return Boolean
     */
    @PostMapping("/delete")
    @Operation(summary = "删除监控预警配置")
    public APIResult<Boolean> delete(@RequestBody MonitoringAlterConfDeleteInputVo monitoringAlterConfDeleteInputVo) {
        return APIResult.success(monitoringAlterConfigurationBiz.delete(monitoringAlterConfDeleteInputVo.getId()));
    }



    /**
     * 修改监控预警配置
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/update")
    @Operation(summary = "修改监控预警配置")
    public APIResult<Long> update(@Validated @RequestBody MonitorConfigurationSaveInputVO inputVO) {
        return APIResult.success(monitoringAlterConfigurationBiz.save(inputVO));
    }


    /**
     * updateState
     *
     * @param monitoringAlterConfUpdateStateInputVo 入参
     * @return Boolean
     */
    @PostMapping("/updateState")
    @Operation(summary = "修改状态")
    public APIResult<Boolean> updateState(@RequestBody MonitoringAlterConfUpdateStateInputVo monitoringAlterConfUpdateStateInputVo) {
        return APIResult.success(monitoringAlterConfigurationBiz.updateState(monitoringAlterConfUpdateStateInputVo.getId(), monitoringAlterConfUpdateStateInputVo.getActionType()));
    }

    /**
     * 监控预警配置列表
     *
     * @param inputVO 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/page")
    @Operation(summary = "监控预警配置列表")
    public APIResult<IPage<MonitoringConfigurationPageOutputVO>> page(@Validated MonitoringConfigurationPageInputVO inputVO) {
        log.info("监控预警配置列表，inputVO:{}", inputVO);
        return APIResult.success(monitoringAlterConfigurationBiz.getPage(inputVO));
    }


    /**
     * 查看监控预警配置
     *
     * @param id 监控预警配置ID
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/view")
    @Operation(summary = "查看监控预警配置")
    public APIResult<MonitorConfigurationSaveInputVO> view(@RequestParam @NotNull(message = "规则id不能为空") Long id) {
        return APIResult.success(monitoringAlterConfigurationBiz.view(id));
    }


    /**
     * 根据服务名称和版本查询调用清单列表
     *
     * @param serviceName 服务名称
     * @param version     版本
     * @return 获取调用清单列表
     */
    @GetMapping("/getManifestList")
    @Operation(summary = "根据服务名称和版本查询调用清单列表")
    public APIResult<Map<String, Long>> getManifestList(@RequestParam("serviceName") @NotEmpty(message = "服务名称不能为空") String serviceName,
                                                        @RequestParam("version") @NotNull(message = "服务版本不能为空") Integer version) {
        return APIResult.success(monitoringAlterConfigurationBiz.getManifestList(serviceName, version));
    }
}
