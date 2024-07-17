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
package com.wiseco.var.process.app.server.controller.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.permission.RPCAccess;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceListCriteria;
import com.wiseco.var.process.app.server.controller.vo.input.ServiceParamsDto;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceAuthOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VarProcessServiceDto;
import com.wiseco.var.process.app.server.service.VariableServiceBiz;
import com.wiseco.var.process.app.server.service.datamodel.DataModelSaveBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * rpc调用
 *
 * @author wiseco
 */
@RestController
@RequestMapping("/rpc/varProcess")
@Slf4j
@Tag(name = "rpc")
public class RpcController {


    @Autowired
    private DataModelSaveBiz dataModelSaveBiz;

    @Autowired
    private VariableServiceBiz  variableServiceBiz;

    /**
     * 数据模型是否引用外数服务
     *
     * @param id id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/variableDataModel/referencedOutsideService/{id}")
    @Operation(summary = "数据模型是否引用外数服务")
    @RPCAccess
    public APIResult<Boolean> referencedOutsideService(@PathVariable("id") Long id) {
        return APIResult.success(dataModelSaveBiz.referencedOutsideService(id));
    }

    /**
     * 获取实时服务信息list(分页)
     * @param serviceListCriteria 分页查询条件
     * @return 服务列表
     */
    @Operation(summary = "获取实时服务信息list(分页)")
    @RPCAccess
    @PostMapping("/service/findServiceList")
    public APIResult<Page<VarProcessServiceDto>> findServiceList(@RequestBody ServiceListCriteria serviceListCriteria) {
        return APIResult.success(variableServiceBiz.findServiceList(serviceListCriteria));
    }

    /**
     * 根据编码获取实时服务入参出参信息
     * @param serviceCode 服务编码
     * @return 入参出参
     */
    @Operation(summary = "获取实时服务入参出参信息")
    @GetMapping("/service/getServiceParams")
    @RPCAccess
    public APIResult<ServiceParamsDto> getServiceParams(@RequestParam("serviceCode") String serviceCode) {
        ServiceParamsDto serviceParams = variableServiceBiz.getServiceParams(serviceCode);
        return APIResult.success(serviceParams);
    }

    /**
     * 校验授权码
     *
     * @param authCode 授权码
     * @param serviceCode 服务code
     * @return message
     */
    @RPCAccess
    @GetMapping("/service/validateAuthCode")
    APIResult<ServiceAuthOutputDto> validateAuthCode(@Parameter(name = "authCode", description = "授权码", in = ParameterIn.QUERY) @RequestParam("authCode") String authCode,
                                                     @Parameter(name = "serviceCode", description = "服务编码", in = ParameterIn.QUERY) @RequestParam("serviceCode") String serviceCode) {
        return APIResult.success(variableServiceBiz.validateAuthCode(authCode, serviceCode));
    }

}
