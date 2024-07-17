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
package com.wiseco.var.process.app.server.commons.enums;

import lombok.Getter;

/**
 * 连接类型枚举类
 */

@Getter
public enum JoinEnum {
    /**
     * inner
     */
    INNER_JOIN("inner", "inner join", "内连接"),
    /**
     * left
     */
    LEFT_JOIN("left", "left join", "左连接"),
    /**
     * right
     */
    RIGHT_JOIN("right", "right join", "右连接"),
    /**
     * full
     */
    FULL_JOIN("full",
            "full join",
            "全连接"), CROSS_JOIN(
            "cross",
            "cross join",
            "交叉连接");

    private String code;

    private String keyWord;

    private String desc;

    JoinEnum(String code, String keyWord, String desc) {
        this.code = code;
        this.keyWord = keyWord;
        this.desc = desc;
    }

    /**
     * getKeyWordByCode
     * @param code code
     * @return java.lang.String
     */
    public static String getKeyWordByCode(String code) {

        for (JoinEnum value : JoinEnum.values()) {
            if (value.code.equals(code)) {
                return value.keyWord;
            }
        }
        return LEFT_JOIN.keyWord;
    }
}
