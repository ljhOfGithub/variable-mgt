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
package com.wiseco.var.process.app.server.controller.test;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.permission.RPCAccess;
import com.wiseco.var.process.app.server.controller.vo.input.VariableExecuteParam;
import com.wiseco.var.process.app.server.service.TestVariableServiceBiz;
import com.wisecotech.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangxianli
 */
@RestController
@RequestMapping("/testVariableService")
@Slf4j
@Tag(name = "变量服务测试")
public class TestVariableServiceController {
    @Resource
    private TestVariableServiceBiz testVariableServiceBiz;

    /**
     * 离线包变量执行
     *
     * @param param
     * @return 执行结果
     */
    @PostMapping("/offlineLibVarExecute")
    @Operation(summary = "离线包变量执行")
    @RPCAccess
    public APIResult<JSONObject> offlineLibVarExecute(@RequestBody VariableExecuteParam param) {
        return APIResult.success(testVariableServiceBiz.offlineLibVarExecute(param));
    }


}
