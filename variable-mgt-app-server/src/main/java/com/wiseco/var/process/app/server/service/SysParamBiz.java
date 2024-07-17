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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.entity.SysParam;
import com.wiseco.var.process.app.server.service.dto.output.SysParamByParamNameListOutputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 系统参数 业务接口实现
 *
 * @author xiewu
 * @since  2022/2/14 14:35
 */
@Slf4j
@Service
@RefreshScope
public class SysParamBiz {

    @Autowired
    private SysParamService sysParamService;

    /**
     * getSysParamByParamNameList
     * @param paramName 入参
     * @return SysParamByParamNameListOutputDto
     */
    public SysParamByParamNameListOutputDto getSysParamByParamNameList(String paramName) {

        // 校验license是否过期

        SysParam sysParam = sysParamService.getOne(new QueryWrapper<SysParam>().lambda()
                .eq(SysParam::getParamName, paramName)
                .eq(SysParam::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (null == sysParam) {
            return null;
        }

        return SysParamByParamNameListOutputDto.builder()
                .id(sysParam.getId())
                .paramNameCn(sysParam.getParamNameCn())
                .paramName(sysParam.getParamName())
                .paramValue(sysParam.getParamValue())
                .paramType(sysParam.getParamType())
                .dataType(sysParam.getDataType())
                .build();
    }
}
