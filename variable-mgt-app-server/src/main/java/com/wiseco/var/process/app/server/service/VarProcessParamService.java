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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessParameter;
import com.wiseco.var.process.app.server.service.dto.input.MultipleVarProcessParamInputDto;
import com.wiseco.var.process.app.server.service.dto.output.VarProcessParamOutputDto;

import java.util.List;

/**
 * <p>
 * 通用配置参数-审核参数表 服务类
 * </p>
 *
 * @author guozhuoyi
 * @since 2023-08-03
 */

public interface VarProcessParamService extends IService<VarProcessParameter> {

    /**
     * updateParams
     *
     * @param inputDto 输入
     * @return java.lang.Boolean
     * @author guozhuoyi
     */
    Boolean updateParams(MultipleVarProcessParamInputDto inputDto);

    /**
     * listParams
     *获取审核参数配置信息，有开关两种状态
     *
     * @return java.util.List
     * @author guozhuoyi
     */
    List<VarProcessParamOutputDto> listParams();

    /**
     * getParamStatus
     *获取审核参数的开关状态
     *
     * @param paramCode code
     * @return java.lang.Boolean
     * @author guozhuoyi
     **/
    Boolean getParamStatus(String paramCode);

    /**
     * setParamStatus
     *设置审核参数的开关状态
     *
     * @param paramCode 参数编码
     * @param isEnabled 是否启用
     * @return java.lang.Boolean
     * @author guozhuoyi
     **/
    Boolean setParamStatus(String paramCode, boolean isEnabled);

}
