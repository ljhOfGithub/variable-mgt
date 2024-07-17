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
 * 变量加工接口文档返回状态码枚举类
 *
 * @author Zhaoxiong Chen
 * @since  2022/10/20
 */
@Getter
@AllArgsConstructor
public enum VarProcessManifestDocumentStatusCodeEnum {

    // Rest 的 ErrorCode
    NO_EXTERNAL_SERIAL_NUM("error_code", "001", "服务调用流水号使用的变量没有传值"),

    MANIFEST_NOT_AVAILABLE("error_code", "002", "服务下无可用的变量清单接口"),

    SERVICE_DISABLED("error_code", "003", "无可用的服务信息");

    /**
     * 状态码类型
     */
    private final String type;

    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态码说明
     */
    private final String description;
}
