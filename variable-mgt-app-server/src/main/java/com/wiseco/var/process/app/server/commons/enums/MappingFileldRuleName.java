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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * @author ycc
 * @since 2022/11/8 16:49
 */
@AllArgsConstructor
@Getter
public enum MappingFileldRuleName {
    /**
     * 删除空格
     */
    trim(1),
    /**
     * 删除前缀
     */
    delPrefix(2),
    /**
     * 删除后缀
     */
    delSuffix(3),
    /**
     * 删除字符
     */
    delStr(4),
    /**
     * 增加前缀
     */
    addPrefix(5),
    /**
     * 增加后缀
     */
    addSuffix(6),
    /**
     * 转大写
     */
    toUpperCase(7),
    /**
     * 转小写
     */
    toLowerCase(8),
    /**
     * 字符串转日期
     */
    strToDate(9),
    /**
     * 日期转字符串
     */
    dateToStr(10),
    /**
     * 截取前N位
     */
    substrPN(11),
    /**
     * 截取后n位
     */
    substrSN(12),
    /**
     * 指定开始位数n向后截取
     */
    substrBN(13),
    /**
     * 查找替换
     */
    replace(14);

    private int code;

    /**
     * getByName
     * @param name 名称
     * @return com.wiseco.var.process.app.server.commons.enums.MappingFileldRuleName
     */
    public static MappingFileldRuleName getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (MappingFileldRuleName value : MappingFileldRuleName.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
