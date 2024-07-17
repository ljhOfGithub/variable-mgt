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

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.TraceQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.TraceListOutputVO;
import com.wiseco.var.process.app.server.service.TraceBiz;
import com.wiseco.var.process.app.server.service.dto.PagedQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/trace")
@Slf4j
@Tag(name = "trace日志")
public class TraceController {

    @Autowired
    private TraceBiz traceBiz;

    /**
     * 查询trace节点信息列表
     *
     * @param traceQueryInputVO  traceQueryInputVO
     * @return trace节点列表
     */
    @GetMapping("/node/list")
    @Operation(summary = "trace节点列表")
    public APIResult<List<TraceListOutputVO>> getTraceNodeList(TraceQueryInputVO traceQueryInputVO) {
        return APIResult.success(traceBiz.getTraceNodeList(traceQueryInputVO));
    }

    /**
     * 查询trace变量信息列表
     *
     * @param traceQueryInputVO traceQueryInputVO
     * @return trace变量列表
     */
    @GetMapping("/variable/list")
    @Operation(summary = "trace变量列表")
    public APIResult<PagedQueryResult<TraceListOutputVO>> getTraceVariableList(TraceQueryInputVO traceQueryInputVO) {
        return APIResult.success(traceBiz.getTraceVariableList(traceQueryInputVO));
    }

    /**
     * trace节点信息导出
     * @param inputVO 入参
     * @param response 响应
     */
    @GetMapping("/node/export")
    @Operation(summary = "trace节点信息导出")
    public void exportNodes(TraceQueryInputVO inputVO,
                             HttpServletResponse response) {
        traceBiz.exportNodes(inputVO,response);
    }

    /**
     * trace变量信息导出
     * @param inputVO 入参
     * @param response 响应
     */
    @GetMapping("/variable/export")
    @Operation(summary = "trace变量信息导出")
    public void exportVariables(TraceQueryInputVO inputVO,
                             HttpServletResponse response) {
        traceBiz.exportVariables(inputVO,response);
    }
}
