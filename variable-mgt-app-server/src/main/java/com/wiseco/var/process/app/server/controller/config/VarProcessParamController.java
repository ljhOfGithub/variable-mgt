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
package com.wiseco.var.process.app.server.controller.config;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessParamService;
import com.wiseco.var.process.app.server.service.dto.input.MultipleVarProcessParamInputDto;
import com.wiseco.var.process.app.server.service.dto.output.VarProcessParamOutputDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 通用参数定义 - 审核参数配置服务
 * </p>
 *
 * @author guozhuoyi
 * @since 2023/8/3
 */
@Slf4j
@RestController
@RequestMapping("/varProcessParam")
@Tag(name = "通用配置参数定义")
@LoggableClass(param = "varProcessParam")
public class VarProcessParamController {
    @Autowired
    private VarProcessParamService varProcessParamService;

    /**
     * 获取审核参数列表
     *
     * @return 审核参数列表
     */
    @GetMapping("/listParams")
    @Operation(summary = "获取审核参数列表")
    public APIResult<List<VarProcessParamOutputDto>> getParamList() {
        return APIResult.success(varProcessParamService.listParams());
    }

    /**
     * 更新审核参数开关设置
     *
     * @param inputDto 输入实体类对象
     * @return 更新审核参数开关设置后的结果
     */
    @PostMapping("/updateParams")
    @Operation(summary = "更新审核参数开关设置")
    @LoggableMethod(value = "保存审核参数设置", type = LoggableMethodTypeEnum.SAVE)
    public APIResult<Boolean> saveParamSetting(@RequestBody MultipleVarProcessParamInputDto inputDto) {
        return APIResult.success(varProcessParamService.updateParams(inputDto));
    }

}
