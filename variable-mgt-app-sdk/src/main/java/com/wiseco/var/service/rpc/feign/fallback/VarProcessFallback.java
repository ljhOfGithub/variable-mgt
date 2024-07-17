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
package com.wiseco.var.service.rpc.feign.fallback;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.commons.web.WisecoAPIResultCode;
import com.wiseco.var.service.rpc.feign.VarProcessServiceFeign;
import com.wiseco.var.service.rpc.feign.dto.ServiceAuthOutputDto;
import com.wiseco.var.service.rpc.feign.dto.ServiceListCriteria;
import com.wiseco.var.service.rpc.feign.dto.ServiceParamsDto;
import com.wiseco.var.service.rpc.feign.dto.VarProcessServiceDto;
import org.springframework.stereotype.Component;


/**
 * 熔断降级配置
 *
 * @author xupei
 */
@Component
public class VarProcessFallback implements VarProcessServiceFeign {

    @Override
    public APIResult referencedOutsideService(Long id) {
        return APIResult.fail(WisecoAPIResultCode.FAILURE);
    }

    @Override
    public APIResult<Page<VarProcessServiceDto>> findServiceList(ServiceListCriteria serviceListCriteria) {
        return APIResult.fail(WisecoAPIResultCode.FAILURE);
    }

    @Override
    public APIResult<ServiceParamsDto> getServiceParams(String serviceCode) {
        return APIResult.fail(WisecoAPIResultCode.FAILURE);
    }

    @Override
    public APIResult<ServiceAuthOutputDto> validateAuthCode(String authCode, String serviceCode) {
        return APIResult.fail(WisecoAPIResultCode.FAILURE);
    }
}
