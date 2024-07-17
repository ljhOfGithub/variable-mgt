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
 * @author ycc
 */
@Getter
@AllArgsConstructor
public enum BacktrackingDataTypeEnum {

    /**
     * JSON报文
     */
    JSON("JSON报文"),
    /**
     * JSON字符串
     */
    JSON_STRING("JSON字符串"),
    /**
     * 结构化数据
     */
    STRUCTURED("结构化数据"),
    /**
     * XML
     */
    XML("XML");

    String desc;

}
