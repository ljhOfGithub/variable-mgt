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

public enum LocalDataTypeEnum {
    // 基本信息
    desc("desc", "基本信息"),
    // 文档信息
    doc("vardoc", "文档信息"),
    // 版本信息
    table("table", "版本信息"),

    // 策略引用
    cell("cell", "策略引用"),

    // 当前版本保存记录
    log("log", "当前版本保存记录"),

    // 备注
    remark("remark", "备注"),

    LIFECYCLE("lifecycle", "生命周期");

    private String code;
    private String explain;

    LocalDataTypeEnum(String code, String explain) {
        this.code = code;
        this.explain = explain;
    }

    public String getCode() {
        return code;
    }

    public String getExplain() {
        return explain;
    }
}
