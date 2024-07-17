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
 * @author mingao
 * @since 2023-08-18
 */

/**
 * 存量模式枚举类
 */
public enum StorageModeEnum implements BaseEnum {
    /**
     * 全量替换
     */
    FULL("0", "全量替换"),
    /**
     * 追加数据
     */
    ADD("1", "追加数据"),
    /**
     * 更新数据
     */
    UPDATE("2", "更新数据");
    @Getter
    String code;
    @Getter
    String desc;

    StorageModeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * parseByName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.StorageModeEnum
     */
    public static StorageModeEnum parseByName(String name) {
        for (StorageModeEnum dtds : StorageModeEnum.values()) {
            if (dtds.name().equals(name)) {
                return dtds;
            }
        }
        return null;
    }
}
