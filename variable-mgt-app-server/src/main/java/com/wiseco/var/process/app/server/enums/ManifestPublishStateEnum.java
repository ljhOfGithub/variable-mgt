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
import lombok.NoArgsConstructor;

/**
 * @author xiongzhewen
 * @since 2023/10/17
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ManifestPublishStateEnum {

    /**
     * 待发布
     */
    TOBEPUBLISHED(0,"待发布"),

    /**
     * 发布中
     */
    PUBLISHING(1,"发布中"),

    /**
     * 已发布
     */
    PUBLISHED(2,"已发布"),

    /**
     * 发布失败
     */
    FAILPUBLISH(3,"发布失败"),

    /**
     * 已停用
     */
    INACTIVE(4,"已停用");

    /**
     * code
     */
    Integer code;

    /**
     * desc
     */
    String desc;
}
