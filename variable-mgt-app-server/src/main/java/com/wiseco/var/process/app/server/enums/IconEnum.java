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
 * @Description:
 * @Author: xiewu
 * @Date: 2022/1/17
 * @Time: 17:33
 */
@Getter
@AllArgsConstructor
public enum IconEnum {

    /**
     * 错误
     */
    ERROR("icon", "error", "错误"),
    /**
     * 警告
     */
    WARNING("icon", "warning", "警告"),
    /**
     * 成功
     */
    SUCCESS("icon", "success", "成功");

    private String key;
    private String code;

    private String message;
}
