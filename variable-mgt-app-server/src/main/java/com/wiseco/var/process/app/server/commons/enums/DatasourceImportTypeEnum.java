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
package com.wiseco.var.process.app.server.commons.enums;

import com.wiseco.decision.common.enums.BaseEnum;
import lombok.Getter;

/**
 * @author Asker.J
 * @since 2022/10/27
 */

/**
 * 导入数据源类型枚举
 */
public enum DatasourceImportTypeEnum implements BaseEnum {
    /**
     * 人工
     */
    STATIC("0", "人工"),
    /**
     * 定时
     */
    DYNAMIC("1", "定时");

    @Getter
    String code;
    @Getter
    String desc;

    DatasourceImportTypeEnum(String code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    /**
     * parseByName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.DatasourceImportTypeEnum
     */
    public static DatasourceImportTypeEnum parseByName(String name) {
        if (name != null) {
            for (DatasourceImportTypeEnum value : DatasourceImportTypeEnum.values()) {
                if (value.name().equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }

}
