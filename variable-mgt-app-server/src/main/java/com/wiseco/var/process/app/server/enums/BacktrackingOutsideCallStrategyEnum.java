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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xupei
 */
@AllArgsConstructor
@Getter
public enum BacktrackingOutsideCallStrategyEnum {
    // 1. 只查缓存
    CACHE_ONLY(OutsideCallStrategyEnum.CACHE_ONLY.name(), "仅使用历史数据"),
    // 2. 优先缓存，增量查接口
    CACHE_FIRST_INTERFACE(OutsideCallStrategyEnum.CACHE_FIRST_INTERFACE.name(), "优先历史数据，增量查询外部接口"),
    // 3. 优先缓存，增量查MOCK
    CACHE_FIRST_MOCK(OutsideCallStrategyEnum.MOCK_ONLY.name(), "优先历史数据，增量查询MOCK"),
    // 4. 优先历史数据，增量优先查MOCK，无MOCK查询外部接口
    CACHE_FIRST_MOCK_THEN_INTERFACE(OutsideCallStrategyEnum.MOCK_CACHE_INTERFACE.name(), "优先历史数据，增量优先查MOCK，无MOCK查询外部接口");

    private final String outsideCallStrategy;
    private final String desc;

    /**
     * getOutsideCallStrategyList
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    public static List<Map<String, Object>> getOutsideCallStrategyList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BacktrackingOutsideCallStrategyEnum outsideCallStrategyEnum : BacktrackingOutsideCallStrategyEnum.values()) {
            Map<String, Object> map = new HashMap<>(MagicNumbers.TWO);
            map.put("code", outsideCallStrategyEnum.name());
            map.put("name", outsideCallStrategyEnum.desc);
            list.add(map);
        }
        return list;
    }

    /**
     * fromName
     *
     * @param name
     * @return com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum
     */
    public static BacktrackingOutsideCallStrategyEnum fromName(String name) {
        for (BacktrackingOutsideCallStrategyEnum outsideCallStrategyEnum : BacktrackingOutsideCallStrategyEnum.values()) {
            if (outsideCallStrategyEnum.outsideCallStrategy.equals(name)) {
                return outsideCallStrategyEnum;
            }
        }
        return null;
    }
}
