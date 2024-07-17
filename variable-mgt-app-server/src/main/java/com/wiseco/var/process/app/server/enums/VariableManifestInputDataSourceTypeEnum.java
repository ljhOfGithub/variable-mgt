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
 * 变量清单和数据模型映射数据来源 枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/30
 */
@Getter
@AllArgsConstructor
public enum VariableManifestInputDataSourceTypeEnum {

    /**
     * 数据源于请求报文
     */
    OUTSIDE_PARAM(1, "外部传入"),

    /**
     * 数据源于外部服务调用响应报文
     */
    OUTSIDE_SERVER(2, "外数调用"),

    INSIDE_DATA(3, "内部数据获取"),

    INSIDE_LOGIC(4, "内部逻辑计算"),;

    private final Integer code;

    private final String description;
}
