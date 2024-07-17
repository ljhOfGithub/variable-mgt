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
package com.wiseco.var.process.app.server.service.engine;

import cn.hutool.core.util.StrUtil;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.decision.engine.var.transform.component.context.VarSyntaxInfo;
import com.wiseco.decision.engine.java.template.parser.context.content.VarActionHistory;
import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.dto.StrComponentVarPathDto;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author chimeng
 * @see TestVariableTypeEnum
 * 1、FUNCTION  校验：公共方法|预处理逻辑|变量模板
 * 2、MANIFEST 变量清单校验
 * 3、VAR  变量校验
 * </p>
 * @since 2023/8/8
 */
@Service
public class SyntaxInfoValidService {

    private static final String BASE_DATA = "base_data";
    private static final String DATA_MODEL = "data_model";
    private static final String VOID = "void";

    /**
     * validSyntax
     *
     * @param info 信息
     * @param varCompileData 变量编译的数据
     * @param space 变量空间
     * @param type 变量测试的类型
     * @return 验证后的结果
     */
    public ValidMsg validSyntax(VarSyntaxInfo info, VarCompileData varCompileData, VarProcessSpace space, TestVariableTypeEnum type) {

        ValidMsg msg = new ValidMsg();
        JSONObject dataModelJson = new JSONObject();
        // 1. var_process_space input_data
        DomainDataModelTreeDto inputDto = DomainModelTreeEntityUtils.getDomainModelTree(space.getInputData());
        transformDataModelToJsonObject(inputDto, dataModelJson);
        // 2. parameter & localVar

        // 3. engine VarActionHistory
        SyntaxInfo syntaxInfo = parseUsedTable(info.getVarUsedTable());

        // FUNCTION  校验：公共方法|预处理逻辑|变量模板

        // 4. 返回值校验
        msg.getErrors().add(validReturnDataType(syntaxInfo, varCompileData));
        // 5. 数据模型的校验
        msg.getErrors().add(validDataModel(syntaxInfo));
        // 6. 未使用变量的校验提醒
        msg.getWarns().add(validUnUsedVar(syntaxInfo));
        return msg;
    }

    /**
     * @param syntaxInfo 数据模型信息
     * @return 报错信息
     */
    private String validUnUsedVar(SyntaxInfo syntaxInfo) {

        return "";
    }

    /**
     * @param syntaxInfo 数据模型信息
     * @return 报错信息
     */
    private String validDataModel(SyntaxInfo syntaxInfo) {

        return "";
    }

    /**
     * 校验公共函数的返回值是否匹配
     *
     * @param syntaxInfo  数据模型信息
     * @param compileData
     * @return 报错信息
     */
    private String validReturnDataType(SyntaxInfo syntaxInfo, VarCompileData compileData) {

        String returnDataType = syntaxInfo.getReturnDataType();
        if (StringUtils.isEmpty(returnDataType)) {
            return "";
        }
        return "";
    }

    private SyntaxInfo parseUsedTable(Map<String, VarActionHistory> usedTable) {
        SyntaxInfo syntaxInfo = new SyntaxInfo();
        try {
            usedTable.forEach((varPath, varInfo) -> {
                // 跳过逻辑
                if (!(StringUtils.isEmpty(varPath) || varPath.startsWith(PositionVarEnum.VARS.getName()) || VOID.equals(varInfo.getVarType()))) {
                    throw new RuntimeException();
                }
                // 公共函数返回值
                if (CommonConstant.COMMON_FUNCTION_RETURN_NAME.equals(varPath) || CommonConstant.VARIABLE_RETURN_NAME.equals(varPath)) {
                    syntaxInfo.setReturnDataType(varInfo.getVarType());
                    throw new RuntimeException();
                }
                // 数据组装
                StrComponentVarPathDto dto = new StrComponentVarPathDto();
                dto.setVarPath(varPath);
                dto.setVarType(varInfo.getVarType());
                dto.setIsArray(Integer.parseInt(varInfo.getIsArr()));
                // 参数和变量
                if (varPath.startsWith(PositionVarEnum.PARAMETERS.getName()) || varPath.startsWith(PositionVarEnum.LOCAL_VARS.getName())) {
                    dto.setActionHistory(varInfo.getActionHistory());
                    dto.setParameterType(StrUtil.blankToDefault(varInfo.getParameterType(), varInfo.getVarType()));
                    dto.setIsParameterArray(Integer.parseInt(StrUtil.blankToDefault(varInfo.getIsParameterArray(), varInfo.getIsArr())));
                    if (DataVariableBasicTypeEnum.getNameEnum(dto.getParameterType()) != null) {
                        // 基础数据类型
                        syntaxInfo.getBaseParamVarList().add(dto);
                    } else {
                        // 引用数据类型
                        syntaxInfo.getRefParamVarList().add(dto);
                    }
                } else {
                    // 数据模型
                    syntaxInfo.getDataModelVarList().add(dto);
                }
            });
        } catch (Exception e) {
            return syntaxInfo;
        }

        return syntaxInfo;
    }

    private void transformDataModelToJsonObject(DomainDataModelTreeDto contentDto, JSONObject newNameJson) {
        //新JSONObject
        JSONObject model = new JSONObject();
        model.put("isArr", contentDto.getIsArr());
        model.put("type", contentDto.getType());
        newNameJson.put(contentDto.getValue(), model);

        List<DomainDataModelTreeDto> children = contentDto.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (DomainDataModelTreeDto treeDto : children) {

            transformDataModelToJsonObject(treeDto, newNameJson);
        }
    }

    private JSONObject getParamLocalVars(String content) {
        JSONObject parametersAndLocalVars = new JSONObject();
        if (StringUtils.isEmpty(content)) {
            return parametersAndLocalVars;
        }
        JSONObject dataModel = JSONObject.parseObject(content).getJSONObject(DATA_MODEL);
        dataModel.forEach((key, value) -> {
            JSONArray params = dataModel.getJSONArray(key);
            for (int i = 0; i < params.size(); i++) {
                JSONObject paramOrLocalVar = params.getJSONObject(i);
                parametersAndLocalVars.put(key + "." + paramOrLocalVar.getString("name"), paramOrLocalVar);
            }
        });
        return parametersAndLocalVars;
    }

    @Data
    static class SyntaxInfo {
        private String returnDataType;
        private List<StrComponentVarPathDto> dataModelVarList = new ArrayList<>();
        private List<StrComponentVarPathDto> baseParamVarList = new ArrayList<>();
        private List<StrComponentVarPathDto> refParamVarList = new ArrayList<>();
    }

    @Data
    static class ValidMsg {

        private List<String> errors;
        private List<String> warns;
    }
}
