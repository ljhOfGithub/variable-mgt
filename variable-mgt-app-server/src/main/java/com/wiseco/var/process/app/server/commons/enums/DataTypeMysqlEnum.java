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
 * mysql数据类型
 */
public enum DataTypeMysqlEnum {
    /**
     * int
     */
    INT("int"),
    /**
     * tinyint
     */
    TINYINT("tinyint"),
    /**
     * varchar
     */
    VARCHAR("varchar"),
    /**
     * char
     */
    CHAR("char"),
    /**
     * text
     */
    TEXT("text"),
    /**
     * mediumtext
     */
    MEDIUMTEXT("mediumtext"),
    /**
     * real
     */
    REAL("real"),
    /**
     * double
     */
    DOUBLE("double"),
    /**
     * float
     */
    FLOAT("float"),
    /**
     * decimal
     */
    DECIMAL("decimal"),
    /**
     * numeric
     */
    NUMERIC("numeric"),
    /**
     * date
     */
    DATE("date"),
    /**
     * datetime
     */
    DATETIME("datetime");

    private String type;

}
