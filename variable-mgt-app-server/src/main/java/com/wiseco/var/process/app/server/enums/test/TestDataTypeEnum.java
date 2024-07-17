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
public enum TestDataTypeEnum {
    /**
     * 测试集明细数据
     */
    TEST_DETAIL("1", "测试集明细数据"),

    /**
     * 测试执行结果
     */
    EXECUTE_RESULT("2", "测试执行结果");

    private String code;

    private String message;
}
