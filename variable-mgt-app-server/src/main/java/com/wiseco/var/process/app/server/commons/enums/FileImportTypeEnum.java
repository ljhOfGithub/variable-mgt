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
 * 文件导入类型枚举类
 */
public enum FileImportTypeEnum implements BaseEnum {
    /**
     * 本地
     */
    LOCAL("0", "本地"),
    /**
     * 文件服务器
     */
    REMOTE("1", "文件服务器");
    @Getter
    String code;
    @Getter
    String desc;

    FileImportTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 枚举还原
     *
     * @param name 名称
     * @return FileImportTypeEnum
     */
    public static FileImportTypeEnum parseByName(String name) {
        for (FileImportTypeEnum dtds : FileImportTypeEnum.values()) {
            if (dtds.name().equals(name)) {
                return dtds;
            }
        }
        return null;
    }
}
