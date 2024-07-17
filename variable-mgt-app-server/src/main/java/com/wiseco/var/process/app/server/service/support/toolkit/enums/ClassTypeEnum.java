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
package com.wiseco.var.process.app.server.service.support.toolkit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * class类型枚举
 *
 * @author fudengkui
 */
@Getter
@AllArgsConstructor
public enum ClassTypeEnum {

    /**
     * class
     */
    CLASS(1, "class"),

    ABSTRACT_CLASS(2, "abstract class"),

    INTERFACE(3, "interface"),

    ENUM(4, "enum");

    private Integer type;

    private String desc;

}
