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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @since 2022/10/17 15:18
 */
@Getter
@AllArgsConstructor
public enum OutputType {
    /**
     * 输出类型
     */

    FILE(1), DB(2);

    private Integer code;

    /**
     * 根据枚举code获取枚举
     *
     * @param outPutType 输出类型
     * @return OutputType
     */
    public static OutputType getByCode(Integer outPutType) {
        if (outPutType == null) {
            return null;
        }
        OutputType[] values = OutputType.values();
        for (OutputType modelOutputType : values) {
            if (outPutType.intValue() == modelOutputType.getCode()) {
                return modelOutputType;
            }
        }
        return null;
    }
}
