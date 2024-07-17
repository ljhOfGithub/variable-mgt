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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessParamMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessParameter;
import com.wiseco.var.process.app.server.service.VarProcessParamService;
import com.wiseco.var.process.app.server.service.dto.input.MultipleVarProcessParamInputDto;
import com.wiseco.var.process.app.server.service.dto.input.VarProcessParamInputDto;
import com.wiseco.var.process.app.server.service.dto.output.VarProcessParamOutputDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 审核参数服务 实现类
 * </p>
 *
 * @author guozhuoyi
 * @since 2023/8/3
 */
@Service
public class VarProcessParamServiceImpl extends ServiceImpl<VarProcessParamMapper, VarProcessParameter> implements VarProcessParamService {

    /**
     * 更新审核参数的配置信息
     */
    @Override
    public Boolean updateParams(MultipleVarProcessParamInputDto inputDto) {
        boolean flag = true;
        List<VarProcessParamInputDto> list = inputDto.getList();

        for (VarProcessParamInputDto dto : list) {
            boolean isEnabled = dto.getIsEnabled() == 1;
            flag = setParamStatus(dto.getCode(), isEnabled) ? flag : false;
        }
        return flag;
    }

    /**
     * 获取审核参数列表
     */
    @Override
    public List<VarProcessParamOutputDto> listParams() {
        List<VarProcessParameter> parameterList = this.list();
        List<VarProcessParamOutputDto> list = new ArrayList<>();
        for (VarProcessParameter param : parameterList) {
            VarProcessParamOutputDto varProcessParamOutputDto = VarProcessParamOutputDto.builder().paramName(param.getParamName())
                    .paramCode(param.getParamCode()).isEnabled(param.getIsEnabled()).build();
            list.add(varProcessParamOutputDto);
        }
        return list;
    }

    /**
     * 返回某个审核参数的开关状态
     */
    @Override
    public Boolean getParamStatus(String paramCode) {
        LambdaQueryWrapper<VarProcessParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessParameter::getParamCode, paramCode);
        VarProcessParameter parameter = this.getOne(wrapper);

        return parameter.getIsEnabled() == 1;
    }

    /**
     * 更改审核参数的开关状态
     */
    @Override
    public Boolean setParamStatus(String paramCode, boolean isEnabled) {
        LambdaQueryWrapper<VarProcessParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessParameter::getParamCode, paramCode);
        VarProcessParameter param = this.getOne(wrapper);

        int modifiedValue = isEnabled ? 1 : 0;
        param.setIsEnabled(modifiedValue);
        return this.update(param, wrapper);
    }
}
