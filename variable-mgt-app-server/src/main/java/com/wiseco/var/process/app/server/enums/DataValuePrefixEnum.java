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
package com.wiseco.var.process.app.server.enums;

import org.springframework.util.StringUtils;

/**
 * DataValuePrefixEnum
 */
public enum DataValuePrefixEnum {
    /**
     * INPUT 输入
     */
    INPUT,
    /**
     * OUTPUT 输出
     */
    OUTPUT,
    /**
     * ENGINEVARS 引擎变量
     */
    ENGINEVARS,
    /**
     * PARAMETERS 参数
     */
    PARAMETERS,
    /**
     * LOCALVARS 本地变量
     */
    LOCALVARS,
    /**
     * COMMONDATA
     */
    COMMONDATA,
    /**
     * BLAZEDATA
     */
    BLAZEDATA,
    /**
     * EXTERNALDATA
     */
    EXTERNALDATA,
    /**
     * EXTERNALVAR
     */
    EXTERNALVARS,
    /**
     * FUNCTIONRETURN
     */
    FUNCTIONRETURN,
    /**
     * 返回
     */
    RETURN,
    /**
     * 结果
     */
    RESULT,
    /**
     * VARS
     */
    VARS,
    /**
     * 原始数据
     */
    RAWDATA;

    /**
     * fromName
     *
     * @param name 名称
     * @return DataValuePrefixEnum
     */
    public static DataValuePrefixEnum fromName(String name) {
        for (DataValuePrefixEnum prefix : values()) {
            if (prefix.name().equalsIgnoreCase(name)) {
                return prefix;
            }
        }
        return null;
    }

    /**
     * isLegalVar
     *
     * @param varName 变量名
     * @return true or false
     */
    public static boolean isLegalVar(String varName) {
        if (StringUtils.hasText(varName)) {
            for (DataValuePrefixEnum prefix : values()) {
                if (varName.toLowerCase().startsWith(prefix.name().toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 以varName是否input开头 无法判断是否是合法变量，需要结合type=data
     * 此方法判断是否以 input. 开头
     *
     * @param varName 变量名
     * @return true or false
     */
    public static boolean isLegalVarNew(String varName) {
        if (StringUtils.hasText(varName)) {
            for (DataValuePrefixEnum prefix : values()) {
                if (varName.toLowerCase().startsWith(prefix.name().toLowerCase() + ".")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断变量是否是参数或本地变量
     *
     * @param varName 变量名
     * @return true or false
     */
    public static boolean isParamOrLocalVar(String varName) {
        if (StringUtils.hasText(varName)) {
            if (varName.toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase()) || varName.toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
