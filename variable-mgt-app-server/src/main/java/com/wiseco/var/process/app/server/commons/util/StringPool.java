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
package com.wiseco.var.process.app.server.commons.util;

import java.nio.charset.StandardCharsets;

/**
 * 字符串常量池
 *
 * @author: fudengkui
 * @since: 2022-07-28 17:38
 */
public interface StringPool {

    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String ASTERISK = "*";
    String STAR = ASTERISK;
    String BACK_SLASH = "\\";
    String COLON = ":";
    String COMMA = ",";
    String COMMA_ZH = "，";
    String DASH = "-";
    String DOLLAR = "$";
    String DOT = ".";
    String DOT_DOT = "..";

    String EMPTY = "";
    String EQUALS = "=";
    String FALSE = "false";
    String SLASH = "/";
    String HASH = "#";
    String HAT = "^";
    String LEFT_BRACE = "{";
    String LEFT_BRACKET = "(";
    String LEFT_CHEV = "<";
    String NEWLINE = "\n";
    String N = "n";
    String NO = "no";
    String NULL = "null";
    String OFF = "off";
    String ON = "on";
    String PERCENT = "%";
    String PIPE = "|";
    String PLUS = "+";
    String QUESTION_MARK = "?";
    String EXCLAMATION_MARK = "!";
    String QUOTE = "\"";
    String RETURN = "\r";
    String TAB = "\t";
    String RIGHT_BRACE = "}";
    String RIGHT_BRACKET = ")";
    String RIGHT_CHEV = ">";
    String SEMICOLON = ";";
    String SINGLE_QUOTE = "'";
    String BACKTICK = "`";
    String SPACE = " ";
    String TILDA = "~";
    String LEFT_SQ_BRACKET = "[";
    String RIGHT_SQ_BRACKET = "]";
    String TRUE = "true";
    String UNDERSCORE = "_";
    String Y = "y";
    String YES = "yes";
    String ONE = "1";
    String ZERO = "0";
    String CRLF = "\r\n";
    String IN = "in";
    String OUT = "out";
    String C = "c";
    String S = "s";
    String UPPERCASE_V = "V";

    String INPUT = "input";
    String OUTPUT = "output";

    String PRIMITIVE_TYPE_BOOLEAN = "boolean";
    String PRIMITIVE_TYPE_BYTE = "byte";
    String PRIMITIVE_TYPE_CHAR = "char";
    String PRIMITIVE_TYPE_DOUBLE = "double";
    String PRIMITIVE_TYPE_FLOAT = "float";
    String PRIMITIVE_TYPE_INT = "int";
    String PRIMITIVE_TYPE_LONG = "long";
    String PRIMITIVE_TYPE_SHORT = "short";
    String PRIMITIVE_TYPE_VOID = "void";

    String TYPE_STRING = "string";
    String TYPE_DATE = "date";
    String TYPE_DATETIME = "datetime";

    // ---------------------------------------------------------------- array

    String[] EMPTY_ARRAY = new String[0];

    byte[] BYTES_NEW_LINE = "\n".getBytes(StandardCharsets.UTF_8);

    // ---------------------------------------------------------------- file extension

    String TXT = "txt";
    String DOT_TXT = "txt";
    String XLS = "xls";
    String DOT_XLS = ".xls";
    String XLSX = "xlsx";
    String DOT_XLSX = ".xlsx";
    String CSV = "csv";
    String DOT_CSV = ".csv";
    String PMML = "pmml";
    String DOT_PMML = ".pmml";
    String PKL = "pkl";
    String DOT_PKL = ".pkl";
    String MODEL = "model";
    String DOT_MODEL = ".model";
    String PY = "py";
    String DOT_PY = ".py";
    String XSD = "xsd";
    String DOT_XSD = ".xsd";
    String XML = "xml";
    String DOT_XML = ".xml";
    String ZIP = "zip";
    String DOT_ZIP = ".zip";
    String TAR = "tar";
    String DOT_TAR = ".tar";
    String REPLACE_START = "${";
    String REPLACE_END = "}";
    String PERCENT_100 = "100%";

    /**
     * 默认方法，提供一个空实现
     *
     */
    default void doNothing() {
    }
}
