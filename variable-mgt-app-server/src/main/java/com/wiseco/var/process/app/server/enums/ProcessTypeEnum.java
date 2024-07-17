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
 * 实时变量加工方式枚举
 *
 * @author Gmm
 * @since  2023/8/10
 */
@AllArgsConstructor
@Getter
public enum ProcessTypeEnum {
    /**
     * 变量加工方式枚举
     */

    WRL("wrl脚本"), PYTHON("python脚本"), ENTRY("变量模版词条");
    private String desc;

}
