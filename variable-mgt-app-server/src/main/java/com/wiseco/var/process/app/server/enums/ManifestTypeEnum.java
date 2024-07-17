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

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/4/9
 * @Time: 17:04
 */
@Getter
@AllArgsConstructor
public enum ManifestTypeEnum {
    /**
     * 清单类型
     */
    MANIFEST(1, "主清单"),
    ASYNC_MANIFEST(0, "异步清单");

    private final Integer code;
    private final String name;

    /**
     * 拿到枚举列表
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    public static List<Map<String, Object>> getManifestTypeList() {
        return Arrays.stream(ManifestTypeEnum.values()).map(item -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", item.getCode());
            map.put("name", item.getName());
            return map;
        }).collect(Collectors.toList());
    }
}
