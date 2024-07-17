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
 * class业务类型枚举
 *
 * @author fudengkui
 */
@Getter
@AllArgsConstructor
public enum ClassBizTypeEnum {

    /**
     * class有属性也有方法
     */
    BOTH_ATTRIBUTE_METHOD(1, "class有属性也有方法"),

    HAVE_ATTRIBUTE_NONE_METHOD(2, "class有属性无方法"),

    NONE_ATTRIBUTE_HAVE_METHOD(3, "class无属性有方法"),

    NONE_ATTRIBUTE_NONE_METHOD(4, "class无属性无方法");

    private Integer type;

    private String desc;

}
