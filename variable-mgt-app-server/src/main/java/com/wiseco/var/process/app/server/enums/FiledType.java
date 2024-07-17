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
 * 字段类型枚举类
 *
 * @author wangJunJie
 */

@Getter
@AllArgsConstructor
public enum FiledType {
    /**
     * 字段类型
     */

    Int8("int", "Int8"), Int16("int", "Int16"), Int32("int", "Int32"), Int64("int", "Int64"), Int128("int", "Int128"), Int256("int", "Int256"), DOUBLE(
            "double",
            "DOUBLE"), DOUBLE_PRECISION(
            "double",
            "DOUBLE PRECISION"), FLOAT(
            "float",
            "FLOAT"), Float32(
            "float",
            "Float32"), Float64(
            "float",
            "Float64"), String(
            "string",
            "String"), Date(
            "datetime",
            "Date"), Date32(
            "datetime",
            "Date32"), DateTime(
            "datetime",
            "DateTime"), DateTime32(
            "datetime",
            "DateTime32"), DateTime64(
            "datetime",
            "DateTime64");

    private String resType;
    private String filedType;

}
