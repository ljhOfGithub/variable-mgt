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
 * 测试结果查询类别 枚举类
 *
 * @author wangxianli
 * @date 2023/12/25
 */
@Getter
@AllArgsConstructor
public enum TestResultsQueryTypeEnum {

    /**
     * 全部
     */
    ALL("0", "全部"),

    NORMAL("1", "正常"),

    EXCEPTION("2", "异常"),

    CONSISTENT("3", "预期一致"),

    INCONSISTENT("4", "预期不一致");

    private String code;
    private String message;

    /**
     * getCode
     *
     * @param code code
     * @return com.wiseco.var.process.app.server.enums.test.TestResultsQueryTypeEnum
     */
    public static TestResultsQueryTypeEnum getCode(String code) {
        for (TestResultsQueryTypeEnum location : TestResultsQueryTypeEnum.values()) {
            if (location.getCode().equals(code)) {
                return location;
            }
        }
        return null;
    }

}
