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
 * @author: xiewu
 */
@AllArgsConstructor
@Getter
public enum SysParamTypeEnum {
    /**
     * 内置参数
     */
    INSIDE_PARAM(1, "内置参数"),
    CUSTOM_PARAM(2, "自定义参数");

    private Integer type;
    private String desc;

    /**
     * getTypeEnum
     * @param type Integer
     * @return SysParamTypeEnum
     */
    public static SysParamTypeEnum getTypeEnum(Integer type) {
        for (SysParamTypeEnum outsideServiceStateEnum : SysParamTypeEnum.values()) {
            if (outsideServiceStateEnum.getType().equals(type)) {
                return outsideServiceStateEnum;
            }
        }
        return null;
    }
}
