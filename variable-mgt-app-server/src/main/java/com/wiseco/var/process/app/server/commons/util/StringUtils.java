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

/**
 * @author xupei
 */
public class StringUtils {
    /**
     * convertCamelCaseToUnderscore
     * 
     * @param camelCase 大小写情况
     * @return java.lang.String
     */
    public static String convertCamelCaseToUnderscore(String camelCase) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append("_").append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * convertUnderscoreToCamelCase
     * 
     * @param underlineName 下划线名
     * @return java.lang.String
     */
    public static String convertUnderscoreToCamelCase(String underlineName) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < underlineName.length(); i++) {
            char currentChar = underlineName.charAt(i);
            if (currentChar == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        return result.toString();
    }

    /**
     * 长字符串截取成字符串list
     * 
     * @param inputString 字符串
     * @param chunkSize 截取大小
     * @return 截取后的数组
     */
    public static String[] splitString(String inputString, int chunkSize) {
        int length = inputString.length();
        int arrayLength = (int)Math.ceil((double)length / chunkSize);
        String[] result = new String[arrayLength];

        char[] charArray = inputString.toCharArray();

        for (int i = 0, j = 0; i < length; i += chunkSize, j++) {
            int endIndex = Math.min(i + chunkSize, length);
            result[j] = new String(charArray, i, endIndex - i);
        }

        return result;
    }

}
