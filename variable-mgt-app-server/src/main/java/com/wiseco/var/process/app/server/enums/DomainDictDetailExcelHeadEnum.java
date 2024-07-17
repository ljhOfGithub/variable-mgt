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
 * @Description: 名单数据导入表头
 * @Author: xiewu
 * @Date: 2021/12/20
 * @Time: 15:40
 */
@AllArgsConstructor
@Getter
public enum DomainDictDetailExcelHeadEnum {

    /**
     * 字典类型编码
     */
    TYPE_CODE("字典类型编码"),
    /**
     * 字典编码
     */
    CODE("字典编码"),
    /**
     * 字典名称
     */
    NAME("字典名称"),
    /**
     * 上级字典编码
     */
    PARENT_CODE("上级字典编码");

    private String message;

    /**
     * getMessageEnum
     * @param message String
     * @return DomainDictDetailExcelHeadEnum
     */
    public static DomainDictDetailExcelHeadEnum getMessageEnum(String message) {
        for (DomainDictDetailExcelHeadEnum domainRosterDataExcelHeadEnum : DomainDictDetailExcelHeadEnum.values()) {
            if (message.equals(domainRosterDataExcelHeadEnum.getMessage())) {
                return domainRosterDataExcelHeadEnum;
            }
        }
        return null;
    }
}
