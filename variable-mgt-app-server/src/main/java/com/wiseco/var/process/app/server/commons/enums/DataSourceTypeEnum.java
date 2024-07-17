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
 * 数据源类型枚举
 */
public enum DataSourceTypeEnum implements BaseEnum {
    /**
     * 数据文件
     */
    FILE("0", "数据文件"),
    /**
     * 远程数据库
     */
    DATABASE("1", "远程数据库"),
    /**
     * 本地数据集
     */
    DATA_SET("2", "本地数据集"),
    /**
     * 生产数据
     */
    PRODUCT_DATA("3", "生产数据");

    @Getter
    String code;
    @Getter
    String desc;

    DataSourceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * parseByName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.DataSourceTypeEnum
     */
    public static DataSourceTypeEnum parseByName(String name) {
        if (name != null) {
            for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
                if (value.name().equals(name)) {
                    return value;
                }
            }
        }
        return null;
    }

}
