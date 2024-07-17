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
 * @author xupei
 */
@AllArgsConstructor
@Getter
public enum VarProcessDataModelSourceType {
    /**
     * 外部传入
     */
    OUTSIDE_PARAM(1, "外部传入"),
    /**
     * 内部数据
     */
    INSIDE_DATA(2, "内部数据"),
    /**
     * 外部数据服务
     */
    OUTSIDE_SERVER(3, "外部数据服务"),
    /**
     * 内部逻辑计算
     */
    INSIDE_LOGIC(4, "内部逻辑计算");

    Integer code;
    String description;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举code
     * @return VarProcessDataModelSourceType
     */
    public static VarProcessDataModelSourceType getByCode(Integer code) {
        for (VarProcessDataModelSourceType sourceType : VarProcessDataModelSourceType.values()) {
            if (sourceType.getCode().equals(code)) {
                return sourceType;
            }
        }
        return null;
    }
}
