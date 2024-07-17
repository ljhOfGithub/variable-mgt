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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试表单报文转换支持格式枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/19
 */
@Getter
@AllArgsConstructor
public enum TestFormDatagramConvertSupportedFormatEnum {

    /**
     * 支持格式: JSON, XML
     */
    JSON("json"), XML("xml");

    private final String format;

    /**
     * getEnumFromFormat
     * @param format 入参
     * @return TestFormDatagramConvertSupportedFormatEnum
     */

    public static TestFormDatagramConvertSupportedFormatEnum getEnumFromFormat(String format) {
        for (TestFormDatagramConvertSupportedFormatEnum formatEnum : TestFormDatagramConvertSupportedFormatEnum.values()) {
            if (format.equals(formatEnum.getFormat())) {
                return formatEnum;
            }
        }
        return null;
    }
}
