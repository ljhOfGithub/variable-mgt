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
 * @Description: 模型元数据对应的类型
 * @Author: xiewu
 * @Date: 2021/10/26
 * @Time: 11:39
 */
@AllArgsConstructor
@Getter
public enum DomainModeTypeEnum {

    /**
     * string类型
     **/
    STRING_DOMAIN_MODE_TYPE("string"),
    /**
     * 对象类型
     **/
    OBJECT_DOMAIN_MODE_TYPE("object"),
    /**
     * 时间类型
     **/
    DATE_DOMAIN_MODE_TYPE("date"),
    /**
     * 小数类型
     **/
    DOUBLE_DOMAIN_MODE_TYPE("double"),
    /**
     * 整数类型
     **/
    INT_DOMAIN_MODE_TYPE("int");

    private String message;

    /**
     * getEnumByMessage
     * @param message String
     * @return DomainModeTypeEnum
     */
    public static DomainModeTypeEnum getEnumByMessage(String message) {
        for (DomainModeTypeEnum modeTypeEnum : DomainModeTypeEnum.values()) {
            if (modeTypeEnum.getMessage().equals(message)) {
                return modeTypeEnum;
            }
        }
        return null;
    }
}
