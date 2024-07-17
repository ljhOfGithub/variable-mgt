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
 * 测试数据输入和预期结果
 *
 * @author wangxianli
 * @date 2021/12/28
 */
@Getter
@AllArgsConstructor
public enum InputExpectTypeEnum {
    /**
     * 是否预期结果，：0-否，1-是
     */
    INPUT("0","输入"),
    EXPECT("1","预期结果"),
    RESULTS("2","实际结果"),;

    private String code;

    private String message;


}
