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
import org.springframework.util.StringUtils;

/**
 * @author ycc
 * @since  2022/10/17 15:23
 */
@Getter
@AllArgsConstructor
public enum OutputFileType {
    /**
     * 输出文件格式枚举
     */
    CSV(1, ".csv");
    private Integer code;
    private String value;

    /**
     * 根据type获取枚举
     *
     * @param fileType 类型
     * @return OutputFileType
     */
    public static OutputFileType getByCode(Integer fileType) {
        if (fileType == null) {
            return null;
        }
        for (OutputFileType value : OutputFileType.values()) {
            if (value.getCode().intValue() == fileType) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据name获取枚举
     *
     * @param name 名字
     * @return OutputFileType
     */
    public static OutputFileType getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (OutputFileType value : OutputFileType.values()) {
            if (name.equals(value.name())) {
                return value;
            }
        }
        return null;
    }
}
