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
 * 变量清单创建方法枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/28
 */
@Getter
@AllArgsConstructor
public enum VariableManifestCreationApproachEnum {

    /**
     * 版本来源: 复制原有
     */
    ORIGINAL(1, "使用原有变量版本"),

    /**
     * 版本来源: 使用最新
     */
    LATEST(2, "使用最新变量版本");

    private final Integer code;

    private final String description;
}
