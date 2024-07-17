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
package com.wiseco.var.process.app.server.enums.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author wuweikang
 */
@AllArgsConstructor
@Getter
public enum StaticTemplateEnum {

    /**
     * 添加语句根节点
     */
    statement_provider("statement_provider", "添加语句根节点", ""),
    multi_statement_template("multi_statement_template", "可选多行添加语句", ""),
    action_statement_template("action_statement_template", "动作添加语句", ""),
    varprocess_statement_provider("varprocess_statement_provider", "变量模板的添加语句", ""),
    while_statement_provider("while_statement_provider", "条件循环下的添加语句", ""),
    if_then_while_statement_provider("if_then_while_statement_provider", "如果那么的添加语句", ""),

    assign_template("assign_template", "变量赋值", ""),
    object_assign_template("object_assign_template", "对象赋值", ""),
    array_assign_template("array_assign_template", "数组赋值", ""),
    update_param_template("update_param_template", "更新策略参数", ""),
    array_add_template("array_add_template", "向数组添加对象", ""),
    array_loop_template("array_loop_template", "数组循环", ""),
    while_do_template("while_do_template", "条件循环", ""),
    if_then_template("if_then_template", "如果。。。那么。。。", ""),
    if_then_else_template("if_then_else_template", "如果。。。那么。。。否则", ""),
    if_then_while_template("if_then_while_template", "循环内的如果那么", ""),
    if_then_else_while_template("if_then_else_while_template", "循环内的如果那么否则", ""),

    logic_provider("logic_provider", "逻辑表达式", "_bool"),
    number_provider("number_provider", "数值", "_number"),
    string_provider("string_provider", "字符串", "_string"),
    date_provider("date_provider", "时间", "_date"),

    function_common_template("function_common_template", "公共函数", ""),
    function_common_string_template("function_common_string_template", "字符串公共函数", "_string"),
    function_common_number_template("function_common_number_template", "数值公共函数", "_number"),
    function_common_date_template("function_common_date_template", "日期公共函数", "_date"),
    function_common_bool_template("function_common_bool_template", "布尔公共函数", "_bool"),

    function_vartemplate_template("function_vartemplate_template", "变量模板", ""),
    function_vartemplate_string_template("function_vartemplate_string_template", "字符串变量模板", "_string"),
    function_vartemplate_number_template("function_vartemplate_number_template", "数值变量模板", "_number"),
    function_vartemplate_date_template("function_vartemplate_date_template", "日期变量模板", "_date"),
    function_vartemplate_bool_template("function_vartemplate_bool_template", "布尔变量模板", "_bool"),

    data_provider_exception_value("data_provider_exception_value", "异常值provider", null),
    data_provider_dict_value("data_provider_dict_value", "字典provider", null),

    function_common_type("common", "公共方法", null),
    function_vartemplate_type("vartemplate", "变量模板", null),;
    private String code;
    private String desc;

    private String remark;

    /**
     * 数据类型的provider
     * @return list
     */
    public static List<StaticTemplateEnum> getDataTypeProviderList() {
        return Arrays.asList(StaticTemplateEnum.logic_provider,StaticTemplateEnum.number_provider,StaticTemplateEnum.string_provider,StaticTemplateEnum.date_provider);
    }

    /**
     * statement list
     * @return list
     */
    public static List<StaticTemplateEnum> getStatementList() {
        return Arrays.asList(StaticTemplateEnum.statement_provider,StaticTemplateEnum.multi_statement_template,StaticTemplateEnum.action_statement_template,
                StaticTemplateEnum.varprocess_statement_provider,StaticTemplateEnum.while_statement_provider,StaticTemplateEnum.if_then_while_statement_provider);
    }

    /**
     * function common datatype template
     * @return list
     */
    public static List<StaticTemplateEnum> getFunctionCommonTypeTemplate() {
        return Arrays.asList(StaticTemplateEnum.function_common_template,StaticTemplateEnum.function_common_string_template,StaticTemplateEnum.function_common_date_template,
                StaticTemplateEnum.function_common_number_template,StaticTemplateEnum.function_common_bool_template);
    }

    /**
     * function vartemplate datatype template
     * @return list
     */
    public static List<StaticTemplateEnum> getFunctionVarTemplateTypeTemplate() {
        return Arrays.asList(StaticTemplateEnum.function_vartemplate_template,StaticTemplateEnum.function_vartemplate_string_template,StaticTemplateEnum.function_vartemplate_date_template,
                StaticTemplateEnum.function_vartemplate_number_template,StaticTemplateEnum.function_vartemplate_bool_template);
    }
}
