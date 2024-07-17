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
 * 批量回溯触发类型枚举类
 *
 * @author wiseco
 * @since  2023/8/8
 */
@Getter
@AllArgsConstructor
public enum BatchBacktrackingTriggerTypeEnum {
    /**
     * 触发类型
     */

    MANUAL("人工"), SCHEDULED("定时");

    private String desc;

}
