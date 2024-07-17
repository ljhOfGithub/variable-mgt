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
package com.wiseco.var.process.app.server.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量更新操作枚举
 * @author Gmm
 */
@Getter
@AllArgsConstructor
public enum VarUpdateActionEnum {

    /**
     * 导入
     */
    IMPORT("导入"),
    /**
     * 导出
     */
    EXPORT("导出"),
    /**
     * 打标签
     */
    TAGS("打标签");

    private String desc;

}
