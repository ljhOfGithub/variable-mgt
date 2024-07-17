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
 * 树形变量中根据类型寻找变量数据接口的beanName名称
 *
 * @author xiewu
 * @since  2022/03/02
 */
@AllArgsConstructor
@Getter
public enum DomainModelTreeFindVarBeanNameEnum {

    /**
     * 领域模型树查找变量数组类型
     */
    ARRAY_TYPE("domainModelTreeFindVarArrayType"),
    /**
     * 领域模型树查找对象变量类型
     */
    OBJECT_TYPE("domainModelTreeFindVarObjectType"),
    /**
     * 领域模型树查找基本变量类型
     */
    BASE_TYPE("domainModelTreeFindVarBaseType"),
    /**
     * 领域模型树查找基本变量数组类型
     */
    BASE_ARRAY_TYPE("domainModelTreeFindVarBaseArrayType"),
    /**
     * domainModelTreeFindVarObjectDynamicType
     */
    OBJECT_DYNAMIC_TYPE("domainModelTreeFindVarObjectDynamicType"),
    /**
     * domainModelTreeFindVarObjectArrayDynamicType
     */
    OBJECT_ARRAY_DYNAMIC_TYPE("domainModelTreeFindVarObjectArrayDynamicType"),
    /**
     * domainModelTreeFindVarBaseTypeAndBaseArrayType
     */
    BASE_TYPE_AND_ARRAY_TYPE("domainModelTreeFindVarBaseTypeAndBaseArrayType"),
    /**
     * domainModelTreeFindVarObjectArrayAndPropertyType
     */
    OBJECT_ARRAY_AND_PROPERTY("domainModelTreeFindVarObjectArrayAndPropertyType");

    private String message;

    /**
     * getEnumByMessage
     * @param message String
     * @return DomainModelTreeFindVarBeanNameEnum
     */
    public static DomainModelTreeFindVarBeanNameEnum getEnumByMessage(String message) {
        for (DomainModelTreeFindVarBeanNameEnum domainModelTreeFindVarBeanNameEnum : DomainModelTreeFindVarBeanNameEnum.values()) {
            if (domainModelTreeFindVarBeanNameEnum.getMessage().equals(message)) {
                return domainModelTreeFindVarBeanNameEnum;
            }
        }
        return null;
    }
}
