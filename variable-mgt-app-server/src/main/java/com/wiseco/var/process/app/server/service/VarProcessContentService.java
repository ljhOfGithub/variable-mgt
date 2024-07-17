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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Gmm
 * @since 2023/9/18
 */
@Service
@Slf4j
public class VarProcessContentService {

    /**
     * 构建词条变量的content
     *
     * @param varName varName
     * @param returnDataType returnDataType
     * @param functionEntryContent functionEntryContent
     * @param userInputValueMap    这里的key是变量模版入参的位置，从0开始，value为用户输入的参数值
     * @return JSONObject
     */
    public JSONObject buildEntireEntryContent(String varName, String returnDataType, String functionEntryContent,
                                              Map<String, String> userInputValueMap) {

        // 词条json对象
        JSONObject entryJson = JSONObject.parseObject(functionEntryContent);
        JSONArray parts = entryJson.getJSONArray("parts");
        JSONArray newParts = new JSONArray();
        for (Object part : parts) {
            JSONObject partJson = (JSONObject) part;
            if (!"text".equalsIgnoreCase(partJson.getString("type"))) {
                partJson.put("inputType", "input");
                // partJson里的paramIndex从1开始，需要手动-1
                int mapParamIdx = partJson.getInteger("paramIndex") - 1;
                partJson.put("value", userInputValueMap.get(String.valueOf(mapParamIdx)));
            }
            newParts.add(partJson);
        }
        entryJson.put("parts", newParts);

        // body
        JSONObject specificData = new JSONObject();
        specificData.put("body", entryJson);

        // result content
        JSONObject content = new JSONObject();
        content.put("specific_data", specificData);
        content.put("base_data", buildBaseData(varName, returnDataType));

        return content;
    }

    private JSONObject buildBaseData(String varName, String returnDataType) {
        // 通用属性
        JSONObject baseData = new JSONObject();
        baseData.put("label", varName);
        baseData.put("dataType", returnDataType);
        // data_model
        JSONObject dataModel = new JSONObject();
        dataModel.put("localVars", new JSONArray());
        baseData.put("data_model", dataModel);
        // 按数据类型补充
        DataTypeEnum dataTypeEnum = DataTypeEnum.getEnum(returnDataType);
        if (dataTypeEnum == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "没有此数据类型：" + returnDataType);
        }
        switch (dataTypeEnum) {
            case INTEGER:
                buildNumber(baseData, DataTypeEnum.INTEGER);
                break;
            case DOUBLE:
                buildNumber(baseData, DataTypeEnum.DOUBLE);
                break;
            default:
                buildOther(baseData);
        }
        return baseData;
    }

    private void buildOther(JSONObject baseData) {
        // precision
        baseData.put("precision", "");
        // dataRange
        JSONObject dataRange = new JSONObject();
        dataRange.put("opr", "");
        dataRange.put("vals", new JSONArray());
        baseData.put("dataRange", dataRange);
    }

    private void buildNumber(JSONObject baseData, DataTypeEnum dataTypeEnum) {
        // precision
        if (DataTypeEnum.DOUBLE == dataTypeEnum) {
            baseData.put("precision", "0.00");
        } else {
            baseData.put("precision", "0");
        }
        // dataRange
        JSONObject dataRange = new JSONObject();
        dataRange.put("opr", "<=..<=");
        JSONArray valsArr = new JSONArray();
        valsArr.add(MagicNumbers.MINUS_99999999);
        valsArr.add(MagicNumbers.INT_99999999);
        dataRange.put("vals", valsArr);
        baseData.put("dataRange", dataRange);
        // enumValue
        baseData.put("enumValue", new JSONArray());
        // specialValue
        baseData.put("specialValue", new JSONArray());
    }

}
