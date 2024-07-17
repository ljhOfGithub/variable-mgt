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
 * 测试数据集表格表头定义枚举
 *
 * @author wangxianli
 * @date 2021/12/28
 */
@Getter
@AllArgsConstructor
public enum TestTableEnum {

    /**
     * master 主表key TODO
     */
    MASTER("master", "输入数据","输入"),

    /**
     * 输入
     */
    INPUT("input", "输入数据","输入"),

    /**
     * 测试结果
     */
    TEST_RESULT("testResult", "测试结果", "测试"),

    /**
     * 预期结果
     */
    EXPECT("expect", "预期结果","预期"),
    CURRENT_EXPECT("currentExpect", "当前预期结果","预期"),

    OUTPUT("output","输出结果","输出"),
    /**
     * 预期结果
     */
    RET("ret", "预期结果","预期"),

    RESULTS("results","实际结果","实际");

    private String code;

    private String message;

    private String shortmsg;
}

