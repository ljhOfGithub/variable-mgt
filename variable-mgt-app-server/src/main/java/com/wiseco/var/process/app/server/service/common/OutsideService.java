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
package com.wiseco.var.process.app.server.service.common;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.outside.service.rpc.dto.input.OutsideServiceAuthInputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceAuthErrorMsgOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceAuthOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceDetailRestOutputDto;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceListRestOutputDto;
import com.wiseco.outside.service.rpc.feign.OutsideServiceFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 外数rpc服务
 * @author wuweikang
 */
@Service
public class OutsideService {
    @Resource
    private OutsideServiceFeign outsideServiceFeign;

    /**
     * 获取外数服务list
     * @return list
     */
    public List<OutsideServiceListRestOutputDto> getOutsideList() {
        return outsideServiceFeign.findListRest().getData();
    }

    /**
     * 通过codelist查询外数服务
     * @param codeList codelist
     * @return list
     */
    public List<OutsideServiceListRestOutputDto> findOutsideServiceByCodes(List<String> codeList) {
        return outsideServiceFeign.getOutsideServiceByCode(codeList).getData();
    }


    /**
     * 根据id获取外数服务
     *
     * @param outsideServiceId 外数服务id
     * @return 外数服务信息
     */
    public OutsideServiceDetailRestOutputDto getOutsideServiceDetailRestById(Long outsideServiceId) {
        return outsideServiceFeign.getDetailRest(outsideServiceId).getData();
    }

    /**
     * 校验外数授权码
     *
     * @param authCode 授权码
     * @param outCode  外数服务code
     * @return 调用方授权校验出参
     */
    public APIResult<OutsideServiceAuthOutputDto> validateOutsideAuthCode(String authCode, String outCode) {
        return  outsideServiceFeign.validateAuthCode(authCode,outCode);
    }


    /**
     * 批量校验外数授权码
     *
     * @param outsideServiceAuthInputDto 调用方授权校验入参
     * @return 调用方授权校验信息出参
     */
    public  APIResult<List<OutsideServiceAuthErrorMsgOutputDto>> validateAuthCodeBatch(OutsideServiceAuthInputDto outsideServiceAuthInputDto) {
        return   outsideServiceFeign.validateAuthCodeBatch(outsideServiceAuthInputDto);
    }
}
