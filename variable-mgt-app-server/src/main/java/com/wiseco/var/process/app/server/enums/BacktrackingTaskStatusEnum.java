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
 * 批量回溯任务执行状态枚举类
 *
 * @author wiseco
 * @since  2023/8/16
 */
@Getter
@AllArgsConstructor
public enum BacktrackingTaskStatusEnum {
    /**
     * 执行状态
     */

    NOT_EXECUTED("未执行"),

    IN_PROGRESS("执行中"),

    SUCCESS("成功"),

    FAIL("失败"),

    PAUSED("暂停"),

    FILE_GENERATING("文件生成中");


    private final String desc;

}
