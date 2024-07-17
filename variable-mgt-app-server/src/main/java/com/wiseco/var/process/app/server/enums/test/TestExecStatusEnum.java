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
package com.wiseco.var.process.app.server.enums.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测试执行状态
 *
 * @author wangxianli
 */
@Getter
@AllArgsConstructor
public enum TestExecStatusEnum {
    /**
     * 异常
     */
    EXCEPTION("0", "异常"),
    NORMAL("1", "正常");

    private String code;
    private String message;

}
