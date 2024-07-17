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
 * 测试结果预期和实际数据对比状态 枚举类
 *
 * @author wangxianli
 * @date 2021/12/28
 */
@Getter
@AllArgsConstructor
public enum TestResultDiffStatusEnum {
    /**
     * 不一致
     */
    INCONSISTENT("0", "不一致"),
    /**
     * 一致
     */
    CONSISTENT("1", "一致"),
    /**
     * 无比较结果
     */
    NO_RESULT("2", "无比对结果");


    private String code;
    private String message;

}
