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
package com.wiseco.var.process.app.server.service.converter;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataVariableSimpleTypeEnum;
import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.enums.VarTemplateTypeEnum;
import com.wiseco.var.process.app.server.enums.template.StaticTemplateEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateDataProviderEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateFunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.template.TemplateVarLocationEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 公共模板枚举转换
 */
@Component
public class CommonTemplateEnumConverter {

    private static final String NOT_ARRAY = "0";

    /**
     * 根据provider获取对应数据类型
     *
     * @param dataProvider 数据提供方
     * @return List
     */
    public List<DataVariableTypeEnum> getDataTypeByProvider(TemplateDataProviderEnum dataProvider) {
        ArrayList<DataVariableTypeEnum> list;
        switch (dataProvider) {
            case DATA_PROVIDER_BOOL:
            case DATA_PROVIDER_ARRAY_BOOL:
            case DATA_PROVIDER_LEFTVALUE_BOOL:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_BOOL:
                list =  new ArrayList<>(Collections.singletonList(DataVariableTypeEnum.BOOLEAN_TYPE));
                break;
            case DATA_PROVIDER_DATE:
            case DATA_PROVIDER_ARRAY_DATE:
            case DATA_PROVIDER_LEFTVALUE_DATE:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_DATE:
                list =  new ArrayList<>(Arrays.asList(DataVariableTypeEnum.DATE_TYPE, DataVariableTypeEnum.DATETIME_TYPE));
                break;
            case DATA_PROVIDER_STRING:
            case DATA_PROVIDER_ARRAY_STRING:
            case DATA_PROVIDER_LEFTVALUE_STRING:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_STRING:
                list =  new ArrayList<>(Collections.singletonList(DataVariableTypeEnum.STRING_TYPE));
                break;
            case DATA_PROVIDER_NUMBER:
            case DATA_PROVIDER_ARRAY_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_NUMBER:
                list =  new ArrayList<>(Arrays.asList(DataVariableTypeEnum.INT_TYPE, DataVariableTypeEnum.DOUBLE_TYPE));
                break;
            case DATA_PROVIDER_LOCAL:
            case DATA_PROVIDER_LEFTVALUE:
                list = new ArrayList<>(Arrays.asList(DataVariableTypeEnum.INT_TYPE, DataVariableTypeEnum.DOUBLE_TYPE,
                        DataVariableTypeEnum.BOOLEAN_TYPE, DataVariableTypeEnum.DATE_TYPE, DataVariableTypeEnum.DATETIME_TYPE,
                        DataVariableTypeEnum.STRING_TYPE));
                break;
            case DATA_PROVIDER_ARRAY_OBJECT:
            case DATA_PROVIDER_LEFTVALUE_OBJECT:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_OBJECT:
                list = new ArrayList<>(Collections.singletonList(DataVariableTypeEnum.OBJECT_TYPE));
                break;
            case DATA_PROVIDER_OBJECT:
                list = new ArrayList<>(Collections.singletonList(DataVariableTypeEnum.OBJECT_TYPE));
                break;
            case DATA_PROVIDER_LEFTVALUE_ARRAY:
            case DATA_PROVIDER_ARRAY:
                list = new ArrayList<>(Collections.singletonList(DataVariableTypeEnum.ARRAY_TYPE));
                break;
            default:
                list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 根据provider获取需要查询的内部变量位置（入参，出参，本地变量）
     *
     * @param dataProvider 数据提供方
     * @return List
     */
    public List<TemplateVarLocationEnum> getParameterAndVarsLocation(TemplateDataProviderEnum dataProvider) {
        switch (dataProvider) {
            case DATA_PROVIDER_BOOL:
            case DATA_PROVIDER_DATE:
            case DATA_PROVIDER_STRING:
            case DATA_PROVIDER_NUMBER:
            case DATA_PROVIDER_ARRAY_STRING:
            case DATA_PROVIDER_ARRAY_DATE:
            case DATA_PROVIDER_ARRAY:
            case DATA_PROVIDER_ARRAY_OBJECT:
            case DATA_PROVIDER_ARRAY_BOOL:
            case DATA_PROVIDER_ARRAY_NUMBER:
            case DATA_PROVIDER_OBJECT:
                return new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.PARAMETER_INPUT, TemplateVarLocationEnum.PARAMETER_OUTPUT,
                        TemplateVarLocationEnum.LOCAL_VARS));
            case DATA_PROVIDER_LOCAL:
            case DATA_PROVIDER_LEFTVALUE:
            case DATA_PROVIDER_LEFTVALUE_STRING:
            case DATA_PROVIDER_LEFTVALUE_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_BOOL:
            case DATA_PROVIDER_LEFTVALUE_DATE:
            case DATA_PROVIDER_LEFTVALUE_OBJECT:
            case DATA_PROVIDER_LEFTVALUE_ARRAY:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_STRING:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_BOOL:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_DATE:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_OBJECT:
                return new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.PARAMETER_OUTPUT, TemplateVarLocationEnum.LOCAL_VARS));
            default:
                return null;
        }
    }

    /**
     * 获取模板变量的位置
     * @param dataProvider        模板数据提供者类型
     * @param functionTypeEnum    模板函数类型
     * @param varTemplateTypeEnum 模板变量类型
     * @return 变量位置列表
     */
    public List<TemplateVarLocationEnum> getModelVarsLocation(TemplateDataProviderEnum dataProvider, TemplateFunctionTypeEnum functionTypeEnum,
                                                               VarTemplateTypeEnum varTemplateTypeEnum) {
        Boolean flag = true;
        List<TemplateVarLocationEnum> result = null;
        switch (dataProvider) {
            case DATA_PROVIDER_BOOL:
            case DATA_PROVIDER_DATE:
            case DATA_PROVIDER_STRING:
            case DATA_PROVIDER_NUMBER:
            case DATA_PROVIDER_OBJECT:
            case DATA_PROVIDER_ARRAY:
            case DATA_PROVIDER_ARRAY_OBJECT:
            case DATA_PROVIDER_ARRAY_BOOL:
            case DATA_PROVIDER_ARRAY_DATE:
            case DATA_PROVIDER_ARRAY_STRING:
            case DATA_PROVIDER_ARRAY_NUMBER:
                result = new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.RAW_DATA, TemplateVarLocationEnum.PARAMETERS, TemplateVarLocationEnum.LOCAL_VARS));
                break;
            case DATA_PROVIDER_LEFTVALUE:
            case DATA_PROVIDER_LEFTVALUE_STRING:
            case DATA_PROVIDER_LEFTVALUE_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_BOOL:
            case DATA_PROVIDER_LEFTVALUE_DATE:
            case DATA_PROVIDER_LEFTVALUE_OBJECT:
            case DATA_PROVIDER_LEFTVALUE_ARRAY:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_BOOL:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_DATE:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_NUMBER:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_STRING:
            case DATA_PROVIDER_LEFTVALUE_ARRAY_OBJECT:
                flag = (functionTypeEnum != null && functionTypeEnum == TemplateFunctionTypeEnum.PREP) || (varTemplateTypeEnum != null && varTemplateTypeEnum == VarTemplateTypeEnum.SERVICE_INTERFACE);
                if (flag) {
                    result = new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.RAW_DATA, TemplateVarLocationEnum.PARAMETERS, TemplateVarLocationEnum.LOCAL_VARS));
                } else {
                    result = new ArrayList<>(Arrays.asList(TemplateVarLocationEnum.PARAMETERS, TemplateVarLocationEnum.LOCAL_VARS));
                }
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * 根据类型查询变量类型
     * @param dataType 前端类型
     * @return 映射的变量类型
     */
    public List<String> getDataTypeByType(String dataType) {
        List<String> result = null;
        switch (dataType) {
            case "string":
                result = new ArrayList<>(Arrays.asList("string"));
                break;
            case "date":
                result = new ArrayList<>(Arrays.asList("date", "datetime"));
                break;
            case "number":
                result = new ArrayList<>(Arrays.asList("double", "int"));
                break;
            case "boolean":
                result = new ArrayList<>(Arrays.asList("boolean"));
                break;
            case "void":
                result = new ArrayList<>(Arrays.asList("string", "double", "int", "date", "datetime", "boolean", "void"));
                break;
            default:
                result = null;
        }
        return result;
    }

    /**
     * getTemplate
     *
     * @param type 类型
     * @param isArray 是否为数组
     * @param direction 方向
     * @return java.lang.String
     */
    public String getTemplate(String type, boolean isArray, String direction) {
        String template = "";
        DataVariableSimpleTypeEnum dataType = DataVariableSimpleTypeEnum.getMessageEnum(type);
        if (dataType == null) {
            if (isArray) {
                return "opt_object_array_template";
            } else {
                return "whole_object_template";
            }
        }
        switch (dataType) {
            case STRING_TYPE:
                template = "string_template";
                break;
            case INT_TYPE:
            case DOUBLE_TYPE:
                template = "number_template";
                break;
            case BOOLEAN_TYPE:
                template = "logic_template";
                break;
            case DATE_TYPE:
            case DATETIME_TYPE:
                template = "date_template";
                break;
            default:
                template = "string_template";
                break;
        }
        if (isArray) {
            template = "array_" + template;
        }
        if (direction.equals(PositionVarEnum.PARAMETERS_OUT.getName())) {
            return "out_" + template;
        } else {
            return template;
        }
    }

    /**
     * getPart3Name
     *
     * @param x x
     * @return Part3的Name(list)
     */
    public Pair<String, String> getPart3Name(DomainDataModelTreeDto x) {
        String type = x.getType();
        DataVariableTypeEnum varsType = DataVariableTypeEnum.getMessageEnum(type);
        Pair<String, String> pair;
        switch (varsType) {
            case OBJECT_TYPE:
                if (x.getIsArr().equals(NOT_ARRAY)) {
                    pair = Pair.of("<对象表达式>", "opt_object_template");
                } else {
                    pair = Pair.of("<对象数组表达式>", "opt_object_array_template");
                }
                break;
            case INT_TYPE:
            case DOUBLE_TYPE:
                if (x.getIsArr().equals(NOT_ARRAY)) {
                    pair = Pair.of("<数值表达式>", "number_template");
                } else {
                    pair = Pair.of("<数值数组表达式>", "array_number_template");
                }
                break;
            case BOOLEAN_TYPE:
                if (x.getIsArr().equals(NOT_ARRAY)) {
                    pair = Pair.of("<逻辑表达式>", "logic_template");
                } else {
                    pair = Pair.of("<布尔数组表达式>", "array_logic_template");
                }
                break;
            case DATE_TYPE:
            case DATETIME_TYPE:
                if (x.getIsArr().equals(NOT_ARRAY)) {
                    pair =  Pair.of("<一个日期>", "date_template");
                } else {
                    pair =  Pair.of("<日期数组表达式>", "array_date_template");
                }
                break;
            case STRING_TYPE:
                if (x.getIsArr().equals(NOT_ARRAY)) {
                    pair =  Pair.of("<字符串表达式>", "string_template");
                } else {
                    pair =  Pair.of("<字符串数组表达式>", "array_string_template");
                }
                break;
            default:
                pair =  Pair.of("<字符串表达式>", "string_template");
                break;
        }
        return pair;
    }

    /**
     * 根据模板后缀判断模板类型名
     * @param staticTemplateEnum 模板枚举
     * @return 对应类型名
     */
    public String getDataTypeMsg(StaticTemplateEnum staticTemplateEnum) {
        switch (staticTemplateEnum.getRemark()) {
            case "_string":
                return "字符串";
            case "_date":
                return "日期";
            case "_number":
                return "数值";
            case "_bool":
                return "布尔";
            default:
                return "";
        }

    }

    /**
     * 根据类型判断所需provider名
     * @param type 变量类型
     * @param isArray 变量是否是数据
     * @param direction 出参/入参
     * @return provider名
     */
    public String getOutDataProvider(String type, Boolean isArray, String direction) {
        String template = "";
        DataVariableSimpleTypeEnum dataType = DataVariableSimpleTypeEnum.getMessageEnum(type);
        if (dataType == null) {
            if (isArray) {
                return TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_OBJECT.getProviderName();
            } else {
                return TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_OBJECT.getProviderName();
            }
        }
        switch (dataType) {
            case STRING_TYPE:
                if (isArray) {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_STRING.getProviderName();
                } else {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_STRING.getProviderName();
                }
                break;
            case INT_TYPE:
            case DOUBLE_TYPE:
                if (isArray) {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_NUMBER.getProviderName();
                } else {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_NUMBER.getProviderName();
                }
                break;
            case BOOLEAN_TYPE:
                if (isArray) {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_BOOL.getProviderName();
                } else {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_BOOL.getProviderName();
                }
                break;
            case DATE_TYPE:
            case DATETIME_TYPE:
                if (isArray) {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_DATE.getProviderName();
                } else {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_DATE.getProviderName();
                }
                break;
            default:
                if (isArray) {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_STRING.getProviderName();
                } else {
                    template = TemplateDataProviderEnum.DATA_PROVIDER_LEFTVALUE_ARRAY_STRING.getProviderName();
                }
                break;
        }
        return template;
    }
}
