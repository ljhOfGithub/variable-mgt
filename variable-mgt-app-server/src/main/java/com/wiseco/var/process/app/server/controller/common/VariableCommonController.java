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
package com.wiseco.var.process.app.server.controller.common;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.DataModelMatchTreeInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarsTreeInputDto;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import com.wiseco.var.process.app.server.service.VariableCommonBiz;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 变量空间通用方法 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/18
 */
@RestController
@RequestMapping("/varProcessCommon")
@Slf4j
@Tag(name = "变量空间公共方法")
public class VariableCommonController {

    @Autowired
    private VariableCommonBiz variableCommonBiz;

    /**
     * 获取后端使用数据库类型
     *
     * @return dataSourceType
     */
    @GetMapping("/findDataSourceType")
    @Operation(summary = "获取后端使用数据库类型:可选值 mysql || clickhouse || dm || sqlServer")
    public APIResult<String> findDataSourceType() {
        return APIResult.success(variableCommonBiz.findDataSourceType());
    }

    /**
     * 查询变量空间数据模型树形结构统一接口
     *
     * @param inputDto 输入实体类对象
     * @return 查询变量空间数据模型树形结构统一接口
     */
    @PostMapping("/findVarsTree")
    @Operation(summary = "查询变量空间数据模型树形结构统一接口")
    public APIResult<List<DomainDataModelTreeDto>> findVarsTree(@RequestBody VarsTreeInputDto inputDto) {
        // 确定模板变量位置
        Set<TemplateVarLocationEnum> locationEnumSet = new HashSet<>();
        if (StringUtils.isNotBlank(inputDto.getVariablePath())) {
            String[] variablePathSegments = inputDto.getVariablePath().split("\\.");
            inputDto.setPositionList(Collections.singletonList(variablePathSegments[0]));
        }

        for (String position : inputDto.getPositionList()) {
            PositionVarEnum varEnum = PositionVarEnum.fromName(position);
            if (varEnum == PositionVarEnum.RAW_DATA) {
                locationEnumSet.add(TemplateVarLocationEnum.RAW_DATA);
            } else if (varEnum == PositionVarEnum.EXTERNAL_DATA) {
                locationEnumSet.add(TemplateVarLocationEnum.EXTERNAL_DATA);
            } else if (varEnum == PositionVarEnum.INTERNAL_DATA) {
                locationEnumSet.add(TemplateVarLocationEnum.INTERNAL_DATA);
            }
        }

        List<DataVariableTypeEnum> dataVarTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inputDto.getVarTypeList())) {
            for (String varType : inputDto.getVarTypeList()) {
                dataVarTypeList.add(DataVariableTypeEnum.getMessageEnum(varType));
            }
        }

        Pair<String, Boolean> targetVariablePath = null;
        if (StringUtils.isNotBlank(inputDto.getVariablePath())) {
            targetVariablePath = Pair.of(inputDto.getVariablePath(), null);
        }

        List<DomainDataModelTreeDto> list = variableCommonBiz.findDataVariable(inputDto.getSpaceId(), new ArrayList<>(locationEnumSet),
                dataVarTypeList, targetVariablePath, null, inputDto.getSessionId());
        return APIResult.success(list);
    }

    /**
     * 变量匹配树接口
     *
     * @param inputDto 输入实体类对象
     * @return 变量匹配树接口
     */
    @PostMapping("/findMatchVarsTree")
    @Operation(summary = "变量匹配树接口")
    public APIResult<List<DomainDataModelTreeDto>> findMatchVarsTree(@RequestBody DataModelMatchTreeInputDto inputDto) {

        List<DataVariableTypeEnum> dataVarTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inputDto.getVarTypeList())) {
            inputDto.getVarTypeList().stream().forEach(e -> dataVarTypeList.add(DataVariableTypeEnum.getMessageEnum(e)));
        } else {
            dataVarTypeList.add(DataVariableTypeEnum.getMessageEnum(inputDto.getVarType()));
        }


        List<DomainDataModelTreeDto> list = variableCommonBiz.findDataVariable(inputDto.getSpaceId(), dataVarTypeList, inputDto.getIsArrAy());

        return APIResult.success(list);
    }

    /**
     * 变量匹配树接口
     *
     * @param inputDto 输入实体类对象
     * @return 变量匹配树接口
     */
    @PostMapping("/direct/findMatchVarsTree")
    @Operation(summary = "变量匹配树接口")
    public APIResult<List<DomainDataModelTreeDto>> findDirectMatchVarsTree(@RequestBody DataModelMatchTreeInputDto inputDto) {
        return APIResult.success(variableCommonBiz.findDirectMatchVarsTree(inputDto));
    }


    /**
     * 获取数据模型下基本数据类型变量树
     * @param dataModelName 数据模型名称
     * @param varTypeList 数据类型 list
     * @return list
     */
    @GetMapping("/findDatModelBasicVars")
    @Operation(summary = "获取数据模型下基本数据类型变量树")
    public ApiResult<List<DomainDataModelTreeDto>> findDatModelBasicVars(@RequestParam @NotEmpty(message = "请传入数据模型名称") String dataModelName, @RequestParam @Parameter(description = "变量类型list") List<String> varTypeList) {
        return ApiResult.success(variableCommonBiz.findDatModelBasicVars(dataModelName,varTypeList));
    }
}
