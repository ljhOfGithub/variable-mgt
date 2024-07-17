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

/**
 * 变量加工数据模型 JSONSchema 样板枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
public enum VarProcessDataModelBoilerplateEnum {

    /**
     * 原始数据
     */
    RAW_DATA("{\"title\": \"rawData\",\"description\": \"原始数据\",\"type\": \"object\"}"),

    /**
     * 内部数据样板
     */
    INTERNAL_DATA("{\"title\": \"internalData\",\"description\": \"内部数据\",\"type\": \"object\"}"),

    /**
     * 外部服务数据样板
     */
    EXTERNAL_DATA("{\"title\": \"externalData\",\"description\": \"外部服务数据\",\"type\": \"object\"}"),;

    private final String content;

    VarProcessDataModelBoilerplateEnum(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
