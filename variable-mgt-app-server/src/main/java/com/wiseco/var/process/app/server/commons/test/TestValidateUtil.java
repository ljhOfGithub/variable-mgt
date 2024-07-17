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
package com.wiseco.var.process.app.server.commons.test;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 策略测试/组件测试验证类
 *
 * @author wangxianli
 * @since 2021/12/25
 */
@Slf4j
public class TestValidateUtil {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^(-|\\+)?[0-9]*");

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("^(-|\\+)?[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
    
    /**
     * 判断字符串是否是数字
     *
     * @param str 输入字符串
     * @return boolean
     */
    public static boolean isNumeric(String str) {

        return NUMBER_PATTERN.matcher(str).matches();

    }

    /**
     * 判断字符串是否是double
     *
     * @param str 输入字符串
     * @return boolean
     */
    public static boolean isDouble(String str) {

        return DOUBLE_PATTERN.matcher(str).matches();

    }

    /**
     * isBoolean
     *
     * @param str 输入字符串
     * @return boolean
     */
    public static boolean isBoolean(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }

}
