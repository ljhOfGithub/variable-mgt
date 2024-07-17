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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源类型枚举
 *
 * @author yangyunsen
 * @since 2022/9/6-13:51
 */
@AllArgsConstructor
@Getter
/**
 * 资源类型枚举类
 */
public enum ResourceTypeEnum {
    /**
     * hadoop集群
     */
    HIVE("hadoop", "hadoop集群"),
    /**
     * clickhouse
     */
    CLICKHOUSE("clickhouse", "clickhouse"),
    /**
     * 对象存储
     */
    OSS("oss", "对象存储"),
    /**
     * mysql
     */
    MYSQL("mysql", "mysql"),
    /**
     * oracle
     */
    ORACLE("oracle", "oracle");

    private String code;
    private String name;

    /**
     * code字段
     * @param code code
     * @return com.wiseco.var.process.app.server.commons.enums.ResourceTypeEnum
     */
    public static ResourceTypeEnum getByCode(String code) {
        for (ResourceTypeEnum t : ResourceTypeEnum.values()) {
            if (t.getCode().equals(code)) {
                return t;
            }
        }
        return null;
    }

    /**
     * 枚举name
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.ResourceTypeEnum
     */
    public static ResourceTypeEnum getByEnumName(String name) {
        for (ResourceTypeEnum t : ResourceTypeEnum.values()) {
            if (t.name().equals(name)) {
                return t;
            }
        }
        return null;
    }
}
