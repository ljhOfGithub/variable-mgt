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
package com.wiseco.var.service.rpc.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.service.rpc.feign.dto.ServiceAuthOutputDto;
import com.wiseco.var.service.rpc.feign.dto.ServiceListCriteria;
import com.wiseco.var.service.rpc.feign.dto.ServiceParamsDto;
import com.wiseco.var.service.rpc.feign.dto.VarProcessServiceDto;
import com.wiseco.var.service.rpc.feign.fallback.VarProcessFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wiseco
 */
@FeignClient(name = "variable-mgt-app", fallback = VarProcessFallback.class)
public interface VarProcessServiceFeign {
    /**
     * 数据模型是否引用外数服务
     *
     * @param id 外数服务ID
     * @return 是否被引用
     */
    @GetMapping("/rpc/varProcess/variableDataModel/referencedOutsideService/{id}")
    APIResult referencedOutsideService(@PathVariable("id") Long id);

    /**
     * 分页获取启用状态的实时服务list
     * @param serviceListCriteria 搜索条件
     * @return 服务list
     */
    @PostMapping("/rpc/varProcess/service/findServiceList")
    APIResult<Page<VarProcessServiceDto>> findServiceList(@RequestBody ServiceListCriteria serviceListCriteria);


    /**
     * 根据服务code拿到入参出参
     * @param serviceCode 服务编码
     * @return 入参出参
     */
    @GetMapping("/rpc/varProcess/service/getServiceParams")
    APIResult<ServiceParamsDto> getServiceParams(@RequestParam("serviceCode") String serviceCode);

    /**
     * 校验授权码
     *
     * @param authCode 授权码
     * @param serviceCode 服务code
     * @return message
     */
    @GetMapping("/rpc/varProcess/service/validateAuthCode")
    APIResult<ServiceAuthOutputDto> validateAuthCode(@RequestParam("authCode") String authCode, @RequestParam("serviceCode") String serviceCode);
}
