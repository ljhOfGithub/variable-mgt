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
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.input.CallVolumeDownLoadDto;
import com.wiseco.var.process.app.server.service.dto.input.CallVolumeDto;
import com.wiseco.var.process.app.server.service.dto.output.CallNumberOutputDto;
import com.wiseco.var.process.app.server.service.statistics.StatisticsCallVolumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 统计分析-调用量
 * @author wuweikang
 */
@RestController
@RequestMapping("/statistics/callVolume")
@Slf4j
@Tag(name =  "统计分析-调用量")
public class StatisticsCallVolumeController {

    @Resource
    private StatisticsCallVolumeService statisticsCallVolumeService;

    /**
     * 调用量统计
     * @param dto 调用量dto
     * @return APIResult
     */
    @PostMapping("/callVolume")
    @Operation(summary = "调用量统计")
    public APIResult<CallNumberOutputDto> callVolume(@RequestBody CallVolumeDto dto) {
        if (dto.getWhichWay().isEmpty()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择时间不能为空");
        }
        return APIResult.success(statisticsCallVolumeService.statisticsCallVolume(dto));
    }

    /**
     * 响应时间统计
     * @param dto 调用量dto
     * @return APIResult
     */
    @PostMapping("/responseTime")
    @Operation(summary = "响应时间统计")
    public APIResult<CallNumberOutputDto> responseTime(@RequestBody CallVolumeDto dto) {
        return APIResult.success(statisticsCallVolumeService.statisticsCallVolume(dto));
    }

    /**
     * 调用量统计报表下载
     * @param callVolumeDownLoadDto 调用量dto
     * @param response 响应体
     */
    @PostMapping("/call/download")
    @Operation(summary = "调用量统计报表下载")
    public void download(CallVolumeDownLoadDto callVolumeDownLoadDto, HttpServletResponse response) {
         statisticsCallVolumeService.download(callVolumeDownLoadDto,response);
    }

}
