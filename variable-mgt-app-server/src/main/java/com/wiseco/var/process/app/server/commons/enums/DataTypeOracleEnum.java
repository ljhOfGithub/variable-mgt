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
 * 函数类型 枚举类
 *
 * @author wangxianli
 * @since 2022/6/10
 */
@AllArgsConstructor
@Getter
/**
 * oracle数据库字段类型
 */
public enum DataTypeOracleEnum {
    /**
     * number
     */
    NUMBER("number"),
    /**
     * varchar2
     */
    VARCHAR("varchar2"),
    /**
     * char
     */
    CHAR("char"),
    /**
     * nvarchar2
     */
    NVARCHAR("nvarchar2"),
    /**
     * nchar
     */
    NCHAR("nchar"),
    /**
     * clob
     */
    CLOB("clob"),
    /**
     * nclob
     */
    NCLOB("nclob"),
    /**
     * date
     */
    DATE("date");

    private String type;

}
